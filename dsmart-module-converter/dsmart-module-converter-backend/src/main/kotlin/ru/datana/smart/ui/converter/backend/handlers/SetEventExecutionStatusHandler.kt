package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.models.ModelEvent.Category
import ru.datana.smart.ui.converter.common.models.ModelEvent.ExecutionStatus

/*
* SetEventExecutionStatusHandler - текущему событию присваиваем статус выполнения
* */
object SetEventExecutionStatusHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        with(context.activeEvent) {
            // статус выполнения вычисляется только у событий категории Critical и Warning
            if (this == ModelEvent.NONE || (category != Category.CRITICAL && category != Category.WARNING))
                return

            val slagRateTime = context.timeStart
            val currentAngle = context.currentStateRepository.currentAngle(null)
            val timeStartWithShift = timeStart.plusMillis(context.reactionTime)
            // сравнивается текущее время со временем стартовым + время реакции,
            // если текущее время больше, то время реакции пользователя истекло
            val isReactionTimeUp = slagRateTime >= timeStartWithShift
            // сравнивается текущий угол конвертера и угол при создании рекомендации,
            // если текущий угол больше на 5 градусов,
            // то считается, что пользователь отреагировал на событие
            val isUserReacted = angleStart - currentAngle > 5

            // вычисление статуса выполнения
            executionStatus = when {
                isReactionTimeUp && isUserReacted -> ExecutionStatus.COMPLETED
                isReactionTimeUp && !isUserReacted -> ExecutionStatus.FAILED
                else -> ExecutionStatus.NONE
            }
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
