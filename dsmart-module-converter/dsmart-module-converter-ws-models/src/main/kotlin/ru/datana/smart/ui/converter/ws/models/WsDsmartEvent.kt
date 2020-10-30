package ru.datana.smart.ui.converter.ws.models
import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartEvent (
    val id: String? = null,
    val timeStart: Long? = null,
    val timeFinish: Long? = null,
    val title: String? = null,
    val textMessage: String? = null,
    val category: WsDsmartEvent.Category? = null,
    val isActive: Boolean? = false
) {
    @Serializable
    enum class Category(val value: String){
        CRITICAL("CRITICAL"),
        WARNING("WARNING"),
        INFO("INFO"),
        HINT("HINT")
    }
}
