package ru.datana.smart.ui.converter.common.models

import kotlinx.coroutines.Job

data class ScheduleCleaner(
    var jobAngles: Job? = null,
    var jobSlagRate: Job? = null,
    var jobFrameMath: Job? = null,
    var jobFrameCamera: Job? = null
) {

    companion object {
        val NONE = ScheduleCleaner()
    }
}
