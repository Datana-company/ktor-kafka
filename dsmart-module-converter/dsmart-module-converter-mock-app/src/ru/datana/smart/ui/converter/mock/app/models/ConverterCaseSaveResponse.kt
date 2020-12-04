package ru.datana.smart.ui.converter.mock.app.models

import com.fasterxml.jackson.annotation.JsonProperty

data class ConverterCaseSaveResponse(
    @JsonProperty("caseId")
    val caseId: String? = null,

    @JsonProperty("errors")
    val errors: List<ConverterMockError>? = null,

    @JsonProperty("status")
    val status: Statuses? = null
) {
    enum class Statuses {
        OK,
        ERROR
    }

}
