package com.messenger.phone.number.text.sms.service.apps.CustomEditText.views;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.core.widget.NestedScrollView;

import com.messenger.phone.number.text.sms.service.apps.CustomEditText.util.ViewUtil;
import com.messenger.phone.number.text.sms.service.apps.R;

public class ScrollViewMaxHeight extends NestedScrollView {

    private int mMaxHeight;
    private int mWidthMeasureSpec;

    public ScrollViewMaxHeight(Context context) {
        super(context);
    }

    public ScrollViewMaxHeight(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ScrollViewMaxHeight,
                0, 0);

        try {
            mMaxHeight = a.getDimensionPixelSize(R.styleable.ScrollViewMaxHeight_maxHeight, ViewUtil.dpToPx(300));
        } finally {
            a.recycle();
        }
    }

    public void setMaxHeight(int height) {
        mMaxHeight = height;
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);
        measure(mWidthMeasureSpec, heightMeasureSpec);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try { mWidthMeasureSpec = widthMeasureSpec;

        // Only cap height if the original spec allows it to be larger than max
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.UNSPECIFIED || heightSize > mMaxHeight) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);
        }

        // Let super handle it with possibly adjusted spec
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (IllegalArgumentException e) {
            // Handle IllegalArgumentException if needed
            e.printStackTrace();
            // Call super.onMeasure with the original widthMeasureSpec and heightMeasureSpec
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (Exception e) {
            // Re-throw any other exceptions
            throw e;
        }
    }

}
