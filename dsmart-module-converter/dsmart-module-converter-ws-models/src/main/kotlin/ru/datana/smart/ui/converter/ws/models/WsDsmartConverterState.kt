package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartConverterState(
    val meltInfo: WsDsmartConverterMeltInfo? = null,
    val events: WsDsmartEventList? = null,
    val warningPoint: Double? = null
)
