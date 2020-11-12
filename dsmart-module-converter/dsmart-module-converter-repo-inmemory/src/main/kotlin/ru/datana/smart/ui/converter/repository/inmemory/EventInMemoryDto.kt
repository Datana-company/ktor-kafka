package ru.datana.smart.ui.converter.repository.inmemory

import ru.datana.smart.ui.converter.common.models.ModelEvent
import java.time.Instant

data class EventInMemoryDto(
    var id: String = "",
    var meltId: String = "",
    var type: ModelEvent.EventType = ModelEvent.EventType.NONE,
    var timeStart: Long = Instant.now().toEpochMilli(),
    var timeFinish: Long = Instant.now().toEpochMilli(),
    var title: String = "Информация",
    var textMessage: String = "",
    var category: ModelEvent.Category = ModelEvent.Category.INFO,
    var isActive: Boolean = true,
    var executionStatus: ModelEvent.ExecutionStatus = ModelEvent.ExecutionStatus.NONE,
    var metalRate: Double = Double.MIN_VALUE,
    var angleStart: Double = Double.MIN_VALUE,
    var angleFinish: Double = Double.MIN_VALUE,
    var angleMax: Double = Double.MIN_VALUE,
    var warningPoint: Double = Double.MIN_VALUE,
    var criticalPoint: Double = Double.MIN_VALUE
) {
    fun toModel(): ModelEvent = ModelEvent(
        id = id,
        type = type,
        meltId = meltId,
        timeStart = timeStart,
        timeFinish = timeFinish,
        title = title,
        textMessage = textMessage,
        category = category,
        isActive = isActive,
        executionStatus = executionStatus,
        metalRate = metalRate,
        angleStart = angleStart,
        angleFinish = angleFinish,
        angleMax = angleMax,
        warningPoint = warningPoint,
        criticalPoint = criticalPoint
    )

    companion object {
        fun of(event: ModelEvent) = of(event, event.id)

        fun of(event: ModelEvent, id: String) = EventInMemoryDto(
            id = id,
            meltId = event.meltId,
            type = event.type,
            timeStart = event.timeStart,
            timeFinish = event.timeFinish,
            title = event.title,
            textMessage = event.textMessage,
            category = event.category,
            isActive = event.isActive,
            executionStatus = event.executionStatus,
            metalRate = event.metalRate,
            angleStart = event.angleStart,
            angleFinish = event.angleFinish,
            angleMax = event.angleMax,
            warningPoint = event.warningPoint,
            criticalPoint = event.criticalPoint
        )
    }
}
