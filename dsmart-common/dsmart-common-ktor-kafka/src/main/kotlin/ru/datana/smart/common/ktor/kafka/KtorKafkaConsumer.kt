package ru.datana.smart.common.ktor.kafka

import io.ktor.application.*
import io.ktor.config.*
import io.ktor.util.*
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import java.util.*
import kotlin.coroutines.CoroutineContext

class KtorKafkaConsumer(val kafkaConsumer: KafkaConsumer<String, ByteArray>) : CoroutineScope {

    private val parent: CompletableJob = Job()

    override val coroutineContext: CoroutineContext
        get() = parent

    private fun shutdown() {
        parent.complete()
    }

    /**
     * Kafka configuration options
     */
    class KafkaOptions {
        var kafkaConsumer: KafkaConsumer<String, ByteArray>? = null
        var kafkaBrokers: String? = null
        var kafkaClientId: String? = null
        var kafkaGroupId: String? = null
        var kafkaKeyDeserializer: Class<Any>? = null
        var kafkaValueDeserializer: Class<Any>? = null
    }

    /**
     * Feature installation object
     */
    companion object Feature : ApplicationFeature<Application, KafkaOptions, KtorKafkaConsumer> {
        override val key = AttributeKey<KtorKafkaConsumer>("KafkaConsumer")

        @KtorExperimentalAPI
        override fun install(pipeline: Application, configure: KafkaOptions.() -> Unit): KtorKafkaConsumer {
            val kafkaOptions = KafkaOptions().apply(configure)
            val kafkaConsumer = kafkaOptions.kafkaConsumer ?: kafkaOptions.run {
                createConsumer(
                    kafkaBrokers,
                    kafkaClientId,
                    kafkaGroupId,
                    kafkaKeyDeserializer,
                    kafkaValueDeserializer,
                    pipeline.environment.config
                )
            }
            val ktorKafkaConsumer = KtorKafkaConsumer(kafkaConsumer)

            pipeline.environment.monitor.subscribe(ApplicationStopPreparing) {
                kafkaConsumer.close()
                ktorKafkaConsumer.shutdown()
            }
            return ktorKafkaConsumer
        }
    }
}

@KtorExperimentalAPI
private fun createConsumer(
    kafkaBrokers: String?,
    kafkaClientId: String?,
    kafkaGroupId: String?,
    kafkaKeyDeserializer: Class<Any>?,
    kafkaValueDeserializer: Class<Any>?,
    appConfig: ApplicationConfig
): KafkaConsumer<String, ByteArray> {
    val props = Properties()
    props["bootstrap.servers"] = kafkaBrokers ?: appConfig.property("ktor.kafka.bootstrap.servers").getString()
    props["client.id"] = kafkaClientId ?: appConfig.property("ktor.kafka.client.id").getString()
    props["group.id"] = kafkaGroupId ?: appConfig.property("ktor.kafka.consumer.group.id").getString()
    props["key.deserializer"] = kafkaKeyDeserializer ?: StringDeserializer::class.java
    props["value.deserializer"] = kafkaValueDeserializer ?: ByteArrayDeserializer::class.java
    return KafkaConsumer(props)
}


