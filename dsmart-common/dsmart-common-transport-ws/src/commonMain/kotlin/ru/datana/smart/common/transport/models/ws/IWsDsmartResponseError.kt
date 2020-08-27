package ru.datana.smart.common.transport.models.ws

interface IWsDsmartResponseError {
    val code: String?
    val group: String?
    val field: String?
    val level: Levels?

    enum class Levels(val level: Int) {
        FATAL(20),
        ERROR(30),
        WARNING(70),
        INFO(90)
    }
}
