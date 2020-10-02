package ru.datana.smart.common.ktor.kafka

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.routing.routing


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module(testing: Boolean = false) {

    install(KtorKafkaConsumer) {
        kafkaBrokersAsString = "172.29.40.58:9092"
        kafkaClientId = "ui-client-kafka"
        kafkaGroupId = "ui-app-kafka"
    }

    routing {
        kafkaListen(listOf("ui-temperature")) {
            records?.iterator()?.forEach { println(it) }
        }
    }
}

