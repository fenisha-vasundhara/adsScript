package com.messenger.phone.number.text.sms.service.apps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityDemoBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

@AndroidEntryPoint
class DemoActivity : AppCompatActivity() {

    lateinit var binding: ActivityDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_demo)

        with(binding) {

            webView.settings.javaScriptEnabled = true
            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?,
                ): Boolean {
                    return super.shouldOverrideUrlLoading(view, request)
                }
            }

            webView.setDownloadListener { url, _, _, _, _ ->
                if (url.startsWith("blob:")) {
                    Log.d("WebView", "Blob URL detected: $url")

                    // Show Toast message indicating download started
                    Toast.makeText(this@DemoActivity, "Download started...", Toast.LENGTH_SHORT)
                        .show()

                    // Call JavaScript to fetch the blob data and initiate the download
                    try {
                        webView.evaluateJavascript(
                            """
                        (function() {
                            var xhr = new XMLHttpRequest();
                            xhr.open("GET", "$url", true);
                            xhr.responseType = "blob";
                            xhr.onload = function() {
                                try {
                                    var blob = xhr.response;
                                    var link = document.createElement("a");
                                    link.href = URL.createObjectURL(blob);
                                    link.download = "downloaded_pdf.pdf"; // Customize the filename
                                    link.click(); // Trigger the download
                                } catch (error) {
                                    console.error("Error during download:", error);
                                    Android.showToast("Error during download: " + error.message);
                                }
                            };
                            xhr.onerror = function(error) {
                                console.error("XHR error: ", error);
                                Android.showToast("Error fetching the Blob.");
                            };
                            xhr.send();
                        })();
                    """, null
                        )

                    } catch (e: Exception) {
                        Log.e("WebView", "Error during download: ${e.message}")
                        // Show error Toast if there's an issue with the JavaScript execution
                        Toast.makeText(
                            this@DemoActivity,
                            "Error during download: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // Handle non-blob URLs if necessary
                    Toast.makeText(this@DemoActivity, "Downloading: $url", Toast.LENGTH_SHORT)
                        .show()
                }

                // Load the website

            }
            webView.loadUrl("http://64.227.165.136:4141/3cd8246b-189b-4a73-b248-b5c51e0935ad")
        }
    }
}