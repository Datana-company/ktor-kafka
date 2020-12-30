package ru.datana.smart.ui.converter.repository.inmemory.currentstate

import ru.datana.smart.ui.converter.common.models.ModelAngles
import java.time.Instant

data class CurrentStateInMemoryAngles(
    val angleTime: Long? = null,
    val angle: Double? = null,
    val source: Double? = null
) {
    fun toModel() = ModelAngles(
        angleTime = angleTime?.let { Instant.ofEpochMilli(it) }?: Instant.MIN,
        angle = angle?: Double.MIN_VALUE,
        source = source?: Double.MIN_VALUE
    )

    companion object {
        fun of(model: ModelAngles) = CurrentStateInMemoryAngles(
            angleTime = model.angleTime.takeIf { it != Instant.MIN }?.toEpochMilli(),
            angle = model.angle.takeIf { it != Double.MIN_VALUE },
            source = model.source.takeIf { it != Double.MIN_VALUE }
        )
    }

}
