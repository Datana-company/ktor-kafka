package ru.datana.smart.ui.converter.repository.inmemory.currentstate

import java.time.Instant

data class CurrentStateInMemoryAngles(
    val angleTime: Instant? = null,
    val angle: Double? = null,
    val source: Double? = null
) {

}
