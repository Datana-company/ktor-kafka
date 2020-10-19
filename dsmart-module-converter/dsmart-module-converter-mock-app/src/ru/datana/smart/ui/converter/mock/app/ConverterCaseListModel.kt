package ru.datana.smart.ui.converter.mock.app

import com.fasterxml.jackson.annotation.JsonProperty

data class ConverterCaseListModel(
    @JsonProperty("cases")
    val cases: List<ConverterCaseModel>? = null
) {}

data class ConverterCaseModel(
    @JsonProperty("name")
    val name: kotlin.String? = null,
    @JsonProperty("dir")
    val dir: kotlin.String? = null
) {}
