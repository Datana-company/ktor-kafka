package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponse
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponseError
import ru.datana.smart.ui.converter.common.Config

@Serializable
@SerialName("converter-stream-status-update")
data class WsDsmartResponseConverterStreamStatus(
    override val data: WsDsmartConverterStreamStatus? = null,
    override val errors: List<IWsDsmartResponseError>? = null,
    override val event: String? = "converter-stream-status-update"
) : IWsDsmartResponse<WsDsmartConverterStreamStatus> {
    override val module: String? = Config.moduleName
}
