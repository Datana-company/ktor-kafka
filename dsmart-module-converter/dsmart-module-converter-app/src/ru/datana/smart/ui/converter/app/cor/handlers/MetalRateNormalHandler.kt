package ru.datana.smart.ui.converter.app.cor.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.app.cor.context.ConverterBeContext
import ru.datana.smart.ui.converter.app.cor.context.CorError
import ru.datana.smart.ui.converter.app.cor.context.CorStatus
import ru.datana.smart.ui.converter.app.cor.repository.ConveyorMetalRateCriticalEvent
import ru.datana.smart.ui.converter.app.cor.repository.ConveyorMetalRateExceedsEvent
import ru.datana.smart.ui.converter.app.cor.repository.ConveyorMetalRateNormalEvent
import ru.datana.smart.ui.mlui.models.ConverterTransportMlUi

object MetalRateNormalHandler: IKonveyorHandler<ConverterBeContext<String, String>> {

    override suspend fun exec(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment) {
        val record = context.records.firstOrNull { it.topic == context.topicConverter } ?: return

        context.logger.trace("topic = ${record.topic}, partition = ${record.partition}, offset = ${record.offset}, key = ${record.key}, value = ${record.value}")

        try {
            val obj = context.jacksonSerializer.readValue(record.value, ConverterTransportMlUi::class.java)!!

            val steelRate = obj.steelRate ?: return
            if (steelRate > 0.05) {
                return
            }

            context.eventsRepository.getActive().map {
                when(it) {
                    is ConveyorMetalRateCriticalEvent -> {
                        val event = ConveyorMetalRateCriticalEvent(
                            id = it.id,
                            timeStart = it.timeStart,
                            timeFinish = it.timeFinish,
                            steelRate = it.steelRate,
                            title = it.title,
                            isActive = false
                        )
                        context.eventsRepository.put(event)
                    }
                    is ConveyorMetalRateExceedsEvent -> {
                        val event = ConveyorMetalRateExceedsEvent(
                            id = it.id,
                            timeStart = it.timeStart,
                            timeFinish = it.timeFinish,
                            steelRate = it.steelRate,
                            title = it.title,
                            isActive = false
                        )
                        context.eventsRepository.put(event)
                    }
                    is ConveyorMetalRateNormalEvent -> {
                        val currentTime = obj.frameTime ?: it.timeFinish
                        val event = if (currentTime - it.timeFinish > 20000) {
                            context.eventsRepository.put(
                                ConveyorMetalRateNormalEvent(
                                    id = it.id,
                                    timeStart = it.timeStart,
                                    timeFinish = it.timeFinish,
                                    steelRate = it.steelRate,
                                    title = it.title,
                                    isActive = false
                                )
                            )
                            ConveyorMetalRateNormalEvent(
                                timeStart = currentTime,
                                timeFinish = currentTime,
                                steelRate = steelRate
                            )
                        } else {
                            ConveyorMetalRateNormalEvent(
                                id = it.id,
                                timeStart = if (it.timeStart > currentTime) currentTime else it.timeStart,
                                timeFinish = if (it.timeFinish < currentTime) currentTime else it.timeFinish,
                                steelRate = it.steelRate,
                                title = it.title,
                                isActive = true
                            )
                        }
                        context.eventsRepository.put(event)
                    }
                }
            }
        } catch (e: Throwable) {
            val msg = "Error parsing data for [Proc]: ${record}"
            context.logger.error(msg)
            context.errors.add(CorError(msg))
            context.status = CorStatus.FAILING
        }
    }

    override fun match(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
