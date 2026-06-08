package com.messenger.phone.number.text.sms.service.apps.CustomGallery.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.demo.adsmanage.helper.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.adapter.ViewAllImageWithDateAdapter
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.getAllImagesAndVideosSortedByRecent
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.getImagesFromAlbum
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass.Photo
import com.messenger.phone.number.text.sms.service.apps.CustomWallpaperActivity
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentRecentsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class RecentsFragment : Fragment() {

    lateinit var binding: FragmentRecentsBinding

    var allimagelist: ArrayList<Photo> = arrayListOf()


    val viewAllImageAdapter: ViewAllImageWithDateAdapter by lazy {
        ViewAllImageWithDateAdapter(Glide.with(this).asBitmap())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recents, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            binding.directoriesGrid.adapter = viewAllImageAdapter

            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    allimagelist = groupmake(ArrayList(requireContext().getAllImagesAndVideosSortedByRecent()))
                }
                withContext(Dispatchers.Main) {
                    val gridLayoutManager = GridLayoutManager(requireContext(), 3)
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
                startActivity(Intent(requireActivity(), CustomWallpaperActivity::class.java).putExtra("imagepath", it))
            }

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
}