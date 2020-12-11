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
* CreateWarningEventHandler - создаём событие типа "Предупреждение",
* и светофор переходит в статус "Предупреждение".
* */
object CreateWarningEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        val slagRateTime = Instant.now()
        val currentAngle = context.currentState.get().lastAngles.angle
        val activeEvent: ModelEvent? = context.eventsRepository
            .getActiveByMeltIdAndEventType(meltId, ModelEvent.EventType.STREAM_RATE_WARNING_EVENT)
        val avgSteelRate = context.currentState.get().avgSlagRate.steelRate
        activeEvent?.let {
            return
        } ?: run {
            context.eventsRepository.create(
                ModelEvent(
                    meltId = meltId,
                    type = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                    timeStart = slagRateTime,
                    timeFinish = slagRateTime,
                    metalRate = avgSteelRate,
                    angleStart = currentAngle,
                    title = "Предупреждение",
                    textMessage = """
                                  В потоке детектирован металл – ${toPercent(avgSteelRate)}% сверх допустимой нормы ${toPercent(context.streamRateWarningPoint)}%. Верните конвертер в вертикальное положение.
                                  """.trimIndent(),
                    category = ModelEvent.Category.WARNING,
                    executionStatus = ModelEvent.ExecutionStatus.STATELESS
                )
            )
            context.signaler = SignalerModel(
                level = SignalerModel.SignalerLevelModel.WARNING,
                sound = SignalerSoundModel.NONE
            )
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
