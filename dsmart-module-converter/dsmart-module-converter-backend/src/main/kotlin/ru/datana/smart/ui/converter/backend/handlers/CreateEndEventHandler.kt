package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent
import java.util.*

object CreateEndEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.currentState.get()?.currentMeltInfo?.id ?: return
        val slagRateTime = context.frame.frameTime
        context.eventsRepository.getActiveByMeltIdAndEventType(meltId, ModelEvent.EventType.END_MELT_EVENT)
            ?: context.eventsRepository.create(
                ModelEvent(
                    id = UUID.randomUUID().toString(),
                    meltId = meltId,
                    type = ModelEvent.EventType.END_MELT_EVENT,
                    timeStart = slagRateTime,
                    timeFinish = slagRateTime,
                    metalRate = context.slagRate.steelRate,
                    title = "Информация",
                    textMessage = """
                                  Скачивание шлака завершено.
                                  """.trimIndent(),
                    category = ModelEvent.Category.INFO
                )
            )
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
