package ru.datana.smart.ui.converter.app.cor.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.app.cor.context.ConverterBeContext
import ru.datana.smart.ui.converter.app.cor.context.CorError
import ru.datana.smart.ui.converter.app.cor.context.CorStatus
import ru.datana.smart.ui.converter.app.cor.repository.events.*
import ru.datana.smart.ui.mlui.models.ConverterTransportMlUi
import java.time.Instant

object MetalRateInfoHandler: IKonveyorHandler<ConverterBeContext<String, String>> {

    override suspend fun exec(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment) {
        val record = context.records.firstOrNull { it.topic == context.topicConverter } ?: return

        context.logger.trace("topic = ${record.topic}, partition = ${record.partition}, offset = ${record.offset}, key = ${record.key}, value = ${record.value}")

        try {
            val obj = context.jacksonSerializer.readValue(record.value, ConverterTransportMlUi::class.java)!!

            val metalRate = obj.steelRate ?: return
            if (metalRate >= 0.05) {
                return
            }

            val frameTime = obj.frameTime ?: Instant.now().toEpochMilli()
            val activeEvent: IConveyorMetalRateEvent? = context.eventsRepository.getActive().find { it is IConveyorMetalRateEvent } as? IConveyorMetalRateEvent

            activeEvent?.let {
                when(it) {
                    is ConveyorMetalRateInfoEvent -> {
                        val updateEvent = ConveyorMetalRateInfoEvent(
                            id = it.id,
                            timeStart = if (it.timeStart > frameTime) frameTime else it.timeStart,
                            timeFinish = if (it.timeFinish < frameTime) frameTime else it.timeFinish,
                            metalRate = it.metalRate,
                            title = it.title,
                            isActive = it.isActive
                        )
                        context.eventsRepository.put(updateEvent)
                    }
                    is ConveyorMetalRateCriticalEvent -> {
                        val historicalEvent = ConveyorMetalRateCriticalEvent(
                            id = it.id,
                            timeStart = it.timeStart,
                            timeFinish = it.timeFinish,
                            metalRate = it.metalRate,
                            title = it.title,
                            isActive = false
                        )
                        context.eventsRepository.put(historicalEvent)
                        val newEvent = ConveyorMetalRateInfoEvent(
                            timeStart = frameTime,
                            timeFinish = frameTime,
                            metalRate = metalRate
                        )
                        context.eventsRepository.put(newEvent)
                    }
                    is ConveyorMetalRateExceedsEvent -> {
                        val historicalEvent = ConveyorMetalRateExceedsEvent(
                            id = it.id,
                            timeStart = it.timeStart,
                            timeFinish = it.timeFinish,
                            metalRate = it.metalRate,
                            title = it.title,
                            isActive = false
                        )
                        context.eventsRepository.put(historicalEvent)
                        val newEvent = ConveyorMetalRateInfoEvent(
                            timeStart = frameTime,
                            timeFinish = frameTime,
                            metalRate = metalRate
                        )
                        context.eventsRepository.put(newEvent)
                    }
                    is ConveyorMetalRateNormalEvent -> {
                        val historicalEvent = ConveyorMetalRateNormalEvent(
                            id = it.id,
                            timeStart = it.timeStart,
                            timeFinish = it.timeFinish,
                            metalRate = it.metalRate,
                            title = it.title,
                            isActive = false
                        )
                        context.eventsRepository.put(historicalEvent)
                        val newEvent = ConveyorMetalRateInfoEvent(
                            timeStart = frameTime,
                            timeFinish = frameTime,
                            metalRate = metalRate
                        )
                        context.eventsRepository.put(newEvent)
                    }
                }
            } ?: context.eventsRepository.put(
                ConveyorMetalRateInfoEvent(
                    timeStart = obj.frameTime ?: Instant.now().toEpochMilli(),
                    timeFinish = obj.frameTime ?: Instant.now().toEpochMilli(),
                    metalRate = metalRate
                )
            )
        } catch (e: Throwable) {
            val msg = "Error parsing data for [Proc]: ${record.value}"
            context.logger.error(msg)
            context.errors.add(CorError(msg))
            context.status = CorStatus.FAILING
        }
    }

    override fun match(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
