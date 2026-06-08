package com.messenger.phone.number.text.sms.service.apps.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ATTACHMENT_DOCUMENT
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ATTACHMENT_MEDIA
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ATTACHMENT_VCARD
import com.messenger.phone.number.text.sms.service.apps.CommanClass.FILE_SIZE_NONE
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isGifMimeType
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isImageMimeType
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isVideoMimeType
import com.messenger.phone.number.text.sms.service.apps.CommanClass.launchViewIntent
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.ItemAttachmentDocumentPreviewBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.ItemAttachmentMediaPreviewBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.ItemAttachmentVcardPreviewBinding
import com.messenger.phone.number.text.sms.service.apps.helper.ImageCompressor
import com.messenger.phone.number.text.sms.service.apps.helper.setupDocumentPreview
import com.messenger.phone.number.text.sms.service.apps.helper.setupVCardPreview
import com.messenger.phone.number.text.sms.service.apps.modelClass.AttachmentSelection
import com.simplemobiletools.commons.extensions.applyColorFilter
import com.simplemobiletools.commons.extensions.beGone
import com.simplemobiletools.commons.extensions.beVisible
import com.simplemobiletools.commons.extensions.beVisibleIf
import com.simplemobiletools.commons.extensions.darkenColor
import com.simplemobiletools.commons.extensions.getProperPrimaryColor
import com.simplemobiletools.commons.extensions.onGlobalLayout
import com.simplemobiletools.commons.extensions.toast
//also make all changes in realtime projects not write code only txt got it
class AttachmentsAdapter(
    val activity: Activity,
    val recyclerView: RecyclerView,
    val onAttachmentsRemoved: () -> Unit,
    val onReady: (() -> Unit),
    val onremove: (() -> Unit),
) : ListAdapter<AttachmentSelection, AttachmentsAdapter.AttachmentsViewHolder>(
    AttachmentDiffCallback()
) {

    private val config = activity.config
    private val resources = activity.resources
    private val primaryColor = activity.getProperPrimaryColor()
    private val imageCompressor by lazy { ImageCompressor(activity) }

    val attachments = mutableListOf<AttachmentSelection>()

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (viewType) {
            ATTACHMENT_DOCUMENT -> ItemAttachmentDocumentPreviewBinding.inflate(
                inflater,
                parent,
                false
            )

            ATTACHMENT_VCARD -> ItemAttachmentVcardPreviewBinding.inflate(inflater, parent, false)
            ATTACHMENT_MEDIA -> ItemAttachmentMediaPreviewBinding.inflate(inflater, parent, false)
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }

        return AttachmentsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttachmentsViewHolder, position: Int) {
        val attachment = getItem(position)
        holder.bindView { binding, _ ->
            when (attachment.viewType) {
                ATTACHMENT_DOCUMENT -> {
                    (binding as ItemAttachmentDocumentPreviewBinding).setupDocumentPreview(
                        uri = attachment.uri,
                        title = attachment.filename,
                        mimeType = attachment.mimetype,
                        onClick = {
                            activity.launchViewIntent(
                                attachment.uri,
                                attachment.mimetype,
                                attachment.filename
                            )
                        },
                        onRemoveButtonClicked = { removeAttachment(attachment) }
                    )
                }

                ATTACHMENT_VCARD -> {
                    (binding as ItemAttachmentVcardPreviewBinding).setupVCardPreview(
                        activity = activity,
                        uri = attachment.uri,
                        onClick = {
//                            val intent = Intent(activity, VCardViewerActivity::class.java).also {
//                                it.putExtra(EXTRA_VCARD_URI, attachment.uri)
//                            }
//                            activity.startActivity(intent)
                        },
                        onRemoveButtonClicked = { removeAttachment(attachment) }
                    )
                }

                ATTACHMENT_MEDIA -> setupMediaPreview(
                    binding = binding as ItemAttachmentMediaPreviewBinding,
                    attachment = attachment
                )
            }
        }
    }

    fun clear() {
        attachments.clear()
        submitList(emptyList())
        recyclerView.onGlobalLayout {
            onAttachmentsRemoved()
        }
    }

    fun addAttachment(attachment: AttachmentSelection) {
        attachments.removeAll { AttachmentSelection.areItemsTheSame(it, attachment) }
        attachments.add(attachment)
        submitList(attachments.toList())
    }

    private fun removeAttachment(attachment: AttachmentSelection) {
        attachments.removeAll { AttachmentSelection.areItemsTheSame(it, attachment) }
        if (attachments.isEmpty()) {
            clear()
            onremove.invoke()
        } else {
            submitList(attachments.toList())
            onremove.invoke()
        }
    }

    private fun setupMediaPreview(
        binding: ItemAttachmentMediaPreviewBinding,
        attachment: AttachmentSelection,
    ) {
        binding.apply {
            mediaAttachmentHolder.background.applyColorFilter(primaryColor.darkenColor())
            mediaAttachmentHolder.setOnClickListener {
                activity.launchViewIntent(attachment.uri, attachment.mimetype, attachment.filename)
            }

            removeAttachmentButtonHolder.removeAttachmentButton.apply {
                beVisible()
//                background.applyColorFilter(primaryColor)
                setOnClickListener {
                    removeAttachment(attachment)
                }
            }

            val compressImage =
                attachment.mimetype.isImageMimeType() && !attachment.mimetype.isGifMimeType()
            if (compressImage && attachment.isPending && config.mmsFileSizeLimit != FILE_SIZE_NONE) {
                thumbnail.beGone()
                compressionProgress.beVisible()

                imageCompressor.compressImage(
                    attachment.uri,
                    config.mmsFileSizeLimit
                ) { compressedUri ->
                    activity.runOnUiThread {
                        when (compressedUri) {
                            attachment.uri -> {
                                attachments.find { it.uri == attachment.uri }?.isPending = false
                                loadMediaPreview(this, attachment)
                            }

                            null -> {
//                                activity.toast(R.string.error)
                                removeAttachment(attachment)
                            }

                            else -> {
                                attachments.remove(attachment)
                                addAttachment(
                                    attachment.copy(
                                        uri = compressedUri,
                                        isPending = false
                                    )
                                )
                            }
                        }
                        onReady()
                    }
                }
            } else {
                loadMediaPreview(this, attachment)
            }
        }
    }

    private fun loadMediaPreview(
        binding: ItemAttachmentMediaPreviewBinding,
        attachment: AttachmentSelection,
    ) {
        val roundedCornersRadius =
            resources.getDimension(com.simplemobiletools.commons.R.dimen.activity_margin).toInt()
        val size = resources.getDimension(R.dimen.attachment_preview_size).toInt()

        val options = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .transform(CenterCrop(), RoundedCorners(roundedCornersRadius))

        Glide.with(binding.thumbnail)
            .load(attachment.uri)
            .transition(DrawableTransitionOptions.withCrossFade())
            .override(size, size)
            .apply(options)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean,
                ): Boolean {
                    removeAttachment(attachment)
                    activity.toast(com.simplemobiletools.commons.R.string.unknown_error_occurred)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean,
                ): Boolean {
                    binding.thumbnail.beVisible()
                    binding.playIcon.beVisibleIf(attachment.mimetype.isVideoMimeType())
                    binding.compressionProgress.beGone()
                    return false
                }
            })
            .into(binding.thumbnail)
    }

    inner class AttachmentsViewHolder(val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(callback: (binding: ViewBinding, adapterPosition: Int) -> Unit) {
            callback(binding, adapterPosition)
        }
    }
}

private class AttachmentDiffCallback : DiffUtil.ItemCallback<AttachmentSelection>() {
    override fun areItemsTheSame(
        oldItem: AttachmentSelection,
        newItem: AttachmentSelection,
    ): Boolean {
        return AttachmentSelection.areItemsTheSame(oldItem, newItem)
    }

    override fun areContentsTheSame(
        oldItem: AttachmentSelection,
        newItem: AttachmentSelection,
    ): Boolean {
        return AttachmentSelection.areContentsTheSame(oldItem, newItem)
    }
}