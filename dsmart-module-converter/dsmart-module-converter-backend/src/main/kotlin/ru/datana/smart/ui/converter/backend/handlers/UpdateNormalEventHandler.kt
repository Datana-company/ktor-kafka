package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.IMetalRateEvent
import ru.datana.smart.ui.converter.common.events.MetalRateInfoEvent
import ru.datana.smart.ui.converter.common.events.MetalRateNormalEvent

object UpdateNormalEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val activeEvent: MetalRateNormalEvent? = context.eventsRepository.getActive().find { it is IMetalRateEvent } as? MetalRateNormalEvent
        activeEvent?.let {
            val historicalEvent = MetalRateNormalEvent(
                id = it.id,
                timeStart = it.timeStart,
                timeFinish = it.timeFinish,
                metalRate = it.metalRate,
                title = it.title,
                isActive = false
            )
            context.eventsRepository.put(historicalEvent)
        } ?: return    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
