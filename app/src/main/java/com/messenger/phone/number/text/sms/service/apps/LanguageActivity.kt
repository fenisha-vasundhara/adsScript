package com.messenger.phone.number.text.sms.service.apps

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.demo.adsmanage.Commen.beGone
import com.demo.adsmanage.Commen.beVisible
import com.demo.adsmanage.Commen.firebaseEventMain
import com.demo.adsmanage.Commen.isValidforExperiment
import com.demo.adsmanage.Commen.log
import com.simplemobiletools.commons.extensions.toast
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.changelang
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getBottomNavigationBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperStatusBarColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isFLow1
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isFLow2
import com.messenger.phone.number.text.sms.service.apps.CommanClass.logOnboardingFunnelStep
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setCursorColorProgrammatically
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLang1
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.adapter.Languagesadapter
import com.messenger.phone.number.text.sms.service.apps.ads.v2.AdUiBinding
import com.messenger.phone.number.text.sms.service.apps.ads.v2.AdsOrchestrator
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityLanguageBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.Languagemodel
import com.messenger.phone.number.text.sms.service.apps.viewModel.LanguageViewModel
import com.simplemobiletools.commons.extensions.onTextChangeListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class LanguageActivity : AppCompatActivity() {


    lateinit var binding: ActivityLanguageBinding

    private var argumentValue: Boolean = false

    private var mainopen: Boolean = false
    private var needintervarA: Boolean = false
    private var flowfinish: Boolean = false
    private var secoundtimeopen: Boolean = false
    private var shouldReturnResultForFlow3: Boolean = false
    private var autoNavigationHandled: Boolean = false

    private var intenttwo: Intent? = null
    private lateinit var model: LanguageViewModel

    var firsttimeuserset = false

    companion object {
        var isappopenlock = false
        const val EXTRA_RETURN_RESULT_FOR_FLOW3 = "extra_return_result_for_flow3"
        private const val KEY_AUTO_NAVIGATION_HANDLED = "auto_navigation_handled"
    }

    var adsloadcount = 3
    private val adsOrchestrator by lazy { AdsOrchestrator.get(application) }
    private val languageAdHandler = Handler(Looper.getMainLooper())
    private val languageAdDelayRunnable = Runnable { maybeShowLanguageNative("delayed_render") }
    private var languageAdRequested = false

    @Inject
    lateinit var adapterP: Languagesadapter

    var lanlistMain: ArrayList<Languagemodel> = arrayListOf()

    private val RECORD_AUDIO_PERMISSION_REQUEST = 101

    private val SPEECH_REQUEST_CODE = 102

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_language)
        setBaseTheme(binding.vAnd15StatusBar)
        mainopen = intent.getBooleanExtra("mainopen", false)
        needintervarA = intent.getBooleanExtra("needintervarA", false)
        flowfinish = intent.getBooleanExtra("flowfinish", false)
        secoundtimeopen = intent.getBooleanExtra("secoundtimeopen", false)
        shouldReturnResultForFlow3 = intent.getBooleanExtra(EXTRA_RETURN_RESULT_FOR_FLOW3, false)
        autoNavigationHandled =
            savedInstanceState?.getBoolean(KEY_AUTO_NAVIGATION_HANDLED, false) ?: false

        config.isapplangscreenopen = true
        changelang = config.SelectedLanguage
        config.firsttimeusersetlang = true
        config.languageShowFirstvarA = true
        if (!config.firsttimeusersetlang) {
            firebaseEventMain("Language")
        }

        if (isFLow1) {
            logOnboardingFunnelStep("First_language")
        } else {
            logOnboardingFunnelStep("First_language")
        }



        if (firsttimeuserset) {
            binding.langChangeNew.beVisible()
            binding.langChange.beGone()
        } else {
            binding.langChangeNew.beVisible()
            binding.langChange.beGone()
        }

        with(binding) {
            fontSize10 = getTextSizeForeNormal10MS()
            fontSize13 = getTextSizeForeNormal13MS()
            fontSize18 = getTextSizeForeNormal18MS()
            fontSize8 = getTextSizeForeNormal8MS()
            fontSize15 = getTextSizeHometitleMS()

            binding.textsizechagefor10 = fontSize10
            binding.textsizechagefor13 = fontSize13
            binding.textsizechagefor18 = fontSize18
            binding.textsizechagefor8 = fontSize8
            binding.textsizechagefor15 = fontSize15

            adapter = adapterP
            adapterP.setHasStableIds(true)

            model = ViewModelProvider(this@LanguageActivity)[LanguageViewModel::class.java]
            model.refreshdata()

            if (config.SelectedLanguage == "ar") {
                searchBarFull.gravity = Gravity.END
            } else {
                searchBarFull.gravity = Gravity.START
            }
            applyMaterialLanguageColors()

            model.lanlistlive.observe(this@LanguageActivity, Observer {
                Log.d(
                    "lanlistlive",
                    "onViewCreated: <----------> 1 <------> ${binding.searchBarFull.text.toString()}"
                )
                val serchtxt = binding.searchBarFull.text.toString()
                norecentfound = it.isEmpty()
                lanlistMain = ArrayList(it)
                adapterP.listitem = lanlistMain
                if (serchtxt.isNotEmpty()) {
                    filter(serchtxt)
                }
                if (it.isNotEmpty()) {
                    maybeShowLanguageNative("list_visible")
                }
            })
            backBtn.setOnClickListener {
                onBackPressed()
            }

            searchBarFull.onTextChangeListener { searchString ->
                filter(searchString)
            }
            messagebutton2.setOnClickListener {
                searchBarFull.setText("")

            }
            serchCleasr.setOnClickListener {
                checkMicrophonePermission()
            }
        }

        adapterP.SelectedLanguageClick = {
//            binding.langChange.visible()
            Log.d("", "onStart: firsttimeusersetlang <-----> 7 ${config.SelectedLanguage}")
            Log.d("", "onStart: firsttimeusersetlang <-----> 77 ${it}")

            changelang = it
            model.refreshdata()
        }


        binding.langChangeNew.setOnClickListener {
            binding.langChange.performClick()
        }


        binding.langChange.setOnClickListener {

            config.isrationshow = false
            config.SelectedLanguage = changelang
            binding.searchBarFull.text!!.clear()
            setLang1(changelang)
            if (shouldReturnResultForFlow3) {
                setResult(RESULT_OK)
                finish()
                return@setOnClickListener
            }
            if (mainopen) {
                if (needintervarA && isFLow1) {
                    if (!BaseSharedPreferences(this@LanguageActivity).mIS_SUBSCRIBED!!) {
                        baseConfig.onboardingHomeAgainPending = true
                    }
                }
                startActivity(Intent(this, HomeABActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finishAffinity()




            } else {

                intenttwo = Intent(
                    this,
                    HomeABActivity::class.java

                )
                intenttwo!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                this.startActivity(intenttwo!!)
                finishAffinity()

            }

        }
        languageAdHandler.postDelayed(languageAdDelayRunnable, 600L)
    }

    override fun onBackPressed() {
//        binding.langChange.performClick()


        config.isrationshow = false
//        config.SelectedLanguage = changelang
        binding.searchBarFull.text!!.clear()
//        setLang1(changelang)
        if (shouldReturnResultForFlow3) {
            setResult(RESULT_OK)
            finish()

        } else {
            if (mainopen) {

                    if (needintervarA && isFLow1) {
                        if (!BaseSharedPreferences(this@LanguageActivity).mIS_SUBSCRIBED!!) {
                            baseConfig.onboardingHomeAgainPending = true
                        }
                    }
                startActivity(Intent(this, HomeABActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })

                finishAffinity()




            } else {

                intenttwo = Intent(
                    this,
                    HomeABActivity::class.java

                )
                intenttwo!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                this.startActivity(intenttwo!!)
                finishAffinity()

            }
        }

//        if (!shouldReturnResultForFlow3) {
//            super.onBackPressed()
//        }
    }

    private fun filter(text: String) {
        try {
            val filterdNames = ArrayList<Languagemodel>()
            for (s in lanlistMain) {
                if (s.language.lowercase(Locale.getDefault())
                        .contains(text.lowercase(Locale.getDefault()))
                ) {
                    filterdNames.add(s)
                } else if (s.languagereal.lowercase(Locale.getDefault())
                        .contains(text.lowercase(Locale.getDefault()))
                ) {
                    filterdNames.add(s)
                }
            }
            binding.norecentfound = filterdNames.isEmpty()
            adapterP.listitem = filterdNames
        } catch (e: Exception) {
        }
    }


    private fun checkMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_PERMISSION_REQUEST
            )
        } else {
            openSpeechRecognition()
        }
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openSpeechRecognition()
            } else {
                toast(getString(R.string.microphone_permission_denied_new))
            }
        }
    }

    private fun openSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (e: ActivityNotFoundException) {
            toast(getString(R.string.speech_recognition_not_supported_new))
        }
    }

    // Handle speech recognition result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = matches?.get(0)
            binding.searchBarFull.setText(spokenText)
        }
    }

    override fun onStart() {
        super.onStart()
        applyMaterialLanguageColors()
        applyMaterialSystemBarColors()
    }

    override fun onPostResume() {
        super.onPostResume()
        if (shouldReturnResultForFlow3) {
            kpSmallNative()
            return
        }
        if (autoNavigationHandled) {
            return
        }
        autoNavigationHandled = true
        if (isValidforExperiment) {
            sendActivity()
        } else {
            sendActivity()
        }
    }

    private fun applyMaterialLanguageColors() {
        val surfaceColor = getProperBackgroundColor()
        val statusBarColor = getProperStatusBarColor()
        val textColor = getProperTextColor()
        val secondaryTextColor = getProperSecondaryTextColor()
        val primaryColor = getProperPrimaryColor()
        val searchFillColor = secondaryTextColor.adjustAlpha(0.08f)
        val searchStrokeColor = secondaryTextColor.adjustAlpha(0.20f)

        binding.mainlayout.setBackgroundColor(surfaceColor)
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)

        binding.toolBarMain.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = resources.getDimension(com.intuit.sdp.R.dimen._30sdp)
            setColor(searchFillColor)
            setStroke(
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp),
                searchStrokeColor
            )
        }

        binding.textView3.setTextColor(textColor)
        binding.textView35.setTextColor(secondaryTextColor)
        binding.searchBarFull.setTextColor(textColor)
        binding.searchBarFull.setHintTextColor(secondaryTextColor.adjustAlpha(0.70f))
        binding.searchBarFull.setCursorColorProgrammatically(primaryColor)

        binding.backBtn.iconTint = ColorStateList.valueOf(secondaryTextColor)
        binding.messagebutton2.iconTint = ColorStateList.valueOf(secondaryTextColor)
        binding.serchCleasr.iconTint = ColorStateList.valueOf(secondaryTextColor)
    }

    private fun applyMaterialSystemBarColors() {
        val surfaceColor = getProperBackgroundColor()
        val statusBarColor = getProperStatusBarColor()
        val navigationBarColor = getBottomNavigationBackgroundColor()

        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }

        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars =
                ThemeModeManager.shouldUseLightSystemBars(this@LanguageActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
    }

    private fun sendActivity() {
        "sendActivity <-------------> 0".log()
        if (flowfinish) {
            "sendActivity <-------------> 1".log()
            config.introshow = false
            if (!secoundtimeopen) {
                if (config.Full_AppLock_Pin == "Not Set") {
                    "sendActivity <-------------> 3".log()

                    "sendActivity <-------------> 5".log()
                    binding.mainlayout.beGone()
                    startActivity(Intent(this, HomeABActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })

                } else {
                    "sendActivity <-------------> 6".log()
                    isappopenlock = true
                    binding.mainlayout.beGone()
                    startActivity(
                        Intent(this, LockScreenActivity::class.java).putExtra(
                            "comefrom", 2
                        ).putExtra("appopen", true).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            } else {
                "sendActivity <-------------> 2".log()
            }
        } else {
            "sendActivity <-------------> 7".log()
            if (!config.introshow) {
                "sendActivity <-------------> 8".log()
                if (config.firsttimeusersetlang) {
                    "sendActivity <-------------> 9".log()
                    if (config.Full_AppLock_Pin == "Not Set") {
                        "sendActivity <-------------> 10".log()

                        "sendActivity <-------------> 12".log()
                        binding.mainlayout.beGone()
                        startActivity(Intent(this, HomeABActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })

                    } else {
                        "sendActivity <-------------> 13".log()
                        isappopenlock = true
                        binding.mainlayout.beGone()
                        startActivity(
                            Intent(this, LockScreenActivity::class.java).putExtra(
                                "comefrom", 2
                            ).putExtra("appopen", true).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                } else {
                    kpSmallNative()
                    "sendActivity <-------------> 14".log()
                }
            } else {
                kpSmallNative()
                "sendActivity <-------------> 15".log()
            }
        }
    }

    private fun kpSmallNative() {
        binding.constSmallNativeAdView.gone()
    }

    private fun maybeShowLanguageNative(trigger: String) {
        if (languageAdRequested) {
            return
        }
        languageAdRequested = true
        languageAdHandler.removeCallbacks(languageAdDelayRunnable)
        adsOrchestrator.showLanguageNative(
            activity = this,
            ui = AdUiBinding(
                rootContainer = binding.constSmallNativeAdView,
                adFrame = binding.frameSmallNativeAdBottom,
                shimmer = binding.smallNativeAdShimmerBottom
            ),
            onNoAd = { binding.constSmallNativeAdView.gone() }
        )
        Log.d("LanguageAds", "native_request_trigger=$trigger")
    }


    override fun onDestroy() {
        languageAdHandler.removeCallbacks(languageAdDelayRunnable)
        super.onDestroy()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_AUTO_NAVIGATION_HANDLED, autoNavigationHandled)
    }
}
