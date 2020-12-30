package ru.datana.smart.ui.converter.common.context

class InnerRecord(
    val topic: String,
    val partition: Int,
    val offset: Long,
    val key: String,
    val value: kotlin.ByteArray
)
