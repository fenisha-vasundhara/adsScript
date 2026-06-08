package com.messenger.phone.number.text.sms.service.apps.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.transition.Transition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.IntroSliderItemBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.IntroModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class IntroSliderAdapter @Inject constructor() : PagerAdapter() {
    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ConstraintLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = IntroSliderItemBinding.inflate(LayoutInflater.from(container.context), container, false)
        binding.dataSlider = list[position]
//        binding.introImg.setImageResource(list[position].img)
//        CoroutineScope(Dispatchers.IO).launch {
//
//        }
        Glide.with(binding.introImg.context)
            .asBitmap()
            .load(list[position].img)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                    binding.introImg.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)

    }


    var list = ArrayList<IntroModel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
}