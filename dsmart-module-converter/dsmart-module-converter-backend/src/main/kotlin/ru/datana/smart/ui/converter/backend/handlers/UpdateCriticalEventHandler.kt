package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.IBizEvent
import ru.datana.smart.ui.converter.common.events.MetalRateCriticalEvent
import ru.datana.smart.ui.converter.common.models.ModelEvent

object UpdateCriticalEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.currentState.get()?.currentMeltInfo?.id ?: return
        val activeEvent: ModelEvent? = context.eventsRepository
            .getActiveByMeltIdAndEventType(meltId, ModelEvent.EventType.METAL_RATE_CRITICAL_EVENT)
        activeEvent?.let {
            val isCompletedEvent = it.angleFinish?.let { angleFinish -> it.angleMax?.compareTo(angleFinish)?.let { it > 0 } } ?: false
//            val historicalEvent = MetalRateCriticalEvent(
//                id = it.id,
//                timeStart = it.timeStart,
//                timeFinish = it.timeFinish,
//                metalRate = it.metalRate,
//                title = it.title,
//                isActive = false,
//                angleStart = it.angleStart,
//                angleFinish = it.angleFinish,
//                angleMax = it.angleMax,
//                criticalPoint = it.criticalPoint,
//                executionStatus = if (isCompletedEvent) IBizEvent.ExecutionStatus.COMPLETED else IBizEvent.ExecutionStatus.FAILED
//            )
            it.executionStatus = if (isCompletedEvent) ModelEvent.ExecutionStatus.COMPLETED else ModelEvent.ExecutionStatus.FAILED
            it.isActive = false
            context.eventsRepository.update(it)
        } ?: return
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
