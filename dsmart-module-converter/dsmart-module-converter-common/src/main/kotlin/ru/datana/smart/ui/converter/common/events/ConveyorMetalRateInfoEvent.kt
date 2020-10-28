package ru.datana.smart.ui.converter.common.events

import java.time.Instant

class ConveyorMetalRateInfoEvent (
    override val id: String = "",
    override val timeStart: Long = Instant.now().toEpochMilli(),
    override val timeFinish: Long = Instant.now().toEpochMilli(),
    override val metalRate: Double,
    override val title: String? = "Информация",
    override val textMessage: String? = """
                                        Допустимая норма потерь металла не была превышена.
                                        """.trimIndent(),
    override val category: IBizEvent.Category = IBizEvent.Category.INFO,
    override val isActive: Boolean = true
): IConveyorMetalRateEvent
