package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.IBizEvent
import ru.datana.smart.ui.converter.common.events.MetalRateCriticalEvent
import ru.datana.smart.ui.converter.common.models.SignalerModel
import ru.datana.smart.ui.converter.common.models.SignalerSoundModel
import java.time.Instant

/*
* AddCriticalEventToHistoryHandler - если прошло время больше, чем значение DATA_TIMEOUT,
* то записываем текущее событие "Критическая ситуация" в историю.
* В зависимости от изменения угла формируется статус события.
* */
object AddCriticalEventToHistoryHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        val slagRateTime = Instant.now()
        val currentAngle = context.currentState.get().lastAngles.angle
        val activeEvent: MetalRateCriticalEvent? =
            context.eventsRepository.getActiveMetalRateEventByMeltId(meltId) as? MetalRateCriticalEvent
        activeEvent?.let {
            val timeStartWithShift = it.timeStart.plusMillis(context.reactionTime)
            val isReactionTimeUp = slagRateTime >= timeStartWithShift
            val isActive = !isReactionTimeUp
            val isUserReacted = it.angleStart - currentAngle > 5
            val executionStatus = when {
                isReactionTimeUp && isUserReacted -> IBizEvent.ExecutionStatus.COMPLETED
                isReactionTimeUp && !isUserReacted -> IBizEvent.ExecutionStatus.FAILED
                else -> IBizEvent.ExecutionStatus.NONE
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
                executionStatus = executionStatus
            )
            context.eventsRepository.put(meltId, currentEvent)
            if (isReactionTimeUp) {
                context.signaler = SignalerModel(
                    level = SignalerModel.SignalerLevelModel.NO_SIGNAL,
                    sound = SignalerSoundModel.NONE
                )
            }
        } ?: return
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}

