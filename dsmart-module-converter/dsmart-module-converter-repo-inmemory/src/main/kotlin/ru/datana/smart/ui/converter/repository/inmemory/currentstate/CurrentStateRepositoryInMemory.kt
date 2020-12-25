package ru.datana.smart.ui.converter.repository.inmemory.currentstate

import org.cache2k.Cache
import org.cache2k.Cache2kBuilder
import ru.datana.smart.ui.converter.common.exceptions.CurrentStateRepoNotFoundException
import ru.datana.smart.ui.converter.common.exceptions.CurrentStateRepoWrongIdException
import ru.datana.smart.ui.converter.common.models.CurrentState
import ru.datana.smart.ui.converter.common.models.ModelAngles
import ru.datana.smart.ui.converter.common.models.ModelMeltInfo
import ru.datana.smart.ui.converter.common.models.ModelSlagRate
import ru.datana.smart.ui.converter.common.repositories.ICurrentStateRepository
import java.time.Instant
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

    override suspend fun delete(id: String): CurrentState? {
       return cache.peekAndRemove(id)?.toModel()
    }

    override suspend fun updateMeltInfo(meltInfo: ModelMeltInfo): CurrentState? {
        TODO("Not yet implemented")
    }

    override suspend fun updateAngles(id: String, lastAngles: ModelAngles): CurrentState? {
        TODO("Not yet implemented")
    }

    override suspend fun updateSlagRate(id: String, lastSlagRate: ModelSlagRate): CurrentState? {
        TODO("Not yet implemented")
    }

    override suspend fun updateStreamRate(id: String, avgStreamRate: ModelSlagRate): CurrentState? {
        TODO("Not yet implemented")
    }

    override suspend fun updateLastTimeAngles(id: String, lastTimeAngles: Instant): CurrentState? {
        TODO("Not yet implemented")
    }

    override suspend fun updateLastTimeFrame(id: String, lastTimeFrame: Instant): CurrentState? {
        TODO("Not yet implemented")
    }

    private fun save(dto: CurrentStateInMemoryDto): CurrentStateInMemoryDto{
        cache.put(dto.id, dto)
        return cache.get(dto.id)
    }
}
