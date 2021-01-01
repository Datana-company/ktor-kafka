package ru.datana.smart.common.ktor.kafka

import io.ktor.application.*
import io.ktor.config.*
import io.ktor.routing.*
import io.ktor.util.*
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import ru.datana.smart.logger.datanaLogger
import java.time.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

//fun Route.kafka(topics: Collection<String>, handle: suspend KtorKafkaConsumerContext.() -> Unit) {
@OptIn(KtorExperimentalAPI::class)
fun <K, V> Route.kafka(config: KafkaRouteConfig<K, V>.() -> Unit) {

    val feature = application.feature(KtorKafkaConsumer)
    val isClosed = AtomicBoolean(false)
    val appConfig = this@kafka.application.environment.config
    val log = datanaLogger(this::class.java)

//    val log = datanaLogger().getLogger("ru.datana.smart.common.ktor.kafka:Route.kafka")

    feature.launch {
        val routeConfig = KafkaRouteConfig<K, V>(
            pollInterval = appConfig.propertyOrNull("ktor.kafka.consumer.poll_ms")?.getString()?.toLongOrNull() ?: 60L,
            brokers = appConfig.propertyOrNull("ktor.kafka.bootstrap.servers")?.getString() ?: "localhost:9092",
            clientId = appConfig.propertyOrNull("ktor.kafka.client.id")?.getString() ?: "",
            groupId = appConfig.propertyOrNull("ktor.kafka.consumer.group.id")?.getString() ?: "",
            keyDeserializer = StringDeserializer::class.java,
            valDeserializer = ByteArrayDeserializer::class.java
        ).apply(config)
        val consumer = routeConfig.consumer ?: run {
            val props = Properties()
            props["bootstrap.servers"] = routeConfig.brokers
            props["client.id"] = routeConfig.clientId
            props["group.id"] = routeConfig.groupId
            props["key.deserializer"] = routeConfig.keyDeserializer
            props["value.deserializer"] = routeConfig.valDeserializer
            KafkaConsumer(props)
        }

        val handlers = routeConfig.topicHandlers
        consumer.subscribe(handlers.map { it.topic }.toList())

        while (!isClosed.get()) {
            val records = consumer.poll(Duration.ofMillis(routeConfig.pollInterval))
            if (!records.isEmpty) {
                println("records cycle -")
                log.debug("Pulled records: {}", records.count())
                println("records cycle |")
                handlers.forEach { handlerObj ->
                    val handler = handlerObj.handler
                    KtorKafkaConsumerContext(consumer, records)
                        .apply { this.handler() }
                }
            } else {
                log.info("No records pulled")
            }
        }

        consumer.close()
    }

//    val feature = application.feature(KtorKafkaConsumer)
//    val consumer = feature.kafkaConsumer
//    val isClosed = AtomicBoolean(false)
//    val log = LoggerFactory.getLogger("ru.datana.smart.common.ktor.kafka:Route.kafka")
//
//    feature.launch {
//        try {
//            consumer.subscribe(topics)
//
//            while (!isClosed.get()) {
//                val records = consumer.poll(Duration.ofSeconds(1))
//                if (!records.isEmpty) {
//                    log.debug("Pulled records: {}", records.count())
//                    KtorKafkaConsumerContext(consumer, records)
//                        .handle()
//                } else {
//                    log.debug("No records pulled")
//                }
//            }
//        } catch (e: Throwable) {
//            log.debug("Caught exception: {}", e.stackTrace)
//            when (e) {
//                is WakeupException -> log.debug("Consumer waked up")
//                else -> log.debug("Polling failed, caught exception: {}", e.stackTrace)
//            }
//        } finally {
//            log.debug("Commit offset synchronously")
//            consumer.commitSync()
//            consumer.close()
//            log.debug("Consumer successfully closed")
//        }
//    }
}

