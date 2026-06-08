package com.messenger.phone.number.text.sms.service.apps.helperClass

import android.app.Activity
import android.util.Log
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager

class ReviewManagerRE {

    fun review(
        fortesting: Boolean = false,
        activity: Activity,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val manager = if (fortesting) {
            FakeReviewManager(activity)
        } else {
            ReviewManagerFactory.create(activity)
        }
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo: ReviewInfo = task.result
                val flow = manager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener { innerTask ->
                    if (innerTask.isSuccessful) {
                        Log.d("Rating Flow", "Review flow launched successfully")
                        onSuccess.invoke()
                    } else {
                        Log.e("Rating Flow", "Failed to launch review flow", innerTask.exception)
                        innerTask.exception?.message?.let { onFailure.invoke(it) }
                    }
                }
            } else {
                val exception = task.exception
                exception?.message?.let { onFailure.invoke(it) }
            }
        }
    }

}