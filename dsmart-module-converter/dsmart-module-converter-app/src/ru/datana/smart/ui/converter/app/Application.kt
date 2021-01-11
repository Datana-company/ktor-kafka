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
import of
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.event.Level
import ru.datana.smart.common.ktor.kafka.KtorKafkaConsumer
import ru.datana.smart.common.ktor.kafka.TestConsumer
import ru.datana.smart.common.ktor.kafka.kafka
import ru.datana.smart.converter.transport.math.ConverterTransportMlUiOuterClass
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.converter.app.common.EventMode
import ru.datana.smart.ui.converter.app.mappings.*
import ru.datana.smart.ui.converter.app.websocket.WsManager
import ru.datana.smart.ui.converter.app.websocket.WsSignalerManager
import ru.datana.smart.ui.converter.backend.ConverterFacade
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorError
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.CurrentState
import java.time.Duration
import ru.datana.smart.ui.converter.common.models.ScheduleCleaner
import ru.datana.smart.ui.converter.repository.inmemory.EventRepositoryInMemory
import ru.datana.smart.ui.converter.repository.inmemory.currentstate.CurrentStateRepositoryInMemory
import java.time.Instant
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@OptIn(ExperimentalTime::class)
@Suppress("unused") // Referenced in application.conf
@KtorExperimentalAPI
fun Application.module(
    testing: Boolean = false,
    kafkaMetaConsumer: Consumer<String, String>? = null,
    kafkaMathConsumer: Consumer<String, ByteArray>? = null
) {
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

    install(KtorKafkaConsumer) {

    }

    val wsManager = WsManager()
    val wsSignalerManager = WsSignalerManager()
    val topicMeta by lazy { environment.config.property("ktor.kafka.consumer.topic.meta").getString().trim() }
    val topicMath by lazy { environment.config.property("ktor.kafka.consumer.topic.math").getString().trim() }
    val topicAngles by lazy { environment.config.property("ktor.kafka.consumer.topic.angles").getString().trim() }
    val topicEvents by lazy { environment.config.property("ktor.kafka.consumer.topic.events").getString().trim() }
    val converterId by lazy { environment.config.property("ktor.datana.converter.id").getString().trim() }
//    val framesBasePath by lazy { environment.config.property("paths.base.frames").getString().trim() }
    val eventMode: EventMode by lazy {
        EventMode.valueOf(
            environment.config.propertyOrNull("ktor.conveyor.eventMode")
                ?.getString()?.trim() ?: "STEEL"
        )
    }
    val dataTimeout: Long by lazy {
        environment.config.propertyOrNull("ktor.conveyor.dataTimeout")
            ?.getString()?.trim()?.toLong() ?: 3000
    }
    val meltTimeout: Long by lazy {
        environment.config.propertyOrNull("ktor.conveyor.meltTimeout")
            ?.getString()?.trim()?.toLong() ?: 10000
    }
    val streamRateCriticalPoint: Double by lazy {
        environment.config.property("ktor.conveyor.streamRatePoint.critical").getString().trim().toDouble()
    }
    val streamRateWarningPoint: Double by lazy {
        environment.config.property("ktor.conveyor.streamRatePoint.warning").getString().trim().toDouble()
    }
    val reactionTime: Long by lazy {
        environment.config.propertyOrNull("ktor.conveyor.reactionTime")
            ?.getString()?.trim()?.toLong() ?: 3000
    }
    val sirenLimitTime: Long by lazy {
        environment.config.propertyOrNull("ktor.conveyor.sirenLimitTime")
            ?.getString()?.trim()?.toLong() ?: 3000
    }
    val roundingWeight: Double by lazy {
        environment.config.property("ktor.conveyor.roundingWeight").getString().trim().toDouble()
    }
    val eventStorageDuration: Int by lazy {
        environment.config.propertyOrNull("ktor.repository.inmemory.event.storageDuration")
            ?.getString()?.trim()?.toInt() ?: 10
    }
    val stateStorageDuration: Int by lazy {
        environment.config.propertyOrNull("ktor.repository.inmemory.state.storageDuration")
            ?.getString()?.trim()?.toInt() ?: 2
    }

    val slagRatesTimeLimit: Long by lazy {
        environment.config.propertyOrNull("ktor.repository.inmemory.state.timeLimit")
            ?.getString()?.trim()?.toLong() ?: 60
    }

    // TODO: в будущем найти место, куда пристроить генератор
//    val metalRateEventGenerator = MetalRateEventGenerator(
//        timeout = metalRateEventGenTimeout,
//        maxValue = metalRateEventGenMax,
//        minValue = metalRateEventGenMin,
//        changeValue = metalRateEventGenChange
//    )
//    metalRateEventGenerator.start()

    val eventRepository = EventRepositoryInMemory(ttl = eventStorageDuration.toDuration(DurationUnit.MINUTES))
    val currentStateRepository = CurrentStateRepositoryInMemory(
        ttl = stateStorageDuration.toDuration(DurationUnit.HOURS),
        converterId = converterId,
        timeLimit = slagRatesTimeLimit
    )

    val currentState: AtomicReference<CurrentState> = AtomicReference(CurrentState.NONE)
    val scheduleCleaner: AtomicReference<ScheduleCleaner> = AtomicReference(ScheduleCleaner.NONE)

    val websocketContext = ConverterBeContext(
        converterId = converterId,
        eventRepository = eventRepository,
        currentStateRepository = currentStateRepository,
        streamRateWarningPoint = streamRateWarningPoint,
        sirenLimitTime = sirenLimitTime
    )

    val converterFacade = ConverterFacade(
        currentStateRepository = currentStateRepository,
        eventRepository = eventRepository,
        wsManager = wsManager,
        wsSignalerManager = wsSignalerManager,
        dataTimeout = dataTimeout,
        eventMode = toEventMode(eventMode),
        meltTimeout = meltTimeout,
        streamRateCriticalPoint = streamRateCriticalPoint,
        streamRateWarningPoint = streamRateWarningPoint,
        reactionTime = reactionTime,
        sirenLimitTime = sirenLimitTime,
        roundingWeight = roundingWeight,
        currentState = currentState,
        converterId = converterId,
//        framesBasePath = framesBasePath,
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

        kafka<String, ByteArray> {
            pollInterval = 60L
            keyDeserializer = StringDeserializer::class.java
            valDeserializer = ByteArrayDeserializer::class.java
            consumer = kafkaMathConsumer
            topic(topicMath) {
                items.items
                    .map {
                        try {
                            val mathTransport =
                                ConverterTransportMlUiOuterClass.ConverterTransportMlUi.parseFrom(it.value)
                            ConverterBeContext(
                                timeStart = Instant.now(),
                                topic = it.topic
                            ).of(mathTransport)
                        } catch (e: Throwable) {
                            val ctx = ConverterBeContext(
                                timeStart = Instant.now(),
                                topic = it.topic,
                                errors = mutableListOf(CorError(message = e.message ?: "")),
                                status = CorStatus.ERROR
                            )
                            logger.error(
                                msg = "Kafka message parsing error",
                                data = object {
                                    val metricType = "converter-backend-KafkaController-error-math"

                                    //                        val mathModel = kafkaModel
                                    val topic = ctx.topic
                                    val error = ctx.errors
                                },
                            )
                            ctx
                        }
                    }
                    .forEach {
                        converterFacade.handleMath(it)
                    }
            }
        }

        kafka<String, String> {
            consumer = kafkaMetaConsumer
            pollInterval = 500
            topics(topicMeta, topicAngles, topicEvents) {
                try {
                    records
                        .sortedByDescending { it.offset() }
//                на самом деле они уже отсортированы сначала по топику, затем по offset по убыванию
//                    TODO Это ерунда. Никакого отношения к времени offset не имеет и порядок не гарантирован
//                    Нужно сначала сконвертировать объект в контекст и уже только потом делать сортировку и
//                    фильтрацию по реальному времени
                        .distinctBy { it.topic() }
                        .map { it.toInnerModel() }
                        .forEach { record ->
                            when (val topic = record.topic) {
                                // получаем данные из топика мета
                                topicMeta -> {
                                    // десериализация данных из кафки
                                    val kafkaModel = toConverterMeltInfo(record)
                                    // логирование
                                    logger.biz(
                                        msg = "Meta model object got",
                                        data = object {
                                            val metricType = "converter-backend-KafkaController-got-meta"
                                            val metaModel = kafkaModel
                                            val topic = topic
                                        },
                                    )
                                    // инициализация контекста
                                    val context = ConverterBeContext(
                                        timeStart = Instant.now(),
                                        topic = topic
                                    )
                                    // маппинг траспортной модели во внутренную модель конвейера и добавление её в контекст
                                    context.setMeltInfo(kafkaModel)
                                    println("topic = meta, currentMeltId = ${currentState.get().currentMeltInfo.id}, meltId = ${context.meltInfo.id}")
                                    // вызов цепочки обработки меты
                                    converterFacade.handleMeltInfo(context)
                                }
                                // получаем данные из топика углов
                                topicAngles -> {
                                    // десериализация данных из кафки
                                    val kafkaModel = toConverterTransportAngle(record)
                                    // логирование
                                    logger.biz(
                                        msg = "Angles model object got",
                                        data = object {
                                            val metricType = "converter-backend-KafkaController-got-angle"
                                            val anglesModel = kafkaModel
                                            val topic = topic
                                        },
                                    )
                                    // инициализация контекста
                                    val context = ConverterBeContext(
                                        timeStart = Instant.now(),
                                        topic = topic
                                    )
                                    // маппинг траспортных моделей во внутренние модели конвейера и добавление их в контекст
                                    context.setAngles(kafkaModel)
                                    context.setMeltInfo(kafkaModel)
                                    println("topic = angles, currentMeltId = ${currentState.get().currentMeltInfo.id}, meltId = ${context.meltInfo.id}")
                                    // вызов цепочки обработки углов
                                    converterFacade.handleAngles(context)
                                }
                                // получаем данные из топика с внешними событиями
                                topicEvents -> {
                                    // десериализация данных из кафки
                                    val kafkaModel = toConverterTransportExternalEvents(record)
                                    // логирование
                                    logger.biz(
                                        msg = "Events model object got",
                                        data = object {
                                            val metricType = "converter-backend-KafkaController-got-event"
                                            val eventsModel = kafkaModel
                                            val topic = topic
                                        },
                                    )
                                    // инициализация контекста
                                    val context = ConverterBeContext(
                                        timeStart = Instant.now(),
                                        topic = topic
                                    )
                                    // маппинг траспортной модели во внутренную модель конвейера и добавление её в контекст
                                    context.setExternalEvent(kafkaModel)
                                    println("topic = events, currentMeltId = ${currentState.get().currentMeltInfo.id}, meltId = ${context.meltInfo.id}")
                                    // вызов цепочки обработки внешних событий
                                    converterFacade.handleExternalEvents(context)
                                }
                            }
                        }
                } catch (e: Throwable) {
                    val msg = e.message ?: ""
                    logger.error(msg)
                } finally {
                    commitAll()
                }
            }
        }
    }
}
