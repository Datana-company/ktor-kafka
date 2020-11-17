package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.IBizEvent
import ru.datana.smart.ui.converter.common.events.MetalRateInfoEvent
import ru.datana.smart.ui.converter.common.events.MetalRateWarningEvent
import ru.datana.smart.ui.converter.common.models.SignalerModel
import ru.datana.smart.ui.converter.common.models.SignalerSoundModel
import java.time.Instant
import java.util.*

object CreateWarningEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        val activeEvent: MetalRateInfoEvent? =
            context.eventsRepository.getActiveMetalRateEventByMeltId(meltId) as? MetalRateInfoEvent
        val slagRateTime = Instant.now()
        activeEvent?.let {
            val timeStartWithShift = it.timeStart.plusMillis(context.reactionTime)
            val isReactionTimeUp = slagRateTime >= timeStartWithShift
            println("isReactionTimeUp = ${isReactionTimeUp}, slagRateTime = ${slagRateTime}, timeStartWithShift = ${timeStartWithShift}, timeStart = ${it.timeStart}")
            if (isReactionTimeUp) {
                val newEvent = MetalRateWarningEvent(
                    id = UUID.randomUUID().toString(),
                    timeStart = slagRateTime,
                    timeFinish = slagRateTime,
                    metalRate = context.slagRate.avgSteelRate,
                    warningPoint = context.metalRateWarningPoint
                )
                context.eventsRepository.put(meltId, newEvent)
            }
            val currentUpdatedEvent = MetalRateWarningEvent(
                id = it.id,
                timeStart = it.timeStart,
                timeFinish = slagRateTime,
                metalRate = it.metalRate,
                title = it.title,
                isActive = !isReactionTimeUp,
                angleStart = it.angleStart,
                angleFinish = it.angleFinish,
                warningPoint = it.warningPoint,
                executionStatus = if (isReactionTimeUp) IBizEvent.ExecutionStatus.FAILED else IBizEvent.ExecutionStatus.NONE
            )
            context.eventsRepository.put(meltId, currentUpdatedEvent)
        } ?: run {
            context.eventsRepository.put(
                meltId,
                MetalRateWarningEvent(
                    id = UUID.randomUUID().toString(),
                    timeStart = slagRateTime,
                    timeFinish = slagRateTime,
                    metalRate = context.slagRate.avgSteelRate,
                    warningPoint = context.metalRateWarningPoint
                )
            )
            context.signaler = SignalerModel(
                level = SignalerModel.SignalerLevelModel.WARNING,
                sound = SignalerSoundModel.NONE
            )
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
