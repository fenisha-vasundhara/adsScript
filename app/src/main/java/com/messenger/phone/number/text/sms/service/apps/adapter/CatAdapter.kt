package com.messenger.phone.number.text.sms.service.apps.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.demo.adsmanage.helper.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.CatAdapterItemBinding
import com.messenger.phone.number.text.sms.service.apps.helperClass.ManageAdapterClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.catAdapterLongClick
import com.messenger.phone.number.text.sms.service.apps.modelClass.Category
import javax.inject.Inject

class CatAdapter @Inject constructor() : RecyclerView.Adapter<CatAdapter.CatAdapterViewHolder>() {


    @Inject
    lateinit var onADClick: ManageAdapterClick

    var startDrag: ((holder: CatAdapter.CatAdapterViewHolder) -> Unit)? = null

    lateinit var catAdapterLongClick: catAdapterLongClick

    var editmodeon = false

    class CatAdapterViewHolder(var binding: CatAdapterItemBinding) : ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatAdapterViewHolder {
        return CatAdapterViewHolder(CatAdapterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CatAdapterViewHolder, position: Int) {
        with(holder.binding) {
            listdata = list[position]
            if (number.context.config.SelectedLanguage == "ar") {
                number.gravity = Gravity.END
            } else {
                number.gravity = Gravity.START
            }
        }

        if (list[position].catName == "otp") {
            holder.binding.number.text = holder.itemView.context.resources.getString(R.string.OTPs)
        } else if (list[position].catName == "All Messages") {
            holder.binding.number.text = holder.itemView.context.resources.getString(R.string.All_Messages_new)
        } else if (list[position].catName == "Personal") {
            holder.binding.number.text = holder.itemView.context.resources.getString(R.string.Personal)
        } else if (list[position].catName == "Transaction") {
            holder.binding.number.text = holder.itemView.context.resources.getString(R.string.Transaction)
        } else if (list[position].catName == "Offers") {
            holder.binding.number.text = holder.itemView.context.resources.getString(R.string.Offers)
        } else if (list[position].catName == "Create Category") {
            holder.binding.number.text = holder.itemView.context.resources.getString(R.string.Create_Category)
            holder.itemView.gone
        } else {
            holder.binding.number.text = list[position].catName
        }

        holder.binding.ivDrag.setOnTouchListener { v, event ->
            startDrag?.invoke(holder)
            true
        }

        if (editmodeon) {
            if (list[position].catName == "All Messages") {
                holder.binding.selectionBoder.gone()
            } else if (list[position].catName == "Personal") {
                holder.binding.selectionBoder.gone()
            } else if (list[position].catName == "Transaction") {
                holder.binding.selectionBoder.gone()
            } else if (list[position].catName == "otp") {
                holder.binding.selectionBoder.gone()
            } else if (list[position].catName == "Offers") {
                holder.binding.selectionBoder.gone()
            } else if (list[position].catName == "Create Category") {
                holder.binding.selectionBoder.gone()
            } else {
                holder.binding.selectionBoder.visible()
            }
            holder.binding.ivDrag.gone()
        } else {
            holder.binding.ivDrag.visible()
            holder.binding.selectionBoder.gone()
        }
        if (position == list.size - 1) {
            holder.binding.viewbottom.gone()
        } else {
            holder.binding.viewbottom.visible()
        }

        onADClick.OnClick(holder, position, list, this, catAdapterLongClick, editmodeon)
    }

    var list = ArrayList<Category>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setInterface(catAdapterLongClick: catAdapterLongClick) {
        this.catAdapterLongClick = catAdapterLongClick
    }

}