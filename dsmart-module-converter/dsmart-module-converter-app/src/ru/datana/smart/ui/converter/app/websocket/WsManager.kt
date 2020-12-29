package ru.datana.smart.ui.converter.app.websocket

import io.ktor.http.cio.websocket.*
import kotlinx.serialization.json.Json
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.converter.app.mappings.*
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.IWsManager
import ru.datana.smart.ui.converter.ws.models.*
import java.util.concurrent.ConcurrentHashMap

class WsManager : IWsManager {
    val logger = datanaLogger(this::class.java)
    val wsSessions: MutableCollection<DefaultWebSocketSession> = ConcurrentHashMap.newKeySet()
    val kotlinxSerializer: Json = Json { encodeDefaults = true }

    suspend fun addSession(session: DefaultWebSocketSession, context: ConverterBeContext) {
        wsSessions += session
        val currentMeltId = context.currentStateRepository.currentMeltId(context.converterId) // что-то здесь не так
        val events = context.eventRepository.getAllByMeltId(currentMeltId)
        context.eventList = events
        val wsConverterState = context.toWsResponseConverterState()
        val converterStateSerializedString =
            kotlinxSerializer.encodeToString(WsDsmartResponseConverterState.serializer(), wsConverterState)
        logger.biz(
            msg = "Add Session",
            data = object {
                val logTypeId = "converter-backend-WsManager-send-addsession"
                val wsAddSession = wsSessions
            })
        session.send(converterStateSerializedString)
    }

    override suspend fun sendFinish(context: ConverterBeContext) {
        val wsConverterState = context.toWsResponseConverterState()
        val converterStateSerializedString =
            kotlinxSerializer.encodeToString(WsDsmartResponseConverterState.serializer(), wsConverterState)
        logger.biz(
            msg = "Send Finish",
            data = object {
                val logTypeId = "converter-backend-WsManager-send-finish"
                val wsConverterState = wsConverterState
            }
        )
        send(converterStateSerializedString)
    }

    override suspend fun sendAngles(context: ConverterBeContext) {
        val wsAngles = context.toWsConverterResponseAngles()
        val meltInfoSerializedString =
            kotlinxSerializer.encodeToString(WsDsmartResponseConverterAngles.serializer(), wsAngles)
        logger.biz(
            msg = "Send Angles",
            data = object {
                val logTypeId = "converter-backend-WsManager-send-angle"
                val wsAngles = wsAngles
            }
        )
        send(meltInfoSerializedString)
    }

    override suspend fun sendMeltInfo(context: ConverterBeContext) {
        val wsMeltInfo = context.toWsConverterResponseMeltInfo()
        val meltInfoSerializedString =
            kotlinxSerializer.encodeToString(WsDsmartResponseConverterMeltInfo.serializer(), wsMeltInfo)
        logger.biz(
            msg = "Send Melt Info",
            data = object {
                val logTypeId = "converter-backend-WsManager-send-meltinfo"
                val wsMeltInfo = wsMeltInfo
            }
        )
        send(meltInfoSerializedString)
    }

    override suspend fun sendSlagRates(context: ConverterBeContext) {
        val wsSlagRate = context.toWsConverterResponseSlagRates()
        val slagRateSerializedString =
            kotlinxSerializer.encodeToString(WsDsmartResponseConverterSlagRates.serializer(), wsSlagRate)
        logger.biz(
            msg = "Send SlagRate",
            data = object {
                val logTypeId = "converter-backend-WsManager-send-slagrate"
                val wsSlagRate = wsSlagRate
            }
        )
        send(slagRateSerializedString)
    }

    override suspend fun sendFrames(context: ConverterBeContext) {
        val wsFrame = context.toWsConverterResponseFrame()
        val frameSerializedString =
            kotlinxSerializer.encodeToString(WsDsmartResponseConverterFrame.serializer(), wsFrame)
        logger.biz(
            msg = "Send Frames",
            data = object {
                val logTypeId = "converter-backend-WsManager-send-frame"
                val wsFrame = wsFrame
            }
        )
        send(frameSerializedString)
    }

    override suspend fun sendEvents(context: ConverterBeContext) {
        val wsEvents = context.toWsResponseConverterEvent()
        val eventsSerializedString =
            kotlinxSerializer.encodeToString(WsDsmartResponseConverterEvents.serializer(), wsEvents)
        logger.biz(
            msg = "Send Events",
            data = object {
                val logTypeId = "converter-backend-WsManager-send-event"
                val wsEvents = wsEvents
            }
        )
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
