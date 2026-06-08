package com.messenger.phone.number.text.sms.service.apps.adapter

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setchatthemecolorTintImfullappforrecyler
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.adapter.ListPopupWindowAdapter.ViewHolder
import com.messenger.phone.number.text.sms.service.apps.databinding.MainScreenMenuItemBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.Menumodel
import javax.inject.Inject

class MainScreenMenuAdapter @Inject constructor() :
    RecyclerView.Adapter<MainScreenMenuAdapter.MainScreenMenuAdapterViewHolder>() {

    class MainScreenMenuAdapterViewHolder(var binding: MainScreenMenuItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    var onClick: ((Int) -> Unit)? = null

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MainScreenMenuAdapterViewHolder {
        return MainScreenMenuAdapterViewHolder(
            MainScreenMenuItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return listmenu.size
    }

    override fun onBindViewHolder(holder: MainScreenMenuAdapterViewHolder, position: Int) {
        val context = holder.itemView.context
        val config = context.config

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

        holder.binding.modellist = listmenu[position]
        if (position == 4) {
            holder.binding.view1.gone()
        } else {
            holder.binding.view1.gone()
        }
        val isSystemMode = ThemeModeManager.getThemeMode(context) == ThemeModeManager.MODE_SYSTEM
        val isDarkTheme = ThemeModeManager.isDarkThemeActive(context)
        if (isSystemMode) {
            holder.binding.txt1.setTextColor(
                MaterialColors.getColor(holder.binding.root, com.google.android.material.R.attr.colorOnSurface)
            )
            holder.binding.view1.setBackgroundColor(
                MaterialColors.getColor(holder.binding.root, com.google.android.material.R.attr.colorOutlineVariant)
            )
        } else if (isDarkTheme) {
            holder.binding.txt1.setTextColor(context.resources.getColor(R.color.white))
            holder.binding.view1.setBackgroundColor(context.resources.getColor(R.color.dark_theme_line))
        } else {
            holder.binding.txt1.setTextColor(context.resources.getColor(R.color.newmessage))
            holder.binding.view1.setBackgroundColor(context.resources.getColor(R.color.black_line))
        }
        if (config.setABHomeActivityPref == "2") {
            holder.binding.imageView12.setchatthemecolorTintImfullappforrecyler(0)
        } else {
            holder.binding.imageView12.setColorFilter(
                if (isSystemMode) {
                    MaterialColors.getColor(holder.binding.root, R.attr.colorPrimary)
                } else {
                    ContextCompat.getColor(context, R.color.appcolor)
                },
                PorterDuff.Mode.SRC_IN
            )
        }

        holder.itemView.setOnClickListener {
            onClick?.invoke(position)
        }
    }

    var listmenu = ArrayList<Menumodel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
}
