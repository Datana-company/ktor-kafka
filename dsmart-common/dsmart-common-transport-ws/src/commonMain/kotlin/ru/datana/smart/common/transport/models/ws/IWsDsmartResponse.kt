package ru.datana.smart.common.transport.models.ws

interface IWsDsmartResponse<T> {
    val module: String?
    val event: String?
    val data: T?
    val status: WsDsmartResponseStatuses?
        get() = errors
            ?.mapNotNull { it.level }
            ?.any { it.level <= IWsDsmartResponseError.Levels.ERROR.level }
            ?.takeIf { it }
            ?.let { WsDsmartResponseStatuses.ERROR }
            ?: WsDsmartResponseStatuses.SUCCESS
    val errors: List<IWsDsmartResponseError>?
}
