package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartConverterMeltDevices (
    val converter: WsDsmartConverterDevicesConverter? = null,
    val irCamera: WsDsmartConverterDevicesIrCamera? = null,
    val selsyn: WsDsmartConverterDevicesSelsyn? = null,
    val slagRate: WsDsmartConverterDevicesSlagRate? = null
)
