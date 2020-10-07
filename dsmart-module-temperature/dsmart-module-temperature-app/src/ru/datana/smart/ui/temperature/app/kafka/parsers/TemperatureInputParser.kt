package ru.datana.smart.ui.temperature.app.kafka.parsers

import ch.qos.logback.classic.Logger
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.application.Application
import io.ktor.application.log
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.ml.models.TemperatureProcUiDto
import ru.datana.smart.ui.temperature.ws.models.WsDsmartResponseTemperature
import ru.datana.smart.ui.temperature.ws.models.WsDsmartTemperatures
import java.util.concurrent.atomic.AtomicLong
import java.time.Instant
import kotlin.math.max

private val jacksonMapper = ObjectMapper()
private val lastTimeProc = AtomicLong(0)

fun Application.parseKafkaInputTemperature(jsonString: String?, sensorId: String): WsDsmartResponseTemperature? {
    private val log = datanaLogger(this.log as Logger)

    if (jsonString == null) return null
    return try {
        val obj = jacksonMapper.readValue(jsonString, TemperatureProcUiDto::class.java)!!
        if (obj.sensorId != sensorId) return null

        val objTime = obj.timeIntervalLatest ?: return null
        val newTime = lastTimeProc.updateAndGet {
            max(objTime, it)
        }

        // Пропускаем устаревшие данные
        if (newTime != objTime) return null

        WsDsmartResponseTemperature(
            data = WsDsmartTemperatures(
                timeBackend = Instant.now().toEpochMilli(),
                timeLatest = obj.timeIntervalLatest,
                timeEarliest = obj.timeIntervalEarliest,
                temperatureScale = obj.temperatureScale?.value,
                temperatureAverage = obj.temperatureAverage,
                tempertureMax = obj.temperatureMax,
                tempertureMin = obj.temperatureMin
            )
        )
    } catch (e: Throwable) {
        log.error("Error parsing data for [Proc]: {}", jsonString)
        null
    }
}
