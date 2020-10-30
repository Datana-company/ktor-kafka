package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.IBizEvent
import ru.datana.smart.ui.converter.common.events.IMetalRateEvent
import ru.datana.smart.ui.converter.common.events.MetalRateCriticalEvent

object UpdateCriticalEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val activeEvent: MetalRateCriticalEvent? = context.eventsRepository.getActiveMetalRateEvent() as? MetalRateCriticalEvent
        activeEvent?.let {
            val historicalEvent = MetalRateCriticalEvent(
                id = it.id,
                timeStart = it.timeStart,
                timeFinish = it.timeFinish,
                metalRate = it.metalRate,
                title = it.title,
                isActive = false,
                angleStart = it.angleStart,
                angleFinish = context.angles.angle!!,
                executionStatus = if (it.angleStart > context.angles.angle!!) IBizEvent.ExecutionStatus.COMPLETED else IBizEvent.ExecutionStatus.FAILED
            )
            context.eventsRepository.put(historicalEvent)
        } ?: return
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
