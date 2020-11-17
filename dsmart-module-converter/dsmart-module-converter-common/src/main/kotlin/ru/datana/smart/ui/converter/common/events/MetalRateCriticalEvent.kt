package ru.datana.smart.ui.converter.common.events

import ru.datana.smart.ui.converter.common.utils.toPercent
import java.time.Instant

data class MetalRateCriticalEvent(
    override val id: String = "",
    override val timeStart: Instant = Instant.MIN,
    override val timeFinish: Instant = Instant.MAX,
    override val metalRate: Double,
    val criticalPoint: Double,
    override val angleStart: Double? = null,
    override val angleFinish: Double? = null,
    override val angleMax: Double? = null,
    override val title: String = "Критическая ситуация",
    override val textMessage: String = """
                                        В потоке детектирован металл – ${toPercent(metalRate)}%, процент потерь превышает критическое значение – ${toPercent(criticalPoint)}%. Верните конвертер в вертикальное положение!

                                        """.trimIndent(),
    override val category: IBizEvent.Category = IBizEvent.Category.CRITICAL,
    override val isActive: Boolean = true,
    override val executionStatus: IBizEvent.ExecutionStatus = IBizEvent.ExecutionStatus.NONE,
): IMetalRateEvent
