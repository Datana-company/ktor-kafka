package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponse
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponseError
import ru.datana.smart.ui.converter.common.Config

@Serializable
@SerialName("temperature-update")
data class WsDsmartResponseTemperature (
    override val data: WsDsmartTemperature? = null,
    override val errors: List<IWsDsmartResponseError>? = null,
    override val event: String? = "temperature-update"
) : IWsDsmartResponse<WsDsmartTemperature> {
    override val module: String? = Config.moduleName
}
