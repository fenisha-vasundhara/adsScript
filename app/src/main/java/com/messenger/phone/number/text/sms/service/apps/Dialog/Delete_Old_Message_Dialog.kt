package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.app.Dialog
import android.app.PendingIntent
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.messenger.phone.number.text.sms.service.apps.CommanClass.AutoMessageDeletedelete
import com.messenger.phone.number.text.sms.service.apps.CommanClass.cancelAutoMessageDeletePendingIntent
import com.messenger.phone.number.text.sms.service.apps.CommanClass.cancelOtpdeletePendingIntent
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.otpoutodelete
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.google.android.material.color.MaterialColors
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.DeleteOldMessageDialogItemBinding
import com.messenger.phone.number.text.sms.service.apps.services.AutoMessagedeleteReceiver
import com.simplemobiletools.commons.extensions.onTextChangeListener
import com.simplemobiletools.commons.extensions.toInt
import com.simplemobiletools.commons.extensions.toast
import javax.annotation.Nullable
import javax.inject.Inject

class Delete_Old_Message_Dialog @Inject constructor() : MaterialDialogFragment() {

    private var bottomSheetDialog: Dialog? = null
    lateinit var binding: DeleteOldMessageDialogItemBinding
    var contTime = ""
    var forsave = false
    var dialogdismiss: ((String) -> Unit)? = null

    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        requireActivity().setLocal()
        binding = DataBindingUtil.inflate(inflater, R.layout.delete_old_message_dialog_item, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val colors = requireContext().resolveDialogColors()
        with(binding) {
            forsave = false
            contTime = requireContext().config.userpreferenceAutoMessageDelete
            setDefaultSelection()
            updateEdittxt()
            val cornerRadius = resources.getDimension(com.intuit.sdp.R.dimen._15sdp)
            binding.mainBg.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                this.cornerRadius = cornerRadius
                setColor(colors.surface)
                setStroke(
                    resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp),
                    colors.outlineVariant
                )
            }
            binding.imageView31.iconTint =
                android.content.res.ColorStateList.valueOf(colors.onSurfaceVariant)
            binding.deletOldTital.setTextColor(colors.onSurface)
            binding.deletOldBottom.setTextColor(colors.onSurfaceVariant)
            binding.deletOldTitalOr.setTextColor(colors.onSurfaceVariant)
            binding.deletOldTitalManuly.setTextColor(colors.onSurface)
            binding.divider.setBackgroundColor(colors.outlineVariant)

            for (index in 0 until binding.radioGroupOldMessageSelection.childCount) {
                val radio = binding.radioGroupOldMessageSelection.getChildAt(index) as? RadioButton
                radio?.setTextColor(colors.onSurface)
                radio?.buttonTintList =
                    android.content.res.ColorStateList.valueOf(colors.primary)
            }

            binding.deletOldManulyInput.setBoxBackgroundColor(colors.optionFill)
            binding.deletOldManulyInput.setBoxStrokeColor(colors.outlineVariant)
            binding.deletOldManulyEdit.setTextColor(colors.onSurface)
            binding.deletOldManulyEdit.setHintTextColor(colors.onSurfaceVariant)

            binding.neverBtn.setTextColor(colors.onSurfaceVariant)
            binding.neverBtn.backgroundTintList = ColorStateList.valueOf(colors.optionFill)
            binding.neverBtn.strokeColor = ColorStateList.valueOf(colors.outlineVariant)
            binding.neverBtn.strokeWidth = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp)
            binding.neverBtn.cornerRadius = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._7sdp)

            binding.saveBtn.setTextColor(colors.primary)
            binding.saveBtn.backgroundTintList = ColorStateList.valueOf(
                MaterialColors.layer(colors.surface, colors.primary, 0.12f)
            )
            binding.saveBtn.cornerRadius = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._7sdp)
            binding.canelBtn.setTextColor(colors.primary)
            val rippleColor = ColorStateList.valueOf(
                MaterialColors.layer(colors.surface, colors.onSurface, 0.16f)
            )
            binding.neverBtn.rippleColor = rippleColor
            binding.saveBtn.rippleColor = rippleColor
            binding.imageView31.rippleColor = rippleColor

            imageView31.setOnClickListener {
                contTime = requireContext().config.userpreferenceAutoMessageDelete
                setDefaultSelection()
                binding.deletOldManulyEdit.setText(requireContext().config.userpreferenceAutoMessageDelete)
                dismiss()
            }

            canelBtn.setOnClickListener {

            }
            neverBtn.setOnClickListener {
                contTime = "0"
                saveDailyCount()
                setDefaultSelection()
                requireActivity().cancelAutoMessageDeletePendingIntent()
                binding.deletOldManulyEdit.setText("")
                dismiss()
            }
            saveBtn.setOnClickListener {
                contTime = binding.deletOldManulyEdit.text.toString()
                if (contTime == requireContext().config.userpreferenceAutoMessageDelete) {
                    requireContext().toast(resources.getString(R.string.EnterAnotherTime))
                } else if (contTime.trim().isEmpty()) {
                    requireContext().toast(getString(R.string.please_enter_time))
                } else if (contTime.trim() == "0") {
                    requireContext().toast(getString(R.string.please_enter_time))
                } else {
                    forsave = true
                    saveDailyCount()
                    setDefaultSelection()
                    setTimer()
                    dismiss()
                }
            }

            radioGroupOldMessageSelection.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.Daily_old_message_delete -> {
                        contTime = "1"
                    }

                    R.id.Weekly_old_message_delete -> {
                        contTime = "7"
                    }

                    R.id.Monthly_old_message_delete -> {
                        contTime = "30"
                    }

                    R.id.Yearly_old_message_delete -> {
                        contTime = "365"
                    }
                }
                updateEdittxt()
            }
        }
    }

    private fun setTimer() {
        val intervalMillis = requireContext().config.userpreferenceAutoMessageDelete.toInt() * 24 * 60 * 60 * 1000L
        Log.d("intervalMillis", "setTimer:intervalMillis <----> ${intervalMillis}")
        requireActivity().AutoMessageDeletedelete(intervalMillis, requireContext().config.userpreferenceAutoMessageDelete.toInt())
    }

    private fun setDefaultSelection() {
        when (contTime) {
            "1" -> {
                binding.radioGroupOldMessageSelection.check(R.id.Daily_old_message_delete)
            }

            "7" -> {
                binding.radioGroupOldMessageSelection.check(R.id.Weekly_old_message_delete)
            }

            "30" -> {
                binding.radioGroupOldMessageSelection.check(R.id.Monthly_old_message_delete)
            }

            "365" -> {
                binding.radioGroupOldMessageSelection.check(R.id.Yearly_old_message_delete)
            }

            else -> {
                binding.radioGroupOldMessageSelection.clearCheck()
            }
        }
    }

    private fun saveDailyCount() {
        requireContext().config.userpreferenceAutoMessageDelete = contTime
        updateEdittxt()
    }

    private fun updateEdittxt() {
        if (contTime == "0") {
            binding.deletOldManulyEdit.setText("")
            Log.d("", "updateEdittxt:deletOldManulyEdit 1 <---> ${contTime} ")
        } else {
            if (forsave) {
                binding.deletOldManulyEdit.setText(requireContext().config.userpreferenceAutoMessageDelete)
            } else {
                binding.deletOldManulyEdit.setText(contTime)
            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bottomSheetDialog = dialog
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        var width = displayMetrics.widthPixels
        width -= width / 8
        bottomSheetDialog!!.window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        bottomSheetDialog?.setCancelable(false)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        try {
            dialogdismiss?.invoke(
                if (binding.deletOldManulyEdit.text.toString().isEmpty()) {
                    "0"
                } else {
                    binding.deletOldManulyEdit.text.toString()
                }
            )
        } catch (e: Exception) {
        }
    }

}
