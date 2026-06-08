package com.messenger.phone.number.text.sms.service.apps.CommanClass

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Telephony.Mms
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentManager
import com.demo.adsmanage.Commen.firebaseEventMain
import com.demo.adsmanage.Commen.log
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.messenger.phone.number.text.sms.service.apps.Dialog.Sub_Dialog_New
import com.messenger.phone.number.text.sms.service.apps.HomeABActivity
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.data.Message
import com.messenger.phone.number.text.sms.service.apps.modelClass.Customnotificationmodel
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.commons.dialogs.ConfirmationAdvancedDialog
import com.simplemobiletools.commons.dialogs.WritePermissionDialog
import com.simplemobiletools.commons.extensions.applyColorFilter
import com.simplemobiletools.commons.extensions.baseConfig
import com.simplemobiletools.commons.extensions.buildDocumentUriSdk30
import com.simplemobiletools.commons.extensions.createAndroidDataOrObbUri
import com.simplemobiletools.commons.extensions.createAndroidSAFFile
import com.simplemobiletools.commons.extensions.createDocumentUriUsingFirstParentTreeUri
import com.simplemobiletools.commons.extensions.createFirstParentTreeUriUsingRootTree
import com.simplemobiletools.commons.extensions.createSAFFileSdk30
import com.simplemobiletools.commons.extensions.getAndroidSAFUri
import com.simplemobiletools.commons.extensions.getAndroidTreeUri
import com.simplemobiletools.commons.extensions.getContrastColor
import com.simplemobiletools.commons.extensions.getDocumentFile
import com.simplemobiletools.commons.extensions.getDoesFilePathExist
import com.simplemobiletools.commons.extensions.getFileUrisFromFileDirItems
import com.simplemobiletools.commons.extensions.getFilenameFromPath
import com.simplemobiletools.commons.extensions.getFirstParentLevel
import com.simplemobiletools.commons.extensions.getFirstParentPath
import com.simplemobiletools.commons.extensions.getMimeType
import com.simplemobiletools.commons.extensions.getMyContactsCursor
import com.simplemobiletools.commons.extensions.getNameLetter
import com.simplemobiletools.commons.extensions.getParentPath
import com.simplemobiletools.commons.extensions.getProperStatusBarColor
import com.simplemobiletools.commons.extensions.hasProperStoredAndroidTreeUri
import com.simplemobiletools.commons.extensions.hasProperStoredDocumentUriSdk30
import com.simplemobiletools.commons.extensions.hasProperStoredFirstParentUri
import com.simplemobiletools.commons.extensions.hasProperStoredTreeUri
import com.simplemobiletools.commons.extensions.hideKeyboard
import com.simplemobiletools.commons.extensions.hideKeyboardSync
import com.simplemobiletools.commons.extensions.isAccessibleWithSAFSdk30
import com.simplemobiletools.commons.extensions.isPathOnOTG
import com.simplemobiletools.commons.extensions.isPathOnSD
import com.simplemobiletools.commons.extensions.isRestrictedSAFOnlyRoot
import com.simplemobiletools.commons.extensions.isRestrictedWithSAFSdk30
import com.simplemobiletools.commons.extensions.isSDCardSetAsDefaultStorage
import com.simplemobiletools.commons.extensions.needsStupidWritePermissions
import com.simplemobiletools.commons.extensions.showErrorToast
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.helpers.CREATE_DOCUMENT_SDK_30
import com.simplemobiletools.commons.helpers.EXTERNAL_STORAGE_PROVIDER_AUTHORITY
import com.simplemobiletools.commons.helpers.EXTRA_SHOW_ADVANCED
import com.simplemobiletools.commons.helpers.OPEN_DOCUMENT_TREE_FOR_ANDROID_DATA_OR_OBB
import com.simplemobiletools.commons.helpers.OPEN_DOCUMENT_TREE_FOR_SDK_30
import com.simplemobiletools.commons.helpers.OPEN_DOCUMENT_TREE_OTG
import com.simplemobiletools.commons.helpers.OPEN_DOCUMENT_TREE_SD
import com.simplemobiletools.commons.helpers.isOnMainThread
import com.simplemobiletools.commons.helpers.isRPlus
import com.simplemobiletools.commons.models.FileDirItem
import com.simplemobiletools.commons.views.MyTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Locale

var isFirst = true
var Alredyclick = false
var langchange = false
var lastAdShownTime: Long = 0
var interstitialAdCount: Int = 0
const val INTERSTITIAL_AD_COOLDOWN = 30 * 1000L // 30 seconds
const val MAX_INTERSTITIALS_PER_SESSION = 3


var gooutside = false

var checkedDocumentPath = ""
var adloadornot = true
var otheradsload = false
const val check1 = "I don't like the design,"
const val check2 = "It doesn't have the function as I need,"
const val check3 = "It's not easy to use,"
const val check4 = "It's too complicated,"
const val GMAIL_ID = "chetanjambukiya23@gmail.com"
var isthreetimecount = 0
var adshowcount = 2

fun Activity.ShowsubScreenDialogopen(
    directopendialog: Boolean = false,
    supportFragmentManager: FragmentManager,
    dialogcolose: ((Boolean) -> Unit)? = null,
) {

    dialogcolose?.invoke(false)


//    val activityName = this.javaClass.simpleName
//    "ShowsubScreenDialogopen <--------> Current activity: ${activityName}".log()
//    val subDialogNew = Sub_Dialog_New.newInstance(activityName)
//    if (!config.subscriptionModelStart) {
//        dialogcolose?.invoke(false)
//        return
//    }
//    if (!BaseSharedPreferences(this@ShowsubScreenDialogopen).mIS_SUBSCRIBED!!) {
//        if (config.sundialogcount == 5) {
//            subDialogNew.show(supportFragmentManager, "")
//            config.sundialogcount++
//        } else if (config.sundialogcount == 10) {
//            if (directopendialog) {
//                subDialogNew.show(supportFragmentManager, "")
//            } else {
////                this.firebaseEvent("Direct_Open", "Subscription_Activity")
//                firebaseEventMain("Subscription_Direct_Open")
//                sendsubactivity()
//            }
//            config.sundialogcount = 0
//        } else {
//            config.sundialogcount++
//            dialogcolose?.invoke(false)
//        }
//    } else {
//        dialogcolose?.invoke(false)
//    }
//
//    subDialogNew.dialogdimisslisner = { subopen ->
//        if (subopen) {
//            dialogcolose?.invoke(true)
//        } else {
//            dialogcolose?.invoke(false)
//        }
//
//    }

}

fun Context.getMyFileUri(file: File): Uri {
    return if (com.simplemobiletools.commons.helpers.isNougatPlus()) {
        FileProvider.getUriForFile(this, "$packageName.provider", file)
    } else {
        Uri.fromFile(file)
    }
}

fun Context.showReceivedMessageNotification(
    messageId: Long,
    address: String,
    body: String,
    threadId: Long,
    bitmap: Bitmap?,
) {
    val privateCursor = getMyContactsCursor(favoritesOnly = false, withPhoneNumbersOnly = true)
    CoroutineScope(Dispatchers.IO).launch {
        val senderName = getNameFromAddress(address, privateCursor)

        Handler(Looper.getMainLooper()).post {
            notificationHelper.showMessageNotification(
                address = address,
                body = body,
                threadId = threadId,
                bitmap = bitmap,
                sender = senderName,
                messageid = messageId
            )
        }
    }
}

fun getContactLetterIconContact(context: Context, name: String): Bitmap {
    val letter = name.getNameLetter()
    val size = context.resources.getDimension(R.dimen.normal_icon_size).toInt()
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val view = TextView(context)
    view.layout(0, 0, size, size)

    val circlePaint = Paint().apply {
        if (context.config.Systemgeneratediconswitchab) {
            color =
                com.simplemobiletools.commons.helpers.letterBackgroundColors[Math.abs(name.hashCode()) % com.simplemobiletools.commons.helpers.letterBackgroundColors.size].toInt()
        } else {
            color = context.config.themeselectedcolor
        }
        isAntiAlias = true
    }

    val wantedTextSize = size / 2f
    val textPaint = Paint().apply {
        color = circlePaint.color.getContrastColor()
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        textSize = wantedTextSize
        style = Paint.Style.FILL
    }

    canvas.drawCircle(size / 2f, size / 2f, size / 2f, circlePaint)

    val xPos = canvas.width / 2f
    val yPos = canvas.height / 2 - (textPaint.descent() + textPaint.ascent()) / 2
    canvas.drawText(letter, xPos, yPos, textPaint)
    view.draw(canvas)
    return bitmap
}

fun Context.getLatestMMS(): Message? {
    val sortOrder = "${Mms.DATE} DESC LIMIT 1"
    return getMMS(sortOrder = sortOrder).firstOrNull()
}

fun Activity.hideKeyboard() {
    if (isOnMainThread()) {
        hideKeyboardSync()
    } else {
        Handler(Looper.getMainLooper()).post {
            hideKeyboardSync()
        }
    }
}

fun Activity.launchViewIntent(uri: Uri, mimetype: String, filename: String) {
    Intent().apply {
        action = Intent.ACTION_VIEW
        setDataAndType(uri, mimetype.lowercase(Locale.getDefault()))
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            hideKeyboard()
            startActivity(this)
        } catch (e: ActivityNotFoundException) {
            val newMimetype = filename.getMimeType()
            if (newMimetype.isNotEmpty() && mimetype != newMimetype) {
                launchViewIntent(uri, newMimetype, filename)
            } else {
                toast(com.simplemobiletools.commons.R.string.no_app_found)
            }
        } catch (e: Exception) {
            showErrorToast(e)
        }
    }
}

fun Activity.hideKeyboardSync() {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow((currentFocus ?: View(this)).windowToken, 0)
    window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    currentFocus?.clearFocus()
}

fun Activity.updateStatusbarColor() {
    val window = this.window;
    window.statusBarColor = ContextCompat.getColor(this, this.getChatThemStatuscolor())
//    window.navigationBarColor = ContextCompat.getColor(this, this.getChatThemStatuscolor())
}

@SuppressLint("Range")
suspend fun Context.getNotificationSounds(): List<Customnotificationmodel> =
    withContext(Dispatchers.IO) {
        val manager = RingtoneManager(this@getNotificationSounds)
        manager.setType(RingtoneManager.TYPE_NOTIFICATION)
        val cursor = manager.cursor

        val list = ArrayList<Customnotificationmodel>()
        list.add(
            Customnotificationmodel(
                ringtonename = "Default",
                ringtonecontentpath = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    .toString(),
                ringtonepath = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    .toString()
            )
        )
        while (cursor.moveToNext()) {
            val notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
            val id = cursor.getString(RingtoneManager.ID_COLUMN_INDEX)
            val uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX)
            list.add(
                Customnotificationmodel(
                    ringtonename = notificationTitle,
                    ringtonecontentpath = "$uri/$id",
                    ringtonepath = getFilePathFromContentUri(
                        ("$uri/$id").toUri()
                    )
                )
            )
        }
        return@withContext list
    }

suspend fun Context.getFilePathFromContentUri(
    selectedVideoUri: Uri,
): String {
    val filePath: String
    val filePathColumn = arrayOf<String>(MediaStore.MediaColumns.DATA)

    val cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null)
    cursor!!.moveToFirst()

    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
    filePath = cursor.getString(columnIndex)
    cursor.close()
    return filePath
}

suspend fun Context.getAllCacheAudioModels(): List<Customnotificationmodel> =
    withContext(Dispatchers.IO) {
        val ringtoneDir = File(Environment.getExternalStorageDirectory(), "Documents/Ringtone")
        val audioFiles = ringtoneDir.listFiles()
        return@withContext audioFiles?.map { file ->
            Customnotificationmodel(
                ringtonename = file.name,
                ringtonecontentpath = Uri.fromFile(file).toString(),
                ringtonepath = file.path.toString()
            )
        } ?: emptyList()
    }

suspend fun Context.createFile(): File? = withContext(Dispatchers.IO) {
    val filePath = File(Environment.getExternalStorageDirectory(), "Documents/Data/MS")
    Log.d("intervalMillis", "AutoFullAppLockRemove onFinish:<---------> 5 " + filePath)
    try {

        if (!filePath.exists()) {
            Log.d("intervalMillis", "AutoFullAppLockRemove onFinish:<---------> 6 " + filePath)
//            filePath.createNewFile()
            filePath.mkdirs()
        } else {
            Log.d("intervalMillis", "AutoFullAppLockRemove onFinish:<---------> 7 " + filePath)
        }
        Log.d("intervalMillis", "AutoFullAppLockRemove onFinish:<---------> 4 " + filePath)
        return@withContext filePath
    } catch (e: IOException) {
        e.printStackTrace()
        Log.d(
            "intervalMillis", "AutoFullAppLockRemove onFinish:<---------> 3 " + e.localizedMessage
        )
        return@withContext null
    }
}

fun containsAlphabets(input: String): Boolean {
    return input.any { it.isLetter() }
}


fun windowInsetsSetController(windowInsetsController: WindowInsetsControllerCompat) {
//    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
//    }
//    windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
}

fun View.windowInsetsSet(
    windowInsetsController: WindowInsetsControllerCompat, windowInsets: WindowInsetsCompat
) {
    val isSystemVisible = windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars())
    if (isSystemVisible) {
        // Re-hide after a small delay, so dialogs like Play Billing can finish
//        postDelayed({
//            windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
//        }, 1000)
    }
}

fun View.findBestInsetParent(): View {
    var current: View = this
    var lastView: View = this

    while (current.parent is View) {
        lastView = current
        current = current.parent as View

        if (current.id == android.R.id.content) {
            return current
        }
    }
    return lastView // top-most reachable view
}


fun ComponentActivity.myEdgeToEdge(
    vAnd15StatusBar: View
) {

    val useLightBars = ThemeModeManager.shouldUseLightSystemBars(this)
    val surfaceColor = if (ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM) {
        MaterialColors.getColor(vAnd15StatusBar, com.google.android.material.R.attr.colorSurface)
    } else {
        ContextCompat.getColor(
            this,
            if (useLightBars) R.color.status_bar_ab_color else R.color.toolbarcolor3new
        )
    }
    enableEdgeToEdge(
        navigationBarStyle = SystemBarStyle.auto(
            surfaceColor,
            surfaceColor
        )
    )

    val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
    window.decorView.setBackgroundColor(surfaceColor)
    window.statusBarColor = surfaceColor
    window.navigationBarColor = surfaceColor

    vAnd15StatusBar.visible()
    vAnd15StatusBar.setBackgroundColor(surfaceColor)
    ViewCompat.setOnApplyWindowInsetsListener(vAnd15StatusBar) { view, insets ->
        val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
        val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

        // Status bar spacer height
        view.updateLayoutParams {
            height = statusBarHeight
        }

        // 🔥 Apply nav bar padding to root parent
        val rootView = view.findBestInsetParent()
        rootView.updatePadding(
            bottom = navBarHeight
        )

        view.windowInsetsSet(windowInsetsController, insets)
        insets
    }



}

fun ComponentActivity.createChatBottomSheetDialog(): BottomSheetDialog {
    return BottomSheetDialog(this, getMaterialBottomSheetTheme())
}

fun ComponentActivity.myChatEdgeToEdge(
    view: View, vAnd15StatusBar: View
) {

    val useLightBars = ThemeModeManager.shouldUseLightSystemBars(this)
    val surfaceColor = getDialogBackgroundColor()
//    val surfaceColor = if (ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM) {
//        MaterialColors.getColor(vAnd15StatusBar, com.google.android.material.R.attr.colorSurface)
//    } else {
//        ContextCompat.getColor(
//            this,
//            if (useLightBars) R.color.status_bar_ab_color else R.color.toolbarcolor3new
//        )
//    }
    enableEdgeToEdge(
        navigationBarStyle = SystemBarStyle.light(
            surfaceColor,
            surfaceColor
        )
    )
    window.decorView.setBackgroundColor(surfaceColor)
    window.statusBarColor = surfaceColor
    window.navigationBarColor = surfaceColor

    val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)

    vAnd15StatusBar.visible()

    ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        val imeVisible = windowInsets.isVisible(WindowInsetsCompat.Type.ime())
        val imeHeight = windowInsets.getInsets(WindowInsetsCompat.Type.ime()).bottom

        v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            leftMargin = insets.left
            rightMargin = insets.right
            bottomMargin = if (imeVisible) 0 else insets.bottom

//            if (topMargin < insets.top) {
//                topMargin = insets.top
//            }

        }
        v.setPadding(0, 0, 0, if (imeVisible) imeHeight else 0)
        v.windowInsetsSet(windowInsetsController, windowInsets)
        windowInsets
    }
    onWindowFocusChanged(true)
}

fun Activity.updateStatusbarColorMAinAppApp() {

//    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//        // Don't fit system windows so content can extend behind system bars
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//
//        // Hide both status and navigation bars
//        val controller = WindowInsetsControllerCompat(window, window.decorView)
//        controller.hide(
//            WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars()
//        )
//        controller.systemBarsBehavior =
//            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//    } else {
//        val window = this.window;
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        if (this.config.activeThemeSelection == 1 || this.config.activeThemeSelection == 4) {
//            WindowCompat.getInsetsController(window, window.decorView).apply {
//                isAppearanceLightStatusBars = true
//            }
//        } else {
//            WindowCompat.getInsetsController(window, window.decorView).apply {
//                isAppearanceLightStatusBars = false
//            }
//        }
//        window.statusBarColor =
//            ContextCompat.getColor(this, this.getChatThemStatuscolorMAinAppnew())
//
//
//        WindowCompat.setDecorFitsSystemWindows(window, true)
//        val controller = WindowInsetsControllerCompat(window, window.decorView)
//        controller.hide(WindowInsetsCompat.Type.navigationBars())
//        controller.systemBarsBehavior =
//            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//    }

    val window = this.window;
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    val useLightBars = ThemeModeManager.shouldUseLightSystemBars(this)
    WindowCompat.getInsetsController(window, window.decorView).apply {
        isAppearanceLightStatusBars = useLightBars
        isAppearanceLightNavigationBars = useLightBars
    }
    val barColor = if (ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM) {
        MaterialColors.getColor(window.decorView, com.google.android.material.R.attr.colorSurface)
    } else {
        ContextCompat.getColor(this, this.getChatThemStatuscolorMAinAppnew())
    }
    window.statusBarColor = barColor
    window.navigationBarColor = barColor
    window.decorView.setBackgroundColor(barColor)

//    WindowCompat.setDecorFitsSystemWindows(window, true)
//    val controller = WindowInsetsControllerCompat(window, window.decorView)
//    controller.hide(WindowInsetsCompat.Type.navigationBars())
//    controller.systemBarsBehavior =
//        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE


//    WindowCompat.setDecorFitsSystemWindows(window, true)

    // Change status bar color to red
//    window.statusBarColor = Color.parseColor("#FF0000") // Red


}

fun Activity.updateStatusbarColorSendMesage() {

//    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//        // Don't fit system windows so content can extend behind system bars
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//
//        // Hide both status and navigation bars
//        val controller = WindowInsetsControllerCompat(window, window.decorView)
//        controller.hide(
//            WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars()
//        )
//        controller.systemBarsBehavior =
//            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//    } else {
//        val window = this.window;
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        if (this.config.activeThemeSelection == 1 || this.config.activeThemeSelection == 4) {
//            WindowCompat.getInsetsController(window, window.decorView).apply {
//                isAppearanceLightStatusBars = true
//            }
//        } else {
//            WindowCompat.getInsetsController(window, window.decorView).apply {
//                isAppearanceLightStatusBars = false
//            }
//        }
//        window.statusBarColor =
//            ContextCompat.getColor(this, this.getChatThemStatuscolorMAinAppnew())
//
//
//        WindowCompat.setDecorFitsSystemWindows(window, true)
//        val controller = WindowInsetsControllerCompat(window, window.decorView)
//        controller.hide(WindowInsetsCompat.Type.navigationBars())
//        controller.systemBarsBehavior =
//            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//    }

    val window = this.window;
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    val useLightBars = ThemeModeManager.shouldUseLightSystemBars(this)
    WindowCompat.getInsetsController(window, window.decorView).apply {
        isAppearanceLightStatusBars = useLightBars
        isAppearanceLightNavigationBars = useLightBars
    }
    val barColor = if (ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM) {
        MaterialColors.getColor(window.decorView, com.google.android.material.R.attr.colorSurface)
    } else {
        ContextCompat.getColor(this, this.getChatThemStatuscolorAfterFullAppnew())
    }
    window.statusBarColor = barColor
    window.navigationBarColor = barColor
    window.decorView.setBackgroundColor(barColor)

//    WindowCompat.setDecorFitsSystemWindows(window, true)
//    val controller = WindowInsetsControllerCompat(window, window.decorView)
//    controller.hide(WindowInsetsCompat.Type.navigationBars())
//    controller.systemBarsBehavior =
//        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE


//    WindowCompat.setDecorFitsSystemWindows(window, true)

    // Change status bar color to red
//    window.statusBarColor = Color.parseColor("#FF0000") // Red


}


fun Activity.checkIfFileExists(file: File): Boolean {
    return filePathForLock.exists()
}

val Activity.filePathForLock
    get() = File(
        Environment.getExternalStorageDirectory(), "Documents/Data/MS"
    )

fun Activity.updateStatusbarColorFullApp() {

//    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {

    val window = this.window;
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    val useLightBars = ThemeModeManager.shouldUseLightSystemBars(this)
    WindowCompat.getInsetsController(window, window.decorView).apply {
        isAppearanceLightStatusBars = useLightBars
        isAppearanceLightNavigationBars = useLightBars
    }
    val barColor = if (ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM) {
        MaterialColors.getColor(window.decorView, com.google.android.material.R.attr.colorSurface)
    } else {
        ContextCompat.getColor(this, this.getChatThemStatuscolorFullAppnew())
    }
    window.statusBarColor = barColor
    window.navigationBarColor = barColor
    window.decorView.setBackgroundColor(barColor)
    WindowCompat.setDecorFitsSystemWindows(window, true)
    val controller = WindowInsetsControllerCompat(window, window.decorView)
//    controller.hide(WindowInsetsCompat.Type.navigationBars())
    controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

}

fun Activity.updateStatusbarColorAfterCallFullApp() {

//    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//        // Don't fit system windows so content can extend behind system bars
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//
//        // Hide both status and navigation bars
//        val controller = WindowInsetsControllerCompat(window, window.decorView)
//        controller.hide(
//            WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars()
//        )
//        controller.systemBarsBehavior =
//            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//    } else {
//        val window = this.window;
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        if (this.config.activeThemeSelection == 1 || this.config.activeThemeSelection == 4) {
//            WindowCompat.getInsetsController(window, window.decorView).apply {
//                isAppearanceLightStatusBars = true
//            }
//        } else {
//            WindowCompat.getInsetsController(window, window.decorView).apply {
//                isAppearanceLightStatusBars = false
//            }
//        }
//        window.statusBarColor =
//            ContextCompat.getColor(this, this.getChatThemStatuscolorAfterFullAppnew())
//        WindowCompat.setDecorFitsSystemWindows(window, true)
//        val controller = WindowInsetsControllerCompat(window, window.decorView)
//        controller.hide(WindowInsetsCompat.Type.navigationBars())
//        controller.systemBarsBehavior =
//            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//    }

    val window = this.window;
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    val useLightBars = ThemeModeManager.shouldUseLightSystemBars(this)
    WindowCompat.getInsetsController(window, window.decorView).apply {
        isAppearanceLightStatusBars = useLightBars
        isAppearanceLightNavigationBars = useLightBars
    }
    val barColor = if (ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM) {
        MaterialColors.getColor(window.decorView, com.google.android.material.R.attr.colorSurface)
    } else {
        ContextCompat.getColor(this, this.getChatThemStatuscolorAfterFullAppnew())
    }
    window.statusBarColor = barColor
    window.navigationBarColor = barColor
    window.decorView.setBackgroundColor(barColor)
    WindowCompat.setDecorFitsSystemWindows(window, true)
    val controller = WindowInsetsControllerCompat(window, window.decorView)
//    controller.hide(WindowInsetsCompat.Type.navigationBars())
    controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

}

fun Activity.updateStatusbarColorFullAppmainstrin() {
    val useLightBars = ThemeModeManager.shouldUseLightSystemBars(this)
    val barColor = if (ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM) {
        MaterialColors.getColor(window.decorView, com.google.android.material.R.attr.colorSurface)
    } else {
        ContextCompat.getColor(this, this.getChatThemStatuscolorFullApp())
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
//        controller.hide(
//            WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars()
//        )
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = useLightBars
            isAppearanceLightNavigationBars = useLightBars
        }
        window.statusBarColor = barColor
        window.navigationBarColor = barColor
    } else {
        val window = this.window;
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = useLightBars
            isAppearanceLightNavigationBars = useLightBars
        }
        window.statusBarColor = barColor
        window.navigationBarColor = barColor
    }
}


fun Activity.updateStatusbarColorForLockScreen() {


    val window = this.window;
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    val useLightBars = ThemeModeManager.shouldUseLightSystemBars(this)
    WindowCompat.getInsetsController(window, window.decorView).apply {
        isAppearanceLightStatusBars = useLightBars
        isAppearanceLightNavigationBars = useLightBars
    }
    val barColor = if (ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM) {
        MaterialColors.getColor(window.decorView, com.google.android.material.R.attr.colorSurface)
    } else {
        ContextCompat.getColor(this, this.getChatThemStatuscolorForLockScreen())
    }
    window.statusBarColor = barColor
    window.navigationBarColor = barColor
    window.decorView.setBackgroundColor(barColor)
    WindowCompat.setDecorFitsSystemWindows(window, true)
    val controller = WindowInsetsControllerCompat(window, window.decorView)
//    controller.hide(WindowInsetsCompat.Type.navigationBars())
    controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

}


fun Activity.getChatThemStatuscolorForLockScreen(): Int {
    return if (ThemeModeManager.isDarkThemeActive(this)) {
        R.color.chatbgcolor3
    } else {
        R.color.chatbgcolor1
    }
}

fun Activity.getChatThemStatuscolorFullApp(): Int {
    return if (ThemeModeManager.isDarkThemeActive(this)) {
        R.color.nav_color
    } else {
        R.color.white
    }
}
fun Activity.getChatThemStatuscolorFullAppnew(): Int {
    return if (ThemeModeManager.isDarkThemeActive(this)) {
        R.color.toolbarcolor3new
    } else {
        if (this.config.setABHomeActivityPref == "1") {
            R.color.appcolor
        } else {
            R.color.status_bar_ab_color
        }
    }
}

fun Activity.getChatThemStatuscolorAfterFullAppnew(): Int {
    return if (ThemeModeManager.isDarkThemeActive(this)) {
        R.color.navdark
    } else {
        R.color.after_call_toolbar
    }
}

fun Context.getChatThemStatuscolorAfterFullAppnew111(): Int {
    return if (ThemeModeManager.isDarkThemeActive(this)) {
        R.color.navdark
    } else {
        R.color.after_call_toolbar
    }
}

fun Activity.getChatThemStatuscolorMAinAppnew(): Int {
    return if (ThemeModeManager.isDarkThemeActive(this)) {
        R.color.toolbarcolor3new
    } else {
        if (this.config.Message_Send_Activity == "2") {
            R.color.white
        } else {
            R.color.appcolor
        }
    }
}

fun Activity.getChatThemStatuscolor(): Int {
    when (this.config.customwallpaperselected) {
        1 -> {
            return R.color.new_theme_toolbar_1
        }

        2 -> {
            return R.color.new_theme_toolbar_2
        }

        3 -> {
            return R.color.new_theme_toolbar_3
        }

        4 -> {
            return R.color.new_theme_toolbar_4
        }

        5 -> {
            return R.color.new_theme_toolbar_5
        }

        6 -> {
            return R.color.new_theme_toolbar_6
        }

        7 -> {
            return R.color.new_theme_toolbar_7
        }

        8 -> {
            return R.color.new_theme_toolbar_8
        }

        9 -> {
            return R.color.new_theme_toolbar_9
        }

        10 -> {
            return R.color.new_theme_toolbar_10
        }

        11 -> {
            return R.color.new_theme_toolbar_11
        }

        12 -> {
            return R.color.new_theme_toolbar_12
        }

        else -> {
            return R.color.new_theme_toolbar_1
        }
    }
}


fun Activity.handleSAFCreateDocumentDialogSdk30(
    path: String,
    callback: (success: Boolean) -> Unit,
): Boolean {
    hideKeyboard()
    return if (!packageName.startsWith("com.messenger.phone.number")) {
        callback(true)
        false
    } else if (isShowingSAFCreateDocumentDialogSdk30(path)) {
        BaseSimpleActivity.funAfterSdk30Action = callback
        true
    } else {
        callback(true)
        false
    }
}

fun Activity.showKeyboard(et: EditText) {
    et.requestFocus()
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)
}

@SuppressLint("InlinedApi")
fun Activity.isShowingSAFCreateDocumentDialogSdk30(path: String): Boolean {
    return if (!hasProperStoredDocumentUriSdk30(path)) {
        runOnUiThread {
            if (!isDestroyed && !isFinishing) {
                WritePermissionDialog(this, WritePermissionDialog.Mode.CreateDocumentSDK30) {
                    Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        type = DocumentsContract.Document.MIME_TYPE_DIR
                        putExtra(EXTRA_SHOW_ADVANCED, true)
                        addCategory(Intent.CATEGORY_OPENABLE)
                        putExtra(
                            DocumentsContract.EXTRA_INITIAL_URI,
                            buildDocumentUriSdk30(path.getParentPath())
                        )
                        putExtra(Intent.EXTRA_TITLE, path.getFilenameFromPath())
                        try {
                            startActivityForResult(this, CREATE_DOCUMENT_SDK_30)
                            checkedDocumentPath = path
                            return@apply
                        } catch (e: Exception) {
                            type = "*/*"
                        }

                        try {
                            startActivityForResult(this, CREATE_DOCUMENT_SDK_30)
                            checkedDocumentPath = path
                        } catch (e: ActivityNotFoundException) {
                            toastMess(R.string.system_service_disabled, Toast.LENGTH_LONG)
                        } catch (e: Exception) {
                            toastMess(R.string.unknown_error_occurred)
                        }
                    }
                }
            }
        }
        true
    } else {
        false
    }
}

fun Activity.handleAndroidSAFDialog(path: String, callback: (success: Boolean) -> Unit): Boolean {
    hideKeyboard()
    return if (!packageName.startsWith("com.messenger.phone.number")) {
        callback(true)
        false
    } else if (isShowingAndroidSAFDialog(path)) {
        HomeABActivity.funAfterSAFPermission = callback
        true
    } else {
        callback(true)
        false
    }
}

fun Activity.isShowingAndroidSAFDialog(path: String): Boolean {
    return if (isRestrictedSAFOnlyRoot(path) && (getAndroidTreeUri(path).isEmpty() || !hasProperStoredAndroidTreeUri(
            path
        ))
    ) {
        runOnUiThread {
            if (!isDestroyed && !isFinishing) {
                ConfirmationAdvancedDialog(
                    this,
                    "",
                    R.string.confirm_storage_access_android_text,
                    R.string.ok,
                    R.string.cancel
                ) { success ->
                    if (success) {
                        Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                            putExtra(EXTRA_SHOW_ADVANCED, true)
                            putExtra(
                                DocumentsContract.EXTRA_INITIAL_URI, createAndroidDataOrObbUri(path)
                            )
                            try {
                                startActivityForResult(
                                    this, OPEN_DOCUMENT_TREE_FOR_ANDROID_DATA_OR_OBB
                                )
                                checkedDocumentPath = path
                                return@apply
                            } catch (e: Exception) {
                                type = "*/*"
                            }

                            try {
                                startActivityForResult(
                                    this, OPEN_DOCUMENT_TREE_FOR_ANDROID_DATA_OR_OBB
                                )
                                checkedDocumentPath = path
                            } catch (e: ActivityNotFoundException) {
                                toastMess(R.string.system_service_disabled, Toast.LENGTH_LONG)
                            } catch (e: Exception) {
                                toastMess(R.string.unknown_error_occurred)
                            }
                        }
                    }
                }
            }
        }
        true
    } else {
        false
    }
}

fun Activity.isShowingSAFDialog(path: String): Boolean {
    return if ((!isRPlus() && isPathOnSD(path) && !isSDCardSetAsDefaultStorage() && (baseConfig.sdTreeUri.isEmpty() || !hasProperStoredTreeUri(
            false
        )))
    ) {
        runOnUiThread {
            if (!isDestroyed && !isFinishing) {
                WritePermissionDialog(this, WritePermissionDialog.Mode.SdCard) {
                    Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                        putExtra(EXTRA_SHOW_ADVANCED, true)
                        try {
                            startActivityForResult(this, OPEN_DOCUMENT_TREE_SD)
                            checkedDocumentPath = path
                            return@apply
                        } catch (e: Exception) {
                            type = "*/*"
                        }

                        try {
                            startActivityForResult(this, OPEN_DOCUMENT_TREE_SD)
                            checkedDocumentPath = path
                        } catch (e: ActivityNotFoundException) {
                            toastMess(R.string.system_service_disabled, Toast.LENGTH_LONG)
                        } catch (e: Exception) {
                            toastMess(R.string.unknown_error_occurred)
                        }
                    }
                }
            }
        }
        true
    } else {
        false
    }
}

fun Activity.handleSAFDialog(path: String, callback: (success: Boolean) -> Unit): Boolean {
    hideKeyboard()
    return if (!packageName.startsWith("com.simplemobiletools")) {
        callback(true)
        false
    } else if (isShowingSAFDialog(path) || isShowingOTGDialog(path)) {
        HomeABActivity.funAfterSAFPermission = callback
        true
    } else {
        callback(true)
        false
    }
}

fun Activity.showOTGPermissionDialog(path: String) {
    runOnUiThread {
        if (!isDestroyed && !isFinishing) {
            WritePermissionDialog(this, WritePermissionDialog.Mode.Otg) {
                Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                    try {
                        startActivityForResult(this, OPEN_DOCUMENT_TREE_OTG)
                        checkedDocumentPath = path
                        return@apply
                    } catch (e: Exception) {
                        type = "*/*"
                    }

                    try {
                        startActivityForResult(this, OPEN_DOCUMENT_TREE_OTG)
                        checkedDocumentPath = path
                    } catch (e: ActivityNotFoundException) {
                        toastMess(R.string.system_service_disabled, Toast.LENGTH_LONG)
                    } catch (e: Exception) {
                        toastMess(R.string.unknown_error_occurred)
                    }
                }
            }
        }
    }
}

fun Activity.isShowingOTGDialog(path: String): Boolean {
    return if (!isRPlus() && isPathOnOTG(path) && (baseConfig.OTGTreeUri.isEmpty() || !hasProperStoredTreeUri(
            true
        ))
    ) {
        showOTGPermissionDialog(path)
        true
    } else {
        false
    }
}

@SuppressLint("StringFormatInvalid")
fun Activity.showFileCreateError(path: String) {
    val error = String.format(getString(R.string.could_not_create_file), path)
    baseConfig.sdTreeUri = ""
    showErrorToast(error)
}

fun Activity.getFileOutputStream(
    fileDirItem: FileDirItem,
    allowCreatingNewFile: Boolean = false,
    callback: (outputStream: OutputStream?) -> Unit,
) {
    val targetFile = File(fileDirItem.path)
    when {
        isRestrictedSAFOnlyRoot(fileDirItem.path) -> {
            handleAndroidSAFDialog(fileDirItem.path) {
                if (!it) {
                    return@handleAndroidSAFDialog
                }

                val uri = getAndroidSAFUri(fileDirItem.path)
                if (!getDoesFilePathExist(fileDirItem.path)) {
                    createAndroidSAFFile(fileDirItem.path)
                }
                callback.invoke(applicationContext.contentResolver.openOutputStream(uri, "wt"))
            }
        }

        needsStupidWritePermissions(fileDirItem.path) -> {
            handleSAFDialog(fileDirItem.path) {
                if (!it) {
                    return@handleSAFDialog
                }

                var document = getDocumentFile(fileDirItem.path)
                if (document == null && allowCreatingNewFile) {
                    document = getDocumentFile(fileDirItem.getParentPath())
                }

                if (document == null) {
                    showFileCreateError(fileDirItem.path)
                    callback(null)
                    return@handleSAFDialog
                }

                if (!getDoesFilePathExist(fileDirItem.path)) {
                    document = getDocumentFile(fileDirItem.path) ?: document.createFile(
                        "", fileDirItem.name
                    )
                }

                if (document?.exists() == true) {
                    try {
                        callback(
                            applicationContext.contentResolver.openOutputStream(
                                document.uri, "wt"
                            )
                        )
                    } catch (e: FileNotFoundException) {
                        showErrorToast(e)
                        callback(null)
                    }
                } else {
                    showFileCreateError(fileDirItem.path)
                    callback(null)
                }
            }
        }

        isAccessibleWithSAFSdk30(fileDirItem.path) -> {
            handleSAFDialogSdk30(fileDirItem.path) {
                if (!it) {
                    return@handleSAFDialogSdk30
                }

                callback.invoke(
                    try {
                        val uri = createDocumentUriUsingFirstParentTreeUri(fileDirItem.path)
                        if (!getDoesFilePathExist(fileDirItem.path)) {
                            createSAFFileSdk30(fileDirItem.path)
                        }
                        applicationContext.contentResolver.openOutputStream(uri, "wt")
                    } catch (e: Exception) {
                        null
                    } ?: createCasualFileOutputStream(this, targetFile)
                )
            }
        }

        isRestrictedWithSAFSdk30(fileDirItem.path) -> {
            callback.invoke(
                try {
                    val fileUri = getFileUrisFromFileDirItems(arrayListOf(fileDirItem))
                    applicationContext.contentResolver.openOutputStream(fileUri.first(), "wt")
                } catch (e: Exception) {
                    null
                } ?: createCasualFileOutputStream(this, targetFile)
            )
        }

        else -> {
            callback.invoke(createCasualFileOutputStream(this, targetFile))
        }
    }


}

fun Activity.createCasualFileOutputStream(activity: Activity, targetFile: File): OutputStream? {
    if (targetFile.parentFile?.exists() == false) {
        targetFile.parentFile?.mkdirs()
    }

    return try {
        FileOutputStream(targetFile)
    } catch (e: Exception) {
        activity.showErrorToast(e)
        null
    }
}

fun Activity.handleSAFDialogSdk30(path: String, callback: (success: Boolean) -> Unit): Boolean {
    hideKeyboard()
    return if (!packageName.startsWith("com.messenger.phone.number")) {
        callback(true)
        false
    } else if (isShowingSAFDialogSdk30(path)) {
        BaseSimpleActivity.funAfterSdk30Action = callback
        true
    } else {
        callback(true)
        false
    }
}

fun Activity.isShowingSAFDialogSdk30(path: String): Boolean {
    return if (isAccessibleWithSAFSdk30(path) && !hasProperStoredFirstParentUri(path)) {
        runOnUiThread {
            if (!isDestroyed && !isFinishing) {
                val level = getFirstParentLevel(path)
                WritePermissionDialog(
                    this, WritePermissionDialog.Mode.OpenDocumentTreeSDK30(
                        path.getFirstParentPath(
                            this, level
                        )
                    )
                ) {
                    Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                        putExtra(EXTRA_SHOW_ADVANCED, true)
                        putExtra(
                            DocumentsContract.EXTRA_INITIAL_URI,
                            createFirstParentTreeUriUsingRootTree(path)
                        )
                        try {
                            startActivityForResult(this, OPEN_DOCUMENT_TREE_FOR_SDK_30)
                            checkedDocumentPath = path
                            return@apply
                        } catch (e: Exception) {
                            type = "*/*"
                        }

                        try {
                            startActivityForResult(this, OPEN_DOCUMENT_TREE_FOR_SDK_30)
                            checkedDocumentPath = path
                        } catch (e: ActivityNotFoundException) {
                            toastMess(R.string.system_service_disabled, Toast.LENGTH_LONG)
                        } catch (e: Exception) {
                            toastMess(R.string.unknown_error_occurred)
                        }
                    }
                }
            }
        }
        true
    } else {
        false
    }
}

fun Activity.updateMenuItemColors(
    menu: Menu?,
    baseColor: Int = getProperStatusBarColor(),
    forceWhiteIcons: Boolean = false,
) {
    if (menu == null) {
        return
    }

    var color = baseColor.getContrastColor()
    if (forceWhiteIcons) {
        color = Color.WHITE
    }

    for (i in 0 until menu.size()) {
        try {
            menu.getItem(i)?.icon?.setTint(color)
        } catch (ignored: Exception) {
        }
    }
}

fun Activity.handleOTGPermission(callback: (success: Boolean) -> Unit) {
    hideKeyboard()
    if (baseConfig.OTGTreeUri.isNotEmpty()) {
        callback(true)
        return
    }
    HomeABActivity.funAfterSAFPermission = callback
    WritePermissionDialog(this, WritePermissionDialog.Mode.Otg) {
        Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            try {
                startActivityForResult(this, OPEN_DOCUMENT_TREE_OTG)
                return@apply
            } catch (e: Exception) {
                type = "*/*"
            }

            try {
                startActivityForResult(this, OPEN_DOCUMENT_TREE_OTG)
            } catch (e: ActivityNotFoundException) {
                toastMess(R.string.system_service_disabled, Toast.LENGTH_LONG)
            } catch (e: Exception) {
                toastMess(R.string.unknown_error_occurred)
            }
        }
    }
}

fun Activity.isProperSDFolder(uri: Uri) = isExternalStorageDocument(uri) && !isInternalStorage(uri)

fun Activity.isInternalStorage(uri: Uri) =
    isExternalStorageDocument(uri) && DocumentsContract.getTreeDocumentId(uri).contains("primary")

fun Activity.isExternalStorageDocument(uri: Uri) =
    EXTERNAL_STORAGE_PROVIDER_AUTHORITY == uri.authority

fun Activity.isAndroidDir(uri: Uri) =
    isExternalStorageDocument(uri) && DocumentsContract.getTreeDocumentId(uri).contains(":Android")

fun Activity.isOTGAndroidDir(uri: Uri) = isProperOTGFolder(uri) && isAndroidDir(uri)
fun Activity.isSDAndroidDir(uri: Uri) = isProperSDFolder(uri) && isAndroidDir(uri)

fun Activity.isRootUri(uri: Uri) = uri.lastPathSegment?.endsWith(":") ?: false

fun Activity.isProperOTGRootFolder(uri: Uri) =
    isExternalStorageDocument(uri) && isRootUri(uri) && !isInternalStorage(uri)

fun Activity.isProperOTGFolder(uri: Uri) = isExternalStorageDocument(uri) && !isInternalStorage(uri)

fun Activity.isInternalStorageAndroidDir(uri: Uri) = isInternalStorage(uri) && isAndroidDir(uri)

fun Activity.isProperAndroidRoot(path: String, uri: Uri): Boolean {
    return when {
        isPathOnOTG(path) -> isOTGAndroidDir(uri)
        isPathOnSD(path) -> isSDAndroidDir(uri)
        else -> isInternalStorageAndroidDir(uri)
    }
}


fun Activity.setupDialogStuff(
    view: View,
    dialog: AlertDialog.Builder,
    titleId: Int = 0,
    titleText: String = "",
    cancelOnTouchOutside: Boolean = true,
    callback: ((alertDialog: AlertDialog) -> Unit)? = null,
) {
    if (isDestroyed || isFinishing) {
        return
    }

    // Theme-aware colors for dialogs
    val isSystemMode = ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM
    val forceDark = ThemeModeManager.isDarkThemeActive(this)

    val primaryColor = if (isSystemMode) {
        MaterialColors.getColor(view, R.attr.colorPrimary)
    } else {
        resources.getColor(R.color.appcolor, theme)
    }
    val titleColor = if (isSystemMode) {
        MaterialColors.getColor(view, com.google.android.material.R.attr.colorOnSurface)
    } else if (forceDark) {
        Color.WHITE
    } else {
        Color.BLACK
    }
    val bodyTextColor = titleColor
    val backgroundColor = getDialogBackgroundColor()
//    val backgroundColor = if (isSystemMode) {
//        MaterialColors.getColor(view, com.google.android.material.R.attr.colorSurface)
//    } else if (forceDark) {
//        resources.getColor(R.color.toolbarcolor3new)
//    } else {
//        resources.getColor(R.color.lock_edit_screen)
//    }
    if (view is ViewGroup) {
        updateTextColors(view)
    } else if (view is MyTextView) {
        view.setColors(bodyTextColor, primaryColor, backgroundColor)
    }

    if (dialog is MaterialAlertDialogBuilder) {
        dialog.create().apply {
            if (titleId != 0) {
                setTitle(titleId)
            } else if (titleText.isNotEmpty()) {
                setTitle(titleText)
            }
            setView(view)
            setCancelable(cancelOnTouchOutside)

            // Force Material light/dark surfaces as requested
            if (!isFinishing) {
                show()
            }
            window?.setBackgroundDrawable(ColorDrawable(backgroundColor))
            // Rounded corners via Material shape if available
            try {
                val surfaceColor = ColorStateList.valueOf(backgroundColor)
                val shapeAppearance = com.google.android.material.shape.ShapeAppearanceModel.builder()
                    .setAllCornerSizes(
                        resources.getDimension(com.simplemobiletools.commons.R.dimen.material_dialog_corner_radius)
                    )
                    .build()
                val materialBg = com.google.android.material.shape.MaterialShapeDrawable(shapeAppearance).apply {
                    fillColor = surfaceColor
                    this.elevation = 1f
                }
                window?.setBackgroundDrawable(materialBg)
            } catch (_: Exception) {
                // Fallback already applied above
            }

            // Title text (Material or platform IDs)
            val titleView =
                findViewById <TextView>(resources.getIdentifier("alertTitle", "id", "android"))
                    ?: findViewById(com.google.android.material.R.id.alertTitle)
            titleView?.setTextColor(titleColor)
            // Body text
            findViewById <com.google.android.material.textview.MaterialTextView>(android.R.id.message)?.setTextColor(bodyTextColor)
            // Button text → primary blue
            getButton(Dialog.BUTTON_POSITIVE)?.setTextColor(primaryColor)
            getButton(Dialog.BUTTON_NEGATIVE)?.setTextColor(primaryColor)
            getButton(Dialog.BUTTON_NEUTRAL)?.setTextColor(primaryColor)
            callback?.invoke(this)
        }
    } else {
        // Fallback for non-Material builders
        dialog.setView(view)
        if (titleId != 0) dialog.setTitle(titleId) else if (titleText.isNotEmpty()) dialog.setTitle(titleText)
        val alert = dialog.create()
        alert.setCancelable(cancelOnTouchOutside)
        if (!isFinishing) alert.show()
        alert.window?.setBackgroundDrawable(ColorDrawable(backgroundColor))
        try {


            val surfaceColor = ColorStateList.valueOf(backgroundColor)

            val shapeAppearance = com.google.android.material.shape.ShapeAppearanceModel.builder()
                .setAllCornerSizes(
                    resources.getDimension(com.simplemobiletools.commons.R.dimen.material_dialog_corner_radius)
                )
                .build()

            val materialBg = com.google.android.material.shape.MaterialShapeDrawable(shapeAppearance).apply {
                fillColor = surfaceColor
                elevation = 1f
            }

            alert.window?.setBackgroundDrawable(materialBg)
        } catch (_: Exception) {
        }
        val titleView =
            alert.findViewById <TextView>(resources.getIdentifier("alertTitle", "id", "android"))
                ?: alert.findViewById(com.google.android.material.R.id.alertTitle)
        titleView?.setTextColor(titleColor)
        alert.findViewById <com.google.android.material.textview.MaterialTextView>(android.R.id.message)?.setTextColor(bodyTextColor)
        alert.getButton(Dialog.BUTTON_POSITIVE)?.setTextColor(primaryColor)
        alert.getButton(Dialog.BUTTON_NEGATIVE)?.setTextColor(primaryColor)
        alert.getButton(Dialog.BUTTON_NEUTRAL)?.setTextColor(primaryColor)
        callback?.invoke(alert)
    }
}
fun Context.getMaterialDialogTheme(): Int {
    return if (isLightChatTheme()) {
        R.style.ThemeOverlay_Dialog_Light
    } else {
        R.style.ThemeOverlay_Dialog_Dark
    }
}

fun Context.isLightChatTheme(): Boolean {
    return ThemeModeManager.shouldUseLightSystemBars(this)
}

fun Context.getMaterialBottomSheetTheme(): Int {
    return if (isLightChatTheme()) {
        R.style.ThemeOverlay_BottomSheet_Light
    } else {
        R.style.ThemeOverlay_BottomSheet_Dark
    }
}

fun Activity.getTempFile(folderName: String, filename: String): File? {
    val folder = File(cacheDir, folderName)
    if (!folder.exists()) {
        if (!folder.mkdir()) {
            toast(R.string.unknown_error_occurred)
            return null
        }
    }

    return File(folder, filename)
}

fun Activity.dialNumber(phoneNumber: String, callback: (() -> Unit)? = null) {
    hideKeyboard()
    Intent(Intent.ACTION_DIAL).apply {
        data = Uri.fromParts("tel", phoneNumber, null)

        try {
            startActivity(this)
            callback?.invoke()
        } catch (e: ActivityNotFoundException) {
            toast(R.string.no_app_found)
        } catch (e: Exception) {
            showErrorToast(e)
        }
    }
}

fun Activity.getchatincolor(): Int {
    return if (ThemeModeManager.isDarkThemeActive(this)) {
        R.color.unselectmessagein_2
    } else {
        R.color.unselectmessagein
    }
}

fun Activity.getchatoutcolor(): Int {
    return if (ThemeModeManager.isDarkThemeActive(this)) {
        R.color.unselectmessageout_3
    } else {
        R.color.unselectmessageout
    }
}

fun Activity.getchatouttxtcolor(): Int {
    return if (ThemeModeManager.isDarkThemeActive(this)) {
        R.color.messageoutcolor_3
    } else {
        R.color.messageoutcolor_1
    }
}

fun Activity.getchatIntxtcolor(): Int {
    return if (ThemeModeManager.isDarkThemeActive(this)) {
        R.color.messageoutcolor_3
    } else {
        R.color.messageoutcolor_1
    }
}

fun Activity.getchatpreIntxtdatecolor(): Int {
    return if (ThemeModeManager.isDarkThemeActive(this)) {
        R.color.chatpreintxtdatecolor_3
    } else {
        R.color.chatpreintxtdatecolor_1
    }
}


fun Activity.getchatpreouttxtdatecolor(): Int {
    return if (ThemeModeManager.isDarkThemeActive(this)) {
        R.color.chatpreouttxtdatecolor_3
    } else {
        R.color.chatpreouttxtdatecolor_1
    }
}

suspend fun Activity.isContactSaved(phoneNumber: String): Boolean = withContext(Dispatchers.IO) {
    val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
    val cursor: Cursor? = contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        projection,
        "${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?",
        arrayOf(phoneNumber),
        null
    )
    cursor?.use {
        if (it.moveToFirst()) {
            return@withContext true
        }
    }
    return@withContext false
}

var isTaskExecuted = false
suspend fun getFilePathFromUri(context: Context, uri: Uri): String? {
    return withContext(Dispatchers.IO) {
        var filePath: String? = null

        // Check if the URI is from the DocumentsProvider
        if (uri.authority?.startsWith("com.android.providers.media.documents") == true) {
            val docId = uri.lastPathSegment?.split(":")?.get(1)
            val selection = "${MediaStore.Audio.Media._ID} = ?"
            val selectionArgs = arrayOf(docId)

            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, selection, selectionArgs, null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                    filePath = cursor.getString(pathIndex)
                }
            }
        } else if (uri.scheme == "file") {
            // Handle file scheme
            filePath = uri.path
        } else if (uri.scheme == "content") {
            // Handle other content URIs
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val pathIndex = cursor.getColumnIndexOrThrow("_data")
                    filePath = cursor.getString(pathIndex)
                }
            }
        }

        filePath
    }
}

suspend fun Activity.copyAudioFile(sourceFilePath: String): File? = withContext(Dispatchers.IO) {
    val ringtoneFolder = createRingtoneFolder(this@copyAudioFile)
    val sourceFile = File(sourceFilePath)

    // Define the destination file in the Ringtone folder
    val destinationFile = File(ringtoneFolder, sourceFile.name)

    return@withContext try {
        FileInputStream(sourceFile).use { input ->
            FileOutputStream(destinationFile).use { output ->
                val buffer = ByteArray(1024)
                var length: Int
                while (input.read(buffer).also { length = it } > 0) {
                    output.write(buffer, 0, length)
                }
            }
        }
        destinationFile
    } catch (e: IOException) {
        e.printStackTrace()
        Log.d("", "onActivityResult: <-----------> file error ${e.localizedMessage}")
        null
    }
}

fun getColoredGroupIconNew(title: String, context: Context): Drawable {
    val icon = context.resources.getDrawable(R.drawable.ic_group_circle_bg_new)
    val bgColor =
        com.simplemobiletools.commons.helpers.letterBackgroundColors[Math.abs(title.hashCode()) % com.simplemobiletools.commons.helpers.letterBackgroundColors.size].toInt()
    (icon as LayerDrawable).findDrawableByLayerId(R.id.attendee_circular_background)
        .applyColorFilter(bgColor.adjustAlpha(0.3f))
    return icon
}

suspend fun Activity.createRingtoneFolder(context: Context): File = withContext(Dispatchers.IO) {
    // Define the Ringtone folder
    val ringtoneFolder = File(Environment.getExternalStorageDirectory(), "Documents/Ringtone")

    // Create the folder if it doesn't exist
    if (!ringtoneFolder.exists()) {
        ringtoneFolder.mkdirs()
    }

    return@withContext ringtoneFolder
}

suspend fun Activity.getFilePathAndName(file: File): Pair<String, String> =
    withContext(Dispatchers.IO) {
        return@withContext Pair(file.absolutePath, file.name)
    }
