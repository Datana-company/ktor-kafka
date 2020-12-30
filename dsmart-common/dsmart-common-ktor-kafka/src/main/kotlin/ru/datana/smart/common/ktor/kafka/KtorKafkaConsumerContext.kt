package ru.datana.smart.common.ktor.kafka

import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer

class KtorKafkaConsumerContext(
    val consumer: KafkaConsumer<String, ByteArray>,
    @Deprecated(
        message = "Must be avoided!!",
        replaceWith = ReplaceWith("items"),
        level = DeprecationLevel.WARNING
    )
    val records: ConsumerRecords<String, ByteArray>
) {
    val items: ConsumerItems
        get() = ConsumerItems(records)

    fun commitAll() = consumer.commitAsync()
}
