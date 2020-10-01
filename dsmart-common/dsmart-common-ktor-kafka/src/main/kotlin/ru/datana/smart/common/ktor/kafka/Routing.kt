package ru.datana.smart.common.ktor.kafka

import io.ktor.application.feature
import io.ktor.routing.Route
import io.ktor.routing.application
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean

fun Route.kafka(topics: Collection<String>, handle: suspend KtorKafkaConsumerContext.() -> Unit) {

    val feature = application.feature(KtorKafkaConsumer)
    val consumer = feature.kafkaConsumer
    val kafkaConsumerContext = KtorKafkaConsumerContext(topics)
    val closed = AtomicBoolean(false)

    feature.launch {
        try {
            while (!closed.get()) {
                consumer.subscribe(topics)
                val records = consumer.poll(Duration.ofSeconds(1))

                if (records.count() > 0) {
                    println("Pulled records: ${records.count()}")
                    kafkaConsumerContext.records = records
                    kafkaConsumerContext.handle()
                } else {
                    println("No records pulled")
                }
            }
        } catch (e: Throwable) {
            println("Caught exception: ${e.stackTrace}")
//        when (e) {
//            is WakeupException -> log.info("Consumer waked up")
//            else -> log.error("Polling failed", e)
//        }
        } finally {
            println("Finally block")
//        log.info("Commit offset synchronously")
            consumer.commitSync()
            consumer.close()
//        log.info("Consumer successfully closed")
        }
    }
}
