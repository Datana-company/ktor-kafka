package ru.datana.smart.ui.converter.angle.app.models

import java.lang.Math.abs

class AngleSchedule (
    val items: MutableList<AngleMessage> = mutableListOf()
) {
    fun clean() = items.clear()

    fun addAll(messages: Collection<AngleMessage>) = items.addAll(messages)

    fun add(vararg messages: AngleMessage) = items.addAll(messages)

    fun getClosest(timeOffset: Long): AngleMessage? = items.minByOrNull {
        abs(it.timeShift?.let { ts -> ts - timeOffset } ?: Long.MAX_VALUE)
    }


    companion object {
        val NONE = AngleSchedule()
    }
}

data class AngleMessage (
    val angle: Double? = null,
    val timeShift: Long? = null
) {
    companion object {
        val NONE = AngleMessage()
    }
}
