package ru.datana.smart.common.ktor.kafka

import org.apache.kafka.clients.consumer.ConsumerRecords
import java.time.Instant

class ConsumerItem(
    val time: Instant,
    val key: String,
    val value: ByteArray,
    val topic: String,
    val partition: Int,
    val offset: Long,
    val headers: Map<String, ByteArray>
)

class ConsumerItems(val items: Sequence<ConsumerItem>) {
    constructor(records: ConsumerRecords<String, ByteArray>) : this(
        records
            .asSequence()
            .map {
                ConsumerItem(
                    time = Instant.ofEpochMilli(it.timestamp()),
                    key = it.key(),
                    value = it.value(),
                    topic = it.topic(),
                    partition = it.partition(),
                    offset = it.offset(),
                    headers = it
                        .headers()
                        .map { it.key() to it.value() }
                        .toMap()
                )
            }
    )
}
