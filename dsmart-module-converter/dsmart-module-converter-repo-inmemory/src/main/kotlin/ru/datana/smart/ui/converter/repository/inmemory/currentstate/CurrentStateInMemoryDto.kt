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
    val lastSlagRate: CurrentStateInMemorySlagRate? = null,
    val avgStreamRate: Double? = null,
    val lastTimeAngles: Long? = null,
    val lastTimeFrame: Long? = null
) {
    fun  toModel() = CurrentState(
        currentMeltInfo = meltInfo?.toModel()?: ModelMeltInfo.NONE,
        lastAngles = lastAngles?.toModel()?: ModelAngles.NONE,
        lastSlagRate = lastSlagRate?.toModel()?: ModelSlagRate.NONE,
        avgStreamRate = avgStreamRate?: Double.MIN_VALUE,
        lastTimeAngles = lastTimeAngles?.let { Instant.ofEpochMilli(it) }?: Instant.MIN,
        lastTimeFrame = lastTimeFrame?.let { Instant.ofEpochMilli(it) }?: Instant.MIN
    )

    companion object {
        fun of(model: CurrentState) = Companion.of(model, model.currentMeltInfo.id)

        fun of(model: CurrentState, id: String) = CurrentStateInMemoryDto(
            id = id.takeIf { it.isNotBlank() },
            meltInfo = model.currentMeltInfo.takeIf { it != ModelMeltInfo.NONE }?.let { CurrentStateInMemoryMeltInfo.of(it) },
            lastAngles = model.lastAngles.takeIf { it != ModelAngles.NONE }?.let { CurrentStateInMemoryAngles.of(it) },
            lastSlagRate = model.lastSlagRate.takeIf { it != ModelSlagRate.NONE }?.let { CurrentStateInMemorySlagRate.of(it) },
            avgStreamRate = model.avgStreamRate.takeIf { it != Double.MIN_VALUE },
            lastTimeAngles = model.lastTimeAngles.takeIf { it != Instant.MIN }?.toEpochMilli(),
            lastTimeFrame = model.lastTimeFrame.takeIf { it != Instant.MIN }?.toEpochMilli()
        )
    }
}
