package com.demo.adsmanage

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.demo.adsmanage.model.SunCommantModel
import java.util.Objects
import java.util.Timer
import java.util.TimerTask
import com.messenger.phone.number.text.sms.service.apps.R

class SliderViewPagerNewAdapter(
    val context: Context,
    val imageList: List<SunCommantModel>,
    private val viewPager: ViewPager? = null,
) : PagerAdapter() {
    override fun getCount(): Int {
        return imageList.size
    }

    private var currentPage = 0
    private var timer: Timer? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mLayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView: View = mLayoutInflater.inflate(R.layout.image_slider_item, container, false)

        val textViewSlider: TextView = itemView.findViewById(R.id.txt1)
        val txt2: TextView = itemView.findViewById(R.id.txt2)
        val txt3: TextView = itemView.findViewById(R.id.txt3)
        textViewSlider.isSelected = true
        textViewSlider.setText(imageList[position].txt2)
        txt2.setText(imageList[position].commanttxt)
        txt3.setText(imageList[position].commantuser)


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
                viewPager?.setCurrentItem(currentPage++, true)
            } else {
                viewPager?.setCurrentItem(currentPage--, true)
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
        private const val PERIOD_MS =
            3000 // Time in milliseconds between successive task executions
    }
}
