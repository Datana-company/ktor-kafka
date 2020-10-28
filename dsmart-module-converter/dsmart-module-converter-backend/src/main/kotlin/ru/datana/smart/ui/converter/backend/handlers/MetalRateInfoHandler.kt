package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorError
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.*
import java.time.Instant
import java.util.*

object MetalRateInfoHandler: IKonveyorHandler<ConverterBeContext> {

    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {

//        val record = context.records.firstOrNull { it.topic == context.topicConverter } ?: return
//
//        context.logger.trace("topic = {}, partition = {}, offset = {}, key = {}, value = {}",
//            objs = arrayOf(
//                record.topic,
//                record.partition,
//                record.offset,
//                record.key,
//                record.value
//            ))

//        val steelRate = context.metalRateEventGenerator.generateValue
//        val currentTime = Instant.now().toEpochMilli()
//        val record = "{\"frameId\": \"1\", \"frameTime\": $currentTime, \"framePath\": \"/frame/to/path\", \"angle\": 79.123, \"steelRate\": $steelRate, \"slagRate\": 0.05, \"meltInfo\": {\"id\": \"1\", \"timeStart\": 1602796302129, \"meltNumber\": \"12\", \"steelGrade\": \"ММК\", \"crewNumber\": \"3\", \"shiftNumber\": \"2\", \"mode\": 1, \"devices\": {\"irCamera\": {\"id\": \"c17ea7ca-7bbf-4f89-a644-7899f21ac629\", \"name\": \"GoPro\", \"uri\": \"video/path\", \"type\": 1}}}}"
//
//        try {
//            val obj = context.jacksonSerializer.readValue(record/*.value*/, ConverterTransportMlUi::class.java)!!
//
//            val metalRate = obj.steelRate ?: return
//            val normalPoint = context.metalRateNormalPoint
//            if (metalRate >= normalPoint) {
//                return
//            }
//
//            val frameTime = obj.frameTime ?: Instant.now().toEpochMilli()
//            val activeEvent: IConveyorMetalRateEvent? = context.eventsRepository.getActive().find { it is IConveyorMetalRateEvent } as? IConveyorMetalRateEvent
//
//            activeEvent?.let {
//                when(it) {
//                    is ConveyorMetalRateInfoEvent -> {
//                        val updateEvent = ConveyorMetalRateInfoEvent(
//                            id = it.id,
//                            timeStart = if (it.timeStart > frameTime) frameTime else it.timeStart,
//                            timeFinish = if (it.timeFinish < frameTime) frameTime else it.timeFinish,
//                            metalRate = it.metalRate,
//                            title = it.title,
//                            isActive = it.isActive
//                        )
//                        context.eventsRepository.put(updateEvent)
//                    }
//                    is ConveyorMetalRateCriticalEvent -> {
//                        val historicalEvent = ConveyorMetalRateCriticalEvent(
//                            id = it.id,
//                            timeStart = it.timeStart,
//                            timeFinish = it.timeFinish,
//                            metalRate = it.metalRate,
//                            title = it.title,
//                            isActive = false
//                        )
//                        context.eventsRepository.put(historicalEvent)
//                        val newEvent = ConveyorMetalRateInfoEvent(
//                            id = UUID.randomUUID().toString(),
//                            timeStart = frameTime,
//                            timeFinish = frameTime,
//                            metalRate = metalRate
//                        )
//                        context.eventsRepository.put(newEvent)
//                    }
//                    is ConveyorMetalRateExceedsEvent -> {
//                        val historicalEvent = ConveyorMetalRateExceedsEvent(
//                            id = it.id,
//                            timeStart = it.timeStart,
//                            timeFinish = it.timeFinish,
//                            metalRate = it.metalRate,
//                            title = it.title,
//                            isActive = false
//                        )
//                        context.eventsRepository.put(historicalEvent)
//                        val newEvent = ConveyorMetalRateInfoEvent(
//                            id = UUID.randomUUID().toString(),
//                            timeStart = frameTime,
//                            timeFinish = frameTime,
//                            metalRate = metalRate
//                        )
//                        context.eventsRepository.put(newEvent)
//                    }
//                    is ConveyorMetalRateNormalEvent -> {
//                        val historicalEvent = ConveyorMetalRateNormalEvent(
//                            id = it.id,
//                            timeStart = it.timeStart,
//                            timeFinish = it.timeFinish,
//                            metalRate = it.metalRate,
//                            title = it.title,
//                            isActive = false
//                        )
//                        context.eventsRepository.put(historicalEvent)
//                        val newEvent = ConveyorMetalRateInfoEvent(
//                            id = UUID.randomUUID().toString(),
//                            timeStart = frameTime,
//                            timeFinish = frameTime,
//                            metalRate = metalRate
//                        )
//                        context.eventsRepository.put(newEvent)
//                    }
//                }
//            } ?: context.eventsRepository.put(
//                ConveyorMetalRateInfoEvent(
//                    id = UUID.randomUUID().toString(),
//                    timeStart = obj.frameTime ?: Instant.now().toEpochMilli(),
//                    timeFinish = obj.frameTime ?: Instant.now().toEpochMilli(),
//                    metalRate = metalRate
//                )
//            )
//        } catch (e: Throwable) {
//            val msg = "Error parsing data for [Proc]: ${record/*.value*/}"
//            context.logger.error(msg)
//            context.errors.add(CorError(msg))
//            context.status = CorStatus.FAILING
//        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
