package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.models.SignalerModel
import ru.datana.smart.ui.converter.common.models.SignalerSoundModel
import ru.datana.smart.ui.converter.common.utils.toPercent
import java.util.*

/*
* CreateInfoSlagEventHandler - создаётся событие типа "Информация",
* и светофор переходит в информационный статус.
* */
object CreateInfoSlagEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        val slagRateTime = context.timeStart
        val currentAngle = context.currentAngle
        val activeEvent: ModelEvent? = context.eventsRepository
            .getActiveByMeltIdAndEventType(meltId, ModelEvent.EventType.STREAM_RATE_INFO_EVENT)
        val avgSlagRate = context.avgSlagRate
        activeEvent?.let {
            return
        } ?: run {
            context.eventsRepository.create(
                ModelEvent(
                    meltId = meltId,
                    type = ModelEvent.EventType.STREAM_RATE_INFO_EVENT,
                    timeStart = slagRateTime,
                    timeFinish = slagRateTime,
                    slagRate = avgSlagRate,
                    angleStart = currentAngle,
                    title = "Информация",
                    textMessage = """
                                  Достигнут предел потерь шлака в потоке – ${avgSlagRate.toPercent()}%.
                                  """.trimIndent(),
                    category = ModelEvent.Category.INFO,
                    executionStatus = ModelEvent.ExecutionStatus.STATELESS
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
