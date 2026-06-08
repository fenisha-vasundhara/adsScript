package com.messenger.phone.number.text.sms.service.apps.animation

import android.animation.Animator
import android.graphics.Color
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.messenger.phone.number.text.sms.service.apps.R

class MyItemAnimator : RecyclerView.ItemAnimator() {

    override fun animateDisappearance(viewHolder: RecyclerView.ViewHolder, preLayoutInfo: ItemHolderInfo, postLayoutInfo: ItemHolderInfo?): Boolean {
        return false
    }

    override fun animateAppearance(viewHolder: RecyclerView.ViewHolder, preLayoutInfo: ItemHolderInfo?, postLayoutInfo: ItemHolderInfo): Boolean {
        viewHolder.itemView.alpha = 0f
        viewHolder.itemView.translationY = 100f
        viewHolder.itemView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT)
                }
                override fun onAnimationEnd(animation: Animator) {
                    viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT)
                }
                override fun onAnimationCancel(animation: Animator) {
                    viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT)
                }
                override fun onAnimationRepeat(animation: Animator) {
                }
            })
            .start()
        return true
    }

    override fun animatePersistence(viewHolder: RecyclerView.ViewHolder, preLayoutInfo: ItemHolderInfo, postLayoutInfo: ItemHolderInfo): Boolean {
        return false
    }

    override fun animateChange(oldHolder: RecyclerView.ViewHolder, newHolder: RecyclerView.ViewHolder, preLayoutInfo: ItemHolderInfo, postLayoutInfo: ItemHolderInfo): Boolean {
        return false
    }

    override fun runPendingAnimations() {

    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {

    }

    override fun endAnimations() {

    }

    override fun isRunning(): Boolean {
        return false
    }
}