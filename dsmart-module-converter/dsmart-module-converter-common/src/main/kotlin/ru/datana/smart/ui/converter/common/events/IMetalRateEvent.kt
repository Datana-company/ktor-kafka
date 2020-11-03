package ru.datana.smart.ui.converter.common.events

interface IMetalRateEvent: IBizEvent {
    val metalRate: Double
    val angleStart: Double?
    val angleFinish: Double?
    val angleMax: Double?
}
