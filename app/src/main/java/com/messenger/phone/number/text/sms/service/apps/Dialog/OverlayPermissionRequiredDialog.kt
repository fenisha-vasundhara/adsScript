package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.LifecycleOwner
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.DialogOverlayPermissionRequiredBinding
import kotlin.math.roundToInt

class OverlayPermissionRequiredDialog(
    private val ctx: Context,
    private val onCancelClicked: () -> Unit,
    private val onAllowClicked: () -> Unit
) : Dialog(ctx) {

    private val binding: DialogOverlayPermissionRequiredBinding =
        DialogOverlayPermissionRequiredBinding.bind(
            LayoutInflater.from(ctx)
                .inflate(R.layout.dialog_overlay_permission_required, null, false)
        )

    init {
        setContentView(binding.root)
        setCancelable(false)
        window?.apply {
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            setLayout(
                (ctx.resources.displayMetrics.widthPixels * 0.9f).roundToInt(),
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
        }

        setupListeners()
        applyTheme()
    }

    private fun setupListeners() {
        binding.btnCancel.setOnClickListener {
            onCancelClicked()
            safeDismiss()
        }

        binding.btnAllow.setOnClickListener {
            onAllowClicked()
        }
    }

    private fun applyTheme() {
        val themeSelection = ctx.config.activeThemeSelection
        val isLightTheme = themeSelection == 1 || themeSelection == 4

        val backgroundColor = when (themeSelection) {
            2 -> R.color.toolbarcolor2
            3 -> R.color.toolbarcolor3new
            else -> R.color.lock_edit_screen
        }

        val cancelBackgroundColor = when (themeSelection) {
            2 -> R.color.toolbarcolor2stock
            3 -> R.color.toolbarcolor3new_
            else -> R.color.defnotdone_
        }

        binding.constCard.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(ctx, backgroundColor))
        binding.btnCancel.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(ctx, cancelBackgroundColor))
        binding.btnAllow.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.appcolor))

        val cancelTextColor = if (isLightTheme) R.color.black else R.color.white
        binding.btnCancel.setTextColor(ContextCompat.getColor(ctx, cancelTextColor))
        binding.btnAllow.setTextColor(ContextCompat.getColor(ctx, R.color.white))
    }

    fun safeDismiss() {
        try {
            if (!isShowing) return

            if (ctx is LifecycleOwner) {
                if (!ctx.lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.STARTED)) {
                    Log.w("Dialog", "Trying to dismiss after Activity destroyed")
                    return
                }
            }

            if (UIThread.isMainThread()) {
                super.dismiss()
            } else {
                UIThread.run {
                    try {
                        super.dismiss()
                    } catch (e: Exception) {
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    private object UIThread {
        fun isMainThread(): Boolean =
            android.os.Looper.myLooper() == android.os.Looper.getMainLooper()

        fun run(block: () -> Unit) =
            android.os.Handler(android.os.Looper.getMainLooper()).post(block)
    }
}
