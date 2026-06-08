package com.messenger.phone.number.text.sms.service.apps.CustomGallery.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.loadImage
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass.Photo
import com.messenger.phone.number.text.sms.service.apps.databinding.DateItemBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.ViewAllImageItemDateBinding
import com.simplemobiletools.commons.helpers.SimpleContactsHelper
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ViewAllImageWithDateAdapter(private val glide: RequestBuilder<Bitmap>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val DATEVIEW: Int = 1
    private val IMAGEVIEW: Int = 2

    var imageclick: ((String) -> Unit)? = null

    inner class ViewAllImageAdapterWithDateViewHolder(var binding: ViewAllImageItemDateBinding) : RecyclerView.ViewHolder(binding.root)

    inner class DateViewHolder(var binding: DateItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DATEVIEW -> {
                DateViewHolder(DateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }

            IMAGEVIEW -> {
                ViewAllImageAdapterWithDateViewHolder(ViewAllImageItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }

            else -> {
                ViewAllImageAdapterWithDateViewHolder(ViewAllImageItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DateViewHolder) {
            with(holder.binding) {
                val data = imagelist[position]
                dateSet.text = formatedate(data.lastModifieddate)
            }
        } else if (holder is ViewAllImageAdapterWithDateViewHolder) {
            with(holder.binding) {
                val data = imagelist[position]
                glide.load(data.path).centerCrop().into(albumImage)
                albumImage.setOnClickListener {
                    imageclick?.invoke(data.path)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return imagelist.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (imagelist[position].selected) {
            DATEVIEW
        } else {
            IMAGEVIEW
        }
    }


    var imagelist = ArrayList<Photo>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun formatedate(inputLong: Long): String? {
        val instant = Instant.ofEpochMilli(inputLong)
        val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy")
        val formattedDate = localDate.format(formatter)
        return formattedDate
    }

}