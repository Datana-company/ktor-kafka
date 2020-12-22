package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.utils.toPercent

/*
* CreateInfoEventHandler - создаётся событие типа "Информация",
* и светофор переходит в информационный статус.
* */
object CreateInfoSteelEventHandler : IKonveyorHandler<ConverterBeContext> {
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
            type = ModelEvent.EventType.STREAM_RATE_INFO_EVENT,
            timeStart = slagRateTime,
            timeFinish = slagRateTime,
            angleStart = currentAngle,
            title = "Информация",
            textMessage = """
                          Достигнут предел потерь металла в потоке – ${avgSteelRate.toPercent()}%.
                          """.trimIndent(),
            category = ModelEvent.Category.INFO
        )
        context.eventsRepository.create(context.activeEvent)
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
