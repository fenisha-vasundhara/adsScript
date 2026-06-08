package com.messenger.phone.number.text.sms.service.apps.CustomGallery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setWallpaperdone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.updateStatusbarColorFullApp
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.adapter.ViewAllImageAdapter
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.adapter.ViewAllImageWithDateAdapter
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass.AllImageModel
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass.Photo
import com.messenger.phone.number.text.sms.service.apps.CustomWallpaperActivity
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityViewAllImageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class ViewAllImageActivity : AppCompatActivity() {
    lateinit var binding: ActivityViewAllImageBinding
    var folderpath: String = ""
    var allimagelist: ArrayList<Photo> = arrayListOf()


    val viewAllImageAdapter: ViewAllImageWithDateAdapter by lazy {
        ViewAllImageWithDateAdapter(Glide.with(this).asBitmap())
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_all_image)
        folderpath = intent.getStringExtra("folderpath").toString()
        binding.directoriesGrid.adapter = viewAllImageAdapter

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.textView3.text = File(folderpath).name

        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                allimagelist = groupmake(ArrayList(getImagesFromAlbum(folderpath)))
            }
            withContext(Dispatchers.Main) {
                val gridLayoutManager = GridLayoutManager(this@ViewAllImageActivity, 3)
                gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (allimagelist[position].selected) {
                            3
                        } else {
                            1
                        }
                    }
                }
                binding.directoriesGrid.layoutManager = gridLayoutManager
                viewAllImageAdapter.imagelist = allimagelist
                binding.progressbarrecent.gone()
            }

        }


        viewAllImageAdapter.imageclick = {
            startActivity(Intent(this, CustomWallpaperActivity::class.java).putExtra("imagepath", it))
        }
    }

    suspend fun groupmake(arrayList: ArrayList<Photo>): ArrayList<Photo> {
        arrayList.sortByDescending { it.lastModifieddate }
        val groupedByDate = arrayList.groupBy {
            formatedate(it.lastModifieddate)!!
        }
        val newList = ArrayList<Photo>()
        groupedByDate.forEach { s, photos ->
            newList.add(Photo(selected = true, path = "", position = -1, lastModifieddate = photos.first().lastModifieddate))
            newList.addAll(photos)
        }

        return newList
    }

    suspend fun formatedate(inputLong: Long): String? {
        val instant = Instant.ofEpochMilli(inputLong)
        val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy")
        val formattedDate = localDate.format(formatter)
        return formattedDate
    }

    override fun onStart() {
        super.onStart()
        ThemeSetup()
        if (setWallpaperdone) {
            finish()
        }
    }
    private fun ThemeSetup() {
        updateStatusbarColorFullApp()
    }

}