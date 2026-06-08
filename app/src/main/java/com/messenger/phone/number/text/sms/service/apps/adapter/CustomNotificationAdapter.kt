package com.messenger.phone.number.text.sms.service.apps.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createCustomDrawable
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.CustomNotificationItemBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.Customnotificationmodel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class CustomNotificationAdapter @Inject constructor() :
    RecyclerView.Adapter<CustomNotificationAdapter.CustomNotificationAdapterViewHoder>() {

    var ringtonclick: ((Int, Customnotificationmodel) -> Unit)? = null
    var ringtsetclickonclick: ((Int, Customnotificationmodel) -> Unit)? = null

    class CustomNotificationAdapterViewHoder(val binding: CustomNotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomNotificationAdapterViewHoder {
        return CustomNotificationAdapterViewHoder(
            CustomNotificationItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return ringtonlist.size
    }

    override fun onBindViewHolder(holder: CustomNotificationAdapterViewHoder, position: Int) {
        val context = holder.itemView.context
        val config = context.config

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
            data = ringtonlist[position]
            imageView39.setOnClickListener {
                ringtonclick?.invoke(position, ringtonlist[position])
            }
        }
        holder.itemView.setOnClickListener {
            ringtsetclickonclick?.invoke(position, ringtonlist[position])
        }
        if (ringtonlist[position].selected) {
            CoroutineScope(Dispatchers.Main).launch {
                val background = context.createCustomDrawable(
                    cornerRadiusResId = com.intuit.sdp.R.dimen._10sdp,
                    solidColorResId =
                    if (config.activeThemeSelection == 1) {
                        R.color.settingbuttonfullapp
                    } else if (config.activeThemeSelection == 2) {
                        R.color.toolbarcolor2
                    } else if (config.activeThemeSelection == 3) {
                        R.color.toolbarcolor3new
                    } else {
                        R.color.toolbarcolor2
                    },
                    strokeColorResId =
                    R.color.appcolor,
                    strokeWidthResId = com.intuit.sdp.R.dimen._1sdp
                )
                holder.binding.lanbtn.background = background
            }
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                val background = context.createCustomDrawable(
                    cornerRadiusResId = com.intuit.sdp.R.dimen._10sdp,
                    solidColorResId =
                    if (config.activeThemeSelection == 1) {
                        R.color.settingbuttonfullapp
                    } else if (config.activeThemeSelection == 2) {
                        R.color.toolbarcolor2
                    } else if (config.activeThemeSelection == 3) {
                        R.color.toolbarcolor3new
                    } else {
                        R.color.toolbarcolor2
                    },
                    strokeColorResId =
                    R.color.lanunselectedcolor,
                    strokeWidthResId = com.intuit.sdp.R.dimen._1sdp
                )
                holder.binding.lanbtn.background = background
            }
        }

    }

    var ringtonlist = ArrayList<Customnotificationmodel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

}