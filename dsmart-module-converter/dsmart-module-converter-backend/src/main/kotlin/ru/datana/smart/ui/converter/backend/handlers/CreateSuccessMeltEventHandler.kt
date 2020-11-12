package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.utils.toPercent
import java.util.*

object CreateSuccessMeltEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        val slagRateTime = context.frame.frameTime
        context.eventsRepository.getAllByMeltId(meltId)
            .map {
                if (it.type == ModelEvent.EventType.METAL_RATE_CRITICAL_EVENT ||
                    it.type == ModelEvent.EventType.METAL_RATE_WARNING_EVENT ||
                    it.type == ModelEvent.EventType.END_MELT_EVENT) {
                    return
                }
            }
        context.eventsRepository.create(
            ModelEvent(
                id = UUID.randomUUID().toString(),
                meltId = meltId,
                type = ModelEvent.EventType.SUCCESS_MELT_EVENT,
                timeStart = slagRateTime,
                timeFinish = slagRateTime,
                warningPoint = context.metalRateWarningPoint,
                isActive = false,
                title = "Информация",
                textMessage = """
                              Допустимая норма потерь металла ${toPercent(context.metalRateWarningPoint)} % не была превышена.
                              """.trimIndent(),
                category = ModelEvent.Category.INFO
            )
        )
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
