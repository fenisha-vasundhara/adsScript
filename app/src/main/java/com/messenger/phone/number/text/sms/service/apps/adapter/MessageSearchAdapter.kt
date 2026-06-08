package com.messenger.phone.number.text.sms.service.apps.adapter

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.text.util.Linkify
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.demo.adsmanage.Commen.Constants
import com.messenger.phone.number.text.sms.service.apps.CommanClass.checkWebsiteSafety
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.drawableCache
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getNameAndPhotoFromPhoneNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isPackageInstalled
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isValidUrl
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.Dialog.MaliciousWebDialog
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.ItemMessageInSerchBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.ItemMessageOutSerchBinding
import com.messenger.phone.number.text.sms.service.apps.helper.RandomDrawableProvider
import com.messenger.phone.number.text.sms.service.apps.highlighter.TextHighlighter
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MessageSearchAdapterClickInterface
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.simplemobiletools.commons.helpers.SimpleContactsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import javax.inject.Inject


class MessageSearchAdapter @Inject constructor() : RecyclerView.Adapter<ViewHolder>() {

    private val MESSAGE_IN: Int = 2
    private var package_name = "com.android.chrome"
    private val MESSAGE_OUT: Int = 1
    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    class InMessageAdapterViewHolder(var binding: ItemMessageInSerchBinding) : ViewHolder(binding.root){
        init {
            binding.nullchack = "null"
        }
    }
    class OutMessageAdapterViewHolder(var binding: ItemMessageOutSerchBinding) : ViewHolder(binding.root)
    var inmessagecolorcustomwallpaper = ""

    var highlighttext = ""
    var isThreeItem = true
    lateinit var messageSearchAdapterClickInterface: MessageSearchAdapterClickInterface

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            MESSAGE_IN -> InMessageAdapterViewHolder(ItemMessageInSerchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            MESSAGE_OUT -> OutMessageAdapterViewHolder(ItemMessageOutSerchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> InMessageAdapterViewHolder(ItemMessageInSerchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun getItemCount(): Int {
        return if (isThreeItem) {
            if (list.size == 0) {
                0
            } else if (list.size >= 10) {
                10
            } else {
                list.size
            }
        } else {
            list.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].type == 1) {
            MESSAGE_OUT
        } else {
            MESSAGE_IN
        }
    }

    override fun getItemId(position: Int): Long {
        val item = list.getOrNull(position) ?: return RecyclerView.NO_ID
        return item.messageId ?: item.id.toLong()
    }
    private fun colorToHex(color: Int): String = String.format("#%08X", color)
    private fun messageInDrawable(context: Context, colorCode: String): Drawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = context.config.messagecorner
            setColor(colorCode.toColorInt())
        }
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

    private fun parseColorOrFallback(colorCode: String?, fallback: Int): Int {
        if (colorCode.isNullOrBlank()) return fallback
        return runCatching { Color.parseColor(colorCode) }.getOrDefault(fallback)
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.setIsRecyclable(false)
        val context = holder.itemView.context
        with(holder) {
            fontSize10 = context.getTextSizeForeNormal10MS()
            fontSize13 = context.getTextSizeForeNormal13MS()
            fontSize18 = context.getTextSizeForeNormal18MS()
            fontSize8 = context.getTextSizeForeNormal8MS()
            fontSize15 = context.getTextSizeHometitleMS()
        }

        if (holder is InMessageAdapterViewHolder) {
            with(holder) {

                binding.textsizechagefor10 = fontSize10
                binding.textsizechagefor13 = fontSize13
                binding.textsizechagefor18 = fontSize18
                binding.textsizechagefor8 = fontSize8
                binding.textsizechagefor15 = fontSize15

                binding.datetxt.gone()
                binding.messagedate.visible()
                binding.list = list[position]
                binding.appnametxt.text = list[position].snippet
                binding.itemclick.radius = binding.appnametxt.context.config.messagecorner
                binding.appnametxt.setLinkTextColor(Color.parseColor("#ffffff"));
                highlitetext(highlighttext, holder.binding.appnametxt,Color.WHITE)

                setOnClickLink(binding.appnametxt)

                binding.appnametxt.setOnClickListener {

                    messageSearchAdapterClickInterface.MessageSearchAdapterOnClick(position, list)

                }
                try {
                    binding.isLetterorNot = list[position].phoneNumber.toString()[0].isLetter()
                    binding.fistcat.text = list[position].title.substring(0, 1)
                } catch (_: Exception) {

                }
                val incomingBubbleHex = colorToHex(resolveIncomingBubbleColor(binding.appnametxt.context))
                binding.sectionbg.background = messageInDrawable(
                    context,
                    incomingBubbleHex
                )

//                binding.sectionbg.background = getDrawable(
//                    binding.appnametxt.context,
//                    (if (binding.datetxt.context.config.activeThemeSelection == 2) {
//                        R.color.unselectmessagein_2
//                    } else {
//                        R.color.unselectmessagein
//                    })
//                )

            }
        } else if (holder is OutMessageAdapterViewHolder) {
            with(holder) {

                binding.textsizechagefor10 = fontSize10
                binding.textsizechagefor13 = fontSize13
                binding.textsizechagefor18 = fontSize18
                binding.textsizechagefor8 = fontSize8
                binding.textsizechagefor15 = fontSize15

                binding.datetxt.gone()
                binding.messagedate.visible()
                binding.maincanternar.visible()
                binding.messName.visible()
                binding.list = list[position]
                binding.appnametxt.text = list[position].snippet
                binding.itemclick.radius = binding.appnametxt.context.config.messagecorner
                val outgoingBubbleColor = resolveOutgoingBubbleColor(binding.appnametxt.context)
                val outgoingTextColor = resolveBubbleTextColor(binding.appnametxt.context)
                val outgoingSecondaryColor = resolveBubbleSecondaryTextColor(binding.appnametxt.context)

                highlitetext(highlighttext, holder.binding.appnametxt, outgoingTextColor)
                setOnClickLink(binding.appnametxt)

                binding.appnametxt.setOnClickListener {
                    messageSearchAdapterClickInterface.MessageSearchAdapterOnClick(position, list)
                }

                try {
                    binding.isLetterorNot = list[position].phoneNumber.toString()[0].isLetter()
                    binding.fistcat.text = list[position].title.substring(0, 1)
                } catch (_: Exception) {

                }

                binding.sectionbg.setBackgroundColor(outgoingBubbleColor)
                binding.appnametxt.setTextColor(outgoingTextColor)
                binding.messagetime.setTextColor(outgoingSecondaryColor)
                binding.messagedate.setTextColor(outgoingSecondaryColor)

                if (list[position].isgroupmessage) {
                    binding.contacticon.setImageDrawable(SimpleContactsHelper(binding.contacticon.context).getColoredGroupIcon(list[position].title))
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        val profile = binding.contacticon.context.getNameAndPhotoFromPhoneNumber(list[position].phoneNumber)
                        if (profile.photoUri?.isNotEmpty() == true) {
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    SimpleContactsHelper(binding.contacticon.context)
                                        .loadContactImage(
                                            profile.photoUri, binding.contacticon, if (list.isEmpty()) {
                                                "jigar"
                                            } else {
                                                try {
                                                    list[position].title
                                                } catch (E: Exception) {
                                                    "jigar"
                                                }
                                            }, null
                                        )
                                } catch (e: Exception) {

                                }
                            }
                        } else {
                            try {
                                val drawable = drawableCache[list[position].threadId.toString()]
                                if (drawable != null) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        binding.contacticon.setImageDrawable(drawable)
                                    }

                                } else {
                                    val randomDrawable = RandomDrawableProvider(binding.contacticon.context).getRandomDrawable(list[position].threadId!!)
                                    CoroutineScope(Dispatchers.Main).launch {
                                        binding.contacticon.setImageDrawable(randomDrawable)
                                    }
                                    drawableCache[list[position].threadId.toString()] = randomDrawable
                                }
                            } catch (E: Exception) {
                            }
                        }
                    }
                }
            }

        }
    }

    private fun highlitetext(highlighttext: String, appnametxt: TextView, black: Int) {
        TextHighlighter()
            .setBackgroundColor(Color.parseColor("#F69191"))
            .setForegroundColor(black)
            .addTarget(appnametxt)
            .highlight(highlighttext, TextHighlighter.CASE_INSENSITIVE_MATCHER);
    }

    private fun resolveOutgoingBubbleColor(context: Context): Int {
        val isLightTheme = ThemeModeManager.shouldUseLightSystemBars(context)
        return ColorUtils.blendARGB(
            context.getProperBackgroundColor(),
            context.getProperTextColor(),
            if (isLightTheme) 0.05f else 0.05f
        )
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


    var list = ArrayList<Conversation>()
        set(value) {
            field = value
            Log.d("", "onCreate: binding.searchResult.gone() <---------> 11111 ${field.size}")
            notifyDataSetChanged()
        }

    fun setInterface(messageSearchAdapterClickInterface: MessageSearchAdapterClickInterface) {
        this.messageSearchAdapterClickInterface = messageSearchAdapterClickInterface
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun Context.getdate(time: Long): String {
        val date2 = Date(time)
        val format = SimpleDateFormat("yyyy-MM-dd")
        val dateString = format.format(date2)
        val date = LocalDate.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("dd MMM", Locale(this.config.SelectedLanguage))
        return date.format(formatter)
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
                            dialog.show((appnametxt.context as FragmentActivity).supportFragmentManager, MaliciousWebDialog.TAG)
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

    fun openlink(url: String, appnametxt: TextView) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            appnametxt.context.startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
            appnametxt.context.toastMess("No web browser app found")
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

}
