package ru.datana.smart.ui.converter.mock.app.models

import com.fasterxml.jackson.annotation.JsonProperty
import io.ktor.http.content.*

data class ConverterCaseSaveRequest(

    /* TESTCASE specification */

    @JsonProperty("caseId")
    var caseId: String? = null,
    @JsonProperty("caseName")
    var caseName: String? = null,

    /* MELT specification */

    @JsonProperty("meltNumber")
    var meltNumber: kotlin.String? = null,
    /* Марка выплавляемой стали */
    @JsonProperty("steelGrade")
    var steelGrade: kotlin.String? = null,
    /* Номер бригады, выполняющей плавку */
    @JsonProperty("crewNumber")
    var crewNumber: kotlin.String? = null,
    /* Номер смены, в которую выполняется плавка */
    @JsonProperty("shiftNumber")
    var shiftNumber: kotlin.String? = null,

    /* CONVERTER */

    @JsonProperty("converterId")
    var converterId: kotlin.String? = null,
    @JsonProperty("converterName")
    var converterName: kotlin.String? = null,

    /* CAMERA and video files */

    @JsonProperty("irCameraId")
    var irCameraId: kotlin.String? = null,
    @JsonProperty("irCameraName")
    var irCameraName: kotlin.String? = null,
    @JsonProperty("fileVideo")
    var fileVideo: PartData.FileItem? = null,

    /* SLAG COMPOSITION resolution device/service */

    @JsonProperty("slagRateId")
    var slagRateDeviceId: kotlin.String? = null,
    @JsonProperty("slagRateName")
    var slagRateDeviceName: kotlin.String? = null,
    @JsonProperty("slagRateJson")
    var slagRateJson: PartData.FileItem? = null,

    /* SELSYN device/service */

    @JsonProperty("selsynId")
    var selsynId: kotlin.String? = null,
    @JsonProperty("selsynName")
    var selsynName: kotlin.String? = null,
    @JsonProperty("selsynJson")
    var selsynJson: PartData.FileItem? = null,
)
