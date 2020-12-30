package ru.datana.smart.ui.converter.repository.inmemory.currentstate

import ru.datana.smart.ui.converter.common.models.ModelMeltDevices
import ru.datana.smart.ui.converter.common.models.ModelMeltInfo
import java.time.Instant

data class CurrentStateInMemoryMeltInfo(
    val id: String? = null,
    val timeStart: Long? = null,
    val meltNumber: String? = null,
    val steelGrade: String? = null,
    val crewNumber: String? = null,
    val shiftNumber: String? = null,
    val mode: Mode? = null,
    val devices: CurrentStateInMemoryMeltDevices? = null
) {
    enum class Mode {
        PROD,
        EMULATION,
        TEST,
        NONE
    }

    fun toModel() = ModelMeltInfo(
        id = id?: "",
        timeStart = timeStart?.let { Instant.ofEpochMilli(it) }?: Instant.MIN,
        meltNumber = meltNumber?: "",
        steelGrade = steelGrade?: "",
        crewNumber = crewNumber?: "",
        shiftNumber = shiftNumber?: "",
        mode = mode?.let { ModelMeltInfo.Mode.valueOf(it.name) }?: ModelMeltInfo.Mode.NONE,
        devices = devices?.toModel()?: ModelMeltDevices.NONE
    )

    companion object {
        fun  of(model: ModelMeltInfo) = CurrentStateInMemoryMeltInfo(
            id = model.id.takeIf { it.isNotBlank() },
            timeStart = model.timeStart.takeIf { it != Instant.MIN }?.toEpochMilli(),
            meltNumber = model.meltNumber.takeIf { it.isNotBlank() },
            steelGrade = model.steelGrade.takeIf { it.isNotBlank() },
            crewNumber = model.crewNumber.takeIf { it.isNotBlank() },
            shiftNumber = model.shiftNumber.takeIf { it.isNotBlank() },
            mode = model.mode.takeIf { it != ModelMeltInfo.Mode.NONE }?.let { Mode.valueOf(it.name) },
            devices = model.devices.takeIf { it != ModelMeltDevices.NONE }?.let { CurrentStateInMemoryMeltDevices.of(it) }
        )
    }

}
