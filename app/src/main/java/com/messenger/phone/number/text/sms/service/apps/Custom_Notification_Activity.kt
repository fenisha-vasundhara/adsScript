package com.messenger.phone.number.text.sms.service.apps

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.demo.adsmanage.Commen.firebaseEventMain
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.copyAudioFile
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createCustomDrawable
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getFilePathAndName
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getFilePathFromContentUri
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getFilePathFromUri
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.iscustomwallpaersetterdidmovilenumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.CommanClass.updateStatusbarColorFullApp
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.Repo.RingtoneAndAudioRepo
import com.messenger.phone.number.text.sms.service.apps.adapter.CustomNotificationAdapter
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityCustomNotificationBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.Customnotificationmodel
import com.messenger.phone.number.text.sms.service.apps.modelClass.ProfileNotificationmodel
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetAllCatViewModel
import com.messenger.phone.number.text.sms.service.apps.viewModel.RingtoneAndAudioViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class Custom_Notification_Activity : AppCompatActivity() {

    private var mProgressDialog: ProgressDialog? = null
    private val PICK_AUDIO_REQUEST_CODE: Int = 992599

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    @Inject
    lateinit var customNotificationAdapter: CustomNotificationAdapter
    var ringtonlistmain = ArrayList<Customnotificationmodel>()
    private var mediaPlayer: MediaPlayer? = null
    var thredid: Long = 0
    lateinit var binding: ActivityCustomNotificationBinding
    var customnotificationmodelforsave: Customnotificationmodel? = null
    var customname = ""
    var custompath = ""
    var curruntplayeruri = ""

    @Inject
    lateinit var ringtoneAndAudioRepo: RingtoneAndAudioRepo

    val ringmodelview by lazy {
        ViewModelProvider(this)[RingtoneAndAudioViewModel::class.java]
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_custom_notification)
        this.firebaseEventMain("Custom_Notification")

        thredid = intent.getLongExtra("thredid", 0L)

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

        mProgressDialog = ProgressDialog(this, R.style.Dialog_Custom)
        mProgressDialog?.setCancelable(false)
        mProgressDialog?.setMessage(resources.getString(R.string.Loading))

        customNotificationAdapter.setHasStableIds(true)
        binding.progressbar.visible()
        customnotificationhighlite(true)
        CoroutineScope(Dispatchers.IO).launch { ringmodelview.getAllAudioandNotification() }
        binding.adapter = customNotificationAdapter
        ringmodelview.liveeinglist.observe(this, Observer { ringtonelist ->
//            Log.d("", "onCreate: ringmodelview.liveeinglist <--------> ${it}")
            binding.progressbar.gone()
            mProgressDialog?.dismiss()
            var ringtonlistnew = ArrayList(ringtonelist).distinctBy { it.ringtonename }
            val onlythisprofiledata = config.getProfileNotification(thredid.toString())
            if (onlythisprofiledata != null) {

                if (File(onlythisprofiledata.notificationUri).exists()) {
                    ringtonlistnew.forEachIndexed { index, customnotificationmodel ->
                        if (customnotificationmodel.ringtonepath == onlythisprofiledata.notificationUri) {
                            customnotificationmodel.selected = true
                        } else {
                            customnotificationmodel.selected = false
                        }
                    }
                } else {
                    ringtonlistnew.forEachIndexed { index, customnotificationmodel ->
                        if (customnotificationmodel.ringtonename == "Default") {
                            customnotificationmodel.selected = true
                        } else {
                            customnotificationmodel.selected = false
                        }
                    }
                }
            } else {
                if (config.ringtoanselectforallcontact) {
                    ringtonlistnew.forEachIndexed { index, customnotificationmodel ->
                        if (customnotificationmodel.ringtonepath == config.ringtoanselectforallcontactPath) {
                            customnotificationmodel.selected = true
                        } else {
                            customnotificationmodel.selected = false
                        }
                    }
                } else {
                    ringtonlistnew.forEachIndexed { index, customnotificationmodel ->
                        if (customnotificationmodel.ringtonename == "Default") {
                            customnotificationmodel.selected = true
                        } else {
                            customnotificationmodel.selected = false
                        }
                    }
                }
            }
            customNotificationAdapter.ringtonlist = ArrayList(ringtonlistnew)
            if (customNotificationAdapter.ringtonlist.isEmpty()) {
                binding.notfound.visible()
            } else {
                binding.notfound.gone()
            }
        })

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.langChange.setOnClickListener {
            savedialogshow()
        }


        binding.lanbtn.setOnClickListener {
            val pickAudioIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "audio/*"
            }
            startActivityForResult(pickAudioIntent, PICK_AUDIO_REQUEST_CODE)
        }

        customNotificationAdapter.ringtsetclickonclick = { position, customnotificationmodel ->
            ringmodelview.selectedname = customnotificationmodel.ringtonename
            allselectionremove()
            customnotificationmodelforsave = customnotificationmodel
            customnotificationmodel.selected = true
            customNotificationAdapter.notifyDataSetChanged()
        }

        customNotificationAdapter.ringtonclick = { position, customnotificationmodel ->
            Log.d(
                "",
                "onCreate: customNotificationAdapter <--------> ${customnotificationmodel.ringtonecontentpath}"
            )
            if (mediaPlayer?.isPlaying == true) {
                if (curruntplayeruri == customnotificationmodel.ringtonepath) {
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    mediaPlayer = null
                    customNotificationAdapter.ringtonlist[position].ringtonisplay = false
                    customNotificationAdapter.notifyDataSetChanged()
                } else {
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    mediaPlayer = null
                    playstockplayer(position, customnotificationmodel)
                }
            } else {
                playstockplayer(position, customnotificationmodel)
            }
        }
    }

    private fun callviewmodel() {
        CoroutineScope(Dispatchers.Main).launch { mProgressDialog?.show() }
        CoroutineScope(Dispatchers.IO).launch { ringmodelview.refreshaudiodata() }
    }

    private fun playstockplayer(position: Int, customnotificationmodel: Customnotificationmodel) {
        allplaystop()
        curruntplayeruri = customnotificationmodel.ringtonepath
        mediaPlayer = MediaPlayer().apply {
            setDataSource(
                this@Custom_Notification_Activity,
                customnotificationmodel.ringtonepath.toUri()
            )
            setOnPreparedListener {
                Log.d("", "mediaPlayer onCreate: <----------> setOnPreparedListener")
                start()
                customNotificationAdapter.ringtonlist[position].ringtonisplay = true
                customNotificationAdapter.notifyDataSetChanged()
            }
            setOnCompletionListener {
                Log.d("", "mediaPlayer onCreate: <----------> setOnCompletionListener")
                customNotificationAdapter.ringtonlist[position].ringtonisplay = false
                customNotificationAdapter.notifyDataSetChanged()
            }
            setOnErrorListener { mp, what, extra ->
                Log.d("", "mediaPlayer onCreate: <----------> setOnErrorListener")
                customNotificationAdapter.ringtonlist[position].ringtonisplay = false
                customNotificationAdapter.notifyDataSetChanged()
                true
            }
            prepareAsync()
        }
    }

    private fun savedialogshow() {

        val options = arrayOf(
            resources.getString(R.string.For_this_chat) + " ${"$iscustomwallpaersetterdidmovilenumber"}",
            resources.getString(R.string.For_all_chats)
        )
        val options2 = arrayOf("Option 1", "Option 2", "Option 3")
        var selectedItem = 0
        val builder = MaterialAlertDialogBuilder(this)
        builder.setCancelable(false)
        builder.setTitle(resources.getString(R.string.Set_Notication))
        builder.setSingleChoiceItems(options, selectedItem) { dialog, which ->
            selectedItem = which
        }
        builder.setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
            when (selectedItem) {
                0 -> {
                    if (customnotificationmodelforsave != null) {
                        config.saveProfileNotification(
                            ProfileNotificationmodel(
                                tredid = thredid.toString(),
                                iscstomselected = false,
                                notificationname = customnotificationmodelforsave?.ringtonename.toString(),
                                notificationUri = customnotificationmodelforsave?.ringtonepath.toString(),
                                allprofile = false
                            )
                        )
                        dialog.dismiss()
                    } else {
                        dialog.dismiss()
                    }
                }

                1 -> {
                    if (customnotificationmodelforsave != null) {
                        config.ringtoanselectforallcontact = true
                        config.removeAllProfileNotification()
                        config.ringtoanselectforallcontactName =
                            customnotificationmodelforsave?.ringtonename.toString()
                        config.ringtoanselectforallcontactPath =
                            customnotificationmodelforsave?.ringtonepath.toString()
                        dialog.dismiss()
                    } else {
                        dialog.dismiss()
                    }
                }
            }
            dialog.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.cancel))
        { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun allplaystop() {
        customNotificationAdapter.ringtonlist.forEachIndexed { index, customnotificationmodel ->
            customnotificationmodel.ringtonisplay = false
        }
        customNotificationAdapter.notifyDataSetChanged()
    }

    private fun allselectionremove() {
        customNotificationAdapter.ringtonlist.forEachIndexed { index, customnotificationmodel ->
            customnotificationmodel.selected = false
        }
        customNotificationAdapter.notifyDataSetChanged()
    }

    override fun onPause() {
        mediaplayerrelleaseandstop()
        super.onPause()
    }

    private fun mediaplayerrelleaseandstop() {
        allplaystop()
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    override fun onDestroy() {
        mediaplayerrelleaseandstop()
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        ThemeSetup()
    }

    private fun ThemeSetup() {
        updateStatusbarColorFullApp()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_AUDIO_REQUEST_CODE && resultCode == RESULT_OK) {
            CoroutineScope(Dispatchers.IO).launch {
                data?.data?.let { uri ->
                    Log.d("", "onActivityResult: <-----------> file uri 1 ${uri}")
                    Log.d(
                        "",
                        "onActivityResult: <-----------> file uri ${
                            getFilePathFromUri(
                                this@Custom_Notification_Activity,
                                uri
                            )!!
                        }"
                    )
                    val uridata = getFilePathFromUri(this@Custom_Notification_Activity, uri)
                    uridata?.let {
                        val copiedFile = copyAudioFile(it)
                        copiedFile?.let {
                            val cacheFileInfo = getFilePathAndName(it)
                            cacheFileInfo.let { (cachedName, cachedPath) ->
                                customname = cachedName
                                custompath = cachedPath
                                callviewmodel()
                            } ?: run {
                                customname = ""
                                custompath = ""
                                runOnUiThread {
                                    toastMess("Something went wrong please try again")
                                }
                            }
                        }

                    }


//                    val fileName = getFileNameFromUri(uri)
//                    fileName?.let { name ->
//
//
//
////                        val cacheFile = File(cacheDir, fileName)
////                        copyFileToCache(uri, cacheFile, fileName)
////                        val cacheFileInfo = getCacheAudioFileNameAndPath(name)
//
//
//
//
////                        cacheFileInfo?.let { (cachedName, cachedPath) ->
////                            customname = cachedName
////                            custompath = cachedPath
////                            callviewmodel()
////                        } ?: run {
////                            customname = ""
////                            custompath = ""
////                            runOnUiThread {
////                                toastMess("Something went wrong plz try again")
////                            }
////                        }
//                    }
                }
            }
        }
    }

    private fun customnotificationhighlite(forselected: Boolean) {
        if (forselected) {
            CoroutineScope(Dispatchers.IO).launch {
                val background = createCustomDrawable(
                    cornerRadiusResId = com.intuit.sdp.R.dimen._10sdp,
                    solidColorResId =
                    if (config.activeThemeSelection == 1) {
                        R.color.settingbuttonfullapp
                    } else if (config.activeThemeSelection == 2) {
                        R.color.toolbarcolor2
                    } else if (config.activeThemeSelection == 3) {
                        R.color.toolbarcolor3new
                    } else {
                        R.color.toolbarcolor2
                    },
                    strokeColorResId =
                    R.color.appcolor,
                    strokeWidthResId = com.intuit.sdp.R.dimen._1sdp
                )
                runOnUiThread {
                    binding.lanbtn.background = background
                }
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val background = createCustomDrawable(
                    cornerRadiusResId = com.intuit.sdp.R.dimen._10sdp,
                    solidColorResId =
                    if (config.activeThemeSelection == 1) {
                        R.color.settingbuttonfullapp
                    } else if (config.activeThemeSelection == 2) {
                        R.color.toolbarcolor2
                    } else if (config.activeThemeSelection == 3) {
                        R.color.toolbarcolor3new
                    } else {
                        R.color.toolbarcolor2
                    },
                    strokeColorResId =
                    R.color.lanunselectedcolor,
                    strokeWidthResId = com.intuit.sdp.R.dimen._1sdp
                )
                runOnUiThread {
                    binding.lanbtn.background = background
                }
            }
        }
    }
}