package ru.datana.smart.ui.converter.common.extensions

import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.utils.toPercent
import java.time.Instant
import java.util.*

/**
 *  Функции-расширения контекста конвертера для создания событий
 */

fun ConverterBeContext.eventExternalReceived():ModelEvent = ModelEvent(
    id = UUID.randomUUID().toString(),
    meltId = this.meltInfo.id,
    type = ModelEvent.EventType.EXT_EVENT,
    timeStart = Instant.now(),
    timeFinish = Instant.now(),
    textMessage = this.extEvents.message ?: "",
    category = when (this.extEvents.level) {
        "INFO" -> {
            ModelEvent.Category.INFO
        }
        "WARNING" -> {
            ModelEvent.Category.WARNING
        }
        "CRITICAL" -> {
            ModelEvent.Category.CRITICAL
        }
        else -> {
            ModelEvent.Category.INFO
        }
    }
)

fun ConverterBeContext.eventSlagInfoReached():ModelEvent = this.eventInfo().also {
    it.slagRate = this.avgSlagRate
    it.textMessage = """
                     Достигнут предел потерь шлака в потоке – ${this.avgSlagRate.toPercent()}%.
                     """.trimIndent()
}

fun ConverterBeContext.eventMetalInfoReached():ModelEvent = this.eventInfo().also {
    it.metalRate = this.avgSteelRate
    it.textMessage = """
                     Достигнут предел потерь металла в потоке – ${this.avgSteelRate.toPercent()}%.
                     """.trimIndent()
}

fun ConverterBeContext.eventSlagWarningReached():ModelEvent = this.eventWarning().also {
    it.slagRate = this.avgSlagRate
    it.textMessage = """
                    В потоке детектирован шлак – ${this.avgSlagRate.toPercent()}% сверх допустимой нормы ${this.streamRateWarningPoint.toPercent()}%. Верните конвертер в вертикальное положение.
                    """.trimIndent()
}

fun ConverterBeContext.eventMetalWarningReached():ModelEvent = this.eventWarning().also {
    it.metalRate = this.avgSteelRate
    it.textMessage = """
                    В потоке детектирован металл – ${this.avgSteelRate.toPercent()}% сверх допустимой нормы ${this.streamRateWarningPoint.toPercent()}%. Верните конвертер в вертикальное положение.
                    """.trimIndent()
}

fun ConverterBeContext.eventSlagCriticalReached():ModelEvent = this.eventCritical().also {
    it.slagRate = this.avgSlagRate
    it.textMessage = """
                    В потоке детектирован шлак – ${this.avgSlagRate.toPercent()}%, процент потерь превышает критическое значение – ${this.streamRateCriticalPoint.toPercent()}%. Верните конвертер в вертикальное положение!
                    """.trimIndent()
}

fun ConverterBeContext.eventMetalCriticalReached():ModelEvent = this.eventCritical().also {
    it.metalRate = this.avgSteelRate
    it.textMessage = """
                    В потоке детектирован металл – ${this.avgSteelRate.toPercent()}%, процент потерь превышает критическое значение – ${this.streamRateCriticalPoint.toPercent()}%. Верните конвертер в вертикальное положение!
                    """.trimIndent()
}

fun ConverterBeContext.eventSlagSuccessReached():ModelEvent = this.eventSuccess().also {
    it.textMessage = """
                    Допустимая норма потерь шлака ${this.streamRateWarningPoint.toPercent()}% не была превышена.
                    """.trimIndent()
}

fun ConverterBeContext.eventMetalSuccessReached():ModelEvent = this.eventSuccess().also {
    it.textMessage = """
                    Допустимая норма потерь металла ${this.streamRateWarningPoint.toPercent()}% не была превышена.
                    """.trimIndent()
}

private fun ConverterBeContext.eventInfo():ModelEvent = this.eventBase().also {
    it.type = ModelEvent.EventType.STREAM_RATE_INFO_EVENT
    it.title = "Информация"
    it.category = ModelEvent.Category.INFO
}

private fun ConverterBeContext.eventWarning():ModelEvent = this.eventBase().also {
    it.type = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT
    it.title = "Предупреждение"
    it.category = ModelEvent.Category.WARNING
}

private fun ConverterBeContext.eventCritical():ModelEvent = this.eventBase().also {
    it.type = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT
    it.title = "Критическая ситуация"
    it.category = ModelEvent.Category.CRITICAL
}

private fun ConverterBeContext.eventSuccess():ModelEvent = this.eventBase().also {
    it.type = ModelEvent.EventType.SUCCESS_MELT_EVENT
    it.isActive = false
    it.title = "Информация"
    it.category = ModelEvent.Category.INFO
}



private fun ConverterBeContext.eventBase():ModelEvent = ModelEvent(
    id = UUID.randomUUID().toString(),
    meltId = this.meltInfo.id,
    timeStart = this.timeStart,
    timeFinish = this.timeStart,
    angleStart = this.currentAngle
)
