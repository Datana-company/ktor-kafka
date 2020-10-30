package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.MetalRateWarningEvent
import java.time.Instant
import java.util.*

object CreateWarningEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val frameTime = context.frame.frameTime ?: Instant.now().toEpochMilli()
        val activeEvent: MetalRateWarningEvent? = context.eventsRepository.getActiveMetalRateEvent() as? MetalRateWarningEvent
        activeEvent?.let {
            val updateEvent = MetalRateWarningEvent(
                id = it.id,
                timeStart = if (it.timeStart > frameTime) frameTime else it.timeStart,
                timeFinish = if (it.timeFinish < frameTime) frameTime else it.timeFinish,
                metalRate = it.metalRate,
                title = it.title,
                isActive = it.isActive
            )
            context.eventsRepository.put(updateEvent)
        } ?: context.eventsRepository.put(
            MetalRateWarningEvent(
                id = UUID.randomUUID().toString(),
                timeStart = context.frame.frameTime ?: Instant.now().toEpochMilli(),
                timeFinish = context.frame.frameTime ?: Instant.now().toEpochMilli(),
                metalRate = context.slagRate.steelRate!!
            )
        )
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
