package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.*
import java.time.Instant
import java.util.*

object MetalRateNormalHandler: IKonveyorHandler<ConverterBeContext> {

    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val metalRate = context.slagRate.steelRate ?: return
        val warningPoint = context.metalRateWarningPoint
        if (metalRate != warningPoint) {
            return
        }

        val frameTime = context.frame.frameTime ?: Instant.now().toEpochMilli()
        val activeEvent: IMetalRateEvent? = context.eventsRepository.getActive().find { it is IMetalRateEvent } as? IMetalRateEvent

        activeEvent?.let {
            when(it) {
                is MetalRateNormalEvent -> {
                    val updateEvent = MetalRateNormalEvent(
                        id = it.id,
                        timeStart = if (it.timeStart > frameTime) frameTime else it.timeStart,
                        timeFinish = if (it.timeFinish < frameTime) frameTime else it.timeFinish,
                        metalRate = if (it.metalRate < metalRate) metalRate else it.metalRate,
                        title = it.title,
                        isActive = it.isActive
                    )
                    context.eventsRepository.put(updateEvent)
                }
                is MetalRateCriticalEvent -> {
                    val historicalEvent = MetalRateCriticalEvent(
                        id = it.id,
                        timeStart = it.timeStart,
                        timeFinish = it.timeFinish,
                        metalRate = it.metalRate,
                        title = it.title,
                        isActive = false
                    )
                    context.eventsRepository.put(historicalEvent)
                    val newEvent = MetalRateNormalEvent(
                        id = UUID.randomUUID().toString(),
                        timeStart = frameTime,
                        timeFinish = frameTime,
                        metalRate = metalRate
                    )
                    context.eventsRepository.put(newEvent)
                }
                is MetalRateExceedsEvent -> {
                    val historicalEvent = MetalRateExceedsEvent(
                        id = it.id,
                        timeStart = it.timeStart,
                        timeFinish = it.timeFinish,
                        metalRate = it.metalRate,
                        title = it.title,
                        isActive = false
                    )
                    context.eventsRepository.put(historicalEvent)
                    val newEvent = MetalRateNormalEvent(
                        id = UUID.randomUUID().toString(),
                        timeStart = frameTime,
                        timeFinish = frameTime,
                        metalRate = metalRate
                    )
                    context.eventsRepository.put(newEvent)
                }
                is MetalRateInfoEvent -> {
                    val historicalEvent = MetalRateInfoEvent(
                        id = it.id,
                        timeStart = it.timeStart,
                        timeFinish = it.timeFinish,
                        metalRate = it.metalRate,
                        title = it.title,
                        isActive = false
                    )
                    context.eventsRepository.put(historicalEvent)
                    val newEvent = MetalRateNormalEvent(
                        id = UUID.randomUUID().toString(),
                        timeStart = frameTime,
                        timeFinish = frameTime,
                        metalRate = metalRate
                    )
                    context.eventsRepository.put(newEvent)
                }
            }
        } ?: context.eventsRepository.put(
            MetalRateNormalEvent(
                id = UUID.randomUUID().toString(),
                timeStart = context.frame.frameTime ?: Instant.now().toEpochMilli(),
                timeFinish = context.frame.frameTime ?: Instant.now().toEpochMilli(),
                metalRate = metalRate
            ))
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
