package ru.datana.smart.ui.converter.repository.inmemory

import ru.datana.smart.ui.converter.common.models.ModelEvent
import java.time.Instant

data class EventInMemoryDto(
    val id: String? = null,
    val meltId: String? = null,
    val type: EventInMemoryTypes? = null,
    val timeStart: Long? = null,
    val timeFinish: Long? = null,
    val title: String? = null,
    val textMessage: String? = null,
    val category: EventInMemoryCategories? = null,
    val isActive: Boolean? = true,
    val executionStatus: EventInMemoryExecutionStatus? = null,
    val metalRate: Double? = null,
    val angleStart: Double? = null,
    val warningPoint: Double? = null,
    val criticalPoint: Double? = null
) {
    fun toModel(): ModelEvent = ModelEvent(
        id = id ?: throw ExceptionInInitializerError("id is null"),
        type = type?.let { ModelEvent.EventType.valueOf(it.name) } ?: ModelEvent.EventType.NONE,
        meltId = meltId ?: "",
        timeStart = timeStart?.let { Instant.ofEpochMilli(it) } ?: Instant.MIN,
        timeFinish = timeFinish?.let { Instant.ofEpochMilli(it) } ?: Instant.MAX,
        title = title ?: "",
        textMessage = textMessage ?: "",
        category = category?.let { ModelEvent.Category.valueOf(it.name) } ?: ModelEvent.Category.NONE,
        isActive = isActive ?: true,
        executionStatus = executionStatus?.let { ModelEvent.ExecutionStatus.valueOf(it.name) } ?: ModelEvent.ExecutionStatus.NONE,
        metalRate = metalRate ?: Double.MIN_VALUE,
        angleStart = angleStart ?: Double.MIN_VALUE,
        warningPoint = warningPoint ?: Double.MIN_VALUE,
        criticalPoint = criticalPoint ?: Double.MIN_VALUE
    )

    companion object {
        fun of(event: ModelEvent) = of(event, event.id)

        fun of(event: ModelEvent, id: String) = EventInMemoryDto(
            id = id.takeIf { it.isNotBlank() },
            meltId = event.meltId.takeIf { it.isNotBlank() },
            type = event.type.takeIf { it != ModelEvent.EventType.NONE }?.let { EventInMemoryTypes.valueOf(it.name) },
            timeStart = event.timeStart.takeIf { it != Instant.MIN }?.toEpochMilli(),
            timeFinish = event.timeFinish.takeIf { it != Instant.MAX }?.toEpochMilli(),
            title = event.title.takeIf { it.isNotBlank() },
            textMessage = event.textMessage.takeIf { it.isNotBlank() },
            category = event.category.takeIf { it != ModelEvent.Category.NONE }?.let { EventInMemoryCategories.valueOf(it.name) },
            isActive = event.isActive,
            executionStatus = EventInMemoryExecutionStatus.valueOf(event.executionStatus.name),
            metalRate = event.metalRate.takeIf { it != Double.MIN_VALUE },
            angleStart = event.angleStart.takeIf { it != Double.MIN_VALUE },
            warningPoint = event.warningPoint.takeIf { it != Double.MIN_VALUE },
            criticalPoint = event.criticalPoint.takeIf { it != Double.MIN_VALUE }
        )
    }
}
