package com.messenger.phone.number.text.sms.service.apps

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.demo.adsmanage.Commen.firebaseEventMain
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.messenger.phone.number.text.sms.service.apps.ApplicationClass.GmailMailSend
import com.messenger.phone.number.text.sms.service.apps.CommanClass.AutoFullAppLockRemove
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createOptionBackground
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getBottomNavigationBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperStatusBarColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isOnline
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isValidEmail
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.CommanClass.updateStatusbarColorFullApp
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.adapter.CustomSpinnerAdapter
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityLockSceenSecQuestionBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.SpinnerModel
import com.simplemobiletools.commons.extensions.adjustAlpha
import com.simplemobiletools.commons.extensions.getContrastColor
import com.simplemobiletools.commons.extensions.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class LockSceenSecQuestionActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var appopen: Boolean = false

    @Inject
    lateinit var gmailMailSend: GmailMailSend

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    private var pos: Int = -1
    lateinit var binding: ActivityLockSceenSecQuestionBinding
    var comefrom: Int = -1
    var password: String = ""
    var selectionpos = -1
    var selectedquestions: String = ""
    var selectedquestionsand: String = ""
    var forpasswordforgot = false
    var Securityquestionslang: ArrayList<SpinnerModel> = arrayListOf()
    var Securityquestions =
        arrayOf(
            "Select any Security Question",
            "Which is your favorite movies ?",
            "Which is your favourite food ?",
            "Which is your favourite actress ?",
            "What’s your lucky number ?",
            "In which city were you born ?",
        )

    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_sceen_sec_question)
        this.firebaseEventMain("Lock_Security_Question")
        setBaseTheme(binding.vAnd15StatusBar)

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


        Securityquestionslang.add(
            SpinnerModel(
                this.resources.getString(R.string.select_any_security_question),
                false
            )
        )
        Securityquestionslang.add(
            SpinnerModel(
                this.resources.getString(R.string.which_is_your_favorite_movies),
                true
            )
        )
        Securityquestionslang.add(
            SpinnerModel(
                this.resources.getString(R.string.which_is_your_favourite_food),
                true
            )
        )
        Securityquestionslang.add(
            SpinnerModel(
                this.resources.getString(R.string.which_is_your_favourite_actress),
                true
            )
        )
        Securityquestionslang.add(
            SpinnerModel(
                this.resources.getString(R.string.what_your_lucky_number),
                true
            )
        )
        Securityquestionslang.add(
            SpinnerModel(
                this.resources.getString(R.string.in_which_city_were_you_born),
                true
            )
        )



        comefrom = intent.getIntExtra("comefrom", -1)
        password = intent.getStringExtra("password").toString()

        forpasswordforgot = intent.getBooleanExtra("forpasswordforgot", false)
        appopen = intent.getBooleanExtra("appopen", false)

        if (forpasswordforgot) {
            binding.arrorw.gone()
        } else {
            binding.arrorw.visible()
        }

        if (!forpasswordforgot) {
            if (comefrom == 1) {
                binding.textView3.text = resources.getString(R.string.Privacy_chat)
            } else if (comefrom == 2) {
                binding.textView3.text = resources.getString(R.string.Two_new_step_Verification)
            }
            binding.forgotpassusingemail.gone()
        } else {
            binding.forgotpassusingemail.visible()
            binding.enterNewpasstxt.text = resources.getString(R.string.change_the_password)
        }

        binding.newpasswordtxt.onItemSelectedListener = this;
//        binding.newpasswordtxt.setPopupBackgroundDrawable(resources.getDrawable(R.drawable.ic_arrow_down_vector))
        val adapter = CustomSpinnerAdapter(this, ArrayList(Securityquestionslang))
        binding.newpasswordtxt.adapter = adapter


        if (forpasswordforgot) {
            if (comefrom == 1) {
                pos = Securityquestions.indexOf(config.Lock_Screen_Sec_Question)
                binding.newpasswordtxt.setSelection(pos)
                binding.newpasswordtxt.isEnabled = false;
                binding.newpasswordtxt.isClickable = false;
            } else if (comefrom == 2) {
                pos = Securityquestions.indexOf(config.Full_AppLock_Sec_Question)
                binding.newpasswordtxt.setSelection(pos)
                binding.newpasswordtxt.isEnabled = false;
                binding.newpasswordtxt.isClickable = false;
            }
        }

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.savebtn.setOnClickListener {
            selectedquestionsand = binding.newpasswordtxt2.text.toString()
            if (forpasswordforgot) {
                if (comefrom == 1) {
                    if (selectedquestionsand == config.Lock_Screen_Sec_Question_Ans) {
                        startActivity(
                            Intent(
                                this,
                                LockScreenPinSetActivity::class.java
                            ).putExtra("pinsetfor", 1).putExtra("forpasswordforgot", true)
                        )
                        finish()
                    } else {
                        if (binding.newpasswordtxt2.text.isEmpty()) {
                            binding.newpasswordtxt2.error =
                                resources.getString(R.string.Please_enter_Answer)
                        } else {
                            binding.newpasswordtxt2.error =
                                resources.getString(R.string.Wrong_Answer)
                        }
                    }
                } else if (comefrom == 2) {
                    if (selectedquestionsand == config.Full_AppLock_Sec_Question_Ans) {
                        startActivity(
                            Intent(
                                this,
                                LockScreenPinSetActivity::class.java
                            ).putExtra("pinsetfor", 2).putExtra("forpasswordforgot", true)
                        )
                        finish()
                    } else {
                        if (binding.newpasswordtxt2.text.isEmpty()) {
                            binding.newpasswordtxt2.error =
                                resources.getString(R.string.Please_enter_Answer)
                        } else {
                            binding.newpasswordtxt2.error =
                                resources.getString(R.string.Wrong_Answer)
                        }
                    }
                }
            } else {
                if (selectionpos == 0) {
                    binding.arrorw.setImageResource(R.drawable.baseline_error_outline_24)
                    toastMess(resources.getString(R.string.Select_any_question))
                } else {
                    if (config.Password_reset_email_id == "") {
                        openEditDialog()
                    } else {
                        nextprocessintent()
                    }
                }

//                if (selectedquestionsand.isEmpty()) {
//                    binding.newpasswordtxt2.error = "Please enter Answer"
//                } else {
//                    if (selectionpos == 0) {
//                        binding.arrorw.setImageResource(R.drawable.baseline_error_outline_24)
//                        toastMess("Select any question")
//                    } else {
//                        when (comefrom) {
//                            1 -> {
//                                config.Lock_Screen_Sec_Question = selectedquestions
//                                config.Lock_Screen_Pin = password
//                                config.Lock_Screen_Sec_Question_Ans = selectedquestionsand
//
//                                val intent = Intent(this@LockSceenSecQuestionActivity, LockScreenActivity::class.java)
//                                    .putExtra("lockype", 2)
//                                    .putExtra("comefrom", comefrom)
//                                startActivity(intent)
//                                this@LockSceenSecQuestionActivity.finish()
//
//
//                            }
//
//                            2 -> {
//
//                                startActivity(
//                                    Intent(this, LockScreenSetupActivity::class.java)
//                                        .putExtra("fullappsetupdone", true)
//                                )
//                                this@LockSceenSecQuestionActivity.finish()
//
//                                config.Full_AppLock_Sec_Question = selectedquestions
//                                config.Full_AppLock_Pin = password
//                                config.Full_AppLock_Sec_Question_Ans = selectedquestionsand
//                            }
//
//                            else -> {
//                            }
//                        }
//                    }
//                }
            }
        }

//        binding.newpasswordtxt.setSelection(3)

        binding.forgotpassusingemail.setOnClickListener {

            if (isOnline()) {
                if (config.Password_reset_email_id == "") {
                    toastMess(resources.getString(R.string.first_set_email_address))
                } else {
                    if (isValidEmail(config.Password_reset_email_id)) {
                        showProgressDialog(this, resources.getString(R.string.please_wait))
                        GlobalScope.launch {
                            gmailMailSend.sendEmail(
                                resources.getString(R.string.password_reset_email_lang),
                                config.Password_reset_email_id,
                                if (comefrom == 1) {
                                    config.Lock_Screen_Pin
                                } else {
                                    config.Full_AppLock_Pin
                                },
                                resources.getString(R.string.Please_remember_to_keep),
                                resources.getString(R.string.tw_Messages),
                                resources.getString(R.string.contact_us_at),
                                resources.getString(R.string.Your_password_has_been),
                                resources.getString(R.string.hello_ms),
                                resources.getString(R.string.Your_Password)
                            )
                        }
                    } else {
                        toastMess(resources.getString(R.string.Email_Address_Not))
                    }
                }
            } else {
                toast(resources.getString(R.string.Please_turn_on))
            }
        }

        gmailMailSend.maillistener = { done ->
            dismissProgressDialog()
            if (done) {
                onBackPressed()
            }
        }


    }

    private fun nextprocessintent() {
        if (selectedquestionsand.isEmpty()) {
            binding.newpasswordtxt2.error = resources.getString(R.string.Please_enter_Answer)
        } else {
            when (comefrom) {
                1 -> {
                    config.isprivatelocktype = 1
                    config.Lock_Screen_Sec_Question = selectedquestions
                    config.Lock_Screen_Pin = password
                    config.Lock_Screen_Sec_Question_Ans = selectedquestionsand

                    val intent =
                        Intent(this@LockSceenSecQuestionActivity, LockScreenActivity::class.java)
                            .putExtra("lockype", 2)
                            .putExtra("comefrom", comefrom)
                    startActivity(intent)
                    this@LockSceenSecQuestionActivity.finish()
                }

                2 -> {
                    val intervalMillis = 3 * 24 * 60 * 60 * 1000L
                    AutoFullAppLockRemove(intervalMillis)
                    config.is2steplocktype = 1
                    startActivity(
                        Intent(this, LockScreenSetupActivity::class.java)
                            .putExtra("fullappsetupdone", true)
                    )
                    this@LockSceenSecQuestionActivity.finish()
                    config.Full_AppLock_Sec_Question = selectedquestions
                    config.Full_AppLock_Pin = password
                    config.Full_AppLock_Sec_Question_Ans = selectedquestionsand
                }

                else -> {
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        binding.arrorw.setImageResource(R.drawable.ic_arrow_down_vector_new_black)
        selectionpos = position
        if (forpasswordforgot) {
            binding.exampleShow.gone()
        } else {
            when (selectionpos) {
                0 -> {
                    binding.exampleShow.gone()
                }

                1 -> {
                    binding.exampleShow.visible()
                    binding.exampleShow.text = resources.getString(R.string.e_g_idiot)
                }

                2 -> {
                    binding.exampleShow.visible()
                    binding.exampleShow.text = resources.getString(R.string.e_g_pizza)
                }

                3 -> {
                    binding.exampleShow.visible()
                    binding.exampleShow.text = resources.getString(R.string.e_g_samantha)
                }

                4 -> {
                    binding.exampleShow.visible()
                    binding.exampleShow.text = resources.getString(R.string.e_g_12)
                }

                5 -> {
                    binding.exampleShow.visible()
                    binding.exampleShow.text = resources.getString(R.string.e_g_surat)
                }
            }
        }
        selectedquestions = Securityquestions[position]
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (comefrom == 2) {
            if (appopen) {
                finishAffinity()
            } else {
                finish()
            }
        } else {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        ThemeSetup()
    }

    private fun ThemeSetup() {
//        updateStatusbarColorFullApp()


        val surfaceColor = getProperBackgroundColor()
        val statusBarColor = getProperStatusBarColor()
        val navigationBarColor = getBottomNavigationBackgroundColor()
        val textColor = getProperTextColor()
        val secondaryTextColor = textColor.adjustAlpha(0.72f)
        val primaryColor = getProperPrimaryColor()
        val inputFillColor = primaryColor.adjustAlpha(0.15f)
        val inputHintColor = textColor.adjustAlpha(0.54f)

        binding.root.setBackgroundColor(surfaceColor)
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        binding.textView3.setTextColor(textColor)
        binding.backBtn.iconTint = ColorStateList.valueOf(textColor)

        binding.enterNewpasstxt.setTextColor(textColor)
        binding.forgotpassusingemail.setTextColor(primaryColor)
        binding.arrorw.drawable.setTint(textColor)


        binding.cardView3.setCardBackgroundColor(inputFillColor)
        binding.cardView4.setCardBackgroundColor(inputFillColor)
        binding.newpasswordtxt.setBackgroundColor(inputFillColor)
        binding.newpasswordtxt2.setTextColor(textColor)
        binding.newpasswordtxt2.setHintTextColor(inputHintColor)

        binding.savebtn.background = createOptionBackground(
            cornerSize = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._50sdp).toFloat(),
            fillColor = primaryColor,
            strokeColor = primaryColor,
            strokeWidth = 0f,
            showRipple = true,
            rippleColor = Color.WHITE.adjustAlpha(0.3f)
        )

        binding.savebtntxt.setTextColor(primaryColor.getContrastColor())


        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars = ThemeModeManager.shouldUseLightSystemBars(this@LockSceenSecQuestionActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
    }

    fun showProgressDialog(context: Context, message: String) {
        progressDialog?.dismiss() // Dismiss any existing dialog
        progressDialog = ProgressDialog(context)
        progressDialog?.setMessage(message)
        progressDialog?.setCancelable(false)
        progressDialog?.setCanceledOnTouchOutside(false)
        progressDialog?.show()
    }

    fun dismissProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    private fun openEditDialog() {
        val builder = MaterialAlertDialogBuilder(this)
        val inflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_edit_text, null)
        val editText = dialogView.findViewById<TextInputEditText>(R.id.editText)
        val textInputLayout = dialogView.findViewById<TextInputLayout>(R.id.textInputLayout)
        editText.setText(config.Password_reset_email_id)
        textInputLayout.setHint(R.string.Enter_Email_Address)
        builder.setCancelable(false)
            .setTitle(R.string.password_reset_email)
            .setPositiveButton(
                android.R.string.ok,
                null
            ) // Set to null to override the default click behavior
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, which -> }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                dialog.cancel()
                nextprocessintent()
            }

        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.show()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(getProperPrimaryColor())
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(getProperTextColor())
        }

                // Apply button colors

                // Ensure initial selection is visually updated

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val text = editText.getText().toString()
            if (text.trim().isEmpty()) {
                toastMess(resources.getString(R.string.Please_enter_your_email))
            } else {
                if (isValidEmail(text)) {
                    CoroutineScope(Dispatchers.IO).launch {
                        config.Password_reset_email_id = text
                        CoroutineScope(Dispatchers.Main).launch { toastMess(resources.getString(R.string.Email_saved_successfully)) }
                    }
                    dialog.dismiss()
                    nextprocessintent()
                } else {
                    CoroutineScope(Dispatchers.Main).launch { toastMess(resources.getString(R.string.Please_enter_valid_email)) }
                }
            }
        }
    }
}