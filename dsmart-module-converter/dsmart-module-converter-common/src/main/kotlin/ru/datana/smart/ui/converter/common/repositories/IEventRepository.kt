package ru.datana.smart.ui.converter.common.repositories

import ru.datana.smart.ui.converter.common.models.ModelEvent

interface IEventRepository {
    suspend fun get(id: String): ModelEvent?
    suspend fun create(event: ModelEvent): ModelEvent?
    suspend fun update(event: ModelEvent): ModelEvent?
    fun getAllByMeltId(meltId: String): MutableList<ModelEvent>
    fun getActiveByMeltId(meltId: String): ModelEvent?
    fun getActiveByMeltIdAndEventType(meltId: String, eventType: ModelEvent.EventType): ModelEvent?

    companion object {
        val NONE = object : IEventRepository {
            override suspend fun get(id: String): ModelEvent? {
                TODO("Not yet implemented")
            }

            override suspend fun create(event: ModelEvent): ModelEvent? {
                TODO("Not yet implemented")
            }

            override suspend fun update(event: ModelEvent): ModelEvent? {
                TODO("Not yet implemented")
            }

            override fun getAllByMeltId(meltId: String): MutableList<ModelEvent> {
                TODO("Not yet implemented")
            }

            override fun getActiveByMeltId(meltId: String): ModelEvent? {
                TODO("Not yet implemented")
            }

            override fun getActiveByMeltIdAndEventType(meltId: String, eventType: ModelEvent.EventType): ModelEvent? {
                TODO("Not yet implemented")
            }
        }
    }
}
