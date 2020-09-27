package ru.datana.smart.ui.temperature.ws.models

import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartAnalysis(
    /**
     * Время до закипания чайника относительно [[timeActual]]
     */
    val timeBackend: Long? = null,

    /**
     * Текущее время согласно данных ML-модели
     */
    val timeActual: Long? = null,

    /**
     * Время ожидания закипания в миллисекундах
     */
    val durationToBoil: Long? = null,

    /**
     * Идентификатор сенсора
     */
    val sensorId: String? = null,

    /**
     * Последняя температура согласно данных ML-модуля
     */
    val temperatureLast: Double? = null,


    /**
     * Вычисленное состояние чайника
     */
    val state: TeapotState? = null

    )
