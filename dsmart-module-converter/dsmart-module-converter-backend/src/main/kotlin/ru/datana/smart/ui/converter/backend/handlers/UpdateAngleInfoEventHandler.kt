package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.MetalRateInfoEvent
import ru.datana.smart.ui.converter.common.models.ModelEvent

object UpdateAngleInfoEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.currentState.get()?.currentMeltInfo?.id ?: return
        val activeEvent: ModelEvent? = context.eventsRepository
            .getActiveByMeltIdAndEventType(meltId, ModelEvent.EventType.METAL_RATE_INFO_EVENT)
        val currentAngle = context.angles.angle
        activeEvent?.let {
            val angleStart = it.angleStart ?: currentAngle
            val angleMax = if (it.angleMax?.let { it.compareTo(currentAngle) > 0 } == true) it.angleMax else currentAngle
//            val historicalEvent = MetalRateInfoEvent(
//                id = it.id,
//                timeStart = it.timeStart,
//                timeFinish = it.timeFinish,
//                metalRate = it.metalRate,
//                title = it.title,
//                isActive = it.isActive,
//                angleStart = angleStart,
//                angleFinish = currentAngle,
//                angleMax = angleMax
//            )
            it.angleStart = angleStart
            it.angleFinish = currentAngle
            it.angleMax = angleMax
            it.isActive = false
            context.eventsRepository.update(it)
        } ?: return
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
