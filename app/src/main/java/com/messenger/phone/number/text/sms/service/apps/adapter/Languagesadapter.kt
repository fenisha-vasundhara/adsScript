package com.messenger.phone.number.text.sms.service.apps.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import androidx.core.graphics.ColorUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createOptionBackground
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
import com.messenger.phone.number.text.sms.service.apps.databinding.LanguagesAdapterItemBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.Languagemodel
import javax.inject.Inject

class Languagesadapter @Inject constructor() : RecyclerView.Adapter<Languagesadapter.LanguagesadapterViewHolder>() {

    var SelectedLanguageClick: ((String) -> Unit)? = null

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    class LanguagesadapterViewHolder(var binding: LanguagesAdapterItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguagesadapterViewHolder {
        return LanguagesadapterViewHolder(
            LanguagesAdapterItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return listitem.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LanguagesadapterViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = listitem[position]

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

        val surfaceColor = context.getProperBackgroundColor()
        val onSurfaceColor = context.getProperTextColor()
        val onSurfaceVariantColor = context.getProperSecondaryTextColor()
        val primaryColor = context.getProperPrimaryColor()

        val rowBackground = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = context.resources.getDimension(com.intuit.sdp.R.dimen._10sdp)
            val fillColor =
                if (item.selected) ColorUtils.blendARGB(surfaceColor, primaryColor, 0.12f)
                else ColorUtils.blendARGB(surfaceColor, onSurfaceColor, 0.04f)
            setColor(fillColor)
            val strokeColor = if (item.selected) primaryColor else surfaceColor
            setStroke(context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp), strokeColor)
        }
//        createOptionBackground(
//            cornerSize = context.resources.getDimension(com.intuit.sdp.R.dimen._10sdp).toFloat(),
//            fillColor = if (item.selected) ColorUtils.blendARGB(surfaceColor, primaryColor, 0.12f)
//            else ColorUtils.blendARGB(surfaceColor, onSurfaceColor, 0.04f),
//            strokeWidth = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp).toFloat(),
//            strokeColor = if (item.selected) primaryColor else surfaceColor,
//            rippleColor = primaryColor.adjustAlpha(0.3f),
//            showRipple = true,
//        )


        holder.binding.lanbtn.background = rowBackground




        with(holder.binding) {
            list = item
            langTxt.text = "${item.language}  (${item.languagereal})"
            if (item.selected) {
                lanArrov.setImageResource(R.drawable.baseline_check_circle_24)
                lanArrov.setColorFilter(primaryColor)
                langTxt.setTextColor(primaryColor)
            } else {
                lanArrov.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
                lanArrov.setColorFilter(onSurfaceVariantColor)
                langTxt.setTextColor(onSurfaceColor)
            }
            lanbtn.setOnClickListener {
                SelectedLanguageClick?.invoke(item.languagecode)
            }
        }
    }

    var listitem = ArrayList<Languagemodel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun getItems(): List<Languagemodel> {
        return listitem
    }
}
