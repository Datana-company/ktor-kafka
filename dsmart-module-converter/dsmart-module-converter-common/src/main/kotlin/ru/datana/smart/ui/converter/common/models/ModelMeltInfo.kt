package ru.datana.smart.ui.converter.common.models

data class ModelMeltInfo(
    val id: String? = null,
    val timeStart: Long? = null,
    val meltNumber: String? = null,
    val steelGrade: String? = null,
    val crewNumber: String? = null,
    val shiftNumber: String? = null,
    val mode: Mode? = null,
    val devices: ModelMeltDevices? = null
) {

    enum class Mode(val value: String) {
        PROD("prod"),
        EMULATION("emulation"),
        TEST("test");
    }

    companion object {
        val NONE = ModelMeltInfo()
    }
}
