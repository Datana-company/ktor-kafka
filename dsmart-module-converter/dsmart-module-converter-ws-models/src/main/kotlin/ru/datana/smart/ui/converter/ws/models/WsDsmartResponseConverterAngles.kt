package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponse
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponseError
import ru.datana.smart.ui.converter.common.Config

@Serializable
@SerialName("converter-angles-update")
data class WsDsmartResponseConverterAngles(
    override val data: WsDsmartConverterAngles? = null,
    override val errors: List<IWsDsmartResponseError>? = null,
    override val event: String? = "converter-angles-update"
) : IWsDsmartResponse<WsDsmartConverterAngles> {
    override val module: String? = Config.moduleName
}
