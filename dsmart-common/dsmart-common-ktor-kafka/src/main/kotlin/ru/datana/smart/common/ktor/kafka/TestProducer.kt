package ru.datana.smart.common.ktor.kafka

import org.apache.kafka.clients.consumer.ConsumerGroupMetadata
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.Metric
import org.apache.kafka.common.MetricName
import org.apache.kafka.common.PartitionInfo
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.record.TimestampType
import ru.datana.smart.logger.datanaLogger
import java.time.Duration
import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.Future

class TestProducer<K, V>() : Producer<K, V> {

    private val logger = datanaLogger(this::class.java)

    private val executor = Executors.newSingleThreadExecutor()
    private val records: MutableList<ConsumerRecord<K,V>> = mutableListOf()

    fun clean() = records.clear()
    fun getSent() = records.toList()

    override fun close() {}

    override fun close(timeout: Duration?) {}

    override fun initTransactions() {}

    override fun beginTransaction() {}

    override fun sendOffsetsToTransaction(
        offsets: MutableMap<TopicPartition, OffsetAndMetadata>?,
        consumerGroupId: String?
    ) {
    }

    override fun sendOffsetsToTransaction(
        offsets: MutableMap<TopicPartition, OffsetAndMetadata>?,
        groupMetadata: ConsumerGroupMetadata?
    ) {
    }

    override fun commitTransaction() {}

    override fun abortTransaction() {}

    override fun send(record: ProducerRecord<K, V>): Future<RecordMetadata> {
        records.add(
            ConsumerRecord<K,V>(
                record.topic(),
                record.partition() ?: 0,
                0,
                record.timestamp() ?: Instant.now().toEpochMilli(),
                TimestampType.CREATE_TIME,
                0L,
                0,0,
                record.key(),
                record.value()
            )
        )
        logger.debug("record is recorded")
        return executor.submit {
            RecordMetadata(
                TopicPartition(record.topic(), 0),
                0L,
                0L,
                Instant.now().toEpochMilli(),
                0L,
                0, 0
            )
        } as Future<RecordMetadata>
    }

    override fun send(record: ProducerRecord<K, V>, callback: Callback?): Future<RecordMetadata> {
        return send(record)
    }

    override fun flush() {}

    override fun partitionsFor(topic: String?): MutableList<PartitionInfo> = mutableListOf()

    override fun metrics(): MutableMap<MetricName, out Metric> = mutableMapOf()
}
