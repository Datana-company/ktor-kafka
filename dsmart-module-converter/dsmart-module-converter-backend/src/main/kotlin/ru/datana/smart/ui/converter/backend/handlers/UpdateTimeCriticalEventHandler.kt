package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.IBizEvent
import ru.datana.smart.ui.converter.common.events.MetalRateCriticalEvent
import ru.datana.smart.ui.converter.common.models.SignalerModel
import ru.datana.smart.ui.converter.common.models.SignalerSoundModel

/*
* UpdateTimeCriticalEventHandler - если прошло время больше, чем значение DATA_TIMEOUT,
* то записываем текущее событие "Критическая ситуация" в историю.
* Запускается сирена.
* */
object UpdateTimeCriticalEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        val slagRateTime = context.frame.frameTime
        val activeEvent: MetalRateCriticalEvent? =
            context.eventsRepository.getActiveMetalRateEventByMeltId(meltId) as? MetalRateCriticalEvent
        activeEvent?.let {
            val isReactionTimeUp = it.timeFinish - it.timeStart >= context.reactionTime
            val isActive = !isReactionTimeUp
            if (isReactionTimeUp) {
                context.signaler = SignalerModel(
                    level = SignalerModel.SignalerLevelModel.CRITICAL,
                    sound = SignalerSoundModel(
                        SignalerSoundModel.SignalerSoundTypeModel.SOUND_1, 3000
                    )
                )
            }
            val currentEvent = MetalRateCriticalEvent(
                id = it.id,
                timeStart = it.timeStart,
                timeFinish = slagRateTime,
                metalRate = it.metalRate,
                title = it.title,
                isActive = isActive,
                angleStart = it.angleStart,
                criticalPoint = it.criticalPoint,
                executionStatus = if (isReactionTimeUp) IBizEvent.ExecutionStatus.FAILED else IBizEvent.ExecutionStatus.NONE
            )
            context.eventsRepository.put(meltId, currentEvent)
        } ?: return
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}

