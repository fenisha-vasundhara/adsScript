package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.data.Config
import com.messenger.phone.number.text.sms.service.apps.databinding.ColorThemeDialogItemBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.EditAutoReplyMessageDrivingModeDialogItemBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.MessageTextSizeDialogItemBinding
import javax.inject.Inject

class Color_Theme_Dialog @Inject constructor() : MaterialDialogFragment() {

    private var bottomSheetDialog: Dialog? = null
    lateinit var binding: ColorThemeDialogItemBinding
    private lateinit var config: Config
    var selectedThemeOption = -1
    var Color_Theme_Dialog_Click: (() -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        config = requireContext().config
        val colors = requireContext().resolveDialogColors()
        setChathemedefaultseletion(config.activeThemeSelection)
        with(binding) {
            binding.root.background = binding.root.background?.mutate()?.apply {
                setTint(colors.surface)
            } ?: run {
                android.graphics.drawable.ColorDrawable(colors.surface)
            }
            applyTextColors(binding.root, colors.onSurface)
            doneBtn.setTextColor(colors.primary)
            doneBtn.applyDialogRipple(colors, alpha = 0.16f)
            themeSelectedFont1.setOnClickListener {
                setChathemedefaultseletion(1)
            }

            themeSelectedFont2.setOnClickListener {
                setChathemedefaultseletion(2)
            }

            themeSelectedFont3.setOnClickListener {
                setChathemedefaultseletion(3)
            }

            themeSelectedFont4.setOnClickListener {
                setChathemedefaultseletion(4)
            }
            doneBtn.setOnClickListener {
                if (selectedThemeOption != -1) {
                    config.activeThemeSelection = selectedThemeOption
                    dismiss()
                } else {
                    dismiss()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireContext().setLocal()
        binding = DataBindingUtil.inflate(inflater, R.layout.color_theme_dialog_item, container, false)
        return binding.root
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
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Color_Theme_Dialog_Click?.invoke()
    }

    private fun setChathemedefaultseletion(chatThemeSelection: Int) {
        when (chatThemeSelection) {
            1 -> {
                selectedThemeOption = 1
                binding.themeSelected1.setImageResource(R.drawable.chacked_redio_button)
                binding.themeSelected2.setImageResource(R.drawable.unchacked_redio_button)
                binding.themeSelected3.setImageResource(R.drawable.unchacked_redio_button)
                binding.themeSelected4.setImageResource(R.drawable.unchacked_redio_button)
            }

            2 -> {
                selectedThemeOption = 2
                binding.themeSelected1.setImageResource(R.drawable.unchacked_redio_button)
                binding.themeSelected2.setImageResource(R.drawable.chacked_redio_button)
                binding.themeSelected3.setImageResource(R.drawable.unchacked_redio_button)
                binding.themeSelected4.setImageResource(R.drawable.unchacked_redio_button)
            }

            3 -> {
                selectedThemeOption = 3
                binding.themeSelected1.setImageResource(R.drawable.unchacked_redio_button)
                binding.themeSelected2.setImageResource(R.drawable.unchacked_redio_button)
                binding.themeSelected3.setImageResource(R.drawable.chacked_redio_button)
                binding.themeSelected4.setImageResource(R.drawable.unchacked_redio_button)
            }

            4 -> {
                selectedThemeOption = 4
                binding.themeSelected1.setImageResource(R.drawable.unchacked_redio_button)
                binding.themeSelected2.setImageResource(R.drawable.unchacked_redio_button)
                binding.themeSelected3.setImageResource(R.drawable.unchacked_redio_button)
                binding.themeSelected4.setImageResource(R.drawable.chacked_redio_button)
            }
        }
    }

    private fun applyTextColors(view: View, color: Int) {
        when (view) {
            is android.widget.TextView -> view.setTextColor(color)
            is ViewGroup -> {
                for (i in 0 until view.childCount) {
                    applyTextColors(view.getChildAt(i), color)
                }
            }
        }
    }

}
