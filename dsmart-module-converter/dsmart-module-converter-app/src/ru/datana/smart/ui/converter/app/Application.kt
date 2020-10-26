package ru.datana.smart.ui.converter.app

import ch.qos.logback.classic.Logger
import io.ktor.application.Application
import io.ktor.application.call
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
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import org.slf4j.event.Level
import ru.datana.smart.common.ktor.kafka.KtorKafkaConsumer
import ru.datana.smart.common.ktor.kafka.kafka
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.converter.app.common.MetalRateEventGenerator
import ru.datana.smart.ui.converter.app.cor.context.ConverterBeContext
import ru.datana.smart.ui.converter.app.cor.repository.UserEventsRepository
import ru.datana.smart.ui.converter.app.websocket.WsManager
import ru.datana.smart.ui.converter.app.mappings.toInnerModel
import java.time.Duration
import ru.datana.smart.ui.converter.app.cor.services.ForwardServiceKafkaUi

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
