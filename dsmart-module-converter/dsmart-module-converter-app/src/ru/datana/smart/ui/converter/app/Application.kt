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
import ru.datana.smart.ui.converter.app.common.EventMode
//import ru.datana.smart.ui.converter.app.common.MetalRateEventGenerator
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.app.mappings.*
import ru.datana.smart.ui.converter.app.websocket.WsManager
import ru.datana.smart.ui.converter.app.websocket.WsSignalerManager
import ru.datana.smart.ui.converter.backend.ConverterFacade
import ru.datana.smart.ui.converter.common.models.CurrentState
import java.time.Duration
import ru.datana.smart.ui.converter.common.models.ScheduleCleaner
import ru.datana.smart.ui.converter.repository.inmemory.EventRepositoryInMemory
import java.time.Instant
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@OptIn(ExperimentalTime::class)
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
    val wsSignalerManager = WsSignalerManager()
    val topicMeta by lazy { environment.config.property("ktor.kafka.consumer.topic.meta").getString().trim() }
    val topicMath by lazy { environment.config.property("ktor.kafka.consumer.topic.math").getString().trim() }
    val topicVideo by lazy { environment.config.property("ktor.kafka.consumer.topic.video").getString().trim() }
    val topicAngles by lazy { environment.config.property("ktor.kafka.consumer.topic.angles").getString().trim() }
    val topicEvents by lazy { environment.config.property("ktor.kafka.consumer.topic.events").getString().trim() }
    val converterId by lazy { environment.config.property("ktor.datana.converter.id").getString().trim() }
    val framesBasePath by lazy { environment.config.property("paths.base.frames").getString().trim() }
//    val metalRateEventGenTimeout: Long by lazy { environment.config.property("ktor.conveyor.metalRateEventGen.timeout").getString().trim().toLong() }
//    val metalRateEventGenMax: Double by lazy { environment.config.property("ktor.conveyor.metalRateEventGen.maxValue").getString().trim().toDouble() }
//    val metalRateEventGenMin: Double by lazy { environment.config.property("ktor.conveyor.metalRateEventGen.minValue").getString().trim().toDouble() }
//    val metalRateEventGenChange: Double by lazy { environment.config.property("ktor.conveyor.metalRateEventGen.changeValue").getString().trim().toDouble() }
    val eventMode: EventMode by lazy {
        EventMode.valueOf(environment.config.property("ktor.conveyor.eventMode").getString().trim())
    }
    val dataTimeout: Long by lazy {
        environment.config.property("ktor.conveyor.dataTimeout").getString().trim().toLong()
    }
    val meltTimeout: Long by lazy {
        environment.config.property("ktor.conveyor.meltTimeout").getString().trim().toLong()
    }
    val streamRateCriticalPoint: Double by lazy {
        environment.config.property("ktor.conveyor.streamRatePoint.critical").getString().trim().toDouble()
    }
    val streamRateWarningPoint: Double by lazy {
        environment.config.property("ktor.conveyor.streamRatePoint.warning").getString().trim().toDouble()
    }
    val reactionTime: Long by lazy {
        environment.config.property("ktor.conveyor.reactionTime").getString().trim().toLong()
    }
    val sirenLimitTime: Long by lazy {
        environment.config.property("ktor.conveyor.sirenLimitTime").getString().trim().toLong()
    }
    val roundingWeight: Double by lazy {
        environment.config.property("ktor.conveyor.roundingWeight").getString().trim().toDouble()
    }
    val storageDuration: Int by lazy {
        environment.config.property("ktor.conveyor.repository.inmemory.storageDuration").getString().trim().toInt()
    }

    // TODO: в будущем найти место, куда пристроить генератор
//    val metalRateEventGenerator = MetalRateEventGenerator(
//        timeout = metalRateEventGenTimeout,
//        maxValue = metalRateEventGenMax,
//        minValue = metalRateEventGenMin,
//        changeValue = metalRateEventGenChange
//    )
//    metalRateEventGenerator.start()

    val userEventsRepository = EventRepositoryInMemory(ttl = storageDuration.toDuration(DurationUnit.MINUTES))

    val currentState: AtomicReference<CurrentState> = AtomicReference(CurrentState.NONE)
    val scheduleCleaner: AtomicReference<ScheduleCleaner> = AtomicReference(ScheduleCleaner.NONE)

    val websocketContext = ConverterBeContext(
        currentState = currentState,
        eventsRepository = userEventsRepository,
        streamRateWarningPoint = streamRateWarningPoint,
        sirenLimitTime = sirenLimitTime
    )

    val converterFacade = ConverterFacade(
        converterRepository = userEventsRepository,
        wsManager = wsManager,
        wsSignalerManager = wsSignalerManager,
        dataTimeout = dataTimeout,
        eventMode = toEventMode(eventMode),
        meltTimeout = meltTimeout,
        metalRateCriticalPoint = streamRateCriticalPoint,
        metalRateWarningPoint = streamRateWarningPoint,
        reactionTime = reactionTime,
        sirenLimitTime = sirenLimitTime,
        roundingWeight = roundingWeight,
        currentState = currentState,
        converterId = converterId,
        framesBasePath = framesBasePath,
        scheduleCleaner = scheduleCleaner
    )

    routing {
        static("/") {
            defaultResource("static/index.html")
            resources("static")
        }

        webSocket("/ws") {
            println("/ws --- onConnect")
            wsManager.addSession(this, websocketContext)
            wsSignalerManager.init(this, websocketContext)
            try {
                for (frame in incoming) {
                }
            } catch (e: ClosedReceiveChannelException) {
                println("onClose ${closeReason.await()}")
            } catch (e: Throwable) {
                logger.error("Error within websocket block due to: ${closeReason.await()}", e)
            } finally {
                wsManager.delSession(this)
                wsSignalerManager.close(this)
            }
        }

//        webSocket("/ws_signaler") {
//            println("/ws_signaler --- onConnect")
//            wsSignalerManager.init(this, websocketContext)
//            try {
//                for (frame in incoming) {
//                }
//            } catch (e: ClosedReceiveChannelException) {
//                println("onClose ${closeReason.await()}")
//            } catch (e: Throwable) {
//                logger.error("Error within websocket block due to: ${closeReason.await()}", e)
//            } finally {
//                wsSignalerManager.close(this)
//            }
//        }

        kafka(listOf(topicMath, topicVideo, topicMeta, topicAngles, topicEvents)) {
            try {
                records.sortedByDescending { it.offset() }
//                на самом деле они уже отсортированы сначала по топику, затем по offset по убыванию
                    .distinctBy { it.topic() }
                    .map { it.toInnerModel() }
                    .forEach { record ->
                        when (record.topic) {
                            topicMath -> {
                                val kafkaModel = toConverterTransportMlUi(record)
                                val context = ConverterBeContext(
                                    timeStart = Instant.now()
                                )
                                context.setSlagRate(kafkaModel)
                                context.setFrame(kafkaModel)
                                context.setMeltInfo(kafkaModel)
                                println("topic = math, currentMeltId = ${currentState.get().currentMeltInfo.id}, meltId = ${context.meltInfo.id}")
                                converterFacade.handleMath(context)
                            }
//                            topicVideo -> {
//                                val kafkaModel = toConverterTransportViMl(record)
//                                val context = ConverterBeContext(
//                                    timeStart = Instant.now()
//                                )
//                                context.setFrame(kafkaModel)
//                                context.setMeltInfo(kafkaModel)
//                                println("topic = video, currentMeltId = ${currentState.get().currentMeltInfo.id}, meltId = ${context.meltInfo.id}")
//                                converterFacade.handleFrame(context)
//                            }
                            topicMeta -> {
                                val kafkaModel = toConverterMeltInfo(record)
                                val context = ConverterBeContext(
                                    timeStart = Instant.now()
                                )
                                context.setMeltInfo(kafkaModel)
                                println("topic = meta, currentMeltId = ${currentState.get().currentMeltInfo.id}, meltId = ${context.meltInfo.id}")
                                converterFacade.handleMeltInfo(context)
                            }
                            topicAngles -> {
                                val kafkaModel = toConverterTransportAngle(record)
                                val context = ConverterBeContext(
                                    timeStart = Instant.now()
                                )
                                context.setAngles(kafkaModel)
                                context.setMeltInfo(kafkaModel)
                                println("topic = angles, currentMeltId = ${currentState.get().currentMeltInfo.id}, meltId = ${context.meltInfo.id}")
                                converterFacade.handleAngles(context)
                            }
                            // 1) Получаем данные из Кафки
//                            topicEvents -> {
                                // 2) Мапим полученные данные на модель (dsmart-module-converter-models-...) с помощью jackson.databind
//                                val kafkaModel = toConverterTransportExtEvents(record)
                                // 3) Конвертируем модель во внутреннюю модель (dsmart-module-converter-common.models)
//                                val context = ConverterBeContext(
//                                    timeStart = Instant.now(),
//                                    extEvents = conveyorModelExtEvents
//                                )
                                // 4) Запихиваем эту модель в контекст
//                                context.setExtEvent(kafkaModel)
                                // 5) Вызываем цепочку для обработки поступившего сообщения
//                                println("topic = events, currentMeltId = ${currentState.get().currentMeltInfo.id}, meltId = ${context.meltInfo.id}")
//                                converterFacade.handleExtEvents(context)
//                            }
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
