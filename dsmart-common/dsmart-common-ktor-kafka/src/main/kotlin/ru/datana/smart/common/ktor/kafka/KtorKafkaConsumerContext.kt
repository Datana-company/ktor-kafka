package ru.datana.smart.common.ktor.kafka

import org.apache.kafka.clients.consumer.ConsumerRecords

class KtorKafkaConsumerContext(

    val topics: Collection<String>,
    var records: ConsumerRecords<String, String>? = null

)
