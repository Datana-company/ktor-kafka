package ru.datana.smart.common.ktor.kafka

import io.ktor.application.*
import io.ktor.config.*
import io.ktor.routing.*
import io.ktor.util.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

@OptIn(KtorExperimentalAPI::class)
fun <K, V> Route.kafka(config: KafkaRouteConfig<K, V>.() -> Unit) {

    val feature = application.feature(KtorKafkaConsumer)
    val isClosed = AtomicBoolean(false)
    val appConfig = this@kafka.application.environment.config
    val log = LoggerFactory.getLogger("ru.datana.smart.common.ktor.kafka.Route.kafka")

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
        val topics = handlers.map { it.topic }.toList()
        try {
            consumer.subscribe(topics)
        } catch (e: Throwable) {
            log.error("Error subscribing topics $topics", e)
            throw e
        }

        while (!isClosed.get()) {
            log.trace("before consumer poll {}", topics)
            val records = try {
                withTimeout(1000L) {
                    consumer.poll(Duration.ofMillis(routeConfig.pollInterval))
                }
            } catch (e: Throwable) {
                log.error("Error polling data from $topics", e)
                throw e
            }
            log.trace("after consumer poll", topics)
            if (!records.isEmpty) {
                log.debug("Pulled {} records from topics {}", records.count(), topics)
                handlers.forEach { handlerObj ->
                    try {
                        log.trace("before handle init", topics)
                        val handler = handlerObj.handler
                        log.trace("after handler init", topics)
                        KtorKafkaConsumerContext(consumer, records)
                            .apply { this.handler() }
                        log.trace("record handling finished", topics)
                    } catch (e: Throwable) {
                        log.error("Error handling kafka records from topics $topics", e)
                    }
                }
            } else {
                log.trace("No records pulled from topics {}", topics)
            }
        }
        log.trace("before consumer close, {}", topics)
        consumer.close()
        log.trace("after consumer close, {}", topics)
    }
}
