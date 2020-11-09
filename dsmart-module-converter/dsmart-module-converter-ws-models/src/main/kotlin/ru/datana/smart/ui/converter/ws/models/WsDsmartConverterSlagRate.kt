package ru.datana.smart.ui.converter.ws.models
import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartConverterSlagRate(
    val slagRateTime: Long? = null,
    val steelRate: Double? = null,
    val slagRate: Double? = null,
    val warningPoint: Double? = null
)
