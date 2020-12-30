package ru.datana.smart.ui.converter.common.extensions

import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.utils.toPercent
import java.time.Instant
import java.util.*

/**
 *  Функции-расширения контекста конвертера для создания событий
 */

fun ConverterBeContext.eventExternalReceived(setActive: Boolean = true):ModelEvent {
    val event = ModelEvent(
        meltId = currentMeltId,
        type = ModelEvent.EventType.EXTERNAL_EVENT,
        timeStart = timeStart,
        timeFinish = timeStart,
        title = ModelEvent.Category.INFO.title,
        textMessage = externalEvent.textMessage,
        alertRuleId = externalEvent.alertRuleId,
        containerId = externalEvent.containerId,
        component = externalEvent.component,
        timestamp = externalEvent.timestamp,
        level = externalEvent.level,
        loggerName = externalEvent.loggerName,
        category = ModelEvent.Category.INFO
    )
    if (setActive) this.activeEvent = event
    return event
}

fun ConverterBeContext.eventSlagInfoReached(setActive: Boolean = true) = this.setEventReached(
    setActive = setActive,
    message = """
                   Достигнут предел потерь шлака в потоке – ${this.avgStreamRate.toPercent()}%.
                  """.trimIndent()){
    this.eventInfo()
}

fun ConverterBeContext.eventSteelInfoReached(setActive: Boolean = true) = this.setEventReached(
        setActive = setActive,
        message = """
                   Достигнут предел потерь металла в потоке – ${this.avgStreamRate.toPercent()}%.
                  """.trimIndent()){
             this.eventInfo()
    }

fun ConverterBeContext.eventSlagWarningReached(setActive: Boolean = true) = this.setEventReached(
    setActive = setActive,
    message = """
              В потоке детектирован шлак – ${this.avgStreamRate.toPercent()}% сверх допустимой нормы ${this.streamRateWarningPoint.toPercent()}%. Верните конвертер в вертикальное положение.
               """.trimIndent()){
    this.eventWarning()
}

fun ConverterBeContext.eventSteelWarningReached(setActive: Boolean = true) = this.setEventReached(
    setActive = setActive,
    message = """
              В потоке детектирован металл – ${this.avgStreamRate.toPercent()}% сверх допустимой нормы ${this.streamRateWarningPoint.toPercent()}%. Верните конвертер в вертикальное положение.
               """.trimIndent()){
    this.eventWarning()
}

fun ConverterBeContext.eventSlagCriticalReached(setActive: Boolean = true) = this.setEventReached(
    setActive = setActive,
    message = """
              В потоке детектирован шлак – ${this.avgStreamRate.toPercent()}%, процент потерь превышает критическое значение – ${this.streamRateCriticalPoint.toPercent()}%. Верните конвертер в вертикальное положение!
               """.trimIndent()){
    this.eventCritical()
}

fun ConverterBeContext.eventSteelCriticalReached(setActive: Boolean = true) = this.setEventReached(
    setActive = setActive,
    message = """
              В потоке детектирован металл – ${this.avgStreamRate.toPercent()}%, процент потерь превышает критическое значение – ${this.streamRateCriticalPoint.toPercent()}%. Верните конвертер в вертикальное положение!
               """.trimIndent()){
    this.eventCritical()
}

fun ConverterBeContext.eventSlagSuccessReached(setActive: Boolean = true) = this.setEventReached(
    setActive = setActive,
    message = """
              Допустимая норма потерь шлака ${this.streamRateWarningPoint.toPercent()}% не была превышена.
               """.trimIndent()){
    this.eventSuccess()
}

fun ConverterBeContext.eventSteelSuccessReached(setActive: Boolean = true) = this.setEventReached(
    setActive = setActive,
    message = """
              Допустимая норма потерь металла ${this.streamRateWarningPoint.toPercent()}% не была превышена.
               """.trimIndent()){
    this.eventSuccess()
}

private inline fun ConverterBeContext.setEventReached(
    setActive: Boolean,
    message: String,
    crossinline eventBlock: ConverterBeContext.() -> ModelEvent): ModelEvent {
    val event = eventBlock().also {
        it.textMessage = message
    }
    if (setActive) this.activeEvent = event
    return event
}

private fun ConverterBeContext.eventInfo():ModelEvent = this.eventBase().also { model ->
    model.type = ModelEvent.EventType.STREAM_RATE_INFO_EVENT
    model.title = ModelEvent.Category.INFO.title
    model.category = ModelEvent.Category.INFO
    model.angleStart = this.currentAngle
}

private fun ConverterBeContext.eventWarning():ModelEvent = this.eventBase().also { model ->
    model.type = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT
    model.title = ModelEvent.Category.WARNING.title
    model.category = ModelEvent.Category.WARNING
    model.angleStart = this.currentAngle
}

private fun ConverterBeContext.eventCritical():ModelEvent = this.eventBase().also { model ->
    model.type = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT
    model.title = ModelEvent.Category.CRITICAL.title
    model.category = ModelEvent.Category.CRITICAL
    model.angleStart = this.currentAngle
}

private fun ConverterBeContext.eventSuccess():ModelEvent = this.eventBase().also { model ->
    model.type = ModelEvent.EventType.SUCCESS_MELT_EVENT
    model.isActive = false
    model.title = ModelEvent.Category.INFO.title
    model.category = ModelEvent.Category.INFO
}



private fun ConverterBeContext.eventBase():ModelEvent = ModelEvent(
    meltId = this.currentMeltId,
    timeStart = this.timeStart,
    timeFinish = this.timeStart
)
