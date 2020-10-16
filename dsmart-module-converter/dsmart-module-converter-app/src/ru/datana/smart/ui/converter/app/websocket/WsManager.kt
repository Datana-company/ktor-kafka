package ru.datana.smart.ui.converter.app.websocket

import io.ktor.http.cio.websocket.DefaultWebSocketSession
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
