package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.extensions.eventMetalCriticalReached
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.models.SignalerModel
import ru.datana.smart.ui.converter.common.models.SignalerSoundModel
import ru.datana.smart.ui.converter.common.utils.toPercent
import java.util.*

/*
* CreateCriticalEventHandler - создаётся событие типа "Критическая ситуация",
* и светофор переходит в критический статус.
* */
object CreateCriticalEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id

        /**
         * NKR-1062:
         *  Нужно ли здесь брать значения из контекста, может ли другой поток вмешаться?
         */
        //val currentAngle = context.currentAngle
        val activeEvent: ModelEvent? = context.eventsRepository
            .getActiveByMeltIdAndEventType(meltId, ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT)
        //val slagRateTime = context.timeStart
        //val avgSteelRate = context.avgSteelRate
        activeEvent?.let {
            return
        } ?: run {
            context.eventsRepository.create(
                context.eventMetalCriticalReached()
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
