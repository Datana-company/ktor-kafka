package ru.datana.smart.ui.converter.common.models

import java.time.Instant

data class ModelEvent(
    var id: String = "",
    var meltId: String = "",
    var type: EventType = EventType.NONE,
    var timeStart: Instant = Instant.MIN,
    var timeFinish: Instant = Instant.MAX,
    var title: String = "",
    var textMessage: String = "",
    var category: Category = Category.NONE,
    var isActive: Boolean = true,
    var executionStatus: ExecutionStatus = ExecutionStatus.NONE,
    var angleStart: Double = Double.MIN_VALUE,
    var alertRuleId: String = "",
    var containerId: String = "",
    var component: String = "",
    var timestamp: String = "",
    var level: String = "",
    var loggerName: String = ""
) {
    companion object {
        val NONE = ModelEvent()
    }

    enum class Category {
        CRITICAL,
        WARNING,
        INFO,
        NONE
    }

    enum class ExecutionStatus {
        COMPLETED,
        FAILED,
        STATELESS,
        NONE
    }

    enum class EventType {
        STREAM_RATE_WARNING_EVENT,
        STREAM_RATE_INFO_EVENT,
        STREAM_RATE_CRITICAL_EVENT,
        SUCCESS_MELT_EVENT,
        EXT_EVENT,
        NONE
    }
}
