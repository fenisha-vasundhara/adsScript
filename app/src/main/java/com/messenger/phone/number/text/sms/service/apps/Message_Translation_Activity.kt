package com.messenger.phone.number.text.sms.service.apps

import android.app.ProgressDialog
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.color.MaterialColors
import com.demo.adsmanage.Commen.firebaseEventMain
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.MessageTraslationlist
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getBottomNavigationBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getDialogBackgroundColor
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
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isOnline
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.Dialog.MessageLanguageDialog
import com.messenger.phone.number.text.sms.service.apps.adapter.MessageTraslateAdapter
import com.messenger.phone.number.text.sms.service.apps.ads.v2.AdsOrchestrator
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityMessageTranslationBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MessageLanguageDialogInterface
import com.messenger.phone.number.text.sms.service.apps.modelClass.MessageTraslationModel
import com.messenger.phone.number.text.sms.service.apps.translateAPI.TranslateApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class Message_Translation_Activity : AppCompatActivity(), MessageLanguageDialogInterface,
    TextToSpeech.OnInitListener {

    private var statusspeck: Int = 0
    lateinit var binding: ActivityMessageTranslationBinding

    var textnew: String = ""
    var posenew: Int = -1
    var fornarmalmessagenew: Boolean = false

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f


    @Inject
    lateinit var messageTraslateAdapter: MessageTraslateAdapter
    private lateinit var mProgressDialog: ProgressDialog
    var messagealltraslate: ArrayList<MessageTraslationModel> = arrayListOf()
    private lateinit var textToSpeech: TextToSpeech


    val translateApi = TranslateApi()

    private var messageLanguageDialog: MessageLanguageDialog? = null
    private val adsOrchestrator by lazy { AdsOrchestrator.get(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_message_translation)
        setBaseTheme(binding.vAnd15StatusBar)
        this.firebaseEventMain("Translation")
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
        applyMaterialTranslationColors()
        applyMaterialSystemBarColors()

        textToSpeech = TextToSpeech(this, this)

        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
            }

            override fun onDone(utteranceId: String?) {
                CoroutineScope(Dispatchers.Main).launch {
                    if (fornarmalmessagenew) {
                        messageTraslateAdapter.list[posenew].speakmessage = false
                    } else {
                        messageTraslateAdapter.list[posenew].speaktraslationmessage = false
                    }
                    messageTraslateAdapter.notifyDataSetChanged()
                }
            }

            override fun onError(utteranceId: String?) {
                CoroutineScope(Dispatchers.Main).launch {
                    if (fornarmalmessagenew) {
                        messageTraslateAdapter.list[posenew].speakmessage = false
                    } else {
                        messageTraslateAdapter.list[posenew].speaktraslationmessage = false
                    }
                    messageTraslateAdapter.notifyDataSetChanged()
                }
            }
        })

        mProgressDialog = ProgressDialog(
            this, R.style.Dialog_Custom
        )
        mProgressDialog.setCancelable(false)
        mProgressDialog.setMessage(resources.getString(R.string.Loading))

        with(binding) {
            adapter = messageTraslateAdapter
            textView36.text = config.userpreferencelanguage
            messageTraslation()
            messageLanguageDialog = MessageLanguageDialog.newInstance(null, 0, callinterface = true)
            messageLanguageDialog?.setInterface(this@Message_Translation_Activity)
            cardView5.setOnClickListener {
                adsOrchestrator.showTranslationRewarded(this@Message_Translation_Activity) {
                    messageLanguageDialog?.show(supportFragmentManager, "messageLanguageDialog")
                }
            }

            backBtn.setOnClickListener {
                finish()
            }
            messageTraslateAdapter.messagespeck = { text, pos, fornarmalmessage ->

                if (messageTraslateAdapter.list[pos].speaktraslationmessage || messageTraslateAdapter.list[pos].speakmessage){
                    Log.d("", "onCreate: messageTraslateAdapter <---> 1")
                    if (::textToSpeech.isInitialized) {
                        Log.d("", "onCreate: messageTraslateAdapter <---> 2")
                        messageTraslateAdapter.list.forEachIndexed { index, messageTraslationModel ->
                            messageTraslateAdapter.list[index].speaktraslationmessage = false
                            messageTraslateAdapter.list[index].speakmessage = false
                        }
                        messageTraslateAdapter.notifyDataSetChanged()
                        textToSpeech.stop()
                    }
                }else{
                    Log.d("", "onCreate: messageTraslateAdapter <---> 3")
                    textnew = text
                    posenew = pos
                    fornarmalmessagenew = fornarmalmessage
                    speak(text, pos, fornarmalmessage)
                }
            }

        }

    }

    override fun onStart() {
        super.onStart()
        ThemeSetup()
    }

    private fun ThemeSetup() {
        applyMaterialTranslationColors()
        applyMaterialSystemBarColors()
    }

    private fun applyMaterialTranslationColors() {
        val surfaceColor = getProperBackgroundColor()
        val statusBarColor = getProperStatusBarColor()
        val textColor = getProperTextColor()
        val secondaryTextColor = getProperSecondaryTextColor()
        val fillColor = getDialogBackgroundColor()
        val strokeColor = secondaryTextColor.adjustAlpha(0.20f)
        val rippleAlpha = if (ThemeModeManager.shouldUseLightSystemBars(this)) 0.12f else 0.28f
        val rippleColor = MaterialColors.layer(surfaceColor, secondaryTextColor, rippleAlpha)
        val rippleState = ColorStateList.valueOf(rippleColor)

        val fillColor1 = getProperPrimaryColor().adjustAlpha(0.1f)
        binding.cardView5.setCardBackgroundColor(fillColor1)

        binding.main.setBackgroundColor(surfaceColor)
        binding.mainMenuAr.setBackgroundColor(surfaceColor)
        binding.toolBarMain.setBackgroundColor(surfaceColor)
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)

        binding.textView3.setTextColor(textColor)
        binding.textView36.setTextColor(textColor)

        binding.backBtn.iconTint = ColorStateList.valueOf(secondaryTextColor)
        binding.backBtn.rippleColor = rippleState


        binding.cardView5.rippleColor = rippleState

        binding.langArrow.imageTintList = ColorStateList.valueOf(secondaryTextColor)
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
                ThemeModeManager.shouldUseLightSystemBars(this@Message_Translation_Activity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
    }

    override fun onLanguageClick(s: String, snippet: String, position: Int, lang: String) {
        binding.textView36.text = lang
        messageTraslation()
    }

    fun messageTraslation() {
        if (!isOnline()) {
            toastMess(resources.getString(R.string.Please_turn_on))
            return
        }
        mProgressDialog.show()
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                messagealltraslate.clear()
                MessageTraslationlist.forEach {
                    translatemessage(it.message, config.userpreferencelanguageCode)
                }
            }
            withContext(Dispatchers.Main) {
                messageTraslateAdapter.list = messagealltraslate
                mProgressDialog.dismiss()
            }
        }
    }

    suspend fun translatemessage(snippet: String, targetlang: String) {
        try {
            translateApi.translate(text = snippet,
                sourceLang = "auto",
                targetLang = targetlang,
                onSuccess = { translatedText ->
                    messagealltraslate.add(
                        MessageTraslationModel(
                            message = snippet,
                            traslationmessage = translatedText
                        )
                    )
                },
                onError = { exception ->
                    messagealltraslate.add(
                        MessageTraslationModel(
                            message = snippet,
                            traslationmessage = snippet
                        )
                    )
                })
        } catch (e: Exception) {
            println("Coroutine error: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        statusspeck = status
    }

    override fun onDestroy() {
        // Shutdown TextToSpeech engine when activity is destroyed.
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }

    override fun onPause() {
        if (::textToSpeech.isInitialized) {
            messageTraslateAdapter.list.forEachIndexed { index, messageTraslationModel ->
                messageTraslateAdapter.list[index].speaktraslationmessage = false
                messageTraslateAdapter.list[index].speakmessage = false
            }
            messageTraslateAdapter.notifyDataSetChanged()
            textToSpeech.stop()
        }
        super.onPause()

    }

    private fun speak(text: String, pos: Int, fornarmalmessage: Boolean) {
        if (statusspeck == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale("gu", "IN"))
            if (textToSpeech.isSpeaking) {

                messageTraslateAdapter.list.forEachIndexed { index, messageTraslationModel ->
                    messageTraslateAdapter.list[index].speaktraslationmessage = false
                    messageTraslateAdapter.list[index].speakmessage = false
                }
                messageTraslateAdapter.notifyDataSetChanged()
                if (fornarmalmessage) {
                    messageTraslateAdapter.list[pos].speakmessage = false
                } else {
                    messageTraslateAdapter.list[pos].speaktraslationmessage = false
                }
                messageTraslateAdapter.notifyDataSetChanged()
                textToSpeech.stop()
            }
            if (fornarmalmessage) {
                messageTraslateAdapter.list[pos].speakmessage = true
            } else {
                messageTraslateAdapter.list[pos].speaktraslationmessage = true
            }
            messageTraslateAdapter.notifyDataSetChanged()
            val params = HashMap<String, String>()
            params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "messageID"
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params)
        } else {
            if (fornarmalmessage) {
                messageTraslateAdapter.list[pos].speakmessage = false
            } else {
                messageTraslateAdapter.list[pos].speaktraslationmessage = false
            }
            messageTraslateAdapter.notifyDataSetChanged()
            toastMess("error")
        }

    }
}
