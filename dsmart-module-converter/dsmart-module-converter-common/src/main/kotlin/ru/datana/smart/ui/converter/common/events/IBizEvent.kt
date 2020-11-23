package ru.datana.smart.ui.converter.common.events

import java.time.Instant

interface IBizEvent {
    val id: String
    val timeStart: Instant
    val timeFinish: Instant
    val title: String
    val textMessage: String
    val category: Category
    val isActive: Boolean
    val executionStatus: ExecutionStatus

    enum class Category(val value: String){
        CRITICAL("CRITICAL"),
        WARNING("WARNING"),
        INFO("INFO"),
        HINT("HINT")
    }

    enum class ExecutionStatus(val value: String){
        COMPLETED("COMPLETED"),
        FAILED("FAILED"),
        NONE("NONE")
    }
}
