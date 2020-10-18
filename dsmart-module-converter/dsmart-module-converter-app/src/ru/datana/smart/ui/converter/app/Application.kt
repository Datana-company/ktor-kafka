package ru.datana.smart.ui.converter.app

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*
import java.time.Instant

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    routing {
        static("/") {
            defaultResource("static/index.html")
            resources("static")
        }
    }
    Instant.now().toEpochMilli()
}
