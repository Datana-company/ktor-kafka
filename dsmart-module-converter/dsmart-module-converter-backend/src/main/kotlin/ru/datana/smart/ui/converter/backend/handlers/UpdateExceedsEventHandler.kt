package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.MetalRateExceedsEvent


object UpdateExceedsEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val activeEvent: MetalRateExceedsEvent? = context.eventsRepository.getActiveMetalRateEvent() as? MetalRateExceedsEvent
        activeEvent?.let {
            val historicalEvent = MetalRateExceedsEvent(
                id = it.id,
                timeStart = it.timeStart,
                timeFinish = it.timeFinish,
                metalRate = it.metalRate,
                title = it.title,
                isActive = false
            )
            context.eventsRepository.put(historicalEvent)
        } ?: return
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
