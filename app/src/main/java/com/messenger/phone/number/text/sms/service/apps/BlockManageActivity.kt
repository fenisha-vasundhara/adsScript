package com.messenger.phone.number.text.sms.service.apps

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.demo.adsmanage.Commen.firebaseEventMain
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getBottomNavigationBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getContrastColor
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
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityBlockManageBinding
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetAllConversationBlockViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BlockManageActivity : AppCompatActivity() {

    lateinit var binding: ActivityBlockManageBinding
    lateinit var model: GetAllConversationBlockViewModel

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_block_manage)
        this.firebaseEventMain("Block_Manage")
        setBaseTheme(binding.vAnd15StatusBar)
        with(binding) {
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

            blockedKeywordsBtnCard.setOnClickListener {
                startActivity(
                    Intent(
                        this@BlockManageActivity,
                        ManageBlockedKeywordsActivity::class.java
                    )
                )
            }

            blockedKeywordsBtnCardAb.setOnClickListener {
                startActivity(
                    Intent(
                        this@BlockManageActivity,
                        ManageBlockedKeywordsActivity::class.java
                    )
                )
            }

            backBtn.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            blockedUserBtnCard.setOnClickListener {
                startActivity(Intent(this@BlockManageActivity, BlockNumberActivity::class.java))
            }

            blockedUserBtnCardAb.setOnClickListener {
                startActivity(Intent(this@BlockManageActivity, BlockNumberActivity::class.java))
            }
        }

        model = ViewModelProvider(this)[GetAllConversationBlockViewModel::class.java]
        model.GetAllConversationlivelist.observe(this, Observer {
            try {
                val blockedCount = it.distinctBy { item -> item.threadId }.size.toString()
                binding.blockUserCount.text = blockedCount
                binding.blockUserCountAb.text = blockedCount
            } catch (_: Exception) {
            }
        })
    }

    override fun onStart() {
        super.onStart()
        themeSetup()
    }

    private fun themeSetup() {
        val surfaceColor = getProperBackgroundColor()
        val statusBarColor = getProperStatusBarColor()
        val navigationBarColor = getBottomNavigationBackgroundColor()
        val textColor = getProperTextColor()
        val secondaryTextColor = getProperSecondaryTextColor()
        val primaryColor = getProperPrimaryColor()
        val optionFillColor = primaryColor.adjustAlpha(0.1f)
//        val badgeFillColor = primaryColor.adjustAlpha(0.18f)
        val badgeFillColor = ColorUtils.blendARGB(primaryColor,surfaceColor,0.6f)
        val dividerColor = secondaryTextColor.adjustAlpha(0.16f)
        val iconSecondaryTint = textColor.adjustAlpha(0.75f)
        val optionCorner = resources.getDimension(com.intuit.sdp.R.dimen._5sdp)
        val outerCorner = resources.getDimension(com.intuit.sdp.R.dimen._10sdp)
        val strokeWidth = resources.getDimension(com.intuit.sdp.R.dimen._1sdp)

        binding.main.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
        binding.textView3.setTextColor(textColor)
        binding.backBtn.iconTint = ColorStateList.valueOf(secondaryTextColor)

        if (config.setABHomeActivityPref == "1") {
            binding.fistContenar.visible()
            binding.fistContenarAb.gone()
        } else {
            binding.fistContenar.gone()
            binding.fistContenarAb.visible()
        }

//        binding.fistContenarAb.background = createOptionBackground(
//            cornerSize = outerCorner,
//            fillColor = primaryColor.adjustAlpha(0.08f),
//            strokeWidth = strokeWidth,
//            strokeColor = surfaceColor
//        )

        listOf(
            binding.blockedUserBtn,
            binding.blockedKeywordsBtn,
            binding.blockedUserBtnAb,
            binding.blockedKeywordsBtnAb
        ).forEach { row ->
            row.background = createOptionBackground(
                cornerSize = optionCorner,
                fillColor = optionFillColor,
                strokeWidth = strokeWidth,
                strokeColor = surfaceColor,
                isTop = row.id == binding.blockedUserBtnAb.id || row.id == binding.blockedUserBtn.id,
                isBottom = row.id == binding.blockedKeywordsBtn.id || row.id == binding.blockedKeywordsBtnAb.id,
                )
        }

        binding.blockUserCountCard.setCardBackgroundColor(badgeFillColor)
        binding.blockUserCountCardAb.setCardBackgroundColor(badgeFillColor)

        listOf(
            binding.blockUserBtn,
            binding.blockKeywordsBtn,
            binding.blockUserBtnAb,
            binding.blockKeywordsBtnAb
        ).forEach { icon ->
            icon.imageTintList = ColorStateList.valueOf(primaryColor)
        }

        listOf(binding.blockUserCountIcon, binding.blockUserCountIconAb).forEach { badgeIcon ->
            badgeIcon.imageTintList = ColorStateList.valueOf(badgeFillColor.getContrastColor())
        }

        listOf(
            binding.textView9,
            binding.textView9keywords,
            binding.textView9Ab,
            binding.textView9keywordsAb,
            binding.blockUserCount,
        ).forEach { text ->
            text.setTextColor(textColor)
        }

        binding.blockUserCountAb.setTextColor(badgeFillColor.getContrastColor())


        binding.blockedUserDivider.setBackgroundColor(dividerColor)
        binding.blockedKeywordsDivider.setBackgroundColor(dividerColor)

        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars =
                ThemeModeManager.shouldUseLightSystemBars(this@BlockManageActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
    }

    private fun createOptionBackground(
        cornerSize: Float,
        fillColor: Int,
        strokeWidth: Float,
        strokeColor: Int,
        isTop: Boolean = false,
        isBottom: Boolean = false,
    ): MaterialShapeDrawable {

        val shapeAppearanceModel = when {
            isTop -> ShapeAppearanceModel.builder()
                .setTopLeftCornerSize(cornerSize * 3)
                .setTopRightCornerSize(cornerSize * 3)
                .setBottomLeftCornerSize(cornerSize)
                .setBottomRightCornerSize(cornerSize)
                .build()

            isBottom -> ShapeAppearanceModel.builder()
                .setTopLeftCornerSize(cornerSize)
                .setTopRightCornerSize(cornerSize)
                .setBottomLeftCornerSize(cornerSize * 3)
                .setBottomRightCornerSize(cornerSize * 3)
                .build()

            else -> ShapeAppearanceModel.builder()
                .setAllCornerSizes(cornerSize)
                .build()
        }

        return MaterialShapeDrawable(shapeAppearanceModel).apply {
            this.fillColor = ColorStateList.valueOf(fillColor)
            setStroke(strokeWidth, strokeColor)
        }
    }
}
