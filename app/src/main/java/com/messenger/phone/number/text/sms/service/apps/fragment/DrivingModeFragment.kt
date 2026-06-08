package com.messenger.phone.number.text.sms.service.apps.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.demo.adsmanage.Commen.firebaseEventMain
import com.messenger.phone.number.text.sms.service.apps.CommanClass.SetSwitchColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.cancelOtpdeletePendingIntent
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createCustomDrawable
import com.messenger.phone.number.text.sms.service.apps.CommanClass.drivingallbuttonshowcaseshow
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.hideKeyboard
import com.messenger.phone.number.text.sms.service.apps.CommanClass.otpoutodelete
import com.messenger.phone.number.text.sms.service.apps.CommanClass.privacyallbuttonshowcaseshow
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setchatthemecolor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setchatthemecolorBg
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setchatthemecolorTintImfullapp
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setchatthemecolorTintTxt
import com.messenger.phone.number.text.sms.service.apps.CommanClass.showsettingshowcasedriving
import com.messenger.phone.number.text.sms.service.apps.CommanClass.showsettingshowcaseprivacy
import com.messenger.phone.number.text.sms.service.apps.CommanClass.updateStatusbarColorFullApp
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.Dialog.EditAutoReplMessageDrivingModeDialog
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.ShowCase.DismissType
import com.messenger.phone.number.text.sms.service.apps.ShowCase.GuideView
import com.messenger.phone.number.text.sms.service.apps.ShowCase.PointerType
import com.messenger.phone.number.text.sms.service.apps.data.Config
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentDrivingModeBinding
import com.simplemobiletools.commons.extensions.baseConfig
import com.simplemobiletools.commons.extensions.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DrivingModeFragment : Fragment() {

    lateinit var binding: FragmentDrivingModeBinding
    private lateinit var config: Config

    private var builder: GuideView.Builder? = null
    private var mGuideView: GuideView? = null

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    val drivingmodedialog by lazy { EditAutoReplMessageDrivingModeDialog() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        requireContext().setLocal()
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_driving_mode, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        config = requireContext().config

        config.isdriwingmodescreenopen = true

        requireContext().config.fragmentshowcasecout = 12

        with(binding) {
            requireActivity().firebaseEventMain("Driving_Mode")
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


            blockUserCount.text = config.drivingmodemessage

            drivingModeAutoReply.isChecked = config.isdringmodeone
            drivingModeAutoReplyAb.isChecked = config.isdringmodeone

            drivingModeAutoReply.setOnCheckedChangeListener { _, isChecked ->
                config.isdringmodeone = isChecked
            }

            drivingModeAutoReplyAb.setOnCheckedChangeListener { _, isChecked ->
                config.isdringmodeone = isChecked
            }

            backBtn.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            divModeUserBtnCard.setOnClickListener {
                drivingmodedialog.show(
                    requireActivity().supportFragmentManager,
                    "drivingmodedialog"
                )
            }


            divModeUserBtnCardAb.setOnClickListener {
                drivingmodedialog.show(
                    requireActivity().supportFragmentManager,
                    "drivingmodedialog"
                )
            }

            drivingmodedialog.dialogonDismiss = {
                requireActivity().hideKeyboard()
                blockUserCount.text = config.drivingmodemessage
            }
        }

        if (drivingallbuttonshowcaseshow) {
//            showCasesettingActivityDrivingFirst()
        } else if (requireActivity().config.forthdshowcaseshowfordrivingmode) {
//            showCasesettingActivityDrivingFirst()
        }

    }

    private fun showCasesettingActivityDrivingFirst() {
        builder = GuideView.Builder(requireActivity())
            .setTitle("1")
            .setContentText("1")
            .setGravity(com.messenger.phone.number.text.sms.service.apps.ShowCase.Gravity.center)
            .setDismissType(DismissType.targetView)
            .setPointerType(PointerType.circle)
            .setTargetView(binding.preandsecBtnCard)
            .setGuideListener { view ->
                when (view.id) {
                    R.id.preandsec_btn_card -> {
                        builder?.setTargetView(binding.divModeUserBtnCard)
                            ?.setTitle("2")
                            ?.setContentText("2")
                    }

                    R.id.div_mode_user_btn_card -> {
                        showsettingshowcasedriving = false
                        requireActivity().config.forthdshowcaseshowfordrivingmode = false
                        return@setGuideListener
                    }
                }
                mGuideView = builder?.build()
                mGuideView?.show()
            }

        mGuideView = builder?.build()
        mGuideView?.show()
    }

    override fun onStart() {
        super.onStart()

        binding.drivingModeAutoReplyAb.SetSwitchColor(0)

        if (requireContext().config.setABHomeActivityPref == "1") {
            binding.fistContenar.visible()
            binding.fistContenarab.gone()
        } else {
            binding.fistContenar.gone()
            binding.fistContenarab.visible()

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
                    binding.fistContenarab.background = background
                }

            }
        }


        ThemeSetup()
    }

    private fun ThemeSetup() {
        binding.defaulttoolshow.setchatthemecolor(0)
        binding.backBtn.setchatthemecolorTintImfullapp(0)
        binding.textView3.setchatthemecolorTintTxt(0)
        requireActivity().updateStatusbarColorFullApp()
    }

}