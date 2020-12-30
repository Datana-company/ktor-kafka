package ru.datana.smart.ui.converter.repository.inmemory.currentstate

import ru.datana.smart.ui.converter.common.models.ModelDeviceType
import ru.datana.smart.ui.converter.common.models.ModelDevicesIrCamera

data class CurrentStateInMemoryDevicesIrCamera(
    override val id: String? = null,
    override val name: String? = null,
    override val uri: String? = null,
    override val deviceType: String? = null,
    override val type: CurrentStateInMemoryDeviceType? = null
): CurrentStateInMemoryDevicesBase(
    id = id,
    name = name,
    uri = uri,
    deviceType = deviceType,
    type = type
) {

    fun toModel() = ModelDevicesIrCamera(
        id = id?: "",
        name = name?: "",
        uri = uri?: "",
        deviceType = deviceType?: "",
        type = type?.let { ModelDeviceType.valueOf(it.name) }?: ModelDeviceType.NONE
    )

    companion object {
        fun of(model: ModelDevicesIrCamera) = CurrentStateInMemoryDevicesIrCamera(
            id = model.id.takeIf { it.isNotBlank() },
            name = model.name.takeIf { it.isNotBlank() },
            uri = model.uri.takeIf { it.isNotBlank() },
            deviceType = model.deviceType.takeIf { it.isNotBlank() },
            type = model.type.takeIf { it != ModelDeviceType.NONE }?.let { CurrentStateInMemoryDeviceType.valueOf(it.name) }
        )
    }

}
