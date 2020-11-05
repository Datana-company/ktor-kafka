package ru.datana.smart.ui.converter.app

import io.ktor.application.Application
import io.ktor.application.install
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
import io.ktor.routing.*
import io.ktor.util.KtorExperimentalAPI
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import org.slf4j.event.Level
import ru.datana.smart.common.ktor.kafka.KtorKafkaConsumer
import ru.datana.smart.common.ktor.kafka.kafka
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.converter.app.common.MetalRateEventGenerator
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.app.mappings.*
import ru.datana.smart.ui.converter.app.websocket.WsManager
import ru.datana.smart.ui.converter.backend.ConverterFacade
import java.time.Duration
import ru.datana.smart.ui.converter.common.models.ModelMeltInfo
import ru.datana.smart.ui.converter.repository.inmemory.UserEventRepositoryInMemory
import java.util.concurrent.atomic.AtomicReference

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
    val topicMeta by lazy { environment.config.property("ktor.kafka.consumer.topic.meta").getString().trim() }
    val topicMath by lazy { environment.config.property("ktor.kafka.consumer.topic.math").getString().trim() }
    val topicVideo by lazy { environment.config.property("ktor.kafka.consumer.topic.video").getString().trim() }
    val topicAngles by lazy { environment.config.property("ktor.kafka.consumer.topic.angles").getString().trim() }
    val topicAlerts by lazy { environment.config.property("ktor.kafka.consumer.topic.alerts").getString().trim() }
    val converterId by lazy { environment.config.property("ktor.datana.converter.id").getString().trim() }
//    val metalRateEventGenTimeout: Long by lazy { environment.config.property("ktor.conveyor.metalRateEventGen.timeout").getString().trim().toLong() }
//    val metalRateEventGenMax: Double by lazy { environment.config.property("ktor.conveyor.metalRateEventGen.maxValue").getString().trim().toDouble() }
//    val metalRateEventGenMin: Double by lazy { environment.config.property("ktor.conveyor.metalRateEventGen.minValue").getString().trim().toDouble() }
//    val metalRateEventGenChange: Double by lazy { environment.config.property("ktor.conveyor.metalRateEventGen.changeValue").getString().trim().toDouble() }
    val metalRateCriticalPoint: Double by lazy { environment.config.property("ktor.conveyor.metalRatePoint.critical").getString().trim().toDouble() }
    val metalRateWarningPoint: Double by lazy { environment.config.property("ktor.conveyor.metalRatePoint.warning").getString().trim().toDouble() }

    // TODO: в будущем найти место, куда пристроить генератор
//    val metalRateEventGenerator = MetalRateEventGenerator(
//        timeout = metalRateEventGenTimeout,
//        maxValue = metalRateEventGenMax,
//        minValue = metalRateEventGenMin,
//        changeValue = metalRateEventGenChange
//    )
//    metalRateEventGenerator.start()

    val userEventsRepository = UserEventRepositoryInMemory()

    val currentMeltInfo: AtomicReference<ModelMeltInfo?> = AtomicReference()

    val websocketContext = ConverterBeContext(
        currentMeltInfo = currentMeltInfo,
        eventsRepository = userEventsRepository
    )

    val converterFacade = ConverterFacade(
        converterRepository = userEventsRepository,
        wsManager = wsManager,
        metalRateCriticalPoint = metalRateCriticalPoint,
        metalRateWarningPoint = metalRateWarningPoint,
        currentMeltInfo = currentMeltInfo,
        converterId = converterId
    )

    routing {
        static("/") {
            defaultResource("static/index.html")
            resources("static")
        }

        webSocket("/ws") {
            println("onConnect")
            wsManager.addSession(this, websocketContext)
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

        kafka(listOf(topicMath, topicVideo, topicMeta, topicAngles)) {
            try {
                val innerModel = records.map { it.toInnerModel() }
                val record = innerModel.firstOrNull()
                when (record?.topic) {
                    topicMath -> {
                        val kafkaModel = toConverterTransportMlUi(record)
                        val conveyorModelSlagRate = toModelSlagRate(kafkaModel)
                        val conveyorModelFrame = toModelFrame(kafkaModel)
                        val conveyorModelMeltInfo = toModelMeltInfo(kafkaModel)
                        val context = ConverterBeContext(
                            slagRate = conveyorModelSlagRate,
                            frame = conveyorModelFrame,
                            meltInfo = conveyorModelMeltInfo
                        )
                        converterFacade.handleMath(context)
                    }
                    topicVideo -> {
                        val kafkaModel = toConverterTransportViMl(record)
                        val conveyorModelFrame = toModelFrame(kafkaModel)
                        val conveyorModelMeltInfo = toModelMeltInfo(kafkaModel)
                        val context = ConverterBeContext(
                            frame = conveyorModelFrame,
                            meltInfo = conveyorModelMeltInfo
                        )
                        converterFacade.handleFrame(context)
                    }
                    topicMeta -> {
                        val kafkaModel = toConverterMeltInfo(record)
                        val conveyorModel = toModelMeltInfo(kafkaModel)
                        val context = ConverterBeContext(
                            meltInfo = conveyorModel
                        )
                        converterFacade.handleMeltInfo(context)
                    }
                    topicAngles -> {
                        val kafkaModel = toConverterTransportAngle(record)
                        val conveyorModelAngles = toModelAngles(kafkaModel)
                        val conveyorModelMeltInfo = toModelMeltInfo(kafkaModel)
                        val context = ConverterBeContext(
                            angles = conveyorModelAngles,
                            meltInfo = conveyorModelMeltInfo
                        )
                        converterFacade.handleAngles(context)
                    }
                }
            } catch(e: Throwable) {
                val msg = e.message ?: ""
                logger.error(msg)
            } finally {
                commitAll()
            }
        }
    }
}

// TODO: дописать метод
//suspend inline fun <reified T, reified K> KtorKafkaConsumerContext.handleMessage(block: (InnerRecord<String, String>) -> Unit) {
//    try {
//        val context = ConverterBeContext()
//
//        val kafkaModel = toConverterTransportMlUi(record)
//        val conveyorModel = toModelAnalysis(kafkaModel) // rate
////        val context = ConverterBeContext(
////            analysis = conveyorModel // добавить поля и маппинг функции
////        )
//        converterFacade.handleSlagRate(context)
//    } catch (e: Throwable) {
//
//    }
//}
