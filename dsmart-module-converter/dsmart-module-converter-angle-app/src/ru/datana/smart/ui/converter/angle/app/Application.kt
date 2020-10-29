package ru.datana.smart.ui.converter.angle.app

import io.ktor.application.*
import io.ktor.routing.routing
import io.ktor.util.*
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import ru.datana.smart.common.ktor.kafka.KtorKafkaConsumer
import ru.datana.smart.common.ktor.kafka.kafka
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.converter.angle.app.cor.context.ConverterAngleContext
import ru.datana.smart.ui.converter.angle.app.cor.services.ForwardServiceKafka
import ru.datana.smart.ui.converter.angle.app.mappings.toInnerModel
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
@kotlin.jvm.JvmOverloads
@KtorExperimentalAPI
fun Application.module(testing: Boolean = false) {

    install(KtorKafkaConsumer) {
    }

    val logger = datanaLogger(::main::class.java)
    val scheduleBasePath by lazy { environment.config.property("paths.angle.base").getString().trim() }
    val topicMeta by lazy { environment.config.property("ktor.kafka.consumer.topic.meta").getString().trim() }
    val kafkaServers: String by lazy {
        environment.config.property("ktor.kafka.bootstrap.servers").getString().trim()
    }
    val topicAngles: String by lazy {
        environment.config.property("ktor.kafka.producer.topic.angles").getString().trim()
    }
    val kafkaProducer: KafkaProducer<String, String> by lazy {
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
        KafkaProducer<String, String>(props)
    }

    routing {

        kafka(listOf(topicMeta)) {
            val context = ConverterAngleContext(
                records = records.map { it.toInnerModel() }
            )
            ForwardServiceKafka(
                logger = logger,
                scheduleBasePath = scheduleBasePath,
                topicAngles = topicAngles,
                kafkaProducer = kafkaProducer,

            ).exec(context)

            commitAll()
        }
    }
}
