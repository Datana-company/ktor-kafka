package ru.datana.smart.ui.converter.repository.inmemory.currentstate

import java.time.Instant


data class CurrentStateInMemoryDto(
    val id: String? = null,
    val meltInfo: CurrentStateInMemoryMeltInfo? = null,
    val lastAngles: CurrentStateInMemoryAngles? = null,
    val lastSlagRate: CurrentStateInMemorySlagRate? = null,
    val avgStreamRate: Double? = null,
    val lastTimeAngles: Instant? = null,
    val lastTimeFrame: Instant? = null
) {

}
