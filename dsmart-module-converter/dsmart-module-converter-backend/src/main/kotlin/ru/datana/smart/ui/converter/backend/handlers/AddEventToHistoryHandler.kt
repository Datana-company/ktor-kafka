package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent

/*
* AddEventToHistoryHandler - если прошло время больше, чем значение DATA_TIMEOUT,
* то записываем текущее событие в историю.
* В зависимости от изменения угла формируется статус события.
* */
object AddEventToHistoryHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        context.activeEvent.takeIf { it != ModelEvent.NONE }?.let {
            val slagRateTime = context.timeStart
            val currentAngle = context.currentAngle
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
            if (isReactionTimeUp) context.activeEvent = ModelEvent.NONE
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
