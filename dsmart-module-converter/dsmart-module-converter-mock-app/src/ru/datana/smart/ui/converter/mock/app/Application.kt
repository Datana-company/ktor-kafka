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
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import java.util.*
import java.util.stream.Collectors

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
            println(" +++ GET /list")
            var absolutePathToCatalog: Path?
            try {
                absolutePathToCatalog = Paths.get(pathToCatalog).toAbsolutePath()
            }
            catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError)
                throw Exception(e)
            }
            if (!Files.exists(absolutePathToCatalog)) {
                call.respond(HttpStatusCode.InternalServerError)
                throw Exception("По пути " + absolutePathToCatalog + " каталог не найден")
            }
            if (!Files.isDirectory(absolutePathToCatalog)) {
                call.respond(HttpStatusCode.InternalServerError)
                throw Exception("По пути " + absolutePathToCatalog + " находится файл, а должен быть каталог")
            }
            val converterCaseListModel = ConverterCaseListModel(cases = Files.walk(absolutePathToCatalog,1) // Смотрим только 1 уровень (т.е. не заходим в каталоги)
                .filter { item -> Files.isDirectory(item) && item.fileName.toString().startsWith("case-") } // Оставляем только каталоги начинающиеся с "case-"
                .map {
                    ConverterCaseModel(
                        name = it.fileName.toString(),
                        dir = it.toString())
                }
                .collect(Collectors.toList<ConverterCaseModel>())
            )
//                .forEach { item -> run{
//                    println("dir: $item --- name: ${item.fileName}")
//                } }
            val converterCaseListJsonString = objectMapper.writeValueAsString(converterCaseListModel)
            call.respondText(converterCaseListJsonString.trimIndent())
//                """
//                {
//                    "cases": [
//                        {"name": "Case1", "dir": "Case1"},
//                        {"name": "Case2", "dir": "Case2"},
//                        {"name": "Case3", "dir": "Case3"},
//                        {"name": "Case4", "dir": "Case4"}
//                    ]
//                }
//                """.trimIndent()
//            )
        }

        get("/send") {
            println(" +++ GET /send")
            println("parameters count: " + call.parameters.names().size)
            val timeStart = Instant.now().toEpochMilli()
            logger.info(
                msg = "Send event is caught by converter-mock backend",
                data = object {
                    val id = "datana-smart-converter-mock-send-caught"
                    val timeStart = timeStart
                }
            )
            val case = call.parameters["case"] ?: throw BadRequestException("No case is specified")
            println("case: " + case)
            println("kafkaServers: " + kafkaServers + " --- kafkaTopic: " + kafkaTopic + " --- pathToCatalog: " + pathToCatalog)
            val meltInfo = try {
                val metaText = File("$pathToCatalog/$case/meta.json").readText()
                println("metaText" + metaText)
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
            val meltId = "${meltInfo.meltNumber}-$timeStart"
            val meltInfoInit = meltInfo.copy(
                id = meltId,
                timeStart = timeStart
            )
            val readyJsonString = objectMapper.writeValueAsString(meltInfoInit)
            try {
                kafkaProducer.send(ProducerRecord(kafkaTopic, meltId, readyJsonString))
                logger.biz(
                    msg = "Send event is caught by converter-mock backend and successfully handled",
                    data = object {
                        val id = "datana-smart-converter-mock-send-done"
                        val meltInfo = meltInfoInit
                    }
                )
                call.respond(HttpStatusCode.OK)
            } catch (e: Throwable) {
                logger.error("Send event failed due to kafka producer {}", objs = arrayOf(e))
                call.respond(HttpStatusCode.InternalServerError)
            }

        }
    }
}
