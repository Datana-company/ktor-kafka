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
    var category: Category = Category.INFO,
    var isActive: Boolean = true,
    var executionStatus: ExecutionStatus = ExecutionStatus.NONE,
    var metalRate: Double = Double.MIN_VALUE,
    var angleStart: Double = Double.MIN_VALUE,
    var warningPoint: Double = Double.MIN_VALUE,
    var criticalPoint: Double = Double.MIN_VALUE
) {
    enum class Category(val value: String) {
        CRITICAL("CRITICAL"),
        WARNING("WARNING"),
        INFO("INFO"),
        HINT("HINT"),
        NONE("NONE")
    }

    enum class ExecutionStatus(val value: String) {
        COMPLETED("COMPLETED"),
        FAILED("FAILED"),
        NONE("NONE")
    }

    enum class EventType(val value: String) {
        METAL_RATE_WARNING_EVENT("METAL_RATE_WARNING_EVENT"),
        METAL_RATE_INFO_EVENT("METAL_RATE_INFO_EVENT"),
        METAL_RATE_CRITICAL_EVENT("METAL_RATE_CRITICAL_EVENT"),
        SUCCESS_MELT_EVENT("SUCCESS_MELT_EVENT"),
        END_MELT_EVENT("END_MELT_EVENT"),
        EXT_EVENT("EXT_EVENT"),
        NONE("NONE")
    }
}
