package com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.afteradapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.RemainderAdapterItemBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.Remindermodel
import javax.inject.Inject

class RemainderAdapter @Inject constructor() :
    RecyclerView.Adapter<RemainderAdapter.RemainderAdapterViewHolder>() {

    var onItemClickListener: ((Remindermodel) -> Unit)? = null
    var onDeleteClickListener: ((Remindermodel) -> Unit)? = null
    var onEditClickListener: ((Remindermodel) -> Unit)? = null

    class RemainderAdapterViewHolder(val binding: RemainderAdapterItemBinding) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemainderAdapterViewHolder {
        return RemainderAdapterViewHolder(
            RemainderAdapterItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return remainderdata.size
    }

    override fun onBindViewHolder(holder: RemainderAdapterViewHolder, position: Int) {
        val data = remainderdata[position]
        with(holder.binding) {

            if (card.context.config.activeThemeSelection==1){
                card.setCardBackgroundColor(card.context.resources.getColor(R.color.white))
            }else{
                card.setCardBackgroundColor(card.context.resources.getColor(R.color.toolbarcolor3new))
            }

            notificationTitle.text = data.remindertitle
            notificationTime.text = data.reminderstartdate
            delete.setOnClickListener {
                onDeleteClickListener?.invoke(data)
            }
            edit.setOnClickListener {
                onEditClickListener?.invoke(data)
            }
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(data)
        }
    }

    var remainderdata = ArrayList<Remindermodel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
}