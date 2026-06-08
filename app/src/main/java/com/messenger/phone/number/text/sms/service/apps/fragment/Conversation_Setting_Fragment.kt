package com.messenger.phone.number.text.sms.service.apps.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getchatIntxtcolor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getchatincolor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getchatoutcolor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getchatpreIntxtdatecolor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getchatpreouttxtdatecolor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.islinechange
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setchatthemecolorBg
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.Dialog.ConSettingTutorialDialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.TutorialDialog
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.adapter.MainMassageAdapter
import com.messenger.phone.number.text.sms.service.apps.data.Config
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentConversationSettingBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class Conversation_Setting_Fragment : Fragment() {

    private lateinit var items2: Array<String>
    private lateinit var items: Array<String>

    @Inject
    lateinit var adapterMainMassage: MainMassageAdapter

    private val conSettingTutorialDialog by lazy { ConSettingTutorialDialog() }
    private var position: Int = 0
    lateinit var binding: FragmentConversationSettingBinding
    private lateinit var config: Config
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        requireContext().setLocal()
        binding = FragmentConversationSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        config = requireContext().config

        if (config.consettingtutorialshow) {
            conSettingTutorialDialog.show(requireActivity().supportFragmentManager, "conSettingTutorialDialog")
        }

        items = arrayOf(
            resources.getString(R.string.Archive), resources.getString(R.string.Delete), resources.getString(R.string.Call), resources.getString(R.string.Mark_as_read), resources.getString(R.string.Pin), resources.getString(R.string.Privacy_chat)
        )

        items2 = arrayOf("Archive", "Delete", "Call", "Mark as read", "Pin", "Private Chat")


        setdefaultselection()
        setdefaultselation()
        setSeletionline(config.line_selection)
        updatechatpreview()
        with(binding) {
            colorTheme.gone()
            colorThemeBtnCard.gone()

            InAppBrowserChange.isChecked = config.isinappbrowser
            InAppBrowserChangeShowMesageCount.isChecked = config.showCharacterCounter

            InAppBrowserChange.setOnCheckedChangeListener { _, isChecked ->
                config.isinappbrowser = isChecked
            }

            InAppBrowserChangeShowMesageCount.setOnCheckedChangeListener { _, isChecked ->
                config.showCharacterCounter = isChecked
            }


            backBtn.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            selectedFont1.setOnClickListener {
                setselection(1)
            }
            selectedFont2.setOnClickListener {
                setselection(2)
            }
            selectedFont3.setOnClickListener {
                setselection(3)
            }
            selectedFont4.setOnClickListener {
                setselection(4)
            }

            rightSwipwBtn.setOnClickListener {
                setnotificationCustomDialog("Right")
            }

            leftSwipwBtn.setOnClickListener {
                setnotificationCustomDialog("Left")
            }

            constraintLayout16.setOnClickListener {
                setSeletionline(3)
                islinechange = true
            }

            constraintLayout15.setOnClickListener {
                setSeletionline(2)
                islinechange = true
            }

            messageCorner.value = config.messagecorner
            messageCorner.setLabelFormatter { value ->
                value.toInt().toString()
            }
            messageCorner.addOnChangeListener { slider, value, fromUser ->
                config.messagecorner = value
                updatechatpreview()
            }
        }
    }

    private fun updatechatpreview() {
        binding.messageOut.setCardBackgroundColor(requireActivity().resources.getColor(requireActivity().getchatoutcolor()))
        binding.messageOut.radius = requireActivity().config.messagecorner
        binding.messageIn.setCardBackgroundColor(requireActivity().resources.getColor(requireActivity().getchatincolor()))
        binding.messageIn.radius = requireActivity().config.messagecorner
        binding.changecorner.radius = requireActivity().config.messagecorner
        binding.conversationArchiveBtn.setchatthemecolorBg(0)
        binding.txtout.setTextColor(requireActivity().resources.getColor(requireActivity().getchatIntxtcolor()))
        binding.txtMwssageOutDate.setTextColor(requireActivity().resources.getColor(requireActivity().getchatpreouttxtdatecolor()))
        binding.txtMwssageInDate.setTextColor(requireActivity().resources.getColor(requireActivity().getchatpreIntxtdatecolor()))
    }

    private fun setSeletionline(i: Int) {
        when (i) {
            3 -> {
                binding.constraintLayout16.background = getDrawable(requireContext(), R.drawable.list_view_seleted_item)
                binding.constraintLayout15.background = getDrawable(requireContext(), R.drawable.list_view_unseleted_item)
                binding.threeLineSeletion.visible()
                binding.twoLineSeletion.gone()
                config.line_selection = 3
            }

            2 -> {
                binding.constraintLayout15.background = getDrawable(requireContext(), R.drawable.list_view_seleted_item)
                binding.constraintLayout16.background = getDrawable(requireContext(), R.drawable.list_view_unseleted_item)
                binding.threeLineSeletion.gone()
                binding.twoLineSeletion.visible()
                config.line_selection = 2
            }
        }
    }

    private fun setnotificationCustomDialog(swipeLeft: String) {


        position = if (swipeLeft == "Right") {
            items2.indexOf(config.Swipe_Right)
        } else {
            items2.indexOf(config.Swipe_Left)
        }
        var selectedItem = position
        MaterialAlertDialogBuilder(requireContext()).setTitle(resources.getString(R.string.Change_Action)).setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
            if (swipeLeft == "Right") {
                config.Swipe_Right = items2[selectedItem]
            } else {
                config.Swipe_Left = items2[selectedItem]
            }
            setdefaultselation()
        }.setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
            dialog.dismiss()
        }.setSingleChoiceItems(items, selectedItem) { _, which ->
            selectedItem = which
        }.show()
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private fun setdefaultselation() {
        binding.swipeRight = config.Swipe_Right
        binding.swipeLeft = config.Swipe_Left
        binding.SwipeRightTxt.text = items[items2.indexOf(config.Swipe_Right)]
        binding.SwipeLeftTxt.text = items[items2.indexOf(config.Swipe_Left)]
    }

    private fun setdefaultselection() {
        when (config.fontsizeselection) {
            "Small" -> {
                setselection(1)
            }

            "Normal" -> {
                setselection(2)
            }

            "Large" -> {
                setselection(3)
            }

            "Extra Large" -> {
                setselection(4)
            }
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

    fun setselection(selection: Int) {
        when (selection) {
            1 -> {
                config.fontsizeselection = "Small"
                binding.textsizechage = requireActivity().getTextSizeMS()
                config.fontsize = 15
                binding.selectedFont1.background = getDrawable(requireContext(), R.drawable.fontsizeseletd_bg)
                binding.selectedFont2.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
                binding.selectedFont3.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
                binding.selectedFont4.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
            }

            2 -> {
                config.fontsizeselection = "Normal"
                binding.textsizechage = requireActivity().getTextSizeMS()
                config.fontsize = 20
                binding.selectedFont1.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
                binding.selectedFont2.background = getDrawable(requireContext(), R.drawable.fontsizeseletd_bg)
                binding.selectedFont3.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
                binding.selectedFont4.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
            }

            3 -> {
                config.fontsizeselection = "Large"
                binding.textsizechage = requireActivity().getTextSizeMS()
                config.fontsize = 30
                binding.selectedFont1.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
                binding.selectedFont2.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
                binding.selectedFont3.background = getDrawable(requireContext(), R.drawable.fontsizeseletd_bg)
                binding.selectedFont4.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
            }

            4 -> {
                config.fontsizeselection = "Extra Large"
                binding.textsizechage = requireActivity().getTextSizeMS()
                config.fontsize = 40
                binding.selectedFont1.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
                binding.selectedFont2.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
                binding.selectedFont3.background = getDrawable(requireContext(), R.drawable.fontsizeunseletd_bg)
                binding.selectedFont4.background = getDrawable(requireContext(), R.drawable.fontsizeseletd_bg)
            }
        }

    }
}
