package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.MetalRateWarningEvent

//TODO: придумать другое имя
/*
* AddHistoryWarningEventHandler - записывает текущее событие "Предупреждение" в историю без изменения статуса
* */

object AddHistoryWarningEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        val activeEvent: MetalRateWarningEvent? = context.eventsRepository.getActiveMetalRateEventByMeltId(meltId) as? MetalRateWarningEvent
        activeEvent?.let {
            val historicalEvent = MetalRateWarningEvent(
                id = it.id,
                timeStart = it.timeStart,
                timeFinish = it.timeFinish,
                metalRate = it.metalRate,
                title = it.title,
                isActive = false,
                angleStart = it.angleStart,
                angleFinish = it.angleFinish,
                angleMax = it.angleMax,
                warningPoint = it.warningPoint
            )
            context.eventsRepository.put(meltId, historicalEvent)
        } ?: return
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
