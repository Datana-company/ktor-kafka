package ru.datana.smart.ui.converter.app.websocket

import io.ktor.http.cio.websocket.*
import kotlinx.serialization.json.Json
import ru.datana.smart.ui.converter.app.mappings.*
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.IWsManager
import ru.datana.smart.ui.converter.ws.models.*
import java.util.concurrent.ConcurrentHashMap

class WsManager : IWsManager {

    val wsSessions: MutableCollection<DefaultWebSocketSession> = ConcurrentHashMap.newKeySet()
    val kotlinxSerializer: Json = Json { encodeDefaults = true }

    suspend fun addSession(session: DefaultWebSocketSession, context: ConverterBeContext) {
        wsSessions += session
        val currentMeltId = context.currentState.get().currentMeltInfo.id
        val events = context.eventsRepository.getAllByMeltId(currentMeltId)
        context.events = events
        val wsConverterState = context.toWsResponseConverterState()
        val converterStateSerializedString = kotlinxSerializer.encodeToString(WsDsmartResponseConverterState.serializer(), wsConverterState)
        session.send(converterStateSerializedString)
    }

    override suspend fun sendFinish(context: ConverterBeContext) {
        val wsConverterState = context.toWsResponseConverterState()
        val converterStateSerializedString = kotlinxSerializer.encodeToString(WsDsmartResponseConverterState.serializer(), wsConverterState)
        send(converterStateSerializedString)
    }

    override suspend fun sendAngles(context: ConverterBeContext) {
        val wsAngles = context.toWsConverterResponseAngles()
        val meltInfoSerializedString = kotlinxSerializer.encodeToString(WsDsmartResponseConverterAngles.serializer(), wsAngles)
        send(meltInfoSerializedString)
    }

    override suspend fun sendMeltInfo(context: ConverterBeContext) {
        val wsMeltInfo = context.toWsConverterResponseMeltInfo()
        val meltInfoSerializedString = kotlinxSerializer.encodeToString(WsDsmartResponseConverterMeltInfo.serializer(), wsMeltInfo)
        send(meltInfoSerializedString)
    }

    override suspend fun sendSlagRate(context: ConverterBeContext) {
        val wsSlagRate = context.toWsConverterResponseSlagRate()
        val slagRateSerializedString = kotlinxSerializer.encodeToString(WsDsmartResponseConverterSlagRate.serializer(), wsSlagRate)
        send(slagRateSerializedString)
    }

    override suspend fun sendFrames(context: ConverterBeContext) {
        val wsFrame = context.toWsConverterResponseFrame()
        val frameSerializedString = kotlinxSerializer.encodeToString(WsDsmartResponseConverterFrame.serializer(), wsFrame)
        send(frameSerializedString)
    }

    override suspend fun sendEvents(context: ConverterBeContext) {
        val wsEvents = context.toWsResponseConverterEvent()
        val eventsSerializedString = kotlinxSerializer.encodeToString(WsDsmartResponseConverterEvents.serializer(), wsEvents)
        send(eventsSerializedString)
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

    fun delSession(session: DefaultWebSocketSession) {
        wsSessions -= session
    }
}
