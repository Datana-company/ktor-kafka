package ru.datana.smart.ui.converter.common.models

data class ModelMeltInfo(
    var id: String = "",
    var timeStart: Long = Long.MIN_VALUE,
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
