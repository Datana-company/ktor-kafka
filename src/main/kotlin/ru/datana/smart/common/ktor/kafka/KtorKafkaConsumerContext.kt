package ru.datana.smart.common.ktor.kafka

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecords

class KtorKafkaConsumerContext<K,V>(
    val consumer: Consumer<K, V>,
    @Deprecated(
        message = "Must be avoided!!",
        replaceWith = ReplaceWith("items"),
        level = DeprecationLevel.WARNING
    )
    val records: ConsumerRecords<K,V>
) {
    val items: ConsumerItems<K,V>
        get() = ConsumerItems(records)

    fun commitAll() = consumer.commitAsync()
}
