package com.messenger.phone.number.text.sms.service.apps

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config

class MSTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    init {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MSTextView,
            0, 0
        )
        val fontFamily = a.getString(R.styleable.MSTextView_customFontFamily)
        val isSystemFont = a.getBoolean(R.styleable.MSTextView_isSystemFont, false)
        if (context.config.SystemTextViewSwitchAb) {
            this.typeface = Typeface.DEFAULT
        } else {
            fontFamily?.let {
                val typeface = ResourcesCompat.getFont(context, context.resources.getIdentifier(it, "font", context.packageName))
                this.typeface = typeface ?: Typeface.DEFAULT
            }
        }
        a.recycle()
    }
}