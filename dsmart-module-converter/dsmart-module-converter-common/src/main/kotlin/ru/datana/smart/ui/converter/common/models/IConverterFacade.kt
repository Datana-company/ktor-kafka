package ru.datana.smart.ui.converter.common.models

import ru.datana.smart.ui.converter.common.context.ConverterBeContext

interface IConverterFacade {

    suspend fun handleMath(context: ConverterBeContext)

    suspend fun handleAngles(context: ConverterBeContext)

    suspend fun handleFrame(context: ConverterBeContext)

    suspend fun handleMeltInfo(context: ConverterBeContext)

    suspend fun handleSteelEvents(context: ConverterBeContext)

    suspend fun handleSlagEvents(context: ConverterBeContext)

    suspend fun handleExtEvents(context: ConverterBeContext)

    companion object {
        val NONE = object: IConverterFacade {
            override suspend fun handleMath(context: ConverterBeContext) {
                TODO("Not yet implemented")
            }

            override suspend fun handleAngles(context: ConverterBeContext) {
                TODO("Not yet implemented")
            }

            override suspend fun handleFrame(context: ConverterBeContext) {
                TODO("Not yet implemented")
            }

            override suspend fun handleMeltInfo(context: ConverterBeContext) {
                TODO("Not yet implemented")
            }

            override suspend fun handleSteelEvents(context: ConverterBeContext) {
                TODO("Not yet implemented")
            }

            override suspend fun handleSlagEvents(context: ConverterBeContext) {
                TODO("Not yet implemented")
            }

            override suspend fun handleExtEvents(context: ConverterBeContext) {
                TODO("Not yet implemented")
            }
        }
    }
}
