package ru.datana.smart.common.ktor.kafka


import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.application.ApplicationStopPreparing
import io.ktor.util.AttributeKey
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * Kafka support feature. It is required to be installed first before binding any kafka endpoints
 *
 * ```
 * install(Kafka)
 *
 * install(Routing) {
 *     webSocket("/ws") {
 *          incoming.consumeForEach { ... }
 *     }
 * }
 * ```
 *
 * @param pingIntervalMillis duration between pings or `null` to disable pings
 * @param timeoutMillis write/ping timeout after that a connection will be closed
 * @param maxFrameSize maximum frame that could be received or sent
 * @param masking whether masking need to be enabled (useful for security)
 */
class KafkaConsumer(
    val paramKafkaConsumer: KafkaConsumer<String, String>
) : CoroutineScope {
    private val parent: CompletableJob = Job()

    override val coroutineContext: CoroutineContext
        get() = parent

    init {
    }


    /**
     * Websockets configuration options
     */
    class KafkaOptions {
        var kafkaBrokersAsString: String = "localhost:9092"
        var kafkaTopicForListener: String = "ui-temperature"
        var kafkaClientId: String = "ui-client-kafka"
        var kafkaGroupId: String = "ui-app-kafka"
    }

    /**
     * Feature installation object
     */
    companion object Feature : ApplicationFeature<Application, KafkaOptions, KafkaConsumer> {
        override val key = AttributeKey<KafkaConsumer>("Kafka")

        override fun install(pipeline: Application, configure: KafkaOptions.() -> Unit): KafkaConsumer {
            val config = KafkaOptions().also(configure)
            with(config) {
                val kafkaConsumer = createConsumer(kafkaBrokersAsString, kafkaGroupId, kafkaClientId)
                kafkaConsumer.apply {
                    subscribe(listOf(kafkaTopicForListener))
                }

                pipeline.environment.monitor.subscribe(ApplicationStopPreparing) {
                    kafkaConsumer.close()
                }
                return KafkaConsumer(kafkaConsumer)

            }


        }
    }
}

fun createConsumer(
    brokers: String,
    kafkaGroupId: String,
    kafkaClientId: String
): KafkaConsumer<String, String> {
    val props = Properties()
    props["bootstrap.servers"] = brokers
    props["group.id"] = kafkaGroupId
    props["client.id"] = kafkaClientId
    props["key.deserializer"] = StringDeserializer::class.java
    props["value.deserializer"] = StringDeserializer::class.java
    return KafkaConsumer<String, String>(props)
}


