package ru.datana.smart.ui.converter.angle.app.cor.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import org.apache.kafka.clients.producer.ProducerRecord
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.converter.angle.app.cor.context.ConverterAngleContext
import ru.datana.smart.ui.converter.angle.app.cor.context.CorError
import ru.datana.smart.ui.converter.angle.app.cor.context.CorStatus
import ru.datana.smart.ui.converter.angle.app.cor.exceptions.EmptyMessageList
import ru.datana.smart.ui.mlui.models.ConverterTransportAngle
import java.time.Instant
import java.util.*
import kotlin.concurrent.schedule

object SendingHandler : IKonveyorHandler<ConverterAngleContext<String, String>> {

    private val logger = datanaLogger(SendingHandler::class.java)

    override suspend fun exec(context: ConverterAngleContext<String, String>, env: IKonveyorEnvironment) {
        try {
            val messages = context.angleSchedule?.items?.run {
                filter { it.angle != null && it.timeShift != null }
                // для отправки по порядку
                sortBy { it.timeShift }
                toList()
            } ?: throw EmptyMessageList("No messages in schedule to send")

            for (message in messages) {
                val metaTimeStart = context.metaInfo!!.timeStart!!
                val angleTimeShift = message.timeShift!!
                val startTimeLong = metaTimeStart + angleTimeShift
                val converterTransportAngle = ConverterTransportAngle(
                    meltInfo = context.metaInfo,
                    angleTime = startTimeLong,
                    angle = message.angle
                )
                val key = "$metaTimeStart-angle-$angleTimeShift"
                val json = context.jacksonSerializer.writeValueAsString(converterTransportAngle)
                val record = ProducerRecord(context.topicAngles, key, json)

                Timer().schedule(Date(startTimeLong)) {
                    context.kafkaProducer.send(record)
                    logger.trace(
                        "Sent message to {}. Record: {}",
                        objs = arrayOf(
                            context.topicAngles,
                            record
                        )
                    )
                }
            }
        } catch (e: Throwable) {
            val msg = e.message ?: ""
            logger.error(msg)
            context.errors.add(CorError(msg))
            context.status = CorStatus.FAILING
        }
    }

    override fun match(context: ConverterAngleContext<String, String>, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
