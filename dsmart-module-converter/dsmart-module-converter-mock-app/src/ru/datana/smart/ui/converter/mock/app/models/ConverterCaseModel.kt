package ru.datana.smart.ui.converter.mock.app.models

import com.fasterxml.jackson.annotation.JsonProperty

data class ConverterCaseModel(
    @JsonProperty("name")
    val name: kotlin.String? = null,
    @JsonProperty("dir")
    val dir: kotlin.String? = null
) {}
