package ru.datana.smart.common.ktor.kafka

import org.apache.kafka.clients.consumer.ConsumerRecords

class CustomKafkaConsumerContext(

    val topics: Collection<String>,
    var records: ConsumerRecords<String, String>? = null

)
