package com.demo.adsmanage.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.demo.adsmanage.Commen.Constants.isAdsClicking
import com.messenger.phone.number.text.sms.service.apps.R
import com.demo.adsmanage.basemodule.BaseActivity

class TermsActivity : BaseActivity() {

    private var mTermsConditionActivity: TermsActivity? = null
    override fun getActivityContext(): AppCompatActivity {
        return this@TermsActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)

        mTermsConditionActivity = this@TermsActivity

        findViewById<View>(R.id.icBack).setOnClickListener { finish() }

        findViewById<View>(R.id.btnShare).setOnClickListener { onclickShare() }
    }

    private fun onclickShare() {
        isAdsClicking=true
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "BG CutOut")
        var shareMessage = "\nGo with BackGround CutOut and Make Someone's day very special....\n\n"
        shareMessage += "https://play.google.com/store/apps/details?id=${packageName}"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, resources.getString(R.string.choose_one)))
    }
}
