package ru.datana.smart.common.transport.models.ws

data class WsDsmartResponseError(
    override val code: String?,
    override val group: String?,
    override val field: String?,
    override val level: IWsDsmartResponseError.Levels?
): IWsDsmartResponseError {
}
