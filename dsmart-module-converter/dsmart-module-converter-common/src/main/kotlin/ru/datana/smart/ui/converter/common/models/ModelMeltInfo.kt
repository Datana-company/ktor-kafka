package ru.datana.smart.ui.converter.common.models

data class ModelMeltInfo(
    val id: String? = null,
    val timeStart: Long? = null,
    val meltNumber: String? = null,
    val steelGrade: String? = null,
    val crewNumber: String? = null,
    val shiftNumber: String? = null,
    val mode: Mode? = null,
    val devices: ModelMeltDevices? = null
) {

    enum class Mode(val value: String) {
        PROD("prod"),
        EMULATION("emulation"),
        TEST("test");
    }

    companion object {
        val NONE = ModelMeltInfo()
    }
}


//data class ConverterMeltInfo (
//    /* Уникальный идентификатор плавки. Строится из даты и номера плавки */
//    @JsonProperty("id")
//    val id: kotlin.String? = null,
//    /* Время начала плавки в миллисекундах от Unix-эпохи в GMT */
//    @JsonProperty("timeStart")
//    val timeStart: kotlin.Long? = null,
//    /* Номер плавки. Не уникален */
//    @JsonProperty("meltNumber")
//    val meltNumber: kotlin.String? = null,
//    /* Марка выплавляемой стали */
//    @JsonProperty("steelGrade")
//    val steelGrade: kotlin.String? = null,
//    /* Номер бригады, выполняющей плавку */
//    @JsonProperty("crewNumber")
//    val crewNumber: kotlin.String? = null,
//    /* Номер смены, в которую выполняется плавка */
//    @JsonProperty("shiftNumber")
//    val shiftNumber: kotlin.String? = null,
//    /* Режим, в котором прогоняется текущая плавка: боевая, эмуляция, тестовая */
//    @JsonProperty("mode")
//    val mode: ConverterMeltInfo.Mode? = null,
//    @JsonProperty("devices")
//    val devices: ConverterMeltDevices? = null
//) {
//
//    /**
//     * Режим, в котором прогоняется текущая плавка: боевая, эмуляция, тестовая
//     * Values: PROD,EMULATION,TEST
//     */
//
//    enum class Mode(val value: kotlin.String){
//        @JsonProperty(value="prod") PROD("prod"),
//        @JsonProperty(value="emulation") EMULATION("emulation"),
//        @JsonProperty(value="test") TEST("test");
//    }
//}
