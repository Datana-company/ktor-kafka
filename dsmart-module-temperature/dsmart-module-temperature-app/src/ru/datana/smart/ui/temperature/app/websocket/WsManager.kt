package ru.datana.smart.ui.temperature.app.websocket

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import io.ktor.http.cio.websocket.send
import kotlinx.serialization.json.Json
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponse
import ru.datana.smart.logger.DatanaLogContext
import ru.datana.smart.ui.temperature.ws.models.WsDsmartResponseAnalysis
import ru.datana.smart.ui.temperature.ws.models.WsDsmartResponseTemperature

class WsManager(
    val wsSessions : MutableCollection<DefaultWebSocketSession>,
    val json : Json,
    val log: DatanaLogContext
) {

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

}
