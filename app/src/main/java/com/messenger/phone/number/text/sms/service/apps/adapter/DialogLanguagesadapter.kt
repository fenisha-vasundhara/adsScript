package com.messenger.phone.number.text.sms.service.apps.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.color.MaterialColors
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import android.graphics.Color
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.Dialog.applyDialogRipple
import com.messenger.phone.number.text.sms.service.apps.Dialog.resolveDialogColors
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.DialogLanguagesAdapterItemBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.LanguagesAdapterItemBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.Languagemodel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.core.graphics.ColorUtils
import javax.inject.Inject

class DialogLanguagesadapter @Inject constructor() : RecyclerView.Adapter<DialogLanguagesadapter.LanguagesadapterViewHolder>() {

    var SelectedLanguageClick: ((String,String) -> Unit)? = null

    class LanguagesadapterViewHolder(var binding: DialogLanguagesAdapterItemBinding) : ViewHolder(binding.root)

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguagesadapterViewHolder {
        return LanguagesadapterViewHolder(DialogLanguagesAdapterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return listitem.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LanguagesadapterViewHolder, position: Int) {
        with(holder.binding) {
            list = listitem[position]
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

            langTxt.text = listitem[position].language
            val colors = context.resolveDialogColors()
            val isDark = ColorUtils.calculateLuminance(colors.surface) < 0.5
            val primary = colors.primary
            val onSurfaceVariant = colors.onSurfaceVariant
            val onSurface = colors.onSurface
            val materialOnSurface = MaterialColors.getColor(
                context,
                com.google.android.material.R.attr.colorOnSurface,
                onSurface
            )
            val itemCornerRadius = context.resources.getDimension(com.intuit.sdp.R.dimen._5sdp)
            val strokeWidth = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp)

            val selected = listitem[position].selected
            val fillColor = if (selected) {
                MaterialColors.layer(colors.surface, colors.primary, if (isDark) 0.30f else 0.14f)
            } else {
                MaterialColors.layer(colors.surface, materialOnSurface, if (isDark) 0.16f else 0.05f)
            }
            val strokeColor = if (selected) {
                MaterialColors.layer(colors.surface, colors.primary, if (isDark) 0.40f else 0.24f)
            } else {
                MaterialColors.layer(colors.surface, materialOnSurface, if (isDark) 0.22f else 0.10f)
            }
            lanbtn.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = itemCornerRadius
                setColor(fillColor)

            }
            lanbtn.applyDialogRipple(colors, alpha = 0.12f)

            if (listitem[position].selected) {
                lanArrov.setColorFilter(primary);
                langTxt.setTextColor(primary)
            } else {
                lanArrov.setColorFilter(onSurfaceVariant, android.graphics.PorterDuff.Mode.MULTIPLY);
                langTxt.setTextColor(if (isDark) materialOnSurface else onSurfaceVariant)
            }
            lanbtn.setOnClickListener {
                SelectedLanguageClick?.invoke(listitem[position].languagecode,listitem[position].language)
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
