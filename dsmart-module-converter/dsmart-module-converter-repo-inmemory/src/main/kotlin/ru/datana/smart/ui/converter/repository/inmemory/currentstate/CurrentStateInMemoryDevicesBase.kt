package ru.datana.smart.ui.converter.repository.inmemory.currentstate

import ru.datana.smart.ui.converter.common.models.ModelDevice
import ru.datana.smart.ui.converter.common.models.ModelDeviceType

abstract class CurrentStateInMemoryDevicesBase(
    open val id: String? = null,
    open val name: String? = null,
    open val uri: String? = null,
    open val deviceType: String? = null,
    open val type: CurrentStateInMemoryDeviceType? = null
) {

    inline fun <reified R: ModelDevice> toModelBase():R {
        val model = object : ModelDevice{
            override var id = this@CurrentStateInMemoryDevicesBase.id?: ""
            override var name = this@CurrentStateInMemoryDevicesBase.name?: ""
            override var uri = this@CurrentStateInMemoryDevicesBase.uri?: ""
            override var deviceType = this@CurrentStateInMemoryDevicesBase.deviceType?: ""
            override var type = this@CurrentStateInMemoryDevicesBase.type?.let { ModelDeviceType.valueOf(it.name) }?: ModelDeviceType.NONE
        }
        return model as R
    }

}
