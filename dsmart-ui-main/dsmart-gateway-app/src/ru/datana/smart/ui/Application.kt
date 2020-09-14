package ru.datana.smart.ui

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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.WakeupException
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.event.Level
import ru.datana.smart.ui.temperature.kf.models.KfDsmartTemperatureData
import ru.datana.smart.ui.temperature.ws.models.WsDsmartResponseTemperature
import ru.datana.smart.ui.temperature.ws.models.WsDsmartTemperatures
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.ConcurrentHashMap

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("unused") // Referenced in application.conf
fun Application.module(testing: Boolean = false) {
    val wsSessions = ConcurrentHashMap.newKeySet<DefaultWebSocketSession>()

    suspend fun sendToAll(data: WsDsmartResponseTemperature) {
        log.trace("sending temperature: $data")
        val wsSessionsIterator = wsSessions.iterator()
        while(wsSessionsIterator.hasNext()) {
            wsSessionsIterator.next().apply {
                try {
                    val jsonString = Json.encodeToString(data)
                    log.trace("Sending to client ${hashCode()}: $jsonString")
                    send(jsonString)
                } catch (e: Throwable) {
                    log.info("Session ${hashCode()} is removed due to exception", e)
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

    install(io.ktor.websocket.WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        static("/") {
            defaultResource("static/index.html")
            resources("static")
        }

        webSocket("/ws") {
            println("onConnect")
            wsSessions += this
            try {
//                incoming.consume { }
                for (frame in incoming) {
//                    if (frame is Frame.Text) {
//                        val message = frame.readText()
//                        log.info("A message is received: $message")
//                        send(Frame.Text("{\"event\": \"update-texts\", \"data\": \"Server received a message\"}"))
//                    }
                }
            } catch (e: ClosedReceiveChannelException) {
                println("onClose ${closeReason.await()}")
            } catch (e: Throwable) {
                log.error("Error within websocket block due to: ${closeReason.await()}", e)
            } finally {
                wsSessions -= this
            }
        }
    }

//    val closed = AtomicBoolean(false)
//    val consumer = buildConsumer(this@module.environment)
    val consumer by lazy { buildConsumer(this@module.environment) }
    launch {
//        while (!closed.get()) {
        while (true) {
            try {
                val records = consumer.poll(Duration.of(1000, ChronoUnit.MILLIS))

                records
                    .firstOrNull()
                    ?.let { record ->
                        log.trace("topic = ${record.topic()}, partition = ${record.partition()}, offset = ${record.offset()}, key = ${record.key()}, value = ${record.value()}")
                        parseKafkaInput(record.value())
                    }
                    ?.takeIf {
                        it.data?.temperature?.isFinite() ?: false
                    }
                    ?.also { temp -> sendToAll(temp) }

                if (!records.isEmpty) {
                    consumer.commitAsync { offsets, exception ->
                        if (exception != null) {
                            log.error("Commit failed for offsets $offsets", exception)
                        } else {
                            log.trace("Offset committed  $offsets")
                        }
                    }
                }
                log.debug("Finish consuming")
            } catch (e: WakeupException) {
                log.info("Consumer waked up")
            } catch (e: Throwable) {
                    log.error("Polling failed", e)
            }
        }
        log.info("Commit offset synchronously")
        consumer.commitSync()
        consumer.close()
        log.info("Consumer successfully closed")
    }
}

private fun Application.parseKafkaInput(value: String?): WsDsmartResponseTemperature? {
    // {"info":{"id":"4a67082b-8bf4-48b7-88c0-0d542ffe2214","channelList":["clean"]},"content":[{"@class":"ru.datana.common.model.SingleSensorModel","request_id":"d076f100-3e96-4857-aba8-1c7f4c866dd5","request_datetime":1598962179670,"response_datetime":1598962179754,"sensor_id":"00000000-0000-4000-9000-000000000006","data":-247.14999999999998,"status":0,"errors":[]}]}
    if (value == null) return null
    return try {
        val obj = Json.decodeFromString(KfDsmartTemperatureData.serializer(), value)
        WsDsmartResponseTemperature(
            data = WsDsmartTemperatures(
                temperature = obj.temperature?.let { it + 273.15 },
                timeMillis = obj.timeMillis,
                durationMillis = obj.durationMillis,
                deviationPositive = obj.deviationPositive,
                deviationNegative = obj.deviationNegative
            )
        )
    } catch (e: Throwable) {
        log.error("Error parsing data for", value)
        null
    }
}

@OptIn(KtorExperimentalAPI::class)
fun buildConsumer(environment: ApplicationEnvironment): KafkaConsumer<String, String> {
    val consumerConfig = environment.config.config("ktor.kafka.consumer")
    val kafkaConfig = environment.config.config("ktor.kafka")
    val consumerProps = Properties().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.property("bootstrap.servers").getList())
        put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString())

        put(ConsumerConfig.GROUP_ID_CONFIG, consumerConfig.property("group.id").getString())
//        this[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = consumerConfig.property("key.deserializer").getString()
//        this[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = consumerConfig.property("value.deserializer").getString()
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
    }
    return KafkaConsumer<String, String>(consumerProps)
        .apply {
            subscribe(listOf(consumerConfig.property("topic").getString()))
        }
}
