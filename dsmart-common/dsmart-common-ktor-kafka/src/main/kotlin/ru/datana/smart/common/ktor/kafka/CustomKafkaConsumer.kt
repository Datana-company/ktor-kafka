package ru.datana.smart.common.ktor.kafka


import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.application.ApplicationStopPreparing
import io.ktor.util.AttributeKey
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.util.*

class CustomKafkaConsumer(val kafkaConsumer: KafkaConsumer<String, String>) {

//    : CoroutineScope {
//    private val parent: CompletableJob = Job()
//
//    override val coroutineContext: CoroutineContext
//        get() = parent

    /**
     * Kafka configuration options
     */
    class KafkaOptions {
        var kafkaBrokersAsString: String = "localhost:9092"
        var kafkaClientId: String = "ui-client-kafka"
        var kafkaGroupId: String = "ui-app-kafka"
//        var kafkaTopicForListener: Collection<String> = listOf("ui-temperature")
    }

    /**
     * Feature installation object
     */
    companion object Feature : ApplicationFeature<Application, KafkaOptions, CustomKafkaConsumer> {
        override val key = AttributeKey<CustomKafkaConsumer>("Kafka")

        override fun install(pipeline: Application, configure: KafkaOptions.() -> Unit): CustomKafkaConsumer {
            val config = KafkaOptions().apply { configure() }
            val kafkaConsumer = config.run {
                createConsumer(
                    kafkaBrokersAsString,
                    kafkaGroupId,
                    kafkaClientId
                )
//                    .apply { subscribe(kafkaTopicForListener) }
            }
            pipeline.environment.monitor.subscribe(ApplicationStopPreparing) {
                kafkaConsumer.close()
            }
            return CustomKafkaConsumer(kafkaConsumer)
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
    return KafkaConsumer(props)
}


