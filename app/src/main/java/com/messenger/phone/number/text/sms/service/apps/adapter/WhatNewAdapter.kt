package com.messenger.phone.number.text.sms.service.apps.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.messenger.phone.number.text.sms.service.apps.databinding.WhatNewItemBinding
import javax.inject.Inject

class WhatNewAdapter @Inject constructor() : RecyclerView.Adapter<WhatNewAdapter.WhatNewAdapterViewHolder>() {

    var whatistemclick: ((Int) -> Unit)? = null

    class WhatNewAdapterViewHolder(var binding: WhatNewItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WhatNewAdapterViewHolder {
        return WhatNewAdapterViewHolder(WhatNewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return whatnewlist.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WhatNewAdapterViewHolder, position: Int) {
        with(holder.binding) {
            whatsnew = whatnewlist[position]
            textView14.text = "${position+1}.  "
        }
        holder.itemView.setOnClickListener {
            whatistemclick?.invoke(position)
        }
    }

    var whatnewlist = ArrayList<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

}