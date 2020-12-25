package ru.datana.smart.ui.converter.repository.inmemory.currentstate

import org.cache2k.Cache
import org.cache2k.Cache2kBuilder
import ru.datana.smart.ui.converter.common.exceptions.CurrentStateRepoNotFoundException
import ru.datana.smart.ui.converter.common.exceptions.CurrentStateRepoWrongIdException
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

    override suspend fun get(id: String): CurrentState {
        if (id.isBlank()) throw CurrentStateRepoWrongIdException(id)
        return cache.get(id)?.toModel()?: throw CurrentStateRepoNotFoundException(id)
    }

    override suspend fun create(currentState: CurrentState): CurrentState {
        if (currentState.currentMeltInfo.id.isBlank()) throw CurrentStateRepoWrongIdException(currentState.currentMeltInfo.id)
        val dto = CurrentStateInMemoryDto.of(currentState)
        return save(dto).toModel()
    }

    override suspend fun update(currentState: CurrentState) = create(currentState)

    private fun save(dto: CurrentStateInMemoryDto): CurrentStateInMemoryDto{
        cache.put(dto.id, dto)
        return cache.get(dto.id)
    }
}
