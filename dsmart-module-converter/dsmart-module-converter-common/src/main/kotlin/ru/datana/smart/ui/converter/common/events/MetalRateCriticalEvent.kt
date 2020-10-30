package ru.datana.smart.ui.converter.common.events

import java.time.Instant

data class MetalRateCriticalEvent(
    override val id: String = "",
    override val timeStart: Long = Instant.now().toEpochMilli(),
    override val timeFinish: Long = Instant.now().toEpochMilli(),
    override val metalRate: Double,
    override val title: String = "Критическая ошибка",
    override val textMessage: String = """
                                        В потоке детектирован $metalRate металла, превышен допустимый предел % потери.
                                        Верните конвертер в вертикальное положение.
                                        """.trimIndent(),
    override val category: IBizEvent.Category = IBizEvent.Category.CRITICAL,
    override val isActive: Boolean = true,
): IMetalRateEvent
