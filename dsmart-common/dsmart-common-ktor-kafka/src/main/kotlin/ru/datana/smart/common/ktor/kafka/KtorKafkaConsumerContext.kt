package ru.datana.smart.common.ktor.kafka

import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.Logger

class KtorKafkaConsumerContext(var records: ConsumerRecords<String, String>) {

    fun commitAsync(consumer: KafkaConsumer<String, String>, log: Logger?) {
        consumer.commitAsync { offsets, exception ->
            log?.run {
                if (exception == null) {
                    trace("Offset committed  $offsets")
                } else {
                    error("Commit failed for offsets $offsets", exception)
                }
            }
        }
    }
}
