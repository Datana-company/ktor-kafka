package ru.datana.smart.ui.converter.repository.inmemory.currentstate

import java.time.Instant

data class CurrentStateInMemoryMeltInfo(
    val id: String? = null,
    val timeStart: Instant? = null,
    val meltNumber: String? = null,
    val steelGrade: String? = null,
    val crewNumber: String? = null,
    val shiftNumber: String? = null,
    val mode: Mode? = null,
    val devices: CurrentStateInMemoryMeltDevices? = null
) {
    enum class Mode {
        PROD,
        EMULATION,
        TEST,
        NONE
    }

}
