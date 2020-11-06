package ru.datana.smart.ui.converter.mock.app.models

import com.fasterxml.jackson.annotation.JsonProperty

data class ConverterCaseListModel(
    @JsonProperty("cases")
    val cases: List<ConverterCaseModel>? = null
) {}
