package com.messenger.phone.number.text.sms.service.apps.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.copyToClipboard
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.ParticipantsMobileListBinding
import com.simplemobiletools.commons.extensions.toast
import javax.inject.Inject

class ParticipantsmobileAdaper @Inject constructor() : RecyclerView.Adapter<ParticipantsmobileAdaper.ParticipantsmobileAdaperViewHolder>() {

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    class ParticipantsmobileAdaperViewHolder(var binding: ParticipantsMobileListBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantsmobileAdaperViewHolder {
        return ParticipantsmobileAdaperViewHolder(ParticipantsMobileListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return listdata.size
    }

    override fun onBindViewHolder(holder: ParticipantsmobileAdaperViewHolder, position: Int) {
        Log.d("withContext", "onCreate: withContext <------> ${listdata}")
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
            mobilenumber = listdata[position]
            numberCopyBtn.setOnClickListener {
                numberCopyBtn.context.copyToClipboard(listdata[position])
                numberCopyBtn.context.toast(numberCopyBtn.context.getString(R.string.Copied_to_Clipboard))
            }
        }
    }

    var listdata = ArrayList<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

}