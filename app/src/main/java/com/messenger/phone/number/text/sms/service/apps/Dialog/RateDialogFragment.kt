package com.check.bank.balance.banking.tool.dialogs

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.Dialog.MaterialDialogFragment
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.data.Config
import com.willy.ratingbar.BaseRatingBar
import com.willy.ratingbar.ScaleRatingBar

class RateDialogFragment(private val packageName: String, private val onDismissDialog: (Boolean) -> Unit) : MaterialDialogFragment() {
    private var bottomSheetDialog: Dialog? = null
    private var rating1 = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.materialButton)
    }

    lateinit var config: Config

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_rate_app, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        config = requireActivity().config
        view.findViewById<View>(R.id.btnNextTime).setOnClickListener { v: View? ->
            Toast.makeText(activity, "Please give rating.!", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<View>(R.id.ivClose).setOnClickListener { v: View? ->
            onDismissDialog.invoke(false)
            dismiss()
        }
        val scaleRatingBar = view.findViewById<ScaleRatingBar>(R.id.ratingBar)
        val handler = Handler()
        val runnable = Runnable {
            if (rating1 > 3) {
//                config.isShowRatingDialog = true
                rateApp()
                onDismissDialog.invoke(true)
                dismiss()
            } else {
                if (activity != null) {
                    onDismissDialog.invoke(true)
                    dismiss()
                }
            }
        }
        scaleRatingBar.setOnRatingChangeListener { ratingBar: BaseRatingBar?, rating: Float, fromUser: Boolean ->
            Log.d("TAG", "onViewCreated: rate_app $fromUser")
            rating1 = rating
//            config.rateDialogPerameter = rating.toInt()
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, 200)
        }
    }


    private fun rateApp() {
        if (activity == null) {
            Log.d("TAG", "rate_app: return")
            return
        }
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (anfe: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bottomSheetDialog = dialog
        // ((View) getView().getParent()).setPadding(0,0,0,);
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        var width = displayMetrics.widthPixels
        width -= (width / 8.5f).toInt()
        bottomSheetDialog!!.window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onPause() {
        super.onPause()
        try {
            bottomSheetDialog!!.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
