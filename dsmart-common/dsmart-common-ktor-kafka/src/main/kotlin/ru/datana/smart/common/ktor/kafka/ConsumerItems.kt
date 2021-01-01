package ru.datana.smart.common.ktor.kafka

import org.apache.kafka.clients.consumer.ConsumerRecords
import java.time.Instant

class ConsumerItem<K,V>(
    val time: Instant,
    val key: K,
    val value: V,
    val topic: String,
    val partition: Int,
    val offset: Long,
    val headers: Map<String, ByteArray>
)

class ConsumerItems<K,V>(val items: Sequence<ConsumerItem<K,V>>) {
    constructor(records: ConsumerRecords<K,V>) : this(
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
