package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartConverterDevicesIrCamera (
    override val id: String? = null,
    override val name: String? = null,
    override val uri: String? = null,
    override val deviceType: String? = null,
    override val type: WsDsmartConverterDeviceType? = null
): WsDsmartConverterDevice
