package ru.datana.smart.ui.converter.common.repositories

import ru.datana.smart.ui.converter.common.models.EventModel

interface IUserEventsRepository {
    fun put(meltId: String, event: EventModel)
    fun getActiveByMeltId(meltId: String): List<EventModel>
    fun getAllByMeltId(meltId: String): List<EventModel>
//    fun getActiveMetalRateEventByMeltId(meltId: String): IMetalRateEvent?
    fun getActiveMetalRateInfoEventByMeltId(meltId: String): EventModel?

    companion object {
        val NONE = object: IUserEventsRepository {
            override fun put(meltId: String, event: EventModel) {
                TODO("Not yet implemented")
            }

            override fun getActiveByMeltId(meltId: String): List<EventModel> {
                TODO("Not yet implemented")
            }

            override fun getAllByMeltId(meltId: String): List<EventModel> {
                TODO("Not yet implemented")
            }

            override fun getActiveMetalRateInfoEventByMeltId(meltId: String): EventModel? {
                TODO("Not yet implemented")
            }

//            override fun getActiveMetalRateEventByMeltId(meltId: String): IMetalRateEvent? {
//                TODO("Not yet implemented")
//            }
        }
    }
}
