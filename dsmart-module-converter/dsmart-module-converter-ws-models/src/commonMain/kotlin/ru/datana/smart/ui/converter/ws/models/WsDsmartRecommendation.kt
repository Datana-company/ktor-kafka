package ru.datana.smart.ui.converter.ws.models
import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartRecommendation (
    val time: Long? = null,
    val category: WsDsmartRecommendation.Category? = null,
    val textMessage: String? = null
) {
    enum class Category(val value: String){
        CRITICAL("CRITICAL"),
        WARNING("WARNING"),
        INFO("INFO");
    }
}
