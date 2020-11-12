package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.IBizEvent
import ru.datana.smart.ui.converter.common.events.MetalRateCriticalEvent
import ru.datana.smart.ui.converter.common.models.SignalerModel
import ru.datana.smart.ui.converter.common.models.SignalerSoundModel

object UpdateCriticalEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.currentState.get()?.currentMeltInfo?.id ?: return
        val activeEvent: MetalRateCriticalEvent? = context.eventsRepository.getActiveMetalRateEventByMeltId(meltId) as? MetalRateCriticalEvent
        activeEvent?.let {
            val isCompletedEvent = it.angleFinish?.let { angleFinish -> it.angleMax?.compareTo(angleFinish)?.let { it > 0 } } ?: false
            val historicalEvent = MetalRateCriticalEvent(
                id = it.id,
                timeStart = it.timeStart,
                timeFinish = it.timeFinish,
                metalRate = it.metalRate,
                title = it.title,
                isActive = false,
                angleStart = it.angleStart,
                angleFinish = it.angleFinish,
                angleMax = it.angleMax,
                criticalPoint = it.criticalPoint,
                executionStatus = if (isCompletedEvent) IBizEvent.ExecutionStatus.COMPLETED else IBizEvent.ExecutionStatus.FAILED
            )
            context.eventsRepository.put(meltId, historicalEvent)
            if (isCompletedEvent) {
                context.signaler = SignalerModel(
                    level = SignalerModel.SignalerLevelModel.CRITICAL,
                    sound = SignalerSoundModel(
                        type = SignalerSoundModel.SignalerSoundTypeModel.NONE,
                        interval = 1
                    )
                )
            } else {
                context.signaler = SignalerModel(
                    level = SignalerModel.SignalerLevelModel.CRITICAL,
                    sound = SignalerSoundModel(
                        type = SignalerSoundModel.SignalerSoundTypeModel.SOUND_1,
                        interval = 1
                    )
                )
            }
        } ?: return
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
