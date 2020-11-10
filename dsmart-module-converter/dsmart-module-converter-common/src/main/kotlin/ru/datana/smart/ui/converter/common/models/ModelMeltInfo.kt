package ru.datana.smart.ui.converter.common.models

data class ModelMeltInfo(
    val id: String = "",
    val timeStart: Long = Long.MIN_VALUE,
    val meltNumber: String = "",
    val steelGrade: String = "",
    val crewNumber: String = "",
    val shiftNumber: String = "",
    val mode: Mode = Mode.NONE,
    val devices: ModelMeltDevices = ModelMeltDevices.NONE
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
