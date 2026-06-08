package com.demo.adsmanage.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.demo.adsmanage.Commen.Constants.mPrivacyPolicyURL
import com.messenger.phone.number.text.sms.service.apps.R
import com.demo.adsmanage.basemodule.BaseActivity
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityPrivacyPolicyBinding


class PrivacyActivity : BaseActivity() {

    private var mPrivacyPolicyActivity: PrivacyActivity?=null
    override fun getActivityContext(): AppCompatActivity {
        return this@PrivacyActivity
    }
    val binding:ActivityPrivacyPolicyBinding by lazy {
        ActivityPrivacyPolicyBinding.inflate(layoutInflater)
    }
    @SuppressLint("SetJavaScriptEnabled", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        mPrivacyPolicyActivity = this@PrivacyActivity

        val url=mPrivacyPolicyURL
        val customIntent = CustomTabsIntent.Builder()

        // below line is setting toolbar color
        // for our custom chrome tab.

        // below line is setting toolbar color
        // for our custom chrome tab.
        customIntent.setToolbarColor(ContextCompat.getColor(this@PrivacyActivity,R.color.purple_200))

        // we are calling below method after
        // setting our toolbar color.

        // we are calling below method after
        // setting our toolbar color.
        openCustomTab(this@PrivacyActivity, customIntent.build(), Uri.parse(url))

//        if (!isOnline) {
//            binding.ctOffline.visibility=View.VISIBLE
//            binding.webView.visibility=View.GONE
//        }else{
//            binding.ctOffline.visibility=View.GONE
//            binding.webView.visibility=View.VISIBLE
//        }
//        binding.ctInternetDisable.setOnClickListener {
//
//            if (!isOnline) {
//                binding.ctOffline.visibility=View.VISIBLE
//                binding.webView.visibility=View.GONE
//                Toast.makeText(
//                    this,
//                    "Please check internet connection.",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }else{
//                startWebView()
//                binding.ctOffline.visibility=View.GONE
//                binding.webView.visibility=View.VISIBLE
//            }
//        }
//        startWebView()
    }
    @SuppressLint("SetJavaScriptEnabled")
    private fun startWebView() {
        val url=mPrivacyPolicyURL
        val settings: WebSettings = binding.webView.settings
        settings.javaScriptEnabled = true
        binding.pdMdialog.visibility=View.VISIBLE
        binding.webView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                if (binding.pdMdialog.isVisible) {
                    binding.pdMdialog.visibility=View.GONE
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
            }
        }
        binding.webView.loadUrl(url!!)
    }

    fun openCustomTab(activity: Activity, customTabsIntent: CustomTabsIntent, uri: Uri?) {
        // package name is the default package
        // for our custom chrome tab
        val packageName = "com.android.chrome"

        // we are checking if the package name is not null
        // if package name is not null then we are calling
        // that custom chrome tab with intent by passing its
        // package name.
        customTabsIntent.intent.setPackage(packageName)

        // in that custom tab intent we are passing
        // our url which we have to browse.
        customTabsIntent.launchUrl(activity, uri!!)
        finish()
    }


}
