package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartConverterVi(
    val frameId: String? = null,
    val frameTime: Long? = null,
    val framePath: String? = null,
    val meltInfo: WsDsmartConverterMeltInfo? = null
)
