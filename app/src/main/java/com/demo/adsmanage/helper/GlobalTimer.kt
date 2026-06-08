package com.demo.adsmanage.helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.CountDownTimer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@SuppressLint("StaticFieldLeak")
object GlobalTimer {

    private const val PREFS = "global_timer_prefs"
    private const val KEY_END_TIME = "end_time"
    private const val KEY_LAST_DURATION = "last_duration"
    private const val KEY_HAS_FINISHED = "has_finished"

    private lateinit var context: Context
    private var countDown: CountDownTimer? = null
    var shouldStartGlobal = true

    private val _remainingMs = MutableStateFlow(TimerState("0", "00", "00"))
    val remainingMs = _remainingMs.asStateFlow()

    private var finishListeners = mutableListOf<() -> Unit>()

    fun init(appCtx: Context) {
        context = appCtx.applicationContext
    }

    fun startOrResume(
        shouldStart: Boolean,
        minutes: Int,
        oneTime: Boolean = true
    ) {
        shouldStartGlobal = shouldStart
        val prefs = prefs()
        val now = System.currentTimeMillis()

        val newDurationMs = minutes * 60 * 1000L
        val lastDuration = prefs.getLong(KEY_LAST_DURATION, -1L)
        val endTime = prefs.getLong(KEY_END_TIME, 0L)
        val hasFinished = prefs.getBoolean(KEY_HAS_FINISHED, false)

        val isNewDuration = lastDuration != newDurationMs
        val timeLeft = endTime - now

        if (oneTime && hasFinished && !isNewDuration) {
            // Block only if finished and same duration
            _remainingMs.tryEmit(TimerState("0", "00", "00"))
            emitFinish()
            return
        }

        if (!shouldStart) {
            stop()
            return
        }

        if (endTime <= now || isNewDuration) {
            // New start
            val newEndTime = now + newDurationMs
            prefs.edit()
                .putLong(KEY_END_TIME, newEndTime)
                .putLong(KEY_LAST_DURATION, newDurationMs)
                .putBoolean(KEY_HAS_FINISHED, false)
                .apply()
            runTimer(newDurationMs)
            return
        }

        // Resume
        if (timeLeft > 0) {
            runTimer(timeLeft)
        } else {
            _remainingMs.tryEmit(TimerState("0", "00", "00"))
            emitFinish()
        }
    }

    fun stop() {
        countDown?.cancel()
        _remainingMs.tryEmit(TimerState("0", "00", "00"))
        prefs().edit()
            .remove(KEY_END_TIME)
            .remove(KEY_LAST_DURATION)
            .remove(KEY_HAS_FINISHED)
            .apply()
        emitFinish()
    }

    fun onFinish(listener: () -> Unit) {
        finishListeners.add(listener)
    }

    private fun runTimer(ms: Long) {
        countDown?.cancel()
        countDown = object : CountDownTimer(ms, 10) {
            override fun onTick(ms: Long) {
                val m = ms / 60000
                val s = (ms % 60000) / 1000
                val ms2 = (ms % 1000) / 10
                _remainingMs.tryEmit(
                    TimerState(
                        "%02d".format(m),
                        "%02d".format(s),
                        "%02d".format(ms2)
                    )
                )
            }

            override fun onFinish() {
                _remainingMs.tryEmit(TimerState("0", "00", "00"))
                prefs().edit().putBoolean(KEY_HAS_FINISHED, true).apply()
                emitFinish()
            }
        }.start()
    }

    private fun emitFinish() {
        finishListeners.forEach { it.invoke() }
        finishListeners.clear()
    }

    fun hasFinished(): Boolean {
        return prefs().getBoolean(KEY_HAS_FINISHED, false)
    }

    private fun prefs(): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
