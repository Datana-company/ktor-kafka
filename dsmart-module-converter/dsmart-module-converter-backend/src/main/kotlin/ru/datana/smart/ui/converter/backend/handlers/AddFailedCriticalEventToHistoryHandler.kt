package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.IBizEvent
import ru.datana.smart.ui.converter.common.events.MetalRateCriticalEvent

/*
* AddFailedCriticalEventToHistoryHandler - записывает текущее событие "Критическая ситуация"
* в историю со статусом "Не выполнено"
* */
object AddFailedCriticalEventToHistoryHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        val activeEvent: MetalRateCriticalEvent? = context.eventsRepository.getActiveMetalRateEventByMeltId(meltId) as? MetalRateCriticalEvent
        activeEvent?.let {
            val historicalEvent = MetalRateCriticalEvent(
                id = it.id,
                timeStart = it.timeStart,
                timeFinish = it.timeFinish,
                metalRate = it.metalRate,
                title = it.title,
                isActive = false,
                angleStart = it.angleStart,
                criticalPoint = it.criticalPoint,
                executionStatus = IBizEvent.ExecutionStatus.FAILED
            )
            context.eventsRepository.put(meltId, historicalEvent)
        } ?: return
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
