package ru.datana.smart.ui.converter.common.events

import java.time.Instant

data class SuccessMeltEvent(
    override val id: String = "",
    override val timeStart: Long = Instant.now().toEpochMilli(),
    override val timeFinish: Long = Instant.now().toEpochMilli(),
    override val metalRate: Double = 0.0,
    override val angleStart: Double? = null,
    override val angleFinish: Double? = null,
    override val angleMax: Double? = null,
    override val title: String = "Информационное сообщение",
    override val textMessage: String = """
                                        Допустимая норма потерь металла % не была превышена.
                                        """.trimIndent(),
    override val category: IBizEvent.Category = IBizEvent.Category.INFO,
    override val isActive: Boolean = true,
    override val executionStatus: IBizEvent.ExecutionStatus = IBizEvent.ExecutionStatus.NONE,
): IMetalRateEvent
