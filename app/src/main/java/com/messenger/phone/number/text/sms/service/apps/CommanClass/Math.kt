package com.messenger.phone.number.text.sms.service.apps.CommanClass

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.content.res.getColorOrThrow
import com.messenger.phone.number.text.sms.service.apps.R
import kotlin.math.roundToInt

/**
 * Returns the closest number divisible by [multipleOf].
 */
fun Int.roundToClosestMultipleOf(multipleOf: Int = 1) = (toDouble() / multipleOf).roundToInt() * multipleOf

fun Int.dpToPx(context: Context): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics).toInt()
}

fun Float.within(min: Float, max: Float): Float {
    return Math.min(max, Math.max(min, this))
}

val Context.materialColors: List<List<Int>>
    get() = listOf(
        R.array.material_red,
        R.array.material_pink,
        R.array.material_purple,
        R.array.material_deep_purple,
        R.array.material_indigo,
        R.array.material_blue,
        R.array.material_light_blue,
        R.array.material_cyan,
        R.array.material_teal,
        R.array.material_green,
        R.array.material_light_green,
        R.array.material_lime,
        R.array.material_yellow,
        R.array.material_amber,
        R.array.material_orange,
        R.array.material_deep_orange,
        R.array.material_brown,
        R.array.material_gray,
        R.array.material_blue_gray)
        .map { res -> resources.obtainTypedArray(res) }
        .map { typedArray -> (0 until typedArray.length()).map(typedArray::getColorOrThrow) }

private val minimumContrastRatio = 2

private val Context.primaryTextLuminance: Double
    get() = measureLuminance(getColorCompat(R.color.white))

fun Context.textPrimaryOnThemeForColor(color: Int): Int = color
    .let { theme -> measureLuminance(theme) }
    .let { themeLuminance -> primaryTextLuminance / themeLuminance }
    .let { contrastRatio -> contrastRatio < minimumContrastRatio }
    .let { contrastRatio -> if (contrastRatio) R.color.textPrimary else R.color.white }
    .let { res -> getColorCompat(res) }

fun Context.measureLuminance(color: Int): Double {
    val array = intArrayOf(Color.red(color), Color.green(color), Color.blue(color))
        .map { if (it < 0.03928) it / 12.92 else Math.pow((it + 0.055) / 1.055, 2.4) }

    return 0.2126 * array[0] + 0.7152 * array[1] + 0.0722 * array[2] + 0.05
}

fun Context.getColorCompat(colorRes: Int): Int {
    return tryOrNull { ContextCompat.getColor(this, colorRes) } ?: Color.BLACK
}

fun <T> tryOrNull(logOnError: Boolean = true, body: () -> T?): T? {
    return try {
        body()
    } catch (e: Exception) {
        null
    }
}