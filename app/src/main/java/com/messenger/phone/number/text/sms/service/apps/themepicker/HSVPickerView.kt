package com.messenger.phone.number.text.sms.service.apps.themepicker

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.demo.adsmanage.Commen.log
import com.messenger.phone.number.text.sms.service.apps.CommanClass.within
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.HsvPickerViewBinding
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

class HSVPickerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    val selectedColor: Subject<Int> = BehaviorSubject.create()

    private val hues =
        arrayOf(0xFFFF0000, 0xFFFFFF00, 0xFF00FF00, 0xFF00FFFF, 0xFF0000FF, 0xFFFF00FF, 0xFFFF0000)
            .map { it.toInt() }.toIntArray()

    private var binding: HsvPickerViewBinding

    var selectedcolor: ((Int) -> Unit)? = null

    private var min: Float = 0f
    private var max = 0f

    private var hue = 0f
        set(value) {
            field = value
            updateHue()
        }

    init {

        binding = HsvPickerViewBinding.inflate(LayoutInflater.from(context), this)

        var swatchX = 0f
        var swatchY = 0f

        with(binding) {
            saturation.setOnTouchListener { _, event ->
                setupBounds()
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        swatchX = event.x - event.rawX
                        swatchY = event.y - event.rawY
                        parent.requestDisallowInterceptTouchEvent(true)
                    }

                    MotionEvent.ACTION_MOVE -> {
                        // Calculate the new x/y position
                        swatch.x = (event.rawX + swatchX + min).within(min, max)
                        swatch.y = (event.rawY + swatchY + min).within(min, max)

                        updateSelectedColor()
                    }

                    MotionEvent.ACTION_UP -> {
                        parent.requestDisallowInterceptTouchEvent(false)
                    }

                    else -> return@setOnTouchListener false
                }
                true
            }
            var hueThumbX = 0f
            hueGroup.setOnTouchListener { _, event ->
                setupBounds()
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        hueThumbX = event.x - event.rawX
                        parent.requestDisallowInterceptTouchEvent(true)
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val x = (event.rawX + hueThumbX + min).within(min, max)

                        hueThumb.x = x
                        hue = (hueThumb.x - min) / (max - min) * 360

                        updateSelectedColor()
                    }

                    MotionEvent.ACTION_UP -> {
                        parent.requestDisallowInterceptTouchEvent(false)
                    }

                    else -> return@setOnTouchListener false
                }
                true
            }
            hueTrack.clipToOutline = true
            hueTrack.setImageDrawable(
                GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    hues
                )
            )
        }


    }

    private fun setupBounds() {
        if (min == 0f || max == 0f) {
            min = binding.saturation.x - binding.swatch.width / 2
            max = min + binding.saturation.width
        }
    }

    private fun updateSelectedColor() {
        setupBounds()

        val range = max - min
        val hsv = floatArrayOf(
            hue,
            (binding.swatch.x - min) / range,
            1 - (binding.swatch.y - min) / range
        )
        val color = Color.HSVToColor(hsv)
        binding.swatch.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        selectedColor.onNext(color)
    }

    fun setColor(color: Int) {
        // Convert the rgb color to HSV
        val hsv = FloatArray(3).apply {
            Color.colorToHSV(color, this)
            hue = this[0]
        }

        // Set the position of the swatch
        post {
            setupBounds()
            val range = max - min

            binding.hueThumb.x = range * hsv[0] / 360 + min
            binding.swatch.x = range * hsv[1] + min
            binding.swatch.y = range * (1 - hsv[2]) + min

            updateSelectedColor()
        }
    }

    private fun updateHue() {
        val hsv = floatArrayOf(hue, 1f, 1f)
        val tint = Color.HSVToColor(hsv)
        val bg = binding.saturation.background
        if (bg != null) {
            bg.mutate().setTint(tint)
        } else {
            binding.saturation.setBackgroundColor(tint)
        }
    }

}
