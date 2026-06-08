package com.messenger.phone.number.text.sms.service.apps

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.invisible
import com.messenger.phone.number.text.sms.service.apps.CommanClass.iscustomwallpaersetterdid
import com.messenger.phone.number.text.sms.service.apps.CommanClass.iscustomwallpaersetterdidmovilenumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setWallpaperdone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityCustomWallpaperBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.CustomProfileAlpha
import com.messenger.phone.number.text.sms.service.apps.modelClass.CustomProfileUri
import com.simplemobiletools.commons.extensions.getContrastColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@AndroidEntryPoint
class CustomWallpaperActivity : AppCompatActivity() {


    lateinit var binding: ActivityCustomWallpaperBinding



    private var imagepath: String? = null
    private var colorcode: String? = null
    var issolidecolor = false

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_custom_wallpaper)
        imagepath = intent.getStringExtra("imagepath")
        colorcode = intent.getStringExtra("colorcode")
        issolidecolor = intent.getBooleanExtra("issolidecolor", false)
        this.firebaseEventMain("Custom_Wallpaper")
        setBaseTheme(binding.vAnd15StatusBar)
//        binding.messageOut.setCardBackgroundColor(Color.parseColor(config.outmessagecolorcustomwallpaper?.let { getAlphaColor(it) }))

        binding.messageOut.radius = config.messagecorner

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


//        binding.messageIn.setCardBackgroundColor(Color.parseColor(config.inmessagecolorcustomwallpaper?.let { getAlphaColor(it) }))

        binding.messageIn.radius = config.messagecorner

        if (config.Message_Send_Activity == "2") {
            binding.datetxt.invisible()
            binding.datetxtCon.visible()
//            binding.messageOut.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
//            binding.messageIn.setCardBackgroundColor(ContextCompat.getColor(this, R.color.inmessagedefaultcolor))
            binding.dateline1.gone()
            binding.dateline2.gone()
            binding.inmessagetext.setTextColor(ContextCompat.getColor(this, R.color.black2))
        } else {
            binding.datetxt.visible()
            binding.datetxtCon.invisible()
//            binding.messageOut.setCardBackgroundColor(ContextCompat.getColor(this, R.color.unselectmessageout))
//            binding.messageIn.setCardBackgroundColor(ContextCompat.getColor(this, R.color.appcolor))
            binding.dateline1.visible()
            binding.dateline2.visible()
            binding.inmessagetext.setTextColor(ContextCompat.getColor(this, R.color.white))
        }


        val customalpha = config.getcustomprofilealpha(iscustomwallpaersetterdid.toString())

        if (customalpha != null) {
            binding.imageAlpha.value = (customalpha.alpha.toDouble() * 100).toFloat()
            binding.alphaset.alpha = customalpha.alpha.toDouble().toFloat()
        } else {
            binding.imageAlpha.value = 0F
            binding.alphaset.alpha = 0F
        }



        binding.imageAlpha.setLabelFormatter { value ->
            value.toInt().toString()
        }
        binding.imageAlpha.addOnChangeListener { slider, value, fromUser ->
            val alpha = value.toFloat() / 100
            config.savecustomprofilealpha(CustomProfileAlpha(iscustomwallpaersetterdid.toString(), alpha.toString()))
            binding.alphaset.alpha = alpha
        }

        binding.backBtn.setOnClickListener {
            finish()

        }

        Glide.with(this).load(imagepath).addListener(object : RequestListener<Drawable?> {
            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable?>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
//                toast("onResourceReady")
                binding.image1.setSiShape(resource)
                return false
            }

            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
//                toast("onLoadFailed")
                return false
            }
        }).override(1000).fitCenter().into(binding.image1)

        if (issolidecolor) {
            binding.image1.gone()
            binding.solidColor.visible()
            if (colorcode != null) {
                binding.solidColor.setBackgroundColor(Color.parseColor(colorcode))
            }

            if (config.Message_Send_Activity == "2") {
//                binding.customwallpaperBg2.visible()
            } else {
                binding.customwallpaperBg2.gone()
            }

        } else {
            binding.image1.visible()
            binding.solidColor.gone()

            binding.customwallpaperBg2.gone()

        }

        binding.customWallpaperBtn.setOnClickListener {
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
            builder.setTitle(resources.getString(R.string.Set_wallpaper))
            builder.setSingleChoiceItems(adapter, selectedItem) { dialog, which ->
                selectedItem = which
                adapter.updateSelection(which)
            }
            builder.setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                when (selectedItem) {
                    0 -> {
                        CoroutineScope(Dispatchers.IO).launch {

//                            config.iscustomgalleryimageset = false
                            captureVisibleBitmap(binding.mainlayoutper, iscustomwallpaersetterdid.toString())
                            config.saveProfileUri(CustomProfileUri(tredid = iscustomwallpaersetterdid.toString(), imguri = iscustomwallpaersetterdid.toString()))
                            setWallpaperdone = true
                            finish()


                        }
                    }

                    1 -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            config.isAllChatWallpaper = true
                            config.isAllChatDefault = false
                            config.removeAllProfileUri()
                            captureVisibleBitmap(binding.mainlayoutper, "isAllChatWallpaper")
                            setWallpaperdone = true
                            finish()


                        }
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

//            CoroutineScope(Dispatchers.IO).launch {
//                config.iscustomgalleryimageset = captureVisibleBitmap(binding.mainlayoutper, "image1")
//                setWallpaperdone = true
//                finish()
//            }


//            CoroutineScope(Dispatchers.IO).launch {
//                config.isAllChatWallpaper = true
//                config.isAllChatDefault = false
//                config.removeAllProfileUri()
//                captureVisibleBitmap(binding.mainlayoutper, "isAllChatWallpaper")
//                setWallpaperdone = true
//                finish()
//
//            }

        }
    }

    suspend fun captureVisibleBitmap(view: View, filename: String): Boolean {
        delay(10)
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return saveBitmapToCache(bitmap, "${filename}.png")
    }

    suspend fun saveBitmapToCache(bitmap: Bitmap, filename: String): Boolean {
        delay(10)
        val cacheDir = cacheDir
        val file = File(cacheDir, filename)
        return try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) // Adjust format and quality as needed
            stream.close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    override fun onStart() {
        super.onStart()
        ThemeSetup()
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
        val optionFillColor = primaryColor
        val optionStrokeColor = surfaceColor
        val rippleColor = primaryColor.adjustAlpha(0.16f)
        val cornerSize = resources.getDimension(com.intuit.sdp.R.dimen._10sdp)
        val strokeWidth = resources.getDimension(com.intuit.sdp.R.dimen._1sdp)

        binding.main.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
        binding.backBtn.iconTint = ColorStateList.valueOf(secondaryTextColor)
        binding.textView3.setTextColor(textColor)

        binding.datetxt.setTextColor(optionFillColor.adjustAlpha(0.5f).getContrastColor())
        binding.datetxtNew.setTextColor(optionFillColor.adjustAlpha(0.5f).getContrastColor())
        binding.datetxtCon.setCardBackgroundColor(optionFillColor.adjustAlpha(0.5f))
        binding.datetxtCon.strokeWidth = 0 // resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp)
        binding.datetxtCon.setCardElevation(0f)

        binding.newmessagecountandbottomscrollin.background = createOptionBackground(
            cornerSize = cornerSize,
            fillColor = optionFillColor,
            strokeWidth = 0f,
            strokeColor = optionStrokeColor,
            rippleColor = rippleColor,
            showRipple = false
        )
        binding.newmessagecountandbottomscrollin.setTextColor(optionFillColor.getContrastColor())
        binding.dateline1.imageTintList = ColorStateList.valueOf(secondaryTextColor.adjustAlpha(0.5f))
        binding.dateline2.imageTintList = ColorStateList.valueOf(secondaryTextColor.adjustAlpha(0.5f))

        binding.customWallpaperBtn.background = createOptionBackground(
            cornerSize = cornerSize,
            fillColor = primaryColor,
            strokeWidth = strokeWidth,
            strokeColor = primaryColor,
            rippleColor = rippleColor,
            showRipple = true
        )
        binding.customWallpaperBtn.setTextColor(primaryColor.getContrastColor())

        binding.imageAlpha.trackActiveTintList = ColorStateList.valueOf(primaryColor)
        binding.imageAlpha.trackInactiveTintList = ColorStateList.valueOf(secondaryTextColor.adjustAlpha(0.30f))
        binding.imageAlpha.thumbTintList = ColorStateList.valueOf(primaryColor)
        binding.imageAlpha.haloTintList = ColorStateList.valueOf(primaryColor.adjustAlpha(0.20f))

        val window = window
        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightBars = ThemeModeManager.shouldUseLightSystemBars(this@CustomWallpaperActivity)
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
