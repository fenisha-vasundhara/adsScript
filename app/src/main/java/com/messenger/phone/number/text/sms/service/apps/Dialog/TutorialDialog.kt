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

class TutorialDialog : MaterialDialogFragment() {

    private var bottomSheetDialog: Dialog? = null
    private var selection = 0

    lateinit var binding: TutorialDialogBinding

    @SuppressLint("SetTextI18n")
    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.tutorial_dialog, container, false)

        if (binding.idBtnnext.context.config.tutorialshow) {
            binding.messageloadinglottie.loop(true)
            selection = 1
            binding.bodyTxt.text = binding.bodyTxt.context.getText(R.string.con_time_click)
            binding.idBtnnext.setOnClickListener {
                when (selection) {
                    1 -> {
//                        binding.messageloadinglottie.setAnimation(R.raw.click_slide)
                        binding.messageloadinglottie.playAnimation()
                        binding.messageloadinglottie.loop(true)
                        binding.bodyTxt.text = binding.bodyTxt.context.getText(R.string.con_click_slide)
                        selection = 2
                    }

                    2 -> {
//                        binding.messageloadinglottie.setAnimation(R.raw.auto_replay)
                        binding.messageloadinglottie.playAnimation()
                        binding.messageloadinglottie.loop(true)
                        binding.bodyTxt.text = binding.bodyTxt.context.getText(R.string.con_autoreply)
                        selection = 3
                    }

                    3 -> {
                        binding.idBtnnext.context.config.tutorialshow = false
                        binding.idBtnnext.context.config.autoreplyshow = false
                        bottomSheetDialog?.dismiss()
                    }
                }
            }
        } else {
            if (binding.idBtnnext.context.config.autoreplyshow) {
//                binding.messageloadinglottie.setAnimation(R.raw.auto_replay)
                binding.messageloadinglottie.playAnimation()
                binding.messageloadinglottie.loop(true)
                binding.bodyTxt.text = binding.bodyTxt.context.getText(R.string.con_autoreply)

                binding.idBtnnext.setOnClickListener {
                    binding.idBtnnext.context.config.tutorialshow = false
                    binding.idBtnnext.context.config.autoreplyshow = false
                    bottomSheetDialog?.dismiss()
                }


            }
        }

        binding.idBtnSkip.setOnClickListener {
            binding.idBtnnext.context.config.tutorialshow = false
            binding.idBtnnext.context.config.autoreplyshow = false
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