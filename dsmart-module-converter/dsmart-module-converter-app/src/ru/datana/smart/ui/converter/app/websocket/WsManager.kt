package ru.datana.smart.ui.converter.app.websocket

import io.ktor.http.cio.websocket.*
import kotlinx.serialization.json.Json
import ru.datana.smart.ui.converter.app.mappings.*
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.IWsManager
import ru.datana.smart.ui.converter.common.models.ModelEvents
import ru.datana.smart.ui.converter.ws.models.*
import java.util.concurrent.ConcurrentHashMap

class WsManager : IWsManager {

    val wsSessions: MutableCollection<DefaultWebSocketSession> = ConcurrentHashMap.newKeySet()
    val kotlinxSerializer: Json = Json { encodeDefaults = true }

    // TODO: Переписать метод
    suspend fun addSession(session: DefaultWebSocketSession, context: ConverterBeContext) {
        wsSessions += session
        context.currentMeltInfo.get()?.let {
            val events = it.id?.let { id -> context.eventsRepository.getAllByMeltId(id) }
            context.also {
                    context -> context.events = ModelEvents(events = events)
            }
            val wsConverterInit = WsDsmartResponseConverterInit(
                data = toWsConverterInitModel(context)
            )
            val converterInitSerializedString = kotlinxSerializer.encodeToString(WsDsmartResponseConverterInit.serializer(), wsConverterInit)
            session.send(converterInitSerializedString)
        }

//        val outObj = context.toWsInit()
//        session.send()
    }

    override suspend fun sendAngles(context: ConverterBeContext) {
        val wsAngles = WsDsmartResponseConverterAngles(
            data = toWsConverterAnglesModel(context.angles)
        )
        val meltInfoSerializedString = kotlinxSerializer.encodeToString(WsDsmartResponseConverterAngles.serializer(), wsAngles)
        send(meltInfoSerializedString)
    }

    override suspend fun sendMeltInfo(context: ConverterBeContext) {
        val wsMeltInfo = WsDsmartResponseConverterMeltInfo(
            data = toWsConverterMeltInfoModel(context.meltInfo)
        )
        val meltInfoSerializedString = kotlinxSerializer.encodeToString(WsDsmartResponseConverterMeltInfo.serializer(), wsMeltInfo)
        send(meltInfoSerializedString)
    }

    override suspend fun sendSlagRate(context: ConverterBeContext) {
        val wsSlagRate = WsDsmartResponseConverterSlagRate(
            data = toWsConverterSlagRateModel(context)
        )
        val slagRateSerializedString = kotlinxSerializer.encodeToString(WsDsmartResponseConverterSlagRate.serializer(), wsSlagRate)
        send(slagRateSerializedString)
    }

    override suspend fun sendFrames(context: ConverterBeContext) {
        val wsFrame = WsDsmartResponseConverterFrame(
            data = toWsConverterFrameModel(context.frame)
        )
        val frameSerializedString = kotlinxSerializer.encodeToString(WsDsmartResponseConverterFrame.serializer(), wsFrame)
        send(frameSerializedString)
    }

    override suspend fun sendEvents(context: ConverterBeContext) {
        val wsEvents = WsDsmartResponseEvents(
            data = toWsEventListModel(context.events)
        )
        val eventsSerializedString = kotlinxSerializer.encodeToString(WsDsmartResponseEvents.serializer(), wsEvents)
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
