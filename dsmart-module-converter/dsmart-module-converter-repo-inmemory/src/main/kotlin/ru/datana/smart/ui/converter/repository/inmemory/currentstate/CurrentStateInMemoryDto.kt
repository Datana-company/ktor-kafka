package ru.datana.smart.ui.converter.repository.inmemory.currentstate

import ru.datana.smart.ui.converter.common.models.CurrentState
import ru.datana.smart.ui.converter.common.models.ModelAngles
import ru.datana.smart.ui.converter.common.models.ModelMeltInfo
import ru.datana.smart.ui.converter.common.models.ModelSlagRate
import java.time.Instant


data class CurrentStateInMemoryDto(
    val id: String? = null,
    val meltInfo: CurrentStateInMemoryMeltInfo? = null,
    val lastAngles: CurrentStateInMemoryAngles? = null,
    val lastTimeAngles: Long? = null,
    val lastTimeFrame: Long? = null,
    val lastAvgSlagRate: Double? = null,
    val slagRateList: MutableList<CurrentStateInMemorySlagRate>? = null
) {
    fun  toModel() = CurrentState(
        currentMeltInfo = meltInfo?.toModel() ?: ModelMeltInfo.NONE,
        lastAngles = lastAngles?.toModel() ?: ModelAngles.NONE,
        lastAvgSlagRate = lastAvgSlagRate ?: Double.MIN_VALUE,
        lastTimeAngles = lastTimeAngles?.let { Instant.ofEpochMilli(it) } ?: Instant.EPOCH,
        lastTimeFrame = lastTimeFrame?.let { Instant.ofEpochMilli(it) } ?: Instant.EPOCH,
        slagRateList = slagRateList?.map { slagRate ->
            ModelSlagRate(
                slagRateTime = slagRate.slagRateTime?.let { Instant.ofEpochMilli(it) } ?: Instant.MIN,
                steelRate = slagRate.steelRate ?: Double.MIN_VALUE,
                slagRate = slagRate.slagRate ?: Double.MIN_VALUE,
                avgSlagRate = slagRate.avgSlagRate ?: Double.MIN_VALUE
            )
        }?.toMutableList() ?: mutableListOf()
    )

    companion object {
        fun of(model: CurrentState) = Companion.of(model, model.currentMeltInfo.devices.converter.id)

        fun of(model: CurrentState, id: String) = CurrentStateInMemoryDto(
            id = id.takeIf { it.isNotBlank() },
            meltInfo = model.currentMeltInfo.takeIf { it != ModelMeltInfo.NONE }?.let { CurrentStateInMemoryMeltInfo.of(it) },
            lastAngles = model.lastAngles.takeIf { it != ModelAngles.NONE }?.let { CurrentStateInMemoryAngles.of(it) },
            slagRateList = model.slagRateList.takeIf { it.isNotEmpty() }?.map { slagRate -> CurrentStateInMemorySlagRate.of(slagRate) }?.toMutableList(),
            lastTimeAngles = model.lastTimeAngles.takeIf { it != Instant.EPOCH }?.toEpochMilli(),
            lastTimeFrame = model.lastTimeFrame.takeIf { it != Instant.EPOCH }?.toEpochMilli()
        )
    }
}
