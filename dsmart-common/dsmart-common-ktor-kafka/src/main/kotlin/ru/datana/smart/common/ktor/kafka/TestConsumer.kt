package ru.datana.smart.common.ktor.kafka

import org.apache.kafka.clients.consumer.*
import org.apache.kafka.common.Metric
import org.apache.kafka.common.MetricName
import org.apache.kafka.common.PartitionInfo
import org.apache.kafka.common.TopicPartition
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class TestConsumer<K, V> : Consumer<K, V> {
    private val _topics = mutableMapOf<String, MutableList<Pair<K, V>>>()

    fun send(topic: String, key: K, body: V) {
        _topics[topic]?.add(key to body) ?: throw RuntimeException("No subscription")
    }

    override fun close() {}

    override fun close(timeout: Long, unit: TimeUnit?) {}

    override fun close(timeout: Duration?) {}

    override fun assignment(): MutableSet<TopicPartition> = subscription()
        .map { TopicPartition(it, 0) }
        .toMutableSet()

    override fun subscription(): MutableSet<String> = _topics.keys.toMutableSet()

    override fun subscribe(topics: MutableCollection<String>?) {
        topics?.forEach {
            _topics.putIfAbsent(it, mutableListOf())
        }
    }

    override fun subscribe(topics: MutableCollection<String>?, callback: ConsumerRebalanceListener?) {
        TODO("Not yet implemented")
    }

    override fun subscribe(pattern: Pattern?, callback: ConsumerRebalanceListener?) {
        TODO("Not yet implemented")
    }

    override fun subscribe(pattern: Pattern?) {
        TODO("Not yet implemented")
    }

    override fun assign(partitions: MutableCollection<TopicPartition>?) {
        TODO("Not yet implemented")
    }

    override fun unsubscribe() {
        _topics.clear()
    }

    override fun poll(timeout: Long): ConsumerRecords<K, V> = ConsumerRecords(
        _topics
            .map { msgs ->
                val pair = TopicPartition(msgs.key, 0) to msgs.value.map { msg ->
                    ConsumerRecord<K, V>(
                        msgs.key,
                        0,
                        0,
                        msg.first,
                        msg.second
                    )
                }
                msgs.value.clear()
                pair
            }
            .toMap()
    )

    override fun poll(timeout: Duration?): ConsumerRecords<K, V> = poll(0L)

    override fun commitSync() {
        TODO("Not yet implemented")
    }

    override fun commitSync(timeout: Duration?) {
        TODO("Not yet implemented")
    }

    override fun commitSync(offsets: MutableMap<TopicPartition, OffsetAndMetadata>?) {
        TODO("Not yet implemented")
    }

    override fun commitSync(offsets: MutableMap<TopicPartition, OffsetAndMetadata>?, timeout: Duration?) {
        TODO("Not yet implemented")
    }

    override fun commitAsync() {
        TODO("Not yet implemented")
    }

    override fun commitAsync(callback: OffsetCommitCallback?) {
        TODO("Not yet implemented")
    }

    override fun commitAsync(offsets: MutableMap<TopicPartition, OffsetAndMetadata>?, callback: OffsetCommitCallback?) {
        TODO("Not yet implemented")
    }

    override fun seek(partition: TopicPartition?, offset: Long) {
        TODO("Not yet implemented")
    }

    override fun seek(partition: TopicPartition?, offsetAndMetadata: OffsetAndMetadata?) {
        TODO("Not yet implemented")
    }

    override fun seekToBeginning(partitions: MutableCollection<TopicPartition>?) {
        TODO("Not yet implemented")
    }

    override fun seekToEnd(partitions: MutableCollection<TopicPartition>?) {
        TODO("Not yet implemented")
    }

    override fun position(partition: TopicPartition?): Long {
        TODO("Not yet implemented")
    }

    override fun position(partition: TopicPartition?, timeout: Duration?): Long {
        TODO("Not yet implemented")
    }

    override fun committed(partition: TopicPartition?): OffsetAndMetadata {
        TODO("Not yet implemented")
    }

    override fun committed(partition: TopicPartition?, timeout: Duration?): OffsetAndMetadata {
        TODO("Not yet implemented")
    }

    override fun committed(partitions: MutableSet<TopicPartition>?): MutableMap<TopicPartition, OffsetAndMetadata> {
        TODO("Not yet implemented")
    }

    override fun committed(
        partitions: MutableSet<TopicPartition>?,
        timeout: Duration?
    ): MutableMap<TopicPartition, OffsetAndMetadata> {
        TODO("Not yet implemented")
    }

    override fun metrics(): MutableMap<MetricName, out Metric> {
        TODO("Not yet implemented")
    }

    override fun partitionsFor(topic: String?): MutableList<PartitionInfo> {
        TODO("Not yet implemented")
    }

    override fun partitionsFor(topic: String?, timeout: Duration?): MutableList<PartitionInfo> {
        TODO("Not yet implemented")
    }

    override fun listTopics(): MutableMap<String, MutableList<PartitionInfo>> {
        TODO("Not yet implemented")
    }

    override fun listTopics(timeout: Duration?): MutableMap<String, MutableList<PartitionInfo>> {
        TODO("Not yet implemented")
    }

    override fun paused(): MutableSet<TopicPartition> {
        TODO("Not yet implemented")
    }

    override fun pause(partitions: MutableCollection<TopicPartition>?) {
        TODO("Not yet implemented")
    }

    override fun resume(partitions: MutableCollection<TopicPartition>?) {
        TODO("Not yet implemented")
    }

    override fun offsetsForTimes(timestampsToSearch: MutableMap<TopicPartition, Long>?): MutableMap<TopicPartition, OffsetAndTimestamp> {
        TODO("Not yet implemented")
    }

    override fun offsetsForTimes(
        timestampsToSearch: MutableMap<TopicPartition, Long>?,
        timeout: Duration?
    ): MutableMap<TopicPartition, OffsetAndTimestamp> {
        TODO("Not yet implemented")
    }

    override fun beginningOffsets(partitions: MutableCollection<TopicPartition>?): MutableMap<TopicPartition, Long> {
        TODO("Not yet implemented")
    }

    override fun beginningOffsets(
        partitions: MutableCollection<TopicPartition>?,
        timeout: Duration?
    ): MutableMap<TopicPartition, Long> {
        TODO("Not yet implemented")
    }

    override fun endOffsets(partitions: MutableCollection<TopicPartition>?): MutableMap<TopicPartition, Long> {
        TODO("Not yet implemented")
    }

    override fun endOffsets(
        partitions: MutableCollection<TopicPartition>?,
        timeout: Duration?
    ): MutableMap<TopicPartition, Long> {
        TODO("Not yet implemented")
    }

    override fun groupMetadata(): ConsumerGroupMetadata {
        TODO("Not yet implemented")
    }

    override fun wakeup() {
        TODO("Not yet implemented")
    }
}
