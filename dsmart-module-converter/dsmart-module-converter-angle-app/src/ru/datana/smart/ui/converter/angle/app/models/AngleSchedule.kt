package ru.datana.smart.ui.converter.angle.app.models

data class AngleSchedule (
    val items: MutableList<AngleMessage>? = null
)


data class AngleMessage (
    val angle: Double? = null,
    val timeShift: Long? = null
)
