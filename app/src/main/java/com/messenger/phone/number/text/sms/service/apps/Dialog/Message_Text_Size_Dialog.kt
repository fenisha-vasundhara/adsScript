package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.color.MaterialColors
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getDialogBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.data.Config
import com.messenger.phone.number.text.sms.service.apps.databinding.EditAutoReplyMessageDrivingModeDialogItemBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.MessageTextSizeDialogItemBinding
import javax.inject.Inject

class Message_Text_Size_Dialog @Inject constructor() : MaterialDialogFragment() {

    private var bottomSheetDialog: Dialog? = null
    lateinit var binding: MessageTextSizeDialogItemBinding
    private lateinit var config: Config

    var fontsizeselection = "no"
    var fontsize = -1

    var fontsizeselectionold = "no"
    var fontsizeold = -1

    var Message_Text_Size_Dialog_Dismiss: (() -> Unit)? = null

    var doneclickofmessagetextsize: ((Boolean, String, Int) -> Unit)? = null

    var livechangemessagetextsize: ((Boolean, String, Int) -> Unit)? = null

    var isdonebuttonclick = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isdonebuttonclick = false
        config = requireContext().config
        setdefaultselection()
        applyDialogTheme()

        fontsizeselectionold = config.fontsizeselection

        fontsizeold = config.fontsize

        with(binding) {
            selectedFont1.setOnClickListener {
                setselection(1)
                liveaupdate()
            }
            selectedFont2.setOnClickListener {
                setselection(2)
                liveaupdate()
            }
            selectedFont3.setOnClickListener {
                setselection(3)
                liveaupdate()
            }
            selectedFont4.setOnClickListener {
                setselection(4)
                liveaupdate()
            }

            doneBtn.setOnClickListener {
                isdonebuttonclick = true
                if (fontsizeselection != "no" || fontsize != -1) {
                    config.fontsizeselection = fontsizeselection
                    config.homemessagetitlesize = fontsizeselection
                    config.fontsize = fontsize
                    dismiss()
                } else {
                    dismiss()
                }
            }

            resetBtn.setOnClickListener {
                isdonebuttonclick = true
                config.fontsizeselection = "Normal"
                config.homemessagetitlesize = "Normal"
                config.fontsize = 20
                dismiss()
            }
        }
    }

    private fun applyDialogTheme() {
        val surfaceColor = requireContext().getDialogBackgroundColor()
        val onSurface = requireContext().getProperTextColor()
        val onSurfaceVariant = requireContext().getProperSecondaryTextColor()
        val primary = requireContext().getProperPrimaryColor()

        binding.root.background = GradientDrawable().apply {
            cornerRadius = resources.getDimension(com.simplemobiletools.commons.R.dimen.material_dialog_corner_radius)
            setColor(surfaceColor)
        }

        // Default text colors
        applyTextColor(binding.root, onSurface)

        // Action text colors
        (binding.resetBtn.getChildAt(0) as? TextView)?.setTextColor(primary)
        (binding.doneBtn.getChildAt(0) as? TextView)?.setTextColor(
            MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorOnPrimary, Color.WHITE)
        )

        // Buttons background
        val optionFill = MaterialColors.layer(surfaceColor, onSurface, 0.08f)
        binding.resetBtn.background = GradientDrawable().apply {
            cornerRadius = resources.getDimension(com.intuit.sdp.R.dimen._6sdp)
            setColor(optionFill)
        }
        binding.doneBtn.background = GradientDrawable().apply {
            cornerRadius = resources.getDimension(com.intuit.sdp.R.dimen._6sdp)
            setColor(primary)
        }
    }

    private fun applyTextColor(view: View, color: Int) {
        when (view) {
            is TextView -> view.setTextColor(color)
            is ViewGroup -> {
                for (i in 0 until view.childCount) {
                    applyTextColor(view.getChildAt(i), color)
                }
            }
        }
    }

    private fun liveaupdate() {
        if (fontsizeselection != "no" || fontsize != -1) {
            config.fontsizeselection = fontsizeselection
            config.homemessagetitlesize = fontsizeselection
            config.fontsize = fontsize
            livechangemessagetextsize?.invoke(isdonebuttonclick, fontsizeselectionold, fontsizeold)
        }

    }

     fun setdefaultselection() {
        when (config.fontsizeselection) {
            "Small" -> {
                setselection(1)
            }

            "Normal" -> {
                setselection(2)
            }

            "Large" -> {
                setselection(3)
            }

            "Extra Large" -> {
                setselection(4)
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.message_text_size_dialog_item, container, false)
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
//        bottomSheetDialog?.window?.setDimAmount(0F)
        bottomSheetDialog?.window?.setDimAmount(0.2F)
        bottomSheetDialog!!.window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog?.window?.setGravity(Gravity.BOTTOM)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Message_Text_Size_Dialog_Dismiss?.invoke()
        doneclickofmessagetextsize?.invoke(isdonebuttonclick, fontsizeselectionold, fontsizeold)
        setdefaultselection()
    }

    fun setselection(selection: Int) {
        when (selection) {
            1 -> {
                fontsizeselection = "Small"
                fontsize = 15
                binding.selectedFont1.background = getDrawable(requireContext(), R.drawable.fontsizeseletd_bg)
                binding.selectedFont2.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
                binding.selectedFont3.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
                binding.selectedFont4.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
            }

            2 -> {
                fontsizeselection = "Normal"
                fontsize = 20
                binding.selectedFont1.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
                binding.selectedFont2.background = getDrawable(requireContext(), R.drawable.fontsizeseletd_bg)
                binding.selectedFont3.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
                binding.selectedFont4.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
            }

            3 -> {
                fontsizeselection = "Large"
                fontsize = 30
                binding.selectedFont1.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
                binding.selectedFont2.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
                binding.selectedFont3.background = getDrawable(requireContext(), R.drawable.fontsizeseletd_bg)
                binding.selectedFont4.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
            }

            4 -> {
                fontsizeselection = "Extra Large"
                fontsize = 40
                binding.selectedFont1.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
                binding.selectedFont2.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
                binding.selectedFont3.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
                binding.selectedFont4.background = getDrawable(requireContext(), R.drawable.fontsizeseletd_bg)
            }
        }

    }

    fun getDrawable(context: Context, id: Int): Drawable? {
        val version = Build.VERSION.SDK_INT
        return if (version >= 21) {
            ContextCompat.getDrawable(context, id)
        } else {
            context.resources.getDrawable(id)
        }
    }


}
