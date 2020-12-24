package ru.datana.smart.ui.converter.repository.inmemory.currentstate

import org.cache2k.Cache
import org.cache2k.Cache2kBuilder
import ru.datana.smart.ui.converter.common.models.CurrentState
import ru.datana.smart.ui.converter.common.repositories.ICurrentStateRepository
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class CurrentStateRepositoryInMemory @OptIn(ExperimentalTime::class) constructor(
    ttl: Duration,
    initObjects: Collection<CurrentState> = emptyList()
): ICurrentStateRepository {
    @OptIn(ExperimentalTime::class)
    private var cache: Cache<String, CurrentStateInMemoryDto> = object : Cache2kBuilder<String, CurrentStateInMemoryDto>() {}
        .expireAfterWrite(ttl.toLongMilliseconds(), TimeUnit.MILLISECONDS)
        .suppressExceptions(false)
        .build()
        .also { cache ->
            initObjects.forEach {
                cache.put(it.currentMeltInfo.id, CurrentStateInMemoryDto.of(it))
            }
        }

    override suspend fun get(id: String): CurrentState? {
        TODO("Not yet implemented")
    }

    override suspend fun create(currentState: CurrentState): CurrentState? {
        TODO("Not yet implemented")
    }

    override suspend fun update(currentState: CurrentState): CurrentState? {
        TODO("Not yet implemented")
    }
}
