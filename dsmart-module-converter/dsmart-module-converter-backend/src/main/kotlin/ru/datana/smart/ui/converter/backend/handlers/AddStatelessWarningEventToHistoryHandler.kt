package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.models.SignalerModel
import ru.datana.smart.ui.converter.common.models.SignalerSoundModel

/*
* AddStatelessWarningEventToHistoryHandler - записывает текущее событие "Предупреждение" в историю без изменения статуса
* */
object AddStatelessWarningEventToHistoryHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        val activeEvent: ModelEvent? = context.eventsRepository
            .getActiveByMeltIdAndEventType(meltId, ModelEvent.EventType.METAL_RATE_WARNING_EVENT)
        activeEvent?.let {
            it.isActive = false
            context.eventsRepository.update(it)
            context.signaler = SignalerModel(
                level = SignalerModel.SignalerLevelModel.NO_SIGNAL,
                sound = SignalerSoundModel.NONE
            )
        } ?: return
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
