package ru.datana.smart.ui.converter.angle.app

import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.util.*
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import ru.datana.smart.common.ktor.kafka.KtorKafkaConsumer
import ru.datana.smart.common.ktor.kafka.kafka
import ru.datana.smart.converter.transport.math.ConverterTransportMlUiOuterClass
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.converter.angle.app.cor.ConverterAngLogics
import ru.datana.smart.ui.converter.angle.app.mappings.of
import ru.datana.smart.ui.converter.angle.app.mappings.toAngleMessage
import ru.datana.smart.ui.converter.angle.app.models.AngleSchedule
import ru.datana.smart.ui.converter.angle.app.models.ConverterAngContext
import ru.datana.smart.ui.converter.common.context.CorError
import ru.datana.smart.ui.converter.common.context.CorStatus
import java.time.Instant
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
@kotlin.jvm.JvmOverloads
@KtorExperimentalAPI
fun Application.module(
    testing: Boolean = false,
    kafkaMetaConsumer: Consumer<String, String>? = null,
    kafkaMathConsumer: Consumer<String, ByteArray>? = null,
    kafkaAnglesProducer: Producer<String, String>? = null
) {

    val logger = datanaLogger(::main::class.java)

    install(KtorKafkaConsumer)

    val scheduleBasePath by lazy { environment.config.property("paths.schedule.base").getString().trim() }
    val topicMeta by lazy { environment.config.property("ktor.kafka.consumer.topic.meta").getString().trim() }
    val topicMath by lazy { environment.config.property("ktor.kafka.consumer.topic.math").getString().trim() }
    val angleSchedule = AngleSchedule()
    val kafkaServers: String by lazy {
        environment.config.property("ktor.kafka.bootstrap.servers").getString().trim()
    }
    val topicAngle: String by lazy {
        environment.config.property("ktor.kafka.producer.topic.angle").getString().trim()
    }
    val converterId by lazy { environment.config.property("ktor.datana.converter.id").getString().trim() }
    val kafkaProducer by lazy {
        kafkaAnglesProducer ?: run {
            val props = Properties().apply {
                put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers)
                put("acks", "all")
                put("retries", 3)
                put("batch.size", 16384)
                put("linger.ms", 1)
                put("buffer.memory", 33554432)
                put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
                put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
            }
            KafkaProducer(props)
        }
    }

    val chain = ConverterAngLogics(
        converterId = converterId,
        producer = kafkaProducer,
        angleSchedule = angleSchedule,
        topicAngle = topicAngle,
        scheduleBasePath = scheduleBasePath
    )

    routing {

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
                            val ctx = ConverterAngContext(
                                timeStart = Instant.now(),
                                topic = it.topic
                            ).of(mathTransport)
                            logger.biz(
                                msg = "Math model object got",
                                data = object {
                                    val metricType = "converter-angles-KafkaController-got-math"
                                    val metaModel = ctx.meltInfo
                                    val topic = it.topic
                                },
                            )
                            ctx
                        } catch (e: Throwable) {
                            val ctx = ConverterAngContext(
                                timeStart = Instant.now(),
                                topic = it.topic,
                                errors = mutableListOf(CorError(message = e.message ?: "")),
                                status = CorStatus.ERROR
                            )
                            logger.error(
                                msg = "Kafka message parsing error",
                                data = object {
                                    val metricType = "converter-angles-KafkaController-error-math"
                                    //                        val mathModel = kafkaModel
                                    val topic = ctx.topic
                                    val error = ctx.errors
                                },
                            )
                            ctx
                        }
                    }
                    .forEach { ctx ->
                        chain.exec(ctx)
                        ctx.toAngleMessage()?.also { record ->
                            kafkaProducer.send(record)
                            logger.biz(
                                msg = "Angles model object sent",
                                data = object {
                                    val metricType = "converter-angles-KafkaController-send-andles"
                                    val metaModel = ctx.meltInfo
                                    val topic = ctx.topic
                                },
                            )
                        } ?: logger.debug("No data to send")
                    }
                commitAll()
            }
        }

        kafka<String, String> {
            consumer = kafkaMetaConsumer
            pollInterval = 500
            topic(topicMeta) {
                items.items
                    .map {
                        val ctx = ConverterAngContext(
                            timeStart = Instant.now(),
                            topic = it.topic
                        ).of(it)
                        logger.biz(
                            msg = "Meta model object got",
                            data = object {
                                val metricType = "converter-angles-KafkaController-got-meta"
                                val metaModel = ctx.meltInfo
                                val topic = it.topic
                            },
                        )
                        ctx
                    }
                    .forEach { ctx ->
                        // вызов цепочки обработки меты
                        chain.exec(ctx)
                    }
                commitAll()
            }
        }
    }
}
