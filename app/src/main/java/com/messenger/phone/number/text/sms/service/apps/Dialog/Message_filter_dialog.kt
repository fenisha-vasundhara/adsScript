package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.color.MaterialColors
import com.google.android.material.datepicker.MaterialDatePicker
import com.messenger.phone.number.text.sms.service.apps.CommanClass.applyPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.filterdialogselection
import com.messenger.phone.number.text.sms.service.apps.CommanClass.formateddate
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.invisible
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isInvisible
import com.messenger.phone.number.text.sms.service.apps.CommanClass.rangeselected
import com.messenger.phone.number.text.sms.service.apps.CommanClass.selecteddateinmillisecound
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.MonthAndYearPicker.MonthPickerDialog
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.FilterDialogLayoutHomeBinding
import androidx.core.graphics.ColorUtils
import java.util.Date
import java.util.Locale


class Message_filter_dialog : MaterialBottomSheetDialogFragment() {

    lateinit var binding: FilterDialogLayoutHomeBinding
    var dialogdismiss: ((String, String, String) -> Unit)? = null
    private lateinit var dialogColors: DialogColors
    private var dialogFactory: (() -> BottomSheetDialog)? = null

    fun setDialogFactory(factory: () -> BottomSheetDialog): Message_filter_dialog {
        dialogFactory = factory
        return this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = dialogFactory?.invoke() ?: super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FilterDialogLayoutHomeBinding.inflate(inflater, container, false)
        dialogColors = requireContext().resolveDialogColors()
        applyFilterDialogTheme()
        listOf(
            binding.unreadBtn,
            binding.defaultBtn,
            binding.todayBtn,
            binding.monthBtn,
            binding.yearBtn,
            binding.dateRangeBtn,
            binding.ApplyButton,
            binding.cancelButton,
            binding.ResetButton
        ).forEach { view ->
            view.applyDialogRipple(dialogColors, alpha = 0.16f)
        }
        setBUttonSelected(binding, filterdialogselection)
        with(binding) {

            ApplyButton.setOnClickListener {
                dialogdismiss?.invoke(
                    filterdialogselection,
                    formateddate,
                    selecteddateinmillisecound.toString()
                )
                dismiss()
            }

            cancelButton.setOnClickListener {
                dismiss()
            }

            ResetButton.setOnClickListener {
                selecteddateinmillisecound = 0L
                formateddate = ""
                rangeselected = false
                filterdialogselection = "defaultBtn"
                setBUttonSelected(binding, "defaultBtn")
                ApplyButton.performClick()
            }

            unreadBtn.setOnClickListener {
                filterdialogselection = "unreadBtn"
                setBUttonSelected(binding, "unreadBtn")
            }
            defaultBtn.setOnClickListener {
                filterdialogselection = "defaultBtn"
                setBUttonSelected(binding, "defaultBtn")
            }
            todayBtn.setOnClickListener {
                filterdialogselection = "todayBtn"
                setBUttonSelected(binding, "todayBtn")
            }
            monthBtn.setOnClickListener {
                filterdialogselection = "monthBtn"
                openmonthseletiondialog()
            }
            yearBtn.setOnClickListener {
                filterdialogselection = "yearBtn"
                openyearseletiondialog()
            }
            dateRangeBtn.setOnClickListener {
                filterdialogselection = "dateRangeBtn"
                opendaterangedialog()
//                setBUttonSelected(binding, "dateRangeBtn")
            }
        }
        return binding.root
    }

    private fun applyFilterDialogTheme() {
        val cornerRadius =
            resources.getDimension(com.simplemobiletools.commons.R.dimen.material_dialog_corner_radius)
        val surface = dialogColors.surface
        val onSurface = dialogColors.onSurface
        val onSurfaceVariant = dialogColors.onSurfaceVariant
        val primary = dialogColors.primary

        binding.root.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadii = floatArrayOf(
                cornerRadius, cornerRadius, // top left
                cornerRadius, cornerRadius, // top right
                0f, 0f, // bottom right
                0f, 0f // bottom left
            )
            setColor(surface)
        }
        binding.root.clipToOutline = true

        binding.textView45.setTextColor(onSurface)
        binding.seletedDate.setTextColor(primary)
        binding.cancelButton.setTextColor(onSurfaceVariant)
        binding.ApplyButton.setTextColor(primary)
        binding.ResetButton.setTextColor(primary)

        binding.imageView41.setImageResource(R.drawable.filter_dialog_union_icon)
        binding.imageView41.setColorFilter(onSurfaceVariant)
    }


    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    private fun setBUttonSelected(binding: FilterDialogLayoutHomeBinding, buttonname: String) {
        val isDark = ColorUtils.calculateLuminance(dialogColors.surface) < 0.5
        val unselectedText =
            if (isDark) dialogColors.onSurface else dialogColors.onSurfaceVariant
        val selectedText = dialogColors.primary
        val radius = resources.getDimension(com.intuit.sdp.R.dimen._8sdp)

        fun buildFilterBg(isSelected: Boolean): GradientDrawable {
            val fill = if (isSelected) {
                MaterialColors.layer(
                    dialogColors.surface,
                    dialogColors.primary,
                    if (isDark) 0.20f else 0.12f
                )
            } else {
                MaterialColors.layer(
                    dialogColors.surface,
                    dialogColors.onSurface,
                    if (isDark) 0.12f else 0.06f
                )
            }
            return GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = radius
                setColor(fill)
            }
        }

        val buttons = listOf(
            Triple(binding.unreadBtn, binding.unreadTxt, "unreadBtn"),
            Triple(binding.defaultBtn, binding.defaultTxt, "defaultBtn"),
            Triple(binding.todayBtn, binding.todayTxt, "todayBtn"),
            Triple(binding.monthBtn, binding.monthTxt, "monthBtn"),
            Triple(binding.yearBtn, binding.yearTxt, "yearBtn"),
            Triple(binding.dateRangeBtn, binding.dateRangeTxt, "dateRangeBtn")
        )

        buttons.forEach { (button, text, buttonName) ->
            val selected = buttonName == buttonname
            button.background = buildFilterBg(selected)
            text.setTextColor(if (selected) selectedText else unselectedText)
        }

        if (filterdialogselection == "defaultBtn") {
            binding.ResetButton.gone()
            selecteddateinmillisecound = 0L
            formateddate = ""
            rangeselected = false
        } else {
            binding.ResetButton.visible()
        }


        if (filterdialogselection == "todayBtn") {
            selecteddateinmillisecound = System.currentTimeMillis()
            val date = Date(selecteddateinmillisecound)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formateddate = dateFormat.format(date)
            binding.seletedDate.visible()
            binding.seletedDate.text = "Date: ${formateddate}"
        } else if (filterdialogselection == "monthBtn") {
            binding.seletedDate.visible()
            binding.seletedDate.text = formateddate
        } else if (filterdialogselection == "yearBtn") {
            binding.seletedDate.visible()
            binding.seletedDate.text = formateddate
        } else if (filterdialogselection == "defaultBtn") {
            binding.seletedDate.invisible()
        } else if (filterdialogselection == "dateRangeBtn") {
            binding.seletedDate.visible()
            binding.seletedDate.text = formateddate
        }


    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    fun openmonthseletiondialog() {
        val today = Calendar.getInstance()
        val builder = MonthPickerDialog.Builder(
            requireActivity(), object : MonthPickerDialog.OnDateSetListener {
                override fun onDateSet(selectedMonth: Int, selectedYear: Int) {
                    val selectedMonth = selectedMonth + 1
                    formateddate = "Month: $selectedMonth/$selectedYear"
                    selecteddateinmillisecound =
                        convertMonthAndYearToMillis(selectedMonth, selectedYear)
                    setBUttonSelected(binding, "monthBtn")
                }
            }, today.get(Calendar.YEAR), today.get(Calendar.MONTH)
        )

        val monthPickerDialog = builder.setActivatedMonth(Calendar.getInstance().get(Calendar.MONTH))
            .setMinYear(1996)
            .setActivatedYear(Calendar.getInstance().get(Calendar.YEAR))
            .setMaxYear(2030)
            .setMinMonth(java.util.Calendar.JANUARY)
            .setMonthRange(
                java.util.Calendar.JANUARY,
                java.util.Calendar.DECEMBER
            )
            .setOnMonthChangedListener(object : MonthPickerDialog.OnMonthChangedListener {
                override fun onMonthChanged(selectedMonth: Int) {
                }
            })
            .setOnYearChangedListener(object : MonthPickerDialog.OnYearChangedListener {
                override fun onYearChanged(selectedYear: Int) {
                }
            })
            .build()
        monthPickerDialog.datePicker.applyPrimaryColor(requireContext())
        monthPickerDialog.show()
    }

    fun openyearseletiondialog() {
        val today = Calendar.getInstance()
        val builder = MonthPickerDialog.Builder(
            requireActivity(), object : MonthPickerDialog.OnDateSetListener {
                override fun onDateSet(selectedMonth: Int, selectedYear: Int) {
                    formateddate = "Year: $selectedYear"
                    selecteddateinmillisecound =
                        yearsToMilliseconds(selectedYear.toDouble())
                    setBUttonSelected(binding, "yearBtn")
                }
            }, today.get(Calendar.YEAR), today.get(Calendar.MONTH)
        )


        val monthPickerDialog = builder.setActivatedMonth(Calendar.getInstance().get(Calendar.MONTH))
            .setMinYear(1996)
            .setActivatedYear(Calendar.getInstance().get(Calendar.YEAR))
            .setMaxYear(2030)
            .setMinMonth(java.util.Calendar.JANUARY)
            .setMonthRange(
                java.util.Calendar.JANUARY,
                java.util.Calendar.DECEMBER
            )
            .showYearOnly()
            .setOnMonthChangedListener(object : MonthPickerDialog.OnMonthChangedListener {
                override fun onMonthChanged(selectedMonth: Int) {
                }
            })
            .setOnYearChangedListener(object : MonthPickerDialog.OnYearChangedListener {
                override fun onYearChanged(selectedYear: Int) {
                }
            })
            .build()
        monthPickerDialog.datePicker.applyPrimaryColor(requireContext())
        monthPickerDialog.show()
    }

    fun convertMonthAndYearToMillis(selectedMonth: Int, selectedYear: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, selectedYear)
        calendar.set(Calendar.MONTH, selectedMonth - 1) // Month is 0-based in Calendar
        calendar.set(Calendar.DAY_OF_MONTH, 1) // Set to the first day of the month
        return calendar.timeInMillis
    }

    fun yearsToMilliseconds(years: Double): Long {
        val millisecondsInOneYear = 365.25 * 24 * 60 * 60 * 1000
        return (years * millisecondsInOneYear).toLong()
    }

    private fun opendaterangedialog() {
        lateinit var materialDatePicker: MaterialDatePicker<androidx.core.util.Pair<Long, Long>>
        val materialDatePickerBuilder = MaterialDatePicker.Builder.dateRangePicker()
        materialDatePickerBuilder.setTitleText("Select a date")
        val now = Calendar.getInstance()
        materialDatePickerBuilder.setSelection(
            androidx.core.util.Pair(
                now.timeInMillis,
                now.timeInMillis
            )
        )
        val isDarkPicker = ColorUtils.calculateLuminance(dialogColors.surface) < 0.5
        val customTheme = if (isDarkPicker) {
            R.style.CustomMaterialDatePickerThemedatetange_Dark
        } else {
            R.style.CustomMaterialDatePickerThemedatetange
        }
        materialDatePickerBuilder.setTheme(customTheme)
        materialDatePickerBuilder.setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
        materialDatePicker = materialDatePickerBuilder.build()
        materialDatePicker.show((activity as FragmentActivity).supportFragmentManager, "tag")
        (activity as FragmentActivity).supportFragmentManager.executePendingTransactions()
        materialDatePicker.dialog?.setOnShowListener {
            materialDatePicker.applyPrimaryColor(requireContext())
        }
        materialDatePicker.applyPrimaryColor(requireContext())
        materialDatePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = selection
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val firstDate = Date(selectedDate.first)
            val firstFormattedDate = dateFormat.format(firstDate)
            val secondDate = Date(selectedDate.second)
            val secondFormattedDate = dateFormat.format(secondDate)
            formateddate = "Date Range: $firstFormattedDate - $secondFormattedDate"
//            selecteddateinmillisecound =
//                yearsToMilliseconds(selectedYear.toDouble())
            setBUttonSelected(binding, "dateRangeBtn")
        }
        materialDatePicker.addOnNegativeButtonClickListener {
            materialDatePicker.dismiss()
        }

    }

}
