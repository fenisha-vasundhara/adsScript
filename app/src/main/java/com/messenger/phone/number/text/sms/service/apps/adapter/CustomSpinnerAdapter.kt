package com.messenger.phone.number.text.sms.service.apps.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.CustomSpinnerItemBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.SpinnerModel

class CustomSpinnerAdapter(context: Context?, algorithmList: ArrayList<SpinnerModel?>?) : ArrayAdapter<SpinnerModel?>(context!!, 0, algorithmList!!) {

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: CustomSpinnerItemBinding
        val itemView: View

        if (convertView == null) {
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.custom_spinner_item,
                parent,
                false
            )
            itemView = binding.root
            itemView.tag = binding
        } else {
            binding = convertView.tag as CustomSpinnerItemBinding
            itemView = convertView
        }

        val textViewName = binding.spinnerText
        val bulletIcon = binding.bulletIcon
        val mainLnrSpr = binding.mainLnrSpr
        mainLnrSpr.setBackgroundColor(Color.TRANSPARENT)
//        mainLnrSpr.setBackgroundColor(mainLnrSpr.context.getProperPrimaryColor().adjustAlpha(0.1f))
        bulletIcon.drawable.setTint(bulletIcon.context.getProperPrimaryColor())
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

        val currentItem = getItem(position)
        currentItem?.let {
            textViewName.text = it.spinertxt
            bulletIcon.visibility = if (it.showbullets) View.VISIBLE else View.GONE
        }

        return itemView
    }
}