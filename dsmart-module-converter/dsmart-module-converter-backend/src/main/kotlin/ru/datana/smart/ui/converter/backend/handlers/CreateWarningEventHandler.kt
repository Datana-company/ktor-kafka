package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.MetalRateWarningEvent
import java.util.*

object CreateWarningEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.currentMeltInfo.get()?.id ?: return
        val slagRateTime = context.frame.frameTime
        val activeEvent: MetalRateWarningEvent? = context.eventsRepository.getActiveMetalRateEventByMeltId(meltId) as? MetalRateWarningEvent
        activeEvent?.let {
            val updateEvent = MetalRateWarningEvent(
                id = it.id,
                timeStart = it.timeStart,
                timeFinish = slagRateTime,
                metalRate = it.metalRate,
                title = it.title,
                isActive = it.isActive,
                angleStart = it.angleStart,
                angleFinish = it.angleFinish,
                angleMax = it.angleMax,
                warningPoint = it.warningPoint
            )
            context.eventsRepository.put(meltId, updateEvent)
        } ?: context.eventsRepository.put(
            meltId,
            MetalRateWarningEvent(
                id = UUID.randomUUID().toString(),
                timeStart = slagRateTime,
                timeFinish = slagRateTime,
                metalRate = context.slagRate.steelRate,
                warningPoint = context.metalRateWarningPoint
            )
        )
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
