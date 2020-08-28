package ru.datana.smart.common.transport.models.ws

interface IWsDsmartQuery<T> {
    val module: String?
    val event: String?
    val data: T?
}
