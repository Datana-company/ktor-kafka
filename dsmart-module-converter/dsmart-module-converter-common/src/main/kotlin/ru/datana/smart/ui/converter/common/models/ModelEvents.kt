package ru.datana.smart.ui.converter.common.models

import ru.datana.smart.ui.converter.common.events.IBizEvent

class ModelEvents(
    var events: List<IBizEvent> = mutableListOf()
) {

    companion object {
        val NONE = ModelEvents()
    }
}
