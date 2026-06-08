package com.messenger.phone.number.text.sms.service.apps.CustomGallery.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestBuilder
import java.io.File

class AlbumSelectionAdapter(private val glide: RequestBuilder<Bitmap>) : RecyclerView.Adapter<AlbumSelectionViewHolder>() {

    private var items = mapOf<File, List<File>>()
    private var indexes = listOf<File>()
    var context: Context? = null
    var folderclick: ((String) -> Unit)? = null

    override fun onBindViewHolder(holder: AlbumSelectionViewHolder, position: Int) {
        holder.bind(getItemAt(position), glide)
        context = holder.itemView.context
        holder.binding.albumImage.setOnClickListener {
            val file = getItemAt(position)
            if (file != null) {
                (file.isNotEmpty()).let {
                    val parent = File(file[0].parent as String)
                    folderclick?.invoke(parent.absolutePath)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumSelectionViewHolder =
        AlbumSelectionViewHolder.new(parent)

    override fun getItemCount(): Int = indexes.size

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(fileMap: Map<File, List<File>>) {
        items = fileMap
        indexes = fileMap.keys.toList().sortedBy { it.nameWithoutExtension }
        notifyDataSetChanged()
    }

    private fun getItemAt(position: Int) = items[indexes[position]]

}