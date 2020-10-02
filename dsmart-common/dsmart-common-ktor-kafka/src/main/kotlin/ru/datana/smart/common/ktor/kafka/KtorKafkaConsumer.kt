package ru.datana.smart.common.ktor.kafka

import com.typesafe.config.ConfigFactory
import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.application.ApplicationStopPreparing
import io.ktor.config.HoconApplicationConfig
import io.ktor.util.AttributeKey
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.util.*
import kotlin.coroutines.CoroutineContext

class KtorKafkaConsumer(val kafkaConsumer: KafkaConsumer<String, String>) : CoroutineScope {

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
        var kafkaBrokers: Collection<String>? = null
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
            val kafkaConsumer = kafkaOptions.run {
                createConsumer(
                    kafkaBrokers,
                    kafkaClientId,
                    kafkaGroupId,
                    kafkaKeyDeserializer,
                    kafkaValueDeserializer
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
    kafkaBrokers: Collection<String>?,
    kafkaClientId: String?,
    kafkaGroupId: String?,
    kafkaKeyDeserializer: Class<Any>?,
    kafkaValueDeserializer: Class<Any>?
): KafkaConsumer<String, String> {
    val appConfig = HoconApplicationConfig(ConfigFactory.load())
    val props = Properties()
    props["bootstrap.servers"] = kafkaBrokers ?: appConfig.property("ktor.kafka.bootstrap.servers").getList()
    props["client.id"] = kafkaClientId ?: appConfig.property("ktor.kafka.client.id").getString()
    props["group.id"] = kafkaGroupId ?: appConfig.property("ktor.kafka.consumer.group.id").getString()
    props["key.deserializer"] = kafkaKeyDeserializer ?: StringDeserializer::class.java
    props["value.deserializer"] = kafkaValueDeserializer ?: StringDeserializer::class.java
    return KafkaConsumer(props)
}


