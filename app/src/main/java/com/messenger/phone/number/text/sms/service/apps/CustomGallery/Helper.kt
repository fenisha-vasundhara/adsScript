package com.messenger.phone.number.text.sms.service.apps.CustomGallery

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.messenger.phone.number.text.sms.service.apps.CommanClass.albumes
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass.MediaItem
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.random.Random

var combinedListAttachment = ArrayList<MediaItem>()
var imageuriGalleryFirstimage: Uri? = null
private const val MAX_MEDIA_ITEMS = 1000
private const val GALLERY_HELPER_TAG = "GalleryHelper"

suspend fun Context.loadData() {
    val folders: HashMap<File, List<File>> =
        sortImagesByFolder(getAllImages(this)) as HashMap<File, List<File>>
    albumes = folders
}

private val READ_IMAGES_PERMISSION =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        Manifest.permission.READ_MEDIA_IMAGES
    else
        Manifest.permission.READ_EXTERNAL_STORAGE

suspend fun Context.getRecentImage(): Uri? = withContext(Dispatchers.IO) {

    if (ContextCompat.checkSelfPermission(
            this@getRecentImage,
            READ_IMAGES_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        val uri = getImagesContentUri()
        val recentProjection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_MODIFIED
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        queryMediaStore(this@getRecentImage, uri, recentProjection, null, null, sortOrder)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val id = cursor.getLong(idColumnIndex)
                return@withContext ContentUris.withAppendedId(uri, id)
            }
        }
        return@withContext null
    } else {
        return@withContext null
    }
}

suspend fun Context.generateRandomColorString(): String = withContext(Dispatchers.IO) {
    val seed = System.currentTimeMillis() // Use current time as seed
    val random = Random(seed)
    val red = random.nextInt(256)
    val green = random.nextInt(256)
    val blue = random.nextInt(256)
    return@withContext String.format("#%02X%02X%02X", red, green, blue)
}

var colorcodelist = listOf<String>(
    "#BCE4E5",
    "#E6E0AF",
    "#FDEEA6",
    "#FDD294",
    "#D6D0F1",
    "#CECECE",
    "#D1DABD",
    "#CBDAED",
    "#69D2D5",
    "#66BDCF",
    "#7CCAA3",
    "#AFD8C6",
    "#FC9A9B",
    "#FB686A",
    "#F9486A",
    "#912241",
    "#DB6E4E",
    "#644D52",
    "#527E7E",
    "#3591BC",
    "#36568B",
    "#55626F",
    "#1D2326",
    "#301E35",
    "#E5E9EA",
    "#FFFD9D",
    "#E7E8D1"
)

suspend fun sortImagesByFolder(files: List<File>): Map<File, List<File>> {
    delay(10)
    val resultMap = mutableMapOf<File, MutableList<File>>()
    for (file in files) {
        val parentFile = file.parentFile ?: continue
        resultMap.getOrPut(parentFile) { mutableListOf() }.add(file)
    }
    return resultMap
}

suspend fun getAllImages(context: Context): List<File> {
    delay(10)
    val sortOrder = MediaStore.Images.Media.DATE_TAKEN + " ASC"
    val imageList =
        queryUri(context, getImagesContentUri(), null, null, sortOrder)
            .use { it?.getResultsFromCursor() ?: listOf() }
    return imageList
}

private fun queryUri(
    context: Context,
    uri: Uri,
    selection: String?,
    selectionArgs: Array<String>?,
    sortOrder: String = "",
): Cursor? {
    return queryMediaStore(
        context,
        uri,
        projection,
        selection,
        selectionArgs,
        sortOrder
    )
}

private fun queryMediaStore(
    context: Context,
    uri: Uri,
    projection: Array<String>?,
    selection: String?,
    selectionArgs: Array<String>?,
    sortOrder: String,
): Cursor? {
    return try {
        context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
    } catch (exception: IllegalArgumentException) {
        val fallbackUri = getExternalVolumeFallbackUri(uri)
        if (fallbackUri == null) {
            Log.w(GALLERY_HELPER_TAG, "MediaStore query failed for uri=$uri", exception)
            null
        } else {
            try {
                context.contentResolver.query(
                    fallbackUri,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
                )
            } catch (fallbackException: Exception) {
                Log.w(
                    GALLERY_HELPER_TAG,
                    "MediaStore query failed for uri=$uri and fallbackUri=$fallbackUri",
                    fallbackException
                )
                null
            }
        }
    } catch (exception: Exception) {
        Log.w(GALLERY_HELPER_TAG, "MediaStore query failed for uri=$uri", exception)
        null
    }
}

private fun getExternalVolumeFallbackUri(uri: Uri): Uri? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        return null
    }

    val externalPrimaryPath = "/${MediaStore.VOLUME_EXTERNAL_PRIMARY}/"
    val externalPath = "/${MediaStore.VOLUME_EXTERNAL}/"
    val uriValue = uri.toString()

    return if (uriValue.contains(externalPrimaryPath)) {
        Uri.parse(uriValue.replace(externalPrimaryPath, externalPath))
    } else {
        null
    }
}

private fun getImagesContentUri(): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }
}

private fun getVideosContentUri(): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }
}

val projection = arrayOf(
    MediaStore.Files.FileColumns._ID,
    MediaStore.Files.FileColumns.DATA,
    MediaStore.Files.FileColumns.DATE_ADDED,
    MediaStore.Files.FileColumns.MIME_TYPE,
    MediaStore.Files.FileColumns.TITLE
)

val imageExtensions = arrayOf("jpg", "jpeg", "png", "gif", "bmp", "webp")

private fun Cursor.getResultsFromCursor(limit: Int? = null): List<File> {
    val results = ArrayList<File>()
    var count = 0
    val pathColumnIndex = this.getColumnIndex(MediaStore.MediaColumns.DATA)

    if (pathColumnIndex == -1) {
        return results
    }

    while (this.moveToNext()) {
        val path = this.getString(pathColumnIndex)
        if (!path.isNullOrBlank()) {
            results.add(File(path))
        }
        count++
        if (limit != null && count >= limit) break
    }
    return results
}

suspend fun getImageVideoNumber(parent: File): Int {
    var imageCount = 0
    for (file in parent.listFiles()!!) {
        if (file.isFile) {
            val fileExtension = file.extension.lowercase()
            if (imageExtensions.contains(fileExtension)) {
                imageCount++
            }
        }
    }
    return imageCount
}

suspend fun getImagesFromAlbum(folder: String): List<Photo> {
    return File(folder)
        .listFiles { file -> file.isFile && imageExtensions.contains(file.extension.lowercase()) }
        ?.sortedWith(compareByDescending { it.lastModified() })
        ?.map { file ->
            Photo(
                path = file.absolutePath,
                position = 0,
                selected = false,
                lastModifieddate = file.lastModified()
            )
        }
        ?: emptyList()
}

fun Context.loadImage(
    path: String,
    albumImage: ImageView,
) {
    loadImageBase(path, albumImage)
}

const val ROUNDED_CORNERS_NONE = 1

@SuppressLint("CheckResult")
fun Context.loadImageBase(
    path: String,
    albumImage: ImageView,
    skipMemoryCacheAtPaths: ArrayList<String>? = null,
    crossFadeDuration: Int = 300,
) {
    val options = RequestOptions()
        .skipMemoryCache(skipMemoryCacheAtPaths?.contains(path) == true)
        .priority(Priority.LOW)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .format(DecodeFormat.PREFER_ARGB_8888)

    options.optionalTransform(CenterCrop())

    options.dontAnimate()
    options.decode(Bitmap::class.java)

    val builder = Glide.with(applicationContext)
        .load(path)
        .apply(options)
        .transition(DrawableTransitionOptions.withCrossFade(crossFadeDuration))

    builder.into(albumImage)
}

suspend fun Context.getAllImagesAndVideosSortedByRecent(): List<Photo> {
    try {
        delay(1)
        val sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC"
        val externalStorageState = Environment.getExternalStorageState()
        if (externalStorageState != Environment.MEDIA_MOUNTED) {
            return emptyList()
        }
        val imageList =
            queryUri(this, getImagesContentUri(), null, null, sortOrder)
                .use { it?.getResultsFromCursor() ?: listOf() }
        val resultList = (imageList).sortedWith(compareByDescending { it.lastModified() })
        return resultList.map { file ->
            Photo(
                id = null,
                path = file.absolutePath,
                position = 0,
                selected = false,
                lastModifieddate = file.lastModified()
            )
        }
    } catch (E: Exception) {
        return emptyList()
    }
}

suspend fun Context.getAllImagesAndVideosSortedByRecentNew(): List<MediaItem> {
    delay(1)
    val combinedList = ArrayList<MediaItem>()
    val demoItem = MediaItem(
        id = 0L,
        path = "/path/to/demo/image.jpg",
        position = 0,
        selected = false,
        lastModifiedDate = System.currentTimeMillis(),
        isPhoto = true
    )
    combinedList.add(demoItem)
    val sortOrder = MediaStore.MediaColumns.DATE_TAKEN + " DESC"
    val imageList =
        queryUri(this, getImagesContentUri(), null, null, sortOrder)
            .use { it?.getResultsFromCursor(MAX_MEDIA_ITEMS) ?: listOf() }
    val videoList =
        queryUri(this, getVideosContentUri(), null, null, sortOrder)
            .use { it?.getResultsFromCursor(MAX_MEDIA_ITEMS) ?: listOf() }


    Log.d("", "getAllImagesAndVideosSortedByRecentNew: imageList <-----> ${imageList.size}")
    Log.d("", "getAllImagesAndVideosSortedByRecentNew: videoList <-----> ${videoList.size}")

    imageList.forEachIndexed { index, file ->
        combinedList.add(
            MediaItem(
                id = null,
                path = file.absolutePath,
                position = 0,
                selected = false,
                lastModifiedDate = file.lastModified(),
                isPhoto = true
            )
        )
    }

    videoList.forEachIndexed { index, file ->
        combinedList.add(
            MediaItem(
                id = null,
                path = file.absolutePath,
                position = 0,
                selected = false,
                lastModifiedDate = file.lastModified(),
                isPhoto = false
            )
        )
    }
    val resultList = combinedList.sortedWith(compareByDescending { it.lastModifiedDate })
    return if (resultList.size > MAX_MEDIA_ITEMS) {
        resultList.take(MAX_MEDIA_ITEMS)
    } else {
        resultList
    }
}
