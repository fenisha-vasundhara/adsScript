package com.messenger.phone.number.text.sms.service.apps.CustomGallery

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.google.common.util.concurrent.ListenableFuture
import com.messenger.phone.number.text.sms.service.apps.CommanClass.PICK_VIDEO_AND_IMAGE_INTENT
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass.MediaItem
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.SendMessageActivity
import com.messenger.phone.number.text.sms.service.apps.camera.CameraActivity
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivitySendMessageBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.FisrtContenarPickerItemBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.ViewAllImageItemDateBinding
import com.simplemobiletools.commons.extensions.getMyFileUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ViewAllImageAndVideoAdapter(
    private val glide: RequestBuilder<Bitmap>,
    var sendMessageActivity: SendMessageActivity,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
        ProcessCameraProvider.getInstance(sendMessageActivity)

    lateinit var imageCapture: ImageCapture

    private var isUsingFrontCamera = false // Flag to track camera state

    private val CAMERAVIEW: Int = 1
    private val IMAGEVIEW: Int = 2

    var imageclick: ((String) -> Unit)? = null

    var cameraclick: ((File) -> Unit)? = null

    var folderclick: (() -> Unit)? = null

    var fullcameraclick: (() -> Unit)? = null

    inner class ViewAllImageAdapterWithDateViewHolder(var binding: ViewAllImageItemDateBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class FisrtContenarViewHolder(var binding: FisrtContenarPickerItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CAMERAVIEW -> {
                FisrtContenarViewHolder(
                    FisrtContenarPickerItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            IMAGEVIEW -> {
                ViewAllImageAdapterWithDateViewHolder(
                    ViewAllImageItemDateBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                ViewAllImageAdapterWithDateViewHolder(
                    ViewAllImageItemDateBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val safePosition = holder.bindingAdapterPosition
            .takeIf { it != RecyclerView.NO_POSITION }
            ?: position
        if (holder is FisrtContenarViewHolder) {
            if (imagelist.getOrNull(safePosition) == null) return
            with(holder.binding) {
                setupcamera(this)
            }
        } else if (holder is ViewAllImageAdapterWithDateViewHolder) {
            val data = imagelist.getOrNull(safePosition) ?: return
            with(holder.binding) {
                glide.load(data.path).centerCrop().into(albumImage)
                albumImage.setOnClickListener {
                    imageclick?.invoke(data.path)
                }
                if (data.isPhoto) {
                    threadMessagePlayOutline.gone()
                } else {
                    threadMessagePlayOutline.visible()
                }
            }
        }
    }

    private fun setupcamera(binding: FisrtContenarPickerItemBinding) {


        binding.fisrtcontenar.setBackgroundColor(
            binding.fisrtcontenar.context.resources.getColor(
                if (binding.fisrtcontenar.context.config.activeThemeSelection == 1) {
                    R.color.white
                } else {
                    R.color.black2
                }
            )
        )


        if (imageuriGalleryFirstimage != null) {
            try {
                binding.imageView43.setImageURI(imageuriGalleryFirstimage)
            } catch (e: Exception) {
                try {
                    Glide.with(binding.imageView43.context).load(imageuriGalleryFirstimage)
                        .into(binding.imageView43)
                } catch (e: Exception) {

                }
            }
        } else {
            binding.imageView43.setImageResource(R.drawable.select_image_gallary)
        }
        binding.imageView43.setOnClickListener {
            if (imageuriGalleryFirstimage != null) {
                imageclick?.invoke(imageuriGalleryFirstimage.toString())
            } else {
                sendMessageActivity.toastMess("Image Not Found")
            }
        }

        cameraProviderFuture.addListener(Runnable {
            imageCapture = ImageCapture.Builder().build()
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            bindCamera(cameraProvider, binding, imageCapture)
        }, ContextCompat.getMainExecutor(sendMessageActivity))

        binding.rotedcamera.setOnClickListener {
            isUsingFrontCamera = !isUsingFrontCamera
            val cameraProviderFuture = ProcessCameraProvider.getInstance(sendMessageActivity)
            cameraProviderFuture.addListener(Runnable {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                bindCamera(cameraProvider, binding, imageCapture)
            }, ContextCompat.getMainExecutor(sendMessageActivity))
        }

        binding.takephoto.setOnClickListener {
            val imageCapture = imageCapture ?: return@setOnClickListener
            val imageFile = File.createTempFile("attachment_", ".jpg", getAttachmentsDir())
            val outputOptions = ImageCapture.OutputFileOptions.Builder(imageFile).build()
            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(sendMessageActivity),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        CoroutineScope(Dispatchers.Main).launch {
                            sendMessageActivity.toastMess("Photo capture failed: ${exc.message}")
                        }
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        cameraclick?.invoke(imageFile)
                    }
                })
        }

        binding.folder.setOnClickListener {
            folderclick?.invoke()
        }

        binding.fullscreen.setOnClickListener {
            fullcameraclick?.invoke()
        }
    }

    private fun getAttachmentsDir(): File {
        return File(sendMessageActivity.cacheDir, "attachments").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    private fun bindCamera(
        cameraProvider: ProcessCameraProvider,
        binding: FisrtContenarPickerItemBinding,
        imageCapture: ImageCapture?,
    ) {
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

        val cameraSelector = if (isUsingFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                sendMessageActivity,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (exc: Exception) {
        }
    }

    override fun getItemCount(): Int {
        return imagelist.size
    }

    override fun getItemViewType(position: Int): Int {
        if (position < 0 || position >= imagelist.size) return IMAGEVIEW
        return if (position == 0) {
            CAMERAVIEW
        } else {
            IMAGEVIEW
        }
    }

    override fun getItemId(position: Int): Long {
        val item = imagelist.getOrNull(position) ?: return RecyclerView.NO_ID
        return item.path.hashCode().toLong()
    }


    var imagelist = ArrayList<MediaItem>()
        set(value) {
            field = ArrayList(value)
            if (Looper.myLooper() == Looper.getMainLooper()) {
                notifyDataSetChanged()
            } else {
                sendMessageActivity.runOnUiThread {
                    notifyDataSetChanged()
                }
            }
        }

    init {
        setHasStableIds(true)
    }

}
