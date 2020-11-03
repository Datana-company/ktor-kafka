package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.MetalRateCriticalEvent
import java.time.Instant

object UpdateAngleCriticalEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val frameTime = context.frame.frameTime ?: Instant.now().toEpochMilli()
        val activeEvent = context.eventsRepository.getActiveMetalRateEvent() as? MetalRateCriticalEvent
        val currentAngle = context.angles.angle!!
        activeEvent?.let {
            val angleStart = it.angleStart ?: currentAngle
            val angleMax = if (it.angleMax?.let { it.compareTo(currentAngle) > 0 } == true) it.angleMax else currentAngle
            val historicalEvent = MetalRateCriticalEvent(
                id = it.id,
                timeStart = if (it.timeStart > frameTime) frameTime else it.timeStart,
                timeFinish = if (it.timeFinish < frameTime) frameTime else it.timeFinish,
                metalRate = it.metalRate,
                title = it.title,
                isActive = it.isActive,
                angleStart = angleStart,
                angleFinish = currentAngle,
                angleMax = angleMax
            )
            context.eventsRepository.put(historicalEvent)
        } ?: return
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
