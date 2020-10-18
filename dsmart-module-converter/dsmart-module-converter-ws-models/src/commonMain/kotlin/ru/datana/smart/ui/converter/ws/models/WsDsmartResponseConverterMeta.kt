package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponse
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponseError
import ru.datana.smart.ui.converter.common.Config

@Serializable
@SerialName("converter-meta-update")
data class WsDsmartResponseConverterMeta(
    override val data: WsDsmartConverterMeltInfo? = null,
    override val errors: List<IWsDsmartResponseError>? = null,
    override val event: String? = "converter-meta-update"
) : IWsDsmartResponse<WsDsmartConverterMeltInfo> {
    override val module: String? = Config.moduleName
}
