package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent
import java.time.Instant

/*
* UpdateTimeInfoEventHandler - если прошло время больше, чем значение DATA_TIMEOUT,
* то записываем текущее событие "Информация" в историю.
* */
object UpdateTimeInfoEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        val activeEvent: ModelEvent? = context.eventsRepository
            .getActiveByMeltIdAndEventType(meltId, ModelEvent.EventType.METAL_RATE_INFO_EVENT)
        val slagRateTime = Instant.now()
        activeEvent?.let {
            val timeStartWithShift = it.timeStart.plusMillis(context.reactionTime)
            val isReactionTimeUp = slagRateTime >= timeStartWithShift
            val isActive = !isReactionTimeUp
            it.timeFinish = slagRateTime
            it.isActive = isActive
            context.eventsRepository.update(it)
        } ?: return
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
