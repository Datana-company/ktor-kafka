package ru.datana.smart.ui.converter.app.common

import java.util.Timer
import kotlin.concurrent.schedule

/**
 * Класс EventTimer
 *
 * Класс используется для повторного отображения рекомендаций через заданное время
 *
 * @property timeout время таймера в миллисекундах
 * @property tag тег рекомендации, по которому будет отсчитываться время до следующего отображения
 * @property isActive переменная, показывающая, происходит ли сейчас отсчёт времени
 * @property timer объект таймера
 */

class EventTimer(
    val timeout: Long = 0L,
    private var tag: String? = null,
    private var isActive: Boolean = false,
    private var timer: Timer = Timer()
) {

    /**
     * Если есть активный таймер, он сбрасывается.
     * По тегу рекомендации [tagRec] стартует новый таймер.
     */
    fun start(tagRec: String) {
        if (isActive) {
            timer.cancel()
            timer = Timer()
        } else {
            isActive = true
        }
        tag = tagRec
        timer.schedule(timeout) {
            isActive = false
        }
    }

    /**
     * Проверка есть ли по тегу рекомендации [ragRec] активный таймер
     * @return true = таймер активный, false = по тегу рекомендации таймер истёк, либо не существует
     */
    fun isNotActive(tagRec: String): Boolean {
        return if (tagRec == tag) {
            isActive
        } else {
            true
        }
    }
}
