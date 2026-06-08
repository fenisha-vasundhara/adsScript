package com.messenger.phone.number.text.sms.service.apps.CustomGallery.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.albumes
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.ViewAllImageActivity
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.adapter.AlbumSelectionAdapter
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.getAllImages
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.loadData
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.sortImagesByFolder
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentGalleryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@AndroidEntryPoint
class GalleryFragment : Fragment() {

    lateinit var binding: FragmentGalleryBinding
    val albumSelectionAdapter: AlbumSelectionAdapter by lazy {
        AlbumSelectionAdapter(Glide.with(requireContext()).asBitmap())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gallery, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            binding.directoriesGrid.adapter = albumSelectionAdapter
            albumes?.let {
                albumSelectionAdapter.setItems(it)
                binding.loader.gone()
            }

            albumSelectionAdapter.folderclick = {
                if (it != "") {
                    startActivity(Intent(requireContext(), ViewAllImageActivity::class.java).putExtra("folderpath", it))
                }
            }

        }
    }
}