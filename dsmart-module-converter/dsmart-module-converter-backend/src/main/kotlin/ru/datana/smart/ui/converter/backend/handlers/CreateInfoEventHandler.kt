package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.MetalRateInfoEvent
import ru.datana.smart.ui.converter.common.models.SignalerModel
import ru.datana.smart.ui.converter.common.models.SignalerSoundModel
import java.util.*

/*
* CreateInfoEventHandler - создаётся событие типа "Информация",
* и светофор переходит в информационный статус.
* */
object CreateInfoEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        val slagRateTime = context.frame.frameTime
        val currentAngle = context.currentState.get().lastAngles.angle
        val activeEvent: MetalRateInfoEvent? = context.eventsRepository.getActiveMetalRateEventByMeltId(meltId) as? MetalRateInfoEvent
        activeEvent?.let {
            return
        } ?: run {
            context.eventsRepository.put(
                meltId,
                MetalRateInfoEvent(
                    id = UUID.randomUUID().toString(),
                    timeStart = slagRateTime,
                    timeFinish = slagRateTime,
                    metalRate = context.slagRate.steelRate,
                    warningPoint = context.metalRateWarningPoint,
                    angleStart = currentAngle
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
