package ru.datana.smart.ui.converter.app.cor.repository.events

interface IUserEvent {
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
