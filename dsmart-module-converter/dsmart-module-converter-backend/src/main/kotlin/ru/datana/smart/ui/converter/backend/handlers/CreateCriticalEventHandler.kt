package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.utils.toPercent
import java.util.*

object CreateCriticalEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.currentState.get()?.currentMeltInfo?.id ?: return
        val slagRateTime = context.frame.frameTime
        val activeEvent: ModelEvent? = context.eventsRepository
            .getActiveByMeltIdAndEventType(meltId, ModelEvent.EventType.METAL_RATE_CRITICAL_EVENT)
        activeEvent?.let {
//            val updateEvent = ModelEvent(
//                id = it.id,
//                timeStart = it.timeStart,
//                timeFinish = slagRateTime,
//                metalRate = it.metalRate,
//                title = it.title,
//                isActive = it.isActive,
//                angleStart = it.angleStart,
//                angleFinish = it.angleFinish,
//                angleMax = it.angleMax,
//                criticalPoint = it.criticalPoint
//            )
            it.timeFinish = slagRateTime
            context.eventsRepository.update(it)
        } ?: context.eventsRepository.create(
            ModelEvent(
                id = UUID.randomUUID().toString(),
                meltId = meltId,
                type = ModelEvent.EventType.METAL_RATE_CRITICAL_EVENT,
                timeStart = slagRateTime,
                timeFinish = slagRateTime,
                metalRate = context.slagRate.steelRate,
                criticalPoint = context.metalRateCriticalPoint,
                title = "Критическая ситуация",
                textMessage = """
                              В потоке детектирован металл – ${toPercent(context.slagRate.steelRate)}%, процент потерь превышает критическое значение – ${toPercent(context.metalRateCriticalPoint)} %. Верните конвертер в вертикальное положение!
                              """.trimIndent(),
                category = ModelEvent.Category.CRITICAL
            )
        )
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
