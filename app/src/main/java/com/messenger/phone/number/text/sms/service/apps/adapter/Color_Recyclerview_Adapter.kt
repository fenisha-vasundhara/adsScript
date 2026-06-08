package com.messenger.phone.number.text.sms.service.apps.adapter

import android.content.Context
import android.content.res.Resources
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.dpToPx
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.textPrimaryOnThemeForColor
import com.messenger.phone.number.text.sms.service.apps.databinding.ThemeListItemBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.ThemePaletteListItemBinding

class Color_Recyclerview_Adapter(var context: Context) :
    RecyclerView.Adapter<Color_Recyclerview_Adapter.Color_Recyclerview_Adapter_ViewHolder>() {

    var oncolorclick: ((Int) -> Unit)? = null

    var selectedColor: Int = -1
        set(value) {
            field = value
            iconTint = context.textPrimaryOnThemeForColor(value)
            notifyDataSetChanged()
        }

    private var iconTint = 0

    class Color_Recyclerview_Adapter_ViewHolder(var binding: ThemePaletteListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.palette.flexWrap = FlexWrap.WRAP
            binding.palette.flexDirection = FlexDirection.ROW
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): Color_Recyclerview_Adapter_ViewHolder {
        return Color_Recyclerview_Adapter_ViewHolder(
            ThemePaletteListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun onBindViewHolder(holder: Color_Recyclerview_Adapter_ViewHolder, position: Int) {
        val palette = list?.get(position)
        val context = holder.itemView.context
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val minPadding = (16 * 6).dpToPx(context)
        val size = if (screenWidth - minPadding > (56 * 5).dpToPx(context)) {
            56.dpToPx(context)
        } else {
            (screenWidth - minPadding) / 5
        }

        holder.binding.viewDivider.setBackgroundColor(
            context.getProperSecondaryTextColor().adjustAlpha(0.25f)
        )

        val swatchPadding = (screenWidth - size * 5) / 12
        holder.binding.palette.removeAllViews()
        holder.binding.palette.setPadding(
            swatchPadding,
            swatchPadding,
            swatchPadding,
            swatchPadding
        )
        val combinedList = reverseAndCombineLists(palette?.subList(0, 5), palette?.subList(5, 10))
        combinedList
            ?.mapIndexed { index, color ->
                val itemBinding = ThemeListItemBinding.inflate(
                    LayoutInflater.from(context),
                    holder.binding.palette,
                    false
                )
                with(itemBinding) {
                    root.setOnClickListener { oncolorclick?.invoke(color) }
                    theme.setCardBackgroundColor(color)
                    check.visibility = if (!context.config.ismorecolorselected) if (color == selectedColor) View.VISIBLE else View.GONE else View.GONE
                    check.setColorFilter(iconTint, PorterDuff.Mode.SRC_IN)
                    root.layoutParams = (root.layoutParams as FlexboxLayout.LayoutParams).apply {
                        height = size
                        width = size
                        isWrapBefore = index % 5 == 0
                        setMargins(swatchPadding, swatchPadding, swatchPadding, swatchPadding)
                    }
                }
                itemBinding.root
            }
            ?.forEach { theme -> holder.binding.palette.addView(theme) }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    var list: List<List<Int>>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun <T> reverseAndCombineLists(list1: List<T>?, list2: List<T>?): List<T>? {
        return list1?.let {
            if (list2 != null) {
                it + list2
            } else {
                it
            }
        }
    }

}
