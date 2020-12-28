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
        val stateId = id?:STUBID
        if (stateId.isBlank()) throw CurrentStateRepoWrongIdException(stateId)
        return cache.get(stateId)?.meltInfo?.toModel()?: throw CurrentStateRepoNotFoundException(stateId)
    }

    override suspend fun currentMeltId(id: String?): String {
        val stateId = id?:STUBID
        if (stateId.isBlank()) throw CurrentStateRepoWrongIdException(stateId)
        val dto = cache.get(stateId)?: throw CurrentStateRepoNotFoundException(stateId)
        return dto.meltInfo?.id?: ""
    }

    override suspend fun currentAngle(id: String?): Double {
        val stateId = id?:STUBID
        if (stateId.isBlank()) throw CurrentStateRepoWrongIdException(stateId)
        val dto = cache.get(stateId)?: throw CurrentStateRepoNotFoundException(stateId)
        return dto.lastAngles?.angle?: Double.MIN_VALUE
    }

    override suspend fun avgStreamRate(id: String?): Double {
        val stateId = id?:STUBID
        if (stateId.isBlank()) throw CurrentStateRepoWrongIdException(stateId)
        val dto = cache.get(stateId)?: throw CurrentStateRepoNotFoundException(stateId)
        return dto.avgStreamRate?: Double.MIN_VALUE
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

    override suspend fun updateMeltInfo(meltInfo: ModelMeltInfo): ModelMeltInfo {
        val stateId = STUBID
        if (stateId.isBlank()) throw CurrentStateRepoWrongIdException(stateId)
        val dto = cache.get(stateId)?: throw CurrentStateRepoNotFoundException(stateId)
        return save(dto.copy(meltInfo = CurrentStateInMemoryMeltInfo.of(meltInfo))).meltInfo?.toModel()?: ModelMeltInfo.NONE
    }

    override suspend fun updateAngles(id: String?, lastAngles: ModelAngles): ModelAngles {
        val stateId = id?: STUBID
        if (stateId.isBlank()) throw CurrentStateRepoWrongIdException(stateId)
        val dto = cache.get(stateId)?: throw CurrentStateRepoNotFoundException(stateId)
        return save(dto.copy(lastAngles = CurrentStateInMemoryAngles.of(lastAngles))).lastAngles?.toModel()?: ModelAngles.NONE
    }

    override suspend fun updateStreamRate(id: String?, avgStreamRate: Double): Double {
        val stateId = id?: STUBID
        if (stateId.isBlank()) throw CurrentStateRepoWrongIdException(stateId)
        val dto = cache.get(stateId)?: throw CurrentStateRepoNotFoundException(stateId)
        return save(dto.copy(avgStreamRate = avgStreamRate)).avgStreamRate?: Double.MIN_VALUE
    }

    override suspend fun addSlagRate(id: String?, timestamp: Instant, slagRate: ModelSlagRate): CurrentState {
        val stateId = id?:STUBID
        if (stateId.isBlank()) throw CurrentStateRepoWrongIdException(stateId)
        var dto = cache.get(stateId)?: throw CurrentStateRepoNotFoundException(stateId)
        if (dto.slagRates == null) dto = dto.copy(slagRates = ConcurrentHashMap())
        dto.slagRates!![timestamp.toEpochMilli()] = CurrentStateInMemorySlagRate.of(slagRate)
        return save(dto).toModel()
    }

    override suspend fun compareAndUpdateLastTimeAngles(id: String?, lastTimeAngles: Instant): Instant {
        val stateId = id?:STUBID
        val dto = cache.get(stateId)?: throw CurrentStateRepoNotFoundException(stateId)
        return save(dto.copy(lastTimeAngles = maxOf(dto.lastTimeAngles?: 0, lastTimeAngles.toEpochMilli())))
            .lastTimeAngles
            ?.let {
                Instant.ofEpochMilli(it)
            }?: Instant.EPOCH
    }

    override suspend fun compareAndUpdateLastTimeFrame(id: String?, lastTimeFrame: Instant): Instant {
        val stateId = id?:STUBID
        val dto = cache.get(stateId)?: throw CurrentStateRepoNotFoundException(stateId)
        return save(dto.copy(lastTimeFrame = maxOf(dto.lastTimeFrame?: 0, lastTimeFrame.toEpochMilli())))
            .lastTimeFrame
            ?.let {
                Instant.ofEpochMilli(it)
            }?: Instant.EPOCH
    }

    private fun save(dto: CurrentStateInMemoryDto): CurrentStateInMemoryDto{
        cache.put(dto.id, dto)
        return cache.get(dto.id)
    }
}
