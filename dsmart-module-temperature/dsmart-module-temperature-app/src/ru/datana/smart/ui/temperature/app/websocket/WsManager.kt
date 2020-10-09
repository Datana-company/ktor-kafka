package ru.datana.smart.ui.temperature.app.websocket

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import io.ktor.http.cio.websocket.send
import kotlinx.serialization.json.Json
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponse
import ru.datana.smart.logger.DatanaLogContext
import ru.datana.smart.ui.temperature.ws.models.WsDsmartResponseAnalysis
import ru.datana.smart.ui.temperature.ws.models.WsDsmartResponseTemperature
import java.util.concurrent.ConcurrentHashMap

class WsManager(
    val json : Json,
    val log: DatanaLogContext
) {

    private val wsSessions = ConcurrentHashMap.newKeySet<DefaultWebSocketSession>()

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

    fun addSession(session: DefaultWebSocketSession) {
        wsSessions += session
    }

    fun delSession(session: DefaultWebSocketSession) {
        wsSessions -= session
    }

}
