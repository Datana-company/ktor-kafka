package ru.datana.smart.ui.converter.app.mappings

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Транспортная модель для передачи событий из внешних источников
 */
data class ConverterTransportExtEvent (

    @JsonProperty("alert-rule-id")
    val alertRuleId: kotlin.String? = null,

    @JsonProperty("container-id")
    val containerId: kotlin.String? = null,

    @JsonProperty("component")
    val component: kotlin.String? = null,

    @JsonProperty("@timestamp")
    val timestamp: kotlin.Long? = null,

    @JsonProperty("logger_name")
    val loggerName: kotlin.String? = null,

    @JsonProperty("message")
    val message: kotlin.String? = null
)
//{
//    "alert-rule-id": "RULE:ID-ALERT-ON-INTO-RANGE-TEMPERATURE",
//    "container-id": "worker1.datana.ru",
//    "component": "adapter-socket",
//    "@timestamp": "2020-11-06T00:36:31.055Z",
//    "logger_name": "ru.datana.integrationadapter.socket.integration.processors.SendInTransportProcessorImpl",
//    "message": "[Версия 6: Для Кафки] Температура в диапазоне 20..30 градусов ",
//}
