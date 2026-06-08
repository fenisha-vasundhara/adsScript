package com.messenger.phone.number.text.sms.service.apps

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.view.ViewCompat


abstract class PorterImageView : TouchImageView {
    private var drawableBitmap: Bitmap? = null
    private var drawableCanvas: Canvas? = null
    private var drawablePaint: Paint? = null
    private var invalidated = true
    private var maskBitmap: Bitmap? = null
    private var maskCanvas: Canvas? = null
    private var maskPaint: Paint? = null
    private var square = false
    var isRotate = true

    private val savedMatrix = Matrix()

    private val MIN_ZOOM = 1.0f
    private val MAX_ZOOM = 3.0f

    // we can be in one of these 3 states
    private val NONE = 0
    private val DRAG = 1
    private val ZOOM = 2
    private var mode = NONE

    // remember some things for zooming
    private val start = PointF()
    private val mid = PointF()
    private var oldDist = 1f
    private var d = 0f
    private var newRot = 0f
    private var lastEvent: FloatArray? = null
    var rotation = true
    var scaledown = true
    var initStatus = false
    var init_scale = 0f


    var scalediff = 0f

    /*
     * Use onSizeChanged() to calculate values based on the view's size.
     * The view has no size during init(), so we must wait for this
     * callback.
     */


    /*
     * Use onSizeChanged() to calculate values based on the view's size.
     * The view has no size during init(), so we must wait for this
     * callback.
     */
    /*
     * Operate on two-finger events to rotate the image.
     * This method calculates the change in angle between the
     * pointers and rotates the image accordingly.  As the user
     * rotates their fingers, the image will follow.
     */
    var parms: RelativeLayout.LayoutParams? = null
    var startwidth = 0
    var startheight = 0
    var dx = 0f
    var dy = 0f
    var cx = 0f
    var cy = 0f

    var angle = 0f


    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val view = v as ImageView
        (view.drawable as BitmapDrawable).setAntiAlias(true)
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                parms = view.layoutParams as RelativeLayout.LayoutParams
                startwidth = parms!!.width
                startheight = parms!!.height
                dx = event.rawX - parms!!.leftMargin
                dy = event.rawY - parms!!.topMargin
//                mode = DRAG
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                if (oldDist > 10f) {
                    mode = ZOOM
                }
                d = rotation(event)
            }
            MotionEvent.ACTION_UP -> {
            }
            MotionEvent.ACTION_POINTER_UP -> mode = NONE
            MotionEvent.ACTION_MOVE -> if (mode == DRAG) {
                cx = event.rawX
                cy = event.rawY
                parms!!.leftMargin = (cx - dx) as Int
                parms!!.topMargin = (cy - dy) as Int
                parms!!.rightMargin = 0
                parms!!.bottomMargin = 0
                parms!!.rightMargin = parms!!.leftMargin + 5 * parms!!.width
                parms!!.bottomMargin = parms!!.topMargin + 10 * parms!!.height
                view.layoutParams = parms
            } else if (mode == ZOOM) {
                if (isRotate) {
                    if (event.pointerCount == 2) {
                        newRot = rotation(event)
                        val r = newRot - d
                        angle = r
                        cx = event.rawX
                        cy = event.rawY
                        val newDist = spacing(event)
                        if (newDist > 10f) {
                            val scale = newDist / oldDist * view.scaleX

                            if (scale > 0.6) {
                                scalediff = scale
                                view.scaleX = scale
                                view.scaleY = scale
                            }
                        }

                        view.animate().rotationBy(angle.toFloat()).setDuration(0).setInterpolator(LinearInterpolator()).start()
                        Log.d(TAG, "onTouch: dssd $scalediff")
                        cx = event.rawX
                        cy = event.rawY
                        parms!!.leftMargin = ((cx - dx + scalediff).toInt())
                        parms!!.topMargin = ((cy - dy + scalediff).toInt())
                        parms!!.rightMargin = 0
                        parms!!.bottomMargin = 0
                        parms!!.rightMargin = parms!!.leftMargin + 5 * parms!!.width
                        parms!!.bottomMargin = parms!!.topMargin + 10 * parms!!.height
                        view.layoutParams = parms
                    }
                } else {
                    Log.d(TAG, "onTouch: CCCC $angle")
                    view.scaleX = 1f
                    view.scaleY = 1f
                    if (angle != view.rotation)
                        view.animate().rotation(0f).start()

                }
            }
        }
        return true
    }


    fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point[x / 2] = y / 2
    }
    fun clearView() {
        scaleX = 1f
        scaleY = 1f
        if (angle != this.getRotation())
            animate().rotation(0f).start()
    }


    open fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }


    open fun rotation(event: MotionEvent): Float {
        val delta_x = (event.getX(0) - event.getX(1)).toDouble()
        val delta_y = (event.getY(0) - event.getY(1)).toDouble()
        val radians = Math.atan2(delta_y, delta_x)
        return Math.toDegrees(radians).toFloat()
    }

    /* access modifiers changed from: protected */
    abstract fun paintMaskCanvas(canvas: Canvas?, paint: Paint?, i: Int, i2: Int)

    constructor(context: Context) : super(context) {
        setup(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setup(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        setup(context, attrs, defStyle)
    }

    fun setSquare(square2: Boolean) {
        square = square2
    }


    private fun setup(context: Context, attrs: AttributeSet?, defStyle: Int) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShaderImageView, defStyle, 0)
            square = typedArray.getBoolean(R.styleable.ShaderImageView_siSquare, false)
            typedArray.recycle()
        }
        if (scaleType == ScaleType.FIT_CENTER) {
            scaleType = ScaleType.CENTER_CROP
        }
        maskPaint = Paint(1)
        maskPaint!!.color = ViewCompat.MEASURED_STATE_MASK
    }

    override fun invalidate() {
        invalidated = true
        super.invalidate()
    }

    public override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createMaskCanvas(w, h, oldw, oldh)
    }

    private fun createMaskCanvas(width: Int, height: Int, oldw: Int, oldh: Int) {
        val sizeChanged: Boolean
        var isValid = false
        sizeChanged = if (width == oldw && height == oldh) {
            false
        } else {
            true
        }
        if (width > 0 && height > 0) {
            isValid = true
        }
        if (!isValid) {
            return
        }
        if (maskCanvas == null || sizeChanged) {
            System.gc()
            maskCanvas = Canvas()
            maskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            maskCanvas!!.setBitmap(maskBitmap)
            maskPaint!!.reset()
            paintMaskCanvas(maskCanvas, maskPaint, width, height)
            drawableCanvas = Canvas()
            drawableBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            drawableCanvas!!.setBitmap(drawableBitmap)
            drawablePaint = Paint(1)
            invalidated = true
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (!isInEditMode) {
            val saveCount = canvas.saveLayer(0.0f, 0.0f, width.toFloat(), height.toFloat(), null, Canvas.ALL_SAVE_FLAG)
            try {
                if (invalidated) {
                    val drawable = drawable
                    if (drawable != null) {
                        invalidated = false
                        val imageMatrix = imageMatrix
                        if (imageMatrix == null) {
                            drawable.draw(drawableCanvas!!)
                        } else {
                            val drawableSaveCount = drawableCanvas!!.saveCount
                            drawableCanvas!!.save()
                            drawableCanvas!!.concat(imageMatrix)
                            drawable.draw(drawableCanvas!!)
                            drawableCanvas!!.restoreToCount(drawableSaveCount)
                        }
                        drawablePaint!!.reset()
                        drawablePaint!!.isFilterBitmap = false
                        drawablePaint!!.xfermode = PORTER_DUFF_XFERMODE
                        drawableCanvas!!.drawBitmap(maskBitmap!!, 0.0f, 0.0f, drawablePaint)
                    }
                }
                if (!invalidated) {
                    drawablePaint!!.xfermode = null
                    canvas.drawBitmap(drawableBitmap!!, 0.0f, 0.0f, drawablePaint)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception occured while drawing $id", e)
            } finally {
                canvas.restoreToCount(saveCount)
            }
        } else {
            super.onDraw(canvas)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (square) {
            val dimen = Math.min(measuredWidth, measuredHeight)
            setMeasuredDimension(dimen, dimen)
        }
    }


    companion object {
        private val PORTER_DUFF_XFERMODE = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        private val TAG = PorterImageView::class.java.simpleName
    }
}