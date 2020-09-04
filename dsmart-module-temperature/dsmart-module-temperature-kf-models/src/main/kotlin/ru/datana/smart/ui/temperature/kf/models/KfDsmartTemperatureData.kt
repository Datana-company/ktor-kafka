package ru.datana.smart.ui.temperature.kf.models

import kotlinx.serialization.Serializable

@Serializable
data class KfDsmartTemperatureData @JvmOverloads constructor(
    /**
     * Время начала диапазона времени, за которое произошло усреднение данных
     */
    val timeMillis: Long? = null,
    /**
     * Длительность времени в миллисекундах, за которое произошло усреднение данных
     */
    val durationMillis: Long? = null,

    /**
     * Идентификатор серсора, с которого пришли данные
     */
    val sensorId: String? = null,

    /**
     * Средняя температура за диапазон времени от timeMillis до timeMillis+durationMillis
     */
    val temperature: Double? = null,
    /**
     * Максимальное отклонение температуры от среднего вверх за диапазон времени от timeMillis до timeMillis+durationMillis
     */
    val deviationPositive: Double? = null,
    /**
     * Максимальное отклонение температуры от среднего вниз за диапазон времени от timeMillis до timeMillis+durationMillis
     */
    val deviationNegative: Double? = null,
)
