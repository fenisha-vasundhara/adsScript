package com.messenger.phone.number.text.sms.service.apps.fragment

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.demo.adsmanage.Commen.firebaseEventMain
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
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
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentConversationSwipeMotionBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class Conversation_swipe_motion_Fragment : Fragment() {

    lateinit var binding: FragmentConversationSwipeMotionBinding
    var swipeRightString = ""
    var swipeLeftString = ""
    var swipeRightStringPri = ""
    var swipeLeftStringPri = ""

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        requireContext().setLocal()
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_conversation_swipe_motion,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            requireActivity().config.isswipemotionscreenopen = true
            requireActivity().firebaseEventMain("Conversation_Swipe_Motion")
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


            swipeRightStringPri = requireActivity().config.Swipe_Right
            swipeLeftStringPri = requireActivity().config.Swipe_Left
            textView17.text = resources.getString(R.string.Change)
            textView18.text = resources.getString(R.string.Change)
            swipRightExpandView.gone()
            swipLeftExpandView.gone()
            textView3.isSelected = true
            setdefaultselection()


            backBtn.setOnClickListener {
//                requireActivity().onBackPressed()
                parentFragmentManager.popBackStack()

            }

            rightSwipwBtn.setOnClickListener {
                if (swipRightExpandView.isVisible) {
                    swipRightExpandView.gone()
                    textView17.text = resources.getString(R.string.Change)
                    requireActivity().config.Swipe_Right = swipeRightStringPri
                } else {
                    swipRightExpandView.visible()
                    textView17.text = resources.getString(R.string.Done)
                }
            }

            leftSwipwBtn.setOnClickListener {
                if (swipLeftExpandView.isVisible) {
                    swipLeftExpandView.gone()
                    textView18.text = resources.getString(R.string.Change)
                    requireActivity().config.Swipe_Left = swipeLeftStringPri
                } else {
                    swipLeftExpandView.visible()
                    textView18.text = resources.getString(R.string.Done)
                }
            }

            rightBtnArchive.setOnClickListener {
                swipeRightStringPri = "Archive"
                setdefaultselection()
            }
            rightBtnDelete.setOnClickListener {
                swipeRightStringPri = "Delete"
                setdefaultselection()
            }
            rightBtnCall.setOnClickListener {
                swipeRightStringPri = "Call"
                setdefaultselection()
            }
            rightBtnMark.setOnClickListener {
                swipeRightStringPri = "Mark as read"
                setdefaultselection()
            }
            rightBtnPin.setOnClickListener {
                swipeRightStringPri = "Pin"
                setdefaultselection()
            }
            rightBtnPrivate.setOnClickListener {
                swipeRightStringPri = "Private Chat"
                setdefaultselection()
            }
//
            leftBtnArchive.setOnClickListener {
                swipeLeftStringPri = "Archive"
                setdefaultselection()
            }
            leftBtnDelete.setOnClickListener {
                swipeLeftStringPri = "Delete"
                setdefaultselection()
            }
            leftBtnCall.setOnClickListener {
                swipeLeftStringPri = "Call"
                setdefaultselection()
            }
            leftBtnMark.setOnClickListener {
                swipeLeftStringPri = "Mark as read"
                setdefaultselection()
            }
            leftBtnPin.setOnClickListener {
                swipeLeftStringPri = "Pin"
                setdefaultselection()
            }
            leftBtnPrivate.setOnClickListener {
                swipeLeftStringPri = "Private Chat"
                setdefaultselection()
            }

            applyMaterialSwipeMotionColors()
        }
    }

    fun setdefaultselection() {
        when (swipeRightStringPri) {
            "Archive" -> {
                swipeRightString = resources.getString(R.string.Archive)
                binding.view5.visible()
                binding.view6.gone()
                binding.view7.gone()
                binding.view8.gone()
                binding.view9.gone()
                binding.view10.gone()

            }

            "Delete" -> {
                swipeRightString = resources.getString(R.string.Delete)
                binding.view5.gone()
                binding.view6.visible()
                binding.view7.gone()
                binding.view8.gone()
                binding.view9.gone()
                binding.view10.gone()
            }

            "Call" -> {
                swipeRightString = resources.getString(R.string.Call)
                binding.view5.gone()
                binding.view6.gone()
                binding.view7.visible()
                binding.view8.gone()
                binding.view9.gone()
                binding.view10.gone()
            }

            "Mark as read" -> {
                swipeRightString = resources.getString(R.string.Mark_as_read)
                binding.view5.gone()
                binding.view6.gone()
                binding.view7.gone()
                binding.view8.visible()
                binding.view9.gone()
                binding.view10.gone()
            }

            "Pin" -> {
                swipeRightString = resources.getString(R.string.Pin)
                binding.view5.gone()
                binding.view6.gone()
                binding.view7.gone()
                binding.view8.gone()
                binding.view9.visible()
                binding.view10.gone()
            }

            "Private Chat" -> {
                swipeRightString = resources.getString(R.string.Privacy_chat)
                binding.view5.gone()
                binding.view6.gone()
                binding.view7.gone()
                binding.view8.gone()
                binding.view9.gone()
                binding.view10.visible()
            }

            else -> {
                binding.view5.gone()
                binding.view6.gone()
                binding.view7.gone()
                binding.view8.gone()
                binding.view9.gone()
                binding.view10.gone()
            }
        }

        when (swipeLeftStringPri) {
            "Archive" -> {
                swipeLeftString = resources.getString(R.string.Archive)
                binding.leftview5.visible()
                binding.leftview6.gone()
                binding.leftview7.gone()
                binding.leftview8.gone()
                binding.leftview9.gone()
                binding.leftview10.gone()
            }

            "Delete" -> {
                swipeLeftString = resources.getString(R.string.Delete)
                binding.leftview5.gone()
                binding.leftview6.visible()
                binding.leftview7.gone()
                binding.leftview8.gone()
                binding.leftview9.gone()
                binding.leftview10.gone()
            }

            "Call" -> {
                swipeLeftString = resources.getString(R.string.Call)
                binding.leftview5.gone()
                binding.leftview6.gone()
                binding.leftview7.visible()
                binding.leftview8.gone()
                binding.leftview9.gone()
                binding.leftview10.gone()
            }

            "Mark as read" -> {
                swipeLeftString = resources.getString(R.string.Mark_as_read)
                binding.leftview5.gone()
                binding.leftview6.gone()
                binding.leftview7.gone()
                binding.leftview8.visible()
                binding.leftview9.gone()
                binding.leftview10.gone()
            }

            "Pin" -> {
                swipeLeftString = resources.getString(R.string.Pin)
                binding.leftview5.gone()
                binding.leftview6.gone()
                binding.leftview7.gone()
                binding.leftview8.gone()
                binding.leftview9.visible()
                binding.leftview10.gone()
            }

            "Private Chat" -> {
                swipeLeftString = resources.getString(R.string.Privacy_chat)
                binding.leftview5.gone()
                binding.leftview6.gone()
                binding.leftview7.gone()
                binding.leftview8.gone()
                binding.leftview9.gone()
                binding.leftview10.visible()
            }

            else -> {
                binding.leftview5.gone()
                binding.leftview6.gone()
                binding.leftview7.gone()
                binding.leftview8.gone()
                binding.leftview9.gone()
                binding.leftview10.gone()
            }
        }
        setdefaultselation()
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private fun setdefaultselation() {
        binding.swipeRight = swipeRightStringPri
        binding.swipeLeft = swipeLeftStringPri
        binding.SwipeRightTxt.text = swipeRightString
        binding.SwipeLeftTxt.text = swipeLeftString
    }

    override fun onStart() {
        super.onStart()
        applyMaterialSwipeMotionColors()
        applyMaterialSystemBarColors()
    }

    private fun applyMaterialSwipeMotionColors() {
        val context = requireContext()
        val backgroundColor = context.getProperBackgroundColor()
        val primaryColor = context.getProperPrimaryColor()
        val textColor = context.getProperTextColor()

        val appBarContainerColor = blendWithBaseColor(primaryColor, backgroundColor, 0.08f)
        val cardContainerColor = blendWithBaseColor(primaryColor, backgroundColor, 0.06f)
        val previewContainerColor = blendWithBaseColor(primaryColor, backgroundColor, 0.10f)
        val cardStrokeColor = blendWithBaseColor(primaryColor, backgroundColor, 0.35f)
        val selectedPillColor = blendWithBaseColor(primaryColor, backgroundColor, 0.28f)
        val textSecondaryColor = ColorUtils.setAlphaComponent(textColor, (255 * 0.68f).toInt())




        binding.root.setBackgroundColor(backgroundColor)
        binding.defaulttoolshow.setBackgroundColor(backgroundColor)
        binding.ct1.setBackgroundColor(backgroundColor)
        binding.ct2.setBackgroundColor(backgroundColor)
        binding.conversationSwipeMotionBtnCard.setBackgroundColor(backgroundColor)
        binding.conversationSwipeMotionArchiveBtn.setBackgroundColor(backgroundColor)

        binding.backBtn.iconTint = android.content.res.ColorStateList.valueOf(textColor)
        binding.textView3.setTextColor(textColor)


        binding.SwipeRight.setTextColor(textColor)
        binding.SwipeLeft.setTextColor(textColor)
        binding.SwipeRightTxt.setTextColor(textSecondaryColor)
        binding.SwipeLeftTxt.setTextColor(textSecondaryColor)
        binding.textView17.setTextColor(primaryColor)
        binding.textView18.setTextColor(primaryColor)

        binding.SwipeRightCard.setCardBackgroundColor(previewContainerColor)
        binding.SwipeLeftCard.setCardBackgroundColor(previewContainerColor)
        binding.constraintLayout12.background = createRoundedBackground(
            fillColor = previewContainerColor,
            strokeColor = cardStrokeColor,
            cornerRadiusResId = com.intuit.sdp.R.dimen._15sdp
        )
        binding.constraintLayout14.background = createRoundedBackground(
            fillColor = previewContainerColor,
            strokeColor = cardStrokeColor,
            cornerRadiusResId = com.intuit.sdp.R.dimen._15sdp
        )
        binding.rightSwipwBtn.background = createRoundedBackground(
            fillColor = previewContainerColor,
            strokeColor = cardStrokeColor,
            cornerRadiusResId = com.intuit.sdp.R.dimen._5sdp
        )

        binding.leftSwipwBtn.background = createRoundedBackground(
            fillColor = previewContainerColor,
            strokeColor = cardStrokeColor,
            cornerRadiusResId = com.intuit.sdp.R.dimen._5sdp
        )



        binding.constraintLayout11.setBackgroundColor(blendWithBaseColor(primaryColor, backgroundColor, 0.48f))
        binding.constraintLayout13.setBackgroundColor(blendWithBaseColor(primaryColor, backgroundColor, 0.48f))

        binding.profileThreeLine9.setTextColor(textColor)
        binding.profileThreeLine10.setTextColor(textSecondaryColor)
        binding.profileThreeLine12.setTextColor(textColor)
        binding.profileThreeLine11.setTextColor(textSecondaryColor)

        listOf(
            binding.txt1, binding.txt2, binding.txt3, binding.txt4, binding.txt5, binding.txt6,
            binding.lefttxt1, binding.lefttxt2, binding.lefttxt3, binding.lefttxt4, binding.lefttxt5, binding.lefttxt6
        ).forEach { it.setTextColor(textSecondaryColor) }

//        listOf(
//            binding.c1, binding.c2, binding.c3, binding.c4, binding.c5, binding.c6,
//            binding.leftc1, binding.leftc2, binding.leftc3, binding.leftc4, binding.leftc5, binding.leftc6
//        ).forEach { it.setCardBackgroundColor(blendWithBaseColor(primaryColor, backgroundColor, 0.22f)) }

//        listOf(
//            binding.view5, binding.view6, binding.view7, binding.view8, binding.view9, binding.view10,
//            binding.leftview5, binding.leftview6, binding.leftview7, binding.leftview8, binding.leftview9, binding.leftview10
//        ).forEach { it.setBackgroundColor(selectedPillColor) }

//        binding.rightSwipwBtn.setColorFilter(primaryColor)
//        binding.leftSwipwBtn.setColorFilter(primaryColor)
    }

    private fun applyMaterialSystemBarColors() {
        val surfaceColor = requireContext().getProperBackgroundColor()
        val statusBarColor = requireContext().getProperStatusBarColor()
        val navigationBarColor = requireContext().getBottomNavigationBackgroundColor()
        val window = requireActivity().window
        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars = ThemeModeManager.shouldUseLightSystemBars(requireContext())
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
}
