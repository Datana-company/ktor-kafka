package ru.datana.smart.ui.converter.app.cor.context

data class InnerRecord<K, V>(
    val topic: String,
    val partition: Int,
    val offset: Long,
    val key: K,
    val value: V
)
