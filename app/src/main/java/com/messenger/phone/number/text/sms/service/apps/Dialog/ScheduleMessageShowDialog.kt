package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getDialogBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.CommanDeleteBlockDialogItemBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.ScheduleMessageDialogBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.ScheduleMessageShowDialogItemBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import android.graphics.drawable.GradientDrawable
import javax.annotation.Nullable
import javax.inject.Inject

class ScheduleMessageShowDialog @Inject constructor() : MaterialDialogFragment() {

    private var bottomSheetDialog: Dialog? = null
    lateinit var binding: ScheduleMessageShowDialogItemBinding
    var schedulemessageshowdialogclick: ((String, Int, ArrayList<Conversation>) -> Unit)? = null
    var pos = -1
    var list: ArrayList<Conversation> = arrayListOf()
    var deletebuttonshow = true

    fun setData(pos: Int, list: ArrayList<Conversation>) {
        this.pos = pos
        this.list = list
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.schedule_message_show_dialog_item, container, false)


        val surfaceColor = requireContext().getDialogBackgroundColor()
        val onSurface = requireContext().getProperTextColor()
        val onSurfaceVariant = requireContext().getProperSecondaryTextColor()
        val primary = requireContext().getProperPrimaryColor()
        binding.isdarktheme = ThemeModeManager.isDarkThemeActive(requireContext())

        val container = (binding.root as? ViewGroup)?.getChildAt(0) ?: binding.root
        container.background = GradientDrawable().apply {
            cornerRadius = resources.getDimension(com.simplemobiletools.commons.R.dimen.material_dialog_corner_radius)
            setColor(surfaceColor)
        }
        applyTextColor(binding.root, onSurface)
        binding.dialogmessage.setTextColor(onSurface)
        binding.imageView25.setColorFilter(primary)
        binding.imageView26.setColorFilter(primary)
        binding.imageView27.setColorFilter(primary)
        binding.imageView28.setColorFilter(primary)


        if (deletebuttonshow) {
            binding.CopyText.visible()
            binding.Delete.visible()
        } else {
            binding.CopyText.gone()
            binding.Delete.gone()
        }
        binding.CopyText.setOnClickListener {
            schedulemessageshowdialogclick?.invoke("CopyText", pos, list)
        }
        binding.Delete.setOnClickListener {
            schedulemessageshowdialogclick?.invoke("Delete", pos, list)
        }
        binding.sendNow.setOnClickListener {
            schedulemessageshowdialogclick?.invoke("SendNow", pos, list)
        }
        binding.Edit.setOnClickListener {
            schedulemessageshowdialogclick?.invoke("EditMessage", pos, list)
        }
        return binding.root
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


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog

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

}
