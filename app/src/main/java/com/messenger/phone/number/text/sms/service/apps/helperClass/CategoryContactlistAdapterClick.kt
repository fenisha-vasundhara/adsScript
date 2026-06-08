package com.messenger.phone.number.text.sms.service.apps.helperClass

import android.annotation.SuppressLint
import android.view.View
import com.messenger.phone.number.text.sms.service.apps.CommanClass.categoryContactlistAdapterList
import com.messenger.phone.number.text.sms.service.apps.adapter.CategoryContactlistAdapter
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.CategoryContactlistAdapterMessageClick
import com.messenger.phone.number.text.sms.service.apps.modelClass.CategoryNumber
import javax.inject.Inject

class CategoryContactlistAdapterClick @Inject constructor() {

    @SuppressLint("NotifyDataSetChanged")
    fun onClick(categoryContactlistAdapter: CategoryContactlistAdapter, position: Int, holder: CategoryContactlistAdapter.CategoryContactlistAdapterViewHolder, list: ArrayList<CategoryNumber>, messageClick: CategoryContactlistAdapterMessageClick?) {

        holder.itemView.setOnClickListener {

            if (categoryContactlistAdapterList.isNotEmpty()) {
                if (categoryContactlistAdapterList.contains(list[position].phoneNumber)) {
                    categoryContactlistAdapterList.remove(list[position].phoneNumber)
                    categoryContactlistAdapter.notifyDataSetChanged()
                    messageClick?.OnLongClick()
                } else {
                    list[position].phoneNumber.let {
                        categoryContactlistAdapterList.add(it)
                        categoryContactlistAdapter.notifyDataSetChanged()
                        messageClick?.OnLongClick()
                    }
                }
            } else {
                messageClick?.onClick(list[position].phoneNumber, position)
            }
        }

        holder.itemView.setOnLongClickListener {

            if (categoryContactlistAdapterList.contains(list[position].phoneNumber)) {
                categoryContactlistAdapterList.remove(list[position].phoneNumber)
                categoryContactlistAdapter.notifyDataSetChanged()
            } else {
                list[position].phoneNumber.let {
                    categoryContactlistAdapterList.add(it)
                    categoryContactlistAdapter.notifyDataSetChanged()
                }
            }
            messageClick?.OnLongClick()
            true
        }
        if (categoryContactlistAdapterList.contains(list[position].phoneNumber)) {
            holder.binding.selection.visibility = View.VISIBLE
        } else {
            holder.binding.selection.visibility = View.GONE
        }

    }

}