package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.ExtEvent
import ru.datana.smart.ui.converter.common.models.ModelMeltInfo
import java.time.Instant
import java.util.*

object CreateExtEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        // TODO Нужно ли делать неактивными все другие сообщения?
        context.currentMeltInfo.set(ModelMeltInfo(id = UUID.randomUUID().toString())) //TODO для тестирования
        val meltId: String = context.currentMeltInfo.get()?.id ?: return
        println(" --- message: " + context.extEvents.message + " --- meltId: " + meltId)
        context.eventsRepository.put(
            meltId,
            ExtEvent(
                id = UUID.randomUUID().toString(),
                timeStart = Instant.now().toEpochMilli(),
                timeFinish = Instant.now().toEpochMilli() + 60000,
                textMessage = context.extEvents.message ?: ""
            )
        )
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
