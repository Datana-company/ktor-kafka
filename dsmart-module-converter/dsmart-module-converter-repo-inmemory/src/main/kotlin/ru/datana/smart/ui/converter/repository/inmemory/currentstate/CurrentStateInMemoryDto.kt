package ru.datana.smart.ui.converter.repository.inmemory.currentstate

import ru.datana.smart.ui.converter.common.models.CurrentState
import ru.datana.smart.ui.converter.common.models.ModelAngles
import ru.datana.smart.ui.converter.common.models.ModelMeltInfo
import ru.datana.smart.ui.converter.common.models.ModelSlagRate
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap


data class CurrentStateInMemoryDto(
    val id: String? = null,
    val meltInfo: CurrentStateInMemoryMeltInfo? = null,
    val lastAngles: CurrentStateInMemoryAngles? = null,
    val slagRates: MutableList<CurrentStateInMemorySlagRate>? = null,
    val avgStreamRate: Double? = null,
    val lastTimeAngles: Long? = null,
    val lastTimeFrame: Long? = null
) {
    fun  toModel() = CurrentState(
        currentMeltInfo = meltInfo?.toModel()?: ModelMeltInfo.NONE,
        lastAngles = lastAngles?.toModel()?: ModelAngles.NONE,
        slagRates = slagRates?.map { it.toModel() }?.toMutableList()?: mutableListOf(),
        avgStreamRate = avgStreamRate?: Double.MIN_VALUE,
        lastTimeAngles = lastTimeAngles?.let { Instant.ofEpochMilli(it) }?: Instant.EPOCH,
        lastTimeFrame = lastTimeFrame?.let { Instant.ofEpochMilli(it) }?: Instant.EPOCH
    )

    companion object {
        fun of(model: CurrentState) = Companion.of(model, model.currentMeltInfo.devices.converter.id)

        fun of(model: CurrentState, id: String) = CurrentStateInMemoryDto(
            id = id.takeIf { it.isNotBlank() },
            meltInfo = model.currentMeltInfo.takeIf { it != ModelMeltInfo.NONE }?.let { CurrentStateInMemoryMeltInfo.of(it) },
            lastAngles = model.lastAngles.takeIf { it != ModelAngles.NONE }?.let { CurrentStateInMemoryAngles.of(it) },
            slagRates = model.slagRates.takeIf { it.isNotEmpty() }?.map{CurrentStateInMemorySlagRate.of(it)}?.toMutableList() ,
            avgStreamRate = model.avgStreamRate.takeIf { it != Double.MIN_VALUE },
            lastTimeAngles = model.lastTimeAngles.takeIf { it != Instant.EPOCH }?.toEpochMilli(),
            lastTimeFrame = model.lastTimeFrame.takeIf { it != Instant.EPOCH }?.toEpochMilli()
        )
    }
}
