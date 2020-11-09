package ru.datana.smart.ui.converter.common.events

import java.time.Instant

data class ExtEvent(
    override val id: String = "",
    override val timeStart: Long = Instant.now().toEpochMilli(),
    override val timeFinish: Long = Instant.now().toEpochMilli(),
    override val title: String = "Информация",
    override val textMessage: String = "",
    override val category: IBizEvent.Category = IBizEvent.Category.INFO, // TODO Где и как определать Категорию?
    override val isActive: Boolean = true,
    override val executionStatus: IBizEvent.ExecutionStatus = IBizEvent.ExecutionStatus.NONE
): IBizEvent
