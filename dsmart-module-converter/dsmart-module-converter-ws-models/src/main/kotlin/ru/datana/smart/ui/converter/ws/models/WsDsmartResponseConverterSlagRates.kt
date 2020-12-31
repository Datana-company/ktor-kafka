package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.*
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponse
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponseError
import ru.datana.smart.ui.converter.common.Config

@Serializable
@SerialName("converter-slag-rates-update")
data class WsDsmartResponseConverterSlagRates(
    override val data: WsDsmartConverterSlagRateList? = null,
    override val errors: List<IWsDsmartResponseError>? = null,
    override val event: String? = "converter-slag-rates-update"
) : IWsDsmartResponse<WsDsmartConverterSlagRateList> {
    override val module: String? = Config.moduleName
}
