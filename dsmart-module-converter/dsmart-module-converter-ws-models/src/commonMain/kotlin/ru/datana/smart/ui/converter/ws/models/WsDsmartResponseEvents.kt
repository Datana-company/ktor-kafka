package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponse
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponseError
import ru.datana.smart.ui.converter.common.Config

@Serializable
@SerialName("events-update")
data class WsDsmartResponseEvents(
    override val data: WsDsmartEventList? = null,
    override val errors: List<IWsDsmartResponseError>? = null,
    override val event: String? = "events-update"
) : IWsDsmartResponse<WsDsmartEventList> {
    override val module: String? = Config.moduleName
}
