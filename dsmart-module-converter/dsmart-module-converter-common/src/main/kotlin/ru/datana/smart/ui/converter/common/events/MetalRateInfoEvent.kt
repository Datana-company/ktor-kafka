package ru.datana.smart.ui.converter.common.events

import ru.datana.smart.ui.converter.common.utils.toPercent
import java.time.Instant

data class MetalRateInfoEvent(
    override val id: String = "",
    override val timeStart: Long = Instant.now().toEpochMilli(),
    override val timeFinish: Long = Instant.now().toEpochMilli(),
    override val metalRate: Double,
    override val angleStart: Double? = null,
    override val angleFinish: Double? = null,
    override val angleMax: Double? = null,
    override val title: String = "Информация",
    override val textMessage: String = """
                                        Достигнут предел потерь металла в потоке – ${toPercent(metalRate)}%.
                                        """.trimIndent(),
    override val category: IBizEvent.Category = IBizEvent.Category.INFO,
    override val isActive: Boolean = true,
    override val executionStatus: IBizEvent.ExecutionStatus = IBizEvent.ExecutionStatus.NONE,
): IMetalRateEvent
