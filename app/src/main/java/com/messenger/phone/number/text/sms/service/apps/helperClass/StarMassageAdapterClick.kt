package com.messenger.phone.number.text.sms.service.apps.helperClass

import android.annotation.SuppressLint
import android.view.View
import com.messenger.phone.number.text.sms.service.apps.CommanClass.categoryContactlistAdapterList
import com.messenger.phone.number.text.sms.service.apps.CommanClass.starMassageAdapterOnClickList
import com.messenger.phone.number.text.sms.service.apps.adapter.StarMassageAdapter
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.CategoryContactlistAdapterMessageClick
import com.messenger.phone.number.text.sms.service.apps.modelClass.StarNumber
import javax.inject.Inject

class StarMassageAdapterClick @Inject constructor() {

    @SuppressLint("NotifyDataSetChanged")
    fun StarMassageAdapterOnClick(starMassageAdapter: StarMassageAdapter, position: Int, list: ArrayList<StarNumber>, messageClick: CategoryContactlistAdapterMessageClick?, holder: StarMassageAdapter.StarMassageAdapterViewHolder) {

        holder.itemView.setOnClickListener {

            if (starMassageAdapterOnClickList.isNotEmpty()) {
                if (starMassageAdapterOnClickList.contains(list[position].phoneNumber)) {
                    starMassageAdapterOnClickList.remove(list[position].phoneNumber)
                    starMassageAdapter.notifyDataSetChanged()
                    messageClick?.OnLongClick()
                } else {
                    list[position].phoneNumber.let {
                        starMassageAdapterOnClickList.add(it)
                        starMassageAdapter.notifyDataSetChanged()
                        messageClick?.OnLongClick()
                    }
                }
            } else {
                messageClick?.onClick(list[position].phoneNumber, position)
            }
        }

        holder.itemView.setOnLongClickListener {

            if (starMassageAdapterOnClickList.contains(list[position].phoneNumber)) {
                starMassageAdapterOnClickList.remove(list[position].phoneNumber)
                starMassageAdapter.notifyDataSetChanged()
            } else {
                list[position].phoneNumber.let {
                    starMassageAdapterOnClickList.add(it)
                    starMassageAdapter.notifyDataSetChanged()
                }
            }
            messageClick?.OnLongClick()
            true
        }
        if (starMassageAdapterOnClickList.contains(list[position].phoneNumber)) {
            holder.binding.selection.visibility = View.VISIBLE
        } else {
            holder.binding.selection.visibility = View.GONE
        }

    }
}