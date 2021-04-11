package ru.datana.smart.common.ktor.kafka

import io.ktor.application.*
import io.ktor.config.*
import io.ktor.routing.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

private val consumersCount = AtomicInteger(0)

@OptIn(KtorExperimentalAPI::class)
fun <K, V> Route.kafka(config: KafkaRouteConfig<K, V>.() -> Unit) {

    val feature = application.feature(KtorKafkaConsumer)
    val isClosed = AtomicBoolean(false)
    val appConfig = this@kafka.application.environment.config
    val log = LoggerFactory.getLogger("ru.datana.smart.common.ktor.kafka.Route.kafka")
    val consumerCoroutineContext = newSingleThreadContext("KtorKafkaConsumer-${consumersCount.getAndIncrement()}")

    feature.launch(context = consumerCoroutineContext) {
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
            props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = routeConfig.brokers
            props[ConsumerConfig.CLIENT_ID_CONFIG] = routeConfig.clientId
            props[ConsumerConfig.GROUP_ID_CONFIG] = routeConfig.groupId
            props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = routeConfig.keyDeserializer
            props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = routeConfig.valDeserializer
            props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
            props[ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG] = 1048576
            props[ConsumerConfig.FETCH_MAX_BYTES_CONFIG] = 52428800
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
            val records = try {
                consumer.poll(Duration.ofMillis(routeConfig.pollInterval))
            } catch (e: Throwable) {
                log.error("Error polling data from $topics", e)
                throw e
            }
            log.trace("after consumer poll, {}", topics)
            if (!records.isEmpty) {
                log.debug("Pulled {} records from topics {}", records.count(), topics)
                withContext(Dispatchers.Default) {
                    handlers.forEach { handlerObj ->
                        try {
                            val handler = handlerObj.handler
                            val curTopic = handlerObj.topic
                            val currentRecords = records.partitions()
                                .filter { it.topic() == curTopic }
                                .associateWith { records.records(it) }
                            KtorKafkaConsumerContext(consumer, ConsumerRecords(currentRecords))
                                .apply { this.handler() }
                        } catch (e: Throwable) {
                            log.error("Error handling kafka records from topics $topics", e)
                        }
                    }
                }
            } else {
                log.trace("No records pulled from topics {}", topics)
            }
        }

        consumer.close()
    }
}
