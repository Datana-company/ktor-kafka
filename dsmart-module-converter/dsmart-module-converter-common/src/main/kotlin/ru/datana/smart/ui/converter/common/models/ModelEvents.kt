package ru.datana.smart.ui.converter.common.models

class ModelEvents(
    val events: List<EventModel>? = null
) {

    companion object {
        val NONE = ModelEvents()
    }
}
