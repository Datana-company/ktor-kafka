package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.*
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponse
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponseError
import ru.datana.smart.ui.converter.common.Config

@Serializable
@SerialName("converter-update")
data class WsDsmartResponseConverter(
    override val data: WsDsmartConverter? = null,
    override val errors: List<IWsDsmartResponseError>? = null,
    override val event: String? = "converter-update"
) : IWsDsmartResponse<WsDsmartConverter> {
    override val module: String? = Config.moduleName
}
