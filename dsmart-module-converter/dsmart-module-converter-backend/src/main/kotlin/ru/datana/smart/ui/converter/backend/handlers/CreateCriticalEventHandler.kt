package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.IBizEvent
import ru.datana.smart.ui.converter.common.events.MetalRateCriticalEvent
import ru.datana.smart.ui.converter.common.models.SignalerModel
import ru.datana.smart.ui.converter.common.models.SignalerSoundModel
import java.util.*

object CreateCriticalEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        val slagRateTime = context.frame.frameTime
        val activeEvent: MetalRateCriticalEvent? =
            context.eventsRepository.getActiveMetalRateEventByMeltId(meltId) as? MetalRateCriticalEvent
        activeEvent?.let {
            val isReactionTimeUp = it.timeFinish - it.timeStart >= context.reactionTime
            if (isReactionTimeUp) {
                val newEvent = MetalRateCriticalEvent(
                    id = UUID.randomUUID().toString(),
                    timeStart = slagRateTime,
                    timeFinish = slagRateTime,
                    metalRate = context.slagRate.avgSteelRate,
                    criticalPoint = context.metalRateCriticalPoint
                )
                context.eventsRepository.put(meltId, newEvent)
                context.signaler = SignalerModel(
                    level = SignalerModel.SignalerLevelModel.CRITICAL,
                    sound = SignalerSoundModel(
                        SignalerSoundModel.SignalerSoundTypeModel.SOUND_1, 3000
                    )
                )
            }
            val currentUpdatedEvent = MetalRateCriticalEvent(
                id = it.id,
                timeStart = it.timeStart,
                timeFinish = slagRateTime,
                metalRate = it.metalRate,
                title = it.title,
                isActive = !isReactionTimeUp,
                angleStart = it.angleStart,
                angleFinish = it.angleFinish,
                angleMax = it.angleMax,
                criticalPoint = it.criticalPoint,
                executionStatus = if (isReactionTimeUp) IBizEvent.ExecutionStatus.FAILED else IBizEvent.ExecutionStatus.NONE
            )
            context.eventsRepository.put(meltId, currentUpdatedEvent)
        } ?: run {
            context.eventsRepository.put(
                meltId,
                MetalRateCriticalEvent(
                    id = UUID.randomUUID().toString(),
                    timeStart = slagRateTime,
                    timeFinish = slagRateTime,
                    metalRate = context.slagRate.avgSteelRate,
                    criticalPoint = context.metalRateCriticalPoint
                )
            )
            context.signaler = SignalerModel(
                level = SignalerModel.SignalerLevelModel.CRITICAL,
                sound = SignalerSoundModel.NONE
            )
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}

