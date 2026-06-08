package com.messenger.phone.number.text.sms.service.apps.adapter

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.drawableCache
import com.messenger.phone.number.text.sms.service.apps.CommanClass.drawableCache2
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getContactLetterIconContact
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getNameAndPhotoFromPhoneNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isVisible
import com.messenger.phone.number.text.sms.service.apps.CommanClass.selectedCatList
import com.messenger.phone.number.text.sms.service.apps.CommanClass.selectedConList
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.data.SimpleContact
import com.messenger.phone.number.text.sms.service.apps.databinding.SelectContactItemNewBinding
import com.messenger.phone.number.text.sms.service.apps.helper.RandomDrawableProvider
import com.messenger.phone.number.text.sms.service.apps.helperClass.SelectContactAdapterClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.ContactNumberClickNew
import com.simplemobiletools.commons.extensions.normalizeString
import com.simplemobiletools.commons.helpers.SimpleContactsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

class SelectContactAdapter @Inject constructor() :
    RecyclerView.Adapter<SelectContactAdapter.SelectContactAdapterViewHolder>() {

    lateinit var onClick: ContactNumberClickNew

    var isselectedAdapter: Boolean = false

    var iscredgrop = true

    @Inject
    lateinit var selectedContactAdapterClick: SelectContactAdapterClick


    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    class SelectContactAdapterViewHolder(var binding: SelectContactItemNewBinding) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SelectContactAdapterViewHolder {
        return SelectContactAdapterViewHolder(
            SelectContactItemNewBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SelectContactAdapterViewHolder, position: Int) {
        val context = holder.itemView.context
        val backgroundColor = context.getProperBackgroundColor()
        val primaryColor = context.getProperPrimaryColor()
        val titleColor = context.getProperTextColor()
        val subtitleColor = ColorUtils.setAlphaComponent(titleColor, (255 * 0.74f).toInt())
        val selectionBorderColor = ColorUtils.setAlphaComponent(primaryColor, (255 * 0.42f).toInt())

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
            listdata = list[position]
            fistalbhabase.text = list[position].name.substring(0, 1)
            lastmessageshow.apply {
                text = TextUtils.join(", ", list[position].phoneNumbers.map { it.normalizedNumber })
            }

            root.setBackgroundColor(backgroundColor)
            number.setTextColor(titleColor)
            lastmessageshow.setTextColor(subtitleColor)
            fistalbhabase.setTextColor(primaryColor)

//            SimpleContactsHelper(holder.itemView.context).loadContactImage(
//                list[position].photoUri,
//                contacticon,
//                list[position].name
//            )


            CoroutineScope(Dispatchers.IO).launch {
                val profile =
                    contacticon.context.getNameAndPhotoFromPhoneNumber(list[position].phoneNumbers[0].normalizedNumber)
                if (profile.photoUri?.isNotEmpty() == true) {
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            SimpleContactsHelper(holder.itemView.context).loadContactImage(
                                list[position].photoUri,
                                contacticon,
                                list[position].name
                            )
                        } catch (e: Exception) {

                        }
                    }
                } else {
                    try {
                        val drawable = drawableCache2[list[position].name.toString()]
                        if (drawable != null) {
                            CoroutineScope(Dispatchers.Main).launch {
                                contacticon.setImageDrawable(drawable)
                            }

                        } else {
                            val randomDrawable =
                                BitmapDrawable(context.resources, getContactLetterIconContact(context,list[position].name))
                            CoroutineScope(Dispatchers.Main).launch {
                                contacticon.setImageDrawable(randomDrawable)
                            }
                            drawableCache2[list[position].name.toString()] =
                                randomDrawable
                        }
                    } catch (E: Exception) {
                    }
                }
            }



            try {
                if (position == 0) {
                    fistalbhabase.visible()
                    linetop.visible()
                } else {
                    if (list[position].name[0].uppercase(Locale.getDefault())
                            .normalizeString() == list[position - 1].name[0]
                            .uppercase(Locale.getDefault()).normalizeString()
                    ) {
                        fistalbhabase.gone()
                        linetop.gone()
//                    linebottom.visible()
                    } else {
                        fistalbhabase.visible()
                        linetop.visible()
//                    linebottom.gone()
                    }

                }
            } catch (e: Exception) {
            }

            if (iscredgrop) {
                selectionBoder.visible()
            } else {
                selectionBoder.gone()
            }

            holder.binding.selectionCon.setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN)
            holder.binding.selectionBoder.backgroundTintList = ColorStateList.valueOf(selectionBorderColor)


            if (selectedConList.contains(list[position])) {
                holder.binding.selectionCon.visibility = View.VISIBLE
            } else {
                holder.binding.selectionCon.visibility = View.GONE
            }
        }
        holder.itemView.setOnClickListener {
            try {
                onClick.onClick(list, position, list[position].name)
            } catch (e: Exception) {
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setContactclick(onClick: ContactNumberClickNew) {
        this.onClick = onClick
    }

    var list = ArrayList<SimpleContact>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

}
