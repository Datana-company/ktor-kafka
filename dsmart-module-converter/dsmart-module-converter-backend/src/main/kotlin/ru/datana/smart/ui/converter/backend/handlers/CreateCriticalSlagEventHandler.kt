package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.utils.toPercent

/*
* CreateCriticalSlagEventHandler - создаётся событие типа "Критическая ситуация",
* и светофор переходит в критический статус.
* */
object CreateCriticalSlagEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        if (context.activeEvent != ModelEvent.NONE) {
            return
        }

        val meltId: String = context.currentMeltId
        val currentAngle = context.currentAngle
        val slagRateTime = context.timeStart
        val avgSlagRate = context.avgStreamRate
        context.activeEvent = ModelEvent(
            meltId = meltId,
            type = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
            timeStart = slagRateTime,
            timeFinish = slagRateTime,
            angleStart = currentAngle,
            title = "Критическая ситуация",
            textMessage = """
                      В потоке детектирован шлак – ${avgSlagRate.toPercent()}%, процент потерь превышает критическое значение – ${context.streamRateCriticalPoint.toPercent()}%. Верните конвертер в вертикальное положение!
                      """.trimIndent(),
            category = ModelEvent.Category.CRITICAL
        )
        context.eventsRepository.create(context.activeEvent)
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
