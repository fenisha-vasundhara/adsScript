package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.color.MaterialColors
import com.intuit.sdp.R.dimen
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getDialogBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.data.Config
import com.messenger.phone.number.text.sms.service.apps.databinding.MessageCornerDialogItemBinding
import javax.inject.Inject


class Message_Corner_Dialog @Inject constructor() : MaterialDialogFragment() {

    private var bottomSheetDialog: Dialog? = null
    lateinit var binding: MessageCornerDialogItemBinding
    var messagecorner = -1f
    var messagecornerold = -1f
    var doneclickofmessagecorner: ((Boolean, Float) -> Unit)? = null
    var livechangecornerofcard: (() -> Unit)? = null
    private lateinit var config: Config

    var isdonebuttonclick = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        config = requireContext().config
        isdonebuttonclick = false
        val surfaceColor = requireContext().getDialogBackgroundColor()
        val onSurface = requireContext().getProperTextColor()
        val onSurfaceVariant = requireContext().getProperSecondaryTextColor()
        val primary = requireContext().getProperPrimaryColor()

        binding.isdarktheme = ThemeModeManager.isDarkThemeActive(requireContext())
        binding.mainBg.background = GradientDrawable().apply {
            cornerRadius = resources.getDimension(com.simplemobiletools.commons.R.dimen.material_dialog_corner_radius)
            setColor(surfaceColor)
        }
        binding.tvReset.setTextColor(onSurfaceVariant)
        (binding.doneBtn.getChildAt(0) as? android.widget.TextView)?.setTextColor(primary)

        binding.messageCorner.value = config.messagecorner
        messagecornerold = config.messagecorner
        with(binding) {
            messagecorner = config.messagecorner
            updatechatpreview()

            val inactiveTrack = MaterialColors.layer(surfaceColor, onSurface, 0.12f)
            messageCorner.trackActiveTintList = ColorStateList.valueOf(primary)
            messageCorner.trackInactiveTintList = ColorStateList.valueOf(inactiveTrack)
            messageCorner.thumbTintList = ColorStateList.valueOf(primary)
            messageCorner.thumbStrokeColor = ColorStateList.valueOf(primary)
            messageCorner.haloTintList = ColorStateList.valueOf(MaterialColors.layer(surfaceColor, onSurface, 0.2f))

            messageCorner.setLabelFormatter { value ->
                val seekBarValue = value
                val mappedValue = (((seekBarValue - 1) * (100.0 - 1)) / (55 - 1) + 1).toInt()
                mappedValue.toString()
            }
            messageCorner.addOnChangeListener { slider, value, fromUser ->
                messagecorner = value
                if (messagecorner != -1f) {
                    config.messagecorner = messagecorner
                }
                updatechatpreview()
                livechangecornerofcard?.invoke()
            }

            doneBtn.setOnClickListener {
                isdonebuttonclick = true
                if (messagecorner != -1f) {
                    config.messagecorner = messagecorner
                    dismiss()
                } else {
                    dismiss()
                }
            }

            resetBtn.setOnClickListener {
                isdonebuttonclick = true
                config.messagecorner = 30f
                messageCorner.value = config.messagecorner
                dismiss()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.message_corner_dialog_item, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bottomSheetDialog = dialog
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        var width = displayMetrics.widthPixels
        width -= width / 10
        bottomSheetDialog?.window?.let { window ->
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            window.setDimAmount(0.2F)
            window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog?.window?.setGravity(Gravity.CENTER)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog?.window?.setDimAmount(0f)
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        doneclickofmessagecorner?.invoke(isdonebuttonclick, messagecornerold)
        binding.messageCorner.value = config.messagecorner
    }

    private fun updatechatpreview() {
        val drawable = ContextCompat.getDrawable(requireActivity(), com.messenger.phone.number.text.sms.service.apps.R.drawable.dialog_bg_stock)
        if (drawable is GradientDrawable) {
            val gradientDrawable = drawable
            val res = resources
            val cornerRadius = messagecorner
            gradientDrawable.cornerRadius = cornerRadius
            gradientDrawable.setStroke(1,binding.root.context.getProperPrimaryColor())
//            imageView.setImageDrawable(gradientDrawable)
            binding.changecorner.background = gradientDrawable
        }
//        binding.changecorner. = messagecorner
    }

}
