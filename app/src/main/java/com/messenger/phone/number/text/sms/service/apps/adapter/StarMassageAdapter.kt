package com.messenger.phone.number.text.sms.service.apps.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.fetchContactIdFromPhoneNumber
import com.messenger.phone.number.text.sms.service.apps.databinding.ItemStarConversationBinding
import com.messenger.phone.number.text.sms.service.apps.helperClass.StarMassageAdapterClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.CategoryContactlistAdapterMessageClick
import com.messenger.phone.number.text.sms.service.apps.modelClass.StarNumber
import javax.inject.Inject

class StarMassageAdapter @Inject constructor() : RecyclerView.Adapter<StarMassageAdapter.StarMassageAdapterViewHolder>() {

    var messageClick: CategoryContactlistAdapterMessageClick? = null

    @Inject
    lateinit var starMassageAdapterClick: StarMassageAdapterClick

    class StarMassageAdapterViewHolder(var binding: ItemStarConversationBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StarMassageAdapterViewHolder {
        return StarMassageAdapterViewHolder(ItemStarConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onBindViewHolder(holder: StarMassageAdapterViewHolder, position: Int) {
        with(holder.binding) {
            listdata = list[position]
//            number.text = fetchContactIdFromPhoneNumber(list[position].phoneNumber, number.context)
            number.text = list[position].phoneNumber
        }
        starMassageAdapterClick.StarMassageAdapterOnClick(this, position, list, messageClick, holder)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    var list = ArrayList<StarNumber>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun setInterface(messageClick: CategoryContactlistAdapterMessageClick) {
        this.messageClick = messageClick
    }

}