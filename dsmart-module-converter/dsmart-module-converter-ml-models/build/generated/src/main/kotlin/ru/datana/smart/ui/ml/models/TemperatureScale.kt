/**
* Транспортные модели для Конвертера
* Транспортные модели для связи различных компонентов IoT-прототипа конвертера
*
* The version of the OpenAPI document: 0.0.1
* Contact: okatov@datana.ru
*
* NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
* https://openapi-generator.tech
* Do not edit the class manually.
*/
package ru.datana.smart.ui.ml.models


import com.fasterxml.jackson.annotation.JsonProperty

/**
* Шкала температуры: C - Цельсия, K - Кельвина, F - Фаренгейта
* Values: C,K,F
*/

enum class TemperatureScale(val value: kotlin.String){


    @JsonProperty(value = "c")
    C("c"),


    @JsonProperty(value = "k")
    K("k"),


    @JsonProperty(value = "f")
    F("f");



	/**
	This override toString avoids using the enum var name and uses the actual api value instead.
	In cases the var name and value are different, the client would send incorrect enums to the server.
	**/
	override fun toString(): String {
        return value
    }

}

