package com.messenger.phone.number.text.sms.service.apps.adapter

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.view.menu.MenuBuilder
import androidx.recyclerview.widget.RecyclerView
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.applyFontToMenuItem
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createOptionBackground
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.ItemManageBlockedKeywordBinding
import com.simplemobiletools.commons.extensions.copyToClipboard
import javax.inject.Inject


class ManageBlockedKeywordsAdapter @Inject constructor() : RecyclerView.Adapter<ManageBlockedKeywordsAdapter.ManageBlockedKeywordsAdapterViewHolder>() {

    protected var selectedKeys = LinkedHashSet<Int>()

    var ActionChange: (() -> Unit)? = null

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    class ManageBlockedKeywordsAdapterViewHolder(var binding: ItemManageBlockedKeywordBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageBlockedKeywordsAdapterViewHolder {
        return ManageBlockedKeywordsAdapterViewHolder(ItemManageBlockedKeywordBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return blockedKeywords.size
    }

    override fun onBindViewHolder(holder: ManageBlockedKeywordsAdapterViewHolder, position: Int) {
        val context = holder.itemView.context
        with(holder) {
            fontSize10 = context.getTextSizeForeNormal10MS()
            fontSize13 = context.getTextSizeForeNormal13MS()
            fontSize18 = context.getTextSizeForeNormal18MS()
            fontSize8 = context.getTextSizeForeNormal8MS()
            fontSize15 = context.getTextSizeHometitleMS()

            binding.textsizechagefor10 = fontSize10
            binding.textsizechagefor13 = fontSize13
            binding.textsizechagefor18 = fontSize18
            binding.textsizechagefor8 = fontSize8
            binding.textsizechagefor15 = fontSize15
        }

        with(holder.binding) {
            val primaryColor = context.getProperPrimaryColor()
            val surfaceColor = context.getProperBackgroundColor()
            val optionSurfaceColor = primaryColor.adjustAlpha(0.1f)
            val cornerSize = context.resources.getDimension(com.intuit.sdp.R.dimen._10sdp)
            val strokeWidth = context.resources.getDimension(com.intuit.sdp.R.dimen._1sdp)


            val background = createOptionBackground(
                cornerSize = cornerSize,
                fillColor = optionSurfaceColor,
                strokeWidth = strokeWidth,
                strokeColor = surfaceColor,
                rippleColor = primaryColor.adjustAlpha(0.3f),
                showRipple = true,
            )

            manageBlockedKeywordHolder.background = background



//            val shapeModel = when {
//                isFirst && isLast -> {
//                    ShapeAppearanceModel.builder()
//                        .setTopLeftCornerSize(cornerSize * 2)
//                        .setTopRightCornerSize(cornerSize * 2)
//                        .setBottomLeftCornerSize(cornerSize * 2)
//                        .setBottomRightCornerSize(cornerSize * 2)
//                        .build()
//                }
//                isFirst -> {
//                    ShapeAppearanceModel.builder()
//                        .setTopLeftCornerSize(cornerSize * 2)
//                        .setTopRightCornerSize(cornerSize * 2)
//                        .setBottomLeftCornerSize(cornerSize)
//                        .setBottomRightCornerSize(cornerSize)
//                        .build()
//                }
//                isLast -> {
//                    ShapeAppearanceModel.builder()
//                        .setTopLeftCornerSize(cornerSize)
//                        .setTopRightCornerSize(cornerSize)
//                        .setBottomLeftCornerSize(cornerSize * 2)
//                        .setBottomRightCornerSize(cornerSize * 2)
//                        .build()
//                }
//                else -> {
//                    ShapeAppearanceModel.builder()
//                        .setAllCornerSizes(cornerSize)
//                        .build()
//                }
//            }


//            manageBlockedKeywordHolder.background = MaterialShapeDrawable(shapeModel).apply {
//                fillColor = ColorStateList.valueOf(optionSurfaceColor)
//                setStroke(strokeWidth, surfaceColor)
//            }


            manageBlockedKeywordTitle.apply {
                text = blockedKeywords[position]
                setTextColor(context.getProperTextColor())
            }

            overflowMenuIcon.imageTintList = ColorStateList.valueOf(context.getProperSecondaryTextColor())

            overflowMenuIcon.setOnClickListener {
                showPopupMenu(overflowMenuAnchor, blockedKeywords[position], overflowMenuIcon.context, position)
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun showPopupMenu(view: View, blockedKeyword: String, context: Context, position: Int) {
        val isDarkPopup = ThemeModeManager.isDarkThemeActive(context)
        val baseThemeRes = if (isDarkPopup) {
            R.style.mainScreenDark
        } else {
            R.style.mainScreen
        }
        val overlayRes = if (isDarkPopup) {
            R.style.App_PopupOverlay_Message_Dark
        } else {
            R.style.App_PopupOverlay_Message_Light
        }
        val baseThemedContext = ContextThemeWrapper(context, baseThemeRes)
        val themedContext = ContextThemeWrapper(baseThemedContext, overlayRes)

        val popup = PopupMenu(themedContext, view, Gravity.END)
        popup.menuInflater.inflate(R.menu.cab_blocked_keywords, popup.menu)
        if (popup.menu is MenuBuilder) {
            val menuBuilder = popup.menu as MenuBuilder
            menuBuilder.setOptionalIconsVisible(true)
            val iconMarginPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8f, context.resources.displayMetrics
            ).toInt()
            for (item in menuBuilder.visibleItems) {
                if (item.icon != null) {
                    item.icon = InsetDrawable(item.icon, iconMarginPx, 0, iconMarginPx, 0)
                }
            }
        }
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.cab_copy_keyword -> {
                    copyKeywordToClipboard(context, blockedKeywords[position])
                }

                R.id.cab_delete -> {
                    deleteSelection(context, position)
                }
            }
            true
        }
        val fontRes = R.font.lato_semibold
        for (i in 0 until popup.menu.size()) {
            val item = popup.menu.getItem(i)
            applyFontToMenuItem(context, item, fontRes)
        }
        popup.show()
    }


    private fun copyKeywordToClipboard(context: Context, text: String) {
//        context.copyToClipboard(text)

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("copied_text", text)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(context, context.getString(R.string.Copied_to_Clipboard), Toast.LENGTH_SHORT).show()

    }


    var blockedKeywords = ArrayList<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private fun deleteSelection(context: Context, position: Int) {
        context.config.removeBlockedKeyword(blockedKeywords[position])
        ActionChange?.invoke()
    }

}
