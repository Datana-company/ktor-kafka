package ru.datana.smart.ui.converter.angle.app.cor.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import org.apache.kafka.clients.producer.ProducerRecord
import ru.datana.smart.ui.converter.angle.app.cor.context.ConverterAngleContext
import ru.datana.smart.ui.converter.angle.app.cor.context.CorError
import ru.datana.smart.ui.converter.angle.app.cor.context.CorStatus
import ru.datana.smart.ui.converter.angle.app.cor.exceptions.EmptyMessageList
import ru.datana.smart.ui.mlui.models.ConverterTransportAngle
import java.time.Instant
import java.util.*
import kotlin.concurrent.schedule

object SendingHandler : IKonveyorHandler<ConverterAngleContext<String, String>> {

    override suspend fun exec(context: ConverterAngleContext<String, String>, env: IKonveyorEnvironment) {
        try {
            val messages = context.angleSchedule?.items?.run {
                filter { it.angle != null && it.timeShift != null }
                sortBy { it.timeShift }
                toList()
            } ?: throw EmptyMessageList("No messages in schedule to send")

            for (message in messages) {
                val converterTransportAngle = ConverterTransportAngle(
                    meltInfo = context.metaInfo,
                    angle = message.angle
                )
                val json = context.jacksonSerializer.writeValueAsString(converterTransportAngle)
                val startDate = Date(context.metaInfo!!.timeStart!! + message.timeShift!!)
                val record = ProducerRecord(context.topicAngles, "key?", json)
                Timer().schedule(startDate) {
                    context.kafkaProducer.send(record)
                    context.logger.trace(
                        "Sent message to {}. Message: {}",
                        objs = *arrayOf(
                            context.topicAngles,
                            record
                        )
                    )
                }
            }
        } catch (e: Throwable) {
            val msg = e.message ?: ""
            context.logger.error(msg)
            context.errors.add(CorError(msg))
            context.status = CorStatus.FAILING
        }
    }

    override fun match(context: ConverterAngleContext<String, String>, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
