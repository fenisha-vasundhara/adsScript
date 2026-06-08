package com.messenger.phone.number.text.sms.service.apps.CommanClass

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlarmManager
import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.database.Cursor
import android.database.sqlite.SQLiteException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.media.AudioAttributes
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.BlockedNumberContract
import android.provider.ContactsContract
import android.provider.ContactsContract.PhoneLookup
import android.provider.OpenableColumns
import android.provider.Telephony
import android.telephony.PhoneNumberUtils
import android.telephony.SmsMessage
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.text.format.Time
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.demo.adsmanage.Activity.PayAllSubsciptionExterimentActivity
import com.demo.adsmanage.Activity.SubActivityTwoplanActivity
import com.demo.adsmanage.Activity.Paywall_FourPlan_Activity
import com.demo.adsmanage.Activity.TimerOfferActivity
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.firebaseEventMain
import com.demo.adsmanage.Commen.log
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.demo.adsmanage.helper.GlobalTimeModel
import com.google.android.material.color.MaterialColors
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.klinker.android.send_message.Settings
import com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.Receiver.ScheduledRemainderReceiver
import com.messenger.phone.number.text.sms.service.apps.BuildConfig
//import com.messenger.phone.number.text.sms.service.apps.MyWidget.MyWidgetProvider
import com.messenger.phone.number.text.sms.service.apps.Notification.NootificationBubble
import com.messenger.phone.number.text.sms.service.apps.Notification.NotificationHelper
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.WelcomeActivity
import com.messenger.phone.number.text.sms.service.apps.ThredCallback.isOnMainThread
import com.messenger.phone.number.text.sms.service.apps.data.AddressBookContact
import com.messenger.phone.number.text.sms.service.apps.data.Attachment
import com.messenger.phone.number.text.sms.service.apps.data.AttachmentUtils.parseAttachmentNames
import com.messenger.phone.number.text.sms.service.apps.data.BaseConfig
import com.messenger.phone.number.text.sms.service.apps.data.BlockedNumber
import com.messenger.phone.number.text.sms.service.apps.data.Config
import com.messenger.phone.number.text.sms.service.apps.data.ConversationNew
import com.messenger.phone.number.text.sms.service.apps.data.Message
import com.messenger.phone.number.text.sms.service.apps.data.MessageAttachment
import com.messenger.phone.number.text.sms.service.apps.data.MyContactsContentProvider
import com.messenger.phone.number.text.sms.service.apps.data.NamePhoto
import com.messenger.phone.number.text.sms.service.apps.data.PhoneNumber
import com.messenger.phone.number.text.sms.service.apps.data.SimpleContact
import com.messenger.phone.number.text.sms.service.apps.data.SimpleContactsHelper
import com.messenger.phone.number.text.sms.service.apps.data.getIntValue
import com.messenger.phone.number.text.sms.service.apps.data.messaging.MessagingUtils
import com.messenger.phone.number.text.sms.service.apps.data.messaging.SmsException
import com.messenger.phone.number.text.sms.service.apps.data.messaging.SmsException.Companion.EMPTY_DESTINATION_ADDRESS
import com.messenger.phone.number.text.sms.service.apps.data.messaging.SmsException.Companion.ERROR_PERSISTING_MESSAGE
import com.messenger.phone.number.text.sms.service.apps.data.messaging.SmsException.Companion.ERROR_SENDING_MESSAGE
import com.messenger.phone.number.text.sms.service.apps.data.messaging.SmsSender
import com.messenger.phone.number.text.sms.service.apps.firebase.FirebaseRemoteModel
import com.messenger.phone.number.text.sms.service.apps.helperClass.NetworkHelper
import com.messenger.phone.number.text.sms.service.apps.modelClass.Contact
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.MessageTraslationModel
import com.messenger.phone.number.text.sms.service.apps.modelClass.Remindermodel
import com.messenger.phone.number.text.sms.service.apps.services.AutoFullAppLockUnlockReceiver
import com.messenger.phone.number.text.sms.service.apps.services.AutoMessagedeleteReceiver
import com.messenger.phone.number.text.sms.service.apps.services.AutoNotificationReceiver
import com.messenger.phone.number.text.sms.service.apps.services.NewMessageTraslateMessageReceiver
import com.messenger.phone.number.text.sms.service.apps.services.OtpMessagedeleteReceiver
import com.messenger.phone.number.text.sms.service.apps.services.ScheduledMessageReceiver
import com.messenger.phone.number.text.sms.service.apps.subscription.Paywall_dynamic_Activity
import com.simplemobiletools.commons.extensions.getContrastColor
import com.simplemobiletools.commons.extensions.getLongValue
import com.simplemobiletools.commons.extensions.getMyContactsCursor
import com.simplemobiletools.commons.extensions.getNameLetter
import com.simplemobiletools.commons.extensions.getPermissionString
import com.simplemobiletools.commons.extensions.getStringValue
import com.simplemobiletools.commons.extensions.hasPermission
import com.simplemobiletools.commons.extensions.normalizeString
import com.simplemobiletools.commons.extensions.showErrorToast
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.extensions.windowManager
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import com.simplemobiletools.commons.helpers.isOreoPlus
import com.simplemobiletools.commons.views.MyAppCompatCheckbox
import com.simplemobiletools.commons.views.MyAppCompatSpinner
import com.simplemobiletools.commons.views.MyAutoCompleteTextView
import com.simplemobiletools.commons.views.MyButton
import com.simplemobiletools.commons.views.MyCompatRadioButton
import com.simplemobiletools.commons.views.MyEditText
import com.simplemobiletools.commons.views.MyFloatingActionButton
import com.simplemobiletools.commons.views.MySeekBar
import com.simplemobiletools.commons.views.MyTextInputLayout
import com.simplemobiletools.commons.views.MyTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.jsoup.Jsoup
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.text.DecimalFormat
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Locale.getDefault
import java.util.regex.Pattern
import kotlin.jvm.java
import kotlin.math.abs
import kotlin.random.Random


var ZopIconget = 1
var isLanguageChanging = false
//var astrozopLink = "https://8920.read.astrozop.com/"
//var criczopLink = "https://10203.read.criczop.com/"

var astrozopLink = "https://10724.read.astrozop.com/"
var criczopLink = "https://10723.play.gamezop.com/"


fun openCustomTab(activity: Context, link: String) {
    val builder = CustomTabsIntent.Builder()
    builder.setShareState(CustomTabsIntent.SHARE_STATE_ON)
    val color = ContextCompat.getColor(activity, R.color.L1)
    val colorParams = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(color)
        .setSecondaryToolbarColor(color)
        .build()
    builder.setDefaultColorSchemeParams(colorParams)
    val customTabsIntent = builder.build()
    try {
        customTabsIntent.launchUrl(activity, link.toUri())
    } catch (e: Exception) {
        fallbackToStandardBrowser(activity, link)
    }
}

fun fallbackToStandardBrowser(activity: Context, link: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, link.toUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


var onlyfinish = false


var call = true

var totalRevenue = 0.0
var impressionCount = 0
val decimalFormat = DecimalFormat("0.00000") // 5 decimal places, no scientific notation

fun ArrayList<com.simplemobiletools.commons.models.SimpleContact>.getAddressesNew() =
    flatMap { it.phoneNumbers }.map { it.normalizedNumber }

var interadscall = false

var isfromenotification = false

var callDuration: String = ""
var notify_vaule_New: String = "jigar"
var isfromnotification_New = true
var formattedTime: String = ""
var incomingCallTime: Long = 0
var incomingCallNumber: String? = null
var callStartTime: Long = 0


var sendmessagebuttondefaultset = false

var filterdialogselection = "defaultBtn"
var selecteddateinmillisecound = 0L
var formateddate = ""
var rangeselected = false

var systemfountstart = false
var SystemGeneratedIconSwitchAb = false
var systemfountstartsetting = false

var isThirdPartyIntentCheck = false

var albumes: HashMap<File, List<File>>? = null
var phoneNumber: String? = ""
var RightColor: Int = 0
var RightSrc: Int = 0
var LeftColor: Int = 0
var LeftSrc: Int = 0

var issendmessasgescreensearchstatrt = false
var higlitetxtstart = ""

var firebaseConfig = FirebaseRemoteModel()

var mainactivityfinish = false

const val ATTACHMENT_DOCUMENT = 7
const val ATTACHMENT_MEDIA = 8
const val ATTACHMENT_VCARD = 9

const val FILE_SIZE_NONE = -1L
const val FILE_SIZE_100_KB = 102_400L
const val FILE_SIZE_200_KB = 204_800L
const val FILE_SIZE_300_KB = 307_200L
const val FILE_SIZE_600_KB = 614_400L
const val FILE_SIZE_1_MB = 1_048_576L
const val FILE_SIZE_2_MB = 2_097_152L

var isdefault = true

// permissions
val ARR_PERMS = arrayOf(
    Manifest.permission.READ_SMS,
    Manifest.permission.SEND_SMS,
    Manifest.permission.READ_CONTACTS,
    Manifest.permission.WRITE_CONTACTS
)


var textdialogopen = false

var tredidIntent = 0L
var nameIntent = ""
var mobileNumberIntent = ""
var isgroupmessageIntent = false

const val PERMISSION_READ_CONTACTS = 5
const val EXTRA_NUMBER = "SENDER"
const val EXTRA_MESSAGE_TEXT = "MESSAGE_TEXT"
const val PERMISSION_REQUEST = 100
const val EXPORT_SMS = "export_sms"
var groupnamecustom: String? = null
const val EXPORT_MMS = "export_mms"
const val ratingdialogshow_pref = "ratingdialogshow_pref"
const val defprivacyaccept_pref = "defprivacyaccept_pref"
const val CONTACT_INSERT_EDIT_REQUEST_CODE = 1001 // Define your request code here
const val CONTACT_EDIT_REQUEST_CODE = 101
const val FONT_SIZE = "font_size"
const val dialogshowcount_pref = "dialogshowcount_pref"
const val PICK_PHOTO_INTENT = 42
const val PICK_VIDEO_INTENT = 49
const val PICK_VIDEO_AND_IMAGE_INTENT = 9925
const val PICK_SAVE_FILE_INTENT = 43
const val CAPTURE_PHOTO_INTENT = 44
const val CAPTURE_VIDEO_INTENT = 45
const val CAPTURE_AUDIO_INTENT = 46
const val PICK_DOCUMENT_INTENT = 47
const val PICK_CONTACT_INTENT = 48
const val FONT_SIZE_SELECTION = "font_size_selection"
const val line_selectionpref = "line_selectionpref"
const val theme_mode_pref = "theme_mode_pref"
const val messagecornerpref = "messagecornerpref"
const val imageAlphapref = "imageAlphapref"
const val SOFT_KEYBOARD_HEIGHT = "soft_keyboard_height"
const val introshowpref = "introshow"
const val tutorialshowpref = "tutorialshowpref"
const val privatechattutorialshowpref = "privatechattutorialshowpref"
const val consettingtutorialshowpref = "consettingtutorialshowpref"
const val autoreplyshowpref = "autoreplyshowpref"
const val whatnewbuttonshowpref = "whatnewbuttonshowpref"
const val whatnewbuttonshowpreftwo = "whatnewbuttonshowpref"
const val isotpdeletesetpref = "isotpdeletesetpref"
const val maliciousWebsiteBtnSwichPpref = "maliciousWebsiteBtnSwichPpref"
const val isendtoendencrteptedpref = "isendtoendencrteptedpref"
const val notificationshowcasepref = "notificationshowcasepref"
const val isdringmodeonepref = "isdringmodeonepref"
const val isinappbrowserpref = "isinappbrowserpref"
const val isnotificationshowpref = "isnotificationshowpref"
const val iswakescreenpref = "iswakescreenpref"
const val isdeliveryconfirmationpref = "isdeliveryconfirmationpref"
const val userpreferencelanguagepref = "userpreferencelanguagepref"
const val userpreferenceSignaturepref = "userpreferenceSignaturepref"
const val userpreferenceAutoMessageDeletepref = "userpreferenceAutoMessageDeletepref"
const val isAutoNotificationStartpref = "isAutoNotificationStartpref"
const val userpreferencelanguageCodepref = "userpreferencelanguageCodepref"
const val userpreferenceSignatureOnOffpref = "userpreferenceSignatureOnOffpref"
const val firsttimeusersetlangpref = "firsttimeusersetlangpref"
const val userfirsttimeopenapppref = "userfirsttimeopenapppref"
const val secondshowcaseshowforonlysettingpref = "secondshowcaseshowforonlysettingpref"
const val thirdshowcaseshowforprivacyandsecuritypref = "thirdshowcaseshowforprivacyandsecuritypref"
const val forthdshowcaseshowfordrivingmodepref = "forthdshowcaseshowfordrivingmodepref"
const val fifthshowcaseshowforsignaturepref = "fifthshowcaseshowforsignaturepref"
const val sixshowcaseshowforinappbrowserpref = "sixshowcaseshowforinappbrowserpref"
const val sevenshowcaseshowforcharactercountpref = "sevenshowcaseshowforcharactercountpref"
const val eightshowcaseshowforsendgropmessagepref = "eightshowcaseshowforsendgropmessagepref"
const val nineshowcaseshowforsendlongmessagepref = "nineshowcaseshowforsendlongmessagepref"
const val tenshowcaseshowforremoveaccentspref = "tenshowcaseshowforremoveaccentspref"
const val elevenshowcaseshowforremoveaccentspref = "elevenshowcaseshowforremoveaccentspref"
const val usershowsendmessageshowcasepref = "usershowsendmessageshowcasepref"
const val usershowviewdetailsshowcasepref = "usershowviewdetailsshowcasepref"
const val isprivacychatscreenopenpref = "isprivacychatscreenopenpref"
const val isapplockchatscreenopenpref = "isapplockchatscreenopenpref"
const val isprivacyandsecscreenopenpref = "isprivacyandsecscreenopenpref"
const val isblocknumberscreenopenpref = "isblocknumberscreenopenpref"
const val ismanageblocknumberscreenopenpref = "ismanageblocknumberscreenopenpref"
const val isdriwingmodescreenopenpref = "isdriwingmodescreenopenpref"
const val isbackupandrestorescreenopenpref = "isbackupandrestorescreenopenpref"
const val isschedulemessagescreenopenpref = "isschedulemessagescreenopenpref"
const val iscolorthemescreenopenpref = "iscolorthemescreenopenpref"
const val isswipemotionscreenopenpref = "isswipemotionscreenopenpref"
const val isapplangscreenopenpref = "isapplangscreenopenpref"
const val isnotificationscreenopenpref = "isnotificationscreenopenpref"
const val showfullsettingshowcasepref = "showfullsettingshowcasepref"
const val appopenandshowcaseshowpref = "appopenandshowcaseshowpref"
const val fragmentshowcasecoutpref = "fragmentshowcasecoutpref"
const val customwallpaperselectedpref = "customwallpaperselectedpref"
const val toolbarcolorcustomwallpaperpref = "toolbarcolorcustomwallpaperpref"
const val backgroundcolorcustomwallpaperpref = "backgroundcolorcustomwallpaperpref"
const val backgroundcolorcustomwallpaperabpref = "backgroundcolorcustomwallpaperabpref"
const val inmessagecolorcustomwallpaperpref = "inmessagecolorcustomwallpaperpref"
const val inmessagecolorcustomwallpaperABpref = "inmessagecolorcustomwallpaperABpref"
const val outmessagecolorcustomwallpaperpref = "outmessagecolorcustomwallpaperpref"
const val outmessagecolorcustomwallpaperABpref = "outmessagecolorcustomwallpaperABpref"
const val iscustomgalleryimagesetpref = "iscustomgalleryimagesetpref"
const val smartreplycolorpref = "smartreplycolorpref"
const val iscustomwallopaperscreenopenpref = "iscustomwallopaperscreenopenpref"
const val ismessagetextsizescreenopenpref = "ismessagetextsizescreenopenpref"
const val ismessagecornerscreenopenpref = "ismessagecornerscreenopenpref"
const val istraslatescreenopenpref = "istraslatescreenopenpref"
var fromnotificationautoclick = "no"
var openautoclick = false
const val is2steplocktypepref = "is2steplocktypepref"

val PREFS_NAME = "MyPrefsFile"
val DATE_KEY = "currentDate"
val DATE_KEY_privacy = "DATE_KEY_privacy"
const val userfivedaymilisecoundpref = "userfivedaymilisecoundpref"
const val SEND_LONG_MESSAGE_MMS = "send_long_message_mms"
const val SEND_GROUP_MESSAGE_MMS = "send_group_message_mms"
const val USE_SIMPLE_CHARACTERS = "use_simple_characters"
const val BLOCKED_KEYWORDS = "blocked_keywords"
const val tabListpref = "tabListpref"
const val AllMessageListpref = "AllMessageListpref"

var changelang = ""
var setWallpaperdone = false

const val notificationpriviewselected = "notificationpriviewselected"
const val allChatDefault_pref = "allChatWallpaperPath_pref"
const val isAllChatDefaultColor_pref = "isAllChatDefaultColor_pref"
const val isadativebennarload_pref = "isadativebennarload_pref"
const val isadativebennarloadlang_pref = "isadativebennarloadlang_pref"
const val isAllChatWallpaper_pref = "isAllChatWallpaper_pref"
const val isAllChatColor_pref = "isAllChatColor_pref"
const val SolidColorlistpref = "SolidColorlistpref"
const val Swipe_Right_pref = "Swipe_Right_pref"
const val NotiButton1_pref = "NotiButton1_pref"
const val NotiButton3_pref = "NotiButton3_pref"
const val NotiButton2_pref = "NotiButton2_pref"
const val drivingmodemessagepref = "drivingmodemessagepref"
const val EVER_SHOWN_FOLDERS = "ever_shown_folders"
const val INCLUDED_FOLDERS = "included_folders"
const val isprivatelocktypepref = "isprivatelocktypepref"
const val isprivatelockpattenpref = "isprivatelockpattenpref"
const val is2steplockpattenpref = "is2steplockpattenpref"
const val SHOW_CHARACTER_COUNTER = "show_character_counter"
const val MMS_FILE_SIZE_LIMIT = "mms_file_size_limit"
const val Swipe_Left_pref = "Swipe_Left_pref"
const val userfisttimeshowsubscreen_pref = "userfisttimeshowsubscreen_pref"
const val usertrailscreenshow_pref = "usertrailscreenshow_pref"
const val Lock_Screen_Pin_pref = "Lock_Screen_Pin_pref"
const val Lock_Screen_Sec_Question_pref_pref = "Lock_Screen_Sec_Question_pref"
const val Password_reset_email_id_pref = "Password_reset_email_id_pref"
const val Full_AppLock_Pin_pref = "Full_AppLock_Pin_pref"
const val Full_AppLock_Sec_Question_pref = "Full_AppLock_Sec_Question_pref"
const val Message_full_App_Font_Style_pref = "Message_full_App_Font_Style_pref"
const val Message_Send_Activity_pref = "Message_Send_Activity_pref"
const val Message_Home_screen_Native_Ad_Show_pref = "Message_Home_screen_Native_Ad_Show_pref"
const val Language_Screen_banner_vs_native_pref = "Language_Screen_banner_vs_native_pref"
const val Language_Screen_banner_vs_native_show_or_hide_pref =
    "Language_Screen_banner_vs_native_show_or_hide_pref"
const val Message_Language_Screen_Ad_Show_or_Hide_pref =
    "Message_Language_Screen_Ad_Show_or_Hide_pref"
const val Message_Home_screen_Native_Ad_AB_pref = "Message_Home_screen_Native_Ad_AB_pref"
const val Message_Home_screen_Native_Ad_Card_And_Normal_pref =
    "Message_Home_screen_Native_Ad_Card_And_Normal_pref"
const val Full_AppLock_Sec_Question_Ans_pref = "Full_AppLock_Sec_Question_Ans_pref"
const val Lock_Screen_Sec_Question_Ans_pref = "Lock_Screen_Sec_Question_Ans_pref"
const val SelectedLanguagepref = "SelectedLanguagepref"
var blockcontectremove: ArrayList<Conversation> = arrayListOf()
const val PINNED_CONVERSATIONS = "pinned_conversations"
const val CotegorySetPref = "CotegorySetPref"
const val IMPORT_SMS = "import_sms"
const val IMPORT_MMS = "import_mms"
var MessageType = "All Messages"


var islinechange = false

var isLoadingAd: Boolean = false


val drawableCache = HashMap<String, Drawable>()
val drawableCachecolor = HashMap<String, Int>()
val drawableCache2 = HashMap<String, Drawable>()

var isfromprofile = false

//var isShowingAd: Boolean = false
//var outApp: Boolean = false
//var isGoOutSide: Boolean = false
var isfisttimead = false
var isfisttimeadopen = false
var ismessageforvard = true
var ismessageloading = true
var isappfistopen = true
var isfistcreateTimer = true
var isfistcreatdatabase = true
var isfisttimepfermision = true
var isfastcrollerishide = false
var isSerchfoundmessage = false
var CAT_VAULE: String? = null
var showsettingshowcase = false
var showsettingshowcaseprivacy = false
var showsettingshowcasedriving = false
var showsettingshowcasefifth = false

var showsettingshowcasesix = false
var showsettingshowcaseseven = false
var showsettingshowcaseeight = false
var showsettingshowcasenine = false
var showsettingshowcaseten = false
var showsettingshowcaseeleven = false


var notificationallbuttonshowcaseshow = false
var privacyallbuttonshowcaseshow = false
var drivingallbuttonshowcaseshow = false

var isfirsttimeopen = true

var archiveoldpos: Long = -1

var messageNotify = "-1"
//var fragmentshowcasecout = 1

var selectedCatList = ArrayList<String>()
var selectedConList = ArrayList<SimpleContact>()
var isselectContact: Boolean = false
var issimcardavailable: Boolean = false
var selectedContactList = ArrayList<String>()
var categoryContactlistAdapterList = ArrayList<String>()
var starMassageAdapterOnClickList = ArrayList<String>()
var selectedContactListdb = ArrayList<String>()
var allresultlist: ArrayList<Conversation> = arrayListOf()
var allresultlistcontact: ArrayList<Contact> = arrayListOf()
var higlitetetxt = ""
var isexportstart = false
var isimportstart = false
var totalmessagecount = 0
var totalgetmessage = 0

var fromnotificationAction = false
var fromnotificationActionFragment = "Nothing Set"
var alldonesetting = false

var deletedmessage: ArrayList<Conversation> = arrayListOf()


var subScreenopen = false

fun Context.firebaseEvent(activityName: String, key: String) {
//    val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
//    val params = Bundle().apply {
//        putString("activity_name", activityName)
//        putString("button_name", key)
//    }
//    "firebaseEvent <------------> ${activityName}_MS_click_${key}".log()
//    firebaseAnalytics.logEvent("${activityName}_MS_click_${key}", params)
}


fun Context.showToast(msg: String, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, msg, duration).show()

fun drawableToBitmap(drawable: Drawable): Bitmap? {
    var bitmap: Bitmap? = null
    if (drawable is BitmapDrawable) {
        val bitmapDrawable = drawable
        if (bitmapDrawable.bitmap != null) {
            return bitmapDrawable.bitmap
        }
    }
    bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
        Bitmap.createBitmap(
            1,
            1,
            Bitmap.Config.ARGB_8888
        ) // Single color bitmap will be created of 1x1 pixel
    } else {
        Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
    }
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

fun Context.getContactLetterIcon(name: String, phonenumber: String): Bitmap {
    val letter = name.getNameLetter()
    val size = resources.getDimension(R.dimen.normal_icon_size).toInt()
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val view = TextView(this)
    view.layout(0, 0, size, size)

    val circlePaint = Paint().apply {
        color =
            com.simplemobiletools.commons.helpers.letterBackgroundColors[Math.abs(name.hashCode()) % com.simplemobiletools.commons.helpers.letterBackgroundColors.size].toInt()
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

@SuppressLint("Range", "Recycle")
fun fetchContactIdFromPhoneNumber(phoneNumber: String?, context: Context): String {
    try {
        val permission = Manifest.permission.READ_CONTACTS
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            return if (phoneNumber == null) {
                "0"
            } else {
                phoneNumber
            }
        }
        if (phoneNumber == null) {
            return "00"
        }
        val uri = Uri.withAppendedPath(
            PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber)
        )
        val cursor: Cursor? = context.contentResolver.query(
            uri, arrayOf(PhoneLookup.DISPLAY_NAME, PhoneLookup._ID), null, null, null
        )
        var contactId: String? = ""
        if (cursor?.moveToFirst() == true) {
            do {
                contactId = cursor.getString(
                    cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME)
                )
            } while (cursor.moveToNext())
        }
        return if (contactId?.isEmpty() == true) {
            phoneNumber
        } else {
            contactId.toString()
        }
    } catch (e: Exception) {
        return "0"
    }

}

fun String.normalizeStringnew() =
    Normalizer.normalize(this, Normalizer.Form.NFD).replace(normalizeRegex, "")

val normalizeRegex = "\\p{InCombiningDiacriticalMarks}+".toRegex()

fun Activity.hideSystemUI() = run {
    window.decorView.systemUiVisibility =
        (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
    window.statusBarColor = Color.TRANSPARENT
}

fun shareApp(context: Context) {
    Constants.isActivitychange = true
    Constants.isAdsClicking = true
    try {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
        var shareMessage =
            "Stay connected with your loved ones anytime, anywhere! Enjoy the freedom of sending unlimited messages to your contacts. Download our app now and keep the conversation going.\n\n"
        shareMessage =
            shareMessage + "Install Now :- " + "https://play.google.com/store/apps/details?id=" + context.packageName
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        context.startActivity(Intent.createChooser(shareIntent, "choose one"))
    } catch (e: java.lang.Exception) { //e.toString();
    }
}

fun isDualSimDevice(context: Context): Boolean {
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return telephonyManager.phoneCount >= 2
}

fun isSimCardAvailable(context: Context): Boolean {
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return telephonyManager.simState == TelephonyManager.SIM_STATE_READY
}

fun getPhotoUri(contactId: Long, context: Context): Uri? {
    val contentResolver: ContentResolver = context.contentResolver
    try {
        val cursor = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            null,
            ContactsContract.Data.CONTACT_ID + "=" + contactId + " AND " + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'",
            null,
            null
        )
        if (cursor != null) {
            if (!cursor.moveToFirst()) {
                return null // no photo
            }
        } else {
            return null // error in cursor process
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
    val person = ContentUris.withAppendedId(
        ContactsContract.Contacts.CONTENT_URI, contactId
    )
    return Uri.withAppendedPath(
        person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY
    )
}

fun getPhotoUriFromPhoneNumber(number: String, context: Context): String {
    if (!context.hasPermission(PERMISSION_READ_CONTACTS)) {
        return ""
    }

    val uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
    val projection = arrayOf(
        PhoneLookup.PHOTO_URI
    )

    try {
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor.use {
            if (cursor?.moveToFirst() == true) {
                return cursor.getStringValue(PhoneLookup.PHOTO_URI) ?: ""
            }
        }
    } catch (ignored: Exception) {
    }

    return ""
}

@SuppressLint("Range")
fun Cursor.getStringValue(key: String) = getString(getColumnIndex(key))

fun Context.hasPermissionnew(permId: Int) = ContextCompat.checkSelfPermission(
    this,
    getPermissionString(permId)
) == PackageManager.PERMISSION_GRANTED

fun Context.getPermissionString(id: Int) = when (id) {
    PERMISSION_READ_STORAGE -> Manifest.permission.READ_EXTERNAL_STORAGE
    PERMISSION_WRITE_STORAGE -> Manifest.permission.WRITE_EXTERNAL_STORAGE
    PERMISSION_CAMERA -> Manifest.permission.CAMERA
    PERMISSION_RECORD_AUDIO -> Manifest.permission.RECORD_AUDIO
    PERMISSION_READ_CONTACTS -> Manifest.permission.READ_CONTACTS
    PERMISSION_WRITE_CONTACTS -> Manifest.permission.WRITE_CONTACTS
    PERMISSION_READ_CALENDAR -> Manifest.permission.READ_CALENDAR
    PERMISSION_WRITE_CALENDAR -> Manifest.permission.WRITE_CALENDAR
    PERMISSION_CALL_PHONE -> Manifest.permission.CALL_PHONE
    PERMISSION_READ_CALL_LOG -> Manifest.permission.READ_CALL_LOG
    PERMISSION_WRITE_CALL_LOG -> Manifest.permission.WRITE_CALL_LOG
    PERMISSION_GET_ACCOUNTS -> Manifest.permission.GET_ACCOUNTS
    PERMISSION_READ_SMS -> Manifest.permission.READ_SMS
    PERMISSION_SEND_SMS -> Manifest.permission.SEND_SMS
    PERMISSION_READ_PHONE_STATE -> Manifest.permission.READ_PHONE_STATE
    PERMISSION_MEDIA_LOCATION -> if (isQPlus()) Manifest.permission.ACCESS_MEDIA_LOCATION else ""
    PERMISSION_POST_NOTIFICATIONS -> Manifest.permission.POST_NOTIFICATIONS
    PERMISSION_READ_MEDIA_IMAGES -> Manifest.permission.READ_MEDIA_IMAGES
    PERMISSION_READ_MEDIA_VIDEO -> Manifest.permission.READ_MEDIA_VIDEO
    PERMISSION_READ_MEDIA_AUDIO -> Manifest.permission.READ_MEDIA_AUDIO
    else -> ""
}

const val PERMISSION_READ_STORAGE = 1
const val PERMISSION_WRITE_STORAGE = 2
const val PERMISSION_CAMERA = 3
const val PERMISSION_RECORD_AUDIO = 4
const val PERMISSION_WRITE_CONTACTS = 6
const val PERMISSION_READ_CALENDAR = 7
const val PERMISSION_WRITE_CALENDAR = 8
const val PERMISSION_CALL_PHONE = 9
const val PERMISSION_READ_CALL_LOG = 10
const val PERMISSION_WRITE_CALL_LOG = 11
const val PERMISSION_GET_ACCOUNTS = 12
const val PERMISSION_READ_SMS = 13
const val PERMISSION_SEND_SMS = 14
const val PERMISSION_READ_PHONE_STATE = 15
const val PERMISSION_MEDIA_LOCATION = 16
const val PERMISSION_POST_NOTIFICATIONS = 17
const val PERMISSION_READ_MEDIA_IMAGES = 18
const val PERMISSION_READ_MEDIA_VIDEO = 19
const val PERMISSION_READ_MEDIA_AUDIO = 20

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
fun isQPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q


fun setisDualsim(a: Boolean, context: Context) {
    val editor = context.getSharedPreferences("setisDualsim", Context.MODE_PRIVATE).edit()
    editor.putBoolean("setisDualsim", a)
    editor.apply()
}

fun getisDualsim(context: Context): Boolean {
    val prefs2 = context.getSharedPreferences("setisDualsim", Context.MODE_PRIVATE)
    val b = prefs2.getBoolean("setisDualsim", false)
    return b
}

fun setMessageSendSim(a: Int = 3, context: Context) {
    val editor = context.getSharedPreferences("setMessageSendSim", Context.MODE_PRIVATE).edit()
    editor.putInt("setMessageSendSim", a)
    editor.apply()
}

fun getMessageSendSim(context: Context): Int {
    val prefs2 = context.getSharedPreferences("setMessageSendSim", Context.MODE_PRIVATE)
    val b = prefs2.getInt("setMessageSendSim", 3)
    return b
}


fun setIsFistTimeMessageget(a: Boolean, context: Context) {
    val editor =
        context.getSharedPreferences("setIsFistTimeMessageget", Context.MODE_PRIVATE).edit()
    editor.putBoolean("setIsFistTimeMessageget", a)
    editor.apply()
}

fun getIsFistTimeMessageget(context: Context): Boolean {
    val prefs2 = context.getSharedPreferences("setIsFistTimeMessageget", Context.MODE_PRIVATE)
    val b = prefs2.getBoolean("setIsFistTimeMessageget", true)
    return b
}


@SuppressLint("Range")
fun fetchContactIdFromPhoneNumberFinal(phoneNumber: String?, context: Context): String? {

    val uri = Uri.withAppendedPath(
        PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber)
    )
    val cursor: Cursor? = context.contentResolver.query(
        uri, arrayOf(PhoneLookup.DISPLAY_NAME, PhoneLookup._ID), null, null, null
    )
    var contactId: String? = ""
    if (cursor?.moveToFirst() == true) {
        do {
            contactId = cursor.getString(
                cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME)
            )
        } while (cursor.moveToNext())
    }
    val numberfinal = if (contactId?.isEmpty() == true) {
        phoneNumber
    } else {
        contactId.toString()
    }
    return numberfinal?.let {
        retrieveContactPhoneNumber(it, context)
    }
}


@SuppressLint("Range")
fun retrieveContactPhoneNumber(displayName: String, context: Context): String? {
    val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
    val selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} = ?"
    val selectionArgs = arrayOf(displayName)

    val cursor = context.contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        null
    )

    cursor?.use {
        if (it.moveToFirst()) {
            phoneNumber =
                it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            // Now you have the phone number, you can use it as needed
        }
    }
    val numbernull = if (phoneNumber == "" || phoneNumber?.isEmpty() == true) {
        displayName
    } else {
        phoneNumber
    }
    val numberchack = if (numbernull?.isEmpty() == true) {
        getMobileNumberContact(context, displayName)
    } else {
        numbernull
    }
//    Log.d("jigar", "retrieveContactPhoneNumber: 123 <--------------> ${getMobileNumberContact(context, displayName)} <--------> $numbernull <-------> $phoneNumber")
    phoneNumber = ""
    return numberchack
}

fun getMobileNumberContact(context: Context, mobilenumber: String): String {
    var numberTwo = mobilenumber
    var shimmilarnumber = ""

    if (getMobileNumber(context).isNotEmpty()) {
        getMobileNumber(context).forEach {
            shimmilarnumber = if (calculateJaccardSimilarity(numberTwo, it) == 0.09) {
                it
            } else {
                numberTwo
            }
        }
    } else {
        shimmilarnumber = mobilenumber
    }
    numberTwo = shimmilarnumber
    if ((numberTwo[0] == '0')) {
        if (numberTwo.length > 1) {
            numberTwo = numberTwo.toString().substring(1, numberTwo.length)
        }
    }
    val number: String = if (numberTwo.startsWith("+")) {
        removeNonPlusAndDigits(numberTwo)
    } else {
        removeNonPlusAndDigits("+91$numberTwo")
    }
    Log.d("number", "onCreate: <-----------------> 12345 $number")
    return number
}

@SuppressLint("Range")
fun getMobileNumber(context: Context): ArrayList<String> {
    val mobilelist: ArrayList<String> = arrayListOf()
    val cursor: Cursor? = context.contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null
    )
    cursor.let {
        while (it?.moveToNext() == true) {
            val phoneNumber =
                it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            mobilelist.add(phoneNumber)
        }
        it?.close()
    }

    return mobilelist
}

fun removeNonPlusAndDigits(input: String): String {
    return input.replace(Regex("[^+\\d]"), "")
}

fun calculateJaccardSimilarity(str1: String, str2: String): Double {
    val set1 = str1.split(" ").toSet()
    val set2 = str2.split(" ").toSet()

    val intersectionSize = set1.intersect(set2).size
    val unionSize = set1.union(set2).size

    return intersectionSize.toDouble() / unionSize.toDouble()
}

fun getSimilarmobilenumber(context: Context, mobilenumber: String): String {
    var numberTwo = mobilenumber
    var shimmilarnumber = ""

    if (getMobileNumber(context).isNotEmpty()) {
        getMobileNumber(context).forEach {
            Log.d(
                "getSimilarmobilenumber",
                "getSimilarmobilenumber: <-------------> ${
                    calculateJaccardSimilarity(
                        mobilenumber,
                        it
                    )
                } <------> it $it <-----> numberTwo $numberTwo"
            )
            shimmilarnumber = if (calculateJaccardSimilarity(mobilenumber, it) == 0.09) {
                it
            } else {
                numberTwo
            }
        }
    } else {
        shimmilarnumber = mobilenumber
    }
    numberTwo = shimmilarnumber
    return numberTwo
}

@SuppressLint("Range")
fun searchSimilarPhoneNumber(phoneNumber: String, context: Context): String {
    var displayName = ""
    var similarPhoneNumber = ""
    val projection = arrayOf(
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )
    val selection = "${ContactsContract.CommonDataKinds.Phone.NUMBER} LIKE ?"
    val selectionArgs = arrayOf("%$phoneNumber%")

    val cursor = context.contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        null
    )

    cursor?.use {
        while (it.moveToNext()) {
            displayName =
                it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            similarPhoneNumber =
                it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            // Do something with the similarPhoneNumber and displayName
        }
    }
    return similarPhoneNumber
}

fun getThreadIdFromPhoneNumber(context: Context, phoneNumber: String): Long {
    val uri = Uri.parse("content://mms-sms/canonical-addresses")
    val projection = arrayOf("_id")
    val selection = "address = ?"
    val selectionArgs = arrayOf(phoneNumber)
    val cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
    if (cursor != null && cursor.moveToFirst()) {
        val threadId = cursor.getLong(0)
        cursor.close()
        return threadId
    }
    return -1 // Return -1 if thread ID not found
}

fun String.normalizePhoneNumber() = PhoneNumberUtils.normalizeNumber(this)

const val SORT_ORDER = "sort_order"
const val SORT_FOLDER_PREFIX =
    "sort_folder_"       // storing folder specific values at using "Use for this folder only"
const val SORT_BY_NAME = 1
const val SORT_BY_DATE_MODIFIED = 2
const val SORT_BY_SIZE = 4
const val SORT_BY_DATE_TAKEN = 8
const val SORT_BY_EXTENSION = 16
const val SORT_BY_PATH = 32
const val SORT_BY_NUMBER = 64
const val SORT_BY_FIRST_NAME = 128
const val SORT_BY_MIDDLE_NAME = 256
const val SORT_BY_SURNAME = 512
const val SORT_DESCENDING = 1024
const val SORT_BY_TITLE = 2048
const val SORT_BY_ARTIST = 4096
const val SORT_BY_DURATION = 8192
const val SORT_BY_RANDOM = 16384
const val SORT_USE_NUMERIC_VALUE = 32768
const val SORT_BY_FULL_NAME = 65536
const val SORT_BY_CUSTOM = 131072
const val SORT_BY_DATE_CREATED = 262144
const val FIRST_GROUP_ID = 10000L
const val SMT_PRIVATE = "smt_private"

val Context.baseConfig: BaseConfig get() = BaseConfig.newInstance(this)


@SuppressLint("StringFormatInvalid")
fun Context.showErrorToastMess(msg: String, length: Int = Toast.LENGTH_LONG) {
//    toast(String.format(getString(R.string.error), msg), length)
}

fun Context.showErrorToastMess(exception: Exception, length: Int = Toast.LENGTH_LONG) {
    CoroutineScope(Dispatchers.Main).launch {
        showErrorToastMess(exception.toString(), length)
    }
}

fun Context.toastMess(msg: String, length: Int = Toast.LENGTH_SHORT) {
    try {
        if (isOnMainThread()) {
            doToast(this, msg, length)
        } else {
            Handler(Looper.getMainLooper()).post {
                doToast(this, msg, length)
            }
        }
    } catch (e: Exception) {
    }
}

private fun doToast(context: Context, message: String, length: Int) {
    try {
        val targetContext = when {
            context is Activity -> {
                if (context.isFinishing || context.isDestroyed) {
                    context.applicationContext ?: context
                } else {
                    context
                }
            }

            else -> context.applicationContext ?: context
        }
        Toast.makeText(targetContext, message, length).show()
    } catch (_: Exception) {
    }
}

fun String.getNameLetter() =
    normalizeString().toCharArray().getOrNull(0)?.toString()?.uppercase(getDefault())
        ?: "A"

val letterBackgroundColors = arrayListOf(
    0xCCD32F2F,
    0xCCC2185B,
    0xCC1976D2,
    0xCC0288D1,
    0xCC0097A7,
    0xCC00796B,
    0xCC388E3C,
    0xCC689F38,
    0xCCF57C00,
    0xCCE64A19
)

val DARK_GREY = 0xFF333333.toInt()

fun Int.getContrastColor(): Int {
    val y = (299 * Color.red(this) + 587 * Color.green(this) + 114 * Color.blue(this)) / 1000
    return if (y >= 149 && this != Color.BLACK) DARK_GREY else Color.WHITE
}

var cout = 0

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N)
fun isNougatPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

@TargetApi(Build.VERSION_CODES.N)
fun Context.getBlockedNumbers(): ArrayList<BlockedNumber> {
    val blockedNumbers = ArrayList<BlockedNumber>()
    if (!isNougatPlus()) {
        return blockedNumbers
    }

    val uri = BlockedNumberContract.BlockedNumbers.CONTENT_URI
    val projection = arrayOf(
        BlockedNumberContract.BlockedNumbers.COLUMN_ID,
        BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER,
        BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER
    )

    queryCursor(uri, projection) { cursor ->
        val id = cursor.getLongValue(BlockedNumberContract.BlockedNumbers.COLUMN_ID)
        val number =
            cursor.getStringValue(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER) ?: ""
        val normalizedNumber =
            cursor.getStringValue(BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER) ?: number
        val comparableNumber = normalizedNumber.trimToComparableNumber()
        val blockedNumber = BlockedNumber(id, number, normalizedNumber, comparableNumber)
        blockedNumbers.add(blockedNumber)
    }

    return blockedNumbers
}

fun String.trimToComparableNumber(): String {
    // don't trim if it's not a phone number
    if (!this.isPhoneNumber()) {
        return this
    }
    val normalizedNumber = buildString(length) {
        this@trimToComparableNumber.forEach { char ->
            if (char.isDigit() || char == '+') {
                append(char)
            }
        }
    }
    val startIndex = Math.max(0, normalizedNumber.length - 9)
    return normalizedNumber.substring(startIndex)
}

fun String.isPhoneNumber(): Boolean {
    return this.matches("^[0-9+\\-\\)\\( *#]+\$".toRegex())
}

fun Context.getThreadSnippet(threadId: Long): String {
    val sortOrder = "${Telephony.Mms.DATE} DESC LIMIT 1"
    val latestMms = getMMS(threadId, false, sortOrder).firstOrNull()
    var snippet = latestMms?.body ?: ""

    val uri = Telephony.Sms.CONTENT_URI
    val projection = arrayOf(
        Telephony.Sms.BODY
    )

    val selection = "${Telephony.Sms.THREAD_ID} = ? AND ${Telephony.Sms.DATE} > ?"
    val selectionArgs = arrayOf(
        threadId.toString(), latestMms?.date?.toString() ?: "0"
    )
    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
        cursor?.use {
            if (cursor.moveToFirst()) {
                snippet = cursor.getStringValue(Telephony.Sms.BODY)
            }
        }
    } catch (ignored: Exception) {
    }
    return snippet
}


fun Context.getMMS(
    threadId: Long? = null,
    getImageResolutions: Boolean = false,
    sortOrder: String? = null,
    dateFrom: Int = -1,
): ArrayList<Message> {
    val uri = Telephony.Mms.CONTENT_URI
    val projection = arrayOf(
        Telephony.Mms._ID,
        Telephony.Mms.DATE,
        Telephony.Mms.READ,
        Telephony.Mms.MESSAGE_BOX,
        Telephony.Mms.THREAD_ID,
        Telephony.Mms.SUBSCRIPTION_ID,
        Telephony.Mms.STATUS
    )

    var selection: String? = null
    var selectionArgs: Array<String>? = null

    if (threadId == null && dateFrom != -1) {
        selection =
            "${Telephony.Sms.DATE} < ${dateFrom.toLong()}" //Should not multiply 1000 here, because date in mms's database is different from sms's.
    } else if (threadId != null && dateFrom == -1) {
        selection = "${Telephony.Sms.THREAD_ID} = ?"
        selectionArgs = arrayOf(threadId.toString())
    } else if (threadId != null) {
        selection =
            "${Telephony.Sms.THREAD_ID} = ? AND ${Telephony.Sms.DATE} < ${dateFrom.toLong()}"
        selectionArgs = arrayOf(threadId.toString())
    }

    val messages = ArrayList<Message>()
    val contactsMap = HashMap<Int, SimpleContact>()
    val threadParticipants = HashMap<Long, ArrayList<SimpleContact>>()
    queryCursor(uri, projection, selection, selectionArgs, sortOrder, showErrors = true) { cursor ->
        val mmsId = cursor.getLongValue(Telephony.Mms._ID)
        val type = cursor.getIntValue(Telephony.Mms.MESSAGE_BOX)
        val date = cursor.getLongValue(Telephony.Mms.DATE)
        val read = cursor.getIntValue(Telephony.Mms.READ) == 1
        val threadId = cursor.getLongValue(Telephony.Mms.THREAD_ID)
        val subscriptionId = cursor.getIntValue(Telephony.Mms.SUBSCRIPTION_ID)
        val status = cursor.getIntValue(Telephony.Mms.STATUS)
        val participants = if (threadParticipants.containsKey(threadId)) {
            threadParticipants[threadId]!!
        } else {
            val parts = getThreadParticipants(threadId, contactsMap)
            threadParticipants[threadId] = parts
            parts
        }

        val isMMS = true
        val attachment = getMmsAttachment(mmsId, getImageResolutions)
        val body = attachment.text
        var senderName = ""
        var senderPhotoUri = ""

        if (type != Telephony.Mms.MESSAGE_BOX_SENT && type != Telephony.Mms.MESSAGE_BOX_FAILED) {
            val number = getMMSSender(mmsId)
            val namePhoto = getNameAndPhotoFromPhoneNumber(number)
            senderName = namePhoto.name
            senderPhotoUri = namePhoto.photoUri ?: ""
        }

        val message = Message(
            mmsId,
            body,
            type,
            status,
            participants,
            date,
            read,
            threadId,
            isMMS,
            attachment,
            senderName,
            senderPhotoUri,
            subscriptionId
        )
        messages.add(message)

        participants.forEach {
            contactsMap[it.rawId] = it
        }
    }

    return messages
}

fun Context.queryCursor(
    uri: Uri,
    projection: Array<String>,
    selection: String? = null,
    selectionArgs: Array<String>? = null,
    sortOrder: String? = null,
    showErrors: Boolean = false,
    callback: (cursor: Cursor) -> Unit,
) {
    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
        cursor?.use {
            if (cursor.moveToFirst()) {
                do {
                    callback(cursor)
                } while (cursor.moveToNext())
            }
        }
    } catch (e: Exception) {
        if (showErrors) {
//            showErrorToast(e)
        }
    }
}

fun Context.getMMS2(
    threadId: Long? = null,
    getImageResolutions: Boolean = false,
    sortOrder: String? = null,
    dateFrom: Int = -1,
): ArrayList<Message> {
    val uri = Telephony.Mms.CONTENT_URI
    val projection = arrayOf(
        Telephony.Mms._ID,
        Telephony.Mms.DATE,
        Telephony.Mms.READ,
        Telephony.Mms.MESSAGE_BOX,
        Telephony.Mms.THREAD_ID,
        Telephony.Mms.SUBSCRIPTION_ID,
        Telephony.Mms.STATUS
    )

    var selection: String? = null
    var selectionArgs: Array<String>? = null

    if (threadId == null && dateFrom != -1) {
        selection =
            "${Telephony.Sms.DATE} < ${dateFrom.toLong()}" //Should not multiply 1000 here, because date in mms's database is different from sms's.
    } else if (threadId != null && dateFrom == -1) {
        selection = "${Telephony.Sms.THREAD_ID} = ?"
        selectionArgs = arrayOf(threadId.toString())
    } else if (threadId != null) {
        selection =
            "${Telephony.Sms.THREAD_ID} = ? AND ${Telephony.Sms.DATE} < ${dateFrom.toLong()}"
        selectionArgs = arrayOf(threadId.toString())
    }

    val messages = ArrayList<Message>()
    val contactsMap = HashMap<Int, SimpleContact>()
    val threadParticipants = HashMap<Long, ArrayList<SimpleContact>>()
    queryCursor(uri, projection, selection, selectionArgs, sortOrder, showErrors = true) { cursor ->
        val mmsId = cursor.getLongValue(Telephony.Mms._ID)
        val type = cursor.getIntValue(Telephony.Mms.MESSAGE_BOX)
        val date = cursor.getLongValue(Telephony.Mms.DATE)
        val read = cursor.getIntValue(Telephony.Mms.READ) == 1
        val threadId = cursor.getLongValue(Telephony.Mms.THREAD_ID)
        val subscriptionId = cursor.getIntValue(Telephony.Mms.SUBSCRIPTION_ID)
        val status = cursor.getIntValue(Telephony.Mms.STATUS)
        val participants = if (threadParticipants.containsKey(threadId)) {
            threadParticipants[threadId]!!
        } else {
            val parts = getThreadParticipants(threadId, contactsMap)
            threadParticipants[threadId] = parts
            parts
        }
        Log.d("TextView", "setNewDateYear: TextView <----------> ${date}")
        val isMMS = true
        val attachment = getMmsAttachment(mmsId, getImageResolutions)
        val body = attachment.text
        var senderName = ""
        var senderPhotoUri = ""

        if (type != Telephony.Mms.MESSAGE_BOX_SENT && type != Telephony.Mms.MESSAGE_BOX_FAILED) {
            val number = getMMSSender(mmsId)
            val namePhoto = getNameAndPhotoFromPhoneNumber(number)
            senderName = namePhoto.name
            senderPhotoUri = namePhoto.photoUri ?: ""
        }

        val message = Message(
            mmsId,
            body,
            type,
            status,
            participants,
            date,
            read,
            threadId,
            isMMS,
            attachment,
            senderName,
            senderPhotoUri,
            subscriptionId
        )
        messages.add(message)

        participants.forEach {
            contactsMap[it.rawId] = it
        }
    }

    return messages
}


fun Context.getMMSSender(msgId: Long): String {
    val uri = Uri.parse("${Telephony.Mms.CONTENT_URI}/$msgId/addr")
    val projection = arrayOf(
        Telephony.Mms.Addr.ADDRESS
    )

    try {
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                return cursor.getStringValue(Telephony.Mms.Addr.ADDRESS)
            }
        }
    } catch (ignored: Exception) {
    }
    return ""
}

fun Context.getContactFromAddress(
    address: String,
    callback: ((contact: com.simplemobiletools.commons.models.SimpleContact?) -> Unit),
) {
    val privateCursor = getMyContactsCursor(false, true)
    com.simplemobiletools.commons.helpers.SimpleContactsHelper(this).getAvailableContacts(false) {
        val contact = it.firstOrNull { it.doesHavePhoneNumber(address) }
        if (contact == null) {
            val privateContacts =
                com.simplemobiletools.commons.helpers.MyContactsContentProvider.getSimpleContacts(
                    this,
                    privateCursor
                )
            val privateContact = privateContacts.firstOrNull { it.doesHavePhoneNumber(address) }
            callback(privateContact)
        } else {
            callback(contact)
        }
    }
}

//fun ArrayList<com.simplemobiletools.commons.models.SimpleContact>.getThreadTitle(): String = TextUtils.join(", ", map { it.name }.toTypedArray()).orEmpty()

fun Context.getThreadParticipants(
    threadId: Long,
    contactsMap: HashMap<Int, SimpleContact>?,
): ArrayList<SimpleContact> {
    val uri = Uri.parse("${Telephony.MmsSms.CONTENT_CONVERSATIONS_URI}?simple=true")
    val projection = arrayOf(
        Telephony.ThreadsColumns.RECIPIENT_IDS
    )
    val selection = "${Telephony.Mms._ID} = ?"
    val selectionArgs = arrayOf(threadId.toString())
    val participants = ArrayList<SimpleContact>()
    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                val address = cursor.getStringValue(Telephony.ThreadsColumns.RECIPIENT_IDS)
                address.split(" ").filter { it.areDigitsOnly() }.forEach {
                    val addressId = it.toInt()
                    if (contactsMap?.containsKey(addressId) == true) {
                        participants.add(contactsMap[addressId]!!)
                        return@forEach
                    }

                    val number = getPhoneNumberFromAddressId(addressId)
                    val namePhoto = getNameAndPhotoFromPhoneNumber(number)
                    val name = namePhoto.name
                    val photoUri = namePhoto.photoUri ?: ""
                    val phoneNumber = PhoneNumber(number, 0, "", number)
                    val contact = SimpleContact(
                        addressId,
                        addressId,
                        name,
                        photoUri,
                        arrayListOf(phoneNumber),
                        ArrayList(),
                        ArrayList()
                    )
                    participants.add(contact)
                }
            }
        }
    } catch (e: Exception) {
        Log.e("YourTag", "Error in getThreadParticipants", e)
        showErrorToastMess(e)
    }
    return participants
}

fun String.areDigitsOnly() = matches(Regex("[0-9]+"))

/*fun Context.getPhoneNumberFromAddressId(canonicalAddressId: Int): String {
    val uri = Uri.withAppendedPath(Telephony.MmsSms.CONTENT_URI, "canonical-addresses")
    val projection = arrayOf(
        Telephony.Mms.Addr.ADDRESS
    )

    val selection = "${Telephony.Mms._ID} = ?"
    val selectionArgs = arrayOf(canonicalAddressId.toString())
    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                return cursor.getStringValue(Telephony.Mms.Addr.ADDRESS)
            }
        }
    } catch (e: Exception) {
        showErrorToastMess(e)
    }
    return ""
}*/

fun Context.getPhoneNumberFromAddressId(canonicalAddressId: Int): String {
    val uri = Uri.withAppendedPath(Telephony.MmsSms.CONTENT_URI, "canonical-addresses")
    val projection = arrayOf(
        Telephony.Mms.Addr.ADDRESS
    )

    val selection = "${Telephony.Mms._ID} = ?"
    val selectionArgs = arrayOf(canonicalAddressId.toString())
    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                return cursor.getStringValue(Telephony.Mms.Addr.ADDRESS)
            }
        }
    } catch (e: Exception) {
        showErrorToastMess(e)
    }
    return ""
}

fun Context.getThreadPhoneNumbers1(recipientIds: List<Int>): ArrayList<String> {
    val numbers = ArrayList<String>()
    recipientIds.forEach {
        numbers.add(getPhoneNumberFromAddressId(it))
    }
    return numbers
}

fun Context.getThreadPhoneNumbers(recipientIds: List<Int>): ArrayList<String> {
    val numbers = ArrayList<String>()
    recipientIds.forEach { id ->
        var address: String? = getPhoneNumberFromAddressId(id)
        // Fallback: if address is numeric or null, get first message's sender in this thread
        if (address == null || address.isEmpty() || address.matches(Regex("^\\d+$"))) {
            address = try {
                contentResolver.query(
                    Uri.parse("content://sms"),
                    arrayOf("address"),
                    "thread_id=?",
                    arrayOf(id.toString()),
                    "date ASC"
                )?.use { cursor ->
                    if (cursor.moveToFirst()) cursor.getString(0) else null
                }
            } catch (_: Exception) {
                null
            }
        }
        if (!address.isNullOrBlank()) numbers.add(address)
    }
    return numbers
}

fun Context.getThreadPhoneNumbersFast(
    recipientIds: List<Int>,
    addressMap: Map<Int, String>
): ArrayList<String> {
    val numbers = ArrayList<String>()
    recipientIds.forEach { id ->
        var address = addressMap[id]
        val original = address

        // Fallback: if address is numeric or null, get first message's sender in this thread
        if (address == null || address.matches(Regex("^\\d+$"))) {
            address = try {
                contentResolver.query(
                    Uri.parse("content://sms"),
                    arrayOf("address"),
                    "thread_id=?",
                    arrayOf(id.toString()),
                    "date ASC"
                )?.use { cursor ->
                    if (cursor.moveToFirst()) cursor.getString(0) else null
                }
            } catch (_: Exception) {
                null
            }
        }
        if (!address.isNullOrBlank()) numbers.add(address)
    }
    return numbers
}


@SuppressLint("NewApi")
fun Context.getMmsAttachment(id: Long, getImageResolutions: Boolean): MessageAttachment {
    val uri = if (isQPlus()) {
        Telephony.Mms.Part.CONTENT_URI
    } else {
        Uri.parse("content://mms/part")
    }

    val projection = arrayOf(
        Telephony.Mms._ID, Telephony.Mms.Part.CONTENT_TYPE, Telephony.Mms.Part.TEXT
    )
    val selection = "${Telephony.Mms.Part.MSG_ID} = ?"
    val selectionArgs = arrayOf(id.toString())
    val messageAttachment = MessageAttachment(id, "", arrayListOf())

    var attachmentNames: List<String>? = null
    var attachmentCount = 0
    queryCursor(uri, projection, selection, selectionArgs, showErrors = true) { cursor ->
        val partId = cursor.getLongValue(Telephony.Mms._ID)
        val mimetype = cursor.getStringValue(Telephony.Mms.Part.CONTENT_TYPE)
        if (mimetype == "text/plain") {
            messageAttachment.text = cursor.getStringValue(Telephony.Mms.Part.TEXT) ?: ""
        } else if (mimetype.startsWith("image/") || mimetype.startsWith("video/")) {
            val fileUri = Uri.withAppendedPath(uri, partId.toString())
            var width = 0
            var height = 0

            if (getImageResolutions) {
                try {
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeStream(
                        contentResolver.openInputStream(fileUri),
                        null,
                        options
                    )
                    width = options.outWidth
                    height = options.outHeight
                } catch (e: Exception) {
                }
            }

            val attachment = Attachment(partId, id, fileUri.toString(), mimetype, width, height, "")
            messageAttachment.attachments.add(attachment)
        } else if (mimetype != "application/smil") {
            val attachmentName = attachmentNames?.getOrNull(attachmentCount) ?: ""
            val attachment = Attachment(
                partId,
                id,
                Uri.withAppendedPath(uri, partId.toString()).toString(),
                mimetype,
                0,
                0,
                attachmentName
            )
            messageAttachment.attachments.add(attachment)
            attachmentCount++
        } else {
            val text = cursor.getStringValue(Telephony.Mms.Part.TEXT)
            attachmentNames = parseAttachmentNames(text)
        }
    }

    return messageAttachment
}

fun Context.isNumberBlocked(
    number: String,
    blockedNumbers: java.util.ArrayList<BlockedNumber> = getBlockedNumbers(),
): Boolean {
    if (!isNougatPlus()) {
        return false
    }

    val numberToCompare = number.trimToComparableNumber()
    return blockedNumbers.any { numberToCompare == it.numberToCompare || numberToCompare == it.number } || isNumberBlockedByPattern(
        number,
        blockedNumbers
    )
}

fun Context.isNumberBlockedByPattern(
    number: String,
    blockedNumbers: java.util.ArrayList<BlockedNumber> = getBlockedNumbers(),
): Boolean {
    for (blockedNumber in blockedNumbers) {
        val num = blockedNumber.number
        if (num.isBlockedNumberPattern()) {
            val pattern = num.replace("+", "\\+").replace("*", ".*")
            if (number.matches(pattern.toRegex())) {
                return true
            }
        }
    }
    return false
}

fun String.isBlockedNumberPattern() = contains("*")

const val MESSAGES_LIMIT = 75

fun Context.getThreadContactNames(
    phoneNumbers: List<String>,
    privateContacts: ArrayList<SimpleContact>,
): ArrayList<String> {
    val names = ArrayList<String>()
    phoneNumbers.forEach { number ->
        val name = SimpleContactsHelper(this).getNameFromPhoneNumber(number)
        if (name != number) {
            names.add(name)
        } else {
            val privateContact = privateContacts.firstOrNull { it.doesHavePhoneNumber(number) }
            if (privateContact == null) {
                names.add(name)
            } else {
                names.add(privateContact.name)
            }
        }
    }
    return names
}

fun parseRcsBotName(jid: String): String {
    val parts = jid.split("@")
    if (parts.size != 2) return "Verified Business"

    val username = parts[0].trimEnd('=') // Remove trailing base32 padding if any

    // If the username is a long obfuscated hash, use standard fallback
    if (username.length > 30 || username.matches(Regex("^[a-z0-9]{30,}$"))) {
        return "Verified Business"
    }

    // Clean up short, readable brand identifiers
    return username.split(Regex("[-._]"))
        .filter { it.isNotEmpty() }
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
}
fun Context.getNameAndPhotoFromPhoneNumber(number: String): NamePhoto {
    try {
        if (!hasPermission(PERMISSION_READ_CONTACTS)) {
            return NamePhoto(number, null)
        }

        Log.e("getname", "getNameAndPhotoFromPhoneNumber: ", )

        val uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
        val projection = arrayOf(
            PhoneLookup.DISPLAY_NAME, PhoneLookup.PHOTO_URI
        )

        try {
            val cursor = contentResolver.query(uri, projection, null, null, null)
            cursor.use {
                if (cursor?.moveToFirst() == true) {
                    val name = cursor.getStringValue(PhoneLookup.DISPLAY_NAME)

                    val photoUri = cursor.getStringValue(PhoneLookup.PHOTO_URI)
                    return NamePhoto(name, photoUri)
                }
            }
        } catch (e: Exception) {
        }

//        Log.d("catch", "getNameAndPhotoFromPhoneNumber: ")

        return NamePhoto(number, null)
    } catch (_: Exception) {
        return NamePhoto(number, null)
    }

}


const val ADDRESS_SEPARATOR = "|"


fun Context.getConversations(
    threadId: Long? = null,
    privateContacts: ArrayList<SimpleContact> = ArrayList(),
): ArrayList<ConversationNew> {
    val uri = Uri.parse("${Telephony.Threads.CONTENT_URI}?simple=true")
    val projection = arrayOf(
        Telephony.Threads._ID,
        Telephony.Threads.SNIPPET,
        Telephony.Threads.DATE,
        Telephony.Threads.READ,
        Telephony.Threads.RECIPIENT_IDS
    )

    var selection = "${Telephony.Threads.MESSAGE_COUNT} > ?"
    var selectionArgs = arrayOf("0")
    if (threadId != null) {
        selection += " AND ${Telephony.Threads._ID} = ?"
        selectionArgs = arrayOf("0", threadId.toString())
    }

    val sortOrder = "${Telephony.Threads.DATE} DESC"

    val conversations = ArrayList<ConversationNew>()
    val simpleContactHelper = SimpleContactsHelper(this)
    val blockedNumbers = getBlockedNumbers()
    queryCursor(uri, projection, selection, selectionArgs, sortOrder, true) { cursor ->
        val id = cursor.getLongValue(Telephony.Threads._ID)
        var snippet = cursor.getStringValue(Telephony.Threads.SNIPPET) ?: ""
        if (snippet.isEmpty()) {
            snippet = getThreadSnippet(id)
        }

        var date = cursor.getLongValue(Telephony.Threads.DATE)
        if (date.toString().length > 10) {
            date /= 1000
        }

        val rawIds = cursor.getStringValue(Telephony.Threads.RECIPIENT_IDS)
        val recipientIds =
            rawIds.split(" ").filter { it.areDigitsOnly() }.map { it.toInt() }.toMutableList()
        val phoneNumbers = getThreadPhoneNumbers(recipientIds)
        if (phoneNumbers.isEmpty() || phoneNumbers.any { isNumberBlocked(it, blockedNumbers) }) {
            return@queryCursor
        }

        val names = getThreadContactNames(phoneNumbers, privateContacts)
        val title = TextUtils.join(", ", names.toTypedArray())
        val photoUri =
            if (phoneNumbers.size == 1) simpleContactHelper.getPhotoUriFromPhoneNumber(phoneNumbers.first()) else ""
        val isGroupConversation = phoneNumbers.size > 1
        val read = cursor.getIntValue(Telephony.Threads.READ) == 1
        val conversation = ConversationNew(
            id,
            snippet,
            date.toInt(),
            read,
            title,
            photoUri,
            isGroupConversation,
            phoneNumbers.first()
        )
        conversations.add(conversation)
    }

    return conversations
}

fun Context.getMyContactsCursor(favoritesOnly: Boolean, withPhoneNumbersOnly: Boolean) = try {
    val getFavoritesOnly = if (favoritesOnly) "1" else "0"
    val getWithPhoneNumbersOnly = if (withPhoneNumbersOnly) "1" else "0"
    val args = arrayOf(getFavoritesOnly, getWithPhoneNumbersOnly)
    CursorLoader(
        this,
        MyContactsContentProvider.CONTACTS_CONTENT_URI,
        null,
        null,
        args,
        null
    ).loadInBackground()
} catch (e: Exception) {
    null
}

fun Context.getMyContactsCursor2(favoritesOnly: Boolean, withPhoneNumbersOnly: Boolean) = try {
    val getFavoritesOnly = if (favoritesOnly) "1" else "0"
    val getWithPhoneNumbersOnly = if (withPhoneNumbersOnly) "1" else "0"
    val args = arrayOf(getFavoritesOnly, getWithPhoneNumbersOnly)
    CursorLoader(
        this,
        com.simplemobiletools.commons.helpers.MyContactsContentProvider.CONTACTS_CONTENT_URI,
        null,
        null,
        args,
        null
    ).loadInBackground()
} catch (e: Exception) {
    null
}


fun Context.getMessages(
    threadId: Long,
    getImageResolutions: Boolean,
    dateFrom: Int = -1,
    includeScheduledMessages: Boolean = true,
    limit: Int = MESSAGES_LIMIT,
): ArrayList<Message> {
    val uri = Telephony.Sms.CONTENT_URI
    val projection = arrayOf(
        Telephony.Sms._ID,
        Telephony.Sms.BODY,
        Telephony.Sms.TYPE,
        Telephony.Sms.ADDRESS,
        Telephony.Sms.DATE,
        Telephony.Sms.READ,
        Telephony.Sms.THREAD_ID,
        Telephony.Sms.SUBSCRIPTION_ID,
        Telephony.Sms.STATUS
    )

    val rangeQuery =
        if (dateFrom == -1) "" else "AND ${Telephony.Sms.DATE} < ${dateFrom.toLong() * 1000}"
    val selection = "${Telephony.Sms.THREAD_ID} = ? $rangeQuery"
    val selectionArgs = arrayOf(threadId.toString())
    val sortOrder = "${Telephony.Sms.DATE} DESC LIMIT $limit"

    val blockStatus = HashMap<String, Boolean>()
    val blockedNumbers = getBlockedNumbers()
    var messages = ArrayList<Message>()
    queryCursor(uri, projection, selection, null, sortOrder, showErrors = true) { cursor ->
        val senderNumber = cursor.getStringValue(Telephony.Sms.ADDRESS) ?: return@queryCursor

        val isNumberBlocked = if (blockStatus.containsKey(senderNumber)) {
            blockStatus[senderNumber]!!
        } else {
            val isBlocked = isNumberBlocked(senderNumber, blockedNumbers)
            blockStatus[senderNumber] = isBlocked
            isBlocked
        }

        if (isNumberBlocked) {
            return@queryCursor
        }

        val id = cursor.getLongValue(Telephony.Sms._ID)
        val body = cursor.getStringValue(Telephony.Sms.BODY)
        val type = cursor.getIntValue(Telephony.Sms.TYPE)
        val namePhoto = getNameAndPhotoFromPhoneNumber(senderNumber)
        val senderName = namePhoto.name
        val photoUri = namePhoto.photoUri ?: ""
        val date = (cursor.getLongValue(Telephony.Sms.DATE) / 1000)
        val read = cursor.getIntValue(Telephony.Sms.READ) == 1
        val thread = cursor.getLongValue(Telephony.Sms.THREAD_ID)
        val subscriptionId = cursor.getIntValue(Telephony.Sms.SUBSCRIPTION_ID)
        val status = cursor.getIntValue(Telephony.Sms.STATUS)
        val participants = senderNumber.split(ADDRESS_SEPARATOR).map { number ->
            val phoneNumber = PhoneNumber(number, 0, "", number)
            val participantPhoto = getNameAndPhotoFromPhoneNumber(number)
            SimpleContact(
                0,
                0,
                participantPhoto.name,
                photoUri,
                arrayListOf(phoneNumber),
                ArrayList(),
                ArrayList()
            )
        }
        val isMMS = false
        val message = Message(
            id,
            body,
            type,
            status,
            ArrayList(participants),
            date,
            read,
            thread,
            isMMS,
            null,
            senderName,
            photoUri,
            subscriptionId
        )
        messages.add(message)
    }

    messages.addAll(getMMS(threadId, getImageResolutions, sortOrder, dateFrom))
    Log.d("", "sendMessage:attachment <-----------> 66 ${messages}")

    messages = messages.filter { it.participants.isNotEmpty() }
        .filterNot { it.isScheduled && it.millis() < System.currentTimeMillis() }
        .sortedWith(compareBy<Message> { it.date }.thenBy { it.id }).takeLast(limit)
        .toMutableList() as ArrayList<Message>

    return messages
}

@SuppressLint("NewApi")
fun Context.getThreadId(address: String): Long {
    return try {
        Telephony.Threads.getOrCreateThreadId(this, address)
    } catch (e: Exception) {
        0L
    }
}

@SuppressLint("NewApi")
fun Context.getThreadId(addresses: Set<String>): Long {
    return try {
        Telephony.Threads.getOrCreateThreadId(this, addresses)
    } catch (e: Exception) {
        0L
    }
}

fun Context.insertNewSMS(
    address: String,
    subject: String,
    body: String,
    date: Long,
    read: Int,
    threadId: Long,
    type: Int,
    subscriptionId: Int,
): Long {
    val uri = Telephony.Sms.CONTENT_URI
    val contentValues = ContentValues().apply {
        put(Telephony.Sms.ADDRESS, address)
        put(Telephony.Sms.SUBJECT, subject)
        put(Telephony.Sms.BODY, body)
        put(Telephony.Sms.DATE, date)
        put(Telephony.Sms.READ, read)
        put(Telephony.Sms.THREAD_ID, threadId)
        put(Telephony.Sms.TYPE, type)
        put(Telephony.Sms.SUBSCRIPTION_ID, subscriptionId)
    }

    return try {
        val newUri = contentResolver.insert(uri, contentValues)
        newUri?.lastPathSegment?.toLong() ?: 0L
    } catch (e: Exception) {
        0L
    }
}

fun Context.getNameFromAddress(address: String, privateCursor: Cursor?): String {
    var sender = getNameAndPhotoFromPhoneNumber(address).name
    if (address == sender) {
        val privateContacts = MyContactsContentProvider.getSimpleContacts(this, privateCursor)
        sender = privateContacts.firstOrNull { it.doesHavePhoneNumber(address) }?.name ?: address
    }
    return sender
}

const val TIME_FORMAT_12 = "hh:mm a"
const val TIME_FORMAT_24 = "HH:mm"

fun Context.getTimeFormat() = if (false) TIME_FORMAT_24 else TIME_FORMAT_12

fun Int.formatDateOrTime(
    context: Context,
    hideTimeAtOtherDays: Boolean,
    showYearEvenIfCurrent: Boolean,
): String {
    val cal = Calendar.getInstance(Locale.ENGLISH)
    cal.timeInMillis = this * 1000L

    return if (DateUtils.isToday(this * 1000L)) {
        DateFormat.format(context.getTimeFormat(), cal).toString()
    } else {
        var format = getDefaultDateFormat(context)
        if (!showYearEvenIfCurrent && isThisYear()) {
            format = format.replace("y", "").trim().trim('-').trim('.').trim('/')
        }

        if (!hideTimeAtOtherDays) {
            format += ", ${context.getTimeFormat()}"
        }

        DateFormat.format(format, cal).toString()
    }
}


fun Int.isThisYear(): Boolean {
    val time = Time()
    time.set(this * 1000L)

    val thenYear = time.year
    time.set(System.currentTimeMillis())

    return (thenYear == time.year)
}

const val DATE_FORMAT_ONE = "dd.MM.yyyy"
const val DATE_FORMAT_TWO = "dd/MM/yyyy"
const val DATE_FORMAT_THREE = "MM/dd/yyyy"
const val DATE_FORMAT_FOUR = "yyyy-MM-dd"
const val DATE_FORMAT_FIVE = "d MMMM yyyy"
const val DATE_FORMAT_SIX = "MMMM d yyyy"
const val DATE_FORMAT_SEVEN = "MM-dd-yyyy"
const val DATE_FORMAT_EIGHT = "dd-MM-yyyy"
const val DATE_FORMAT_NINE = "yyyyMMdd"
const val DATE_FORMAT_TEN = "yyyy.MM.dd"
const val DATE_FORMAT_ELEVEN = "yy-MM-dd"
const val DATE_FORMAT_TWELVE = "yyMMdd"
const val DATE_FORMAT_THIRTEEN = "yy.MM.dd"
const val DATE_FORMAT_FOURTEEN = "yy/MM/dd"

private fun getDefaultDateFormat(context: Context): String {
    val format = DateFormat.getDateFormat(context)
    val pattern = (format as SimpleDateFormat).toLocalizedPattern()
    return when (pattern.lowercase().replace(" ", "")) {
        "d.M.y" -> DATE_FORMAT_ONE
        "dd/mm/y" -> DATE_FORMAT_TWO
        "mm/dd/y" -> DATE_FORMAT_THREE
        "y-mm-dd" -> DATE_FORMAT_FOUR
        "dmmmmy" -> DATE_FORMAT_FIVE
        "mmmmdy" -> DATE_FORMAT_SIX
        "mm-dd-y" -> DATE_FORMAT_SEVEN
        "dd-mm-y" -> DATE_FORMAT_EIGHT
        else -> DATE_FORMAT_ONE
    }
}

fun removeCountryCodeAndLeadingZeros(phoneNumber: String): String {
    // Remove all non-digit characters from the input
    val digitsOnly = phoneNumber.replace(Regex("[^0-9]"), "")

    // Check if the input starts with a plus sign and remove it
    val normalizedNumber = if (digitsOnly.startsWith("+")) {
        digitsOnly.substring(1)
    } else {
        digitsOnly
    }

    // Remove leading zeros and additional digits that may represent country codes
    return normalizedNumber.replaceFirst("^0+".toRegex(), "")
}

fun Context.getSendMessageSettings(): Settings {
    val settings = Settings()
    settings.useSystemSending = true
    settings.deliveryReports = true
    settings.sendLongAsMms = config.sendLongMessageMMS
    settings.sendLongAsMmsAfter = 1
    settings.group = config.sendGroupMessageMMS
    return settings
}

fun Context.getAllDrafts(): HashMap<Long, String?> {
    val drafts = HashMap<Long, String?>()
    val uri = Telephony.Sms.Draft.CONTENT_URI
    val projection = arrayOf(Telephony.Sms.BODY, Telephony.Sms.THREAD_ID)

    try {
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            while (it.moveToNext()) {
                val threadId = it.getLongValue(Telephony.Sms.THREAD_ID)
                val draft = it.getStringValue(Telephony.Sms.BODY)
                if (draft != null) {
                    drafts[threadId] = draft
                }
            }
        }
    } catch (e: Exception) {
    }

    return drafts
}

fun Context.saveSmsDraft(body: String, threadId: Long) {
    val uri = Telephony.Sms.Draft.CONTENT_URI
    val contentValues = ContentValues().apply {
        put(Telephony.Sms.BODY, body)
        put(Telephony.Sms.DATE, System.currentTimeMillis().toString())
        put(Telephony.Sms.TYPE, Telephony.Sms.MESSAGE_TYPE_DRAFT)
        put(Telephony.Sms.THREAD_ID, threadId)
    }

    try {
        contentResolver.insert(uri, contentValues)
    } catch (e: Exception) {
    }
}

fun Context.getSmsDraft(threadId: Long): String? {
    val uri = Telephony.Sms.Draft.CONTENT_URI
    val projection = arrayOf(Telephony.Sms.BODY)
    val selection = "${Telephony.Sms.THREAD_ID} = ?"
    val selectionArgs = arrayOf(threadId.toString())

    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor.use {
            if (cursor?.moveToFirst() == true) {
                return cursor.getString(0)
            }
        }
    } catch (e: Exception) {
    }

    return null
}

fun Context.deleteSmsDraft(threadId: Long) {
    val uri = Telephony.Sms.Draft.CONTENT_URI
    val projection = arrayOf(Telephony.Sms._ID)
    val selection = "${Telephony.Sms.THREAD_ID} = ?"
    val selectionArgs = arrayOf(threadId.toString())
    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor.use {
            if (cursor?.moveToFirst() == true) {
                val draftId = cursor.getLong(0)
                val draftUri = Uri.withAppendedPath(Telephony.Sms.CONTENT_URI, "/${draftId}")
                contentResolver.delete(draftUri, null, null)
            }
        }
    } catch (e: Exception) {
        Log.d("", "onPause: deleteSmsDraft <--------------------> 222 ${e.message}")
    }
}

const val EXCLUDED_FOLDERS = "excluded_folders"
var monthdiscount = ""
val Context.config: Config get() = Config.newInstance(applicationContext)

var MessageTraslationlist: ArrayList<MessageTraslationModel> = arrayListOf()

val Context.messagingUtils get() = MessagingUtils(this)

val Context.smsSender get() = SmsSender.getInstance(applicationContext as Application)

fun ArrayList<SimpleContact>.getThreadTitle(): String =
    TextUtils.join(", ", map { it.name }.toTypedArray()).orEmpty()

fun ArrayList<com.simplemobiletools.commons.models.SimpleContact>.getThreadTitlePre(): String =
    TextUtils.join(", ", map { it.name }.toTypedArray()).orEmpty()

fun ArrayList<SimpleContact>.getAddresses() =
    flatMap { it.phoneNumbers }.map { it.normalizedNumber }

fun ArrayList<com.simplemobiletools.commons.models.SimpleContact>.getAddressesPre() =
    flatMap { it.phoneNumbers }.map { it.normalizedNumber }

fun ArrayList<com.simplemobiletools.commons.models.SimpleContact>.getAddressesPre2() =
    flatMap { it.phoneNumbers }.map { it.normalizedNumber }
        .map { if (it.startsWith("91")) it.removePrefix("91") else it }

fun View.isVisible() = visibility == View.VISIBLE

fun View.isVisibleMess() = visibility == View.VISIBLE

fun isLikelyShortCode(address: String?): Boolean {
    if (address.isNullOrEmpty()) return false

    val normalized = address.uppercase()


    if (normalized.contains("@BOT.RCS.GOOGLE.COM") ||
        normalized.contains("@RCS.GOOGLE.COM") ||
        normalized.endsWith(".RCS.GOOGLE.COM") ||
        normalized.matches(Regex(".+@.+\\.GOOGLE\\.COM$"))
    ) {
        return true
    }



    return when {

        normalized.matches(Regex("^[A-Z]{2}-[A-Z0-9]{6}$")) -> true           // VM-ABC123
        normalized.matches(Regex("^[A-Z]{2}[A-Z0-9]{6}$")) -> true            // VMABC123
        normalized.matches(Regex("^[A-Z]{2,4}-[A-Z0-9-]{5,10}$")) -> true     // AX-BNK123, JIO-MYJIO-S, AD-HDFCBNK, etc.
        else -> false
    }
}

fun View.isInvisible() = visibility == View.INVISIBLE

fun View.isGone() = visibility == View.GONE

val Context.realScreenSize: Point
    get() {
        val size = Point()
        windowManager.defaultDisplay.getRealSize(size)
        return size
    }

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun isSPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

fun Context.getMessageRecipientAddress(messageId: Long): String {
    val uri = Telephony.Sms.CONTENT_URI
    val projection = arrayOf(
        Telephony.Sms.ADDRESS
    )

    val selection = "${Telephony.Sms._ID} = ?"
    val selectionArgs = arrayOf(messageId.toString())

    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                return cursor.getStringValue(Telephony.Sms.ADDRESS)
            }
        }
    } catch (e: Exception) {
    }

    return ""
}

fun Context.getConversationIds(): List<Long> {
    val uri = Uri.parse("${Telephony.Threads.CONTENT_URI}?simple=true")
    val projection = arrayOf(Telephony.Threads._ID)
    val selection = "${Telephony.Threads.MESSAGE_COUNT} > ?"
    val selectionArgs = arrayOf("0")
    val sortOrder = "${Telephony.Threads.DATE} ASC"
    val conversationIds = mutableListOf<Long>()
    queryCursor(uri, projection, selection, selectionArgs, sortOrder, true) { cursor ->
        val id = cursor.getLongValue(Telephony.Threads._ID)
        conversationIds.add(id)
    }
    return conversationIds
}

fun Context.isLongMmsMessage(text: String, settings: Settings = getSendMessageSettings()): Boolean {
    val data = SmsMessage.calculateLength(text, false)
    val numPages = data.first()
    return numPages > settings.sendLongAsMmsAfter && settings.sendLongAsMms
}

fun Context.toastMess(id: Int, length: Int = Toast.LENGTH_SHORT) {
    toast(getString(id), length)
}

fun Context.subscriptionManagerCompat(): SubscriptionManager {
    return getSystemService(SubscriptionManager::class.java)
}

fun Context.getSharedPrefs() = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
fun Context.getSharedPrefs2() = getSharedPreferences(PREFS_KEY2, Context.MODE_PRIVATE)
fun Context.getSharedPrefs3() = getSharedPreferences(PREFS_KEY3, Context.MODE_PRIVATE)
fun Context.getSharedPrefs4() = getSharedPreferences(PREFS_KEY4, Context.MODE_PRIVATE)
fun Context.getSharedPrefs5() = getSharedPreferences(PREFS_KEY5, Context.MODE_PRIVATE)
fun Context.getSharedPrefs6() = getSharedPreferences(PREFS_KEY5, Context.MODE_PRIVATE)
fun Context.getSharedPrefs7() = getSharedPreferences(PREFS_KEY7, Context.MODE_PRIVATE)

const val PREFS_KEY = "Prefs"
const val PREFS_KEY2 = "Prefs2"
const val PREFS_KEY3 = "Prefs3"
const val PREFS_KEY4 = "Prefs4"

const val PREFS_KEY5 = "Prefs5"
const val PREFS_KEY7 = "Prefs7"


const val USE_SIM_ID_PREFIX = "use_sim_id_"

inline fun <T> List<T>.indexOfFirstOrNull(predicate: (T) -> Boolean): Int? {
    var index = 0
    for (item in this) {
        if (predicate(item)) return index
        index++
    }
    return null
}

fun Context.copyToClipboard(text: String) {
    val clip = ClipData.newPlainText(getString(R.string.app_name), text)
    (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
}

fun Context.shareText(text: String, title: String = "Share via") {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val chooser = Intent.createChooser(shareIntent, title)
    if (shareIntent.resolveActivity(packageManager) != null) {
        startActivity(chooser)
    }
}

var isdataloading = false

val Context.notificationHelper get() = NotificationHelper(this)

val Context.notificationHelperBubble
    @RequiresApi(Build.VERSION_CODES.O) get() = NootificationBubble(this)

@RequiresApi(Build.VERSION_CODES.O)
fun Context.showReceivedMessageNotification(
    address: String,
    body: String,
    threadId: Long,
    bitmap: Bitmap?,
    newMessageId: Long,
) {
    val privateCursor = getMyContactsCursor(favoritesOnly = false, withPhoneNumbersOnly = true)
    ensureBackgroundThread {
        val senderName = getNameFromAddress(address, privateCursor)

        Handler(Looper.getMainLooper()).post {
            notificationHelper.showMessageNotification(
                address,
                body,
                threadId,
                bitmap,
                senderName,
                messageid = newMessageId
            )
        }
    }
}

fun Context.markastoallread() {
    val values = ContentValues()
    values.put(Telephony.Sms.READ, 1)
    contentResolver.update(
        Telephony.Sms.Inbox.CONTENT_URI, values, Telephony.Sms.READ + "=0", null
    )
}

fun Context.markThreadAsRead(threadId: Long) {
    val values = ContentValues().apply {
        put(Telephony.Sms.READ, 1)
    }

    val selection = "${Telephony.Sms.THREAD_ID} = ? AND ${Telephony.Sms.READ} = 0"
    val selectionArgs = arrayOf(threadId.toString())
    try {
        contentResolver.update(
            Telephony.Sms.Inbox.CONTENT_URI, values, selection, selectionArgs
        )
    } catch (e: SecurityException) {
    } catch (e: Exception) {
    }
}
//fun Context.markThreadAsUnreadSingle(threadId: Long) {
//
//    val projection = arrayOf(Telephony.Sms._ID)
//
//    val selection = "${Telephony.Sms.THREAD_ID} = ?"
//    val selectionArgs = arrayOf(threadId.toString())
//
//    val sortOrder = "${Telephony.Sms.DATE} DESC LIMIT 1"
//
//    try {
//        contentResolver.query(
//            Telephony.Sms.CONTENT_URI,
//            projection,
//            selection,
//            selectionArgs,
//            sortOrder
//        )?.use { cursor ->
//
//            if (cursor.moveToFirst()) {
//                val msgId = cursor.getLong(0)
//
//                val values = ContentValues().apply {
//                    put(Telephony.Sms.READ, 0)
//                }
//
//                contentResolver.update(
//                    Telephony.Sms.CONTENT_URI,
//                    values,
//                    "${Telephony.Sms._ID} = ?",
//                    arrayOf(msgId.toString())
//                )
//            }
//        }
//
//    } catch (_: Exception) {
//    }
//}

fun Context.markThreadAsUnreadSingle(threadId: Long) {

    try {
        // STEP 1: mark all as read
        val readValues = ContentValues().apply {
            put(Telephony.Sms.READ, 1)
        }

        contentResolver.update(
            Telephony.Sms.CONTENT_URI,
            readValues,
            "${Telephony.Sms.THREAD_ID} = ?",
            arrayOf(threadId.toString())
        )

        // STEP 2: get latest message
        val projection = arrayOf(Telephony.Sms._ID)
        val sortOrder = "${Telephony.Sms.DATE} DESC LIMIT 1"

        contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            projection,
            "${Telephony.Sms.THREAD_ID} = ?",
            arrayOf(threadId.toString()),
            sortOrder
        )?.use { cursor ->

            if (cursor.moveToFirst()) {
                val msgId = cursor.getLong(0)

                val unreadValues = ContentValues().apply {
                    put(Telephony.Sms.READ, 0)
                }

                // STEP 3: mark only latest as unread
                contentResolver.update(
                    Telephony.Sms.CONTENT_URI,
                    unreadValues,
                    "${Telephony.Sms._ID} = ?",
                    arrayOf(msgId.toString())
                )
            }
        }

    } catch (_: Exception) {
    }
}

fun extractUriFromString(inputString: String): String? {
    val uriPattern = "https?://\\S+"
    val pattern = Pattern.compile(uriPattern)
    val matcher = pattern.matcher(inputString)
    return if (matcher.find()) {
        matcher.group()
    } else {
        null
    }
}

fun Context.getNotificationBitmap(photoUri: String): Bitmap? {
    val size = resources.getDimension(R.dimen.notification_large_icon_size).toInt()
    if (photoUri.isEmpty()) {
        return null
    }

    val options = RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).centerCrop()

    return try {
        Glide.with(this).asBitmap().load(photoUri).apply(options)
            .apply(RequestOptions.circleCropTransform()).into(size, size).get()
    } catch (e: Exception) {
        null
    }
}

fun isShortCodeWithLetters(address: String): Boolean {
    return address.any { it.isLetter() }
}

private const val PATH = "com.messenger.phone.number.text.sms.service.apps.action."
const val MARK_AS_READ = PATH + "mark_as_read"
const val REPLY = PATH + "reply"
const val THREAD_ID = "thread_id"
const val THREAD_NUMBER = "thread_number"
val MyOnClick = "myOnClick"


var NOTIFICATION_CHANNEL = "messenger_sms_own"

fun generateRandomId(length: Int = 9): Long {
    val millis = DateTime.now(DateTimeZone.UTC).millis
    val random = abs(Random(millis).nextLong())
    return random.toString().takeLast(length).toLong()
}

val DEFAULT_CHANNEL_ID = "notifications_default"
val VIBRATE_PATTERN = longArrayOf(0, 200, 0, 200)

fun Context.getNotificationChannel(threadId: Long): NotificationChannel? {
    val channelId = buildNotificationChannelId(threadId)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        return notificationManager.notificationChannels
            .find { channel -> channel.id == channelId }
    }

    return null
}

fun buildNotificationChannelId(threadId: Long): String {
    return when (threadId) {
        0L -> DEFAULT_CHANNEL_ID
        else -> "notifications_$threadId"
    }
}


fun Context.createNotificationChannel(threadId: Long, title: String) {

    // Only proceed if the android version supports notification channels, and the channel hasn't
    // already been created
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || getNotificationChannel(threadId) != null) {
        return
    }

    val channel = when (threadId) {
        0L -> NotificationChannel(
            DEFAULT_CHANNEL_ID,
            "Default",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            lightColor = Color.WHITE
            enableVibration(true)
            vibrationPattern = VIBRATE_PATTERN
        }

        else -> {
            val channelId = buildNotificationChannelId(threadId)
            NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_HIGH).apply {
                enableLights(true)
                lightColor = Color.WHITE
                enableVibration(true)
                vibrationPattern = VIBRATE_PATTERN
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
//                setSound(
//                    config.ringtone().let(Uri::parse), AudioAttributes.Builder()
//                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
//                        .build()
//                )


                val soundUri = config.ringtone()
                    ?.takeIf { it.isNotBlank() }
                    ?.let { Uri.parse(it) }

                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()

                if (soundUri != null) {
                    setSound(soundUri, audioAttributes)
                }
            }
        }
    }
    try {
        notificationManager.createNotificationChannel(channel)
    } catch (e: IllegalArgumentException) {
        Log.e("NotificationChannel", "Failed to create channel", e)
    }
}

fun Context.removeDiacriticsIfNeeded(text: String): String {
    return if (true) text.normalizeString() else text
}

val Context.notificationManager: NotificationManager get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

val GENERIC_PERM_HANDLER = 100

const val Conversation_CLICK = PATH + "CLICK"
const val LIST_UPDATE = "LIST_UPDATE"

//fun Context.broadcastUpdateWidgetState() {
//    Intent(this, MyWidgetProvider::class.java).apply {
//        action = LIST_UPDATE
//        sendBroadcast(this)
//    }
//}

const val EXPORT_FILE_EXT = ".json"
val PICK_EXPORT_FILE_INTENT = 21
val PICK_IMPORT_SOURCE_INTENT = 11
const val EXPORT_MIME_TYPE = "application/json"

fun Context.updateTextColors(viewGroup: ViewGroup) {
    val isSystemMode = ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM
    val forceDark = ThemeModeManager.isDarkThemeActive(this)

    val textColor = if (isSystemMode) {
        MaterialColors.getColor(viewGroup, com.google.android.material.R.attr.colorOnSurface)
    } else {
        getProperTextColor()
    }

    val backgroundColor = if (isSystemMode) {
        MaterialColors.getColor(viewGroup, com.google.android.material.R.attr.colorSurface)
    } else if (forceDark) {
        ContextCompat.getColor(this, R.color.toolbarcolor3new)
    } else {
        ContextCompat.getColor(this, R.color.lock_edit_screen)
    }

    val accentColor = if (isSystemMode) {
        MaterialColors.getColor(viewGroup, R.attr.colorPrimary)
    } else {
        getProperPrimaryColor()
    }

    val cnt = viewGroup.childCount
    (0 until cnt).map { viewGroup.getChildAt(it) }.forEach {
        when (it) {
            is MyTextView -> it.setColors(textColor, accentColor, backgroundColor)
            is MyAppCompatSpinner -> it.setColors(textColor, accentColor, backgroundColor)
            is MyCompatRadioButton -> it.setColors(textColor, accentColor, backgroundColor)
            is MyAppCompatCheckbox -> it.setColors(textColor, accentColor, backgroundColor)
            is MyEditText -> it.setColors(textColor, accentColor, backgroundColor)
            is MyAutoCompleteTextView -> it.setColors(textColor, accentColor, backgroundColor)
            is MyFloatingActionButton -> it.setColors(textColor, accentColor, backgroundColor)
            is MySeekBar -> it.setColors(textColor, accentColor, backgroundColor)
            is MyButton -> it.setColors(textColor, accentColor, backgroundColor)
            is MyTextInputLayout -> it.setColors(textColor, accentColor, backgroundColor)
            is ViewGroup -> updateTextColors(it)
        }
    }
}

fun Context.getDefaultKeyboardHeight() =
    resources.getDimensionPixelSize(R.dimen.default_keyboard_height)

const val SCHEDULED_MESSAGE_ID = "scheduled_message_id"

fun Context.scheduleMessage(message: Conversation) {
    val pendingIntent = getScheduleSendPendingIntent(message)
    val triggerAtMillis = message.time

    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (triggerAtMillis != null) {
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }
}

fun Context.scheduleRemainder(remainder: Remindermodel) {
    val pendingIntent = getScheduleRemainderSendPendingIntent(remainder)
    val triggerAtMillis = remainder.reminderenddate?.toLong()

    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (triggerAtMillis != null) {
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }
}


fun Context.otpoutodelete() {

    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, 1)
    }

    val firstTriggerTime = calendar.timeInMillis
    val intervalMillis = 24 * 60 * 60 * 1000L
    val pendingIntent = getOtpdeletePendingIntent()
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setInexactRepeating(
        AlarmManager.RTC_WAKEUP, firstTriggerTime, intervalMillis, pendingIntent
    )
}


fun Context.AutonotificationStat(intro: Int, interval: Long, day: Int) {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 9)
        set(Calendar.MINUTE, 30)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, 0)
    }
    val firstTriggerTime = calendar.timeInMillis
    val pendingIntent = getAutoNotificationPendingIntent(intro)
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setInexactRepeating(
        AlarmManager.RTC_WAKEUP, firstTriggerTime, AlarmManager.INTERVAL_DAY, pendingIntent
//        AlarmManager.RTC_WAKEUP, firstTriggerTime,  1 * 24 * 60 * 60 * 1000L, pendingIntent
    )
}


fun Context.AutoMessageDeletedelete(interval: Long, day: Int) {
    Log.d("", "AutoMessageDeletedelete:<------> 1")
    Log.d("", "AutoMessageDeletedelete:<------> 3 ${interval}")
    Log.d("", "AutoMessageDeletedelete:<------> 4 ${day}")

    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, day)
    }

    val firstTriggerTime = calendar.timeInMillis
    val pendingIntent = getAutoMessagedeletePendingIntent()
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setInexactRepeating(
        AlarmManager.RTC_WAKEUP, firstTriggerTime, interval, pendingIntent
    )
}

fun Context.messagetraslateset(message: Conversation) {
    val pendingIntent = getnewmessageTraslatePendingIntent(message)
    val triggerAtMillis = message.time?.plus(30000L)

    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (triggerAtMillis != null) {
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }
}

fun Context.cancelOtpdeletePendingIntent() {
    val intent = Intent(this, OtpMessagedeleteReceiver::class.java)
    val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    PendingIntent.getBroadcast(this, 9664766, intent, flags).cancel()
}

fun Context.cancelAutoMessageDeletePendingIntent() {
    val intent = Intent(this, AutoMessagedeleteReceiver::class.java)
    val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    PendingIntent.getBroadcast(this, 9925998, intent, flags).cancel()
}


fun Context.getScheduleSendPendingIntent(message: Conversation): PendingIntent {
    val intent = Intent(this, ScheduledMessageReceiver::class.java)
    intent.putExtra(THREAD_ID, message.threadId)
    intent.putExtra(SCHEDULED_MESSAGE_ID, message.messageId)

    val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    return PendingIntent.getBroadcast(this, message.messageId!!.toInt(), intent, flags)
}

fun Context.getScheduleRemainderSendPendingIntent(remainder: Remindermodel): PendingIntent {
    "remainder data <--------------------> id 2 <-----> ${remainder.id}".log()
    val intent = Intent(this, ScheduledRemainderReceiver::class.java)
    intent.putExtra("remindertitle", remainder.remindertitle)
    intent.putExtra("reminderid", remainder.id.toString())
    val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    return PendingIntent.getBroadcast(this, remainder.id, intent, flags)
}

fun Context.isOnline(): Boolean {
    try {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Can't check, assume online to avoid blocking functionality
            return true
        }

        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                Log.i("Internet", "Cellular connection")
                true
            }

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                Log.i("Internet", "Wi-Fi connection")
                true
            }

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                Log.i("Internet", "Ethernet connection")
                true
            }

            else -> false
        }
    } catch (e: SecurityException) {
        Log.e("Internet", "SecurityException while checking network", e)
        return false // or true if you want to fail open
    } catch (e: Exception) {
        Log.e("Internet", "Unexpected error checking network", e)
        return false
    }
}

fun Context.isInternetAvailable(): Boolean {
    return isOnline()
}


fun Context.getnewmessageTraslatePendingIntent(message: Conversation): PendingIntent {
    val intent = Intent(this, NewMessageTraslateMessageReceiver::class.java)
    intent.putExtra(THREAD_ID, message.threadId)
    intent.putExtra(SCHEDULED_MESSAGE_ID, message.messageId)

    val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    return PendingIntent.getBroadcast(this, message.messageId!!.toInt(), intent, flags)
}


fun Context.cancelnewmessageTraslatePendingIntent(messageId: Long) {
    val intent = Intent(this, NewMessageTraslateMessageReceiver::class.java)
    val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    PendingIntent.getBroadcast(this, messageId.toInt(), intent, flags).cancel()
}


fun Context.getOtpdeletePendingIntent(): PendingIntent {
    val intent = Intent(this, OtpMessagedeleteReceiver::class.java)
    val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    return PendingIntent.getBroadcast(this, 9664766, intent, flags)
}

fun Context.getAutoNotificationPendingIntent(intro: Int): PendingIntent {
    val requestCode = Random.nextInt()
    val intent = Intent(this, AutoNotificationReceiver::class.java)
    intent.putExtra("intro", intro)
    intent.putExtra("requestCode", requestCode)
    val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    return PendingIntent.getBroadcast(this, 78744, intent, flags)
}

fun Context.cancelAutoNotificationPendingIntent() {
    val intent = Intent(this, AutoNotificationReceiver::class.java)
    val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    PendingIntent.getBroadcast(this, 78744, intent, flags).cancel()
}

fun Context.getAutoMessagedeletePendingIntent(): PendingIntent {
    val intent = Intent(this, AutoMessagedeleteReceiver::class.java)
    val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    return PendingIntent.getBroadcast(this, 9925998, intent, flags)
}

fun Context.cancelScheduleSendPendingIntent(messageId: Long) {
    val intent = Intent(this, ScheduledMessageReceiver::class.java)
    val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    PendingIntent.getBroadcast(this, messageId.toInt(), intent, flags).cancel()
}

fun Context.cancelRemainderPendingIntent(messageId: Long) {
    val intent = Intent(this, ScheduledRemainderReceiver::class.java)
    val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    PendingIntent.getBroadcast(this, messageId.toInt(), intent, flags).cancel()
}


suspend fun getSmsCount(contentResolver: ContentResolver, context: Context): Int {
    delay(10)
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_SMS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return 0
    }
    var totalMessages = 0
    var smsbody: String? = null

    // Use the official Telephony.Sms.CONTENT_URI
    val uri = Telephony.Sms.CONTENT_URI
    val projection = arrayOf(Telephony.Sms.BODY)

    var c: Cursor? = null
    try {
        c = contentResolver.query(uri, projection, null, null, null)
        c?.use {
            while (it.moveToNext()) {
                smsbody = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.BODY))
                smsbody?.let { _ -> totalMessages++ }
            }
        }
    } catch (e: SQLiteException) {
        e.printStackTrace()
    } finally {
        c?.close()
    }

    return totalMessages
}


fun findOtpInString(message: String): String {

    val messageex = message.lowercase(Locale.getDefault())
    var otpfinal = ""

    if (messageex.contains("otp for aadhaar")) {
        val words = message.split(" ")
        val wordArray = words.toTypedArray()
        wordArray.forEachIndexed { index, otp ->
            val otpremove = otp.replace(",", "")
            if (otpremove.isDigitsOnly()) {
                if (otp.length in 5..7) {
                    otpfinal = otpremove
                }
            }
        }
        return otpfinal
    } else if (messageex.contains("one time password") || messageex.contains("do not share") || messageex.contains(
            "don't share it"
        ) || messageex.contains("otp") || messageex.contains("pin") || messageex.contains("password") || messageex.contains(
            "access code"
        )
    ) {
        val words = message.split(" ")
        val wordArray = words.toTypedArray()
        wordArray.forEachIndexed { index, otp ->
            val otpremove = otp.replace(",", "")
            if (otpremove.isDigitsOnly()) {
                if (otp.length in 4..7) {
                    otpfinal = otpremove
                }
            }
        }
        return otpfinal
    } else {
        return ""
    }
}

fun Context.isPackageInstalled(packageName: String): Boolean {
    return try {
        packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}


@SuppressLint("UseCompatLoadingForColorStateLists")
fun Context.getcurruntseletedRightcolor(): Int {
    when (config.Swipe_Right) {
        "Archive" -> {
            RightColor = resources.getColor(R.color.Archive)
        }

        "Delete" -> {
            RightColor = resources.getColor(R.color.Delete)
        }

        "Call" -> {
            RightColor = resources.getColor(R.color.Call)
        }

        "Mark as read" -> {
            RightColor = resources.getColor(R.color.Mark_as_read)
        }

        "Pin" -> {
            RightColor = resources.getColor(R.color.Pin)
        }

        "Private Chat" -> {
            RightColor = resources.getColor(R.color.Private_Chat)
        }

        else -> {
            RightColor = resources.getColor(R.color.Archive)
        }
    }
    return RightColor
}

fun Context.getcurruntseletedRightsrc(): Int {
    when (config.Swipe_Right) {
        "Archive" -> {
            RightSrc = R.drawable.swipe_archive
        }

        "Delete" -> {
            RightSrc = (R.drawable.swipe_delete)
        }

        "Call" -> {
            RightSrc = (R.drawable.swipe_call)
        }

        "Mark as read" -> {
            RightSrc = (R.drawable.swipe_mark_as_read_new)
        }

        "Pin" -> {
            RightSrc = (R.drawable.swipe_pin)
        }

        "Private Chat" -> {
            RightSrc = (R.drawable.swipe_private_chat)
        }

        else -> {
            RightSrc = R.drawable.swipe_archive
        }
    }
    return RightSrc
}

fun Context.getcurruntseletedLeftsrc(): Int {
    when (config.Swipe_Left) {
        "Archive" -> {
            LeftSrc = R.drawable.swipe_archive
        }

        "Delete" -> {
            LeftSrc = (R.drawable.swipe_delete)
        }

        "Call" -> {
            LeftSrc = (R.drawable.swipe_call)
        }

        "Mark as read" -> {
            LeftSrc = (R.drawable.swipe_mark_as_read_new)
        }

        "Pin" -> {
            LeftSrc = (R.drawable.swipe_pin)
        }

        "Private Chat" -> {
            LeftSrc = (R.drawable.swipe_private_chat)
        }

        else -> {
            LeftSrc = R.drawable.swipe_archive
        }
    }
    return LeftSrc
}


@SuppressLint("UseCompatLoadingForColorStateLists")
fun Context.getcurruntseletedLeftcolor(): Int {
    when (config.Swipe_Left) {
        "Archive" -> {
            LeftColor = resources.getColor(R.color.Archive)
        }

        "Delete" -> {
            LeftColor = resources.getColor(R.color.Delete)
        }

        "Call" -> {
            LeftColor = resources.getColor(R.color.Call)
        }

        "Mark as read" -> {
            LeftColor = resources.getColor(R.color.Mark_as_read)
        }

        "Pin" -> {
            LeftColor = resources.getColor(R.color.Pin)
        }

        "Private Chat" -> {
            LeftColor = resources.getColor(R.color.Private_Chat)
        }

        else -> {
            LeftColor = resources.getColor(R.color.Archive)
        }
    }
    return LeftColor
}

fun Context.getCustomDrawable(color: Int): GradientDrawable {
    val drawable = GradientDrawable()
    drawable.shape = GradientDrawable.RECTANGLE
    drawable.color = ColorStateList.valueOf(resources.getColor(color))
    drawable.setStroke(
        resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._2sdp),
        Color.parseColor("#FC9E9E")
    )
    drawable.cornerRadius = config.messagecorner
    return drawable
}

fun Context.setLocal() {
    CoroutineScope(Dispatchers.IO).launch {
        val language = this@setLocal.config.setDefaultLanguage
        setLang(language)
    }
}

fun Context.setLang1(languageCode: String?) {
    CoroutineScope(Dispatchers.IO).launch {
        val local = languageCode?.let { Locale(it) }
        if (local != null) {
            Locale.setDefault(local)
        }
        isLanguageChanging = true
        val configuration = Configuration()
        configuration.locale = local
        this@setLang1.resources.updateConfiguration(
            configuration,
            this@setLang1.resources.displayMetrics
        )
        this@setLang1.config.setDefaultLanguage = languageCode
        if (languageCode != null) {
            Constants.Applang = languageCode
        }
    }
}

fun Context.setLang(languageCode: String?) {
    CoroutineScope(Dispatchers.IO).launch {
        val local = languageCode?.let { Locale(it) }
        if (local != null) {
            Locale.setDefault(local)
        }
        val configuration = Configuration()
        configuration.locale = local
        this@setLang.resources.updateConfiguration(
            configuration,
            this@setLang.resources.displayMetrics
        )
        this@setLang.config.setDefaultLanguage = languageCode
        if (languageCode != null) {
            Constants.Applang = languageCode
        }
    }
}

fun Context.restartApp() {
    val intent = Intent(getApplicationContext(), WelcomeActivity::class.java)
    val mPendingIntentId: Int = 200
    val mPendingIntent = PendingIntent.getActivity(
        getApplicationContext(),
        mPendingIntentId,
        intent,
        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val mgr = getApplicationContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    mgr[AlarmManager.RTC, System.currentTimeMillis() + 100] = mPendingIntent
    System.exit(0)
}


var thredidLock = 0L
var nameLock = ""
var mobileNumberLock = ""


fun Context.getTextSizeMS() = when (this.config.fontsizeselection) {
    "Small" -> resources.getDimension(R.dimen.smaller_text_size)
    "Normal" -> resources.getDimension(R.dimen.bigger_text_size)
    "Large" -> resources.getDimension(R.dimen.big_text_size)
    else -> resources.getDimension(R.dimen.extra_big_text_size)
}


fun Context.getTextSizeHometitleMS() = when (this.config.homemessagetitlesize) {
    "Small" -> resources.getDimension(com.intuit.sdp.R.dimen._12sdp) * 0.85f
    "Normal" -> resources.getDimension(com.intuit.sdp.R.dimen._15sdp) * 0.85f
    "Large" -> resources.getDimension(com.intuit.sdp.R.dimen._16sdp) * 0.85f
    else -> resources.getDimension(com.intuit.sdp.R.dimen._17sdp) * 0.85f
}


fun Context.getTextSizeForeNormal10MS() = when (this.config.homemessagetitlesize) {
    "Small" -> resources.getDimension(com.intuit.sdp.R.dimen._8sdp) * 0.85f
    "Normal" -> resources.getDimension(com.intuit.sdp.R.dimen._10sdp) * 0.85f
    "Large" -> resources.getDimension(com.intuit.sdp.R.dimen._11sdp) * 0.85f
    else -> resources.getDimension(com.intuit.sdp.R.dimen._12sdp) * 0.85f
}


fun Context.getTextSizeForeNormal13MS() = when (this.config.homemessagetitlesize) {
    "Small" -> resources.getDimension(com.intuit.sdp.R.dimen._11sdp) * 0.85f
    "Normal" -> resources.getDimension(com.intuit.sdp.R.dimen._13sdp) * 0.85f
    "Large" -> resources.getDimension(com.intuit.sdp.R.dimen._14sdp) * 0.85f
    else -> resources.getDimension(com.intuit.sdp.R.dimen._15sdp) * 0.85f
}


fun Context.getTextSizeForeNormal18MS() = when (this.config.homemessagetitlesize) {
    "Small" -> resources.getDimension(com.intuit.sdp.R.dimen._16sdp) * 0.85f
    "Normal" -> resources.getDimension(com.intuit.sdp.R.dimen._18sdp) * 0.85f
    "Large" -> resources.getDimension(com.intuit.sdp.R.dimen._19sdp) * 0.85f
    else -> resources.getDimension(com.intuit.sdp.R.dimen._20sdp) * 0.85f
}


fun Context.getTextSizeForeNormal8MS() = when (this.config.homemessagetitlesize) {
    "Small" -> resources.getDimension(com.intuit.sdp.R.dimen._6sdp) * 0.85f
    "Normal" -> resources.getDimension(com.intuit.sdp.R.dimen._8sdp) * 0.85f
    "Large" -> resources.getDimension(com.intuit.sdp.R.dimen._9sdp) * 0.85f
    else -> resources.getDimension(com.intuit.sdp.R.dimen._10sdp) * 0.85f
}


//fun containsURL(inputString: String): Boolean {
//    val regexPattern = "(?i)\\b(?:https?://|www\\.)\\S+\\b"
//    val regex = Regex(regexPattern)
//    val matches = regex.find(inputString)
//
//    if (matches != null) {
//        val url = matches.value
//        return URLUtil.isValidUrl(url)
//    }
//
//    return false
//}

fun containsURL(inputString: String): Boolean {
    // Define the regular expression pattern to match the specific URL format
    val regexPattern =
        "(?i)https?://www\\.google\\.com/maps\\?q=([-+]?\\d*\\.\\d+)(?:,\\s*([-+]?\\d*\\.\\d+))?\\b"
    val regex = Regex(regexPattern)
    val matches = regex.find(inputString)

    if (matches != null) {
        val url = matches.value
        return URLUtil.isValidUrl(url)
    }

    return false
}

fun containsURLAll(inputString: String): Boolean {
    // Define the regular expression pattern to match any URL format
    val regexPattern = "(?i)\\bhttps?://\\S+\\b"
    val regex = Regex(regexPattern)
    val matchResult = regex.find(inputString)

    if (matchResult != null) {
        val url = matchResult.value
        return URLUtil.isValidUrl(url)
    }

    return false
}

fun extractURL(str: String): String {
    // Creating an empty list to store URLs
    val urlList = mutableListOf<String>()

    // Regular Expression to extract URLs from the string
    val regexStr =
        """\b((?:https?|ftp|file):\/\/[a-zA-Z0-9+&@#\\/%?=~_|!:,.;]*[a-zA-Z0-9+&@#\\/%=~_|])"""

    // Compile the Regular Expression
    val r = Regex(regexStr, RegexOption.IGNORE_CASE)

    // Find the match between the string and the regular expression
    val m = r.findAll(str)

    // Find and store all the URLs in the list
    m.forEach { matchResult ->
        urlList.add(matchResult.value)
    }

    // If no URLs are found, print -1, otherwise print the URLs
    var urllink = ""
    if (urlList.isEmpty()) {
        println("-1")
    } else {
        for (url in urlList) {
            urllink = url
        }
    }
    if (urlList.isEmpty()) {
        return ""
    } else {
        return urllink
    }
}

fun Context.launchActivityIntent(intent: Intent) {
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        toast(R.string.no_app_found)
    } catch (e: Exception) {
        showErrorToast(e)
    }
}

@SuppressLint("Range")
suspend fun Context.getNewMessageCountForThreadId(threadId: Long): Int {
    var newMessageCount = 0
    if (ActivityCompat.checkSelfPermission(
            this, Manifest.permission.READ_CONTACTS
        ) != PackageManager.PERMISSION_DENIED && PermissionChecker.checkSelfPermission(
            this, Manifest.permission.READ_SMS
        ) == PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(
            this, Manifest.permission.SEND_SMS
        ) == PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(
            this, android.Manifest.permission.WRITE_CONTACTS
        ) == PermissionChecker.PERMISSION_GRANTED
    ) {
        val projection = arrayOf("count(*) as count")
        val selection = "thread_id = ? AND read = 0" // Unread messages for a specific thread
        val selectionArgs = arrayOf(threadId.toString())

        val uri = Telephony.Sms.CONTENT_URI
        val cursor: Cursor? = contentResolver.query(
            uri, projection, selection, selectionArgs, null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                newMessageCount = it.getInt(it.getColumnIndex("count"))
            }
        }

        cursor?.close()
    }
    return newMessageCount
}

suspend fun Context.retrieveContactName(contactData: Uri?): String? {
    if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        var contactName: String? = null
        contactData?.let { uri ->
            val cursor: Cursor? = this.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameColumnIndex: Int =
                        it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    contactName = it.getString(nameColumnIndex)
                }
            }
        }
        return contactName
    } else {
        return "Not Found"
    }
}

fun Context.formatTextWithItalic(originalText: String, italicText: String): String {
    return if (config.userpreferenceSignatureOnOff) {
        var mainstring = ""
//        mainstring = "${originalText + "\n" + "${"<br><i>$italicText</i></br>"}"}"
        mainstring = "${originalText + "\n" + "${"$italicText"}"}"
        mainstring
    } else {
        originalText
    }
}

fun Context.dialNumber(phoneNumber: String, callback: (() -> Unit)? = null) {
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

//fun Context.getContactNamesFromNumbers(
//    phoneNumbers: LongArray,
//    list: ArrayList<AddressBookContact>,
//): List<String> {
//    val contactNames = mutableListOf<String>()
//    for (phoneNumber in phoneNumbers) {
//        val valued = list.filter { it1 -> it1.phones == phoneNumber.toString() }
//        if (valued.isNotEmpty()) {
//            contactNames.add(valued[0].name)
//        } else {
//            contactNames.add(fetchContactIdFromPhoneNumber(phoneNumber.toString(), this))
//        }
//    }
//    return contactNames
//}

fun Context.getContactNamesFromNumbers(
    phoneNumbers: LongArray,
    list: ArrayList<AddressBookContact>,
): List<String> {

    val contactMap = list.associateBy { it.phones } // O(N)

    val contactNames = ArrayList<String>(phoneNumbers.size)

    for (phoneNumber in phoneNumbers) {
        val numberStr = phoneNumber.toString()

        val contact = contactMap[numberStr]

        if (contact != null) {
            contactNames.add(contact.name)
        } else {
            contactNames.add(
                fetchContactIdFromPhoneNumber(numberStr, this)
            )
        }
    }

    return contactNames
}

fun String.isGroupMessage(): Boolean {
    return this.length > 10
}


@SuppressLint("Range")
fun Context.getMessagesByThreadId(threadId: Long): List<Conversation> {
    val messageList = mutableListOf<Conversation>()
    var isNewUserMessage: Boolean = false
    var messagestatus: String = "SMS delivered"
    var messagetype: String = "normalmessage"
    var messageotp: String? = null
    val alredyincountact: ArrayList<Conversation> = arrayListOf()
    val conList: ArrayList<Conversation> = arrayListOf()
    var gropname: String? = null
    lateinit var phonrnumber: String
    val projection = arrayOf(
        Telephony.Sms._ID,
        Telephony.Sms.THREAD_ID,
        Telephony.Sms.ADDRESS,
        Telephony.Sms.BODY,
        Telephony.Sms.TYPE,
        Telephony.Sms.STATUS,
        Telephony.Sms.READ,
        Telephony.Sms.DATE
    )
    val selection = "${Telephony.Sms.THREAD_ID} = ?"
    val selectionArgs = arrayOf(threadId.toString())
    val sortOrder = "${Telephony.Sms.DATE} DESC"
    val uri = Telephony.Sms.CONTENT_URI

    contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)?.apply {
        val blockedNumbers = this@getMessagesByThreadId.getBlockedNumbers()

        while (moveToNext()) {
            val smsaddress = getString(getColumnIndex(Telephony.Sms.ADDRESS)) ?: ""
            val smsbody = getString(getColumnIndexOrThrow(Telephony.Sms.BODY)) ?: ""
            val smsdate = getString(getColumnIndexOrThrow(Telephony.Sms.DATE)) ?: ""
            val smsmessageid = getString(getColumnIndexOrThrow(Telephony.Sms._ID)) ?: ""
            val typeID = getString(getColumnIndexOrThrow(Telephony.Sms.TYPE)) ?: ""
            val thread = getLong(getColumnIndexOrThrow(Telephony.Sms.THREAD_ID))
            val status = getInt(getColumnIndexOrThrow(Telephony.Sms.STATUS))
            val read = getInt(getColumnIndexOrThrow(Telephony.Sms.READ)) == 1

            when (status) {
                -1 -> {
                    messagestatus = "SMS delivered"
                }

                0 -> {
                    messagestatus = "SMS delivered"
                }

                32 -> {
                    messagestatus = "Sending"
                }

                64 -> {
                    messagestatus = "Error"
                }

                else -> {
                    messagestatus = "SMS delivered"
                }
            }
            if (typeID.toInt() != 3) {
                phonrnumber = smsaddress ?: ""
                val number = phonrnumber

                val numbersArray: LongArray = if (number.contains("|")) {
                    try {
                        number.split("|").map { it.toLong() }.toLongArray()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        longArrayOf() // Empty LongArray in case of error
                    }
                } else {
                    try {
                        longArrayOf(number.toLong())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        longArrayOf() // Empty LongArray in case of error
                    }
                }

                val isgropmessage = numbersArray.size >= 2
                if (isgropmessage) {
                    val contactNames = getContactNamesFromNumbers(numbersArray, arrayListOf())
                    gropname = contactNames.joinToString()
                }

                val isBlocked = isNumberBlocked(number, blockedNumbers)
                isNewUserMessage = !read
                val isNewUserMessageCont = if (isNewUserMessage) {
                    1
                } else {
                    0
                }

                try {
                    val mess = findOtpInString(smsbody)
                    if (mess.isEmpty()) {
                        messagetype = "normalmessage"
                    } else {
                        messagetype = "otp"
                        messageotp = mess
                    }
                } catch (_: Exception) {

                }

                if (number.isPhoneNumber()) {

                    if (number.length <= 7) {
                        smsaddress?.let {
                            val conversation = Conversation(
                                0,
                                smsdate,
                                true,
                                number,
                                null,
                                false,
                                phonrnumber,
                                smsbody,
                                smsdate.toLong(),
                                typeID.toInt(),
                                true,
                                messagestatus,
                                messageId = smsmessageid.toLong(),
                                threadId = thread,
                                isblocknumber = isBlocked,
                                messagetype = messagetype,
                                messageotp = messageotp,
                                isnewmessage = isNewUserMessage,
                                newMessageCount = isNewUserMessageCont,
                                isgroupmessage = isgropmessage,
                                groupName = gropname
                            )
                            conList.add(conversation)
                        }
                    } else {

                        val isalredyincontact =
                            alredyincountact.filter { it.phoneNumber == phonrnumber }
                        if (isalredyincontact.isEmpty()) {

                            smsaddress?.let {
                                val conversation = Conversation(
                                    0,
                                    smsdate,
                                    true,
                                    getContactName(number, this@getMessagesByThreadId)!!,
                                    null,
                                    false,
                                    phonrnumber,
                                    smsbody,
                                    smsdate.toLong(),
                                    typeID.toInt(),
                                    true,
                                    messagestatus,
                                    messageId = smsmessageid.toLong(),
                                    threadId = thread,
                                    isblocknumber = isBlocked,
                                    messagetype = messagetype,
                                    messageotp = messageotp,
                                    isnewmessage = isNewUserMessage,
                                    newMessageCount = isNewUserMessageCont,
                                    isgroupmessage = isgropmessage,
                                    groupName = gropname
                                )
                                alredyincountact.add(conversation)
                                conList.add(conversation)

                            }

                        } else {
                            smsaddress?.let {
                                val conversation = Conversation(
                                    0,
                                    smsdate,
                                    true,
                                    isalredyincontact[0].title,
                                    null,
                                    false,
                                    phonrnumber,
                                    smsbody,
                                    smsdate.toLong(),
                                    typeID.toInt(),
                                    true,
                                    messagestatus,
                                    messageId = smsmessageid.toLong(),
                                    threadId = thread,
                                    isblocknumber = isBlocked,
                                    messagetype = messagetype,
                                    messageotp = messageotp,
                                    isnewmessage = isNewUserMessage,
                                    newMessageCount = isNewUserMessageCont,
                                    isgroupmessage = isgropmessage,
                                    groupName = gropname
                                )
                                conList.add(conversation)

                            }
                        }


                    }
                } else {
                    smsaddress?.let {
                        val conversation = Conversation(
                            0,
                            smsdate,
                            true,
                            number,
                            null,
                            false,
                            phonrnumber,
                            smsbody,
                            smsdate.toLong(),
                            typeID.toInt(),
                            true,
                            messagestatus,
                            messageId = smsmessageid.toLong(),
                            threadId = thread,
                            isblocknumber = isBlocked,
                            messagetype = messagetype,
                            messageotp = messageotp,
                            isnewmessage = isNewUserMessage,
                            newMessageCount = isNewUserMessageCont,
                            isgroupmessage = isgropmessage,
                            groupName = gropname
                        )
                        Log.d("TAG", "refreshSmsInbox: <------> 5 <----->")
                        conList.add(conversation)

                    }
                }
            }
        }
    }
    return messageList
}

fun getContactName(phoneNumber: String?, context: Context): String? {
    if (phoneNumber.isNullOrEmpty()) {
        return null
    }

    val uri = Uri.withAppendedPath(
        ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
        Uri.encode(phoneNumber)
    )
    val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)

    context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val contactName = cursor.getString(0)
            return contactName
        }
    }

    return phoneNumber
}

fun Context.insertSmsMessageForGroup(
    subId: Int,
    dest: String,
    text: String,
    timestamp: Long,
    threadId: Long,
    status: Int = Telephony.Sms.STATUS_NONE,
    type: Int = Telephony.Sms.MESSAGE_TYPE_OUTBOX,
): Uri {
    val response: Uri?
    val values = ContentValues().apply {
        put(Telephony.Sms.ADDRESS, dest)
        put(Telephony.Sms.DATE, timestamp)
        put(Telephony.Sms.READ, 1)
        put(Telephony.Sms.SEEN, 1)
        put(Telephony.Sms.BODY, text)

        // insert subscription id only if it is a valid one.
        if (subId != Settings.DEFAULT_SUBSCRIPTION_ID) {
            put(Telephony.Sms.SUBSCRIPTION_ID, subId)
        }

        if (status != Telephony.Sms.STATUS_NONE) {
            put(Telephony.Sms.STATUS, status)
        }
        if (type != Telephony.Sms.MESSAGE_TYPE_ALL) {
            put(Telephony.Sms.TYPE, type)
        }
        if (threadId != -1L) {
            put(Telephony.Sms.THREAD_ID, threadId)
        }
    }

    try {
        response = contentResolver.insert(Telephony.Sms.CONTENT_URI, values)
    } catch (e: Exception) {
        throw SmsException(SmsException.ERROR_PERSISTING_MESSAGE, e)
    }
    return response ?: throw SmsException(SmsException.ERROR_PERSISTING_MESSAGE)
}


suspend fun deleteAllSms(context: Context) {
    val contentResolver: ContentResolver = context.contentResolver
    val inboxUri: Uri = Uri.parse("content://sms")
    val sentUri: Uri = Telephony.Sms.Sent.CONTENT_URI
    val draftUri: Uri = Telephony.Sms.Draft.CONTENT_URI

    deleteMessages(contentResolver, inboxUri)
}

suspend fun deleteMessages(contentResolver: ContentResolver, uri: Uri) {
    contentResolver.delete(uri, null, null)
}

@SuppressLint("Range")
suspend fun Context.getSenderAndRecipientNumbers(messageId: Long): Triple<String?, String?, Int?> {
    val uri = Uri.parse("content://sms")
    val projection = arrayOf(Telephony.Sms.ADDRESS, Telephony.Sms.SUBSCRIPTION_ID)
    val selection = "${Telephony.Sms._ID} = ?"
    val selectionArgs = arrayOf(messageId.toString())

    var senderNumber: String? = null
    var recipientNumber: String? = null
    var simSlotIndex: Int? = null

    contentResolver.query(
        uri, projection, selection, selectionArgs, null
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            val address = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS))
            val subscriptionId = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.SUBSCRIPTION_ID))

            senderNumber = address
            recipientNumber = getPhoneNumber()
            simSlotIndex = getSimSlotIndex(subscriptionId)
        }
    }

    return Triple(senderNumber, recipientNumber, simSlotIndex)
}

@SuppressLint("MissingPermission", "HardwareIds")
private fun Context.getPhoneNumber(): String {
    val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return telephonyManager.line1Number ?: "Unknown"
}

private fun Context.getSimSlotIndex(subscriptionId: Int): Int {
    val subscriptionManager =
        getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_PHONE_STATE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return -1
    }
    val subscriptionInfo = subscriptionManager.getActiveSubscriptionInfo(subscriptionId)
    return subscriptionInfo?.simSlotIndex ?: -1
}

fun Context.getSubscriptionIdFromMessageId(messageId: Long): String {
    val contentResolver: ContentResolver = contentResolver
    val uri = Uri.parse("content://sms")
    val projection = arrayOf(Telephony.Sms.SUBSCRIPTION_ID)
    val selection = "_id=?"
    val selectionArgs = arrayOf(messageId.toString())
    var subscriptionId = -1

    try {
        val cursor: Cursor? = contentResolver.query(uri, projection, selection, selectionArgs, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(Telephony.Sms.SUBSCRIPTION_ID)
                subscriptionId = it.getInt(columnIndex)
            }
        }
    } catch (e: Exception) {
        Log.e("SmsManager", "Error retrieving subscription ID", e)
    }

    return getMobileNumber(
        if (subscriptionId == 3) {
            subscriptionId
        } else {
            subscriptionId
        }
    )
}

fun Context.getMobileNumber(subscriptionId: Int): String {
    Log.d("TAG", "getMobileNumber: getMobileNumber <--------> ${subscriptionId} ")
    val subscriptionManager = SubscriptionManager.from(this)
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_PHONE_STATE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return "Unknown"
    } else {
        val subscriptionInfo = subscriptionManager.getActiveSubscriptionInfo(subscriptionId)
        return subscriptionInfo?.number ?: "Unknown"
    }
}

@RequiresApi(Build.VERSION_CODES.S)
fun Context.openRequestExactAlarmSettings(appId: String) {
    if (isSPlus()) {
        Constants.isActivitychange = true
        Constants.isAdsClicking = true
        ispermissiobnor14 = true
        val uri = Uri.fromParts("package", appId, null)
        val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        intent.data = uri
        startActivity(intent)
    }
}

var ispermissiobnor14 = false

fun convertStringToArrayList(inputString: String, s: String): ArrayList<String> {
    if (s == "forname") {
        val stringArray = inputString.split(",").toTypedArray()
        val arrayList = ArrayList<String>()

        for (str in stringArray) {
            arrayList.add(str.trim())
        }

        return arrayList
    } else {
        val stringArray = inputString.split("|").toTypedArray()
        val arrayList = ArrayList<String>()

        for (str in stringArray) {
            arrayList.add(str.trim())
        }

        return arrayList
    }

}

fun removeZero(number: String): String {
    return if (number.startsWith('0')) {
        number.substring(1)
    } else {
        number
    }
}

fun deleteCountry(phone: String): String {
    val phoneInstance = PhoneNumberUtil.getInstance()
    try {
        val phoneNumber = phoneInstance.parse(phone, null)
        return phoneNumber?.nationalNumber?.toString() ?: phone
    } catch (_: Exception) {
    }
    return phone
}


fun phoneNumberWithoutCountryCode(phoneNumberWithCountryCode: String): String? {
    val countryCodeLengths = mapOf(
        "1" to setOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        "20" to setOf(2),
        "27" to setOf(2),
        "30" to setOf(2),
        "31" to setOf(2),
        "32" to setOf(2),
        "33" to setOf(2),
        "34" to setOf(2),
        "36" to setOf(2),
        "39" to setOf(2),
        "40" to setOf(2),
        "41" to setOf(2),
        "43" to setOf(2),
        "44" to setOf(2, 3, 4),
        "45" to setOf(2),
        "46" to setOf(2),
        "47" to setOf(2),
        "48" to setOf(2),
        "49" to setOf(2),
        "51" to setOf(2),
        "52" to setOf(2),
        "53" to setOf(2),
        "54" to setOf(2),
        "55" to setOf(2),
        "56" to setOf(2),
        "57" to setOf(2),
        "58" to setOf(2),
        "60" to setOf(2),
        "61" to setOf(2),
        "62" to setOf(2),
        "63" to setOf(2),
        "64" to setOf(2),
        "65" to setOf(2),
        "66" to setOf(2),
        "81" to setOf(2),
        "82" to setOf(2),
        "84" to setOf(2),
        "86" to setOf(2),
        "90" to setOf(2),
        "91" to setOf(2),
        "92" to setOf(2),
        "93" to setOf(2),
        "94" to setOf(2),
        "95" to setOf(2),
        "98" to setOf(2),
        "211" to setOf(3),
        "212" to setOf(3),
        "213" to setOf(3),
        "216" to setOf(3),
        "218" to setOf(3),
        "220" to setOf(3),
        "221" to setOf(3),
        "222" to setOf(3),
        "223" to setOf(3),
        "224" to setOf(3),
        "225" to setOf(3),
        "226" to setOf(3),
        "227" to setOf(3),
        "228" to setOf(3),
        "229" to setOf(3),
        "230" to setOf(3),
        "231" to setOf(3),
        "232" to setOf(3),
        "233" to setOf(3),
        "234" to setOf(3),
        "235" to setOf(3),
        "236" to setOf(3),
        "237" to setOf(3),
        "238" to setOf(3),
        "239" to setOf(3),
        "240" to setOf(3),
        "241" to setOf(3),
        "242" to setOf(3),
        "243" to setOf(3),
        "244" to setOf(3),
        "245" to setOf(3),
        "246" to setOf(3),
        "247" to setOf(3),
        "248" to setOf(3),
        "249" to setOf(3),
        "250" to setOf(3),
        "251" to setOf(3),
        "252" to setOf(3),
        "253" to setOf(3),
        "254" to setOf(3),
        "255" to setOf(3),
        "256" to setOf(3),
        "257" to setOf(3),
        "258" to setOf(3),
        "260" to setOf(3),
        "261" to setOf(3),
        "262" to setOf(3),
        "263" to setOf(3),
        "264" to setOf(3),
        "265" to setOf(3),
        "266" to setOf(3),
        "267" to setOf(3),
        "268" to setOf(3),
        "269" to setOf(3),
        "290" to setOf(3),
        "291" to setOf(3),
        "297" to setOf(3),
        "298" to setOf(3),
        "299" to setOf(3),
        "350" to setOf(3),
        "351" to setOf(3),
        "352" to setOf(3),
        "353" to setOf(3),
        "354" to setOf(3),
        "355" to setOf(3),
        "356" to setOf(3),
        "357" to setOf(3),
        "358" to setOf(3),
        "359" to setOf(3),
        "370" to setOf(3),
        "371" to setOf(3),
        "372" to setOf(3),
        "373" to setOf(3),
        "374" to setOf(3),
        "375" to setOf(3),
        "376" to setOf(3),
        "377" to setOf(3),
        "378" to setOf(3),
        "379" to setOf(3),
        "380" to setOf(3),
        "381" to setOf(3),
        "382" to setOf(3),
        "383" to setOf(3),
        "385" to setOf(3),
        "386" to setOf(3),
        "387" to setOf(3),
        "389" to setOf(3),
        "420" to setOf(3),
        "421" to setOf(3),
        "423" to setOf(3),
        "500" to setOf(3),
        "501" to setOf(3),
        "502" to setOf(3),
        "503" to setOf(3),
        "504" to setOf(3),
        "505" to setOf(3),
        "506" to setOf(3),
        "507" to setOf(3),
        "508" to setOf(3),
        "509" to setOf(3),
        "590" to setOf(3),
        "591" to setOf(3),
        "592" to setOf(3),
        "593" to setOf(3),
        "595" to setOf(3),
        "597" to setOf(3),
        "598" to setOf(3),
        "599" to setOf(3),
        "670" to setOf(3),
        "671" to setOf(3),
        "672" to setOf(3),
        "673" to setOf(3),
        "674" to setOf(3),
        "675" to setOf(3),
        "676" to setOf(3),
        "677" to setOf(3),
        "678" to setOf(3),
        "679" to setOf(3),
        "680" to setOf(3),
        "681" to setOf(3),
        "682" to setOf(3),
        "683" to setOf(3),
        "684" to setOf(3),
        "685" to setOf(3),
        "686" to setOf(3),
        "687" to setOf(3),
        "688" to setOf(3),
        "689" to setOf(3),
        "690" to setOf(3),
        "691" to setOf(3),
        "692" to setOf(3),
        "850" to setOf(3),
        "852" to setOf(3),
        "853" to setOf(3),
        "855" to setOf(3),
        "856" to setOf(3),
        "880" to setOf(3),
        "886" to setOf(3),
        "960" to setOf(3),
        "961" to setOf(3),
        "962" to setOf(3),
        "963" to setOf(3),
        "964" to setOf(3),
        "965" to setOf(3),
        "966" to setOf(3),
        "967" to setOf(3),
        "968" to setOf(3),
        "970" to setOf(3),
        "971" to setOf(3),
        "972" to setOf(3),
        "973" to setOf(3),
        "974" to setOf(3),
        "975" to setOf(3),
        "976" to setOf(3),
        "977" to setOf(3),
        "992" to setOf(3),
        "993" to setOf(3),
        "994" to setOf(3),
        "995" to setOf(3),
        "996" to setOf(3),
        "998" to setOf(3),
    )

    val countryCode = countryCodeLengths.keys.find { phoneNumberWithCountryCode.startsWith(it) }
    return countryCode?.let { countryCode ->
        val lengths = countryCodeLengths[countryCode]
        lengths?.forEach { length ->
            val possibleNumber = phoneNumberWithCountryCode.substring(
                countryCode.length,
                countryCode.length + length
            )
            if (possibleNumber.matches(Regex("\\d{$length}"))) {
                return phoneNumberWithCountryCode.substring(countryCode.length + length).trim()
            }
        }
        null
    } ?: phoneNumberWithCountryCode


}

const val DEMO_LATITUDE = 41.4036299
const val DEMO_LONGITUDE = 2.1743558

fun Context.bitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 10, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun Context.bitmapToBase64(bitmap: Bitmap, maxBase64Size: Int): String? {
    var scaledBitmap = bitmap

    // Resize the bitmap if needed to reduce its dimensions
    val originalWidth = bitmap.width
    val originalHeight = bitmap.height
    val maxSize = originalWidth.coerceAtLeast(originalHeight)

    if (maxSize > 1024) { // If bitmap dimension is greater than 1024
        val scaleRatio = 1024f / maxSize.toFloat()
        val newWidth = (originalWidth * scaleRatio).toInt()
        val newHeight = (originalHeight * scaleRatio).toInt()
        scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    val outputStream = ByteArrayOutputStream()
    val quality = 70 // Compression quality
    var base64String: String? = null

    // Compress the bitmap and check if the resulting Base64 string size is within the limit
    do {
        outputStream.reset() // Reset the output stream
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val byteArray = outputStream.toByteArray()
        base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
    } while (base64String?.length!! > maxBase64Size && quality > 0)

    return if (base64String.length <= maxBase64Size) {
        base64String
    } else {
        null // Failed to compress within the size limit
    }
}

// Function to get Bitmap from URI
fun Context.getBitmapFromUri(uri: Uri): Bitmap? {
    return try {
        val inputStream = contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        null
    }
}

fun Context.base64ToBitmap(base64String: String): Bitmap? {
    val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}

fun isBase64(input: String): Boolean {
//    return try {
//        val data: ByteArray = Base64.decode(input, Base64.DEFAULT)
//        Base64.decode(input, Base64.DEFAULT)
//        true
//    } catch (e: IllegalArgumentException) {
//        false
//    }

    return try {
        val data: ByteArray = Base64.decode(input, Base64.DEFAULT)
        val text = String(data, charset("UTF-8"))
        true
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
        false
    } catch (ar: java.lang.IllegalArgumentException) {
        ar.printStackTrace()
        false
    }
}

//fun isBase64(input: String): Boolean {
//    return try {
//        val decodedBytes = Base64.decode(input, Base64.DEFAULT)
//        val decodedString =  String(decodedBytes, charset("UTF-8"))
//        input == Base64.encodeToString(decodedString.toByteArray(), Base64.DEFAULT)
//    } catch (e: IllegalArgumentException) {
//        false
//    }
//}


fun encodeImageToHex(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return bytesToHex(byteArray)
}

// Function to decode hexadecimal string to Bitmap
fun decodeHexToImage(hexString: String): Bitmap {
    val byteArray = hexToBytes(hexString)
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}

// Function to convert byte array to hexadecimal string
fun bytesToHex(bytes: ByteArray): String {
    val hexArray = "0123456789ABCDEF".toCharArray()
    val hexChars = CharArray(bytes.size * 2)
    for (i in bytes.indices) {
        val v = bytes[i].toInt() and 0xFF
        hexChars[i * 2] = hexArray[v ushr 4]
        hexChars[i * 2 + 1] = hexArray[v and 0x0F]
    }
    return String(hexChars)
}

// Function to convert hexadecimal string to byte array
fun hexToBytes(hexString: String): ByteArray {
    val len = hexString.length
    val data = ByteArray(len / 2)
    var i = 0
    while (i < len) {
        data[i / 2] = ((Character.digit(hexString[i], 16) shl 4) + Character.digit(
            hexString[i + 1],
            16
        )).toByte()
        i += 2
    }
    return data
}

fun Context.getFileSizeFromUri(uri: Uri): Long {
    val assetFileDescriptor = try {
        contentResolver.openAssetFileDescriptor(uri, "r")
    } catch (e: FileNotFoundException) {
        null
    }

    // uses ParcelFileDescriptor#getStatSize underneath if failed
    val length = assetFileDescriptor?.use { it.length } ?: FILE_SIZE_NONE
    if (length != -1L) {
        return length
    }

    // if "content://" uri scheme, try contentResolver table
    if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
        return contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)
            ?.use { cursor ->
                // maybe shouldn't trust ContentResolver for size:
                // https://stackoverflow.com/questions/48302972/content-resolver-returns-wrong-size
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex == -1) {
                    return@use FILE_SIZE_NONE
                }
                cursor.moveToFirst()
                return try {
                    cursor.getLong(sizeIndex)
                } catch (_: Throwable) {
                    FILE_SIZE_NONE
                }
            } ?: FILE_SIZE_NONE
    } else {
        return FILE_SIZE_NONE
    }
}

fun encodeToHex(input: String): String {
    return input.toByteArray().joinToString("") { "%02x".format(it) }
}

fun decodeFromHex(input: String): String {
    val hexChars = input.chunked(2)
    val byteArray = ByteArray(hexChars.size) { hexChars[it].toInt(16).toByte() }
    return String(byteArray)
}

fun isHex(input: String): Boolean {
    val hexRegex = Regex("[0-9A-Fa-f]+")
    return hexRegex.matches(input)
}

fun Context.openNotificationSettings() {
    if (isOreoPlus()) {
        val intent = Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        intent.putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, packageName)
        startActivity(intent)
    } else {
        // For Android versions below Oreo, you can't directly open the app's notification settings.
        // You can open the general notification settings instead.
        val intent = Intent(android.provider.Settings.ACTION_SETTINGS)
        startActivity(intent)
    }
}

fun Context.getsendbuttonforcustoombutton(fordisable: Boolean, tredid: String): Int {
    val config = this.config
    var customwallpaperselected: Int = 1
    val getProfilecolor = config.getProfileColor(tredid.toString())
    if (config.isAllChatColor) {
        Log.d("jigar", "getsendbuttonforcustoombutton: <----------------> 1")
        val getAllProfilecolor = config.getProfileColorAll(tredid.toString())
        if (getAllProfilecolor != null) {
            Log.d("jigar", "getsendbuttonforcustoombutton: <----------------> 2")
            customwallpaperselected = getAllProfilecolor.shareprefselected
        } else {
            Log.d("jigar", "getsendbuttonforcustoombutton: <----------------> 3")
            customwallpaperselected = config.customwallpaperselected
        }
    } else {
        Log.d("jigar", "getsendbuttonforcustoombutton: <----------------> 4")
        customwallpaperselected = 1
    }

    getProfilecolor?.let {
        Log.d("jigar", "getsendbuttonforcustoombutton: <----------------> 5")
        customwallpaperselected = it.shareprefselected
    }

    if (!fordisable) {
        Log.d("jigar", "getsendbuttonforcustoombutton: <----------------> 6")
        when (customwallpaperselected) {
            1 -> {
                return R.drawable.send_new_message_final
            }

            2 -> {
                return R.drawable.send_new_message_final
            }

            3 -> {
                return R.drawable.send_new_message_final_three
            }

            4 -> {
                return R.drawable.send_new_message_final_four
            }

            5 -> {
                return R.drawable.send_new_message_final_five
            }

            6 -> {
                return R.drawable.send_new_message_final_six
            }

            7 -> {
                return R.drawable.send_new_message_final_seven
            }

            8 -> {
                return R.drawable.send_new_message_final_eight
            }

            9 -> {
                return R.drawable.send_new_message_final_nine
            }

            10 -> {
                return R.drawable.send_new_message_final_ten
            }

            11 -> {
                return R.drawable.send_new_message_final_eleven
            }

            12 -> {
                return R.drawable.send_new_message_final_twelve
            }

            else -> {
                return R.drawable.send_new_message_final
            }
        }
    } else {
        Log.d("jigar", "getsendbuttonforcustoombutton: <----------------> 7")
        when (customwallpaperselected) {
            1 -> {
                return R.drawable.send_new_message_final_disable
            }

            2 -> {
                return R.drawable.send_new_message_final_disable
            }

            3 -> {
                return R.drawable.send_new_message_final_disable_three
            }

            4 -> {
                return R.drawable.send_new_message_final_disable_four
            }

            5 -> {
                return R.drawable.send_new_message_final_disable_five
            }

            6 -> {
                return R.drawable.send_new_message_final_disable_six
            }

            7 -> {
                return R.drawable.send_new_message_final_disable_seven
            }

            8 -> {
                return R.drawable.send_new_message_final_disable_eight
            }

            9 -> {
                return R.drawable.send_new_message_final_disable_nine
            }

            10 -> {
                return R.drawable.send_new_message_final_disable_ten
            }

            11 -> {
                return R.drawable.send_new_message_final_disable_eleven
            }

            12 -> {
                return R.drawable.send_new_message_final_disable_twelve
            }

            else -> {
                return R.drawable.send_new_message_final_disable
            }
        }
    }
}

fun Context.getsendbuttonforcustoombuttonAB(fordisable: Boolean, tredid: String): Int {
    val config = this.config
    var customwallpaperselected: Int = 1
    val getProfilecolor = config.getProfileColor(tredid.toString())
    if (config.isAllChatColor) {
        Log.d("jigar", "getsendbuttonforcustoombutton: <----------------> 1")
        val getAllProfilecolor = config.getProfileColorAll(tredid.toString())
        if (getAllProfilecolor != null) {
            Log.d("jigar", "getsendbuttonforcustoombutton: <----------------> 2")
            customwallpaperselected = getAllProfilecolor.shareprefselected
        } else {
            Log.d("jigar", "getsendbuttonforcustoombutton: <----------------> 3")
            customwallpaperselected = config.customwallpaperselected
        }
    } else {
        Log.d("jigar", "getsendbuttonforcustoombutton: <----------------> 4")
        customwallpaperselected = 1
    }

    getProfilecolor?.let {
        Log.d("jigar", "getsendbuttonforcustoombutton: <----------------> 5")
        customwallpaperselected = it.shareprefselected
    }

    if (!fordisable) {
        Log.d("jigar", "getsendbuttonforcustoombutton: <----------------> 6")
//        when (customwallpaperselected) {
//            1 -> {
//                return R.drawable.sendmessagebutton2
//            }
//
//            2 -> {
//                return R.drawable.sendmessagebutton2
//            }
//
//            3 -> {
//                return R.drawable.send_new_message_final_ab_three
//            }
//
//            4 -> {
//                return R.drawable.send_new_message_final_ab_four
//            }
//
//            5 -> {
//                return R.drawable.send_new_message_final_ab_five
//            }
//
//            6 -> {
//                return R.drawable.send_new_message_final_ab_six
//            }
//
//            7 -> {
//                return R.drawable.send_new_message_final_ab_seven
//            }
//
//            8 -> {
//                return R.drawable.send_new_message_final_ab_eight
//            }
//
//            9 -> {
//                return R.drawable.send_new_message_final_ab_nine
//            }
//
//            10 -> {
//                return R.drawable.send_new_message_final_ab_ten
//            }
//
//            11 -> {
//                return R.drawable.send_new_message_final_ab_eleven
//            }
//
//            12 -> {
//                return R.drawable.send_new_message_final_ab_twelve
//            }
//
//            else -> {
//                return R.drawable.sendmessagebutton2
//            }
//        }

        when (customwallpaperselected) {
            1 -> {
                return R.drawable.sendmessageappimage
            }

            2 -> {
                return R.drawable.sendmessageappimage
            }

            3 -> {
                return R.drawable.sendmessageappimage
            }

            4 -> {
                return R.drawable.sendmessageappimage
            }

            5 -> {
                return R.drawable.sendmessageappimage
            }

            6 -> {
                return R.drawable.sendmessageappimage
            }

            7 -> {
                return R.drawable.sendmessageappimage
            }

            8 -> {
                return R.drawable.sendmessageappimage
            }

            9 -> {
                return R.drawable.sendmessageappimage
            }

            10 -> {
                return R.drawable.sendmessageappimage
            }

            11 -> {
                return R.drawable.sendmessageappimage
            }

            12 -> {
                return R.drawable.sendmessageappimage
            }

            else -> {
                return R.drawable.sendmessageappimage
            }
        }
    } else {
        Log.d("jigar", "getsendbuttonforcustoombutton: <----------------> 7")
//        when (customwallpaperselected) {
//            1 -> {
//                return R.drawable.sendmessagebutton2
//            }
//
//            2 -> {
//                return R.drawable.sendmessagebutton2
//            }
//
//            3 -> {
//                return R.drawable.send_new_message_final_disable_ab_three
//            }
//
//            4 -> {
//                return R.drawable.send_new_message_final_disable_ab_four
//            }
//
//            5 -> {
//                return R.drawable.send_new_message_final_disable_ab_five
//            }
//
//            6 -> {
//                return R.drawable.send_new_message_final_disable_ab_six
//            }
//
//            7 -> {
//                return R.drawable.send_new_message_final_disable_ab_seven
//            }
//
//            8 -> {
//                return R.drawable.send_new_message_final_disable_ab_eight
//            }
//
//            9 -> {
//                return R.drawable.send_new_message_final_disable_ab_nine
//            }
//
//            10 -> {
//                return R.drawable.send_new_message_final_disable_ab_ten
//            }
//
//            11 -> {
//                return R.drawable.send_new_message_final_disable_ab_eleven
//            }
//
//            12 -> {
//                return R.drawable.send_new_message_final_disable_ab_twelve
//            }
//
//            else -> {
//                return R.drawable.sendmessagebutton2
//            }
//        }

        when (customwallpaperselected) {
            1 -> {
                return R.drawable.sendmessageappimage
            }

            2 -> {
                return R.drawable.sendmessageappimage
            }

            3 -> {
                return R.drawable.sendmessageappimage
            }

            4 -> {
                return R.drawable.sendmessageappimage
            }

            5 -> {
                return R.drawable.sendmessageappimage
            }

            6 -> {
                return R.drawable.sendmessageappimage
            }

            7 -> {
                return R.drawable.sendmessageappimage
            }

            8 -> {
                return R.drawable.sendmessageappimage
            }

            9 -> {
                return R.drawable.sendmessageappimage
            }

            10 -> {
                return R.drawable.sendmessageappimage
            }

            11 -> {
                return R.drawable.sendmessageappimage
            }

            12 -> {
                return R.drawable.sendmessageappimage
            }

            else -> {
                return R.drawable.sendmessagebutton2
            }
        }
    }
}

suspend fun Context.createCustomDrawable(wrontOTP: Boolean): Drawable =
    withContext(Dispatchers.IO) {
        val gradientDrawable = GradientDrawable()
        val solidColor = ContextCompat.getColor(
            this@createCustomDrawable, when (config.activeThemeSelection) {
                1 -> {
                    R.color.lock_edit_screen
                }

                2 -> {
                    R.color.toolbarcolor2
                }

                3 -> {
                    R.color.toolbarcolor3new
                }

                else -> {
                    R.color.toolbarcolor2
                }
            }
        )
        gradientDrawable.setColor(solidColor)
        val strokeColor = ContextCompat.getColor(
            this@createCustomDrawable, if (wrontOTP) {
                R.color.red
            } else {
                R.color.Call
            }
        )
        val strokeWidth = resources.getDimension(com.intuit.sdp.R.dimen._1sdp).toInt()
        gradientDrawable.setStroke(strokeWidth, strokeColor)
        return@withContext gradientDrawable
    }

suspend fun Context.createCustomDrawableNew(
    topLeftRadiusResId: Int,
    topRightRadiusResId: Int,
    bottomLeftRadiusResId: Int,
    bottomRightRadiusResId: Int,
    solidColorResId: Int,
    strokeColorResId: Int,
    strokeWidthResId: Int,
): Drawable = withContext(Dispatchers.IO) {
    val gradientDrawable = GradientDrawable()
    val topLeftRadius = this@createCustomDrawableNew.resources.getDimension(topLeftRadiusResId)
    val topRightRadius = this@createCustomDrawableNew.resources.getDimension(topRightRadiusResId)
    val bottomLeftRadius =
        this@createCustomDrawableNew.resources.getDimension(bottomLeftRadiusResId)
    val bottomRightRadius =
        this@createCustomDrawableNew.resources.getDimension(bottomRightRadiusResId)
    gradientDrawable.cornerRadii = floatArrayOf(
        topLeftRadius, topLeftRadius,
        topRightRadius, topRightRadius,
        bottomRightRadius, bottomRightRadius,
        bottomLeftRadius, bottomLeftRadius
    )
    val solidColor = ContextCompat.getColor(this@createCustomDrawableNew, solidColorResId)
    gradientDrawable.setColor(solidColor)
    val strokeColor = ContextCompat.getColor(this@createCustomDrawableNew, strokeColorResId)
    val strokeWidth = this@createCustomDrawableNew.resources.getDimension(strokeWidthResId)
        .toInt() // Convert dp to pixels
    gradientDrawable.setStroke(strokeWidth, strokeColor)
    return@withContext gradientDrawable
}

suspend fun Context.createCustomDrawable(
    cornerRadiusResId: Int, solidColorResId: Int, strokeColorResId: Int, strokeWidthResId: Int,
): Drawable = withContext(Dispatchers.IO) {
    val gradientDrawable = GradientDrawable()
    val cornerRadius = this@createCustomDrawable.resources.getDimension(cornerRadiusResId)
    gradientDrawable.cornerRadius = cornerRadius
    val solidColor = ContextCompat.getColor(this@createCustomDrawable, solidColorResId)
    gradientDrawable.setColor(solidColor)
    val strokeColor = ContextCompat.getColor(this@createCustomDrawable, strokeColorResId)
    val strokeWidth = this@createCustomDrawable.resources.getDimension(strokeWidthResId)
        .toInt() // Convert dp to pixels
    gradientDrawable.setStroke(strokeWidth, strokeColor)
    return@withContext gradientDrawable
}

suspend fun Context.createCustomNewDrawable(
    cornerRadiusResId: Int, solidColorResId: Int, strokeColorResId: Int, strokeWidthResId: Int,
): Drawable = withContext(Dispatchers.IO) {
    val gradientDrawable = GradientDrawable()
    val cornerRadius = this@createCustomNewDrawable.resources.getDimension(cornerRadiusResId)
    gradientDrawable.cornerRadius = cornerRadius
    val solidColor = ContextCompat.getColor(this@createCustomNewDrawable, solidColorResId)
    gradientDrawable.setColor(solidColor)
    val strokeColor = strokeColorResId
    val strokeWidth = this@createCustomNewDrawable.resources.getDimension(strokeWidthResId)
        .toInt() // Convert dp to pixels
    gradientDrawable.setStroke(strokeWidth, strokeColor)
    return@withContext gradientDrawable
}


var backgroundcolorcustomwallpaperold: String = ""
var toolbarcolorcustomwallpaperold: String = ""
var inmessagecolorcustomwallpaperold: String = ""
var outmessagecolorcustomwallpaperold: String = ""
var smartreplycolorold: String = ""
var shareprefselectedoldold: Int = 2

var isresetbuttonclickcon = false

var istemereselect = false


fun View.fadeIn() {
    ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f).apply {
        duration = 0 // Adjust duration as needed
        start()
    }
    this.visibility = View.VISIBLE
}

fun View.fadeOut() {
    ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0f).apply {
        duration = 0 // Adjust duration as needed
        start()
    }.doOnEnd {
        this.visibility = View.GONE
    }
}

var iscustomwallpaersetterdid: Long? = null
var iscustomwallpaersetterdidmovilenumber: String? = null


fun Context.sendOfferActivity(isfirsttime: Boolean = false) {
    if (!BaseSharedPreferences(this@sendOfferActivity).mIS_SUBSCRIBED!!) {
        startActivity(
            Intent(this, TimerOfferActivity::class.java).putExtra(
                "AppOpen",
                "SettingsActivity"
            ).putExtra("isfirsttime", isfirsttime)
        )
    }
}


/*******************    💫 Codegeex Suggestion    *******************/

fun Context.sendsubactivityFromWel(isfirsttime: Boolean = false) {
    if (BaseSharedPreferences(this).mIS_SUBSCRIBED) return
    if (!NetworkHelper.isOnline(this)) {
        toast(getString(R.string.please_check_internet_connection))
        return
    }
    if (this is Activity) {
        if (isFinishing || isDestroyed) {
            Log.w("sendsubactivity", "Activity is finishing/destroyed. Not starting.")
            return
        }
    }
    fun buildIntent(target: Class<*>) = Intent(this, target)
        .putExtra("AppOpen", "SplashScreen")

        .putExtra("isfirsttime", isfirsttime)
        .apply {
            if (this@sendsubactivityFromWel !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

    val intent = buildIntent(Paywall_dynamic_Activity::class.java)
    try {
        startActivity(intent)
        Handler(Looper.getMainLooper()).postDelayed({
            config.userfisttimeshowsubscreen = false
        }, 1000)
    } catch (e: Exception) {
    }
}

fun Context.sendsubactivityFromVarA(isfirsttime: Boolean = false) {
    if (BaseSharedPreferences(this).mIS_SUBSCRIBED) return
    if (!NetworkHelper.isOnline(this)) {
        toast(getString(R.string.please_check_internet_connection))
        return
    }
    if (this is Activity) {
        if (isFinishing || isDestroyed) {
            Log.w("sendsubactivity", "Activity is finishing/destroyed. Not starting.")
            return
        }
    }
    fun buildIntent(target: Class<*>) = Intent(this, target)
        .putExtra("AppOpen", "SettingsActivity")
        .putExtra("from", "VarA")

        .putExtra("isfirsttime", isfirsttime)
        .apply {
            if (this@sendsubactivityFromVarA !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

    val intent = buildIntent(Paywall_dynamic_Activity::class.java)
    try {
        startActivity(intent)
        Handler(Looper.getMainLooper()).postDelayed({
            config.userfisttimeshowsubscreen = false
        }, 1000)
    } catch (e: Exception) {
    }
}


fun Context.sendsubactivityOldnew(isfirsttime: Boolean = false) {
    if (BaseSharedPreferences(this).mIS_SUBSCRIBED) return
    if (!NetworkHelper.isOnline(this)) {
        toast(getString(R.string.please_check_internet_connection))
        return
    }
    if (this is Activity) {
        if (isFinishing || isDestroyed) {
            Log.w("sendsubactivity", "Activity is finishing/destroyed. Not starting.")
            return
        }
    }
    fun buildIntent(target: Class<*>) = Intent(this, target)
        .putExtra("AppOpen", "setDefault")

        .putExtra("isfirsttime", isfirsttime)
        .apply {
            if (this@sendsubactivityOldnew !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

    val intent = buildIntent(Paywall_dynamic_Activity::class.java)
    try {
        startActivity(intent)
        Handler(Looper.getMainLooper()).postDelayed({
            config.userfisttimeshowsubscreen = false
        }, 1000)
    } catch (e: Exception) {
    }
}

/****************  1a38bf085945494e8d44c355ca649a4d  ****************/

fun Context.sendsubactivitydynamic(isfirsttime: Boolean = false) {
    if (BaseSharedPreferences(this).mIS_SUBSCRIBED) return
    if (!NetworkHelper.isOnline(this)) {
        toast(getString(R.string.please_check_internet_connection))
        return
    }
    if (this is Activity) {
        if (isFinishing || isDestroyed) {
            Log.w("sendsubactivity", "Activity is finishing/destroyed. Not starting.")
            return
        }
    }
    fun buildIntent(target: Class<*>) = Intent(this, target)
        .putExtra("AppOpen", "SettingsActivity")
        .putExtra("isfirsttime", isfirsttime)
        .apply {
            if (this@sendsubactivitydynamic !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

    val intent = buildIntent(Paywall_dynamic_Activity::class.java)

    try {

        startActivity(intent)
        Handler(Looper.getMainLooper()).postDelayed({
            config.userfisttimeshowsubscreen = false
        }, 1000)

    } catch (e: Exception) {

    }
}

//=====================================================

fun Context.sendsubactivity(isfirsttime: Boolean = false) {
    if (BaseSharedPreferences(this).mIS_SUBSCRIBED) return
    if (!NetworkHelper.isOnline(this)) {
        toast(getString(R.string.please_check_internet_connection))
        return
    }
    if (this is Activity) {
        if (isFinishing || isDestroyed) {
            Log.w("sendsubactivity", "Activity is finishing/destroyed. Not starting.")
            return
        }
    }
    fun buildIntent(target: Class<*>) = Intent(this, target)
        .putExtra("AppOpen", "SettingsActivity")
        .putExtra("isfirsttime", isfirsttime)
        .apply {
            if (this@sendsubactivity !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

    val intent = buildIntent(Paywall_dynamic_Activity::class.java)
    /*    val intent = when (BaseSharedPreferences(this).isCurrentPlan) {
    //        "Current Offering" -> buildIntent(PayAllSubsciptionExterimentActivity::class.java)
    //        "Experiment Offering" -> buildIntent(SubActivityTwoplanActivity::class.java)
    //        else -> buildIntent(PayAllSubsciptionExterimentActivity::class.java)

            "Current Offering" -> buildIntent(PayAllSubsciptionExterimentActivity::class.java)
            "Experiment Offering" -> buildIntent(SubActivityTwoplanActivity::class.java)
            else -> buildIntent(PayAllSubsciptionExterimentActivity::class.java)
        }*/
    try {
        startActivity(intent)
        Handler(Looper.getMainLooper()).postDelayed({
            config.userfisttimeshowsubscreen = false
        }, 1000)
    } catch (e: Exception) {
    }
}


fun Context.sendsubactivityPin(isfirsttime: Boolean = false) {
    if (BaseSharedPreferences(this).mIS_SUBSCRIBED) return
    if (!NetworkHelper.isOnline(this)) {
        toast(getString(R.string.please_check_internet_connection))
        return
    }
    if (this is Activity) {
        if (isFinishing || isDestroyed) {
            Log.w("sendsubactivity", "Activity is finishing/destroyed. Not starting.")
            return
        }
    }
    fun buildIntent(target: Class<*>) = Intent(this, target)
        .putExtra("AppOpen", "SettingsActivity")
        .putExtra("from", "pinLimit")
        .putExtra("isfirsttime", isfirsttime)
        .apply {
            if (this@sendsubactivityPin !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

    val intent = buildIntent(Paywall_dynamic_Activity::class.java)
    /*    val intent = when (BaseSharedPreferences(this).isCurrentPlan) {
    //        "Current Offering" -> buildIntent(PayAllSubsciptionExterimentActivity::class.java)
    //        "Experiment Offering" -> buildIntent(SubActivityTwoplanActivity::class.java)
    //        else -> buildIntent(PayAllSubsciptionExterimentActivity::class.java)

            "Current Offering" -> buildIntent(PayAllSubsciptionExterimentActivity::class.java)
            "Experiment Offering" -> buildIntent(SubActivityTwoplanActivity::class.java)
            else -> buildIntent(PayAllSubsciptionExterimentActivity::class.java)
        }*/
    try {
        startActivity(intent)
        Handler(Looper.getMainLooper()).postDelayed({
            config.userfisttimeshowsubscreen = false
        }, 1000)
    } catch (e: Exception) {
    }
}

fun getAlphaColor(outmessagecolorcustomwallpaper: String): String {
    val color = Color.parseColor(outmessagecolorcustomwallpaper)
    val transparentColor = Color.argb(100, Color.red(color), Color.green(color), Color.blue(color))
    val transparentColorCode = String.format("#%08X", transparentColor)
    return transparentColorCode
}

fun isValidEmail(target: String): Boolean {
    return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
}

fun isEmojiOnly(input: String): Boolean {
    val regex = Regex("[\\p{So}]+")
    input.matches(regex)
    return false
}

fun checkWebsiteSafety(url: String, callback: (Boolean) -> Unit) {

    if (!url.startsWith("http://") && !url.startsWith("https://")) {
        callback(false) // or handle separately
        return
    }

    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            callback(false)
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
                    callback(false)
                    return
                }

                val htmlContent = response.body?.string()
                val doc = Jsoup.parse(htmlContent)
                val suspiciousElements = doc.select("iframe")

                callback(suspiciousElements.isEmpty())
            }
        }
    })
//    var callbackInvoked = false // Flag to track whether callback has been invoked
//    val client = OkHttpClient()
//    val request = Request.Builder()
//        .url(url)
//        .build()
//
//    client.newCall(request).enqueue(object : Callback {
//        override fun onFailure(call: Call, e: java.io.IOException) {
//            if (!callbackInvoked) { // Check if callback hasn't been invoked yet
//                callback(false)
//                callbackInvoked = true // Set the flag to true after invoking the callback
//            }
//        }
//
//        override fun onResponse(call: Call, response: Response) {
//            response.use {
//                if (!response.isSuccessful) {
//                    if (!callbackInvoked) {
//                        callback(false)
//                        callbackInvoked = true
//                    }
//                    return
//                }
//                val htmlContent = response.body?.string()
//                val doc = Jsoup.parse(htmlContent)
//                val suspiciousElements = doc.select("iframe")
//                if (suspiciousElements.isNotEmpty()) {
//                    if (!callbackInvoked) {
//                        callback(false)
//                        callbackInvoked = true
//                    }
//                } else {
//                    if (!callbackInvoked) {
//                        callback(true)
//                        callbackInvoked = true
//                    }
//                }
//            }
//        }
//    })
}

fun Context.sendMessageCompat(
    text: String,
    addresses: List<String>,
    subId: Int?,
    attachments: List<Attachment>,
    messageId: Long? = null,
) {
    val settings = getSendMessageSettings()
    if (subId != null) {
        settings.subscriptionId = subId
    }

    val messagingUtils = messagingUtils
    val isMms = attachments.isNotEmpty() || isLongMmsMessage(
        text,
        settings
    ) || addresses.size > 1 && settings.group
    if (isMms) {
        Log.d("jigar", "sendMessageCompat: <------------> 1")
        // we send all MMS attachments separately to reduces the chances of hitting provider MMS limit.
        if (attachments.isNotEmpty()) {
            Log.d("jigar", "sendMessageCompat: <------------> 2")
            val lastIndex = attachments.lastIndex
            if (attachments.size > 1) {
                Log.d("jigar", "sendMessageCompat: <------------> 3")
                for (i in 0 until lastIndex) {
                    val attachment = attachments[i]
                    messagingUtils.sendMmsMessage2("", addresses, attachment, settings, messageId)
                }
            }
            Log.d("jigar", "sendMessageCompat: <------------> 4")
            val lastAttachment = attachments[lastIndex]
            messagingUtils.sendMmsMessage2(text, addresses, lastAttachment, settings, messageId)
        } else {
            Log.d("jigar", "sendMessageCompat: <------------> 5")
            messagingUtils.sendMmsMessage2(text, addresses, null, settings, messageId)
        }
    } else {
        Log.d("jigar", "sendMessageCompat: <------------> 6")
        try {
            Log.d(
                "jigar", "sendMessageCompat: <------------> 7 text <-> ${text} \n" +
                        "addresses <-> ${addresses.toSet()} \n " +
                        "messageId <-> ${messageId}"
            )
            messagingUtils.sendSmsMessage2(
                text,
                addresses.toSet(),
                settings.subscriptionId,
                settings.deliveryReports,
                messageId
            )
        } catch (e: SmsException) {
            when (e.errorCode) {
                EMPTY_DESTINATION_ADDRESS -> toast(
                    id = R.string.empty_destination_address,
                    length = Toast.LENGTH_LONG
                )

                ERROR_PERSISTING_MESSAGE -> toast(
                    id = R.string.unable_to_save_message,
                    length = Toast.LENGTH_LONG
                )

                ERROR_SENDING_MESSAGE -> toast(
                    msg = getString(R.string.unknown_error_occurred_sending_message, e.errorCode),
                    length = Toast.LENGTH_LONG
                )
            }
        } catch (e: Exception) {
            showErrorToast(e)
        }
    }
}


fun isDarkMode(context: Context): Boolean {
    val mode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return mode == Configuration.UI_MODE_NIGHT_YES
}

suspend fun Context.getMessageUriFromId(messageId: Long): Uri? = withContext(Dispatchers.IO) {
    val uri = Uri.parse("content://sms/")
    val cursor = contentResolver.query(
        uri,
        arrayOf("_id"),
        "_id=?",
        arrayOf(messageId.toString()),
        null
    )
    cursor?.use { c ->
        if (c.moveToFirst()) {
            val messageIdIndex = c.getColumnIndex("_id")
            val messageId = c.getLong(messageIdIndex)
            val messageUri = Uri.withAppendedPath(Telephony.Sms.CONTENT_URI, messageId.toString())
            Log.d("Message URI", messageUri.toString()) // Logging the URI
            Log.d(
                "sendSMSManager",
                "setMessage:sendSMSManager <------------> isfaildmessage  66 ${messageUri}"
            )
            return@withContext messageUri
        }
    }
    return@withContext null
}

fun Context.AutoFullAppLockRemove(interval: Long) {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    // Add 3 days to the current time to set the first trigger time
    calendar.add(Calendar.DAY_OF_YEAR, 3)
    val firstTriggerTime = calendar.timeInMillis

    // Create a PendingIntent for the alarm
    val pendingIntent = getAutoFullAppLockRemovePendingIntent()

    // Get AlarmManager system service
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Set the repeating alarm
    alarmManager.setInexactRepeating(
        AlarmManager.RTC_WAKEUP,
        firstTriggerTime,
        interval,
        pendingIntent
    )

    Log.d("AutoFullAppLock", "Alarm set to trigger first at: ${calendar.time}")
    Log.d("AutoFullAppLock", "Interval: $interval")
}

fun Context.getAutoFullAppLockRemovePendingIntent(): PendingIntent {
    val intent = Intent(this, AutoFullAppLockUnlockReceiver::class.java)
    val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    return PendingIntent.getBroadcast(this, 987654, intent, flags)
}

fun Context.cancelAutoFullAppLockPendingIntent() {
    val intent = Intent(this, AutoFullAppLockUnlockReceiver::class.java)
    val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    PendingIntent.getBroadcast(this, 987654, intent, flags).cancel()
}


fun getCountryTier(country: String): String {
    val normalizedCountry = country.trim().lowercase()

    val tier1 = setOf(
        "australia",
        "austria",
        "belgium",
        "canada",
        "china (mainland)",
        "denmark",
        "finland",
        "france",
        "germany",
        "hong kong",
        "ireland",
        "italy",
        "japan",
        "korea republic",
        "luxembourg",
        "netherlands",
        "new zealand",
        "norway",
        "singapore",
        "spain",
        "sweden",
        "switzerland",
        "united kingdom",
        "united states"
    )

    val tier2 = setOf(
        "argentina",
        "brazil",
        "chile",
        "colombia",
        "croatia",
        "cyprus",
        "czech republic",
        "estonia",
        "egypt",
        "greece",
        "hungary",
        "iceland",
        "indonesia",
        "israel",
        "latvia",
        "lithuania",
        "malaysia",
        "malta",
        "mexico",
        "niger",
        "nigeria",
        "philippines",
        "poland",
        "portugal",
        "qatar",
        "romania",
        "russia",
        "saudi arabia",
        "slovakia",
        "slovenia",
        "south africa",
        "taiwan",
        "thailand",
        "türkiye",
        "ukraine",
        "united arab emirates",
        "uruguay",
        "vietnam"
    )

    val tier3 = setOf(
        "afghanistan",
        "albania",
        "algeria",
        "angola",
        "antigua and barbuda",
        "armenia",
        "azerbaijan",
        "bahamas",
        "bahrain",
        "bangladesh",
        "barbados",
        "belarus",
        "belize",
        "benin",
        "bermuda",
        "bhutan",
        "bolivia",
        "bosnia and herzegovina",
        "botswana",
        "burkina faso",
        "burundi",
        "cambodia",
        "cameroon",
        "cape verde",
        "central african republic",
        "chad",
        "comoros",
        "congo (democratic republic of the)",
        "congo (republic of the)",
        "costa rica",
        "cuba",
        "djibouti",
        "dominica",
        "dominican republic",
        "ecuador",
        "el salvador",
        "equatorial guinea",
        "eritrea",
        "eswatini",
        "ethiopia",
        "fiji",
        "gabon",
        "gambia",
        "georgia",
        "ghana",
        "grenada",
        "guatemala",
        "guinea",
        "guinea-bissau",
        "guyana",
        "haiti",
        "honduras",
        "iran",
        "india",
        "iraq",
        "jamaica",
        "jordan",
        "kazakhstan",
        "kenya",
        "kosovo",
        "kuwait",
        "kyrgyzstan",
        "laos",
        "lebanon",
        "lesotho",
        "liberia",
        "libya",
        "madagascar",
        "malawi",
        "maldives",
        "mali",
        "marshall islands",
        "mauritania",
        "mauritius",
        "micronesia",
        "moldova",
        "monaco",
        "mongolia",
        "montenegro",
        "morocco",
        "mozambique",
        "myanmar",
        "namibia",
        "nauru",
        "nepal",
        "nicaragua",
        "north korea",
        "north macedonia",
        "oman",
        "pakistan",
        "palau",
        "panama",
        "papua new guinea",
        "paraguay",
        "peru",
        "saint kitts and nevis",
        "saint lucia",
        "saint vincent and the grenadines",
        "samoa",
        "san marino",
        "sao tome and principe",
        "senegal",
        "serbia",
        "seychelles",
        "sierra leone",
        "solomon islands",
        "somalia",
        "south sudan",
        "sri lanka",
        "sudan",
        "suriname",
        "syria",
        "tajikistan",
        "tanzania",
        "timor-leste",
        "togo",
        "tonga",
        "trinidad and tobago",
        "tunisia",
        "turkmenistan",
        "tuvalu",
        "uganda",
        "uzbekistan",
        "vanuatu",
        "venezuela",
        "yemen",
        "zambia",
        "zimbabwe"
    )

    return when (normalizedCountry) {
        in tier1 -> "Tier 1"
        in tier2 -> "Tier 2"
        in tier3 -> "Tier 3"
        else -> "Unknown Tier"
    }
}


suspend fun getCountryName(): Pair<String?, String?> = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val request = Request.Builder().url("https://whatismycountry.com/").build()
    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw Exception("Unexpected code $response")
        return@withContext extractCountryName(response.body?.string() ?: "")
    }
}

suspend fun extractCountryName(html: String): Pair<String?, String?> = withContext(Dispatchers.IO) {
    val doc = Jsoup.parse(html)
    val fullText = doc.getElementById("country")?.text() ?: return@withContext Pair(null, null)
    val countryWithEmoji = fullText.replace("The ", "").replace("Your country is", "").trim()
    return@withContext Pair(
        getCountryTier(
            countryWithEmoji.replace(Regex("[^\\p{L} ]"), "").trim()
        ), countryWithEmoji
    )
}

fun parseGlobalTimeModel(jsonString: String): GlobalTimeModel {
    // Configure a forgiving parser
    val json = Json {
        ignoreUnknownKeys = true        // tolerate extra keys
        coerceInputValues = true        // bad values => defaults
        isLenient = true                // allow trailing commas, etc.
    }
    return runCatching {
        json.decodeFromString<GlobalTimeModel>(jsonString)
    }.getOrElse {                       // any exception → return defaults
        GlobalTimeModel()
    }
}
