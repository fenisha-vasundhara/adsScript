package com.messenger.phone.number.text.sms.service.apps.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.MessageTraslationItemBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.MessageTraslationModel
import javax.inject.Inject


class MessageTraslateAdapter @Inject constructor() :
    RecyclerView.Adapter<MessageTraslateAdapter.MessageTraslateAdapterViewHolder>() {

    var messagespeck: ((String, Int, Boolean) -> Unit)? = null


    class MessageTraslateAdapterViewHolder(var binding: MessageTraslationItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageTraslateAdapterViewHolder {
        return MessageTraslateAdapterViewHolder(
            MessageTraslationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: MessageTraslateAdapterViewHolder, position: Int) {
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

            val fillColor = cardMessageTraslation.context.getProperPrimaryColor().adjustAlpha(0.1f)

            cardMessageTraslation.setCardBackgroundColor(fillColor)
            dataitem = list[position]
            imageView39.setOnClickListener {
                messagespeck?.invoke(list[position].message, position, true)
            }
            imageView40.setOnClickListener {
                messagespeck?.invoke(list[position].traslationmessage, position, false)
            }
            if (list[position].speaktraslationmessage) {
                imageView40.setImageDrawable(imageView40.context.resources.getDrawable(R.drawable.baseline_volume_up_24))
            } else {
                imageView40.setImageDrawable(imageView40.context.resources.getDrawable(R.drawable.baseline_play_circle_24))
            }

            if (list[position].speakmessage) {
                imageView39.setImageDrawable(imageView39.context.resources.getDrawable(R.drawable.baseline_volume_up_24))
            } else {
                imageView39.setImageDrawable(imageView39.context.resources.getDrawable(R.drawable.baseline_play_circle_24))
            }
        }
    }

    var list = ArrayList<MessageTraslationModel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
}