package ru.datana.smart.ui.converter.common.repositories

import ru.datana.smart.ui.converter.common.events.IBizEvent

interface IUserEventsRepository {
    fun put(event: IBizEvent)
    fun getActive(): List<IBizEvent>
    fun getAll(): List<IBizEvent>

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

        }
    }
}
