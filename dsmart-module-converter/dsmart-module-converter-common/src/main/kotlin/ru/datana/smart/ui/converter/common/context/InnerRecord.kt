package ru.datana.smart.ui.converter.common.context

data class InnerRecord<K, V>(
    val topic: String,
    val partition: Int,
    val offset: Long,
    val key: K,
    val value: V
)
