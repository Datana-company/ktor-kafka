package ru.datana.smart.ui.converter.app.cor.repository.events

import java.time.Instant

data class ConveyorMetalRateExceedsEvent (
    override val id: String = "",
    override val timeStart: Long = Instant.now().toEpochMilli(),
    override val timeFinish: Long = Instant.now().toEpochMilli(),
    override val metalRate: Double,
    override val title: String? = "Предупреждение",
    override val textMessage: String? = """
                                        В потоке детектирован $metalRate металла.
                                        Верните конвертер в вертикальное положение.
                                        """.trimIndent(),
    override val category: IBizEvent.Category = IBizEvent.Category.WARNING,
    override val isActive: Boolean = true
): IConveyorMetalRateEvent
