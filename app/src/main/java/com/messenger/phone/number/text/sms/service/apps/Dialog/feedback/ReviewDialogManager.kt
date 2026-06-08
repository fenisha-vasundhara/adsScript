package com.messenger.phone.number.text.sms.service.apps.Dialog.feedback

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import com.messenger.phone.number.text.sms.service.apps.R
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.demo.adsmanage.Commen.firebaseFunnel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.messenger.phone.number.text.sms.service.apps.BuildConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMaterialBottomSheetTheme
import com.messenger.phone.number.text.sms.service.apps.databinding.ReviewFormBinding
import kotlinx.coroutines.launch

class ReviewDialogManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val onClose: () -> Unit
) {

    fun show() {
        val dialog = BottomSheetDialog(context, context.getMaterialBottomSheetTheme())
        val binding: ReviewFormBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.review_form,
            null,
            false
        )

        binding.okButton.setOnClickListener {
            binding.okButton.isEnabled = false
            val name = binding.nameInput.text.trim().toString()
            val email = binding.emailInput.text.trim().toString()
            val feedback = binding.feedbackInput.text.trim().toString()
            if (name.isNotEmpty() && email.isNotEmpty() && feedback.isNotEmpty()) {
                if (isEmail(email)) {
                    submitFeedback(name, email, feedback, dialog)
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.review_form_invalid_email),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.review_form_fill_all_fields),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.cancelButton.setOnClickListener {
            context.firebaseFunnel("Feedback_cancel")
            dialog.dismiss()
            onClose()
        }

        dialog.setContentView(binding.root)
        dialog.show()
    }

    fun isEmail(text: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()
    }

    private fun submitFeedback(
        name: String,
        email: String,
        feedback: String,
        dialog: BottomSheetDialog
    ) {
        lifecycleOwner.lifecycleScope.launch {
            val feedbackRequest = FeedbackRequest(
                appName = "Messages",
                appVersion = BuildConfig.VERSION_NAME,
                name = name,
                email_id = email,
                feedback = feedback
            )
            Log.d("FeedbackSubmit", "App Name: ${feedbackRequest.appName}")
            Log.d("FeedbackSubmit", "App Version: ${feedbackRequest.appVersion}")
            Log.d("FeedbackSubmit", "User Name: ${feedbackRequest.name}")
            Log.d("FeedbackSubmit", "User Email: ${feedbackRequest.email_id}")
            Log.d("FeedbackSubmit", "Feedback: ${feedbackRequest.feedback}")

            val apiService = FeedbackApiService()
            val success = apiService.submitFeedback(feedbackRequest)

            if (success) {
                Log.d("FeedbackSubmit", "Feedback submitted successfully")
                Toast.makeText(
                    context,
                    context.getString(R.string.review_form_submit_success),
                    Toast.LENGTH_SHORT
                ).show()
                context.baseConfig.isReviewSubmitted = true

                context.firebaseFunnel("Feedback_submit")
            } else {
                Log.e("FeedbackSubmit", "Failed to submit feedback")
                Toast.makeText(
                    context,
                    context.getString(R.string.review_form_submit_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
            dialog.dismiss()
            onClose()
        }
    }
}
