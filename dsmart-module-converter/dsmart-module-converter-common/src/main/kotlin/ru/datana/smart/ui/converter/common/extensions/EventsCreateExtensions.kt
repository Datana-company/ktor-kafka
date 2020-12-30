package ru.datana.smart.ui.converter.common.extensions

import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.utils.toPercent

/**
 *  Функции-расширения контекста конвертера для создания событий
 */

suspend fun ConverterBeContext.eventExternalReceived(setActive: Boolean = true):ModelEvent {
    val event = ModelEvent(
        meltId = meltInfo.id,
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

suspend fun ConverterBeContext.eventSlagInfoReached(setActive: Boolean = true) = this.setEventReached(
    setActive = setActive,
    message = """
                   Достигнут предел потерь шлака в потоке – ${currentStateRepository.lastAvgSlagRate(converterId).toPercent()}%.
                  """.trimIndent()){
    this.eventInfo()
}

suspend fun ConverterBeContext.eventSteelInfoReached(setActive: Boolean = true) = this.setEventReached(
        setActive = setActive,
        message = """
                   Достигнут предел потерь металла в потоке – ${currentStateRepository.lastAvgSteelRate(converterId).toPercent()}%.
                  """.trimIndent()){
             this.eventInfo()
    }

suspend fun ConverterBeContext.eventSlagWarningReached(setActive: Boolean = true) = this.setEventReached(
    setActive = setActive,
    message = """
              В потоке детектирован шлак – ${currentStateRepository.lastAvgSlagRate(converterId).toPercent()}% сверх допустимой нормы ${this.streamRateWarningPoint.toPercent()}%. Верните конвертер в вертикальное положение.
               """.trimIndent()){
    this.eventWarning()
}

suspend fun ConverterBeContext.eventSteelWarningReached(setActive: Boolean = true) = this.setEventReached(
    setActive = setActive,
    message = """
              В потоке детектирован металл – ${currentStateRepository.lastAvgSteelRate(converterId).toPercent()}% сверх допустимой нормы ${this.streamRateWarningPoint.toPercent()}%. Верните конвертер в вертикальное положение.
               """.trimIndent()){
    this.eventWarning()
}

suspend fun ConverterBeContext.eventSlagCriticalReached(setActive: Boolean = true) = this.setEventReached(
    setActive = setActive,
    message = """
              В потоке детектирован шлак – ${currentStateRepository.lastAvgSlagRate(converterId).toPercent()}%, процент потерь превышает критическое значение – ${this.streamRateCriticalPoint.toPercent()}%. Верните конвертер в вертикальное положение!
               """.trimIndent()){
    this.eventCritical()
}

suspend fun ConverterBeContext.eventSteelCriticalReached(setActive: Boolean = true) = this.setEventReached(
    setActive = setActive,
    message = """
              В потоке детектирован металл – ${currentStateRepository.lastAvgSteelRate(converterId).toPercent()}%, процент потерь превышает критическое значение – ${this.streamRateCriticalPoint.toPercent()}%. Верните конвертер в вертикальное положение!
               """.trimIndent()){
    this.eventCritical()
}

suspend fun ConverterBeContext.eventSlagSuccessReached(setActive: Boolean = true) = this.setEventReached(
    setActive = setActive,
    message = """
              Допустимая норма потерь шлака ${this.streamRateWarningPoint.toPercent()}% не была превышена.
               """.trimIndent()){
    this.eventSuccess()
}

suspend fun ConverterBeContext.eventSteelSuccessReached(setActive: Boolean = true) = this.setEventReached(
    setActive = setActive,
    message = """
              Допустимая норма потерь металла ${this.streamRateWarningPoint.toPercent()}% не была превышена.
               """.trimIndent()){
    this.eventSuccess()
}

private suspend inline fun ConverterBeContext.setEventReached(
    setActive: Boolean,
    message: String,
    crossinline eventBlock: suspend ConverterBeContext.() -> ModelEvent): ModelEvent {
    val event = eventBlock().also {
        it.textMessage = message
    }
    if (setActive) this.activeEvent = event
    return event
}

private suspend fun ConverterBeContext.eventInfo():ModelEvent = this.eventBase().also { model ->
    model.type = ModelEvent.EventType.STREAM_RATE_INFO_EVENT
    model.title = ModelEvent.Category.INFO.title
    model.category = ModelEvent.Category.INFO
    model.angleStart = currentStateRepository.currentAngle(converterId)
}

private suspend fun ConverterBeContext.eventWarning():ModelEvent = this.eventBase().also { model ->
    model.type = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT
    model.title = ModelEvent.Category.WARNING.title
    model.category = ModelEvent.Category.WARNING
    model.angleStart = currentStateRepository.currentAngle(converterId)
}

private suspend fun ConverterBeContext.eventCritical():ModelEvent = this.eventBase().also { model ->
    model.type = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT
    model.title = ModelEvent.Category.CRITICAL.title
    model.category = ModelEvent.Category.CRITICAL
    model.angleStart = currentStateRepository.currentAngle(converterId)
}

private suspend fun ConverterBeContext.eventSuccess():ModelEvent = this.eventBase().also { model ->
    model.type = ModelEvent.EventType.SUCCESS_MELT_EVENT
    model.isActive = false
    model.title = ModelEvent.Category.INFO.title
    model.category = ModelEvent.Category.INFO
}



private suspend fun ConverterBeContext.eventBase():ModelEvent = ModelEvent(
    meltId = meltInfo.id,
    timeStart = this.timeStart,
    timeFinish = this.timeStart
)
