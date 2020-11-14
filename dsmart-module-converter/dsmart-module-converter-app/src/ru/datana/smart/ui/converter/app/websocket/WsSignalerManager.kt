package ru.datana.smart.ui.converter.app.websocket

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import io.ktor.http.cio.websocket.send
import kotlinx.serialization.json.Json
import ru.datana.smart.ui.converter.app.mappings.*
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.IWsSignalerManager
import ru.datana.smart.ui.converter.common.models.ModelEvents
import ru.datana.smart.ui.converter.ws.models.WsDsmartResponseConverterState
import ru.datana.smart.ui.converter.ws.models.WsDsmartResponseEvents
import ru.datana.smart.ui.converter.ws.models.WsDsmartResponseConverterSignaler
import java.util.concurrent.ConcurrentHashMap

class WsSignalerManager : IWsSignalerManager {

    val wsSessions: MutableCollection<DefaultWebSocketSession> = ConcurrentHashMap.newKeySet()
    val kotlinxSerializer: Json = Json { encodeDefaults = true }

    suspend fun init(session: DefaultWebSocketSession, context: ConverterBeContext) {
        wsSessions += session
        val wsSignaler = WsDsmartResponseConverterSignaler(
            data = toWsConverterSignalerModel(context.signaler)
        )
        val converterStateSerializedString = kotlinxSerializer.encodeToString(
            WsDsmartResponseConverterSignaler.serializer(),
            wsSignaler
        )
        session.send(converterStateSerializedString)
    }

    override suspend fun sendSignaler(context: ConverterBeContext) {
        val wsSignaler = WsDsmartResponseConverterSignaler(
            data = toWsConverterSignalerModel(context.signaler)
        )
        val signalerSerializedString = kotlinxSerializer
            .encodeToString(WsDsmartResponseConverterSignaler.serializer(), wsSignaler)
        send(signalerSerializedString)
    }

    private suspend fun send(serializedString: String) {
        val wsSessionsIterator = wsSessions.iterator()
        while (wsSessionsIterator.hasNext()) {
            wsSessionsIterator.next().apply {
                try {
                    send(serializedString)
                } catch (e: Throwable) {
                    wsSessionsIterator.remove()
                }
            }
        }
    }

    fun close(session: DefaultWebSocketSession) {
        wsSessions -= session
    }
}
