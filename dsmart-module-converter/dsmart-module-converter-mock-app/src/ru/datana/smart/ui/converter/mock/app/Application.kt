package ru.datana.smart.ui.converter.mock.app

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.meta.models.ConverterMeltInfo
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Location for uploading videos.
 */
@Location("/upload")
class Upload()

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@OptIn(KtorExperimentalAPI::class)
@Suppress("unused") // Referenced in application.conf
fun Application.module(testing: Boolean = false) {

    val logger = datanaLogger(::main::class.java)

    val pathToCatalog: String by lazy { environment.config.property("ktor.catalog.path").getString().trim() }
    val kafkaServers: String by lazy { environment.config.property("ktor.kafka.bootstrap.servers").getString().trim() }
    val kafkaTopic: String by lazy { environment.config.property("ktor.kafka.producer.topic.meta").getString().trim() }

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

    val caseCatalogDir = File(pathToCatalog)
    if (!caseCatalogDir.mkdirs() && !caseCatalogDir.exists()) {
        throw IOException("Failed to create directory ${caseCatalogDir.absolutePath}")
    }

    val listService by lazy { ConverterMockListService(
        pathToCatalog = pathToCatalog
    ) }
    val startService by lazy { ConverterMockStartService(
        pathToCatalog = pathToCatalog,
        kafkaProducer = kafkaProducer,
        kafkaTopic = kafkaTopic
    ) }
    val createService by lazy { ConverterMockCreateService(
        pathToCatalog = pathToCatalog
    ) }
    val uploadService by lazy { ConverterMockStartService(
        pathToCatalog = pathToCatalog,
        kafkaProducer = kafkaProducer,
        kafkaTopic = kafkaTopic
    ) }

    routing {
        static("/") {
            defaultResource("static/index.html")
            resources("static")
        }

        get("/list") {
            val context = ConverterMockContext()
            listService.exec(context)
            when(context.status) {
                ConverterMockContext.Statuses.OK -> call.respond(context.responseData)
                else -> call.respond(HttpStatusCode.InternalServerError)
            }
        }


        get("/front-config") {
            call.respondText(
                """
                {
                    "settings": [
                        {"variable": "variable1"},
                        {"variable": "variable2"},
                        {"variable": "variable3"}
                    ]
                }
                """.trimIndent()
            )
        }

        get("/send") {
            val case = call.parameters["case"] ?: throw BadRequestException("No case is specified")
            val context = ConverterMockContext(
                startCase = case
            )
            startService.exec(context)
            when(context.status) {
                ConverterMockContext.Statuses.OK -> call.respond(HttpStatusCode.OK)
                else -> call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post("/add_case") {
            val request: ConverterCaseSaveRequest = try {
                call.receive()
            } catch (e: Throwable) {
                logger.error("Error parsing meltInfo body from frontend: {}", call.receiveText())
                return@post
            }
            val context = ConverterMockContext(
                requestToSave = request
            )
            createService.exec(context)
            when(context.status) {
                ConverterMockContext.Statuses.OK -> call.respond(HttpStatusCode.OK, context.responseToSave)
                else -> call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post<Upload> {
            call.respond(HttpStatusCode.OK)
            val multipart = call.receiveMultipart()
            val context = ConverterMockContext(
            )
            createService.exec(context)
            when(context.status) {
                ConverterMockContext.Statuses.OK -> call.respond(HttpStatusCode.OK, context.responseToSave)
                else -> call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}
