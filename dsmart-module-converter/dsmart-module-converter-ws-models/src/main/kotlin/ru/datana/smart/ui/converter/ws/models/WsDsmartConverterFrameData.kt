package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class WsDsmartConverterFrameData(
    val frameId: String? = null,
    val frameTime: Long? = null,
    val framePath: String? = null,
    val image: String? = null,
    val channel: String? = null
)
