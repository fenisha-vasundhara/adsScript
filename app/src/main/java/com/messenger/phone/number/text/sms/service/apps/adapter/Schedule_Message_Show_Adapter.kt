package com.messenger.phone.number.text.sms.service.apps.adapter

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.drawableCache
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getContactLetterIcon
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getNameAndPhotoFromPhoneNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.ScheduleMessageItemBinding
import com.messenger.phone.number.text.sms.service.apps.helper.RandomDrawableProvider
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.simplemobiletools.commons.helpers.SimpleContactsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class Schedule_Message_Show_Adapter @Inject constructor() :
    RecyclerView.Adapter<Schedule_Message_Show_Adapter.Schedule_Message_Show_Adapter_ViewHolder>() {

    var onMessageClick: ((Int, ArrayList<Conversation>) -> Unit)? = null
    var ismessagefind: Boolean = false
    var ismessagefindtime: Long = 0L
    var signature = ""

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    inner class Schedule_Message_Show_Adapter_ViewHolder(var binding: ScheduleMessageItemBinding) :
        ViewHolder(binding.root) {
        init {
            var con = binding.lastmessageshow.context

            if (con.config.userpreferenceSignatureOnOff) {
                signature = if (con.config.userpreferenceSignature[0] == '-') {
                    con.config.userpreferenceSignature
                } else {
                    "-" + con.config.userpreferenceSignature
                }
            } else {
                signature = con.config.userpreferenceSignature
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): Schedule_Message_Show_Adapter_ViewHolder {
        return Schedule_Message_Show_Adapter_ViewHolder(
            ScheduleMessageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return schedulemessagelist.size
    }

    override fun onBindViewHolder(holder: Schedule_Message_Show_Adapter_ViewHolder, position: Int) {
        val context = holder.itemView.context
        val textColor = context.getProperTextColor()
        val secondaryTextColor = context.getProperSecondaryTextColor()

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
            binding.conversationFrame.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            binding.number.setTextColor(textColor)
            binding.lastmessageshow.setTextColor(secondaryTextColor)
            binding.lastmessageshowdate.setTextColor(secondaryTextColor)
            binding.itemDivider.setBackgroundColor(secondaryTextColor.adjustAlpha(0.16f))
        }

        with(holder) {
            binding.executePendingBindings()
            binding.listdata = schedulemessagelist[position]
            if (schedulemessagelist[position].isgroupmessage) {
                binding.number.text = schedulemessagelist[position].groupName
                Log.d(
                    "schedulemessagelist",
                    "onBindViewHolder:schedulemessagelist <------> 1 ${schedulemessagelist[position].groupName}"
                )
            } else {
                binding.number.text = schedulemessagelist[position].title
                Log.d(
                    "schedulemessagelist",
                    "onBindViewHolder:schedulemessagelist <------> 2 ${schedulemessagelist[position].title}"
                )
            }
            val txt = schedulemessagelist[position].snippet
            val lines: List<String> = txt.split("\n")
            val lastLine: String = lines.get(lines.size - 1)
            if (lastLine.contains("<br><i>")) {
                binding.lastmessageshow.text = Html.fromHtml(txt);
            } else {
                binding.lastmessageshow.text = txt
            }

//            if (schedulemessagelist[position].title.isNotEmpty()) {
//                if (schedulemessagelist[position].title.first().isDigit()) {
//                    binding.contacticon.setImageBitmap(binding.contacticon.context.getContactLetterIcon(schedulemessagelist[position].title, schedulemessagelist[position].phoneNumber))
//                } else {
//                    val drawable = drawableCache[schedulemessagelist[position].threadId.toString()]
//                    if (drawable != null) {
//                        binding.contacticon.setImageDrawable(drawable)
//                    } else {
//                        val randomDrawable = RandomDrawableProvider(binding.contacticon.context).getRandomDrawable()
//                        binding.contacticon.setImageDrawable(randomDrawable)
//                        drawableCache[schedulemessagelist[position].threadId.toString()] = randomDrawable
//                    }
//                }
//            }


            if (schedulemessagelist[position].isgroupmessage) {
                binding.contacticon.setImageDrawable(
                    SimpleContactsHelper(binding.contacticon.context).getColoredGroupIcon(
                        schedulemessagelist[position].title
                    )
                )
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    val profile = binding.userProfileImage.context.getNameAndPhotoFromPhoneNumber(
                        schedulemessagelist[position].phoneNumber
                    )
                    if (profile.photoUri?.isNotEmpty() == true) {
                        CoroutineScope(Dispatchers.Main).launch {
                            try {
                                SimpleContactsHelper(binding.contacticon.context)
                                    .loadContactImage(
                                        profile.photoUri,
                                        binding.contacticon,
                                        if (schedulemessagelist.isEmpty()) {
                                            "jigar"
                                        } else {
                                            try {
                                                schedulemessagelist[position].title
                                            } catch (E: Exception) {
                                                "jigar"
                                            }
                                        },
                                        null
                                    )
                            } catch (e: Exception) {

                            }
                        }
                    } else {
                        try {
                            val drawable =
                                drawableCache[schedulemessagelist[position].threadId.toString()]
                            if (drawable != null) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    binding.contacticon.setImageDrawable(drawable)
                                }

                            } else {
                                val randomDrawable =
                                    RandomDrawableProvider(binding.contacticon.context).getRandomDrawable(
                                        schedulemessagelist[position].threadId!!
                                    )
                                CoroutineScope(Dispatchers.Main).launch {
                                    binding.contacticon.setImageDrawable(randomDrawable)
                                }
                                drawableCache[schedulemessagelist[position].threadId.toString()] =
                                    randomDrawable
                            }
                        } catch (E: Exception) {
                        }
                    }
                }
            }

            binding.conversationFrame.setOnClickListener {
                Log.d("setOnClickListener", "onBindViewHolder: setOnClickListener <---------> 444")
                onMessageClick?.invoke(position, schedulemessagelist)
            }
        }
    }

    var schedulemessagelist = ArrayList<Conversation>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
}
