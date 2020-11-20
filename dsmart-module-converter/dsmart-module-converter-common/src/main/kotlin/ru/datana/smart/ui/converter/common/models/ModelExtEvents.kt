package ru.datana.smart.ui.converter.common.models

data class ModelExtEvents(
    var alertRuleId: String = "",
    var containerId: String = "",
    var component: String = "",
    var timestamp: String = "",
    var level: String = "",
    var loggerName: String = "",
    var message: String = ""
) {

    companion object {
        val NONE = ModelExtEvents()
    }
}
