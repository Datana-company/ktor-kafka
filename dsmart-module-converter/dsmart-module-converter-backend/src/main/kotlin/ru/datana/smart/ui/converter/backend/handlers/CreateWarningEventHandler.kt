package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.IBizEvent
import ru.datana.smart.ui.converter.common.events.MetalRateWarningEvent
import java.util.*

object CreateWarningEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.currentState.get()?.currentMeltInfo?.id ?: return
        val slagRateTime = context.frame.frameTime
        val activeEvent: MetalRateWarningEvent? = context.eventsRepository.getActiveMetalRateEventByMeltId(meltId) as? MetalRateWarningEvent
        activeEvent?.let {
            val isReactionTimeUp = slagRateTime - it.timeStart >= context.reactionTime
            if (isReactionTimeUp) {
                val newEvent = MetalRateWarningEvent(
                    id = UUID.randomUUID().toString(),
                    timeStart = slagRateTime,
                    timeFinish = slagRateTime,
                    metalRate = context.slagRate.steelRate,
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
        } ?: context.eventsRepository.put(
            meltId,
            MetalRateWarningEvent(
                id = UUID.randomUUID().toString(),
                timeStart = slagRateTime,
                timeFinish = slagRateTime,
                metalRate = context.slagRate.steelRate,
                warningPoint = context.metalRateWarningPoint
            )
        )
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
