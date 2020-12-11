package ru.datana.smart.ui.converter.repository.inmemory

import org.cache2k.Cache
import org.cache2k.Cache2kBuilder
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.repositories.IEventRepository
import ru.datana.smart.ui.converter.common.exceptions.EventRepoWrongIdException
import ru.datana.smart.ui.converter.common.exceptions.EventRepoNotFoundException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class EventRepositoryInMemory@OptIn(ExperimentalTime::class) constructor(
    ttl: Duration,
    initObjects: Collection<ModelEvent> = emptyList()
) : IEventRepository {
    @OptIn(ExperimentalTime::class)
    private var cache: Cache<String, EventInMemoryDto> = object : Cache2kBuilder<String, EventInMemoryDto>() {}
        .expireAfterWrite(ttl.toLongMilliseconds(), TimeUnit.MINUTES) // expire/refresh after 5 minutes
        .suppressExceptions(false)
        .build()
        .also { cache ->
            initObjects.forEach {
                cache.put(it.id, EventInMemoryDto.Companion.of(it))
            }
        }

    override suspend fun get(id: String): ModelEvent? {
        return cache.getIfPresent(id)?.toModel()
    }

    override suspend fun create(event: ModelEvent): ModelEvent? {
        val dto = EventInMemoryDto.of(event)
        return save(dto)?.toModel()
    }

    override suspend fun update(event: ModelEvent): ModelEvent {
        return save(EventInMemoryDto.of(event)).toModel()
    }

    private fun save(dto: EventInMemoryDto): EventInMemoryDto {
        cache.put(dto.id, dto)
        return cache.get(dto.id)
    }

    override fun getAll(): MutableList<ModelEvent> {
        return cache.asMap().values.asSequence()
            .map { event -> event.toModel() }
            .sortedByDescending { it.timeStart }
            .sortedByDescending { it.isActive }
            .take(10)
            .toMutableList()
    }

    override fun getAllByMeltId(meltId: String): MutableList<ModelEvent> {
        return cache.asMap().values.asSequence()
            .map { event -> event.toModel() }
            .filter { event -> event.meltId == meltId }
            .sortedByDescending { it.timeStart }
            .sortedByDescending { it.isActive }
            .take(10)
            .toMutableList()
    }

    override fun getAllActiveByMeltId(meltId: String): MutableList<ModelEvent> {
        return cache.asMap().values.asSequence()
            .map { event -> event.toModel() }
            .filter { event -> event.isActive }
            .filter { event -> event.meltId == meltId }
            .sortedByDescending { it.timeStart }
            .toMutableList()
    }

    override fun getActiveByMeltIdAndEventType(meltId: String, eventType: ModelEvent.EventType): ModelEvent? {
        return cache.asMap().values.asSequence()
            .map { event -> event.toModel() }
            .filter { event -> event.isActive }
            .filter { event -> event.meltId == meltId }
            .filter { event -> event.type == eventType }
            .sortedByDescending { it.timeStart }
            .firstOrNull()
    }
}
