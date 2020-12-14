package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.models.SignalerModel
import ru.datana.smart.ui.converter.common.models.SignalerSoundModel

/*
* AddWarningEventToHistoryHandler - если прошло время больше, чем значение DATA_TIMEOUT,
* то записываем текущее событие "Предупреждение" в историю.
* В зависимости от изменения угла формируется статус события.
* */
object AddWarningEventToHistoryHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        val slagRateTime = context.timeStart
        val currentAngle = context.currentAngle
        val activeEvent: ModelEvent? = context.eventsRepository
            .getActiveByMeltIdAndEventType(meltId, ModelEvent.EventType.STREAM_RATE_WARNING_EVENT)
        activeEvent?.let {
            val timeStartWithShift = it.timeStart.plusMillis(context.reactionTime)
            val isReactionTimeUp = slagRateTime >= timeStartWithShift
            val isActive = !isReactionTimeUp
            val isUserReacted = it.angleStart - currentAngle > 5
            val executionStatus = when {
                isReactionTimeUp && isUserReacted -> ModelEvent.ExecutionStatus.COMPLETED
                isReactionTimeUp && !isUserReacted -> ModelEvent.ExecutionStatus.FAILED
                else -> ModelEvent.ExecutionStatus.STATELESS
            }
            it.timeFinish = slagRateTime
            it.isActive = isActive
            it.executionStatus = executionStatus
            context.eventsRepository.update(it)
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
