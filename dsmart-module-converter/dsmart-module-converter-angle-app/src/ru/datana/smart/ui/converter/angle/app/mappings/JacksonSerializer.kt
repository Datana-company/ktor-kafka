package ru.datana.smart.ui.converter.angle.app.mappings

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper

val jacksonSerializer: ObjectMapper = ObjectMapper()
    .configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true).configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
    // Если в десериализуемом JSON-е встретится поле, которого нет в классе,
    // то не будет выброшено исключение UnrecognizedPropertyException,
    // т.е. мы отменяем проверку строгого соответствия JSON и класса
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
