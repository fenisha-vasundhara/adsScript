package com.messenger.phone.number.text.sms.service.apps

//import com.google.mlkit.nl.translate.TranslateLanguage
//import com.google.mlkit.nl.translate.Translation
//import com.google.mlkit.nl.translate.TranslatorOptions

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.format.DateUtils
import android.text.util.Linkify
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.demo.adsmanage.Commen.Constants
import com.messenger.phone.number.text.sms.service.apps.CommanClass.checkWebsiteSafety
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.containsURL
import com.messenger.phone.number.text.sms.service.apps.CommanClass.extractURL
import com.messenger.phone.number.text.sms.service.apps.CommanClass.extractUriFromString
import com.messenger.phone.number.text.sms.service.apps.CommanClass.fadeIn
import com.messenger.phone.number.text.sms.service.apps.CommanClass.fadeOut
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getCustomDrawable
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isEmojiOnly
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isPackageInstalled
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isSerchfoundmessage
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isValidUrl
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.Dialog.MaliciousWebDialog
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.Waitinggame.ui.GameActivity
import com.messenger.phone.number.text.sms.service.apps.WebUrlParser.OpenGraphCacheProvider
import com.messenger.phone.number.text.sms.service.apps.WebUrlParser.OpenGraphCallback
import com.messenger.phone.number.text.sms.service.apps.WebUrlParser.OpenGraphParser
import com.messenger.phone.number.text.sms.service.apps.WebUrlParser.OpenGraphResult
import com.messenger.phone.number.text.sms.service.apps.databinding.ItemMessageInAbBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.ItemMessageOutAbBinding
import com.messenger.phone.number.text.sms.service.apps.highlighter.TextHighlighter
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MessageSelectUnselect
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.Selectedmessagetraslateinterface
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Language
import com.simplemobiletools.commons.extensions.formatDate
import com.simplemobiletools.commons.extensions.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import javax.inject.Inject


class InMainAdapterAB @Inject constructor() : RecyclerView.Adapter<ViewHolder>() {


    @Inject
    lateinit var repo: MessagerDatabaseRepo

    var onMessageClick: ((Int, ArrayList<Conversation>) -> Unit)? = null
    var onMessageFaildClick: ((Int, ArrayList<Conversation>) -> Unit)? = null
    private var fontSize = 16f
    private val MESSAGE_IN: Int = 2
    private val MESSAGE_OUT: Int = 1
    var pos = -1
    var posnew = -1
    var traslatepos = -1
    var selectedMessageList: ArrayList<Conversation> = arrayListOf()
    lateinit var messageselectunselect: MessageSelectUnselect
    lateinit var selectedmessagetraslateinterface: Selectedmessagetraslateinterface
    lateinit var binding: ItemMessageInAbBinding
    private var package_name = "com.android.chrome";
    var avoidfisrttimecalling = false
    var signature = ""
    private lateinit var adapter: ArrayAdapter<Language>
    var inmessagecolorcustomwallpaper = "#4AA6FB"
    var outmessagecolorcustomwallpaper = "#E8F4FF"
    var iscustomwallpaperset = false
    var highlighttext = ""
    var originalList: ArrayList<Conversation> = arrayListOf()
//    val availableLanguages: List<Language> = TranslateLanguage.getAllLanguages().map { Language(it) }

    init {
        setHasStableIds(true)
    }

    inner class InMessageAdapterViewHolder(var binding: ItemMessageInAbBinding) :
        ViewHolder(binding.root) {
        var con = binding.appnametxt.context

        init {
            if (con.config.userpreferenceSignatureOnOff) {
                signature = if (con.config.userpreferenceSignature[0] == '-') {
                    con.config.userpreferenceSignature
                } else {
                    "-" + con.config.userpreferenceSignature
                }
            } else {
                signature = con.config.userpreferenceSignature
            }
            binding.nullchack = "null"
        }
    }

    inner class OutMessageAdapterViewHolder(var binding: ItemMessageOutAbBinding) :
        ViewHolder(binding.root), AdapterView.OnItemSelectedListener {

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long,
        ) {
            if (avoidfisrttimecalling) {
                avoidfisrttimecalling = false
                Log.d("", "onItemSelected: <----------> ")
            }
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            MESSAGE_IN -> {
                InMessageAdapterViewHolder(
                    ItemMessageInAbBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            MESSAGE_OUT -> {
                OutMessageAdapterViewHolder(
                    ItemMessageOutAbBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }

            else -> {
                InMessageAdapterViewHolder(
                    ItemMessageInAbBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("UseCompatLoadingForColorStateLists", "NotifyDataSetChanged", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        posnew = position
        fontSize = holder.itemView.context.getTextSizeMS()
        if (holder is InMessageAdapterViewHolder) {
            this@InMainAdapterAB.binding = holder.binding
            with(holder) {
                binding.textsizechage = fontSize
                binding.list = list[position]
                val txt = list[position].snippet
                val lines: List<String> = txt.split("\n")
                val lastLine: String = lines.get(lines.size - 1)
                if (lastLine.contains("<br><i>")) {
                    binding.appnametxt.text = Html.fromHtml(txt);
                } else {
                    binding.appnametxt.text = txt
                }
                highlitetext(highlighttext, holder.binding.appnametxt, Color.WHITE)
                if (list[position].is_scheduled) {
                    binding.scheduledMessageHolder.visible()
                    list[position].time?.let {
                        val dateTime = DateTime(it, DateTimeZone.forID("+05:30"))
                        binding.scheduledMessageButton.text =
                            if (dateTime.yearOfCentury().get() > DateTime.now().yearOfCentury()
                                    .get()
                            ) {
                                it.formatDate(binding.scheduledMessageButton.context)
                            } else {
                                val flags =
                                    DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_NO_YEAR
                                DateUtils.formatDateTime(
                                    binding.scheduledMessageButton.context,
                                    it,
                                    flags
                                )
                            }
                    }
                } else {
                    binding.scheduledMessageButton.text = ""
                    binding.scheduledMessageHolder.gone()
                }

                binding.itemclick.radius = binding.appnametxt.context.config.messagecorner
                binding.messagewebprecontenar.background = changeCornerRadius(
                    binding.appnametxt.context,
                    binding.datetxt.context.resources,
                    binding.appnametxt.context.config.messagecorner
                )
                binding.appnametxt.setLinkTextColor(Color.parseColor("#ffffff"));
                setOnClickLink(binding.appnametxt)
                if (containsURL(list[position].snippet)) {
                    val urllink = extractURL(list[position].snippet)
                    println("The string contains a valid URL.")
                    val listener: OpenGraphCallback = object : OpenGraphCallback {
                        override fun onPostResponse(openGraphResult: OpenGraphResult) {
                            binding.webUrlDis.text = openGraphResult.title
                            binding.webUrlWebsideName.text = openGraphResult.url
                            try {
                                Glide.with(binding.webUrlDis.context).load(openGraphResult.image)
                                    .into(binding.webUrlImage)
                            } catch (E: Exception) {
                            }
                            binding.messagewebprecontenar.visible()
                        }

                        override fun onError(error: String) {
                            binding.messagewebprecontenar.gone()
                        }
                    }
                    val openGraphParser by lazy {
                        OpenGraphParser(
                            listener = listener,
                            showNullOnEmpty = true,
                            cacheProvider = OpenGraphCacheProvider(binding.appnametxt.context)
                        )
                    }
                    if (urllink.isEmpty()) {
                        binding.messagewebprecontenar.gone()
                    } else {
                        openGraphParser.parse(urllink)
                    }

                } else {
                    println("No valid URL found in the string.")
                    binding.messagewebprecontenar.gone()
                }
                if (position == 0) {
                    binding.datetxt.visible()
                    binding.datetxtCon.visible()
                } else {
                    if (getdate(
                            list[position].time!!,
                            binding.datetxt.context
                        ) == getdate(list[position - 1].time!!, binding.datetxt.context)
                    ) {
                        binding.datetxt.gone()
                        binding.datetxtCon.gone()
                    } else {
                        binding.datetxt.visible()
                        binding.datetxtCon.visible()
                    }
                }

                binding.messagewebprecontenar.setOnClickListener {

                    openmapclick(list[position].snippet, binding.appnametxt)

                }

                binding.appnametxt.setOnLongClickListener {
                    isSerchfoundmessage = false

                    list.forEachIndexed { index, conversation ->
                        conversation.messagetraslateshow = false
                    }

                    try {
                        if (selectedMessageList.contains(list[position])) {
                            selectedMessageList.remove(list[position])
                        } else {
                            selectedMessageList.add(list[position])
                        }
                        messageselectunselect.onMessageSelect(
                            pos,
                            list[position].snippet!!,
                            selectedMessageList
                        )
                        notifyDataSetChanged()
                    } catch (_: Exception) {
                        Toast.makeText(binding.click.context, "try Again", Toast.LENGTH_SHORT)
                            .show()
                    }
                    true
                }
                binding.appnametxt.setOnClickListener {
                    if (selectedMessageList.isNotEmpty()) {
                        if (selectedMessageList.contains(list[position])) {
                            selectedMessageList.remove(list[position])
                        } else {
                            selectedMessageList.add(list[position])
                        }
                        messageselectunselect.onMessageSelect(
                            pos,
                            list[position].snippet!!,
                            selectedMessageList
                        )
                        notifyDataSetChanged()
                    } else {
                        if (isEmojiOnly(list[position].snippet)) {
                            binding.appnametxt.context.startActivity(
                                Intent(
                                    binding.appnametxt.context,
                                    GameActivity::class.java
                                )
                            )
                        } else {
                            if (!list[position].is_scheduled) {
                                list[position].isexpandmessageview =
                                    !list[position].isexpandmessageview
                                notifyDataSetChanged()
                            } else {
                                onMessageClick?.invoke(position, list)
                            }
                        }
                    }
                }

                binding.itemclick.setOnClickListener {
                    onMessageClick?.invoke(position, list)
                }

                if (list[position].messagetraslationanimationshow) {
                    binding.sendMessageTraslateIn.visible()
                } else {
                    binding.sendMessageTraslateIn.gone()
                }

                if (list[position].isnewmessagescroll) {
                    binding.newmessagecountandbottomscrollin.visible()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val count = list[position].phoneNumber.let {
                                repo.getnewmessagecountRepo(it, 1)
                            }
                            count.forEach {
                                if (it.phoneNumber == list[position].phoneNumber) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        binding.newmessagecountandbottomscrollin.text =
                                            binding.appnametxt.context.resources.getString(R.string.Unread) + " " + count.size + " " + binding.appnametxt.context.resources.getString(
                                                R.string.Message_new
                                            )
                                    }
                                }
                            }
                        } catch (_: Exception) {
                        }
                    }
                } else {
                    binding.newmessagecountandbottomscrollin.gone()
                }

                if (list[position].isexpandmessageview) {
                    binding.timecontenar.visible()
                } else {
                    binding.timecontenar.gone()
                }


                binding.messageTraslateBtnIn.setOnClickListener {
                    selectedmessagetraslateinterface.onClickTraslatebutton(list, position)
                }
                binding.messageTraslateBtnIn.setOnLongClickListener {
                    binding.datetxt.context.toast("Long")
                    true
                }

                if (selectedMessageList.contains(list[position])) {
                    binding.sectionbg.background =
                        getDrawable(binding.appnametxt.context, R.color.selectmessage)
                } else {
                    val context = binding.appnametxt.context

                    if (iscustomwallpaperset) {
//                        binding.sectionbg.background = unselectedgetDrawable(
//                            binding.appnametxt.context, getAlphaColor(inmessagecolorcustomwallpaper)
//                        )
                        binding.sectionbg.background = unselectedgetDrawable(
                            binding.appnametxt.context, inmessagecolorcustomwallpaper
                        )
                    } else {
                        binding.sectionbg.background = unselectedgetDrawable(
                            context,
                            inmessagecolorcustomwallpaper
                        )
                    }


                }

                if (isSerchfoundmessage) {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (list[position].messageId?.let { repo.isSelectedMessageRepo(it) } == true) {
                            CoroutineScope(Dispatchers.Main).launch {
                                binding.sectionbg.background =
                                    binding.sectionbg.context.getCustomDrawable(R.color.unselectmessagein)
                            }
                        }
                    }
                }

                if (list[position].messageStatus == "Error") {
                    funsetTextStatus(R.color.red, "Not sent,tap to try again.", true)
                } else if (list[position].messageStatus == "Sending") {
                    funsetTextStatus(R.color.black2, "Sending", true)
                } else if (list[position].messageStatus == "SMS delivered") {
                    funsetTextStatus(R.color.black2, "Sending", false)
                } else {
                    funsetTextStatus(R.color.black2, "Sending", false)
                }

                binding.messageFaild.setOnClickListener {
                    if (list[position].messageStatus == "Error") {
                        onMessageFaildClick?.invoke(position, list)
                    }
                }

            }
        }
        else if (holder is OutMessageAdapterViewHolder) {
            with(holder) {
                binding.textsizechage2 = fontSize
                binding.list = list[position]

//                if (isBase64(list[position].snippet)) {
//                    val decodedBytes = Base64.decode(list[position].snippet, Base64.DEFAULT)
//                    list[position].snippet =  String(decodedBytes, charset("UTF-8"))
//                }

                binding.appnametxt.text = list[position].snippet

                binding.itemclick.radius = binding.appnametxt.context.config.messagecorner
                binding.messagewebprecontenar.background = changeCornerRadius(
                    binding.appnametxt.context,
                    binding.datetxt.context.resources,
                    binding.appnametxt.context.config.messagecorner
                )
                highlitetext(highlighttext, holder.binding.appnametxt, Color.WHITE)
                setOnClickLink(binding.appnametxt)
                if (position == 0) {
                    binding.datetxt.visible()
                    binding.datetxtCon.visible()
//                    binding.dateline1.visible()
//                    binding.dateline2.visible()
                } else {
                    if (getdate(
                            list[position].time!!,
                            binding.datetxt.context
                        ) == getdate(list[position - 1].time!!, binding.datetxt.context)
                    ) {
                        binding.datetxt.gone()
                        binding.datetxtCon.gone()
//                        binding.dateline1.gone()
//                        binding.dateline2.gone()
                    } else {
                        binding.datetxt.visible()
                        binding.datetxtCon.visible()
//                        binding.dateline1.visible()
//                        binding.dateline2.visible()
                    }
                }

                binding.messagewebprecontenar.setOnClickListener {
                    openmapclick(list[position].snippet, binding.appnametxt)
                }

                binding.appnametxt.setOnLongClickListener {
//                    binding.messageTraslateBtnOut.gone()

//                    list.forEachIndexed { index, conversation ->
//                        conversation.messagetraslateshow = false
//                    }

                    isSerchfoundmessage = false
                    if (selectedMessageList.contains(list[position])) {
//                        list[position].messagetraslateshow = false
                        selectedMessageList.remove(list[position])
                    } else {
//                        list[position].messagetraslateshow = true
                        selectedMessageList.add(list[position])
                    }
                    messageselectunselect.onMessageSelect(
                        pos,
                        list[position].snippet!!,
                        selectedMessageList
                    )
                    notifyDataSetChanged()
                    true
                }
                binding.appnametxt.setOnClickListener {
//                    binding.messageTraslateBtnOut.gone()

//                    list.forEachIndexed { index, conversation ->
//                        conversation.messagetraslateshow = false
//                    }

                    if (selectedMessageList.isNotEmpty()) {
                        if (selectedMessageList.contains(list[position])) {
//                            list[position].messagetraslateshow = false
                            selectedMessageList.remove(list[position])
                        } else {
//                            list[position].messagetraslateshow = true
                            selectedMessageList.add(list[position])
                        }
                        messageselectunselect.onMessageSelect(
                            pos,
                            list[position].snippet!!,
                            selectedMessageList
                        )
                        notifyDataSetChanged()
                    } else {
                        list[position].isexpandmessageview = !list[position].isexpandmessageview
                        notifyDataSetChanged()
                    }
                }


                if (list[position].isexpandmessageview) {
                    binding.timecontenar.visible()
                } else {
                    binding.timecontenar.gone()
                }


//                if (list[position].isnewmessagescroll) {
//
//                    binding.newmessagecountandbottomscrollout.visible()
//
//                    CoroutineScope(Dispatchers.IO).launch {
//                        try {
//                            val count = list[position].phoneNumber.let {
//                                repo.getnewmessagecountRepo(it, 1)
//                            }
//                            count.forEach {
//                                if (it.phoneNumber == list[position].phoneNumber) {
//                                    CoroutineScope(Dispatchers.Main).launch { binding.newmessagecountandbottomscrollout.text = binding.appnametxt.context.resources.getString(R.string.Unread) + " " + count.size + " " + binding.appnametxt.context.resources.getString(R.string.Message_new) }
//                                }
//                            }
//                        } catch (_: Exception) {
//                        }
//                    }
//
//                } else {
//
//                    binding.newmessagecountandbottomscrollout.gone()
//
//                }

                if (list[position].isnewmessagescroll) {
                    binding.newmessagecountandbottomscrollout.fadeIn()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val count = list[position].phoneNumber.let {
                                repo.getnewmessagecountRepo(it, 1)
                            }
                            count.forEach {
                                if (it.phoneNumber == list[position].phoneNumber) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        binding.newmessagecountandbottomscrollout.text =
                                            binding.appnametxt.context.resources.getString(R.string.Unread) + " " + count.size + " " + binding.appnametxt.context.resources.getString(
                                                R.string.Message_new
                                            )
                                    }
                                }
                            }
                        } catch (_: Exception) {
                        }
                    }
                } else {
                    binding.newmessagecountandbottomscrollout.fadeOut()
                }


//                binding.newmessagecountandbottomscrollout.setOnClickListener {
//                    CoroutineScope(Dispatchers.IO).launch {   list[position].threadId?.let { repo.setiscrolltonewmessageoffRepo(it) } }
//                }

                if (list[position].messagetraslationanimationshow) {
                    binding.sendMessageTraslateOut.visible()
                } else {
                    binding.sendMessageTraslateOut.gone()
                }

                if (list[position].messagetraslateshow) {
//                    binding.messagetraslateoneminuteshowbtn.visible()
                } else {
//                    binding.messagetraslateoneminuteshowbtn.gone()
                }

                binding.messagetraslateoneminuteshowbtn.setOnClickListener {
                    selectedmessagetraslateinterface.onClickTraslatebutton(list, position)
                }

//                if (list[position].messagetraslateshow) {
//
//                    list[position].messagetraslateshow = true
//                } else {
//
//                    list[position].messagetraslateshow = false
//                }


                binding.messageTraslateBtnOut.setOnClickListener {
                    selectedmessagetraslateinterface.onClickTraslatebutton(list, position)
                }


                if (selectedMessageList.contains(list[position])) {
                    if (!binding.messagetraslateoneminuteshowbtn.isVisible) {
//                        binding.messageTraslateBtnOut.visible()
                    }
//                    binding.appnametxt.backgroundTintList = binding.itemclick.context.resources.getColorStateList(R.color.selectmessage)
                    binding.sectionbg.background =
                        getDrawable(binding.appnametxt.context, R.color.selectmessage)
                    binding.appnametxt.setTextColor(
                        ContextCompat.getColor(
                            binding.appnametxt.context,
                            R.color.white
                        )
                    )
                    binding.appnametxt.setLinkTextColor(Color.parseColor("#ffffff"));
                    binding.messagetime.setTextColor(
                        ContextCompat.getColor(
                            binding.appnametxt.context,
                            R.color.white
                        )
                    )

                } else {
//                    binding.appnametxt.backgroundTintList = binding.itemclick.context.resources.getColorStateList(R.color.unselectmessageout)

//                    binding.messageTraslateBtnOut.gone()


                    if (iscustomwallpaperset) {
//                        binding.sectionbg.background = unselectedgetDrawable(
//                            binding.appnametxt.context, getAlphaColor(outmessagecolorcustomwallpaper)
//                        )
                        binding.sectionbg.background = unselectedgetDrawable(
                            binding.appnametxt.context, outmessagecolorcustomwallpaper
                        )
                    } else {
                        binding.sectionbg.background = unselectedgetDrawable(
                            binding.appnametxt.context, outmessagecolorcustomwallpaper
                        )
                    }


//                    binding.sectionbg.background = getDrawable(binding.appnametxt.context, R.color.unselectmessageout)


                    binding.appnametxt.setTextColor(
                        ContextCompat.getColor(
                            binding.appnametxt.context, R.color.black2
                        )
                    )



                    binding.messagetime.setTextColor(
                        ContextCompat.getColor(
                            binding.appnametxt.context,
                            R.color.black2
                        )
                    )
                    binding.appnametxt.setLinkTextColor(Color.parseColor("#4AA6FB"));
                }

                if (isSerchfoundmessage) {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (list[position].messageId?.let { repo.isSelectedMessageRepo(it) } == true) {
                            CoroutineScope(Dispatchers.Main).launch {
                                binding.sectionbg.background =
                                    binding.sectionbg.context.getCustomDrawable(R.color.unselectmessageout)
                            }
                        }
                    }
                }
            }

            if (containsURL(list[position].snippet)) {
                val urllink = extractURL(list[position].snippet)
                println("The string contains a valid URL.")
                val listener: OpenGraphCallback = object : OpenGraphCallback {
                    override fun onPostResponse(openGraphResult: OpenGraphResult) {
                        holder.binding.webUrlDis.text = openGraphResult.title
                        holder.binding.webUrlWebsideName.text = openGraphResult.url
                        if (openGraphResult.image != null) {
                            try {
                                Glide.with(holder.binding.webUrlDis.context)
                                    .load(openGraphResult.image)
                                    .into(holder.binding.webUrlImage)
                            } catch (E: Exception) {
                            }
                        }
                        holder.binding.messagewebprecontenar.visible()
                    }

                    override fun onError(error: String) {
//                            Toast.makeText(binding.webUrlDis.context, "1"+error+" "+urllink, Toast.LENGTH_SHORT).show()
                        holder.binding.messagewebprecontenar.gone()
                    }
                }
                val openGraphParser by lazy {
                    OpenGraphParser(
                        listener = listener,
                        showNullOnEmpty = true,
                        cacheProvider = OpenGraphCacheProvider(holder.binding.appnametxt.context)
                    )
                }


                if (urllink.isEmpty()) {
                    holder.binding.messagewebprecontenar.gone()
//                        Toast.makeText(binding.webUrlDis.context, "2", Toast.LENGTH_SHORT).show()
                } else {
                    openGraphParser.parse(urllink)
                }

            } else {
                println("No valid URL found in the string.")
                holder.binding.messagewebprecontenar.gone()
            }
        }
    }

    private fun funsetTextStatus(textcolor: Int, message: String, visibleornot: Boolean) {
        if (visibleornot) {
            binding.messageFaild.visible()
        } else {
            binding.messageFaild.gone()
        }
        binding.messageFaild.text = message
        binding.messageFaild.setTextColor(binding.messageFaild.context.resources.getColor(textcolor))
    }

    fun openmapclick(uri: String, appnametxt: TextView) {
        if (appnametxt.context.config.maliciousWebsiteBtnSwichP) {
            val weburi = extractUriFromString(uri)
            if (weburi != null) {
                checkWebsiteSafety(weburi) { isSafe ->
                    if (isSafe) {
                        openmapurl(appnametxt, weburi)
                    } else {
                        val dialog = MaliciousWebDialog.newInstance()
                            .setOnPositiveClick {
                                openmapurl(appnametxt, weburi)
                            }
                        dialog.show(
                            (appnametxt.context as FragmentActivity).supportFragmentManager,
                            MaliciousWebDialog.TAG
                        )
                    }
                }
            }
        } else {
            val weburi = extractUriFromString(uri)
            if (weburi != null) {
                openmapurl(appnametxt, weburi)
            }
        }
    }

    private fun openmapurl(appnametxt: TextView, weburi: String) {
        Constants.isActivitychange = true
        if (appnametxt.context.config.isinappbrowser) {
            if (weburi != null) {
                if (!weburi.isValidUrl()) {
                    openlink(weburi, appnametxt)
                } else {
                    openinapplink(weburi, appnametxt)
                }
            }
        } else {
            if (weburi != null) {
                openlink(weburi, appnametxt)
            }
        }
    }


    private fun setOnClickLink(appnametxt: TextView) {
        Linkify.addLinks(appnametxt, Linkify.ALL)
        appnametxt.movementMethod = BetterLinkMovementMethod.getInstance()
        appnametxt.movementMethod = BetterLinkMovementMethod.newInstance().apply {
            setOnLinkClickListener { textView, url ->
                if (appnametxt.context.config.maliciousWebsiteBtnSwichP) {
                    checkWebsiteSafety(url) { isSafe ->
                        if (isSafe) {
                            linkopenclick(appnametxt, url)
                        } else {
                            val dialog = MaliciousWebDialog.newInstance()
                                .setOnPositiveClick {
                                    linkopenclick(appnametxt, url)
                                }
                            dialog.show(
                                (appnametxt.context as FragmentActivity).supportFragmentManager,
                                MaliciousWebDialog.TAG
                            )
                        }
                    }
                } else {
                    Log.d("jigar", "setOnClickLink: setOnLinkClickListener <---------> 1 ${url}")
                    linkopenclick(appnametxt, url)
                }
                true
            }
            setOnLinkLongClickListener { _, _ ->
                Log.d("jigar", "setOnClickLink: setOnLinkClickListener <---------> 2")
                true
            }
        }
    }

    private fun linkopenclick(appnametxt: TextView, url: String) {
        Log.d("jigar", "setOnClickLink: setOnLinkClickListener <---------> 1 ${url}")
        Constants.isActivitychange = true
        if (appnametxt.context.config.isinappbrowser) {
            if (!url.isValidUrl()) {
                openlink(url, appnametxt)
            } else {
                openinapplink(url, appnametxt)
            }
        } else {
            openlink(url, appnametxt)
        }
    }


    var list = ArrayList<Conversation>()
        set(value) {
            field = value
            originalList.addAll(value)
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].type == 1) {
            MESSAGE_OUT
        } else {
            MESSAGE_IN
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setInterface(messageselectunselect: MessageSelectUnselect) {
        this.messageselectunselect = messageselectunselect
    }

    fun setOnLangInterface(selectedmessagetraslateinterface: Selectedmessagetraslateinterface) {
        this.selectedmessagetraslateinterface = selectedmessagetraslateinterface
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getdate(time: Long, context: Context): String {
        return try {
            val date2 = Date(time)
            val format = SimpleDateFormat("yyyy-MM-dd")
            val dateString = format.format(date2)
            val date = LocalDate.parse(dateString)
            val formatter =
                DateTimeFormatter.ofPattern("dd MMM", Locale(context.config.SelectedLanguage))
            date.format(formatter)
        } catch (e: Exception) {
            "12 May"
        }
    }

    fun getDrawable(context: Context, id: Int): Drawable? {
        val version = Build.VERSION.SDK_INT
        return if (version >= 21) {
            ContextCompat.getDrawable(context, id)
        } else {
            context.resources.getDrawable(id)
        }
    }

    fun unselectedgetDrawable(context: Context, colorCode: String): Drawable? {
        return ColorDrawable(Color.parseColor(colorCode))
    }

    //
//    fun translatemessage(snippet: String, appnametxt: TextView, context: Context, code: String) {
//        val targetLangCode = TranslateLanguage.fromLanguageTag(code)!!
//        val translator = TranslatorOptions.Builder()
//            .setSourceLanguage(TranslateLanguage.ENGLISH) // Source language
//            .setTargetLanguage(targetLangCode) // Target language
//            .build()
//
//        val textTranslator = Translation.getClient(translator)
//
//        textTranslator.downloadModelIfNeeded()
//            .addOnSuccessListener {
//                textTranslator.translate(snippet)
//                    .addOnSuccessListener { translatedText ->
//                        // Handle the translated text
//                        appnametxt.text = translatedText
//                        Log.d("Translation", translatedText)
//                    }
//                    .addOnFailureListener { exception ->
//                        // Handle translation error
////                        context.toast("Translation failed: $exception")
//                        Log.e("Translation", "Translation failed: $exception")
//                    }
//            }
//            .addOnFailureListener { exception ->
//                // Handle model download error
////                context.toast("Model download failed: $exception")
//                Log.e("Translation", "Model download failed: $exception")
//            }
//
//    }

    fun changeCornerRadius(
        context: Context,
        resources: Resources,
        cornerRadius: Float,
    ): GradientDrawable {
        // Inflate the drawable resource
        val drawable =
            ContextCompat.getDrawable(context, R.drawable.map_bg_linr) as GradientDrawable

        // Set new corner radius
        drawable.cornerRadii = floatArrayOf(
            0f, 0f,  // Top-left corner
            0f, 0f,  // Top-right corner
            cornerRadius, cornerRadius,  // Bottom-right corner
            cornerRadius, cornerRadius   // Bottom-left corner
        )

        return drawable
    }

    fun openlink(url: String, appnametxt: TextView) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            appnametxt.context.startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
            appnametxt.context.toastMess("No web browser app found")
        }
    }

    fun openinapplink(url: String, appnametxt: TextView) {
        val context = appnametxt.context
        val builder = CustomTabsIntent.Builder()
        val params = CustomTabColorSchemeParams.Builder()
        params.setToolbarColor(ContextCompat.getColor(context, R.color.appcolor))
        builder.setDefaultColorSchemeParams(params.build())
        builder.setShowTitle(true)
        builder.setShareState(CustomTabsIntent.SHARE_STATE_ON)
        builder.setInstantAppsEnabled(true)
        val customBuilder = builder.build()
        if (context.isPackageInstalled(package_name)) {
            customBuilder.intent.setPackage(package_name)
            try {
                customBuilder.launchUrl(context, Uri.parse(url))
            } catch (e: Exception) {
                openlink(url, appnametxt)
            }
        } else {
            openlink(url, appnametxt)
        }
    }

    private fun highlitetext(highlighttext: String, appnametxt: TextView, black: Int) {
        TextHighlighter()
            .setBackgroundColor(Color.parseColor("#F69191"))
            .setForegroundColor(black)
            .addTarget(appnametxt)
            .highlight(highlighttext, TextHighlighter.CASE_INSENSITIVE_MATCHER);
    }

}
