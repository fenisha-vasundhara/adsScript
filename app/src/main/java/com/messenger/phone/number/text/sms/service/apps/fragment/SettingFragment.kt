package com.messenger.phone.number.text.sms.service.apps.fragment

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.AlarmManager
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.ALARM_SERVICE
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.demo.adsmanage.Commen.firebaseFunnel
import com.demo.adsmanage.Commen.log
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.messenger.phone.number.text.sms.service.apps.ArchivedActivity
import com.messenger.phone.number.text.sms.service.apps.BekupActivity
import com.messenger.phone.number.text.sms.service.apps.BlockManageActivity
import com.messenger.phone.number.text.sms.service.apps.CommanClass.AutonotificationStat
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ShowsubScreenDialogopen
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adloadornot
import com.messenger.phone.number.text.sms.service.apps.CommanClass.checkIfFileExists
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createOptionBackground
import com.messenger.phone.number.text.sms.service.apps.CommanClass.filePathForLock
import com.messenger.phone.number.text.sms.service.apps.CommanClass.firebaseEvent
import com.messenger.phone.number.text.sms.service.apps.CommanClass.fromnotificationAction
import com.messenger.phone.number.text.sms.service.apps.CommanClass.fromnotificationActionFragment
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getBottomNavigationBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getDialogBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMaterialDialogTheme
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
import com.messenger.phone.number.text.sms.service.apps.CommanClass.onlyfinish
import com.messenger.phone.number.text.sms.service.apps.CommanClass.openRequestExactAlarmSettings
import com.messenger.phone.number.text.sms.service.apps.CommanClass.sendsubactivity
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.shareApp
import com.messenger.phone.number.text.sms.service.apps.CommanClass.systemfountstart
import com.messenger.phone.number.text.sms.service.apps.CommanClass.systemfountstartsetting
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.Dialog.Conversation_List_View_Dialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.Message_Corner_Dialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.Message_Text_Size_Dialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.Message_Text_Size_New_Dialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.Signature_Setting_Dialog
import com.messenger.phone.number.text.sms.service.apps.GoogleMobileAdsConsentManagerChack.GoogleMobileAdsConsentManager
import com.messenger.phone.number.text.sms.service.apps.HomeABActivity
import com.messenger.phone.number.text.sms.service.apps.LockScreenActivity
import com.messenger.phone.number.text.sms.service.apps.LockScreenPinSetActivity
import com.messenger.phone.number.text.sms.service.apps.LockScreenSettingActivity
import com.messenger.phone.number.text.sms.service.apps.OverlayPermissionAnimationActivity
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.RecycleBinActivity
import com.messenger.phone.number.text.sms.service.apps.Schedule_Message_Show_Activity
import com.messenger.phone.number.text.sms.service.apps.WallpaperChoiceDialogAdapter
import com.messenger.phone.number.text.sms.service.apps.ads.v2.AdUiBinding
import com.messenger.phone.number.text.sms.service.apps.ads.v2.AdsOrchestrator
import com.messenger.phone.number.text.sms.service.apps.databinding.DialogMessageBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentSettingBinding
import com.messenger.phone.number.text.sms.service.apps.onbackbress123
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetAllConversationArchivedViewModel
import com.simplemobiletools.commons.extensions.getContrastColor
import com.simplemobiletools.commons.helpers.isSPlus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SettingFragment : Fragment() {


    lateinit var binding: FragmentSettingBinding
    private var settingNativeAdRequested = false
    private val settingAdHandler = Handler(Looper.getMainLooper())
    private val settingAdDelayRunnable = Runnable { maybeShowSettingNative("dwell_25s") }


    var userInteracted = false

    private lateinit var appOps: AppOpsManager
    private var appOpsListener: AppOpsManager.OnOpChangedListener? = null
    private var pendingCallerSwitchState: Boolean? = null

    private val overlayPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (!isAdded || !::binding.isInitialized) return@registerForActivityResult
            if (Settings.canDrawOverlays(requireContext())) {
                handleOverlayPermissionGranted()
            } else {
                pendingCallerSwitchState = null
                stopOverlayPermissionWatcher()
                binding.settingAb.SystemTextViewSwitchAbCaller.isChecked = false
            }
        }


    var onbackpressfornotification = false

    lateinit var model: GetAllConversationArchivedViewModel

    val messageTextSizeDialog: Message_Text_Size_Dialog by lazy { Message_Text_Size_Dialog() }

    @Inject
    lateinit var messageCornerDialog: Message_Corner_Dialog

    @Inject
    lateinit var conversationListViewDialog: Conversation_List_View_Dialog

    @Inject
    lateinit var signatureSettingDialog: Signature_Setting_Dialog

    var filled = true

    var firsttimenotcall = false

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f
    var isshowads: Boolean = true
    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        requireContext().setLocal()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        return binding.root
    }

    fun Context.forcedNightContext(): Context {
        val config = resources.configuration.apply {
            uiMode =
                (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_YES
        }
        return createConfigurationContext(config)
    }

    fun Context.forcedLightContext(): Context {
        val config = resources.configuration.apply {
            uiMode =
                (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_NO
        }
        return createConfigurationContext(config)
    }




    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            setuptxtsize()

            setdefaultselection()
            setThemeModeSummary()
            setDefaultview()
            setupThemeModeSelection()

//            binding.textView3.setOnClickListener {
//                if (filled) {
//                    filled = false
//                    setIcon(filled)
//                } else {
//                    filled = true
//                    setIcon(filled)
//                }
//            }

            settingAb.InAppBrowserChangeAb.isChecked = requireContext().config.isinappbrowser

            settingAb.InAppBrowserChangeShowMesageCountAb.isChecked =
                requireContext().config.showCharacterCounter

            settingAb.InAppSignatureShowMesageCountAb.isChecked =
                requireContext().config.userpreferenceSignatureOnOff


            settingAb.SystemTextViewSwitchAb.isChecked =
                requireContext().config.SystemTextViewSwitchAb


//            settingAb.SystemTextViewSwitchAbCaller.isChecked =
//
//                requireContext().config.SystemTextViewSwitchAbCaller


//            settingAb.SystemGeneratedIconSwitchAb.isChecked =
//                requireContext().config.Systemgeneratediconswitchab
//
//
//            settingAb.SystemGeneratedIconSwitchAb.setOnCheckedChangeListener { _, isChecked ->
//                requireContext().config.Systemgeneratediconswitchab = isChecked
//                SystemGeneratedIconSwitchAb = true
//            }

            settingAb.SystemTextViewSwitchAb.setOnCheckedChangeListener { _, isChecked ->
                adloadornot = false
                requireContext().config.SystemTextViewSwitchAb = isChecked
                systemrestart()
            }

            settingAb.SystemTextViewSwitchAbCaller.setOnTouchListener { _, _ ->
                userInteracted = true
                false // allow normal switch behavior
            }



            settingAb.SystemTextViewSwitchAbCaller.setOnCheckedChangeListener { _, isChecked ->
                if (userInteracted) {
                    userInteracted = false // reset flag
                    if (!isChecked) {
                        requireContext().config.SystemTextViewSwitchAbCaller = false
                        return@setOnCheckedChangeListener
                    }
                    if (Settings.canDrawOverlays(requireContext())) {
                        if ((ContextCompat.checkSelfPermission(
                                requireContext(), android.Manifest.permission.READ_PHONE_STATE
                            ) == PackageManager.PERMISSION_GRANTED)
                        ) {
                            requireContext().config.SystemTextViewSwitchAbCaller = isChecked
                        } else {
                            requestPermissions(
                                arrayOf(android.Manifest.permission.READ_PHONE_STATE), 101
                            )
                        }
                    } else {
                        onlyfinish = true
                        binding.settingAb.SystemTextViewSwitchAbCaller.isChecked = false
                        requestOverlayPermissionForCallerSwitch(isChecked)

                    }
                } else {
                    Log.d("Debug", "Ignored programmatic toggle")
                }
            }


            googleMobileAdsConsentManager =
                GoogleMobileAdsConsentManager.getInstance(requireActivity().applicationContext)

            googleMobileAdsConsentManager.gatherConsent(requireActivity()) { consentError ->
                if (consentError != null) {
                    Log.w(
                        "TAG",
                        "<-----------> ${consentError.errorCode}. ${consentError.message}"
                    )
                }
            }


            settingAb.recylerbinUserBtnCard.setOnClickListener {


                requireActivity().ShowsubScreenDialogopen(
                    supportFragmentManager = requireActivity().supportFragmentManager,
                    dialogcolose = { subopen ->
                        if (!subopen) {
                            requireActivity().firebaseEvent("SettingActivity", "recycle bin")
                            startActivity(
                                Intent(requireActivity(), RecycleBinActivity::class.java)
                            )
                        }
                    })
            }

            settingAb.messageTxtSizeBtnCardAb.setOnClickListener {
                Message_Text_Size_New_Dialog { isdonebuttonclick, fontsizeselectionold, fontsizeold ->
                    if (isdonebuttonclick) {
                        setuptxtsize()
                        makebackgroundcontenar()
                    }
                }.show(
                    requireActivity().supportFragmentManager, "messageTextSizeDialog"
                )
            }

            messageTextSizeDialog.doneclickofmessagetextsize =
                { isdonebuttonclick, fontsizeselectionold, fontsizeold ->
                    if (isdonebuttonclick) {
                        setuptxtsize()
                        makebackgroundcontenar()
                    }
                }

            if (googleMobileAdsConsentManager.canRequestAds) {
                isshowads = true
            }

            if (isshowads) {
                isshowads = requireActivity().config.All_Ads_On
            }


            settingAb.InAppSignatureShowMesageCountAb.setOnCheckedChangeListener { _, isChecked ->
                requireActivity().firebaseEvent("SettingActivity", "Signature")
                if (isChecked) {
                    val existingFragment =
                        requireActivity().supportFragmentManager.findFragmentByTag("InAppSignatureShowMesageCountNew")
                    if (existingFragment == null) {
                        signatureSettingDialog.show(
                            requireActivity().supportFragmentManager,
                            "InAppSignatureShowMesageCountNew"
                        )
                    }


                } else {
                    requireContext().config.userpreferenceSignatureOnOff = false
                }
            }




            signatureSettingDialog.Signature_Setting_DialogDismiss = {

                //done
                settingAb.InAppSignatureShowMesageCountAb.isChecked =
                    requireContext().config.userpreferenceSignatureOnOff
            }


            //done
            settingAb.InAppBrowserChangeAb.setOnCheckedChangeListener { _, isChecked ->
                requireActivity().firebaseEvent("SettingActivity", "In App Browser")
                requireContext().config.isinappbrowser = isChecked
            }



            settingAb.InAppBrowserChangeShowMesageCountAb.setOnCheckedChangeListener { _, isChecked ->
                requireActivity().firebaseEvent("SettingActivity", "Show Message Count")
                requireContext().config.showCharacterCounter = isChecked
            }


            if (requireContext().config.SelectedLanguage == "ar") {
                settingAb.InSystemTextViewSwitchAbCaller.gravity = Gravity.END

            } else {

                settingAb.InSystemTextViewSwitchAbCaller.gravity = Gravity.START
            }

            //done
            if (requireContext().config.SelectedLanguage == "ar") {
                settingAb.info6Ab.gravity = Gravity.END
                settingAb.info1Ab.gravity = Gravity.END
                settingAb.info2Ab.gravity = Gravity.END
                settingAb.InAppBrowserAb.gravity = Gravity.END
                settingAb.OpenExternalAb.gravity = Gravity.END
                settingAb.InAppBrowserShowMesageCountAb.gravity = Gravity.END
                settingAb.info7Ab.gravity = Gravity.END
                settingAb.textView99BtnAb.gravity = Gravity.END

            } else {
                settingAb.info6Ab.gravity = Gravity.START
                settingAb.info1Ab.gravity = Gravity.START
                settingAb.info2Ab.gravity = Gravity.START
                settingAb.InAppBrowserAb.gravity = Gravity.START
                settingAb.OpenExternalAb.gravity = Gravity.START
                settingAb.InAppBrowserShowMesageCountAb.gravity = Gravity.START
                settingAb.info7Ab.gravity = Gravity.START
                settingAb.textView99BtnAb.gravity = Gravity.START
            }

            // done <------>


            // done <------> <------->


            settingAb.blockBtnCardAb.setOnClickListener {

                startActivity(Intent(requireActivity(), BlockManageActivity::class.java))


            }
            settingAb.privateBtnCardAb.setOnClickListener {

                if (requireActivity().config.Lock_Screen_Pin != "Not Set") {
                    startActivity(
                        Intent(requireActivity(), LockScreenActivity::class.java)
                            .putExtra("lockype", 2)
                            .putExtra("comefrom", 1)
                    )
                } else {
                    startActivity(
                        Intent(
                            requireActivity(),
                            LockScreenPinSetActivity::class.java
                        ).putExtra("pinsetfor", 1)
                    )
                }


            }

            settingAb.archiveBtnCardAb.setOnClickListener {
                requireActivity().firebaseEvent("SettingActivity", "Archive")
                startActivity(Intent(requireActivity(), ArchivedActivity::class.java))
            }

            settingAb.notificationBtnCardAb.setOnClickListener {


                requireActivity().ShowsubScreenDialogopen(
                    directopendialog = true,
                    supportFragmentManager = requireActivity().supportFragmentManager,
                    dialogcolose = { subopen ->
                        if (!subopen) {
                            requireActivity().firebaseEvent(
                                "SettingActivity", "Nification and Sound"
                            )
                            val action =
                                SettingFragmentDirections.actionSettingFragmentToNotificationandsoundFragment()
                            findNavController().navigate(action)
                        }
                    })
            }

            // done <------> <------->





            backBtn.setOnClickListener {


                "SettingFrg1 onBackPressed onbackpressfornotification <-----------------> $onbackpressfornotification".log()
                "SettingFrg1 onBackPressed onbackbress123 <-----------------> $onbackbress123".log()

                if (onbackpressfornotification||onbackbress123) {
                    onbackpressfornotification = false
                    onbackbress123 = true
                    startActivity(
                        Intent(
                            requireActivity(),
                            HomeABActivity::class.java
                        )
                    )
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                } else {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }

            }

            settingAb.deliveryConfirmationOnoffAb.isChecked =
                requireContext().config.isdeliveryconfirmation

            settingAb.deliveryConfirmationOnoffAb.setOnCheckedChangeListener { _, isChecked ->

                if (isChecked) {
                    requireContext().config.isdeliveryconfirmation = isChecked
                } else {
                    requireContext().config.isdeliveryconfirmation = false
                }
            }

            settingAb.conversationBtnCardAb.setOnClickListener {
                requireActivity().firebaseEvent("SettingActivity", "Conversation Setting")
                val action =
                    SettingFragmentDirections.actionSettingFragmentToConversationSettingFragment()
                findNavController().navigate(action)
            }




            settingAb.LanguageBtnCardAb.setOnClickListener {


                requireActivity().ShowsubScreenDialogopen(
                    directopendialog = true,
                    supportFragmentManager = requireActivity().supportFragmentManager,
                    dialogcolose = { subopen ->
                        if (!subopen) {
                            requireActivity().firebaseEvent("SettingActivity", "Language")
                            val action =
                                SettingFragmentDirections.actionSettingFragmentToLanguageFragment()
                            findNavController().navigate(action)
                        }
                    })

            }


            settingAb.ConversationSwipBtnCardAb.setOnClickListener {
                requireActivity().firebaseEvent("SettingActivity", "Conversation Swip Motion")
//                val action =
//                    SettingFragmentDirections.actionSettingFragmentToConversationSwipMotionFragment()
//                findNavController().navigate(action)
                val navController = findNavController()

                if (navController.currentDestination?.id == R.id.settingFragment) {
                    val action =
                        SettingFragmentDirections.actionSettingFragmentToConversationSwipMotionFragment()
                    navController.navigate(action)
                }
            }





            settingAb.stepVarificationUserBtnCardAb.setOnClickListener {
                requireActivity().firebaseEvent("SettingActivity", "Two Step Verification")


                if (isSPlus()) {
                    val alarmManager: AlarmManager =
                        requireActivity().getSystemService(ALARM_SERVICE) as AlarmManager
                    if (alarmManager.canScheduleExactAlarms()) {
                        sendtwosteplockscreen()
                    } else {
                        dilogopeen2Step()
                    }
                } else {
                    sendtwosteplockscreen()
                }
            }



            settingAb.MessageTxtBtnCardAb.setOnClickListener {
                requireActivity().firebaseEvent("SettingActivity", "Check App Update")
                messageTextSizeDialog.show(
                    requireActivity().supportFragmentManager, "messageTextSizeDialog"
                )
            }

            settingAb.MessageCornerBtnCardAb.setOnClickListener {
                messageCornerDialog.show(
                    requireActivity().supportFragmentManager, "messageCornerDialog"
                )
            }

            settingAb.ConversationListBtnCardAb.setOnClickListener {
                conversationListViewDialog.show(
                    requireActivity().supportFragmentManager, "conversationListViewDialog"
                )
            }

            settingAb.backupAndRestoreBtnCardNewAb.setOnClickListener {
                requireActivity().firebaseEvent("SettingActivity", "Backup and Restore")
                startActivity(Intent(requireActivity(), BekupActivity::class.java))
            }

            settingAb.scheduleMessageBtnCardAb.setOnClickListener {
                requireActivity().firebaseEvent("SettingActivity", "Schedule Message")
                startActivity(Intent(requireActivity(), Schedule_Message_Show_Activity::class.java))
            }


            // done <------> <------->


//            messageTextSizeDialog.Message_Text_Size_Dialog_Dismiss = {
//                setdefaultselection()
//            }


            conversationListViewDialog.Conversation_List_View_Dialog_Dismiss = {

                setDefaultview()

            }

        }

        model =
            ViewModelProvider(requireActivity())[GetAllConversationArchivedViewModel::class.java]

        model.GetAllConversationlivelist.observe(requireActivity(), Observer {
            try {

                binding.settingAb.archiveUserCountAb.text =
                    "${it.distinctBy { it.threadId }.size.toString()} " + resources.getString(R.string.Conversation)
            } catch (_: Exception) {
            }
        })
        setNotificationAction()
        if (requireContext().config.appopenandshowcaseshow) {
            showfullscreenshowcase()
        } else {
            requireContext().config.appopenandshowcaseshow = true
        }


    }
    private fun dilogopeen2Step() {
        val activity = requireActivity()
        val dialogBinding = DialogMessageBinding.inflate(layoutInflater)

        val isSystemMode =
            ThemeModeManager.getThemeMode(activity) == ThemeModeManager.MODE_SYSTEM

        val forceDark = ThemeModeManager.isDarkThemeActive(activity)

        val bodyTextColor = if (isSystemMode) {
            MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorOnSurface)
        } else if (forceDark) {
            Color.WHITE
        } else {
            Color.BLACK
        }

        dialogBinding.message.text =
            getString(R.string.allow_alarm_for_three_day_try_new)

        dialogBinding.message.setTextColor(bodyTextColor)

        val dialog = MaterialAlertDialogBuilder(activity, activity.getMaterialDialogTheme())
            .setTitle(R.string.permission_required)
            .setView(dialogBinding.root) // IMPORTANT: not binding.root
            .setPositiveButton(R.string.grant_permission) { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    activity.openRequestExactAlarmSettings(
                        "com.messenger.phone.number.text.sms.service.apps"
                    )
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                ?.setTextColor(activity.getProperPrimaryColor())

            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
                ?.setTextColor(activity.getProperSecondaryTextColor())

            dialog.findViewById<TextView>(com.google.android.material.R.id.alertTitle)
                ?.setTextColor(bodyTextColor)

            dialog.window?.setBackgroundDrawable(
                MaterialShapeDrawable().apply {
                    fillColor = ColorStateList.valueOf(activity.getDialogBackgroundColor())
                    shapeAppearanceModel =
                        ShapeAppearanceModel.builder()
                            .setAllCornerSizes(80f)
                            .build()
                }
            )
        }

        dialog.show()
    }
    private fun requestOverlayPermissionForCallerSwitch(desiredState: Boolean) {
        val ctx = context ?: return
        pendingCallerSwitchState = desiredState
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${ctx.packageName}")
        )
        overlayPermissionLauncher.launch(intent)
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isAdded) return@postDelayed
            val animationIntent = Intent(ctx, OverlayPermissionAnimationActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                ctx,
                R.anim.fade_in,
                R.anim.fade_out
            )
            startActivity(animationIntent, options.toBundle())
        }, 100)
        startOverlayPermissionWatcher()
    }

    private fun startOverlayPermissionWatcher() {
        val ctx = context ?: return
        if (!::appOps.isInitialized) {
            appOps = ctx.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        }
        stopOverlayPermissionWatcher()
        val listener = AppOpsManager.OnOpChangedListener { _, packageName ->
            val safeContext = context ?: return@OnOpChangedListener
            if (packageName == safeContext.packageName && Settings.canDrawOverlays(safeContext)) {
                requireContext().firebaseFunnel("Aftercall_allow")
                handleOverlayPermissionGranted()
            }
        }
        appOpsListener = listener
        appOps.startWatchingMode(
            AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
            ctx.packageName,
            listener
        )
    }

    private fun handleOverlayPermissionGranted() {
        if (!isAdded || !::binding.isInitialized) return
        stopOverlayPermissionWatcher()
        val desiredState = pendingCallerSwitchState ?: return
        pendingCallerSwitchState = null
        if (!desiredState) {
            requireContext().config.SystemTextViewSwitchAbCaller = false
            binding.settingAb.SystemTextViewSwitchAbCaller.isChecked = false
            return
        }
        if ((ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            requireContext().config.SystemTextViewSwitchAbCaller = true
            binding.settingAb.SystemTextViewSwitchAbCaller.isChecked = true
        } else {
            binding.settingAb.SystemTextViewSwitchAbCaller.isChecked = false
            requestPermissions(arrayOf(android.Manifest.permission.READ_PHONE_STATE), 101)
        }
    }

    private fun stopOverlayPermissionWatcher() {
        if (!::appOps.isInitialized) return
        appOpsListener?.let { listener ->
            appOps.stopWatchingMode(listener)
        }
        appOpsListener = null
    }

    private fun systemrestart() {
        systemfountstart = true
        systemfountstartsetting = true
        requireActivity().recreate()
    }

    private fun setuptxtsize() {
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

        binding.settingAb.textsizechagefor10 = fontSize10
        binding.settingAb.textsizechagefor13 = fontSize13
        binding.settingAb.textsizechagefor18 = fontSize18
        binding.settingAb.textsizechagefor8 = fontSize8
        binding.settingAb.textsizechagefor15 = fontSize15
    }


    private fun sendtwosteplockscreen() {

        if (requireActivity().config.isAutoNotificationStart) {
            requireActivity().config.isAutoNotificationStart = false
            requireActivity().AutonotificationStat(0, 1 * 24 * 60 * 60 * 1000L, 1)
        }

        if (requireActivity().config.Full_AppLock_Pin != "Not Set") {
            startActivity(
                Intent(
                    requireActivity(),
                    LockScreenSettingActivity::class.java
                ).putExtra("comefrom", 2)
            )
        } else {
            if ((BaseSharedPreferences(requireActivity()).mIS_SUBSCRIBED == true)) {
                startActivity(
                    Intent(
                        requireActivity(), LockScreenPinSetActivity::class.java
                    ).putExtra("pinsetfor", 2)
                )
            } else {
                if (requireActivity().checkIfFileExists(requireActivity().filePathForLock)) {
                    requireActivity().sendsubactivity()
                } else {
                    startActivity(
                        Intent(
                            requireActivity(), LockScreenPinSetActivity::class.java
                        ).putExtra("pinsetfor", 2)
                    )
                }
            }
        }
    }


    private fun setDefaultview() {
        when (requireActivity().config.line_selection) {
            2 -> {
                binding.settingAb.ConversationListUserCountAb.text =
                    resources.getString(R.string.Two_Lines)
            }

            3 -> {
                binding.settingAb.ConversationListUserCountAb.text =
                    resources.getString(R.string.Three_Lines)
            }
        }
    }

    private fun setupThemeModeSelection() {
        binding.settingAb.themeChange.gone()
        binding.settingAb.ColorThemeBtnCardAb.setOnClickListener {
            showThemeModeDialog()
        }
    }

    private fun setThemeModeSummary() {
        val mode = ThemeModeManager.getThemeMode(requireContext())
        binding.settingAb.textView43.text = resources.getString(R.string.theme_mode)
        binding.settingAb.ColorThemeUserCountAb.text = resources.getString(getThemeModeLabel(mode))
    }

    private fun getThemeModeLabel(mode: Int): Int {
        return when (mode) {
            ThemeModeManager.MODE_SYSTEM -> R.string.system_default
            ThemeModeManager.MODE_DARK -> R.string.Dark
            else -> R.string.light_mode
        }
    }

    private fun showThemeModeDialog() {
        val currentMode = ThemeModeManager.getThemeMode(requireContext())
        val modeValues = intArrayOf(
            ThemeModeManager.MODE_SYSTEM,
            ThemeModeManager.MODE_LIGHT,
            ThemeModeManager.MODE_DARK
        )
        val options = arrayOf(
            resources.getString(R.string.system_default),
            resources.getString(R.string.light_mode),
            resources.getString(R.string.Dark)
        )

        var selectedItem = modeValues.indexOf(currentMode).coerceAtLeast(0)

        val primaryColor = requireContext().getProperPrimaryColor()
        val textColor    = requireContext().getProperTextColor()

        val adapter = WallpaperChoiceDialogAdapter(
            context = requireContext(),
            options = options,
            selectedPosition = selectedItem,
            primaryColor = primaryColor,
            textColor = textColor
        )

        MaterialAlertDialogBuilder(requireContext(), requireContext().getMaterialDialogTheme())
            .setTitle(resources.getString(R.string.select_theme_mode))
            .setSingleChoiceItems(adapter, selectedItem) { _, which ->
                selectedItem = which
                adapter.updateSelection(which)
            }
            .setPositiveButton(R.string.ok) { dialog, _ ->
                onbackbress123 = true
                val selectedMode = modeValues[selectedItem]
                if (selectedMode != currentMode) {
                    adloadornot = false
                    ThemeModeManager.setThemeMode(requireActivity(), selectedMode)
                    systemrestart()
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
            .also { dialog ->
                // Apply button colors
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(primaryColor)
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(textColor)
                // Ensure initial selection is visually updated
                dialog.listView?.post {
                    adapter.updateSelection(selectedItem)
                }
            }
    }

    private fun styleWallpaperChoiceDialog(dialog: AlertDialog) {
        val primaryColor = requireContext().getProperPrimaryColor()
        val textColor = requireContext().getProperTextColor()

        dialog.getButton(android.app.Dialog.BUTTON_POSITIVE)?.setTextColor(primaryColor)
        dialog.getButton(android.app.Dialog.BUTTON_NEGATIVE)?.setTextColor(textColor)
    }



    private fun setdefaultselection() {
        when (requireActivity().config.fontsizeselection) {
            "Small" -> {
                binding.settingAb.MessageTxtUserCountAb.text = resources.getString(R.string.Small)
            }

            "Normal" -> {
                binding.settingAb.MessageTxtUserCountAb.text = resources.getString(R.string.Normal)
            }

            "Large" -> {
                binding.settingAb.MessageTxtUserCountAb.text = resources.getString(R.string.Large)
            }

            "Extra Large" -> {
                binding.settingAb.MessageTxtUserCountAb.text =
                    resources.getString(R.string.Extra_Large)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.ispro = BaseSharedPreferences(requireContext()).mIS_SUBSCRIBED!!
        binding.settingAb.ispro = BaseSharedPreferences(requireContext()).mIS_SUBSCRIBED!!
        setupSendLongMessageAsMMS()
        setupGroupMessageAsMMS()
        setupUseSimpleCharacters()
    }

    private fun setupSendLongMessageAsMMS() = binding.apply {


        settingAb.InSwitchSendLongMessageAb.isChecked = requireContext().config.sendLongMessageMMS
        settingAb.InSwitchSendLongMessageAb.setOnCheckedChangeListener { _, isChecked ->
            requireContext().config.sendLongMessageMMS = isChecked
        }
    }

    private fun setupGroupMessageAsMMS() = binding.apply {


        settingAb.InSwitchSendGroupMessageAb.isChecked = requireContext().config.sendGroupMessageMMS
        settingAb.InSwitchSendGroupMessageAb.setOnCheckedChangeListener { _, isChecked ->
            requireContext().config.sendGroupMessageMMS = isChecked
        }
    }

    private fun setupUseSimpleCharacters() = binding.apply {
        settingAb.InSwitchRemoveAccentsAb.isChecked = requireContext().config.useSimpleCharacters
        settingAb.InSwitchRemoveAccentsAb.setOnCheckedChangeListener { _, isChecked ->
            requireContext().config.useSimpleCharacters = isChecked
        }


    }


    private fun setNotificationAction() {
        "getCodeReffrence setting <-----------------> 6666 ${fromnotificationActionFragment}".log()
        if (fromnotificationAction) {
            onbackpressfornotification = true
            fromnotificationAction = false
            if (fromnotificationActionFragment != "Nothing Set") {
                if (fromnotificationActionFragment == "notificationandsound") {
                    val action =
                        SettingFragmentDirections.actionSettingFragmentToNotificationandsoundFragment()
                    findNavController().navigate(action)
                } else if (fromnotificationActionFragment == "language") {
                    val action = SettingFragmentDirections.actionSettingFragmentToLanguageFragment()
                    findNavController().navigate(action)
                } else if (fromnotificationActionFragment == "messagetextsize") {
                    messageTextSizeDialog.show(
                        requireActivity().supportFragmentManager, "messageTextSizeDialog"
                    )
                } else if (fromnotificationActionFragment == "colortheme") {
                    showThemeModeDialog()
                } else if (fromnotificationActionFragment == "messagecorner") {
                    messageCornerDialog.show(
                        requireActivity().supportFragmentManager, "messageCornerDialog"
                    )
                } else if (fromnotificationActionFragment == "listview") {
                    conversationListViewDialog.show(
                        requireActivity().supportFragmentManager, "conversationListViewDialog"
                    )
                } else if (fromnotificationActionFragment == "swipemotion") {
                    val action =
                        SettingFragmentDirections.actionSettingFragmentToConversationSwipMotionFragment()
                    findNavController().navigate(action)
                } else if (fromnotificationActionFragment == "swipemotionwithfreetrial") {
                    val action =
                        SettingFragmentDirections.actionSettingFragmentToConversationSwipMotionFragment()
                    findNavController().navigate(action)
                } else if (fromnotificationActionFragment == "privacyandsecurity") {
                    val action =
                        SettingFragmentDirections.actionSettingFragmentToPrivacyandsecurityFragment()
                    findNavController().navigate(action)
                } else if (fromnotificationActionFragment == "drivingmode") {
                    val action =
                        SettingFragmentDirections.actionSettingFragmentToDrivingModeFragment()
                    findNavController().navigate(action)
                } else if (fromnotificationActionFragment == "backupandrestore") {
                    startActivity(Intent(requireActivity(), BekupActivity::class.java))
                } else if (fromnotificationActionFragment == "schedulemessage") {
                    startActivity(
                        Intent(
                            requireActivity(), Schedule_Message_Show_Activity::class.java
                        )
                    )
                } else if (fromnotificationActionFragment == "sharemessage") {
                    shareApp(requireContext())
                } else if (fromnotificationActionFragment == "about") {
                    val action = SettingFragmentDirections.actionSettingFragmentToAboutFragment()
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun showCasesettingActivityFirst(i: Int) {
    }


    private fun showfullscreenshowcase() {
    }

    private fun viewscrollandvisible(visibleview: View, scrollview: View, cont: Int) {

    }


    override fun onStart() {
        super.onStart()

            binding.settingAbScroll.visible()

        ThemeSetup()
        makebackgroundcontenar()

        calleridbutton()
        setupSettingNativeTriggerIfNeeded()

    }

    override fun onDestroyView() {
        settingAdHandler.removeCallbacks(settingAdDelayRunnable)
        super.onDestroyView()
    }

    private fun setupSettingNativeTriggerIfNeeded() {
        if (settingNativeAdRequested) {
            return
        }
        binding.settingAb.settingScrollAb.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (!settingNativeAdRequested && scrollY > resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._180sdp)) {
                maybeShowSettingNative("scroll_lower_group")
            }
        }
        settingAdHandler.removeCallbacks(settingAdDelayRunnable)
        settingAdHandler.postDelayed(settingAdDelayRunnable, 25_000L)
    }

    private fun maybeShowSettingNative(trigger: String) {
        if (settingNativeAdRequested) {
            return
        }
        settingNativeAdRequested = true
        settingAdHandler.removeCallbacks(settingAdDelayRunnable)
        AdsOrchestrator.get(requireContext()).showSettingNative(
            activity = requireActivity(),
            ui = AdUiBinding(
                rootContainer = binding.settingAb.nativeAds1,
                adFrame = binding.settingAb.nativeAds
            ),
            trigger = trigger,
            onNoAd = { binding.settingAb.nativeAds1.gone() }
        )
    }

    private fun calleridbutton() {
        with(binding) {
            if (Settings.canDrawOverlays(requireContext())) {
                if ((ContextCompat.checkSelfPermission(
                        requireContext(), android.Manifest.permission.READ_PHONE_STATE
                    ) == PackageManager.PERMISSION_GRANTED)
                ) {
                    if (requireContext().config.SystemTextViewSwitchAbCaller) {
                        settingAb.SystemTextViewSwitchAbCaller.isChecked = true
                    } else {
                        settingAb.SystemTextViewSwitchAbCaller.isChecked = false
                    }
                } else {
                    settingAb.SystemTextViewSwitchAbCaller.isChecked = false
                }
            } else {
                settingAb.SystemTextViewSwitchAbCaller.isChecked = false
            }
        }
    }

    private fun ThemeSetup() {
        applyMaterialSurfaceThemeColors()
        applyMaterialSystemBarColors()
        applyToolbarColors()
//        binding.settingAb.SystemTextViewSwitchAb.SetSwitchColor(0)
//        binding.settingAb.SystemTextViewSwitchAbCaller.SetSwitchColor(0)
//        binding.settingAb.SystemGeneratedIconSwitchAb.SetSwitchColor(0)
//        binding.settingAb.InAppSignatureShowMesageCountAb.SetSwitchColor(0)
//        binding.settingAb.InAppBrowserChangeAb.SetSwitchColor(0)
//        binding.settingAb.InAppBrowserChangeShowMesageCountAb.SetSwitchColor(0)
        applyMaterialSwitchColors()
        setThemeModeSummary()
        applyMaterialSettingIcons()
//        AdsLoadAndShow()

    }

    private fun applyMaterialSurfaceThemeColors() {
        val surfaceColor = requireContext().getProperBackgroundColor()
        val primaryColor = requireContext().getProperPrimaryColor()

        binding.mainBg.setBackgroundColor(surfaceColor)
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)

        binding.settingAb.txtGeneral.setTextColor(primaryColor)
        binding.settingAb.txtConversation.setTextColor(primaryColor)
        binding.settingAb.txtAdvance.setTextColor(primaryColor)
        binding.settingAb.ColorThemeUserCountAb.setTextColor(primaryColor)
        binding.settingAb.ColorThemeimageView17.drawable?.setTint(primaryColor)


    }

    private fun applyMaterialSystemBarColors() {
        val surfaceColor = requireContext().getProperBackgroundColor()
        val statusBarColor = requireContext().getProperStatusBarColor()
        val navigationBarColor = requireContext().getBottomNavigationBackgroundColor()
        val window = requireActivity().window
        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars = ThemeModeManager.shouldUseLightSystemBars(requireContext())
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
    }

    private fun applyToolbarColors() {
        val textColor = requireContext().getProperTextColor()
        val secondaryTextColor = requireContext().getProperSecondaryTextColor()
        binding.backBtn.iconTint = ColorStateList.valueOf(textColor)
        binding.textView3.setTextColor(textColor)
    }

    private fun applyMaterialSwitchColors() {
        val accentColor = requireContext().getProperPrimaryColor()
        val textColor = requireContext().getProperTextColor()
        val onPrimary = accentColor.getContrastColor()
        val outlineColor = textColor.adjustAlpha(0.4f)
        val trackColor = textColor.adjustAlpha(0.2f)



        listOf(
            binding.settingAb.SystemTextViewSwitchAb,
            binding.settingAb.SystemTextViewSwitchAbCaller,
            binding.settingAb.SystemGeneratedIconSwitchAb,
            binding.settingAb.InAppSignatureShowMesageCountAb,
            binding.settingAb.InAppBrowserChangeAb,
            binding.settingAb.InAppBrowserChangeShowMesageCountAb,
        ).forEach { switchView ->

            switchView.apply {

                setTextColor(textColor)

                trackTintList = ColorStateList(
                    arrayOf(
                        intArrayOf(-android.R.attr.state_checked),
                        intArrayOf(android.R.attr.state_checked)
                    ),
                    intArrayOf(trackColor, accentColor)
                )

                thumbTintList = ColorStateList(
                    arrayOf(
                        intArrayOf(-android.R.attr.state_checked),
                        intArrayOf(android.R.attr.state_checked)
                    ),
                    intArrayOf(outlineColor, onPrimary)
                )

                thumbIconTintList = ColorStateList(
                    arrayOf(
                        intArrayOf(-android.R.attr.state_checked),
                        intArrayOf(android.R.attr.state_checked)
                    ),
                    intArrayOf(trackColor, accentColor)
                )

                trackDecorationTintList = ColorStateList(
                    arrayOf(
                        intArrayOf(-android.R.attr.state_checked),
                        intArrayOf(android.R.attr.state_checked)
                    ),
                    intArrayOf(outlineColor, accentColor)
                )

            }

        }


    }

    private fun applyMaterialSettingIcons() = with(binding.settingAb) {
        val iconTintColor = requireContext().getProperSecondaryTextColor()
        icFlNotificationBtnAb.setImageResource(R.drawable.baseline_notifications_24)
        icFlLanguageBtnAb.setImageResource(R.drawable.baseline_g_translate_24)
        icFlColorThemeTxtAb.setImageResource(R.drawable.baseline_color_lens_24)
        icFlThemeColorTxt.setImageResource(R.drawable.baseline_color_lens_24)
        messageTxtSizeThemeTxtAb.setImageResource(R.drawable.baseline_open_in_full_24)

        icFlSystemGeneratedIconSwitchAb.setImageResource(R.drawable.baseline_person_add_alt_24)
        icFlSystemTextViewSwitchAb.setImageResource(R.drawable.baseline_format_list_bulleted_24)
        icFlSystemTextViewSwitchAbCaller.setImageResource(R.drawable.baseline_call_24)

        stepVarificationUserBtnImgAb.setImageResource(R.drawable.baseline_fingerprint_24)
        recylerbinUserBtnImg.setImageResource(R.drawable.baseline_delete_24)

        icFlConversationSwipTxtAb.setImageResource(R.drawable.baseline_reply_24_share)
        icFlSignatureSwipTxtAb.setImageResource(R.drawable.baseline_edit_24)
        icFlAppBrowserTxtAb.setImageResource(R.drawable.baseline_web_24)
        icFlShowCharacterCountAb.setImageResource(R.drawable.baseline_message_24_new)

        imageView16Ab.setImageResource(R.drawable.outline_cancel_24)
        imageView161Ab.setImageResource(R.drawable.ic_lock)
        backupAndRestoreImageView16Ab.setImageResource(R.drawable.baseline_file_upload_24_vector)
        scheduleMessageImageView16Ab.setImageResource(R.drawable.baseline_calendar_month_24)
        deliveryConfirmationimageView16Ab.setImageResource(R.drawable.round_done_all_24_message_deliverd)

        icFlMessageTxtAb.setImageResource(R.drawable.baseline_open_in_full_24)
        icFlMessageCornerAb.setImageResource(R.drawable.baseline_message_24)
        icFlConversationListTxtAb.setImageResource(R.drawable.baseline_format_list_bulleted_24)
        icSendGroupMessageAb.setImageResource(R.drawable.baseline_message_24)
        icSendLongMessageAb.setImageResource(R.drawable.baseline_message_24_new)
        icRemoveAccentsAb.setImageResource(R.drawable.baseline_g_translate_24)
        icFlArchiveBtnAb.setImageResource(R.drawable.baseline_archive_24)
        icFlConversationBtnAb.setImageResource(R.drawable.baseline_format_list_bulleted_24)

        listOf(
            icFlNotificationBtnAb,
            icFlLanguageBtnAb,
            icFlColorThemeTxtAb,
            icFlThemeColorTxt,
            messageTxtSizeThemeTxtAb,
            icFlSystemGeneratedIconSwitchAb,
            icFlSystemTextViewSwitchAb,
            icFlSystemTextViewSwitchAbCaller,
            stepVarificationUserBtnImgAb,
            recylerbinUserBtnImg,
            icFlConversationSwipTxtAb,
            icFlSignatureSwipTxtAb,
            icFlAppBrowserTxtAb,
            icFlShowCharacterCountAb,
            imageView16Ab,
            imageView161Ab,
            backupAndRestoreImageView16Ab,
            scheduleMessageImageView16Ab,
            deliveryConfirmationimageView16Ab,
            icFlMessageTxtAb,
            icFlMessageCornerAb,
            icFlConversationListTxtAb,
            icSendGroupMessageAb,
            icSendLongMessageAb,
            icRemoveAccentsAb,
            icFlArchiveBtnAb,
            icFlConversationBtnAb,
        ).forEach {
            it.imageTintList = ColorStateList.valueOf(iconTintColor)
            it.gone()
        }
    }


    private fun makebackgroundcontenar() {
        val cornerSize = resources.getDimension(com.intuit.sdp.R.dimen._5sdp)
        val strokeWidth = resources.getDimension(com.intuit.sdp.R.dimen._1sdp)
        val itemSpacing = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._6sdp)
        val isSystemMode =
            ThemeModeManager.getThemeMode(requireContext()) == ThemeModeManager.MODE_SYSTEM
        val isDarkTheme = ThemeModeManager.isDarkThemeActive(requireContext())
        val surfaceColor = requireContext().getProperBackgroundColor()
        val optionSurfaceColor =
            requireContext().getProperPrimaryColor().adjustAlpha(0.1f)

        val outlineColor = requireContext().getProperBackgroundColor()

        val shapeAppearanceNormal =
            ShapeAppearanceModel.builder().setAllCornerSizes(cornerSize).build()


        val shapeAppearanceTop = ShapeAppearanceModel.builder()
            .setTopLeftCornerSize(cornerSize * 3)
            .setTopRightCornerSize(cornerSize * 3)
            .setBottomLeftCornerSize(cornerSize)
            .setBottomRightCornerSize(cornerSize).build()

        val shapeAppearanceBottom =
            ShapeAppearanceModel.builder()
                .setTopLeftCornerSize(cornerSize)
                .setTopRightCornerSize(cornerSize)
                .setBottomLeftCornerSize(cornerSize * 3)
                .setBottomRightCornerSize(cornerSize * 3).build()


        fun createOptionSurface(shapeAppearanceModel: ShapeAppearanceModel): MaterialShapeDrawable {
            return MaterialShapeDrawable(shapeAppearanceModel).apply {
                fillColor = ColorStateList.valueOf(optionSurfaceColor)
                setStroke(strokeWidth, outlineColor.takeIf { it != 0 } ?: surfaceColor)
            }
        }

        with(binding.settingAb) {

            settingAbFirstContenar.setCardBackgroundColor(Color.TRANSPARENT)
            settingAbFirstContenar.strokeWidth = 0
            secoundAbContenar.setCardBackgroundColor(Color.TRANSPARENT)
            secoundAbContenar.strokeWidth = 0
            thirdAbContenar.setCardBackgroundColor(Color.TRANSPARENT)
            thirdAbContenar.strokeWidth = 0

            val optionRows = listOf(
                notificationBtnCardAb,
                LanguageBtnCardAb,
                ColorThemeBtnCardAb,
                messageTxtSizeBtnCardAb,
                SystemGeneratedIcon,
                SystemTextView,
                CallerInfoScreen,
                stepVarificationUserBtnCardAb,
                recylerbinUserBtnCard,
                ConversationSwipBtnCardAb,
                conversationSignatureBtnCardShowMesageCountAb,
                conversationOtherSettingBtnCardAb,
                conversationOtherSettingBtnCardShowMesageCountAb,
                blockBtnCardAb,
                privateBtnCardAb,
                backupAndRestoreBtnCardNewAb,
                scheduleMessageBtnCardAb,
                MessageTxtBtnCardAb,
                MessageCornerBtnCardAb,
                ConversationListBtnCardAb,
                conversationCardSendLongMessageAb,
                conversationCardSendGroupMessageAb,
                conversationCardRemoveAccentsAb,
                archiveBtnCardAb,
                conversationBtnCardAb,
                deliveryConfirmationBtnCardAb,
            )

            optionRows.forEach { row ->

                row.background = createOptionBackground(
                    cornerSize = cornerSize,
                    fillColor = optionSurfaceColor,
                    strokeWidth = strokeWidth,
                    strokeColor = outlineColor.takeIf { it != 0 } ?: surfaceColor,
                    showRipple = true,
                    rippleColor = requireContext().getProperPrimaryColor().adjustAlpha(0.3f),
                    isTop = row.id == R.id.notification_btn_card_ab || row.id == R.id.Conversation_Swip_btn_card_ab || row.id == R.id.block_btn_card_ab,
                    isBottom = row.id == R.id.recylerbin_user_btn_card || row.id == R.id.conversation_other_setting_btn_card_show_mesage_count_ab || row.id == R.id.schedule_message_btn_card_ab,
                )


                row.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    bottomMargin = itemSpacing
                }
            }
        }
    }


}
