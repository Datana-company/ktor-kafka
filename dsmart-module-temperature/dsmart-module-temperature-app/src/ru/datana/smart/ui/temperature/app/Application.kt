package ru.datana.smart.ui.temperature.app

import ch.qos.logback.classic.Logger
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.WakeupException
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.event.Level
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponse
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.ml.models.TemperatureMlUiDto
import ru.datana.smart.ui.ml.models.TemperatureProcUiDto
import ru.datana.smart.ui.temperature.ws.models.*
import ru.datana.smart.common.ktor.kafka.kafka
import ru.datana.smart.common.ktor.kafka.KtorKafkaConsumer
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.max

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
@KtorExperimentalAPI
fun Application.module(testing: Boolean = false) {

    val wsSessions = ConcurrentHashMap.newKeySet<DefaultWebSocketSession>()
    val log = datanaLog()
    val json = Json {
        encodeDefaults = true
    }

    suspend fun sendToAll(data: IWsDsmartResponse<*>) {
        log.trace("sending to client: $data")
        val wsSessionsIterator = wsSessions.iterator()
        while (wsSessionsIterator.hasNext()) {
            wsSessionsIterator.next().apply {
                try {
                    val jsonString = when(data) {
                        is WsDsmartResponseTemperature -> json.encodeToString(WsDsmartResponseTemperature.serializer(), data)
                        is WsDsmartResponseAnalysis -> json.encodeToString(WsDsmartResponseAnalysis.serializer(), data)
                        else -> throw RuntimeException("Unknown type of data")
                    }
                    log.trace("Sending to client ${hashCode()}: $jsonString")
                    send(jsonString)
                } catch (e: Throwable) {
                    log.error("Session ${hashCode()} is removed due to exception {}", e)
                    wsSessionsIterator.remove()
                }
            }
        }
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    install(KtorKafkaConsumer)

    val topicRaw by lazy { environment.config.property("ktor.kafka.consumer.topic.raw").getString()?.trim() }
    val topicAnalysis by lazy { environment.config.property("ktor.kafka.consumer.topic.analysis").getString()?.trim() }
    val sensorId by lazy { environment.config.property("ktor.datana.sensor.id").getString()?.trim() }

    routing {
        static("/") {
            defaultResource("static/index.html")
            resources("static")
        }

        webSocket("/ws") {
            println("onConnect")
            wsSessions += this
            try {
                for (frame in incoming) { }
            } catch (e: ClosedReceiveChannelException) {
                println("onClose ${closeReason.await()}")
            } catch (e: Throwable) {
                log.error("Error within websocket block due to: ${closeReason.await()}", e)
            } finally {
                wsSessions -= this
            }
        }

        kafka(listOf(topicRaw, topicAnalysis)) {
            records
                ?.firstOrNull { it.topic() == topicRaw }
                ?.let { record ->
                    log.trace("topic = ${record.topic()}, partition = ${record.partition()}, offset = ${record.offset()}, key = ${record.key()}, value = ${record.value()}")
                    parseKafkaInputTemperature(record.value(), sensorId)
                }
                ?.takeIf {
                    it.data?.temperatureAverage?.isFinite() ?: false
                }
                ?.also { temp -> sendToAll(temp) }

            records
                ?.firstOrNull { it.topic() == topicAnalysis }
                ?.let { record ->
                    log.trace("topic = ${record.topic()}, partition = ${record.partition()}, offset = ${record.offset()}, key = ${record.key()}, value = ${record.value()}")
                    parseKafkaInputAnalysis(record.value(), sensorId)
                }
                ?.takeIf {
                    it.data?.timeActual != null
                }
                ?.also { temp -> sendToAll(temp) }

            commitAll()
        }
    }
}

private fun Application.datanaLog() = datanaLogger(this.log as Logger)
private val jacksonMapper = ObjectMapper()

private fun Application.parseKafkaInputTemperature(jsonString: String?, sensorId: String): WsDsmartResponseTemperature? {
    // {"info":{"id":"4a67082b-8bf4-48b7-88c0-0d542ffe2214","channelList":["clean"]},"content":[{"@class":"ru.datana.common.model.SingleSensorModel","request_id":"d076f100-3e96-4857-aba8-1c7f4c866dd5","request_datetime":1598962179670,"response_datetime":1598962179754,"sensor_id":"00000000-0000-4000-9000-000000000006","data":-247.14999999999998,"status":0,"errors":[]}]}
    if (jsonString == null) return null
    return try {
//        val obj = Json.decodeFromString(KfDsmartTemperatureData.serializer(), value)
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
//                temperature = obj.temperature?.let { it + 273.15 },
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

private fun Application.parseKafkaInputAnalysis(value: String?, sensorId: String): WsDsmartResponseAnalysis? = value?.let { json ->
    val log = datanaLogger(this.log as Logger)
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

private fun TemperatureMlUiDto.State.toWs() = when(this) {
    TemperatureMlUiDto.State.SWITCHED_ON -> TeapotState(
        id = "switchedOn",
        name = "Включен",
        message = "Чайник включен"
    )
    TemperatureMlUiDto.State.SWITCHED_OFF -> TeapotState(
        id = "switchedOff",
        name = "Выключен",
        message = "Чайник выключен"
    )
    else -> TeapotState(
        id = "unknown",
        name = "Неизвестно",
        message = "Состояние чайника неизвестно"
    )
}

private val lastTimeProc = AtomicLong(0)
private val lastTimeMl = AtomicLong(0)

