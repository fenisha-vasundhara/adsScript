package com.messenger.phone.number.text.sms.service.apps

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.demo.adsmanage.Commen.firebaseEventMain
import com.messenger.phone.number.text.sms.service.apps.ApplicationClass.MessagerApplication
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createCustomDrawable
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createOptionBackground
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getBottomNavigationBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperStatusBarColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.mainactivityfinish
import com.messenger.phone.number.text.sms.service.apps.CommanClass.mobileNumberLock
import com.messenger.phone.number.text.sms.service.apps.CommanClass.nameLock
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.thredidLock
import com.messenger.phone.number.text.sms.service.apps.CommanClass.updateStatusbarColorForLockScreen
import com.messenger.phone.number.text.sms.service.apps.LanguageActivity.Companion.isappopenlock
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityLockScreenBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.KeybordPrivacyClick
import com.messenger.phone.number.text.sms.service.apps.lockscreen.LockKeyBordAdapter
import com.messenger.phone.number.text.sms.service.apps.lockscreen.OnOtpCompletionListener
import com.messenger.phone.number.text.sms.service.apps.modelClass.KeybordModelClass
import com.simplemobiletools.commons.extensions.adjustAlpha
import com.simplemobiletools.commons.extensions.getProperBackgroundColor
import com.simplemobiletools.commons.extensions.getProperPrimaryColor
import com.simplemobiletools.commons.extensions.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class LockScreenActivity : AppCompatActivity(), KeybordPrivacyClick, OnOtpCompletionListener {
    companion object {
        const val EXTRA_RETURN_RESULT = "extra_return_result"
    }


    lateinit var binding: ActivityLockScreenBinding


    private lateinit var mobileNumber: String
    private var tredid: Long = 0L
    private lateinit var name: String

    var keybordlist: ArrayList<KeybordModelClass> = arrayListOf()

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    var demopass = "123456"
    var locktype = -1
    var comefrom = -1
    var appopen = false
    var ispasswordchange = false
    var fromnotification = false
    var openprivatechat = false
    var gotingprivatechat = false
    private var shouldReturnResult = false

    @Inject
    lateinit var adapterkeybord: LockKeyBordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_screen)
        this.firebaseEventMain("Lock")
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
        ThemeSetup()
        setOTPView()

        keybordlist.add(KeybordModelClass("1", false, R.drawable.baseline_backspace_24, 0))
        keybordlist.add(KeybordModelClass("2", false, R.drawable.baseline_backspace_24, 0))
        keybordlist.add(KeybordModelClass("3", false, R.drawable.baseline_backspace_24, 0))
        keybordlist.add(KeybordModelClass("4", false, R.drawable.baseline_backspace_24, 0))
        keybordlist.add(KeybordModelClass("5", false, R.drawable.baseline_backspace_24, 0))
        keybordlist.add(KeybordModelClass("6", false, R.drawable.baseline_backspace_24, 0))
        keybordlist.add(KeybordModelClass("7", false, R.drawable.baseline_backspace_24, 0))
        keybordlist.add(KeybordModelClass("8", false, R.drawable.baseline_backspace_24, 0))
        keybordlist.add(KeybordModelClass("9", false, R.drawable.baseline_backspace_24, 0))
        keybordlist.add(
            if (isBiometricSupported()) {
                KeybordModelClass(
                    "", true, if (config.activeThemeSelection == 1) {
                        R.drawable.fingerprint_lock_screen_btn
                    } else if (config.activeThemeSelection == 2) {
                        R.drawable.fingerprint_lock_screen_btn_dark
                    } else if (config.activeThemeSelection == 3) {
                        R.drawable.fingerprint_lock_screen_btn_night
                    } else {
                        R.drawable.fingerprint_lock_screen_btn_dark
                    }, 10
                )
            } else {
                KeybordModelClass("-1", false, R.drawable.baseline_backspace_24, -1)
            }
        )
        keybordlist.add(KeybordModelClass("0", false, R.drawable.baseline_backspace_24, 0))
        keybordlist.add(
            KeybordModelClass(
                "", true, if (config.activeThemeSelection == 1) {
                    R.drawable.backspace_lock_screen_btn
                } else if (config.activeThemeSelection == 2) {
                    R.drawable.backspace_lock_screen_btn_dark
                } else if (config.activeThemeSelection == 3) {
                    R.drawable.backspace_lock_screen_btn_night
                } else {
                    R.drawable.backspace_lock_screen_btn_dark
                }, 20
            )
        )

//        KeybordModelClass("", true, R.drawable.fingerprint_lock_screen_btn, 10)
        name = intent.getStringExtra("name") ?: ""
        mobileNumber = intent.getStringExtra("mobileNumber") ?: ""
        tredid = intent.getLongExtra("tredid", 0L)
        locktype = intent.getIntExtra("lockype", -1)
        comefrom = intent.getIntExtra("comefrom", -1)
        appopen = intent.getBooleanExtra("appopen", false)
        ispasswordchange = intent.getBooleanExtra("ispasswordchange", false)
        fromnotification = intent.getBooleanExtra("fromnotification", false)
        openprivatechat = intent.getBooleanExtra("openprivatechat", false)
        gotingprivatechat = intent.getBooleanExtra("gotingprivatechat", false)
        shouldReturnResult = intent.getBooleanExtra(EXTRA_RETURN_RESULT, false)

        if (comefrom == 1) {
            demopass = config.Lock_Screen_Pin
            binding.whatlock.text = resources.getString(R.string.Privacy_chat)
        } else if (comefrom == 2) {
            demopass = config.Full_AppLock_Pin
            binding.whatlock.text = resources.getString(R.string.Two_new_step_Verification)
        }


        with(binding) {
            adapterkeybord.setHasStableIds(true)
            keybordadapter = adapterkeybord
            adapterkeybord.setLeyInterface(this@LockScreenActivity)
            adapterkeybord.buttonlist = keybordlist
            otpView!!.setOtpCompletionListener(this@LockScreenActivity)
            forgotpass.setOnClickListener {

                when (comefrom) {
                    1 -> {
                        if (config.isprivatelocktype == 2) {
                            startActivity(
                                Intent(this@LockScreenActivity, PattenActivity::class.java)
                                    .putExtra("comefrom", comefrom)
                                    .putExtra("forpasswordforgot", true)
                                    .putExtra("appopen", appopen)
                            )
                        } else {
                            startActivity(
                                Intent(
                                    this@LockScreenActivity,
                                    LockSceenSecQuestionActivity::class.java
                                )
                                    .putExtra("comefrom", comefrom)
                                    .putExtra("forpasswordforgot", true)
                                    .putExtra("appopen", appopen)
                            )
                        }
                    }

                    2 -> {
                        if (config.is2steplocktype == 2) {
                            startActivity(
                                Intent(this@LockScreenActivity, PattenActivity::class.java)
                                    .putExtra("comefrom", comefrom)
                                    .putExtra("forpasswordforgot", true)
                                    .putExtra("appopen", appopen)
                            )
                        } else {
                            startActivity(
                                Intent(
                                    this@LockScreenActivity,
                                    LockSceenSecQuestionActivity::class.java
                                )
                                    .putExtra("comefrom", comefrom)
                                    .putExtra("forpasswordforgot", true)
                                    .putExtra("appopen", appopen)
                            )
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setOTPView() {
        val primaryColor = getProperPrimaryColor()
        val textColor = getProperTextColor()
        binding.otpView.apply {

            setItemBackground(
                createOptionBackground(
                    cornerSize = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._5sdp)
                        .toFloat(),
                    fillColor = textColor.adjustAlpha(0.25f),

                    )
            )

            setTextColor(textColor)

            setLineColor(getColor(android.R.color.transparent))
        }
    }

    override fun buttonclick(key: String) {
        if (binding.otpView.text?.toString() != key) {
            binding.otpView.setText(key)
        }
    }

    override fun onBackPressed() {
        if (shouldReturnResult && comefrom == 2) {
            (application as? MessagerApplication)?.onFullAppLockCancelled()
            setResult(RESULT_CANCELED)
            finish()
            return
        }

        super.onBackPressed()
        if (comefrom == 2) {
            (application as? MessagerApplication)?.onFullAppLockCancelled()
            Log.d("", "onBackPressed: comefrom <----------> 1 ${config.Full_AppLock_Pin}")
            mainactivityfinish = true

            if (ispasswordchange){
                            finish()
            }else{
                finishAffinity()

            }

        } else if (comefrom == 1) {
            if (fromnotification) {
                Log.d("", "onBackPressed: comefrom <----------> 2 ")
                finishAffinity()
            } else {
                Log.d("", "onBackPressed: comefrom <----------> 3 ")
                finish()
            }
        } else {
            Log.d("", "onBackPressed: comefrom <----------> 4 ")
            finish()
        }

    }

    override fun onFingerprintclick() {
        if (isBiometricSupported()) {
            showBiometricPrompt()
        } else {
            toast(resources.getString(R.string.NotWorking))
        }
    }

    override fun onBackpressClick(keyborttxt: String) {
        if (binding.otpView.text?.toString() != keyborttxt) {
            binding.otpView.setText(keyborttxt)
        }
    }

    private fun showBiometricPrompt() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(resources.getString(R.string.Sign_in_using_fingerprint))
            .setNegativeButtonText(resources.getString(R.string.Enter_Messages_Pin)).build()

        val biometricPrompt = BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    showMessage((resources.getString(R.string.Authentication_error) + " $errString"))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    showMessage(resources.getString(R.string.Authentication_succeeded))
                    passPrivateChat()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showMessage(resources.getString(R.string.Authentication_failed))
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isBiometricSupported(): Boolean {
        val biometricManager = BiometricManager.from(this)
        val canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
        return when (canAuthenticate) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                true
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE, BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE, BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                false
            }

            else -> {
                // Biometric status unknown or another error occurred
                false
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onOtpCompleted(otp: String?) {
        if (demopass == otp) {
            var doneOtp: Drawable? = null
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    doneOtp = createCustomDrawable(false)
                }
                withContext(Dispatchers.Main) {
                    binding.otpView.setItemBackground(doneOtp)
                }
            }
            passPrivateChat()
        } else {
            val animShake = AnimationUtils.loadAnimation(this, R.anim.shake);
            binding.otpView.startAnimation(animShake)
            var wrongOtp: Drawable? = null
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    wrongOtp = createCustomDrawable(true)
                }
                withContext(Dispatchers.Main) {
                    binding.otpView.setItemBackground(wrongOtp)
                }
            }
            Handler(Looper.myLooper()!!).postDelayed({
                setOTPView()
                binding.otpView.text?.clear()
                adapterkeybord.keyborttxt = ""
            }, 1000)
            toast(resources.getString(R.string.Wrong_pin))
        }
    }

    private fun passPrivateChat() {
        if (comefrom == 2) {
            (application as? MessagerApplication)?.onFullAppLockVerified()
        }

        Log.d(
            "",
            "passPrivateChat: 1 <----------> tredid ${tredid} <----------> name ${name}<----------> mobileNumber ${mobileNumber}"
        )
        Handler(Looper.myLooper()!!).postDelayed({

            if (ispasswordchange) {
                startActivity(
                    Intent(
                        this,
                        LockScreenPinSetActivity::class.java
                    ).putExtra("pinsetfor", comefrom).putExtra("forpasswordforgot", true)
                )
            } else {
                if (shouldReturnResult && comefrom == 2) {
                    setResult(RESULT_OK)
                    finish()
                    return@postDelayed
                }

                if (comefrom == 1) {
                    if (locktype == 1) {
                        startActivity(
                            Intent(
                                this, if (config.Message_Send_Activity == "1") {
                                    SendMessageActivity::class.java
                                } else {
                                    SendMessageActivity::class.java
                                }
                            ).putExtra("name", name).putExtra("mobileNumber", mobileNumber)
                                .putExtra("tredid", tredid)
                                .putExtra("fromnotification", fromnotification)
                        )
                    } else if (locktype == 2) {
                        startActivity(Intent(this, PrivacyChatActivity::class.java))
                    }
                } else if (comefrom == 2) {
                    if (appopen) {
//                        toastMess("1")

                        startActivity(Intent(this, HomeABActivity::class.java))
                        finish()

                    } else {
//                        toastMess("2")
                        if (openprivatechat) {
                            if (gotingprivatechat) {
                                this?.finish()
                                startActivity(
                                    Intent(this, LockScreenActivity::class.java).putExtra(
                                        "lockype",
                                        2
                                    ).putExtra("comefrom", 1)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                            } else {
                                startActivity(
                                    Intent(this, LockScreenActivity::class.java).putExtra(
                                        "tredid",
                                        thredidLock
                                    ).putExtra("name", nameLock)
                                        .putExtra("mobileNumber", mobileNumberLock)
                                        .putExtra("lockype", 1).putExtra("comefrom", 1)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                            }
                        } else {
                            if (isappopenlock) {
                                isappopenlock = false

                                startActivity(Intent(this, HomeABActivity::class.java))
                                finish()

                            } else {
                                finish()
                            }
                        }

                    }
                }
            }
            finish()
        }, 1000)
    }

    override fun onStart() {
        super.onStart()
        ThemeSetup()
        if (comefrom == 1) {
            demopass = config.Lock_Screen_Pin
            binding.whatlock.text = resources.getString(R.string.Privacy_chat)
        } else if (comefrom == 2) {
            demopass = config.Full_AppLock_Pin
            binding.whatlock.text = resources.getString(R.string.Two_new_step_Verification)
        }
    }

    override fun onResume() {
        super.onResume()
        ThemeSetup()
    }
    private fun ThemeSetup() {


        updateStatusbarColorForLockScreen()

        val statusBarColor = getProperStatusBarColor()
        val navigationBarColor = getBottomNavigationBackgroundColor()
        val surfaceColor = getProperBackgroundColor()
        val primaryColor = getProperPrimaryColor()
        val textColor = getProperTextColor()



        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars = ThemeModeManager.shouldUseLightSystemBars(this@LockScreenActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }

        binding.keybordadapterset.setBackgroundColor(surfaceColor)
        binding.root.setBackgroundColor(surfaceColor)



        setOTPView()

        binding.whatlock.setTextColor(textColor)
        binding.whatlockpin.setTextColor(textColor)
        binding.mainConstraint.setBackgroundColor(surfaceColor)
        binding.forgotpass.setTextColor(primaryColor)

    }


}
