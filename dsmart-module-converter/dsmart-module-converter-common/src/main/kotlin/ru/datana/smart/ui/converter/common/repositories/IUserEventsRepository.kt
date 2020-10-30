package ru.datana.smart.ui.converter.common.repositories

import ru.datana.smart.ui.converter.common.events.IBizEvent
import ru.datana.smart.ui.converter.common.events.IMetalRateEvent

interface IUserEventsRepository {
    fun put(event: IBizEvent)
    fun getActive(): List<IBizEvent>
    fun getAll(): List<IBizEvent>
    fun getActiveMetalRateEvent(): IMetalRateEvent?

    companion object {
        val NONE = object: IUserEventsRepository {
            override fun put(event: IBizEvent) {
                TODO("Not yet implemented")
            }

            override fun getActive(): List<IBizEvent> {
                TODO("Not yet implemented")
            }

            override fun getAll(): List<IBizEvent> {
                TODO("Not yet implemented")
            }

            override fun getActiveMetalRateEvent(): IMetalRateEvent? {
                TODO("Not yet implemented")
            }

        }
    }
}
