package ru.datana.smart.ui.converter.common.events

interface IBizEvent {
    val id: String
    val timeStart: Long
    val timeFinish: Long
    val title: String?
    val textMessage: String?
    val category: Category?
    val isActive: Boolean

    enum class Category(val value: String){
        CRITICAL("CRITICAL"),
        WARNING("WARNING"),
        INFO("INFO"),
        HINT("HINT")
    }
}
