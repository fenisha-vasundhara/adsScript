package com.messenger.phone.number.text.sms.service.apps

import android.content.res.ColorStateList
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.demo.adsmanage.Commen.firebaseEventMain
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
import com.messenger.phone.number.text.sms.service.apps.CommanClass.iscustomwallpaersetterdid
import com.messenger.phone.number.text.sms.service.apps.CommanClass.iscustomwallpaersetterdidmovilenumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setWallpaperdone
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.generateRandomColorString
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityCustomWallpaperManageBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.CustomProfileUri
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomWallpaperManageActivity : AppCompatActivity() {

    lateinit var binding: ActivityCustomWallpaperManageBinding

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            launchCustomWallpaperPreview(selectedUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_custom_wallpaper_manage)
        setBaseTheme(binding.vAnd15StatusBar)
        this.firebaseEventMain("Custom_Wallpaper_Manage")

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


        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.solidImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.albumImage.setOnClickListener {
            startActivity(Intent(this@CustomWallpaperManageActivity, SolidColorListActivity::class.java))
        }

        binding.defaultWallpaperBtn.setOnClickListener {

            val options = arrayOf(resources.getString(R.string.For_this_chat) + " ${"$iscustomwallpaersetterdidmovilenumber"}", resources.getString(R.string.For_all_chats))
            var selectedItem = 0
            val builder = MaterialAlertDialogBuilder(this, getMaterialDialogTheme())
            val adapter = WallpaperChoiceDialogAdapter(
                context = ContextThemeWrapper(this, getMaterialDialogTheme()),
                options = options,
                selectedPosition = selectedItem,
                primaryColor = getProperPrimaryColor(),
                textColor = getProperTextColor()
            )
            builder.setCancelable(false)
            builder.setTitle(resources.getString(R.string.Set_default_wallpaper))
            builder.setSingleChoiceItems(adapter, selectedItem) { dialog, which ->
                selectedItem = which
                adapter.updateSelection(which)
            }
            builder.setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                when (selectedItem) {
                    0 -> {
                        config.saveProfileUri(CustomProfileUri(tredid = iscustomwallpaersetterdid.toString(), imguri = ""))
                        setWallpaperdone = true
                        finish()
                    }

                    1 -> {
                        config.isAllChatWallpaper = false
                        config.isAllChatDefault = true
                        config.removeAllProfileUri()
                        setWallpaperdone = true
                        finish()
                    }
                }
                dialog.dismiss()
            }
            builder.setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            builder.show().also { dialog ->
                styleWallpaperChoiceDialog(dialog)
                dialog.listView?.post {
                    adapter.updateSelection(selectedItem)
                }
            }


//            config.iscustomgalleryimageset = false
//            setWallpaperdone = true
//            finish()

//            config.isAllChatWallpaper = false
//            config.isAllChatDefault = true
//            config.removeAllProfileUri()
//            setWallpaperdone = true
//            finish()

        }
    }

    private fun launchCustomWallpaperPreview(uri: Uri) {
        startActivity(
            Intent(this, CustomWallpaperActivity::class.java)
                .putExtra("imagepath", uri.toString())
                .putExtra("issolidecolor", false)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        )
    }

    suspend fun generateRandomColorAndgetFIrstColor(): String {
        return if (config.SolidColorlist.isNotEmpty()) {
            config.SolidColorlist[0]
        } else {
            val numberOfColors = 50
            val colors = List(numberOfColors) { generateRandomColorString() }
            config.SolidColorlist = colors
            config.SolidColorlist[0]
        }
    }

    override fun onStart() {
        super.onStart()
        ThemeSetup()
        if (setWallpaperdone) {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        ThemeSetup()
    }

    private fun ThemeSetup() {
        val surfaceColor = getProperBackgroundColor()
        val statusBarColor = getProperStatusBarColor()
        val navigationBarColor = getBottomNavigationBackgroundColor()
        val textColor = getProperTextColor()
        val secondaryTextColor = getProperSecondaryTextColor()
        val primaryColor = getProperPrimaryColor()
        val optionFillColor = primaryColor.adjustAlpha(0.15f)
        val optionStrokeColor = surfaceColor
        val rippleColor = primaryColor.adjustAlpha(0.3f)
        val cornerSize = resources.getDimension(com.intuit.sdp.R.dimen._10sdp)
        val strokeWidth = resources.getDimension(com.intuit.sdp.R.dimen._1sdp)
        val cardStrokeWidth = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp)

        binding.main.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
        binding.backBtn.iconTint = ColorStateList.valueOf(secondaryTextColor)
        binding.textView3.setTextColor(textColor)
        binding.textView25.setTextColor(textColor)
        binding.textView26.setTextColor(textColor)
        binding.textView27.setTextColor(textColor)
        binding.defaultWallpaperBtnImg.imageTintList = ColorStateList.valueOf(primaryColor)



        binding.defaultWallpaperBtn.background = createOptionBackground(
            cornerSize = cornerSize,
            fillColor = optionFillColor,
            strokeWidth = strokeWidth,
            strokeColor = optionStrokeColor,
            rippleColor = rippleColor,
            showRipple = true
        )


        binding.albumImage.background = createOptionBackground(
            cornerSize = cornerSize,
            fillColor = optionFillColor,
            strokeWidth = strokeWidth,
            strokeColor = optionStrokeColor,
            rippleColor = rippleColor,
            showRipple = true
        )

        binding.solidImage.background = createOptionBackground(
            cornerSize = cornerSize,
            fillColor = optionFillColor,
            strokeWidth = strokeWidth,
            strokeColor = optionStrokeColor,
            rippleColor = rippleColor,
            showRipple = true
        )


        binding.albumImage.drawable.setTint(primaryColor)
        binding.solidImage.drawable.setTint(primaryColor)

        val window = window
        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightBars = ThemeModeManager.shouldUseLightSystemBars(this@CustomWallpaperManageActivity)
            isAppearanceLightStatusBars = useLightBars
            isAppearanceLightNavigationBars = useLightBars
        }
    }

    private fun styleWallpaperChoiceDialog(dialog: AlertDialog) {
        val primaryColor = getProperPrimaryColor()
        val textColor = getProperTextColor()

        dialog.getButton(android.app.Dialog.BUTTON_POSITIVE)?.setTextColor(primaryColor)
        dialog.getButton(android.app.Dialog.BUTTON_NEGATIVE)?.setTextColor(textColor)
    }
}
