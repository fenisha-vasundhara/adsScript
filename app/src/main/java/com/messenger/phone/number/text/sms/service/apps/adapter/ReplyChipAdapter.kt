package com.messenger.phone.number.text.sms.service.apps.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.nl.smartreply.SmartReplySuggestion
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.SmartReplyChipBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.ClickListener
import javax.inject.Inject

class ReplyChipAdapter @Inject constructor() : RecyclerView.Adapter<ReplyChipAdapter.ViewHolder>() {

    lateinit var listener: ClickListener
    var isCustomWallpaperMode: Boolean = false

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f


    fun SmartReplyInterface(listener: ClickListener) {
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            SmartReplyChipBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val context = holder.itemView.context

        with(holder) {
            fontSize10 = context.getTextSizeForeNormal10MS()
            fontSize13 = context.getTextSizeForeNormal13MS()
            fontSize18 = context.getTextSizeForeNormal18MS()
            fontSize8 = context.getTextSizeForeNormal8MS()
            fontSize15 = context.getTextSizeHometitleMS()

            binding.textsizechagefor10 = fontSize10
            binding.textsizechagefor13 = fontSize13
            binding.textsizechagefor18 = fontSize18
            binding.textsizechagefor8 = fontSize8
            binding.textsizechagefor15 = fontSize15
        }

        holder.binding.messagetxt = suggestions[position].text

        val isLightTheme = ThemeModeManager.shouldUseLightSystemBars(context)
        val backgroundColor = context.getProperBackgroundColor()
        val textColor = context.getProperTextColor()
        val secondaryTextColor = context.getProperSecondaryTextColor()
        val primaryColor = context.getProperPrimaryColor()

        val chipFillColor = when {
            // Keep chip solid when wallpaper is visible to avoid readability issues.
            isCustomWallpaperMode -> ColorUtils.blendARGB(backgroundColor, textColor, if (isLightTheme) 0.08f else 0.22f)
            isLightTheme -> ColorUtils.blendARGB(backgroundColor, primaryColor, 0.10f)
            else -> ColorUtils.blendARGB(backgroundColor, primaryColor, 0.10f)
        }
        val chipStrokeColor = if (isCustomWallpaperMode) {
            ColorUtils.blendARGB(textColor, backgroundColor, 0.75f)
        } else {
            ColorUtils.blendARGB(secondaryTextColor, backgroundColor, if (isLightTheme) 0.9f else 0.9f)
        }
        val chipTextColor = if (isCustomWallpaperMode) {
            textColor
        } else {
            ColorUtils.blendARGB(textColor, primaryColor, if (isLightTheme) 0.10f else 0.18f)
        }

        holder.binding.framchip.chipBackgroundColor = ColorStateList.valueOf(chipFillColor)
        holder.binding.framchip.chipStrokeColor = ColorStateList.valueOf(chipStrokeColor)
        holder.binding.framchip.chipStrokeWidth = 0f // context.resources.getDimension(com.intuit.sdp.R.dimen._1sdp)
        holder.binding.framchip.rippleColor = ColorStateList.valueOf(primaryColor.adjustAlpha(0.22f))
        holder.binding.framchip.closeIconTint = ColorStateList.valueOf(primaryColor)

        holder.binding.framchip.setTextColor(chipTextColor)


        holder.itemView.setOnClickListener {
            listener.onChipClick(suggestions[position].text)
        }
    }

    override fun getItemCount(): Int {
        return suggestions.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    var suggestions = ArrayList<SmartReplySuggestion>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


//    fun updateList(newList: List<SmartReplySuggestion>) {
//        val diffResult = DiffUtil.calculateDiff(ReplyDiffCallback(suggestions, newList))
//        suggestions.clear()
//        suggestions.addAll(newList)
//        diffResult.dispatchUpdatesTo(this)
//    }

    inner class ViewHolder(var binding: SmartReplyChipBinding) :
        RecyclerView.ViewHolder(binding.root)
}

class ReplyDiffCallback(
    private val oldList: List<SmartReplySuggestion>,
    private val newList: List<SmartReplySuggestion>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].text == newList[newItemPosition].text

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}
