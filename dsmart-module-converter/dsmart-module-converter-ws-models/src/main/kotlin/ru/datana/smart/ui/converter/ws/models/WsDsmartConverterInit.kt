package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartConverterInit(
    val meltInfo: WsDsmartConverterMeltInfo? = null,
    val events: WsDsmartEventList? = null
)
