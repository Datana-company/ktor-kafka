package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.MetalRateCriticalEvent
import java.time.Instant
import java.util.*

object CreateCriticalEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.currentMeltInfo.get()?.id ?: return
        val frameTime = context.frame.frameTime ?: Instant.now().toEpochMilli()
        val activeEvent: MetalRateCriticalEvent? = context.eventsRepository.getActiveMetalRateEventByMeltId(meltId) as? MetalRateCriticalEvent
        activeEvent?.let {
            val updateEvent = MetalRateCriticalEvent(
                id = it.id,
                timeStart = if (it.timeStart > frameTime) frameTime else it.timeStart,
                timeFinish = if (it.timeFinish < frameTime) frameTime else it.timeFinish,
                metalRate = it.metalRate,
                title = it.title,
                isActive = it.isActive,
                angleStart = it.angleStart,
                angleFinish = it.angleFinish,
                angleMax = it.angleMax,
                criticalPoint = it.criticalPoint
            )
            context.eventsRepository.put(meltId, updateEvent)
        } ?: context.eventsRepository.put(
            meltId,
            MetalRateCriticalEvent(
                id = UUID.randomUUID().toString(),
                timeStart = context.frame.frameTime ?: Instant.now().toEpochMilli(),
                timeFinish = context.frame.frameTime ?: Instant.now().toEpochMilli(),
                metalRate = context.slagRate.steelRate!!,
                criticalPoint = context.metalRateCriticalPoint
            )
        )
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
