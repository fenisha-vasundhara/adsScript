package com.messenger.phone.number.text.sms.service.apps

//import com.google.mlkit.nl.translate.TranslateLanguage
//import com.google.mlkit.nl.translate.Translation
//import com.google.mlkit.nl.translate.TranslatorOptions
//import com.google.mlkit.nl.translate.TranslateLanguage
//import com.google.mlkit.nl.translate.Translation
//import com.google.mlkit.nl.translate.TranslatorOptions


import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.database.ContentObserver
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import android.provider.Telephony
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.telephony.PhoneNumberUtils
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.telephony.SubscriptionInfo
import android.telephony.TelephonyManager
import android.text.format.DateUtils
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MenuRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import androidx.camera.core.ImageCapture
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.graphics.ColorUtils
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.emoji2.text.EmojiCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.firebaseFunnel
import com.demo.adsmanage.Commen.log
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.demo.adsmanage.helper.logD
import com.demo.adsmanage.helper.logE
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.google.android.material.transition.platform.MaterialSharedAxis
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.gson.Gson
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ADDRESS_SEPARATOR
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ARR_PERMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.Alredyclick
import com.messenger.phone.number.text.sms.service.apps.CommanClass.CAPTURE_AUDIO_INTENT
import com.messenger.phone.number.text.sms.service.apps.CommanClass.CAPTURE_PHOTO_INTENT
import com.messenger.phone.number.text.sms.service.apps.CommanClass.CAPTURE_VIDEO_INTENT
import com.messenger.phone.number.text.sms.service.apps.CommanClass.DEMO_LATITUDE
import com.messenger.phone.number.text.sms.service.apps.CommanClass.FILE_SIZE_NONE
import com.messenger.phone.number.text.sms.service.apps.CommanClass.GMAIL_ID
import com.messenger.phone.number.text.sms.service.apps.CommanClass.MessageTraslationlist
import com.messenger.phone.number.text.sms.service.apps.CommanClass.PICK_CONTACT_INTENT
import com.messenger.phone.number.text.sms.service.apps.CommanClass.PICK_DOCUMENT_INTENT
import com.messenger.phone.number.text.sms.service.apps.CommanClass.PICK_PHOTO_INTENT
import com.messenger.phone.number.text.sms.service.apps.CommanClass.PICK_VIDEO_AND_IMAGE_INTENT
import com.messenger.phone.number.text.sms.service.apps.CommanClass.PICK_VIDEO_INTENT
import com.messenger.phone.number.text.sms.service.apps.CommanClass.SystemGeneratedIconSwitchAb
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.applyFontToMenuItem
import com.messenger.phone.number.text.sms.service.apps.CommanClass.backgroundcolorcustomwallpaperold
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.cancelScheduleSendPendingIntent
import com.messenger.phone.number.text.sms.service.apps.CommanClass.check1
import com.messenger.phone.number.text.sms.service.apps.CommanClass.check2
import com.messenger.phone.number.text.sms.service.apps.CommanClass.check3
import com.messenger.phone.number.text.sms.service.apps.CommanClass.check4
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.convertStringToArrayList
import com.messenger.phone.number.text.sms.service.apps.CommanClass.copyToClipboard
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createChatBottomSheetDialog
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createOptionBackground
import com.messenger.phone.number.text.sms.service.apps.CommanClass.deleteSmsDraft
import com.messenger.phone.number.text.sms.service.apps.CommanClass.deletedmessage
import com.messenger.phone.number.text.sms.service.apps.CommanClass.drawableCache
import com.messenger.phone.number.text.sms.service.apps.CommanClass.fetchContactIdFromPhoneNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.findOtpInString
import com.messenger.phone.number.text.sms.service.apps.CommanClass.firebaseEvent
import com.messenger.phone.number.text.sms.service.apps.CommanClass.formatTextWithItalic
import com.messenger.phone.number.text.sms.service.apps.CommanClass.fromnotificationautoclick
import com.messenger.phone.number.text.sms.service.apps.CommanClass.generateRandomId
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getAddresses
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getAddressesNew
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getAddressesPre
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getAddressesPre2
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getBottomNavigationBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getContrastColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getConversations
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getDefaultKeyboardHeight
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getDialogBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getFileSizeFromUri
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMessages
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getNameAndPhotoFromPhoneNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperStatusBarColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getSendMessageSettings
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getSmsDraft
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getSubscriptionIdFromMessageId
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadId
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadParticipants
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadTitle
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadTitlePre
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getisDualsim
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gooutside
import com.messenger.phone.number.text.sms.service.apps.CommanClass.groupnamecustom
import com.messenger.phone.number.text.sms.service.apps.CommanClass.handleDefaultSmsClick_1
import com.messenger.phone.number.text.sms.service.apps.CommanClass.hideKeyboard
import com.messenger.phone.number.text.sms.service.apps.CommanClass.higlitetxtstart
import com.messenger.phone.number.text.sms.service.apps.CommanClass.indexOfFirstOrNull
import com.messenger.phone.number.text.sms.service.apps.CommanClass.inmessagecolorcustomwallpaperold
import com.messenger.phone.number.text.sms.service.apps.CommanClass.insertSmsMessageForGroup
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isFLow1
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isFLow2
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isGifMimeType
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isImageMimeType
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isInternetAvailable
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isLikelyShortCode
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isOnline
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isSerchfoundmessage
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isVisible
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isVisibleMess
import com.messenger.phone.number.text.sms.service.apps.CommanClass.iscustomwallpaersetterdid
import com.messenger.phone.number.text.sms.service.apps.CommanClass.iscustomwallpaersetterdidmovilenumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isfromenotification
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isfromprofile
import com.messenger.phone.number.text.sms.service.apps.CommanClass.issendmessasgescreensearchstatrt
import com.messenger.phone.number.text.sms.service.apps.CommanClass.issimcardavailable
import com.messenger.phone.number.text.sms.service.apps.CommanClass.launchViewIntent
import com.messenger.phone.number.text.sms.service.apps.CommanClass.logOnboardingFunnelStep
import com.messenger.phone.number.text.sms.service.apps.CommanClass.mainactivityfinish
import com.messenger.phone.number.text.sms.service.apps.CommanClass.markThreadAsRead
import com.messenger.phone.number.text.sms.service.apps.CommanClass.messageNotify
import com.messenger.phone.number.text.sms.service.apps.CommanClass.mobileNumberLock
import com.messenger.phone.number.text.sms.service.apps.CommanClass.monthdiscount
import com.messenger.phone.number.text.sms.service.apps.CommanClass.nameLock
import com.messenger.phone.number.text.sms.service.apps.CommanClass.openRequestExactAlarmSettings
import com.messenger.phone.number.text.sms.service.apps.CommanClass.openautoclick
import com.messenger.phone.number.text.sms.service.apps.CommanClass.outmessagecolorcustomwallpaperold
import com.messenger.phone.number.text.sms.service.apps.CommanClass.realScreenSize
import com.messenger.phone.number.text.sms.service.apps.CommanClass.requestDefaultApp
import com.messenger.phone.number.text.sms.service.apps.CommanClass.roleDefaultCheck
import com.messenger.phone.number.text.sms.service.apps.CommanClass.saveSmsDraft
import com.messenger.phone.number.text.sms.service.apps.CommanClass.scheduleMessage
import com.messenger.phone.number.text.sms.service.apps.CommanClass.sendmessagebuttondefaultset
import com.messenger.phone.number.text.sms.service.apps.CommanClass.sendsubactivity
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setCursorColorProgrammatically
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setMessageSendSim
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setWallpaperdone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.shareText
import com.messenger.phone.number.text.sms.service.apps.CommanClass.shareprefselectedoldold
import com.messenger.phone.number.text.sms.service.apps.CommanClass.showKeyboard
import com.messenger.phone.number.text.sms.service.apps.CommanClass.showWithAnimation
import com.messenger.phone.number.text.sms.service.apps.CommanClass.smartreplycolorold
import com.messenger.phone.number.text.sms.service.apps.CommanClass.subscriptionManagerCompat
import com.messenger.phone.number.text.sms.service.apps.CommanClass.textPrimaryOnThemeForColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.textdialogopen
import com.messenger.phone.number.text.sms.service.apps.CommanClass.thredidLock
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toArrayList
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toolbarcolorcustomwallpaperold
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.ViewAllImageAndVideoAdapter
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.combinedListAttachment
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.getAllImagesAndVideosSortedByRecentNew
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.getRecentImage
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.imageuriGalleryFirstimage
import com.messenger.phone.number.text.sms.service.apps.CustomSnackbar.CookieBar
import com.messenger.phone.number.text.sms.service.apps.Dialog.CommanDeleteBlockDialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.Custom_Wallpaper_Dialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.DialogSimCardSelect
import com.messenger.phone.number.text.sms.service.apps.Dialog.MessageLanguageDialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.Message_Corner_Dialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.Message_Text_Size_Dialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.PermissionRequiredDialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.ScheduleMessageDialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.ScheduleMessageShowDialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.Show_Paticular_Edit_Dialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.TutorialDialog
import com.messenger.phone.number.text.sms.service.apps.GoogleMobileAdsConsentManagerChack.GoogleMobileAdsConsentManager
import com.messenger.phone.number.text.sms.service.apps.JAds.ConnectionLiveData
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.ShowCase.DismissType
import com.messenger.phone.number.text.sms.service.apps.ShowCase.GuideView
import com.messenger.phone.number.text.sms.service.apps.ShowCase.PointerType
import com.messenger.phone.number.text.sms.service.apps.SwipeController.MessageSwipeControllerNew
import com.messenger.phone.number.text.sms.service.apps.SwipeController.SwipeControllerActions
import com.messenger.phone.number.text.sms.service.apps.adapter.AttachmentsAdapter
import com.messenger.phone.number.text.sms.service.apps.adapter.AutoCompleteTextViewAdapter
import com.messenger.phone.number.text.sms.service.apps.adapter.InMainAdapter
import com.messenger.phone.number.text.sms.service.apps.adapter.ReplyChipAdapter

import com.messenger.phone.number.text.sms.service.apps.camera.CameraActivity
import com.messenger.phone.number.text.sms.service.apps.camera.hasAllPermissions
import com.messenger.phone.number.text.sms.service.apps.data.Attachment
import com.messenger.phone.number.text.sms.service.apps.data.GetMobileMessage
import com.messenger.phone.number.text.sms.service.apps.data.Message
import com.messenger.phone.number.text.sms.service.apps.data.MessageAttachment
import com.messenger.phone.number.text.sms.service.apps.data.SIMCard
import com.messenger.phone.number.text.sms.service.apps.data.messaging.getSmsManager
import com.messenger.phone.number.text.sms.service.apps.data.newsync.SmsSyncManager
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivitySendMessageBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.AttachMediaAndContentDialogBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.DialogMessageBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.DialogRateAppNewOneBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.FeedBackDialogLayoutBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.MessageResendBottomSheetBinding
import com.messenger.phone.number.text.sms.service.apps.helper.RandomDrawableProvider
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.ClickListener
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.CommanDeleteBlockDialogInterface
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MessageLanguageDialogInterface
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MessageSelectUnselect
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.SelectSimCardDialogInterface
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.Selectedmessagetraslateinterface
import com.messenger.phone.number.text.sms.service.apps.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import com.messenger.phone.number.text.sms.service.apps.mapBuilder.LATITUDE
import com.messenger.phone.number.text.sms.service.apps.mapBuilder.LEKU_POI
import com.messenger.phone.number.text.sms.service.apps.mapBuilder.LOCATION_ADDRESS
import com.messenger.phone.number.text.sms.service.apps.mapBuilder.LONGITUDE
import com.messenger.phone.number.text.sms.service.apps.mapBuilder.LekuPoi
import com.messenger.phone.number.text.sms.service.apps.mapBuilder.LocationPickerActivity
import com.messenger.phone.number.text.sms.service.apps.modelClass.AttachmentSelection
import com.messenger.phone.number.text.sms.service.apps.modelClass.ChatScreenColor
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversationbin
import com.messenger.phone.number.text.sms.service.apps.modelClass.Language
import com.messenger.phone.number.text.sms.service.apps.modelClass.MessageTraslationModel
import com.messenger.phone.number.text.sms.service.apps.sms.SendSMSManager
import com.messenger.phone.number.text.sms.service.apps.sms.SmsUtils.deleteSMS
import com.messenger.phone.number.text.sms.service.apps.translateAPI.TranslateApi
import com.messenger.phone.number.text.sms.service.apps.viewModel.SmartReplyViewModel
import com.simplemobiletools.commons.extensions.adjustAlpha
import com.simplemobiletools.commons.extensions.applyColorFilter
import com.simplemobiletools.commons.extensions.beGone
import com.simplemobiletools.commons.extensions.beVisible
import com.simplemobiletools.commons.extensions.beVisibleIf
import com.simplemobiletools.commons.extensions.formatDate
import com.simplemobiletools.commons.extensions.getFilenameFromUri
import com.simplemobiletools.commons.extensions.getMyFileUri
import com.simplemobiletools.commons.extensions.onTextChangeListener
import com.simplemobiletools.commons.extensions.showErrorToast
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.extensions.usableScreenSize
import com.simplemobiletools.commons.extensions.value
import com.simplemobiletools.commons.helpers.SimpleContactsHelper
import com.simplemobiletools.commons.helpers.isSPlus
import com.simplemobiletools.commons.models.PhoneNumber
import com.simplemobiletools.commons.models.SimpleContact
import com.simplemobiletools.commons.views.MyGridLayoutManager
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.shape.Circle
import com.messenger.phone.number.text.sms.service.apps.realmplan.ConversationDataSourceProvider
import com.messenger.phone.number.text.sms.service.apps.realmplan.RealmFeatureFlag
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import io.github.xilinjia.krdb.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import java.io.File
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.math.ceil


@AndroidEntryPoint
class SendMessageActivity : AppCompatActivity(), SelectSimCardDialogInterface,
    MessageSelectUnselect, ClickListener, AdapterView.OnItemSelectedListener,
    MessageLanguageDialogInterface, CommanDeleteBlockDialogInterface,
    Selectedmessagetraslateinterface {
    private val DEFAULT_SMS_INSTRUCTION_REQUEST = 361


    lateinit var binding: ActivitySendMessageBinding

    private val CUSTOME_CAMERA_REQUEST_CODE: Int = 9999

    private var spotlightTraslate: Spotlight? = null
    private var newSize: Int = 0
    var unreadmessagecoundshow = false
    private var base64String: String? = null
    var userScrolling = false
    private var capturedImageUri: Uri? = null
    var datasedule: Conversation? = null
    var listsedule: ArrayList<Conversation> = arrayListOf()
    private var emojiCompat: EmojiCompat? = null

    //    private var emojiPopup: EmojiPopup? = null
    var higlitetxt = ""
    var newmessagecount = 0
    var curruntmessagesize = 0
    val translateApi = TranslateApi()
    var isattachmentopen = false
    var iseligibledimiss = false
    private var consentRequested = false
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    val viewAllImageAdapter: ViewAllImageAndVideoAdapter by lazy {
        ViewAllImageAndVideoAdapter(Glide.with(this).asBitmap(), this)
    }

    @Inject
    lateinit var messageCornerDialog: Message_Corner_Dialog

    @Inject
    lateinit var scheduleMessageShowDialog: ScheduleMessageShowDialog

    @Inject
    lateinit var messageTextSizeDialog: Message_Text_Size_Dialog

    var isshowcasestartshowing = false

    var firsttimeautoscrollcall = true

    var autoscrollstart = false

    var isshowcaseshow1 = false

    private var isUsingFrontCamera = false // Flag to track camera state

    private var isUserNotificationShow: Boolean = true
    private val MY_PERMISSIONS_REQUEST_LOCATION = 1001
    private var builder: GuideView.Builder? = null
    private var mGuideView: GuideView? = null
    var isshowcaseshow = true
    var senderNumberGet: String = "Unknown"
    private lateinit var messageSwipeController: MessageSwipeControllerNew
    private lateinit var conversation: Conversation
    private var isarchiv: Boolean = false
    private var isPrivateChat: Boolean = false
    private var mobileNumber: String? = null
    private var writemessageedtttxt: String? = null
    private var commanDeleteBlockDialog: CommanDeleteBlockDialog? = null
    private var currentSIMCardIndex = 0
    private var lastClickTime: Long = 0
    private var isAttachmentPickerVisible = false
    private var senderName: String = ""
    private var conversationData: Conversation? = null
    private var isSearchFoundmessage: String = ""
    private var isSearchFound: Boolean = false
    private var forscheduleMessage: Boolean = false
    private lateinit var fromenumber: String
    private val PICK_CONTACT_REQUEST = 1
    private var scheduledMessage: Conversation? = null
    private val availableSIMCards = ArrayList<SIMCard>()
    private var isforvard: Boolean = false
    private var isgroupmessage: Boolean = false
    private var isnetonoff: Boolean = false
    private var message: String = ""
    private var messagesudule: Conversation? = null
    private var name: String? = ""
    private var namenew: String? = ""
    private val ICON_MARGIN = 8

    var iscroll = true

    var messagetype: String = "normalmessage"
    var messageotp: String? = null
    var traslatebtnclick = false
    var fromnotification = false
    var fromnotification2 = false
    private var openFromAfterCallList = false
    private val tutorialdialog by lazy { TutorialDialog() }
    private val customWallpaperDialog by lazy { Custom_Wallpaper_Dialog() }

    var traslateornetonoff = false

    val showPaticularEditDialog by lazy {
        Show_Paticular_Edit_Dialog()
    }

    //    var configurationHelper: ContainerTransformConfigurationHelper? = null
    var selected: ArrayList<String> = arrayListOf()
    var selectedMessageList2: ArrayList<Conversation> = arrayListOf()
    var posisserchmessage = -1
    private val SCROLL_TO_BOTTOM_FAB_LIMIT = 20
    private var isScheduledMessage: Boolean = false
    private var isReplayMessage: Boolean = false
    private var ReplayMessage: String? = null
    private lateinit var scheduledDateTime: DateTime
    private var messages = ArrayList<Message>()
    private var smsContentObserver: ContentObserver? = null
    private val smsContentObserverHandler = Handler(Looper.getMainLooper())
    private val smsContentObserverRunnable = Runnable { syncSmsProviderForChatIfNeeded() }

    private var participants: ArrayList<SimpleContact> = arrayListOf()

    private var datanetonoff: ArrayList<Conversation> = arrayListOf()
    private var demolist: ArrayList<Conversation> = arrayListOf()

    @Inject
    lateinit var messagerDatabaseRepo: MessagerDatabaseRepo

    @Inject
    lateinit var realm: Lazy<Realm>

    @Inject
    lateinit var sendSMSManager: SendSMSManager

    @Inject
    lateinit var getMobileMessage: GetMobileMessage

    @Inject
    lateinit var replyChipAdapter: ReplyChipAdapter
    private var lastObservedThreadSnapshot: Int? = null
    private val chatSmsSyncManager: SmsSyncManager by lazy {
        SmsSyncManager(applicationContext, messagerDatabaseRepo, lifecycleScope)
    }

    private var mLastClickTime: Long = 0


    private var messageLanguageDialog: MessageLanguageDialog? = null

    val smartReplyViewModel by lazy {
        ViewModelProvider(this)[SmartReplyViewModel::class.java]
    }


    var isshowads: Boolean = false
    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

    var tredid: Long? = null

    var iscroling: Boolean = false

    var dialogSimCardSelect: DialogSimCardSelect = DialogSimCardSelect()

    var issendmessage = false
    var issendmessagenew = false

    var messagetraslatepos = -1

    companion object {
        var maxChatBubbleWidth = 0.0F
    }

    //    val availableLanguages: List<Language> = TranslateLanguage.getAllLanguages().map { Language(it) }
    private lateinit var adapter: ArrayAdapter<Language>
    private lateinit var connectionLiveData: ConnectionLiveData


    @Inject
    lateinit var inMainAdapter: InMainAdapter


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeModeManager.applyThemeMode(this)
        if (ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM) {
            DynamicColors.applyToActivityIfAvailable(this)
        }

        super.onCreate(savedInstanceState)


//        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
//        val enter = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
//            addTarget(R.id.main_bg)
//        }
//        window.enterTransition = enter
//        window.allowEnterTransitionOverlap = true

        setLocal()
        binding = setContentViewSafely()

//        window.decorView.post {
//            startPostponedEnterTransition()
//        }


        binding.txtNoReplayBox.text =
            if (hasSim()) getString(R.string.short_code) else getString(R.string.sim_card_not_available)


//        myChatEdgeToEdge(binding.main, binding.vAnd15StatusBar)
//        setbasetheme(binding.vAnd15StatusBar)
        tredid = intent.getLongExtra("tredid", 0L)
        maxChatBubbleWidth = usableScreenSize.x * 0.8f
        clearAllNotifications()
        keybordlisner()
        binding.issimavailableornot = issimcardavailable
        setUpEmojiPopup()
        googleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.getInstance(applicationContext)

        binding.subPoster.setOnClickListener {
            firebaseEvent("SendMessageActivity", "subscription poster")
            sendsubactivity()
        }
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
        if (checkPermissions()) {
            CoroutineScope(Dispatchers.IO).launch {
                if (combinedListAttachment.isEmpty()) {
                    imageuriGalleryFirstimage = getRecentImage()
                    combinedListAttachment.clear()
                    combinedListAttachment = ArrayList(getAllImagesAndVideosSortedByRecentNew())
                }
            }
        }
        setupAttachmentPicker()
//        setupBannerAdLayout()
        getsetIntent(intent)

//        CoroutineScope(Dispatchers.IO).launch {
//            (0..100000).forEachIndexed { index, i ->
//                val maxAsterisks = 10
//                val asterisks: String
//
//                val effectiveIndex = index % (2 * maxAsterisks - 2)
//                asterisks = when {
//                    effectiveIndex < maxAsterisks -> "*".repeat(effectiveIndex + 1) // Increasing
//                    else -> "*".repeat((2 * maxAsterisks - 2) - effectiveIndex) // Decreasing
//                }
//
//
//                runOnUiThread {
//                    binding.writemessageedt.setText(asterisks)
//                    binding.sendbutton.performClick()
//                }
//
//            }
//        }

    }

    private fun setContentViewSafely(): ActivitySendMessageBinding {
        return try {
            DataBindingUtil.setContentView(this, R.layout.activity_send_message)
        } catch (exception: Resources.NotFoundException) {
            Log.e(
                "SendMessageActivity",
                "setContentView failed due to missing framework animation/interpolator resource, retrying with fallback theme",
                exception
            )
            setTheme(R.style.sendmessage_crash_safe)
            window.setWindowAnimations(0)
            DataBindingUtil.setContentView(this, R.layout.activity_send_message)
        }
    }


    private fun DialogBuilder() {
        dialogSimCardSelect.show(supportFragmentManager, "DialogSimCardSelect")
    }

    private fun isSmsSupported(): Boolean {
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) return false
        val telephonyManager =
            getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager ?: return false
        return try {
            telephonyManager.isSmsCapable
        } catch (_: Exception) {
            true
        }
    }

    private fun setupBannerAdLayout() {
        // Function to handle banner ad visibility and layout adjustments
        binding.constnativeBannerAdView.viewTreeObserver.addOnGlobalLayoutListener {
            if (binding.constnativeBannerAdView.visibility == View.GONE) {
                // When banner is gone, ensure main content extends to bottom
                val layoutParams =
                    binding.mainbackground.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
                layoutParams.bottomToBottom =
                    androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams.bottomToTop =
                    androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET
                binding.mainbackground.layoutParams = layoutParams
            } else {
                // When banner is visible, constrain main content above it
                val layoutParams =
                    binding.mainbackground.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
                layoutParams.bottomToTop = binding.constnativeBannerAdView.id
                layoutParams.bottomToBottom =
                    androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET
                binding.mainbackground.layoutParams = layoutParams
            }
        }
    }

    suspend fun sendMessage() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(
                "Opert-->",
                "sendMessage start group=$isgroupmessage scheduled=$isScheduledMessage defaultSms=${
                    Telephony.Sms.getDefaultSmsPackage(
                        this@SendMessageActivity
                    ) == packageName
                }"
            )
            if (isReplayMessage) {
                isReplayMessage = false
                ReplayMessage = ReplayMessage?.replace("➥", "")?.replace("-", "")
                val textwritemessage = binding.writemessageedt.text.toString()
                writemessageedtttxt = "${"${ReplayMessage}\n"}${"  "}${"➥ "}${textwritemessage}"
                runOnUiThread { hideReplayUi() }
            } else {
                writemessageedtttxt = binding.writemessageedt.text.toString()
            }

            issendmessage = true
            var signature = ""
            signature = if (config.userpreferenceSignatureOnOff) {
                if (config.userpreferenceSignature[0] == '-') {
                    config.userpreferenceSignature
                } else {
                    "-" + config.userpreferenceSignature
                }
            } else {
                config.userpreferenceSignature
            }

            if (isgroupmessage) {
                if (writemessageedtttxt!!.isNotEmpty()) {
                    val addresses = participants.getAddressesPre()
                    val stringWithoutBlankLines = if (config.useSimpleCharacters) {
                        if (config.isendtoendencrtepted) {
                            Base64.encodeToString(
                                formatTextWithItalic(
                                    writemessageedtttxt.toString()
                                        .replace(Regex("^\\s+|\\s+$"), ""), signature
                                ).toByteArray(), Base64.DEFAULT
                            )

                        } else {
                            formatTextWithItalic(
                                writemessageedtttxt.toString().replace(Regex("^\\s+|\\s+$"), ""),
                                signature
                            )
                        }
                    } else {
                        if (config.isendtoendencrtepted) {
                            Base64.encodeToString(
                                formatTextWithItalic(
                                    writemessageedtttxt.toString()
                                        .replace(Regex("^\\s+|\\s+$"), ""), signature
                                ).toByteArray(), Base64.DEFAULT
                            )
                        } else {
                            formatTextWithItalic(
                                writemessageedtttxt.toString().replace(Regex("^\\s+|\\s+$"), ""),
                                signature
                            )
                        }
                    }

                    val subscriptionId =
                        availableSIMCards.getOrNull(currentSIMCardIndex)?.subscriptionId
                            ?: SmsManager.getDefaultSmsSubscriptionId()
                    if (isScheduledMessage) {
                        Log.d("Opert-->", "group scheduled send, addresses=1 ${addresses.size}")
                        sendScheduledMessage(
                            stringWithoutBlankLines, addresses, subscriptionId, arrayListOf(), true
                        )
                    } else {
                        Log.d("Opert-->", "group immediate send, addresses=2 ${addresses.size}")
                        sendSMSManager.sendMessageCompat(
                            stringWithoutBlankLines, addresses, subscriptionId, arrayListOf(), true
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            val settings = getSendMessageSettings()
                            if (addresses.size > 1) {
                                val broadCastThreadId =
                                    this@SendMessageActivity.getThreadId(addresses.toSet())
                                val mergedAddresses = addresses.joinToString(ADDRESS_SEPARATOR)
                                val messageuri = insertSmsMessageForGroup(
                                    subId = settings.subscriptionId,
                                    dest = mergedAddresses,
                                    text = stringWithoutBlankLines,
                                    timestamp = System.currentTimeMillis(),
                                    threadId = broadCastThreadId,
                                    status = Telephony.Sms.Sent.STATUS_COMPLETE,
                                    type = Telephony.Sms.Sent.MESSAGE_TYPE_SENT
                                )
                                val insertedId = messageuri.lastPathSegment!!.toLong()
                                Log.d("Opert-->", "group message inserted providerId=$insertedId")

                                val list =
                                    tredid?.let { messagerDatabaseRepo.getUserMessageListrepo(it) }

                                isarchiv = if (list?.isEmpty() == true) {
                                    false
                                } else {
                                    list!![0].isarchived
                                }

                                isPrivateChat = if (list?.isEmpty() == true) {
                                    false
                                } else {
                                    list!![0].isPrivateChat
                                }

                                if (!messagerDatabaseRepo.isMessageExitsRepo(insertedId)) {
                                    val c: Date = Calendar.getInstance().time
                                    val data =
                                        messagerDatabaseRepo.getUserMessageListChackrepo(tredid!!)
                                    if (data.isNotEmpty()) {
                                        val isgropmessage = data[0].isgroupmessage
                                        if (isgropmessage) {
                                            val conversation = Conversation(
                                                0,
                                                c.time.toString(),
                                                true,
                                                data[0].title,
                                                null,
                                                false,
                                                data[0].phoneNumber,
                                                stringWithoutBlankLines,
                                                c.time,
                                                2,
                                                true,
                                                null,
                                                messageId = insertedId,
                                                threadId = tredid,
                                                isgroupmessage = true,
                                                groupName = data[0].groupName,
                                                isarchived = isarchiv,
                                                isPrivateChat = isPrivateChat
                                            )
                                            messagerDatabaseRepo.insertmessage(conversation)
                                        }
                                    } else {
                                        Log.d("Opert-->", "bom-> 1")

                                        val participantsforgroupchat =
                                            getThreadParticipants(tredid!!, null)

                                        if (participantsforgroupchat.isNotEmpty()) {
                                            val addresses = participantsforgroupchat.getAddresses()
                                            val result = addresses.joinToString(separator = "|")

                                            val conversation = Conversation(
                                                0,
                                                c.time.toString(),
                                                true,
                                                participantsforgroupchat.getThreadTitle(),
                                                null,
                                                false,
                                                result,
                                                stringWithoutBlankLines,
                                                c.time,
                                                2,
                                                true,
                                                null,
                                                messageId = insertedId,
                                                threadId = tredid,
                                                isgroupmessage = true,
                                                groupName = if (groupnamecustom != null) {
                                                    groupnamecustom
                                                } else {
                                                    participantsforgroupchat.getThreadTitle()
                                                },
                                                isarchived = isarchiv,
                                                isPrivateChat = isPrivateChat
                                            )
                                            messagerDatabaseRepo.insertmessage(conversation)
                                            Log.d("Opert-->", "bom-> 2")
                                        } else {
                                            var addresses = participants.getAddressesPre2()
                                            val result = addresses.joinToString(separator = "|")
                                            Log.d("Opert-->", "bom-> 3")
                                            val conversation = Conversation(
                                                0,
                                                c.time.toString(),
                                                true,
                                                participants.getThreadTitlePre(),
                                                null,
                                                false,
                                                result,
                                                stringWithoutBlankLines,
                                                c.time,
                                                2,
                                                true,
                                                null,
                                                messageId = insertedId,
                                                threadId = tredid,
                                                isgroupmessage = true,
                                                groupName = if (groupnamecustom != null) {
                                                    groupnamecustom
                                                } else {
                                                    participants.getThreadTitlePre()
                                                },
                                                isarchived = isarchiv,
                                                isPrivateChat = isPrivateChat
                                            )
                                            messagerDatabaseRepo.insertmessage(conversation)
                                            Log.d("Opert-->", "bom-> 4")

                                        }

                                    }
                                }
                            }
                        }
                    }
                    runOnUiThread { binding.writemessageedt.text!!.clear() }
                }
            } else {
                Log.d("", "sendMessage: attechment <--------> 1")
                if (mobileNumber?.length!! > 0 && writemessageedtttxt.toString()
                        .isNotEmpty() || getAttachmentSelections().isNotEmpty()
                ) {

                    Log.d("", "sendMessage: attechment <--------> 2")

                    val stringWithoutBlankLines = if (config.useSimpleCharacters) {

                        if (config.isendtoendencrtepted) {


                            Base64.encodeToString(
                                formatTextWithItalic(
                                    writemessageedtttxt.toString()
                                        .replace(Regex("^\\s+|\\s+$"), ""), signature
                                ).toByteArray(), Base64.DEFAULT
                            )

                        } else {
                            formatTextWithItalic(
                                writemessageedtttxt.toString().replace(Regex("^\\s+|\\s+$"), ""),
                                signature
                            )

                        }


                    } else {

                        if (config.isendtoendencrtepted) {

                            Base64.encodeToString(
                                formatTextWithItalic(
                                    writemessageedtttxt.toString()
                                        .replace(Regex("^\\s+|\\s+$"), ""), signature
                                ).toByteArray(), Base64.DEFAULT
                            )

                        } else {

                            formatTextWithItalic(
                                writemessageedtttxt.toString().replace(Regex("^\\s+|\\s+$"), ""),
                                signature
                            )

                        }


                    }

                    val subscriptionId =
                        availableSIMCards.getOrNull(currentSIMCardIndex)?.subscriptionId
                            ?: SmsManager.getDefaultSmsSubscriptionId()
                    if (isScheduledMessage) {
                        Log.d("Opert-->", "single scheduled send to=$mobileNumber")
                        val attachments = buildMessageAttachments()
                        if (attachments.isNotEmpty()) {
                            CoroutineScope(Dispatchers.Main).launch {
                                val builder = MaterialAlertDialogBuilder(this@SendMessageActivity)
                                builder.setMessage("The scheduling feature currently does not support attachments, but we are working on it.")
                                builder.setCancelable(true)
                                builder.setPositiveButton(
                                    "ok"
                                ) { p0, p1 ->
                                    p0?.dismiss()
                                }
                                builder.create()
                                builder.show()
                            }
                        } else {
                            sendScheduledMessage(
                                stringWithoutBlankLines,
                                listOf(mobileNumber!!),
                                subscriptionId,
                                arrayListOf(),
                                false
                            )
                        }
                    } else {

                        val attachments = buildMessageAttachments()
                        if (!isSmsSupported()) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@SendMessageActivity,
                                    getString(R.string.sms_not_supported),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            return@launch
                        }
                        val c: Date = Calendar.getInstance().time
                        val settings = getSendMessageSettings()
                        val smsManager = getSmsManager(settings.subscriptionId)
                        val messagessub = runCatching {
                            smsManager.divideMessage(stringWithoutBlankLines)
                        }.getOrElse {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@SendMessageActivity,
                                    getString(R.string.sms_not_supported),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            return@launch
                        }
                        val addresses = participants.getAddressesNew()

                        Log.d(
                            "Opert-->",
                            "single immediate send to=${addresses.joinToString()} attachments=${attachments.size}"
                        )
                        sendSMSManager.sendMessageCompat(
                            stringWithoutBlankLines, addresses, subscriptionId, attachments, false
                        )
                        Log.d("", "sendMessage:attachment <-----------> 1")
                        if (attachments.isNotEmpty()) {
                            Log.d("", "sendMessage:attachment <-----------> 2")
                            isUserNotificationShow =
                                if (messagerDatabaseRepo.isNewUserMessageExitsRepo(tredid!!)) {
                                    messagerDatabaseRepo.isUserNotificationshowRepo(tredid!!)
                                } else {
                                    true
                                }

                            val list = messagerDatabaseRepo.getUserMessageListrepo(tredid!!)
                            val isarchiv = if (list.isEmpty()) {
                                false
                            } else {
                                list[0].isarchived
                            }

                            val isPrivateChat = if (list.isEmpty()) {
                                false
                            } else {
                                list[0].isPrivateChat
                            }


                            listOf(mobileNumber!!).forEachIndexed { index, number ->
                                Log.d("", "sendMessage:attachment <-----------> 3")
                                val messageIds = messages.map { it.id }

                                val messages = getMessages(
                                    tredid!!,
                                    getImageResolutions = true,
                                    limit = maxOf(1, attachments.size)
                                ).filter { it.id !in messageIds }

                                Log.d(
                                    "",
                                    "sendMessage:attachment <-----------> 33 ${messages.isEmpty()}"
                                )
                                Log.d("", "sendMessage:attachment <-----------> 44 ${messages}")
                                Log.d("", "sendMessage:attachment <-----------> 55 ${tredid}")
                                Log.d("", "sendMessage:attachment <-----------> 88 ${participants}")

                                val attachmentall = ArrayList<Attachment>()
                                messages.forEachIndexed { index, message ->
                                    attachmentall.addAll(message.attachment!!.attachments)
                                }
                                messages.forEachIndexed { index, message ->
                                    Log.d(
                                        "",
                                        "sendMessage:forEachIndexed <----------> ${attachmentall}"
                                    )
                                    val lastIndex = attachmentall.lastIndex

                                    if (attachmentall.size > 1) {
                                        Log.d("", "sendMessage:attachment <-----------> 4")
                                        Log.d(
                                            "",
                                            "sendMessage:forEachIndexed <----------> 501 ${attachmentall}"
                                        )
                                        for (i in 0 until lastIndex) {
                                            val attachment = attachmentall[i]
                                            val dest = PhoneNumberUtils.stripSeparators(number)

//                                            if (!messagerDatabaseRepo.isMessageExitsRepo(attachment.messageId)) {
//
//
//                                            }else{
//                                                Log.d(
//                                                    "",
//                                                    "sendMessage:forEachIndexed <----------> 502 ${attachmentall}"
//                                                )
//                                            }

                                            Log.d(
                                                "",
                                                "sendMessage:forEachIndexed <----------> 500 ${attachmentall}"
                                            )
                                            val conversationold = Conversation(
                                                0,
                                                c.time.toString(),
                                                false,
                                                fetchContactIdFromPhoneNumber(
                                                    dest, this@SendMessageActivity
                                                )!!,
                                                null,
                                                false,
                                                dest,
                                                "",
                                                c.time,
                                                2,
                                                dest.isDigitsOnly(),
                                                messageStatus = null,
                                                messageId = attachment.messageId,
                                                threadId = tredid!!,
                                                messagewithattachment = MessageAttachment(
                                                    attachment.messageId,
                                                    "",
                                                    arrayListOf(attachment)
                                                ),
                                                isarchived = isarchiv,
                                                isPrivateChat = isPrivateChat,
                                                shownotification = isUserNotificationShow
                                            )
                                            val ismessageid = messagerDatabaseRepo.insertmessage(
                                                conversationold
                                            )
                                            ismessageid?.let {
                                                Log.d(
                                                    "",
                                                    "updateAppDatabase: <-------------> 31 ${ismessageid}"
                                                )
                                                messagerDatabaseRepo.updatemessagestatusRepo(
                                                    "Sending", it
                                                )
                                            }
                                        }
                                    } else {
                                        Log.d("", "sendMessage:attachment <-----------> 5")
                                        Log.d(
                                            "",
                                            "sendMessage:forEachIndexed <----------> 503 ${attachmentall}"
                                        )
                                    }
                                    val lastAttachment = attachmentall[lastIndex]
                                    val dest = PhoneNumberUtils.stripSeparators(number)
                                    Log.d("", "sendMessage:attachment <-----------> 6")
                                    Log.d(
                                        "",
                                        "sendMessage:forEachIndexed <----------> 504 ${lastAttachment}"
                                    )
                                    val conversation = Conversation(
                                        0,
                                        c.time.toString(),
                                        false,
                                        fetchContactIdFromPhoneNumber(
                                            dest, this@SendMessageActivity
                                        )!!,
                                        null,
                                        false,
                                        dest,
                                        messagessub.joinToString(),
                                        c.time,
                                        2,
                                        dest.isDigitsOnly(),
                                        messageStatus = null,
                                        messageId = lastAttachment.messageId,
                                        threadId = tredid!!,
                                        messagewithattachment = MessageAttachment(
                                            lastAttachment.messageId,
                                            messagessub.joinToString(),
                                            arrayListOf(lastAttachment)
                                        ),
                                        isarchived = isarchiv,
                                        isPrivateChat = isPrivateChat,
                                        shownotification = isUserNotificationShow
                                    )

                                    val ismessageid =
                                        messagerDatabaseRepo.insertmessage(conversation)

                                    ismessageid?.let {
                                        Log.d("", "sendMessage:attachment <-----------> 6")
                                        Log.d(
                                            "",
                                            "updateAppDatabase: <-------------> 32 ${ismessageid}"
                                        )
                                        messagerDatabaseRepo.updatemessagestatusRepo(
                                            "Sending", it
                                        )
                                    }
//                                    if (!messagerDatabaseRepo.isMessageExitsRepo(lastAttachment.messageId)) {
//                                        Log.d(
//                                            "",
//                                            "sendMessage:forEachIndexed <----------> 504 ${lastAttachment}"
//                                        )
//                                        val conversation = Conversation(
//                                            0,
//                                            c.time.toString(),
//                                            false,
//                                            fetchContactIdFromPhoneNumber(
//                                                dest,
//                                                this@SendMessageActivity
//                                            )!!,
//                                            null,
//                                            false,
//                                            dest,
//                                            messagessub.joinToString(),
//                                            c.time,
//                                            2,
//                                            dest.isDigitsOnly(),
//                                            messageStatus = null,
//                                            messageId = lastAttachment.messageId,
//                                            threadId = tredid!!,
//                                            messagewithattachment = MessageAttachment(
//                                                lastAttachment.messageId,
//                                                messagessub.joinToString(),
//                                                arrayListOf(lastAttachment)
//                                            ),
//                                            isarchived = isarchiv,
//                                            isPrivateChat = isPrivateChat,
//                                            shownotification = isUserNotificationShow
//                                        )
//
//                                        val ismessageid =
//                                            messagerDatabaseRepo.insertmessage(conversation)
//
//                                        ismessageid?.let {
//                                            Log.d(
//                                                "",
//                                                "updateAppDatabase: <-------------> 32 ${ismessageid}"
//                                            )
//                                            messagerDatabaseRepo.updatemessagestatusRepo(
//                                                "Sending",
//                                                it
//                                            )
//                                        }
//                                    }else{
//                                        Log.d(
//                                            "",
//                                            "sendMessage:forEachIndexed <----------> 505 ${lastAttachment}"
//                                        )
//                                    }
                                }
                            }
                        }
                    }
                    runOnUiThread {
                        if (isScheduledMessage) {
                            val attachments = buildMessageAttachments()
                            if (attachments.isNotEmpty()) {
                                return@runOnUiThread
                            }
                        }
                        binding.writemessageedt.text!!.clear()
                        getAttachmentsAdapter()?.clear()
                    }
                }
            }
            baseConfig.apply {
                if ("First_messagesent".funnelState) {
                    firebaseFunnel("First_messagesent")
                    "First_messagesent".funnelState = false
                }
            }


        }
    }


    private fun sendScheduledMessage(
        text: String,
        addresses: List<String>,
        subscriptionId: Int,
        attachments: List<Attachment>,
        isgroupmessage: Boolean,
    ) {
        if (scheduledDateTime.millis < System.currentTimeMillis() + 1000L) {
            toast(R.string.must_pick_time_in_the_future)
            launchScheduleSendDialog(scheduledDateTime)
            return
        }

        try {
            val messageId = scheduledMessage?.id?.toLong() ?: generateRandomId()
            CoroutineScope(Dispatchers.IO).launch {

                try {
                    val mess = findOtpInString(text)
                    if (mess.isEmpty()) {
                        messagetype = "normalmessage"
                    } else {
                        messagetype = "otp"
                        messageotp = mess
                    }
                } catch (_: Exception) {

                }

                val list = messagerDatabaseRepo.getUserMessageListrepo(tredid!!)
                val isarchiv = if (list.isEmpty()) {
                    false
                } else {
                    list[0].isarchived
                }

                val isPrivateChat = if (list.isEmpty()) {
                    false
                } else {
                    list[0].isPrivateChat
                }


                isUserNotificationShow =
                    if (messagerDatabaseRepo.isNewUserMessageExitsRepo(tredid!!)) {
                        messagerDatabaseRepo.isUserNotificationshowRepo(tredid!!)
                    } else {
                        true
                    }
                val c: Date = Calendar.getInstance().time



                if (addresses.size > 1) {


                    if (!messagerDatabaseRepo.isMessageExitsRepo(messageId)) {


                        val data = messagerDatabaseRepo.getUserMessageListChackrepo(tredid!!)
                        if (data.isNotEmpty()) {


                            val isgropmessage = data[0].isgroupmessage
                            if (isgropmessage) {


                                val conversation = Conversation(
                                    0,
                                    c.time.toString(),
                                    true,
                                    data[0].title,
                                    null,
                                    false,
                                    data[0].phoneNumber,
                                    text,
                                    (scheduledDateTime.millis),
                                    2,
                                    true,
                                    null,
                                    messageId = messageId,
                                    is_scheduled = true,
                                    threadId = tredid,
                                    isgroupmessage = true,
                                    groupName = data[0].groupName,
                                    isarchived = isarchiv,
                                    isPrivateChat = isPrivateChat
                                )
                                messagerDatabaseRepo.insertmessage(conversation)
                                scheduleMessage(conversation)
                            } else {

                            }
                        } else {


                            val participantsforgroupchat = getThreadParticipants(tredid!!, null)

                            if (participantsforgroupchat.isNotEmpty()) {
                                val addresses = participantsforgroupchat.getAddresses()
                                val result = addresses.joinToString(separator = "|")


                                val conversation = Conversation(
                                    0,
                                    c.time.toString(),
                                    true,
                                    participantsforgroupchat.getThreadTitle(),
                                    null,
                                    false,
                                    result,
                                    text,
                                    (scheduledDateTime.millis),
                                    2,
                                    true,
                                    null,
                                    messageId = messageId,
                                    threadId = tredid,
                                    isgroupmessage = true,
                                    is_scheduled = true,
                                    groupName = if (groupnamecustom != null) {
                                        groupnamecustom
                                    } else {
                                        participantsforgroupchat.getThreadTitle()
                                    },
                                    isarchived = isarchiv,
                                    isPrivateChat = isPrivateChat
                                )
                                messagerDatabaseRepo.insertmessage(conversation)
                                scheduleMessage(conversation)
                            } else {
                                var addresses = participants.getAddressesPre()
                                val result = addresses.joinToString(separator = "|")


                                val conversation = Conversation(
                                    0,
                                    c.time.toString(),
                                    true,
                                    participants.getThreadTitlePre(),
                                    null,
                                    false,
                                    result,
                                    text,
                                    (scheduledDateTime.millis),
                                    2,
                                    true,
                                    null,
                                    messageId = messageId,
                                    threadId = tredid,
                                    isgroupmessage = true,
                                    is_scheduled = true,
                                    groupName = if (groupnamecustom != null) {
                                        groupnamecustom
                                    } else {
                                        participants.getThreadTitlePre()
                                    },
                                    isarchived = isarchiv,
                                    isPrivateChat = isPrivateChat
                                )
                                messagerDatabaseRepo.insertmessage(conversation)
                                scheduleMessage(conversation)

                            }
                        }
                    }
                } else {


                    conversation = mobileNumber?.let {
                        Conversation(
                            0,
                            (scheduledDateTime.millis).toString(),
                            false,
                            fetchContactIdFromPhoneNumber(mobileNumber, this@SendMessageActivity)!!,
                            null,
                            false,
                            it,
                            text,
                            (scheduledDateTime.millis),
                            2,
                            it.isDigitsOnly(),
                            "ScheduledMessage",
                            messageId = messageId,
                            threadId = tredid,
                            is_scheduled = true,
                            messagetype = messagetype,
                            messageotp = messageotp,
                            isarchived = isarchiv,
                            isPrivateChat = isPrivateChat,
                            shownotification = isUserNotificationShow,
                            isgroupmessage = isgroupmessage
                        )

                    }!!
                    messagerDatabaseRepo.insertmessage(conversation)
                    scheduleMessage(conversation)
                }

                runOnUiThread {
                    hideScheduleSendUi()
                    scheduledMessage = null
                }
            }

        } catch (e: Exception) {
            showErrorToast(e.localizedMessage ?: getString(R.string.unknown_error_occurred))
        }

        Constants.isActivitychange = true
//        startActivity(
//            Intent(this, Schedule_Message_Show_Activity::class.java).putExtra("ismessagefind", true).putExtra("ismessagefindtime", (scheduledDateTime.millis))
//        )
//        finish() ̰


    }

    override fun SimOne() {
        setMessageSendSim(0, this)
        binding.simNumber = "1"
    }

    override fun SimTwo() {
        setMessageSendSim(1, this)
        binding.simNumber = "2"
    }

    private fun setDefaultApp() {
        when {
            packageName != Telephony.Sms.getDefaultSmsPackage(this) -> {
                requestDefaultApp(onDefaultAppResult)
            }
        }
    }

    private val onDefaultAppResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            config.defaultSmsDialogSuppressed = true

            if (PackageManager.PERMISSION_DENIED in Array(ARR_PERMS.size) {
                    ActivityCompat.checkSelfPermission(this, ARR_PERMS[it])
                }) {

            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    if (sendmessagebuttondefaultset) {
//                        finish()
                    }
                    chackDataBase()
                }
            }
        }

    suspend fun chackDataBase() {
        if (isFLow1 || isFLow2) {
            val threadId = tredid ?: 0L
            if (threadId > 0L) {
                chatSmsSyncManager.syncThread(threadId)
            }
            return
        }
        getMobileMessage.refreshSmsInbox()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        finish()
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {

            config.issubbannershow = false
            messageNotify = "-1"
            groupnamecustom = null
            CoroutineScope(Dispatchers.IO).launch {
                messagerDatabaseRepo.updateismassageinselectedRepo()
                tredid?.let { messagerDatabaseRepo.setisoldmessageRepo(false, it) }
                tredid?.let { messagerDatabaseRepo.setisoldmessageCountRepo(0, it) }
//            tredid?.let { messagerDatabaseRepo.setiscrolltonewmessageoffRepo(it) }
                tredid?.let { markThreadAsRead(it) }
            }
        } catch (e: Exception) {
        }
    }

    override fun onStop() {
        unregisterSmsContentObserver()
        super.onStop()
        markallmessageread()
    }

    private fun shouldSyncSmsProvider(): Boolean {
        return packageName != Telephony.Sms.getDefaultSmsPackage(this)
    }

    private fun registerSmsContentObserver() {
        if (!shouldSyncSmsProvider() || smsContentObserver != null) {
            return
        }

        smsContentObserver = object : ContentObserver(smsContentObserverHandler) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
                if (!shouldSyncSmsProvider()) {
                    return
                }
                // Debounce to avoid heavy refreshes for rapid changes.
                smsContentObserverHandler.removeCallbacks(smsContentObserverRunnable)
                smsContentObserverHandler.postDelayed(smsContentObserverRunnable, 350L)
            }
        }

        contentResolver.registerContentObserver(
            Telephony.Sms.CONTENT_URI, true, smsContentObserver!!
        )
    }

    private fun unregisterSmsContentObserver() {
        smsContentObserver?.let {
            contentResolver.unregisterContentObserver(it)
        }
        smsContentObserver = null
        smsContentObserverHandler.removeCallbacks(smsContentObserverRunnable)
    }

    private fun syncSmsProviderForChatIfNeeded() {
        if (!shouldSyncSmsProvider()) {
            return
        }
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        lifecycleScope.launch(Dispatchers.IO) {
            if (isFLow1 || isFLow2) {
                val threadId = tredid ?: 0L
                if (threadId > 0L) {
                    chatSmsSyncManager.syncThread(threadId)
                }
            } else {
                getMobileMessage.refreshSmsInbox()
            }
        }
    }

    private fun markallmessageread() {
        CoroutineScope(Dispatchers.IO).launch {
            tredid?.let { messagerDatabaseRepo.setisoldmessageRepo(false, it) }
            tredid?.let { messagerDatabaseRepo.setisoldmessageCountRepo(0, it) }
            tredid?.let { markThreadAsRead(it) }
        }
    }

    private fun buildThreadSnapshot(messages: List<Conversation>): Int {
        var hash = 1
        messages.forEach { msg ->
            hash = 31 * hash + (msg.messageId ?: msg.id.toLong()).hashCode()
            hash = 31 * hash + (msg.time ?: 0L).hashCode()
            hash = 31 * hash + msg.snippet.hashCode()
            hash = 31 * hash + (msg.messageStatus ?: "").hashCode()
            hash = 31 * hash + msg.read.hashCode()
            hash = 31 * hash + (msg.isnewmessage ?: false).hashCode()
            hash = 31 * hash + (msg.newMessageCount ?: 0).hashCode()
            hash = 31 * hash + (msg.messagewithattachment?.attachments?.size ?: 0)
            hash = 31 * hash + (
                    msg.messagewithattachment?.attachments?.joinToString(separator = "|") { it.uriString }
                        ?.hashCode() ?: 0
                    )
        }
        return hash
    }

    private fun launchSafeContactPicker() {
//        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
//        startActivityForResult(intent, PICK_CONTACT_REQUEST)


        val intent =
            Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI).apply {
                type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
                // optional extras
                // putExtra("android.intent.extra.TITLE", "Choose contact")
            }

        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, PICK_CONTACT_REQUEST)
        } else {
//            Toast.makeText(this, "No contact picker found on this device", Toast.LENGTH_LONG).show()
        }

        /*
        try {
            startActivityForResult(intent, PICK_CONTACT_REQUEST)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No contact picker available", Toast.LENGTH_LONG).show()
        }
        */
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    fun getsetIntent(intent: Intent) {

        showOrHideKeyboard(true)

        if (config.SelectedLanguage == "ar") {
            binding.writemessageedt.gravity = Gravity.END
        } else {
            binding.writemessageedt.gravity = Gravity.START
        }

        googleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.getInstance(applicationContext)
        requestConsentIfActive()

        if (googleMobileAdsConsentManager.canRequestAds) {
            isshowads = true
        }

        forscheduleMessage = intent.getBooleanExtra("forscheduleMessage", false)
        connectionLiveData = ConnectionLiveData(this)
        connectionLiveData.observe(this, Observer {
            isnetonoff = it
            if (it == false) {
                if (traslateornetonoff) {
                    traslateornetonoff = false
                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            messagerDatabaseRepo.getUserMessageListChackrepo(tredid!!)
                                .forEachIndexed { index, conversation ->
                                    inMainAdapter.list[index].snippet = conversation.snippet
                                    inMainAdapter.list[index].messagetraslationanimationshow = false
                                }
                        }
                        withContext(Dispatchers.Main) {
                            inMainAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        })

        if (!replyChipAdapter.hasObservers()) {
            replyChipAdapter.setHasStableIds(true)
        }

        binding.attachFileBtn.setImageResource(
            R.drawable.baseline_attach_file_24
        )


        if (binding.writemessageedt.text?.isNotEmpty() == true) {
            binding.sendbutton.setImageResource(R.drawable.baseline_send_24)
        } else {
            binding.sendbutton.setImageResource(R.drawable.baseline_send_24)
        }

        binding.replyChipAdapterData = replyChipAdapter
        replyChipAdapter.SmartReplyInterface(this)
        binding.messagecountsend.beVisibleIf(config.showCharacterCounter)
        adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, arrayListOf()
        )
        binding.selectlan.adapter = adapter
        binding.selectlan.onItemSelectedListener = this;
        binding.showusermessage.removeAllViews()

        messageSwipeController = MessageSwipeControllerNew(this, object : SwipeControllerActions {
            override fun showReplyUI(position: Int) {
                if (binding.notreply.isVisibleMess()) {
                    return
                }
                isReplayMessage = true
                binding.SwipToReplyHolder.visible()
                binding.writemessageedt.requestFocus()
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                this@SendMessageActivity.showKeyboard(binding.writemessageedt)
                binding.SwipMessageTxt.text = datanetonoff[position].snippet
                ReplayMessage = datanetonoff[position].snippet
                binding.SwipMessageTxtReceverName.text = binding.userid.text.toString()
                applyMaterialComposerBackground(mergeTopCorners = true)
            }
        })

        val itemTouchHelper = ItemTouchHelper(messageSwipeController)
        itemTouchHelper.attachToRecyclerView(binding.showusermessage)
        tredid = intent.getLongExtra("tredid", 0L)
        fromnotification = intent.getBooleanExtra("fromnotification", false)
        fromnotification2 = intent.getBooleanExtra("fromnotification2", false)
        openFromAfterCallList = intent.getBooleanExtra("open_from_after_call_list", false)
        isfromenotification = fromnotification2
        binding.writemessageedt.isVerticalScrollBarEnabled = true;

        binding.writemessageedt.onTextChangeListener {

            if (it.trim().isNotEmpty() || getAttachmentSelections().isNotEmpty()) {
                binding.sendbutton.alpha = 1.0f
            } else {
                binding.sendbutton.alpha = 0.25f
            }

            val messageLength = SmsMessage.calculateLength(it, false)
            binding.messagecountsend.text = "${messageLength[2]}/${messageLength[0]}"


        }

        mobileNumber = if (intent.getStringExtra("mobileNumber") == null) {
            ""
        } else {
            intent.getStringExtra("mobileNumber")
        }


        namenew = (intent.getStringExtra("name"))

        name = if (intent.getStringExtra("name") == null) {
            mobileNumber
        } else {
            intent.getStringExtra("name")
        }



        isgroupmessage = intent.getBooleanExtra("isgroupmessage", false)
        if (isgroupmessage) {
            binding.colorTheme.visibility = View.GONE
        } else {
            binding.colorTheme.visibility = View.VISIBLE
        }

        CoroutineScope(Dispatchers.IO).launch {
            val phoneNumber = mobileNumber?.let { PhoneNumber(it, 0, "", it) }
            val contact = name?.let {
                SimpleContact(
                    0, 0, it, "", arrayListOf(phoneNumber!!), ArrayList(), ArrayList()
                )
            }
            if (contact != null) {
                if (contact.phoneNumbers.isNotEmpty()) {
                    if (contact.phoneNumbers[0].value.isNotEmpty()) {
                        if (isgroupmessage) {
                            val number = contact.phoneNumbers[0].value
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

                            if (numbersArray.isNotEmpty()) {
                                for (numberget in numbersArray) {
                                    val phoneNumbergrop =
                                        numberget.toString().let { PhoneNumber(it, 0, "", it) }
                                    val contactgrop = fetchContactIdFromPhoneNumber(
                                        phoneNumbergrop.normalizedNumber, this@SendMessageActivity
                                    ).let {
                                        SimpleContact(
                                            0,
                                            0,
                                            it,
                                            "",
                                            arrayListOf(phoneNumbergrop),
                                            ArrayList(),
                                            ArrayList()
                                        )
                                    }
                                    participants.add(contactgrop)
                                }
                            }

                        } else {
                            participants.add(contact)
                        }
                    }
                }

            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            senderName = try {
                val conversation = getConversations(tredid).firstOrNull()
                conversation?.phoneNumber!!
            } catch (e: Exception) {
                mobileNumber!!
            }
            CoroutineScope(Dispatchers.Main).launch {
                val isFLetter =
                    senderName.isNotEmpty() && /*senderName.first().isLetter()*/ isLikelyShortCode(
                        mobileNumber
                    )
                try {
                    binding.notreplychack =
                        isFLetter || !hasSim() || isLikelyShortCode(mobileNumber)
                    "<-----------> senderName ${senderName}".log()
                    "<-----------> senderName 1 ${isFLetter}".log()
                    if (isFLetter && isLikelyShortCode(mobileNumber)) {
//                        adsLoadAndShow()
                        binding.smartRepliesRecycler.gone()
                    } else {
                        try {
                            binding.constnativeBannerAdView.removeAllViews()
                        } catch (_: Exception) {

                        }
                        binding.constnativeBannerAdView.gone()
                        if (!hasSim()) {
                            binding.smartRepliesRecycler.gone()
                        } else {
                            binding.smartRepliesRecycler.visible()
                        }
                    }

                    if (!isFLetter) {
                        Log.d("", "getsetIntent: notreply <-------> 1")
                        if (config.sedulemessageshowcaseshow) {
                            Log.d("", "getsetIntent: notreply <-------> 2")
                            config.sedulemessageshowcaseshow = false
//                            showTutorial()
                        } else {
                            Log.d("", "getsetIntent: notreply <-------> 3")
                        }
                    } else {
                        Log.d("", "getsetIntent: notreply <-------> 4")
                    }

                } catch (e: Exception) {

                }
            }
        }


//---------------------


        binding.threadMessagesFastscroller.updateColors(resources.getColor(R.color.procolor, theme))

        CoroutineScope(Dispatchers.IO).launch {
            if (isThirdPartyIntent()) {
                val phoneNumber = URLDecoder.decode(
                    intent.dataString!!.removePrefix("sms:").removePrefix("smsto:")
                        .removePrefix("mms").removePrefix("mmsto:").replace("+", "%2b").trim()
                )
                val numbers = phoneNumber.split(";").toSet()
                val number = if (numbers.size == 1) phoneNumber else Gson().toJson(numbers)
                mobileNumber = number
                tredid = getThreadId(number)

                senderName = try {
                    val conversation = getConversations(tredid).firstOrNull()
                    conversation?.phoneNumber!!
                } catch (e: Exception) {
                    mobileNumber!!
                }

                name = fetchContactIdFromPhoneNumber(mobileNumber, this@SendMessageActivity)
                if (name?.isEmpty() == true) {
                    name = mobileNumber
                }


                CoroutineScope(Dispatchers.Main).launch {
                    val isFLetter =
                        senderName.isNotEmpty()   /*senderName.first().isLetter()*/ && isLikelyShortCode(
                            mobileNumber
                        )

                    binding.notreplychack = isFLetter || !hasSim()
                    if (isFLetter) {
//                        adsLoadAndShow()
                        binding.smartRepliesRecycler.gone()
                    } else {
                        try {
                            binding.constnativeBannerAdView.removeAllViews()
                        } catch (_: Exception) {

                        }
                        binding.constnativeBannerAdView.gone()
                        if (!hasSim()) {
                            binding.smartRepliesRecycler.gone()
                        } else {
                            binding.smartRepliesRecycler.visible()
                        }
                    }

                    if (!isFLetter) {

                        Log.d("", "getsetIntent: notreply <-------> 1")
                        if (config.sedulemessageshowcaseshow) {
                            Log.d("", "getsetIntent: notreply <-------> 2")
                            config.sedulemessageshowcaseshow = false
//                            showTutorial()
                        } else {
                            Log.d("", "getsetIntent: notreply <-------> 3")
                        }
                    } else {
                        Log.d("", "getsetIntent: notreply <-------> 4")
                    }
                }

            }
        }


//---------------

        message = intent.getStringExtra("message").toString()

        isforvard = intent.getBooleanExtra("isforvard", false)

        isSearchFound = intent.getBooleanExtra("isSearchFound", false)


        if (isforvard) {
            binding.writemessageedt.setText(message)
        }

        if (tredid == null) {
            finish()
        }

        if (isgroupmessage) {
            CoroutineScope(Dispatchers.IO).launch {
                val participantsforgroupchat = getThreadParticipants(tredid!!, null)
                if (participantsforgroupchat.isNotEmpty()) {
                    name = participantsforgroupchat.getThreadTitle()
                    val addresses = participantsforgroupchat.getAddresses()
                    senderName = addresses.joinToString()
                }
            }
        }

        inMainAdapter.setInterface(this)
        inMainAdapter.setOnLangInterface(this)
        messageNotify = tredid.toString()

        try {
            binding.isLetterorNot = name?.get(0)?.isLetter()
        } catch (_: Exception) {
            binding.isLetterorNot = false
        }
        if (chackPermission()) {

            try {
                binding.fistcat.text = name?.substring(0, 1)
            } catch (_: Exception) {
                binding.fistcat.text = "J"
            }
        }


//        binding.sendbutton.setOnClickListener {
//            if (SystemClock.elapsedRealtime() - mLastClickTime < 300) {
//                return@setOnClickListener;
//            }
//            mLastClickTime = SystemClock.elapsedRealtime();
//
//
//
//            if (binding.writemessageedt.text.toString().trim() != "") {
//                GlobalScope.launch {
//                    sendMessage()
//                }
//                scrollToBottom()
//            }
//        }

        binding.sendbutton.setOnLongClickListener {
            if (binding.writemessageedt.text.toString().trim() != "") {
                if (isSPlus()) {
                    val alarmManager: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                    if (alarmManager.canScheduleExactAlarms()) {
                        sendSuduleMessage()
                    } else {
                        PermissionRequiredDialog(
                            activity = this,
                            textId = R.string.allow_alarm_scheduled_messages,
                            positiveActionCallback = {
                                openRequestExactAlarmSettings("com.messenger.phone.number.text.sms.service.apps")
                            },
                        )
                    }
                } else {
                    sendSuduleMessage()
                }
            } else {

            }
            return@setOnLongClickListener true
        }

        dialogSimCardSelect.setinterface(this)

        try {
//            if (chackPermission()) {
//
//            }
            binding.userid.text = name
        } catch (error: Exception) {
            binding.userid.text = name
        }
        inMainAdapter.setHasStableIds(true)


//        val itemAnimator = MyItemAnimator()
//        binding.showusermessage.itemAnimator = itemAnimator


        binding.adapter = inMainAdapter

        thredidLock = tredid!!
        nameLock = name!!
        mobileNumberLock = mobileNumber.toString()

        binding.datetxtCon.setOnClickListener {
            scrollToBottom()
        }

        binding.serchCleasr.setOnClickListener {
            binding.messageserch.text!!.clear()
        }

        val realmInstance = if (RealmFeatureFlag(this).useRealmReads) {
            runCatching { realm.get() }.getOrNull()
        } else {
            null
        }
        ConversationDataSourceProvider.get(this, messagerDatabaseRepo, realmInstance).observeMessages(tredid!!).observe(this, Observer {
            val currentThreadData = it.distinctBy { msg -> msg.messageId ?: msg.id.toLong() }
            val currentSnapshot = buildThreadSnapshot(currentThreadData)
            if (lastObservedThreadSnapshot == currentSnapshot) {
                return@Observer
            }
            lastObservedThreadSnapshot = currentSnapshot
            Log.d("Krupal", "setDataSerch: -->datanetonoff: ${it.map { it.messagewithattachment }}")
            messages.clear()
            CoroutineScope(Dispatchers.IO).launch { messages.addAll(getMessages(tredid!!, true)) }
            try {
                val itarraylist: ArrayList<Conversation> = arrayListOf()
                it.forEachIndexed { index, conversation ->
                    if (conversation.is_scheduled) {
                        conversation.isexpandmessageview = false
                        itarraylist.add(conversation)
                    }
                }

                demolist.clear()
                demolist.addAll(ArrayList(it.distinctBy { it.messageId }))
                datanetonoff = ArrayList(it.distinctBy { it.messageId })
                datanetonoff =
                    ArrayList(datanetonoff.sortedWith(compareBy<Conversation> { it.date }.thenBy { it.id }))
                if (binding.messageSearchholder.isVisibleMess()) {
                    binding.messageserch.setText(higlitetxt.toString())
                    binding.messageserch.setSelection(binding.messageserch.text!!.length)
                }

                messageSwipeController.getlistlive(datanetonoff)
                if (it.isEmpty()) {
                    showOrHideKeyboard(true)
                }
                if (!isSearchFound) {
                    inMainAdapter.list = datanetonoff
//                    inMainAdapter.setNewData(datanetonoff)
                }

                if (it.isNotEmpty()) {
                    smartReplyViewModel.addMessage(it.last().snippet)
                    setDataSerch(ArrayList(it.sortedWith(compareBy<Conversation> { it.date }.thenBy { it.id })))
                }
                if (!isThirdPartyIntent()) {
                    if (issendmessage) {
                        if (it.isEmpty()) {
//                        finish()
                        }
                    }
                } else {
                    if (issendmessage) {
                        if (it.isEmpty()) {
                            startActivity(
                                Intent(
                                    this, HomeABActivity::class.java
                                )
                            )
//                        finish()
                        }
                    }
                }



                if (!isSearchFound) {
                    try {
                        if (it.isNotEmpty()) {
                            if (selectedMessageList2.isEmpty()) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val data =
                                        messagerDatabaseRepo.getisnewMessageRepo(tredid!!.toLong())
                                    if (data != null) {
                                        if (datanetonoff.isNotEmpty()) {
                                            val pos = findIndexOfFirstNewMessage(datanetonoff)
                                            try {
                                                inMainAdapter.list[pos].isnewmessagescroll = true
                                                if (autoscrollstart) {
                                                    runOnUiThread {
                                                        try {
                                                            if (binding.messageserch.text!!.isEmpty()) {
                                                                binding.showusermessage.smoothScrollToPosition(
                                                                    it.size
                                                                )
                                                            }
                                                            if (inMainAdapter.list.isNotEmpty()) {
                                                                inMainAdapter.list[datanetonoff.size - 1].isbanneradshow =
                                                                    true
                                                            }
                                                        } catch (e: Exception) {
                                                        }
                                                    }
                                                } else {
                                                    if (!unreadmessagecoundshow) {
                                                        unreadmessagecoundshow = true
                                                        runOnUiThread {
                                                            try {
                                                                binding.showusermessage.scrollToPosition(
                                                                    pos - 1
                                                                )
                                                                if (inMainAdapter.list.isNotEmpty()) {
                                                                    inMainAdapter.list[pos - 1].isbanneradshow =
                                                                        true
                                                                }
                                                            } catch (_: Exception) {

                                                            }
                                                        }
                                                    }
                                                }
                                                return@launch
                                            } catch (_: Exception) {
                                            }
                                        }
                                    } else {
                                        if (autoscrollstart) {
                                            try {
                                                runOnUiThread {
                                                    binding.showusermessage.smoothScrollToPosition(
                                                        it.size
                                                    )
                                                }
                                                if (inMainAdapter.list.isNotEmpty()) {
                                                    inMainAdapter.list[datanetonoff.size - 1].isbanneradshow =
                                                        true
                                                }
                                            } catch (e: Exception) {
                                            }
                                        } else {
                                            try {
                                                if (inMainAdapter.list.isNotEmpty()) {
                                                    inMainAdapter.list[datanetonoff.size - 1].isbanneradshow =
                                                        true
                                                }
                                            } catch (r: Exception) {

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {

                    }
                }

                newSize = datanetonoff.size
                if (newSize > curruntmessagesize) {
                    newmessagecount += 1
                    curruntmessagesize = newSize
                }

                if (autoscrollstart) {
                    newmessagecount = 0
                    binding.datetxtCon.gone()
                } else {
                    if (newmessagecount != 0) {
                        binding.datetxtCon.visible()
                        binding.datetxt.text =
                            "${newmessagecount} " + resources.getString(R.string.channel_new_messages)
                    }
                }
            } catch (e: Exception) {
            }
        })

        smartReplyViewModel.getSuggestions().observe(
            this
        ) { suggestions ->
            run {
                val list = (ArrayList(suggestions))
                if (list.isNotEmpty()) {
                    replyChipAdapter.suggestions = list
                }
            }
        }

        binding.sendbutton.setOnClickListener {


            "makeMy -> ${Telephony.Sms.getDefaultSmsPackage(this@SendMessageActivity) == packageName}".log()
            if (packageName == Telephony.Sms.getDefaultSmsPackage(this@SendMessageActivity)) {
                if (binding.writemessageedt.text.toString()
                        .trim() != "" || getAttachmentSelections().isNotEmpty()
                ) {
                    GlobalScope.launch {
                        sendMessage()
                    }
                    scrollToBottom()
                }
            } else {
                handleDefaultSmsClick()
            }
        }


        binding.messagetraslateall.setOnClickListener {
            firebaseEvent("SendMessageActivity", "Message Translation")

            if (config.forfirsttimeuser) {
                messageLanguageDialog =
                    MessageLanguageDialog.newInstance(null, 0, callinterface = true)
                messageLanguageDialog?.setInterface(this@SendMessageActivity)
                messageLanguageDialog?.show(supportFragmentManager, "messageLanguageDialog")
            } else {
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        MessageTraslationlist.clear()
                        inMainAdapter.selectedMessageList.forEach {
                            if (it.snippet != "") {
                                MessageTraslationlist.add(
                                    MessageTraslationModel(
                                        traslationmessage = it.snippet, message = it.snippet
                                    )
                                )
                            }
                        }
                    }
                    withContext(Dispatchers.Main) {
                        if (MessageTraslationlist.isNotEmpty()) {
                            startActivity(
                                Intent(
                                    this@SendMessageActivity,
                                    Message_Translation_Activity::class.java
                                )
                            )
                            removeselection()
                        } else {
                            toastMess("no")
                        }

                    }
                }
            }


//            if (!isOnline()) {
//                toastMess(resources.getString(R.string.Please_turn_on))
//            }
//
//            if (isshowcasestartshowing) {
//                return@setOnClickListener;
//            }
//
//            selectedMessageList2.toList().forEach { conversation ->
//                val position =
//                    inMainAdapter.list.indexOfFirst { it.messageId == conversation.messageId }
//                if (position != -1) { // Ensure the item is found in inMainAdapter.list
//                    val message = inMainAdapter.list[position].snippet
//                    inMainAdapter.list[position].snippet =
//                        resources.getString(R.string.translate_to) + " " + config.userpreferencelanguage
//                    inMainAdapter.list[position].messagetraslationanimationshow = true
//                    inMainAdapter.list[position].messagetraslateshow = false
//
//                    synchronized(inMainAdapter.selectedMessageList) {
//                        val iterator = inMainAdapter.selectedMessageList.iterator()
//                        while (iterator.hasNext()) {
//                            val item = iterator.next()
//                            if (item.messageId == conversation.messageId) {
//                                iterator.remove()
//                            }
//                        }
//                    }
//
//                    inMainAdapter.notifyDataSetChanged()
//                    CoroutineScope(Dispatchers.IO).launch {
//                        translatemessage(message, config.userpreferencelanguageCode, position)
//                    }
//                }
//            }

//            if (!isOnline()) {
//                toastMess(resources.getString(R.string.Please_turn_on))
//                return@setOnClickListener
//            }
//            GlobalScope.launch {
//                withContext(Dispatchers.IO) {
//                    MessageTraslationlist.clear()
//                    inMainAdapter.selectedMessageList.forEach {
//                        MessageTraslationlist.add(
//                            MessageTraslationModel(
//                                traslationmessage = it.snippet,
//                                message = it.snippet
//                            )
//                        )
//                    }
//                }
//                withContext(Dispatchers.Main) {
//                    startActivity(
//                        Intent(
//                            this@SendMessageActivity, Message_Translation_Activity::class.java
//                        )
//                    )
//                    removeselection()
//                }
//            }


        }

//        binding.messagetraslateall.setOnClickListener {
//
//            if (!isOnline()) {
//                toastMess(resources.getString(R.string.Please_turn_on))
//            }
//
//            if (isshowcasestartshowing) {
//                return@setOnClickListener;
//            }
//
//            selectedMessageList2.toList().forEach { conversation ->
//                val position =
//                    inMainAdapter.list.indexOfFirst { it.messageId == conversation.messageId }
//                if (position != -1) { // Ensure the item is found in inMainAdapter.list
//                    val message = inMainAdapter.list[position].snippet
//                    inMainAdapter.list[position].snippet =
//                        resources.getString(R.string.translate_to) + " " + config.userpreferencelanguage
//                    inMainAdapter.list[position].messagetraslationanimationshow = true
//                    inMainAdapter.list[position].messagetraslateshow = false
//
//                    synchronized(inMainAdapter.selectedMessageList) {
//                        val iterator = inMainAdapter.selectedMessageList.iterator()
//                        while (iterator.hasNext()) {
//                            val item = iterator.next()
//                            if (item.messageId == conversation.messageId) {
//                                iterator.remove()
//                            }
//                        }
//                    }
//
//                    inMainAdapter.notifyDataSetChanged()
//                    CoroutineScope(Dispatchers.IO).launch {
//                        translatemessage(message, config.userpreferencelanguageCode, position)
//                    }
//                }
//            }
//            removeselection()
//        }

        binding.translateBtn.setOnClickListener {

            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener;
            }

            if (isshowcasestartshowing) {
                return@setOnClickListener;
            }

            if (!isOnline()) {
                toast(resources.getString(R.string.Please_turn_on))
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            traslatebtnclick = true
            val selectedMessage = selectedMessageList2.firstOrNull() ?: return@setOnClickListener
            var pos = inMainAdapter.list.indexOf(selectedMessage)
            messageLanguageDialog =
                MessageLanguageDialog.newInstance(selectedMessage.snippet, pos)
            messageLanguageDialog?.setInterface(this@SendMessageActivity)
            messageLanguageDialog?.show(supportFragmentManager, "messageLanguageDialog")
        }

        binding.copyBtn.setOnClickListener {
            if (isshowcasestartshowing) {
                return@setOnClickListener;
            }
            val selectedMessage = selectedMessageList2.firstOrNull() ?: return@setOnClickListener
            val trimmedSnippet = selectedMessage.snippet.replace(Regex("^\\s+|\\s+$"), "")
            copyToClipboard(trimmedSnippet)
            showcustomSnackbar(trimmedSnippet)
            selectedMessageList2.clear()
            inMainAdapter.selectedMessageList.clear()
            inMainAdapter.notifyDataSetChanged()
            binding.moreBtn.visibility = View.GONE
            binding.copyBtn.visibility = View.GONE
            binding.messageshare.visibility = View.GONE
            binding.messagetraslateall.visibility = View.GONE
            binding.deleteBtn.visibility = View.GONE
            binding.messagecount.visibility = View.GONE
            binding.userid.visibility = View.VISIBLE
            binding.maincanternar.visibility = View.VISIBLE
            binding.moreBtnForEditing.visibility = View.VISIBLE
            binding.newSearch.visibility = View.VISIBLE
            if (isgroupmessage) {
                binding.colorTheme.visibility = View.GONE
            } else {
                binding.colorTheme.visibility = View.VISIBLE
            }
        }

        binding.messageshare.setOnClickListener {
            if (isshowcasestartshowing) {
                return@setOnClickListener;
            }
            val selectedMessage = selectedMessageList2.firstOrNull() ?: return@setOnClickListener
            if (packageName == Telephony.Sms.getDefaultSmsPackage(this@SendMessageActivity)) {

                startActivity(
                    Intent(this, SelectContactActivity::class.java).putExtra(
                        "message", selectedMessage.snippet.replace(Regex("^\\s+|\\s+$"), "")
                    ).putExtra("isforvard", true)
                )
            } else {
                handleDefaultSmsClick_1(this@SendMessageActivity)
            }
            selectedMessageList2.clear()
            inMainAdapter.selectedMessageList.clear()
            inMainAdapter.notifyDataSetChanged()
            binding.moreBtn.visibility = View.GONE
            binding.copyBtn.visibility = View.GONE
            binding.messageshare.visibility = View.GONE
            binding.messagetraslateall.visibility = View.GONE
            binding.deleteBtn.visibility = View.GONE
            binding.messagecount.visibility = View.GONE
            binding.userid.visibility = View.VISIBLE
            binding.maincanternar.visibility = View.VISIBLE
            binding.moreBtnForEditing.visibility = View.VISIBLE
            binding.newSearch.visibility = View.VISIBLE
            if (isgroupmessage) {
                binding.colorTheme.visibility = View.GONE
            } else {
                binding.colorTheme.visibility = View.VISIBLE
            }
        }

        binding.moreBtn.setOnClickListener {
            showMenu(it, R.menu.in_message_menu)
        }

        binding.ShareBtn.setOnClickListener {
            val selectedMessage = selectedMessageList2.firstOrNull() ?: return@setOnClickListener
            val intent = Intent(Intent.ACTION_SEND)
            val shareBody = selectedMessage.snippet
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(intent, getString(R.string.app_name)))
            selectedMessageList2.clear()
            inMainAdapter.selectedMessageList.clear()
            inMainAdapter.notifyDataSetChanged()
            binding.forverdBtn.visibility = View.GONE
            binding.infoBtn.visibility = View.GONE
            binding.copyBtn.visibility = View.GONE
            binding.messageshare.visibility = View.GONE
            binding.messagetraslateall.visibility = View.GONE
            binding.deleteBtn.visibility = View.GONE
            binding.ShareBtn.visibility = View.GONE
            binding.messagecount.visibility = View.GONE
            binding.userid.visibility = View.VISIBLE
            binding.maincanternar.visibility = View.VISIBLE
            binding.moreBtnForEditing.visibility = View.VISIBLE
            binding.newSearch.visibility = View.VISIBLE
            if (isgroupmessage) {
                binding.colorTheme.visibility = View.GONE
            } else {
                binding.colorTheme.visibility = View.VISIBLE
            }

        }

        binding.forverdBtn.setOnClickListener {
            val selectedMessage = selectedMessageList2.firstOrNull() ?: return@setOnClickListener
            startActivity(
                Intent(this, SelectContactActivity::class.java).putExtra(
                    "message", selectedMessage.snippet
                ).putExtra("isforvard", true)
            )
            selectedMessageList2.clear()
            inMainAdapter.selectedMessageList.clear()
            inMainAdapter.notifyDataSetChanged()
            binding.forverdBtn.visibility = View.GONE
            binding.infoBtn.visibility = View.GONE
            binding.copyBtn.visibility = View.GONE
            binding.messagetraslateall.visibility = View.GONE
//            binding.addcontact.visibility = View.VISIBLE
            binding.deleteBtn.visibility = View.GONE
            binding.ShareBtn.visibility = View.GONE
            binding.messagecount.visibility = View.GONE
            binding.userid.visibility = View.VISIBLE
            binding.maincanternar.visibility = View.VISIBLE
            binding.moreBtnForEditing.visibility = View.VISIBLE
            binding.newSearch.visibility = View.VISIBLE
            if (isgroupmessage) {
                binding.colorTheme.visibility = View.GONE
            } else {
                binding.colorTheme.visibility = View.VISIBLE
            }
        }

        binding.infoBtn.setOnClickListener {
            val selectedMessage = selectedMessageList2.firstOrNull() ?: return@setOnClickListener
            if (selectedMessage.type == 1) {
                fromenumber = selectedMessage.title!!
            }
            val builder =
                MaterialAlertDialogBuilder(this).setTitle(resources.getString(R.string.Message_details))
                    .setMessage(
                        resources.getString(R.string.Type_Text_Message) + " ${
                            selectedMessage.time?.let { it1 ->
                                setNewDateYear(
                                    it1
                                )
                            }
                        },${selectedMessage.time?.let { it1 -> setNewTime(it1) }}"
                    )
            builder.create()
            builder.show()

        }

        binding.deleteBtn.setOnClickListener {
            if (isshowcasestartshowing) {
                return@setOnClickListener;
            }
            issendmessage = true
            if (packageName == Telephony.Sms.getDefaultSmsPackage(this@SendMessageActivity)) {

                showcommandialog(
                    dialogtital = resources.getString(R.string.Delete_this_message),
                    dialogmessage = resources.getString(R.string.This_is_permanent),
                    positivebutton = resources.getString(R.string.Delete),
                    negativebutton = resources.getString(R.string.cancel),
                    "delete"
                )
            } else {
                handleDefaultSmsClick_1(this@SendMessageActivity)
            }
        }

        binding.selectsim.setOnClickListener {
            if (!getisDualsim(this)) {
                return@setOnClickListener
            }
            DialogBuilder()
        }

        binding.backBtn.setOnClickListener {
            Constants.isActivitychange = true
            "double ads show issue <------------------> 112".log()
            onbackis()

        }

        binding.gotobuttom.setOnClickListener {
            scrollToBottom()
        }

        binding.selectContact.setOnClickListener {
            Constants.isActivitychange = true
            launchSafeContactPicker()
        }

        binding.scheduleMessage.setOnClickListener {
            firebaseEvent("SendMessageActivity", "Schedule Message")
            if (isSPlus()) {
                val alarmManager: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                if (alarmManager.canScheduleExactAlarms()) {
                    sendSuduleMessage()
                } else {
                    PermissionRequiredDialog(
                        activity = this,
                        textId = R.string.allow_alarm_scheduled_messages,
                        positiveActionCallback = {
                            openRequestExactAlarmSettings("com.messenger.phone.number.text.sms.service.apps")
                        },
                    )
                }
            } else {
                sendSuduleMessage()
            }
        }

        binding.blurebg.setOnClickListener {
            if (!isattachmentopen || !iseligibledimiss) {
                return@setOnClickListener;
            }
//            attchmentdialogshowornot(false)
            iseligibledimiss = false
        }

        binding.selectContact2Card.setOnClickListener {
            if (!isattachmentopen) {
                return@setOnClickListener;
            }
//            attchmentdialogshowornot(false)
            Constants.isActivitychange = true
            launchSafeContactPicker()

        }

        binding.scheduleMessage2Card.setOnClickListener {
            if (!isattachmentopen) {
                return@setOnClickListener;
            }
//            attchmentdialogshowornot(false)
            if (isSPlus()) {
                val alarmManager: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                if (alarmManager.canScheduleExactAlarms()) {
                    sendSuduleMessage()
                } else {
                    PermissionRequiredDialog(
                        activity = this,
                        textId = R.string.allow_alarm_scheduled_messages,
                        positiveActionCallback = {
                            openRequestExactAlarmSettings("com.messenger.phone.number.text.sms.service.apps")
                        },
                    )
                }
            } else {
                sendSuduleMessage()
            }
        }

        binding.seduleMessageNew.setOnClickListener {
//            attchmentdialogshowornot(false)
            if (isSPlus()) {
                val alarmManager: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                if (alarmManager.canScheduleExactAlarms()) {
                    sendSuduleMessage()
                } else {
                    PermissionRequiredDialog(
                        activity = this,
                        textId = R.string.allow_alarm_scheduled_messages,
                        positiveActionCallback = {
                            openRequestExactAlarmSettings("com.messenger.phone.number.text.sms.service.apps")
                        },
                    )
                }
            } else {
                sendSuduleMessage()
            }
        }

        binding.selectMap2Card.setOnClickListener {
            if (!isattachmentopen) {
                return@setOnClickListener;
            }
//            attchmentdialogshowornot(false)
            requestLocationPermission()
        }

        binding.attachFileBtn.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener;
            }
            if (isAttachmentPickerVisible) {
                hideAttachmentPicker()
                return@setOnClickListener
            }
            val binding = AttachMediaAndContentDialogBinding.inflate(LayoutInflater.from(this))
            binding.isblacktheme = ThemeModeManager.isDarkThemeActive(this)
            val bottomSheetDialog = createChatBottomSheetDialog()
            if (!BaseSharedPreferences(this@SendMessageActivity).mIS_SUBSCRIBED!!) {
                binding.swipeConPremium.gone()
            } else {
                binding.swipeConPremium.gone()
            }
            if (isgroupmessage) {
                binding.imageAttachment.gone()
                binding.recordAudioAttachment.gone()
                binding.fileAttachment.gone()
            } else {
                binding.imageAttachment.visible()
                binding.recordAudioAttachment.visible()
                binding.fileAttachment.visible()
            }
            applyAttachmentBottomSheetMaterialTheme(binding)
            binding.imageView18.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            binding.imageAttachment.setOnClickListener {
                firebaseEvent("SendMessageActivity", "Image Attachment")
//                showFullPickerDialog()
                bottomSheetDialog.dismiss()
                if (this@SendMessageActivity.binding.bottomSheetOpen.isVisible()) {
                    hideAttachmentPicker()
                } else {
//                    combinedListAttachment.clear()
                    if (combinedListAttachment.isEmpty()) {
                        if (checkPermissions()) {
                            CoroutineScope(Dispatchers.IO).launch {
                                imageuriGalleryFirstimage = getRecentImage()
                                combinedListAttachment.clear()
                                combinedListAttachment =
                                    ArrayList(getAllImagesAndVideosSortedByRecentNew())
                                runOnUiThread {
                                    setupAttachmentPicker()
                                }
                            }
                        }
                    }
                    showAttachmentPicker()
                }
//                launchGetContentIntent(arrayOf("image/*"), PICK_PHOTO_INTENT)
            }

            binding.fileAttachment.setOnClickListener {
                firebaseEvent("SendMessageActivity", "File Attachment")
                bottomSheetDialog.dismiss()
                launchGetContentIntent(arrayOf("*/*"), PICK_DOCUMENT_INTENT)
            }

            binding.recordAudioAttachment.setOnClickListener {
                firebaseEvent("SendMessageActivity", "Record Audio Attachment")
                bottomSheetDialog.dismiss()
                launchCaptureAudioIntent()
            }

            binding.contactAttachment.setOnClickListener {
                firebaseEvent("SendMessageActivity", "Conatct Attachment")
                bottomSheetDialog.dismiss()
                launchPickContactIntent()
            }

            binding.contactCard.setOnClickListener {
                firebaseEvent("SendMessageActivity", "Conatct Attachment")
                bottomSheetDialog.dismiss()
                Constants.isActivitychange = true
                launchSafeContactPicker()

            }
            binding.scheduleMessageCard.setOnClickListener {
                firebaseEvent("SendMessageActivity", "Schedule Message")
                bottomSheetDialog.dismiss()
                if (isSPlus()) {
                    val alarmManager: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                    if (alarmManager.canScheduleExactAlarms()) {
                        sendSuduleMessage()
                    } else {
                        PermissionRequiredDialog(
                            activity = this,
                            textId = R.string.allow_alarm_scheduled_messages,
                            positiveActionCallback = {
                                openRequestExactAlarmSettings("com.messenger.phone.number.text.sms.service.apps")
                            },
                        )
                    }
                } else {
                    sendSuduleMessage()
                }
            }
            binding.locationMessageCard.setOnClickListener {
                firebaseEvent("SendMessageActivity", "Location Attachment")
                bottomSheetDialog.dismiss()
                requestLocationPermission()
            }

            bottomSheetDialog.setContentView(binding.root)
            bottomSheetDialog.show()

            bottomSheetDialog.setOnShowListener {
                applyMaterialSystemBarColors()
            }

//            Log.d("jigar", "attchmentdialogshowornot: <------------> 3")
//            mLastClickTime = SystemClock.elapsedRealtime();
//            isattachmentopen = true
//            WindowCompat.getInsetsController(window, binding.writemessageedt)
//                .hide(WindowInsetsCompat.Type.ime())
//            Handler(Looper.myLooper()!!).postDelayed({
//                attchmentdialogshowornot(true)
//            }, 1000)


        }

        binding.scheduledMessageButton.apply {
            setOnClickListener {
                launchScheduleSendDialog(scheduledDateTime)
            }
        }

        binding.discardScheduledMessage.setOnClickListener {
            hideScheduleSendUi()
        }

        binding.discardReplayMessage.setOnClickListener {
            hideReplayUi()
        }

        binding.userid.setOnClickListener {
            binding.maincanternar.performClick()
        }

        binding.maincanternar.setOnClickListener {
            if (!binding.messageSearchholder.isVisible) {
                if (SystemClock.elapsedRealtime() - lastClickTime < 700) {
                    return@setOnClickListener;
                }
                lastClickTime = SystemClock.elapsedRealtime();
                Constants.isActivitychange = true
                if (isgroupmessage) {
                    startActivity(
                        Intent(this, GroupDetailsActivity::class.java).putExtra("uesrname", name)
                            .putExtra("sendername", senderName)
                            .putExtra("mobilenumber", mobileNumber)
                            .putExtra("thredid", tredid).putExtra("isgroupmessage", isgroupmessage)
                    )
                } else {
                    startActivity(
                        Intent(this, ViewDetailsActivity::class.java).putExtra("uesrname", name)
                            .putExtra("sendername", senderName)
                            .putExtra("mobilenumber", mobileNumber)
                            .putExtra("thredid", tredid).putExtra("isgroupmessage", isgroupmessage)
                    )
                }
            }
        }
        binding.confirmInsertedNumber.setOnClickListener {
            val number = binding.addContactOrNumber.value
            val phoneNumber = PhoneNumber(number, 0, "", number)
            val contact = SimpleContact(
                number.hashCode(),
                number.hashCode(),
                number,
                "",
                arrayListOf(phoneNumber),
                ArrayList(),
                ArrayList()
            )
            addSelectedContact(contact)
        }

        binding.selectImage.setOnClickListener {
            if (checkPermissions()) {
                sendtoCustomwallpaper()
            } else {
                if (Build.VERSION.SDK_INT >= 33) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 40
                    )
                } else {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ), 40
                    )
                }
            }
        }

        binding.confirmManageContacts.setOnClickListener {
            hideKeyboard()
            binding.threadAddContacts.beGone()

            val numbers = HashSet<String>()
            participants.forEach { contact ->
                contact.phoneNumbers.forEach {
                    numbers.add(it.normalizedNumber)
                }
            }
            val newThreadId = getThreadId(numbers)
            if (tredid != newThreadId) {
                val participantsforgroupchat = getThreadParticipants(newThreadId!!, null)
                val addresses = participantsforgroupchat.getAddresses()
                val result = addresses.joinToString(separator = "|")
                hideKeyboard()
                Intent(
                    this, if (config.Message_Send_Activity == "1") {
                        SendMessageActivity::class.java
                    } else {
                        SendMessageActivity::class.java
                    }
                ).apply {
                    putExtra("tredid", newThreadId)
                    putExtra("isgroupmessage", true)
                    putExtra("name", participantsforgroupchat.getThreadTitle())
                    putExtra("mobileNumber", result)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(this)
                }
            }
        }


        iconset()
        setupAdapterContact()
        setupScrollFab()
        setupSIMSelector()
        setupKeyboardListener()
        hideAttachmentPicker()

        if (forscheduleMessage) {
            forscheduleMessage = false
//            binding.scheduleMessage.performClick()
            if (isSPlus()) {
                val alarmManager: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                if (alarmManager.canScheduleExactAlarms()) {
                    if (isScheduledMessage) {
                        launchScheduleSendDialog(scheduledDateTime)
                    } else {
                        launchScheduleSendDialog()
                    }
                } else {
                    PermissionRequiredDialog(
                        activity = this,
                        textId = R.string.allow_alarm_scheduled_messages,
                        positiveActionCallback = {
                            openRequestExactAlarmSettings("com.messenger.phone.number.text.sms.service.apps")
                        },
                    )
                }
            } else {
                if (isScheduledMessage) {
                    launchScheduleSendDialog(scheduledDateTime)
                } else {
                    launchScheduleSendDialog()
                }
            }
        }

        binding.selectMapSend.setOnClickListener {
            requestLocationPermission()
        }

        binding.moreBtnForEditing.setOnClickListener {
            if (isshowcasestartshowing) {
                return@setOnClickListener;
            }
            if (!binding.messageSearchholder.isVisible) {
                showMenuselection(it, R.menu.send_message_editing_menu)

            }
        }
        binding.newSearch.setOnClickListener {
            startandstopmessagesearch(true)
        }

        binding.colorTheme.setOnClickListener {
            if (!binding.messageSearchholder.isVisible) {
                isfromprofile = true
                startActivity(
                    Intent(this, ThemeColorActivity::class.java).putExtra(
                        "treadid", tredid
                    )
                )
            }
        }

        messageCornerDialog.doneclickofmessagecorner = { isdonebuttonclick, messagecornerold ->
            if (!isdonebuttonclick) {
                config.messagecorner = messagecornerold
            }
            inMainAdapter.notifyDataSetChanged()
        }


        customWallpaperDialog.livewallpaperchangepreview = {
                backgroundcolorcustomwallpaper,
                toolbarcolorcustomwallpaper,
                inmessagecolorcustomwallpaper,
                outmessagecolorcustomwallpaper,
                smartreplycolor,
                isdonebuttonclick,
                isdonebuttonclickwithoutresete,
            ->


            if (customWallpaperDialog.shareprefselected != 1) {


                if (isdonebuttonclick) {

                    val options = arrayOf(
                        resources.getString(R.string.For_this_chat) + " ${"$iscustomwallpaersetterdidmovilenumber"}",
                        resources.getString(R.string.For_all_chats)
                    )
                    val options2 = arrayOf("Option 1", "Option 2", "Option 3")
                    var selectedItem = 0
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setCancelable(false)
                    builder.setTitle(resources.getString(R.string.Set_theme))
                    builder.setSingleChoiceItems(options, selectedItem) { dialog, which ->
                        selectedItem = which
                    }
                    builder.setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->

                        when (selectedItem) {

                            0 -> {
                                CoroutineScope(Dispatchers.IO).launch {
                                    config.saveProfileColor(
                                        ChatScreenColor(
                                            tredid = tredid.toString(),
                                            backgroundcolorcustomwallpaper = backgroundcolorcustomwallpaper,
                                            toolbarcolorcustomwallpaper = toolbarcolorcustomwallpaper,
                                            inmessagecolorcustomwallpaper = inmessagecolorcustomwallpaper,
                                            outmessagecolorcustomwallpaper = outmessagecolorcustomwallpaper,
                                            smartreplycolor = smartreplycolor,
                                            shareprefselected = customWallpaperDialog.shareprefselected
                                        )
                                    )

                                    if (config.Message_Send_Activity == "1") {
                                        config.inmessagecolorcustomwallpaper =
                                            inmessagecolorcustomwallpaper
                                        config.outmessagecolorcustomwallpaper =
                                            outmessagecolorcustomwallpaper
                                    } else {
                                        config.inmessagecolorcustomwallpaperAB =
                                            inmessagecolorcustomwallpaper
                                        config.outmessagecolorcustomwallpaperAB =
                                            outmessagecolorcustomwallpaper
                                    }
                                    recreate()
                                }
                            }

                            1 -> {
                                CoroutineScope(Dispatchers.IO).launch {

                                    config.isAllChatColor = true
                                    config.isAllChatDefaultColor = false

                                    config.removeAllProfileColor()

                                    config.saveProfileColorAll(
                                        ChatScreenColor(
                                            tredid = tredid.toString(),
                                            backgroundcolorcustomwallpaper = backgroundcolorcustomwallpaper,
                                            toolbarcolorcustomwallpaper = toolbarcolorcustomwallpaper,
                                            inmessagecolorcustomwallpaper = inmessagecolorcustomwallpaper,
                                            outmessagecolorcustomwallpaper = outmessagecolorcustomwallpaper,
                                            smartreplycolor = smartreplycolor,
                                            shareprefselected = customWallpaperDialog.shareprefselected
                                        )
                                    )

                                    if (config.Message_Send_Activity == "1") {
                                        config.inmessagecolorcustomwallpaper =
                                            inmessagecolorcustomwallpaper
                                        config.outmessagecolorcustomwallpaper =
                                            outmessagecolorcustomwallpaper
                                    } else {
                                        config.inmessagecolorcustomwallpaperAB =
                                            inmessagecolorcustomwallpaper
                                        config.outmessagecolorcustomwallpaperAB =
                                            outmessagecolorcustomwallpaper
                                    }

                                    recreate()
                                }
                            }

                        }
                        dialog.dismiss()
                    }
                    builder.setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->

                        if (config.Message_Send_Activity == "1") {
                            config.backgroundcolorcustomwallpaper =
                                backgroundcolorcustomwallpaperold
                        } else {
                            config.backgroundcolorcustomwallpaperab =
                                backgroundcolorcustomwallpaperold

                        }


                        config.toolbarcolorcustomwallpaper = toolbarcolorcustomwallpaperold

                        if (config.Message_Send_Activity == "1") {
                            config.inmessagecolorcustomwallpaper = inmessagecolorcustomwallpaperold
                            config.outmessagecolorcustomwallpaper =
                                outmessagecolorcustomwallpaperold
                        } else {
                            config.inmessagecolorcustomwallpaperAB =
                                inmessagecolorcustomwallpaperold
                            config.outmessagecolorcustomwallpaperAB =
                                outmessagecolorcustomwallpaperold
                        }
                        config.smartreplycolor = smartreplycolorold
//                        inMainAdapter.notifyDataSetChanged()
                        config.customwallpaperselected = shareprefselectedoldold
                        setThemeColor(
                            toolbarcolorcustomwallpaperold,
                            backgroundcolorcustomwallpaperold,
                            false,
                            "",
                            "",
                            "",
                            -1
                        )
                        dialog.dismiss()
                    }
                    builder.show()

                } else {
                    if (config.Message_Send_Activity == "1") {
                        config.backgroundcolorcustomwallpaper = backgroundcolorcustomwallpaper
                    } else {
                        config.backgroundcolorcustomwallpaperab = backgroundcolorcustomwallpaper

                    }

                    config.toolbarcolorcustomwallpaper = toolbarcolorcustomwallpaper

                    if (config.Message_Send_Activity == "1") {
                        config.inmessagecolorcustomwallpaper = inmessagecolorcustomwallpaperold
                        config.outmessagecolorcustomwallpaper = outmessagecolorcustomwallpaperold
                    } else {
                        config.inmessagecolorcustomwallpaperAB = inmessagecolorcustomwallpaperold
                        config.outmessagecolorcustomwallpaperAB = outmessagecolorcustomwallpaperold
                    }

                    config.smartreplycolor = smartreplycolor
//                    inMainAdapter.notifyDataSetChanged()
                    config.customwallpaperselected = customWallpaperDialog.shareprefselected
                    setThemeColor(
                        toolbarcolorcustomwallpaper,
                        backgroundcolorcustomwallpaper,
                        true,
                        inmessagecolorcustomwallpaper,
                        outmessagecolorcustomwallpaper,
                        smartreplycolor,
                        customWallpaperDialog.shareprefselected
                    )

                }

            } else {
                customWallpaperDialog.dismiss()
                if (checkPermissions()) {
                    sendtoCustomwallpaper()
                } else {
                    if (Build.VERSION.SDK_INT >= 33) {
                        ActivityCompat.requestPermissions(
                            this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 40
                        )
                    } else {
                        ActivityCompat.requestPermissions(
                            this, arrayOf(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ), 40
                        )
                    }
                }
            }

        }

        messageCornerDialog.livechangecornerofcard = {
            inMainAdapter.notifyDataSetChanged()
        }

        messageTextSizeDialog.doneclickofmessagetextsize =
            { isdonebuttonclick, fontsizeselectionold, fontsizeold ->
                if (!isdonebuttonclick) {
                    config.fontsizeselection = fontsizeselectionold
                    config.homemessagetitlesize = fontsizeselectionold
                    config.fontsize = fontsizeold
                }
                textdialogopen = false
                inMainAdapter.notifyDataSetChanged()
            }

        messageTextSizeDialog.livechangemessagetextsize =
            { isdonebuttonclick, fontsizeselectionold, fontsizeold ->
                textdialogopen = true
                inMainAdapter.notifyDataSetChanged()
            }

        iscustomwallpaersetterdid = tredid
        iscustomwallpaersetterdidmovilenumber = name

        inMainAdapter.onMessageClick = { pos, list ->
            if (list[pos].is_scheduled) {
                if (!scheduleMessageShowDialog.isAdded) {
                    scheduleMessageShowDialog.setData(pos, list)
                    scheduleMessageShowDialog.deletebuttonshow = false
                    scheduleMessageShowDialog.show(
                        supportFragmentManager, "scheduleMessageShowAdapter"
                    )
                }
            }
        }

        inMainAdapter.onMessageFaildClick = { pos, list ->
            resendbottomsheetshow(pos, list)
        }

        scheduleMessageShowDialog.schedulemessageshowdialogclick = { dialog, pos, list ->
            datasedule = list[pos]
            when (dialog) {
                "CopyText" -> {
                    copyToClipboard(datasedule!!.snippet.replace(Regex("^\\s+|\\s+$"), ""))
                    toast(resources.getString(R.string.message_copied_to_clipboard_new))
                }

                "Delete" -> {
                    if (packageName == Telephony.Sms.getDefaultSmsPackage(this@SendMessageActivity)) {
                        showcommandialog(
                            dialogtital = resources.getString(R.string.Delete_this_message),
                            dialogmessage = resources.getString(R.string.This_is_permanent),
                            positivebutton = resources.getString(R.string.Delete),
                            negativebutton = resources.getString(R.string.cancel),
                            "delete"
                        )
                    } else {
                        handleDefaultSmsClick_1(this@SendMessageActivity)
                    }

                }

                "SendNow" -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        setMessage(list[pos], false)
                    }
                }

                "EditMessage" -> {
                    openEditDialog(pos, list)
                }
            }
            scheduleMessageShowDialog.dismiss()
        }

        binding.imageView2.setOnClickListener {
            if (isattachmentopen) {
                return@setOnClickListener;
            }
//            emojiPopup!!.toggle()
        }

        if (openautoclick) {
            if (fromnotificationautoclick != "no") {
                if (fromnotificationautoclick == "iscustomwallopaperscreenopen") {
                    config.iscustomwallopaperscreenopen = true
                    openautoclick = false
                    fromnotificationautoclick = "no"
                    if (checkPermissions()) {
                        sendtoCustomwallpaper()
                    } else {
                        if (Build.VERSION.SDK_INT >= 33) {
                            ActivityCompat.requestPermissions(
                                this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 40
                            )
                        } else {
                            ActivityCompat.requestPermissions(
                                this, arrayOf(
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ), 40
                            )
                        }
                    }
                } else if (fromnotificationautoclick == "ismessagetextsizescreenopen") {
                    config.ismessagetextsizescreenopen = true
                    openautoclick = false
                    fromnotificationautoclick = "no"
                    messageTextSizeDialog.show(supportFragmentManager, "messageTextSizeDialog")
                } else if (fromnotificationautoclick == "ismessagecornerscreenopen") {
                    config.ismessagecornerscreenopen = true
                    openautoclick = false
                    fromnotificationautoclick = "no"
                    messageCornerDialog.show(supportFragmentManager, "messageCornerDialog")
                } else if (fromnotificationautoclick == "istraslatescreenopen") {
                    config.istraslatescreenopen = true
                    openautoclick = false
                    fromnotificationautoclick = "no"
                    messageLanguageDialog?.show(supportFragmentManager, "messageLanguageDialog")
                }
            }
        }

        var currentPos = -1
        val searchItemPositionList = ArrayList<Int>()
        var searchJob: Job? = null

        binding.messageUpBtn.setOnClickListener {
            if (currentPos != -1 && currentPos < searchItemPositionList.size - 1) {
                currentPos += 1
                binding.showusermessage.smoothScrollToPosition(searchItemPositionList[currentPos])
                binding.textView34.text =
                    "${currentPos + 1}/${searchItemPositionList.size} " + resources.getString(R.string.results_found_search)
            }
        }

        binding.messageDownBtn.setOnClickListener {
            if (currentPos >= 1) {
                currentPos -= 1
                binding.showusermessage.smoothScrollToPosition(searchItemPositionList[currentPos])
                binding.textView34.text =
                    "${currentPos + 1}/${searchItemPositionList.size} " + resources.getString(R.string.results_found_search)
            }
        }

        binding.messagebutton2.setOnClickListener {
            startandstopmessagesearch(false)
        }

        binding.messageserch.onTextChangeListener { txt ->
            higlitetxt = txt

            if (txt.isEmpty()) {
                binding.serchCleasr.gone()
            } else {
                binding.serchCleasr.visible()
            }

            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                val query = txt.trim().lowercase().replace("\\s+".toRegex(), "")
                val matchedPositions = withContext(Dispatchers.Default) {
                    val result = ArrayList<Int>()
                    if (query.isNotEmpty()) {
                        demolist.forEachIndexed { index, s ->
                            if (s.snippet.trim().lowercase().replace("\\s+".toRegex(), "")
                                    .contains(query)
                            ) {
                                result.add(index)
                            }
                        }
                        result.reverse()
                    }
                    result
                }

                // Ignore stale results from an outdated text-change callback.
                if (txt != higlitetxt) return@launch

                searchItemPositionList.clear()
                searchItemPositionList.addAll(matchedPositions)
                inMainAdapter.highlighttext = txt
                inMainAdapter.notifyDataSetChanged()
                if (searchItemPositionList.isNotEmpty()) {
                    currentPos = 0
                    binding.textView34.text =
                        "1/${searchItemPositionList.size} " + resources.getString(R.string.results_found_search)
                } else {
                    currentPos = -1
                    binding.textView34.text =
                        "0/${searchItemPositionList.size} " + resources.getString(R.string.results_found_search)
                }
            }

        }

        inMainAdapter.onMessageForwardClick = { pos, list ->
            startActivity(
                Intent(this, SelectContactActivity::class.java).putExtra(
                    "message", list[pos].snippet.replace(Regex("^\\s+|\\s+$"), "")
                ).putExtra("isforvard", true)
            )
        }

        inMainAdapter.viewimage = { uri, mimetype, filename ->
            launchViewIntent(uri, mimetype, filename)
        }


    }

    private fun handleDefaultSmsClick() {

        if (config.defaultSmsDialogSuppressed) {
//            showDefaultSmsSettingsDialog()
            openDefaultSmsInstruction()
            return
        }
        sendmessagebuttondefaultset = true
        setDefaultApp()
    }

    private fun openDefaultSmsInstruction() {
        val intent = Intent(this, DefaultSmsInstructionActivity::class.java)
        startActivityForResult(intent, DEFAULT_SMS_INSTRUCTION_REQUEST)
    }

    private fun openDefaultSmsSettings() {

        val intent = try {
            Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        } catch (_: Exception) {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
        }
        try {
            gooutside = true
            startActivity(intent)
        } catch (_: Exception) {
            val fallback = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
            gooutside = true
            startActivity(fallback)
        }
    }

    private fun showDefaultSmsSettingsDialog() {
        val view = DialogMessageBinding.inflate(layoutInflater)

        val bodyTextColor = getProperTextColor()

        view.message.text = getString(R.string.default_sms_settings_message)
        view.message.setTextColor(bodyTextColor)

        val dialog = MaterialAlertDialogBuilder(this).setTitle(R.string.Set_as_Default_SMS_app)
            .setView(view.root).setPositiveButton(R.string.set_as_default_app) { _, _ ->
                openDefaultSmsSettings()
            }.setNegativeButton(R.string.cancel, null).create()

        dialog.setOnShowListener {

//            val backgroundColor = getDialogBackgroundColor()

            // ✅ Set background AFTER show → no blink
//            dialog.window?.setBackgroundDrawable(
//                ColorDrawable(backgroundColor)
//            )

            // Buttons
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getProperPrimaryColor())

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(getProperSecondaryTextColor())

            // Title
            val titleView = dialog.findViewById<View>(androidx.appcompat.R.id.alertTitle)
                ?: dialog.findViewById(com.google.android.material.R.id.alertTitle)
            (titleView as? TextView)?.setTextColor(bodyTextColor)
        }

        dialog.show()
    }

    private fun iconset() {
        if (isgroupmessage) {
            binding.contacticon.setImageDrawable(SimpleContactsHelper(this).getColoredGroupIcon(name.toString()))
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val profile = mobileNumber?.let { getNameAndPhotoFromPhoneNumber(it) }
                if (profile?.photoUri?.isNotEmpty() == true) {
                    CoroutineScope(Dispatchers.Main).launch {
                        if (profile.photoUri != null && !isDestroyed && !isFinishing) {
                            SimpleContactsHelper(this@SendMessageActivity).loadContactImage(
                                profile.photoUri, binding.contacticon, name!!, null
                            )
                        }
                    }
                } else {
                    val drawable = drawableCache[tredid.toString()]
                    if (drawable != null) {
                        CoroutineScope(Dispatchers.Main).launch {
                            binding.contacticon.setImageDrawable(drawable)
                        }

                    } else {
                        val randomDrawable =
                            RandomDrawableProvider(binding.contacticon.context).getRandomDrawable(
                                tredid!!
                            )
                        CoroutineScope(Dispatchers.Main).launch {
                            binding.contacticon.setImageDrawable(randomDrawable)
                        }
                        drawableCache[tredid.toString()] = randomDrawable
                    }
                }
            }
        }
        val profilecolordata = config.getProfileThemeColor(tredid.toString())
        if (profilecolordata == null) {
        } else {
            profilecolordata?.let {
                binding.colorTheme.iconTint = ColorStateList.valueOf(it.color)
            }
        }
    }

    private fun sendSuduleMessage() {

        if (isScheduledMessage) {
            launchScheduleSendDialog(scheduledDateTime)
        } else {
            launchScheduleSendDialog()
        }

//        if (!BaseSharedPreferences(this).mIS_SUBSCRIBED!!) {
////                !BaseSharedPreferences(this).mIS_SUBSCRIBED!!
//            if (isScheduledMessage) {
//                launchScheduleSendDialog(scheduledDateTime)
//            } else {
//                if (isshowads) {
//                    val existingFragment =
//                        supportFragmentManager.findFragmentByTag("watchAdsDialog")
//                    if (existingFragment == null) {
//                        watchAdsDialog.show(supportFragmentManager, "watchAdsDialog")
//                    }
//                } else {
//                    if (isScheduledMessage) {
//                        launchScheduleSendDialog(scheduledDateTime)
//                    } else {
//                        launchScheduleSendDialog()
//                    }
//                }
//
//            }
//
//        } else {
//            if (isScheduledMessage) {
//                launchScheduleSendDialog(scheduledDateTime)
//            } else {
//                launchScheduleSendDialog()
//            }
//        }
    }


    private fun showPaticulareditpopup(text: String) {
        val showPaticularEditDialog by lazy {
            Show_Paticular_Edit_Dialog()
        }
        showPaticularEditDialog.edittxt = text
        showPaticularEditDialog.show(supportFragmentManager, "showPaticularEditDialog")
    }

    private fun setupAdapterContact() {

        SimpleContactsHelper(this).getAvailableContacts(false) { contacts ->
            runOnUiThread {
                val adapter = AutoCompleteTextViewAdapter(this, contacts)
                binding.addContactOrNumber.setAdapter(adapter)
                binding.addContactOrNumber.imeOptions = EditorInfo.IME_ACTION_NEXT
                binding.addContactOrNumber.setOnItemClickListener { _, _, position, _ ->
                    val currContacts =
                        (binding.addContactOrNumber.adapter as AutoCompleteTextViewAdapter).resultList
                    val selectedContact = currContacts[position]
                    addSelectedContact(selectedContact)
                }

                binding.addContactOrNumber.onTextChangeListener {
                    binding.confirmInsertedNumber.beVisibleIf(it.length > 2)
                }
            }
        }

    }

    private fun addSelectedContact(contact: SimpleContact) {
        binding.addContactOrNumber.setText("")
        if (participants.map { it.rawId }.contains(contact.rawId)) {
            return
        }
        participants.add(contact)
        showSelectedContacts()
    }

    private fun setupSIMSelector() {

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val availableSIMs = subscriptionManagerCompat().activeSubscriptionInfoList ?: return
            if (availableSIMs.size > 1) {
                availableSIMs.forEachIndexed { index, subscriptionInfo ->
                    var label = subscriptionInfo.displayName?.toString() ?: ""
                    if (subscriptionInfo.number?.isNotEmpty() == true) {
                        label += " (${subscriptionInfo.number})"
                    }
                    val simCard = SIMCard(index + 1, subscriptionInfo.subscriptionId, label)
                    availableSIMCards.add(simCard)
                }
                val numbers = ArrayList<String>()
                participants.forEach { contact ->
                    contact.phoneNumbers.forEach {
                        numbers.add(it.normalizedNumber)
                    }
                }

                if (numbers.isEmpty()) {
                    return
                }

                currentSIMCardIndex = getProperSimIndex(availableSIMs, numbers)
                binding.selectsim.beVisible()
                binding.selectsim.beVisible()

                if (availableSIMCards.isNotEmpty()) {
                    binding.selectsim.setOnClickListener {
                        currentSIMCardIndex = (currentSIMCardIndex + 1) % availableSIMCards.size
                        val currentSIMCard = availableSIMCards[currentSIMCardIndex]
                        binding.simNumber = currentSIMCard.id.toString()
                        val currentSubscriptionId = currentSIMCard.subscriptionId
                        numbers.forEach {
                            config.saveUseSIMIdAtNumber(it, currentSubscriptionId)
                        }
                        toast(currentSIMCard.label)
                    }
                }

                try {
                    binding.simNumber = (availableSIMCards[currentSIMCardIndex].id).toString()
                } catch (e: Exception) {
                    showErrorToast(e)
                }

            }
        }
    }

    private fun scrollToBottom() {
        val position = inMainAdapter.list.lastIndex
        if (position >= 0) {
            binding.showusermessage.scrollToPosition(position)
        }
    }

    private fun setupScrollFab() {
        binding.showusermessage.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("SetTextI18n")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                autoscrollstart = if (isLastItemVisible(recyclerView)) {
                    true
                } else {
//                    markallmessageread()
                    false
                }

                if (autoscrollstart) {
                    newmessagecount = 0
                    binding.datetxtCon.gone()
                } else {
                    if (newmessagecount != 0) {
                        binding.datetxtCon.visible()
//                        newmessagecount
                        binding.datetxt.text =
                            "${newmessagecount} " + resources.getString(R.string.channel_new_messages)
                    }
                }

                val isCloseToBottom =
                    lastVisibleItemPosition >= inMainAdapter.list.size - SCROLL_TO_BOTTOM_FAB_LIMIT
                if (isCloseToBottom) {
                    binding.gotobuttom.hide()
                } else {
                    binding.gotobuttom.show()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                userScrolling = newState == RecyclerView.SCROLL_STATE_DRAGGING
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        if (binding.gotobuttom.visibility == View.VISIBLE) {
                            Handler(Looper.myLooper()!!).postDelayed({
                                binding.gotobuttom.hide()
                            }, 2000)
                        }
                    }

                    RecyclerView.SCROLL_STATE_DRAGGING -> println("Scrolling now")
                    RecyclerView.SCROLL_STATE_SETTLING -> println("Scroll Settling")
                }
            }
        })
    }

    private fun setDataSerch(conversations: ArrayList<Conversation>) {
        if (isSearchFound) {
            isSerchfoundmessage = true
            isSearchFoundmessage = intent.getStringExtra("isSearchFoundmessage").toString()
            val searchMessageId = intent.getLongExtra("isSearchFoundMessageId", -1L)
            val searchList = ArrayList(conversations.distinctBy { it.messageId })
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    conversationData = searchList.firstOrNull {
                        if (searchMessageId > 0L) {
                            it.messageId == searchMessageId
                        } else {
                            it.snippet.trim() == isSearchFoundmessage.trim()
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    val targetConversation = conversationData
                    if (targetConversation == null) {
                        Toast.makeText(
                            this@SendMessageActivity,
                            resources.getString(R.string.Message_Not_Found),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        return@withContext
                    }

                    inMainAdapter.list.forEachIndexed { index, conversation ->
                        CoroutineScope(Dispatchers.IO).launch {
                            if (conversation.isMessagefound) {
                                posisserchmessage = index
                            }
                        }
                    }
                    Log.d(
                        "Krupal",
                        "setDataSerch: -->${conversations.map { it.messagewithattachment }}"
                    )
                    inMainAdapter.list = searchList
                    posisserchmessage = searchList.indexOf(targetConversation)
                    if (posisserchmessage >= 0) {
                        binding.showusermessage.scrollToPosition(posisserchmessage)
                    }
                    Handler(Looper.myLooper()!!).postDelayed({
                        isSearchFound = false
                    }, 5000)
                }
            }
        }

    }

    private fun clearAllNotifications() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        "double ads show issue <------------------> 111".log()
        onbackis()
    }

    private fun onbackis() {
        exitScreen()
    }

    private fun exitScreen() {
        Alredyclick = false
        if (binding.messageSearchholder.isVisibleMess()) {
            startandstopmessagesearch(false)
        } else {
            onBackprocess()
        }
    }

    private fun showFeedbackDialog() {
        val builder = MaterialAlertDialogBuilder(this)
        val binding = FeedBackDialogLayoutBinding.inflate(layoutInflater)
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding.cancel.setOnClickListener {
            dialog.dismiss()
            "double ads show issue <------------------> 113".log()
            onbackis()
        }

        var feedback = ""
        binding.checkBox1.text = check1
        binding.checkBox2.text = check2
        binding.checkBox3.text = check3
        binding.checkBox4.text = check4

        binding.checkBox1.setOnCheckedChangeListener { _, b ->
            if (b) feedback += "${check1} \n"
            else feedback = feedback.replace("${check1} \n", "")
        }

        binding.checkBox2.setOnCheckedChangeListener { _, b ->
            if (b) feedback += "${check2} \n"
            else feedback = feedback.replace("${check2} \n", "")
        }

        binding.checkBox3.setOnCheckedChangeListener { compoundButton, b ->
            if (b) feedback += "${check3} \n"
            else feedback = feedback.replace("${check3} \n", "")
        }

        binding.checkBox4.setOnCheckedChangeListener { compoundButton, b ->
            if (b) feedback += "${check4} \n"
            else feedback = feedback.replace("${check4}, \n", "")
        }

        binding.submit.setOnClickListener {
            dialog.dismiss()
            sendGmail(
                "Message FeedBack",
                "$feedback \n ${binding.editText2.text}",
                GMAIL_ID,
                listOf(GMAIL_ID)
            )
        }
        dialog.show()
    }

    private fun sendGmail(
        subject: String,
        body: String,
        toEmail: String,
        recipientEmail: List<String> = emptyList(),
    ) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                data = Uri.parse("mailto:$toEmail")
                if (recipientEmail.isNotEmpty()) putExtra(
                    Intent.EXTRA_EMAIL, recipientEmail.toTypedArray()
                )
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                setPackage("com.google.android.gm")
            }
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val fallbackIntent = Intent(Intent.ACTION_VIEW).apply {
                data =
                    Uri.parse("mailto:$toEmail?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}")
            }
            try {
                startActivity(fallbackIntent)
            } catch (e: Exception) {

            }
        } catch (e: Exception) {
        }
    }

    private fun showinterAdsAndFinish() {
        if (config.shouldShowChatBackAd() && isFLow2) {
            config.markChatBackAdShown()
        }
        finish()
    }
    private fun onBackprocess() {
        CoroutineScope(Dispatchers.IO).launch {
            if (tredid != null) {
                messagerDatabaseRepo.removeTraslatedMessagemessageRepo(tredid!!)
            }
        }

        if (openFromAfterCallList) {
            if (selectedMessageList2.isNotEmpty()) {
                removeselection()
                Constants.isActivitychange = true
                return
            }
            startActivity(
                Intent(this, HomeABActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
            )
            finish()
            Constants.isActivitychange = true
            return
        }

        if (fromnotification) {
            if (config.Lock_Screen_Pin != "Not Set") {
                finishAffinity()
            } else {
                //                    isRatingDialogshow
//                RatingDialogshowCount
                if (selectedMessageList2.isEmpty()) {

                    if (baseConfig.isRatingDialogshow) {
                        if (baseConfig.RatingDialogshowCount % 3 == 0) {
                            showRateDialog()
                        } else {
                            baseConfig.RatingDialogshowCount++
                            finish()
                        }
                    } else {
                        finish()
                    }

//                    if (!baseConfig.isComeFirstTimeInTransaction) {
//                        baseConfig.isComeFirstTimeInTransaction = true
//                    } else {
//                        finish()
//                    }
                } else {
                    removeselection()
                }
            }
        } else {

            if (selectedMessageList2.isEmpty()) {


                if (baseConfig.isRatingDialogshow) {
                    if (baseConfig.RatingDialogshowCount % 3 == 0) {
                        showRateDialog()
                    } else {
                        baseConfig.RatingDialogshowCount++



                        showinterAdsAndFinish()

                    }
                } else {
                    if (config.shouldShowChatBackAd()) {

                        showinterAdsAndFinish()


                    } else {
                        finish()
                    }
                }


            } else {
                removeselection()
            }
        }
        Constants.isActivitychange = true
    }

    override fun onMessageSelect(
        pos: Int,
        snippet: String,
        selectedMessageList: ArrayList<Conversation>,
    ) {
        selectmessage(selectedMessageList, snippet)
    }


    fun selectmessage(selectedMessageList: ArrayList<Conversation>, snippet: String) {

        val hasFilledSnippet = selectedMessageList.any { it.snippet.isNotEmpty() }
        if (hasFilledSnippet) {
            binding.messagetraslateall.visibility = View.VISIBLE
        } else {
            binding.messagetraslateall.visibility = View.GONE
        }
        selected.clear()
        binding.messagecount.text = "" + selectedMessageList.size
        selectedMessageList2.clear()
        selectedMessageList2.addAll(selectedMessageList)
        selected.add(snippet)
        if (selectedMessageList.isEmpty()) {
            Log.d("", "selectmessage: selectedMessageList <--------> 1")
            binding.copyBtn.visibility = View.GONE
            binding.messageshare.visibility = View.GONE
            binding.messagetraslateall.visibility = View.GONE
            binding.deleteBtn.visibility = View.GONE
            binding.messagecount.visibility = View.GONE
            binding.moreBtn.visibility = View.GONE
            binding.userid.visibility = View.VISIBLE
            binding.maincanternar.visibility = View.VISIBLE
            binding.moreBtnForEditing.visibility = View.VISIBLE
            binding.newSearch.visibility = View.VISIBLE
            if (isgroupmessage) {
                binding.colorTheme.visibility = View.GONE
            } else {
                binding.colorTheme.visibility = View.VISIBLE
            }
        } else if (selectedMessageList.size == 1) {
            Log.d("", "selectmessage: selectedMessageList <--------> 2")
            binding.moreBtn.visibility = View.VISIBLE
            binding.copyBtn.visibility = View.VISIBLE
            val hasFilledSnippet = selectedMessageList.any { it.snippet.isNotEmpty() }
            if (hasFilledSnippet) {
                binding.messageshare.visibility = View.VISIBLE
            } else {
                binding.messageshare.visibility = View.GONE
            }

            binding.deleteBtn.visibility = View.VISIBLE
            binding.messagecount.visibility = View.VISIBLE
            binding.userid.visibility = View.GONE
            binding.maincanternar.visibility = View.GONE
            binding.moreBtnForEditing.visibility = View.GONE
            binding.newSearch.visibility = View.GONE
            binding.colorTheme.visibility = View.GONE
            if (config.traslatemessageshowcaseshow) {
                config.traslatemessageshowcaseshow = false
//                showTutorialNew()
            }

        } else {
            Log.d("", "selectmessage: selectedMessageList <--------> 3")
            binding.moreBtn.visibility = View.GONE
            binding.copyBtn.visibility = View.GONE
            binding.messageshare.visibility = View.GONE
            binding.ShareBtn.visibility = View.GONE
            binding.messagecount.visibility = View.VISIBLE

            val hasFilledSnippet = selectedMessageList.any { it.snippet.isNotEmpty() }
            if (hasFilledSnippet) {
                binding.messagetraslateall.visibility = View.VISIBLE
            } else {
                binding.messagetraslateall.visibility = View.GONE
            }

//            binding.messagetraslateall.visibility = View.VISIBLE
            binding.deleteBtn.visibility = View.VISIBLE
            binding.userid.visibility = View.GONE
            binding.maincanternar.visibility = View.GONE
            binding.moreBtnForEditing.visibility = View.GONE
            binding.newSearch.visibility = View.GONE
            binding.colorTheme.visibility = View.GONE
        }
    }

    fun chackPermission(): Boolean {
        return PermissionChecker.checkSelfPermission(
            this, android.Manifest.permission.SEND_SMS
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setNewDateYear(time: Long): String? {
        try {
            val date2 = Date(time)
            val format = SimpleDateFormat("yyyy-MM-dd")
            val dateString = format.format(date2)
            val date = LocalDate.parse(dateString)
            val formatter =
                DateTimeFormatter.ofPattern("dd MMM yyyy", Locale(config.SelectedLanguage))
            return date.format(formatter)
        } catch (e: Exception) {
            return ""
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    fun setNewTime(time: Long): String {
        val dt: Date = Date(time)
        val sdf = SimpleDateFormat("hh:mm aa")
        val time1 = sdf.format(dt)
        return time1
    }

    @RequiresApi(VERSION_CODES.O)
    @SuppressLint("RestrictedApi")
    private fun showMenu(v: View, @MenuRes menuRes: Int) {


        val isDarkPopup = ThemeModeManager.isDarkThemeActive(this)
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
        val baseThemedContext = ContextThemeWrapper(this, baseThemeRes)
        val themedContext = ContextThemeWrapper(baseThemedContext, overlayRes)


        val popup = PopupMenu(themedContext, v!!)
        popup.gravity = Gravity.END

//        val wrapper = ContextThemeWrapper(this, R.style.ThemeOverlay_Message_PopupMenu)
//        val popup = PopupMenu(wrapper, v)
        popup.menuInflater.inflate(menuRes, popup.menu)
        if (popup.menu is MenuBuilder) {
            val menuBuilder = popup.menu as MenuBuilder
            // hdhdugh
            menuBuilder.setOptionalIconsVisible(true)
            for (item in menuBuilder.visibleItems) {
                val iconMarginPx = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, ICON_MARGIN.toFloat(), resources.displayMetrics
                ).toInt()
                if (item.icon != null) {
                    item.icon = InsetDrawable(item.icon, iconMarginPx, 0, iconMarginPx, 0)
                }
            }
        }
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {


                R.id.message_details -> {

                    if (selectedMessageList2[0].type == 1) {
                        fromenumber = selectedMessageList2[0].title!!
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        selectedMessageList2[0].messageId?.let {
                            senderNumberGet = getSubscriptionIdFromMessageId(it)
                        }
                    }
                    val builder =
                        MaterialAlertDialogBuilder(this).setTitle(resources.getString(R.string.Message_details))
                            .setMessage(
                                if (selectedMessageList2[0].type == 1) {
                                    resources.getString(R.string.type_message) + resources.getString(
                                        R.string.From_to
                                    ) + " $mobileNumber\n" + resources.getString(R.string.To_to) + " $senderNumberGet\n" + resources.getString(
                                        R.string.To_to_Recive
                                    ) + " ${
                                        selectedMessageList2[0].time?.let { it1 ->
                                            setNewDateYear(
                                                it1
                                            )
                                        }
                                    },${selectedMessageList2[0].time?.let { it1 -> setNewTime(it1) }}"
                                } else {
                                    resources.getString(R.string.type_message) + resources.getString(
                                        R.string.From_to
                                    ) + " $senderNumberGet\n" + resources.getString(R.string.To_to) + " $mobileNumber\n" + resources.getString(
                                        R.string.To_to_Send
                                    ) + " ${
                                        selectedMessageList2[0].time?.let { it1 ->
                                            setNewDateYear(
                                                it1
                                            )
                                        }
                                    },${selectedMessageList2[0].time?.let { it1 -> setNewTime(it1) }}"
                                }
                            )


                    builder.create()
                    builder.show()

                }

                R.id.message_forward -> {
                    startActivity(
                        Intent(this, SelectContactActivity::class.java).putExtra(
                            "message",
                            selectedMessageList2[0].snippet.replace(Regex("^\\s+|\\s+$"), "")
                        ).putExtra("isforvard", true)
                    )
                    selectedMessageList2.clear()
                    inMainAdapter.selectedMessageList.clear()
                    inMainAdapter.notifyDataSetChanged()
                    binding.moreBtn.visibility = View.GONE
                    binding.copyBtn.visibility = View.GONE
                    binding.messageshare.visibility = View.GONE
                    binding.messagetraslateall.visibility = View.GONE
                    binding.deleteBtn.visibility = View.GONE
                    binding.messagecount.visibility = View.GONE
                    binding.userid.visibility = View.VISIBLE
                    binding.maincanternar.visibility = View.VISIBLE
                    binding.moreBtnForEditing.visibility = View.VISIBLE
                    binding.newSearch.visibility = View.VISIBLE
                    if (isgroupmessage) {
                        binding.colorTheme.visibility = View.GONE
                    } else {
                        binding.colorTheme.visibility = View.VISIBLE
                    }
                }

                R.id.message_share -> {
                    Constants.isActivitychange = true

                    val intent = Intent(Intent.ACTION_SEND)
                    val shareBody = selectedMessageList2[0].snippet
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                    intent.putExtra(Intent.EXTRA_TEXT, shareBody)
                    startActivity(Intent.createChooser(intent, getString(R.string.app_name)))
                    selectedMessageList2.clear()
                    inMainAdapter.selectedMessageList.clear()
                    inMainAdapter.notifyDataSetChanged()
                    binding.moreBtn.visibility = View.GONE
                    binding.copyBtn.visibility = View.GONE
                    binding.messageshare.visibility = View.GONE
//                    binding.translateBtn.visibility = View.GONE
                    binding.messagetraslateall.visibility = View.GONE
//                    binding.addcontact.visibility = View.VISIBLE
                    binding.deleteBtn.visibility = View.GONE
                    binding.messagecount.visibility = View.GONE

                    binding.userid.visibility = View.VISIBLE
                    binding.maincanternar.visibility = View.VISIBLE
                    binding.moreBtnForEditing.visibility = View.VISIBLE
                    binding.newSearch.visibility = View.VISIBLE
                    if (isgroupmessage) {
                        binding.colorTheme.visibility = View.GONE
                    } else {
                        binding.colorTheme.visibility = View.VISIBLE
                    }
                }
            }
            true
        }
        val fontRes = R.font.lato_semibold
        for (i in 0 until popup.menu.size()) {
            val item = popup.menu.getItem(i)
            applyFontToMenuItem(this, item, fontRes)
        }
        popup.show()
    }


    private fun isThirdPartyIntent(): Boolean {
        if ((intent.action == Intent.ACTION_SENDTO || intent.action == Intent.ACTION_SEND || intent.action == Intent.ACTION_VIEW) && intent.dataString != null) {
            val number =
                intent.dataString!!.removePrefix("sms:").removePrefix("smsto:").removePrefix("mms")
                    .removePrefix("mmsto:").replace("+", "%2b").trim()
            return true
        }
        return false
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DEFAULT_SMS_INSTRUCTION_REQUEST) {

            return
        }

        if (requestCode === PICK_CONTACT_REQUEST && resultCode === RESULT_OK) {
            val contactUri: Uri? = data?.data
            if (contactUri != null) {
                val (name, phoneNumber) = getContactNameAndNumber(contactUri)
                val nameOrNumber = when {
                    !name.isNullOrBlank() && !phoneNumber.isNullOrBlank() -> "${name}\n${phoneNumber}"
                    !phoneNumber.isNullOrBlank() -> phoneNumber
                    else -> name
                }
                if (!nameOrNumber.isNullOrBlank()) {
                    binding.writemessageedt.setText(nameOrNumber)
                }
            }
        } else if (requestCode == CAPTURE_PHOTO_INTENT && capturedImageUri != null) {
            addAttachment(capturedImageUri!!)
        } else if (data != null) {
            when (requestCode) {
                CAPTURE_VIDEO_INTENT,
                PICK_DOCUMENT_INTENT,
                CAPTURE_AUDIO_INTENT,
                PICK_PHOTO_INTENT,
                PICK_VIDEO_INTENT,
                PICK_VIDEO_AND_IMAGE_INTENT,
                CUSTOME_CAMERA_REQUEST_CODE,
                    -> addAttachment(
                    data.data
                )

                PICK_CONTACT_INTENT -> data.data?.let { addContactAttachment(it) }
            }
        }
        if (getAttachmentSelections().isNotEmpty() || binding.writemessageedt.text?.isNotEmpty() == true) {
            binding.sendbutton.alpha = 1.0f
        } else {
            binding.sendbutton.alpha = 0.25f
        }
    }

    private fun buildMessageAttachments(messageId: Long = -1L) = getAttachmentSelections().map {
        Attachment(
            null, messageId, it.uri.toString(), it.mimetype, 0, 0, it.filename
        )
    }.toArrayList()

    private fun getAttachmentSelections() = getAttachmentsAdapter()?.attachments ?: emptyList()

    private fun addContactAttachment(contactUri: Uri) {

    }

    private fun addAttachment(uri2: Uri?) {
        var uri: Uri? = null
        Log.d("uri", "addAttachment:<--------->1 ${uri2}")
        if (uri2?.getScheme() != null && uri2.getScheme().equals("content")) {
            uri = uri2
        } else {
            val contentUri = getContentUriFromFilePath(contentResolver, uri2.toString())
            uri = contentUri
        }
        Log.d("uri", "addAttachment:<---------> ${uri}")
        val id = uri.toString()
        if (getAttachmentSelections().any { it.id == id }) {
            toast(R.string.duplicate_item_warning)
            return
        }

        val mimeType = uri?.let { contentResolver.getType(it) }
        if (mimeType == null) {
            toast(com.simplemobiletools.commons.R.string.unknown_error_occurred)
            return
        }
        val isImage = mimeType.isImageMimeType()
        val isGif = mimeType.isGifMimeType()
        if (isGif || !isImage) {
            // is it assumed that images will always be compressed below the max MMS size limit
            val fileSize = uri?.let { getFileSizeFromUri(it) }
            val mmsFileSizeLimit = config.mmsFileSizeLimit
            if (fileSize != null) {
                if (mmsFileSizeLimit != FILE_SIZE_NONE && fileSize > mmsFileSizeLimit) {
                    toast(R.string.attachment_sized_exceeds_max_limit, length = Toast.LENGTH_LONG)
                    return
                }
            }
        }

        var adapter = getAttachmentsAdapter()
        if (adapter == null) {
            adapter = AttachmentsAdapter(
                activity = this,
                recyclerView = binding.threadAttachmentsRecyclerview,
                onAttachmentsRemoved = {
                    binding.threadAttachmentsRecyclerview.beGone()
                },
                onReady = {

                },
                onremove = {
                    if (getAttachmentSelections().isNotEmpty()) {
                        binding.sendbutton.alpha = 1.0f
                    } else {
                        binding.sendbutton.alpha = 0.25f
                    }
                })
            binding.threadAttachmentsRecyclerview.adapter = adapter
        }

        binding.threadAttachmentsRecyclerview.beVisible()
        val attachment = uri?.let {
            AttachmentSelection(
                id = id,
                uri = it,
                mimetype = mimeType,
                filename = getFilenameFromUri(uri),
                isPending = isImage && !isGif
            )
        }
        if (attachment != null) {
            adapter.addAttachment(attachment)
        }
    }

    @SuppressLint("Range")
    private fun retrievePhoneNumber(cursor: Cursor): String? {
        val contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
        return if (contactId.isNullOrBlank()) null else retrievePhoneNumberById(contactId)
    }

    @SuppressLint("Range")
    private fun retrievePhoneNumberById(contactId: String): String? {
        var phoneNumber: String? = null
        safeQuery(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
            arrayOf(contactId),
            null
        )?.use { phoneCursor ->
            if (phoneCursor.moveToFirst()) {
                phoneNumber =
                    phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            }
        }
        return phoneNumber
    }

    @SuppressLint("Range")
    private fun getContactNameAndNumber(contactUri: Uri): Pair<String?, String?> {
        var name: String? = null
        var phoneNumber: String? = null

        safeQuery(
            contactUri, arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
            ), null, null, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                phoneNumber =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                if (phoneNumber.isNullOrBlank()) {
                    val contactId =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                    if (!contactId.isNullOrBlank()) {
                        phoneNumber = retrievePhoneNumberById(contactId)
                    }
                }
            }
        }

        if (!name.isNullOrBlank() || !phoneNumber.isNullOrBlank()) {
            return name to phoneNumber
        }

        val resolvedContactUri = resolveContactUri(contactUri) ?: contactUri
        safeQuery(
            resolvedContactUri,
            arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME),
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val contactId =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                if (!contactId.isNullOrBlank()) {
                    phoneNumber = retrievePhoneNumberById(contactId)
                }
            }
        }

        return name to phoneNumber
    }

    private fun resolveContactUri(contactUri: Uri): Uri? {
        return try {
            ContactsContract.Contacts.lookupContact(contentResolver, contactUri)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    private fun safeQuery(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        return try {
            contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
        } catch (e: IllegalArgumentException) {
            null
        }
    }


//    private fun launchScheduleSendDialog(originalDateTime: DateTime? = null) {
//        ScheduleMessageDialog(this@SendMessageActivity, originalDateTime) { newDateTime ->
//            if (newDateTime != null) {
//                scheduledDateTime = newDateTime
//                showScheduleMessageDialog()
//            }
//        }
//    }
private fun hasCoreSmsPermissions(): Boolean {
    val permissions = listOf(
        Manifest.permission.READ_SMS,
        Manifest.permission.SEND_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_PHONE_STATE
    )
    return permissions.all { perm ->
        val s =
            ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED
        s
    }
}
    private fun launchScheduleSendDialog(originalDateTime: DateTime? = null) {
        if(roleDefaultCheck()||hasCoreSmsPermissions()||hasSim()){
        runOnUiThread {
            ScheduleMessageDialog(this@SendMessageActivity, originalDateTime) { newDateTime ->
                if (newDateTime != null) {
                    scheduledDateTime = newDateTime


                    showScheduleMessageDialog()
                }
            }
        }
        }
    }

    private fun showScheduleMessageDialog() {


        isScheduledMessage = true
        binding.scheduledMessageHolder.beVisible()
        applyMaterialComposerBackground(mergeTopCorners = true)

        val dateTime = scheduledDateTime
        val millis = dateTime.millis
        binding.scheduledMessageButton.text =
            if (dateTime.yearOfCentury().get() > DateTime.now().yearOfCentury().get()) {
                millis.formatDate(this)
            } else {
                val flags =
                    DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_NO_YEAR
                DateUtils.formatDateTime(this, millis, flags)
            }
    }

    private fun hideScheduleSendUi() {
        isScheduledMessage = false
        binding.scheduledMessageHolder.beGone()
        applyMaterialComposerBackground(mergeTopCorners = isReplayMessage)
    }

    private fun hideReplayUi() {
        isReplayMessage = false
        binding.SwipToReplyHolder.beGone()
        ReplayMessage = null
        applyMaterialComposerBackground(mergeTopCorners = isScheduledMessage)
    }

    suspend fun senddelayMessage(
        text: String,
        listOf: List<String>,
        subscriptionId: Int,
        attachments: List<Attachment>,
    ) {


        try {
            val mess = findOtpInString(text)
            if (mess.isEmpty()) {
                messagetype = "normalmessage"
            } else {
                messagetype = "otp"
                messageotp = mess
            }
        } catch (_: Exception) {

        }

        try {

            val list = messagerDatabaseRepo.getUserMessageListrepo(tredid!!)
            val isarchiv = if (list.isEmpty()) {
                false
            } else {
                list[0].isarchived
            }

            val isPrivateChat = if (list.isEmpty()) {
                false
            } else {
                list[0].isPrivateChat
            }

            isUserNotificationShow = if (messagerDatabaseRepo.isNewUserMessageExitsRepo(tredid!!)) {
                messagerDatabaseRepo.isUserNotificationshowRepo(tredid!!)
            } else {
                true
            }

            val messageId = scheduledMessage?.id?.toLong() ?: generateRandomId()
            CoroutineScope(Dispatchers.IO).launch {
                conversation = mobileNumber?.let {
                    Conversation(
                        0,
                        (System.currentTimeMillis() + 10000L).toString(),
                        false,
                        fetchContactIdFromPhoneNumber(mobileNumber, this@SendMessageActivity)!!,
                        null,
                        false,
                        it,
                        text,
                        (System.currentTimeMillis() + 10000L),
                        2,
                        it.isDigitsOnly(),
                        "delaytime",
                        messageId = messageId,
                        threadId = tredid,
                        is_scheduled = true,
                        messagetype = messagetype,
                        messageotp = messageotp,
                        isarchived = isarchiv,
                        isPrivateChat = isPrivateChat,
                        shownotification = isUserNotificationShow
                    )
                }!!
                messagerDatabaseRepo.insertmessage(conversation)

                scheduleMessage(conversation)
                runOnUiThread {
                    hideScheduleSendUi()
                    scheduledMessage = null
                }
            }
        } catch (e: Exception) {
            showErrorToast(e.localizedMessage ?: getString(R.string.unknown_error_occurred))
        }

    }

    @SuppressLint("MissingPermission")
    private fun getProperSimIndex(
        availableSIMs: MutableList<SubscriptionInfo>,
        numbers: List<String>,
    ): Int {
        val userPreferredSimId = config.getUseSIMIdAtNumber(numbers.first())
        val userPreferredSimIdx =
            availableSIMs.indexOfFirstOrNull { it.subscriptionId == userPreferredSimId }

        messages = getMessages(tredid!!, false)
        val lastMessage = messages.lastOrNull()
        val senderPreferredSimIdx = if (lastMessage?.type == 1) {
            availableSIMs.indexOfFirstOrNull { it.subscriptionId == lastMessage.subscriptionId }
        } else {
            null
        }

        val defaultSmsSubscriptionId = SmsManager.getDefaultSmsSubscriptionId()
        val systemPreferredSimIdx = if (defaultSmsSubscriptionId >= 0) {
            availableSIMs.indexOfFirstOrNull { it.subscriptionId == defaultSmsSubscriptionId }
        } else {
            null
        }

        return userPreferredSimIdx ?: senderPreferredSimIdx ?: systemPreferredSimIdx ?: 0
    }

    private fun managePeople() {
        if (binding.threadAddContacts.isVisibleMess()) {
            hideKeyboard()
            binding.threadAddContacts.beGone()
        } else {
            showSelectedContacts()
            binding.threadAddContacts.beVisible()
            binding.addContactOrNumber.requestFocus()
            showKeyboard(binding.addContactOrNumber)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables", "InflateParams")
    private fun showSelectedContacts() {
        val properPrimaryColor = resources.getColor(R.color.appcolor, theme)
        val views = ArrayList<View>()
        participants.forEach { contact ->
            val view = layoutInflater.inflate(R.layout.item_selected_contact, null)
            val selected_contact_holder =
                view.findViewById<RelativeLayout>(R.id.selected_contact_holder)
            val selected_contact_name =
                view.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.selected_contact_name)
            val selected_contact_remove = view.findViewById<ImageView>(R.id.selected_contact_remove)
            val selectedContactBg =
                resources.getDrawable(R.drawable.item_selected_contact_background)
            (selectedContactBg as LayerDrawable).findDrawableByLayerId(R.id.selected_contact_bg)
                .applyColorFilter(properPrimaryColor)
            selected_contact_holder.background = selectedContactBg

            selected_contact_name.text = contact.name
            selected_contact_name.setTextColor(properPrimaryColor.getContrastColor())
            selected_contact_remove.applyColorFilter(properPrimaryColor.getContrastColor())

            selected_contact_remove.setOnClickListener {
                if (contact.rawId != participants.first().rawId) {
                    removeSelectedContact(contact.rawId)
                }
            }
            views.add(view)

        }
        showSelectedContact(views)
    }

    private fun removeSelectedContact(id: Int) {
        participants =
            participants.filter { it.rawId != id }.toMutableList() as ArrayList<SimpleContact>
        showSelectedContacts()
    }

    private fun showSelectedContact(views: ArrayList<View>) {
        binding.selectedContacts.removeAllViews()
        var newLinearLayout = LinearLayout(this)
        newLinearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        newLinearLayout.orientation = LinearLayout.HORIZONTAL

        val sideMargin =
            (binding.selectedContacts.layoutParams as RelativeLayout.LayoutParams).leftMargin
        val mediumMargin = resources.getDimension(R.dimen.medium_margin).toInt()
        val parentWidth = realScreenSize.x - sideMargin * 2
        val firstRowWidth =
            parentWidth - resources.getDimension(R.dimen.normal_icon_size).toInt() + sideMargin / 2
        var widthSoFar = 0
        var isFirstRow = true

        for (i in views.indices) {
            val layout = LinearLayout(this)
            layout.orientation = LinearLayout.HORIZONTAL
            layout.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
            layout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            views[i].measure(0, 0)

            var params = LinearLayout.LayoutParams(
                views[i].measuredWidth, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, mediumMargin, 0)
            layout.addView(views[i], params)
            layout.measure(0, 0)
            widthSoFar += views[i].measuredWidth + mediumMargin

            val checkWidth = if (isFirstRow) firstRowWidth else parentWidth
            if (widthSoFar >= checkWidth) {
                isFirstRow = false
                binding.selectedContacts.addView(newLinearLayout)
                newLinearLayout = LinearLayout(this)
                newLinearLayout.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                newLinearLayout.orientation = LinearLayout.HORIZONTAL
                params = LinearLayout.LayoutParams(layout.measuredWidth, layout.measuredHeight)
                params.topMargin = mediumMargin
                newLinearLayout.addView(layout, params)
                widthSoFar = layout.measuredWidth
            } else {
                if (!isFirstRow) {
                    (layout.layoutParams as LinearLayout.LayoutParams).topMargin = mediumMargin
                }
                newLinearLayout.addView(layout)
            }
        }
        binding.selectedContacts.addView(newLinearLayout)
    }

    private fun showOrHideAttachmentPicker() {
        val type = WindowInsetsCompat.Type.ime()
        val insets = ViewCompat.getRootWindowInsets(window.decorView) ?: return
        val isKeyboardVisible = insets.isVisible(type)

        if (isKeyboardVisible) {

            val keyboardHeight = insets.getInsets(type).bottom
            val bottomBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

            // check keyboard height just to be sure, 150 seems like a good middle ground between ime and navigation bar
            config.keyboardHeight = if (keyboardHeight > 150) {
                keyboardHeight - bottomBarHeight
            } else {
                getDefaultKeyboardHeight()
            }

//            hideAttachmentPicker()
        } else if (isAttachmentPickerVisible) {
//            showAttachmentPicker()
        }
    }

    private fun hideAttachmentPicker() {
        isAttachmentPickerVisible = false
        binding.bottomSheetOpen.beGone()
        animateAttachmentButton(rotation = 0f)
    }

    private fun animateAttachmentButton(rotation: Float) {
        if (isAttachmentPickerVisible) {
            binding.attachFileBtn.setImageResource(R.drawable.ic_m3_close_24)
        } else {
            binding.attachFileBtn.setImageResource(R.drawable.baseline_attach_file_24)
        }
        val onSurfaceVariantColor = resolveChatOnSurfaceVariantColor()
        binding.attachFileBtn.imageTintList = ColorStateList.valueOf(onSurfaceVariantColor)
    }

    private fun showAttachmentPicker() {
        isAttachmentPickerVisible = true
        binding.bottomSheetOpen.showWithAnimation()
        animateAttachmentButton(rotation = 135f)
        if (combinedListAttachment.isEmpty()) {
            binding.imageattachmentpickerprogress.visible()
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    if (checkPermissions()) {
                        imageuriGalleryFirstimage = getRecentImage()
                        combinedListAttachment.clear()
                        combinedListAttachment = ArrayList(getAllImagesAndVideosSortedByRecentNew())
                    }
                }
                withContext(Dispatchers.Main) {
                    binding.imageattachmentpickerprogress.gone()
                    setupAttachmentPicker()
                }
            }
        } else {
            binding.imageattachmentpickerprogress.gone()
        }
    }


    private fun setupKeyboardListener() {
        window.decorView.setOnApplyWindowInsetsListener { view, insets ->
            showOrHideAttachmentPicker()
            view.onApplyWindowInsets(insets)
        }

        val callback =
            object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE) {
                override fun onPrepare(animation: WindowInsetsAnimationCompat) {
                    super.onPrepare(animation)
                    showOrHideAttachmentPicker()
                }

                override fun onProgress(
                    insets: WindowInsetsCompat,
                    runningAnimations: MutableList<WindowInsetsAnimationCompat>,
                ) = insets
            }
        ViewCompat.setWindowInsetsAnimationCallback(window.decorView, callback)
    }

    /*private fun adsLoadAndShow() {
        if (BaseSharedPreferences(this).mIS_SUBSCRIBED) {
            binding.constnativeBannerAdView.gone()
        }
        if (config.All_Ads_On && isInternetAvailable() && !BaseSharedPreferences(this).mIS_SUBSCRIBED) {
            binding.constnativeBannerAdView.visible()

            NativeCache.peekAnyCachedEntry(excludeKey = "Chat")?.let { cached ->
                Log.d(
                    "AdsGenNew__",
                    "Chat cached key=${cached.key} slot=${cached.slotLabel} adUnitId=${cached.adUnitId}"
                )


            }

            if (NativeCache.showCachedIfAny(
                    shimmer = binding.nativeBannerAdShimmerBottom,
                    container = binding.framenativeBannerAdBottom,
                    activity = this,
                    displayKey = "Chat",
                    excludeKey = "fff",
                    useCachedKeyForDisplay = true,
                    useLargeLayout = false

                )
            ) {

                return
            } else {
                NativeCache.checkAndShowNativeSmall(
                    scope = lifecycleScope,
                    shimmer = binding.nativeBannerAdShimmerBottom,
                    container = binding.framenativeBannerAdBottom,
                    activity = this,
                    key = "Chat",
                    adUnitIds = listOfChatNative,*//*    bannerFallbackAdUnitId = chat_small_banner_1,*//*
                    onNoAdAvailable = { binding.constnativeBannerAdView.visibility = View.GONE })


            }

            // use new class
            *//*     loadShowNativeAdWithMultiID(
                     ArrayList(listOfChatNative),
                     adFrame = binding.framenativeBannerAdBottom,
                     nativeLayout = R.layout.native_banner_ad_bb,
                     isAdLoading = {
                         if (it) {
                             binding.nativeBannerAdShimmerBottom.visible()
                         } else {
                             binding.nativeBannerAdShimmerBottom.gone()
                         }
                     },
                     onShow = {
                         binding.nativeBannerAdShimmerBottom.gone()
                         binding.framenativeBannerAdBottom.visible()
                     },
                     onFail = {
                         binding.constnativeBannerAdView.gone()
                     })*//*

        } else {
            binding.constnativeBannerAdView.gone()
            binding.nativeBannerAdShimmerBottom.gone()
            binding.framenativeBannerAdBottom.gone()
        }

    }*/


    override fun onStart() {
        super.onStart()
        applyMaterialSystemBarColors()
        registerSmsContentObserver()


        if (SystemGeneratedIconSwitchAb) {
            SystemGeneratedIconSwitchAb = false
            drawableCache.clear()
        }
        iconset()
        if (config.issubbannershow && Constants.packagerenlist.isNotEmpty() &&
            BaseSharedPreferences(this@SendMessageActivity).mIS_SUBSCRIBED == false &&
            monthdiscount != ""
        ) {
            binding.monthPriceDiscount.text = monthdiscount
            binding.txt1.isSelected = true
            binding.txt2.isSelected = true
            val animShake = AnimationUtils.loadAnimation(this, R.anim.shakenew);
            binding.monthPriceDiscountStatic.isSelected = true
            binding.subPoster.startAnimation(animShake)
            binding.subPoster.visible()
        } else {
            binding.monthPriceDiscountStatic.isSelected = true
            binding.subPoster.gone()
        }
        if (setWallpaperdone) {
            setWallpaperdone = false
            recreate()
        }

        setThemeColor(
            config.toolbarcolorcustomwallpaper!!, if (config.Message_Send_Activity == "1") {
                config.backgroundcolorcustomwallpaper!!
            } else {
                config.backgroundcolorcustomwallpaperab!!

            }, false, "", "", "", -1
        )
        ThemeSetup()
        if (BaseSharedPreferences(this).mIS_SUBSCRIBED!!) {
            binding.messagePremium.gone()
            binding.constnativeBannerAdView.gone()
        } else {
            binding.messagePremium.visible()
        }
        if (mainactivityfinish) {
            mainactivityfinish = false
            finishAffinity()
        }
        chackdefault()
        if (issendmessasgescreensearchstatrt) {
            issendmessasgescreensearchstatrt = false
            startandstopmessagesearch(true)
        }
        if (this@SendMessageActivity.binding.bottomSheetOpen.isVisible()) {
            try {
                viewAllImageAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
            }
        }

    }

    private fun chackdefault() {
        when {
            packageName != Telephony.Sms.getDefaultSmsPackage(this) -> {
                try {
//                    finish()
                } catch (e: Exception) {
                }
            }

            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_DENIED -> {

            }
        }
    }


    override fun onPause() {
        super.onPause()
        CoroutineScope(Dispatchers.IO).launch {
            if (binding.writemessageedt.value != "") {
                tredid?.let {
                    saveSmsDraft(binding.writemessageedt.value, it)
                }
            } else {
                tredid?.let { deleteSmsDraft(it) }
            }
        }
    }


    override fun onResume() {
        super.onResume()

        consentRequested = false
        requestConsentIfActive()

        CoroutineScope(Dispatchers.IO).launch {

            val smsDraft = tredid?.let { getSmsDraft(it) }
            if (smsDraft != null) {
                if (!isforvard) {
                    runOnUiThread {
                        binding.writemessageedt.setText(smsDraft)
                        try {
                            binding.writemessageedt.text?.length?.let {
                                binding.writemessageedt.setSelection(it)
                            }
                        } catch (e: Exception) {
                        }
                    }
                }
            }


            val list = tredid?.let { messagerDatabaseRepo.getUserMessageListrepo(it) }
            val nameNew = if (list?.isEmpty() == true) {
                if (isgroupmessage) {
                    if (groupnamecustom != null) {
                        groupnamecustom
                    } else {
                        name
                    }
                } else {
                    if (PermissionChecker.checkSelfPermission(
                            this@SendMessageActivity, Manifest.permission.READ_CONTACTS
                        ) == PermissionChecker.PERMISSION_GRANTED
                    ) {
                        name?.let { getNameAndPhotoFromPhoneNumber(it) }?.name
                    } else {
                        name
                    }
                }

            } else {

                if (isgroupmessage) {
                    if (groupnamecustom != null) {
                        groupnamecustom
                    } else {
                        list!![0].groupName
                    }
                } else {
                    if (list!![0].title.isEmpty()) {
                        if (PermissionChecker.checkSelfPermission(
                                this@SendMessageActivity, Manifest.permission.READ_CONTACTS
                            ) == PermissionChecker.PERMISSION_GRANTED
                        ) {
                            name?.let { getNameAndPhotoFromPhoneNumber(it) }?.name
                        } else {
                            name
                        }
                    } else {
                        list!![0].title
                    }
                }

            }
            runOnUiThread {
//                binding.userid.text = nameNew

                val resolvedName = nameNew?.takeIf { it.isNotBlank() } ?: mobileNumber ?: name
                binding.userid.text = resolvedName
                name = resolvedName
            }

        }
    }


    private fun requestConsentIfActive() {
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            return
        }
        val decorView = window?.decorView
        if (decorView == null || !decorView.isAttachedToWindow) {
            return
        }
        if (consentRequested) {
            return
        }
        consentRequested = true
        googleMobileAdsConsentManager.gatherConsent(this@SendMessageActivity) { consentError ->
            if (consentError != null) {
                Log.w(
                    "TAG", "<-----------> ${consentError.errorCode}. ${consentError.message}"
                )
            }
            if (googleMobileAdsConsentManager.canRequestAds) {

            }


            if (googleMobileAdsConsentManager.isPrivacyOptionsRequired) {
                googleMobileAdsConsentManager.showPrivacyOptionsForm(this) { formError ->
                    if (formError != null) {
                        Toast.makeText(
                            this@SendMessageActivity, formError.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onChipClick(chipText: String) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 300) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        binding.writemessageedt.setText(chipText)
        binding.sendbutton.performClick()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        inMainAdapter.avoidfisrttimecalling = true
    }

    suspend fun translatemessage(snippet: String, target: String, position: Int) {
        if (!isOnline()) {
            CoroutineScope(Dispatchers.Main).launch {
                inMainAdapter.list[position].snippet = snippet
                inMainAdapter.list[position].messagetraslationanimationshow = false
                inMainAdapter.notifyDataSetChanged()
            }
        } else {

            GlobalScope.launch(Dispatchers.Main) {
                try {
                    translateApi.translate(
                        text = snippet,
                        sourceLang = "auto",
                        targetLang = target,
                        onSuccess = { translatedText ->
                            runOnUiThread {
                                inMainAdapter.list[position].snippet = translatedText
                                inMainAdapter.list[position].messagetraslationanimationshow = false
                                inMainAdapter.notifyDataSetChanged()
                            }
                        },
                        onError = { exception ->
                            runOnUiThread {
                                try {
                                    inMainAdapter.list[position].snippet = snippet
                                    inMainAdapter.list[position].messagetraslationanimationshow =
                                        false
                                    toastMess(getString(R.string.translation_failed))
                                    inMainAdapter.notifyDataSetChanged()
                                } catch (e: Exception) {

                                }
                            }
                        })
                } catch (e: Exception) {
                    println("Coroutine error: ${e.message}")
                }
            }

//            CoroutineScope(Dispatchers.IO).launch {
//                translateTextFI(snippet, target) { txt ->
//                    Log.d("notifyDataSetChanged", "translatemessage: notifyDataSetChanged  <---> ${txt}")
//                    if (txt == "FIERROR") {
//                        CoroutineScope(Dispatchers.Main).launch {
//                            inMainAdapter.list[position].snippet = snippet
//                            inMainAdapter.list[position].messagetraslationanimationshow = false
//                            toastMess(getString(R.string.translation_failed))
//                            inMainAdapter.notifyDataSetChanged()
//
//                        }
//                    } else {
//                        CoroutineScope(Dispatchers.Main).launch {
//                            inMainAdapter.list[position].snippet = txt
//                            inMainAdapter.list[position].messagetraslationanimationshow = false
//                            inMainAdapter.notifyDataSetChanged()
//                        }
//                    }
//                }
//            }
        }


//        val languageIdentifier = LanguageIdentification.getClient()
//        languageIdentifier.identifyLanguage(snippet).addOnSuccessListener { languageCode ->
//            if (languageCode != "und") { // "und" means undeterminedˇ©
//                if (!isOnline()) {
//                    inMainAdapter.list[position].snippet = snippet
//                    inMainAdapter.notifyDataSetChanged()
//                } else {
//                    CoroutineScope(Dispatchers.IO).launch { setlanguage(target, languageCode, snippet, position) }
//                }
//            } else {
//                if (!isOnline()) {
//                    inMainAdapter.list[position].snippet = snippet
//                    inMainAdapter.notifyDataSetChanged()
//                } else {
//                    CoroutineScope(Dispatchers.IO).launch { setlanguage(target, "en", snippet, position) }
//                }
//            }
//        }.addOnFailureListener { e ->
//            if (!isOnline()) {
//                inMainAdapter.list[position].snippet = snippet
//                inMainAdapter.notifyDataSetChanged()
//            } else {
//                CoroutineScope(Dispatchers.IO).launch { setlanguage(target, "en", snippet, position) }
//            }
//        }


    }

    suspend fun translateTextFI(text: String, targetLanguage: String, callback: (String) -> Unit) {

//        val languageIdentifier = LanguageIdentification.getClient(
//            LanguageIdentificationOptions.Builder()
//                .build()
//        )
//
//        languageIdentifier.identifyLanguage(text)
//            .addOnSuccessListener { languageCode ->
//                val options = TranslatorOptions.Builder()
//                    .setSourceLanguage(languageCode)
//                    .setTargetLanguage(targetLanguage)
//                    .build()
//                val translator = Translation.getClient(options)
//                translator.downloadModelIfNeeded()
//                    .addOnSuccessListener {
//                        translator.translate(text)
//                            .addOnSuccessListener { translatedText ->
//                                callback(translatedText)
//                            }
//                            .addOnFailureListener { e ->
//                                Log.e("TranslateText", "Translation failed: ${e.localizedMessage}")
//                                callback("FIERROR")
//                            }
//                    }
//                    .addOnFailureListener { e ->
//                        Log.e("TranslateText", "Model download failed: ${e.localizedMessage}")
//                        callback("FIERROR")
//                    }
//            }


//        val languageIdentifier = FirebaseNaturalLanguage.getInstance().languageIdentification
//        languageIdentifier.identifyLanguage(text)
//            .addOnSuccessListener { languageCode ->
//
//                val sourceLanguageFi = FirebaseTranslateLanguage.languageForLanguageCode(languageCode)
//                val TargetLanguageFi = FirebaseTranslateLanguage.languageForLanguageCode(targetLanguage)
//
//                Log.d("notifyDataSetChanged", "translatemessage: notifyDataSetChanged  <---> 5 FIERROR sourceLanguageFi ${sourceLanguageFi} TargetLanguageFi ${TargetLanguageFi} languageCode ${languageCode}")
//
//                if (sourceLanguageFi != null && TargetLanguageFi != null) {
//                    val translatorOptionsWithTarget = FirebaseTranslatorOptions.Builder()
//                        .setSourceLanguage(sourceLanguageFi)
//                        .setTargetLanguage(TargetLanguageFi)
//                        .build()
//
//                    val translator = FirebaseNaturalLanguage.getInstance().getTranslator(translatorOptionsWithTarget)
//
//                    translator.downloadModelIfNeeded()
//                        .addOnSuccessListener {
//                            translator.translate(text)
//                                .addOnSuccessListener { translatedText ->
//                                    callback(translatedText)
//                                }
//                                .addOnFailureListener { e ->
//                                    Log.d("notifyDataSetChanged", "translatemessage: notifyDataSetChanged  <---> 3 ${e.localizedMessage}")
//                                    callback("FIERROR")
//                                }
//                        }
//                        .addOnFailureListener { e ->
//                            Log.d("notifyDataSetChanged", "translatemessage: notifyDataSetChanged  <---> 2 ${e.localizedMessage}")
//                            callback("FIERROR")
//                        }
//                } else {
//                    callback("FIERROR")
//                    Log.d("notifyDataSetChanged", "translatemessage: notifyDataSetChanged  <---> 4 FIERROR")
//                }
//
//            }.addOnFailureListener { e ->
//                Log.d("notifyDataSetChanged", "translatemessage: notifyDataSetChanged  <---> 1 ${e.localizedMessage}")
//                callback("FIERROR")
//            }

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (selectedMessageList2.isNotEmpty()) {
            if (traslatebtnclick) {
                traslatebtnclick = false
                val message = selectedMessageList2[0].snippet
                val messagedate = selectedMessageList2[0].time
                var traslatemessage = ""
                inMainAdapter.list.forEachIndexed { index, conversation ->
                    if (conversation.time == messagedate) {
                        messagetraslatepos = index
                    }
                }
                selectedMessageList2.clear()
                inMainAdapter.selectedMessageList.clear()
                binding.moreBtn.visibility = View.GONE
                binding.copyBtn.visibility = View.GONE
                binding.messageshare.visibility = View.GONE
//                binding.translateBtn.visibility = View.GONE
                binding.messagetraslateall.visibility = View.GONE
                binding.deleteBtn.visibility = View.GONE
                binding.messagecount.visibility = View.GONE
                binding.userid.visibility = View.VISIBLE
                binding.maincanternar.visibility = View.VISIBLE
                binding.moreBtnForEditing.visibility = View.VISIBLE
                binding.newSearch.visibility = View.VISIBLE
                if (isgroupmessage) {
                    binding.colorTheme.visibility = View.GONE
                } else {
                    binding.colorTheme.visibility = View.VISIBLE
                }
                inMainAdapter.list[messagetraslatepos].snippet = "Translating message"
                inMainAdapter.notifyDataSetChanged()
                CoroutineScope(Dispatchers.IO).launch {
//                    translatemessage(message, availableLanguages[p2].code, position)
                }
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }


    @SuppressLint("NotifyDataSetChanged")
    suspend fun setlanguage(
        targetLangCodeget: String,
        SourceLangCodeget: String,
        message: String,
        position: Int,
    ) {

//        delay(10)
//        var txt = ""
//        val targetLangCode = TranslateLanguage.fromLanguageTag(targetLangCodeget) ?: "en"
//        val SourceLangCode = TranslateLanguage.fromLanguageTag(SourceLangCodeget) ?: "en"
//        val translator = TranslatorOptions.Builder().setSourceLanguage(SourceLangCode) // Source language
//            .setTargetLanguage(targetLangCode) // Target language
//            .build()
//
//        val textTranslator = Translation.getClient(translator)
//
//        textTranslator.downloadModelIfNeeded().addOnSuccessListener {
//            textTranslator.translate(message).addOnSuccessListener { translatedText ->
//                txt = translatedText
//                CoroutineScope(Dispatchers.Main).launch {
//                    inMainAdapter.list[position].snippet = translatedText
//                    inMainAdapter.list[position].messagetraslationanimationshow = false
//                    inMainAdapter.notifyDataSetChanged()
//                }
//            }.addOnFailureListener { exception ->
//                CoroutineScope(Dispatchers.Main).launch {
//                    Toast.makeText(this@SendMessageActivity, getString(R.string.translation_failed), Toast.LENGTH_SHORT).show()
//                    inMainAdapter.list[position].snippet = message
//                    inMainAdapter.list[position].messagetraslationanimationshow = false
//                    inMainAdapter.notifyDataSetChanged()
//                }
//            }
//        }.addOnFailureListener { exception ->
//            CoroutineScope(Dispatchers.Main).launch {
//                Toast.makeText(this@SendMessageActivity, getString(R.string.model_download_failed), Toast.LENGTH_SHORT).show()
//                inMainAdapter.list[position].snippet = message
//                inMainAdapter.list[position].messagetraslationanimationshow = false
//                inMainAdapter.notifyDataSetChanged()
//            }
//        }

    }

//    @SuppressLint("NotifyDataSetChanged")
//    suspend fun setlanguagefull(targetLangCodeget: String, SourceLangCodeget: String, message: String, pos: Int) {
//        delay(10)
//        var txt = ""
//        val targetLangCode = TranslateLanguage.fromLanguageTag(targetLangCodeget) ?: "en"
//        val SourceLangCode = TranslateLanguage.fromLanguageTag(SourceLangCodeget) ?: "en"
//        val translator = TranslatorOptions.Builder().setSourceLanguage(SourceLangCode) // Source language
//            .setTargetLanguage(targetLangCode) // Target language
//            .build()
//
//        val textTranslator = Translation.getClient(translator)
//
//        textTranslator.downloadModelIfNeeded().addOnSuccessListener {
//            textTranslator.translate(message).addOnSuccessListener { translatedText ->
//                txt = translatedText
//                Log.d("", "translatemessage: 5 <------------------->")
//                CoroutineScope(Dispatchers.Main).launch {
//                    inMainAdapter.list[pos].snippet = translatedText
//                    inMainAdapter.notifyDataSetChanged()
//                }
//            }.addOnFailureListener { exception ->
//                Log.d("", "translatemessage: 6 <------------------->")
//                CoroutineScope(Dispatchers.Main).launch {
//                    Toast.makeText(this@SendMessageActivity, "Translation failed", Toast.LENGTH_SHORT).show()
//                    inMainAdapter.list[pos].snippet = message
//                    inMainAdapter.notifyDataSetChanged()
//                }
//            }
//        }.addOnFailureListener { exception ->
//            Log.d("", "translatemessage: 7 <------------------->")
//            CoroutineScope(Dispatchers.Main).launch {
//                Toast.makeText(this@SendMessageActivity, "Model download failed", Toast.LENGTH_SHORT).show()
//                inMainAdapter.list[pos].snippet = message
//                inMainAdapter.notifyDataSetChanged()
//            }
//        }
//    }


    override fun onLanguageClick(langcode: String, snippet: String, position: Int, lang: String) {


        if (config.forfirsttimeuser) {
            config.forfirsttimeuser = false
            binding.messagetraslateall.performClick()
        } else {
            traslateornetonoff = true
            if (traslatebtnclick) {
                traslatebtnclick = false
                if (snippet.isEmpty()) {
                    config.userpreferencelanguage = lang
                    config.userpreferencelanguageCode = langcode
                } else {
                    config.userpreferencelanguage = lang
                    config.userpreferencelanguageCode = langcode
                    val message = snippet
                    inMainAdapter.list[position].snippet =
                        resources.getString(R.string.translate_to) + " " + lang
                    inMainAdapter.list[position].messagetraslationanimationshow = true
                    inMainAdapter.notifyDataSetChanged()
                    CoroutineScope(Dispatchers.IO).launch {
                        translatemessage(message, langcode, position)
                    }
                }
            }
        }

//        traslateornetonoff = true
//        if (traslatebtnclick) {
//            traslatebtnclick = false
//            if (snippet.isEmpty()) {
//                config.userpreferencelanguage = lang
//                config.userpreferencelanguageCode = langcode
//            } else {
//                config.userpreferencelanguage = lang
//                config.userpreferencelanguageCode = langcode
//                val message = snippet
//                inMainAdapter.list[position].snippet =
//                    resources.getString(R.string.translate_to) + " " + lang
//                inMainAdapter.list[position].messagetraslationanimationshow = true
//                inMainAdapter.notifyDataSetChanged()
//                CoroutineScope(Dispatchers.IO).launch {
//                    translatemessage(message, langcode, position)
//                }
//            }
//        }
    }

    override fun onClickTraslatebutton(snippet: ArrayList<Conversation>, position: Int) {
        traslateornetonoff = true
        if (!isOnline()) {
            toast(resources.getString(R.string.Please_turn_on))
            return
        }



        traslatebtnclick = true
        if (config.userpreferencelanguageCode != "No" && config.userpreferencelanguage != "No") {
            if (traslatebtnclick) {
                traslatebtnclick = false
                val message = snippet[position].snippet
                inMainAdapter.list[position].snippet =
                    resources.getString(R.string.translate_to) + " " + config.userpreferencelanguage
                inMainAdapter.list[position].messagetraslationanimationshow = true
                inMainAdapter.list[position].messagetraslateshow = false
                val data =
                    inMainAdapter.selectedMessageList.filter { it.messageId == snippet[position].messageId }
                if (data.isNotEmpty()) {
                    inMainAdapter.selectedMessageList.remove(data[0])
                }
                inMainAdapter.notifyDataSetChanged()
                CoroutineScope(Dispatchers.IO).launch {
                    translatemessage(message, config.userpreferencelanguageCode, position)
                }
            }
        } else {
            messageLanguageDialog =
                MessageLanguageDialog.newInstance(snippet[position].snippet, position)
            messageLanguageDialog?.setInterface(this@SendMessageActivity)
            messageLanguageDialog?.show(supportFragmentManager, "messageLanguageDialog")
        }


        if (inMainAdapter.selectedMessageList.isEmpty()) {
//            binding.fullChatTranslateBtn.visible()
            binding.copyBtn.visibility = View.GONE
            binding.messageshare.visibility = View.GONE
//            binding.translateBtn.visibility = View.GONE
            binding.messagetraslateall.visibility = View.GONE
//            binding.addcontact.visibility = View.VISIBLE
            binding.deleteBtn.visibility = View.GONE
            binding.messagecount.visibility = View.GONE
            binding.moreBtn.visibility = View.GONE

            binding.userid.visibility = View.VISIBLE
            binding.maincanternar.visibility = View.VISIBLE
            binding.moreBtnForEditing.visibility = View.VISIBLE
            binding.newSearch.visibility = View.VISIBLE
            if (isgroupmessage) {
                binding.colorTheme.visibility = View.GONE
            } else {
                binding.colorTheme.visibility = View.VISIBLE
            }


        }

    }

    fun removeselection() {
        selectedMessageList2.clear()
        inMainAdapter.selectedMessageList.clear()
        inMainAdapter.notifyDataSetChanged()

        binding.moreBtn.visibility = View.GONE
        binding.copyBtn.visibility = View.GONE
        binding.messageshare.visibility = View.GONE
//        binding.translateBtn.visibility = View.GONE
        binding.messagetraslateall.visibility = View.GONE
//            binding.addcontact.visibility = View.VISIBLE
        binding.deleteBtn.visibility = View.GONE
        binding.messagecount.visibility = View.GONE


        binding.userid.visibility = View.VISIBLE
        binding.maincanternar.visibility = View.VISIBLE
        binding.moreBtnForEditing.visibility = View.VISIBLE
        binding.newSearch.visibility = View.VISIBLE
        if (isgroupmessage) {
            binding.colorTheme.visibility = View.GONE
        } else {
            binding.colorTheme.visibility = View.VISIBLE
        }

        newmessagecount = 0
        curruntmessagesize = 0
        newSize = 0
        spotlightTraslate?.finish()
    }

    private fun showcommandialog(
        dialogtital: String,
        dialogmessage: String,
        positivebutton: String,
        negativebutton: String,
        whatfordialog: String,
    ) {
        commanDeleteBlockDialog = CommanDeleteBlockDialog.newInstance(
            dialogtital, dialogmessage, positivebutton, negativebutton, whatfordialog
        )
        commanDeleteBlockDialog?.setInterface(this)
        commanDeleteBlockDialog?.show(supportFragmentManager, "delete")

    }

    override fun onpositive(whatfordialog: String) {
        when (whatfordialog) {
            "delete" -> {
                commanDeleteBlockDialog?.dismiss()
                val selectedMessagesSnapshot = ArrayList(selectedMessageList2)
                GlobalScope.launch {

                    withContext(Dispatchers.IO) {
                        selectedMessagesSnapshot.forEachIndexed { index, conversation ->
                            if (conversation.is_scheduled) {
                                conversation.messageId?.let { it1 ->
                                    cancelScheduleSendPendingIntent(
                                        it1
                                    )
                                }
                            }
                            conversation.messageId?.toInt()?.let { it1 ->
                                if (conversation.messagewithattachment?.attachments?.isEmpty() == true) {
                                    deleteSMS(it1, false)
                                } else {
                                    deleteSMS(it1, true)
                                }
                            }

                            messagerDatabaseRepo.insertOrUpdateRecycleBinRepo(
                                Conversationbin(
                                    date = conversation.date,
                                    draftmessage = conversation.draftmessage,
                                    messagetype = conversation.messagetype,
                                    messageotp = conversation.messageotp,
                                    groupName = conversation.groupName,
                                    CategoryName = conversation.CategoryName,
                                    customtimeuri = conversation.customtimeuri,
                                    phoneNumber = conversation.phoneNumber,
                                    snippet = conversation.snippet,
                                    title = conversation.title,
                                    photoUri = conversation.photoUri,
                                    messageStatus = conversation.messageStatus,
                                    isbanneradshow = conversation.isbanneradshow,
                                    isnewmessagescroll = conversation.isnewmessagescroll,
                                    isonlyselectedthem = conversation.isonlyselectedthem,
                                    shownotification = conversation.shownotification,
                                    messagetraslateshow = conversation.messagetraslateshow,
                                    messagetraslationanimationshow = conversation.messagetraslationanimationshow,
                                    isgroupmessage = conversation.isgroupmessage,
                                    isblocknumber = conversation.isblocknumber,
                                    isexpandmessageview = conversation.isexpandmessageview,
                                    is_scheduled = conversation.is_scheduled,
                                    isMessagefound = conversation.isMessagefound,
                                    isPrivateChat = conversation.isPrivateChat,
                                    isarchived = conversation.isarchived,
                                    ispinned = conversation.ispinned,
                                    isnewmessage = conversation.isnewmessage!!,
                                    isnumaric = conversation.isnumaric,
                                    read = conversation.read,
                                    usesCustomTitle = conversation.usesCustomTitle,
                                    messageId = conversation.messageId,
                                    threadId = conversation.threadId,
                                    time = conversation.time,
                                    pinneddate = conversation.pinneddate,
                                    type = conversation.type,
                                    newMessageCount = conversation.newMessageCount,
                                    themenumber = conversation.themenumber,
                                    messagewithattachment = conversation.messagewithattachment
                                )
                            )
                            Log.d(
                                "",
                                "onpositive:conversation.messageId <--------> 2 ${conversation.messageId}"
                            )
                            conversation.messageId?.let {
                                Log.d("", "onpositive:conversation.messageId <--------> 1 ${it}")
                                messagerDatabaseRepo.deleteMessagefrommessageidRepo(it)
                            }
                        }
                    }

                    withContext(Dispatchers.Main) {

                        deletedmessage.clear()
                        deletedmessage.addAll(selectedMessagesSnapshot)
                        selectedMessageList2.clear()
                        inMainAdapter.selectedMessageList.clear()
                        inMainAdapter.notifyDataSetChanged()
                        binding.forverdBtn.visibility = View.GONE
                        binding.infoBtn.visibility = View.GONE
                        binding.copyBtn.visibility = View.GONE
                        binding.messageshare.visibility = View.GONE
//                        binding.translateBtn.visibility = View.GONE
                        binding.messagetraslateall.visibility = View.GONE
//                            binding.addcontact.visibility = View.VISIBLE
                        binding.deleteBtn.visibility = View.GONE
                        binding.ShareBtn.visibility = View.GONE
                        binding.messagecount.visibility = View.GONE
                        binding.moreBtn.visibility = View.GONE

                        binding.userid.visibility = View.VISIBLE
                        binding.maincanternar.visibility = View.VISIBLE
                        binding.moreBtnForEditing.visibility = View.VISIBLE
                        binding.newSearch.visibility = View.VISIBLE
                        if (isgroupmessage) {
                            binding.colorTheme.visibility = View.GONE
                        } else {
                            binding.colorTheme.visibility = View.VISIBLE
                        }
                    }

                }
            }
        }
    }

    override fun onnegative(whatfordialog: String) {
        when (whatfordialog) {
            "delete" -> {
                dismissCommanDeleteBlockDialogSafely()
                selectedMessageList2.clear()
                inMainAdapter.selectedMessageList.clear()
                inMainAdapter.notifyDataSetChanged()
                binding.forverdBtn.visibility = View.GONE
                binding.infoBtn.visibility = View.GONE
                binding.copyBtn.visibility = View.GONE
                binding.messageshare.visibility = View.GONE
//                binding.translateBtn.visibility = View.GONE
                binding.messagetraslateall.visibility = View.GONE
//                    binding.addcontact.visibility = View.VISIBLE
                binding.deleteBtn.visibility = View.GONE
                binding.ShareBtn.visibility = View.GONE
                binding.messagecount.visibility = View.GONE
                binding.moreBtn.visibility = View.GONE

                binding.userid.visibility = View.VISIBLE
                binding.maincanternar.visibility = View.VISIBLE
                binding.moreBtnForEditing.visibility = View.VISIBLE
                binding.newSearch.visibility = View.VISIBLE
                if (isgroupmessage) {
                    binding.colorTheme.visibility = View.GONE
                } else {
                    binding.colorTheme.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun dismissCommanDeleteBlockDialogSafely() {
        val dialog = commanDeleteBlockDialog ?: return
        if (isFinishing || isDestroyed || supportFragmentManager.isStateSaved) {
            dialog.dismissAllowingStateLoss()
        } else {
            dialog.dismiss()
        }
    }


    private fun showcustomSnackbar(copytextnew: String) {
        CookieBar.build(this).setCustomView(R.layout.copy_text_custom_leyout)
            .setCustomViewInitializer { view ->
                val copytext =
                    view.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.copytext)
                val textcopy = view.findViewById<ConstraintLayout>(R.id.textcopy)
                val textshare = view.findViewById<ConstraintLayout>(R.id.textshare)
                copytext.text = copytextnew
                textcopy.setOnClickListener {
                    CookieBar.dismiss(this)
                    showPaticulareditpopup(copytextnew)
                }
                textshare.setOnClickListener {
                    CookieBar.dismiss(this)
                    shareText(copytextnew)
                }
            }.setAction("Close") { CookieBar.dismiss(this) }
            .setTitle(R.string.two_Step_Verification_new)
            .setMessage(R.string.two_Step_Verification_new).setEnableAutoDismiss(true)
            .setDuration(7000).setSwipeToDismiss(true).setCookiePosition(Gravity.BOTTOM).show()
    }

    private fun onMapWithStylesClicked(context: Context) {

        val locationManager =
            com.messenger.phone.number.text.sms.service.apps.helper.LocationManager(context)
        locationManager.getCurrentLocation { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                openMapActivity(latitude, longitude)
            } else {
                openMapActivity(DEMO_LATITUDE, DEMO_LATITUDE)
            }
        }

    }

    private fun openMapActivity(latitude: Double, longitude: Double) {
        val locationPickerIntent =
            LocationPickerActivity.Builder(this).withLocation(latitude, longitude)
                .withMapStyle(R.raw.map_style_retro).build()
        mapPoisActivityResultLauncher.launch(locationPickerIntent)
    }

    val mapPoisActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                onResultWithPois(result.data)
            } else {
                Log.d("RESULT WITH POIS****", "CANCELLED")
            }
        }

    private fun onResultWithPois(data: Intent?) {
        val latitude = data?.getDoubleExtra(LATITUDE, 0.0)
        val longitude = data?.getDoubleExtra(LONGITUDE, 0.0)
        val address = data?.getStringExtra(LOCATION_ADDRESS)
        val lekuPoi = data?.getParcelableExtra<LekuPoi>(LEKU_POI)
        val locationUrl = "https://www.google.com/maps?q=$latitude,$longitude"
        binding.writemessageedt.setText(locationUrl)
        binding.sendbutton.performClick()
    }


    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            ), MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun promptEnableLocation() {

        PermissionRequiredDialog(
            activity = this,
            textId = R.string.enable_them,
            positiveActionCallback = {
                startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            },
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isLocationEnabled()) {
                    onMapWithStylesClicked(this)
                } else {
                    promptEnableLocation()
                }
            } else {
                if (!shouldShowPermissionRationale()) {
                    showPermissionDeniedDialog()
                }
            }
        } else if (requestCode == 40) {
            if (grantResults.isNotEmpty()) {
                if (Build.VERSION.SDK_INT >= 33) {
                    if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                        sendtoCustomwallpaper()
                    } else {
                        val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.READ_MEDIA_IMAGES
                        )
                        if (!showRationale) {
                            showPermissionDeniedDialog()
                        }
                    }
                } else {
                    if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED && grantResults[1] == PermissionChecker.PERMISSION_GRANTED) {
                        sendtoCustomwallpaper()
                    } else {
                        val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        if (!showRationale) {
                            showPermissionDeniedDialog()
                        }

                    }
                }
            }
        }


    }


    private fun shouldShowPermissionRationale(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun showPermissionDeniedDialog() {
        PermissionRequiredDialog(
            activity = this,
            textId = R.string.access_this_feature,
            positiveActionCallback = {
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                })
            },
        )
    }

    private fun launchGetContentIntent(mimeTypes: Array<String>, requestCode: Int) {



        Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            launchActivityForResult(this, requestCode)
        }
    }

    private fun launchGetContentIntentMix(requestCode: Int) {
        val mimeTypes = arrayOf("image/*", "video/*")
        Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            launchActivityForResult(this, requestCode)
        }
    }

    private fun getAttachmentsDir(): File {
        return File(cacheDir, "attachments").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }


    private fun launchCapturePhotoIntent() {
        val imageFile = File.createTempFile("attachment_", ".jpg", getAttachmentsDir())
        capturedImageUri = getMyFileUri(imageFile)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri)
        }
        launchActivityForResult(intent, CAPTURE_PHOTO_INTENT)
    }

    private fun launchCaptureVideoIntent() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        launchActivityForResult(intent, CAPTURE_VIDEO_INTENT)
    }

    private fun launchPickContactIntent() {

        Intent(Intent.ACTION_PICK).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE
            launchActivityForResult(this, PICK_CONTACT_INTENT)
        }
    }

    private fun launchCaptureAudioIntent() {


        val recordIntent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        if (recordIntent.resolveActivity(packageManager) != null) {
            launchActivityForResult(recordIntent, CAPTURE_AUDIO_INTENT)
        } else {
            launchGetContentIntent(arrayOf("audio/*"), CAPTURE_AUDIO_INTENT)
        }
    }


    private fun launchActivityForResult(
        intent: Intent,
        requestCode: Int,
        @StringRes error: Int = com.simplemobiletools.commons.R.string.no_app_found,
    ) {
        hideKeyboard()
        try {
            startActivityForResult(intent, requestCode)
//            toastMess("jigar")
        } catch (e: ActivityNotFoundException) {
//            showErrorToast(getString(error))
            toastMess("this feature is coming soon")
        } catch (e: Exception) {
//            showErrorToast(e)
//            toastMess("jigar 3")
        }
    }

    private fun getAttachmentsAdapter(): AttachmentsAdapter? {
        val adapter = binding.threadAttachmentsRecyclerview.adapter
        return adapter as? AttachmentsAdapter
    }

    @SuppressLint("RestrictedApi")
    private fun showMenuselection(v: View?, menuRes: Int) {

        val isDarkPopup = ThemeModeManager.isDarkThemeActive(this)
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
        val baseThemedContext = ContextThemeWrapper(this, baseThemeRes)
        val themedContext = ContextThemeWrapper(baseThemedContext, overlayRes)


        val popup = PopupMenu(themedContext, v!!)
        popup.gravity = Gravity.END
//        val wrapper = ContextThemeWrapper(this, R.style.ThemeOverlay_Message_PopupMenu)
//        val popup = PopupMenu(wrapper, v)
        popup.menuInflater.inflate(menuRes, popup.menu)
        val addtocontactselecdtionbtn = popup.menu.findItem(R.id.add_contact_menu_btn)
        addtocontactselecdtionbtn.isVisible = isgroupmessage
        if (popup.menu is MenuBuilder) {
            val menuBuilder = popup.menu as MenuBuilder
            menuBuilder.setOptionalIconsVisible(true)
            for (item in menuBuilder.visibleItems) {
                val iconMarginPx = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, ICON_MARGIN.toFloat(), resources.displayMetrics
                ).toInt()
                if (item.icon != null) {
                    item.icon = InsetDrawable(item.icon, iconMarginPx, 0, iconMarginPx, 0)
                }
            }
        }
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {

                R.id.view_profile_message_search -> {
//                    startActivity(
//                        Intent(
//                            this, NewSearchActivity::class.java
//                        ).putExtra("isfromSendmessageActivity", true).putExtra("treadid", tredid)
//                    )
                }

                R.id.message_translate -> {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        true;
                    }

                    if (isshowcasestartshowing) {
                        true
                    }

                    if (!isOnline()) {
                        toast(resources.getString(R.string.Please_turn_on))
                        true
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    traslatebtnclick = true
                    if (selectedMessageList2.isEmpty()) {
                        true
                    }
//                    var pos = inMainAdapter.list.indexOf(selectedMessageList2[0])
                    messageLanguageDialog = MessageLanguageDialog.newInstance(null, 0)
                    messageLanguageDialog?.setInterface(this@SendMessageActivity)
                    messageLanguageDialog?.show(supportFragmentManager, "messageLanguageDialog")
                }

                R.id.add_contact_menu_btn -> {
                    startActivity(
                        Intent(this, SelectContactActivity::class.java).putExtra("isgropset", true)
                            .putExtra("grouptread", tredid)
                            .putExtra("mobilenumerofxiomi", mobileNumber)
                            .putExtra("nameofxiomi", name)
                    )
                }

                R.id.view_profile_menu_btn -> {
                    if (!binding.messageSearchholder.isVisible) {
                        Constants.isActivitychange = true
                        if (isgroupmessage) {
                            startActivity(
                                Intent(this, GroupDetailsActivity::class.java).putExtra(
                                    "uesrname", name
                                ).putExtra("sendername", senderName)
                                    .putExtra("mobilenumber", mobileNumber)
                                    .putExtra("thredid", tredid)
                                    .putExtra("isgroupmessage", isgroupmessage)
                            )
                        } else {
                            startActivity(
                                Intent(this, ViewDetailsActivity::class.java).putExtra(
                                    "uesrname",
                                    name
                                )
                                    .putExtra("sendername", senderName)
                                    .putExtra("mobilenumber", mobileNumber)
                                    .putExtra("thredid", tredid)
                                    .putExtra("isgroupmessage", isgroupmessage)
                            )

                        }

                    }
                }

                R.id.message_corner_menu_btn -> {
                    messageCornerDialog.show(supportFragmentManager, "messageCornerDialog")
//                    inMainAdapter.notifyDataSetChanged()
                }


                R.id.message_text_size_menu_btn -> {
                    messageTextSizeDialog.show(supportFragmentManager, "messageTextSizeDialog")
                }

                R.id.custom_wallpaper_menu_btn -> {
//                    customWallpaperDialog.show(supportFragmentManager, "customWallpaperDialog")
                    sendtoCustomwallpaper()

                }

                R.id.theme_wallpaper_menu_btn -> {
                    isfromprofile = true
                    startActivity(
                        Intent(this, ThemeColorActivity::class.java).putExtra(
                            "treadid", tredid
                        )
                    )
                }

                R.id.select_all_message -> {
                    var selectlist: ArrayList<Conversation> = arrayListOf()
                    selectlist.addAll(inMainAdapter.list)
                    selectlist.forEachIndexed { index, conversation ->
                        inMainAdapter.selectedMessageList.add(conversation)
                        inMainAdapter.notifyDataSetChanged()
                    }
                    selectmessage(selectlist, "")
//                    selectmessage()
                }
            }
            true
        }
        val fontRes = R.font.lato_semibold
        for (i in 0 until popup.menu.size()) {
            val item = popup.menu.getItem(i)
            applyFontToMenuItem(this, item, fontRes)
        }
        popup.show()


    }

    override fun recreate() {

        finish()
        overridePendingTransition(
            android.R.anim.fade_in, android.R.anim.fade_out
        )
        startActivity(intent)
        overridePendingTransition(
            android.R.anim.fade_in, android.R.anim.fade_out
        )

    }

    private fun lastmessageseleteforshowcase() {
        isshowcasestartshowing = true
//        binding.translateBtn.visibility = View.VISIBLE
        binding.messagetraslateall.visibility = View.VISIBLE
        binding.messagecount.text = "0"

        binding.moreBtn.visibility = View.VISIBLE
        binding.copyBtn.visibility = View.VISIBLE
        binding.messageshare.visibility = View.VISIBLE
        binding.deleteBtn.visibility = View.VISIBLE
        binding.messagecount.visibility = View.VISIBLE
        binding.userid.visibility = View.GONE
        binding.maincanternar.visibility = View.GONE
        binding.moreBtnForEditing.visibility = View.GONE
        binding.newSearch.visibility = View.GONE
        binding.colorTheme.visibility = View.GONE
    }


    private fun lastmessageseleteremoveforshowcase() {
        isshowcasestartshowing = false
        binding.copyBtn.visibility = View.GONE
        binding.messageshare.visibility = View.GONE
//        binding.translateBtn.visibility = View.GONE
        binding.messagetraslateall.visibility = View.GONE
        binding.deleteBtn.visibility = View.GONE
        binding.messagecount.visibility = View.GONE
        binding.moreBtn.visibility = View.GONE
        binding.userid.visibility = View.VISIBLE
        binding.maincanternar.visibility = View.VISIBLE
        binding.moreBtnForEditing.visibility = View.VISIBLE
        binding.newSearch.visibility = View.VISIBLE
        if (isgroupmessage) {
            binding.colorTheme.visibility = View.GONE
        } else {
            binding.colorTheme.visibility = View.VISIBLE
        }
        showOrHideKeyboard(true)
    }

    private fun showOrHideKeyboard(editText: Boolean) {
        binding.writemessageedt.isEnabled = editText
    }

    private fun isLightChatTheme(): Boolean {
        return ThemeModeManager.shouldUseLightSystemBars(this)
    }

    private fun resolveChatSurfaceColor(): Int {
        return getProperBackgroundColor()
    }

    private fun resolveChatOnSurfaceColor(): Int {
        return getProperTextColor()
    }

    private fun resolveChatOnSurfaceVariantColor(): Int {
        return getProperSecondaryTextColor()
    }

    private fun resolveChatPrimaryColor(): Int {
        return getProperPrimaryColor()
    }

    private fun formatColor(color: Int): String = String.format(Locale.US, "#%08X", color)

    private fun resolveDefaultIncomingBubbleColor(): Int {
        val isLightTheme = ThemeModeManager.shouldUseLightSystemBars(this)

        val defaultColor = ColorUtils.blendARGB(
            getProperBackgroundColor(), getProperPrimaryColor(), if (isLightTheme) 0.4f else 0.25f
        )
        return defaultColor
    }

    private fun resolveDefaultOutgoingBubbleColor(): Int {
        val isLightTheme = ThemeModeManager.shouldUseLightSystemBars(this)
        val defaultColor = ColorUtils.blendARGB(
            getProperBackgroundColor(), getProperTextColor(), if (isLightTheme) 0.05f else 0.05f
        )

        return defaultColor
    }

    private fun applyAdapterBubblePalette() {
        inMainAdapter.inmessagecolorcustomwallpaper =
            formatColor(resolveDefaultIncomingBubbleColor())
        inMainAdapter.outmessagecolorcustomwallpaper =
            formatColor(resolveDefaultOutgoingBubbleColor())
    }

    private fun resolveChatOutlineVariantColor(surfaceColor: Int, onSurfaceColor: Int): Int {
        return MaterialColors.layer(surfaceColor, onSurfaceColor, 0.12f)
    }

    private fun ThemeSetup() {
        applyMaterialChatTheme()
        applyMaterialSystemBarColors()
        if (::inMainAdapter.isInitialized) {
            inMainAdapter.notifyThemeChanged()
        }
    }

    private fun applyMaterialChatTheme() {
        val surfaceColor = resolveChatSurfaceColor()
        val statusBarColor = getProperStatusBarColor()
        val onSurfaceColor = resolveChatOnSurfaceColor()
        val onSurfaceVariantColor = resolveChatOnSurfaceVariantColor()
        val primaryColor = resolveChatPrimaryColor()
        val outlineVariantColor = resolveChatOutlineVariantColor(surfaceColor, onSurfaceColor)
        val optionFillColor = MaterialColors.layer(surfaceColor, onSurfaceColor, 0.04f)

        applyAdapterBubblePalette()
        binding.writemessageedt.setCursorColorProgrammatically(primaryColor)
        binding.messageserch.setCursorColorProgrammatically(primaryColor)

        binding.main.setBackgroundColor(surfaceColor)
        binding.mainbackground.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        binding.constraintLayout.setBackgroundColor(surfaceColor)
        binding.messageSearchholder.setBackgroundColor(surfaceColor)
        binding.messageserchlayoutcontent.setBackgroundColor(surfaceColor)
        binding.searchMessageBg.background = createOptionBackground(
            cornerSize = resources.getDimension(com.intuit.sdp.R.dimen._20sdp),
            fillColor = optionFillColor,
            strokeWidth = resources.getDimension(com.intuit.sdp.R.dimen._1sdp),
            strokeColor = surfaceColor
        )

        binding.datetxtCon.setCardBackgroundColor(optionFillColor)
        binding.datetxtCon.strokeWidth = 0
        binding.datetxtCon.cardElevation = 0f

        applyMaterialComposerBackground(mergeTopCorners = isReplayMessage || isScheduledMessage)

        binding.userid.setTextColor(onSurfaceColor)
        binding.txtNoReplayBox.setTextColor(onSurfaceColor)
        binding.messagecount.setTextColor(onSurfaceColor)
        binding.messageserch.setTextColor(onSurfaceColor)
        binding.messageserch.setHintTextColor(onSurfaceVariantColor.adjustAlpha(0.20f))
        binding.writemessageedt.setTextColor(onSurfaceColor)
        binding.writemessageedt.setHintTextColor(onSurfaceVariantColor.adjustAlpha(0.20f))
        binding.messagecountsend.setTextColor(onSurfaceVariantColor)
        binding.textView34.setTextColor(onSurfaceColor)
        binding.datetxt.setTextColor(onSurfaceColor)

        binding.backBtn.setIconResource(R.drawable.recycle_bin_material_ic_arrow_back_rounded)
        binding.messagebutton2.setIconResource(R.drawable.recycle_bin_material_ic_arrow_back_rounded)
        binding.serchCleasr.setIconResource(R.drawable.ic_m3_close_24)
        binding.attachFileBtn.setImageResource(R.drawable.baseline_attach_file_24)
        binding.sendbutton.setImageResource(R.drawable.baseline_send_24)
        binding.seduleMessageNew.setImageResource(R.drawable.home_material_ic_schedule_rounded)
        binding.scheduledMessageIcon.setImageResource(R.drawable.home_material_ic_schedule_rounded)

        val toolbarIconTint = ColorStateList.valueOf(onSurfaceVariantColor)
        listOf(
            binding.messagebutton2,
            binding.serchCleasr,
            binding.messageUpBtn,
            binding.messageDownBtn,
            binding.infoBtn,
            binding.ShareBtn,
            binding.messageshare,
            binding.messagetraslateall,
            binding.forverdBtn,
            binding.moreBtn,
            binding.moreBtnForEditing,
            binding.newSearch,
            binding.copyBtn,
            binding.deleteBtn,
            binding.fullChatTranslateBtn,
            binding.translateIcon,
        ).forEach { icon ->
            icon.iconTint = toolbarIconTint
        }

        listOf(
            binding.selectsim,
            binding.scheduledMessageIcon,
            binding.imgGet,
        ).forEach { icon ->
            icon.imageTintList = ColorStateList.valueOf(onSurfaceVariantColor)
        }

        binding.attachFileBtn.apply {

            background = createOptionBackground(
                cornerSize = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._500sdp)
                    .toFloat(),
                fillColor = getProperPrimaryColor(),
                rippleColor = Color.WHITE.adjustAlpha(0.5f),
                showRipple = true,
            )

            imageTintList = ColorStateList.valueOf(getProperPrimaryColor().getContrastColor())
        }




        binding.backBtn.iconTint = ColorStateList.valueOf(onSurfaceColor)
        binding.sendbutton.imageTintList = ColorStateList.valueOf(primaryColor)
        binding.gotobuttom.backgroundTintList = ColorStateList.valueOf(optionFillColor)
        binding.gotobuttom.imageTintList = ColorStateList.valueOf(primaryColor)
        binding.view.setBackgroundColor(outlineVariantColor)

        applyToolbarIconRipple(surfaceColor, onSurfaceColor)

    }

    private fun applyAttachmentBottomSheetMaterialTheme(sheetBinding: AttachMediaAndContentDialogBinding) {
        val surfaceColor = getDialogBackgroundColor()
        val onSurfaceColor = getProperTextColor()
        val onSurfaceVariantColor = getProperSecondaryTextColor()
        val primaryColor = getProperPrimaryColor()
        val isLightTheme = ThemeModeManager.shouldUseLightSystemBars(this)
        val rowFillColor = ColorUtils.blendARGB(surfaceColor, primaryColor, 0.1f)
        val strokeColor =
            MaterialColors.layer(surfaceColor, onSurfaceColor, if (isLightTheme) 0.12f else 0.24f)
        val sheetRippleColor = primaryColor.adjustAlpha(0.2f)
        val sheetCorner = resources.getDimension(com.intuit.sdp.R.dimen._20sdp)
        val sectionCorner = resources.getDimension(com.intuit.sdp.R.dimen._6sdp)
        val strokeWidth = resources.getDimension(com.intuit.sdp.R.dimen._1sdp)

        sheetBinding.attachmentSheetRoot.background = createOptionBackground(
            fillColor = surfaceColor,
            strokeWidth = 0f,
            strokeColor = strokeColor,
            useCustomCorners = true,
            topLeftCornerSize = sheetCorner,
            topRightCornerSize = sheetCorner,
            bottomLeftCornerSize = 0f,
            bottomRightCornerSize = 0f
        )

        sheetBinding.content.setTextColor(onSurfaceColor)

        listOf(
            R.id.textView41contact,
            R.id.textView41schedule,
            R.id.textView41last,
            R.id.textView41lastimage_attachment,
            R.id.textView41lastrecord_audio_attachment,
            R.id.textView41lastfile_attachment,
            R.id.textView41lastcontact_attachment
        ).forEach { viewId ->
            sheetBinding.root.findViewById<TextView>(viewId)?.setTextColor(onSurfaceColor)
        }
        listOf(
            R.id.textView42contact,
            R.id.textView42schedule,
            R.id.textView42last,
            R.id.textView42lastimage_attachment,
            R.id.textView42lastrecord_audio_attachment,
            R.id.textView42file_attachment,
            R.id.textView42contact_attachment
        ).forEach { viewId ->
            sheetBinding.root.findViewById<TextView>(viewId)?.setTextColor(onSurfaceVariantColor)
        }
        listOf(
            R.id.view22, R.id.view, R.id.view2, R.id.view3, R.id.view4, R.id.view8
        ).forEach { dividerId ->
            sheetBinding.root.findViewById<View>(dividerId)?.setBackgroundColor(strokeColor)
        }

        val styleRow: (rowId: Int, isTop: Boolean, isBottom: Boolean) -> Unit =
            { rowId, isTop, isBottom ->
                sheetBinding.root.findViewById<View>(rowId)?.background = createOptionBackground(
                    cornerSize = sectionCorner,
                    fillColor = rowFillColor,
                    strokeWidth = strokeWidth,
                    strokeColor = surfaceColor,
                    rippleColor = sheetRippleColor,
                    showRipple = true,
                    isTop = isTop,
                    isBottom = isBottom,
                    cornerSizeMultiplier = 2f
                )
            }
        styleRow(R.id.contact_card, true, false)
        styleRow(R.id.schedule_message_card, false, false)
        styleRow(R.id.location_message_card, false, false)
        styleRow(R.id.image_attachment, false, false)
        styleRow(R.id.record_audio_attachment, false, false)
        styleRow(R.id.file_attachment, false, true)
        styleRow(R.id.contact_attachment, true, true)

        listOf(
            R.id.ic_contact to R.drawable.contact_page_24px,
            R.id.ic_schedule to R.drawable.send_message_material_ic_schedule_rounded,
            R.id.ic_last_progres to R.drawable.location_on_24px,
            R.id.ic_last_image_attachment to R.drawable.photo_library_24px,
            R.id.ic_last_record_audio_attachment to R.drawable.mic_24px,
            R.id.ic_last_file_attachment to R.drawable.file_copy_24px
        ).forEach { (viewId, drawableId) ->
            sheetBinding.root.findViewById<com.google.android.material.button.MaterialButton>(viewId)
                ?.apply {
                    setIconResource(drawableId)
                    iconTint = ColorStateList.valueOf(primaryColor)
                    backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
                }
        }

        sheetBinding.imageView18.apply {
            setIconResource(R.drawable.home_material_ic_close_rounded)
            iconTint = ColorStateList.valueOf(onSurfaceColor)
            backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        }
    }

    private fun applyToolbarIconRipple(surfaceColor: Int, onSurfaceColor: Int) {
        val rippleAlpha = if (isLightChatTheme()) 0.12f else 0.28f
        val rippleColor = MaterialColors.layer(surfaceColor, onSurfaceColor, rippleAlpha)
        val rippleState = ColorStateList.valueOf(rippleColor)
        binding.backBtn.rippleColor = rippleState
        val mask = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.WHITE)
        }
        val baseRipple = RippleDrawable(ColorStateList.valueOf(rippleColor), null, mask)
        listOf(
            binding.moreBtn,
            binding.moreBtnForEditing,
            binding.colorTheme,
            binding.infoBtn,
            binding.ShareBtn,
            binding.messagetraslateall,
            binding.messageshare,
            binding.copyBtn,
            binding.deleteBtn,
            binding.forverdBtn,
            binding.messagebutton2,
            binding.serchCleasr,
            binding.messageUpBtn,
            binding.messageDownBtn,
            binding.fullChatTranslateBtn,
            binding.translateIcon,
        ).forEach { button ->
            button.rippleColor = rippleState
            button.iconTint = ColorStateList.valueOf(getProperTextColor())
        }
        listOf(
            binding.maincanternar,
            binding.translateBtn,
            binding.sendbutton,

            ).forEach { view ->
            view.background = baseRipple.constantState?.newDrawable()?.mutate()
        }


    }

    private fun applyMaterialComposerBackground(mergeTopCorners: Boolean) {
        val surfaceColor = resolveChatSurfaceColor()
        val primaryColor = getProperPrimaryColor()
        val inputContainerColor = ColorUtils.blendARGB(primaryColor, surfaceColor, 0.78f)

        val radius = resources.getDimension(com.intuit.sdp.R.dimen._20sdp)
        val topCornerSize =
            if (mergeTopCorners) resources.getDimension(com.intuit.sdp.R.dimen._8sdp) else radius

        binding.composerBottomRoot.background = createOptionBackground(
            fillColor = surfaceColor,
            strokeWidth = 0f,
            strokeColor = surfaceColor,
            useCustomCorners = true,
            topLeftCornerSize = topCornerSize,
            topRightCornerSize = topCornerSize,
            bottomLeftCornerSize = 0f,
            bottomRightCornerSize = 0f
        )

        binding.composerInputSendContainer.background = createOptionBackground(
            cornerSize = radius,
            fillColor = inputContainerColor,
            strokeWidth = 0f,
            strokeColor = inputContainerColor
        )

        binding.composerContainer.background = null
        binding.constraintLayout2.background = null
    }

    private fun applyMaterialSystemBarColors() {
        val surfaceColor = resolveChatSurfaceColor()
        val statusBarColor = getProperStatusBarColor()
        val navigationBarColor = getBottomNavigationBackgroundColor()
        val window = this.window
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars =
                ThemeModeManager.shouldUseLightSystemBars(this@SendMessageActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
    }

    private fun resolveSystemBarSurfaceColor(): Int {
        val wallpaperColor = if (binding.customwallpaperBg.isVisibleMess()) {
            val colorString = if (config.Message_Send_Activity == "1") {
                config.backgroundcolorcustomwallpaper
            } else {
                config.backgroundcolorcustomwallpaperab
            }
            colorString?.let { runCatching { Color.parseColor(it) }.getOrNull() }
        } else {
            null
        }
        val mainBackgroundColor = (binding.mainbackground.background as? ColorDrawable)?.color
        val rootBackgroundColor = (binding.main.background as? ColorDrawable)?.color
        val toolbarBackgroundColor = (binding.constraintLayout.background as? ColorDrawable)?.color
        return wallpaperColor ?: mainBackgroundColor ?: rootBackgroundColor
        ?: toolbarBackgroundColor ?: getProperBackgroundColor()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setThemeColor(
        toolbarcolor: String,
        backgroundcolor: String,
        priv: Boolean,
        inmessagecolorcustomwallpaper: String,
        outmessagecolorcustomwallpaper: String,
        smartreplycolor: String,
        shareprefselected: Int,
    ) {
        setImageviewSize()
        binding.customwallpaperBg.gone()

        applyMaterialSystemBarColors()

        //wallapaper

        if (config.isAllChatWallpaper) {
            val bitmap = getBitmapFromCache("isAllChatWallpaper.png")
            binding.customwallpaperBg.setImageBitmap(bitmap)
            binding.customwallpaperBg.visible()
        } else if (config.isAllChatDefault) {
            binding.customwallpaperBg.gone()
        }

        val customdata = config.getProfileUri(tredid.toString())
        customdata?.let {
            try {
                if (customdata.imguri.isEmpty()) {
                    binding.customwallpaperBg.gone()
                } else {
                    val bitmap = getBitmapFromCache("${customdata.imguri}.png")
                    binding.customwallpaperBg.setImageBitmap(bitmap)
                    binding.customwallpaperBg.visible()
                }
            } catch (e: Exception) {

            }

        }


//        if (binding.writemessageedt.text?.isNotEmpty() == true) {
//
//            binding.sendbutton.setImageResource(
//                getsendbuttonforcustoombuttonAB(
//                    false,
//                    tredid.toString()
//                )
//            )
//
//        } else {
//
//            binding.sendbutton.setImageResource(
//                getsendbuttonforcustoombuttonAB(
//                    true,
//                    tredid.toString()
//                )
//            )
//
//        }

        if (binding.customwallpaperBg.isVisibleMess()) {
            inMainAdapter.iscustomwallpaperset = true
        } else {
            inMainAdapter.iscustomwallpaperset = false
        }

        applyMaterialComposerBackground(mergeTopCorners = isReplayMessage || isScheduledMessage)
        applyAdapterBubblePalette()
        val surfaceColor = resolveChatSurfaceColor()
        val onSurfaceColor = resolveChatOnSurfaceColor()
        val onSurfaceVariantColor = resolveChatOnSurfaceVariantColor()
        val primaryColor = resolveChatPrimaryColor()
        val optionFillColor = MaterialColors.layer(surfaceColor, onSurfaceColor, 0.04f)

        binding.writemessageedt.setTextColor(onSurfaceColor)
        binding.writemessageedt.setHintTextColor(onSurfaceVariantColor)
        binding.messagecountsend.setTextColor(onSurfaceVariantColor)
        binding.SwipToReplyHolder.background =
            createOptionBackground(
                cornerSize = resources.getDimension(com.intuit.sdp.R.dimen._5sdp),
                fillColor = optionFillColor,
                strokeWidth = resources.getDimension(com.intuit.sdp.R.dimen._1sdp),
                strokeColor = surfaceColor
            )
        binding.swipReplayUnderBg.setBackgroundColor(optionFillColor)
        binding.SwipMessageTxt.setTextColor(onSurfaceColor)
        binding.SwipMessageTxtReceverName.setTextColor(primaryColor)
        binding.scheduledMessageHolder.background =
            createOptionBackground(
                cornerSize = resources.getDimension(com.intuit.sdp.R.dimen._10sdp),
                fillColor = optionFillColor,
                strokeWidth = resources.getDimension(com.intuit.sdp.R.dimen._1sdp),
                strokeColor = surfaceColor,
            )
        binding.ScheduleMessage.setTextColor(onSurfaceColor)
        binding.scheduledMessageButton.setTextColor(onSurfaceColor)


        replyChipAdapter.notifyDataSetChanged()
        inMainAdapter.notifyDataSetChanged()
        applyMaterialChatTheme()
        applyMaterialSystemBarColors()
    }

    private fun updatetoolbarcolor(toolbarcolor: Int) {
        applyMaterialSystemBarColors()
    }

//    private fun fullappwallpaperset(backgroundcolor: String) {
//        val bitmap = getBitmapFromCache("image1.png")
//        if (bitmap != null) {
//            Log.d("", "setThemeColor: iscustomgalleryimageset <----------> 2")
//            runOnUiThread {
//                binding.customwallpaperBg.visible()
//                binding.customwallpaperBg.setImageBitmap(bitmap)
//            }
//
//        } else {
//            runOnUiThread {
//                binding.customwallpaperBg.gone()
//                binding.mainbackground.background = ColorDrawable(Color.parseColor(backgroundcolor))
//            }
//        }
//    }

    fun checkPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= 33) {
            PermissionChecker.checkSelfPermission(
                this, Manifest.permission.READ_MEDIA_IMAGES
            ) == PermissionChecker.PERMISSION_GRANTED
        } else {
            PermissionChecker.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PermissionChecker.PERMISSION_GRANTED
        }
    }

    private fun sendtoCustomwallpaper() {
        CoroutineScope(Dispatchers.IO).launch {
            startActivity(
                Intent(
                    this@SendMessageActivity, CustomWallpaperManageActivity::class.java
                )
            )

        }
    }

    fun getBitmapFromCache(filename: String): Bitmap? {
        val cacheDir = cacheDir
        val file = File(cacheDir, filename)

        if (file.exists()) {
            try {
                val displayMetrics = resources.displayMetrics
                val reqWidth = displayMetrics.widthPixels.coerceAtLeast(1)
                val reqHeight = displayMetrics.heightPixels.coerceAtLeast(1)
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(file.absolutePath, options)
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
                options.inJustDecodeBounds = false
                options.inPreferredConfig = Bitmap.Config.RGB_565
                options.inDither = true
                return BitmapFactory.decodeFile(file.absolutePath, options)
            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
                return null
            }
        }
        return null
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        if (height <= 0 || width <= 0) {
            return 1
        }

        val heightRatio = ceil(height / reqHeight.toDouble()).toInt().coerceAtLeast(1)
        val widthRatio = ceil(width / reqWidth.toDouble()).toInt().coerceAtLeast(1)
        val rawSampleSize = maxOf(heightRatio, widthRatio)

        var inSampleSize = 1
        while (inSampleSize < rawSampleSize) {
            inSampleSize = inSampleSize shl 1
        }
        return inSampleSize.coerceAtLeast(1)
    }

    fun setImageviewSize() {
        setImageviewSize2()
        val layoutParams = binding.customwallpaperBg.layoutParams as ViewGroup.MarginLayoutParams
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels
        val screenWidth = displayMetrics.widthPixels
        layoutParams.width = screenWidth
        layoutParams.height = screenHeight
        binding.customwallpaperBg.layoutParams = layoutParams
        binding.customwallpaperBg.scaleType = ImageView.ScaleType.FIT_START
    }

    fun setImageviewSize2() {
//        Glide.with(this@SendMessageActivity)
//            .asBitmap()
//            .load(R.drawable.sendmessagebgnew)
//            .into(object : CustomTarget<Bitmap>() {
//                override fun onResourceReady(
//                    resource: Bitmap,
//                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?,
//                ) {
//                    CoroutineScope(Dispatchers.Main).launch {
//                        binding.customwallpaperBg2.setImageBitmap(
//                            resource
//                        )
//                    }
//                }
//
//                override fun onLoadCleared(placeholder: Drawable?) {
//
//                }
//            })
//        val layoutParams = binding.customwallpaperBg2.layoutParams as ViewGroup.MarginLayoutParams
//        val displayMetrics = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(displayMetrics)
//        val screenHeight = displayMetrics.heightPixels
//        val screenWidth = displayMetrics.widthPixels
//        layoutParams.width = screenWidth
//        layoutParams.height = screenHeight
//        binding.customwallpaperBg2.layoutParams = layoutParams
//        binding.customwallpaperBg2.scaleType = ImageView.ScaleType.CENTER_CROP

    }

    fun convertDpToPixel(dp: Float): Float {
        val density = resources.displayMetrics.density
        return dp * density
    }

    fun findIndexOfFirstNewMessage(messages: List<Conversation>): Int {
        for ((index, message) in messages.withIndex()) {
            if (message.isnewmessage == true) {
                return index
            }
        }
        return -1 // Return -1 if no new message found
    }

    private fun openEditDialog(pos: Int, list: ArrayList<Conversation>) {
        val builder = MaterialAlertDialogBuilder(this)
        val inflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_edit_text, null)
        val editText = dialogView.findViewById<TextInputEditText>(R.id.editText)
        editText.setText(list[pos].snippet)
        builder.setCancelable(false).setTitle(R.string.Edit_message)
            .setPositiveButton("OK") { dialog, which -> }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }

        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val text = editText.getText().toString()
            if (text.trim().isEmpty()) {
                toastMess(resources.getString(R.string.Please_enter_message))
                openEditDialog(pos, list)
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    list[pos].messageId?.let { messagerDatabaseRepo.updateMessageRepo(it, text) }
                }
                dialog.dismiss()
            }
        }
    }

    suspend fun setMessage(data: Conversation, forfaild: Boolean) {
        messagesudule = data.messageId?.let {
            data.threadId?.let { it1 ->
                if (forfaild) {
                    messagerDatabaseRepo.getMessageWithIdRepo(it1, it)
                } else {
                    messagerDatabaseRepo.getScheduledMessageWithIdRepo(it1, it)
                }

            }
        }
        if (messagesudule != null) {
            try {
                val subscriptionId = SmsManager.getDefaultSmsSubscriptionId()
                messagesudule?.let { messagebrod ->
                    val addresses = convertStringToArrayList(messagebrod.phoneNumber, "fornumber")
                    val settings = getSendMessageSettings()
                    if (addresses.size > 1) {
                        sendSMSManager.sendMessageCompat(
                            messagebrod.snippet,
                            addresses,
                            subscriptionId,
                            arrayListOf(),
                            isgroupmessage = true
                        )

                        val broadCastThreadId = getThreadId(addresses.toSet())
                        val mergedAddresses = addresses.joinToString(ADDRESS_SEPARATOR)
                        val messageuri = insertSmsMessageForGroup(
                            subId = settings.subscriptionId,
                            dest = mergedAddresses,
                            text = messagebrod.snippet,
                            timestamp = System.currentTimeMillis(),
                            threadId = broadCastThreadId,
                            status = Telephony.Sms.Sent.STATUS_COMPLETE,
                            type = Telephony.Sms.Sent.MESSAGE_TYPE_SENT
                        )
                        val insertedId = messageuri.lastPathSegment!!.toLong()
                        CoroutineScope(Dispatchers.IO).launch {

                            val job = CoroutineScope(Dispatchers.IO).launch {
                                listsedule = messagebrod.threadId?.toLong()
                                    ?.let { messagerDatabaseRepo.getUserMessageListrepo(it) }
                                    ?.let { ArrayList(it) }!!
                            }
                            job.join()


                            if (listsedule.isNotEmpty()) {


                                isarchiv = if (listsedule?.isEmpty() == true) {
                                    false
                                } else {
                                    listsedule!![0].isarchived
                                }

                                isPrivateChat = if (listsedule?.isEmpty() == true) {
                                    false
                                } else {
                                    listsedule!![0].isPrivateChat
                                }

                                CoroutineScope(Dispatchers.IO).launch {
                                    if (!messagerDatabaseRepo.isMessageExitsRepo(insertedId)) {
                                        val c: Date = Calendar.getInstance().time
                                        val data = messagerDatabaseRepo.getUserMessageListChackrepo(
                                            messagebrod.threadId!!
                                        )
                                        if (data.isNotEmpty()) {
                                            val isgropmessage = data[0].isgroupmessage
                                            if (isgropmessage) {
                                                val conversation = Conversation(
                                                    0,
                                                    c.time.toString(),
                                                    true,
                                                    data[0].title,
                                                    null,
                                                    false,
                                                    data[0].phoneNumber,
                                                    messagebrod.snippet,
                                                    c.time,
                                                    2,
                                                    true,
                                                    null,
                                                    messageId = insertedId,
                                                    threadId = messagebrod.threadId,
                                                    isgroupmessage = true,
                                                    groupName = data[0].groupName,
                                                    isarchived = isarchiv,
                                                    isPrivateChat = isPrivateChat,
                                                    isnewmessage = false
                                                )
                                                messagerDatabaseRepo.insertmessage(conversation)
                                            }
                                        } else {

                                            val participantsforgroupchat =
                                                getThreadParticipants(messagebrod.threadId!!, null)
                                            val addresses = participantsforgroupchat.getAddresses()
                                            val result = addresses.joinToString(separator = "|")

                                            val conversation = Conversation(
                                                0,
                                                c.time.toString(),
                                                true,
                                                participantsforgroupchat.getThreadTitle(),
                                                null,
                                                false,
                                                result,
                                                messagebrod.snippet,
                                                c.time,
                                                2,
                                                true,
                                                null,
                                                messageId = insertedId,
                                                threadId = messagebrod.threadId,
                                                isgroupmessage = true,
                                                groupName = participantsforgroupchat.getThreadTitle(),
                                                isarchived = isarchiv,
                                                isPrivateChat = isPrivateChat,
                                                isnewmessage = false
                                            )
                                            messagerDatabaseRepo.insertmessage(conversation)
                                        }
                                    }
                                }

                            } else {
                                Log.d("list", "setMessage:list <----------> 3")
                            }

                        }

                    } else {
                        if (forfaild) {
//                            data?.messageId?.let { messagerDatabaseRepo.deletemessageRepo(it) }
//                            sendMessageCompat(messagebrod.snippet, listOf(messagebrod.phoneNumber), subscriptionId, arrayListOf(), messagebrod.messageId)
                            Log.d(
                                "sendSMSManager",
                                "setMessage:sendSMSManager <------------> isfaildmessage  1"
                            )
                            sendSMSManager.sendMessageCompat(
                                messagebrod.snippet,
                                listOf(messagebrod.phoneNumber),
                                subscriptionId,
                                arrayListOf(),
                                false,
                                isfaildmessage = true,
                                messagebrod.messageId!!,
                                messagebrod.time!!
                            )
                        } else {
                            Log.d(
                                "sendSMSManager",
                                "setMessage:sendSMSManager <------------> isfaildmessage  2"
                            )
//                            sendSMSManager.sendMessageCompat(messagebrod.snippet, listOf(messagebrod.phoneNumber), subscriptionId, arrayListOf(), false)
                            sendSMSManager.sendMessageCompat(
                                messagebrod.snippet,
                                listOf(messagebrod.phoneNumber),
                                subscriptionId,
                                arrayListOf(),
                                false,
                                isfaildmessage = false
                            )

                        }
                    }
                }
                if (!forfaild) {
                    data?.messageId?.let { messagerDatabaseRepo.deletemessageRepo(it) }
                }


            } catch (e: Exception) {
                runOnUiThread { showErrorToast(e) }
            } catch (e: Error) {
                runOnUiThread {
                    showErrorToast(
                        e.localizedMessage ?: getString(R.string.unknown_error_occurred)
                    )
                }
            }
            if (data?.is_scheduled == true) {
                data?.messageId?.let { it1 -> cancelScheduleSendPendingIntent(it1) }
            }
        } else {
            runOnUiThread { toast(resources.getString(R.string.Something_Went_Wrong)) }
        }
    }

    private fun setUpEmojiPopup() {
//        emojiPopup = EmojiPopup.Builder.fromRootView(binding.mainbackground)
//            .setOnEmojiBackspaceClickListener(object : OnEmojiBackspaceClickListener {
//                override fun onEmojiBackspaceClick(v: View) {
//                }
//            })
//            .setOnEmojiClickListener(object : OnEmojiClickListener {
//                override fun onEmojiClick(imageView: EmojiImageView, emoji: Emoji) {
//                }
//            })
//            .setOnEmojiPopupShownListener(object : OnEmojiPopupShownListener {
//                override fun onEmojiPopupShown() {
//                    binding.imageView2.setImageResource(R.drawable.open_keybord_number_not)
//                }
//            })
//            .setOnSoftKeyboardOpenListener(object : OnSoftKeyboardOpenListener {
//                override fun onKeyboardOpen(keyBoardHeight: Int) {
//                }
//            })
//            .setOnEmojiPopupDismissListener(object : OnEmojiPopupDismissListener {
//                override fun onEmojiPopupDismiss() {
//                    binding.imageView2.setImageResource(R.drawable.emoji_btn)
//
//
//                }
//            })
//            .setOnSoftKeyboardCloseListener(object : OnSoftKeyboardCloseListener {
//                override fun onKeyboardClose() {
//                }
//            })
//            .build(binding.writemessageedt)
    }


    fun isLastItemVisible(recyclerView: RecyclerView): Boolean {
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is LinearLayoutManager) {
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            val totalItemCount = recyclerView.adapter?.itemCount ?: 0
            return lastVisibleItemPosition == totalItemCount - 1
        }
        return false
    }


    fun startandstopmessagesearch(start: Boolean) {
        if (start) {
            with(binding) {
                messageSearchholder.visible()
                messageserchlayoutcontent.visible()
                higlitetxt = higlitetxtstart
                binding.messageserch.setText(higlitetxt.toString())
            }
        } else {
            with(binding) {
                messageSearchholder.gone()
                messageserchlayoutcontent.gone()
                inMainAdapter.highlighttext = ""
                inMainAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun resendbottomsheetshow(pos: Int, list: ArrayList<Conversation>) {

        val binding = MessageResendBottomSheetBinding.inflate(layoutInflater)
        val dialogBg = getDialogBackgroundColor()
        val rootBackground = binding.root.background?.mutate()
        if (rootBackground is GradientDrawable) {
            rootBackground.setColor(dialogBg)
        } else {
            binding.root.setBackgroundColor(dialogBg)
        }
        val bottomSheetDialog = createChatBottomSheetDialog()

        bottomSheetDialog.setContentView(binding.root)
        val iconTint = ColorStateList.valueOf(getProperTextColor())
        listOf(
            binding.imageView37,
            binding.imageView38
        ).forEach { icon ->
            iconTint.also { icon.imageTintList = it }
        }

        binding.messageResend.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
//                list[pos].messageId?.toInt()?.let { deleteSMS(it) }
                setMessage(list[pos], true)
            }
            bottomSheetDialog.dismiss()
        }
        binding.messageDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                list[pos].let { conversation ->
                    if (conversation.is_scheduled) {
                        conversation.messageId?.let { it1 -> cancelScheduleSendPendingIntent(it1) }
                    }
                    messagerDatabaseRepo.deleteMessagerRepo(conversation.id)
                    conversation.messageId?.toInt()?.let { it1 -> deleteSMS(it1) }
                }
            }
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()

    }


//    fun attchmentdialogshowornot(check: Boolean) {
//        if (check) {
////            binding.attachFileBtn.setColorFilter(
////                ContextCompat.getColor(this, R.color.scr_tint_traslapantioncolor),
////                android.graphics.PorterDuff.Mode.SRC_IN
////            );
//            Log.d("jigar", "attchmentdialogshowornot: <------------> 1")
//            binding.attachFileBtn.gone()
//            binding.transformationLayout.startTransform()
//            var blurbitmap: Bitmap? = null
//            GlobalScope.launch {
//                withContext(Dispatchers.IO) {
//                    blurbitmap = getScreenShotFromView(binding.mainbackground)
//                }
//                withContext(Dispatchers.Main) {
//                    binding.blurebg.setImageBitmap(blurbitmap)
//                }
//            }
//            fadeInAnimation(binding.blurebg)
//        } else {
//            Log.d("jigar", "attchmentdialogshowornot: <------------> 2")
//            binding.transformationLayout.finishTransform()
//            fadeOutAnimation(binding.blurebg) {
//                Handler(Looper.myLooper()!!).postDelayed({
//                    binding.blurebg.gone()
//                    binding.attachFileBtn.visible()
//                    isattachmentopen = false
//                }, 100)
//            }
//        }
//    }

    private fun fadeInAnimation(view: View) {
        view.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(500).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    iseligibledimiss = true
                }
            })
        }
    }

    private fun fadeOutAnimation(view: View, onAnimationEnd: () -> Unit) {
        view.apply {
            animate().alpha(0f).setDuration(500).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = View.GONE
                    onAnimationEnd.invoke()
                }
            })
        }
    }

    suspend fun getScreenShotFromView(v: View): Bitmap? {
        var screenshot: Bitmap? = null
        try {
            screenshot =
                Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(screenshot)
            v.draw(canvas)
        } catch (e: Exception) {
            Log.e("GFG", "Failed to capture screenshot because:" + e.message)
        }
        return blurBitmap(this, screenshot!!, 25f, Color.WHITE)
    }

    suspend fun blurBitmap(
        context: Context,
        inputBitmap: Bitmap,
        blurRadius: Float,
        overlayColor: Int,
    ): Bitmap {
        val outputBitmap =
            Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)
        canvas.drawBitmap(inputBitmap, 0f, 0f, null)

        val rs = RenderScript.create(context)
        val blurInput = Allocation.createFromBitmap(rs, outputBitmap)
        val blurOutput = Allocation.createTyped(rs, blurInput.type)

        val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        blurScript.setRadius(blurRadius)
        blurScript.setInput(blurInput)
        blurScript.forEach(blurOutput)

        blurOutput.copyTo(outputBitmap)

        // Apply white overlay
        val paint = Paint()
        paint.color = overlayColor
        paint.alpha = 200 // Adjust the alpha value as needed for the overlay effect
        canvas.drawRect(0f, 0f, outputBitmap.width.toFloat(), outputBitmap.height.toFloat(), paint)

        rs.destroy()

        return outputBitmap
    }

    private fun showTutorial() {
        binding.apply {

            val targets = java.util.ArrayList<com.takusemba.spotlight.Target>()
            seduleMessageNew.post {

                val firstRoot = FrameLayout(this@SendMessageActivity)
                val first = layoutInflater.inflate(R.layout.img_edit_tutorial2_step1, firstRoot)
                val firstTarget =
                    com.takusemba.spotlight.Target.Builder().setAnchor(seduleMessageNew)
                        .setShape(Circle(radius = seduleMessageNew.height.div(2).toFloat()))
                        .setOverlay(first).build()

                targets.add(firstTarget)


                val spotlight = Spotlight.Builder(this@SendMessageActivity).setTargets(targets)
                    .setBackgroundColorRes(R.color.showcasecoor).setDuration(100L)
                    .setAnimation(DecelerateInterpolator(2f)).build()

                first.setOnClickListener {
                    spotlight.finish()
                    seduleMessageNew.performClick()
                }

                binding.root.doOnPreDraw {
                    spotlight.start()
                }
            }
        }
    }


    private fun removePattern(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val plainColorBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)

                // Check if the pixel is part of the pattern (adjust the color range based on your image)
                if (isPatternPixel(pixel)) {
                    // Set the pixel to a plain color (dark maroon in this case)
                    plainColorBitmap.setPixel(x, y, Color.rgb(53, 16, 23))
                } else {
                    plainColorBitmap.setPixel(x, y, pixel) // Keep non-pattern pixels unchanged
                }
            }
        }
        return plainColorBitmap
    }

    // Check if the pixel is part of the pattern (adjust the color threshold based on your pattern)
    private fun isPatternPixel(pixel: Int): Boolean {
        val red = Color.red(pixel)
        val green = Color.green(pixel)
        val blue = Color.blue(pixel)

        // Example condition for detecting white pattern color (adjust for your pattern)
        return red > 200 && green > 200 && blue > 200
    }


    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    private fun setupAttachmentPicker() {


        lateinit var imageCapture: ImageCapture


        if (combinedListAttachment.isEmpty()) {
            if (checkPermissions()) {
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        imageuriGalleryFirstimage = getRecentImage()
                        combinedListAttachment.clear()
                        combinedListAttachment = ArrayList(getAllImagesAndVideosSortedByRecentNew())
                    }
                    withContext(Dispatchers.Main) {
                        if (combinedListAttachment.isNotEmpty()) {
                            binding.imageandvideolist.visible()
                            binding.imageandvideolist.adapter = viewAllImageAdapter
                            viewAllImageAdapter.imagelist = combinedListAttachment
                        } else {
                            binding.imageandvideolist.gone()
                        }
                    }
                }
            }
        } else {
            if (combinedListAttachment.isNotEmpty()) {
                binding.imageandvideolist.visible()
                binding.imageandvideolist.adapter = viewAllImageAdapter

                val gridLayoutManager = MyGridLayoutManager(this@SendMessageActivity, 3)
                gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (position == 0) {
                            3
                        } else {
                            1
                        }
                    }
                }
                binding.imageandvideolist.layoutManager = gridLayoutManager
                viewAllImageAdapter.imagelist = combinedListAttachment
            } else {
                binding.imageandvideolist.gone()
            }
        }


        viewAllImageAdapter.cameraclick = {
            addAttachment(getMyFileUri(it))
            CoroutineScope(Dispatchers.Main).launch {
                toastMess("Photo capture succeeded")
            }
            if (getAttachmentSelections().isNotEmpty()) {
                binding.sendbutton.alpha = 1.0f
            } else {
                binding.sendbutton.alpha = 0.25f
            }
        }

        viewAllImageAdapter.folderclick = {
            launchGetContentIntentMix(PICK_VIDEO_AND_IMAGE_INTENT)
        }

        viewAllImageAdapter.fullcameraclick = {
            val intent = Intent(this, CameraActivity::class.java)
            startActivityForResult(intent, CUSTOME_CAMERA_REQUEST_CODE)
        }

        viewAllImageAdapter.imageclick = { uri ->
            if (uri != null) {
                addAttachment(uri.toUri())
            } else {
                toastMess("Image Not Found")
            }
            if (getAttachmentSelections().isNotEmpty()) {
                binding.sendbutton.alpha = 1.0f
            } else {
                binding.sendbutton.alpha = 0.25f
            }
        }
    }

    fun getContentUriFromFilePath(contentResolver: ContentResolver, filePath: String): Uri? {
        val file = File(filePath)
        if (file.exists()) {
            val projection: Array<String>
            val selection: String
            val selectionArgs = arrayOf(filePath)
            projection = arrayOf(MediaStore.Images.Media._ID)
            selection = "${MediaStore.Images.Media.DATA}=?"
            var uri = queryMediaStore(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs
            )
            if (uri == null) {
                projection[0] = MediaStore.Video.Media._ID
                uri = queryMediaStore(
                    contentResolver,
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs
                )
            }
            return uri
        }
        return null
    }

    private fun queryMediaStore(
        contentResolver: ContentResolver,
        uri: Uri,
        projection: Array<String>,
        selection: String,
        selectionArgs: Array<String>,
    ): Uri? {
        val cursor: Cursor? = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val id = it.getLong(it.getColumnIndexOrThrow(projection[0]))
                return ContentUris.withAppendedId(uri, id)
            }
        }
        return null
    }

    private fun keybordlisner() {
        setEventListener(
            this
        ) {

            logD("Keyboard-->", it.toString())

            if (it) {
                binding.constnativeBannerAdView.gone()
            } else {
                if (BaseSharedPreferences(this).mIS_SUBSCRIBED) {
                    binding.constnativeBannerAdView.gone()
                } else {
                    senderName.firstOrNull()?.isLetter()?.or(true)?.let { it1 ->
                        if (!it1) {
                            binding.constnativeBannerAdView.gone()
                            binding.nativeBannerAdShimmerBottom.gone()
                            binding.framenativeBannerAdBottom.gone()
                            return@setEventListener
                        }
                    }

                    try {
                        val child = binding.constnativeBannerAdView.getChildAt(0)
                        if (child != null && child.isVisible) {
                            binding.constnativeBannerAdView.visible()
                        } else {
                            binding.constnativeBannerAdView.gone()
                        }
                    } catch (e: Exception) {
                        binding.constnativeBannerAdView.gone()
                        binding.nativeBannerAdShimmerBottom.gone()
                        binding.framenativeBannerAdBottom.gone()
                    }

                }
            }
        }
    }

    private fun showRateDialog() {
        if (isFinishing || isDestroyed) {
            return
        }
        val bottomSheetDialog = createChatBottomSheetDialog()
        val view = DialogRateAppNewOneBinding.inflate(LayoutInflater.from(this))

        val cardColor = getDialogBackgroundColor()
        val titleColor = getProperTextColor()
        val bodyColor = getProperSecondaryTextColor()
        val iconTint = getProperTextColor()
        val starSelectedTint = getProperPrimaryColor()
        val starUnselectedTint = getProperSecondaryTextColor()
        val primaryColor = getProperPrimaryColor()


        view.rateCard.setCardBackgroundColor(cardColor)
        view.textView6.setTextColor(titleColor)
        view.textView21.setTextColor(bodyColor)
        view.textView22.setTextColor(bodyColor)
        view.textView37.setTextColor(bodyColor)
        view.ivClose.setColorFilter(iconTint)
        view.btnNextTime.backgroundTintList = ColorStateList.valueOf(primaryColor)
        val stars = listOf(
            view.star1, view.star2, view.star3, view.star4, view.star5
        )
        stars.forEach { star ->
            star.setImageResource(R.drawable.baseline_star_outline_24)
            star.setColorFilter(starUnselectedTint)
        }
        view.btnNextTime.alpha = 1f
        view.btnNextTime.isEnabled = true
//        view.btnNextTime.text = "Rate Now"
        var rate = 0
        stars.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                for (i in stars.indices) {
                    if (i <= index) {
                        stars[i].setImageResource(R.drawable.baseline_star_rate_24)
                        stars[i].setColorFilter(getColor(R.color.duskYellow))
                        rate = i + 1
                    } else {
                        stars[i].setImageResource(R.drawable.baseline_star_outline_24)
                        stars[i].setColorFilter(starUnselectedTint)
                    }
                }
            }
        }

        bottomSheetDialog.setOnDismissListener {
            baseConfig.RatingDialogshowCount++
        }

        view.ivClose.setOnClickListener {
            bottomSheetDialog.dismiss()
            showinterAdsAndFinish()
        }

        view.btnNextTime.setOnClickListener {
            "btnNextTime <-----------------> ${rate}".log()
            if (rate > 3) {
                baseConfig.isRatingDialogshow = false
                bottomSheetDialog.dismiss()
                openPlayReview()
//                finish()
//                showFeedbackDialog()
            } else {

                if (rate!=0){
                    Toast.makeText(this, "thank you for rating", Toast.LENGTH_SHORT).show()
                    bottomSheetDialog.dismiss()
                    showinterAdsAndFinish()  }else{
                    Toast.makeText(this, "please select stars", Toast.LENGTH_SHORT).show()

                }

            }
//            finish()
        }

        bottomSheetDialog.setContentView(view.root)
        if (!(isFinishing || isDestroyed)) {
            bottomSheetDialog.show()
        }
    }

    private fun rateApp() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (anfe: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }

    private fun openPlayReview() {
        val manager = ReviewManagerFactory.create(this)
//                val manager = FakeReviewManager(this)

        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo_ = task.result
                reviewInfo_?.let { reviewInfo ->
                    val flow = manager.launchReviewFlow(this@SendMessageActivity, reviewInfo)
                    flow.addOnCompleteListener { task ->
//                        Log.e(TAG, "ReviewFlow: ${task.isSuccessful}")

                    }.addOnFailureListener { error ->
//                        Log.e(TAG, "ReviewFlow:  Failure-->$error")

                    }
                }
            } else {
                // There was some problem, log or handle the error code.
                val reviewException = (task.exception)
//                Log.e(TAG, "getRating: reviewException:$reviewException")
            }
        }
    }

    fun Context.hasSim(): Boolean {
        // Quick fail if no permission
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.READ_PHONE_STATE
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("SendMessageActivity", "hasSim: READ_PHONE_STATE permission denied")
            return false // or throw exception / request permission
        }

        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val hasSim = telephonyManager.simState != TelephonyManager.SIM_STATE_ABSENT
        Log.d("SendMessageActivity", "hasSim: simState=${telephonyManager.simState} hasSim=$hasSim")
        return hasSim
    }
}
