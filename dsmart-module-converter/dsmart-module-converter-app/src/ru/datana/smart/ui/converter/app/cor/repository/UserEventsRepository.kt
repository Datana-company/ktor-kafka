package ru.datana.smart.ui.converter.app.cor.repository

import java.util.*
import kotlin.collections.HashMap
import kotlin.streams.toList

class UserEventsRepository(
    private val events: HashMap<String, IUserEvent> = hashMapOf()
): IUserEventsRepository<IUserEvent> {

    override fun put(event: IUserEvent) {
        events[event.id] = event
    }

    override fun getAll(): List<IUserEvent> {
        return events.values.stream()
            .sorted(Comparator.comparing(IUserEvent::timeStart).reversed())
            .sorted(Comparator.comparing(IUserEvent::isActive).reversed())
            .toList()
    }

    override fun getActive(): List<IUserEvent> {
        return events.values.stream()
            .filter { recommendation -> recommendation.isActive }
            .sorted(Comparator.comparingLong(IUserEvent::timeStart).reversed())
            .toList()
    }

}
