package ru.datana.smart.ui.converter.common.models

data class ModelDevicesSelsyn(
    override val id: String? = null,
    override val name: String? = null,
    override val uri: String? = null,
    override val deviceType: String? = null,
    override val type: ModelDeviceType? = null
): ModelDevice
