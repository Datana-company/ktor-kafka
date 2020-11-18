package ru.datana.smart.ui.converter.common.models

import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import java.io.Closeable

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
