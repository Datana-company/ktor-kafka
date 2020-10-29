package ru.datana.smart.ui.converter.app

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
import org.slf4j.event.Level
import ru.datana.smart.common.ktor.kafka.KtorKafkaConsumer
import ru.datana.smart.common.ktor.kafka.kafka
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.converter.app.common.MetalRateEventGenerator
import ru.datana.smart.ui.converter.app.cor.context.ConverterBeContext
import ru.datana.smart.ui.converter.app.cor.repository.UserEventsRepository
import ru.datana.smart.ui.converter.app.cor.services.ForwardServiceKafkaUi
import ru.datana.smart.ui.converter.app.mappings.toInnerModel
import ru.datana.smart.ui.converter.app.websocket.WsManager
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
    val topicTemperature by lazy { environment.config.property("ktor.kafka.consumer.topic.temperature").getString().trim() }
    val topicConverter by lazy { environment.config.property("ktor.kafka.consumer.topic.converter").getString().trim() }
    val topicVideo by lazy { environment.config.property("ktor.kafka.consumer.topic.video").getString().trim() }
    val topicMeta by lazy { environment.config.property("ktor.kafka.consumer.topic.meta").getString().trim() }
    val sensorId by lazy { environment.config.property("ktor.datana.sensor.id").getString().trim() }
    val metalRateEventGenTimeout: Long by lazy { environment.config.property("ktor.conveyor.metalRateEventGen.timeout").getString().trim().toLong() }
    val metalRateEventGenMax: Double by lazy { environment.config.property("ktor.conveyor.metalRateEventGen.maxValue").getString().trim().toDouble() }
    val metalRateEventGenMin: Double by lazy { environment.config.property("ktor.conveyor.metalRateEventGen.minValue").getString().trim().toDouble() }
    val metalRateEventGenChange: Double by lazy { environment.config.property("ktor.conveyor.metalRateEventGen.changeValue").getString().trim().toDouble() }
    val metalRateCriticalPoint: Double by lazy { environment.config.property("ktor.conveyor.metalRatePoint.critical").getString().trim().toDouble() }
    val metalRateNormalPoint: Double by lazy { environment.config.property("ktor.conveyor.metalRatePoint.normal").getString().trim().toDouble() }

    val metalRateEventGenerator = MetalRateEventGenerator(
        timeout = metalRateEventGenTimeout,
        maxValue = metalRateEventGenMax,
        minValue = metalRateEventGenMin,
        changeValue = metalRateEventGenChange
    )
    metalRateEventGenerator.start()

    val userEventsRepository = UserEventsRepository()
    val videoAdapterStream by lazy {environment.config.property("ktor.datana.video.adapter.url").getString().trim()}

    routing {
        static("/") {
            defaultResource("static/index.html")
            resources("static")
        }

        get("/front-config") {
            call.respondText(
                """
                {
                    "settings": [
                        {"videoadapterstreamurl": "$videoAdapterStream"}
                    ]
                }
                """.trimIndent()
            )
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

        kafka(listOf(topicTemperature, topicConverter, topicVideo, topicMeta)) {
            val context = ConverterBeContext(
                records = records.map { it.toInnerModel() }
            )
            ForwardServiceKafkaUi(
                logger = logger,
                wsManager = wsManager,
                topicTemperature = topicTemperature,
                topicConverter = topicConverter,
                topicVideo = topicVideo,
                topicMeta = topicMeta,
                metalRateEventGenerator = metalRateEventGenerator,
                sensorId = sensorId,
                metalRateCriticalPoint = metalRateCriticalPoint,
                metalRateNormalPoint = metalRateNormalPoint,
                eventsRepository = userEventsRepository
            ).exec(context)

            commitAll()
        }
    }
}
