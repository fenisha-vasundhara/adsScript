package com.messenger.phone.number.text.sms.service.apps.fragment

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.app.AlarmManager
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.demo.adsmanage.Commen.firebaseEventMain
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.messenger.phone.number.text.sms.service.apps.BlockManageActivity
import com.messenger.phone.number.text.sms.service.apps.BlockNumberActivity
import com.messenger.phone.number.text.sms.service.apps.CommanClass.SetSwitchColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.cancelOtpdeletePendingIntent
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createCustomDrawable
import com.messenger.phone.number.text.sms.service.apps.CommanClass.firebaseEvent
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isValidEmail

import com.messenger.phone.number.text.sms.service.apps.CommanClass.notificationallbuttonshowcaseshow
import com.messenger.phone.number.text.sms.service.apps.CommanClass.openRequestExactAlarmSettings
import com.messenger.phone.number.text.sms.service.apps.CommanClass.otpoutodelete
import com.messenger.phone.number.text.sms.service.apps.CommanClass.privacyallbuttonshowcaseshow
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.showsettingshowcase
import com.messenger.phone.number.text.sms.service.apps.CommanClass.showsettingshowcaseprivacy
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.Dialog.Delete_Old_Message_Dialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.PermissionRequiredDialog
import com.messenger.phone.number.text.sms.service.apps.LockScreenActivity
import com.messenger.phone.number.text.sms.service.apps.LockScreenPinSetActivity
import com.messenger.phone.number.text.sms.service.apps.LockScreenSettingActivity
import com.messenger.phone.number.text.sms.service.apps.LockScreenSetupActivity
import com.messenger.phone.number.text.sms.service.apps.ManageBlockedKeywordsActivity
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.ShowCase.DismissType
import com.messenger.phone.number.text.sms.service.apps.ShowCase.GuideView
import com.messenger.phone.number.text.sms.service.apps.ShowCase.PointerType
import com.messenger.phone.number.text.sms.service.apps.data.Config
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentPrivacyandsecurityBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetAllConversationBlockViewModel
import com.simplemobiletools.commons.helpers.isSPlus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PrivacyandsecurityFragment : Fragment() {

    private lateinit var config: Config
    lateinit var binding: FragmentPrivacyandsecurityBinding
    lateinit var model: GetAllConversationBlockViewModel


    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f


    @Inject
    lateinit var repo: MessagerDatabaseRepo

    @Inject
    lateinit var deleteOldMessageDialog: Delete_Old_Message_Dialog

    private var builder: GuideView.Builder? = null
    private var mGuideView: GuideView? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        requireContext().setLocal()
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_privacyandsecurity,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        config = requireContext().config

        config.isprivacyandsecscreenopen = true


        requireContext().config.fragmentshowcasecout = 11
        with(binding) {
            requireActivity().firebaseEventMain("Privacy_and_security")
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

            autoMessageTextView8.isSelected = true
            textView9keywords.isSelected = true
            setPasswordResetEmailBtnTxtAb.isSelected = true
            otpAutoDelete.isChecked = config.isotpdeleteset
            maliciousWebsiteBtnSwich.isChecked = config.maliciousWebsiteBtnSwichP

            autoMessageTextView8Ab.isSelected = true
            textView9keywordsAb.isSelected = true
            otpAutoDeleteAb.isChecked = config.isotpdeleteset
            maliciousWebsiteBtnSwichAb.isChecked = config.maliciousWebsiteBtnSwichP

            endToEndEncodeSwitch.isChecked = config.isendtoendencrtepted
            textView8.isSelected = true
            maliciousWebsitekeywords.isSelected = true

            endToEndEncodeSwitchAb.isChecked = config.isendtoendencrtepted
            textView8Ab.isSelected = true
            maliciousWebsitekeywordsAb.isSelected = true


            if (requireContext().config.SelectedLanguage == "ar") {
                info10.gravity = Gravity.END
                autoMessageTextView8.gravity = Gravity.END
                textView99Btn.gravity = Gravity.END
                setPasswordResetEmailBtnTxt.gravity = Gravity.END
                maliciousWebsitekeywords.gravity = Gravity.END

                info10Ab.gravity = Gravity.END
                autoMessageTextView8Ab.gravity = Gravity.END
                textView99BtnAb.gravity = Gravity.END
                setPasswordResetEmailBtnTxtAb.gravity = Gravity.END
                maliciousWebsitekeywordsAb.gravity = Gravity.END

            } else {
                info10.gravity = Gravity.START
                autoMessageTextView8.gravity = Gravity.START
                setPasswordResetEmailBtnTxt.gravity = Gravity.START
                textView99Btn.gravity = Gravity.START
                maliciousWebsitekeywords.gravity = Gravity.START

                info10Ab.gravity = Gravity.START
                autoMessageTextView8Ab.gravity = Gravity.START
                setPasswordResetEmailBtnTxtAb.gravity = Gravity.START
                textView99BtnAb.gravity = Gravity.START
                maliciousWebsitekeywordsAb.gravity = Gravity.START
            }

            otpAutoDelete.setOnCheckedChangeListener { _, isChecked ->
                requireActivity().firebaseEvent("Privacy and security", "OTP Auto Delete")
                if (isSPlus()) {
                    val alarmManager: AlarmManager =
                        requireActivity().getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
                    if (alarmManager.canScheduleExactAlarms()) {
                        setOtpDelete(isChecked)
                    } else {
                        setOtpDelete(false)
                        PermissionRequiredDialog(
                            activity = requireActivity(),
                            textId = R.string.allow_alarm_scheduled_messages,
                            positiveActionCallback = {
                                requireActivity().openRequestExactAlarmSettings("com.messenger.phone.number.text.sms.service.apps")
                            },
                        )
                    }
                } else {
                    setOtpDelete(isChecked)
                }


            }

            otpAutoDeleteAb.setOnCheckedChangeListener { _, isChecked ->
                requireActivity().firebaseEvent("Privacy and security", "OTP Auto Delete")
                if (isSPlus()) {
                    val alarmManager: AlarmManager =
                        requireActivity().getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
                    if (alarmManager.canScheduleExactAlarms()) {
                        setOtpDelete(isChecked)
                    } else {
                        setOtpDelete(false)
                        PermissionRequiredDialog(
                            activity = requireActivity(),
                            textId = R.string.allow_alarm_scheduled_messages,
                            positiveActionCallback = {
                                requireActivity().openRequestExactAlarmSettings("com.messenger.phone.number.text.sms.service.apps")
                            },
                        )
                    }
                } else {
                    setOtpDelete(isChecked)
                }


            }


            endToEndEncodeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                config.isendtoendencrtepted = isChecked
            }

            maliciousWebsiteBtnSwich.setOnCheckedChangeListener { buttonView, isChecked ->
                requireActivity().firebaseEvent("Privacy and security", "Malicious Website")
                config.maliciousWebsiteBtnSwichP = isChecked
            }

            binding.messageDeleteTime.text = requireContext().config.userpreferenceAutoMessageDelete

            deleteOldMessageDialog.dialogdismiss = {
                binding.messageDeleteTime.text = it
                binding.messageDeleteTimeAb.text = it
                onStart()
            }


            endToEndEncodeSwitchAb.setOnCheckedChangeListener { buttonView, isChecked ->
                config.isendtoendencrtepted = isChecked
            }

            maliciousWebsiteBtnSwichAb.setOnCheckedChangeListener { buttonView, isChecked ->
                requireActivity().firebaseEvent("Privacy and security", "Malicious Website")
                config.maliciousWebsiteBtnSwichP = isChecked
            }

            binding.messageDeleteTimeAb.text =
                requireContext().config.userpreferenceAutoMessageDelete






            blockedUserBtnCard.setOnClickListener {
                startActivity(Intent(requireActivity(), BlockManageActivity::class.java))
            }

            backBtn.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            privacyChatUserBtnCard.setOnClickListener {
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



            blockedUserBtnCardAb.setOnClickListener {
                requireActivity().firebaseEvent("Privacy and security", "Blocked User")
                startActivity(Intent(requireActivity(), BlockManageActivity::class.java))
            }

            backBtn.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            privacyChatUserBtnCardAb.setOnClickListener {
                requireActivity().firebaseEvent("Privacy and security", "Privacy Chat User")
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





            stepVarificationUserBtnCard.setOnClickListener {
                startActivity(
                    Intent(
                        requireActivity(),
                        LockScreenSetupActivity::class.java
                    ).putExtra("comefrom", 2)
                )
            }
            autoMessageBtnCard.setOnClickListener {
                deleteOldMessageDialog.show(
                    requireActivity().supportFragmentManager,
                    "deleteOldMessageDialog"
                )
            }


            stepVarificationUserBtnCardAb.setOnClickListener {
                startActivity(
                    Intent(
                        requireActivity(),
                        LockScreenSetupActivity::class.java
                    ).putExtra("comefrom", 2)
                )
            }
            autoMessageBtnCardAb.setOnClickListener {
                requireActivity().firebaseEvent("Privacy and security", "Auto Message Delete")
                deleteOldMessageDialog.show(
                    requireActivity().supportFragmentManager,
                    "deleteOldMessageDialog"
                )
            }


            blockedKeywordsBtnCard.setOnClickListener {
                startActivity(Intent(requireActivity(), ManageBlockedKeywordsActivity::class.java))
            }

            setPasswordResetEmail.setOnClickListener {
                openEditDialog()
            }


            blockedKeywordsBtnCardAb.setOnClickListener {
                startActivity(Intent(requireActivity(), ManageBlockedKeywordsActivity::class.java))
            }

            setPasswordResetEmailAb.setOnClickListener {
                openEditDialog()
            }

        }

        model = ViewModelProvider(requireActivity())[GetAllConversationBlockViewModel::class.java]

        model.GetAllConversationlivelist.observe(requireActivity(), Observer {
            try {
                binding.blockUserCount.text = it.distinctBy { it.threadId }.size.toString()
                binding.blockUserCountAb.text = it.distinctBy { it.threadId }.size.toString()
            } catch (_: Exception) {
            }
        })

        if (privacyallbuttonshowcaseshow) {
//            showCasesettingActivitynotificationFirst()
        } else if (requireActivity().config.thirdshowcaseshowforprivacyandsecurity) {
//            showCasesettingActivitynotificationFirst()
        }

    }

    private fun setOtpDelete(isChecked: Boolean) {
        config.isotpdeleteset = isChecked
        binding.otpAutoDelete.isChecked = isChecked
        binding.otpAutoDeleteAb.isChecked = isChecked
        if (isChecked) {
            requireActivity().otpoutodelete()
        } else {
            requireActivity().cancelOtpdeletePendingIntent()
        }
    }

    private fun showCasesettingActivitynotificationFirst() {


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
                        builder?.setTargetView(binding.autoMessageBtnCard)
                            ?.setTitle("2")
                            ?.setContentText("2")
                    }

                    R.id.auto_message_btn_card -> {
                        builder?.setTargetView(binding.blockedUserBtnCard)
                            ?.setTitle("3")
                            ?.setContentText("3")
                    }

                    R.id.blocked_user_btn_card -> {
                        builder?.setTargetView(binding.blockedKeywordsBtnCard)
                            ?.setTitle("4")
                            ?.setContentText("4")
                    }

                    R.id.blocked_keywords_btn_card -> {
                        builder?.setTargetView(binding.privacyChatUserBtnCard)
                            ?.setTitle("5")
                            ?.setContentText("5")
                    }

                    R.id.privacy_chat_user_btn_card -> {
                        builder?.setTargetView(binding.stepVarificationUserBtnCard)
                            ?.setTitle("6")
                            ?.setContentText("6")
                    }

                    R.id.step_varification_user_btn_card -> {
                        showsettingshowcaseprivacy = false
                        requireActivity().config.thirdshowcaseshowforprivacyandsecurity = false
//                        requireActivity().config.savethidshowcaseforprivacyOnce()
                        return@setGuideListener
                    }
                }
                mGuideView = builder?.build()
                mGuideView?.show()
            }

        mGuideView = builder?.build()
        mGuideView?.show()

    }

    private fun openEditDialog() {
        onStart()
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_edit_text, null)
        val editText = dialogView.findViewById<TextInputEditText>(R.id.editText)
        val textInputLayout = dialogView.findViewById<TextInputLayout>(R.id.textInputLayout)
        editText.setText(requireActivity().config.Password_reset_email_id)
        textInputLayout.setHint(R.string.Enter_Email_Address)
        builder.setCancelable(false)
            .setTitle(R.string.password_reset_email)
            .setPositiveButton(
                android.R.string.ok,
                null
            ) // Set to null to override the default click behavior
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton("OK") { dialog, which ->

            }
            .setNegativeButton("Cancel") { dialog, which ->
                onStart()
                dialog.cancel()
            }

        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.show()

        dialog.getButton(BUTTON_POSITIVE).setOnClickListener {
            val text = editText.getText().toString()
            if (text.trim().isEmpty()) {
                requireContext().toastMess(resources.getString(R.string.Please_enter_your_email))
            } else {
                if (isValidEmail(text)) {
                    CoroutineScope(Dispatchers.IO).launch {
                        requireActivity().config.Password_reset_email_id = text
                        CoroutineScope(Dispatchers.Main).launch {
                            requireContext().toastMess(
                                resources.getString(R.string.Email_saved_successfully)
                            )
                        }
                    }
                    dialog.dismiss()
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        requireContext().toastMess(
                            resources.getString(
                                R.string.Please_enter_valid_email
                            )
                        )
                    }
                }
            }
            onStart()
        }
        onStart()
    }


    override fun onStart() {
        super.onStart()
        binding.otpAutoDeleteAb.SetSwitchColor(0)
        binding.maliciousWebsiteBtnSwichAb.SetSwitchColor(0)
        if (requireContext().config.setABHomeActivityPref == "1") {
            binding.fistContenar.visible()
            binding.fistContenarAb.gone()
        } else {
            binding.fistContenar.gone()
            binding.fistContenarAb.visible()
            if (requireContext().config.activeThemeSelection == 1) {
                binding.childFirstContenar.background = requireActivity().resources.getDrawable(R.drawable.child_first_contenar_background)
                binding.childSecoundContenar.background = requireActivity().resources.getDrawable(R.drawable.child_first_contenar_background)
            } else if (requireContext().config.activeThemeSelection == 2) {
                binding.childFirstContenar.background = requireActivity().resources.getDrawable(R.drawable.child_first_contenar_background_2)
                binding.childSecoundContenar.background = requireActivity().resources.getDrawable(R.drawable.child_first_contenar_background_2)
            } else if (requireContext().config.activeThemeSelection == 3) {
                binding.childFirstContenar.background = requireActivity().resources.getDrawable(R.drawable.child_first_contenar_background_3)
                binding.childSecoundContenar.background = requireActivity().resources.getDrawable(R.drawable.child_first_contenar_background_3)
            } else {
                binding.childFirstContenar.background = requireActivity().resources.getDrawable(R.drawable.child_first_contenar_background)
                binding.childSecoundContenar.background = requireActivity().resources.getDrawable(R.drawable.child_first_contenar_background)
            }
        }


    }
}