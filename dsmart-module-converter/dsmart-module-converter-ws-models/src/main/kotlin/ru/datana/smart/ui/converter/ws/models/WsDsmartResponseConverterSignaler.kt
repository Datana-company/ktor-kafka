package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponse
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponseError
import ru.datana.smart.ui.converter.common.Config

@Serializable
@SerialName("signaler-update")
data class WsDsmartResponseConverterSignaler(
    override val data: WsDsmartConverterSignaler? = null,
    override val errors: List<IWsDsmartResponseError>? = null,
    override val event: String? = "signaler-update"
) : IWsDsmartResponse<WsDsmartConverterSignaler> {
    override val module: String? = Config.moduleName
}
