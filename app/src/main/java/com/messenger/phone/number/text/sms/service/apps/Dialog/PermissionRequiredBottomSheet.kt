package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMaterialBottomSheetTheme
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isLightChatTheme
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.DialogPermissionRequiredBottomsheetBinding
import kotlin.math.roundToInt

class PermissionRequiredBottomSheet private constructor(
    private val activity: Activity
) : BottomSheetDialog(activity, activity.getMaterialBottomSheetTheme()) {

    private val binding: DialogPermissionRequiredBottomsheetBinding =
        DialogPermissionRequiredBottomsheetBinding.inflate(LayoutInflater.from(activity))

    private var onAllowClicked: (() -> Unit)? = null
    private var onCancelClicked: (() -> Unit)? = null

    companion object {
        fun create(
            activity: Activity,
            title: String,
            subtitle: String,
            step1: String,
            step2Template: String,
            step2Highlight: String,
            step3Template: String,
            step3Highlight: String,
            buttonText: String,
            onAllow: () -> Unit,
            onCancel: () -> Unit = {}
        ): PermissionRequiredBottomSheet {
            return PermissionRequiredBottomSheet(activity).apply {
                onAllowClicked = onAllow
                onCancelClicked = onCancel
                setContentView(binding.root)
                val highlightColor =
                    activity.getProperPrimaryColor()
                binding.txtTitle.text = title
                binding.txtSubtitle.text = subtitle
                binding.txtStep1.text = step1
                setHighlightedText(binding.txtStep2, step2Template, step2Highlight, highlightColor)
                setHighlightedText(binding.txtStep3, step3Template, step3Highlight, highlightColor)
                binding.btnAllow.text = buttonText
                initUI()
            }
        }
    }

    private fun initUI() {
        setCancelable(true)
        behavior.isHideable = true

        window?.apply {
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }

        val width = (activity.resources.displayMetrics.widthPixels * 0.94f).roundToInt()
        window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        setupListeners()
        applyTheme()
    }

    private fun setupListeners() {
        setOnCancelListener { onCancelClicked?.invoke() }
        binding.btnAllow.setOnClickListener {
            onAllowClicked?.invoke()
            safeDismiss()
        }
    }

    private fun applyTheme() {
        val isDark = !activity.isLightChatTheme()

        val bgColor =
            if (isDark) R.color.permission_bs_bg_dark else R.color.permission_bs_bg_light
        val titleColor =
            if (isDark) R.color.permission_bs_title_dark else R.color.permission_bs_title_light
        val textColor =
            if (isDark) R.color.permission_bs_text_dark else R.color.permission_bs_text_light

        binding.container.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(activity, bgColor))
        binding.txtTitle.setTextColor(ContextCompat.getColor(activity, titleColor))
        binding.txtSubtitle.setTextColor(ContextCompat.getColor(activity, textColor))
        binding.btnAllow.backgroundTintList =  ColorStateList.valueOf(activity.getProperPrimaryColor())
//        binding.txtStep1.setTextColor(ContextCompat.getColor(activity, textColor))
//        binding.txtStep2.setTextColor(ContextCompat.getColor(activity, textColor))
//        binding.txtStep3.setTextColor(ContextCompat.getColor(activity, textColor))
    }

    fun safeDismiss() {
        try {
            if (!isShowing) return
            val owner = activity as? LifecycleOwner
            if (owner != null && !owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                return
            }
            if (Looper.myLooper() == Looper.getMainLooper()) {
                dismiss()
            } else {
                Handler(Looper.getMainLooper()).post { dismiss() }
            }
        } catch (_: Exception) {
        }
    }
}

private fun setHighlightedText(
    textView: TextView,
    template: String,
    highlight: String,
    color: Int
) {
    val fullText = String.format(template, highlight)
    val spannable = SpannableString(fullText)
    val start = fullText.indexOf(highlight)
    if (start >= 0) {
        spannable.setSpan(
            ForegroundColorSpan(color),
            start,
            start + highlight.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    textView.text = spannable
}
