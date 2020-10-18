package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponse
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponseError
import ru.datana.smart.ui.converter.common.Config

@Serializable
@SerialName("converter-video-update")
data class WsDsmartResponseConverterVi(
    override val data: WsDsmartConverterVi? = null,
    override val errors: List<IWsDsmartResponseError>? = null,
    override val event: String? = "converter-video-update"
) : IWsDsmartResponse<WsDsmartConverterVi> {
    override val module: String? = Config.moduleName
}
