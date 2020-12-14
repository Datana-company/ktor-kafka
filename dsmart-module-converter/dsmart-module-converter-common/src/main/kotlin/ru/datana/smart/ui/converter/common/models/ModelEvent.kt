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
    var metalRate: Double = Double.MIN_VALUE,
    var angleStart: Double = Double.MIN_VALUE,
    var angleFinish: Double = Double.MIN_VALUE,
    var angleMax: Double = Double.MIN_VALUE,
    var warningPoint: Double = Double.MIN_VALUE,
    var criticalPoint: Double = Double.MIN_VALUE,
    var alertRuleId: String = "",
    var component: String = "",
    var timestamp: String = ""
) {
    enum class Category() {
        CRITICAL,
        WARNING,
        INFO,
        HINT,
        NONE
    }

    enum class ExecutionStatus() {
        COMPLETED,
        FAILED,
        NONE
    }

    enum class EventType() {
        METAL_RATE_WARNING_EVENT,
        METAL_RATE_INFO_EVENT,
        METAL_RATE_CRITICAL_EVENT,
        SUCCESS_MELT_EVENT,
        EXT_EVENT,
        NONE
    }
}
