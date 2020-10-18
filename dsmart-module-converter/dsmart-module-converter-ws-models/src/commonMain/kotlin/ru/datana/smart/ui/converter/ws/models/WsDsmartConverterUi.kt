package ru.datana.smart.ui.converter.ws.models
import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartConverterUi(
    val frameId: String? = null,
    val frameTime: Long? = null,
    val framePath: String? = null,
    val meltInfo: WsDsmartConverterMeltInfo? = null,
    val angle: Double? = null,
    val steelRate: Double? = null,
    val slagRate: Double? = null
)
