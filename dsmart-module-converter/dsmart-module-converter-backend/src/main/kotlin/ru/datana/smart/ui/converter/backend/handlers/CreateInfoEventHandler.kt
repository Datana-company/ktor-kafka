package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.MetalRateInfoEvent
import ru.datana.smart.ui.converter.common.models.SignalerModel
import ru.datana.smart.ui.converter.common.models.SignalerSoundModel
import java.time.Instant
import java.util.*

object CreateInfoEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        val activeEvent: MetalRateInfoEvent? =
            context.eventsRepository.getActiveMetalRateEventByMeltId(meltId) as? MetalRateInfoEvent
        val slagRateTime = Instant.now()
        activeEvent?.let {
            val timeStartWithShift = it.timeStart.plusMillis(context.reactionTime)
            val isReactionTimeUp = slagRateTime >= timeStartWithShift
            if (isReactionTimeUp) {
                val newEvent = MetalRateInfoEvent(
                    id = UUID.randomUUID().toString(),
                    timeStart = slagRateTime,
                    timeFinish = slagRateTime,
                    metalRate = context.slagRate.avgSteelRate,
                    warningPoint = context.metalRateWarningPoint
                )
                context.eventsRepository.put(meltId, newEvent)
            }
            val currentUpdatedEvent = MetalRateInfoEvent(
                id = it.id,
                timeStart = it.timeStart,
                timeFinish = slagRateTime,
                metalRate = it.metalRate,
                title = it.title,
                isActive = !isReactionTimeUp,
                angleStart = it.angleStart,
                angleFinish = it.angleFinish,
                angleMax = it.angleMax,
                warningPoint = it.warningPoint
            )
            context.eventsRepository.put(meltId, currentUpdatedEvent)
        } ?: run {
            context.eventsRepository.put(
                meltId,
                MetalRateInfoEvent(
                    id = UUID.randomUUID().toString(),
                    timeStart = slagRateTime,
                    timeFinish = slagRateTime,
                    metalRate = context.slagRate.avgSteelRate,
                    warningPoint = context.metalRateWarningPoint
                )
            )
            context.signaler = SignalerModel(
                level = SignalerModel.SignalerLevelModel.INFO,
                sound = SignalerSoundModel.NONE
            )
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
