package com.messenger.phone.number.text.sms.service.apps

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

class PercentConstraintLayout : ConstraintLayout {
    constructor(context: Context?) : super(context!!)

    constructor(context: Context?, attrs: AttributeSet) : super(context!!, attrs)

     override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width: Int = MeasureSpec.getSize(widthMeasureSpec)
        val percent: Float = getResources().getDimension(R.dimen.nav_drawer_width)
        val newWidth = (width * percent).toInt()

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(newWidth, MeasureSpec.EXACTLY),
            heightMeasureSpec
        )
    }
}