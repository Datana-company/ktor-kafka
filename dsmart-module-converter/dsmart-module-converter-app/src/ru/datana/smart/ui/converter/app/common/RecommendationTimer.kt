package ru.datana.smart.ui.converter.app.common

import java.util.Timer
import kotlin.concurrent.schedule

class RecommendationTimer(
    val timeout: Long = 0L,
    private var tag: String? = null,
    private var isActivity: Boolean = true,
    private var timer: Timer = Timer()
) {

    fun start(tagRec: String) {
        if (!isActivity) {
            timer.cancel()
            timer = Timer()
        } else {
            isActivity = false
        }
        tag = tagRec
        timer.schedule(timeout) {
            isActivity = true
        }
    }

    fun isTimeOver(tagRec: String): Boolean {
        return if (tagRec == tag) {
            isActivity
        } else {
            true
        }
    }

    companion object {
        fun getInstance(delay: Long): RecommendationTimer = RecommendationTimer(delay)
    }
}
