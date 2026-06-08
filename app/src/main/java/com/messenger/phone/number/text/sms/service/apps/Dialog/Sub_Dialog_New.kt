package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.demo.adsmanage.Commen.Constants.testpackagerenlist
import com.demo.adsmanage.Commen.firebaseEventMain
import com.messenger.phone.number.text.sms.service.apps.CommanClass.firebaseEvent
import com.messenger.phone.number.text.sms.service.apps.CommanClass.sendsubactivity
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.SubDialogNewItemBinding
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PackageType
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

class Sub_Dialog_New : MaterialDialogFragment() {

    lateinit var binding: SubDialogNewItemBinding
    private var bottomSheetDialog: Dialog? = null

    var dialogdimisslisner: ((Boolean) -> Unit)? = null

    var sunopen = false

    companion object {
        fun newInstance(activityName: String): Sub_Dialog_New {
            val fragment = Sub_Dialog_New()
            val args = Bundle()
            args.putString("activity_name", activityName)
            fragment.arguments = args
            return fragment
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.sub_dialog_new_item, container, false)
        with(binding) {
            val activityName = arguments?.getString("activity_name", "")
            if (testpackagerenlist.isNotEmpty()) {
                val month =
                    testpackagerenlist.find { it.packageType == PackageType.MONTHLY }
                val week =
                    testpackagerenlist.find { it.packageType == PackageType.WEEKLY }

                val monthly =
                    week?.let { weekPackage ->
                        month?.let { monthPackage ->
                            getDiscount(
                                weekPackage,
                                monthPackage
                            )
                        }
                    }

                persentageTxt.text =
                    "${monthly?.second}%\n${requireActivity().resources.getText(R.string.off)}"
            }

            dialogClose.setOnClickListener {
                dismiss()
            }
            accessNowBtn.setOnClickListener {
                sunopen = true
                requireActivity().sendsubactivity()
                requireActivity().firebaseEvent("Premium_dialog", "Subscription_Activity")
                requireActivity().firebaseEventMain("${"Message_Subscription_Dialog_${activityName}"}")
                dismiss()
            }
        }
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (sunopen){
            dialogdimisslisner?.invoke(true)
        }else{
            dialogdimisslisner?.invoke(false)
        }
        sunopen = false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bottomSheetDialog = dialog
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        var width = displayMetrics.widthPixels
        width -= width / 9
        bottomSheetDialog!!.window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        bottomSheetDialog?.setCancelable(false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    fun getDiscount(weekPlan: Package, product: Package): Triple<Int, Int, String> {
        var price = weekPlan?.product?.price?.amountMicros?.div(1000000) ?: 120
        var pair = Triple(0, 0, "")
        product?.let {
            when (it.packageType) {
                PackageType.MONTHLY -> {
                    pair = getPair(4.3f, price, product, "P1M")

                }

                PackageType.SIX_MONTH -> {
                    pair = getPair(25.8f, price, product, "P6M")
                }

                PackageType.ANNUAL -> {
                    pair = getPair(52f, price, product, "P1Y")
                }

                else -> {

                }
            }
        }
        return pair
    }

    private fun getPair(
        time: Float,
        price: Long,
        product: Package,
        DUR: String,
    ): Triple<Int, Int, String> {
        var highPrice = (price.times(time.roundToInt()))// for round figure the duration
        var productPrice: Int = (product?.product?.price?.amountMicros?.div(1000000) ?: 120).toInt()
        var less = highPrice.minus(productPrice)
        var discount = if (highPrice != 0L) {
            (less?.times(100))?.div(highPrice) ?: 0
        } else {
            0
        }
        Log.d("TAG", "getPair235: $productPrice - $less   ")
        if (highPrice < productPrice) {
            highPrice = productPrice.toLong()
            discount = 0
        }
        var perDur = ""
        if (DUR == "P1M") {
            perDur = "${(productPrice / time.roundToInt()).formatPrice()}/Week"
        } else if (DUR == "P6M") {
            perDur = "${(productPrice / time.roundToInt()).formatPrice()}/Week"
        } else if (DUR == "P1Y") {
            perDur = "${(productPrice / time.roundToInt()).formatPrice()}/Week"
        }
        return Triple(highPrice.toInt(), discount.toInt(), perDur)
    }

    private fun Int.formatPrice(): String {
        val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
        return "₹${formatter.format(this)}"
    }
}
