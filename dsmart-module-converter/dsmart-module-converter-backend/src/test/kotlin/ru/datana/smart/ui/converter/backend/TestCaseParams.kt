package ru.datana.smart.ui.converter.backend

import ru.datana.smart.ui.converter.common.models.ModelFrame
import java.time.Instant

data class TestCaseParams(
    val angleTime: Instant? = null,
    val angle: Double? = null,
    val source: Double? = null,
    val frameId: String? = null,
    val frameTime: Instant? = null,
    val framePath: String? = null,
    val image: String? = null,
    val channel: ModelFrame.Channels? = null,
    val steelRate: Double? = null,
    val slagRate: Double? = null,
    val avgSteelRate: Double? = null,
    val meltId: String? = null,
    val converterId: String? = null
)
