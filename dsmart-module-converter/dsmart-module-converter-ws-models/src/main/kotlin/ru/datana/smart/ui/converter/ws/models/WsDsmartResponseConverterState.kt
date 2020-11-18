package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponse
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponseError
import ru.datana.smart.ui.converter.common.Config

@Serializable
@SerialName("converter-state-update")
data class WsDsmartResponseConverterState(
    override val data: WsDsmartConverterState? = null,
    override val errors: List<IWsDsmartResponseError>? = null,
    override val event: String? = "converter-state-update"
) : IWsDsmartResponse<WsDsmartConverterState> {
    override val module: String? = Config.moduleName
}
