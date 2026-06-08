package com.messenger.phone.number.text.sms.service.apps

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.firebaseEventMain
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.google.android.material.color.DynamicColors
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.messenger.phone.number.text.sms.service.apps.CommanClass.allresultlist
import com.messenger.phone.number.text.sms.service.apps.CommanClass.allresultlistcontact
import com.messenger.phone.number.text.sms.service.apps.CommanClass.blockcontectremove
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
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
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadId
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.hideKeyboard
import com.messenger.phone.number.text.sms.service.apps.CommanClass.higlitetetxt
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.showKeyboard
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setCursorColorProgrammatically
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.adapter.ContactSearchAdapter
import com.messenger.phone.number.text.sms.service.apps.adapter.MessageSearchAdapter
import com.messenger.phone.number.text.sms.service.apps.adapter.RecentSearchAdapter
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivitySearchBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.ContactNumberClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MessageSearchAdapterClickInterface
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.RecentSearchAdapterInterface
import com.messenger.phone.number.text.sms.service.apps.modelClass.Contact
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Recentsearch
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetAllConversationViewModel
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetAllRecentSearchViewModel
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetContactNumberViewModel

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.transition.platform.MaterialSharedAxis
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class SearchActivity : AppCompatActivity(), MessageSearchAdapterClickInterface, ContactNumberClick,
    RecentSearchAdapterInterface {

    lateinit var binding: ActivitySearchBinding

    @Inject
    lateinit var adapter: MessageSearchAdapter

    @Inject
    lateinit var contactSearchAdapter: ContactSearchAdapter

    lateinit var model: GetAllConversationViewModel

    @Inject
    lateinit var repo: MessagerDatabaseRepo

    @Inject
    lateinit var recentSearchAdapter: RecentSearchAdapter

    var serchtext = ""

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    private var convolist2: ArrayList<Conversation> = arrayListOf()
    private var contactlist: ArrayList<Contact> = arrayListOf()
    var fromgetnewsearchactivity = false
    private var hasMessageResults = false
    private var hasContactResults = false
    private var selectedTab = SearchTabType.MESSAGE

    private enum class SearchTabType {
        MESSAGE,
        CONTACT
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeModeManager.applyThemeMode(this)
        if (ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM) {
            DynamicColors.applyToActivityIfAvailable(this)
        }
        super.onCreate(savedInstanceState)
        setLocal()
//        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)

//        val enter = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
//            addTarget(R.id.main_bg)
//        }
//        window.enterTransition = enter
//
//        window.allowEnterTransitionOverlap = true

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
//        setupSharedElementTransitions()
        setBaseTheme(binding.vAnd15StatusBar)

//        applyStatusBarInsets()
        this.firebaseEventMain("Search_All_Message")
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

        binding.adapter = adapter
        binding.contactSearchAdapter = contactSearchAdapter
        binding.recentSearchAdapter = recentSearchAdapter
        adapter.setHasStableIds(true)
        binding.searchResult.itemAnimator = null
        binding.searchResultCon.itemAnimator = null
        binding.searchBarFull.requestFocus()
        applyMaterialSearchColors()
        setupTabs()

        //kpBannerAd()
        binding.searchBarFull.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else {
                false
            }
        }


//        binding.keybordopen.setOnClickListener {
//
//            binding.keybordopen.setImageResource(R.drawable.open_keybord_number_not)
//
//            binding.searchBarFull.inputType = InputType.TYPE_CLASS_TEXT
//            binding.searchBarFull.requestFocus()
//            this.showKeyboard(binding.searchBarFull)
//        }


        adapter.setInterface(this)
        recentSearchAdapter.setInterface(this)
        contactSearchAdapter.setContactclick(this)

        model = ViewModelProvider(this)[GetAllConversationViewModel::class.java]

        model.GetAllConversationlivelist.observe(this, Observer {

            convolist2 = ArrayList(it)
            if (serchtext != "") {
                filter(serchtext)
                filtercontact(serchtext)
            }
        })

        val model = ViewModelProvider(this)[GetContactNumberViewModel::class.java]

        model.ccontactlist.observe(this) {

            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    it.forEachIndexed { index, conversation ->
                        if (blockcontectremove.any { it.title == conversation.name && it.isPrivateChat == false }) {
                            contactlist.add(conversation)
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    if (serchtext != "") {
                        filter(serchtext)
                        filtercontact(serchtext)
                    }
                }
            }


        }

        val recentmodel = ViewModelProvider(this)[GetAllRecentSearchViewModel::class.java]

        recentmodel.GetAllRecentSearchlivelist.observe(this) {

//            recentSearchAdapter.listdataget = it as ArrayList<Recentsearch>
//            binding.recentvisiblrornot = binding.searchBarFull.text.isNullOrEmpty() && it.isNotEmpty()
//            binding.norecentfound = it.isEmpty()
            binding.recentvisiblrornot = false
            binding.norecentfound = true

        }

        binding.messagebutton2.setOnClickListener {
            Constants.isActivitychange = true
           onBackPressed()
        }

        binding.serchCleasr.setOnClickListener {
            binding.searchBarFull.text?.clear()
        }


        binding.RecentMessageCLEAR.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                repo.deleteAllrecentsearchRepo()
            }
        }

        binding.searchBarFull.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
//                if (recentSearchAdapter.listdataget.isNotEmpty()) {
//                    binding.recentvisiblrornot = editable.isEmpty()
//                } else {
                    binding.recentvisiblrornot = false
//                }
                serchtext = editable.toString()
                filter(editable.toString())
                filtercontact(editable.toString())

                higlitetetxt = editable.toString()
                adapter.highlighttext = editable.toString()
                contactSearchAdapter.highlighttext = editable.toString()
                if (editable.isEmpty()) {
                    binding.serchCleasr.gone()
                } else {
                    binding.serchCleasr.visible()
                }


            }
        })

        binding.txtConversationtotal.setOnClickListener {
            Constants.isActivitychange = true
            startActivity(
                Intent(this@SearchActivity, SearchAllResultActivity::class.java).putExtra(
                    "iscon",
                    true
                )
            )
            allresultlistcontact = contactSearchAdapter.list
            higlitetetxt = contactSearchAdapter.highlighttext
        }

        binding.txtMessagetotal.setOnClickListener {
            Constants.isActivitychange = true
            startActivity(
                Intent(this@SearchActivity, SearchAllResultActivity::class.java).putExtra(
                    "iscon",
                    false
                )
            )
            allresultlist = adapter.list
            higlitetetxt = adapter.highlighttext
        }

        fromgetnewsearchactivity = intent.getBooleanExtra("fromgetnewsearchactivity", false)
        if (fromgetnewsearchactivity) {
            val datafromgetnewsearchactivity =
                intent.getStringExtra("datafromgetnewsearchactivity").toString()
            if (datafromgetnewsearchactivity.trim().isNotEmpty()) {
                binding.searchBarFull.setText(datafromgetnewsearchactivity)
            }
        }
    }

    private fun setupSharedElementTransitions() {
        val transitionDuration = 600L
        val easing = FastOutSlowInInterpolator()

        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(R.id.main_bg)
            drawingViewId = android.R.id.content
            scrimColor = Color.TRANSPARENT
            fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
            duration = transitionDuration
            interpolator = easing
        }

        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(R.id.main_bg)
            drawingViewId = android.R.id.content
            scrimColor = Color.TRANSPARENT
            fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
            duration = transitionDuration
            interpolator = easing
        }
    }



    private fun applyStatusBarInsets() {
        binding.vAnd15StatusBar.visible()
        ViewCompat.setOnApplyWindowInsetsListener(binding.vAnd15StatusBar) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            view.layoutParams = view.layoutParams.apply {
                height = statusBarHeight
            }
            binding.mainBg.updatePadding(bottom = navBarHeight)
            insets
        }
        ViewCompat.requestApplyInsets(binding.vAnd15StatusBar)
    }

    private fun setupTabs() {
        binding.searchTabs.setOnCheckedStateChangeListener { _, checkedIds ->
            val checkedChipId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            selectedTab = if (checkedChipId == R.id.message_chip) {
                SearchTabType.MESSAGE
            } else {
                SearchTabType.CONTACT
            }
            updateSearchSectionVisibility()
        }
        selectedTab = SearchTabType.MESSAGE
        updateTabTitles()
        binding.searchTabs.check(R.id.message_chip)
        updateSearchSectionVisibility()
    }

    private fun updateTabTitles() {
        binding.messageChip.text =
            "${resources.getString(R.string.Message)} (${adapter.list.size})"
        binding.contactChip.text =
            "${resources.getString(R.string.Contact)} (${contactSearchAdapter.list.size})"
    }

    private fun updateSearchSectionVisibility() {
        val hasAnyResults = hasMessageResults || hasContactResults
        if (serchtext.isBlank() || !hasAnyResults) {
            binding.searchTabs.gone()
            binding.fullScreen.gone()
            binding.messageContenar.gone()
            binding.ContactContenar.gone()
            return
        }

        binding.messageChip.visibility = if (hasMessageResults) VISIBLE else GONE
        binding.contactChip.visibility = if (hasContactResults) VISIBLE else GONE
        binding.searchTabs.visible()
        binding.divederView.gone()

        if (!hasMessageResults) {
            selectedTab = SearchTabType.CONTACT
        } else if (!hasContactResults) {
            selectedTab = SearchTabType.MESSAGE
        }

        val selectedChipId = if (selectedTab == SearchTabType.MESSAGE) {
            R.id.message_chip
        } else {
            R.id.contact_chip
        }
        if (binding.searchTabs.checkedChipId != selectedChipId) {
            binding.searchTabs.check(selectedChipId)
        }

        binding.fullScreen.visible()
        when (selectedTab) {
            SearchTabType.MESSAGE -> {
                binding.messageContenar.visible()
                binding.ContactContenar.gone()
            }

            SearchTabType.CONTACT -> {
                binding.messageContenar.gone()
                binding.ContactContenar.visible()
            }
        }
    }
    private fun resolveDefaultIncomingBubbleColor(): Int {
        val isLightTheme = ThemeModeManager.shouldUseLightSystemBars(this)

        val defaultColor = ColorUtils.blendARGB(
            getProperBackgroundColor(), getProperPrimaryColor(), if (isLightTheme) 0.4f else 0.25f
        )
        return defaultColor
    }
    private fun applyAdapterBubblePalette() {
        adapter.inmessagecolorcustomwallpaper =
            formatColor(resolveDefaultIncomingBubbleColor())

    }
    private fun formatColor(color: Int): String = String.format(Locale.US, "#%08X", color)

    private fun applyMaterialSearchColors() {
        val backgroundColor = getProperBackgroundColor()
        val statusBarColor = getProperStatusBarColor()
        val primaryColor = getProperPrimaryColor()
        val textColor = getProperTextColor()

        val primaryContainerMedium = blendWithBaseColor(primaryColor, backgroundColor, 0.10f)
        val primaryContainerLow = blendWithBaseColor(primaryColor, backgroundColor, 0.06f)
        val primaryOutline = blendWithBaseColor(primaryColor, backgroundColor, 0.40f)
        val primaryText = ColorUtils.blendARGB(textColor, primaryColor, 0.32f)
        val primaryTextSecondary = ColorUtils.blendARGB(textColor, primaryColor, 0.22f)

        binding.mainBg.setBackgroundColor(backgroundColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        binding.toolBarMain.setCardBackgroundColor(primaryContainerMedium)
        binding.toolBarMain.strokeColor = primaryOutline
        binding.searchMessageBg.background = createRoundedBackground(
            fillColor = Color.TRANSPARENT,
            strokeColor = Color.TRANSPARENT,
            cornerRadiusResId = com.intuit.sdp.R.dimen._20sdp
        )
        val chipBackgroundColors = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf()
            ),
            intArrayOf(
                primaryContainerLow,
                Color.TRANSPARENT
            )
        )
        val chipTextColors = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf()
            ),
            intArrayOf(
                primaryColor,
                primaryTextSecondary.adjustAlpha(0.9f)
            )
        )
        val tintedCheck = resources.getDrawable(R.drawable.check_chip, null)?.mutate()
        tintedCheck?.setTint(primaryColor)
        listOf(binding.messageChip, binding.contactChip).forEach { chip ->
            chip.chipBackgroundColor = chipBackgroundColors
            chip.setTextColor(chipTextColors)
            chip.chipStrokeColor = ColorStateList.valueOf(primaryOutline)
            chip.chipStrokeWidth = resources.getDimension(com.intuit.sdp.R.dimen._1sdp)
            chip.rippleColor = ColorStateList.valueOf(primaryColor.adjustAlpha(0.16f))
            chip.isCheckedIconVisible = true
            chip.checkedIcon= tintedCheck
           chip.checkedIconTint =  ColorStateList.valueOf(primaryColor)


        }
        applyAdapterBubblePalette()
        binding.fullScreen.setBackgroundColor(backgroundColor)
        binding.messageContenar.setBackgroundColor(backgroundColor)
        binding.ContactContenar.setBackgroundColor(backgroundColor)

        binding.searchBarFull.setTextColor(primaryText)
        binding.searchBarFull.setHintTextColor(primaryTextSecondary.adjustAlpha(0.57f))
        binding.searchBarFull.setCursorColorProgrammatically(primaryColor)

        binding.txtMessage.setTextColor(primaryText)
        binding.txtConversation.setTextColor(primaryText)
        binding.RecentMessage.setTextColor(primaryTextSecondary)
        binding.RecentMessageCLEAR.setTextColor(primaryColor)
        binding.txtMessagetotal.setTextColor(primaryColor)
        binding.txtConversationtotal.setTextColor(primaryColor)
        binding.serchCleasr.setColorFilter(primaryTextSecondary)
        binding.messagebutton2.setColorFilter(primaryTextSecondary)
//        binding.keybordopen.setColorFilter(primaryTextSecondary)
        binding.divederView.setBackgroundColor(primaryOutline)
    }

    private fun norecentsechfound() {
        Handler(Looper.myLooper()!!).postDelayed({
            Log.d(
                "",
                "norecentsechfound: <--------> 1 ${!(binding.fullScreen.visibility == VISIBLE || binding.RecentMessageCLEAR.visibility == VISIBLE)}"
            )
            binding.norecentfound =
                !(binding.fullScreen.visibility == VISIBLE || binding.RecentMessageCLEAR.visibility == VISIBLE)
        }, 50)

    }



    @SuppressLint("SetTextI18n")
    private fun filtercontact(text: String) {

        try {
            val filterdNames = ArrayList<Contact>()


            GlobalScope.launch {

                withContext(Dispatchers.IO) {
                    try {
                        for (s in contactlist) {
                            if (s.name.trim().replace("\\s".toRegex(), "")
                                    .lowercase(Locale.getDefault()).contains(
                                        text.trim().replace("\\s".toRegex(), "")
                                            .lowercase(Locale.getDefault())
                                    )
                            ) {
                                filterdNames.add(s)
                            } else if (s.number.trim().replace("\\s".toRegex(), "").replace("(", "")
                                    .replace(")", "")
                                    .replace("-", "")
                                    .lowercase(Locale.getDefault())
                                    .contains(
                                        text.trim().replace("\\s".toRegex(), "")
                                            .lowercase(Locale.getDefault())
                                    )
                            ) {
                                filterdNames.add(s)
                            }
                        }
                    } catch (e: Exception) {

                    }
                }

                withContext(Dispatchers.Main) {
                    if (text.isEmpty()) {
                        contactSearchAdapter.list = arrayListOf()
                    } else {
                        contactSearchAdapter.list = filterdNames
                    }
                    hasContactResults = contactSearchAdapter.list.isNotEmpty()

//                    binding.txtConversation.text =
//                        resources.getString(R.string.Contact) + " " + "(" + contactSearchAdapter.list.size.toString() + ")"
                    updateTabTitles()
                    updateSearchSectionVisibility()
                    norecentsechfound()

                }

            }


        } catch (e: Exception) {
        }

    }

    @SuppressLint("SetTextI18n")
    private fun filter(text: String) {

        try {
            val filterdNames = ArrayList<Conversation>()

            for (s in convolist2) {
                if (s.snippet.trim().replace("\\s".toRegex(), "").lowercase(Locale.getDefault())
                        .contains(
                            text.trim().replace("\\s".toRegex(), "").lowercase(Locale.getDefault())
                        )
                ) {
                    filterdNames.add(s)
                }
            }

            runOnUiThread {
                if (text.isEmpty()) {
                    adapter.list = arrayListOf()
                } else {
                    adapter.list = filterdNames
                }
                hasMessageResults = adapter.list.isNotEmpty()
//
//                binding.txtMessage.text =
//                    resources.getString(R.string.Message) + " " + "(" + adapter.list.size.toString() + ")"
                updateTabTitles()
                updateSearchSectionVisibility()
                norecentsechfound()
            }

        } catch (e: Exception) {

        }
    }

    override fun MessageSearchAdapterOnClick(position: Int, list: ArrayList<Conversation>) {
        if (position < 0 || position >= list.size) {
            return
        }
        Log.d("", "getsetIntent: 122 <---> <---------> ${list[position].snippet}")
        Constants.isActivitychange = true
        addrecentsearch()
        CoroutineScope(Dispatchers.IO).launch {
            list[position].messageId?.toLong()?.let { repo.addmessagefoundRepo(it) }
        }
        Log.e("FFF", "MessageSearchAdapterOnClick:" +
                " \ntredid  ${list[position].threadId}" +
                "\nname  ${list[position].title}" +
                "\nisSearchFound  ${"true"}" +
                "\nisSearchFoundmessage  ${list[position].snippet}" +
                "\nisSearchFoundMessageId  ${list[position].messageId ?: -1L}" +
                "\nmobileNumber  ${list[position].phoneNumber}")
        startActivity(
            Intent(
                this@SearchActivity,  SendMessageActivity::class.java)
                .putExtra("tredid", list[position].threadId)
                .putExtra("name", list[position].title)
                .putExtra("isSearchFound", true)
                .putExtra("isSearchFoundmessage", list[position].snippet)
                .putExtra("isSearchFoundMessageId", list[position].messageId ?: -1L)
                .putExtra("mobileNumber", list[position].phoneNumber)

        )
    }

    override fun onClick(mobilenumber: String, pos: Int, name: String) {
        Constants.isActivitychange = true
        if (chackPermission()) {
            addrecentsearch()
            startActivity(
                Intent(
                    this@SearchActivity, if (config.Message_Send_Activity == "1") {
                        SendMessageActivity::class.java
                    } else {
                        SendMessageActivity::class.java
                    }
                ).putExtra("tredid", getThreadId(mobilenumber)).putExtra("name", name)
                    .putExtra("mobileNumber", mobilenumber)
            )
        }

    }

    override fun OnLongClick() {

    }

    fun chackPermission(): Boolean {
        return PermissionChecker.checkSelfPermission(
            this,
            android.Manifest.permission.SEND_SMS
        ) == PermissionChecker.PERMISSION_GRANTED
    }


    fun addrecentsearch() {
        CoroutineScope(Dispatchers.IO).launch {
            if (!repo.isrecentsearchExitsRepo(higlitetetxt)) {
                repo.addrecentsearchRepo(Recentsearch(recentsearch = higlitetetxt))
            }
        }
    }

    override fun onRecentSearchClick(postion: Int, recentlist: ArrayList<Recentsearch>) {
        binding.searchBarFull.setText(recentlist[postion].recentsearch)
    }


    override fun onStart() {
        super.onStart()
        ThemeSetup()
        updateTabTitles()
        updateSearchSectionVisibility()
//        if (recentSearchAdapter.listdataget.isNotEmpty()) {
//            binding.recentvisiblrornot = binding.searchBarFull.text.isNullOrEmpty()
//        } else {
            binding.recentvisiblrornot = false
//        }
    }

    private fun ThemeSetup() {
        applyMaterialSearchColors()
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
            val useLightSystemBars = ThemeModeManager.shouldUseLightSystemBars(this@SearchActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
    }

    private fun createRoundedBackground(
        fillColor: Int,
        strokeColor: Int,
        cornerRadiusResId: Int,
    ): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = resources.getDimension(cornerRadiusResId)
            setColor(fillColor)
            setStroke(resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp), strokeColor)
        }
    }

    private fun blendWithBaseColor(primaryColor: Int, baseColor: Int, ratio: Float): Int {
        return ColorUtils.blendARGB(baseColor, primaryColor, ratio.coerceIn(0f, 1f))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        hideKeyboard()
    }

}
