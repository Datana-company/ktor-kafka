package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.models.ModelSignaler
import ru.datana.smart.ui.converter.common.models.ModelSignalerSound
import ru.datana.smart.ui.converter.common.utils.toPercent

/*
* CreateCriticalEventHandler - создаётся событие типа "Критическая ситуация",
* и светофор переходит в критический статус.
* */
object CreateCriticalSteelEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        context.activeEvent.takeIf { it == ModelEvent.NONE }?.let {
            val meltId: String = context.meltInfo.id
            val currentAngle = context.currentAngle
            val slagRateTime = context.timeStart
            val avgSteelRate = context.avgStreamRate
            context.activeEvent = ModelEvent(
                meltId = meltId,
                type = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = slagRateTime,
                timeFinish = slagRateTime,
                angleStart = currentAngle,
                title = "Критическая ситуация",
                textMessage = """
                              В потоке детектирован металл – ${avgSteelRate.toPercent()}%, процент потерь превышает критическое значение – ${context.streamRateCriticalPoint.toPercent()}%. Верните конвертер в вертикальное положение!
                              """.trimIndent(),
                category = ModelEvent.Category.CRITICAL,
                executionStatus = ModelEvent.ExecutionStatus.STATELESS
            )
            context.eventsRepository.create(context.activeEvent)
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
