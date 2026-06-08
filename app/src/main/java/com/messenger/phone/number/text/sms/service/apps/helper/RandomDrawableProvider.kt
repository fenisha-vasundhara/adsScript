package com.messenger.phone.number.text.sms.service.apps.helper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.VectorDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.Shape
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.demo.adsmanage.Commen.log
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.drawableCachecolor
import com.messenger.phone.number.text.sms.service.apps.R
import kotlin.random.Random

class RandomDrawableProvider(private val context: Context) {
    private val drawableList = listOf(
        R.drawable.contacticon_1,
        R.drawable.contacticon_2,
        R.drawable.contacticon_3,
        R.drawable.contacticon_4,
        R.drawable.contacticon_5,
        R.drawable.contacticon_6,
        R.drawable.contacticon_7,
        R.drawable.contacticon_8,
        R.drawable.contacticon_9,
        R.drawable.contacticon_10,
        R.drawable.contacticon_11,
        R.drawable.contacticon_12,
        R.drawable.contacticon_13,
        R.drawable.contacticon_14,
        R.drawable.contacticon_15,
        R.drawable.contacticon_16,
    )

    private val drawableListWithName = listOf(
        R.color.contacticon_1,
        R.color.contacticon_2,
        R.color.contacticon_3,
        R.color.contacticon_4,
        R.color.contacticon_5,
        R.color.contacticon_6,
        R.color.contacticon_7,
        R.color.contacticon_8,
        R.color.contacticon_9,
        R.color.contacticon_10,
        R.color.contacticon_11,
        R.color.contacticon_12,
        R.color.contacticon_13,
        R.color.contacticon_14,
        R.color.contacticon_15,
        R.color.contacticon_16
    )

    fun isColorLight(color: Int): Boolean {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        val luminance = (0.2126 * r + 0.7152 * g + 0.0722 * b) / 255
        return luminance > 0.5
    }

    fun darkenColor(color: Int): Int {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        val factor = 1.8f
        val newR = (r * factor).toInt()
        val newG = (g * factor).toInt()
        val newB = (b * factor).toInt()
        return Color.rgb(newR, newG, newB)
    }

    fun lightenColor(color: Int): Int {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        val factor = 1.2f
        val newR = (r * factor).coerceAtMost(255f).toInt()
        val newG = (g * factor).coerceAtMost(255f).toInt()
        val newB = (b * factor).coerceAtMost(255f).toInt()
        return Color.rgb(newR, newG, newB)
    }

    fun getDarkOrLightColor(color: Int): Int {
        return if (isColorLight(color)) {
           darkenColor(color)
        } else {
            Color.WHITE
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getRandomDrawable(treadId: Long = -0L): Drawable {
        val profilecolordata =
            context.config.getProfileThemeColor(treadId.toString())
        if (profilecolordata != null) {
            val ovalShape = ShapeDrawable(OvalShape())
            ovalShape.paint.color = profilecolordata.color
            val imageDrawable: Drawable =
                context.resources.getDrawable(R.drawable.contacticon_trasparant)
            val tintcolor = getDarkOrLightColor(profilecolordata.color)
            imageDrawable.setTint(tintcolor)
            val layerDrawable = LayerDrawable(arrayOf(ovalShape, imageDrawable))
            return layerDrawable
        } else {
            if (context.config.Systemgeneratediconswitchab) {
                val randomIndex = Random.nextInt(0, drawableList.size)
                val randomDrawableId = drawableList[randomIndex]
                drawableCachecolor[treadId.toString()] = drawableListWithName[randomIndex]
                return context.getDrawable(randomDrawableId)
                    ?: throw RuntimeException("Drawable not found")
            } else {
                "themeselectedcolor <------------> ${context.config.themeselectedcolor}".log()
                val ovalShape = ShapeDrawable(OvalShape())
                ovalShape.paint.color = context.config.themeselectedcolor
                val imageDrawable: Drawable =
                    context.resources.getDrawable(R.drawable.contacticon_trasparant)
                val layerDrawable = LayerDrawable(arrayOf(ovalShape, imageDrawable))
                return layerDrawable
            }
        }
    }


    fun getRandomColorDrawable(): Drawable {
        if (context.config.Systemgeneratediconswitchab) {
            val randomIndex = Random.nextInt(0, drawableList.size)
            val randomDrawableId = drawableList[randomIndex]
            return context.getDrawable(randomDrawableId)
                ?: throw RuntimeException("Drawable not found")
        } else {
            "themeselectedcolor <------------> ${context.config.themeselectedcolor}".log()
            val ovalShape = ShapeDrawable(OvalShape())
            ovalShape.paint.color = context.config.themeselectedcolor
            val imageDrawable: Drawable =
                context.resources.getDrawable(R.drawable.contacticon_trasparant)
            val layerDrawable = LayerDrawable(arrayOf(ovalShape, imageDrawable))
            return layerDrawable
        }
    }
}