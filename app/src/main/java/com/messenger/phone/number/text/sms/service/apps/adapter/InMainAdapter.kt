package com.messenger.phone.number.text.sms.service.apps.adapter

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

//import com.google.mlkit.nl.translate.TranslateLanguage
//import com.google.mlkit.nl.translate.Translation
//import com.google.mlkit.nl.translate.TranslatorOptions

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.format.DateUtils
import android.text.util.Linkify
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.demo.adsmanage.Commen.Constants
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.checkWebsiteSafety
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.containsURL
import com.messenger.phone.number.text.sms.service.apps.CommanClass.extractURL
import com.messenger.phone.number.text.sms.service.apps.CommanClass.extractUriFromString
import com.messenger.phone.number.text.sms.service.apps.CommanClass.fadeIn
import com.messenger.phone.number.text.sms.service.apps.CommanClass.fadeOut
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getAlphaColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getCustomDrawable
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isEmojiOnly
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isImageMimeType
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isPackageInstalled
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isSerchfoundmessage
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isVCardMimeType
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isValidUrl
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isVideoMimeType
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.Dialog.MaliciousWebDialog
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.SendMessageActivity.Companion.maxChatBubbleWidth
import com.messenger.phone.number.text.sms.service.apps.Waitinggame.ui.GameActivity
import com.messenger.phone.number.text.sms.service.apps.WebUrlParser.OpenGraphCacheProvider
import com.messenger.phone.number.text.sms.service.apps.WebUrlParser.OpenGraphCallback
import com.messenger.phone.number.text.sms.service.apps.WebUrlParser.OpenGraphParser
import com.messenger.phone.number.text.sms.service.apps.WebUrlParser.OpenGraphResult
import com.messenger.phone.number.text.sms.service.apps.data.Attachment
import com.messenger.phone.number.text.sms.service.apps.databinding.ItemAttachmentDocumentBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.ItemAttachmentImageBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.ItemMessageInBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.ItemMessageOutBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.SendMessageBannerAdItemBinding
import com.messenger.phone.number.text.sms.service.apps.helper.setupDocumentPreview
import com.messenger.phone.number.text.sms.service.apps.highlighter.TextHighlighter
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MessageSelectUnselect
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.Selectedmessagetraslateinterface
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Language
import com.simplemobiletools.commons.extensions.beGone
import com.simplemobiletools.commons.extensions.beVisible
import com.simplemobiletools.commons.extensions.beVisibleIf
import com.simplemobiletools.commons.extensions.formatDate
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
import androidx.core.graphics.toColorInt
import com.google.android.material.color.MaterialColors
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getDialogBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperStatusBarColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS


class InMainAdapter @Inject constructor() : RecyclerView.Adapter<ViewHolder>() {

    companion object {
        private const val PAYLOAD_THEME = "payload_theme"
    }

    var onMessageFaildClick: ((Int, ArrayList<Conversation>) -> Unit)? = null
    var viewimage: ((Uri, String, String) -> Unit)? = null

    @Inject
    lateinit var repo: MessagerDatabaseRepo
    private var fontSize = 16f
    private val MESSAGE_IN: Int = 2
    private val MESSAGE_OUT: Int = 1
    var pos = -1
    var posnew = -1

    var onMessageClick: ((Int, ArrayList<Conversation>) -> Unit)? = null
    var onMessageForwardClick: ((Int, ArrayList<Conversation>) -> Unit)? = null
    var traslatepos = -1
    var selectedMessageList: ArrayList<Conversation> = arrayListOf()
    lateinit var messageselectunselect: MessageSelectUnselect
    lateinit var selectedmessagetraslateinterface: Selectedmessagetraslateinterface
    lateinit var binding: ItemMessageInBinding
    private var package_name = "com.android.chrome";
    var avoidfisrttimecalling = false
    var signature = ""
    private lateinit var adapter: ArrayAdapter<Language>
    var inmessagecolorcustomwallpaper = ""
    var outmessagecolorcustomwallpaper = ""
    var iscustomwallpaperset = false
    var highlighttext = ""
    var originalList: ArrayList<Conversation> = arrayListOf()
    lateinit var rippleState: ColorStateList
    lateinit var baseRipple: RippleDrawable
    init {
        setHasStableIds(true)
    }

    inner class InMessageAdapterViewHolder(var binding: ItemMessageInBinding) :
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

    inner class BannerAdViewHolder(var binding: SendMessageBannerAdItemBinding) :
        ViewHolder(binding.root)

    inner class OutMessageAdapterViewHolder(var binding: ItemMessageOutBinding) :
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
    private fun isLightChatTheme(context: Context): Boolean {
        return ThemeModeManager.shouldUseLightSystemBars(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {



        var  rippleAlpha = if (isLightChatTheme(parent.context)) 0.12f else 0.28f
        var  rippleColor = MaterialColors.layer(parent.context.getProperBackgroundColor(), parent.context.getProperTextColor(), rippleAlpha)
           rippleState = ColorStateList.valueOf(rippleColor)
        val mask = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.WHITE)
        }
         baseRipple = RippleDrawable(ColorStateList.valueOf(rippleColor), null, mask)
        return when (viewType) {


            MESSAGE_IN -> {



                InMessageAdapterViewHolder(
                    ItemMessageInBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            MESSAGE_OUT -> {
                OutMessageAdapterViewHolder(
                    ItemMessageOutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                InMessageAdapterViewHolder(
                    ItemMessageInBinding.inflate(
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

    fun notifyThemeChanged() {
        if (itemCount == 0) return
        notifyItemRangeChanged(0, itemCount, PAYLOAD_THEME)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.contains(PAYLOAD_THEME)) {
            when (holder) {
                is InMessageAdapterViewHolder -> applyDateHeaderTheme(holder.binding)
                is OutMessageAdapterViewHolder -> applyDateHeaderTheme(holder.binding)
            }
            return
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    @SuppressLint("UseCompatLoadingForColorStateLists", "NotifyDataSetChanged", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        posnew = position
        val data = list[position]
        fontSize = holder.itemView.context.getTextSizeForeNormal13MS() * 0.95f
        if (holder is InMessageAdapterViewHolder) {
            this@InMainAdapter.binding = holder.binding
            with(holder) {
                binding.textsizechage = fontSize
                binding.list = list[position]
                val txt = list[position].snippet
                if (txt == "converremoved_12345") {
                    binding.mainItemClick.gone()
                } else {
                    binding.mainItemClick.visible()
                }
                val lines: List<String> = txt.split("\n")
                val lastLine: String = lines.get(lines.size - 1)

                applyIncomingTextColors(binding)

                applyDateHeaderTheme(binding)

                if (lastLine.contains("<br><i>")) {
                    binding.appnametxt.text = Html.fromHtml(txt);
                } else {
                    binding.appnametxt.text = txt
                }

                Log.d(
                    "",
                    "onBindViewHolder:messagewithattachment <----------> 11 ${list[position]}"
                )
                if (data.messagewithattachment?.attachments?.isNotEmpty() == true) {
                    Log.d("", "onBindViewHolder:messagewithattachment <----------> 1")
                    if (data.snippet.isEmpty()) {
                        holder.binding.itemclick.gone()
                    } else {
                        holder.binding.itemclick.visible()
                    }
                    binding.threadMessageAttachmentsHolder.beVisible()
                    binding.threadMessageAttachmentsHolder.removeAllViews()
                    for (attachment in data.messagewithattachment!!.attachments) {
                        val mimetype = attachment.mimetype
                        when {
                            mimetype.isImageMimeType() || mimetype.isVideoMimeType() -> setupImageView(
                                holder,
                                binding = binding,
                                data,
                                attachment,
                                position
                            )

                            mimetype.isVCardMimeType() -> setupVCardView(
                                holder,
                                binding.threadMessageAttachmentsHolder,
                                data,
                                attachment,
                                position
                            )

                            else -> {
                                setupFileView(
                                    holder,
                                    binding.threadMessageAttachmentsHolder,
                                    data,
                                    attachment,
                                    position
                                )
                            }
                        }
                        binding.threadMessagePlayOutline.beVisibleIf(mimetype.startsWith("video/"))
                    }
                } else {
                    Log.d("", "onBindViewHolder:messagewithattachment <----------> 2")
                    binding.attachmentImage.gone()
                    binding.threadMessageAttachmentsHolder.beGone()
                    binding.threadMessagePlayOutline.beGone()
                    binding.itemclick.visible()
                }

                binding.scheduledMessageButton.isSelected = true
                binding.ScheduleMessage.isSelected = true
                highlitetext(highlighttext, holder.binding.appnametxt, Color.BLACK)
                if (list[position].is_scheduled) {
                    binding.scheduledMessageHolder.gone()
                    binding.sendbutton.backgroundTintList = ColorStateList.valueOf( binding.sendbutton.context.getProperPrimaryColor())

                    binding.sendbutton.visible()
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
                    binding.sendbutton.gone()
                }
                binding.itemclick.radius = binding.appnametxt.context.config.messagecorner

                binding.messagewebprecontenar.background = changeCornerRadius(
                    binding.appnametxt.context,
                    binding.datetxt.context.resources,
                    binding.appnametxt.context.config.messagecorner
                )

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
                    setLongOnclickIn(position)
                    true
                }

                binding.lytStatusContainer.setOnLongClickListener {
                    setLongOnclickIn(position)
                    true
                }

                binding.selectedLayerClick.setOnLongClickListener {
                    setLongOnclickIn(position)
                    true
                }

                binding.itemclick.setOnLongClickListener {
                    setLongOnclickIn(position)
                    true
                }

                binding.appnametxt.setOnClickListener {
                    setOnclickIn(position)
                }

                binding.lytStatusContainer.setOnClickListener {
                    setOnclickIn(position)
                }

                binding.selectedLayerClick.setOnClickListener {
                    setOnclickIn(position)
                }

                binding.itemclick.setOnClickListener {
                    setOnclickIn(position)
                }

                binding.scheduledMessageHolder.setOnClickListener {
                    onMessageClick?.invoke(position, list)
                }

                binding.sendbutton.setOnClickListener {
                    onMessageClick?.invoke(position, list)
                }

                binding.messageForwardInBtn.setOnClickListener {
                    onMessageForwardClick?.invoke(position, list)
                }

                if (list[position].messagetraslationanimationshow) {
                    binding.sendMessageTraslateIn.visible()
                } else {

                    binding.sendMessageTraslateIn.gone()
                }

                if (list[position].isnewmessagescroll) {
                    binding.newmessagecountandbottomscrollin.visibility = View.VISIBLE
                    binding.viewone.visibility = View.VISIBLE
                    binding.viewtwo.visibility = View.VISIBLE
                    val unreadCount = list.count { it.isnewmessage == true }
                    binding.newmessagecountandbottomscrollin.text = formatUnreadLabel(
                        binding.appnametxt.context,
                        unreadCount
                    )
                } else {
                    binding.newmessagecountandbottomscrollin.fadeOut()
                    binding.viewone.fadeOut()
                    binding.viewtwo.fadeOut()
                }

                binding.messageTraslateBtnIn.setOnClickListener {
                    selectedmessagetraslateinterface.onClickTraslatebutton(list, position)
                }



                val incomingBubbleHex = colorToHex(resolveIncomingBubbleColor(binding.appnametxt.context))
                if (selectedMessageList.contains(list[position])) {
                    val context = binding.appnametxt.context
                    binding.sectionbg.background = messageInDrawable(
                        context,
                        incomingBubbleHex
                    )

                    binding.selectedLayer.visible()
                    binding.selectedLayer.background = unselectedgetDrawable(
                        binding.appnametxt.context, getAlphaColor(
                            colorToHex(
                                ColorUtils.blendARGB(
                                    Color.parseColor(incomingBubbleHex),
                                    context.getProperPrimaryColor(),
                                    0.35f
                                )
                            )
                        )
                    )
                } else {
                    val context = binding.appnametxt.context
                    binding.selectedLayer.gone()


                    binding.sectionbg.background = messageInDrawable(
                        context,
                        incomingBubbleHex
                    )


                }

                if (isSerchfoundmessage) {

                    CoroutineScope(Dispatchers.IO).launch {
                        if (list[position].messageId?.let { repo.isSelectedMessageRepo(it) } == true) {
                            CoroutineScope(Dispatchers.Main).launch {
//                                binding.sectionbg.background = binding.sectionbg.context.getCustomDrawable(R.color.unselectmessagein)
                        binding.selectedLayerClick.setBackgroundColor(binding.selectedLayerClick.context.getDialogBackgroundColor().adjustAlpha(0.55f))
                            }
                        }
                    }
                }else{

                }
                if (list[position].messageStatus == "Error"
                    || list[position].messageStatus == "Generic failure"
                    || list[position].messageStatus == "No service"
                    || list[position].messageStatus == "Null PDU"
                    || list[position].messageStatus == "Radio off"
                    || list[position].messageStatus == "SMS not delivered"
                ) {

                    funsetTextStatus(R.color.red, "Not sent,tap to try again.", true, position)
                } else if (list[position].messageStatus == "Sending") {
                    funsetTextStatus(binding.messageFaild.context.getProperTextColor(), "Sending", true, position)
                } else if (list[position].messageStatus == "SMS delivered") {
                    funsetTextStatus(binding.messageFaild.context.getProperTextColor(), "SMS delivered", false, position)
                } else {
                    funsetTextStatus(binding.messageFaild.context.getProperTextColor(), "SMS delivered", false, position)
                }

                binding.messageFaild.setOnClickListener {
                    try {
                        if (list[position].messageStatus == "Error"
                            || list[position].messageStatus == "Generic failure"
                            || list[position].messageStatus == "No service"
                            || list[position].messageStatus == "Null PDU"
                            || list[position].messageStatus == "Radio off"
                            || list[position].messageStatus == "SMS not delivered"
                        ) {
                            onMessageFaildClick?.invoke(position, list)
                        }
                    } catch (E: Exception) {
                    }
                }

            }
        }
        else if (holder is OutMessageAdapterViewHolder) {
            with(holder) {
                binding.textsizechage2 = fontSize
                binding.list = list[position]
                binding.appnametxt.text = list[position].snippet
                highlitetext(highlighttext, holder.binding.appnametxt, Color.BLACK)
                binding.itemclick.radius = binding.appnametxt.context.config.messagecorner




                applyDateHeaderTheme(binding)


                binding.messagewebprecontenar.background = changeCornerRadius(
                    binding.appnametxt.context,
                    binding.datetxt.context.resources,
                    binding.appnametxt.context.config.messagecorner
                )

                    setOnClickLink(binding.appnametxt)


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

                if (data.messagewithattachment?.attachments?.isNotEmpty() == true) {
                    if (data.snippet.isEmpty()) {
                        holder.binding.itemclick.gone()
                    } else {
                        holder.binding.itemclick.visible()
                    }
                    binding.threadMessageAttachmentsHolder.beVisible()
                    binding.threadMessageAttachmentsHolder.removeAllViews()
                    for (attachment in data.messagewithattachment!!.attachments) {
                        val mimetype = attachment.mimetype
                        when {
                            mimetype.isImageMimeType() || mimetype.isVideoMimeType() -> setupImageOutView(
                                holder,
                                binding = binding,
                                data,
                                attachment,
                                position
                            )

//                            mimetype.isVCardMimeType() -> setupVCardView(
//                                holder,
//                                binding.threadMessageAttachmentsHolder,
//                                data,
//                                attachment
//                            )

                            else -> {
                                setupFileViewOut(
                                    holder,
                                    binding.threadMessageAttachmentsHolder,
                                    data,
                                    attachment,
                                    position
                                )
                            }
                        }
                        binding.threadMessagePlayOutline.beVisibleIf(mimetype.startsWith("video/"))
                    }
                } else {
                    binding.threadMessageAttachmentsHolder.beGone()
                    binding.threadMessagePlayOutline.beGone()
                    binding.itemclick.visible()
                }


                binding.messagewebprecontenar.setOnClickListener {
                    openmapclick(list[position].snippet, binding.appnametxt)
                }


                //                setonselectedout <-------->
                binding.itemclick.setOnClickListener {
                    setonselectedout(position)
                }
/*
                binding.appnametxt.setOnClickListener {
                    setonselectedout(position)
                }

                binding.lytStatusContainer.setOnClickListener {
                    setonselectedout(position)
                }

                binding.selectedLayerClick.setOnClickListener {
                    setonselectedout(position)
                }

                */

                //                setonlongselectedout  <-------->

                binding.appnametxt.setOnLongClickListener {
                    setonlongselectedout(position)
                    true
                }

                binding.itemclick.setOnLongClickListener {
                    setonlongselectedout(position)
                    true
                }

                binding.lytStatusContainer.setOnLongClickListener {
                    setonlongselectedout(position)
                    true
                }

                binding.selectedLayerClick.setOnLongClickListener {
                    setonlongselectedout(position)
                    true
                }

                binding.messageForwardOutBtn.setOnClickListener {
                    onMessageForwardClick?.invoke(position, list)
                }


                if (list[position].isexpandmessageview) {
//                    binding.timecontenar.visible()
                } else {
//                    binding.timecontenar.gone()
                }


                if (list[position].isnewmessagescroll) {
                    binding.newmessagecountandbottomscrollout.visibility = View.VISIBLE
                    binding.viewone.visibility = View.VISIBLE
                    binding.viewtwo.visibility = View.VISIBLE
                    val unreadCount = list.count { it.isnewmessage == true }
                    binding.newmessagecountandbottomscrollout.text = formatUnreadLabel(
                        binding.appnametxt.context,
                        unreadCount
                    )
                } else {
                    binding.newmessagecountandbottomscrollout.visibility = View.GONE
                    binding.viewone.visibility = View.GONE
                    binding.viewtwo.visibility = View.GONE
                }



                if (list[position].messagetraslationanimationshow) {
                    binding.sendMessageTraslateOut.visible()
                } else {
                    binding.sendMessageTraslateOut.gone()
                }
                binding.messagetraslateoneminuteshowbtn.setOnClickListener {
                    selectedmessagetraslateinterface.onClickTraslatebutton(list, position)
                }
                binding.messageTraslateBtnOut.setOnClickListener {
                    selectedmessagetraslateinterface.onClickTraslatebutton(list, position)
                }

                applyOutgoingTextColors(binding)
                val outgoingBubbleHex = colorToHex(resolveOutgoingBubbleColor(binding.appnametxt.context))




                if (selectedMessageList.contains(list[position])) {
                    val context = binding.appnametxt.context
                    binding.sectionbg.background = unselectedgetDrawable(
                        binding.appnametxt.context,
                        outgoingBubbleHex
                    )
                    binding.selectedLayer.visible()
                    binding.selectedLayer.background = unselectedgetDrawable(
                        binding.appnametxt.context, getAlphaColor(
                            colorToHex(
                                ColorUtils.blendARGB(
                                    Color.parseColor(outgoingBubbleHex),
                                    context.getProperPrimaryColor(),
                                    0.35f
                                )
                            )
                        )
                    )
                } else {
                    binding.selectedLayer.gone()
//                    if (iscustomwallpaperset) {
//                        binding.sectionbg.background = unselectedgetDrawable(
//                            binding.appnametxt.context, outmessagecolorcustomwallpaper
//                        )
//                    } else {
//                        binding.sectionbg.background = unselectedgetDrawable(
//                            binding.appnametxt.context, outmessagecolorcustomwallpaper
//                        )
//                    }


                    binding.sectionbg.background = unselectedgetDrawable(
                        binding.appnametxt.context,
                        outgoingBubbleHex
                    )
                }
                if (isSerchfoundmessage) {


                    CoroutineScope(Dispatchers.IO).launch {
                        if (list[position].messageId?.let { repo.isSelectedMessageRepo(it) } == true) {
                            CoroutineScope(Dispatchers.Main).launch {
//                                binding.sectionbg.background =      binding.sectionbg.context.getCustomDrawable(R.color.unselectmessagein)
                                binding.selectedLayerClick.setBackgroundColor(binding.selectedLayerClick.context.getDialogBackgroundColor().adjustAlpha(0.55f))


                            }
                        }
                    }
                }else{

                }

            }

            if (containsURL(list[position].snippet)) {
                val urllink = extractURL(list[position].snippet)
                println("The string contains a valid URL.")
                val listener: OpenGraphCallback = object : OpenGraphCallback {
                    override fun onPostResponse(openGraphResult: OpenGraphResult) {
                        try {
                            holder.binding.webUrlDis.text = openGraphResult.title
                            holder.binding.webUrlWebsideName.text = openGraphResult.url
                            Glide.with(holder.binding.webUrlDis.context).load(openGraphResult.image)
                                .into(holder.binding.webUrlImage)
                            holder.binding.messagewebprecontenar.visible()
                        } catch (_: Exception) {
                        }
                    }

                    override fun onError(error: String) {
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
                } else {
                    openGraphParser.parse(urllink)
                }

            } else {
                println("No valid URL found in the string.")
                holder.binding.messagewebprecontenar.gone()
            }
        }
    }

    private fun applyDateHeaderTheme(binding: ItemMessageInBinding) {
        binding.datetxt.setTextColor(binding.root.context.getProperTextColor())
        binding.datetxtCon.setCardBackgroundColor(binding.root.context.getProperBackgroundColor())
    }

    private fun applyDateHeaderTheme(binding: ItemMessageOutBinding) {
        binding.datetxt.setTextColor(binding.root.context.getProperTextColor())
        binding.datetxtCon.setCardBackgroundColor(binding.root.context.getProperBackgroundColor())
    }



    private fun setupVCardView(
        holder: InMessageAdapterViewHolder,
        threadMessageAttachmentsHolder: LinearLayout,
        data: Conversation,
        attachment: Attachment,
        position: Int,
    ) {
        val uri = attachment.getUri()
    }

    private fun setupFileView(
        holder: InMessageAdapterViewHolder,
        parent: LinearLayout,
        data: Conversation,
        attachment: Attachment,
        position: Int,
    ) {

        val mimetype = attachment.mimetype
        val uri = attachment.getUri()
        val attachmentView =
            ItemAttachmentDocumentBinding.inflate(LayoutInflater.from(holder.binding.appnametxt.context))
                .apply {
                    setupDocumentPreview(
                        uri = uri,
                        title = attachment.filename,
                        mimeType = attachment.mimetype,
                        onClick = {
                            if (selectedMessageList.isNotEmpty()) {
                                updateSelection(position)
                                notifyRowChanged(position)

                            } else {
                                viewimage?.invoke(uri, mimetype, attachment.filename)
                            }
                        },
                        onLongClick = {
                            updateSelection(position)
                            notifyRowChanged(position)
                        }
                    )
                }.root
        parent.addView(attachmentView)
    }

    private fun setupFileViewOut(
        holder: OutMessageAdapterViewHolder,
        parent: LinearLayout,
        data: Conversation,
        attachment: Attachment,
        position: Int,
    ) {

        val mimetype = attachment.mimetype
        val uri = attachment.getUri()
        val attachmentView =
            ItemAttachmentDocumentBinding.inflate(LayoutInflater.from(holder.binding.appnametxt.context))
                .apply {
                    setupDocumentPreview(
                        uri = uri,
                        title = attachment.filename,
                        mimeType = attachment.mimetype,
                        onClick = {
                            if (selectedMessageList.isNotEmpty()) {
                                updateSelection(position)
                                notifyRowChanged(position)

                            } else {
                                viewimage?.invoke(uri, mimetype, attachment.filename)
                            }
                        },
                        onLongClick = {
                            updateSelection(position)
                            notifyRowChanged(position)
                        }
                    )
                }.root
        parent.addView(attachmentView)
    }


    private fun setupImageView(
        holder: InMessageAdapterViewHolder,
        binding: ItemMessageInBinding,
        data: Conversation,
        attachment: Attachment,
        position: Int,
    ) = binding.apply {
        val mimetype = attachment.mimetype
        val uri = attachment.getUri()
        Log.d("", "onBindViewHolder:messagewithattachment <----------> POPOP->$attachment")
        val imageView =
            ItemAttachmentImageBinding.inflate(LayoutInflater.from(holder.binding.appnametxt.context))
        threadMessageAttachmentsHolder.addView(imageView.root)

        val placeholderDrawable = ColorDrawable(Color.TRANSPARENT)
        val isTallImage = attachment.height > attachment.width
        val transformation = if (isTallImage) CenterCrop() else FitCenter()
        val options = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(placeholderDrawable)
//            .transform(transformation)

        var builder = Glide.with(root.context)
            .load(uri)
            .apply(options)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean,
                ): Boolean {
                    Log.d("", "onBindViewHolder:messagewithattachment <----------> 4")
                    threadMessagePlayOutline.beGone()
                    threadMessageAttachmentsHolder.removeView(imageView.root)
                    return false
                }

                override fun onResourceReady(
                    dr: Drawable,
                    a: Any,
                    t: Target<Drawable>,
                    d: DataSource,
                    i: Boolean,
                ) = false
            })

        // limit attachment sizes to avoid causing OOM
//        var wantedAttachmentSize = Size(attachment.width, attachment.height)
//        if (wantedAttachmentSize.width > maxChatBubbleWidth) {
//            val newHeight =
//                wantedAttachmentSize.height / (wantedAttachmentSize.width / maxChatBubbleWidth)
//            wantedAttachmentSize = Size(maxChatBubbleWidth.toInt(), newHeight.toInt())
//        }
////
//        builder = if (isTallImage) {
//            builder.override(wantedAttachmentSize.width, wantedAttachmentSize.width)
//        } else {
//            builder.override(wantedAttachmentSize.width, wantedAttachmentSize.height)
//        }
        builder = builder.override(500, 500)
        try {
            builder.into(imageView.attachmentImage)
        } catch (ignore: Exception) {
        }

        imageView.attachmentImage.setOnClickListener {

            if (selectedMessageList.isNotEmpty()) {
                if (selectedMessageList.contains(this@InMainAdapter.list[position])) {
                    selectedMessageList.remove(this@InMainAdapter.list[position])
                } else {
                    selectedMessageList.add(this@InMainAdapter.list[position])
                }
                messageselectunselect.onMessageSelect(
                    pos,
                    this@InMainAdapter.list[position].snippet!!,
                    selectedMessageList
                )
                notifyDataSetChanged()

            } else {
                viewimage?.invoke(uri, mimetype, attachment.filename)
            }
        }
        imageView.root.setOnLongClickListener {
            if (selectedMessageList.contains(this@InMainAdapter.list[position])) {
                selectedMessageList.remove(this@InMainAdapter.list[position])
            } else {
                selectedMessageList.add(this@InMainAdapter.list[position])
            }
            messageselectunselect.onMessageSelect(
                pos,
                this@InMainAdapter.list[position].snippet!!,
                selectedMessageList
            )
            notifyDataSetChanged()
            true
        }
    }


    private fun setupImageOutView(
        holder: OutMessageAdapterViewHolder,
        binding: ItemMessageOutBinding,
        data: Conversation,
        attachment: Attachment,
        position: Int,
    ) = binding.apply {
        val mimetype = attachment.mimetype
        val uri = attachment.getUri()

        val imageView =
            ItemAttachmentImageBinding.inflate(LayoutInflater.from(holder.binding.appnametxt.context))
        threadMessageAttachmentsHolder.addView(imageView.root)

        val placeholderDrawable = ColorDrawable(Color.TRANSPARENT)
        val isTallImage = attachment.height > attachment.width
        val transformation = if (isTallImage) CenterCrop() else FitCenter()
        val options = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(placeholderDrawable)
//            .transform(transformation)

        var builder = Glide.with(root.context)
            .load(uri)
            .apply(options)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean,
                ): Boolean {
                    threadMessagePlayOutline.beGone()
                    threadMessageAttachmentsHolder.removeView(imageView.root)
                    return false
                }

                override fun onResourceReady(
                    dr: Drawable,
                    a: Any,
                    t: Target<Drawable>,
                    d: DataSource,
                    i: Boolean,
                ) = false
            })

        // limit attachment sizes to avoid causing OOM
//        var wantedAttachmentSize = Size(attachment.width, attachment.height)
//        if (wantedAttachmentSize.width > maxChatBubbleWidth) {
//            val newHeight =
//                wantedAttachmentSize.height / (wantedAttachmentSize.width / maxChatBubbleWidth)
//            wantedAttachmentSize = Size(maxChatBubbleWidth.toInt(), newHeight.toInt())
//        }
//
//        builder = if (isTallImage) {
//            builder.override(wantedAttachmentSize.width, wantedAttachmentSize.width)
//        } else {
//            builder.override(wantedAttachmentSize.width, wantedAttachmentSize.height)
//        }

        builder = builder.override(500, 500)

        try {
            builder.into(imageView.attachmentImage)
        } catch (ignore: Exception) {
        }

        imageView.attachmentImage.setOnClickListener {
            if (selectedMessageList.isNotEmpty()) {
                updateSelection(position)
                notifyRowChanged(position)

            } else {
                viewimage?.invoke(uri, mimetype, attachment.filename)
            }
        }
        imageView.root.setOnLongClickListener {
            updateSelection(position)
            notifyRowChanged(position)
            true
        }
    }


    private fun setOnclickIn(position: Int) {
        if (selectedMessageList.isNotEmpty()) {
            updateSelection(position)
            notifyRowChanged(position)
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
                    list[position].isexpandmessageview = !list[position].isexpandmessageview
                    notifyRowChanged(position)
                } else {
                    onMessageClick?.invoke(position, list)
                }
            }
        }
    }

    private fun setLongOnclickIn(position: Int) {
        isSerchfoundmessage = false

        val changedPositions = mutableSetOf<Int>()
        list.forEachIndexed { index, conversation ->
            if (conversation.messagetraslateshow) {
                conversation.messagetraslateshow = false
                changedPositions.add(index)
            }
        }

        try {
            updateSelection(position)
            changedPositions.add(position)
            notifyRowsChanged(changedPositions)
        } catch (_: Exception) {
            Toast.makeText(
                binding.click.context,
                binding.click.context.resources.getString(R.string.Try_Again),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateSelection(position: Int) {
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
    }

    private fun notifyRowChanged(position: Int) {
        if (position >= 0 && position < list.size) {
            notifyItemChanged(position)
        }
    }

    private fun notifyRowsChanged(positions: Set<Int>) {
        positions.forEach { position ->
            notifyRowChanged(position)
        }
    }



    private fun setonselectedout(position: Int) {
        if (position < 0 || position >= list.size) {
            return
        }
        if (selectedMessageList.isNotEmpty()) {
            updateSelection(position)
            notifyRowChanged(position)
        } else {

            if (isEmojiOnly(list[position].snippet)) {
                binding.appnametxt.context.toastMess("isEmojiOnly")
            } else {
                list[position].isexpandmessageview = !list[position].isexpandmessageview
                notifyRowChanged(position)
            }

        }
    }

    private fun setonlongselectedout(position: Int) {
        isSerchfoundmessage = false
        updateSelection(position)
        notifyRowChanged(position)
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

    try {


        Linkify.addLinks(appnametxt, Linkify.ALL)
        appnametxt.movementMethod = BetterLinkMovementMethod.getInstance()
        appnametxt.movementMethod = BetterLinkMovementMethod.newInstance().apply {
            setOnLinkClickListener { textView, url ->
                Log.d("jigar", "setOnClickLink: setOnLinkClickListener <---------> 16 ${url}")
                if (appnametxt.context.config.maliciousWebsiteBtnSwichP) {
                    Log.d("jigar", "setOnClickLink: setOnLinkClickListener <---------> 15 ${url}")
                    checkWebsiteSafety(url) { isSafe ->
                        Log.d(
                            "jigar",
                            "setOnClickLink: setOnLinkClickListener <---------> 11 ${url}"
                        )
                        if (isSafe) {
                            Log.d(
                                "jigar",
                                "setOnClickLink: setOnLinkClickListener <---------> 12 ${url}"
                            )
                            linkopenclick(appnametxt, url)

                        } else {
                            Log.d(
                                "jigar",
                                "setOnClickLink: setOnLinkClickListener <---------> 13 ${url}"
                            )
                            val dialog = MaliciousWebDialog.newInstance()
                                .setOnPositiveClick {
                                    linkopenclick(appnametxt, url)
                                }

                            val activity = appnametxt.context as? FragmentActivity
                            if (activity?.supportFragmentManager != null && !activity.isDestroyed && !activity.isFinishing) {
                                dialog.show(activity.supportFragmentManager, MaliciousWebDialog.TAG)
                            } else {
                                linkopenclick(appnametxt, url)
                            }
                        }
                    }
                } else {
                    Log.d("jigar", "setOnClickLink: setOnLinkClickListener <---------> 14 ${url}")
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
    }  catch (e: Exception)  {

    }  }

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

    fun openlink(url: String, appnametxt: TextView) {
//        val browserIntent = Intent(Intent.ACTION_VIEW)
//        browserIntent.data = Uri.parse(url)
//        appnametxt.context.startActivity(browserIntent)

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


    var list = ArrayList<Conversation>()
        set(value) {
//            setNewData(value)
            field = value
            originalList.addAll(value)
            notifyDataSetChanged()
        }

    fun setNewData(newData: List<Conversation>) {
        val diffCallback = DiffUtilCallback(list, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        list.clear()
        list.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
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
//        return list[position].id.toLong()
    }

    fun setInterface(messageselectunselect: MessageSelectUnselect) {
        this.messageselectunselect = messageselectunselect
    }

    fun setOnLangInterface(selectedmessagetraslateinterface: Selectedmessagetraslateinterface) {
        this.selectedmessagetraslateinterface = selectedmessagetraslateinterface
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getdate(time2: Long, context: Context): String {
        var time = 0L
        if (time2.toString().length <= 10) {
            time = time2 * 1000
        } else {
            time = time2
        }
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

    private fun parseColorOrFallback(colorCode: String?, fallback: Int): Int {
        if (colorCode.isNullOrBlank()) return fallback
        return runCatching { Color.parseColor(colorCode) }.getOrDefault(fallback)
    }


    private fun resolveIncomingBubbleColor(context: Context): Int {
        val isLightTheme = ThemeModeManager.shouldUseLightSystemBars(context)

        val defaultColor = ColorUtils.blendARGB(
            context.getProperPrimaryColor(),
            context.getProperTextColor() ,
            if (isLightTheme) 0.24f else 0.35f
        )
        return parseColorOrFallback(inmessagecolorcustomwallpaper, defaultColor)
    }



    private fun resolveOutgoingBubbleColor(context: Context): Int {
        val isLightTheme = ThemeModeManager.shouldUseLightSystemBars(context)
        val defaultColor = ColorUtils.blendARGB(
            context.getProperBackgroundColor(),
             context.getProperTextColor(),
            if (isLightTheme) 0.5f else 0.2f
        )
        return parseColorOrFallback(outmessagecolorcustomwallpaper, defaultColor)
    }



    private fun resolveBubbleTextColor(context: Context): Int {
        return context.getProperTextColor()
    }

    private fun resolveBubbleSecondaryTextColor(context: Context): Int {
        return if (ThemeModeManager.shouldUseLightSystemBars(context)) {
            context.getProperSecondaryTextColor()
        } else {
            ColorUtils.blendARGB(context.getProperTextColor(), context.getProperBackgroundColor(), 0.2f)
        }
    }

    private fun resolveBubbleLinkColor(context: Context): Int {
        return context.getProperPrimaryColor()
    }

    private fun applyIncomingTextColors(binding: ItemMessageInBinding) {
        val context = binding.appnametxt.context
        val textColor = resolveBubbleTextColor(context)
        val secondaryColor = resolveBubbleSecondaryTextColor(context)
        binding.appnametxt.setTextColor(textColor)
        binding.appnametxt.setLinkTextColor(resolveBubbleLinkColor(context))
        binding.messagedate.setTextColor(secondaryColor)
        binding.messagetime.setTextColor(secondaryColor)
    }

    private fun applyOutgoingTextColors(binding: ItemMessageOutBinding) {
        val context = binding.appnametxt.context
        val textColor = resolveBubbleTextColor(context)
        val secondaryColor = resolveBubbleSecondaryTextColor(context)
        binding.appnametxt.setTextColor(textColor)
        binding.appnametxt.setLinkTextColor(resolveBubbleLinkColor(context))
        binding.messagetime.setTextColor(secondaryColor)
        binding.messagedate.setTextColor(secondaryColor)
    }

    private fun colorToHex(color: Int): String = String.format("#%08X", color)

    private fun messageInDrawable(context: Context, colorCode: String): Drawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = context.config.messagecorner
            setColor(colorCode.toColorInt())
        }
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

//    private fun funsetTextStatus(textcolor: Int, message: String, visibleornot: Boolean) {
//        if (visibleornot) {
//            binding.messageFaild.visible()
//        } else {
//            binding.messageFaild.gone()
//        }
//        binding.messageFaild.text = message
//        binding.messageFaild.setTextColor(binding.messageFaild.context.resources.getColor(textcolor))
//    }

    private fun funsetTextStatus(
        textcolor: Int,
        message: String,
        visibleornot: Boolean,
        position: Int,
    ) {
        if (visibleornot) {
            binding.messageFaild.visible()
        } else {
            binding.messageFaild.gone()
        }
        binding.messageFaild.text = message
        try {
            binding.messageFaild.setTextColor(binding.messageFaild.context.resources.getColor(textcolor))

        }catch (e: Exception){
            binding.messageFaild.setTextColor(textcolor)

        }


    }

    private fun formatUnreadLabel(context: Context, unreadCount: Int): String {
        val safeCount = unreadCount.coerceAtLeast(0)
        return context.resources.getString(R.string.Unread) + " " + safeCount + " " +
            context.resources.getString(R.string.Message_new)
    }

    private fun highlitetext(highlighttext: String, appnametxt: TextView, black: Int) {
        TextHighlighter()
            .setBackgroundColor(Color.parseColor("#FFFF00"))
            .setForegroundColor(black)
            .addTarget(appnametxt)
            .highlight(highlighttext, TextHighlighter.CASE_INSENSITIVE_MATCHER);
    }
}

class DiffUtilCallback(
    private val oldList: List<Conversation>,
    private val newList: List<Conversation>,
) :
    DiffUtil.Callback() {

    // old size
    override fun getOldListSize(): Int = oldList.size

    // new list size
    override fun getNewListSize(): Int = newList.size

    // if items are same
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.javaClass == newItem.javaClass
    }

    // check if contents are same
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return oldItem.hashCode() == newItem.hashCode()
    }


}
