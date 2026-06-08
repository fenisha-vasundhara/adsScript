package com.messenger.phone.number.text.sms.service.apps.fragment

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.demo.adsmanage.Commen.firebaseEventMain
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createOptionBackground
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getBottomNavigationBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMaterialDialogTheme
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
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.notificationallbuttonshowcaseshow
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.showsettingshowcase
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.ShowCase.DismissType
import com.messenger.phone.number.text.sms.service.apps.ShowCase.GuideView
import com.messenger.phone.number.text.sms.service.apps.ShowCase.PointerType
import com.messenger.phone.number.text.sms.service.apps.WallpaperChoiceDialogAdapter
import com.messenger.phone.number.text.sms.service.apps.data.Config
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentNotificationandsoundBinding
import com.simplemobiletools.commons.extensions.getContrastColor

class NotificationandsoundFragment : Fragment() {

    lateinit var binding: FragmentNotificationandsoundBinding


    private lateinit var config: Config
    private lateinit var items2: Array<String>
    private lateinit var items: Array<String>
    private var position: Int = 0


    private var builder: GuideView.Builder? = null
    private var mGuideView: GuideView? = null


    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireContext().setLocal()
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_notificationandsound,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        config = requireContext().config
        config.isnotificationscreenopen = true
        requireContext().config.fragmentshowcasecout = 2
        with(binding) {
            requireActivity().firebaseEventMain("Notification_Setting")
            fontSize10 = requireActivity().getTextSizeForeNormal10MS()
            fontSize13 = requireActivity().getTextSizeForeNormal13MS()
            fontSize18 = requireActivity().getTextSizeForeNormal18MS()
            fontSize8 = requireActivity().getTextSizeForeNormal8MS()
            fontSize15 = requireActivity().getTextSizeHometitleMS()

            binding.textsizechagefor10 = fontSize10
            binding.textsizechagefor13 = fontSize13
            binding.textsizechagefor18 = fontSize18
            binding.textsizechagefor8 = fontSize8
            binding.textsizechagefor15 = fontSize15

            // items is translate
            items = arrayOf(
                resources.getString(R.string.None),
                resources.getString(R.string.Mark_as_read),
                resources.getString(R.string.Reply),
                resources.getString(R.string.Call),
                resources.getString(R.string.Delete)
            )

            items2 = arrayOf("None", "Mark as read", "Reply", "Call", "Delete")

            setSelectionButton()

            backBtn.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            notificationGetOrNot.isChecked = config.isnotificationshow

            notificationGetOrNot.setOnCheckedChangeListener { _, isChecked ->
                config.isnotificationshow = isChecked
            }

            notificationGetOrNotAb.isChecked = config.isnotificationshow

            notificationGetOrNotAb.setOnCheckedChangeListener { _, isChecked ->
                config.isnotificationshow = isChecked
            }


            notiWakeScreenGetOrNot.isChecked = config.iswakescreen
            notiWakeScreenGetOrNotAb.isChecked = config.iswakescreen

            notiWakeScreenGetOrNot.setOnCheckedChangeListener { _, isChecked ->
                config.iswakescreen = isChecked
            }

            notiWakeScreenGetOrNotAb.setOnCheckedChangeListener { _, isChecked ->
                config.iswakescreen = isChecked
            }


            notificationUserBtnCard.setOnClickListener {
                setnotificationCustomDialog()
            }

            notificanBtn2.setOnClickListener {
                setnotificationCustomDialogAction("notificanBtn2")
            }

            notificanBtn1.setOnClickListener {
                setnotificationCustomDialogAction("notificanBtn1")
            }

            notificanBtn3.setOnClickListener {
                setnotificationCustomDialogAction("notificanBtn3")
            }


            notificationUserBtnCardAb.setOnClickListener {
                setnotificationCustomDialog()
            }

            notificanBtn2Ab.setOnClickListener {
                setnotificationCustomDialogAction("notificanBtn2")
            }

            notificanBtn1Ab.setOnClickListener {
                setnotificationCustomDialogAction("notificanBtn1")
            }

            notificanBtn3Ab.setOnClickListener {
                setnotificationCustomDialogAction("notificanBtn3")
            }






            if (requireContext().config.SelectedLanguage == "ar") {
                textView9.gravity = Gravity.END
                info9.gravity = Gravity.END
                info8.gravity = Gravity.END
                textView9Ab.gravity = Gravity.END
                info9Ab.gravity = Gravity.END
                info8Ab.gravity = Gravity.END

            } else {
                textView9.gravity = Gravity.START
                info9.gravity = Gravity.START
                info8.gravity = Gravity.START
                textView9Ab.gravity = Gravity.START
                info9Ab.gravity = Gravity.START
                info8Ab.gravity = Gravity.START
            }
        }

        if (notificationallbuttonshowcaseshow) {
//            showCasesettingActivitynotificationFirst()
        } else if (requireActivity().config.secondshowcaseshowforonlysetting) {
//            showCasesettingActivitynotificationFirst()
        }

    }

    private fun showCasesettingActivitynotificationFirst() {


        builder = GuideView.Builder(requireActivity())
            .setTitle("1")
            .setContentText("1")
            .setGravity(com.messenger.phone.number.text.sms.service.apps.ShowCase.Gravity.center)
            .setDismissType(DismissType.targetView)
            .setPointerType(PointerType.circle)
            .setTargetView(binding.preandsecBtnCard)
            .setGuideListener { view ->
                when (view.id) {
                    R.id.preandsec_btn_card -> {
                        builder?.setTargetView(binding.notificationUserBtnCard)
                            ?.setTitle("2")
                            ?.setContentText("2")
                    }

                    R.id.notification_user_btn_card -> {
                        builder?.setTargetView(binding.notiWakeScreenBtnCard)
                            ?.setTitle("3")
                            ?.setContentText("3")
                    }

                    R.id.noti_wake_screen_btn_card -> {
                        builder?.setTargetView(binding.notificanBtn1)
                            ?.setTitle("4")
                            ?.setContentText("4")
                    }

                    R.id.notifican_btn_1 -> {
                        builder?.setTargetView(binding.notificanBtn2)
                            ?.setTitle("5")
                            ?.setContentText("5")
                    }

                    R.id.notifican_btn_2 -> {
                        builder?.setTargetView(binding.notificanBtn3)
                            ?.setTitle("6")
                            ?.setContentText("6")
                    }

                    R.id.notifican_btn_3 -> {
                        builder?.setTargetView(binding.notificanBtn3)
                            ?.setTitle("7")
                            ?.setContentText("7")
                        showsettingshowcase = false
                        requireActivity().config.secondshowcaseshowforonlysetting = false
                        requireActivity().config.savethidshowcaseforprivacyOnce()
                        return@setGuideListener
                    }
                }
                mGuideView = builder?.build()
                mGuideView?.show()
            }

        mGuideView = builder?.build()
        mGuideView?.show()

    }

    private fun setSelectionButton() {

        binding.archiveUserCount.text = items[items2.indexOf(config.NotiButton1)]
        binding.btnArchiveUserCount.text = items[items2.indexOf(config.NotiButton2)]
        binding.btn3ArchiveUserCount.text = items[items2.indexOf(config.NotiButton3)]

        binding.archiveUserCountAb.text = items[items2.indexOf(config.NotiButton1)]
        binding.btnArchiveUserCountAb.text = items[items2.indexOf(config.NotiButton2)]
        binding.btn3ArchiveUserCountAb.text = items[items2.indexOf(config.NotiButton3)]

    }

    private fun setnotificationCustomDialog() {
        val items = arrayOf(
            resources.getString(R.string.Show_name_new_ui),
            resources.getString(R.string.Show_name_and_message)
        )
        val items2 = arrayOf("Show name", "Show name and message", "Hide contents")
        val position = items2.indexOf(config.lastselectedpreviews)
        var selectedItem = position
        val primaryColor = requireContext().getProperPrimaryColor()
        val textColor = requireContext().getProperTextColor()
        val adapter = WallpaperChoiceDialogAdapter(
            context = requireContext(),
            options = items,
            selectedPosition = selectedItem,
            primaryColor = primaryColor,
            textColor = textColor
        )

        val dialog = MaterialAlertDialogBuilder(requireContext(), requireContext().getMaterialDialogTheme())
            .setTitle(resources.getString(R.string.Notification_previews))
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                config.lastselectedpreviews = items2[selectedItem]
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .setSingleChoiceItems(adapter, selectedItem) { _, which ->
                selectedItem = which
                adapter.updateSelection(which)
            }
            .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(primaryColor)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(textColor)
        dialog.listView?.post {
            adapter.updateSelection(selectedItem)
        }

    }

    private fun setnotificationCustomDialogAction(buttonselected: String) {

        position = if (buttonselected == "notificanBtn2") {
            items2.indexOf(config.NotiButton2)
        } else if (buttonselected == "notificanBtn1") {
            items2.indexOf(config.NotiButton1)
        } else {
            items2.indexOf(config.NotiButton3)
        }
        var selectedItem = position
        val primaryColor = requireContext().getProperPrimaryColor()
        val textColor = requireContext().getProperTextColor()
        val adapter = WallpaperChoiceDialogAdapter(
            context = requireContext(),
            options = items,
            selectedPosition = selectedItem,
            primaryColor = primaryColor,
            textColor = textColor
        )
        MaterialAlertDialogBuilder(requireContext(), requireContext().getMaterialDialogTheme())
            .setTitle(resources.getString(R.string.Change_Action))
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                if (buttonselected == "notificanBtn2") {
                    if (config.NotiButton1 == items2[selectedItem] || config.NotiButton3 == items2[selectedItem]) {
                        if (items2[selectedItem] == "None") {
                            config.NotiButton2 = items2[selectedItem]
                        } else {
                            if (items2[selectedItem] == config.NotiButton1) {
                                config.NotiButton1 = config.NotiButton2
                                config.NotiButton2 = items2[selectedItem]
                            } else if (items2[selectedItem] == config.NotiButton3) {
                                config.NotiButton3 = config.NotiButton2
                                config.NotiButton2 = items2[selectedItem]
                            }
                            setSelectionButton()
                            return@setPositiveButton
                        }
                    } else {
                        config.NotiButton2 = items2[selectedItem]
                    }
                } else if (buttonselected == "notificanBtn1") {
                    if (config.NotiButton2 == items2[selectedItem] || config.NotiButton3 == items2[selectedItem]) {
                        if (items2[selectedItem] == "None") {
                            config.NotiButton1 = items2[selectedItem]
                        } else {
                            if (items2[selectedItem] == config.NotiButton2) {
                                config.NotiButton2 = config.NotiButton1
                                config.NotiButton1 = items2[selectedItem]
                            } else if (items2[selectedItem] == config.NotiButton3) {
                                config.NotiButton3 = config.NotiButton1
                                config.NotiButton1 = items2[selectedItem]
                            }
                            setSelectionButton()
                            return@setPositiveButton
                        }
                    } else {
                        config.NotiButton1 = items2[selectedItem]
                    }
                } else {
                    if (config.NotiButton1 == items2[selectedItem] || config.NotiButton2 == items2[selectedItem]) {
                        if (items2[selectedItem] == "None") {
                            config.NotiButton3 = items2[selectedItem]
                        } else {
                            if (items2[selectedItem] == config.NotiButton1) {
                                config.NotiButton1 = config.NotiButton3
                                config.NotiButton3 = items2[selectedItem]
                            } else if (items2[selectedItem] == config.NotiButton2) {
                                config.NotiButton2 = config.NotiButton3
                                config.NotiButton3 = items2[selectedItem]
                            }
                            setSelectionButton()
                            return@setPositiveButton
                        }
                    } else {
                        config.NotiButton3 = items2[selectedItem]
                    }
                }
                setSelectionButton()
            }.setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                setSelectionButton()
                dialog.dismiss()
            }.setSingleChoiceItems(adapter, selectedItem) { _, which ->
                selectedItem = which
                adapter.updateSelection(which)
            }.show()
            .also { dialog ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(primaryColor)
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(textColor)
                dialog.listView?.post {
                    adapter.updateSelection(selectedItem)
                }
            }

    }


    override fun onStart() {
        super.onStart()

        if (requireContext().config.setABHomeActivityPref == "1") {
            binding.firstContenar.visible()
            binding.firstContenarAb.gone()
        } else {
            binding.firstContenar.gone()
            binding.firstContenarAb.visible()
        }

        applyMaterialNotificationTheme()
        applyMaterialSystemBarColors()

    }

    private fun applyMaterialNotificationTheme() {
        val context = requireContext()
        val surfaceColor = context.getProperBackgroundColor()
        val textColor = context.getProperTextColor()
        val secondaryTextColor = context.getProperSecondaryTextColor()
        val primaryColor = context.getProperPrimaryColor()
        val sectionFill = primaryColor.adjustAlpha(0.10f)
        val chipFill = primaryColor.adjustAlpha(0.20f)
        val dividerColor = secondaryTextColor.adjustAlpha(0.18f)
        val rippleColor = primaryColor.adjustAlpha(0.28f)
        val strokeColor = surfaceColor
        val rowCorner = resources.getDimension(com.intuit.sdp.R.dimen._5sdp)
        val sectionCorner = resources.getDimension(com.intuit.sdp.R.dimen._10sdp)
        val strokeWidth = resources.getDimension(com.intuit.sdp.R.dimen._1sdp)
        val dividerHeight = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp)

        binding.mainBg.setBackgroundColor(surfaceColor)
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
        binding.textView3.setTextColor(textColor)
        binding.backBtn.iconTint = ColorStateList.valueOf(secondaryTextColor)

        applyTextColorRecursive(binding.firstContenar, textColor)
        applyTextColorRecursive(binding.firstContenarAb, textColor)

        listOf(
            binding.info9,
            binding.info8,
            binding.info9Ab,
            binding.info8Ab
        ).forEach {
            it.setTextColor(textColor)
        }

        listOf(
            binding.notificationacction,
            binding.notificationacctionAb
        ).forEach { it.setTextColor(primaryColor) }




        listOf(
            binding.preandsecBtnCard,
            binding.notificationUserBtnCard,
            binding.notiWakeScreenBtnCard,
            binding.notificanBtn1,
            binding.notificanBtn2,
            binding.notificanBtn3,
            binding.preandsecBtnCardAb,
            binding.notificationUserBtnCardAb,
            binding.notiWakeScreenBtnCardAb,
            binding.notificanBtn1Ab,
            binding.notificanBtn2Ab,
            binding.notificanBtn3Ab
        ).forEach { container ->
            container.background = createOptionBackground(
                cornerSize = rowCorner,
                fillColor = sectionFill,
                strokeWidth = strokeWidth,
                strokeColor = strokeColor,
                rippleColor = rippleColor,
                showRipple = container.id == binding.notificationUserBtnCardAb.id ||
                        container.id == binding.notificanBtn1Ab.id ||
                        container.id == binding.notificanBtn2Ab.id ||
                        container.id == binding.notificanBtn3Ab.id,
                isTop = container.id == binding.preandsecBtnCardAb.id || container.id == binding.notificanBtn1Ab.id,
                isBottom = container.id == binding.notiWakeScreenBtnCardAb.id || container.id == binding.notificanBtn3Ab.id,
            )
        }


        val actionTextPillColor = ColorUtils.blendARGB(primaryColor, surfaceColor, 0.7f)

        listOf(
            binding.notificationActionButton1,
            binding.notificationActionButton2,
            binding.notificationActionButton3,
        ).forEach { view ->

            view.background = createOptionBackground(
                cornerSize = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._50sdp).toFloat(),
                fillColor = actionTextPillColor,
                strokeWidth = 0f,
                strokeColor = strokeColor,
                rippleColor = rippleColor,
                showRipple = false
            )
        }


        listOf(
            binding.archiveUserCount,
            binding.btnArchiveUserCount,
            binding.btn3ArchiveUserCount,
            binding.archiveUserCountAb,
            binding.btnArchiveUserCountAb,
            binding.btn3ArchiveUserCountAb
        ).forEach {
            it.setTextColor(actionTextPillColor.getContrastColor())
        }


        listOf(
            binding.imageView17,
            binding.btnImageView17,
            binding.btn3ImageView17,
            binding.imageView17Ab,
            binding.btnImageView17Ab,
            binding.btn3ImageView17Ab
        ).forEach {
            it.imageTintList = ColorStateList.valueOf(actionTextPillColor.getContrastColor())
        }

//        binding.noTitleAb.background = createOptionBackground(
//            cornerSize = sectionCorner,
//            fillColor = sectionFill,
//            strokeWidth = strokeWidth,
//            strokeColor = strokeColor,
//            rippleColor = rippleColor,
//            showRipple = false
//        )
//        binding.titalContenar.background = createOptionBackground(
//            cornerSize = sectionCorner,
//            fillColor = sectionFill,
//            strokeWidth = strokeWidth,
//            strokeColor = strokeColor,
//            rippleColor = rippleColor,
//            showRipple = false
//        )

        listOf(binding.firstContenar, binding.firstContenarAb).forEach { root ->
            tintDescendantCardViews(root, chipFill)
            tintDividers(root, dividerColor, dividerHeight)
        }

        applyMaterialSwitchColors(
            primaryColor = primaryColor,
            textColor = textColor
        )
    }

    private fun applyMaterialSwitchColors(primaryColor: Int, textColor: Int) {
        val offTrackColor = textColor.adjustAlpha(0.20f)
        val offThumbColor = textColor.adjustAlpha(0.40f)
        val onThumbColor = primaryColor.getContrastColor()

        val switches = listOf(
            binding.notificationGetOrNot,
            binding.notiWakeScreenGetOrNot,
            binding.notificationGetOrNotAb,
            binding.notiWakeScreenGetOrNotAb
        )

        switches.forEach { switchView ->
            switchView.trackTintList = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_checked)
                ),
                intArrayOf(offTrackColor, primaryColor)
            )
            switchView.thumbTintList = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_checked)
                ),
                intArrayOf(offThumbColor, onThumbColor)
            )
            switchView.thumbIconTintList = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_checked)
                ),
                intArrayOf(offTrackColor, primaryColor)
            )
            switchView.trackDecorationTintList = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_checked)
                ),
                intArrayOf(offThumbColor, primaryColor)
            )
        }
    }

    private fun applyMaterialSystemBarColors() {
        val context = requireContext()
        val surfaceColor = context.getProperBackgroundColor()
        val statusBarColor = context.getProperStatusBarColor()
        val navigationBarColor = context.getBottomNavigationBackgroundColor()
        val window = requireActivity().window
        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars = ThemeModeManager.shouldUseLightSystemBars(context)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
    }

    private fun applyTextColorRecursive(root: View, color: Int) {
        if (root is com.google.android.material.textview.MaterialTextView) {
            root.setTextColor(color)
        }
        if (root is ViewGroup) {
            for (index in 0 until root.childCount) {
                applyTextColorRecursive(root.getChildAt(index), color)
            }
        }
    }

    private fun tintDescendantCardViews(root: View, fillColor: Int) {
        if (root is CardView) {
            root.setCardBackgroundColor(fillColor)
        }
        if (root is ViewGroup) {
            for (index in 0 until root.childCount) {
                tintDescendantCardViews(root.getChildAt(index), fillColor)
            }
        }
    }

    private fun tintDividers(root: View, color: Int, dividerHeight: Int) {
        if (root is ViewGroup) {
            for (index in 0 until root.childCount) {
                val child = root.getChildAt(index)
                val childHeight = child.layoutParams?.height ?: 0
                if (child !is MaterialSwitch && childHeight in 1..dividerHeight) {
                    child.setBackgroundColor(color)
                }
                tintDividers(child, color, dividerHeight)
            }
        }
    }


}
