package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.utils.toPercent

/*
* CreateWarningSteelEventHandler - создаём событие типа "Предупреждение" по содержанию металла.
* */
object CreateWarningSteelEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        if (context.activeEvent != ModelEvent.NONE) {
            return
        }

        val meltId: String = context.currentMeltId
        val slagRateTime = context.timeStart
        val currentAngle = context.currentAngle
        val avgSteelRate = context.avgStreamRate
        context.activeEvent = ModelEvent(
            meltId = meltId,
            type = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
            timeStart = slagRateTime,
            timeFinish = slagRateTime,
            angleStart = currentAngle,
            title = "Предупреждение",
            textMessage = """
                      В потоке детектирован металл – ${avgSteelRate.toPercent()}% сверх допустимой нормы ${context.streamRateWarningPoint.toPercent()}%. Верните конвертер в вертикальное положение.
                      """.trimIndent(),
            category = ModelEvent.Category.WARNING
        )
        context.eventsRepository.create(context.activeEvent)
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
