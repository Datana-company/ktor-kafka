package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartEventList(
    val list: List<WsDsmartEvent>? = null
)
