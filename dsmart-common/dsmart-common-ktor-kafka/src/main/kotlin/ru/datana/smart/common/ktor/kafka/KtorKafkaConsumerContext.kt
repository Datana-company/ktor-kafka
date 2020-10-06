package ru.datana.smart.common.ktor.kafka

import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer

class KtorKafkaConsumerContext(
    val consumer: KafkaConsumer<String, String>,
    val records: ConsumerRecords<String, String>
) {

    fun commitAll() {
        consumer.commitAsync()
    }
}
