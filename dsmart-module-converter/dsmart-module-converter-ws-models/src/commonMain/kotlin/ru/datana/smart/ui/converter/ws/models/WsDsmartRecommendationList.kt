package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartRecommendationList(
    val list: List<WsDsmartRecommendation>? = null
)
