package ru.datana.smart.common.ktor.kafka

import io.ktor.application.feature
import io.ktor.routing.Route
import io.ktor.routing.application
import kotlinx.coroutines.launch
import org.apache.kafka.common.errors.WakeupException
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean

fun Route.kafkaListen(topics: Collection<String>, handle: suspend KtorKafkaConsumerContext.() -> Unit) {

    val feature = application.feature(KtorKafkaConsumer)
    val consumer = feature.kafkaConsumer
    val ctx = KtorKafkaConsumerContext()
    val isClosed = AtomicBoolean(false)

    feature.launch {
        try {
            while (!isClosed.get()) {
                consumer.subscribe(topics)
                val records = consumer.poll(Duration.ofSeconds(1))

                if (!records.isEmpty) {
                    println("Pulled records: ${records.count()}")
                    ctx.records = records
                    ctx.handle()
                } else {
                    println("No records pulled")
                }
            }
        } catch (e: Throwable) {
            println("Caught exception: ${e.stackTrace}")
            when (e) {
                is WakeupException -> println("Consumer waked up")
                else -> println("Polling failed, caught exception: $e")
            }
        } finally {
            println("Commit offset synchronously")
            consumer.commitSync()
            consumer.close()
            println("Consumer successfully closed")
        }
    }
}
