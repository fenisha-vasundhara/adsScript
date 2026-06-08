package com.messenger.phone.number.text.sms.service.apps.CommanClass

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.messenger.phone.number.text.sms.service.apps.MonthPickerView
import com.messenger.phone.number.text.sms.service.apps.R
import com.simplemobiletools.commons.extensions.getContrastColor

fun MaterialDatePicker<*>.applyPrimaryColor(context: Context) {
    val dialog = dialog ?: return
    val primary = context.getProperPrimaryColor()
    val onPrimary = primary.getContrastColor()

    dialog.findViewById<android.view.View>(com.google.android.material.R.id.mtrl_picker_header)
        ?.setBackgroundColor(primary)

    dialog.findViewById<TextView>(com.google.android.material.R.id.mtrl_picker_header_selection_text)
        ?.setTextColor(onPrimary)
    dialog.findViewById<TextView>(com.google.android.material.R.id.mtrl_picker_title_text)
        ?.setTextColor(onPrimary)

    dialog.findViewById<ImageView>(com.google.android.material.R.id.mtrl_picker_header_toggle)
        ?.imageTintList = ColorStateList.valueOf(onPrimary)


    dialog.findViewById<Button>(com.google.android.material.R.id.confirm_button)
        ?.setTextColor(primary)
    dialog.findViewById<Button>(com.google.android.material.R.id.cancel_button)
        ?.setTextColor(primary)

    val root = dialog.window?.decorView ?: return
    if (root.getTag(R.id.picker_primary_applied_tag) != true) {
        root.setTag(R.id.picker_primary_applied_tag, true)
        root.viewTreeObserver.addOnGlobalLayoutListener {
            applyCalendarDayStyling(root, context)
            applyDateRangeInputStyling(dialog, context)
        }
    }
    applyCalendarDayStyling(root, context)
    applyDateRangeInputStyling(dialog, context)
}

private fun applyDateRangeInputStyling(dialog: android.app.Dialog, context: Context) {
    val primary = context.getProperPrimaryColor()
    val textColor = context.getProperTextColor()

    val activeAndDefault = ColorStateList(
        arrayOf(intArrayOf(android.R.attr.state_focused), intArrayOf()),
        intArrayOf(primary, primary)
    )
    val strokeColors = ColorStateList(
        arrayOf(
            intArrayOf(android.R.attr.state_focused),
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf()
        ),
        intArrayOf(primary, primary.adjustAlpha(0.38f), primary)
    )

    val startInputLayout = dialog.findViewById<TextInputLayout>(
        com.google.android.material.R.id.mtrl_picker_text_input_range_start
    )
    val endInputLayout = dialog.findViewById<TextInputLayout>(
        com.google.android.material.R.id.mtrl_picker_text_input_range_end
    )

    listOf(startInputLayout, endInputLayout).forEach { layout ->
        layout ?: return@forEach
        layout.setBoxStrokeColorStateList(strokeColors)
        layout.defaultHintTextColor = activeAndDefault
        layout.hintTextColor = activeAndDefault

        layout.editText?.let { editText ->
            editText.setTextColor(textColor)
            editText.setHintTextColor(primary)
            try {
                editText.setCursorColorProgrammaticallyet(primary)
            } catch (_: Exception) {
            }
        }
    }
}

fun MonthPickerView.applyPrimaryColor(context: Context) {
    val primary = context.getProperPrimaryColor()
    val onPrimary = primary.getContrastColor()
    val background = context.getDialogBackgroundColor()
    val textColor = context.getProperTextColor()
    val secondaryText = context.getProperSecondaryTextColor()

    val header =
        findViewById<android.view.View>(com.messenger.phone.number.text.sms.service.apps.R.id.header)
    val pickerBg =
        findViewById<android.view.View>(com.messenger.phone.number.text.sms.service.apps.R.id.picker_view)
    val actionBg =
        findViewById<android.view.View>(com.messenger.phone.number.text.sms.service.apps.R.id.action_btn_lay)
    val ok = findViewById<TextView>(com.messenger.phone.number.text.sms.service.apps.R.id.ok_action)
    val cancel =
        findViewById<TextView>(com.messenger.phone.number.text.sms.service.apps.R.id.cancel_action)
    val month = findViewById<TextView>(com.messenger.phone.number.text.sms.service.apps.R.id.month)
    val year = findViewById<TextView>(com.messenger.phone.number.text.sms.service.apps.R.id.year)
    val title = findViewById<TextView>(com.messenger.phone.number.text.sms.service.apps.R.id.title)

    header?.setBackgroundColor(primary)
    pickerBg?.setBackgroundColor(background)
    actionBg?.setBackgroundColor(background)
    ok?.setTextColor(primary)
    cancel?.setTextColor(primary)
    title?.setTextColor(onPrimary)

    _headerFontColorSelected = onPrimary
    _headerFontColorNormal = secondaryText
    month?.setTextColor(onPrimary)
    year?.setTextColor(secondaryText)

    val colors = HashMap<String?, Int?>().apply {
        put("monthBgColor", background)
        put("monthBgSelectedColor", primary)
        put("monthFontColorNormal", textColor)
        put("monthFontColorSelected", onPrimary)
        put("monthFontColorDisabled", secondaryText)
    }
    _monthViewAdapter.setColors(colors)
    _yearView.setColors(colors)
    _monthViewAdapter.notifyDataSetChanged()
    (_yearView.adapter as? android.widget.BaseAdapter)?.notifyDataSetChanged()
}

private fun applyCalendarDayStyling(root: View, context: Context) {
    val primary = context.getProperPrimaryColor()
    val onPrimary = primary.getContrastColor()
    val grids = ArrayList<GridView>()
    collectCalendarGrids(root, grids)
    if (grids.isEmpty()) {
        return
    }

    for (grid in grids) {
        val lastApplied = grid.getTag(R.id.picker_primary_grid_color_tag) as? Int
        if (lastApplied == primary) {
            continue
        }
        if (updateCalendarGridStyle(grid, primary, onPrimary)) {
            grid.setTag(R.id.picker_primary_grid_color_tag, primary)
        }
    }
}

private fun collectCalendarGrids(root: View, out: MutableList<GridView>) {
    if (root is GridView && root.javaClass.name == "com.google.android.material.datepicker.MaterialCalendarGridView") {
        out.add(root)
        return
    }
    if (root is ViewGroup) {
        for (i in 0 until root.childCount) {
            collectCalendarGrids(root.getChildAt(i), out)
        }
    }
}

private fun updateCalendarGridStyle(grid: GridView, primary: Int, onPrimary: Int): Boolean {
    val adapter = grid.adapter ?: return false
    val calendarStyle = getFieldValue(adapter, "calendarStyle") ?: return false

    val selectedDay = getFieldValue(calendarStyle, "selectedDay")
    val todayDay = getFieldValue(calendarStyle, "todayDay")
    val selectedYear = getFieldValue(calendarStyle, "selectedYear")
    val todayYear = getFieldValue(calendarStyle, "todayYear")
    val rangeFill = getFieldValue(calendarStyle, "rangeFill") as? Paint

    val selectedStyle = createCalendarItemStyleLike(selectedDay, primary, onPrimary, primary)
    val todayStyle = createCalendarItemStyleLike(todayDay, Color.TRANSPARENT, primary, primary)
    val selectedYearStyle = createCalendarItemStyleLike(selectedYear, primary, onPrimary, primary)
    val todayYearStyle = createCalendarItemStyleLike(todayYear, Color.TRANSPARENT, primary, primary)

    setFieldValue(calendarStyle, "selectedDay", selectedStyle)
    setFieldValue(calendarStyle, "todayDay", todayStyle)
    setFieldValue(calendarStyle, "selectedYear", selectedYearStyle)
    setFieldValue(calendarStyle, "todayYear", todayYearStyle)

    rangeFill?.color = primary.adjustAlpha(0.2f)
    grid.invalidateViews()
    return true
}

fun MaterialTimePicker.applyPrimaryColor(context: Context) {
    val dialog = dialog ?: return
    val root = dialog.window?.decorView
    if (root != null && root.getTag(R.id.picker_primary_time_applied_tag) != true) {
        root.setTag(R.id.picker_primary_time_applied_tag, true)
        root.viewTreeObserver.addOnGlobalLayoutListener {
            applyTimePickerStyling(dialog, context)
        }
    }
    applyTimePickerStyling(dialog, context)
}

private fun applyTimePickerStyling(dialog: android.app.Dialog, context: Context) {
    val primary = context.getProperPrimaryColor()
    val onPrimary = primary.getContrastColor()
    val textColor = context.getProperTextColor()

    // 1. Border color (outline)

    dialog.findViewById<TextView>(com.google.android.material.R.id.header_title)
        ?.setTextColor(textColor)

    dialog.findViewById<Button>(com.google.android.material.R.id.material_timepicker_ok_button)
        ?.setTextColor(primary)
    dialog.findViewById<Button>(com.google.android.material.R.id.material_timepicker_cancel_button)
        ?.setTextColor(primary)

    dialog.findViewById<MaterialButton>(com.google.android.material.R.id.material_timepicker_mode_button)
        ?.iconTint = ColorStateList.valueOf(primary)

    dialog.findViewById<MaterialButtonToggleGroup>(com.google.android.material.R.id.material_clock_period_toggle)
        ?.let { group ->
            val applyPeriodStyle: (MaterialButton) -> Unit = { button ->
                val fill = if (button.isChecked) primary.adjustAlpha(0.24f) else Color.TRANSPARENT
                val text = if (button.isChecked) onPrimary else primary
                button.setBackgroundColor(fill)
                button.strokeColor = ColorStateList.valueOf(primary)
                button.setTextColor(text)
                button.iconTint = ColorStateList.valueOf(text)
                button.rippleColor = ColorStateList.valueOf(primary.adjustAlpha(0.2f))
            }
            for (i in 0 until group.childCount) {
                val button = group.getChildAt(i) as? MaterialButton ?: continue
                applyPeriodStyle(button)
            }
            dialog.findViewById<MaterialButton>(com.google.android.material.R.id.material_clock_period_am_button)
                ?.let(applyPeriodStyle)
            dialog.findViewById<MaterialButton>(com.google.android.material.R.id.material_clock_period_pm_button)
                ?.let(applyPeriodStyle)

            if (group.getTag(R.id.picker_primary_time_toggle_listener_tag) != true) {
                group.setTag(R.id.picker_primary_time_toggle_listener_tag, true)
                group.addOnButtonCheckedListener { _, _, _ ->
                    dialog.window?.decorView?.post { applyTimePickerStyling(dialog, context) }
                }
            }
        }

    val chipBackground = ColorStateList(
        arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
        intArrayOf(primary.adjustAlpha(0.24f), Color.TRANSPARENT)
    )
    val chipTextColors = ColorStateList(
        arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
        intArrayOf(textColor, textColor)
    )
    dialog.findViewById<Chip>(com.google.android.material.R.id.material_hour_tv)
        ?.let { applyTimeChipColors(it, chipBackground, chipTextColors, primary) }
    dialog.findViewById<Chip>(com.google.android.material.R.id.material_minute_tv)
        ?.let { applyTimeChipColors(it, chipBackground, chipTextColors, primary) }

    dialog.findViewById<View>(com.google.android.material.R.id.material_clock_hand)
        ?.let { applyClockHandColor(it, primary) }
    dialog.findViewById<View>(com.google.android.material.R.id.material_clock_face)
        ?.let { applyClockFaceColors(it, onPrimary, textColor) }

    dialog.findViewById<View>(com.google.android.material.R.id.material_hour_text_input)
        ?.let { applyChipTextInputColors(it, chipBackground, chipTextColors, primary, textColor) }
    dialog.findViewById<View>(com.google.android.material.R.id.material_minute_text_input)
        ?.let { applyChipTextInputColors(it, chipBackground, chipTextColors, primary, textColor) }
    dialog.window?.decorView?.let {
        applyAllTimeTextInputStyling(it, primary, textColor)
        applyAllTimePeriodButtonStyling(it, primary, onPrimary)
    }
}

private fun applyAllTimeTextInputStyling(root: View, primary: Int, textColor: Int) {
    when (root) {
        is TextInputLayout -> {
            root.setBoxStrokeColorStateList(
                ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_focused),
                        intArrayOf(android.R.attr.state_activated),
                        intArrayOf(android.R.attr.state_hovered),
                        intArrayOf(-android.R.attr.state_enabled),
                        intArrayOf()
                    ),
                    intArrayOf(
                        primary,
                        primary,
                        primary,
                        primary.adjustAlpha(0.5f),
                        primary
                    )
                )
            )
            root.boxStrokeWidth = 2
            root.boxStrokeWidthFocused = 2
            root.defaultHintTextColor = ColorStateList.valueOf(primary)
            root.hintTextColor = ColorStateList.valueOf(primary)
            root.boxBackgroundColor = primary.adjustAlpha(0.24f)
            root.editText?.let { editText ->
                editText.setTextColor(textColor)
                editText.setHintTextColor(primary.adjustAlpha(0.7f))
                editText.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
                editText.highlightColor = primary.adjustAlpha(0.24f)
                editText.background?.mutate()?.setTint(Color.TRANSPARENT)
                try {
                    editText.setCursorColorProgrammaticallyet(primary)
                } catch (_: Exception) {
                }
            }
        }

        is ViewGroup -> {
            for (i in 0 until root.childCount) {
                applyAllTimeTextInputStyling(root.getChildAt(i), primary, textColor)
            }
        }
    }
}

private fun applyAllTimePeriodButtonStyling(root: View, primary: Int, onPrimary: Int) {
    when (root) {
        is MaterialButton -> {
            val label = root.text?.toString()?.trim()?.lowercase().orEmpty()
            val isPeriodButton = label == "am" || label == "pm" ||
                    root.id == com.google.android.material.R.id.material_clock_period_am_button ||
                    root.id == com.google.android.material.R.id.material_clock_period_pm_button
            if (isPeriodButton) {
                root.backgroundTintList = ColorStateList(
                    arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                    intArrayOf(primary.adjustAlpha(0.24f), Color.TRANSPARENT)
                )
                root.strokeColor = ColorStateList.valueOf(primary)
                root.setTextColor(
                    ColorStateList(
                        arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                        intArrayOf(onPrimary, primary)
                    )
                )
                root.iconTint = ColorStateList(
                    arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                    intArrayOf(onPrimary, primary)
                )
                root.rippleColor = ColorStateList.valueOf(primary.adjustAlpha(0.2f))
            }
        }

        is ViewGroup -> {
            for (i in 0 until root.childCount) {
                applyAllTimePeriodButtonStyling(root.getChildAt(i), primary, onPrimary)
            }
        }
    }
}


private fun applyTimeChipColors(
    chip: Chip,
    background: ColorStateList,
    textColors: ColorStateList,
    primary: Int
) {
    chip.chipBackgroundColor = background
    chip.setTextColor(textColors)
    chip.chipStrokeWidth = 0f
    chip.chipStrokeColor = ColorStateList.valueOf(Color.TRANSPARENT)
    chip.rippleColor = ColorStateList.valueOf(primary.adjustAlpha(0.2f))
}

private fun applyClockHandColor(handView: View, primary: Int) {
    try {
        val paintField = handView.javaClass.getDeclaredField("paint")
        paintField.isAccessible = true
        val paint = paintField.get(handView) as? Paint ?: return
        paint.color = primary
        handView.invalidate()
    } catch (_: Throwable) {
        // Ignore reflection failures to keep the picker functional.
    }
}

private fun applyClockFaceColors(clockFace: View, selectedText: Int, defaultText: Int) {
    try {
        val gradientField = clockFace.javaClass.getDeclaredField("gradientColors")
        gradientField.isAccessible = true
        val gradientColors = gradientField.get(clockFace) as? IntArray
        if (gradientColors != null && gradientColors.size >= 3) {
            gradientColors[0] = selectedText
            gradientColors[1] = selectedText
            gradientColors[2] = defaultText
        }
    } catch (_: Throwable) {
        // Ignore reflection failures to keep the picker functional.
    }

    val textColors = ColorStateList(
        arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf()),
        intArrayOf(selectedText, defaultText)
    )
    applyTextColorToClockFace(clockFace, textColors)
    clockFace.invalidate()
}

private fun applyTextColorToClockFace(root: View, textColors: ColorStateList) {
    when (root) {
        is TextView -> {
            val text = root.text?.toString()?.trim().orEmpty()
            if (text.toIntOrNull() != null) {
                root.setTextColor(textColors)
            }
        }

        is ViewGroup -> {
            for (i in 0 until root.childCount) {
                applyTextColorToClockFace(root.getChildAt(i), textColors)
            }
        }
    }
}

private fun applyChipTextInputColors(
    view: View,
    chipBackground: ColorStateList,
    chipTextColors: ColorStateList,
    primary: Int,
    textColor: Int
) {
    val chip = getFieldValue(view, "chip") as? Chip
    if (chip != null) {
        applyTimeChipColors(chip, chipBackground, chipTextColors, primary)
    }
    val inputLayout = getFieldValue(view, "textInputLayout") as? TextInputLayout
    inputLayout?.setBoxStrokeColorStateList(
        ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_focused), intArrayOf()),
            intArrayOf(primary, primary)
        )
    )
    inputLayout?.boxStrokeWidth = 2
    inputLayout?.boxStrokeWidthFocused = 2
    inputLayout?.defaultHintTextColor = ColorStateList.valueOf(primary)
    inputLayout?.hintTextColor = ColorStateList.valueOf(primary)
    inputLayout?.boxBackgroundColor = primary.adjustAlpha(0.24f)
    val editText = getFieldValue(view, "editText") as? EditText
    editText?.setTextColor(textColor)
    editText?.setHintTextColor(primary.adjustAlpha(0.7f))
    editText?.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
    editText?.highlightColor = primary.adjustAlpha(0.24f)
    try {
        editText?.setCursorColorProgrammaticallyet(primary)
    } catch (_: Exception) {
    }
}

private fun createCalendarItemStyleLike(
    baseStyle: Any?,
    fillColor: Int,
    textColor: Int,
    strokeColor: Int
): Any? {
    if (baseStyle == null) return null
    return try {
        val styleClass = baseStyle.javaClass
        val insets = (getFieldValue(baseStyle, "insets") as? Rect)?.let { Rect(it) } ?: Rect()
        val strokeWidth = (getFieldValue(baseStyle, "strokeWidth") as? Int) ?: 0
        val itemShape =
            getFieldValue(baseStyle, "itemShape") as? ShapeAppearanceModel ?: return null
        val ctor = styleClass.getDeclaredConstructor(
            ColorStateList::class.java,
            ColorStateList::class.java,
            ColorStateList::class.java,
            Int::class.javaPrimitiveType,
            ShapeAppearanceModel::class.java,
            Rect::class.java
        )
        ctor.isAccessible = true
        ctor.newInstance(
            ColorStateList.valueOf(fillColor),
            ColorStateList.valueOf(textColor),
            ColorStateList.valueOf(strokeColor),
            strokeWidth,
            itemShape,
            insets
        )
    } catch (_: Throwable) {
        null
    }
}

private fun getFieldValue(instance: Any, name: String): Any? {
    return try {
        val field = instance.javaClass.getDeclaredField(name)
        field.isAccessible = true
        field.get(instance)
    } catch (_: Throwable) {
        null
    }
}

private fun setFieldValue(instance: Any, name: String, value: Any?) {
    if (value == null) return
    try {
        val field = instance.javaClass.getDeclaredField(name)
        field.isAccessible = true
        field.set(instance, value)
    } catch (_: Throwable) {
        // Ignore reflection failures to keep the picker functional.
    }
}
