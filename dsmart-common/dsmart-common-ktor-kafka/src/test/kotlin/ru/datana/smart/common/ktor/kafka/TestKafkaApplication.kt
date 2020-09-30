package ru.datana.smart.common.ktor.kafka

import io.ktor.application.Application
import io.ktor.application.log
import io.ktor.application.install
import io.ktor.routing.routing


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module(testing: Boolean = false) {
    log.info("Finish consuming")

    install(KafkaConsumer) {
        kafkaBrokersAsString = "localhost:9092"
        kafkaTopicForListener = "ui-temperature"
        kafkaClientId = "ui-client-kafka"
        kafkaGroupId = "ui-app-kafka"
    }

    routing {
        kafka(listOf("test-topic")) { this: KafkaConsumerContex ->
            this.messsages.forEach {
                println(it.record)
            }
        }
    }
}

