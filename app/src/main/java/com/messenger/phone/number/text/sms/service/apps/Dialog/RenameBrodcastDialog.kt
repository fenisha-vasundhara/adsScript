package com.messenger.phone.number.text.sms.service.apps.Dialog

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.app.Dialog
import android.content.res.ColorStateList
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
import com.messenger.phone.number.text.sms.service.apps.CategoryContactlistActivity
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.groupnamecustom
import com.messenger.phone.number.text.sms.service.apps.CommanClass.selectedCatList
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.RenameBrodcastDialogItemBinding
import com.google.android.material.color.MaterialColors
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setCardBackgroundColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class RenameBrodcastDialog @Inject constructor() : MaterialDialogFragment() {

    private var bottomSheetDialog: Dialog? = null
    lateinit var binding: RenameBrodcastDialogItemBinding
    var catname = ""
    var tredid = ""

    var dialogdismiss: ((String) -> Unit)? = null

    @Inject
    lateinit var messagerDatabaseRepo: MessagerDatabaseRepo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val colors = requireContext().resolveDialogColors()
        with(binding) {
            binding.drivingmodetxt.setText(catname)
            imageView18.setOnClickListener {
                dismiss()
            }
            binding.isdarktheme = ThemeModeManager.isDarkThemeActive(requireContext())
//            binding.root.setBackgroundColor(colors.surface)
            cardMain.setCardBackgroundColor(colors.surface)
            filledTextField.setBoxBackgroundColor(colors.optionFill)
            filledTextField.setBoxStrokeColor(colors.outlineVariant)
            drivingmodetxt.setTextColor(colors.onSurface)
            drivingmodetxt.setHintTextColor(colors.onSurfaceVariant)
            textView.setTextColor(colors.onSurface)
            negativebuttonclick.setTextColor(colors.onSurfaceVariant)
            positivebuttonclick.setTextColor(colors.primary)
//            positivebuttonclick.backgroundTintList = ColorStateList.valueOf(colors.optionFill)
//            positivebuttonclick.strokeColor = ColorSta/teList.valueOf(colors.primary)
//            positivebuttonclick.strokeWidth = resources.getDimensionPixelSize(R.dimen._1sdp)
//            positivebuttonclick.cornerRadius = resources.getDimensionPixelSize(R.dimen._7sdp)
            imageView18.iconTint =
                android.content.res.ColorStateList.valueOf(colors.onSurfaceVariant)
            val rippleColor = ColorStateList.valueOf(
                MaterialColors.layer(colors.surface, colors.onSurface, 0.16f)
            )
            positivebuttonclick.rippleColor = rippleColor
            negativebuttonclick.rippleColor = rippleColor
            imageView18.rippleColor = rippleColor
            positivebuttonclick.setOnClickListener {
                val message = drivingmodetxt.text.toString()
                CoroutineScope(Dispatchers.IO).launch {
                    if (messagerDatabaseRepo.isBrodcastExitsRepo(message)) {
                        CoroutineScope(Dispatchers.Main).launch { context?.getString(R.string.Please_Select_Another_Broadcast_Name)?.let { it1 -> context?.toastMess(it1) } }
                    } else if (message.isEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch { context?.getString(R.string.Please_Broadcast_Name)?.let { it1 -> context?.toastMess(it1) } }
                    } else {
                        if (message != "") {
                            groupnamecustom  = message
                            messagerDatabaseRepo.updateBroadCastNameRepo(message, tredid.toLong())
                            CoroutineScope(Dispatchers.Main).launch {
                                dismiss()
                                dialogdismiss?.invoke(message)
                            }
                        } else {
                            CoroutineScope(Dispatchers.Main).launch { context?.getString(R.string.Please_Broadcast_Name)?.let { it1 -> context?.toastMess(it1) } }
                        }
                    }
                }
            }
            negativebuttonclick.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.rename_brodcast_dialog_item, container, false)
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
