package com.messenger.phone.number.text.sms.service.apps.SwipeController

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isVisible
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setTxtColor
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.adapter.InMainAdapter
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation

class MessageSwipeController(private val context: Context) :
    ItemTouchHelper.Callback() {

    private var y: Float = 0.0f
    private var replyDrawable: Drawable? = null
    private var currentItemViewHolder: RecyclerView.ViewHolder? = null
    private var mView: View? = null
    private var dX = 0f
    private var replyButtonProgress = 0f
    private var lastReplyButtonAnimationTime: Long = 0
    private var swipeBack = false
    private var isVibrate = false
    private var startTracking = false
    private val density: Float = context.resources.displayMetrics.density

    private var isDragornot = true

    private val SWIPE_THRESHOLD = convertToDp(40)
    private val DRAWABLE_SIZE = convertToDp(16)
    private val MAX_TRANSLATION = convertToDp(80)
    var conlist: ArrayList<Conversation> = arrayListOf()


    init {
        Log.d("", "drawReplyButton:tertrtertertertr  <-----------> 13")
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        mView = viewHolder.itemView
        val adapterPosition = viewHolder.adapterPosition
//        val adapter = recyclerView.adapter as InMainAdapter
//        Log.d("", "drawReplyButton:tertrtertertertr  <-----------> 15 chack ${adapter.binding.datetxt.isVisible()}")
        isDragornot = when (conlist[viewHolder.position].type) {
            2 -> {
                true
            }

            1 -> {
                false
            }

            else -> {
                false
            }
        }


        val animation: AnimatedVectorDrawable

        try {
            when (conlist[viewHolder.position].messageStatus) {

                "delaytime" -> {
                    replyDrawable = context.getDrawable(R.drawable.vector_loader_animated)
                    val d: Drawable? = replyDrawable
                    if (d is AnimatedVectorDrawable) {
                        animation = d
                        animation.start()
                    }
                }

                "ScheduledMessage" -> {
                    replyDrawable = context.getDrawable(R.drawable.baseline_access_time_24)
                }

                "Sending" -> {
                    replyDrawable = context.getDrawable(R.drawable.message_sending_process_icon_new)
                }

                "Generic failure", "No service", "Null PDU", "Radio off", "Error", "SMS not delivered" -> {
                    replyDrawable = context.getDrawable(R.drawable.baseline_error_outline_24)
                }

                "SMS delivered" -> {
                    replyDrawable = if (context.config.isdeliveryconfirmation){
                        context.getDrawable(R.drawable.message_delivered_done_icon_new)
                    }else{
                        context.getDrawable(R.drawable.message_sending_process_icon_new)
                    }
                }

                else -> {
                    replyDrawable = if (context.config.isdeliveryconfirmation){
                        context.getDrawable(R.drawable.message_delivered_done_icon_new)
                    }else{
                        context.getDrawable(R.drawable.message_sending_process_icon_new)
                    }
                }
            }
        } catch (e: Exception) {

        }
        return makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.RIGHT)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = false
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ACTION_STATE_SWIPE) {
            setTouchListener(recyclerView, viewHolder)
        }
        if (mView?.translationX!! > convertToDp(-50) || dX > this.dX) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            this.dX = dX
            startTracking = true
        }
        currentItemViewHolder = viewHolder
        drawReplyButton(c)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        recyclerView.setOnTouchListener { _, motionEvent ->
            swipeBack = motionEvent.action == MotionEvent.ACTION_CANCEL || motionEvent.action == MotionEvent.ACTION_UP
            false
        }
    }

    private fun drawReplyButton(canvas: Canvas) {
        currentItemViewHolder ?: return
        val translationX = mView!!.translationX
        val newTime = System.currentTimeMillis()
        val dt = Math.min(17, newTime - lastReplyButtonAnimationTime) / 2
        lastReplyButtonAnimationTime = newTime
        val showing = translationX >= convertToDp(30)
        val showing1 = translationX <= -convertToDp(30)
        updateReplyButtonProgress(showing, showing1, dt.toFloat())
        val (alpha, scale) = calculateAlphaAndScale(showing, showing1)
        replyDrawable?.alpha = alpha
        Log.d("", "drawReplyButton:tertrtertertertr  <-----------> 12")
        drawReplyDrawable(canvas, scale)
    }

    private fun updateReplyButtonProgress(showing: Boolean, showing1: Boolean, dt: Float) {
        when {
            showing || showing1 -> {
                if (replyButtonProgress < 1.0f) {
                    replyButtonProgress += dt / 180.0f
                    if (replyButtonProgress > 1.0f) {
                        replyButtonProgress = 1.0f
                    } else {
                        mView?.invalidate()
                    }
                }
            }

            mView!!.translationX == 0.0f -> {
                replyButtonProgress = 0f
                startTracking = false
                isVibrate = false
            }

            else -> {
                if (replyButtonProgress > 0.0f) {
                    replyButtonProgress -= dt / 180.0f
                    if (replyButtonProgress < 0.1f) {
                        replyButtonProgress = 0f
                    } else {
                        mView?.invalidate()
                    }
                }
            }
        }
    }

    private fun calculateAlphaAndScale(showing: Boolean, showing1: Boolean): Pair<Int, Float> {
        val alpha: Int
        val scale: Float
        if (showing || showing1) {
            scale = if (replyButtonProgress <= 0.8f) 1.2f * (replyButtonProgress / 0.8f) else 1.2f - 0.2f * ((replyButtonProgress - 0.8f) / 0.2f)
            alpha = Math.min(255.0f, 255f * (replyButtonProgress / 0.8f)).toInt()
        } else {
            scale = replyButtonProgress
            alpha = Math.min(255.0f, 255f * replyButtonProgress).toInt()
        }
        return Pair(alpha, scale)
    }


    private fun drawReplyDrawable(canvas: Canvas, scale: Float) {
        y = (mView!!.top + mView!!.measuredHeight / 1.2).toFloat()
        val x = calculateDrawablePosition()
        replyDrawable?.setBounds(
            (x.toFloat() - DRAWABLE_SIZE.toFloat() * scale).toInt(),
            (y - DRAWABLE_SIZE.toFloat() * scale).toInt(),
            (x.toFloat() + DRAWABLE_SIZE.toFloat() * scale).toInt(),
            (y + DRAWABLE_SIZE.toFloat() * scale).toInt()
        )
        replyDrawable?.draw(canvas)
        replyDrawable?.alpha = 255
    }

    private fun calculateDrawablePosition(): Int {
        return when {
            mView!!.translationX > 0 -> {
                if (mView!!.translationX > MAX_TRANSLATION.toFloat()) {
                    MAX_TRANSLATION / 2
                } else {
                    (mView!!.translationX / 2f).toInt()
                }
            }

            0 > mView!!.translationX -> {
                if (mView!!.translationX < -MAX_TRANSLATION.toFloat()) {
                    mView!!.right + (mView!!.translationX / 2f).toInt()
                } else {
                    mView!!.right + (mView!!.translationX / 2f).toInt()
                }
            }

            else -> 0
        }
    }

    private fun convertToDp(pixel: Int): Int {
        return dp(pixel.toFloat())
    }

    private fun dp(value: Float): Int {
        return if (value == 0.0f) 0 else Math.ceil((density * value).toDouble()).toInt()
    }

    interface SwipeControllerActions {
        fun showReplyUI(position: Int)
    }

    fun getlistlive(conversations: List<Conversation>) {
        conlist = ArrayList(conversations)
    }

}
