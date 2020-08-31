package ru.datana.smart.ui.temperature.ws.models

import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartTemperatures(
    val temperature: Double? = null,
    val deviationPositive: Double? = null,
    val deviationNegative: Double? = null,
)
