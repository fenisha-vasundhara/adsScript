package com.messenger.phone.number.text.sms.service.apps.adapter

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.R.attr.transitionName
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.ContentResolver
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.Html
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.graphics.Typeface
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.provider.Telephony
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.drawableCache
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getAllDrafts
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getColoredGroupIconNew
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getNameAndPhotoFromPhoneNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.textPrimaryOnThemeForColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.data.NamePhoto
import com.messenger.phone.number.text.sms.service.apps.databinding.ItemConversationBinding
import com.messenger.phone.number.text.sms.service.apps.helper.RandomDrawableProvider
import com.messenger.phone.number.text.sms.service.apps.helperClass.ItemConversationAdapterClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MainMessageClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MessageClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MoreOPtionClick
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.simplemobiletools.commons.extensions.adjustAlpha
import com.simplemobiletools.commons.extensions.beVisibleIf
import com.simplemobiletools.commons.helpers.SimpleContactsHelper
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.ConcurrentModificationException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Locale.getDefault
import java.util.RandomAccess
import javax.inject.Inject
import java.time.Instant
import java.time.ZoneId


class MainMassageAdapter @Inject constructor() :
    RecyclerView.Adapter<MainMassageAdapter.MainMassageAdapterViewHolder>(), Filterable {

    private var fontSize = 12f
    private var fontSize10 = 16f
    private var count2: Int? = 0
    private lateinit var photouri: NamePhoto
    var messageClick: MessageClick? = null
    var moreOPtionClick: MoreOPtionClick? = null
    var mainMessageClick: MainMessageClick? = null
    var showpinnedconversation: Boolean = false

    var adapteremtyornot: ((Boolean) -> Unit)? = null

    var newmessagecountshow = true

    var enableDebugLogging: Boolean = false
    var debugTag: String = "MainMassageAdapter"

    private var drafts = HashMap<Long, String?>()

    @Inject
    lateinit var itemConversationAdapterClick: ItemConversationAdapterClick

    @Inject
    lateinit var repo: MessagerDatabaseRepo

    var isselectedAdapter: Boolean = false

    lateinit var binding2: ItemConversationBinding

    var catname: String = "No"

    var ismoreoption: Boolean = false

    var catselection: Boolean = false

    var selecteditem: ArrayList<Conversation> = arrayListOf()
    var isSearchResultMode: Boolean = false

    var signature = ""


    private var textSize = 16f

    private val originalList = ArrayList<Conversation>()
    private var currentQuery: String = ""
    private var searchHighlightQuery: String = ""
    var onFilterResultsChanged: ((filteredCount: Int, query: String) -> Unit)? = null


    @SuppressLint("ClickableViewAccessibility")
    inner class MainMassageAdapterViewHolder(var binding: ItemConversationBinding) :
        ViewHolder(binding.root) {
        init {
            val con = binding.lastmessageshow.context
            binding2 = binding
            if (con.config.userpreferenceSignatureOnOff) {
                signature = if (con.config.userpreferenceSignature[0] == '-') {
                    con.config.userpreferenceSignature
                } else {
                    "-" + con.config.userpreferenceSignature
                }
            } else {
                signature = con.config.userpreferenceSignature
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MainMassageAdapterViewHolder {
        return MainMassageAdapterViewHolder(
            ItemConversationBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        val conversation = list.getOrNull(position) ?: return RecyclerView.NO_ID
        return buildStableItemId(conversation)
    }


    fun getUnreadSmsCountForThread(
        contentResolver: ContentResolver,
        threadId: Long
    ): Int {
        val uri: Uri = Telephony.Sms.Inbox.CONTENT_URI

        val selection = "${Telephony.Sms.THREAD_ID} = ? AND ${Telephony.Sms.READ} = ?"
        val selectionArgs = arrayOf(threadId.toString(), "0")   // 0 = unread

        contentResolver.query(
            uri,
            arrayOf(Telephony.Sms._ID),   // minimal projection for count
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            return cursor.count
        } ?: return 0
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MainMassageAdapterViewHolder, position: Int) {
        val context = holder.itemView.context


        if (position < 0 || position >= list.size) {
            return
        }
        val conversation = list[position]
        val binding = holder.binding


        fontSize = context.resources.getDimension(com.intuit.ssp.R.dimen._12ssp)
//        fontSize = context.getTextSizeHometitleMS()
        fontSize10 = context.getTextSizeForeNormal10MS()

        binding.newmessagecountshowbind = newmessagecountshow



        val primaryColor = context.getProperPrimaryColor()
        val newTitleColor = context.getProperTextColor()
        val newBodyColor = context.getProperTextColor()
        val oldTitleColor = newTitleColor.adjustAlpha(0.8f)
//        val oldBodyColor = newBodyColor.adjustAlpha(0.8f)
        val oldBodyColor = oldTitleColor.adjustAlpha(1.9f)


        binding.viewDivider.setBackgroundColor(newTitleColor)

        try {




            with(holder) {
//                binding.messagelasttime.text = formatToKolkataTime(conversation.time!!)
                binding.executePendingBindings()
                binding.listdata = conversation
                binding.textsizechage = fontSize
                binding.textsizechagefor10 = fontSize10
                binding.textsizechagefor8 = fontSize10 * 0.9f
                binding.textsizechagefor13 = context.getTextSizeForeNormal13MS()


                binding.pinIndicator.drawable.setTint(newTitleColor)

                val txt = conversation.snippet
                val dataattechment = conversation.messagewithattachment?.attachments

                val lines: List<String> = txt.split("\n")
                val lastLine: String = lines.get(lines.size - 1)

                if (lastLine.contains("<br><i>")) {
                    binding.lastmessageshow.text = Html.fromHtml(txt);
                } else {
                    if (txt.isEmpty()) {
                        if (dataattechment?.isNotEmpty() == true) {
                            val attachementlasttxt = dataattechment.first()
                            Log.d(
                                "",
                                "onBindViewHolder: attachementlasttxt 1111 <---> ${dataattechment}"
                            )
                            if (attachementlasttxt != null && attachementlasttxt.mimetype.isNotEmpty()) {
                                binding.lastmessageshow.text = binding.root.context.getString(R.string.attachment)
                            } else {
                                binding.lastmessageshow.text = binding.root.context.getString(R.string.attachment)
                            }
                        } else {
                            binding.lastmessageshow.text = binding.root.context.getString(R.string.attachment)
                        }
                    } else {

                        binding.lastmessageshow.text = txt
                    }

                }
//                android:textColor="@{listdata.isnewmessage ? @color/newmessageTwo : @color/oldmessagesecound}"
                if (conversation.isnewmessage == true) {
                    binding.number.setTextColor(newTitleColor)
//                    binding.lastmessageshow.setTextColor(newBodyColor)
                    binding.lastmessageshow.setTextColor(newTitleColor)
                    binding.messagelasttime.setTextColor(newBodyColor)
                } else {
//                    binding.number.setTextColor(oldTitleColor)
                    binding.number.setTextColor(oldBodyColor)
                    binding.lastmessageshow.setTextColor(oldBodyColor)
                    binding.messagelasttime.setTextColor(oldBodyColor)
                }


                binding.conversationBodyShort.setTextColor(oldBodyColor)



                if (conversation.isgroupmessage) {
                    binding.contacticon.setImageDrawable(
                        getColoredGroupIconNew(
                            conversation.title,
                            binding.number.context
                        )
                    )
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        val profile =
                            binding.userProfileImage.context.getNameAndPhotoFromPhoneNumber(conversation.phoneNumber)



                        if (profile.photoUri?.isNotEmpty() == true) {
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    SimpleContactsHelper(binding.contacticon.context)
                                        .loadContactImage(
                                            profile.photoUri,
                                            binding.contacticon,
                                            if (list.isEmpty()) {
                                                "jigar"
                                            } else {
                                                try {
                                                    conversation.title
                                                } catch (E: Exception) {
                                                    "jigar"
                                                }
                                            },
                                            null
                                        )
                                } catch (e: Exception) {

                                }
                            }
                        } else {
                            try {
                                val drawable = drawableCache[conversation.threadId.toString()]
                                if (drawable != null) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        binding.contacticon.setImageDrawable(drawable)
                                    }

                                } else {
                                    val randomDrawable =
                                        RandomDrawableProvider(binding.contacticon.context).getRandomDrawable(
                                            conversation.threadId ?: 0L
                                        )
                                    CoroutineScope(Dispatchers.Main).launch {
                                        binding.contacticon.setImageDrawable(randomDrawable)
                                        setmessagecountcolor(context, holder, conversation)
                                    }
                                    drawableCache[conversation.threadId.toString()] =
                                        randomDrawable
                                }
                            } catch (E: Exception) {
                            }
                        }
                    }

//                    if (list[position].title.isNotEmpty()) {
//                        if (list[position].title.first().isDigit()) {
//                            binding.contacticon.setImageBitmap(binding.contacticon.context.getContactLetterIcon(list[position].title, list[position].phoneNumber))
//                        } else {
//                            val drawable = drawableCache[list[position].threadId.toString()]
//                            if (drawable != null) {
//                                binding.contacticon.setImageDrawable(drawable)
//                            } else {
//                                val randomDrawable = RandomDrawableProvider(binding.contacticon.context).getRandomDrawable()
//                                binding.contacticon.setImageDrawable(randomDrawable)
//                                drawableCache[list[position].threadId.toString()] = randomDrawable
//                            }
//                        }
//                    }
                }

//                binding.numberfisttxt.text = firstTwo(list[position].title)
//                binding.notreplychack = list[position].phoneNumber.first().isLetter()
//                binding.isLetterorNot = list[position].title[0].isLetter()

                if (conversation.isgroupmessage) {
                    binding.number.text = conversation.groupName

//
                } else {
                    binding.number.text = conversation.title

                }

                CoroutineScope(Dispatchers.IO).launch {
//                    Log.d("", "onBindViewHolder: count2 <-----------> ${count2} <--------> ${list[position].title}")

                    try {
                        binding.isTodayMassagechack =
                            samedateortime(conversation.time, binding.number.context)

                        val count = conversation.phoneNumber.let {
                            repo.getnewmessagecountRepo(it, 1)
                        }
                        val unreadCountForThread = count.count { it.threadId == conversation.threadId }


                        CoroutineScope(Dispatchers.Main).launch {
//                            binding.lastmessagetime.text = unreadCountForThread.toString()
                        }

                    } catch (_: Exception) {
                    }
                }

//                GlobalScope.launch {
//
//                    withContext(Dispatchers.IO) {
//                        count2 = list[position].threadId?.let { binding.number.context.getNewMessageCountForThreadId(it) }
//                    }
//
//                    withContext(Dispatchers.Main) {
//
//                        if (count2 != 0) {
//                            list[position].newMessageCount = count2
//                            binding.isNewmessagefoundchack = true
//                            notifyItemChanged(position)
//                        } else {
//                            binding.isNewmessagefoundchack = false
//                        }
//
//                    }
//
//                }


                itemView.setOnClickListener {

                    if (isSearchResultMode) {
                        if (list.isNotEmpty()) {
                            messageClick?.onClick(
                                conversation.threadId,
                                position,
                                conversation.title,
                                conversation.phoneNumber,
                                holder,
                                list,
                                position
                            )
                        }
                        return@setOnClickListener
                    }

                    if (catselection) {
                        try {
                            if (selecteditem.contains(conversation)) {
                                selecteditem.remove(conversation)
                            } else {
                                selecteditem.add(conversation)
                            }
                            mainMessageClick?.onMainClick(
                                position,
                                list,
                                selecteditem
                            )
//                        notifyItemChanged(position)
                            notifyDataSetChanged()
                        } catch (_: Exception) {
                        }
                    } else {
                            if (selecteditem.isNotEmpty()) {
                            if (selecteditem.contains(conversation)) {
                                selecteditem.remove(conversation)
                            } else {
                                selecteditem.add(conversation)
                            }
                            mainMessageClick?.onMainClick(
                                position,
                                list,
                                selecteditem
                            )
//                        notifyItemChanged(position)
                            notifyDataSetChanged()
                        } else {
                            if (list.isNotEmpty()) {
                                messageClick?.onClick(
                                    conversation.threadId,
                                    position,
                                    conversation.title,
                                    conversation.phoneNumber,
                                    holder,
                                    list,
                                    position
                                )
                            }
                        }
                    }

                }

                itemView.setOnLongClickListener {
                    if (isSearchResultMode) {
                        return@setOnLongClickListener true
                    }
                    try {
                        if (selecteditem.contains(conversation)) {
                            selecteditem.remove(conversation)
                        } else {
                            selecteditem.add(conversation)
                        }
                        mainMessageClick?.onMainClick(
                            position,
                            list,
                            selecteditem
                        )
//                        notifyItemChanged(position)
                        notifyDataSetChanged()
                    } catch (_: Exception) {
                    }
                    true
                }

                if (selecteditem.contains(conversation)) {
                    binding.itemselected.visible()

                    val drawable = binding.itemselected.drawable as LayerDrawable
                    val bg = drawable.findDrawableByLayerId(R.id.bg)
                    bg.setTint(primaryColor)
                } else {
                    binding.itemselected.gone()
                }
                binding.star.setOnClickListener {
                    moreOPtionClick?.onClickMenu(position, list, holder)
                }
                if (showpinnedconversation) {
                    binding.pinIndicator.beVisibleIf(
                        binding.itemselected.context.config.pinnedConversations.contains(
                            conversation.threadId.toString()
                        )
                    )
                }


                //draft


//                if (binding.contacticon.context.config.line_selection == 2) {
//                    if (smsDraft != null) {
//                        binding.conversationBodyShort.text = smsDraft
//                        binding.lastmessageshow.gone()
//                    } else {
//                        binding.lastmessageshow.maxLines = 1
//                        binding.lastmessageshow.visible()
//                    }
//                } else {
//                    if (smsDraft != null) {
//                        binding.conversationBodyShort.text = smsDraft
//                        binding.lastmessageshow.maxLines = 1
//                        binding.lastmessageshow.visible()
//                    } else {
//                        binding.lastmessageshow.maxLines = 2
//                        binding.lastmessageshow.visible()
//                    }
//
//                }

            }
            if (isselectedAdapter) {
                itemConversationAdapterClick.onClick(
                    this,
                    position,
                    holder,
                    list,
                    isselectedAdapter,
                    catname
                )
            }
        } catch (e: Exception) {
        }


        val smsDraft = drafts[conversation.threadId]
        holder.binding.draftIndicator.beVisibleIf(smsDraft != null)
        holder.binding.conversationBodyShort.beVisibleIf(smsDraft != null)


//        if (smsDraft != null) {
//            holder.binding.conversationBodyShort.text = smsDraft
//            holder.binding.lastmessageshow.gone()
//        } else {
//
//        }


//        if (list[position].snippet == "converremoved_12345") {
//            holder.binding.lastmessageshow.gone()
//        } else {
//            if (smsDraft != null) {
//                holder.binding.lastmessageshow.gone()
//                holder.binding.conversationBodyShort.text = smsDraft
//            } else {
//                if (list[position].isnewmessage == true){
//                    holder.binding.lastmessageshow.maxLines = 2
//                    holder.binding.lastmessageshow.visible()
//                }else{
//                    holder.binding.lastmessageshow.maxLines = 1
//                    holder.binding.lastmessageshow.visible()
//                }
//
//            }
//        }

        if (conversation.snippet == "converremoved_12345") {
            holder.binding.lastmessageshow.gone()
        } else {
            if (smsDraft != null) {
                holder.binding.lastmessageshow.gone()
                holder.binding.conversationBodyShort.text = smsDraft
            } else {
                if (conversation.isnewmessage == true) {
                    holder.binding.lastmessageshow.setSingleLine(false)
                    holder.binding.lastmessageshow.maxLines = 1
                    holder.binding.lastmessageshow.visible()
                } else {
                    holder.binding.lastmessageshow.maxLines = 1
                    holder.binding.lastmessageshow.ellipsize = TextUtils.TruncateAt.END
                    holder.binding.lastmessageshow.setSingleLine(true)
                    holder.binding.lastmessageshow.visible()
                }

            }
        }
        applySearchHighlightIfNeeded(holder, context)
        setmessagecountcolor(context, holder, conversation)
    }

    private fun applySearchHighlightIfNeeded(holder: MainMassageAdapterViewHolder, context: Context) {
        if (searchHighlightQuery.isBlank()) {
            return
        }
        val highlightColor = context.getProperPrimaryColor()
        holder.binding.number.text = buildHighlightedText(
            holder.binding.number.text,
            searchHighlightQuery,
            highlightColor
        )
        holder.binding.lastmessageshow.text = buildHighlightedText(
            holder.binding.lastmessageshow.text,
            searchHighlightQuery,
            highlightColor
        )
        holder.binding.conversationBodyShort.text = buildHighlightedText(
            holder.binding.conversationBodyShort.text,
            searchHighlightQuery,
            highlightColor
        )
    }

    private fun buildHighlightedText(
        text: CharSequence?,
        query: String,
        highlightColor: Int,
    ): CharSequence {
        val sourceText = text?.toString().orEmpty()
        val normalizedQuery = query.trim()
        if (sourceText.isBlank() || normalizedQuery.isBlank()) {
            return sourceText
        }

        val sourceLower = sourceText.lowercase(Locale.getDefault())
        val queryLower = normalizedQuery.lowercase(Locale.getDefault())
        val spannable = SpannableStringBuilder(sourceText)
        var startIndex = sourceLower.indexOf(queryLower)
        while (startIndex >= 0) {
            val endIndex = startIndex + queryLower.length
            spannable.setSpan(
                ForegroundColorSpan(highlightColor),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            startIndex = sourceLower.indexOf(queryLower, endIndex)
        }
        return spannable
    }

    private fun setmessagecountcolor(
        context: Context,
        holder: MainMassageAdapterViewHolder,
        conversation: Conversation,
    ) {

        try {
            val  primaryColor = context.getProperPrimaryColor()

            val profilecolordata =
                context.config.getProfileThemeColor(conversation.threadId.toString())
            if (profilecolordata != null) {
                holder.binding.lastmessagetime.backgroundTintList = ColorStateList.valueOf(
                    primaryColor
                )
                holder.binding.lastmessagetime.setTextColor(Color.WHITE)

            } else {

                try {
                    holder.binding.lastmessagetime.backgroundTintList = ColorStateList.valueOf(
                        primaryColor
                    )
                    holder.binding.lastmessagetime.setTextColor(Color.WHITE)
                } catch (e: Exception) {
                    holder.binding.lastmessagetime.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.appcolor)
                    )
                    holder.binding.lastmessagetime.setTextColor(Color.WHITE)
                }
            }
        }catch (e:Exception){

        }


    }
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun getAllColorsInHex(bitmap: Bitmap): Set<String> {
        val colors = mutableSetOf<String>()
        val width = bitmap.width
        val height = bitmap.height

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixelColor = bitmap.getPixel(x, y)
                val hexColor = colorToHex(pixelColor)
                colors.add(hexColor)
            }
        }
        return colors
    }

    fun colorToHex(color: Int): String {
        val red = (color shr 16) and 0xFF
        val green = (color shr 8) and 0xFF
        val blue = color and 0xFF
        return String.format("#%02X%02X%02X", red, green, blue)
    }

    var list = ArrayList<Conversation>()
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }

    fun updateList(newList: List<Conversation>) {
        val listSnapshot = snapshotConversationList(newList)
        val sanitizedList = sanitizeConversationList(listSnapshot)
        logListStats(sanitizedList)
        Log.d("", "updateList: newList <--------> 11111 ${sanitizedList.size}")
        Log.d(
            "",
            "onCreate: GetAllConversationlivelist <----------> 111111111111 chack fast ${sanitizedList.size}"
        )
        originalList.clear()
        originalList.addAll(sanitizedList)
        applyQuery(
            query = currentQuery,
            notifyAdapterEmptyState = currentQuery.isBlank(),
            notifyFilterCallback = currentQuery.isNotBlank()
        )
    }

    fun setSearchHighlightQuery(query: String, refreshList: Boolean = true) {
        val normalizedQuery = query.trim()
        if (searchHighlightQuery == normalizedQuery) {
            return
        }
        searchHighlightQuery = normalizedQuery
        if (refreshList) {
            notifyDataSetChanged()
        }
    }

    override fun getFilter(): Filter = conversationFilter

    private val conversationFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val query = constraint?.toString().orEmpty()
            val normalizedQuery = query.trim()
            val filteredItems = if (normalizedQuery.isEmpty()) {
                ArrayList(originalList)
            } else {
                ArrayList(
                    originalList.filter { conversation ->
                        matchesSearchQuery(conversation, normalizedQuery)
                    }
                )
            }

            return FilterResults().apply {
                values = filteredItems
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            currentQuery = constraint?.toString()?.trim().orEmpty()
            list.clear()
            list.addAll(results?.values as? ArrayList<Conversation> ?: arrayListOf())
            notifyDataSetChanged()
            onFilterResultsChanged?.invoke(list.size, currentQuery)
        }
    }

    private fun applyQuery(
        query: String,
        notifyAdapterEmptyState: Boolean,
        notifyFilterCallback: Boolean,
    ) {
        currentQuery = query.trim()
        val filteredItems = if (currentQuery.isEmpty()) {
            ArrayList(originalList)
        } else {
            ArrayList(
                originalList.filter { conversation ->
                    matchesSearchQuery(conversation, currentQuery)
                }
            )
        }

        list.clear()
        list.addAll(filteredItems)
        if (notifyAdapterEmptyState) {
            adapteremtyornot?.invoke(list.isEmpty())
        }
        notifyDataSetChanged()
        if (notifyFilterCallback) {
            onFilterResultsChanged?.invoke(list.size, currentQuery)
        }
    }

    private fun sanitizeConversationList(source: List<Conversation>): ArrayList<Conversation> {
        val sourceSnapshot = snapshotConversationList(source)
        val seenKeys = HashSet<String>(sourceSnapshot.size)
        val sanitized = ArrayList<Conversation>(sourceSnapshot.size)

        sourceSnapshot.forEach { conversation ->
            val uniqueKey = buildConversationUniqueKey(conversation)
            if (seenKeys.add(uniqueKey)) {
                sanitized.add(conversation)
            }
        }

        return sanitized
    }

    private fun buildConversationUniqueKey(conversation: Conversation): String {
        val threadId = conversation.threadId ?: 0L
        if (threadId > 0L) {
            return "thread:$threadId"
        }

        val messageId = conversation.messageId ?: 0L
        if (messageId > 0L) {
            return "message:$messageId"
        }

        return buildString {
            append("fallback:")
            append(conversation.phoneNumber)
            append('|')
            append(conversation.title)
            append('|')
            append(conversation.date)
            append('|')
            append(conversation.snippet)
        }
    }

    private fun buildStableItemId(conversation: Conversation): Long {
        val threadId = conversation.threadId ?: 0L
        if (threadId > 0L) {
            return threadId shl 2
        }

        val messageId = conversation.messageId ?: 0L
        if (messageId > 0L) {
            return (messageId shl 2) or 1L
        }

        val localId = conversation.id.toLong()
        if (localId > 0L) {
            return (localId shl 2) or 2L
        }

        val fallbackHash = buildConversationUniqueKey(conversation).hashCode().toLong()
        return (fallbackHash shl 2) or 3L
    }

    private fun matchesSearchQuery(conversation: Conversation, query: String): Boolean {
        val normalizedQuery = query.lowercase(Locale.getDefault())
        val title = conversation.title.lowercase(Locale.getDefault())
        val snippet = conversation.snippet.lowercase(Locale.getDefault())
        val phone = conversation.phoneNumber.lowercase(Locale.getDefault())
        val groupName = conversation.groupName.orEmpty().lowercase(Locale.getDefault())

        return title.contains(normalizedQuery) ||
                title.startsWith(normalizedQuery) ||
                snippet.contains(normalizedQuery) ||
                snippet.startsWith(normalizedQuery) ||
                phone.contains(normalizedQuery) ||
                phone.startsWith(normalizedQuery) ||
                groupName.contains(normalizedQuery) ||
                groupName.startsWith(normalizedQuery)
    }

    private fun logListStats(newList: List<Conversation>) {
        try {
            if (!enableDebugLogging) return
            val listSnapshot = snapshotConversationList(newList)
            val total = listSnapshot.size
            val threadIdCounts = listSnapshot.groupingBy { it.threadId }.eachCount()
            val dupThreads = threadIdCounts.filterValues { it > 1 }
            val dupThreadCount = dupThreads.size
            val dupItems = dupThreads.values.sum()
            Log.d(
                debugTag,
                "updateList: total=$total distinctThreads=${threadIdCounts.size} dupThreads=$dupThreadCount dupItems=$dupItems"
            )
            if (dupThreads.isNotEmpty()) {
                val topThreads = dupThreads.entries
                    .sortedByDescending { it.value }
                    .take(5)
                    .joinToString { "${it.key}:${it.value}" }
                Log.d(debugTag, "updateList: topDuplicateThreads=$topThreads")
            }
            val messageIdCounts = listSnapshot.groupingBy { it.messageId }.eachCount()
            val dupMessageIds = messageIdCounts.filterValues { it > 1 }
            if (dupMessageIds.isNotEmpty()) {
                val topMsg = dupMessageIds.entries
                    .sortedByDescending { it.value }
                    .take(5)
                    .joinToString { "${it.key}:${it.value}" }
                Log.d(debugTag, "updateList: duplicateMessageIds=$topMsg")
            }
        }catch (_: Exception){

        }

    }

    private fun snapshotConversationList(source: List<Conversation>): ArrayList<Conversation> {
        repeat(3) { attempt ->
            try {
                return if (source is RandomAccess) {
                    snapshotRandomAccessList(source)
                } else {
                    ArrayList(source)
                }
            } catch (exception: ConcurrentModificationException) {
                if (enableDebugLogging) {
                    Log.w(
                        debugTag,
                        "snapshotConversationList: retry=$attempt due to concurrent mutation",
                        exception
                    )
                }
            } catch (exception: IndexOutOfBoundsException) {
                if (enableDebugLogging) {
                    Log.w(
                        debugTag,
                        "snapshotConversationList: retry=$attempt due to unstable list size",
                        exception
                    )
                }
            }
        }

        return try {
            snapshotRandomAccessList(source)
        } catch (_: Exception) {
            arrayListOf()
        }
    }

    private fun snapshotRandomAccessList(source: List<Conversation>): ArrayList<Conversation> {
        val initialSize = source.size
        val snapshot = ArrayList<Conversation>(initialSize)
        for (index in 0 until initialSize) {
            snapshot.add(source[index])
        }
        return snapshot
    }

//    fun updateList(newList: List<Conversation>) {
//        Log.d("", "updateList: newList <--------> 11111 ${newList.size}")
//        Log.d("", "onCreate: GetAllConversationlivelist <----------> 111111111111 chack fast ${newList.size}")
//        list.clear()
//
//        // Adding data in chunks
//        var index = 0
//        while (index < newList.size) {
//            val endIndex = minOf(index + 1, newList.size)
//            list.addAll(newList.subList(index, endIndex))
//            notifyDataSetChanged()
//            index += 15
//        }
//    }

    fun updateDrafts(context: Context) {
        ensureBackgroundThread {
            val newDrafts = HashMap<Long, String?>()
            fetchDrafts(newDrafts, context)
            if (drafts != newDrafts) {
                drafts = newDrafts
                CoroutineScope(Dispatchers.Main).launch {
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun updateviewForSwipe(context: Context) {
        ensureBackgroundThread {
                CoroutineScope(Dispatchers.Main).launch {
                    notifyDataSetChanged()
                }
        }
    }


    fun setInterface(messageClick: MessageClick) {
        this.messageClick = messageClick
    }

    fun setInterfaceMoreClick(moreOPtionClick: MoreOPtionClick) {
        this.moreOPtionClick = moreOPtionClick
    }

    fun setInterfaceMainClick(mainMessageClick: MainMessageClick) {
        this.mainMessageClick = mainMessageClick
    }


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun samedateortime(time: Long?, context: Context): Boolean {
        try {
            val date3 = Calendar.getInstance().time
            val date2 = Date(time!!)
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val dateString = format.format(date2)
            val date = LocalDate.parse(dateString)
            val formatter =
                DateTimeFormatter.ofPattern("dd MMM", Locale(context.config.SelectedLanguage))
            date.format(formatter)
            val date4 = Date(date3.time)
            val format2 = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val dateStrin1 = format2.format(date4)
            val date5 = LocalDate.parse(dateStrin1)
            val formatter2 =
                DateTimeFormatter.ofPattern("dd MMM", Locale(context.config.SelectedLanguage))
            date5.format(formatter2)
            return date.format(formatter) == date5.format(formatter2)
        } catch (e: Exception) {
            return false
        }
    }

    fun firstTwo(str: String): String? {
        return if (str.length < 2) str else str.substring(0, 1).uppercase(getDefault())
    }

//    override fun onChange(position: Int): CharSequence {
////        return list.getOrNull(position)?.title ?: ""
//    }


}

private fun fetchDrafts(drafts: HashMap<Long, String?>, context: Context) {
    drafts.clear()
    for ((threadId, draft) in context.getAllDrafts()) {
        drafts[threadId] = draft
    }
}
