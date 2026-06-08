package com.messenger.phone.number.text.sms.service.apps.helperClass

import android.annotation.SuppressLint
import android.view.View
import com.messenger.phone.number.text.sms.service.apps.CommanClass.selectedContactListdb
import com.messenger.phone.number.text.sms.service.apps.adapter.SelectContactAdapter
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.ContactNumberClick
import com.messenger.phone.number.text.sms.service.apps.modelClass.Contact
import javax.inject.Inject

class SelectContactAdapterClick @Inject constructor() {

    @SuppressLint("NotifyDataSetChanged")
    fun selectContactAdapterClick(holder: SelectContactAdapter.SelectContactAdapterViewHolder, position: Int, list: ArrayList<Contact>, selectContactAdapter: SelectContactAdapter, onClick: ContactNumberClick) {

        holder.itemView.setOnClickListener {

            if (selectedContactListdb.isNotEmpty()) {
                if (selectedContactListdb.contains(list[position].number)) {
                    selectedContactListdb.remove(list[position].number)
                    onClick.OnLongClick()
                    selectContactAdapter.notifyDataSetChanged()
                } else {
                    list[position].number.let {
                        selectedContactListdb.add(it)
                        onClick.OnLongClick()
                        selectContactAdapter.notifyDataSetChanged()
                    }
                }
            } else {
                onClick.onClick(list[position].onlynumber, position, list[position].name)
            }
        }

        holder.itemView.setOnLongClickListener {

            if (selectedContactListdb.contains(list[position].number)) {
                selectedContactListdb.remove(list[position].number)
                selectContactAdapter.notifyDataSetChanged()
            } else {
                list[position].number.let {
                    selectedContactListdb.add(it)
                    selectContactAdapter.notifyDataSetChanged()
                }
            }
            onClick.OnLongClick()
            true
        }

        if (selectedContactListdb.contains(list[position].number)) {
            holder.binding.selection.visibility = View.VISIBLE
        } else {
            holder.binding.selection.visibility = View.GONE
        }

    }

}