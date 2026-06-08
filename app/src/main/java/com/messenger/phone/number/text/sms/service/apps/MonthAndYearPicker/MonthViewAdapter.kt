package com.messenger.phone.number.text.sms.service.apps.MonthAndYearPicker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import com.messenger.phone.number.text.sms.service.apps.MonthAndYearPicker.MonthView.OnMonthClickListener
import com.messenger.phone.number.text.sms.service.apps.R
import java.util.Calendar

class MonthViewAdapter(private val _context: Context) : BaseAdapter() {
    private var _minMonth = 0
    private var _maxMonth = 0
    private var _activatedMonth = 0
    private var _colors: HashMap<String?, Int?>? = null
    private var mOnDaySelectedListener: OnDaySelectedListener? = null

    override fun getCount(): Int {
        return 1
    }

    override fun getItem(i: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
        val v: MonthView
        if (convertView != null) {
            v = convertView as MonthView
        } else {
            v = MonthView(_context)
            v.setColors(_colors!!)
            val params = AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT
            )
            v.layoutParams = params
            v.isClickable = true
            v.setOnMonthClickListener(mOnDayClickListener)
        }
        v.setBackgroundDrawable(_context.getDrawable(R.drawable.month_ripplr))
        v.setMonthParams(_activatedMonth, _minMonth, _maxMonth)
        v.reuse()
        v.invalidate()
        return v
    }

    private val mOnDayClickListener: OnMonthClickListener = object : OnMonthClickListener {
        override fun onMonthClick(view: MonthView?, day: Int) {
            Log.d("MonthViewAdapter", "onDayClick $day")
            if (isCalendarInRange(day)) {
                Log.d("MonthViewAdapter", "day not null && Calender in range $day")
                setSelectedMonth(day)
                if (mOnDaySelectedListener != null) {
                    mOnDaySelectedListener!!.onDaySelected(this@MonthViewAdapter, day)
                }
            }
        }
    }

    init {
        setRange()
    }


    fun isCalendarInRange(value: Int): Boolean {
        return value >= _minMonth && value <= _maxMonth
    }

    /**
     * Updates the selected day and related parameters.
     *
     * @param month The day to highlight
     */
    fun setSelectedMonth(month: Int) {
        Log.d("MonthViewAdapter", "setSelectedMonth : $month")
        _activatedMonth = month
        notifyDataSetChanged()
    }

    /* set min and max date and years*/
    fun setRange() {
        _minMonth = Calendar.JANUARY
        _maxMonth = Calendar.DECEMBER
        _activatedMonth = Calendar.AUGUST
        notifyDataSetInvalidated()
    }

    /**
     * Sets the listener to call when the user selects a day.
     *
     * @param listener The listener to call.
     */
    fun setOnDaySelectedListener(listener: OnDaySelectedListener?) {
        mOnDaySelectedListener = listener
    }

    interface OnDaySelectedListener {
        fun onDaySelected(view: MonthViewAdapter?, month: Int)
    }

    fun setMaxMonth(maxMonth: Int) {
        if (maxMonth <= Calendar.DECEMBER && maxMonth >= Calendar.JANUARY) {
            _maxMonth = maxMonth
        } else {
            throw IllegalArgumentException("Month out of range please send months between Calendar.JANUARY, Calendar.DECEMBER")
        }
    }


    fun setMinMonth(minMonth: Int) {
        if (minMonth >= Calendar.JANUARY && minMonth <= Calendar.DECEMBER) {
            _minMonth = minMonth
        } else {
            throw IllegalArgumentException("Month out of range please send months between Calendar.JANUARY, Calendar.DECEMBER")
        }
    }

    fun setActivatedMonth(activatedMonth: Int) {
        if (activatedMonth >= Calendar.JANUARY && activatedMonth <= Calendar.DECEMBER) {
            _activatedMonth = activatedMonth
        } else {
            throw IllegalArgumentException("Month out of range please send months between Calendar.JANUARY, Calendar.DECEMBER")
        }
    }

    fun setColors(map: HashMap<String?, Int?>?) {
        _colors = map
    }
}