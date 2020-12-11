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
* CreateSuccessMeltSlagEventHandler - создаётся событие типа "Информация" об успешном завершении плавки
* и сразу записывается в историю.
* */
object CreateSuccessMeltSlagEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        context.signaler = SignalerModel(
            level = SignalerModel.SignalerLevelModel.NO_SIGNAL,
            sound = SignalerSoundModel.NONE
        )
        context.status = CorStatus.FINISHED
        val meltId: String = context.meltInfo.id
        val slagRateTime = context.timeStart
        context.eventsRepository.getAllByMeltId(meltId).map {
            if (it.type == ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT ||
                it.type == ModelEvent.EventType.STREAM_RATE_WARNING_EVENT
            ) {
                return
            }
        }
        context.eventsRepository.create(
            ModelEvent(
                id = UUID.randomUUID().toString(),
                meltId = meltId,
                type = ModelEvent.EventType.SUCCESS_MELT_EVENT,
                timeStart = slagRateTime,
                timeFinish = slagRateTime,
                warningPoint = context.streamRateWarningPoint,
                isActive = false,
                title = "Информация",
                textMessage = """
                              Допустимая норма потерь шлака ${context.streamRateWarningPoint.toPercent()}% не была превышена.
                              """.trimIndent(),
                category = ModelEvent.Category.INFO
            )
        )
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
