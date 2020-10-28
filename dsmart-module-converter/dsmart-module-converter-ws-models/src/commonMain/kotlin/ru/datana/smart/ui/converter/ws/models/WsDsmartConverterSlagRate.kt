package ru.datana.smart.ui.converter.ws.models
import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartConverterSlagRate(
    val steelRate: Double? = null,
    val slagRate: Double? = null
)
