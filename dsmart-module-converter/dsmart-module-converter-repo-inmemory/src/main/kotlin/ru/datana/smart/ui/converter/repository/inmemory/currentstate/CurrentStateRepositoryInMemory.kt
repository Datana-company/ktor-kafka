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
    initObjects: Collection<CurrentState> = emptyList(),
    val converterId: String
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

    override suspend fun get(id: String): CurrentState? {
        //if (converterId.isBlank()) throw CurrentStateRepoWrongIdException(id)
        return cache.get(converterId)?.toModel()
    }

    override suspend fun getAllSlagRates(id: String): MutableList<ModelSlagRate>? {
        //if (id.isBlank()) throw CurrentStateRepoWrongIdException(id)
        return cache.get(converterId)?.toModel()?.slagRateList
    }

    override suspend fun currentMeltInfo(id: String): ModelMeltInfo? {
        //if (id.isBlank()) throw CurrentStateRepoWrongIdException(id)
        return cache.get(converterId)?.meltInfo?.toModel()
    }

    override suspend fun currentMeltId(id: String): String {
        //if (id.isBlank()) throw CurrentStateRepoWrongIdException(id)
        return cache.get(converterId)?.meltInfo?.id?: ""
    }

    override suspend fun currentAngle(id: String): Double {
        //if (id.isBlank()) throw CurrentStateRepoWrongIdException(id)
        return cache.get(converterId)?.lastAngles?.angle?: Double.MIN_VALUE
    }

    override suspend fun lastAvgSlagRate(id: String): Double {
        //if (id.isBlank()) throw CurrentStateRepoWrongIdException(id)
        return cache.get(converterId)?.lastAvgSlagRate ?: Double.MIN_VALUE
    }

    override suspend fun lastTimeAngles(id: String): Instant {
        //if (id.isBlank()) throw CurrentStateRepoWrongIdException(id)
        return cache.get(converterId)?.let {
            Instant.ofEpochMilli(it.lastTimeAngles?: Long.MIN_VALUE)
        }?: Instant.MIN
    }

    override suspend fun lastTimeFrame(id: String): Instant {
        //if (id.isBlank()) throw CurrentStateRepoWrongIdException(id)
        return cache.get(converterId)?.let {
            Instant.ofEpochMilli(it.lastTimeAngles?: Long.MIN_VALUE)
        }?: Instant.MIN
    }

    override suspend fun create(currentState: CurrentState): CurrentState {
//        if (currentState.currentMeltInfo.devices.converter.id.isBlank())
//            throw CurrentStateRepoWrongIdException(currentState.currentMeltInfo.devices.converter.id)
        val dto = CurrentStateInMemoryDto.of(currentState, converterId) // временная реализация
        println("CREATE_UPDATE: $dto")
        return save(dto).toModel()
    }

    override suspend fun update(currentState: CurrentState) = create(currentState)

    override suspend fun delete(id: String): CurrentState {
        return cache.peekAndRemove(converterId)?.toModel()?: CurrentState.NONE
    }

    override suspend fun updateMeltInfo(meltInfo: ModelMeltInfo): ModelMeltInfo? {
//        val  id = meltInfo.devices.converter.id
//        if (id.isBlank()) throw CurrentStateRepoWrongIdException(id)
        val dto = cache.get(converterId)?: return null
        return save(dto.copy(meltInfo = CurrentStateInMemoryMeltInfo.of(meltInfo))).meltInfo?.toModel()
    }

    override suspend fun updateAngles(id: String, lastAngles: ModelAngles): ModelAngles? {
        //if (id.isBlank()) throw CurrentStateRepoWrongIdException(id)
        val dto = cache.get(converterId)?: return null
        return save(dto.copy(lastAngles = CurrentStateInMemoryAngles.of(lastAngles))).lastAngles?.toModel()
    }

    override suspend fun addSlagRate(id: String, slagRate: ModelSlagRate): CurrentState? {
        //if (id.isBlank()) throw CurrentStateRepoWrongIdException(id)
        var dto = cache.get(converterId)?: return null
        if (dto.slagRateList == null) dto = dto.copy(slagRateList = mutableListOf())
        dto.slagRateList!!.add(CurrentStateInMemorySlagRate.of(slagRate))
        return save(dto).toModel()
    }

    override suspend fun updateLastAvgSlagRate(id: String, avgSlagRate: Double): Double? {
        //if (id.isBlank()) throw CurrentStateRepoWrongIdException(id)
        val dto = cache.get(converterId)?: return null
        return save(dto.copy(lastAvgSlagRate = avgSlagRate)).lastAvgSlagRate
    }

    override suspend fun updateLastTimeAngles(id: String, lastTimeAngles: Instant): Instant? {
        val dto = cache.get(converterId)?: return null
        return save(dto.copy(lastTimeAngles = lastTimeAngles.toEpochMilli()))
            .lastTimeAngles
            ?.let {
                Instant.ofEpochMilli(it)
            }
    }

    override suspend fun updateLastTimeFrame(id: String, lastTimeFrame: Instant): Instant? {
        val dto = cache.get(converterId)?: return null
        return save(dto.copy(lastTimeFrame = lastTimeFrame.toEpochMilli()))
            .lastTimeFrame
            ?.let {
                Instant.ofEpochMilli(it)
            }
    }

    private fun save(dto: CurrentStateInMemoryDto): CurrentStateInMemoryDto{
        cache.put(dto.id, dto)
        return cache.get(dto.id)
    }
}
