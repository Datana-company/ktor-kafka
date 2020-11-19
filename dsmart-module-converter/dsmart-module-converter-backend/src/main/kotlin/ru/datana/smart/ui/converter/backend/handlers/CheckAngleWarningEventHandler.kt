package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.IBizEvent
import ru.datana.smart.ui.converter.common.events.MetalRateWarningEvent

/*
* CheckAngleWarningEventHandler - если угол стал меньше на 5 градусов,
* то присваиваем текущему событию типа "Предупреждение" статус "Выполнено"
* и записываем его в историю.
* */
object CheckAngleWarningEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        val currentAngle = context.currentState.get().lastAngles.angle
        val activeEvent: MetalRateWarningEvent? =
            context.eventsRepository.getActiveMetalRateEventByMeltId(meltId) as? MetalRateWarningEvent
        activeEvent?.let {
            if (it.angleStart - currentAngle > 5) {
                val currentEvent = MetalRateWarningEvent(
                    id = it.id,
                    timeStart = it.timeStart,
                    timeFinish = it.timeFinish,
                    metalRate = it.metalRate,
                    title = it.title,
                    isActive = false,
                    angleStart = it.angleStart,
                    warningPoint = it.warningPoint,
                    executionStatus = IBizEvent.ExecutionStatus.COMPLETED
                )
                context.eventsRepository.put(meltId, currentEvent)
            }
        } ?: return
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
