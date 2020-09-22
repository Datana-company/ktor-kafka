package ru.datana.smart.ui.temperature.ws.models

import kotlinx.serialization.*
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponse
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponseError
import ru.datana.smart.ui.temperature.common.Config

@Serializable
data class WsDsmartResponseAnalysis(
    override val data: WsDsmartAnalysis? = null,
    override val errors: List<IWsDsmartResponseError>? = null,
    override val event: String? = "temperature-analysis"
) : IWsDsmartResponse<WsDsmartAnalysis> {
    override val module: String? = Config.moduleName
}
