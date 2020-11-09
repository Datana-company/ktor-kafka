package ru.datana.smart.ui.temperature.app

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.json.Json
import org.slf4j.event.Level
import ru.datana.smart.common.ktor.kafka.KtorKafkaConsumer
import ru.datana.smart.common.ktor.kafka.kafka
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.temperature.app.cor.context.TemperatureBeContext
import ru.datana.smart.ui.temperature.app.cor.services.ForwardServiceKafkaUi
import ru.datana.smart.ui.temperature.app.mappings.toInnerModel
import ru.datana.smart.ui.temperature.app.websocket.WsManager
import java.time.Duration

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
@KtorExperimentalAPI
fun Application.module(testing: Boolean = false) {
    val logger = datanaLogger(::main::class.java)

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

    val wsManager = WsManager()
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
                records = records.map { it.toInnerModel() }
            )
            ForwardServiceKafkaUi(
                logger = logger,
                jacksonSerializer = ObjectMapper().configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true),
                kotlinxSerializer = Json { encodeDefaults = true },
                wsManager = wsManager,
                topicRaw = topicRaw,
                topicAnalysis = topicAnalysis,
                sensorId = sensorId
            ).exec(context)

            commitAll()
        }
    }
}


