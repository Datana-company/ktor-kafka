package ru.datana.smart.ui.converter.mock.app

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.producer.ProducerRecord
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.meta.models.ConverterDevicesIrCamerta
import ru.datana.smart.ui.meta.models.ConverterMeltDevices
import ru.datana.smart.ui.meta.models.ConverterMeltInfo
import java.io.File
import java.time.Instant
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@OptIn(KtorExperimentalAPI::class)
@Suppress("unused") // Referenced in application.conf
fun Application.module(testing: Boolean = false) {
    val logger = datanaLogger(this.log as ch.qos.logback.classic.Logger)
    val objectMapper = jacksonObjectMapper()
    val pathToCatalog: String by lazy {
        environment.config.property("ktor.catalog.path").getString().trim()
    }
    val kafkaServers: String by lazy {
        environment.config.property("ktor.kafka.bootstrap.servers").getString().trim()
    }
    val kafkaTopic: String by lazy {
        environment.config.property("ktor.kafka.producer.topic.meta").getString().trim()
    }
    val kafkaProducer: KafkaProducer<String, String> by lazy {
        val props = Properties().apply {
            put(BOOTSTRAP_SERVERS_CONFIG, kafkaServers)
            put("acks", "all")
            put("retries", 3)
            put("batch.size", 16384);
            put("linger.ms", 1);
            put("buffer.memory", 33554432);
            put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        }
        KafkaProducer<String, String>(props)
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

    routing {
        static("/") {
            defaultResource("static/index.html")
            resources("static")
        }

        get("/list") {
            call.respondText(
                """
                {
                    "cases": [
                        {"name": "Case1", "dir": "Case1"},
                        {"name": "Case2", "dir": "Case2"},
                        {"name": "Case3", "dir": "Case3"},
                        {"name": "Case4", "dir": "Case4"}
                    ]
                }
                """.trimIndent()
            )
        }

        get("/send") {
            val case = call.parameters["case"] ?: throw BadRequestException("No case is specified")
            val meltInfo = try {
                val metaText = File("$pathToCatalog/$case/meta.json").readText()
                objectMapper.readValue<ConverterMeltInfo>(metaText)
            } catch (e: Throwable) {
                ConverterMeltInfo(
                    meltNumber = "unknown",
                    steelGrade = "unknown",
                    crewNumber = "0",
                    shiftNumber = "-1",
                    mode = ConverterMeltInfo.Mode.EMULATION,
                    devices = ConverterMeltDevices(
                        irCamera = ConverterDevicesIrCamerta(
                            id = "unknown-camera",
                            name = "Неизвестная камера",
                            type = ConverterDevicesIrCamerta.Type.FILE,
                            uri = "file:///some/where/in/ceph/file.ravi"
                        )
                    )
                )
            }
            val timeStart = Instant.now().toEpochMilli()
            val meltId = "${meltInfo.meltNumber}-$timeStart"
            val meltInfoInit = meltInfo.copy(
                id = meltId,
                timeStart = timeStart
            )
            val readyJsonString = objectMapper.writeValueAsString(meltInfoInit)
            kafkaProducer.send(ProducerRecord(meltId, readyJsonString))
            call.respond(HttpStatusCode.OK)

        }
    }

}
