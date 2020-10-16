package ru.datana.smart.ui.converter.ws.models
import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartRecommendation (
    val time: Long? = null,
    val title: String? = null,
    val textMessage: String? = null
)
