package com.messenger.phone.number.text.sms.service.apps.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.firebaseEventMain
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createCustomDrawable
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isPackageInstalled
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isValidUrl
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setchatthemecolor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setchatthemecolorBg
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setchatthemecolorTintImfullapp
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setchatthemecolorTintTxt
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.CommanClass.updateStatusbarColorFullApp
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentAboutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AboutFragment : Fragment() {

    private val url: String = "https://imagecropnwallpaperchanger.blogspot.com/2023/08/privacy-policy-messages.html"
    lateinit var binding: FragmentAboutBinding

    private var package_name = "com.android.chrome";


    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireContext().setLocal()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            requireActivity().firebaseEventMain("About")
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

            backBtn.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            newUserBtnCard.setOnClickListener {
//                val action = AboutFragmentDirections.AboutFragmentToWhatnewfragment()
//                findNavController().navigate(action)

            }

            ppUserBtnCard.setOnClickListener {
//                Constants.isActivitychange = true
//                Constants.isAdsClicking = true
//                if (requireActivity().config.isinappbrowser) {
//                    if (!url.isValidUrl()) {
//                        openlink(url)
//                    } else {
//                        openUrl(url)
//                    }
//                } else {
//                    openlink(url)
//                }
//                val action = AboutFragmentDirections.AboutFragmentToPrivacypolicyfragment()
//                findNavController().navigate(action)
                openUrl("https://privacypolicy.kriadl.com/messages-sms-and-private-chat/privacy-policy")
            }

            ppUserBtnCardAb.setOnClickListener {
                openUrl("https://privacypolicy.kriadl.com/messages-sms-and-private-chat/privacy-policy")
            }


            tremCardBtn.setOnClickListener {

                Constants.isActivitychange = true
                Constants.isAdsClicking = true
                if (requireActivity().config.isinappbrowser) {
                    if (!url.isValidUrl()) {
                        openlink(url)
                    } else {
                        openUrl(url)
                    }
                } else {
                    openlink(url)
                }

            }

        }
    }

    fun openUrl(url: String) {
        val builder = CustomTabsIntent.Builder()
        val params = CustomTabColorSchemeParams.Builder()
        params.setToolbarColor(ContextCompat.getColor(requireContext(), R.color.appcolor))
        builder.setDefaultColorSchemeParams(params.build())
        builder.setShowTitle(true)
        builder.setShareState(CustomTabsIntent.SHARE_STATE_ON)
        builder.setInstantAppsEnabled(true)
        val customBuilder = builder.build()
        if (requireActivity().isPackageInstalled(package_name)) {
            customBuilder.intent.setPackage(package_name)
            try {
                customBuilder.launchUrl(requireContext(), Uri.parse(url))
            } catch (e: Exception) {
                openlink(url)
            }
        } else {
            openlink(url)
        }
    }



    fun openlink(url: String) {
//        val browserIntent = Intent(Intent.ACTION_VIEW)
//        browserIntent.data = Uri.parse(url)
//        requireActivity().startActivity(browserIntent)

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            requireActivity().startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
            requireActivity().toastMess("No web browser app found")
        }
    }


    override fun onStart() {
        super.onStart()
        ThemeSetup()

        if (requireContext().config.setABHomeActivityPref == "1") {
            binding.ppUserBtnCard.visible()
            binding.ppUserBtnCardAb.gone()
        } else {
            binding.ppUserBtnCard.gone()
            binding.ppUserBtnCardAb.visible()

            CoroutineScope(Dispatchers.IO).launch {

                val background = requireContext().createCustomDrawable(
                    cornerRadiusResId = com.intuit.sdp.R.dimen._10sdp,
                    solidColorResId =
                    if (requireContext().config.activeThemeSelection == 1) {
                        R.color.white
                    } else if (requireContext().config.activeThemeSelection == 2) {
                        R.color.setting_button_color_solid2
                    } else if (requireContext().config.activeThemeSelection == 3) {
                        R.color.setting_button_color_solid3
                    } else {
                        R.color.white
                    },
                    strokeColorResId =
                    if (requireContext().config.activeThemeSelection == 1) {
                        R.color.setting_button_color_boder
                    } else if (requireContext().config.activeThemeSelection == 2) {
                        R.color.setting_button_color_boder1
                    } else if (requireContext().config.activeThemeSelection == 3) {
                        R.color.setting_button_color_boder2
                    } else {
                        R.color.toolbarcolor2
                    },
                    strokeWidthResId = com.intuit.sdp.R.dimen._2sdp
                )
                CoroutineScope(Dispatchers.Main).launch {
                    binding.ppUserBtnCardAb.background = background
                }

            }
        }

    }

    private fun ThemeSetup() {
        binding.defaulttoolshow.setchatthemecolor(0)
        binding.backBtn.setchatthemecolorTintImfullapp(0)
        binding.textView3.setchatthemecolorTintTxt(0)
        requireActivity().updateStatusbarColorFullApp()
    }
}