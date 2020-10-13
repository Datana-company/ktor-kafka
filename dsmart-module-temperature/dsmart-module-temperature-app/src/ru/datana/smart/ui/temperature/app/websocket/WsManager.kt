package ru.datana.smart.ui.temperature.app.websocket

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import io.ktor.http.cio.websocket.send
import kotlinx.serialization.json.Json
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponse
import ru.datana.smart.logger.DatanaLogContext
import ru.datana.smart.ui.temperature.ws.models.WsDsmartResponseAnalysis
import ru.datana.smart.ui.temperature.ws.models.WsDsmartResponseTemperature
import java.util.concurrent.ConcurrentHashMap

class WsManager {

    val wsSessions: MutableCollection<DefaultWebSocketSession> = ConcurrentHashMap.newKeySet()

    fun addSession(session: DefaultWebSocketSession) {
        wsSessions += session
    }

    fun delSession(session: DefaultWebSocketSession) {
        wsSessions -= session
    }
}
