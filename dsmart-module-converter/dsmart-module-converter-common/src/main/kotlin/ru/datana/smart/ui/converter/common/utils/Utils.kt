package ru.datana.smart.ui.converter.common.utils

import java.lang.IllegalArgumentException

fun toPercent(double: Double): Int {
    return if (double in 0.0..1.0) {
        (double * 100).toInt()
    } else {
        throw IllegalArgumentException("The value cannot be converted to percent. The value must be in the range from 0 to 1")
    }
}
