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
import io.ktor.locations.*
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.producer.ProducerRecord
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.meta.models.ConverterDevicesIrCamerta
import ru.datana.smart.ui.meta.models.ConverterMeltDevices
import ru.datana.smart.ui.meta.models.ConverterMeltInfo
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import java.util.*
import kotlin.streams.toList

/**
 * Location for uploading videos.
 */
@Location("/upload")
class Upload()

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
    val pathToUpload: String by lazy {
        environment.config.property("ktor.upload.path").getString().trim()
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

    // Allows to use classes annotated with @Location to represent URLs.
    // They are typed, can be constructed to generate URLs, and can be used to register routes.
    install(Locations)

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

    val uploadDir = File(pathToUpload)
    if (!uploadDir.mkdirs() && !uploadDir.exists()) {
        throw IOException("Failed to create directory ${uploadDir.absolutePath}")
    }

    routing {
        static("/") {
            defaultResource("static/index.html")
            resources("static")
        }

        get("/list") {
            logger.info(" +++ GET /list")
            var absolutePathToCatalog: Path?
            try {
                absolutePathToCatalog = Paths.get(pathToCatalog).toAbsolutePath()
            } catch (e: Throwable) {
                logger.error(e.localizedMessage)
                call.respond(HttpStatusCode.InternalServerError)
                return@get
            }
            if (!Files.exists(absolutePathToCatalog)) {
                val errorMsg = "По пути " + absolutePathToCatalog + " каталог не найден"
                logger.error(errorMsg)
                call.respond(HttpStatusCode.InternalServerError, errorMsg)
                return@get
            }
            if (!Files.isDirectory(absolutePathToCatalog)) {
                val errorMsg = "По пути " + absolutePathToCatalog + " находится файл, а должен быть каталог"
                logger.error(errorMsg)
                call.respond(HttpStatusCode.InternalServerError, errorMsg)
                return@get
            }
            val converterCaseListModel = ConverterCaseListModel(cases = Files.walk(
                absolutePathToCatalog,
                1
            ) // Смотрим только 1 уровень (т.е. не заходим в каталоги)
                .filter { item -> Files.isDirectory(item) && item.fileName.toString().startsWith("case-") } // Оставляем только каталоги начинающиеся с "case-"
                .map {
                    ConverterCaseModel(
                        name = it.fileName.toString(),
                        dir = it.toString()
                    )
                }
                .toList()
            )
            val converterCaseListJsonString = objectMapper.writeValueAsString(converterCaseListModel)
            call.respondText(converterCaseListJsonString.trimIndent())
        }

        get("/send") {
            logger.info(" +++ GET /send")
            logger.debug("parameters count: " + call.parameters.names().size)
            val timeStart = Instant.now().toEpochMilli()
            logger.info(
                msg = "Send event is caught by converter-mock backend",
                data = object {
                    val id = "datana-smart-converter-mock-send-caught"
                    val timeStart = timeStart
                }
            )
            val case = call.parameters["case"] ?: throw BadRequestException("No case is specified")
            logger.debug("case: " + case)
            logger.debug("kafkaServers: " + kafkaServers + " --- kafkaTopic: " + kafkaTopic + " --- pathToCatalog: " + pathToCatalog)
            val meltInfo = try {
                val metaText = File("$pathToCatalog/$case/meta.json").readText()
                logger.debug("metaText" + metaText)
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

        upload(uploadDir)
    }
}
