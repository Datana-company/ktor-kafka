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

// TODO На будущее. Пока не заработало
//    val id: String? = null,
//    val meltId: String? = null,
//    val type: ModelEvent.EventType? = null,
//    val timeStart: Long? = null,
//    val timeFinish: Long? = null,
//    val title: String? = null,
//    val textMessage: String? = null,
//    val category: ModelEvent.Category? = null,
//    val isActive: Boolean? = true,
//    val executionStatus: ModelEvent.ExecutionStatus? = null,
//    val metalRate: Double? = null,
//    val angleStart: Double? = null,
//    val angleFinish: Double? = null,
//    val angleMax: Double? = null,
//    val warningPoint: Double? = null,
//    val criticalPoint: Double? = null
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

// TODO На будущее. Пока не заработало
//        id = id ?: throw ExceptionInInitializerError("id is null"),
//        type = type ?: ModelEvent.EventType.NONE,
//        meltId = meltId ?: "",
//        timeStart = timeStart ?: 0,
//        timeFinish = timeFinish ?: 0,
//        title = title ?: "",
//        textMessage = textMessage ?: "",
//        category = category ?: ModelEvent.Category.NONE,
//        isActive = isActive ?: true,
//        executionStatus = executionStatus ?: ModelEvent.ExecutionStatus.NONE,
//        metalRate = metalRate ?: 0.0,
//        angleStart = angleStart ?: 0.0,
//        angleFinish = angleFinish ?: 0.0,
//        angleMax = angleMax ?: 0.0,
//        warningPoint = warningPoint ?: 0.0,
//        criticalPoint = criticalPoint ?: 0.0
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
