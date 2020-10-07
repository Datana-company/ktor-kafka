package ru.datana.smart.ui.temperature.app

import ch.qos.logback.classic.Logger
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.cio.websocket.DefaultWebSocketSession
import io.ktor.http.cio.websocket.pingPeriod
import io.ktor.http.cio.websocket.send
import io.ktor.http.cio.websocket.timeout
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.path
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.json.Json
import org.slf4j.event.Level
import ru.datana.smart.common.ktor.kafka.KtorKafkaConsumer
import ru.datana.smart.common.ktor.kafka.kafka
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponse
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.temperature.app.kafka.parsers.parseKafkaInputAnalysis
import ru.datana.smart.ui.temperature.app.kafka.parsers.parseKafkaInputTemperature
import ru.datana.smart.ui.temperature.ws.models.WsDsmartResponseAnalysis
import ru.datana.smart.ui.temperature.ws.models.WsDsmartResponseTemperature
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
@KtorExperimentalAPI
fun Application.module(testing: Boolean = false) {
    val log = datanaLogger(this.log as Logger)

    val wsSessions = ConcurrentHashMap.newKeySet<DefaultWebSocketSession>()
    val json = Json {
        encodeDefaults = true
    }

    suspend fun sendToAll(data: IWsDsmartResponse<*>) {
        log.trace("sending to client: $data")
        val wsSessionsIterator = wsSessions.iterator()
        while (wsSessionsIterator.hasNext()) {
            wsSessionsIterator.next().apply {
                try {
                    val jsonString = when(data) {
                        is WsDsmartResponseTemperature -> json.encodeToString(WsDsmartResponseTemperature.serializer(), data)
                        is WsDsmartResponseAnalysis -> json.encodeToString(WsDsmartResponseAnalysis.serializer(), data)
                        else -> throw RuntimeException("Unknown type of data")
                    }
                    log.trace("Sending to client ${hashCode()}: $jsonString")
                    send(jsonString)
                } catch (e: Throwable) {
                    log.error("Session ${hashCode()} is removed due to exception {}", e)
                    wsSessionsIterator.remove()
                }
            }
        }
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    install(KtorKafkaConsumer)

    val topicRaw by lazy { environment.config.property("ktor.kafka.consumer.topic.raw").getString().trim() }
    val topicAnalysis by lazy { environment.config.property("ktor.kafka.consumer.topic.analysis").getString().trim() }
    val sensorId by lazy { environment.config.property("ktor.datana.sensor.id").getString().trim() }

    routing {
        static("/") {
            defaultResource("static/index.html")
            resources("static")
        }

        webSocket("/ws") {
            println("onConnect")
            wsSessions += this
            try {
                for (frame in incoming) { }
            } catch (e: ClosedReceiveChannelException) {
                println("onClose ${closeReason.await()}")
            } catch (e: Throwable) {
                log.error("Error within websocket block due to: ${closeReason.await()}", e)
            } finally {
                wsSessions -= this
            }
        }

        kafka(listOf(topicRaw, topicAnalysis)) {
            records
                .firstOrNull { it.topic() == topicRaw }
                ?.let { record ->
                    log.trace("topic = ${record.topic()}, partition = ${record.partition()}, offset = ${record.offset()}, key = ${record.key()}, value = ${record.value()}")
                    parseKafkaInputTemperature(record.value(), sensorId)
                }
                ?.takeIf {
                    it.data?.temperatureAverage?.isFinite() ?: false
                }
                ?.also { temp -> sendToAll(temp) }

            records
                .firstOrNull { it.topic() == topicAnalysis }
                ?.let { record ->
                    log.trace("topic = ${record.topic()}, partition = ${record.partition()}, offset = ${record.offset()}, key = ${record.key()}, value = ${record.value()}")
                    parseKafkaInputAnalysis(record.value(), sensorId)
                }
                ?.takeIf {
                    it.data?.timeActual != null
                }
                ?.also { temp -> sendToAll(temp) }

            commitAll()
        }
    }
}


