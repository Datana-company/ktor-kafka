package ru.datana.smart.ui.converter.app.cor.repository
import ru.datana.smart.ui.converter.app.cor.repository.events.IBizEvent

interface IUserEventsRepository {
    fun put(event: IBizEvent)
    fun getActive(): List<IBizEvent>
    fun getAll(): List<IBizEvent>
}
