package ru.datana.smart.ui.temperature.app

import ch.qos.logback.classic.Logger
import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.cio.websocket.pingPeriod
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
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.temperature.app.cor.context.TemperatureBeContext
import ru.datana.smart.ui.temperature.app.websocket.WsManager
import ru.datana.smart.ui.temperature.app.mappings.toInnerModel
import java.time.Duration
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.ui.temperature.app.cor.handlers.AnalysisTopicHandler
import ru.datana.smart.ui.temperature.app.cor.handlers.RawTopicHandler
import ru.datana.smart.ui.temperature.app.cor.services.ForwardServiceKafkaUi

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
@KtorExperimentalAPI
fun Application.module(testing: Boolean = false) {
    val logger = datanaLogger(this.log as Logger)

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

    val wsManager = WsManager(
        Json { encodeDefaults = true },
        logger
    )

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
            wsManager.addSession(this)
            try {
                for (frame in incoming) {
                }
            } catch (e: ClosedReceiveChannelException) {
                println("onClose ${closeReason.await()}")
            } catch (e: Throwable) {
                logger.error("Error within websocket block due to: ${closeReason.await()}", e)
            } finally {
                wsManager.delSession(this)
            }
        }

        kafka(listOf(topicRaw, topicAnalysis)) {
            val context = TemperatureBeContext(
                records = records.map { it.toInnerModel() },
                logger = logger,
                jacksonMapper = ObjectMapper(),
                json = Json { encodeDefaults = true }
            )
            val env = DefaultKonveyorEnvironment.also {
                it["topicRaw"] = topicRaw
                it["topicAnalysis"] = topicAnalysis
                it["sensorId"] = sensorId
            }
            ForwardServiceKafkaUi().exec(context, env)
            context.forwardObjects.forEach {
                wsManager.sendToAll(it)
            }
            commitAll()
        }
    }
}


