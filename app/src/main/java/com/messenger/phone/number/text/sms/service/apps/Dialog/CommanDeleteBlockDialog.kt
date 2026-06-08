package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getDialogBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.data.Config
import com.messenger.phone.number.text.sms.service.apps.databinding.CommanDeleteBlockDialogItemBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.CommanDeleteBlockDialogInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.annotation.Nullable

class CommanDeleteBlockDialog() : MaterialDialogFragment() {


    private var dialogtital: String? = null
    private var dialogmessage: String? = null
    private var positivebutton: String? = null
    private var negativebutton: String? = null
    private var whatfordialog: String? = null

    private lateinit var config: Config
    private var bottomSheetDialog: Dialog? = null
    lateinit var binding: CommanDeleteBlockDialogItemBinding
    private var commanDeleteBlockDialogInterface: CommanDeleteBlockDialogInterface? = null
    private var actionHandled = false

    companion object {
        fun newInstance(
            dialogtital: String,
            dialogmessage: String,
            positivebutton: String,
            negativebutton: String,
            whatfordialog: String
        ): CommanDeleteBlockDialog {
            return CommanDeleteBlockDialog().apply {
                arguments = Bundle().apply {
                    putString("dialogtital", dialogtital)
                    putString("dialogmessage", dialogmessage)
                    putString("positivebutton", positivebutton)
                    putString("negativebutton", negativebutton)
                    putString("whatfordialog", whatfordialog)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dialogtital = it.getString("dialogtital")
            dialogmessage = it.getString("dialogmessage")
            positivebutton = it.getString("positivebutton")
            negativebutton = it.getString("negativebutton")
            whatfordialog = it.getString("whatfordialog")
        }
    }

    override fun onAttach(context: android.content.Context) {
        super.onAttach(context)
        commanDeleteBlockDialogInterface = when {
            parentFragment is CommanDeleteBlockDialogInterface -> {
                parentFragment as CommanDeleteBlockDialogInterface
            }
            context is CommanDeleteBlockDialogInterface -> context
            else -> null
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        requireContext().setLocal()
        binding = DataBindingUtil.inflate(inflater, R.layout.comman_delete_block_dialog_item, container, false)
        binding.dialogmessage.text = dialogmessage
        binding.positivebuttontxt.text = positivebutton
        binding.negativebuttonclick.text = negativebutton
        binding.negativebuttonclick.setOnClickListener {
            actionHandled = true
            val target = commanDeleteBlockDialogInterface
            if (target != null && whatfordialog != null) {
                target.onnegative(whatfordialog!!)
            } else {
                dismissAllowingStateLoss()
            }
        }


        binding.positivebuttonclick.setOnClickListener {
            actionHandled = true
            val target = commanDeleteBlockDialogInterface
            if (target != null && whatfordialog != null) {
                target.onpositive(whatfordialog!!)
            } else {
                dismissAllowingStateLoss()
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            config = requireContext().config
            val surfaceColor = requireContext().getDialogBackgroundColor()


            val radius = resources.getDimension(com.simplemobiletools.commons.R.dimen.material_dialog_corner_radius)
            binding.mainBg.background = GradientDrawable().apply {
                cornerRadius = radius
                setColor(surfaceColor)
            }
            val onSurface = requireContext().getProperTextColor()
            val onSurfaceVariant = requireContext().getProperSecondaryTextColor()
            val primary = requireContext().getProperPrimaryColor()
            binding.dialogmessage.setTextColor(onSurface)
            binding.negativebuttonclick.setTextColor(onSurfaceVariant)
            binding.positivebuttontxt.setTextColor(primary)
        }

        return binding.root
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (!actionHandled && isAdded && !parentFragmentManager.isStateSaved) {
            val target = commanDeleteBlockDialogInterface
            if (target != null && whatfordialog != null) {
                target.onnegative(whatfordialog!!)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        commanDeleteBlockDialogInterface = null
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
        bottomSheetDialog?.setCancelable(true)
        bottomSheetDialog?.setCanceledOnTouchOutside(true)
    }

    fun setInterface(commanDeleteBlockDialogInterface: CommanDeleteBlockDialogInterface) {
        this.commanDeleteBlockDialogInterface = commanDeleteBlockDialogInterface
    }
}
