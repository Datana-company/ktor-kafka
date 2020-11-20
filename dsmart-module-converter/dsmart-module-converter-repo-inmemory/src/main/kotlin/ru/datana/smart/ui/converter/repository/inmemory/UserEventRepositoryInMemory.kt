package ru.datana.smart.ui.converter.repository.inmemory

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import ru.datana.smart.ui.converter.common.events.IBizEvent
import ru.datana.smart.ui.converter.common.events.IMetalRateEvent
import ru.datana.smart.ui.converter.common.repositories.IUserEventsRepository
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.TimeUnit
import kotlin.streams.toList

class UserEventRepositoryInMemory: IUserEventsRepository {

    private val cache: Cache<String, ConcurrentMap<String, IBizEvent>> = CacheBuilder
        .newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build()

    override fun put(meltId: String, event: IBizEvent) {
        val events: ConcurrentMap<String, IBizEvent> = cache.getIfPresent(meltId) ?: ConcurrentHashMap()
        events.put(event.id, event)
        cache.put(meltId, events)
    }

    override fun getAllByMeltId(meltId: String): List<IBizEvent> {
        return cache.getIfPresent(meltId)?.let { it.values.stream()
            .sorted(Comparator.comparing(IBizEvent::timeStart).reversed())
            .sorted(Comparator.comparing(IBizEvent::isActive).reversed())
            .limit(10)
            .toList()
        } ?: mutableListOf()
    }

    override fun getActiveByMeltId(meltId: String): List<IBizEvent> {
        return cache.getIfPresent(meltId)?.let { it.values.stream()
            .filter { event -> event.isActive }
            .sorted(Comparator.comparing(IBizEvent::timeStart).reversed())
            .toList()
        } ?: mutableListOf()
    }

    override fun getActiveMetalRateEventByMeltId(meltId: String): IMetalRateEvent? {
        return cache.getIfPresent(meltId)?.let {
            it.values.stream()
                .filter { event -> event.isActive }
                .sorted(Comparator.comparing(IBizEvent::timeStart).reversed())
                .filter { event -> event is IMetalRateEvent }
                .map { event -> event as? IMetalRateEvent }
                .findFirst()
                .orElse(null)
        }
    }
}
