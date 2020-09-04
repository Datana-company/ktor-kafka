package ru.datana.smart.common.ktor.kafka


import io.ktor.application.Application
import io.ktor.application.ApplicationEnvironment
import io.ktor.application.ApplicationFeature
import io.ktor.application.ApplicationStopPreparing
import io.ktor.util.AttributeKey
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.apache.kafka.clients.consumer.ConsumerConfig
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
class Kafka(
    val pingIntervalMillis: Long,
    val timeoutMillis: Long,
    val masking: Boolean
) : CoroutineScope {
    private val parent: CompletableJob = Job()

    override val coroutineContext: CoroutineContext
        get() = parent

    init {
        require(pingIntervalMillis >= 0)
        require(timeoutMillis >= 0)
    }

    private fun shutdown() {
        parent.complete()
    }

    /**
     * Websockets configuration options
     */
    class KafkaOptions {

        /**
         * Duration between pings or `0` to disable pings
         */
        var pingPeriodMillis: Long = 0


        /**
         * write/ping timeout after that a connection will be closed
         */
        var timeoutMillis: Long = 15000L

        /**
         * Maximum frame that could be received or sent
         */
        var maxFrameSize: Long = Long.MAX_VALUE

        /**
         * Whether masking need to be enabled (useful for security)
         */
        var masking: Boolean = false
        var kafkaConsumer: KafkaConsumer<String, String>? = null
    }

    /**
     * Feature installation object
     */
    companion object Feature : ApplicationFeature<Application, KafkaOptions, Kafka> {
        override val key = AttributeKey<Kafka>("Kafka")

        override fun install(pipeline: Application, configure: KafkaOptions.() -> Unit): Kafka {
            val config = KafkaOptions().also(configure)
            with(config) {
                val kafka = Kafka(pingPeriodMillis, timeoutMillis, masking)

                pipeline.environment.monitor.subscribe(ApplicationStopPreparing) {
                    kafka.shutdown()
                }

                kafkaConsumer = buildConsumer(pipeline.environment)

                return kafka
            }
        }

        //@OptIn(KtorExperimentalAPI::class)
        fun buildConsumer(environment: ApplicationEnvironment): KafkaConsumer<String, String> {
            val consumerConfig = environment.config.config("ktor.kafka.consumer")
            val kafkaConfig = environment.config.config("ktor.kafka")
            val consumerProps = Properties().apply {
                put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.property("bootstrap.servers").getList())
                put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString())

                put(ConsumerConfig.GROUP_ID_CONFIG, consumerConfig.property("group.id").getString())
                //        this[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = consumerConfig.property("key.deserializer").getString()
                //        this[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = consumerConfig.property("value.deserializer").getString()
                put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
                put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
            }
            return KafkaConsumer<String, String>(consumerProps)
                .apply {
                    subscribe(listOf(consumerConfig.property("topic").getString()))
                }
        }

    }


}
