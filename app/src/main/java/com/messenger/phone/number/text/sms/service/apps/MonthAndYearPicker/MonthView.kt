package com.messenger.phone.number.text.sms.service.apps.MonthAndYearPicker

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.widget.ListView
import com.messenger.phone.number.text.sms.service.apps.R
import java.text.DateFormatSymbols
import java.util.Locale

internal class MonthView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.style.MonthPickerDialogStyle
) : ListView(context, attrs, defStyleAttr) {
    // days to display
    private val _numDays = DEFAULT_NUM_DAYS
    private var _numCells = _numDays
    private var _numRows = DEFAULT_NUM_ROWS

    // layout padding
    private var _padding = 40
    private var _width = 0
    private var _rowHeight = DEFAULT_HEIGHT

    // paints
    private var _monthNumberPaint: Paint? = null
    private var _monthNumberDisabledPaint: Paint? = null
    private var _monthNumberSelectedPaint: Paint? = null

    // month
    private val _monthNames: Array<String> =
        DateFormatSymbols(Locale.getDefault()).shortMonths
    private val _monthTextSize: Int
    private val _monthHeaderSize: Int
    private var _monthSelectedCircleSize = 0
    private var _monthBgSelectedColor = 0
    private var _monthFontColorNormal = 0
    private var _monthFontColorSelected = 0
    private var _monthFontColorDisabled = 0
    private var _maxMonth = 0
    private var _minMonth = 0
    private val _rowHeightKey: Int
    private var _selectedMonth = -1

    // listener
    private var _onMonthClickListener: OnMonthClickListener? = null

    init {
        val displayMetrics = context.resources.displayMetrics

        _monthTextSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            16f, displayMetrics
        ).toInt()
        _monthHeaderSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            16f, displayMetrics
        ).toInt()

        _monthSelectedCircleSize =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    43f, displayMetrics
                ).toInt()
            } else {
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    43f, displayMetrics
                ).toInt()
            }

        _rowHeightKey = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            250f, displayMetrics
        ).toInt()
        _rowHeight = (_rowHeightKey - _monthHeaderSize) / MAX_NUM_ROWS

        _padding = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            16f, displayMetrics
        ).toInt()
    }

    /**
     * Sets up the text and style properties for painting.
     */
    private fun initView() {
        _monthNumberSelectedPaint = Paint()
        _monthNumberSelectedPaint!!.isAntiAlias = true
        if (_monthBgSelectedColor != 0) _monthNumberSelectedPaint!!.color = _monthBgSelectedColor
        // _monthNumberSelectedPaint.setAlpha(200);
        _monthNumberSelectedPaint!!.textAlign = Paint.Align.CENTER
        _monthNumberSelectedPaint!!.style = Paint.Style.FILL
        _monthNumberSelectedPaint!!.isFakeBoldText = true

        _monthNumberPaint = Paint()
        _monthNumberPaint!!.isAntiAlias = true
        if (_monthFontColorNormal != 0) _monthNumberPaint!!.color = _monthFontColorNormal
        _monthNumberPaint!!.textSize = _monthTextSize.toFloat()
        _monthNumberPaint!!.textAlign = Paint.Align.CENTER
        _monthNumberPaint!!.style = Paint.Style.FILL
        _monthNumberPaint!!.isFakeBoldText = false

        _monthNumberDisabledPaint = Paint()
        _monthNumberDisabledPaint!!.isAntiAlias = true
        if (_monthFontColorDisabled != 0) _monthNumberDisabledPaint!!.color =
            _monthFontColorDisabled
        _monthNumberDisabledPaint!!.textSize = _monthTextSize.toFloat()
        _monthNumberDisabledPaint!!.textAlign = Paint.Align.CENTER
        _monthNumberDisabledPaint!!.style = Paint.Style.FILL
        _monthNumberDisabledPaint!!.isFakeBoldText = false
    }

    override fun onDraw(canvas: Canvas) {
        drawDays(canvas)
    }

    /**
     * Draws the month days.
     */
    private fun drawDays(canvas: Canvas) {
        var y = (((_rowHeight + _monthTextSize) / 2) - DAY_SEPARATOR_WIDTH) + _monthHeaderSize
        val dayWidthHalf = (_width - _padding * 2) / (_numDays * 2)
        var j = 0
        for (month in _monthNames.indices) {
            val x = (2 * j + 1) * dayWidthHalf + _padding
            if (_selectedMonth == month) {
                canvas.drawCircle(
                    x.toFloat(),
                    (y - (_monthTextSize / 3)).toFloat(),
                    _monthSelectedCircleSize.toFloat(),
                    _monthNumberSelectedPaint!!
                )
                if (_monthFontColorSelected != 0) _monthNumberPaint!!.color =
                    _monthFontColorSelected
            } else {
                if (_monthFontColorNormal != 0) _monthNumberPaint!!.color = _monthFontColorNormal
            }

            val paint =
                if ((month < _minMonth || month > _maxMonth)) _monthNumberDisabledPaint else _monthNumberPaint
            canvas.drawText(_monthNames[month], x.toFloat(), y.toFloat(), paint!!)
            j++
            if (j == _numDays) {
                j = 0
                y += _rowHeight
            }
        }
    }


    /**
     * Calculates the day that the given x position is in, accounting for week
     * number. Returns the day or -1 if the position wasn't in a day.
     *
     * @param x The x position of the touch event
     * @return The day number, or -1 if the position wasn't in a day
     */
    private fun getMonthFromLocation(x: Float, y: Float): Int {
        val dayStart = _padding
        if (x < dayStart || x > _width - _padding) {
            return -1
        }
        // Selection is (x - start) / (pixels/day) == (x -s) * day / pixels
        val row = (y - _monthHeaderSize).toInt() / _rowHeight
        val column = ((x - dayStart) * _numDays / (_width - dayStart - _padding)).toInt()
        var day = column + 1
        day += row * _numDays
        if (day < 0 || day > _numCells) {
            return -1
        }
        // position - 1 to match with Calender.JANUARY and Calender.DECEMBER
        return day - 1
    }

    /**
     * Called when the user clicks on a day. Handles callbacks to the
     * [OnMonthClickListener] if one is set.
     *
     * @param day The day that was clicked
     */
    private fun onDayClick(day: Int) {
        if (_onMonthClickListener != null) {
            _onMonthClickListener!!.onMonthClick(this, day)
        }
    }

    fun setColors(colors: HashMap<String?, Int?>) {
        if (colors.containsKey("monthBgSelectedColor")) _monthBgSelectedColor =
            colors["monthBgSelectedColor"]!!
        if (colors.containsKey("monthFontColorNormal")) _monthFontColorNormal =
            colors["monthFontColorNormal"]!!
        if (colors.containsKey("monthFontColorSelected")) _monthFontColorSelected =
            colors["monthFontColorSelected"]!!
        if (colors.containsKey("monthFontColorDisabled")) _monthFontColorDisabled =
            colors["monthFontColorDisabled"]!!
        initView()
    }

    /**
     * Handles callbacks when the user clicks on a time object.
     */
    interface OnMonthClickListener {
        fun onMonthClick(view: MonthView?, month: Int)
    }

    fun setOnMonthClickListener(listener: OnMonthClickListener?) {
        _onMonthClickListener = listener
    }

    fun setMonthParams(selectedMonth: Int, minMonth: Int, maxMonth: Int) {
        _selectedMonth = selectedMonth
        this._minMonth = minMonth
        this._maxMonth = maxMonth
        _numCells = 12
    }

    fun reuse() {
        _numRows = DEFAULT_NUM_ROWS
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec), _rowHeight * _numRows
                    + (_monthHeaderSize * 2)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        _width = w
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                val day = getMonthFromLocation(event.x, event.y)
                if (day >= 0) {
                    onDayClick(day)
                }
            }
        }
        return true
    }

    companion object {
        // constants
        private const val DEFAULT_HEIGHT = 100
        private const val DEFAULT_NUM_DAYS = 4
        private const val DEFAULT_NUM_ROWS = 3
        private const val MAX_NUM_ROWS = 3
        private const val DAY_SEPARATOR_WIDTH = 1
    }
}