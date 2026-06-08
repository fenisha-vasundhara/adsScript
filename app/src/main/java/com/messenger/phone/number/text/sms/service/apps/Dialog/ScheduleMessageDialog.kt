package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.text.format.DateFormat
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setupDialogStuff
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMaterialDialogTheme
import com.messenger.phone.number.text.sms.service.apps.CommanClass.roundToClosestMultipleOf
import com.messenger.phone.number.text.sms.service.apps.CommanClass.applyPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getDialogBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.R
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.views.MyTextView
import org.joda.time.DateTime
import java.util.*

class ScheduleMessageDialog(
    private val activity: Activity,
    private var dateTime: DateTime? = null,
    private val callback: (dateTime: DateTime?) -> Unit
) {
    private lateinit var materialDatePicker: MaterialDatePicker<androidx.core.util.Pair<Long, Long>>

    private var time_image: AppCompatImageView?
    private var date_image: AppCompatImageView?
    private var edit_date: MyTextView?
    private var edit_time: MyTextView?
    private var subtitle: MyTextView?
    private val view = activity.layoutInflater.inflate(R.layout.schedule_message_dialog, null)
    private val isDarkTheme = ThemeModeManager.isDarkThemeActive(activity)
    private val textColor = activity.getProperTextColor()
    private val secondaryTextColor = activity.getProperSecondaryTextColor()
    private val primaryColor = activity.getProperPrimaryColor()

    private var previewDialog: AlertDialog? = null
    private var previewShown = false
    private var isNewMessage = dateTime == null


    private val calendar = Calendar.getInstance()

    init {

        subtitle = view.findViewById<MyTextView>(R.id.subtitle)
        edit_time = view.findViewById<MyTextView>(R.id.edit_time)
        edit_date = view.findViewById<MyTextView>(R.id.edit_date)
        date_image = view.findViewById<AppCompatImageView>(R.id.date_image)
        time_image = view.findViewById<AppCompatImageView>(R.id.time_image)



        arrayOf(subtitle).forEach {
            it?.setTextColor(secondaryTextColor)
        }

        arrayOf(edit_time, edit_date).forEach {
            it?.setTextColor(textColor)
        }

        arrayOf(date_image, time_image).forEach {
            it?.applyColorFilter(primaryColor)
        }
        view.setBackgroundColor(activity.getDialogBackgroundColor())
        edit_date?.setOnClickListener { showDatePicker() }
        edit_time?.setOnClickListener { showTimePicker() }

        val targetDateTime = dateTime ?: DateTime.now().plusHours(1)
        updateTexts(targetDateTime)

        if (isNewMessage) {
            showDatePicker()
        } else {
            showPreview()
        }
    }

    private fun updateTexts(dateTime: DateTime) {
        val dateFormat = "dd/MM/y"
        val timeFormat = activity.getTimeFormat()
        edit_date?.text = dateTime.toString(dateFormat)
        edit_time?.text = dateTime.toString(timeFormat)
    }

    private fun showPreview() {
        if (previewShown) {
            return
        }

        MaterialAlertDialogBuilder(activity, activity.getMaterialDialogTheme())
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel, null)

            .apply {
                previewShown = true
                activity.runOnUiThread {

                activity.setupDialogStuff(view, this, R.string.schedule_message) { dialog ->
                    previewDialog = dialog
                    dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    dialog.window?.setDimAmount(0.25F)
                    val dimColor = activity.getDialogBackgroundColor()

//                    dialog.window?.decorView?.rootView?.setBackgroundColor(dimColor)
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        if (validateDateTime()) {
                            callback(dateTime)
                            dialog.dismiss()
                        }
                    }

                    dialog.setOnDismissListener {
                        previewShown = false
                        previewDialog = null
                    }
                }

            }}
    }

    private fun showDatePicker() {

        val materialDatePickerBuilder = MaterialDatePicker.Builder.dateRangePicker()
        materialDatePickerBuilder.setTitleText("Select a date")
        val now = android.icu.util.Calendar.getInstance()
        materialDatePickerBuilder.setSelection(
            androidx.core.util.Pair(
                now.timeInMillis,
                now.timeInMillis
            )
        )
        val customTheme = if (!isDarkTheme) {
            R.style.CustomMaterialDatePickerThemedatetange
        } else {
            R.style.CustomMaterialDatePickerThemedatetange_Dark
        }


        materialDatePickerBuilder.setTheme(customTheme)
        materialDatePickerBuilder.setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)

        materialDatePicker = materialDatePickerBuilder.build()
        materialDatePicker.show((activity as FragmentActivity).supportFragmentManager, "tag")
        (activity as FragmentActivity).supportFragmentManager.executePendingTransactions()
        materialDatePicker.dialog?.setOnShowListener {
            materialDatePicker.applyPrimaryColor(activity)
        }

        materialDatePicker.applyPrimaryColor(activity)
        materialDatePicker.addOnPositiveButtonClickListener { selection ->
//            val selectedDate = selection
//            val selectedCalendar = Calendar.getInstance()
//            selectedCalendar.timeInMillis = selectedDate
//            val year = selectedCalendar.get(Calendar.YEAR)
//            val month = selectedCalendar.get(Calendar.MONTH)
//            val day = selectedCalendar.get(Calendar.DAY_OF_MONTH)
//            dateSet(year, month, day)

            val startMillis = selection.first ?: return@addOnPositiveButtonClickListener
            val selectedCalendar = Calendar.getInstance().apply {
                timeInMillis = startMillis
            }

            val year = selectedCalendar.get(Calendar.YEAR)
            val month = selectedCalendar.get(Calendar.MONTH)
            val day = selectedCalendar.get(Calendar.DAY_OF_MONTH)

            dateSet(year, month, day)
        }


        materialDatePicker.addOnNegativeButtonClickListener {
            materialDatePicker.dismiss()
        }

    }

    private fun showTimePicker() {
        val hourOfDay = dateTime?.hourOfDay ?: getNextHour()
        val minute = dateTime?.minuteOfHour ?: getNextMinute()

        val timePickerTheme = if (!isDarkTheme) {
            R.style.NewCustomMaterialTimePickerTheme
        } else {
            R.style.NewCustomMaterialTimePickerTheme_Dark
        }
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(hourOfDay)
            .setMinute(minute)
            .setTheme(timePickerTheme)
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val selectedHour = timePicker.hour
            val selectedMinute = timePicker.minute
            timeSet(selectedHour, selectedMinute)
        }
        timePicker.addOnNegativeButtonClickListener {
            timePicker.dismiss()
        }

        timePicker.show((activity as FragmentActivity).supportFragmentManager, "timePicker")
        (activity as FragmentActivity).supportFragmentManager.executePendingTransactions()
        timePicker.dialog?.setOnShowListener {
            timePicker.applyPrimaryColor(activity)
        }
        timePicker.applyPrimaryColor(activity)

    }

    private fun dateSet(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        if (isNewMessage) {
            showTimePicker()
        }

        dateTime = DateTime.now()
            .withDate(year, monthOfYear + 1, dayOfMonth)
            .run {
                if (dateTime != null) {
                    withTime(dateTime!!.hourOfDay, dateTime!!.minuteOfHour, 0, 0)
                } else {
                    withTime(getNextHour(), getNextMinute(), 0, 0)
                }
            }

        if (!isNewMessage) {
            validateDateTime()
        }

        isNewMessage = false
        updateTexts(dateTime!!)
    }

    private fun timeSet(hourOfDay: Int, minute: Int) {
        dateTime = dateTime?.withHourOfDay(hourOfDay)?.withMinuteOfHour(minute)
        if (validateDateTime()) {
            updateTexts(dateTime!!)
            showPreview()
        } else {
            showTimePicker()
        }
    }

    private fun validateDateTime(): Boolean {
        return if (dateTime?.isAfterNow == false) {
            activity.toast(R.string.must_pick_time_in_the_future)
            false
        } else {
            true
        }
    }

    private fun getNextHour() = (calendar.get(Calendar.HOUR_OF_DAY) + 1).coerceIn(0, 23)

    private fun getNextMinute() = (calendar.get(Calendar.MINUTE) + 5).roundToClosestMultipleOf(5).coerceIn(0, 59)
}
