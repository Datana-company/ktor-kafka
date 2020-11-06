package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartConverterAngles(
    val angleTime: Long? = null,
    val angle: Double? = null,
    val source: Double? = null
)
