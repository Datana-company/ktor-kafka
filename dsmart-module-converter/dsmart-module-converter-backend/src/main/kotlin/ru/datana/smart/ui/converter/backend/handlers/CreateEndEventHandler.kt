package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.EndMeltEvent
import java.time.Instant
import java.util.*

object CreateEndEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.currentMeltInfo.get()?.id ?: return
        context.eventsRepository.getActiveMetalRateEventByMeltId(meltId) as? EndMeltEvent ?: context.eventsRepository.put(
            meltId,
            EndMeltEvent(
                id = UUID.randomUUID().toString(),
                timeStart = context.frame.frameTime ?: Instant.now().toEpochMilli(),
                timeFinish = context.frame.frameTime ?: Instant.now().toEpochMilli(),
                metalRate = context.slagRate.steelRate!!
            )
        )
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
