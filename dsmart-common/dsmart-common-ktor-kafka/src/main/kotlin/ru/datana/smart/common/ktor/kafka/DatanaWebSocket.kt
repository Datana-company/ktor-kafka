package ru.datana.smart.common.ktor.kafka

import io.ktor.application.Application
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.Frame
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*

fun Application.module(testing: Boolean = false) {
    install(Kafka) {
        clientId = "lksjd"
        bootstrapServers = listOf("localhost:9091")
    }

    routing {
        kafka(topics = listOf("some-topic")) {
            // whatever you need
        }
    }
}
