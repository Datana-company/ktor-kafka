package ru.datana.smart.ui.converter.common.models

import java.time.Instant

data class ModelMeltInfo(
    var id: String = "",
    var timeStart: Instant = Instant.MIN,
    var meltNumber: String = "",
    var steelGrade: String = "",
    var crewNumber: String = "",
    var shiftNumber: String = "",
    var mode: Mode = Mode.NONE,
    var devices: ModelMeltDevices = ModelMeltDevices.NONE
) {

    enum class Mode(val value: String) {
        PROD("prod"),
        EMULATION("emulation"),
        TEST("test"),
        NONE("none")
    }

    companion object {
        val NONE = ModelMeltInfo()
    }
}
