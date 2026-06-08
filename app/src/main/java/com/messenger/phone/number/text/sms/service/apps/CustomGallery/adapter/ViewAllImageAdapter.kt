package com.messenger.phone.number.text.sms.service.apps.CustomGallery.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass.AllImageModel
import com.messenger.phone.number.text.sms.service.apps.databinding.ViewAllImageItemBinding

class ViewAllImageAdapter(private val glide: RequestBuilder<Bitmap>) : RecyclerView.Adapter<ViewAllImageAdapter.ViewAllImageAdapterViewHolder>() {

    inner class ViewAllImageAdapterViewHolder(var binding: ViewAllImageItemBinding) : RecyclerView.ViewHolder(binding.root){
        val viewAllImageWithDateAdapter: ViewAllImageWithDateAdapter by lazy {
            ViewAllImageWithDateAdapter(glide)
        }

        init {
            binding.directoriesGridInner.apply {
                isNestedScrollingEnabled = true
                adapter = viewAllImageWithDateAdapter
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewAllImageAdapterViewHolder {
        return ViewAllImageAdapterViewHolder(ViewAllImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return imagelist.size
    }

    override fun onBindViewHolder(holder: ViewAllImageAdapterViewHolder, position: Int) {
        with(holder.binding) {
            val data = imagelist[position]
            textView24.text = data.date
            directoriesGridInner.setItemViewCacheSize(data.image.size)
            holder.viewAllImageWithDateAdapter.imagelist = data.image
        }
    }

    var imagelist = ArrayList<AllImageModel>()
        set(value) {
            field = value
            notifyItemInserted(field.size)
        }

}