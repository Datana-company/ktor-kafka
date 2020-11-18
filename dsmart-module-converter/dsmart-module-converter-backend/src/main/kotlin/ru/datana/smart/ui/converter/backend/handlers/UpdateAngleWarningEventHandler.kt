package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.MetalRateWarningEvent

object UpdateAngleWarningEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        val activeEvent = context.eventsRepository.getActiveMetalRateEventByMeltId(meltId) as? MetalRateWarningEvent
        val currentAngle = context.angles.angle
        activeEvent?.let {
            val angleStart = it.angleStart ?: currentAngle
            val angleMax = if (it.angleMax?.let { it.compareTo(currentAngle) > 0 } == true) it.angleMax else currentAngle
            val historicalEvent = MetalRateWarningEvent(
                id = it.id,
                timeStart = it.timeStart,
                timeFinish = it.timeFinish,
                metalRate = it.metalRate,
                title = it.title,
                isActive = it.isActive,
                angleStart = angleStart,
                angleFinish = currentAngle,
                angleMax = angleMax,
                warningPoint = context.metalRateWarningPoint,
            )
            context.eventsRepository.put(meltId, historicalEvent)
        } ?: return
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
