package com.messenger.phone.number.text.sms.service.apps.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.demo.adsmanage.Commen.beGone
import com.demo.adsmanage.Commen.beVisible
import com.demo.adsmanage.Commen.firebaseEventMain
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adloadornot
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.changelang
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createOptionBackground
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
import com.messenger.phone.number.text.sms.service.apps.CommanClass.langchange
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setCursorColorProgrammatically
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLang
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.HomeABActivity
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.adapter.Languagesadapter
import com.messenger.phone.number.text.sms.service.apps.ads.v2.AdUiBinding
import com.messenger.phone.number.text.sms.service.apps.ads.v2.AdsOrchestrator
import com.messenger.phone.number.text.sms.service.apps.data.Config
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentLanguageBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.Languagemodel
import com.messenger.phone.number.text.sms.service.apps.viewModel.LanguageViewModel
import com.simplemobiletools.commons.extensions.onTextChangeListener
import com.simplemobiletools.commons.extensions.toast
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class LanguageFragment : Fragment() {

    lateinit var binding: FragmentLanguageBinding

//    private val dataSourceLocalNative by lazy { DataSourceLocalNative() }
//
//    private val dataSourceRemoteNative by lazy { DataSourceRemoteNative(requireActivity()) }
//
//    private val viewModelNative by viewModels<ViewModelNative>()


    private var argumentValue: Boolean = false
    private var intent: Intent? = null
    private lateinit var model: LanguageViewModel

    var firsttimeuserset = false

    private lateinit var config: Config

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
    private val languageAdHandler = Handler(Looper.getMainLooper())
    private val languageAdDelayRunnable = Runnable { maybeShowLanguageNative("delayed_render") }
    private var languageAdRequested = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        firsttimeuserset = arguments?.getBoolean("firsttimeusersetlang") == true
        requireContext().setLocal()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_language, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        config = requireContext().config
        config.isapplangscreenopen = true
//        AdsLoadAndShowAdsLoadAndShow()
        changelang = config.SelectedLanguage
        config.firsttimeusersetlang = true
        if (!config.firsttimeusersetlang) {
            requireActivity().firebaseEventMain("Language")
        }

        if (firsttimeuserset) {
            binding.langChangeNew.beVisible()
            binding.langChange.beGone()
        } else {
            binding.langChange.beVisible()
            binding.langChangeNew.beGone()
        }

        with(binding) {
            fontSize10 = requireActivity().getTextSizeForeNormal10MS()
            fontSize13 = requireActivity().getTextSizeForeNormal13MS()
            fontSize18 = requireActivity().getTextSizeForeNormal18MS()
            fontSize8 = requireActivity().getTextSizeForeNormal8MS()
            fontSize15 = requireActivity().getTextSizeHometitleMS()

            binding.textsizechagefor10 = fontSize10
            binding.textsizechagefor13 = fontSize13
            binding.textsizechagefor18 = fontSize18
            binding.textsizechagefor8 = fontSize8
            binding.textsizechagefor15 = fontSize15

            adapter = adapterP
            adapterP.setHasStableIds(true)

            model = ViewModelProvider(requireActivity())[LanguageViewModel::class.java]
            model.refreshdata()

            if (requireContext().config.SelectedLanguage == "ar") {
                searchBarFull.gravity = Gravity.END
            } else {
                searchBarFull.gravity = Gravity.START
            }
            applyMaterialLanguageColors()

            model.lanlistlive.observe(requireActivity(), Observer {
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

                val count = parentFragmentManager.backStackEntryCount
//
                if (count > 0) {
                    parentFragmentManager.popBackStack()
                } else {
                    requireActivity().onBackPressed()
                }
            }


            binding.messagebutton2.setOnClickListener {
                searchBarFull.setText("")

            }
            binding.searchBarFull.onTextChangeListener { searchString ->
                filter(searchString)
            }

            serchCleasr.setOnClickListener {
                checkMicrophonePermission()
            }
        }
        languageAdHandler.removeCallbacks(languageAdDelayRunnable)
        languageAdHandler.postDelayed(languageAdDelayRunnable, 600L)
        adapterP.SelectedLanguageClick = {
//            binding.langChange.visible()
            Log.d("", "onStart: firsttimeusersetlang <-----> 7 ${config.SelectedLanguage}")
            Log.d("", "onStart: firsttimeusersetlang <-----> 77 ${it}")

            /*     if (it == config.SelectedLanguage) {
                     binding.langChange.setImageResource(R.drawable.language_arror)
     //                binding.langchangeLottie.gone()
                 } else {
                     binding.langChange.setImageResource(R.drawable.language_arror)
     //                binding.langchangeLottie.visible()
                 }*/

            changelang = it
            model.refreshdata()
        }


        binding.langChangeNew.setOnClickListener {
            binding.langChange.performClick()
        }

        binding.langChange.setOnClickListener {
            langchange = true
            adloadornot = false
            config.isrationshow = false
            config.SelectedLanguage = changelang
            binding.searchBarFull.text!!.clear()
            requireContext().setLang(changelang)

//            intent = if (firsttimeuserset) {
//                if (config.introshow) {
//                    Intent(requireActivity(), IntroActivity::class.java)
//                } else {
//                    Intent(
//                        requireActivity(),
//                         HomeABActivity::class.java
//                    )
//                }
//            } else {
//                Intent(
//                    requireActivity(),
//                     HomeABActivity::class.java
//                )
//            }

            intent = Intent(
                requireActivity(),
                HomeABActivity::class.java
            )
            this.startActivity(intent!!)
            requireActivity().finishAffinity()
        }
    }

    private fun maybeShowLanguageNative(trigger: String) {
        if (languageAdRequested || !isAdded) {
            return
        }
        languageAdRequested = true
        languageAdHandler.removeCallbacks(languageAdDelayRunnable)
        AdsOrchestrator.get(requireContext()).showLanguageNative(
            activity = requireActivity(),
            ui = AdUiBinding(
                rootContainer = binding.constSmallNativeAdView,
                adFrame = binding.frameSmallNativeAdBottom,
                shimmer = binding.smallNativeAdShimmerBottom
            ),
            onNoAd = { binding.constSmallNativeAdView.gone() }
        )
        Log.d("LanguageAds", "native_request_trigger=$trigger")
    }

    override fun onStart() {
        super.onStart()
        applyMaterialLanguageColors()
        applyMaterialSystemBarColors()
    }

    private fun applyMaterialLanguageColors() {
        val context = requireContext()
        val surfaceColor = context.getProperBackgroundColor()
        val textColor = context.getProperTextColor()
        val secondaryTextColor = context.getProperSecondaryTextColor()
        val primaryColor = context.getProperPrimaryColor()
        val searchFillColor = secondaryTextColor.adjustAlpha(0.08f)
        val searchStrokeColor = secondaryTextColor.adjustAlpha(0.20f)

        binding.mainBg.setBackgroundColor(surfaceColor)
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
//        binding.toolBarMain.background = GradientDrawable().apply {
//            shape = GradientDrawable.RECTANGLE
//            cornerRadius = resources.getDimension(com.intuit.sdp.R.dimen._30sdp)
//            setColor(searchFillColor)
//            setStroke(0, searchStrokeColor)
//        }

        binding.toolBarMain.background = createOptionBackground(
            cornerSize = resources.getDimension(com.intuit.sdp.R.dimen._30sdp),
            fillColor = searchFillColor,
        )

        binding.textView3.setTextColor(textColor)
        binding.textView35.setTextColor(secondaryTextColor)
        binding.searchBarFull.setTextColor(textColor)
        binding.searchBarFull.setHintTextColor(secondaryTextColor.adjustAlpha(0.70f))
        binding.searchBarFull.setCursorColorProgrammatically(primaryColor)

        binding.backBtn.iconTint = ColorStateList.valueOf(secondaryTextColor)
        binding.messagebutton2.iconTint = ColorStateList.valueOf(secondaryTextColor)
        binding.serchCleasr.iconTint = ColorStateList.valueOf(secondaryTextColor)
        binding.langChange.iconTint = ColorStateList.valueOf(primaryColor)
        binding.langChangeNew.iconTint = ColorStateList.valueOf(primaryColor)
    }

    private fun applyMaterialSystemBarColors() {
        val context = requireContext()
        val surfaceColor = context.getProperBackgroundColor()
        val statusBarColor = context.getProperStatusBarColor()
        val navigationBarColor = context.getBottomNavigationBackgroundColor()
        val window = requireActivity().window
        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars = ThemeModeManager.shouldUseLightSystemBars(context)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
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
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_AUDIO_PERMISSION_REQUEST
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
        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openSpeechRecognition()
            } else {
                requireActivity().toast(getString(R.string.microphone_permission_denied_new))
            }
        }
    }

    private fun openSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (e: ActivityNotFoundException) {
            requireActivity().toast(getString(R.string.speech_recognition_not_supported_new))
        }
    }

    // Handle speech recognition result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = matches?.get(0)
            binding.searchBarFull.setText(spokenText)
        }
    }

    override fun onDestroyView() {
        languageAdHandler.removeCallbacks(languageAdDelayRunnable)
        super.onDestroyView()
    }
}
