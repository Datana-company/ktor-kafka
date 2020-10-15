package ru.datana.smart.ui.converter.app.cor.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.app.cor.context.ConverterBeContext
import ru.datana.smart.ui.ml.models.TemperatureProcUiDto
import ru.datana.smart.ui.converter.app.cor.context.CorError
import ru.datana.smart.ui.converter.app.cor.context.CorStatus
import ru.datana.smart.ui.converter.ws.models.WsDsmartResponseConverter
import ru.datana.smart.ui.converter.app.mappings.toWsConverterModel
import kotlin.math.max

object RawTopicHandler : IKonveyorHandler<ConverterBeContext<String, String>> {

    override suspend fun exec(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment) {
        val record = context.records.firstOrNull { it.topic == context.topicRaw } ?: return

        context.logger.trace("topic = ${record.topic}, partition = ${record.partition}, offset = ${record.offset}, key = ${record.key}, value = ${record.value}")

        try {
            val obj = context.jacksonSerializer.readValue(record.value, TemperatureProcUiDto::class.java)!!
            if (obj.sensorId != context.sensorId) {
                val msg = "Sensor Id ${obj.sensorId} is not proper in respect to ${context.sensorId}"
                context.logger.trace(msg)
                context.errors.add(CorError(msg))
                context.status = CorStatus.FAILING
                return
            }

            val objTime = obj.timeIntervalLatest ?: return
            val newTime = context.lastTimeProc.updateAndGet {
                max(objTime, it)
            }

            // Пропускаем устаревшие данные
            if (newTime != objTime) return

            val response = WsDsmartResponseConverter(
                data = toWsConverterModel(obj)
            )
            response.data?.tiltAngle?.isFinite()?.apply { context.forwardObjects.add(response) }

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
