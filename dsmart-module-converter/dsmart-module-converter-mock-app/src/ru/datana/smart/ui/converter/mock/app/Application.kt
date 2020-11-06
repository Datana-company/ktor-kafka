package ru.datana.smart.ui.converter.mock.app

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.jackson.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.converter.mock.app.models.ConverterCaseSaveRequest
import java.io.File
import java.io.IOException
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@OptIn(KtorExperimentalAPI::class)
@Suppress("unused") // Referenced in application.conf
fun Application.module(testing: Boolean = false) {

    val logger = datanaLogger(::main::class.java)

    val pathToCatalog: String by lazy { environment.config.property("ktor.catalog.path").getString().trim() }
    val kafkaServers: String by lazy { environment.config.property("ktor.kafka.bootstrap.servers").getString().trim() }
    val kafkaTopic: String by lazy { environment.config.property("ktor.kafka.producer.topic.meta").getString().trim() }
    val jacksonObjectMapper: ObjectMapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);

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

    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Post)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("Origin")
        header("X-Requested-With")
        header("Content-Type")
        header("Accept")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    val caseCatalogDir = File(pathToCatalog)
    if (!caseCatalogDir.mkdirs() && !caseCatalogDir.exists()) {
        throw IOException("Failed to create directory ${caseCatalogDir.absolutePath}")
    }

    val listService by lazy {
        ConverterMockListService(
            pathToCatalog = pathToCatalog
        )
    }
    val startService by lazy {
        ConverterMockStartService(
            pathToCatalog = pathToCatalog,
            kafkaProducer = kafkaProducer,
            kafkaTopic = kafkaTopic
        )
    }
    val createService by lazy {
        ConverterMockCreateService(
            pathToCatalog = pathToCatalog
        )
    }

    routing {
        static("/") {
            defaultResource("static/index.html")
            resources("static")
        }

        get("/list") {
            val context = ConverterMockContext()
            listService.exec(context)
            when (context.status) {
                ConverterMockContext.Statuses.OK -> call.respondText(
                    jacksonObjectMapper().writeValueAsString(context.responseData)
                )
                else -> call.respond(HttpStatusCode.InternalServerError)
            }
        }

        get("/get_case_data/{case_name}") {
            val caseName = call.parameters["case_name"]
            logger.info(" +++ GET /get_case_data/$caseName")
            val pathToMetaJson = "$pathToCatalog/$caseName/meta.json"

            val metaText = try {
                File(pathToMetaJson).readText(Charsets.UTF_8)
            } catch (e: Throwable) {
                ""
            }
            call.respondText(metaText, status = HttpStatusCode.OK)
        }

        get("/send") {
            val case = call.parameters["case"] ?: throw BadRequestException("No case is specified")
            val context = ConverterMockContext(
                startCase = case
            )
            startService.exec(context)
            when (context.status) {
                ConverterMockContext.Statuses.OK -> call.respond(HttpStatusCode.OK)
                else -> call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post("/add_case") {

            val context = try {
                log.info("Data received: {}", call.receiveText())
                val case = call.receive<ConverterCaseSaveRequest>()
//                val multipart = call.receiveMultipart()
//                val parts = multipart.readAllParts()
//                val case = ConverterCaseSaveRequest(
//                    caseId = (parts.find { it.name == "caseId" } as? PartData.FormItem)?.value,
//                    caseName = (parts.find { it.name == "caseName" } as? PartData.FormItem)?.value,
//                    meltNumber = (parts.find { it.name == "meltNumber" } as? PartData.FormItem)?.value,
//                    steelGrade = (parts.find { it.name == "steelGrade" } as? PartData.FormItem)?.value,
//                    crewNumber = (parts.find { it.name == "crewNumber" } as? PartData.FormItem)?.value,
//                    shiftNumber = (parts.find { it.name == "shiftNumber" } as? PartData.FormItem)?.value,
//                    converterId = (parts.find { it.name == "converterId" } as? PartData.FormItem)?.value,
//                    converterName = (parts.find { it.name == "converterName" } as? PartData.FormItem)?.value,
//                    irCameraId = (parts.find { it.name == "irCameraId" } as? PartData.FormItem)?.value,
//                    irCameraName = (parts.find { it.name == "irCameraName" } as? PartData.FormItem)?.value,
//                    fileVideo = parts.find { it.name == "fileVideo" } as? PartData.FileItem,
//                    slagRateDeviceId = (parts.find { it.name == "slagRateDeviceId" } as? PartData.FormItem)?.value,
//                    slagRateDeviceName = (parts.find { it.name == "slagRateDeviceName" } as? PartData.FormItem)?.value,
//                    slagRateJson = parts.find { it.name == "slagRateJson" } as? PartData.FileItem,
//                    selsynId = (parts.find { it.name == "selsynId" } as? PartData.FormItem)?.value,
//                    selsynName = (parts.find { it.name == "selsynName" } as? PartData.FormItem)?.value,
//                    selsynJson = parts.find { it.name == "selsynJson" } as? PartData.FileItem
//                )

                logger.info(" +++ POST /add_case {}", objs = arrayOf(case))
                ConverterMockContext(
                    requestToSave = case
                )
            } catch (e: Throwable) {
                log.error("Error: {}", e)
                ConverterMockContext(
                    errors = mutableListOf(e),
                    status = ConverterMockContext.Statuses.ERROR
                )
            }
            createService.exec(context)
            when (context.status) {
                ConverterMockContext.Statuses.OK -> {
                    call.respond(HttpStatusCode.OK, context.responseToSave)
                }
                else -> call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}
