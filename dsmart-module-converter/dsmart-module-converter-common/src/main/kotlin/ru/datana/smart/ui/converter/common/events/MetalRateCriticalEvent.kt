package ru.datana.smart.ui.converter.common.events

import ru.datana.smart.ui.converter.common.utils.toPercent
import java.time.Instant

data class MetalRateCriticalEvent(
    override val id: String = "",
    override val timeStart: Long = Instant.now().toEpochMilli(),
    override val timeFinish: Long = Instant.now().toEpochMilli(),
    override val metalRate: Double,
    override val angleStart: Double? = null,
    override val angleFinish: Double? = null,
    override val title: String = "Критическая ситуация",
    override val textMessage: String = """
                                        В потоке детектирован ${toPercent(metalRate)}% металла, превышен допустимый предел потери в процентах.
                                        Верните конвертер в вертикальное положение.
                                        """.trimIndent(),
    override val category: IBizEvent.Category = IBizEvent.Category.CRITICAL,
    override val isActive: Boolean = true,
    override val executionStatus: IBizEvent.ExecutionStatus = IBizEvent.ExecutionStatus.NONE,
): IMetalRateEvent
