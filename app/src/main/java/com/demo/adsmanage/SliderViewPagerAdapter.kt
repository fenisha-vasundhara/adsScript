package com.demo.adsmanage

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.demo.adsmanage.model.SunCommantModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Objects
import java.util.Timer
import java.util.TimerTask
import com.messenger.phone.number.text.sms.service.apps.R

class SliderViewPagerAdapter(val context: Context, val imageList: List<Int>, private val viewPager: ViewPager) : PagerAdapter() {

    private var currentPage = 0
    private var timer: Timer? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun getCount(): Int {
        return imageList.size
    }

    var istextviewshow = false
    var textlist: ArrayList<String> = arrayListOf()

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    @SuppressLint("MissingInflatedId")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mLayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView: View = mLayoutInflater.inflate(R.layout.image_slider_item_auto, container, false)
        val textViewSlider: ImageView = itemView.findViewById(R.id.image_set)
        val textset: TextView = itemView.findViewById(R.id.textset)
        Log.d("textViewSlider", "instantiateItem: textViewSlider ${imageList[position]}")
        textViewSlider.setImageResource(imageList[position])
        if (istextviewshow) {
            textset.visibility = View.VISIBLE
            if (position == 0) {
                textset.setText(R.string.never_forget_new)
            } else if (position == 1) {
                textset.setText(R.string.Block_Numbre)
            } else if (position == 2) {
                textset.setText(R.string.Secure_Message_sec_new)
            } else if (position == 3) {
                textset.setText(R.string.traslate_message)
            } else if (position == 4) {
                textset.setText(R.string.swip_Theme_new)
            } else if (position == 5) {
                textset.setText(R.string.customization_Theme_new)
            }

        }


//        CoroutineScope(Dispatchers.IO).launch {
//            Glide.with(textViewSlider.context)
//                .asBitmap()
//                .load(imageList[position])
//                .into(object : CustomTarget<Bitmap>() {
//                    override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
//                        textViewSlider.setImageBitmap(resource)
//                    }
//
//                    override fun onLoadCleared(placeholder: Drawable?) {
//
//                    }
//                })
//        }

//        val txt2: TextView = itemView.findViewById(R.id.txt2)
//        val txt3: TextView = itemView.findViewById(R.id.txt3)
//        textViewSlider.isSelected = true
//        textViewSlider.setText(imageList[position].txt2)
//        txt2.setText(imageList[position].commanttxt)
//        txt3.setText(imageList[position].commantuser)


        Objects.requireNonNull(container).addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

    fun startAutoSlide() {
        var isForward = true // Flag to track the direction

        val update = Runnable {
            if (isForward) {
                viewPager.setCurrentItem(currentPage++, true)
            } else {
                viewPager.setCurrentItem(currentPage--, true)
            }

            // Check if we reached the end or beginning of the list
            if (currentPage >= imageList.size) {
                isForward = false // Change direction to backward
                currentPage = imageList.size - 2 // Set to second last item
            } else if (currentPage < 0) {
                isForward = true // Change direction to forward
                currentPage = 1 // Set to second item
            }
        }

        timer = Timer() // This will create a new Thread
        timer?.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, DELAY_MS.toLong(), PERIOD_MS.toLong())
    }

    fun stopAutoSlide() {
        timer?.cancel()
        timer = null
    }

    companion object {
        private const val DELAY_MS = 1000 // Delay in milliseconds before task is to be executed
        private const val PERIOD_MS = 3000 // Time in milliseconds between successive task executions
    }
}
