package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.MetalRateInfoEvent
import ru.datana.smart.ui.converter.common.models.SignalerModel
import ru.datana.smart.ui.converter.common.models.SignalerSoundModel
import java.util.*

object CreateInfoEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.currentState.get()?.currentMeltInfo?.id ?: return
        val slagRateTime = context.frame.frameTime
        val activeEvent: MetalRateInfoEvent? = context.eventsRepository.getActiveMetalRateEventByMeltId(meltId) as? MetalRateInfoEvent
        activeEvent?.let {
            val updateEvent = MetalRateInfoEvent(
                id = it.id,
                timeStart = it.timeStart,
                timeFinish = slagRateTime,
                metalRate = it.metalRate,
                title = it.title,
                isActive = it.isActive,
                angleStart = it.angleStart,
                angleFinish = it.angleFinish,
                angleMax = it.angleMax
            )
            context.eventsRepository.put(meltId, updateEvent)
        } ?: context.eventsRepository.put(
            meltId,
            MetalRateInfoEvent(
                id = UUID.randomUUID().toString(),
                timeStart = slagRateTime,
                timeFinish = slagRateTime,
                metalRate = context.slagRate.steelRate
            )
        )
//         переключаем ли светофор при отправке инфо события?
        context.signaler = SignalerModel(
            level = SignalerModel.SignalerLevelModel.INFO,
            sound = SignalerSoundModel.NONE
        )
///////////////////////////////////////////////////////////
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
