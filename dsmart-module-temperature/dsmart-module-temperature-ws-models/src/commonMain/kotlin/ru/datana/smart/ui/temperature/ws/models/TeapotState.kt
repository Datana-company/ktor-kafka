package ru.datana.smart.ui.temperature.ws.models

import kotlinx.serialization.Serializable

@Serializable
data class TeapotState(

    /**
     * Идентификатор состояния чайника
     */
    val id: String? = null,

    /**
     * Человекочитаемое наименование состояния. Например, "Включен"
     */
    val name: String? = null,

    /**
     * Сообщение (описание события) для состояния. Например, "Чайник включен"
     */
    val message: String? = null
)
