package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.messenger.phone.number.text.sms.service.apps.Dialog.BaseDialogFragment
import com.messenger.phone.number.text.sms.service.apps.CategoryContactlistActivity
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createCustomDrawable
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.AddCategoryDialogItemBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.EditAutoReplyMessageDrivingModeDialogItemBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddCategoryDialogNew : BaseDialogFragment() {

    private var bottomSheetDialog: Dialog? = null
    lateinit var binding: AddCategoryDialogItemBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireContext().setLocal()
        binding = AddCategoryDialogItemBinding.inflate(inflater, container, false)
        val config = requireContext().config
        with(binding) {
            android.util.Log.d("onViewCreated", "onViewCreated:onViewCreated <----> dialog ${drivingmodetxtnew.text}")
            imageView18.setOnClickListener {
                dismiss()
            }
            positivebuttonclick.setOnClickListener {
                dismiss()
//                val message = drivingmodetxtnew.text.toString()
//                CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
//                    if (messagerDatabaseRepo.isCatExitsRepo(message)) {
//                        CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch { context?.getString(com.messenger.phone.number.text.sms.service.apps.R.string.Please_Select_Another_Category_Name)?.let { it1 -> context?.toastMess(it1) } }
//                    } else if (message.trim().isEmpty()) {
//                        CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch { context?.getString(com.messenger.phone.number.text.sms.service.apps.R.string.Please_Category_Name)?.let { it1 -> context?.toastMess(it1) } }
//                    } else {
//                        messagerDatabaseRepo.insertorupdatecatgoryrep(Category(0, message, message, false))
//                        CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch { dismiss() }
//                        startActivity(Intent(requireContext(), com.messenger.phone.number.text.sms.service.apps.CategoryContactlistActivity::class.java).putExtra("catname", message))
//                    }
//                }
            }
            negativebuttonclick.setOnClickListener {
                dismiss()
            }


        }
        if (config.activeThemeSelection == 1) {
            binding.filledTextField.setBoxBackgroundColor(resources.getColor(R.color.commandialogposbutton))
            binding.drivingmodetxtnew.setBackgroundColor(resources.getColor(R.color.commandialogposbutton))
            binding.drivingmodetxtnew.setTextColor(resources.getColor(R.color.signachurdialog))
            binding.drivingmodetxtnew.setHintTextColor(resources.getColor(R.color.signachurdialog))
        } else if (config.activeThemeSelection == 2) {
            binding.filledTextField.setBoxBackgroundColor(resources.getColor(R.color.chatbgcolor22))
            binding.drivingmodetxtnew.setBackgroundColor(resources.getColor(R.color.chatbgcolor22))
            binding.drivingmodetxtnew.setTextColor(resources.getColor(R.color.white))
            binding.drivingmodetxtnew.setHintTextColor(resources.getColor(R.color.white))
        } else if (config.activeThemeSelection == 3) {
            binding.filledTextField.setBoxBackgroundColor(resources.getColor(R.color.toolbarcolor3new3))
            binding.drivingmodetxtnew.setBackgroundColor(resources.getColor(R.color.toolbarcolor3new3))
            binding.drivingmodetxtnew.setTextColor(resources.getColor(R.color.white))
            binding.drivingmodetxtnew.setHintTextColor(resources.getColor(R.color.white))
        } else {
            binding.filledTextField.setBoxBackgroundColor(resources.getColor(R.color.commandialogposbutton))
            binding.drivingmodetxtnew.setBackgroundColor(resources.getColor(R.color.commandialogposbutton))
            binding.drivingmodetxtnew.setTextColor(resources.getColor(R.color.white))
            binding.drivingmodetxtnew.setHintTextColor(resources.getColor(R.color.white))
        }

        CoroutineScope(Dispatchers.Main).launch {
            val background2 = requireContext().createCustomDrawable(
                cornerRadiusResId = com.intuit.sdp.R.dimen._7sdp,
                solidColorResId =
                if (config.activeThemeSelection == 1) {
                    R.color.commandialogposbutton
                } else if (config.activeThemeSelection == 2) {
                    R.color.chatbgcolor22
                } else if (config.activeThemeSelection == 3) {
                    R.color.toolbarcolor3new3
                } else {
                    R.color.commandialogposbutton
                },
                strokeColorResId =
                R.color.appcolor,
                strokeWidthResId = com.intuit.sdp.R.dimen._1sdp
            )
//            binding.positivebuttonclick.background = background2
        }

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
        bottomSheetDialog?.setCancelable(false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
}
