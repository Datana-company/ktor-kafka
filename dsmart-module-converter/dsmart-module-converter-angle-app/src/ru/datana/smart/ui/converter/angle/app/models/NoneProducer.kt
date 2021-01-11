package ru.datana.smart.ui.converter.angle.app.models

import org.apache.kafka.clients.consumer.ConsumerGroupMetadata
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.Metric
import org.apache.kafka.common.MetricName
import org.apache.kafka.common.PartitionInfo
import org.apache.kafka.common.TopicPartition
import java.time.Duration
import java.util.concurrent.Future

object NoneProducer : Producer<String, String> {
    override fun close() {
        TODO("Not yet implemented")
    }

    override fun close(timeout: Duration?) {
        TODO("Not yet implemented")
    }

    override fun initTransactions() {
        TODO("Not yet implemented")
    }

    override fun beginTransaction() {
        TODO("Not yet implemented")
    }

    override fun sendOffsetsToTransaction(
        offsets: MutableMap<TopicPartition, OffsetAndMetadata>?,
        consumerGroupId: String?
    ) {
        TODO("Not yet implemented")
    }

    override fun sendOffsetsToTransaction(
        offsets: MutableMap<TopicPartition, OffsetAndMetadata>?,
        groupMetadata: ConsumerGroupMetadata?
    ) {
        TODO("Not yet implemented")
    }

    override fun commitTransaction() {
        TODO("Not yet implemented")
    }

    override fun abortTransaction() {
        TODO("Not yet implemented")
    }

    override fun send(record: ProducerRecord<String, String>?): Future<RecordMetadata> {
        TODO("Not yet implemented")
    }

    override fun send(record: ProducerRecord<String, String>?, callback: Callback?): Future<RecordMetadata> {
        TODO("Not yet implemented")
    }

    override fun flush() {
        TODO("Not yet implemented")
    }

    override fun partitionsFor(topic: String?): MutableList<PartitionInfo> {
        TODO("Not yet implemented")
    }

    override fun metrics(): MutableMap<MetricName, out Metric> {
        TODO("Not yet implemented")
    }

}
