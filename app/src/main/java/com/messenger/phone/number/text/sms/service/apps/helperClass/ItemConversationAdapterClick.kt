package com.messenger.phone.number.text.sms.service.apps.helperClass

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.annotation.SuppressLint
import android.view.View
import com.messenger.phone.number.text.sms.service.apps.CommanClass.selectedContactList
import com.messenger.phone.number.text.sms.service.apps.adapter.MainMassageAdapter
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ItemConversationAdapterClick @Inject constructor() {


    @Inject
    lateinit var repo: MessagerDatabaseRepo

    var isnumberselected: Boolean = false

    @SuppressLint("NotifyDataSetChanged")
    fun onClick(mainMassageAdapter: MainMassageAdapter, position: Int, holder: MainMassageAdapter.MainMassageAdapterViewHolder, list: ArrayList<Conversation>, isselectedAdapter: Boolean, catname: String) {

        holder.itemView.setOnClickListener {

            if (selectedContactList.isNotEmpty()) {
                if (selectedContactList.contains(list[position].phoneNumber)) {
                    selectedContactList.remove(list[position].phoneNumber)
                    mainMassageAdapter.notifyDataSetChanged()
                } else {
                    list[position].phoneNumber.let {
                        selectedContactList.add(it!!)
                        mainMassageAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        holder.itemView.setOnLongClickListener {

            if (selectedContactList.contains(list[position].phoneNumber)) {
                selectedContactList.remove(list[position].phoneNumber)
                mainMassageAdapter.notifyDataSetChanged()
            } else {
                list[position].phoneNumber.let {
                    selectedContactList.add(it!!)
                    mainMassageAdapter.notifyDataSetChanged()
                }
            }
            true
        }

        if (selectedContactList.contains(list[position].phoneNumber)) {
            holder.binding.selection.visibility = View.VISIBLE
        } else {
            holder.binding.selection.visibility = View.GONE
        }

//        CoroutineScope(Dispatchers.IO).launch {
//            if (repo.isCatNumberSelectedRepo(list[position].phoneNumber)) {
//                CoroutineScope(Dispatchers.Main).launch {
//                    holder.binding.selection.visibility = View.VISIBLE
//                }
//            } else {
//                CoroutineScope(Dispatchers.Main).launch {
//                    holder.binding.selection.visibility = View.GONE
//                }
//            }
//        }

    }

}