package ru.datana.smart.ui.converter.common.models

import ru.datana.smart.ui.converter.common.context.ConverterBeContext

interface IWsManager {

    suspend fun sendFinish(context: ConverterBeContext)

    suspend fun sendAngles(context: ConverterBeContext)

    suspend fun sendMeltInfo(context: ConverterBeContext)

    suspend fun sendSlagRates(context: ConverterBeContext)

    suspend fun sendFrames(context: ConverterBeContext)

    suspend fun sendEvents(context: ConverterBeContext)

    companion object {
        val NONE = object: IWsManager {
            override suspend fun sendFinish(context: ConverterBeContext) {
                TODO("Not yet implemented")
            }

            override suspend fun sendAngles(context: ConverterBeContext) {
                TODO("Not yet implemented")
            }

            override suspend fun sendMeltInfo(context: ConverterBeContext) {
                TODO("Not yet implemented")
            }

            override suspend fun sendSlagRates(context: ConverterBeContext) {
                TODO("Not yet implemented")
            }

            override suspend fun sendFrames(context: ConverterBeContext) {
                TODO("Not yet implemented")
            }

            override suspend fun sendEvents(context: ConverterBeContext) {
                TODO("Not yet implemented")
            }
        }
    }
}
