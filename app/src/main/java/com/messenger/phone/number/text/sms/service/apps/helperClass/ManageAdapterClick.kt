package com.messenger.phone.number.text.sms.service.apps.helperClass

import android.annotation.SuppressLint
import android.view.View
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.selectedCatList
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.adapter.CatAdapter
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.catAdapterLongClick
import com.messenger.phone.number.text.sms.service.apps.modelClass.Category
import javax.inject.Inject

class ManageAdapterClick @Inject constructor() {

    @SuppressLint("NotifyDataSetChanged")
    fun OnClick(holder: CatAdapter.CatAdapterViewHolder, position: Int, list: ArrayList<Category>, catAdapter: CatAdapter, catAdapterLongClick: catAdapterLongClick, editmodeon: Boolean) {


        holder.itemView.setOnClickListener {
            if (list[position].catName == "All Messages"
                || list[position].catName == "Personal"
                || list[position].catName == "Transaction"
                || list[position].catName == "otp"
                || list[position].catName == "Offers"
            ) {

            } else {
                if (editmodeon) {
                    if (selectedCatList.isNotEmpty()) {
                        if (selectedCatList.contains(list[position].catName)) {
                            selectedCatList.remove(list[position].catName)
                            catAdapter.notifyDataSetChanged()
                            catAdapterLongClick.onLongClick(position, list)
                        } else {
                            list[position].catName.let {
                                selectedCatList.add(it)
                                catAdapter.notifyDataSetChanged()
                                catAdapterLongClick.onLongClick(position, list)
                            }
                        }
                    } else {
                        if (selectedCatList.contains(list[position].catName)) {
                            selectedCatList.remove(list[position].catName)
                            catAdapter.notifyDataSetChanged()
                            catAdapterLongClick.onLongClick(position, list)
                        } else {
                            list[position].catName.let {
                                selectedCatList.add(it)
                                catAdapter.notifyDataSetChanged()
                                catAdapterLongClick.onLongClick(position, list)
                            }
                        }
                    }
                } else {
                    catAdapterLongClick.onClick(list, position)
                }
            }
        }

        holder.itemView.setOnLongClickListener {
            if (list[position].catName == "All Messages"
                || list[position].catName == "Personal"
                || list[position].catName == "Transaction"
                || list[position].catName == "otp"
                || list[position].catName == "Offers"
            ) {

            } else {
                if (editmodeon) {
                    if (selectedCatList.contains(list[position].catName)) {
                        selectedCatList.remove(list[position].catName)
                        catAdapter.notifyDataSetChanged()
                    } else {
                        list[position].catName.let {
                            selectedCatList.add(it)
                            catAdapter.notifyDataSetChanged()
                        }
                    }
                    catAdapterLongClick.onLongClick(position, list)
                }
            }

            true
        }

        if (selectedCatList.contains(list[position].catName)) {
            holder.binding.selection.visibility = View.VISIBLE
        } else {
            holder.binding.selection.visibility = View.GONE
        }

    }
}