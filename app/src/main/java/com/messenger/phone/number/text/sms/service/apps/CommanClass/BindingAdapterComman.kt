package com.messenger.phone.number.text.sms.service.apps.CommanClass

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageButton
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.messenger.phone.number.text.sms.service.apps.R
import com.simplemobiletools.commons.extensions.baseConfig
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale
import kotlin.random.Random

private val APP_BAR_IMAGE_ACTION_IDS = setOf(
    R.id.back_btn,
    R.id.more_btn,
    R.id.more_btn_2,
    R.id.search_message_bg,
    R.id.message_premium,
    R.id.more_button,
    R.id.filter_message_bg,
    R.id.ic_drawer_ab_button,
    R.id.message_archive,
    R.id.message_delete,
    R.id.message_pin,
    R.id.message_unpin,
    R.id.selectallmessage,
    R.id.more_button_mark,
    R.id.message_selection_close,
    R.id.select_all_message,
    R.id.message_select_all,
    R.id.message_edit
)

private val APP_BAR_BUTTON_ACTION_IDS = setOf(
    R.id.back_btn,
    R.id.more_btn,
    R.id.more_btn_2,
    R.id.selected_more_btn,
    R.id.selected_more_btn_2,
    R.id.message_selection_close
)

private fun Context.isSystemThemeMode(): Boolean {
    return ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM
}

private fun Context.isDarkThemeMode(): Boolean {
    return ThemeModeManager.isDarkThemeActive(this)
}

private fun Context.resolveSolidColor(lightRes: Int, darkRes: Int): Int {
    return ContextCompat.getColor(this, if (isDarkThemeMode()) darkRes else lightRes)
}

private fun View.resolveThemeColor(systemAttr: Int, lightRes: Int, darkRes: Int): Int {
    return if (context.isSystemThemeMode()) {
        MaterialColors.getColor(this, systemAttr)
    } else {
        context.resolveSolidColor(lightRes, darkRes)
    }
}

private fun ImageView.applyAppBarActionSizingIfNeeded() {
    if (id == View.NO_ID || id !in APP_BAR_IMAGE_ACTION_IDS) return

    val actionSize = resources.getDimensionPixelSize(R.dimen.mf_appbar_action_size)
    val actionPadding = resources.getDimensionPixelSize(R.dimen.mf_appbar_action_padding)

    updateLayoutParams {
        width = actionSize
        height = actionSize
    }
    if (paddingLeft != actionPadding || paddingTop != actionPadding ||
        paddingRight != actionPadding || paddingBottom != actionPadding
    ) {
        setPadding(actionPadding)
    }
    scaleType = ImageView.ScaleType.CENTER_INSIDE
}

private fun MaterialButton.applyAppBarActionSizingIfNeeded() {
    if (id == View.NO_ID || id !in APP_BAR_BUTTON_ACTION_IDS) return

    val actionSize = resources.getDimensionPixelSize(R.dimen.mf_appbar_action_size)
    val actionPadding = resources.getDimensionPixelSize(R.dimen.mf_appbar_action_padding)
    val iconTarget = actionSize - (actionPadding * 2)

    updateLayoutParams {
        width = actionSize
        height = actionSize
    }

    minimumWidth = 0
    minimumHeight = 0
    minWidth = 0
    minHeight = 0
    insetTop = 0
    insetBottom = 0
    iconPadding = 0
    if (iconTarget > 0) {
        iconSize = iconTarget
    }
    if (paddingLeft != actionPadding || paddingTop != actionPadding ||
        paddingRight != actionPadding || paddingBottom != actionPadding
    ) {
        setPadding(actionPadding)
    }
}

private fun View.applyAppBarActionSizingToKnownChildren() {
    val allIds = APP_BAR_IMAGE_ACTION_IDS + APP_BAR_BUTTON_ACTION_IDS
    allIds.forEach { targetId ->
        val target = findViewById<View>(targetId) ?: return@forEach
        when (target) {
            is ImageView -> target.applyAppBarActionSizingIfNeeded()
            is MaterialButton -> target.applyAppBarActionSizingIfNeeded()
        }
    }
}


@BindingAdapter("Homescreenmessagelable")
fun ImageView.Homescreenmessagelable(con: Int) {
    applyAppBarActionSizingIfNeeded()
    if (context.config.Message_Send_Activity == "2") {
        val tint = if (context.isSystemThemeMode()) {
            MaterialColors.getColor(this, R.attr.colorPrimary)
        } else {
            ContextCompat.getColor(context, R.color.appcolor)
        }
        setColorFilter(tint)
    } else {
        setColorFilter(resolveThemeColor(com.google.android.material.R.attr.colorOnSurface, R.color.black2, R.color.white))
    }
}

@BindingAdapter("HomescreenmessagelableNew")
fun ImageView.HomescreenmessagelableNew(con: Int) {
    applyAppBarActionSizingIfNeeded()
    if (context.config.Message_Send_Activity == "2") {
        setColorFilter(resolveThemeColor(com.google.android.material.R.attr.colorOnSurface, R.color.black2, R.color.white))
    } else {
        setColorFilter(resolveThemeColor(com.google.android.material.R.attr.colorOnSurface, R.color.black2, R.color.white))
    }
}


@BindingAdapter("setchatthemecolor")
fun View.setchatthemecolor(con: Int) {
    val color = if (context.isSystemThemeMode()) {
        MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurface)
    } else if (!context.isDarkThemeMode() && context.config.setABHomeActivityPref == "1") {
        ContextCompat.getColor(context, R.color.appcolor)
    } else {
        context.resolveSolidColor(R.color.status_bar_ab_color, R.color.toolbarcolor3new)
    }
    background = ColorDrawable(color)
    applyAppBarActionSizingToKnownChildren()
}

@BindingAdapter("setchatthemecolorformainscreen")
fun View.setchatthemecolorformainscreen(con: Int) {
    val color = if (context.isSystemThemeMode()) {
        MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurface)
    } else if (!context.isDarkThemeMode() && context.config.Message_Send_Activity != "2") {
        ContextCompat.getColor(context, R.color.appcolor)
    } else {
        context.resolveSolidColor(R.color.status_bar_ab_color, R.color.toolbarcolor3new)
    }
    background = ColorDrawable(color)
    applyAppBarActionSizingToKnownChildren()
}

@BindingAdapter("setHomeViewBgColor")
fun View.setHomeViewBgColor(con: Int) {
    if (context.isSystemThemeMode()) {
        setBackgroundColor(
            MaterialColors.getColor(this, com.google.android.material.R.attr.colorOutlineVariant)
        )
    } else if (!context.isDarkThemeMode()) {
        setBackgroundColor(context.getColor(R.color.black_line))
    } else {
        setBackgroundColor(context.getColor(R.color.dark_theme_line))
    }
}

@BindingAdapter("sendmessagedivider")
fun View.sendmessagedivider(con: Int) {
    if (!context.isDarkThemeMode()) {
        background = ColorDrawable(resources.getColor(R.color.black2))
    } else {
        background = ColorDrawable(resources.getColor(R.color.white))
    }
}

@BindingAdapter("setchatthemecolortab")
fun View.setchatthemecolortab(con: Int) {
    background = ColorDrawable(
        resolveThemeColor(
            com.google.android.material.R.attr.colorSurface,
            R.color.chatbgcolor1,
            R.color.chatbgcolor3
        )
    )
}

@BindingAdapter("setchatthemecolorFloting")
fun FloatingActionButton.setchatthemecolorFloting(con: Int) {
    val color = if (context.isSystemThemeMode()) {
        MaterialColors.getColor(this, R.attr.colorPrimary)
    } else {
        ContextCompat.getColor(context, if (context.isDarkThemeMode()) R.color.toolbarcolor3new else R.color.appcolor)
    }
    backgroundTintList = ColorStateList.valueOf(color)
}

@BindingAdapter("setchatthemecolorforbutton")
fun View.setchatthemecolorforbutton(con: Int) {
    background = ColorDrawable(
        resolveThemeColor(
            com.google.android.material.R.attr.colorSurface,
            R.color.settingbuttonfullapp,
            R.color.toolbarcolor3new
        )
    )
}

@BindingAdapter("settingABicontintset")
fun ImageView.settingABicontintset(con: Int) {
    val color = if (context.isSystemThemeMode()) {
        MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurfaceVariant)
    } else if (context.isDarkThemeMode()) {
        context.resources.getColor(R.color.setting_icon_stock_color_3)
    } else {
        context.resources.getColor(R.color.setting_icon_stock_color_1)
    }
    this.setColorFilter(
        color, PorterDuff.Mode.SRC_IN
    )
}

@BindingAdapter("settingABicontintset")
fun MaterialButton.settingABicontintset(con: Int) {
    val color = if (context.isSystemThemeMode()) {
        MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurfaceVariant)
    } else if (context.isDarkThemeMode()) {
        context.resources.getColor(R.color.setting_icon_stock_color_3)
    } else {
        context.resources.getColor(R.color.setting_icon_stock_color_1)
    }
    iconTint = ColorStateList.valueOf(color)
}

@BindingAdapter("settingABtextcolorset")
fun TextView.settingABtextcolorset(con: Int) {
    val color = if (context.isSystemThemeMode()) {
        MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurface)
    } else if (context.isDarkThemeMode()) {
        context.resources.getColor(R.color.setting_icon_stock_color_3)
    } else {
        context.resources.getColor(R.color.setting_text_color_1)
    }
    this.setTextColor(
        color
    )
}


@BindingAdapter("SetSwitchColor")
fun MaterialSwitch.SetSwitchColor(con: Int) {
    val trackColorStateList = ColorStateList(
        arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf()
        ),
        intArrayOf(
            this.context.config.themeselectedcolor,
            Color.parseColor("#E0E0E0")
        )
    )
    this.trackTintList = trackColorStateList
}


@BindingAdapter("setchatthemecolorBg")
fun View.setchatthemecolorBg(con: Int) {
    background = ColorDrawable(
        resolveThemeColor(
            com.google.android.material.R.attr.colorSurface,
            R.color.chatbgcolor1,
            R.color.chatbgcolor3
        )
    )
}

@BindingAdapter("setchatthemecolorBgIntro2")
fun ConstraintLayout.setchatthemecolorBgIntro2(con: Int) {
    if (context.isDarkThemeMode()) {
        background = ColorDrawable(resources.getColor(R.color.toolbarcolor3new))
    } else {
        background = ColorDrawable(resources.getColor(R.color.pre_new))
    }
}

@BindingAdapter("setTextColorThemeWise")
fun TextView.setTextColorThemeWise(con: Int) {
    if (context.isDarkThemeMode()) {
        setTextColor(resources.getColor(R.color.white))
    } else {
        setTextColor(resources.getColor(R.color.black2))
    }
}

@BindingAdapter("setCardBackgroundToolbarColor")
fun CardView.setCardBackgroundToolbarColor(con: Int) {
    if (context.isDarkThemeMode()) {
        setCardBackgroundColor(resources.getColor(R.color.black_new_theme))
    } else {
        setCardBackgroundColor(resources.getColor(R.color.toolbar_color))
    }
}

@BindingAdapter("setchatthemecolorforHomeScreenBg")
fun View.setchatthemecolorforHomeScreenBg(con: Int) {
    background = ColorDrawable(
        resolveThemeColor(
            android.R.attr.colorBackground,
            R.color.chatbgcolor1,
            R.color.chatbgcolor3
        )
    )
}

@BindingAdapter("aftercallScreen")
fun View.aftercallScreen(con: Int) {
    background = ColorDrawable(
        resolveThemeColor(
            com.google.android.material.R.attr.colorSurface,
            R.color.after_call_title,
            R.color.toolbarcolor3new
        )
    )
}





@BindingAdapter("setchatthemecolorTintImfullapp")
fun ImageView.setchatthemecolorTintImfullapp(con: Int) {
    applyAppBarActionSizingIfNeeded()
    if (context.isSystemThemeMode()) {
        setColorFilter(MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurface))
    } else {
        if (!context.isDarkThemeMode() && this.context.config.setABHomeActivityPref != "1") {
            setColorFilter(resources.getColor(R.color.black2))
        } else {
            setColorFilter(resources.getColor(R.color.white))
        }
    }
}

@BindingAdapter("setchatthemecolorTintImfullapp")
fun MaterialButton.setchatthemecolorTintImfullapp(con: Int) {
    applyAppBarActionSizingIfNeeded()
    iconTint = if (context.isSystemThemeMode()) {
        ColorStateList.valueOf(MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurface))
    } else if (!context.isDarkThemeMode() && this.context.config.setABHomeActivityPref != "1") {
        ColorStateList.valueOf(resources.getColor(R.color.black2))
    } else {
        ColorStateList.valueOf(resources.getColor(R.color.white))
    }
}


@BindingAdapter("setchatthemecolorTintMTfullapp")
fun MaterialButton.setchatthemecolorTintMTfullapp(con: Int) {
    applyAppBarActionSizingIfNeeded()
    if (context.isSystemThemeMode()) {
        iconTint = ColorStateList.valueOf(MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurface))
    } else {
        if (!context.isDarkThemeMode() && this.context.config.setABHomeActivityPref != "1") {
            setIconTintResource(R.color.black2)
        } else {
            setIconTintResource(R.color.white)
        }
    }
}


@BindingAdapter("setchatthemecolorTintImfullappforrecyler")
fun ImageView.setchatthemecolorTintImfullappforrecyler(con: Int) {
    if (context.isSystemThemeMode()) {
        setColorFilter(MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurface))
    } else if (context.isDarkThemeMode()) {
        setColorFilter(resources.getColor(R.color.white))
    } else {
        setColorFilter(resources.getColor(R.color.black2))
    }
}

@BindingAdapter("setchatthemecolorTintImfullappforrecyler")
fun MaterialButton.setchatthemecolorTintImfullappforrecyler(con: Int) {
    iconTint = if (context.isSystemThemeMode()) {
        ColorStateList.valueOf(MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurface))
    } else if (context.isDarkThemeMode()) {
        ColorStateList.valueOf(resources.getColor(R.color.white))
    } else {
        ColorStateList.valueOf(resources.getColor(R.color.black2))
    }
}

@BindingAdapter("setlayoutbackgroundcolordrawer")
fun ConstraintLayout.setlayoutbackgroundcolordrawer(con: Int) {
    if (context.isSystemThemeMode()) {
        setBackgroundColor(MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurface))
    } else if (context.isDarkThemeMode()) {
        setBackgroundColor(resources.getColor(R.color.drawer_bg_color_3))
    } else {
        setBackgroundColor(resources.getColor(R.color.white))
    }
}


@BindingAdapter("setKeybordimage")
fun ImageView.setKeybordimage(con: Int) {
    if ((tag as? Int) == con) return
    tag = con
    try {
        setImageResource(con)
    } catch (_: OutOfMemoryError) {
        // Degrade gracefully on low-memory devices instead of crashing during vector inflation.
        setImageDrawable(null)
    } catch (_: Resources.NotFoundException) {
        setImageDrawable(null)
    }
}

@BindingAdapter("setchatthemecolorTintTxt")
fun TextView.setchatthemecolorTintTxt(con: Int) {
    if (context.isSystemThemeMode()) {
        setTextColor(MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurface))
    } else {
        if (!context.isDarkThemeMode() && this.context.config.setABHomeActivityPref != "1") {
            setTextColor(resources.getColor(R.color.black2))
        } else {
            setTextColor(resources.getColor(R.color.white))
        }
    }
}

@BindingAdapter("setchatthemecolorTintTxtSedule")
fun TextView.setchatthemecolorTintTxtSedule(con: Int) {
    if (context.isSystemThemeMode()) {
        setTextColor(MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurface))
    } else if (context.isDarkThemeMode()) {
        setTextColor(resources.getColor(R.color.white))
    } else {
        setTextColor(resources.getColor(R.color.sedulemessagetext))
    }
}

@BindingAdapter("setchatthemecolorTintTxtautodelete")
fun TextView.setchatthemecolorTintTxtautodelete(con: Int) {
    if (context.isSystemThemeMode()) {
        setTextColor(MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurface))
    } else if (context.isDarkThemeMode()) {
        setTextColor(resources.getColor(R.color.white))
    } else {
        setTextColor(resources.getColor(R.color.oldmessagedelete))
    }
}

@BindingAdapter("setchatthemecolorTintTxtautodeletesstockcolorchange")
fun RadioButton.setchatthemecolorTintTxtautodeletesstockcolorchange(con: Int) {
    if (context.isSystemThemeMode()) {
        buttonTintList = ColorStateList.valueOf(
            MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurface)
        )
    } else if (context.isDarkThemeMode()) {
        buttonTintList = ColorStateList.valueOf(resources.getColor(R.color.white))
    } else {
        buttonTintList = ColorStateList.valueOf(resources.getColor(R.color.oldmessagedelete))
    }
}

@BindingAdapter("setchatthemecolorTintTxtcontactserch")
fun TextView.setchatthemecolorTintTxtcontactserch(con: Int) {
    if (context.isDarkThemeMode()) {
        setTextColor(resources.getColor(R.color.white))
    } else {
        setTextColor(resources.getColor(R.color.colorselectcontact))
    }
}

@BindingAdapter("setchatthemecolorTintTxtcontactGropserch")
fun TextView.setchatthemecolorTintTxtcontactGropserch(con: Int) {
    if (context.isDarkThemeMode()) {
        setTextColor(resources.getColor(R.color.white))
    } else {
        setTextColor(resources.getColor(R.color.groupnameshow))
    }
}

@BindingAdapter("setchatthemecolorTintTxtFullapp")
fun TextView.setchatthemecolorTintTxtFullapp(con: Int) {

//    setTextColor(resources.getColor(R.color.duskYellow))

    if (context.isSystemThemeMode()) {
        setTextColor(MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurface))
    } else if (context.isDarkThemeMode()) {
        setTextColor(resources.getColor(R.color.white))
    } else {
        setTextColor(resources.getColor(R.color.black2))
    }
}

@BindingAdapter("setchatthemecolorTintTxtmangecate")
fun TextView.setchatthemecolorTintTxtmangecate(con: Int) {
    if (context.isDarkThemeMode()) {
        setTextColor(resources.getColor(R.color.white))
    } else {
        setTextColor(resources.getColor(R.color.managecatallonesolidtxtcolor))
    }
}

@BindingAdapter("conversationswipemoitontxt")
fun TextView.conversationswipemoitontxt(con: Int) {
    if (context.isDarkThemeMode()) {
        setTextColor(resources.getColor(R.color.white))
    } else {
        setTextColor(resources.getColor(R.color.swipemotioncolor))
    }
}

@BindingAdapter("setchatthemecolorTintTxtFullappForLocksetup")
fun TextView.setchatthemecolorTintTxtFullappForLocksetup(con: Int) {
    if (context.isDarkThemeMode()) {
        setTextColor(resources.getColor(R.color.white))
    } else {
        setTextColor(resources.getColor(R.color.lockscreen))
    }
}

@BindingAdapter("setchatthemecolorTintTxtFullappForLocksetupforhint")
fun TextView.setchatthemecolorTintTxtFullappForLocksetupforhint(con: Int) {
    if (context.isDarkThemeMode()) {
        setTextColor(resources.getColor(R.color.lockscreenhintsecound))
    } else {
        setTextColor(resources.getColor(R.color.lockscreenhint))
    }
}

@BindingAdapter("setchatthemecolorTintTxtFullappForLockScreenforhint")
fun TextView.setchatthemecolorTintTxtFullappForLockScreenforhint(con: Int) {
    if (context.isDarkThemeMode()) {
        setTextColor(resources.getColor(R.color.lockscreenhintsecound))
    } else {
        setTextColor(resources.getColor(R.color.lockscreenpin))
    }
}

@BindingAdapter("setCardBackgroundColor")
fun CardView.setCardBackgroundColor(con: Int) {
    if (context.isDarkThemeMode()) {
        setCardBackgroundColor(resources.getColor(R.color.toolbarcolor3new))
    } else {
        setCardBackgroundColor(resources.getColor(R.color.lock_edit_screen))
    }
}

@BindingAdapter("setChatDateBackgroundColor")
fun CardView.setChatDateBackgroundColor(lightColor: Int) {
    val isLightTheme = if (context.isSystemThemeMode()) {
        MaterialColors.isColorLight(
            MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurface)
        )
    } else {
        !context.isDarkThemeMode()
    }
    val color = if (isLightTheme) lightColor else Color.TRANSPARENT
    setCardBackgroundColor(color)
}

@BindingAdapter("setCardBackgroundColorTwo")
fun CardView.setCardBackgroundColorTwo(con: Int) {
    if (context.isDarkThemeMode()) {
        setCardBackgroundColor(resources.getColor(R.color.toolbarcolor3new3))
    } else {
        setCardBackgroundColor(resources.getColor(R.color.automessagedelete_cardcolor))
    }
}

@BindingAdapter("setCardBackgroundColorEdittextTwo")
fun EditText.setCardBackgroundColorEdittextTwo(con: Int) {
    if (context.isDarkThemeMode()) {
        setBackgroundColor(resources.getColor(R.color.toolbarcolor3new3))
        setTextColor(resources.getColor(R.color.white))
        setHintTextColor(resources.getColor(R.color.white))
    } else {
        setBackgroundColor(resources.getColor(R.color.automessagedelete_cardcolor))
        setTextColor(resources.getColor(R.color.signachurdialog))
        setHintTextColor(resources.getColor(R.color.signachurdialog))
    }
}

@BindingAdapter("setLockScreenPinBackground")
fun TextView.setLockScreenPinBackground(con: Int) {
    if (context.isDarkThemeMode()) {
        backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.toolbarcolor3new))
        setTextColor(resources.getColor(R.color.white))
    } else {
        backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.lock_edit_screen))
        setTextColor(resources.getColor(R.color.black2))
    }
}

@BindingAdapter("setLinearLayoutBackgroundColor")
fun LinearLayout.setLinearLayoutBackgroundColor(con: Int) {
    if (context.isDarkThemeMode()) {
        setBackgroundColor(resources.getColor(R.color.toolbarcolor3new))
    } else {
        setBackgroundColor(resources.getColor(R.color.lock_edit_screen))
    }
}

@BindingAdapter("setSpinnerBackgroundColor")
fun Spinner.setSpinnerBackgroundColor(con: Int) {
    if (context.isDarkThemeMode()) {
        setBackgroundColor(resources.getColor(R.color.toolbarcolor3new))
    } else {
        setBackgroundColor(resources.getColor(R.color.lock_edit_screen))
    }
}

@BindingAdapter("setCardBackgroundColorEdittext")
fun EditText.setCardBackgroundColorEdittext(con: Int) {
    if (context.isDarkThemeMode()) {
        setBackgroundColor(resources.getColor(R.color.toolbarcolor3new))
    } else {
        setBackgroundColor(resources.getColor(R.color.lock_edit_screen))
    }
}

@BindingAdapter("setCardBackgroundColorEdittextForSetup")
fun EditText.setCardBackgroundColorEdittextForSetup(con: Int) {
    if (context.isDarkThemeMode()) {
        setTextColor(resources.getColor(R.color.lockscreenhintsecound))
        setHintTextColor(resources.getColor(R.color.lockscreenhintsecound))
    } else {
        setTextColor(resources.getColor(R.color.lockscreenhint))
        setHintTextColor(resources.getColor(R.color.lockscreenhint))
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("setMessageTimeandDate")
fun TextView.setMessageTimeandDate(time: Long) {
    val defaultLocale = Locale.getDefault()
    val locale = try {
        Locale(
            if (this.context.config.SelectedLanguage == "ar") {
                "en"
            } else {
                this.context.config.SelectedLanguage
            }
        )
    } catch (e: Exception) {
        defaultLocale
    }

    val dt: Date = Date(time)
    val sdf = SimpleDateFormat("MMM dd,hh:mm aa", locale)
    val time1 = sdf.format(dt)
    this.text = "" + time1
}


@BindingAdapter("setchatthemecolorDateLineSrcImLeft")
fun ImageView.setchatthemecolorDateLineSrcImLeft(con: Int) {
//    when (context.config.activeThemeSelection) {
//        1 -> {
//            this.setImageResource(R.drawable.dateline1)
//        }
//
//        2 -> {
//            this.setImageResource(R.drawable.dateline2left)
//        }
//
//        3 -> {
//            this.setImageResource(R.drawable.dateline2left)
//        }
//
//        4 -> {
//            this.setImageResource(R.drawable.dateline4left)
//        }
//    }
    this.setImageResource(R.drawable.dateline1)
}

@BindingAdapter("setchatthemecolorselect")
fun ImageView.setchatthemecolorselect(con: Int) {
    if (context.isDarkThemeMode()) {
        setImageResource(R.drawable.contact_divider_ic_three)
    } else {
        setImageResource(R.drawable.contact_divider_ic)
    }
}

@BindingAdapter("setchatthemecolorDateSrcImRight")
fun ImageView.setchatthemecolorDateSrcImRight(con: Int) {
//    when (context.config.activeThemeSelection) {
//        1 -> {
//            this.setImageResource(R.drawable.dateline2)
//        }
//
//        2 -> {
//            this.setImageResource(R.drawable.dateline2right)
//        }
//
//        3 -> {
//            this.setImageResource(R.drawable.dateline2right)
//        }
//
//        4 -> {
//            this.setImageResource(R.drawable.dateline4right)
//        }
//    }
    this.setImageResource(R.drawable.dateline2)
}

@BindingAdapter("setchatsendmessage")
fun ImageView.setchatsendmessage(con: Int) {
    if (context.isDarkThemeMode()) {
        setImageResource(R.drawable.send_new_message_final_2)
    } else {
        setImageResource(R.drawable.send_new_message_final)
    }
}


@BindingAdapter("ColorChangeImgeTint")
fun ImageView.ColorChangeImgeTint(pos: Int) {
//    val r = Random()
//    val red: Int = r.nextInt(255 - 0 + 1) + 0
//    val green: Int = r.nextInt(255 - 0 + 1) + 0
//    val blue: Int = r.nextInt(255 - 0 + 1) + 0

    val random = Random.Default

    // Define the range for each color component (red, green, blue)
    val minComponentValue = 200 // Adjust as needed to control the color brightness
    val maxComponentValue = 255

    val red = random.nextInt(minComponentValue, maxComponentValue + 1)
    val green = random.nextInt(minComponentValue, maxComponentValue + 1)
    val blue = random.nextInt(minComponentValue, maxComponentValue + 1)

    // Create a 32-bit color value (ARGB) by shifting and combining the components
    val color = (0xFF shl 24) or (red shl 16) or (green shl 8) or blue

    this.setColorFilter(color)
}

@RequiresApi(Build.VERSION_CODES.O)
@BindingAdapter("setTextSize")
fun TextView.setTextSize(size: Float) {
    this.setTextSize(TypedValue.COMPLEX_UNIT_PX, size * 1.2f)
}

@RequiresApi(Build.VERSION_CODES.O)
@BindingAdapter("setTextSizeFullApp")
fun TextView.setTextSizeFullApp(size: Float) {
    this.setTextSize(TypedValue.COMPLEX_UNIT_PX, size * 1f)
}

@RequiresApi(Build.VERSION_CODES.O)
@BindingAdapter("setperColor")
fun TextView.setperColor(size: Float) {
    if (context.isDarkThemeMode()) {
        setTextColor(resources.getColor(R.color.white))
    } else {
        setTextColor(resources.getColor(R.color.black2))
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SetTextI18n", "SimpleDateFormat")
@BindingAdapter("setNewDate")
fun TextView.setNewDate(time: Long) {
    try {
        if (time == 0L) {
            return
        }
        val date2 = Date(time)
        val format = SimpleDateFormat("yyyy-MM-dd")
        val dateString = format.format(date2)
        val date = LocalDate.parse(dateString)
        val formatter =
            DateTimeFormatter.ofPattern("dd MMM", Locale(context.config.SelectedLanguage))
        this.text = "" + date.format(formatter)
    } catch (_: Exception) {
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SetTextI18n", "SimpleDateFormat")
@BindingAdapter("setNewDateYear")
fun TextView.setNewDateYear(time: Long) {
//    this.text="jigar"

    val date2 = Date(time)
    Log.d("TextView", "setNewDateYear: TextView <----------> ${date2}")
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    val dateString = format.format(date2)
    val date =
        LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH))
    val formatter = DateTimeFormatter.ofPattern(
        "EEEE, dd MMMM", Locale(
            if (this.context.config.SelectedLanguage == "ar") {
                "en"
            } else {
                this.context.config.SelectedLanguage
            }
        )
    )
    this.text = "" + date.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SetTextI18n", "SimpleDateFormat")
@BindingAdapter("setNewDateYearIMAGE")
fun TextView.setNewDateYearIMAGE(time2: Long) {
    var time = 0L
    if (time2.toString().length <= 10) {
        time = time2 * 1000
    } else {
        time = time2
    }
    val date2 = Date(time)
    var date: LocalDate? = null
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    val dateString = format.format(date2)
    try {
        date =
            LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH))
        // Proceed with formatting...
    } catch (e: DateTimeParseException) {
        Log.d("DateParsingError", "Invalid date format <----> ${time}: $dateString")
//        Log.d("DateParsingError", "Invalid date format <----> 2 ${time2}: $dateString")
        // Handle the error (e.g., set a default text or show a message)
    }
    val formatter = DateTimeFormatter.ofPattern(
        "EEEE, dd MMMM", Locale(
            if (this.context.config.SelectedLanguage == "ar") {
                "en"
            } else {
                this.context.config.SelectedLanguage
            }
        )
    )
    this.text = "" + date?.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SetTextI18n", "SimpleDateFormat")
@BindingAdapter("setNewTime")
fun TextView.setNewTime(time: Long) {
    val dt: Date = Date(time)
    val sdf = SimpleDateFormat("hh:mm aa")
    val time1 = sdf.format(dt)
    this.text = "" + time1
}
fun formatToKolkataTime(timestampSeconds: Long): String {
    val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
    return Instant.ofEpochSecond(timestampSeconds)
        .atZone(ZoneId.of("Asia/Kolkata"))
        .format(formatter)
}
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SetTextI18n", "SimpleDateFormat")
@BindingAdapter("isTodayMassage", "isTodayMassagechack")
fun TextView.isTodayMassage(timebase: Long, chack: Boolean) {

var time  =timebase
    val digitCount = time.toString().length
    if (digitCount==10){
        time = timebase*1000
    }

    val defaultLocale = Locale.getDefault()
    val locale = try {
        Locale(
            if (this.context.config.SelectedLanguage == "ar") {
                "en"
            } else {
                this.context.config.SelectedLanguage
            }
        )
    } catch (e: Exception) {
        defaultLocale
    }

    val zoneId = ZoneId.systemDefault()

    val messageDateTime = Instant.ofEpochMilli(time).atZone(zoneId)
    val messageDate = messageDateTime.toLocalDate()

    val today = LocalDate.now(zoneId)

    if (/*chack*/  messageDate.isEqual(today)) {



        val dt: Date = Date(time)
        val sdf = SimpleDateFormat("hh:mm aa", locale)
        val time1 = sdf.format(dt)
        this.text = "" + time1



    } else {




        val date2 = Date(time)
        val format = SimpleDateFormat("yyyy-MM-dd", locale)
        val dateString = format.format(date2)

        val date = LocalDate.parse(dateString)
//        val formatter = DateTimeFormatter.ofPattern("dd MMM YYYY", locale)
//        this.text = "" + date.format(formatter)



        val currentYear = LocalDate.now().year

        val formatter = if (date.year == currentYear) {
            DateTimeFormatter.ofPattern("dd MMM", locale)       // 12 Jan
        } else {
            DateTimeFormatter.ofPattern("dd/MM/yyyy", locale)   // 12 Jan, 22
        }

        this.text = date.format(formatter)
    }

}




@BindingAdapter("setTxtColor")
fun ImageView.setTxtColor(mess: String) {

    val animation: AnimatedVectorDrawable

    try {
        when (mess) {

            "delaytime" -> {
                this.setImageResource(R.drawable.vector_loader_animated)
                val d: Drawable = this.drawable
                if (d is AnimatedVectorDrawable) {
                    animation = d
                    animation.start()
                }
            }

            "ScheduledMessage" -> {
                this.setImageResource(R.drawable.baseline_access_time_24)
            }

            "Sending" -> {
                this.setImageResource(R.drawable.message_sending_process_icon)
            }

            "Generic failure", "No service", "Null PDU", "Radio off", "Error", "SMS not delivered" -> {
                this.setImageResource(R.drawable.baseline_error_outline_24)
            }

            "SMS delivered" -> {
                this.setImageResource(R.drawable.message_delivered_done_icon)
            }

            else -> {
                this.setImageResource(R.drawable.message_delivered_done_icon)
            }
        }
    } catch (e: Exception) {

    }
}

@SuppressLint("UseCompatLoadingForColorStateLists")
@BindingAdapter("setbgcolor")
fun ConstraintLayout.setbgcolor(name: String) {
    when (name) {
        "Archive" -> {
            this.backgroundTintList = resources.getColorStateList(R.color.Archive)
        }

        "Delete" -> {
            this.backgroundTintList = resources.getColorStateList(R.color.Delete)
        }

        "Call" -> {
            this.backgroundTintList = resources.getColorStateList(R.color.Call)
        }

        "Mark as read" -> {
            this.backgroundTintList = resources.getColorStateList(R.color.Mark_as_read)
        }

        "Pin" -> {
            this.backgroundTintList = resources.getColorStateList(R.color.Pin)
        }

        "Private Chat" -> {
            this.backgroundTintList = resources.getColorStateList(R.color.Private_Chat)
        }

        else -> {
            this.backgroundTintList = resources.getColorStateList(R.color.Archive)
        }

    }
}

@SuppressLint("UseCompatLoadingForColorStateLists")
@BindingAdapter("setbgswipesrc")
fun ImageView.setbgswipesrc(name: String) {
    when (name) {
        "Archive" -> {
            this.setImageResource(R.drawable.swipe_archive)
        }

        "Delete" -> {
            this.setImageResource(R.drawable.swipe_delete)
        }

        "Call" -> {
            this.setImageResource(R.drawable.swipe_call)
        }

        "Mark as read" -> {
            this.setImageResource(R.drawable.swipe_mark_as_read_new)
        }

        "Pin" -> {
            this.setImageResource(R.drawable.swipe_pin)
        }

        "Private Chat" -> {
            this.setImageResource(R.drawable.swipe_private_chat)
        }

        else -> {
            this.setImageResource(R.drawable.outline_cancel_24)
        }
    }
}


fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}


@BindingAdapter("fullappfontstyleregular")
fun TextView.fullappfontstyleregular(con: Int) {

    when (context.config.Message_full_App_Font_Style) {
        "1" -> {
            this.typeface =
                ResourcesCompat.getFont(context, R.font.rubikregular)
        }

        "2" -> {
            if (context.config.SystemTextViewSwitchAb) {
//                this.typeface = Typeface.DEFAULT
                this.typeface =
                    ResourcesCompat.getFont(context, R.font.poppins_regular)
            } else {
                this.typeface = ResourcesCompat.getFont(context, R.font.opensansregular)
            }
        }

        else -> {
            this.typeface =
                ResourcesCompat.getFont(context, R.font.rubikregular)
        }
    }
}

@BindingAdapter("fullappfontstylesemibold")
fun TextView.fullappfontstylesemibold(con: Int) {
    when (context.config.Message_full_App_Font_Style) {
        "1" -> {
            this.typeface =
                ResourcesCompat.getFont(context, R.font.rubiksemibold)
        }

        "2" -> {
//            this.typeface = ResourcesCompat.getFont(context, R.font.opensanssemibold);
            if (context.config.SystemTextViewSwitchAb) {
//                this.typeface = Typeface.DEFAULT
                this.typeface =
                    ResourcesCompat.getFont(context, R.font.poppins_semibold)
            } else {
                this.typeface = ResourcesCompat.getFont(context, R.font.opensanssemibold)
            }
        }

        else -> {
            this.typeface =
                ResourcesCompat.getFont(context, R.font.rubiksemibold)
        }
    }
}

@BindingAdapter("fullappfontstylebold")
fun TextView.fullappfontstylebold(con: Int) {
    when (context.config.Message_full_App_Font_Style) {
        "1" -> {
            this.typeface = ResourcesCompat.getFont(context, R.font.rubikbold)
        }

        "2" -> {
//            this.typeface = ResourcesCompat.getFont(context, R.font.opensansbold);
            if (context.config.SystemTextViewSwitchAb) {
//                this.typeface = Typeface.DEFAULT
                this.typeface =
                    ResourcesCompat.getFont(context, R.font.poppins_bold)
            } else {
                this.typeface = ResourcesCompat.getFont(context, R.font.opensansbold)
            }
        }

        else -> {
            this.typeface = ResourcesCompat.getFont(context, R.font.rubikbold)
        }
    }
}

@BindingAdapter("fullappfontstylemediam")
fun TextView.fullappfontstylemediam(con: Int) {
    when (context.config.Message_full_App_Font_Style) {
        "1" -> {
            this.typeface = ResourcesCompat.getFont(context, R.font.rubikmedium)
        }

        "2" -> {
//            this.typeface = ResourcesCompat.getFont(context, R.font.opensansmedium);
            if (context.config.SystemTextViewSwitchAb) {
//                this.typeface = Typeface.DEFAULT
                this.typeface =
                    ResourcesCompat.getFont(context, R.font.poppins_medium)
            } else {
                this.typeface = ResourcesCompat.getFont(context, R.font.opensansmedium)
            }
        }

        else -> {
            this.typeface = ResourcesCompat.getFont(context, R.font.rubikmedium)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@BindingAdapter("setTextSyle")
fun TextView.setTextSyle(chak: Boolean) {
    if (chak) {
        when (context.config.Message_full_App_Font_Style) {
            "1" -> {
                this.typeface =
                    ResourcesCompat.getFont(context, R.font.rubiksemibold)
            }

            "2" -> {
//                this.typeface = ResourcesCompat.getFont(context, R.font.opensanssemibold);
                if (context.config.SystemTextViewSwitchAb) {
//                this.typeface = Typeface.DEFAULT
                    this.typeface = ResourcesCompat.getFont(
                        context,
                        R.font.poppins_regular
                    )
                } else {
                    this.typeface = ResourcesCompat.getFont(context, R.font.opensansregular)
                }
            }

            else -> {
                this.typeface =
                    ResourcesCompat.getFont(context, R.font.rubiksemibold)
            }
        }

    } else {

        when (context.config.Message_full_App_Font_Style) {
            "1" -> {
                this.typeface =
                    ResourcesCompat.getFont(context, R.font.rubikregular)
            }

            "2" -> {
//                this.typeface = ResourcesCompat.getFont(context, R.font.opensansregular);
                if (context.config.SystemTextViewSwitchAb) {
//                this.typeface = Typeface.DEFAULT
                    this.typeface =
                        ResourcesCompat.getFont(context, R.font.poppins_regular)
                } else {
                    this.typeface = ResourcesCompat.getFont(context, R.font.opensansregular)
                }
            }

            else -> {
                this.typeface =
                    ResourcesCompat.getFont(context, R.font.rubikregular)
            }
        }
    }
}

@BindingAdapter("reviewFormBackground")
fun View.reviewFormBackground(con: Int) {
    val isLightTheme = !context.isDarkThemeMode()
    val drawableRes = if (isLightTheme) {
        R.drawable.bottom_sheet_rounded_bg
    } else {
        R.drawable.bottom_sheet_rounded_bg_dark
    }
    setBackgroundResource(drawableRes)
}

@BindingAdapter("reviewFormTitleColor")
fun TextView.reviewFormTitleColor(con: Int) {
    val colorRes = if (!context.isDarkThemeMode()) {
        R.color.review_form_title_light
    } else {
        R.color.review_form_title_dark
    }
    setTextColor(resources.getColor(colorRes))
}

@BindingAdapter("reviewFormLabelColor")
fun TextView.reviewFormLabelColor(con: Int) {
    val colorRes = if (!context.isDarkThemeMode()) {
        R.color.review_form_label_light
    } else {
        R.color.review_form_label_dark
    }
    setTextColor(resources.getColor(colorRes))
}

@BindingAdapter("reviewFormInputBackground")
fun EditText.reviewFormInputBackground(con: Int) {
    val isLightTheme = !context.isDarkThemeMode()
    val drawableRes = if (isLightTheme) {
        R.drawable.bg_review_input
    } else {
        R.drawable.bg_review_input_dark
    }
    setBackgroundResource(drawableRes)
}

@BindingAdapter("reviewFormInputColors")
fun EditText.reviewFormInputColors(con: Int) {
    if (!context.isDarkThemeMode()) {
        setTextColor(resources.getColor(R.color.review_form_input_text_light))
        setHintTextColor(resources.getColor(R.color.review_form_input_hint_light))
    } else {
        setTextColor(resources.getColor(R.color.review_form_input_text_dark))
        setHintTextColor(resources.getColor(R.color.review_form_input_hint_dark))
    }
}

@BindingAdapter("reviewFormIconTint")
fun AppCompatImageButton.reviewFormIconTint(con: Int) {
    val colorRes = if (!context.isDarkThemeMode()) {
        R.color.review_form_icon_light
    } else {
        R.color.review_form_icon_dark
    }
    imageTintList = ColorStateList.valueOf(resources.getColor(colorRes))
}
