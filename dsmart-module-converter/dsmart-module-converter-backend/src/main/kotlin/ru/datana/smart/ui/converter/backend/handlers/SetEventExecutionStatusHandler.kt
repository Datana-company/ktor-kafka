package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.models.ModelEvent.Category
import ru.datana.smart.ui.converter.common.models.ModelEvent.ExecutionStatus

/*
* SetEventExecutionStatusHandler - если прошло время больше, чем значение DATA_TIMEOUT,
* то записываем текущее событие в историю.
* В зависимости от изменения угла формируется статус события.
* */
object SetEventExecutionStatusHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        with(context.activeEvent) {
            if (this == ModelEvent.NONE || (category != Category.CRITICAL && category != Category.WARNING))
                return

            val slagRateTime = context.timeStart
            val currentAngle = context.currentAngle
            val timeStartWithShift = timeStart.plusMillis(context.reactionTime)
            val isReactionTimeUp = slagRateTime >= timeStartWithShift
            val isUserReacted = angleStart - currentAngle > 5

            executionStatus = when {
                isReactionTimeUp && isUserReacted -> ExecutionStatus.COMPLETED
                isReactionTimeUp && !isUserReacted -> ExecutionStatus.FAILED
                else -> ExecutionStatus.STATELESS
            }
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
