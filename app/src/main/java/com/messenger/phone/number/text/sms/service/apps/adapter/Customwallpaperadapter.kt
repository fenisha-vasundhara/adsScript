package com.messenger.phone.number.text.sms.service.apps.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.databinding.CustomWallpaperAdapterItemBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.Customwallpaperadaptermodel

class Customwallpaperadapter :
    RecyclerView.Adapter<Customwallpaperadapter.CustomwallpaperadapterViewHoledr>() {

    var customwallpaperclick: ((Customwallpaperadaptermodel) -> Unit)? = null

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    class CustomwallpaperadapterViewHoledr(var binding: CustomWallpaperAdapterItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CustomwallpaperadapterViewHoledr {
        return CustomwallpaperadapterViewHoledr(
            CustomWallpaperAdapterItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: CustomwallpaperadapterViewHoledr, position: Int) {
        holder.setIsRecyclable(false)
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

        with(holder.binding) {
            adapterdata = datalist[position]
        }
        holder.itemView.setOnClickListener {
            customwallpaperclick?.invoke(datalist[position])
        }
    }

    var datalist = ArrayList<Customwallpaperadaptermodel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

}