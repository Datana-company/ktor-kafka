package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.models.SignalerModel
import ru.datana.smart.ui.converter.common.models.SignalerSoundModel
import ru.datana.smart.ui.converter.common.utils.toPercent
import java.time.Instant
import java.util.*

/*
* CreateInfoSlagEventHandler - создаётся событие типа "Информация",
* и светофор переходит в информационный статус.
* */
object CreateInfoSlagEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        val slagRateTime = Instant.now()
        val currentAngle = context.currentState.get().lastAngles.angle
        val activeEvent: ModelEvent? = context.eventsRepository
            .getActiveByMeltIdAndEventType(meltId, ModelEvent.EventType.STREAM_RATE_INFO_EVENT)
        val avgSlagRate = context.currentState.get().avgSlagRate.slagRate
        activeEvent?.let {
            return
        } ?: run {
            context.eventsRepository.create(
                ModelEvent(
                    id = UUID.randomUUID().toString(),
                    meltId = meltId,
                    type = ModelEvent.EventType.STREAM_RATE_INFO_EVENT,
                    timeStart = slagRateTime,
                    timeFinish = slagRateTime,
                    slagRate = avgSlagRate,
                    warningPoint = context.streamRateWarningPoint,
                    angleStart = currentAngle,
                    title = "Информация",
                    textMessage = """
                                  Достигнут предел шлака в потоке – ${toPercent(avgSlagRate)}%.
                                  """.trimIndent(),
                    category = ModelEvent.Category.INFO
                )
            )
            context.signaler = SignalerModel(
                level = SignalerModel.SignalerLevelModel.INFO,
                sound = SignalerSoundModel.NONE
            )
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
