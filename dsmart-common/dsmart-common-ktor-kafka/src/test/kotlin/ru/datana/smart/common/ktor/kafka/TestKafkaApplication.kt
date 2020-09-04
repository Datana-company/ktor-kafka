package ru.datana.smart.common.ktor.kafka

import io.ktor.application.Application
import io.ktor.application.log
import io.ktor.application.install


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module(testing: Boolean = false) {
    log.info("Finish consuming")
    install(Kafka) {
    }
}

