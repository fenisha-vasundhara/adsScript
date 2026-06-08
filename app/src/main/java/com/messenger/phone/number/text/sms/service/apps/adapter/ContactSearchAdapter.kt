package com.messenger.phone.number.text.sms.service.apps.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.color.MaterialColors
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.drawableCache
import com.messenger.phone.number.text.sms.service.apps.CommanClass.*
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.databinding.SelectContactItemBinding
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

class ContactSearchAdapter @Inject constructor() :
    RecyclerView.Adapter<ContactSearchAdapter.ConversationSearchAdapterViewHolder>() {


    // Variable to store the highlighted text
    var highlighttext = ""
    // Interface to handle click events
    lateinit var messageSearchAdapterClickInterface: MessageSearchAdapterClickInterface
    var isThreeItem = true
    lateinit var onClick: ContactNumberClick

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    class ConversationSearchAdapterViewHolder(var binding: SelectContactItemBinding) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ConversationSearchAdapterViewHolder {
        return ConversationSearchAdapterViewHolder(
            SelectContactItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
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
        if (position < 0) return
        val item = try {
            list[position]
        } catch (_: IndexOutOfBoundsException) {
            return
        }
        holder.binding.apply {
            val context = holder.itemView.context
            val onSurfaceColor =
                MaterialColors.getColor(context, com.google.android.material.R.attr.colorOnSurface, Color.BLACK)
            val onSurfaceVariantColor = MaterialColors.getColor(
                context,
                com.google.android.material.R.attr.colorOnSurfaceVariant,
                onSurfaceColor
            )
            listdata = item
            number.text = item.name
            number.setTextColor(onSurfaceColor)
            highlitetext(highlighttext, number, onSurfaceColor)
            fistalbhabase.gone()
            lastmessageshow.text = item.number.replace(" ", "")
            lastmessageshow.setTextColor(onSurfaceVariantColor)
            isLetterorNot = item.name?.firstOrNull()?.isLetter() == true
            highlitetext(highlighttext, lastmessageshow, onSurfaceColor)

            fontSize10 = context.getTextSizeForeNormal10MS()
            fontSize13 = context.getTextSizeForeNormal13MS()
            fontSize18 = context.getTextSizeForeNormal18MS()
            fontSize8 = context.getTextSizeForeNormal8MS()
            fontSize15 = context.getTextSizeHometitleMS()

            textsizechagefor10 = fontSize10
            textsizechagefor13 = fontSize13
            textsizechagefor18 = fontSize18
            textsizechagefor8 = fontSize8
            textsizechagefor15 = fontSize15

        }

        with(holder) {
            CoroutineScope(Dispatchers.IO).launch {
                val profile =
                    binding.userProfileImage.context.getNameAndPhotoFromPhoneNumber(item.number)
                if (profile.photoUri?.isNotEmpty() == true) {
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            SimpleContactsHelper(binding.contacticon.context)
                                .loadContactImage(
                                    profile.photoUri,
                                    binding.contacticon,
                                    if (item.number.isNotBlank()) item.number else "jigar",
                                    null
                                )
                        } catch (e: Exception) {

                        }
                    }
                } else {
                    try {
                        val drawable = drawableCache[item.number]
                        if (drawable != null) {
                            CoroutineScope(Dispatchers.Main).launch {
                                binding.contacticon.setImageDrawable(drawable)
                            }

                        } else {
                            val randomDrawable =
                                RandomDrawableProvider(binding.contacticon.context).getRandomDrawable()
                            CoroutineScope(Dispatchers.Main).launch {
                                binding.contacticon.setImageDrawable(randomDrawable)
                            }
                            drawableCache[item.number.toString()] = randomDrawable
                        }
                    } catch (E: Exception) {
                    }
                }
            }
        }

        holder.itemView.setOnClickListener {
            onClick.onClick(item.onlynumber, position, item.name)
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
