package ru.datana.smart.ui.converter.repository.inmemory.currentstate

import org.cache2k.Cache
import org.cache2k.Cache2kBuilder
import ru.datana.smart.ui.converter.common.models.CurrentState
import ru.datana.smart.ui.converter.common.models.ModelAngles
import ru.datana.smart.ui.converter.common.models.ModelMeltInfo
import ru.datana.smart.ui.converter.common.models.ModelSlagRate
import ru.datana.smart.ui.converter.common.repositories.ICurrentStateRepository
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class CurrentStateRepositoryInMemory @OptIn(ExperimentalTime::class) constructor(
    ttl: Duration,
    initObjects: Collection<CurrentState> = emptyList(),
    val converterId: String,
    val timeLimit: Long
): ICurrentStateRepository {
    @OptIn(ExperimentalTime::class)
    private var cache: Cache<String, CurrentStateInMemoryDto> = object : Cache2kBuilder<String, CurrentStateInMemoryDto>() {}
        .expireAfterWrite(ttl.toLongMilliseconds(), TimeUnit.MILLISECONDS)
        .suppressExceptions(false)
        .build()

    override suspend fun get(): CurrentState? {
        return cache.get(converterId)?.toModel()
    }

    override suspend fun getAllSlagRates(): MutableList<ModelSlagRate>? {
        val slagrates = cache.get(converterId)?.toModel()?.slagRateList?: return null
        val endTime = slagrates.last().slagRateTime
        val startTime = endTime.takeIf {
            it > Instant.MIN.plus(timeLimit, ChronoUnit.SECONDS)
        }?.minus(timeLimit, ChronoUnit.SECONDS)?: return slagrates
        return slagrates.filter { it.slagRateTime >= startTime }.toMutableList()
    }

    override suspend fun currentMeltInfo(): ModelMeltInfo? {
        return cache.get(converterId)?.meltInfo?.toModel()
    }

    override suspend fun currentMeltId(): String {
        return cache.get(converterId)?.meltInfo?.id?: ""
    }

    override suspend fun currentAngle(): Double {
        return cache.get(converterId)?.lastAngles?.angle?: Double.MIN_VALUE
    }

    override suspend fun lastAvgSteelRate(): Double {
        return cache.get(converterId)?.lastAvgSteelRate ?: Double.MIN_VALUE
    }

    override suspend fun lastAvgSlagRate(): Double {
        return cache.get(converterId)?.lastAvgSlagRate ?: Double.MIN_VALUE
    }

    override suspend fun lastTimeAngles(): Instant {
        return cache.get(converterId)?.let {
            Instant.ofEpochMilli(it.lastTimeAngles?: Long.MIN_VALUE)
        }?: Instant.MIN
    }

    override suspend fun lastTimeFrame(): Instant {
        return cache.get(converterId)?.let {
            Instant.ofEpochMilli(it.lastTimeAngles?: Long.MIN_VALUE)
        }?: Instant.MIN
    }

    override suspend fun create(currentState: CurrentState): CurrentState {
        val dto = CurrentStateInMemoryDto.of(currentState, converterId) // временная реализация
        return save(dto).toModel()
    }

    override suspend fun update(currentState: CurrentState) = create(currentState)

    override suspend fun delete(): CurrentState {
        return cache.peekAndRemove(converterId)?.toModel()?: CurrentState.NONE
    }

    override suspend fun updateMeltInfo(meltInfo: ModelMeltInfo): ModelMeltInfo? {
        val dto = cache.get(converterId)?: return null
        return save(dto.copy(meltInfo = CurrentStateInMemoryMeltInfo.of(meltInfo))).meltInfo?.toModel()
    }

    override suspend fun updateAngles(lastAngles: ModelAngles): ModelAngles? {
        val dto = cache.get(converterId)?: return null
        return save(dto.copy(lastAngles = CurrentStateInMemoryAngles.of(lastAngles))).lastAngles?.toModel()
    }

    override suspend fun addSlagRate(slagRate: ModelSlagRate): CurrentState? {
        var dto = cache.get(converterId)?: return null
        if (dto.slagRateList == null) dto = dto.copy(slagRateList = mutableListOf())
        dto.slagRateList!!.add(CurrentStateInMemorySlagRate.of(slagRate))
        return save(dto).toModel()
    }

    override suspend fun updateLastAvgSteelRate(avgSteelRate: Double): Double? {
        val dto = cache.get(converterId)?: return null
        return save(dto.copy(lastAvgSteelRate = avgSteelRate)).lastAvgSteelRate
    }

    override suspend fun updateLastAvgSlagRate(avgSlagRate: Double): Double? {
        val dto = cache.get(converterId)?: return null
        return save(dto.copy(lastAvgSlagRate = avgSlagRate)).lastAvgSlagRate
    }

    override suspend fun updateLastTimeAngles(lastTimeAngles: Instant): Instant? {
        val dto = cache.get(converterId)?: return null
        return save(dto.copy(lastTimeAngles = lastTimeAngles.toEpochMilli()))
            .lastTimeAngles
            ?.let {
                Instant.ofEpochMilli(it)
            }
    }

    override suspend fun updateLastTimeFrame(lastTimeFrame: Instant): Instant? {
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
