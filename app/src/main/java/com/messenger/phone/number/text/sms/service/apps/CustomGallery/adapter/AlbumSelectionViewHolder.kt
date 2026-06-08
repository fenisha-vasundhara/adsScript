package com.messenger.phone.number.text.sms.service.apps.CustomGallery.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestBuilder
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.getImageVideoNumber
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.AlbumBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AlbumSelectionViewHolder(var binding: AlbumBinding) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun new(viewGroup: ViewGroup) = AlbumSelectionViewHolder(
            AlbumBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        )
    }

    fun bind(files: List<File>?, glide: RequestBuilder<Bitmap>) {
        if (files != null) {
            (files.isNotEmpty()).let {
                val firstChild = files[0]
                val parent = File(firstChild.parent as String)
                with(binding) {
                    albumTitle.text = parent.nameWithoutExtension
                    glide.load(firstChild).centerCrop().into(albumImage)

                    var items = 0
                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            try {
                                items = getImageVideoNumber(parent)
                            } catch (_: Exception) {
                            }
                        }
                        withContext(Dispatchers.Main) {
                            albumCounter.text = if (items > 1) albumCounter.context.getString(R.string.multi_items, items.toString()) else albumCounter.context.getString(R.string.single_item)
                        }
                    }

                }
            }
        }
    }
}