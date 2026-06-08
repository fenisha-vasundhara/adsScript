package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.islinechange
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.data.Config
import com.messenger.phone.number.text.sms.service.apps.databinding.ColorThemeDialogItemBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.ConversationListViewDialogItemBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.EditAutoReplyMessageDrivingModeDialogItemBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.MessageCornerDialogItemBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.MessageTextSizeDialogItemBinding
import javax.inject.Inject

class Conversation_List_View_Dialog @Inject constructor() : MaterialDialogFragment() {

    private var bottomSheetDialog: Dialog? = null
    lateinit var binding: ConversationListViewDialogItemBinding
    var line_selection = -1
    private lateinit var config: Config
    var Conversation_List_View_Dialog_Dismiss: (() -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        config = requireContext().config
        setSeletionline(config.line_selection)
        with(binding) {
            constraintLayout16.setOnClickListener {
                setSeletionline(3)
                islinechange = true
            }

            constraintLayout15.setOnClickListener {
                setSeletionline(2)
                islinechange = true
            }

            doneBtn.setOnClickListener {
                if (line_selection != -1) {
                    config.line_selection = line_selection
                    dismiss()
                } else {
                    dismiss()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.conversation_list_view_dialog_item, container, false)
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
        Conversation_List_View_Dialog_Dismiss?.invoke()
    }

    private fun setSeletionline(i: Int) {
        when (i) {
            3 -> {
                binding.constraintLayout16.background = getDrawable(requireContext(), R.drawable.list_view_seleted_item)
                binding.constraintLayout15.background = getDrawable(requireContext(), R.drawable.list_view_unseleted_item)
                binding.threeLineSeletion.visible()
                binding.twoLineSeletion.gone()
                line_selection = 3
            }

            2 -> {
                binding.constraintLayout15.background = getDrawable(requireContext(), R.drawable.list_view_seleted_item)
                binding.constraintLayout16.background = getDrawable(requireContext(), R.drawable.list_view_unseleted_item)
                binding.threeLineSeletion.gone()
                binding.twoLineSeletion.visible()
                line_selection = 2
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