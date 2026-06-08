package com.messenger.phone.number.text.sms.service.apps.adapter

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.color.MaterialColors
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.databinding.RecentSearchItemBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.RecentSearchAdapterInterface
import com.messenger.phone.number.text.sms.service.apps.modelClass.Recentsearch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class RecentSearchAdapter @Inject constructor() :
    RecyclerView.Adapter<RecentSearchAdapter.RecentSearchAdapterViewHolder>() {


    @Inject
    lateinit var repo: MessagerDatabaseRepo


    lateinit var recentSearchAdapterInterface: RecentSearchAdapterInterface


    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f


    class RecentSearchAdapterViewHolder(var binding: RecentSearchItemBinding) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecentSearchAdapterViewHolder {
        return RecentSearchAdapterViewHolder(
            RecentSearchItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return if (listdataget.size == 0) {
            0
        } else if (listdataget.size >= 5) {
            5
        } else {
            listdataget.size
        }
    }

    override fun onBindViewHolder(holder: RecentSearchAdapterViewHolder, position: Int) {
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

        with(holder) {
            binding.listdata = listdataget[position]
            val context = itemView.context
            val onSurfaceVariantColor =
                MaterialColors.getColor(context, com.google.android.material.R.attr.colorOnSurfaceVariant, 0)
            binding.recentSearchTxt.setTextColor(onSurfaceVariantColor)
            binding.recentSearchDeleteBtn.setColorFilter(onSurfaceVariantColor)

            binding.recentSearchDeleteBtn.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    repo.deleterecentsearchRepo(listdataget[position].recentsearch)
                }
            }
            itemView.setOnClickListener {
                recentSearchAdapterInterface.onRecentSearchClick(position, listdataget)
            }
        }
    }

    var listdataget = ArrayList<Recentsearch>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun setInterface(recentSearchAdapterInterface: RecentSearchAdapterInterface) {
        this.recentSearchAdapterInterface = recentSearchAdapterInterface
    }

}
