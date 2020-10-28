package ru.datana.smart.ui.converter.common.events

import java.time.Instant

data class ConveyorMetalRateNormalEvent (
    override val id: String = "",
    override val timeStart: Long = Instant.now().toEpochMilli(),
    override val timeFinish: Long = Instant.now().toEpochMilli(),
    override val metalRate: Double,
    override val title: String? = "Предупреждение",
    override val textMessage: String? = """
                                        Достигнут предел металла $metalRate в потоке.
                                        """.trimIndent(),
    override val category: IBizEvent.Category = IBizEvent.Category.WARNING,
    override val isActive: Boolean = true
): IConveyorMetalRateEvent
