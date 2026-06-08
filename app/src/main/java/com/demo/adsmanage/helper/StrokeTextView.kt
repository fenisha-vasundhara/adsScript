package com.demo.adsmanage.helper

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class StrokeTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    var strokeColor: Int = Color.WHITE
    var strokeWidth: Float = 10f

    override fun onDraw(canvas: Canvas) {
        val originalColor = currentTextColor

        // Draw stroke
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        setTextColor(strokeColor)
        super.onDraw(canvas)

        // Draw shadow + fill
        paint.style = Paint.Style.FILL
        setTextColor(originalColor)
        super.onDraw(canvas)
    }
}
