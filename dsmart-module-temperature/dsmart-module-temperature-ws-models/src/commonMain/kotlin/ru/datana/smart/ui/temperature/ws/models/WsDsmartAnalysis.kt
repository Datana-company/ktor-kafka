package ru.datana.smart.ui.temperature.ws.models

import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartAnalysis(

    /**
     * Оценка времени, когда чайник закипит
     */
    val boilTime: Long? = null,

    /**
     * Вычисленное состояние чайника
     */
    val state: TeapotState? = null

)
