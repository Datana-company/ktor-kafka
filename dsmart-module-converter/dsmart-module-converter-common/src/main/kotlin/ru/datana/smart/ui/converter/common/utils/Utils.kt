package ru.datana.smart.ui.converter.common.utils

import java.lang.IllegalArgumentException
import kotlin.math.roundToInt

fun Double.toPercent(): Int {
    return if (this in 0.0..1.0) {
        (this * 100).roundToInt()
    } else {
        throw IllegalArgumentException("The value cannot be converted to percent. The value must be in the range from 0 to 1")
    }
}

fun Double?.isEmpty() = this == null || this == Double.MIN_VALUE

fun Double?.isNotEmpty() = !isEmpty()
