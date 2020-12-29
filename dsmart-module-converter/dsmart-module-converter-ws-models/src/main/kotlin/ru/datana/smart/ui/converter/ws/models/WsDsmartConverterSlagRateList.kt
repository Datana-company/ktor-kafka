package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartConverterSlagRateList(
    val list: List<WsDsmartConverterSlagRate>? = null
)
