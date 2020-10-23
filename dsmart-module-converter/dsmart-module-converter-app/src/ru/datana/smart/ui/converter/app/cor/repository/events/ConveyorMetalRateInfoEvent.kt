package ru.datana.smart.ui.converter.app.cor.repository.events

import java.time.Instant
import java.util.*

class ConveyorMetalRateInfoEvent (
    override val id: String = UUID.randomUUID().toString(),
    override val timeStart: Long = Instant.now().toEpochMilli(),
    override val timeFinish: Long = Instant.now().toEpochMilli(),
    override val metalRate: Double,
    override val title: String? = "Информация",
    override val textMessage: String? = """
                                        Допустимая норма потерь металла не была превышена.
                                        """.trimIndent(),
    override val category: IUserEvent.Category = IUserEvent.Category.INFO,
    override val isActive: Boolean = true
): IConveyorMetalRateEvent
