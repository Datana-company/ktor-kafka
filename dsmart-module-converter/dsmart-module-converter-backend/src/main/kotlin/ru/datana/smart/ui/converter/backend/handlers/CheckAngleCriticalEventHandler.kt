package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent

/*
* CheckAngleCriticalEventHandler - если угол стал меньше на 5 градусов,
* то присваиваем текущему событию типа "Критическая ситуация" статус "Выполнено"
* и записываем его в историю.
* */
object CheckAngleCriticalEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        val currentAngle = context.currentState.get().lastAngles.angle
        val activeEvent: ModelEvent? = context.eventsRepository
            .getActiveByMeltIdAndEventType(meltId, ModelEvent.EventType.METAL_RATE_CRITICAL_EVENT)
        activeEvent?.let {
            if (it.angleStart - currentAngle > 5) {
                it.isActive = false
                it.executionStatus = ModelEvent.ExecutionStatus.COMPLETED
                context.eventsRepository.create(it)
            }
        } ?: return
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
