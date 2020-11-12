package ru.datana.smart.ui.converter.common.models

import ru.datana.smart.ui.converter.common.context.ConverterBeContext

interface IWsSignalerManager {

    suspend fun sendSignaler(context: ConverterBeContext)

    companion object {
        val NONE = object : IWsSignalerManager {
            override suspend fun sendSignaler(context: ConverterBeContext) {
                TODO("Not yet implemented")
            }
        }
    }
}
