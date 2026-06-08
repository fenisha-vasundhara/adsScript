package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMaterialDialogTheme
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.RatingDialogLayoutBinding

class RatingDialogFragment : MaterialDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireActivity(), requireActivity().getMaterialDialogTheme())
        val inflater = requireActivity().layoutInflater
        val binding = RatingDialogLayoutBinding.inflate(inflater)
        val view = binding.root
        builder.setCancelable(false)
        val ratingBar = binding.ratingBar
        builder.setView(view)
            .setTitle(requireActivity().resources.getString(R.string.Rate_this_App))
            .setMessage(requireActivity().resources.getString(R.string.message_rating))
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }
            .setPositiveButton("Submit") { dialog, which -> }
        val dialog = builder.create()
        dialog.getButton(BUTTON_POSITIVE).setOnClickListener {

        }
        return dialog
    }
}
