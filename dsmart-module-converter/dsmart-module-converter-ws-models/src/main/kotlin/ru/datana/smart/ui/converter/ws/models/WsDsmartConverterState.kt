package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartConverterState(
    val meltInfo: WsDsmartConverterMeltInfo? = null,
    val eventList: WsDsmartEventList? = null,
    val slagRateList: WsDsmartConverterSlagRateList? = null,
    val warningPoint: Double? = null
)
