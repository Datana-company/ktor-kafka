package ru.datana.smart.ui.converter.app.cor.repository.events

import java.time.Instant
import java.util.*

data class ConveyorMetalRateCriticalEvent(
    override val id: String = UUID.randomUUID().toString(),
    override val timeStart: Long = Instant.now().toEpochMilli(),
    override val timeFinish: Long = Instant.now().toEpochMilli(),
    val steelRate: Double? = null,
    override val title: String? = "Критическая ошибка",
    override val textMessage: String? = """
                                        В потоке детектирован $steelRate металла, превышен допустимый предел % потери.
                                        Верните конвертер в вертикальное положение.
                                        """.trimIndent(),
    override val category: IUserEvent.Category = IUserEvent.Category.CRITICAL,
    override val isActive: Boolean = true,
): IUserEvent
