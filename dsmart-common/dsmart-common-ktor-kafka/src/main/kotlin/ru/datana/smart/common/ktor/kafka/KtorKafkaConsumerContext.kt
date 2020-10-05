package ru.datana.smart.common.ktor.kafka

import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer

class KtorKafkaConsumerContext(private val consumer: KafkaConsumer<String, String>) {

    var records: ConsumerRecords<String, String>? = null

    fun commitAll() {
        consumer.commitAsync()
    }
}
