package ru.datana.smart.ui.converter.mock.app

import com.fasterxml.jackson.annotation.JsonProperty
import ru.datana.smart.ui.meta.models.ConverterMeltInfo

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

data class ConverterCaseSaveRequest(
    @JsonProperty("caseName")
    val caseName: String? = null,

    @JsonProperty("meltInfo")
    val meltInfo: ConverterMeltInfo? = null
)

data class ConverterCaseSaveResponse(
    @JsonProperty("caseName")
    val caseName: String? = null,

    @JsonProperty("meltInfo")
    val meltInfo: ConverterMeltInfo? = null
)
