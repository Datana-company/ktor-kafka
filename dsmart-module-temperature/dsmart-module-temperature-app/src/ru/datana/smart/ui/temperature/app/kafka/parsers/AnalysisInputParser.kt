package ru.datana.smart.ui.temperature.app.kafka.parsers

import ch.qos.logback.classic.Logger
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.application.Application
import io.ktor.application.log
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.ml.models.TemperatureMlUiDto
import ru.datana.smart.ui.temperature.ws.models.WsDsmartResponseAnalysis
import ru.datana.smart.ui.temperature.ws.models.WsDsmartAnalysis
import ru.datana.smart.ui.temperature.ws.models.TeapotState
import java.util.concurrent.atomic.AtomicLong
import java.time.Instant
import kotlin.math.max

private val jacksonMapper = ObjectMapper()
private val lastTimeMl = AtomicLong(0)

fun Application.parseKafkaInputAnalysis(value: String?, sensorId: String): WsDsmartResponseAnalysis? {
    val log = datanaLogger(this.log as Logger)

    return value?.let { json ->
        try {
            val obj = jacksonMapper.readValue(json, TemperatureMlUiDto::class.java)!!
            if (obj.version != "0.2") {
                log.error("Wrong TemperatureUI (input ML-data) version ")
                return null
            }
            if (obj.sensorId?.trim() != sensorId) {
                log.trace("Sensor Id {} is not proper in respect to {}", objs = arrayOf(obj.sensorId, sensorId))
                return null
            }

            log.trace("Checking time {}", obj.timeActual)
            val objTime = obj.timeActual ?: return null
            val newTime = lastTimeMl.updateAndGet {
                max(objTime, it)
            }

            log.trace("Test for actuality: {} === {}", objs = arrayOf(objTime, newTime))
            // Пропускаем устаревшие данные
            if (newTime != objTime) return null

            WsDsmartResponseAnalysis(
                data = WsDsmartAnalysis(
                    timeBackend = Instant.now().toEpochMilli(),
                    timeActual = obj.timeActual,
                    durationToBoil = obj.durationToBoil,
                    sensorId = obj.sensorId,
                    temperatureLast = obj.temperatureLast,
                    state = obj.state?.toWs()
                )
            )
        } catch (e: Throwable) {
            log.error("Error parsing data for [ML]: {}", value)
            null
        }
    }
}




