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
import java.util.concurrent.ConcurrentHashMap
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
//        .also { cache ->
//            initObjects.forEach {
//                cache.put(it.currentMeltInfo.id, CurrentStateInMemoryDto.of(it))
//            }
//        }
    private  val STUBID = "stub_id" // временный стабовый id для текущего состояния
    override suspend fun get(id: String?): CurrentState {
        val stateId = id?:STUBID
        if (stateId.isBlank()) throw CurrentStateRepoWrongIdException(stateId)
        return cache.get(stateId)?.toModel()?: throw CurrentStateRepoNotFoundException(stateId)
    }

    override suspend fun getAllSlagRates(id: String?): ConcurrentHashMap<Instant, ModelSlagRate> {
        val stateId = id?:STUBID
        if (stateId.isBlank()) throw CurrentStateRepoWrongIdException(stateId)
        return cache.get(stateId)?.toModel()?.slagRates?: throw CurrentStateRepoNotFoundException(stateId)
    }

    override suspend fun currentMeltInfo(id: String?): ModelMeltInfo {
        TODO("Not yet implemented")
    }

    override suspend fun currentMeltId(id: String?): String {
        TODO("Not yet implemented")
    }

    override suspend fun currentAngle(id: String?): Double {
        TODO("Not yet implemented")
    }

    override suspend fun avgStreamRate(id: String?): Double {
        TODO("Not yet implemented")
    }

    override suspend fun create(currentState: CurrentState): CurrentState {
        // В будущем использовать meltId
        //if (currentState.currentMeltInfo.id.isBlank()) throw CurrentStateRepoWrongIdException(currentState.currentMeltInfo.id)
        val dto = CurrentStateInMemoryDto.of(currentState, STUBID) // временная реализация
        return save(dto).toModel()
    }

    override suspend fun update(currentState: CurrentState) = create(currentState)

    override suspend fun delete(id: String?): CurrentState {
        val stateId = id?:STUBID
        return cache.peekAndRemove(stateId)?.toModel()?: CurrentState.NONE
    }

    override suspend fun updateMeltInfo(meltInfo: ModelMeltInfo): CurrentState {
        TODO("Not yet implemented")
    }

    override suspend fun updateAngles(id: String?, lastAngles: ModelAngles): ModelAngles {
        TODO("Not yet implemented")
    }

    override suspend fun updateSlagRate(id: String?, lastSlagRate: ModelSlagRate): CurrentState {
        TODO("Not yet implemented")
    }

    override suspend fun updateStreamRate(id: String?, avgStreamRate: Double): Double {
        TODO("Not yet implemented")
    }

    override suspend fun addSlagRate(id: String?, timestamp: Instant, slagRate: ModelSlagRate): CurrentState {
        val stateId = id?:STUBID
        if (stateId.isBlank()) throw CurrentStateRepoWrongIdException(stateId)
        var dto = cache.get(stateId)
        if (dto.slagRates == null) dto = dto.copy(slagRates = ConcurrentHashMap())
        dto.slagRates!![timestamp.toEpochMilli()] = CurrentStateInMemorySlagRate.of(slagRate)
        return save(dto).toModel()
    }

    override suspend fun compareAndUpdateLastTimeAngles(id: String?, lastTimeAngles: Instant): Instant {
        val stateId = id?:STUBID
        val stateModel = get(stateId)
        stateModel.lastTimeAngles = maxOf(stateModel.lastTimeAngles, lastTimeAngles)
        return save(CurrentStateInMemoryDto.of(stateModel)).toModel().lastTimeAngles
    }

    override suspend fun compareAndUpdateLastTimeFrame(id: String?, lastTimeFrame: Instant): Instant {
        val stateId = id?:STUBID
        val stateModel = get(stateId)
        stateModel.lastTimeFrame = maxOf(stateModel.lastTimeFrame, lastTimeFrame)
        return save(CurrentStateInMemoryDto.of(stateModel)).toModel().lastTimeFrame
    }

    private fun save(dto: CurrentStateInMemoryDto): CurrentStateInMemoryDto{
        cache.put(dto.id, dto)
        return cache.get(dto.id)
    }
}
