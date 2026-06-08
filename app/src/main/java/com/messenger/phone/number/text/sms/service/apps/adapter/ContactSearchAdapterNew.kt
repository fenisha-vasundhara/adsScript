package com.messenger.phone.number.text.sms.service.apps.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.drawableCache
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getNameAndPhotoFromPhoneNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.databinding.SelectContactItemBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.SelectContactItemNewForSearchBinding
import com.messenger.phone.number.text.sms.service.apps.helper.RandomDrawableProvider
import com.messenger.phone.number.text.sms.service.apps.highlighter.TextHighlighter
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.ContactNumberClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MessageSearchAdapterClickInterface
import com.messenger.phone.number.text.sms.service.apps.modelClass.Contact
import com.simplemobiletools.commons.helpers.SimpleContactsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContactSearchAdapterNew @Inject constructor() : RecyclerView.Adapter<ContactSearchAdapterNew.ConversationSearchAdapterViewHolder>() {


    var highlighttext = ""
    lateinit var messageSearchAdapterClickInterface: MessageSearchAdapterClickInterface
    var isThreeItem = true
    lateinit var onClick: ContactNumberClick

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    class ConversationSearchAdapterViewHolder(var binding: SelectContactItemNewForSearchBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationSearchAdapterViewHolder {
        return ConversationSearchAdapterViewHolder(SelectContactItemNewForSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return if (isThreeItem) {
            if (list.size == 0) {
                0
            } else if (list.size >= 10) {
                10
            } else {
                list.size
            }
        } else {
            list.size
        }

    }

    override fun onBindViewHolder(holder: ConversationSearchAdapterViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.binding.apply {
            listdata = list[position]
            number.text  = list[position].name
            highlitetext(highlighttext,number, Color.BLACK)
            isLetterorNot = list[position].name.toString()[0].isLetter()
        }

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

        with(holder){
            CoroutineScope(Dispatchers.IO).launch {
                val profile = binding.userProfileImage.context.getNameAndPhotoFromPhoneNumber(list[position].number)
                if (profile.photoUri?.isNotEmpty() == true) {
                    CoroutineScope(Dispatchers.Main).launch {
                        if (list.isEmpty()) {

                        }
                        try {
                            SimpleContactsHelper(binding.contacticon.context)
                                .loadContactImage(
                                    profile.photoUri, binding.contacticon, if (list.isEmpty()) {
                                        "jigar"
                                    } else {
                                        try {
                                            list[position].number
                                        } catch (E: Exception) {
                                            "jigar"
                                        }
                                    }, null
                                )
                        } catch (e: Exception) {

                        }
                    }
                } else {
                    try {
                        val drawable = drawableCache[list[position].number]
                        if (drawable != null) {
                            CoroutineScope(Dispatchers.Main).launch {
                                binding.contacticon.setImageDrawable(drawable)
                            }

                        } else {
                            val randomDrawable = RandomDrawableProvider(binding.contacticon.context).getRandomDrawable()
                            CoroutineScope(Dispatchers.Main).launch {
                                binding.contacticon.setImageDrawable(randomDrawable)
                            }
                            drawableCache[list[position].number.toString()] = randomDrawable
                        }
                    } catch (E: Exception) {
                    }
                }
            }
        }


        holder.itemView.setOnClickListener {
            onClick.onClick(list[position].onlynumber, position, list[position].name)
        }
    }

    private fun highlitetext(highlighttext: String, appnametxt: TextView, black: Int) {
        TextHighlighter()
            .setBackgroundColor(Color.parseColor("#F69191"))
            .setForegroundColor(black)
            .addTarget(appnametxt)
            .highlight(highlighttext, TextHighlighter.CASE_INSENSITIVE_MATCHER);
    }

    var list = ArrayList<Contact>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun setContactclick(onClick: ContactNumberClick) {
        this.onClick = onClick
    }

}