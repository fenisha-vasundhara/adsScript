package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.adapter.BackupsFileDialogAdapter
import com.messenger.phone.number.text.sms.service.apps.databinding.AddCategoryDialogItemBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.BackupFileDialogItemBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.BackupFilesModel

class BackupsFileDialog(
    var datalist: ArrayList<BackupFilesModel> = arrayListOf(),
    var itemclick: ((BackupFilesModel) -> Unit),
) : MaterialDialogFragment() {

    private var bottomSheetDialog: Dialog? = null
    lateinit var binding: BackupFileDialogItemBinding
    private var backupsFileDialogAdapter = BackupsFileDialogAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BackupFileDialogItemBinding.inflate(inflater, container, false)
        binding.adapter = backupsFileDialogAdapter
        backupsFileDialogAdapter.datalist = datalist
        if (datalist.isEmpty()) {
            binding.backupsNotFound.visible()
        } else {
            binding.backupsNotFound.gone()
        }
        binding.imageView18.setOnClickListener {
            dismiss()
        }
        backupsFileDialogAdapter.itemclick = {
            itemclick.invoke(it)
            dismiss()
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