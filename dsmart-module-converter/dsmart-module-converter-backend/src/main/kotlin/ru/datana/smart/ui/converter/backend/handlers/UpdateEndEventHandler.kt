package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.EndMeltEvent
import ru.datana.smart.ui.converter.common.models.ModelEvent

object UpdateEndEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.currentState.get()?.currentMeltInfo?.id ?: return
        val activeEvent: ModelEvent? = context.eventsRepository
            .getActiveByMeltIdAndEventType(meltId, ModelEvent.EventType.END_MELT_EVENT)
        activeEvent?.let {
//            val historicalEvent = EndMeltEvent(
//                id = it.id,
//                timeStart = it.timeStart,
//                timeFinish = it.timeFinish,
//                metalRate = it.metalRate,
//                title = it.title,
//                isActive = false,
//                angleStart = it.angleStart,
//                angleFinish = it.angleFinish
//            )
            it.isActive = false
            context.eventsRepository.update(it)
        } ?: return
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
