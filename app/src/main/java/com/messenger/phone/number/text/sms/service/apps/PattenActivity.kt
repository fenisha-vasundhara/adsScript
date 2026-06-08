package com.messenger.phone.number.text.sms.service.apps

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.demo.adsmanage.Commen.firebaseEventMain
import com.google.android.material.color.MaterialColors
import com.messenger.phone.number.text.sms.service.apps.CommanClass.AutoFullAppLockRemove
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
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
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityPattenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PattenActivity : AppCompatActivity() {
    //    lateinit var mPatternLockView: PatternLockView
    lateinit var binding: ActivityPattenBinding
    var lock: String = "012"
    var fornewpattenset = false
    var isfirsttimedraw = true
    var userpattendraw = "-1"
    var comefrom: Int = -1
    var password: String = ""
    var passwordwrong: Boolean = false

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_patten)
        setBaseTheme(binding.vAnd15StatusBar)
//        mPatternLockView = findViewById(R.id.patter_lock_view)
        this.firebaseEventMain("Pattern_Lock")
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
        applyMaterialPatternColors()
        applyMaterialSystemBarColors()


        password = intent.getStringExtra("password").toString()
        fornewpattenset = intent.getBooleanExtra("fornewpattenset", false)
        comefrom = intent.getIntExtra("comefrom", -1)

//        if (!fornewpattenset){
//            binding.drawYourPatten
//        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.patterLockView.setCallBack(object : PatternLockView.CallBack {
            override fun onFinish(passwordnew: PatternLockView.Password?): Int {

                val userlock: String = passwordnew?.list?.joinToString("") { it.toString() }.toString()

                Log.d("intervalMillis", "AutoFullAppLockRemove onFinish:<---------> ${userlock}")
                Log.d(
                    "intervalMillis",
                    "AutoFullAppLockRemove onFinish:<---------> ${userlock.length}"
                )
                if (fornewpattenset) {
                    if (isfirsttimedraw) {
                        if (userlock.length >= 4) {
                            binding.drawYourPatten.text =
                                resources.getString(R.string.draw_your_patten_conform)
                            isfirsttimedraw = false
                            userpattendraw = userlock
                            passwordwrong = false
//                            return PatternLockView.CODE_PASSWORD_CORRECT;
                        } else {
                            passwordwrong = true
                            toastMess(resources.getString(R.string.pattern))
//                            return PatternLockView.CODE_PASSWORD_ERROR;
                        }
                    } else {
                        if (userlock.length >= 4) {
                            if (userpattendraw == userlock) {
                                when (comefrom) {
                                    1 -> {
                                        config.isprivatelocktype = 2
                                        config.isprivatelockpatten = userlock
                                        config.Lock_Screen_Pin = password
                                        val intent =
                                            Intent(
                                                this@PattenActivity,
                                                LockScreenActivity::class.java
                                            )
                                                .putExtra("lockype", 2)
                                                .putExtra("comefrom", comefrom)
                                        startActivity(intent)
                                        this@PattenActivity.finish()
                                    }

                                    2 -> {
                                        Log.d(
                                            "intervalMillis",
                                            "AutoFullAppLockRemove onFinish:<---------> "
                                        )
                                        val intervalMillis = 3 * 24 * 60 * 60 * 1000L
                                        AutoFullAppLockRemove(intervalMillis)
                                        config.is2steplocktype = 2
                                        config.is2steplockpatten = userlock
                                        config.Full_AppLock_Pin = password
                                        startActivity(
                                            Intent(
                                                this@PattenActivity,
                                                LockScreenSetupActivity::class.java
                                            )
                                                .putExtra("fullappsetupdone", true)
                                        )
                                        this@PattenActivity.finish()
                                    }
                                }
                                passwordwrong = false
//                                return PatternLockView.CODE_PASSWORD_CORRECT;
                            } else {
                                passwordwrong = true
                                toastMess(resources.getString(R.string.pattern_not_mach))
//                                return PatternLockView.CODE_PASSWORD_ERROR;
                            }
                        } else {
                            toastMess(resources.getString(R.string.pattern_not_mach))
//                            return PatternLockView.CODE_PASSWORD_ERROR;
                            passwordwrong = true
                        }
                    }
                } else {
                    if (userlock.length >= 4) {
                        when (comefrom) {
                            1 -> {
                                if (userlock == config.isprivatelockpatten.toString()) {
                                    Handler(Looper.myLooper()!!).postDelayed(
                                        {
                                            startActivity(
                                                Intent(
                                                    this@PattenActivity,
                                                    LockScreenPinSetActivity::class.java
                                                ).putExtra("pinsetfor", 1)
                                                    .putExtra("forpasswordforgot", true)
                                            )
                                            finish()
                                        },
                                        1000
                                    )
                                    passwordwrong = false
//                                    return PatternLockView.CODE_PASSWORD_CORRECT;
                                } else {
                                    passwordwrong = true
                                    toastMess(resources.getString(R.string.pattern_not_mach))
//                                    return PatternLockView.CODE_PASSWORD_ERROR;
                                }
                            }

                            2 -> {
                                Log.d("jigar", "onComplete: config.userlock <--> ${userlock}")
                                if (userlock == config.is2steplockpatten.toString()) {
                                    Handler(Looper.myLooper()!!).postDelayed(
                                        {
                                            startActivity(
                                                Intent(
                                                    this@PattenActivity,
                                                    LockScreenPinSetActivity::class.java
                                                ).putExtra("pinsetfor", 2)
                                                    .putExtra("forpasswordforgot", true)
                                            )
                                            finish()
                                        },
                                        1000
                                    )
                                    passwordwrong = false
//                                    return PatternLockView.CODE_PASSWORD_CORRECT;
                                } else {
                                    passwordwrong = true
                                    toastMess(resources.getString(R.string.pattern_not_mach))
//                                    return PatternLockView.CODE_PASSWORD_ERROR;
                                }
                            }
                        }
                    } else {
                        passwordwrong = true
                        toastMess(resources.getString(R.string.pattern_not_mach))
//                        return PatternLockView.CODE_PASSWORD_ERROR;
                    }
                }
                if (passwordwrong) {
                    Log.d("", "onFinish: passwordwrong <---------> 1")
                    return PatternLockView.CODE_PASSWORD_ERROR;
                } else {
                    Log.d("", "onFinish: passwordwrong <---------> 2")
                    return PatternLockView.CODE_PASSWORD_CORRECT;
                }
            }
        })


//        mPatternLockViewListener =
//            object : PatternLockViewListener {
//                override fun onStarted() {
//                    Log.d("", "onStarted:PatternLockViewListener <=========> ")
//                }
//
//                override fun onProgress(progressPattern: List<PatternLockView.Dot?>?) {
//                    Log.d("", "onProgress:PatternLockViewListener <=========> ")
//                }
//
//                override fun onComplete(pattern: List<PatternLockView.Dot?>?) {
//                    Log.d("", "onComplete:PatternLockViewListener <=========> ")
//                    val userlock: String =
//                        PatternLockUtils.patternToString(mPatternLockView, pattern)
//
//                    if (fornewpattenset) {
//                        if (isfirsttimedraw) {
//                            if (userlock.length >= 4) {
//                                mPatternLockView.clearPattern()
////                                val handler = Handler(Looper.myLooper()!!)
////                                handler.postDelayed({
////
////                                }, 1000)
//                                mPatternLockView.clearPattern()
//                                binding.drawYourPatten.text =
//                                    resources.getString(R.string.draw_your_patten_conform)
//                                isfirsttimedraw = false
//                                userpattendraw = userlock
//                            } else {
//                                toastMess(resources.getString(R.string.pattern))
//                                mPatternLockView.setCorrectStateColor(resources.getColor(R.color.wrong_color))
//                                mPatternLockView.clearPattern()
////                                Handler(Looper.myLooper()!!).postDelayed(
////                                    { },
////                                    1000
////                                )
//                            }
//                        } else {
//                            if (userlock.length >= 4) {
//                                if (userpattendraw == userlock) {
//                                    mPatternLockView.clearPattern()
//                                    when (comefrom) {
//                                        1 -> {
//                                            config.isprivatelocktype = 2
//                                            config.isprivatelockpatten = userlock
//                                            config.Lock_Screen_Pin = password
//                                            val intent =
//                                                Intent(
//                                                    this@PattenActivity,
//                                                    LockScreenActivity::class.java
//                                                )
//                                                    .putExtra("lockype", 2)
//                                                    .putExtra("comefrom", comefrom)
//                                            startActivity(intent)
//                                            this@PattenActivity.finish()
//                                        }
//
//                                        2 -> {
//                                            config.is2steplocktype = 2
//                                            config.is2steplockpatten = userlock
//                                            config.Full_AppLock_Pin = password
//                                            startActivity(
//                                                Intent(
//                                                    this@PattenActivity,
//                                                    LockScreenSetupActivity::class.java
//                                                )
//                                                    .putExtra("fullappsetupdone", true)
//                                            )
//                                            this@PattenActivity.finish()
//                                        }
//
//                                        else -> {
//                                        }
//                                    }
//                                } else {
//                                    toastMess(resources.getString(R.string.pattern_not_mach))
//                                    mPatternLockView.setCorrectStateColor(resources.getColor(R.color.wrong_color))
//                                    Handler(Looper.myLooper()!!).postDelayed(
//                                        { mPatternLockView.clearPattern() },
//                                        1000
//                                    )
//                                }
//                            } else {
//                                toastMess(resources.getString(R.string.pattern_not_mach))
//                                mPatternLockView.setCorrectStateColor(resources.getColor(R.color.wrong_color))
//                                Handler(Looper.myLooper()!!).postDelayed(
//                                    { mPatternLockView.clearPattern() },
//                                    1000
//                                )
//                            }
//                        }
//                    } else {
//                        if (userlock.length >= 4) {
//                            when (comefrom) {
//                                1 -> {
//                                    if (userlock == config.isprivatelockpatten.toString()) {
//                                        mPatternLockView.setCorrectStateColor(resources.getColor(R.color.Call))
//
//                                        Handler(Looper.myLooper()!!).postDelayed(
//                                            {
//                                                mPatternLockView.clearPattern()
//                                                startActivity(
//                                                    Intent(
//                                                        this@PattenActivity,
//                                                        LockScreenPinSetActivity::class.java
//                                                    ).putExtra("pinsetfor", 1)
//                                                        .putExtra("forpasswordforgot", true)
//                                                )
//                                                finish()
//                                            },
//                                            1000
//                                        )
//
//
//                                    } else {
//                                        toastMess(resources.getString(R.string.pattern_not_mach))
//                                        mPatternLockView.setCorrectStateColor(resources.getColor(R.color.wrong_color))
//                                        Handler(Looper.myLooper()!!).postDelayed(
//                                            { mPatternLockView.clearPattern() },
//                                            1000
//                                        )
//                                    }
//                                }
//
//                                2 -> {
//                                    Log.d(
//                                        "jigar",
//                                        "onComplete: config.is2steplockpatten <--> ${config.is2steplockpatten}"
//                                    )
//                                    Log.d("jigar", "onComplete: config.userlock <--> ${userlock}")
//                                    if (userlock == config.is2steplockpatten.toString()) {
//                                        mPatternLockView.setCorrectStateColor(resources.getColor(R.color.Call))
//                                        Handler(Looper.myLooper()!!).postDelayed(
//                                            {
//                                                mPatternLockView.clearPattern()
//                                                startActivity(
//                                                    Intent(
//                                                        this@PattenActivity,
//                                                        LockScreenPinSetActivity::class.java
//                                                    ).putExtra("pinsetfor", 2)
//                                                        .putExtra("forpasswordforgot", true)
//                                                )
//                                                finish()
//                                            },
//                                            1000
//                                        )
//                                    } else {
//                                        toastMess(resources.getString(R.string.pattern_not_mach))
//                                        mPatternLockView.setCorrectStateColor(resources.getColor(R.color.wrong_color))
//                                        Handler(Looper.myLooper()!!).postDelayed(
//                                            {
//
//                                                mPatternLockView.clearPattern()
//
//
//                                            },
//                                            1000
//                                        )
//                                    }
//                                }
//                            }
//                        } else {
//                            toastMess(resources.getString(R.string.pattern_not_mach))
//                            mPatternLockView.setCorrectStateColor(resources.getColor(R.color.wrong_color))
//                            Handler(Looper.myLooper()!!).postDelayed(
//                                { mPatternLockView.clearPattern() },
//                                1000
//                            )
//                        }
//                    }
//
////                    if (userlock == lock) {
////                        mPatternLockView.setCorrectStateColor(resources.getColor(R.color.Call))
////                        Toast.makeText(this@PattenActivity, "Correct Password", Toast.LENGTH_LONG)
////                            .show()
////                        val handler = Handler(Looper.myLooper()!!)
////                        handler.postDelayed({ mPatternLockView.clearPattern() }, 1000)
////                    } else {
////                        mPatternLockView.setCorrectStateColor(resources.getColor(R.color.wrong_color))
////                        Toast.makeText(this@PattenActivity, "Wrong Password", Toast.LENGTH_LONG)
////                            .show()
////                        val handler = Handler(Looper.myLooper()!!)
////                        handler.postDelayed({ mPatternLockView.clearPattern() }, 1000)
////                    }
//                }
//
//                override fun onCleared() {
//                    Log.d("", "onCleared:PatternLockViewListener <=========> ")
//                }
//            }

//        with(binding) {
//            mPatternLockView.setDotCount(3)
//            mPatternLockView.setDotNormalSize(
//                ResourceUtils.getDimensionInPx(
//                    this@PattenActivity,
//                    R.dimen.pattern_lock_dot_size
//                ).toInt()
//            )
//            mPatternLockView.setDotSelectedSize(
//                ResourceUtils.getDimensionInPx(
//                    this@PattenActivity,
//                    R.dimen.pattern_lock_dot_selected_size
//                ).toInt()
//            )
//            mPatternLockView.setPathWidth(
//                ResourceUtils.getDimensionInPx(
//                    this@PattenActivity,
//                    R.dimen.pattern_lock_path_width
//                ).toInt()
//            )
//            mPatternLockView.setAspectRatioEnabled(true)
//            mPatternLockView.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS)
//            mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT)
////            mPatternLockView.setDotAnimationDuration(150)
////            mPatternLockView.setPathEndAnimationDuration(100)
//            mPatternLockView.setCorrectStateColor(
//                ResourceUtils.getColor(
//                    this@PattenActivity,
//                    R.color.appcolor
//                )
//            )
//            mPatternLockView.setInStealthMode(false)
//            mPatternLockView.setTactileFeedbackEnabled(true)
//            mPatternLockView.setInputEnabled(true)
//            mPatternLockView.addPatternLockListener(mPatternLockViewListener)
//        }

    }


    override fun onStart() {
        super.onStart()
        ThemeSetup()
    }

    private fun ThemeSetup() {
        applyMaterialPatternColors()
        applyMaterialSystemBarColors()
    }

    private fun applyMaterialPatternColors() {
        val surfaceColor = getProperBackgroundColor()
        val primaryColor = getProperPrimaryColor()
        val statusBarColor = getProperStatusBarColor()
        val textColor = getProperTextColor()
        val secondaryTextColor = getProperSecondaryTextColor()
        val rippleAlpha = if (ThemeModeManager.shouldUseLightSystemBars(this)) 0.12f else 0.28f
        val rippleColor = MaterialColors.layer(surfaceColor, secondaryTextColor, rippleAlpha)
        val rippleState = ColorStateList.valueOf(rippleColor)
        val lineColor = ColorUtils.setAlphaComponent(primaryColor, 0x69)

        binding.main.setBackgroundColor(surfaceColor)
        binding.mainMenuAr.setBackgroundColor(surfaceColor)
        binding.toolBarMain.setBackgroundColor(surfaceColor)
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)

        binding.textView3.setTextColor(textColor)
        binding.enterNewpasstxt.setTextColor(textColor)
        binding.drawYourPatten.setTextColor(textColor)

        binding.backBtn.iconTint = ColorStateList.valueOf(secondaryTextColor)
        binding.backBtn.rippleColor = rippleState

        binding.patterLockView.setLineColor(lineColor)
        AppCompatResources.getDrawable(this, R.drawable.pattern_lock_dot_node_highlighted)
            ?.let { highlight ->
                binding.patterLockView.setNodeHighlightSrc(highlight.tinted(primaryColor))
            }
    }

    private fun Drawable.tinted(color: Int): Drawable {
        val wrapped = DrawableCompat.wrap(mutate())
        DrawableCompat.setTint(wrapped, color)
        return wrapped
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
            val useLightSystemBars = ThemeModeManager.shouldUseLightSystemBars(this@PattenActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
    }
}
