package ru.datana.smart.common.ktor.kafka

import org.apache.kafka.clients.consumer.ConsumerRecords

data class KtorKafkaConsumerContext(

    var records: ConsumerRecords<String, String>? = null

)
