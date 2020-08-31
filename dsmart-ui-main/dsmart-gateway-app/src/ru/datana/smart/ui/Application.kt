package ru.datana.smart.ui

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.content.*
import io.ktor.http.content.*
import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.websocket.*
import io.ktor.http.cio.websocket.*
import java.time.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.websocket.*
import io.ktor.client.features.websocket.WebSockets
import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import io.ktor.client.features.logging.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.stringify
import ru.datana.smart.ui.temperature.ws.models.WsDsmartResponseTemperature
import ru.datana.smart.ui.temperature.ws.models.WsDsmartTemperatures
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module(testing: Boolean = false) {
    val wsSessions = ConcurrentHashMap.newKeySet<DefaultWebSocketSession>()
    val random = Random(System.currentTimeMillis())
    val jsonMapper = Json

    suspend fun sendToAll(temp: Double) {
        wsSessions.forEach {
            val data = WsDsmartResponseTemperature(data = WsDsmartTemperatures(temperature = temp))
            val jsonString = Json.encodeToString(data)
            log.info("Sending $jsonString")
            it.send(jsonString)
        }
    }

    launch {
        while (true) {
            sendToAll(random.nextDouble(15.0, 35.0))
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

    val client = HttpClient(CIO) {
        install(Logging) {
            level = LogLevel.HEADERS
        }
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
                for (frame in incoming) {
                    if (frame is Frame.Text) {
//                        val message = frame.readText()
//                        log.info("A message is received: $message")
//                        send(Frame.Text("{\"event\": \"update-texts\", \"data\": \"Server received a message\"}"))
                    }
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

}


