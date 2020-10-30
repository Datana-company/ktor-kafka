package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartConverterMeltInfo(
    val id: String? = null,
    val timeStart: Long? = null,
    val meltNumber: String? = null,
    val steelGrade: String? = null,
    val crewNumber: String? = null,
    val shiftNumber: String? = null,
    val mode: WsDsmartConverterMeltInfo.Mode? = null,
    val devices: WsDsmartConverterMeltDevices? = null
) {
    @Serializable
    enum class Mode(val value: String){
        PROD("prod"),
        EMULATION("emulation"),
        TEST("test");
    }
}
