package ru.datana.smart.common.ktor.kafka

import io.ktor.application.feature
import io.ktor.application.log
import io.ktor.routing.Route
import io.ktor.routing.application
import javafx.application.Application.launch
import kotlinx.coroutines.launch
import org.apache.kafka.common.errors.WakeupException
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.concurrent.atomic.AtomicBoolean

fun Route.kafka(protocol: String? = null, handler: suspend () -> Unit) {
    val kafka =application.feature(Kafka) // early require
    kafka.pingIntervalMillis=1

    val closed = AtomicBoolean(false)
    val consumer = buildConsumer(this@module.environment)
    launch {
        try {
            while (!closed.get()) {
                val records = consumer.poll(Duration.of(1000, ChronoUnit.MILLIS))

                records
                    .firstOrNull()
                    ?.let { record ->
                        log.trace("topic = ${record.topic()}, partition = ${record.partition()}, offset = ${record.offset()}, key = ${record.key()}, value = ${record.value()}")
                        parseKafkaInput(record.value())
                    }
                    ?.takeIf {
                        it.data?.temperature?.isFinite() ?: false
                    }
                    ?.also { temp -> sendToAll(temp) }

                if (!records.isEmpty) {
                    consumer.commitAsync { offsets, exception ->
                        if (exception != null) {
                            log.error("Commit failed for offsets $offsets", exception)
                        } else {
                            log.trace("Offset committed  $offsets")
                        }
                    }
                }
            }
            log.info("Finish consuming")
        } catch (e: Throwable) {
            when (e) {
                is WakeupException -> log.info("Consumer waked up")
                else -> log.error("Polling failed", e)
            }
        } finally {
            log.info("Commit offset synchronously")
            consumer.commitSync()
            consumer.close()
            log.info("Consumer successfully closed")
        }
    }

}
