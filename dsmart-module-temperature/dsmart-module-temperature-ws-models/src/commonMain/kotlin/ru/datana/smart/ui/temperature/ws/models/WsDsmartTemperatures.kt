package ru.datana.smart.ui.temperature.ws.models

import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartTemperatures(
    val timeBackend: Long? = null,
    val timeLatest: Long? = null,
    val timeEarliest: Long? = null,
    val temperatureScale: String? = null,
    val temperatureAverage: Double? = null,
    val durationMillis: Long? = null,
    val tempertureMin: Double? = null,
    val tempertureMax: Double? = null,
)
