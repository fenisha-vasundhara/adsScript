package com.messenger.phone.number.text.sms.service.apps.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.fetchContactIdFromPhoneNumber
import com.messenger.phone.number.text.sms.service.apps.databinding.ItemConversationCatBinding
import com.messenger.phone.number.text.sms.service.apps.helperClass.CategoryContactlistAdapterClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.CategoryContactlistAdapterMessageClick
import com.messenger.phone.number.text.sms.service.apps.modelClass.CategoryNumber
import javax.inject.Inject

class CategoryContactlistAdapter @Inject constructor() : RecyclerView.Adapter<CategoryContactlistAdapter.CategoryContactlistAdapterViewHolder>() {

    var messageClick: CategoryContactlistAdapterMessageClick? = null

    @Inject
    lateinit var categoryContactlistAdapterClick: CategoryContactlistAdapterClick

    class CategoryContactlistAdapterViewHolder(var binding: ItemConversationCatBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryContactlistAdapterViewHolder {
        return CategoryContactlistAdapterViewHolder(ItemConversationCatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CategoryContactlistAdapterViewHolder, position: Int) {
        with(holder.binding) {
            listdata = list[position]
            number.text = fetchContactIdFromPhoneNumber(list[position].phoneNumber, number.context)
//            number.text = list[position].phoneNumber
        }
        categoryContactlistAdapterClick.onClick(this, position, holder, list,messageClick)
    }

    var list = ArrayList<CategoryNumber>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun setInterface(messageClick: CategoryContactlistAdapterMessageClick) {
        this.messageClick = messageClick
    }
}