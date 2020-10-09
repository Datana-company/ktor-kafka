package ru.datana.smart.ui.temperature.app.cor.context

data class Record<K, V>(
    val topic: String,
    val partition: Int,
    val offset: Long,
    val key: K,
    val value: V
)



