package ru.datana.smart.ui.converter.app.common

import java.util.Timer
import kotlin.concurrent.schedule

class MetalRateEventGenerator(
    val timeout: Long = 0L,
    val minValue: Double = 0.0,
    val maxValue: Double = 1.0,
    val changeValue: Double = 0.05,
    var generateValue: Double = maxValue,
    var isActive: Boolean = false,
    private var isIncrease: Boolean = false,
    private var timer: Timer = Timer()
) {

    fun start() {
        if (isActive) {
            timer.cancel()
            timer = Timer()
        } else {
            isActive = true
        }
        timer.schedule(timeout) {
            generate()
            isActive = false
        }
    }

    private fun generate(): Double {
        if (isIncrease) {
            generateValue += changeValue
        } else {
            generateValue -= changeValue
        }

        if (generateValue >= maxValue) {
            if (generateValue > maxValue) {
                generateValue = maxValue
            }
            isIncrease = false
        } else if (generateValue <= minValue) {
            if (generateValue < maxValue) {
                generateValue = minValue
            }
            isIncrease = true
        }
        return generateValue
    }

    fun isNotActive(): Boolean = !isActive
}
