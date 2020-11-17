package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.MetalRateCriticalEvent
import ru.datana.smart.ui.converter.common.events.MetalRateWarningEvent
import ru.datana.smart.ui.converter.common.events.SuccessMeltEvent
import ru.datana.smart.ui.converter.common.models.SignalerModel
import ru.datana.smart.ui.converter.common.models.SignalerSoundModel
import java.time.Instant
import java.util.*

object CreateSuccessMeltEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        context.signaler = SignalerModel(
            level = SignalerModel.SignalerLevelModel.INFO,
            sound = SignalerSoundModel.NONE
        )
        context.status = CorStatus.FINISHED
        val meltId: String = context.meltInfo.id
        val slagRateTime = Instant.now()
        context.eventsRepository.getAllByMeltId(meltId).map {
            if (it is MetalRateCriticalEvent || it is MetalRateWarningEvent) {
                return
            }
        }
        context.eventsRepository.put(
            meltId,
            SuccessMeltEvent(
                id = UUID.randomUUID().toString(),
                timeStart = slagRateTime,
                timeFinish = slagRateTime,
                warningPoint = context.metalRateWarningPoint,
                isActive = false
            )
        )
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
