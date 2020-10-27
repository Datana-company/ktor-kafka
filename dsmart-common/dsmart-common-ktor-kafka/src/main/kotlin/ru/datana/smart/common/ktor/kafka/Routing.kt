package ru.datana.smart.common.ktor.kafka

import io.ktor.application.feature
import io.ktor.application.log
import io.ktor.routing.Route
import io.ktor.routing.application
import kotlinx.coroutines.launch
import org.apache.kafka.common.errors.WakeupException
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean

fun Route.kafka(topics: Collection<String>, handle: suspend KtorKafkaConsumerContext.() -> Unit) {

    val feature = application.feature(KtorKafkaConsumer)
    val consumer = feature.kafkaConsumer
    val isClosed = AtomicBoolean(false)
    val log = LoggerFactory.getLogger("ru.datana.smart.common.ktor.kafka:Route.kafka")

    feature.launch {
        try {
            consumer.subscribe(topics)

            while (!isClosed.get()) {
                val records = consumer.poll(Duration.ofSeconds(1))
                if (!records.isEmpty) {
                    log.debug("Pulled records: {}", records.count())
                    KtorKafkaConsumerContext(consumer, records).handle()
                } else {
                    log.debug("No records pulled")
                }
            }
        } catch (e: Throwable) {
            log.debug("Caught exception: {}", e.stackTrace)
            when (e) {
                is WakeupException -> log.debug("Consumer waked up")
                else -> log.debug("Polling failed, caught exception: {}", e.stackTrace)
            }
        } finally {
            log.debug("Commit offset synchronously")
            consumer.commitSync()
            consumer.close()
            log.debug("Consumer successfully closed")
        }
    }
}

