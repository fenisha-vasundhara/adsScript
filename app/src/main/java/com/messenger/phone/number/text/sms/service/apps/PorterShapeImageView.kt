package com.messenger.phone.number.text.sms.service.apps

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet

class PorterShapeImageView : PorterImageView {
    private var drawMatrix: Matrix? = null
    private var mMatrix: Matrix? = null
    private var shape: Drawable? = null

    constructor(context: Context) : super(context) {
        setup(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setup(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        setup(context, attrs, defStyle)
    }

    private fun setup(context: Context, attrs: AttributeSet?, defStyle: Int) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShaderImageView, defStyle, 0)
            shape = typedArray.getDrawable(R.styleable.ShaderImageView_siShape)
            typedArray.recycle()
        }
        this.mMatrix = Matrix()
    }

    fun setSiShape(drawable: Drawable?) {
        shape = drawable
        invalidate()
    }

    override fun paintMaskCanvas(maskCanvas: Canvas?, maskPaint: Paint?, width: Int, height: Int) {
        if (shape != null) {
            if (shape is BitmapDrawable) {
                configureBitmapBounds(width, height)
                if (drawMatrix != null) {
                    val drawableSaveCount = maskCanvas!!.saveCount
                    maskCanvas.save()
                    maskCanvas.concat(this.mMatrix)
                    shape?.draw(maskCanvas)
                    maskCanvas.restoreToCount(drawableSaveCount)
                    return
                }
            }
            shape!!.setBounds(0, 0, width, height)
            shape!!.draw(maskCanvas!!)
        }
    }

    private fun configureBitmapBounds(viewWidth: Int, viewHeight: Int) {
        val fits: Boolean
        drawMatrix = null
        val drawableWidth = shape!!.intrinsicWidth
        val drawableHeight = shape!!.intrinsicHeight
        fits = viewWidth == drawableWidth && viewHeight == drawableHeight
        if (drawableWidth > 0 && drawableHeight > 0 && !fits) {
            shape!!.setBounds(0, 0, drawableWidth, drawableHeight)
            val scale = Math.min(viewWidth.toFloat() / drawableWidth.toFloat(), viewHeight.toFloat() / drawableHeight.toFloat())
            val dx = ((viewWidth.toFloat() - drawableWidth.toFloat() * scale) * 0.5f + 0.5f).toInt().toFloat()
            val dy = ((viewHeight.toFloat() - drawableHeight.toFloat() * scale) * 0.5f + 0.5f).toInt().toFloat()
            this.mMatrix?.setScale(scale, scale)
            this.mMatrix?.postTranslate(dx, dy)
        }
    }
}