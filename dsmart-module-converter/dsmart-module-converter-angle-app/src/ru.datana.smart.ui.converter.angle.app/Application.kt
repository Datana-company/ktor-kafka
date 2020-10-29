package ru.datana.smart.ui.converter.angle.app

import io.ktor.application.*
import io.ktor.util.*
import ru.datana.smart.logger.datanaLogger

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
@kotlin.jvm.JvmOverloads
@KtorExperimentalAPI
fun Application.module(testing: Boolean = false) {

    val logger = datanaLogger(::main::class.java)
}
