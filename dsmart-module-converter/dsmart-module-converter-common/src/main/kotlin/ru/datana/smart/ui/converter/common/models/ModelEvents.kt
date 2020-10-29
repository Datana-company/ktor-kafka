package ru.datana.smart.ui.converter.common.models

import ru.datana.smart.ui.converter.common.events.IBizEvent

class ModelEvents(
    val events: List<IBizEvent>? = null
) {

    companion object {
        val NONE = ModelEvents()
    }
}
