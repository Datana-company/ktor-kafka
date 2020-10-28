package ru.datana.smart.ui.converter.repository.inmemory

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import ru.datana.smart.ui.converter.common.events.IBizEvent
import ru.datana.smart.ui.converter.common.repositories.IUserEventsRepository
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.streams.toList

class UserEventsRepository : IUserEventsRepository {

    private val events: Cache<String, IBizEvent> = CacheBuilder
        .newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build()

    override fun put(event: IBizEvent) {
        events.put(event.id, event)
    }

    override fun getAll(): List<IBizEvent> {
        return events.asMap().values.stream()
            .sorted(Comparator.comparing(IBizEvent::timeStart).reversed())
            .sorted(Comparator.comparing(IBizEvent::isActive).reversed())
            .limit(10)
            .toList()
    }

    override fun getActive(): List<IBizEvent> {
        return events.asMap().values.stream()
            .filter { recommendation -> recommendation.isActive }
            .sorted(Comparator.comparingLong(IBizEvent::timeStart).reversed())
            .toList()
    }

}
