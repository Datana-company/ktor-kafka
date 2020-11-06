package ru.datana.smart.ui.converter.common.models

class ModelExtEvents(
    val alertRuleId: String? = null,
    val containerId: String? = null,
    val component: String? = null,
    val timestamp: Long? = null,
    val loggerName: String? = null,
    val message: String? = null
) {

    companion object {
        val NONE = ModelExtEvents()
    }
}
