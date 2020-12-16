package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.utils.toPercent

/*
* CreateSuccessMeltSlagEventHandler - создаётся событие типа "Информация" об успешном завершении плавки
* и сразу записывается в историю.
* */
object CreateSuccessMeltSlagEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        context.activeEvent.takeIf { it == ModelEvent.NONE }?.let {
            val meltId: String = context.meltInfo.id
            val slagRateTime = context.timeStart
            context.eventsRepository.getAllByMeltId(meltId).map {
                if (it.type == ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT ||
                    it.type == ModelEvent.EventType.STREAM_RATE_WARNING_EVENT
                ) {
                    return
                }
            }
            context.activeEvent = ModelEvent(
                meltId = meltId,
                type = ModelEvent.EventType.SUCCESS_MELT_EVENT,
                timeStart = slagRateTime,
                timeFinish = slagRateTime,
                isActive = false,
                title = "Информация",
                textMessage = """
                          Допустимая норма потерь шлака ${context.streamRateWarningPoint.toPercent()}% не была превышена.
                          """.trimIndent(),
                category = ModelEvent.Category.INFO,
                executionStatus = ModelEvent.ExecutionStatus.STATELESS
            )
            context.eventsRepository.create(context.activeEvent)
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
