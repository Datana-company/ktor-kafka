package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.MetalRateInfoEvent
import java.time.Instant

object UpdateAngleInfoEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val frameTime = context.frame.frameTime ?: Instant.now().toEpochMilli()
        val activeEvent = context.eventsRepository.getActiveMetalRateEvent() as? MetalRateInfoEvent
        val currentAngle = context.angles.angle!!
        activeEvent?.let {
            val angleStart = it.angleStart ?: currentAngle
            val historicalEvent = MetalRateInfoEvent(
                id = it.id,
                timeStart = if (it.timeStart > frameTime) frameTime else it.timeStart,
                timeFinish = if (it.timeFinish < frameTime) frameTime else it.timeFinish,
                metalRate = it.metalRate,
                title = it.title,
                isActive = it.isActive,
                angleStart = angleStart,
                angleFinish = currentAngle
            )
            context.eventsRepository.put(historicalEvent)
        } ?: return
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
