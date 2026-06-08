package com.messenger.phone.number.text.sms.service.apps.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createOptionBackground
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.data.SimpleContact
import com.messenger.phone.number.text.sms.service.apps.databinding.GroupMemberAdapterItemBinding
import javax.inject.Inject

class GroupMemberAdapter @Inject constructor() :
    RecyclerView.Adapter<GroupMemberAdapter.GroupMemberAdapterViewHolder>() {

    class GroupMemberAdapterViewHolder(var binding: GroupMemberAdapterItemBinding) :
        ViewHolder(binding.root)


    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): GroupMemberAdapterViewHolder {
        return GroupMemberAdapterViewHolder(
            GroupMemberAdapterItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return Datlist.size
    }

    override fun onBindViewHolder(holder: GroupMemberAdapterViewHolder, position: Int) {
        val context = holder.itemView.context
        with(holder) {
            fontSize10 = context.getTextSizeForeNormal10MS()
            fontSize13 = context.getTextSizeForeNormal13MS()
            fontSize18 = context.getTextSizeForeNormal18MS()
            fontSize8 = context.getTextSizeForeNormal8MS()
            fontSize15 = context.getTextSizeHometitleMS()
            binding.textsizechagefor10 = fontSize10
            binding.textsizechagefor13 = fontSize13
            binding.textsizechagefor18 = fontSize18
            binding.textsizechagefor8 = fontSize8
            binding.textsizechagefor15 = fontSize15


        }

        with(holder.binding) {
            datacon = Datlist[position]



            clMain.background = createOptionBackground(
                cornerSize = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._5sdp).toFloat(),
                fillColor = context.getProperPrimaryColor().adjustAlpha(0.1f),
                strokeColor = context.getProperBackgroundColor(),
                strokeWidth = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp).toFloat(),
                isTop = position == 0,
                isBottom =  position == (itemCount - 1)
            )


        }
    }

    var Datlist = ArrayList<SimpleContact>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
}
