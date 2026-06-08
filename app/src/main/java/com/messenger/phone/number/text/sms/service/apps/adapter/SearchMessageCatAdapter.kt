package com.messenger.phone.number.text.sms.service.apps.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createOptionBackground
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.databinding.SearchMessageCatItemBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.Searchmessagecatmodel
import javax.inject.Inject

class SearchMessageCatAdapter @Inject constructor() : RecyclerView.Adapter<SearchMessageCatAdapter.SearchMessageCatAdapterViewHolder>() {

    class SearchMessageCatAdapterViewHolder(var binding: SearchMessageCatItemBinding) : RecyclerView.ViewHolder(binding.root)

    var catClick: ((Searchmessagecatmodel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchMessageCatAdapterViewHolder {
        return SearchMessageCatAdapterViewHolder(SearchMessageCatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SearchMessageCatAdapterViewHolder, position: Int) {
        val context = holder.binding.imageView36.context
        val primaryColor = context.getProperPrimaryColor()
        val textColor = context.getProperTextColor()
        with(holder.binding) {
            modelclassdata = list[position]
            imageView36.setImageResource(list[position].icon)
        }
        holder.itemView.setOnClickListener {
            catClick?.invoke(list[position])
        }

        holder.binding.mainlayoutbg.background = createOptionBackground(
            cornerSize = context.resources.getDimension(com.intuit.sdp.R.dimen._5sdp),
            fillColor = primaryColor.adjustAlpha(0.12f),
            strokeWidth = context.resources.getDimension(com.intuit.sdp.R.dimen._1sdp),
            strokeColor = primaryColor.adjustAlpha(0.36f),
            rippleColor = primaryColor.adjustAlpha(0.25f),
            showRipple = true
        )
        holder.binding.nameStr.setTextColor(textColor)
        holder.binding.imageView36.imageTintList = ColorStateList.valueOf(textColor)

    }

    var list = ArrayList<Searchmessagecatmodel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

}
