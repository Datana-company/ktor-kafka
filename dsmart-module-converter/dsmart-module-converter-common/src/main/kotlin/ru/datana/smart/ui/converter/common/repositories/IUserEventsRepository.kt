package ru.datana.smart.ui.converter.common.repositories

import ru.datana.smart.ui.converter.common.events.IBizEvent
import ru.datana.smart.ui.converter.common.events.IMetalRateEvent

interface IUserEventsRepository {
    fun put(meltId: String, event: IBizEvent)
    fun getActiveByMeltId(meltId: String): List<IBizEvent>
    fun getAllByMeltId(meltId: String): List<IBizEvent>
    fun getActiveMetalRateEventByMeltId(meltId: String): IMetalRateEvent?

    companion object {
        val NONE = object: IUserEventsRepository {
            override fun put(meltId: String, event: IBizEvent) {
                TODO("Not yet implemented")
            }

            override fun getActiveByMeltId(meltId: String): List<IBizEvent> {
                TODO("Not yet implemented")
            }

            override fun getAllByMeltId(meltId: String): List<IBizEvent> {
                TODO("Not yet implemented")
            }

            override fun getActiveMetalRateEventByMeltId(meltId: String): IMetalRateEvent? {
                TODO("Not yet implemented")
            }
        }
    }
}
