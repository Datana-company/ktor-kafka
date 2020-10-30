package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartTemperature (
    val temperatureAverage: Double? = null
)
