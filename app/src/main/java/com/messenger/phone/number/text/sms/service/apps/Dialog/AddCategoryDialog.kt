package com.messenger.phone.number.text.sms.service.apps.Dialog

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.messenger.phone.number.text.sms.service.apps.CategoryContactlistActivity
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.google.android.material.color.MaterialColors
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.AddCategoryDialogItemBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddCategoryDialog @Inject constructor() : MaterialDialogFragment() {

    private var bottomSheetDialog: Dialog? = null
    lateinit var binding: AddCategoryDialogItemBinding

    @Inject
    lateinit var messagerDatabaseRepo: MessagerDatabaseRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireContext().setLocal()
        binding = AddCategoryDialogItemBinding.inflate(inflater, container, false)
        val colors = requireContext().resolveDialogColors()
        with(binding) {
            android.util.Log.d("onViewCreated", "onViewCreated:onViewCreated <----> dialog ${drivingmodetxtnew.text}")
            imageView18.setOnClickListener {
                dismiss()
            }
            positivebuttonclick.setOnClickListener {
                val message = drivingmodetxtnew.text.toString()
                CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    if (messagerDatabaseRepo.isCatExitsRepo(message)) {
                        CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch { context?.getString(com.messenger.phone.number.text.sms.service.apps.R.string.Please_Select_Another_Category_Name)?.let { it1 -> context?.toastMess(it1) } }
                    } else if (message.trim().isEmpty()) {
                        CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch { context?.getString(com.messenger.phone.number.text.sms.service.apps.R.string.Please_Category_Name)?.let { it1 -> context?.toastMess(it1) } }
                    } else {
                        messagerDatabaseRepo.insertorupdatecatgoryrep(Category(0, message, message, false))
                        CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch { dismiss() }
                        startActivity(Intent(requireContext(), com.messenger.phone.number.text.sms.service.apps.CategoryContactlistActivity::class.java).putExtra("catname", message))
                    }
                }
            }
            negativebuttonclick.setOnClickListener {
                dismiss()
            }

            binding.cardMain.setCardBackgroundColor(colors.surface)
            binding.filledTextField.setBoxBackgroundColor(colors.optionFill)
            binding.filledTextField.setBoxStrokeColor(colors.outlineVariant)
            binding.drivingmodetxtnew.setTextColor(colors.onSurface)
            binding.drivingmodetxtnew.setHintTextColor(colors.onSurfaceVariant)
            binding.textView.setTextColor(colors.onSurface)
            binding.negativebuttonclick.setTextColor(colors.onSurfaceVariant)
            binding.positivebuttonclick.setTextColor(colors.primary)
            binding.positivebuttonclick.backgroundTintList = ColorStateList.valueOf(colors.optionFill)
            binding.positivebuttonclick.strokeColor = ColorStateList.valueOf(colors.primary)
            binding.positivebuttonclick.strokeWidth = resources.getDimensionPixelSize(R.dimen._1sdp)
            binding.positivebuttonclick.cornerRadius = resources.getDimensionPixelSize(R.dimen._7sdp)
            binding.imageView18.iconTint =
                android.content.res.ColorStateList.valueOf(colors.onSurfaceVariant)
            val rippleColor = ColorStateList.valueOf(
                MaterialColors.layer(colors.surface, colors.onSurface, 0.16f)
            )
            binding.positivebuttonclick.rippleColor = rippleColor
            binding.negativebuttonclick.rippleColor = rippleColor
            binding.imageView18.rippleColor = rippleColor
        }
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        binding.drivingmodetxtnew.setText("")
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
