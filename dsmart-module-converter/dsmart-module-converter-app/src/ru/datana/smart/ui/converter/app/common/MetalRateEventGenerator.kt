package ru.datana.smart.ui.converter.app.common

import ru.datana.smart.ui.converter.backend.ConverterFacade
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.ModelAngles
import ru.datana.smart.ui.converter.common.models.ModelSlagRate
import java.util.Timer
import kotlin.concurrent.schedule

/**
 * Класс MetalRateEventGenerator
 *
 * Класс используется для генерации значения содержания металла через заданное время
 *
 * @property timeout время таймера в миллисекундах
 * @property minValue минимальное значение, которое может принимать генерированное значение
 * @property maxValue максимальное значение, которое может принимать генерированное значение
 * @property changeValue значение, на которое будет изменяться генерированное значение через заданное время
 */
class MetalRateEventGenerator(
    val timeout: Long = 5000L,
    val minValue: Double = 0.0,
    val maxValue: Double = 1.0,
    val changeValue: Double = 0.05,
    val converterFacade: ConverterFacade
) {

    /**
     * Сгенерированное значение
     */
    var generateValue: Double = maxValue

    /**
     * Переменная, имеет значение true, пока сгенерированное число не достигнет максимального значения,
     * и false, пока сгенерированное число не достигнет минимального значения
     */
    private var isIncrease: Boolean = false

    /**
     * Константное значение задержки таймера
     */
    private val delay: Long = 5000L
    /**
     * Запускается таймер, который каждый раз через заданное время
     * будет возвращать сгенерированное значение
     */
    suspend fun start() {
//        Timer().schedule(delay, timeout) {
//          generate()
//        }
        generate()
        val context = ConverterBeContext(
            angles = ModelAngles(generateValue),
            slagRate = ModelSlagRate(generateValue, 1 - generateValue)
        )
        converterFacade.handleEvents(context)
    }

    /**
     * Метод генерирует значение содержания металла
     */
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
}
