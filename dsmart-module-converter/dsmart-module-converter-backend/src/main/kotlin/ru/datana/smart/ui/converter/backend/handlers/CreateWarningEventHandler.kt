package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.MetalRateWarningEvent
import ru.datana.smart.ui.converter.common.models.SignalerModel
import ru.datana.smart.ui.converter.common.models.SignalerSoundModel
import java.time.Instant
import java.util.*

/*
* CreateWarningEventHandler - создаём событие типа "Предупреждение",
* и светофор переходит в статус "Предупреждение".
* */
object CreateWarningEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        val slagRateTime = Instant.now()
        val currentAngle = context.currentState.get().lastAngles.angle
        val activeEvent: MetalRateWarningEvent? =
            context.eventsRepository.getActiveMetalRateEventByMeltId(meltId) as? MetalRateWarningEvent
        activeEvent?.let {
            return
        } ?: run {
            context.eventsRepository.put(
                meltId,
                MetalRateWarningEvent(
                    id = UUID.randomUUID().toString(),
                    timeStart = slagRateTime,
                    timeFinish = slagRateTime,
                    metalRate = context.slagRate.avgSteelRate,
                    warningPoint = context.metalRateWarningPoint,
                    angleStart = currentAngle
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
