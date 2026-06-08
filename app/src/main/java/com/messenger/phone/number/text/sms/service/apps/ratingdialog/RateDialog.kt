package com.messenger.phone.number.text.sms.service.apps.ratingdialog


import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import com.messenger.phone.number.text.sms.service.apps.Dialog.BaseDialogFragment
import com.demo.adsmanage.Commen.Constants.isAdsClicking
import com.demo.adsmanage.helper.click
import com.messenger.phone.number.text.sms.service.apps.CommanClass.GMAIL_ID
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.DialogRateAppNewBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.FeedBackDialogLayoutBinding

class RateDialog() : BaseDialogFragment() {
    private lateinit var runnable: Runnable
    private var bottomSheetDialog: Dialog? = null
    private var rating1 = 0f

    lateinit var binding: DialogRateAppNewBinding
    private lateinit var stars: List<ImageView>

    private var packageName: String? = null

    companion object {
        fun newInstance(packageName: String): RateDialog {
            val fragment = RateDialog()
            val args = Bundle()
            args.putString("PACKAGE_NAME", packageName)
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        packageName = arguments?.getString("PACKAGE_NAME")
        setStyle(STYLE_NO_TITLE, R.style.materialButton)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        if (!this::binding.isInitialized) {
            binding = DialogRateAppNewBinding.inflate(layoutInflater)
        }
        return binding.root
    }

    val handler = Handler()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stars = listOf(
            binding.star1,
            binding.star2,
            binding.star3,
            binding.star4,
            binding.star5
        )

        stars.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                updateStars(index + 1)
                rating1 = (index + 1).toFloat()
            }
        }

        binding.btnNextTime.setOnClickListener { v: View? ->
            if (isAdded){
                if (rating1 > 3) {
                    requireActivity().config.isShowRatingDialogNew = true
                    rateApp()
                } else {
                    showFeedbackDialog()
                }
            }
        }
        binding.ivClose.setOnClickListener { v: View? ->

            dismiss()
        }

        runnable = Runnable {
            if (isAdded){
                if (rating1 > 3) {
                    binding.btnNextTime.alpha = 1f
                    binding.btnNextTime.isEnabled = true
                    binding.btnNextTime.text = resources.getString(R.string.rate_now)
                } else {
                    binding.btnNextTime.alpha = 1f
                    binding.btnNextTime.isEnabled = true
                    binding.btnNextTime.text = resources.getString(R.string.give_feedback)
                }
            }
        }

    }

    private fun updateStars(selectedRating: Int) {
        for (i in stars.indices) {
            if (i < selectedRating) {
                stars[i].setImageResource(R.drawable.ic_select_star1)
            } else {
                stars[i].setImageResource(R.drawable.ic_unselect_star)
            }
        }
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, 200)
    }


    private fun rateApp() {
        isAdsClicking = true
        if (activity == null) {
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

    private fun showFeedbackDialog() {
        if (!isAdded) return
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val binding = FeedBackDialogLayoutBinding.inflate(layoutInflater)
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding.cancel.click {
            dialog.dismiss()
        }

        var feedback = ""
        binding.checkBox1.text = resources.getString(R.string.check1)
        binding.checkBox2.text = resources.getString(R.string.check2)
        binding.checkBox3.text = resources.getString(R.string.check3)
        binding.checkBox4.text = resources.getString(R.string.check4)

        binding.checkBox1.setOnCheckedChangeListener { _, b ->
            if (b) feedback += "${resources.getString(R.string.check1)} \n"
            else feedback = feedback.replace("${resources.getString(R.string.check1)} \n", "")
        }

        binding.checkBox2.setOnCheckedChangeListener { _, b ->
            if (b) feedback += "${resources.getString(R.string.check2)} \n"
            else feedback = feedback.replace("${resources.getString(R.string.check2)} \n", "")
        }

        binding.checkBox3.setOnCheckedChangeListener { _, b ->
            if (b) feedback += "${resources.getString(R.string.check3)} \n"
            else feedback = feedback.replace("${resources.getString(R.string.check3)} \n", "")
        }

        binding.checkBox4.setOnCheckedChangeListener { _, b ->
            if (b) feedback += "${resources.getString(R.string.check4)} \n"
            else feedback = feedback.replace("${resources.getString(R.string.check4)}, \n", "")
        }

        binding.submit.click {
            dialog.dismiss()
            dismiss()
            sendGmail(
                resources.getString(R.string.app_name),
                "$feedback \n ${binding.editText2.text}",
                listOf(GMAIL_ID)
            )
        }
        dialog.show()
    }

    private fun sendGmail(
        subject: String,
        body: String,
        recipientEmail: List<String> = emptyList(),
    ) {
        isAdsClicking = true
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                data = Uri.parse("mailto:$GMAIL_ID")
                if (recipientEmail.isNotEmpty()) putExtra(
                    Intent.EXTRA_EMAIL, recipientEmail.toTypedArray()
                )
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                setPackage("com.google.android.gm")
            }
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val fallbackIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                    "mailto:$GMAIL_ID?subject=${Uri.encode(subject)}&body=${
                        Uri.encode(body)
                    }"
                )
            }
            startActivity(fallbackIntent)
        } catch (e: Exception) {

        }
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
