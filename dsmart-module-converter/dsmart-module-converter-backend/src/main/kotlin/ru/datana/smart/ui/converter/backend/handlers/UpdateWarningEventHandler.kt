package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.EventModel


object UpdateWarningEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.currentMeltInfo.get()?.id ?: return
//        val activeEvent: MetalRateWarningEvent? = context.eventsRepository.getActiveMetalRateEventByMeltId(meltId) as? MetalRateWarningEvent
        val activeEvent: EventModel? = context.eventsRepository.getActiveMetalRateInfoEventByMeltId(meltId)
        activeEvent?.let {
            val isCompletedEvent = it.angleFinish?.let { angleFinish -> it.angleMax?.compareTo(angleFinish)?.let { it > 0 } } ?: false
//            val historicalEvent = BizEvent(
//                id = it.id,
//                type = it.type,
//                timeStart = it.timeStart,
//                timeFinish = it.timeFinish,
//                metalRate = it.metalRate,
//                title = it.title,
//                isActive = false,
//                angleStart = it.angleStart,
//                angleFinish = it.angleFinish,
//                angleMax = it.angleMax,
//                warningPoint = it.warningPoint,
//                executionStatus = if (isCompletedEvent) BizEvent.ExecutionStatus.COMPLETED else BizEvent.ExecutionStatus.FAILED
//            )
            it.executionStatus = if (isCompletedEvent) EventModel.ExecutionStatus.COMPLETED else EventModel.ExecutionStatus.FAILED
            it.isActive = false
            context.eventsRepository.put(meltId, it)
        } ?: return
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
