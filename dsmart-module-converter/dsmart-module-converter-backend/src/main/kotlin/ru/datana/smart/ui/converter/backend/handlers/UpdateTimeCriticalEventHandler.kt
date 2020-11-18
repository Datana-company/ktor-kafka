package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.models.SignalerModel
import ru.datana.smart.ui.converter.common.models.SignalerSoundModel
import java.time.Instant

/*
* UpdateTimeCriticalEventHandler - если прошло время больше, чем значение DATA_TIMEOUT,
* то записываем текущее событие "Критическая ситуация" в историю.
* Запускается сирена.
* */
object UpdateTimeCriticalEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        val slagRateTime = Instant.now()
        val activeEvent: ModelEvent? = context.eventsRepository
            .getActiveByMeltIdAndEventType(meltId, ModelEvent.EventType.METAL_RATE_CRITICAL_EVENT)
        activeEvent?.let {
            val timeStartWithShift = it.timeStart.plusMillis(context.reactionTime)
            val isReactionTimeUp = slagRateTime >= timeStartWithShift
            val isActive = !isReactionTimeUp
            if (isReactionTimeUp) {
                context.signaler = SignalerModel(
                    level = SignalerModel.SignalerLevelModel.CRITICAL,
                    sound = SignalerSoundModel(
                        SignalerSoundModel.SignalerSoundTypeModel.SOUND_1, 3000
                    )
                )
            }
            it.timeFinish = slagRateTime
            it.isActive = isActive
            context.eventsRepository.update(it)
        } ?: return
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}

