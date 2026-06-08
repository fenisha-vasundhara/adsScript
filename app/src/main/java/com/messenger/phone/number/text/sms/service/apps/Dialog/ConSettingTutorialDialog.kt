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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.TutorialDialogBinding

import javax.annotation.Nullable

class ConSettingTutorialDialog : MaterialDialogFragment() {

    private var bottomSheetDialog: Dialog? = null
    private var selection = 0

    lateinit var binding: TutorialDialogBinding

    @SuppressLint("SetTextI18n")
    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.tutorial_dialog, container, false)
        binding.messageloadinglottie.loop(true)
        selection = 1
        binding.bodyTxt.text = binding.bodyTxt.context.getText(R.string.con_theme)
        binding.idBtnnext.setOnClickListener {
            when (selection) {
                1 -> {
                    binding.messageloadinglottie.playAnimation()
                    binding.messageloadinglottie.loop(true)
                    binding.bodyTxt.text = binding.bodyTxt.context.getText(R.string.con_text)
                    selection = 2
                }

                2 -> {
                    binding.messageloadinglottie.playAnimation()
                    binding.messageloadinglottie.loop(true)
                    binding.bodyTxt.text = binding.bodyTxt.context.getText(R.string.con_swipe_motion)
                    selection = 3
                }

                3 -> {
                    binding.idBtnnext.context.config.consettingtutorialshow = false
                    bottomSheetDialog?.dismiss()
                }
            }

        }
        binding.idBtnSkip.setOnClickListener {
            binding.idBtnnext.context.config.consettingtutorialshow = false
            bottomSheetDialog?.dismiss()
        }
        return binding.root
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
        bottomSheetDialog?.setCancelable(false)
    }
}