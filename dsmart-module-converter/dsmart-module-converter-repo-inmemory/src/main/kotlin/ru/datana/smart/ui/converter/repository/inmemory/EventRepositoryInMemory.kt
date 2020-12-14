package ru.datana.smart.ui.converter.repository.inmemory

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.repositories.IEventRepository
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.streams.toList

class EventRepositoryInMemory : IEventRepository {

    private val cache: Cache<String, EventInMemoryDto> = CacheBuilder
        .newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build()

    override suspend fun get(id: String): ModelEvent? {
        return cache.getIfPresent(id)?.toModel()
    }

    override suspend fun create(event: ModelEvent): ModelEvent? {
        val dto = EventInMemoryDto.of(event)
        return save(dto)?.toModel()
    }

    override suspend fun update(event: ModelEvent): ModelEvent? {
        return save(EventInMemoryDto.of(event))?.toModel()
    }

    private suspend fun save(dto: EventInMemoryDto): EventInMemoryDto? {
        cache.put(dto.id, dto)
        return cache.getIfPresent(dto.id)
    }

    override fun getAllByMeltId(meltId: String): MutableList<ModelEvent> {
        return cache.asMap().values.stream()
            .filter { event -> event.meltId == meltId }
            .sorted(Comparator.comparing(EventInMemoryDto::timeStart).reversed())
            .sorted(Comparator.comparing(EventInMemoryDto::isActive).reversed())
            .limit(10)
            .map(EventInMemoryDto::toModel)
            .toList().toMutableList()
    }

    override fun getAllActiveByMeltId(meltId: String): MutableList<ModelEvent> {
        return cache.asMap().values.stream()
            .filter { event -> event.isActive }
            .filter { event -> event.meltId == meltId }
            .sorted(Comparator.comparingLong(EventInMemoryDto::timeStart).reversed())
            .map(EventInMemoryDto::toModel)
            .toList().toMutableList()
    }

    override fun getActiveByMeltIdAndEventType(meltId: String, eventType: ModelEvent.EventType): ModelEvent? {
        return cache.asMap().values.stream()
            .filter { event -> event.isActive }
            .filter { event -> event.meltId == meltId }
            .filter { event -> event.type == eventType }
            .sorted(Comparator.comparingLong(EventInMemoryDto::timeStart).reversed())
            .map(EventInMemoryDto::toModel)
            .findFirst()
            .orElse(null)
    }

    // TODO На будущее. Пока не заработало
//    override fun getAll(): MutableList<ModelEvent> {
//        return cache.asMap().values.asSequence()
//            .sortedByDescending { it.timeStart }
//            .sortedByDescending { it.isActive }
//            .take(10)
//            .map(EventInMemoryDto::toModel)
//            .toMutableList()
//    }
//
//    override fun getAllActiveByMeltId(meltId: String): MutableList<ModelEvent> {
//        return cache.asMap().values.asSequence()
//            .filter { event -> event.isActive ?: false }
//            .filter { event -> event.meltId == meltId }
//            .sortedByDescending { it.timeStart }
//            .map(EventInMemoryDto::toModel)
//            .toMutableList()
//    }
//
//    override fun getActiveByMeltIdAndEventType(meltId: String, eventType: ModelEvent.EventType): ModelEvent? {
//        return cache.asMap().values.asSequence()
//            .filter { event -> event.isActive ?: false }
//            .filter { event -> event.meltId == meltId }
//            .filter { event -> event.type == eventType }
//            .sortedByDescending { it.timeStart }
//            .map(EventInMemoryDto::toModel)
//            .first()
//    }

}
