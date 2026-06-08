package com.messenger.phone.number.text.sms.service.apps.fragment

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isPackageInstalled
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setchatthemecolor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setchatthemecolorTintImfullapp
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setchatthemecolorTintTxt
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.CommanClass.updateStatusbarColorFullApp
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentPrivacyPoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Fragment_privacy_policy : Fragment() {


    lateinit var binding: FragmentPrivacyPoBinding
    private var package_name = "com.android.chrome";

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_privacy_po, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            txt1.setOnClickListener {
                openUrl("https://policies.google.com/privacy")
            }
            txt2.setOnClickListener {
                openUrl("https://support.google.com/admob/answer/6128543?hl=en")
            }
            txt3.setOnClickListener {
                openUrl("https://firebase.google.com/policies/analytics")
            }
            txt4.setOnClickListener {
                openUrl("https://firebase.google.com/support/privacy/")
            }
            txt5.setOnClickListener {
                openUrl("https://www.facebook.com/about/privacy/update/printable")
            }
            backBtn.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
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
                customBuilder.launchUrl(requireActivity(), Uri.parse(url))
            } catch (e: Exception) {
                openlink(url)
            }
        } else {
            openlink(url)
        }
    }

    fun openlink(url: String) {
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
    }

    private fun ThemeSetup() {
        binding.defaulttoolshow.setchatthemecolor(0)
        binding.backBtn.setchatthemecolorTintImfullapp(0)
        binding.textView3.setchatthemecolorTintTxt(0)
        requireActivity().updateStatusbarColorFullApp()
    }


}
