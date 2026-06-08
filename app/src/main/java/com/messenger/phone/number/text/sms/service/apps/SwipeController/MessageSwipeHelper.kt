package com.messenger.phone.number.text.sms.service.apps.SwipeController

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlin.math.roundToInt


class MessageSwipeHelper(
    var context: Context,
    val height: Int,
    val width: Int,
    val deleteColor: Int,
    val archiveColor: Int,
    val deleteIcon: Int,
    val archiveIcon: Int,
    val interfaceSwipAction: InterfaceSwipAction,
) : ItemTouchHelper.Callback() {

    private var mClearPaint: Paint? = null
    var swiplastClickTime: Long = 0L
    private var swipeInteractionEnabled = true
    private var activeSwipeViewHolder: RecyclerView.ViewHolder? = null

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int {
        if (!swipeInteractionEnabled) {
            return makeMovementFlags(0, 0)
        }

        if (activeSwipeViewHolder != null && activeSwipeViewHolder !== viewHolder) {
            return makeMovementFlags(0, 0)
        }

        return if (context.config.Swipe_Right == "Nothing") {
            makeMovementFlags(0, ItemTouchHelper.LEFT)
        } else {
            makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        }

    }

    init {
        mClearPaint = Paint()
        mClearPaint?.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        viewHolder1: RecyclerView.ViewHolder,
    ): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return swipeInteractionEnabled
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && viewHolder != null && swipeInteractionEnabled) {
            activeSwipeViewHolder = viewHolder
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.bindingAdapterPosition
        if (position == RecyclerView.NO_POSITION) {
            activeSwipeViewHolder = null
            return
        }
        if (direction == ItemTouchHelper.LEFT) {
            interfaceSwipAction.onSwipeRight(position, viewHolder.itemView)
        } else {
            interfaceSwipAction.onSwipeLeft(position, viewHolder.itemView)
        }
    }


    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
    ) {
        val itemView = viewHolder.itemView
        val isCancelled = dX == 0f && !isCurrentlyActive
        if (isCancelled) {
            clearCanvas(
                canvas,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(
                canvas,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
            return
        }

        RecyclerViewSwipeDecorator.Builder(
            canvas,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
            .addSwipeLeftBackgroundColor(deleteColor)
            .addSwipeLeftActionIcon(deleteIcon)
            .addSwipeRightBackgroundColor(archiveColor)
            .addSwipeRightActionIcon(archiveIcon)
            .create()
            .decorate();

        super.onChildDraw(
            canvas,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )

    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.apply {
            translationX = 0f
            translationY = 0f
            alpha = 1f
        }
        if (activeSwipeViewHolder === viewHolder) {
            activeSwipeViewHolder = null
        }
        if (recyclerView.isComputingLayout || recyclerView.scrollState != RecyclerView.SCROLL_STATE_IDLE) {
            recyclerView.post { recyclerView.invalidate() }
        } else {
            recyclerView.invalidate()
        }
    }


    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, mClearPaint!!)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7f
    }

    fun setSwipeInteractionEnabled(enabled: Boolean) {
        swipeInteractionEnabled = enabled
        if (!enabled) {
            return
        }

        if (activeSwipeViewHolder?.bindingAdapterPosition == RecyclerView.NO_POSITION) {
            activeSwipeViewHolder = null
        }
    }

    fun hasPendingSwipeState(): Boolean {
        return !swipeInteractionEnabled || activeSwipeViewHolder != null
    }

}
