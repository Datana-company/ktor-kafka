package ru.datana.smart.ui.temperature.ws.models

import kotlinx.serialization.*
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponse
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponseError
import ru.datana.smart.ui.temperature.common.Config

@Serializable
@SerialName("temperature-update")
data class WsDsmartResponseTemperature(
    override val data: WsDsmartTemperatures? = null,
    override val errors: List<IWsDsmartResponseError>? = null,
    override val event: String? = "temperature-update"
) : IWsDsmartResponse<WsDsmartTemperatures> {
    override val module: String? = Config.moduleName
}
