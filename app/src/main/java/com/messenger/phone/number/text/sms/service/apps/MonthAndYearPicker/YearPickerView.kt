package com.messenger.phone.number.text.sms.service.apps.MonthAndYearPicker

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.messenger.phone.number.text.sms.service.apps.R

class YearPickerView(private val _context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    ListView(
        _context, attrs, defStyleAttr
    ) {
    lateinit var _adapter: YearAdapter
    val _viewSize: Int
    val _childSize: Int
    var _onYearSelectedListener: OnYearSelectedListener? = null
    var _colors: HashMap<String?, Int?>? = null

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : this(
        context,
        attrs,
        R.style.AppTheme
    ) {
        super.setSelector(android.R.color.transparent)
    }

    init {
        val frame = LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
        )
        layoutParams = frame
        val res = _context.resources
        _viewSize = res.getDimensionPixelOffset(R.dimen.datepicker_view_animator_height)
        _childSize = res.getDimensionPixelOffset(R.dimen.datepicker_year_label_height)
        onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val year = _adapter.getYearForPosition(position)
            _adapter.setSelection(year)
            if (_onYearSelectedListener != null) {
                _onYearSelectedListener!!.onYearChanged(this@YearPickerView, year)
            }
        }
        _adapter = YearAdapter(context)
        adapter = _adapter
    }

    fun setOnYearSelectedListener(listener: OnYearSelectedListener?) {
        _onYearSelectedListener = listener
    }

    /**
     * Sets the currently selected year. Jumps immediately to the new year.
     *
     * @param year the target year
     */
    fun setYear(year: Int) {
        _adapter.setSelection(year)
        post {
            val position = _adapter.getPositionForYear(year)
            if (position >= 0 /*&& position < getCount()*/) {
                setSelectionCentered(position)
            }
        }
    }

    fun setSelectionCentered(position: Int) {
        val offset = _viewSize / 2 - _childSize / 2
        setSelectionFromTop(position, offset)
    }

    fun setRange(min: Int, max: Int) {
        _adapter.setRange(min, max)
    }

    fun setColors(colors: HashMap<String?, Int?>?) {
        this._colors = colors
    }

    inner class YearAdapter(context: Context?) : BaseAdapter() {
        private val ITEM_LAYOUT = R.layout.year_label_text_view
        private val __inflater: LayoutInflater = LayoutInflater.from(context)
        private var __activatedYear = 0
        private var __minYear = 0
        private var __maxYear = 0
        private var __count = 0

        fun setRange(min: Int, max: Int) {
            val count = max - min + 1
            if (__minYear != min || __maxYear != max || __count != count) {
                __minYear = min
                __maxYear = max
                __count = count
                notifyDataSetInvalidated()
            }
        }

        fun setSelection(year: Int): Boolean {
            if (__activatedYear != year) {
                __activatedYear = year
                notifyDataSetChanged()
                return true
            }
            return false
        }

        override fun getCount(): Int {
            return __count
        }

        override fun getItem(position: Int): Int {
            return getYearForPosition(position)
        }

        override fun getItemId(position: Int): Long {
            return getYearForPosition(position).toLong()
        }

        fun getPositionForYear(year: Int): Int {
            return year - __minYear
        }

        fun getYearForPosition(position: Int): Int {
            return __minYear + position
        }

        override fun hasStableIds(): Boolean {
            return true
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v: TextView
            val hasNewView = convertView == null
            v = if (hasNewView) {
                __inflater.inflate(ITEM_LAYOUT, parent, false) as TextView
            } else {
                convertView as TextView
            }
            val year = getYearForPosition(position)
            val activated = __activatedYear == year

            if (hasNewView || v.tag != null || v.tag == activated) {
                if (activated) {
                    if (_colors!!.containsKey("monthBgSelectedColor")) {
                        v.setTextColor(_colors!!["monthBgSelectedColor"]!!)
                    }
                    v.textSize = 25f
                } else {
                    if (_colors!!.containsKey("monthFontColorNormal")) {
                        v.setTextColor(_colors!!["monthFontColorNormal"]!!)
                    }
                    v.textSize = 20f
                }
                v.tag = activated
            }
            v.text = year.toString()
            return v
        }

        override fun getItemViewType(position: Int): Int {
            return 0
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun isEmpty(): Boolean {
            return false
        }

        override fun areAllItemsEnabled(): Boolean {
            return true
        }

        override fun isEnabled(position: Int): Boolean {
            return true
        }

        fun setMaxYear(maxYear: Int) {
            __maxYear = maxYear
            __count = __maxYear - __minYear + 1
            notifyDataSetInvalidated()
        }

        fun setMinYear(minYear: Int) {
            __minYear = minYear
            __count = __maxYear - __minYear + 1
            notifyDataSetInvalidated()
        }

        fun setActivatedYear(activatedYear: Int) {
            if (activatedYear >= __minYear && activatedYear <= __maxYear) {
                __activatedYear = activatedYear
                setYear(activatedYear)
            } else {
                throw IllegalArgumentException("activated date is not in range")
            }
        }
    }

    val firstPositionOffset: Int
        get() {
            val firstChild = getChildAt(0) ?: return 0
            return firstChild.top
        }

    /**
     * The callback used to indicate the user changed the year.
     */
    interface OnYearSelectedListener {
        /**
         * Called upon a year change.
         *
         * @param view The view associated with this listener.
         * @param year The year that was set.
         */
        fun onYearChanged(view: YearPickerView?, year: Int)
    }

    fun setMinYear(minYear: Int) {
        _adapter.setMinYear(minYear)
    }

    fun setMaxYear(maxYear: Int) {
        _adapter.setMaxYear(maxYear)
    }

    fun setActivatedYear(activatedYear: Int) {
        _adapter.setActivatedYear(activatedYear)
    }
}
