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

    suspend fun addSession(session: DefaultWebSocketSession, context: ConverterBeContext) {
        wsSessions += session
        if (context.currentMeltInfo.get() != null) {
            val wsMeltInfo = WsDsmartResponseConverterMeltInfo(
                data = toWsConverterMeltInfoModel(context.currentMeltInfo.get()!!)
            )
            val meltInfoSerializedString = Json { encodeDefaults = true }.encodeToString(WsDsmartResponseConverterMeltInfo.serializer(), wsMeltInfo)
            session.send(meltInfoSerializedString)
//            val outObj = context.toWsInit()
//            session.send()
        }
    }

    override suspend fun sendToAll(context: ConverterBeContext) {

    }

    override suspend fun sendAngles(context: ConverterBeContext) {
        val wsAngles = WsDsmartResponseConverterAngles(
            data = toWsConverterAnglesModel(context.angles)
        )
        val meltInfoSerializedString = Json { encodeDefaults = true }.encodeToString(WsDsmartResponseConverterAngles.serializer(), wsAngles)
        send(meltInfoSerializedString)
    }

    override suspend fun sendMeltInfo(context: ConverterBeContext) {
        val wsMeltInfo = WsDsmartResponseConverterMeltInfo(
            data = toWsConverterMeltInfoModel(context.meltInfo)
        )
        val meltInfoSerializedString = Json { encodeDefaults = true }.encodeToString(WsDsmartResponseConverterMeltInfo.serializer(), wsMeltInfo)
        send(meltInfoSerializedString)
    }

    override suspend fun sendSlagRate(context: ConverterBeContext) {
        val wsSlagRate = WsDsmartResponseConverterSlagRate(
            data = toWsConverterSlagRateModel(context.slagRate)
        )
        val slagRateSerializedString = Json { encodeDefaults = true }.encodeToString(WsDsmartResponseConverterSlagRate.serializer(), wsSlagRate)
        send(slagRateSerializedString)
    }

    override suspend fun sendFrames(context: ConverterBeContext) {
        val wsFrame = WsDsmartResponseConverterFrame(
            data = toWsConverterFrameModel(context.frame)
        )
        val frameSerializedString = Json { encodeDefaults = true }.encodeToString(WsDsmartResponseConverterFrame.serializer(), wsFrame)
        send(frameSerializedString)
    }

    override suspend fun sendEvents(context: ConverterBeContext) {
        val wsEvents = WsDsmartResponseEvents(
            data = WsDsmartEventList(
                list = toWsEventListModel(context.events)
            )
        )
        val eventsSerializedString = Json { encodeDefaults = true }.encodeToString(WsDsmartResponseEvents.serializer(), wsEvents)
        send(eventsSerializedString)
    }

    override suspend fun sendTemperature(context: ConverterBeContext) {
        val wsTemperature = WsDsmartResponseTemperature(
            data = toWsTemperatureModel(context.temperature)
        )
        val temperatureSerializedString = Json { encodeDefaults = true }.encodeToString(WsDsmartResponseTemperature.serializer(), wsTemperature)
        send(temperatureSerializedString)
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
