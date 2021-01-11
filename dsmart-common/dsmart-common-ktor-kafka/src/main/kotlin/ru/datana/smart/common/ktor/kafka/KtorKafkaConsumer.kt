package ru.datana.smart.common.ktor.kafka

import io.ktor.application.*
import io.ktor.config.*
import io.ktor.util.*
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class KtorKafkaConsumer() : CoroutineScope {

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
    }

    /**
     * Feature installation object
     */
    companion object Feature : ApplicationFeature<Application, KafkaOptions, KtorKafkaConsumer> {
        override val key = AttributeKey<KtorKafkaConsumer>("KtorKafkaConsumer")

        @KtorExperimentalAPI
        override fun install(pipeline: Application, configure: KafkaOptions.() -> Unit): KtorKafkaConsumer {
            val kafkaOptions = KafkaOptions().apply(configure)
            val ktorKafkaConsumer = KtorKafkaConsumer()

            pipeline.environment.monitor.subscribe(ApplicationStopPreparing) {
                ktorKafkaConsumer.shutdown()
            }
            return ktorKafkaConsumer
        }
    }
}


