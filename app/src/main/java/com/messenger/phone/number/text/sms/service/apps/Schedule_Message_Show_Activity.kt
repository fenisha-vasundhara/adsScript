package com.messenger.phone.number.text.sms.service.apps

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.firebaseEventMain
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ADDRESS_SEPARATOR
import com.messenger.phone.number.text.sms.service.apps.CommanClass.cancelScheduleSendPendingIntent
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.convertStringToArrayList
import com.messenger.phone.number.text.sms.service.apps.CommanClass.copyToClipboard
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createOptionBackground
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getAddresses
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getBottomNavigationBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperStatusBarColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getSendMessageSettings
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadId
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadParticipants
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadTitle
import com.messenger.phone.number.text.sms.service.apps.CommanClass.insertSmsMessageForGroup
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.handleDefaultSmsClick_1
import com.messenger.phone.number.text.sms.service.apps.Dialog.CommanDeleteBlockDialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.ScheduleMessageShowDialog
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.adapter.Schedule_Message_Show_Adapter
import com.messenger.phone.number.text.sms.service.apps.ads.v2.AdsOrchestrator
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityScheduleMessageShowBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.CommanDeleteBlockDialogInterface
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.sms.SendSMSManager
import com.messenger.phone.number.text.sms.service.apps.sms.SmsUtils.deleteSMS
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetAllScheduleConversationViewModel
import com.simplemobiletools.commons.extensions.getContrastColor
import com.simplemobiletools.commons.extensions.showErrorToast
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.extensions.underlineText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject


@AndroidEntryPoint
class Schedule_Message_Show_Activity : AppCompatActivity(), CommanDeleteBlockDialogInterface {


    @Inject
    lateinit var sendSMSManager: SendSMSManager

    lateinit var binding: ActivityScheduleMessageShowBinding

    @Inject
    lateinit var scheduleMessageShowAdapter: Schedule_Message_Show_Adapter

    lateinit var model: GetAllScheduleConversationViewModel

    @Inject
    lateinit var scheduleMessageShowDialog: ScheduleMessageShowDialog

    @Inject
    lateinit var messagerDatabaseRepo: MessagerDatabaseRepo

    private var commanDeleteBlockDialog: CommanDeleteBlockDialog? = null

    private var message: Conversation? = null

    var data: Conversation? = null

    var ismessagefind: Boolean = false
    var ismessagefindtime: Long = 0L
    var posfond = -1

    private var isarchiv: Boolean = false
    private var isPrivateChat: Boolean = false

    var list: ArrayList<Conversation> = arrayListOf()


    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f
    private val adsOrchestrator by lazy { AdsOrchestrator.get(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule_message_show)
        setBaseTheme(binding.vAnd15StatusBar)
        this.firebaseEventMain("Schedule_Message_Show")
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

        config.isschedulemessagescreenopen = true
        ismessagefind = intent.getBooleanExtra("ismessagefind", false)
        ismessagefindtime = intent.getLongExtra("ismessagefindtime", 0L)

        binding.adapter = scheduleMessageShowAdapter
        scheduleMessageShowAdapter.setHasStableIds(true)
        model = ViewModelProvider(this)[GetAllScheduleConversationViewModel::class.java]
        model.GetAllConversationlivelist.observe(this, Observer {
            scheduleMessageShowAdapter.schedulemessagelist = ArrayList(it)
            binding.isnotfound = scheduleMessageShowAdapter.schedulemessagelist.isEmpty()
//            if (ismessagefind) {
//                posfond = ArrayList(it).indexOfFirst { it.time == ismessagefindtime }
//                itemfoundnotifiy()
//            }
        })
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        scheduleMessageShowAdapter.onMessageClick = { pos, list ->
            Log.d("", "onCreate: scheduleMessageShowAdapter <---------------> 111")
            if (!scheduleMessageShowDialog.isAdded) {
                scheduleMessageShowDialog.setData(pos, list)
                scheduleMessageShowDialog.show(supportFragmentManager, "scheduleMessageShowAdapter")
            }

        }

        binding.scheduleMessageSendBtn.setOnClickListener {
            openScheduleComposerWithRewardGate()
        }
        binding.scheduleMessagePlaceholderAction.apply {
            underlineText()
            setOnClickListener {
                openScheduleComposerWithRewardGate()
            }
        }

        scheduleMessageShowDialog.schedulemessageshowdialogclick = { dialog, pos, list ->
            data = list[pos]
            when (dialog) {
                "CopyText" -> {
                    copyToClipboard(data!!.snippet.replace(Regex("^\\s+|\\s+$"), ""))
                    toast(resources.getString(R.string.message_copied_to_clipboard_new))
                }

                "Delete" -> {
                    if (packageName == Telephony.Sms.getDefaultSmsPackage(this@Schedule_Message_Show_Activity)) {

                    showcommandialog(
                        dialogtital = resources.getString(R.string.Delete_this_message),
                        dialogmessage = resources.getString(R.string.This_is_permanent),
                        positivebutton = resources.getString(R.string.Delete),
                        negativebutton = resources.getString(R.string.cancel),
                        "delete"
                    )
                    }else{
                        handleDefaultSmsClick_1(this@Schedule_Message_Show_Activity)
                    }
                }

                "SendNow" -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        setMessage()
                    }
                }

                "EditMessage" -> {
                    openEditDialog(pos, list)
                }
            }
            scheduleMessageShowDialog.dismiss()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun itemfoundnotifiy() {

        binding.allmassagerecycler.scrollToPosition(posfond)
        scheduleMessageShowAdapter.ismessagefind = true
        scheduleMessageShowAdapter.ismessagefindtime = ismessagefindtime
        scheduleMessageShowAdapter.notifyDataSetChanged()
    }

    private fun showcommandialog(
        dialogtital: String,
        dialogmessage: String,
        positivebutton: String,
        negativebutton: String,
        whatfordialog: String
    ) {
        commanDeleteBlockDialog = CommanDeleteBlockDialog.newInstance(
            dialogtital,
            dialogmessage,
            positivebutton,
            negativebutton,
            whatfordialog
        )
        commanDeleteBlockDialog?.setInterface(this)
        commanDeleteBlockDialog?.show(supportFragmentManager, "delete")

    }

    override fun onpositive(whatfordialog: String) {
        when (whatfordialog) {
            "delete" -> {
                commanDeleteBlockDialog?.dismiss()
                CoroutineScope(Dispatchers.IO).launch {
                    data?.id?.let { messagerDatabaseRepo.deleteMessagerRepo(it) }
                    data?.messageId?.toInt()?.let { it1 -> deleteSMS(it1) }
                    if (data?.is_scheduled == true) {
                        data?.messageId?.let { it1 -> cancelScheduleSendPendingIntent(it1) }
                    }
                }
            }
        }
    }

    override fun onnegative(whatfordialog: String) {
        when (whatfordialog) {
            "delete" -> {
                commanDeleteBlockDialog?.dismiss()
            }
        }
    }

    suspend fun setMessage() {
        message = data?.messageId?.let { data?.threadId?.let { it1 -> messagerDatabaseRepo.getScheduledMessageWithIdRepo(it1, it) } }
        if (message != null) {
            try {
                val subscriptionId = SmsManager.getDefaultSmsSubscriptionId()
                message?.let { messagebrod ->
                    val addresses = convertStringToArrayList(messagebrod.phoneNumber, "fornumber")
                    val settings = getSendMessageSettings()
                    if (addresses.size > 1) {

                        sendSMSManager.sendMessageCompat(messagebrod.snippet, addresses, subscriptionId, arrayListOf(), isgroupmessage = true)

                        val broadCastThreadId = getThreadId(addresses.toSet())
                        val mergedAddresses = addresses.joinToString(ADDRESS_SEPARATOR)
                        val messageuri = insertSmsMessageForGroup(
                            subId = settings.subscriptionId, dest = mergedAddresses, text = messagebrod.snippet, timestamp = System.currentTimeMillis(), threadId = broadCastThreadId, status = Telephony.Sms.Sent.STATUS_COMPLETE, type = Telephony.Sms.Sent.MESSAGE_TYPE_SENT
                        )
                        val insertedId = messageuri.lastPathSegment!!.toLong()
                        CoroutineScope(Dispatchers.IO).launch {

                            val job = CoroutineScope(Dispatchers.IO).launch {
                                list = messagebrod.threadId?.toLong()?.let { messagerDatabaseRepo.getUserMessageListrepo(it) }?.let { ArrayList(it) }!!
                            }
                            job.join()

                            Log.d("list", "setMessage:list <----------> 1 ${messagebrod.threadId}")

                            if (list.isNotEmpty()) {

                                Log.d("list", "setMessage:list <----------> 2")

                                isarchiv = if (list?.isEmpty() == true) {
                                    false
                                } else {
                                    list!![0].isarchived
                                }

                                isPrivateChat = if (list?.isEmpty() == true) {
                                    false
                                } else {
                                    list!![0].isPrivateChat
                                }

                                CoroutineScope(Dispatchers.IO).launch {
                                    if (!messagerDatabaseRepo.isMessageExitsRepo(insertedId)) {
                                        val c: Date = Calendar.getInstance().time
                                        val data = messagerDatabaseRepo.getUserMessageListChackrepo(messagebrod.threadId!!)
                                        if (data.isNotEmpty()) {
                                            val isgropmessage = data[0].isgroupmessage
                                            if (isgropmessage) {
                                                val conversation = Conversation(
                                                    0,
                                                    c.time.toString(),
                                                    true,
                                                    data[0].title,
                                                    null,
                                                    false,
                                                    data[0].phoneNumber,
                                                    messagebrod.snippet,
                                                    c.time,
                                                    2,
                                                    true,
                                                    null,
                                                    messageId = insertedId,
                                                    threadId = messagebrod.threadId,
                                                    isgroupmessage = true,
                                                    groupName = data[0].groupName,
                                                    isarchived = isarchiv,
                                                    isPrivateChat = isPrivateChat
                                                )
                                                messagerDatabaseRepo.insertmessage(conversation)
                                            }
                                        } else {

                                            val participantsforgroupchat = getThreadParticipants(messagebrod.threadId!!, null)
                                            val addresses = participantsforgroupchat.getAddresses()
                                            val result = addresses.joinToString(separator = "|")

                                            val conversation = Conversation(
                                                0,
                                                c.time.toString(),
                                                true,
                                                participantsforgroupchat.getThreadTitle(),
                                                null,
                                                false,
                                                result,
                                                messagebrod.snippet,
                                                c.time,
                                                2,
                                                true,
                                                null,
                                                messageId = insertedId,
                                                threadId = messagebrod.threadId,
                                                isgroupmessage = true,
                                                groupName = participantsforgroupchat.getThreadTitle(),
                                                isarchived = isarchiv,
                                                isPrivateChat = isPrivateChat
                                            )
                                            messagerDatabaseRepo.insertmessage(conversation)
                                        }
                                    }
                                }

                            } else {
                                Log.d("list", "setMessage:list <----------> 3")
                            }

                        }

                    } else {
                        sendSMSManager.sendMessageCompat(messagebrod.snippet, listOf(messagebrod.phoneNumber), subscriptionId, arrayListOf(), false)
                    }
                }
                data?.messageId?.let { messagerDatabaseRepo.deletemessageRepo(it) }


            } catch (e: Exception) {
                runOnUiThread { showErrorToast(e) }
            } catch (e: Error) {
                runOnUiThread { showErrorToast(e.localizedMessage ?: getString(R.string.unknown_error_occurred)) }
            }
            if (data?.is_scheduled == true) {
                data?.messageId?.let { it1 -> cancelScheduleSendPendingIntent(it1) }
            }
        } else {
            runOnUiThread { toast(resources.getString(R.string.Something_Went_Wrong)) }
        }
    }

    override fun onStart() {
        super.onStart()
        ThemeSetup()
    }

    override fun onResume() {
        super.onResume()
        ThemeSetup()
    }

    private fun openScheduleComposerWithRewardGate() {
        adsOrchestrator.showScheduleRewarded(this) {
            openScheduleMessageComposer()
        }
    }

    private fun openScheduleMessageComposer() {
        Constants.isActivitychange = true
        startActivity(Intent(this, SelectContactActivity::class.java).putExtra("forscheduleMessage", true))
        finish()
    }

    private fun ThemeSetup() {
        val surfaceColor = getProperBackgroundColor()
        val statusBarColor = getProperStatusBarColor()
        val navigationBarColor = getBottomNavigationBackgroundColor()
        val textColor = getProperTextColor()
        val secondaryTextColor = getProperSecondaryTextColor()
        val primaryColor = getProperPrimaryColor()
        val fabCorner = resources.getDimension(com.intuit.sdp.R.dimen._50sdp)

        binding.root.setBackgroundColor(surfaceColor)
        binding.main.setBackgroundColor(surfaceColor)
        binding.toolBarMain.setBackgroundColor(surfaceColor)
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        binding.conversationsFastscroller.setBackgroundColor(surfaceColor)
        binding.allmassagerecycler.setBackgroundColor(surfaceColor)
        binding.textView3.setTextColor(textColor)
        binding.commanTxt.setTextColor(secondaryTextColor)
        binding.scheduleMessagePlaceholderAction.setTextColor(primaryColor)
        binding.backBtn.iconTint = ColorStateList.valueOf(secondaryTextColor)
        binding.conversationsFastscroller.updateColors(primaryColor)
        scheduleMessageShowAdapter.notifyDataSetChanged()

        binding.scheduleMessageSendBtn.setImageResource(R.drawable.baseline_send_24)
        binding.scheduleMessageSendBtn.background = createOptionBackground(
            cornerSize = fabCorner,
            fillColor = primaryColor,
            strokeWidth = 0f,
            strokeColor = primaryColor,
            showRipple = true,
            rippleColor = Color.WHITE.adjustAlpha(0.3f)
        )
        binding.scheduleMessageSendBtn.imageTintList =
            ColorStateList.valueOf(primaryColor.getContrastColor())
        val fabPadding = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._12sdp)
        binding.scheduleMessageSendBtn.setPadding(fabPadding, fabPadding, fabPadding, fabPadding)

        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars = ThemeModeManager.shouldUseLightSystemBars(this@Schedule_Message_Show_Activity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
    }

    private fun openEditDialog(pos: Int, list: ArrayList<Conversation>) {
        val builder = MaterialAlertDialogBuilder(this)
        val inflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_edit_text, null)
        val editText = dialogView.findViewById<TextInputEditText>(R.id.editText)
        editText.setText(list[pos].snippet)
        builder.setCancelable(false)
            .setTitle(R.string.Edit_message)
            .setPositiveButton("OK") { dialog, which -> }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }

        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.show()

        dialog.getButton(BUTTON_POSITIVE).setOnClickListener {
            val text = editText.getText().toString()
            if (text.trim().isEmpty()) {
                toastMess(resources.getString(R.string.Please_enter_message))
                openEditDialog(pos, list)
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    list[pos].messageId?.let { messagerDatabaseRepo.updateMessageRepo(it, text) }
                }
                dialog.dismiss()
            }
        }
    }


}
