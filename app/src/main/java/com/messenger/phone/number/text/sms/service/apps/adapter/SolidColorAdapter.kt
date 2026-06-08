package com.messenger.phone.number.text.sms.service.apps.adapter

import android.graphics.Color
import android.provider.CalendarContract.Colors
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.messenger.phone.number.text.sms.service.apps.databinding.SolidColorItemBinding
import javax.inject.Inject

class SolidColorAdapter @Inject constructor() : RecyclerView.Adapter<SolidColorAdapter.SolidColorAdapterViewHolder>() {

    var colorclick: ((String) -> Unit)? = null

    class SolidColorAdapterViewHolder(var binding: SolidColorItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolidColorAdapterViewHolder {
        return SolidColorAdapterViewHolder(SolidColorItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return colorlist.size
    }

    override fun onBindViewHolder(holder: SolidColorAdapterViewHolder, position: Int) {
        val color = colorlist[position]
        Log.d("colorlist", "onCreate: colorlist <-------> ArrayList(colorlist) ${color}")
        holder.binding.albumImage.setBackgroundColor(Color.parseColor(color))
        holder.binding.albumImage.setOnClickListener {
            colorclick?.invoke(color)
        }
    }

    var colorlist = ArrayList<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

}