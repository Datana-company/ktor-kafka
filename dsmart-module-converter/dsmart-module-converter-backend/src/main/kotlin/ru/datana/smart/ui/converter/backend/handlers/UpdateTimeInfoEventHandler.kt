package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.MetalRateInfoEvent

/*
* UpdateTimeInfoEventHandler - если прошло время больше, чем значение DATA_TIMEOUT,
* то записываем текущее событие "Информация" в историю.
* */
object UpdateTimeInfoEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        val slagRateTime = context.frame.frameTime
        val activeEvent: MetalRateInfoEvent? = context.eventsRepository.getActiveMetalRateEventByMeltId(meltId) as? MetalRateInfoEvent
        activeEvent?.let {
            val isReactionTimeUp = it.timeFinish - it.timeStart >= context.reactionTime
            val isActive = !isReactionTimeUp
            val currentEvent = MetalRateInfoEvent(
                id = it.id,
                timeStart = it.timeStart,
                timeFinish = slagRateTime,
                metalRate = it.metalRate,
                title = it.title,
                isActive = isActive,
                angleStart = it.angleStart,
                warningPoint = it.warningPoint
            )
            context.eventsRepository.put(meltId, currentEvent)
        } ?: return
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
