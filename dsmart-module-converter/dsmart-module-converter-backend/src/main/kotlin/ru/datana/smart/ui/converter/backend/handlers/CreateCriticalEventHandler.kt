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
* CreateCriticalEventHandler - создаётся событие типа "Критическая ситуация",
* и светофор переходит в критический статус.
* */
object CreateCriticalEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        val currentAngle = context.currentState.get().lastAngles.angle
        val activeEvent: ModelEvent? = context.eventsRepository
            .getActiveByMeltIdAndEventType(meltId, ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT)
        val slagRateTime = Instant.now()
        val avgSteelRate = context.currentState.get().avgSlagRate.steelRate
        activeEvent?.let {
            return
        } ?: run {
            context.eventsRepository.create(
                ModelEvent(
                    meltId = meltId,
                    type = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                    timeStart = slagRateTime,
                    timeFinish = slagRateTime,
                    metalRate = avgSteelRate,
                    angleStart = currentAngle,
                    title = "Критическая ситуация",
                    textMessage = """
                                  В потоке детектирован металл – ${toPercent(avgSteelRate)}%, процент потерь превышает критическое значение – ${toPercent(context.streamRateCriticalPoint)}%. Верните конвертер в вертикальное положение!
                                  """.trimIndent(),
                    category = ModelEvent.Category.CRITICAL,
                    executionStatus = ModelEvent.ExecutionStatus.STATELESS
                )
            )
            context.signaler = SignalerModel(
                level = SignalerModel.SignalerLevelModel.CRITICAL,
                sound = SignalerSoundModel(
                    SignalerSoundModel.SignalerSoundTypeModel.SOUND_1, 3000
                )
            )
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
