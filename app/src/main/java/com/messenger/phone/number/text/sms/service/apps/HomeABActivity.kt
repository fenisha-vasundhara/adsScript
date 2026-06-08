package com.messenger.phone.number.text.sms.service.apps


//import com.simplemobiletools.commons.helpers.PERMISSION_POST_NOTIFICATIONS

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.AppOpsManager
import android.app.Dialog
import android.app.ProgressDialog
import android.app.role.RoleManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import android.provider.Telephony
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.appcompat.view.ActionMode
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.graphics.ColorUtils
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.demo.adsmanage.Activity.PayAllSubsciptionExterimentActivity
import com.demo.adsmanage.Activity.SubActivityTwoplanActivity
import com.demo.adsmanage.AdsManage.checkPurchases
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.Constants.offerscreenshow
import com.demo.adsmanage.Commen.firebaseEventMain
import com.demo.adsmanage.Commen.firebaseFunnel
import com.demo.adsmanage.Commen.firebaseeventTXT
import com.demo.adsmanage.Commen.log
import com.demo.adsmanage.SubscriptionBaseClass.SubSplashBaseActivity
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.demo.adsmanage.billing.ProductPurchaseHelper
import com.demo.adsmanage.helper.GlobalTimer
import com.demo.adsmanage.helper.click
import com.demo.adsmanage.helper.gone
import com.demo.adsmanage.helper.logD
import com.demo.adsmanage.helper.logE
import com.google.android.gms.ads.MobileAds
import com.google.android.material.chip.Chip
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.firebase.analytics.FirebaseAnalytics
import com.messenger.phone.number.text.sms.service.apps.CommanClass.Alredyclick
import com.messenger.phone.number.text.sms.service.apps.CommanClass.AutonotificationStat
import com.messenger.phone.number.text.sms.service.apps.CommanClass.CONTACT_INSERT_EDIT_REQUEST_CODE
import com.messenger.phone.number.text.sms.service.apps.CommanClass.HomescreenmessagelableNew
import com.messenger.phone.number.text.sms.service.apps.CommanClass.MessageType
import com.messenger.phone.number.text.sms.service.apps.CommanClass.PERMISSION_RECEIVE_SMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.SetSwitchColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ShowsubScreenDialogopen
import com.messenger.phone.number.text.sms.service.apps.CommanClass.SystemGeneratedIconSwitchAb
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.applyFontToMenuItem
import com.messenger.phone.number.text.sms.service.apps.CommanClass.archiveoldpos
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.blockcontectremove
import com.messenger.phone.number.text.sms.service.apps.CommanClass.checkSenderIsValid
import com.messenger.phone.number.text.sms.service.apps.CommanClass.checkedDocumentPath
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createOptionBackground
import com.messenger.phone.number.text.sms.service.apps.CommanClass.dialNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.drawableCache
import com.messenger.phone.number.text.sms.service.apps.CommanClass.drawableCache2
import com.messenger.phone.number.text.sms.service.apps.CommanClass.fromnotificationautoclick
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getDialogBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getIsFistTimeMessageget
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMaterialDialogTheme
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getcurruntseletedLeftcolor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getcurruntseletedLeftsrc
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getcurruntseletedRightcolor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getcurruntseletedRightsrc
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gooutside
import com.messenger.phone.number.text.sms.service.apps.CommanClass.handleDefaultSmsClick_1
import com.messenger.phone.number.text.sms.service.apps.CommanClass.hideKeyboard
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isDynamicTheme
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isFLow1
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isFLow1_2
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isFLow2
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isOfferMessage
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isOnline
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isSplash
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isSplashadsDone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isSystemInDarkMode
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isVisible
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isappfistopen
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isdefault
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isfastcrollerishide
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isfistcreatdatabase
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isfistcreateTimer
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isfisttimeadopen
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isfromnotification_New
import com.messenger.phone.number.text.sms.service.apps.CommanClass.islinechange
import com.messenger.phone.number.text.sms.service.apps.CommanClass.langchange
import com.messenger.phone.number.text.sms.service.apps.CommanClass.logOnboardingFunnelStep
import com.messenger.phone.number.text.sms.service.apps.CommanClass.markThreadAsRead
import com.messenger.phone.number.text.sms.service.apps.CommanClass.markThreadAsUnreadSingle
import com.messenger.phone.number.text.sms.service.apps.CommanClass.markastoallread
import com.messenger.phone.number.text.sms.service.apps.CommanClass.notify_vaule_New
import com.messenger.phone.number.text.sms.service.apps.CommanClass.openNotificationSettings
import com.messenger.phone.number.text.sms.service.apps.CommanClass.openautoclick
import com.messenger.phone.number.text.sms.service.apps.CommanClass.retrieveContactName
import com.messenger.phone.number.text.sms.service.apps.CommanClass.roleDefaultCheck
import com.messenger.phone.number.text.sms.service.apps.CommanClass.sendOfferActivity
import com.messenger.phone.number.text.sms.service.apps.CommanClass.sendmessagebuttondefaultset
import com.messenger.phone.number.text.sms.service.apps.CommanClass.sendsubactivity
import com.messenger.phone.number.text.sms.service.apps.CommanClass.sendsubactivityFromVarA
import com.messenger.phone.number.text.sms.service.apps.CommanClass.sendsubactivityOldnew
import com.messenger.phone.number.text.sms.service.apps.CommanClass.sendsubactivityPin
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setHomeViewBgColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setIsFistTimeMessageget
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setTextSizeFullApp
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setchatthemecolorTintImfullappforrecyler
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setchatthemecolorTintTxtFullapp
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setchatthemecolorTintTxtautodelete
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setchatthemecolorforHomeScreenBg
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setlayoutbackgroundcolordrawer
import com.messenger.phone.number.text.sms.service.apps.CommanClass.shareApp
import com.messenger.phone.number.text.sms.service.apps.CommanClass.startPulseAnimation
import com.messenger.phone.number.text.sms.service.apps.CommanClass.subScreenopen
import com.messenger.phone.number.text.sms.service.apps.CommanClass.systemfountstart
import com.messenger.phone.number.text.sms.service.apps.CommanClass.systemfountstartsetting
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.CommanClass.totalgetmessage
import com.messenger.phone.number.text.sms.service.apps.CommanClass.totalmessagecount
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.CustomMenu.MenuAnimation
import com.messenger.phone.number.text.sms.service.apps.CustomMenu.OnMenuItemClickListener
import com.messenger.phone.number.text.sms.service.apps.CustomMenu.PowerMenu
import com.messenger.phone.number.text.sms.service.apps.CustomMenu.PowerMenuItem
import com.messenger.phone.number.text.sms.service.apps.Dialog.AddCategoryDialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.CommanDeleteBlockDialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.FilePickerDialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.Message_Text_Size_New_Dialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.Message_filter_dialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.PermissionRequiredBottomSheet
import com.messenger.phone.number.text.sms.service.apps.Dialog.PermissionRequiredDialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.Signature_Setting_Dialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.feedback.ReviewDialogManager
import com.messenger.phone.number.text.sms.service.apps.GoogleMobileAdsConsentManagerChack.GoogleMobileAdsConsentManager
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.ShowCase.GuideView
import com.messenger.phone.number.text.sms.service.apps.SwipeController.InterfaceSwipAction
import com.messenger.phone.number.text.sms.service.apps.SwipeController.MessageSwipeHelper
import com.messenger.phone.number.text.sms.service.apps.adapter.MainMassageAdapter
import com.messenger.phone.number.text.sms.service.apps.adapter.MainScreenMenuAdapter
import com.messenger.phone.number.text.sms.service.apps.adapter.MainViewPagerAdapter
import com.messenger.phone.number.text.sms.service.apps.ads.NetworkObserver
import com.messenger.phone.number.text.sms.service.apps.ads.v2.AdUiBinding
import com.messenger.phone.number.text.sms.service.apps.ads.v2.AdsOrchestrator
import com.messenger.phone.number.text.sms.service.apps.data.GetMobileMessage
import com.messenger.phone.number.text.sms.service.apps.data.newsync.SmsSyncManager
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityHomeBcpAbactivityBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.DialogMessageBinding
import com.messenger.phone.number.text.sms.service.apps.helper.MessagesExporter
import com.messenger.phone.number.text.sms.service.apps.helperClass.NetworkHelper
import com.messenger.phone.number.text.sms.service.apps.inAppUpdate.InAppUpdate
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.CommanDeleteBlockDialogInterface
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MainMessageClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MessageClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MoreOPtionClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.TabAdapterClick
import com.messenger.phone.number.text.sms.service.apps.modelClass.Category
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversationbin
import com.messenger.phone.number.text.sms.service.apps.modelClass.Languagemodel
import com.messenger.phone.number.text.sms.service.apps.modelClass.Menumodel
import com.messenger.phone.number.text.sms.service.apps.modelClass.StarNumber
import com.messenger.phone.number.text.sms.service.apps.services.scheduleWorker
import com.messenger.phone.number.text.sms.service.apps.sms.SmsUtils.deleteConversation
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetAllCatViewModel
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetAllConversationViewModel
import com.simplemobiletools.commons.activities.BaseSimpleActivity.Companion.funAfterSdk30Action
import com.simplemobiletools.commons.extensions.beVisibleIf
import com.simplemobiletools.commons.extensions.convertToBitmap
import com.simplemobiletools.commons.extensions.createFirstParentTreeUri
import com.simplemobiletools.commons.extensions.getContrastColor
import com.simplemobiletools.commons.extensions.getFirstParentLevel
import com.simplemobiletools.commons.extensions.getFirstParentPath
import com.simplemobiletools.commons.extensions.hasPermission
import com.simplemobiletools.commons.extensions.humanizePath
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.helpers.KEY_PHONE
import com.simplemobiletools.commons.helpers.OPEN_DOCUMENT_TREE_FOR_SDK_30
import com.simplemobiletools.commons.helpers.PERMISSION_READ_CONTACTS
import com.simplemobiletools.commons.helpers.PERMISSION_READ_PHONE_STATE
import com.simplemobiletools.commons.helpers.PERMISSION_READ_SMS
import com.simplemobiletools.commons.helpers.PERMISSION_SEND_SMS
import com.simplemobiletools.commons.helpers.isNougatMR1Plus
import com.simplemobiletools.commons.helpers.isQPlus
import com.simplemobiletools.commons.helpers.isSPlus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.messenger.phone.number.text.sms.service.apps.realmplan.MigrationState
import com.messenger.phone.number.text.sms.service.apps.realmplan.MigrationStateHolder
import com.simplemobiletools.commons.extensions.onGlobalLayout
//import com.vision.aftercall.sdk.ads.advanceAds.registerAdsConfigSubscribe
import javax.inject.Inject
import kotlin.math.roundToInt


var onceShowDialog = false
var onceSetDefaulrt = false
var onceautoSetDefaulrt = false
private var isopenDirect = false
private var isopenDirectinter = false

@AndroidEntryPoint
class HomeABActivity : SubSplashBaseActivity(), ProductPurchaseHelper.ProductPurchaseListener,
    MessageClick, MainMessageClick, MoreOPtionClick, InterfaceSwipAction,
    CommanDeleteBlockDialogInterface, TabAdapterClick {
    lateinit var binding: ActivityHomeBcpAbactivityBinding
    var fromSetasdefault: String = ""
    var msOfDefault = 0L
    private val homeDiagTag = "HOME_LIST_DIAG"

    //    var overlayPermissionRequiredDialog: OverlayPermissionRequiredDialog2? = null
//    var overlayPermissionRequiredBottomDialog: OverlayPermissionRequiredBottomSheet? = null
    private val adOpenHandler = Handler(Looper.getMainLooper())
    private var openAdRunnable: Runnable? = null
    private var isSyncInProgress = false
    private var hasFlow23BootstrapSyncStarted = false
    private var flow23CenterLoaderRunnable: Runnable? = null
    var byUser = false
    private var themeSwitchUserAction = false
    private var migrationSnackbar: Snackbar? = null
    private val shouldUseNewSyncFlow: Boolean
        get() = baseConfig.OnBordingFlow_AB == "1" || baseConfig.OnBordingFlow_AB == "2"

    private val smsSyncManager: SmsSyncManager by lazy {
        SmsSyncManager(applicationContext, messagerDatabaseRepo, lifecycleScope)
    }

    private var permissionRequiredBottomSheet: PermissionRequiredBottomSheet? = null
    val requestCallPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            refreshHomeAfterPermissionGrant(11)
        } else {
            logD("TAG", "permissionCheck: byUser--->$byUser")
            if (byUser) {
                showRelationalPhoneDialog()
            }
            Log.e("FFF", ": requestCallPermissionLauncher else")

        }
    }

    val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            proceedAfterNotificationFlow()
        } else {
            if (byUser) {
                if (!hasPermission(Manifest.permission.READ_PHONE_STATE)) {
                    proceedAfterNotificationFlow()
                } else {
                    showRelationalNotificationDialog()

                }
            } else {
                proceedAfterNotificationFlow()
            }
            Log.e("FFF", ": requestNotificationPermissionLauncher else")

        }
    }
    private var activityResultForCallState =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (hasPermission(Manifest.permission.READ_PHONE_STATE)) {
                refreshHomeAfterPermissionGrant(12)
            } else {
                beNext()
            }
        }
    private var activityResultForNotification =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            proceedAfterNotificationFlow()
        }

    //    private var activityResultLauncherForOverlays =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (Settings.canDrawOverlays(this)) {
//                overlayPermissionRequiredDialog?.safeDismiss()
//
//                updateSmartCallerBannerVisibility(false, 1)
//
//            } else {
//                updateSmartCallerBannerVisibility(true, 2)
//
//                if (!::appOps.isInitialized) return@registerForActivityResult
//                if (!::appOpsListener.isInitialized) return@registerForActivityResult
//                appOps.stopWatchingMode(appOpsListener)
//            }
//            Handler(Looper.myLooper()!!).postDelayed(
//                {
//                    isTakingPermission = false
//                }, 2000
//            )
//        }
//
//    private var activityResultLauncherForOverlaysBottom =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (Settings.canDrawOverlays(this)) {
//                overlayPermissionRequiredBottomDialog?.safeDismiss()
//
//                updateSmartCallerBannerVisibility(false, 1)
//
//            } else {
//                updateSmartCallerBannerVisibility(true, 2)
//
//                if (!::appOps.isInitialized) return@registerForActivityResult
//                if (!::appOpsListener.isInitialized) return@registerForActivityResult
//                appOps.stopWatchingMode(appOpsListener)
//            }
//            Handler(Looper.myLooper()!!).postDelayed(
//                {
//                    isTakingPermission = false
//                }, 2000
//            )
//        }
    private lateinit var appOps: AppOpsManager
    lateinit var appOpsListener: AppOpsManager.OnOpChangedListener

    var filterdialogselectionmain = "defaultBtn"
    var selecteddateinmillisecoundmain = 0L
    var formateddatemain = ""
    var rangeselectedmain = false
    private val MAKE_DEFAULT_APP_REQUEST = 360
    private val DEFAULT_SMS_INSTRUCTION_REQUEST = 361
    private val FLOW3_LANGUAGE_REQUEST = 362
    private val SMS_PERMISSION_REQUEST = 1102
    private var builder: GuideView.Builder? = null
    private var mGuideView: GuideView? = null
    var isfisrttimecall = true
    var selectautocate = false
    private var isFirstItemClick = true
    var selectautocateName = "No"


    //    private var inter: InterstitialAdManager? = null
    private var pInfo: PackageInfo? = null
    private var itemTouchHelper: ItemTouchHelper? = null
    private var popupdialog: Dialog? = null
    private var homePopupMenu: PopupMenu? = null
    private lateinit var messageSwipeHelper: MessageSwipeHelper
    private lateinit var mProgressDialog: ProgressDialog
    private lateinit var conversation: Conversation
    private var actionMode: ActionMode? = null
    private var convolist: ArrayList<Conversation> = arrayListOf()
    private val ICON_MARGIN = 8
    var selecteditemmain: ArrayList<String> = arrayListOf()
    var selecteditemmainpin: ArrayList<Conversation> = arrayListOf()
    private var convolist2: ArrayList<Conversation> = arrayListOf()
    private var allmessage: ArrayList<Conversation> = arrayListOf()
    private var convolist2forpin: ArrayList<Conversation> = arrayListOf()
    private var transactionFilteredCache: ArrayList<Conversation>? = null
    private val transactionSnippetValidityCache = HashMap<String, Boolean>()
    private var offersFilteredCache: ArrayList<Conversation>? = null
    private val offersSnippetValidityCache = HashMap<String, Boolean>()
    var firsttime = false
    private var homemenu: PowerMenu? = null
    var isdefaultcatremove = false

    private var liststar: ArrayList<StarNumber> = arrayListOf()

    private var mCurFilter: String? = null
    private var isHomeSearchOpen: Boolean = false
    private var currentHomeSearchQuery: String = ""
    private var mainNoMessageVisibleState: Boolean = false
    private var lastNoResultsToastQuery: String? = null
    var actionOnPermission: ((granted: Boolean) -> Unit)? = null
    var isAskingPermissions = false
    private val GENERIC_PERM_HANDLER = 100


    private var commanDeleteBlockDialog: CommanDeleteBlockDialog? = null

    companion object {
        var funAfterSAFPermission: ((success: Boolean) -> Unit)? = null
    }

    private val smsExporter by lazy { MessagesExporter(this) }

    @Inject
    lateinit var mainViewPagerAdapter: MainViewPagerAdapter

    @Inject
    lateinit var mainScreenMenuAdapter: MainScreenMenuAdapter

    @Inject
    lateinit var getMobileMessage: GetMobileMessage

    @Inject
    lateinit var signatureSettingDialog: Signature_Setting_Dialog

    @Inject
    lateinit var messagerDatabaseRepo: MessagerDatabaseRepo

    @Inject
    lateinit var adapterMainMassage: MainMassageAdapter

    private val draftRefreshHandler = Handler(Looper.getMainLooper())
    private val draftRefreshRunnable = Runnable {
        if (::adapterMainMassage.isInitialized) {
            adapterMainMassage.updateDrafts(this)
        }

    }
    private var isPinSwipeInProgress = false
    private var pinSwipeRefreshCounter = 0
    private val pinSwipeRefreshThreshold = 10
    private val pinSwipeRefreshRunnable = Runnable {
        if (!isFinishing && !isDestroyed) {
            refreshHomeSwipe()
        }
    }
    private val homeBannerTriggerHandler = Handler(Looper.getMainLooper())
    private val homeBannerDelayRunnable = Runnable { maybeShowHomeBanner("dwell_20s") }
    private var homeBannerTriggered = false


    @Inject
    lateinit var addCategoryDialog: AddCategoryDialog

    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

    lateinit var model: GetAllConversationViewModel

    @Inject
    lateinit var repo: MessagerDatabaseRepo

    private lateinit var star: StarNumber

    private var isstarselected = false

    private var onMessageClick = false

    var whichbuttonclick = "Nothing"

    //    var list: ArrayList<Category> = arrayListOf()
    var tablist: ArrayList<Category> = arrayListOf()
    private var lastRenderedTabNames: List<String> = emptyList()
    private var lastTabChipLocaleTag: String = ""

    val inAppUpdate: InAppUpdate by lazy {
        InAppUpdate(this)
    }

    var menuitemlist: ArrayList<Menumodel> = arrayListOf()


    private val Splesh_TIME: Long = 1L
    private var secondsRemaining: Long = 0L

    //    var openManager: AppOpenManager? = null
    var status = 0
    var handler: Handler = Handler()

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var dragHelper: ItemTouchHelper

    var catdatachack: ArrayList<Category> = arrayListOf()
    var isshowads: Boolean = true
    private val adsOrchestrator by lazy { AdsOrchestrator.get(application) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()
        ChackCallIntent(intent)
        baseConfig.firsttimeflowuse = true
        if (!langchange) {
            if (config.All_Ads_On) {

            }
        } else {
            langchange = false
        }
//        if (BuildConfig.DEBUG) {
//        showToast("Home flow:${baseConfig.OnBordingFlow_AB}")
//        }

        val isFirstTime123 = baseConfig.tryjustcheckMarkOnboardingStepLogged(
            baseConfig.OnBordingFlow_AB,
            "First_home"
        )

        if (isFirstTime123) {
            logOnboardingFunnelStep("First_home")
//            baseConfig.FirstHome = true
        } else {
            firebaseEventMain("Home")

        }
        val colorPrimary = getProperPrimaryColor()


//        baseConfig.apply {
//        baseConfig.apply {
//            if ("Message_First_home".funnelState) {
//                "Message_First_home".funnelState = false
//                firebaseFunnel("Message_First_home")
//            } else {
//                "config.firsttimelangEvent <---------------------> Message_Home".log()
//                "DataBindingUtil <---------------------> Message_Home".log()
//            }
//        }
        appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
//        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_bcp_abactivity)
//        setupSearchSharedElementTransitions()
        setBaseTheme(binding.vAnd15StatusBar)
        applyPulseColor(colorPrimary)
        startSaveButtonPulse()

        setStatusbar()
        initializeMobileAdsSdk()

        if (!onceSetDefaulrt) {
            onceSetDefaulrt = true
            baseConfig.HomeDefaultSmsCount++
        }
        adsLoadAndShowTopNativeBanner()
        loadBannerSafe()
        binding.icDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//        loadinterads(getString(R.string.Inter_setdefaultapp))
        binding.offerTimeContenar.setOnClickListener {
            sendOfferActivity(false)
        }

        lifecycleScope.launch {
            GlobalTimer.remainingMs.collect { data ->
                with(binding) {
                    minutesCount.text = data.Min
                    secondsCount.text = data.Sec
                }
            }
        }

        lifecycleScope.launch {
            MigrationStateHolder.state.collect { state ->
                when (state) {
                    is MigrationState.Running -> {
                        migrationSnackbar?.dismiss()
                        migrationSnackbar = Snackbar.make(
                            binding.root,
                            "Migrating chat history…",
                            Snackbar.LENGTH_INDEFINITE
                        ).also { it.show() }
                    }

                    is MigrationState.Success -> {
                        migrationSnackbar?.dismiss()
                        migrationSnackbar = null
                    }

                    is MigrationState.PermanentlyFailed, is MigrationState.Failed -> {
                        migrationSnackbar?.dismiss()
                        migrationSnackbar = null
                    }

                    else -> Unit
                }
            }
        }

        if (baseConfig.US_subscription_flow_MS == "2") {
            if (baseConfig.FirstTimeShowSubscriptionTwo) {
                baseConfig.FirstTimeShowSubscriptionTwo = false
                sendsubactivity(true)
            }
        } else if (baseConfig.US_subscription_flow_MS == "3") {
            if (baseConfig.SecoundtimeSubshow == 2) {
                "config.firsttimelangEvent <---------------------> Message_Second_Premium".log()
                firebaseEventMain("Second_Premium")
                sendsubactivity(true)
                baseConfig.SecoundtimeSubshow++
            } else {
                baseConfig.SecoundtimeSubshow++
            }
        }

        binding.backBtn.setOnClickListener {
            binding.icDrawer.closeDrawer(GravityCompat.START)
        }
        scheduleWorker(this)
        config.saveCurrentDateOnce()
        setMainNoMessageState(false)
        binding.mainMenu.binding.selectioncount.isSelected = true
        binding.setDefault.startPulseAnimation()
        binding.btntxt.startPulseAnimation()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        binding.mainMenu.click {
//            if (BuildConfig.DEBUG) {
//                CallerModule.requestIgnoreBatteryOptimizations(this)
//            }
        }
        adapterMainMassage.adapteremtyornot = adapterEmpty@{ isListEmpty ->
            val isDefaultSms = roleDefaultCheck()
            val hasSmsPermissions = hasCoreSmsPermissions()
            isdefault = isDefaultSms
            Log.d(
                "Krupal", "permissionlayershow: isDefaultSms<--------------> ${isDefaultSms}-"
            )

            if (!hasSmsPermissions) {
                if (!isDefaultSms) {
                    permissionlayershow(true, 1)
                    binding.messageloadinglottie.gone()
                    binding.messageloadinglottie2.gone()
                    binding.messageper.gone()
                    binding.messageperper.gone()
                    setMainNoMessageState(false)
                    return@adapterEmpty
                } else {
                    permissionlayershow(true, 2)
                    updateDefaultSmsBannerVisibilitybyFlow2()
                    binding.messageloadinglottie.gone()
                    binding.messageloadinglottie2.gone()
                    binding.messageper.gone()
                    binding.messageperper.gone()
                    setMainNoMessageState(false)
                    return@adapterEmpty
                }
            } else {

                updateDefaultSmsBannerVisibilitybyFlow2()
                binding.messageloadinglottie.gone()
                binding.messageloadinglottie2.gone()
                binding.messageper.gone()
                binding.messageperper.gone()
                logD(
                    "refreshSmsInbox", "nomessagefoundchack 5 $isListEmpty"
                )
                setMainNoMessageState(isListEmpty)
                permissionlayershow(false, 3)
            }

            permissionlayershow(false, 4)
            binding.notdefault = !isDefaultSms
            binding.defaultSmsBanner.beVisibleIf(!isDefaultSms && !isFLow1)


            if (isListEmpty) {
                logD(
                    "refreshSmsInbox",
                    "getIsFistTimeMessageget 2 ${getIsFistTimeMessageget(this@HomeABActivity)}"
                )

                if (getIsFistTimeMessageget(this)) {
                    setMainNoMessageState(false)
                    var percentage = 1
                    logD("refreshSmsInbox", "oddie 1")
                    binding.messageloadinglottie.visible()
                    binding.messageloadinglottie2.visible()
                    binding.messageper.visible()
                    binding.messageperper.visible()
//                    binding.newmessage.gone()
                    showdefaultbutton(false)
//                                binding.nomessagefoundchack = false
                    binding.messageloadinglottie2.progress = percentage.toInt()
                    if (percentage.toInt() == 0) {
                        //binding.messageloadingloti.visible()
                        binding.messageper.text = resources.getString(R.string.sync_messages)
                    } else {
                        //binding.messageloadingloti.gone()
                        binding.messageper.text = resources.getString(R.string.sync_messages)
                    }
                } else {
                    logD(
                        "refreshSmsInbox", "nomessagefoundchack 1 true"
                    )
                    setMainNoMessageState(true)
                }
            } else {
                setMainNoMessageState(false)
            }
        }

        val default_but_not_per = roleDefaultCheck() && !hasCoreSmsPermissions()
        logD(
            "refreshSmsInbox",
            "oddie 2 $default_but_not_per/ ${hasCoreSmsPermissions()}/${roleDefaultCheck()}"
        )
        if (default_but_not_per) {
            binding.apply {
                setDefaultHelp.gone()
                setDefaultClick.gone()
                textView33.gone()
                textView44.gone()
                if (baseConfig.allPermissionRlationalDone) {
                    textView32.setText(resources.getString(R.string.default_but_not_permission_GRANTSSETTING))
                    btntxt.setText(resources.getString(R.string.settingsnew))
                } else {
                    textView32.setText(resources.getString(R.string.default_but_not_permission_info))
                    btntxt.setText(resources.getString(R.string.default_but_not_permission_cta))
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {


//                val resId = if (default_but_not_per) applyHintImagesForTheme()
//                else R.drawable.set_permission_image_background

//                val bitmap = BitmapFactory.decodeResource(resources, resId)
//                    ?: return

            // Optional: If you still want high quality
//                binding.imageView19.setImageBitmap(bitmap)
//                binding.imageView19.setImageResource(resId)


            CoroutineScope(Dispatchers.IO).launch {
                Glide.with(this@HomeABActivity)
                    .asBitmap()
                    .load(if (default_but_not_per) applyHintImagesForTheme() else R.drawable.set_permission_image_background)
                    .apply(
                        RequestOptions()
                            .format(DecodeFormat.PREFER_ARGB_8888) // ← This is the key for sharpness
                            .override(Target.SIZE_ORIGINAL)          // Load original size (no downsampling)
                            .dontTransform()                         // Prevent any auto scaling/transform
                    )
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            CoroutineScope(Dispatchers.Main).launch {
                                binding.imageView19.setImageBitmap(resource)
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
            }
        }

        binding.defaultSmsBanner.setOnClickListener { handleDefaultSmsClick("bnr") }
        binding.setDefault.setOnClickListener { handleDefaultSmsClick("") }


        if (isFLow2 && !roleDefaultCheck() && !isopenDirect) {
            isopenDirect = true
            if (!config.defaultSmsDialogSuppressed) {
                binding.setDefault.performClick()

            }

        }


        binding.imageView19.setOnClickListener { handleDefaultSmsClick("") }

        binding.setDefaultHelp.setOnClickListener {
            showVIdeo()
        }
        binding.setDefaultClick.setOnClickListener {
            showVIdeo()
        }


        if (config.SelectedLanguage == "ar") {
            binding.mainMenu.binding.selectioncount.gravity = Gravity.END
        } else {
            binding.mainMenu.binding.selectioncount.gravity = Gravity.START
        }
        binding.mainMenu.binding.icDrawerAbButton.setOnClickListener {
            firebaseEventMain("Home_Drawer_open")
            binding.icDrawer.openDrawer(GravityCompat.START)
        }


        try {
            checkPurchases { isSubscribecheck, data ->

                var isSubscribe = isSubscribecheck
                if (isappfistopen) {
                    isappfistopen = false
                }
                BaseSharedPreferences(this).mIS_SUBSCRIBED = isSubscribe
//                registerAdsConfigSubscribe(isSubscribe)
//                iZooto.setSubscription(isSubscribe)

//                BaseSharedPreferences(this).mIS_SUBSCRIBED = true
                val eventParamsList = mutableListOf<Pair<String, String>>()

//                data?.entitlements?.all?.forEach { (key, subscription) ->
//                    val status = determineSubscriptionStatus(subscription)
//                    Log.d(
//                        "TAG",
//                        "checkPurchases: entitlements <----> main ${convertSubscriptionStatus("${"Subscription '$key': $status"}")}"
//                    )
//                    Log.d(
//                        "TAG",
//                        "checkPurchases: entitlements <----> main ${"Subscription '$key': $status"}}"
//                    )
//                    try {
//                        firebaseEventMain(convertSubscriptionStatus("${"Subscription '$key': $status"}"))
//                    } catch (E: Exception) {}
////                    val params = Bundle().apply {
////                        var stringkey = "Subscription $key".replace(" ", "_")
////                        putString(stringkey, status)
////                    }
////                    firebaseAnalytics.logEvent("MessageApp", params)
//                }

            }
        } catch (e: Exception) {
        }

        binding.InAppSignatureShowMesageCount.isChecked = config.userpreferenceSignatureOnOff


        if (ThemeModeManager.isDarkThemeActive(this)) {
            binding.themeChange.isChecked = true
        } else {
            binding.themeChange.isChecked = false
        }

        binding.themeChange.setOnTouchListener { _, _ ->
            themeSwitchUserAction = true
            false
        }

        binding.themeChange.setOnCheckedChangeListener { _, isChecked ->
            if (!themeSwitchUserAction) {
                return@setOnCheckedChangeListener
            }
            themeSwitchUserAction = false
            firebaseEventMain("Home_Drawer_Theme_Change")
            if (isChecked) {
                ThemeModeManager.setThemeMode(this, ThemeModeManager.MODE_DARK)
            } else {
                ThemeModeManager.setThemeMode(this, ThemeModeManager.MODE_LIGHT)
            }
            settextsize()
        }

        openautoclick = intent.getBooleanExtra("isfromnotificationauto", false)
        if (openautoclick) {
            fromnotificationautoclick = intent.getStringExtra("notify_vaule").toString()
        }

        binding.backupAndRestoreBtnCardNew.setOnClickListener {
            whichbuttonclick = "backupAndRestoreBtnCardNew"

            startActivity(Intent(this@HomeABActivity, BekupActivity::class.java))

        }

        binding.scheduleMessageBtnCard.setOnClickListener {
            firebaseEventMain("Home_Drawer_Schedule")
            ShowsubScreenDialogopen(
                directopendialog = true,
                supportFragmentManager = supportFragmentManager,
                dialogcolose = { subopen ->
                    if (!subopen) {
                        startActivity(
                            Intent(
                                this@HomeABActivity, Schedule_Message_Show_Activity::class.java
                            )
                        )
                    }
                })
        }

        binding.preandsecBtnCard.setOnClickListener {
            startActivity(
                Intent(this, SettingActivity::class.java).putExtra(
                    "loadiswhatsnew", 9925
                )
            )
        }

        binding.ColorThemeBtnCard.setOnClickListener {
//            startActivity(
//                Intent(this, SettingActivity::class.java).putExtra(
//                    "loadiswhatsnew",
//                    992599
//                )
//            )
        }
        binding.ColorThemeBtnCard.gone()

        binding.RecycleBinBtnCard.setOnClickListener {
            firebaseEventMain("Home_Drawer_Recyclerbin")
            ShowsubScreenDialogopen(
                directopendialog = true,
                supportFragmentManager = supportFragmentManager,
                dialogcolose = { subopen ->
                    if (!subopen) {
                        startActivity(
                            Intent(this, RecycleBinActivity::class.java)
                        )
                    }
                })
        }

        binding.FontSizeBtnCard.setOnClickListener {
            firebaseEventMain("Home_Drawer_Font_Change")
            Message_Text_Size_New_Dialog { isdonebuttonclick, fontsizeselectionold, fontsizeold ->
                if (isdonebuttonclick) {
                    settextsize()
                }
            }.show(
                supportFragmentManager, "messageTextSizeDialog"
            )
        }

        CoroutineScope(Dispatchers.IO).launch {
            val messlist = config.getAllMessageList()
            if (messlist?.isNotEmpty() == true) {
                CoroutineScope(Dispatchers.Main).launch {
                    binding.messageloadinglottie.gone()
                    binding.messageper.gone()
                    binding.messageloadinglottie2.gone()
                    binding.mainpro.gone()

                    if (hasCoreSmsPermissions()) {
                        showdefaultbutton(true)
                    } else {
                        showdefaultbutton(false)
                    }
                }
//                setupconversation(messlist)
            }
        }


        model = ViewModelProvider(this)[GetAllConversationViewModel::class.java]

        model.GetAllConversationlivelistAlldata.observe(this, Observer {
            logHomeListDiag("observer_before size=${it.size}")
            blockcontectremove.clear()
            blockcontectremove.addAll(it.distinctBy { it.threadId })

            setupconversation(it)
            stabilizeHomeListAfterConversationUpdate(it.size)
            logHomeListDiag("observer_after size=${it.size}")
            CoroutineScope(Dispatchers.IO).launch { config.saveAllMessageList(it) }


        })
        CoroutineScope(Dispatchers.IO).launch {
            getCodeReffrence()
        }

        CoroutineScope(Dispatchers.IO).launch {
            if (!messagerDatabaseRepo.isCatExitsRepo("All Messages")) {
                val categories = listOf(
                    Category(
                        id = 0,
                        catName = "All Messages",
                        filterName = "All Messages",
                        isDefaultCategory = true
                    ),
                    Category(
                        id = 0,
                        catName = "Personal",
                        filterName = "Personal",
                        isDefaultCategory = true
                    ),
                    Category(
                        id = 0,
                        catName = "Transaction",
                        filterName = "Transaction",
                        isDefaultCategory = true
                    ),
                    Category(id = 0, catName = "otp", filterName = "otp", isDefaultCategory = true),
                    Category(
                        id = 0, catName = "Offers", filterName = "Offers", isDefaultCategory = true
                    ),
                    Category(id = 0, "Create Category", filterName = "Create Category", true)
                )
                categories.forEach {
                    messagerDatabaseRepo.insertorupdatecatgoryrep(it)
                }
            }
        }
        homemenu = getIconPowerMenu(this, this, onIconMenuItemClickListener)
        pInfo = packageManager.getPackageInfo(packageName, 0)

        googleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.getInstance(applicationContext)

        googleMobileAdsConsentManager.gatherConsent(this@HomeABActivity) { consentError ->
            if (consentError != null) {
                Log.w(
                    "TAG", "<-----------> ${consentError.errorCode}. ${consentError.message}"
                )
            }
            if (googleMobileAdsConsentManager.canRequestAds) {
                if (!BaseSharedPreferences(this@HomeABActivity).mIS_SUBSCRIBED!!) {
                    if (isOnline()) {
                    }
                }
            }

            if (googleMobileAdsConsentManager.isPrivacyOptionsRequired) {
                googleMobileAdsConsentManager.showPrivacyOptionsForm(this) { formError ->
                    if (formError != null) {
                        Toast.makeText(this@HomeABActivity, formError.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

        }

        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                menuitemlist.add(
                    Menumodel(
                        resources.getString(R.string.Language_nre),
                        R.drawable.home_material_ic_translate_rounded
                    )
                )
                menuitemlist.add(
                    Menumodel(
                        resources.getString(R.string.Mark_all_as_read),
                        R.drawable.home_material_ic_mark_email_read_rounded
                    )
                )

                menuitemlist.add(
                    Menumodel(
                        resources.getString(R.string.filter),
                        R.drawable.home_material_ic_filter_alt_rounded_dark
                    )
                )

//                menuitemlist.add(
//                    Menumodel(
//                        resources.getString(R.string.Select_all_message),
//                        R.drawable.selected_all_message
//                    )
//                )
//                menuitemlist.add(
//                    Menumodel(
//                        resources.getString(R.string.Manage_Category),
//                        R.drawable.manage_cat_ic_new
//                    )
//                )
                menuitemlist.add(
                    Menumodel(
                        resources.getString(R.string.Archive),
                        R.drawable.home_material_ic_archive_rounded
                    )
                )
                menuitemlist.add(
                    Menumodel(
                        resources.getString(R.string.Share_app),
                        R.drawable.home_material_ic_share_rounded
                    )
                )
                menuitemlist.add(
                    Menumodel(
                        resources.getString(R.string.Settings),
                        R.drawable.home_material_ic_settings_rounded
                    )
                )
            }
            withContext(Dispatchers.Main) {
                mainScreenMenuAdapter.setHasStableIds(true)
                mainScreenMenuAdapter.listmenu = menuitemlist
            }
        }
        mainScreenMenuAdapter.onClick = {
            when (it) {
                0 -> {
                    firebaseEventMain("Home_Menu_Language_open")
                    startActivity(
                        Intent(
                            this, SettingActivity::class.java
                        ).putExtra("loadiswhatsnew", 1)
                    )
                    if (!(isFinishing || isDestroyed)) {
                        popupdialog?.dismiss()
                    }


                }

                1 -> {

                    if (binding.newmessage.isVisible) {
                        if (roleDefaultCheck()) {

                            showcommandialog(
                                dialogtital = resources.getString(R.string.Delete_this_conversation),
                                dialogmessage = resources.getString(R.string.mark_all_messages_as_read),
                                positivebutton = resources.getString(R.string.ok),
                                negativebutton = resources.getString(R.string.cancel),
                                "mark all messages as read"
                            )
                        } else {
                            binding.setDefault.performClick()
                        }
                    } else {
                        binding.setDefault.performClick()
                    }
                    if (!(isFinishing || isDestroyed)) {
                        popupdialog?.dismiss()
                    }


                }

                2 -> {
                    firebaseEventMain("Home_Menu_Filter_open")
                    if (binding.newmessage.isVisible) {
                        showfilterdialog()
                    } else {
                        binding.setDefault.performClick()
                    }
                    if (!(isFinishing || isDestroyed)) {
                        popupdialog?.dismiss()
                    }
                }

                3 -> {
                    firebaseEventMain("Home_Menu_Archive")
                    startActivity(Intent(this, ArchivedActivity::class.java))
                    if (!(isFinishing || isDestroyed)) {
                        popupdialog?.dismiss()
                    }
                }

                4 -> {
                    firebaseEventMain("Home_Menu_App_Share")
                    shareApp(this)
                    if (!(isFinishing || isDestroyed)) {
                        popupdialog?.dismiss()
                    }
                }

                5 -> {
                    firebaseEventMain("Home_Menu_Setting")
                    startActivity(Intent(this, SettingActivity::class.java))
                    if (!(isFinishing || isDestroyed)) {
                        popupdialog?.dismiss()
                    }
                }
            }
        }

        binding.conversationsFastscroller.updateColors(getProperPrimaryColor().adjustAlpha(0.75f))
        ProductPurchaseHelper.initBillingClient(this, this)
        getMobileMessage.onCompleted = {
            if (isfastcrollerishide) {
                runOnUiThread { binding.conversationsProgressBar.gone() }


            }
            openAdOnO(true, 1200)
        }


        val modeltab = ViewModelProvider(this)[GetAllCatViewModel::class.java]
        modeltab.livecatlist.observe(this, Observer { cate ->
            setCatdata(cate)
        })

        adapterMainMassage.setHasStableIds(true)
        binding.adapter = adapterMainMassage

        adapterMainMassage.enableDebugLogging = true
        adapterMainMassage.debugTag = "HomeABAdapter"

        adapterMainMassage.isselectedAdapter = false
        adapterMainMassage.ismoreoption = true
        adapterMainMassage.showpinnedconversation = true
        adapterMainMassage.setInterface(this@HomeABActivity)
        adapterMainMassage.setInterfaceMoreClick(this@HomeABActivity)
        adapterMainMassage.setInterfaceMainClick(this@HomeABActivity)
        observeFlow23SyncUi()


        binding.imageView41.setOnClickListener {
            if (BaseSharedPreferences(this@HomeABActivity).mIS_SUBSCRIBED!!) {
                shareApp(this)
            } else {
                firebaseeventTXT = "Msg_purchased_drawer"
                sendsubactivity()
            }

        }

        binding.SettingBtnCard.setOnClickListener {
            firebaseEventMain("Home_Drawer_Setting")
            ShowsubScreenDialogopen(
                directopendialog = true,
                supportFragmentManager = supportFragmentManager,
                dialogcolose = { subopen ->
                    if (!subopen) {
                        startActivity(Intent(this, SettingActivity::class.java))
                    }
                })
        }

        if (openautoclick) {
            adapterMainMassage.registerAdapterDataObserver(object :
                RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    super.onChanged()
                    if (isFirstItemClick && adapterMainMassage.itemCount > 0) {
                        binding.allmassagerecycler.post {
                            binding.allmassagerecycler.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
                            isFirstItemClick = false
                        }
                    }
                }
            })
        }

        setupMenu()
        setupHomeInlineSearch()

        mProgressDialog = ProgressDialog(this, R.style.Dialog_Custom)
        mProgressDialog.setCancelable(false)
        mProgressDialog.setMessage(resources.getString(R.string.plz_wait))

        binding.allmassagerecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    maybeShowHomeBanner("first_scroll")
                }
                if (dy > 10 && binding.newmessage.isExtended) {
                    binding.newmessage.shrink()
//                    binding.newmessage.text = null
                }
                if (dy < -10 && !binding.newmessage.isExtended) {
//                    binding.newmessage.extend()
//                    binding.newmessage.text = getString(R.string.Chat)
                    binding.newmessage.iconGravity =
                        ExtendedFloatingActionButton.ICON_GRAVITY_TEXT_START
                }
                if (!recyclerView.canScrollVertically(-1)) {
//                    binding.newmessage.extend()
//                    binding.newmessage.text = getString(R.string.Chat)
                    binding.newmessage.iconGravity =
                        ExtendedFloatingActionButton.ICON_GRAVITY_TEXT_START
                }
            }
        })

        beforeCreateSetUp()
//        if (/*hasPermission(Manifest.permission.POST_NOTIFICATIONS) &&*/ hasPermission(Manifest.permission.READ_PHONE_STATE)) {
//            beforeCreateSetUp()
//        } else {
//            permissionCheck(true)
//
//        }

        binding.searchContact.setOnClickListener {
            shareApp(this)
        }

        binding.mainMenu.binding.filterMessageBg.setOnClickListener {
            showfilterdialog()
        }

        binding.mainMenu.binding.selectallmessage.setOnClickListener {
            selectAllMessage()
        }

        binding.sub.setOnClickListener {
            val subscriptionTarget =
                if (BaseSharedPreferences(this).isCurrentPlan == "Experiment Offering") {
                    SubActivityTwoplanActivity::class.java
                } else {
                    PayAllSubsciptionExterimentActivity::class.java
                }
            startActivity(
                Intent(this, subscriptionTarget).putExtra(
                    "AppOpen", "SettingsActivity"
                )
            )
        }
        val openPremium = {
//            firebaseEvent("HomeActivity", "Home_subscription")
//            firebaseEventMain("Home_Subscription")
            firebaseeventTXT = "Msg_purchased_home"
            sendsubactivity()
        }
        binding.mainMenu.binding.messagePremium.setOnClickListener { openPremium() }
        binding.mainMenu.binding.qurekaaa.setOnClickListener { openPremium() }

        binding.mainMenu.binding.moreButton.setOnClickListener {
            firebaseEventMain("Home_Menu_open")
//            if (binding.messageper.isVisible) {
//                return@setOnClickListener
//            }
//            firebaseEvent("HomeActivity", "Home_Menu")
//            if (!isdefault) {
//                return@setOnClickListener
//            }
            showpopupdialog(
                binding.mainMenu.binding.moreButton.x, binding.mainMenu.binding.moreButton.y, it
            )
        }


        binding.newmessage.setOnClickListener {

            selectionRemove()
            Constants.isActivitychange = true
            startActivity(Intent(this, SelectContactActivity::class.java))
        }
        binding.navDrawer.setOnClickListener {}

        binding.Star.setOnClickListener {

            if (!isstarselected) {
                binding.Star.setImageResource(R.drawable.home_material_ic_star_rounded)
                setStarAdapter()
                isstarselected = true
            } else {
                binding.Star.setImageResource(R.drawable.home_material_ic_star_border_rounded)
                adapterMainMassage.updateList(convolist2)
                isstarselected = false
            }

        }

        binding.mainMenu.binding.messageArchive.setOnClickListener {
//            firebaseEvent("HomeActivity", "Home_Archive")
            messagemessageArchive()
        }

        binding.mainMenu.binding.messageDelete.setOnClickListener {
            if (roleDefaultCheck()) {
                showcommandialog(
                    dialogtital = resources.getString(R.string.Delete_this_conversation),
                    dialogmessage = resources.getString(R.string.This_is_permanent),
                    positivebutton = resources.getString(R.string.Delete),
                    negativebutton = resources.getString(R.string.cancel),
                    "delete"
                )
            } else {
                binding.setDefault.performClick()
            }
        }

        binding.mainMenu.binding.messageSelectionClose.setOnClickListener {
            selectionRemove()
        }

        binding.mainMenu.binding.messagePin.setOnClickListener {
            pinConversation(true)
        }

        binding.mainMenu.binding.messageUnpin.setOnClickListener {
            pinConversation(false)
        }

        binding.mainMenu.binding.moreButtonMark.setOnClickListener {
            showMenuselection(it, R.menu.selection_menu)
        }

        binding.LanguageBtnCard.setOnClickListener {
//            firebaseEvent("HomeActivity", "Home_Drawer_premium")
            firebaseEventMain("Home_Drawer_Language")
            startActivity(Intent(this, SettingActivity::class.java).putExtra("loadiswhatsnew", 1))
        }

        signatureSettingDialog.Signature_Setting_DialogDismiss = {
            binding.InAppSignatureShowMesageCount.isChecked = config.userpreferenceSignatureOnOff
        }

        binding.InAppSignatureShowMesageCount.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                whichbuttonclick = "InAppSignatureShowMesageCount"

                signatureSettingDialog.show(
                    supportFragmentManager, "InAppSignatureShowMesageCount"
                )

//                if (!BaseSharedPreferences(this).mIS_SUBSCRIBED!!) {
//                    if (!config.userpreferenceSignatureOnOff) {
//                        val existingFragment =
//                            supportFragmentManager.findFragmentByTag("watchAdsDialog")
//                        if (existingFragment == null) {
//                            // Fragment not added, proceed to add or show it
//                            if (isshowads) {
//                                if (firebaseConfig.Reward_Setting_Signature) {
//                                    watchAdsDialog.setDialogContent(resources.getString(R.string.For_unrestricted_access_Signature))
//                                    watchAdsDialog.show(
//                                        supportFragmentManager,
//                                        "watchAdsDialog"
//                                    )
//                                } else {
//                                    sendsubactivity()
//                                }
//                            } else {
//                                signatureSettingDialog.show(
//                                    supportFragmentManager,
//                                    "InAppSignatureShowMesageCount"
//                                )
//                            }
//                        } else {
//
//                        }
//                    }
//                    binding.InAppSignatureShowMesageCount.isChecked =
//                        config.userpreferenceSignatureOnOff
//                } else {
//                    signatureSettingDialog.show(
//                        supportFragmentManager,
//                        "InAppSignatureShowMesageCount"
//                    )
//                }
            } else {
                config.userpreferenceSignatureOnOff = false
            }
        }

        binding.ConversationSwipBtnCard.setOnClickListener {
//            firebaseEvent("HomeActivity", "Home_Drawer_Swip")
            firebaseEventMain("Home_Drawer_Swipe")
            whichbuttonclick = "ConversationSwipBtnCard"


            startActivity(
                Intent(this, SettingActivity::class.java).putExtra(
                    "loadiswhatsnew", 99
                )
            )


        }

        if (config.isrationshow && !systemfountstart) {
            config.isrationshow = true
//            openRateDialog()
        } else {
            systemfountstart = false
            config.isrationshow = true
        }

        //autoDilog SetDefault custom ------
        if (!onceautoSetDefaulrt) {
            onceautoSetDefaulrt = true
            if (/*hasPermission(Manifest.permission.POST_NOTIFICATIONS) &&*/ hasPermission(Manifest.permission.READ_PHONE_STATE)) {

                val isDefaultSms = roleDefaultCheck()
                if (!isDefaultSms) {
                    fromSetasdefault = ""
                    if (config.defaultSmsDialogSuppressed) {
                        if (!isFLow1_2) {
//                            showDefaultSmsSettingsDialog("")
                        }
                    }
                }
            }
        }

        // . ---- autoDilog SetDefault custom


        applyTabChipStyle()
    }

    fun updateRelationalPermissionSettingFlow(byUser1: Boolean) {
        if (this@HomeABActivity.roleDefaultCheck()) {
            binding.textView32.setText(resources.getString(R.string.default_but_not_permission_GRANTSSETTING))
            binding.btntxt.setText(resources.getString(R.string.settingsnew))
        }
        if (byUser1) {
            openAppSettings()
        }
    }

    fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${packageName}")
        )
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun settextsize() {
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


    }

    override fun recreate() {

        return

        finish()
        overridePendingTransition(
            android.R.anim.fade_in, android.R.anim.fade_out
        )
        startActivity(intent)
        overridePendingTransition(
            android.R.anim.fade_in, android.R.anim.fade_out
        )
    }

    private fun initializeMobileAdsSdk() {
        binding.loadadAbcotenar.gone()

    }

    private fun setupSearchSharedElementTransitions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return
        }

        val transitionDuration = 380L
        val easing = FastOutSlowInInterpolator()

        window.sharedElementExitTransition = MaterialContainerTransform().apply {
            addTarget(R.id.search_message_bg)
            drawingViewId = android.R.id.content
            scrimColor = Color.TRANSPARENT
            fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
            duration = transitionDuration
            interpolator = easing
        }

        window.sharedElementReenterTransition = MaterialContainerTransform().apply {
            addTarget(R.id.search_message_bg)
            drawingViewId = android.R.id.content
            scrimColor = Color.TRANSPARENT
            fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
            duration = transitionDuration
            interpolator = easing
        }
    }

    private fun setCatdata(categories: List<Category>?) {
        tablist.clear()
        categories?.forEach { category ->
            if (category.catName != "Create Category") {
                if (category.isDefaultCategory) {
                    tablist.add(category)
                }
            }
        }

        if (isfisrttimecall) {
            if (tablist.isNotEmpty()) {
                isfisrttimecall = false
                if (selectautocate) {
                    selectautocate = false
                    MessageType = selectautocateName
                } else {
                    MessageType = tablist[0].catName
                }
            }
        } else if (tablist.isNotEmpty() && tablist.none { it.catName == MessageType }) {
            MessageType = tablist[0].catName
        }
        renderCategoryChips()
    }

    private fun renderCategoryChips() {
        if (tablist.isEmpty()) {
            binding.tabChips.setOnCheckedStateChangeListener(null)
            binding.tabChips.removeAllViews()
            lastRenderedTabNames = emptyList()
            lastTabChipLocaleTag = currentLocaleTag()
            binding.tabLayout.gone()
            return
        }

        val selectedCategory =
            tablist.firstOrNull { it.catName == MessageType }?.catName ?: tablist[0].catName
        MessageType = selectedCategory
        val tabNames = tablist.map { it.catName }
        val localeChanged = currentLocaleTag() != lastTabChipLocaleTag
        val shouldRebuild =
            tabNames != lastRenderedTabNames || binding.tabChips.childCount != tablist.size

        if (shouldRebuild) {
            binding.tabChips.setOnCheckedStateChangeListener(null)
            binding.tabChips.removeAllViews()

            tablist.forEachIndexed { index, category ->
                val chip = Chip(this).apply {
                    id = View.generateViewId()
                    text = getCategoryChipTitle(category)
                    isCheckable = true
                    isCheckedIconVisible = false
                    tag = index
                    layoutParams = ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
                binding.tabChips.addView(chip)
            }

            applyTabChipStyle()
            binding.tabChips.setOnCheckedStateChangeListener { group, checkedIds ->
                val checkedChipId =
                    checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
                val selectedChip = group.findViewById<Chip>(checkedChipId)
                    ?: return@setOnCheckedStateChangeListener
                val position = selectedChip.tag as? Int ?: return@setOnCheckedStateChangeListener
                val selectedTab =
                    tablist.getOrNull(position) ?: return@setOnCheckedStateChangeListener

                scrollTabChipToPosition(position, smooth = true)
                if (selectedTab.catName != MessageType) {
                    onClickTab(selectedTab.catName, position, tablist)
                }
            }

            lastRenderedTabNames = tabNames
        }

        if (shouldRebuild || localeChanged) {
            for (index in tablist.indices) {
                val view = binding.tabChips.getChildAt(index) as? Chip ?: continue
                view.text = getCategoryChipTitle(tablist[index])
            }
        }
        lastTabChipLocaleTag = currentLocaleTag()

        val selectedPosition =
            tablist.indexOfFirst { it.catName == selectedCategory }.coerceAtLeast(0)
        val selectedChip = binding.tabChips.getChildAt(selectedPosition) as? Chip
        if (selectedChip != null && binding.tabChips.checkedChipId != selectedChip.id) {
            binding.tabChips.check(selectedChip.id)
        }
        scrollTabChipToPosition(selectedPosition, smooth = false)
    }

    private fun scrollTabChipToPosition(position: Int, smooth: Boolean = true) {
        if (position < 0 || position >= binding.tabChips.childCount) {
            return
        }

        binding.tabLayout.post {
            val chipView = binding.tabChips.getChildAt(position) ?: return@post
            val targetX =
                (chipView.left - (binding.tabLayout.width - chipView.width) / 2).coerceAtLeast(0)
            if (smooth) {
                binding.tabLayout.smoothScrollTo(targetX, 0)
            } else {
                binding.tabLayout.scrollTo(targetX, 0)
            }
        }
    }

    private fun applyTabChipStyle() {
        val backgroundColor = getProperBackgroundColor()
        val primaryColor = getProperPrimaryColor()
        val textColor = getProperTextColor()
        val primaryContainerLow = blendWithBaseColor(primaryColor, backgroundColor, 0.06f)
        val primaryOutline = blendWithBaseColor(primaryColor, backgroundColor, 0.40f)
        val primaryTextSecondary = ColorUtils.blendARGB(textColor, primaryColor, 0.22f)

        val chipBackgroundColors = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked), intArrayOf()
            ), intArrayOf(
                primaryContainerLow, backgroundColor
            )
        )

        val chipTextColors = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked), intArrayOf()
            ), intArrayOf(
                primaryColor, primaryTextSecondary.adjustAlpha(0.9f)
            )
        )

        val tintedCheck = resources.getDrawable(R.drawable.check_chip, null)?.mutate()
        tintedCheck?.setTint(primaryColor)
        for (index in 0 until binding.tabChips.childCount) {
            val view = binding.tabChips.getChildAt(index)
            if (view is Chip) {
                view.chipBackgroundColor = chipBackgroundColors
                view.setTextColor(chipTextColors)
                view.chipStrokeColor = ColorStateList.valueOf(primaryOutline)
                view.chipStrokeWidth = resources.getDimension(com.intuit.sdp.R.dimen._1sdp)
                view.rippleColor = ColorStateList.valueOf(primaryColor.adjustAlpha(0.16f))
                view.isCheckedIconVisible = true
                // or .setTintList(...) if you want state-based

                view.checkedIcon = tintedCheck
//                view.checkedIcon = resources.getDrawable(R.drawable.check_chip)
                view.checkedIconTint = ColorStateList.valueOf(primaryColor)
                view.invalidate()
                view.refreshDrawableState()
            }
        }
    }

    private fun getCategoryChipTitle(category: Category): String {
        val nameKey = normalizeCategoryKey(category.catName)
        val filterKey = normalizeCategoryKey(category.filterName ?: "all")
        return when {
            nameKey == "otp" || nameKey == "otps" || filterKey == "otp" -> getString(R.string.OTPs)
            nameKey == "allmessages" || nameKey == "all" || filterKey == "allmessages" || filterKey == "all" -> getString(
                R.string.all_message
            )

            nameKey == "personal" || filterKey == "personal" -> getString(R.string.Personal)
            nameKey == "transaction" || filterKey == "transaction" -> getString(R.string.Transaction)
            nameKey == "offers" || filterKey == "offers" -> getString(R.string.Offers)
            else -> category.catName
        }
    }

    private fun normalizeCategoryKey(value: String): String {
        return value.trim().lowercase(Locale.ROOT).replace("\\s+".toRegex(), "")
    }

    private fun currentLocaleTag(): String {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION") resources.configuration.locale
        }
        return locale.toLanguageTag()
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

    //    fun applyFontToMenuItem(context: Context, menuItem: MenuItem, fontRes: Int) {
//        val typeface = ResourcesCompat.getFont(context, fontRes)
//        val spannable = SpannableString(menuItem.title)
//
//        spannable.setSpan(
//            object : TypefaceSpan("") {
//                override fun updateDrawState(ds: TextPaint) {
//                    ds.typeface = typeface
//                }
//
//                override fun updateMeasureState(paint: TextPaint) {
//                    paint.typeface = typeface
//                }
//            },
//            0,
//            spannable.length,
//            Spannable.SPAN_INCLUSIVE_INCLUSIVE
//        )
//
//        menuItem.title = spannable
//    }
    @SuppressLint("RestrictedApi")
    private fun showMenuselection(v: View?, menuRes: Int) {
        setLocal()
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


        val popupGravity = if (config.SelectedLanguage == "ar") {
            Gravity.START
        } else {
            Gravity.END
        }
        val popup = PopupMenu(themedContext, v!!, popupGravity)


        popup.menuInflater.inflate(menuRes, popup.menu)

        val addtocontactselecdtionbtn = popup.menu.findItem(R.id.add_to_contact_selecdtion_btn)
        val removecat = popup.menu.findItem(R.id.remove_to_category_btn)

        removecat?.isVisible = isdefaultcatremove

        addtocontactselecdtionbtn?.isVisible =
            selecteditemmainpin.isNotEmpty() && selecteditemmainpin.size == 1 && !selecteditemmainpin[0].isgroupmessage


        if (popup.menu is MenuBuilder) {
            val menuBuilder = popup.menu as MenuBuilder
            menuBuilder.setOptionalIconsVisible(true)

            val iconMarginPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, ICON_MARGIN.toFloat(), resources.displayMetrics
            ).toInt()

            for (item in menuBuilder.visibleItems) {
                if (item.icon != null) {
                    item.icon = InsetDrawable(item.icon, iconMarginPx, 0, iconMarginPx, 0)
                }
            }
        }





        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.mark_as_read_selecdtion_btn -> {
                    selectedmessageread()
                }

                R.id.mark_as_unread_selecdtion_btn -> {
                    selectedmessageUnread()
                }

                R.id.move_to_privacy_selecdtion_btn -> {
                    movetoprivacychat()
                }

                R.id.add_to_contact_selecdtion_btn -> {
                    addtocontact()
                }

                R.id.remove_to_category_btn -> {
                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            if (selecteditemmainpin.isNotEmpty()) {
                                messagerDatabaseRepo.updateEmptymessageCatNameRepo(
                                    "", selecteditemmain
                                )
                            }
                        }
                        withContext(Dispatchers.Main) {
                            selectionRemove()
                        }
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

        popup.show() // show first so ListView is created
    }

    private fun addtocontact() {
        val conversation = selecteditemmainpin[0] ?: return
        Intent().apply {
            action = Intent.ACTION_INSERT_OR_EDIT
            type = "vnd.android.cursor.item/contact"
            putExtra(KEY_PHONE, conversation.phoneNumber)
            startActivityForResult(this, CONTACT_INSERT_EDIT_REQUEST_CODE)
        }
    }

    private fun movetoprivacychat() {

        if (config.Lock_Screen_Pin == "Not Set") {
            toastMess(resources.getString(R.string.Please_set_first_private))
            startActivity(
                Intent(this, LockScreenSetupActivity::class.java).putExtra(
                    "comefrom", 1
                )
            )
        } else {
            if (selecteditemmain.isEmpty()) {
                selectionRemove()
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    if (!BaseSharedPreferences(this@HomeABActivity).mIS_SUBSCRIBED) {
                        if (repo.getallconversationunPrivacyOrnotListrepo()
                                .distinctBy { it.threadId }.size + selecteditemmain.size > 3
                        ) {
                            sendsubactivity()
                            CoroutineScope(Dispatchers.Main).launch {
                                selectionRemove()
                            }
                        } else {
                            config.removePinnedConversations(selecteditemmainpin)
                            repo.PrivacyConversationRepo(selecteditemmain)
                            CoroutineScope(Dispatchers.Main).launch {
                                selectionRemove()
                                binding.mainMenu.binding.isselection = false
                                adapterMainMassage.notifyDataSetChanged()
                            }
                        }
                    } else {
                        config.removePinnedConversations(selecteditemmainpin)
                        repo.PrivacyConversationRepo(selecteditemmain)
                        CoroutineScope(Dispatchers.Main).launch {
                            selectionRemove()
                            binding.mainMenu.binding.isselection = false
                            adapterMainMassage.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    private fun selectedmessageread() {

        GlobalScope.launch {
            // Make a snapshot copy – safe even if original list is modified later
            val selectedIds = selecteditemmain.toList()

            withContext(Dispatchers.IO) {
                selectedIds.forEach { item ->
                    repo.setisoldmessageRepo(false, item.toLong())
                    repo.setisoldmessageCountRepo(0, item.toLong())
                    markThreadAsRead(item.toLong())
                }
            }

            withContext(Dispatchers.Main) {
                selectionRemove()
            }
        }

    }

    private fun selectedmessageUnread() {

        GlobalScope.launch {
            val selectedIds = selecteditemmain.toList()

            withContext(Dispatchers.IO) {
                selectedIds.forEach { item ->
                    val threadId = item.toLong()
                    repo.setisoldmessageRepo(true, threadId)
                    repo.setisoldmessageCountRepo(1, threadId)
                    markThreadAsUnreadSingle(item.toLong())
//                    try {
//                        val values = android.content.ContentValues().apply {
//                            put(Telephony.Sms.READ, 0)
//                        }
////                        val selection = "${Telephony.Sms.THREAD_ID} = ? AND ${Telephony.Sms.READ} = 1"
//
//                        val selection = "${Telephony.Sms.THREAD_ID} = ?"
//                        val selectionArgs = arrayOf(threadId.toString())
//                        contentResolver.update(
//                            Telephony.Sms.Inbox.CONTENT_URI, values, selection, selectionArgs
//                        )
//
//
////                        ---------
//
//                    } catch (_: SecurityException) {
//                    } catch (_: Exception) {
//                    }
                }
            }

            withContext(Dispatchers.Main) {
                selectionRemove()
            }
        }
    }

    /*    private fun selectedmessageread() {

         GlobalScope.launch {
             withContext(Dispatchers.IO) {
                 selecteditemmain.forEach {
                     repo.setisoldmessageRepo(false, it.toLong())
                     repo.setisoldmessageCountRepo(0, it.toLong())
                     markThreadAsRead(it.toLong())
                 }
             }
             withContext(Dispatchers.Main) {
                 selectionRemove()
             }
         }

    }*/

    private fun setupMenu() {
        binding.mainMenu.toggleHideOnScroll(false)
    }

    private fun showKeyboard123(editText: AppCompatEditText) {
        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        editText.apply {
            requestFocus()
            onGlobalLayout {
                setSelection(text.toString().length)
            }
        }
    }

    private fun setupHomeInlineSearch() {
        val searchInput = binding.mainMenu.binding.homeSearchInput

        searchInput.setText(currentHomeSearchQuery)
        searchInput.setSelection(searchInput.text?.length ?: 0)
        searchInput.addTextChangedListener { editable ->
            applyHomeSearchQuery(editable?.toString().orEmpty())
        }


        binding.mainMenu.binding.searchMessageBg.setOnClickListener { binding.mainMenu.binding.homeSearchInput.performClick() }

        adapterMainMassage.onFilterResultsChanged = { filteredCount, query ->
            currentHomeSearchQuery = query
            isHomeSearchOpen = query.isNotBlank()
            adapterMainMassage.setSearchHighlightQuery(query, refreshList = false)
            if (hasCoreSmsPermissions()) {
                setMainNoMessageState(filteredCount == 0)
            }
        }
    }

    private fun applyHomeSearchQuery(query: String) {
        val normalizedQuery = query.trim()
        val queryChanged = currentHomeSearchQuery != normalizedQuery

        currentHomeSearchQuery = normalizedQuery
        isHomeSearchOpen = normalizedQuery.isNotBlank()
        adapterMainMassage.setSearchHighlightQuery(normalizedQuery, refreshList = false)

        if (!queryChanged && adapterMainMassage.itemCount > 0) {
            return
        }

        adapterMainMassage.filter.filter(normalizedQuery)
    }

    private fun shouldReloadHomeDataOnResume(): Boolean {
        val hasCachedConversations = convolist2.isNotEmpty() || adapterMainMassage.list.isNotEmpty()
        return !(currentHomeSearchQuery.isNotBlank() && hasCachedConversations)
    }

    private fun restoreHomeSearchResultsOnResume() {
        if (!::adapterMainMassage.isInitialized) return
        if (currentHomeSearchQuery.isBlank()) return

        when {
            convolist2.isNotEmpty() -> adapterMainMassage.updateList(convolist2)
            allmessage.isNotEmpty() -> adapterMainMassage.updateList(allmessage)
        }
    }

    private fun restoreHomeListAfterThemeRefresh(source: Int) {
        if (!::adapterMainMassage.isInitialized) return
        if (!roleDefaultCheck() || !hasCoreSmsPermissions()) return

        binding.main.post {
            if (isFinishing || isDestroyed) return@post

            when {
                convolist2.isNotEmpty() -> {
                    adapterMainMassage.updateList(convolist2)
                    showHomeConversationContent()
                    logHomeListDiag("theme_restore_convolist2 source=$source size=${convolist2.size}")
                }

                adapterMainMassage.list.isNotEmpty() -> {
                    showHomeConversationContent()
                    logHomeListDiag("theme_restore_adapter source=$source size=${adapterMainMassage.list.size}")
                }

                allmessage.isNotEmpty() -> {
                    setupconversation(ArrayList(allmessage))
                    showHomeConversationContent()
                    logHomeListDiag("theme_restore_allmessage source=$source size=${allmessage.size}")
                }
            }
        }
    }

    private fun invalidateTransactionCaches() {
        transactionFilteredCache = null
        transactionSnippetValidityCache.clear()
    }

    private fun invalidateOffersCaches() {
        offersFilteredCache = null
        offersSnippetValidityCache.clear()
    }

    private fun getOrComputeTransactionMessages(source: List<Conversation>): ArrayList<Conversation> {
        transactionFilteredCache?.let { return ArrayList(it) }

        val safeSource = source.toList() // 💥 critical fix

        val filtered = ArrayList<Conversation>()
        safeSource.forEach { message ->
            val snippet = message.snippet
            if (snippet.isNotEmpty()) {
                val isValidTransaction = transactionSnippetValidityCache.getOrPut(snippet) {
                    snippet.checkSenderIsValid()
                }
                if (isValidTransaction) {
                    filtered.add(message)
                }
            }
        }

        transactionFilteredCache = ArrayList(filtered)
        return filtered
    }/*    private fun getOrComputeTransactionMessages(source: List<Conversation>): ArrayList<Conversation> {
            transactionFilteredCache?.let { return ArrayList(it) }

            val filtered = ArrayList<Conversation>()
            source.forEach { message ->
                val snippet = message.snippet
                if (snippet.isNotEmpty()) {
                    val isValidTransaction = transactionSnippetValidityCache.getOrPut(snippet) {
                        snippet.checkSenderIsValid()
                    }
                    if (isValidTransaction) {
                        filtered.add(message)
                    }
                }
            }

            transactionFilteredCache = ArrayList(filtered)
            return filtered
        }*/

    private fun getOrComputeOffersMessages(source: List<Conversation>): ArrayList<Conversation> {
        offersFilteredCache?.let { return ArrayList(it) }

        val filtered = ArrayList<Conversation>()

        val safeSource = ArrayList(source) // 👈 copy to avoid concurrent modification

        safeSource.forEach { message ->
            val snippet = message.snippet
            if (!message.title.isDigitsOnly() && !message.phoneNumber.isDigitsOnly() && snippet.isNotEmpty()) {
                val isOffer = offersSnippetValidityCache.getOrPut(snippet) {
                    snippet.isOfferMessage()
                }
                if (isOffer) {
                    filtered.add(message)
                }
            }
        }

        offersFilteredCache = ArrayList(filtered)
        return filtered
    }

    fun pinConversation(pin: Boolean) {
//        mProgressDialog.show()
        val conversations = selecteditemmainpin
        if (conversations.isEmpty()) {
            selectionRemove()
            return
        }
        if (conversations.size > 4) {
            if (!BaseSharedPreferences(this).mIS_SUBSCRIBED) {
                selectionRemove()
//                binding.mainMenu.binding.messagePremium.performClick()
                firebaseeventTXT = "Msg_purchased_home_pin"
                sendsubactivityPin()
            } else {
                toastMess("You can pin only 4 conversations. Please select up to 4.")
            }
            return
        }
        GlobalScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    if (selecteditemmainpin.isNotEmpty()) {
                        if (pin) {
                            config.addPinnedConversations(conversations)
                            conversations.forEach { conversation ->
                                conversation.threadId?.let { threadId ->
                                    repo.addpinConversationRepo(threadId)
                                }
                            }
                        } else {
                            config.removePinnedConversations(conversations)
                            conversations.forEach {
                                it.threadId?.let { it1 -> repo.removepinConversationRepo(it1) }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                try {
                    selectionRemove()
                    refreshPinnedConversationUi()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (!(isFinishing || isDestroyed)) {
//                    mProgressDialog.dismiss()
                }

            }
        }

    }

    fun pinConversationSwipe(pin: Boolean, selecteditemmainpin: ArrayList<Conversation>, pos: Int) {
        val conversations = selecteditemmainpin
        if (conversations.isEmpty()) {
            return
        }

        if (isPinSwipeInProgress) {
            resetSwipedRow(pos)
            return
        }

        isPinSwipeInProgress = true
        setHomeSwipeInteractionEnabled(false)

        GlobalScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    if (pin) {
                        config.addPinnedConversations(conversations)
                        conversations.forEach {
                            it.threadId?.let { it1 -> repo.addpinConversationRepo(it1) }
                        }
                    } else {
                        config.removePinnedConversations(conversations)
                        conversations.forEach {
                            it.threadId?.let { it1 -> repo.removepinConversationRepo(it1) }
                        }
                    }
                }
            }.onFailure {
                Log.e("HomeABActivity", "pinConversationSwipe failed", it)
            }
            withContext(Dispatchers.Main) {
                resetSwipedRow(pos)
                reattachitemswap()
                schedulePinSwipeRefreshFallback()
                setHomeSwipeInteractionEnabled(true)
                isPinSwipeInProgress = false
            }
        }
    }

    private fun setHomeSwipeInteractionEnabled(enabled: Boolean) {
        if (!::messageSwipeHelper.isInitialized) return
        messageSwipeHelper.setSwipeInteractionEnabled(enabled)

        val recyclerView = binding.allmassagerecycler
        if (!enabled) {
            recyclerView.itemAnimator?.endAnimations()
            if (recyclerView.scrollState != RecyclerView.SCROLL_STATE_IDLE) {
                recyclerView.stopScroll()
            }
            return
        }

        if (recyclerView.isAttachedToWindow && itemTouchHelper == null) {
            itemTouchHelper = ItemTouchHelper(messageSwipeHelper)
            itemTouchHelper?.attachToRecyclerView(recyclerView)
        }
    }

    private fun reattachitemswap() {
        if (!binding.allmassagerecycler.isAttachedToWindow) {
            return
        }
        if (itemTouchHelper == null) {
            itemTouchHelper = ItemTouchHelper(messageSwipeHelper)
        }
        itemTouchHelper?.attachToRecyclerView(binding.allmassagerecycler)

    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun isConversationPinned(conversation: Conversation?): Boolean {
        val threadId = conversation?.threadId ?: return false
        return config.pinnedConversations.contains(threadId.toString())
    }

    private fun sanitizeConversations(source: List<Conversation>): ArrayList<Conversation> {
        val safeConversations = ArrayList<Conversation>(source.size)
        @Suppress("UNCHECKED_CAST")
        for (conversation in source as List<Conversation?>) {
            if (conversation != null) {
                safeConversations.add(conversation)
            }
        }
        return safeConversations
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun setupconversation(it: List<Conversation>) {
        if (!hasCoreSmsPermissions()) {
            return
        }
        if (isfastcrollerishide) {
            binding.conversationsProgressBar.gone()
        }
        try {
            convolist2.clear()
            allmessage.clear()
            convolist2forpin.clear()
            invalidateTransactionCaches()
            invalidateOffersCaches()
//            var sortedConversations = it.sortedWith(compareByDescending<Conversation> { config.pinnedConversations.contains(it.threadId.toString()) }.thenByDescending { it.date }).toMutableList() as ArrayList<Conversation>
            val sortedConversations = sanitizeConversations(it)
            allmessage.addAll(sortedConversations)
            if (!isstarselected) {
                if (MessageType == "otp") {
                    isdefaultcatremove = false
                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            sortedConversations.forEachIndexed { index, conversation ->
                                if (conversation.messagetype == "otp") {
                                    convolist2.add(conversation)
                                }
                            }
                        }
                        withContext(Dispatchers.Main) {
                            var dataunread: ArrayList<Conversation> = arrayListOf()
                            if (filterdialogselectionmain == "unreadBtn") {
                                dataunread.clear()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        convolist2.toList().forEachIndexed { index, conversation ->
                                            if (conversation.isnewmessage == true) {
                                                dataunread.add(conversation)
                                            }
                                        }
                                    }
                                    withContext(Dispatchers.Main) {
                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))
                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else if (filterdialogselectionmain == "defaultBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                dataunread.addAll(convolist2)
                                CoroutineScope(Dispatchers.Main).launch {
                                    dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                        .sortedWith(compareByDescending<Conversation> {
                                            isConversationPinned(it)
                                        }.thenByDescending { it.date }))
                                    adapterMainMassage.updateList(dataunread)
                                }
                            } else if (filterdialogselectionmain == "todayBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        dataunread.addAll(filterTodayData(convolist2))
                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))

                                    }
                                    withContext(Dispatchers.Main) {
                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else if (filterdialogselectionmain == "monthBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        val monthandyear =
                                            extractMonthYearFromString(formateddatemain)
                                        if (monthandyear != null) {
                                            dataunread.addAll(
                                                filterDataByMonthYear(
                                                    convolist2,
                                                    monthandyear.first,
                                                    monthandyear.second
                                                )
                                            )
                                        } else {
                                            dataunread.addAll(convolist2)
                                        }

                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))

                                    }
                                    withContext(Dispatchers.Main) {
                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else if (filterdialogselectionmain == "yearBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        val monthandyear = extractYearFromString(formateddatemain)
                                        if (monthandyear != null) {
                                            dataunread.addAll(
                                                filterDataByYear(
                                                    convolist2, monthandyear
                                                )
                                            )
                                        } else {
                                            dataunread.addAll(convolist2)
                                        }
                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))
                                    }
                                    withContext(Dispatchers.Main) {
                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else if (filterdialogselectionmain == "dateRangeBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        val (firstFormattedDate, secondFormattedDate) = extractAndFormatDateRange(
                                            formateddatemain
                                        )
                                        if (firstFormattedDate != null && secondFormattedDate != null) {
                                            dataunread.addAll(
                                                filterDataByDateRange(
                                                    convolist2,
                                                    firstFormattedDate,
                                                    secondFormattedDate
                                                )
                                            )
                                        } else {
                                            dataunread.addAll(convolist2)
                                        }
                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))
                                    }
                                    withContext(Dispatchers.Main) {
                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else {
                                convolist2 = ArrayList(convolist2.distinctBy { it.threadId }
                                    .sortedWith(compareByDescending<Conversation> {
                                        isConversationPinned(it)
                                    }.thenByDescending { it.date }))
                                adapterMainMassage.updateList(convolist2)
                            }
                            convolist2forpin = convolist2
                        }
                    }
                } else if (MessageType == "Personal") {
                    isdefaultcatremove = false
                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            sortedConversations.forEachIndexed { index, conversation ->
                                if (!conversation.title.replace("+", "")
                                        .isDigitsOnly() && conversation.phoneNumber.replace("+", "")
                                        .isDigitsOnly()
                                ) {
                                    convolist2.add(conversation)
                                }
                            }
//                            convolist2 = ArrayList(convolist2.distinctBy { it.threadId })
                        }
                        withContext(Dispatchers.Main) {
                            var dataunread: ArrayList<Conversation> = arrayListOf()
                            if (filterdialogselectionmain == "unreadBtn") {
                                dataunread.clear()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        convolist2.toList().forEachIndexed { index, conversation ->
                                            if (conversation.isnewmessage == true) {
                                                dataunread.add(conversation)
                                            }
                                        }
                                    }
                                    withContext(Dispatchers.Main) {

                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))

                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else if (filterdialogselectionmain == "defaultBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                dataunread.addAll(convolist2)
                                CoroutineScope(Dispatchers.Main).launch {
                                    dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                        .sortedWith(compareByDescending<Conversation> {
                                            isConversationPinned(it)
                                        }.thenByDescending { it.date }))
                                    adapterMainMassage.updateList(dataunread)
                                }
                            } else if (filterdialogselectionmain == "todayBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        dataunread.addAll(filterTodayData(convolist2))
                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))

                                    }
                                    withContext(Dispatchers.Main) {
                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else if (filterdialogselectionmain == "monthBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        val monthandyear =
                                            extractMonthYearFromString(formateddatemain)
                                        if (monthandyear != null) {
                                            dataunread.addAll(
                                                filterDataByMonthYear(
                                                    convolist2,
                                                    monthandyear.first,
                                                    monthandyear.second
                                                )
                                            )
                                        } else {
                                            dataunread.addAll(convolist2)
                                        }

                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))

                                    }
                                    withContext(Dispatchers.Main) {
                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else if (filterdialogselectionmain == "yearBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        val monthandyear = extractYearFromString(formateddatemain)
                                        if (monthandyear != null) {
                                            dataunread.addAll(
                                                filterDataByYear(
                                                    convolist2, monthandyear
                                                )
                                            )
                                        } else {
                                            dataunread.addAll(convolist2)
                                        }
                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))
                                    }
                                    withContext(Dispatchers.Main) {
                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else if (filterdialogselectionmain == "dateRangeBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        val (firstFormattedDate, secondFormattedDate) = extractAndFormatDateRange(
                                            formateddatemain
                                        )
                                        if (firstFormattedDate != null && secondFormattedDate != null) {
                                            dataunread.addAll(
                                                filterDataByDateRange(
                                                    convolist2,
                                                    firstFormattedDate,
                                                    secondFormattedDate
                                                )
                                            )
                                        } else {
                                            dataunread.addAll(convolist2)
                                        }
                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))
                                    }
                                    withContext(Dispatchers.Main) {
                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else {

                                convolist2 = ArrayList(convolist2.distinctBy { it.threadId }
                                    .sortedWith(compareByDescending<Conversation> {
                                        isConversationPinned(it)
                                    }.thenByDescending { it.date }))

                                adapterMainMassage.updateList(convolist2)
                            }
                            convolist2forpin = convolist2
                        }
                    }
                } else if (MessageType == "Transaction") {
                    isdefaultcatremove = false
                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            convolist2.addAll(getOrComputeTransactionMessages(sortedConversations))
                        }
                        withContext(Dispatchers.Main) {
                            var dataunread: ArrayList<Conversation> = arrayListOf()
                            if (filterdialogselectionmain == "unreadBtn") {
                                dataunread.clear()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        convolist2.toList().forEachIndexed { index, conversation ->
                                            if (conversation.isnewmessage == true) {
                                                dataunread.add(conversation)
                                            }
                                        }
                                    }
                                    withContext(Dispatchers.Main) {

                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))

                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else if (filterdialogselectionmain == "defaultBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                dataunread.addAll(convolist2)
                                CoroutineScope(Dispatchers.Main).launch {
                                    dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                        .sortedWith(compareByDescending<Conversation> {
                                            isConversationPinned(it)
                                        }.thenByDescending { it.date }))
                                    adapterMainMassage.updateList(dataunread)
                                }
                            } else if (filterdialogselectionmain == "todayBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        dataunread.addAll(filterTodayData(convolist2))
                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))

                                    }
                                    withContext(Dispatchers.Main) {
                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else if (filterdialogselectionmain == "monthBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        val monthandyear =
                                            extractMonthYearFromString(formateddatemain)
                                        if (monthandyear != null) {
                                            dataunread.addAll(
                                                filterDataByMonthYear(
                                                    convolist2,
                                                    monthandyear.first,
                                                    monthandyear.second
                                                )
                                            )
                                        } else {
                                            dataunread.addAll(convolist2)
                                        }

                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))

                                    }
                                    withContext(Dispatchers.Main) {
                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else if (filterdialogselectionmain == "yearBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        val monthandyear = extractYearFromString(formateddatemain)
                                        if (monthandyear != null) {
                                            dataunread.addAll(
                                                filterDataByYear(
                                                    convolist2, monthandyear
                                                )
                                            )
                                        } else {
                                            dataunread.addAll(convolist2)
                                        }
                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))
                                    }
                                    withContext(Dispatchers.Main) {
                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else if (filterdialogselectionmain == "dateRangeBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        val (firstFormattedDate, secondFormattedDate) = extractAndFormatDateRange(
                                            formateddatemain
                                        )
                                        if (firstFormattedDate != null && secondFormattedDate != null) {
                                            dataunread.addAll(
                                                filterDataByDateRange(
                                                    convolist2,
                                                    firstFormattedDate,
                                                    secondFormattedDate
                                                )
                                            )
                                        } else {
                                            dataunread.addAll(convolist2)
                                        }
                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))
                                    }
                                    withContext(Dispatchers.Main) {
                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else {

                                convolist2 = ArrayList(convolist2.distinctBy { it.threadId }
                                    .sortedWith(compareByDescending<Conversation> {
                                        isConversationPinned(it)
                                    }.thenByDescending { it.date }))

                                adapterMainMassage.updateList(convolist2)
                            }
                            convolist2forpin = convolist2
                        }
                    }
                } else if (MessageType == "Offers") {
                    isdefaultcatremove = false
                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            convolist2.addAll(getOrComputeOffersMessages(sortedConversations))
//                            convolist2 = ArrayList(convolist2.distinctBy { it.threadId })
                        }
                        withContext(Dispatchers.Main) {
                            var dataunread: ArrayList<Conversation> = arrayListOf()
                            if (filterdialogselectionmain == "unreadBtn") {
                                dataunread.clear()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        convolist2.toList().forEachIndexed { index, conversation ->
                                            if (conversation.isnewmessage == true) {
                                                dataunread.add(conversation)
                                            }
                                        }
                                    }
                                    withContext(Dispatchers.Main) {

                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))

                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else if (filterdialogselectionmain == "defaultBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                dataunread.addAll(convolist2)
                                CoroutineScope(Dispatchers.Main).launch {
                                    dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                        .sortedWith(compareByDescending<Conversation> {
                                            isConversationPinned(it)
                                        }.thenByDescending { it.date }))
                                    adapterMainMassage.updateList(dataunread)
                                }
                            } else if (filterdialogselectionmain == "todayBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        dataunread.addAll(filterTodayData(convolist2))
                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))

                                    }
                                    withContext(Dispatchers.Main) {
                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else if (filterdialogselectionmain == "monthBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        val monthandyear =
                                            extractMonthYearFromString(formateddatemain)
                                        if (monthandyear != null) {
                                            dataunread.addAll(
                                                filterDataByMonthYear(
                                                    convolist2,
                                                    monthandyear.first,
                                                    monthandyear.second
                                                )
                                            )
                                        } else {
                                            dataunread.addAll(convolist2)
                                        }

                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))

                                    }
                                    withContext(Dispatchers.Main) {
                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else if (filterdialogselectionmain == "yearBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        val monthandyear = extractYearFromString(formateddatemain)
                                        if (monthandyear != null) {
                                            dataunread.addAll(
                                                filterDataByYear(
                                                    convolist2, monthandyear
                                                )
                                            )
                                        } else {
                                            dataunread.addAll(convolist2)
                                        }
                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))
                                    }
                                    withContext(Dispatchers.Main) {
                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else if (filterdialogselectionmain == "dateRangeBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        val (firstFormattedDate, secondFormattedDate) = extractAndFormatDateRange(
                                            formateddatemain
                                        )
                                        if (firstFormattedDate != null && secondFormattedDate != null) {
                                            dataunread.addAll(
                                                filterDataByDateRange(
                                                    convolist2,
                                                    firstFormattedDate,
                                                    secondFormattedDate
                                                )
                                            )
                                        } else {
                                            dataunread.addAll(convolist2)
                                        }
                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))
                                    }
                                    withContext(Dispatchers.Main) {
                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else {
                                convolist2 = ArrayList(convolist2.distinctBy { it.threadId }
                                    .sortedWith(compareByDescending<Conversation> {
                                        isConversationPinned(it)
                                    }.thenByDescending { it.date }))

                                adapterMainMassage.updateList(convolist2)
                            }
                            convolist2forpin = convolist2
                        }
                    }
                } else if (MessageType == "All Messages") {
                    isdefaultcatremove = false
//                    sortedConversations = it.distinctBy { it.threadId }.sortedWith(compareByDescending<Conversation> { config.pinnedConversations.contains(it.threadId.toString()) }.thenByDescending { it.date }).toMutableList() as ArrayList<Conversation>
                    convolist2 = sortedConversations
                    var dataunread: ArrayList<Conversation> = arrayListOf()
                    if (filterdialogselectionmain == "unreadBtn") {
                        dataunread.clear()
                        GlobalScope.launch {
                            withContext(Dispatchers.IO) {
                                convolist2.toList().forEachIndexed { index, conversation ->
                                    if (conversation.isnewmessage == true) {
                                        dataunread.add(conversation)
                                    }
                                }
                            }
                            withContext(Dispatchers.Main) {

                                dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                    .sortedWith(compareByDescending<Conversation> {
                                        isConversationPinned(it)
                                    }.thenByDescending { it.date }))

                                adapterMainMassage.updateList(dataunread)
                            }
                        }
                    } else if (filterdialogselectionmain == "defaultBtn") {
                        var dataunread: ArrayList<Conversation> = arrayListOf()
                        dataunread.addAll(convolist2)
                        CoroutineScope(Dispatchers.Main).launch {
                            dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                .sortedWith(compareByDescending<Conversation> {
                                    isConversationPinned(it)
                                }.thenByDescending { it.date }))
                            adapterMainMassage.updateList(dataunread)
                        }
                    } else if (filterdialogselectionmain == "todayBtn") {
                        var dataunread: ArrayList<Conversation> = arrayListOf()
                        GlobalScope.launch {
                            withContext(Dispatchers.IO) {
                                dataunread.addAll(filterTodayData(convolist2))
                                dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                    .sortedWith(compareByDescending<Conversation> {
                                        isConversationPinned(it)
                                    }.thenByDescending { it.date }))

                            }
                            withContext(Dispatchers.Main) {
                                adapterMainMassage.updateList(dataunread)
                            }
                        }
                    } else if (filterdialogselectionmain == "monthBtn") {
                        var dataunread: ArrayList<Conversation> = arrayListOf()
                        GlobalScope.launch {
                            withContext(Dispatchers.IO) {
                                val monthandyear = extractMonthYearFromString(formateddatemain)
                                if (monthandyear != null) {
                                    dataunread.addAll(
                                        filterDataByMonthYear(
                                            convolist2, monthandyear.first, monthandyear.second
                                        )
                                    )
                                } else {
                                    dataunread.addAll(convolist2)
                                }

                                dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                    .sortedWith(compareByDescending<Conversation> {
                                        isConversationPinned(it)
                                    }.thenByDescending { it.date }))

                            }
                            withContext(Dispatchers.Main) {
                                adapterMainMassage.updateList(dataunread)
                            }
                        }
                    } else if (filterdialogselectionmain == "yearBtn") {
                        var dataunread: ArrayList<Conversation> = arrayListOf()
                        GlobalScope.launch {
                            withContext(Dispatchers.IO) {
                                val monthandyear = extractYearFromString(formateddatemain)
                                if (monthandyear != null) {
                                    dataunread.addAll(
                                        filterDataByYear(
                                            convolist2, monthandyear
                                        )
                                    )
                                } else {
                                    dataunread.addAll(convolist2)
                                }
                                dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                    .sortedWith(compareByDescending<Conversation> {
                                        isConversationPinned(it)
                                    }.thenByDescending { it.date }))
                            }
                            withContext(Dispatchers.Main) {
                                adapterMainMassage.updateList(dataunread)
                            }
                        }
                    } else if (filterdialogselectionmain == "dateRangeBtn") {
                        var dataunread: ArrayList<Conversation> = arrayListOf()
                        GlobalScope.launch {
                            withContext(Dispatchers.IO) {
                                val (firstFormattedDate, secondFormattedDate) = extractAndFormatDateRange(
                                    formateddatemain
                                )
                                if (firstFormattedDate != null && secondFormattedDate != null) {
                                    dataunread.addAll(
                                        filterDataByDateRange(
                                            convolist2, firstFormattedDate, secondFormattedDate
                                        )
                                    )
                                } else {
                                    dataunread.addAll(convolist2)
                                }
                                dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                    .sortedWith(compareByDescending<Conversation> {
                                        isConversationPinned(it)
                                    }.thenByDescending { it.date }))
                            }
                            withContext(Dispatchers.Main) {
                                adapterMainMassage.updateList(dataunread)
                            }
                        }
                    } else {
                        convolist2 = ArrayList(convolist2.distinctBy { it.threadId }
                            .sortedWith(compareByDescending<Conversation> {
                                isConversationPinned(it)
                            }.thenByDescending { it.date }))

                        adapterMainMassage.updateList(convolist2)
                    }
                    convolist2forpin = sortedConversations
                } else {
                    isdefaultcatremove = true
                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            sortedConversations.forEachIndexed { index, conversation ->
                                if (conversation.CategoryName == MessageType) {
                                    Log.d("", "setupconversation: convolist2forpin <-----> ")
                                    convolist2.add(conversation)
                                }
                            }
//                            convolist2 = ArrayList(convolist2.distinctBy { it.threadId })
                        }
                        withContext(Dispatchers.Main) {
                            var dataunread: ArrayList<Conversation> = arrayListOf()
                            if (filterdialogselectionmain == "unreadBtn") {
                                dataunread.clear()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        convolist2.toList().forEachIndexed { index, conversation ->
                                            if (conversation.isnewmessage == true) {
                                                dataunread.add(conversation)
                                            }
                                        }
                                    }
                                    withContext(Dispatchers.Main) {

                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))

                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else if (filterdialogselectionmain == "defaultBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                dataunread.addAll(convolist2)
                                CoroutineScope(Dispatchers.Main).launch {
                                    dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                        .sortedWith(compareByDescending<Conversation> {
                                            isConversationPinned(it)
                                        }.thenByDescending { it.date }))
                                    adapterMainMassage.updateList(dataunread)
                                }
                            } else if (filterdialogselectionmain == "todayBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        dataunread.addAll(filterTodayData(convolist2))
                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))

                                    }
                                    withContext(Dispatchers.Main) {
                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else if (filterdialogselectionmain == "monthBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        val monthandyear =
                                            extractMonthYearFromString(formateddatemain)
                                        if (monthandyear != null) {
                                            dataunread.addAll(
                                                filterDataByMonthYear(
                                                    convolist2,
                                                    monthandyear.first,
                                                    monthandyear.second
                                                )
                                            )
                                        } else {
                                            dataunread.addAll(convolist2)
                                        }

                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))

                                    }
                                    withContext(Dispatchers.Main) {
                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else if (filterdialogselectionmain == "yearBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        val monthandyear = extractYearFromString(formateddatemain)
                                        if (monthandyear != null) {
                                            dataunread.addAll(
                                                filterDataByYear(
                                                    convolist2, monthandyear
                                                )
                                            )
                                        } else {
                                            dataunread.addAll(convolist2)
                                        }
                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))
                                    }
                                    withContext(Dispatchers.Main) {
                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else if (filterdialogselectionmain == "dateRangeBtn") {
                                var dataunread: ArrayList<Conversation> = arrayListOf()
                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        val (firstFormattedDate, secondFormattedDate) = extractAndFormatDateRange(
                                            formateddatemain
                                        )
                                        if (firstFormattedDate != null && secondFormattedDate != null) {
                                            dataunread.addAll(
                                                filterDataByDateRange(
                                                    convolist2,
                                                    firstFormattedDate,
                                                    secondFormattedDate
                                                )
                                            )
                                        } else {
                                            dataunread.addAll(convolist2)
                                        }
                                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))
                                    }
                                    withContext(Dispatchers.Main) {
                                        adapterMainMassage.updateList(dataunread)
                                    }
                                }
                            } else {

                                convolist2 = ArrayList(convolist2.distinctBy { it.threadId }
                                    .sortedWith(compareByDescending<Conversation> {
                                        isConversationPinned(it)
                                    }.thenByDescending { it.date }))

                                adapterMainMassage.updateList(convolist2)
                            }
                            convolist2forpin = convolist2

                        }
                    }
                }
            }
            if (it.isNotEmpty()) {
                Log.d("TAG", "onCreate: 4444 <-------->")
                setIsFistTimeMessageget(false, this)
            }
            logD(
                "refreshSmsInbox",
                "getIsFistTimeMessageget 3 ${getIsFistTimeMessageget(this@HomeABActivity)}"
            )
            if (!getIsFistTimeMessageget(this)) {
                if (!(isFinishing || isDestroyed)) {

                    mProgressDialog.dismiss()
                }
            }
        } catch (_: Exception) {
        }
        startProgress(1)
        Log.d("TAG", "startProgress: <-------->222  2")
    }

    private fun messagemessageArchive() {
        try {
            archiveContact()
        } catch (_: Exception) {
        }
        Snackbar.make(
            binding.root,
            "${selecteditemmain.size} " + resources.getString(R.string.conversation_archived),
            Snackbar.LENGTH_LONG
        ).setAction(resources.getString(R.string.Undo)) {
            Log.d("TAG", "Try to undo ticket")
            UnarchiveContact()

        }.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onDismissed(
                transientBottomBar: Snackbar?, event: Int,
            ) {
                super.onDismissed(transientBottomBar, event)
                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                    selectionRemove()
                }
            }
        }).show()

    }

    private fun archiveContact() {
        if (selecteditemmain.isEmpty()) {
            selectionRemove()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                config.removePinnedConversations(selecteditemmainpin)
                repo.archivConversationRepo(selecteditemmain)
//                selecteditemmain.forEachIndexed { index, conversation ->
//                    conversation.threadId?.let {
//                        repo.archivConversationRepo(it)
//                    }
//                }
                CoroutineScope(Dispatchers.Main).launch {
                    binding.mainMenu.binding.isselection = false
                    adapterMainMassage.notifyDataSetChanged()
                }
            }
        }
    }

    private fun UnarchiveContact() {
        if (selecteditemmain.isEmpty()) {
            selectionRemove()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                repo.removearchivConversationRepo(selecteditemmain)
//                selecteditemmain.forEachIndexed { index, conversation ->
//                    conversation.let {
//                    }
//                }
                CoroutineScope(Dispatchers.Main).launch {
                    selectionRemove()
                }
            }
        }
    }

    private fun deleteMessage() {
        val selectedThreadIds = selecteditemmainpin.mapNotNull { it.threadId }.ifEmpty {
            selecteditemmain.mapNotNull { it.toLongOrNull() }
        }.distinct()

        if (selectedThreadIds.isEmpty()) {
            selectionRemove()
            return
        }

        val sizeofmessages = selectedThreadIds.size
        if (!(isFinishing || isDestroyed)) {
            if (selectedThreadIds.size > 2) {
                mProgressDialog.show()
            }

        }

        CoroutineScope(Dispatchers.IO).launch {
            repo.moveConversationsToRecycleBin(selectedThreadIds)
            selectedThreadIds.forEach { threadId ->
                runCatching { deleteConversation(threadId) }
            }

            CoroutineScope(Dispatchers.Main).launch {
                if (!(isFinishing || isDestroyed)) {
                    if (mProgressDialog.isShowing) {
                        mProgressDialog.dismiss()
                    }

                }
                Snackbar.make(
                    binding.root,
                    "${selectedThreadIds.size} " + resources.getString(R.string.conversation_deleted),
                    Snackbar.LENGTH_LONG
                ).show()
                selectionRemove()
                reattachitemswap()

                if (sizeofmessages > 600) {
                    if (!(isFinishing || isDestroyed)) {
//                        onResumeCopyDub()
                        recreate()
//                    reloadHomeListFromDatabaseIfNeeded(16)
                    }
                }

            }
        }
    }

    private fun selectionRemove() {

        binding.mainMenu.binding.isselection = false
        binding.mainMenu.binding.selectioncount.text = resources.getString(R.string.selected) + " 0"
        selecteditemmain.clear()
        selecteditemmainpin.clear()
        adapterMainMassage.selecteditem.clear()

        Log.d("jigar", "onMainClick: mainclcik <----> 4 ${adapterMainMassage.selecteditem.size}")
        adapterMainMassage.notifyDataSetChanged()
    }


    override fun registerPurchases(
        isSubscribe: Boolean,
        paymentState: Int,
        sku: String,
        orderId: String,
    ) {
//        if (isappfistopen) {
//            isappfistopen = false
//        }
//        Log.d(TAG, "registerPurchases: registerPurchases <------------> ${isSubscribe}")
//        BaseSharedPreferences(this).mIS_SUBSCRIBED = isSubscribe
    }


    private fun setMainNoMessageState(visible1: Boolean) {
        mainNoMessageVisibleState = visible1
        binding.nomessagefoundchack = visible1
//        binding.nomessagefound.isVisible = visible1
    }


    private fun setStarAdapter() {
        convolist.clear()
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                liststar.forEach {
                    convolist.add(
                        Conversation(
                            0,
                            it.date,
                            it.read,
                            it.title,
                            it.photoUri,
                            it.usesCustomTitle,
                            it.phoneNumber,
                            it.snippet,
                            it.time,
                            it.type,
                            it.isnumaric,
                            null
                        )
                    )
                }

            }

            withContext(Dispatchers.Main) {

                adapterMainMassage.updateList(convolist)


            }

        }

    }

    override fun onClick(
        tredid: Long?,
        pos: Int,
        title: String,
        phoneNumber: String,
        holder: MainMassageAdapter.MainMassageAdapterViewHolder,
        list: ArrayList<Conversation>,
        position: Int,
    ) {

        hideKeyboard()


        openContactScreen(tredid, title, phoneNumber, holder.itemView, list, position)


    }

    fun openContactScreen(
        tredid1: Long?,
        title1: String,
        phoneNumber1: String,
        itemView1: View,
        list1: ArrayList<Conversation>,
        position1: Int
    ) {
        if (ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(State.RESUMED)) {

            Constants.isActivitychange = true
            onMessageClick = true

            // ✅ ALWAYS launch on Main
            lifecycleScope.launch(Dispatchers.Main) {

                openActivityFast(
                    tredid1, title1, phoneNumber1, itemView1, list1, position1
                )

                // 🚀 Background work AFTER navigation
                if (tredid1 != null) {
                    launch(Dispatchers.IO) {
                        markThreadAsRead(tredid1)
                    }
                }
            }
        }
    }

    fun openActivityFast(
        tredid: Long?,
        title: String,
        phoneNumber: String,
        itemView: View,
        list: ArrayList<Conversation>,
        position: Int,
    ) {
        try {
            val intent = Intent(this, SendMessageActivity::class.java).apply {
                putExtra("tredid", tredid)
                putExtra("name", title)
                putExtra("mobileNumber", phoneNumber)
                putExtra("isgroupmessage", list[position].isgroupmessage)
            }
            startActivity(intent)

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Set fast exit transition for CURRENT activity (list fades out)
                            val options = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions.makeSceneTransitionAnimation(
                    this,
                    itemView,
                    "shared_element_container"
                )
            } else null
            startActivity(intent, options?.toBundle())
            } else {
                startActivity(intent)
            }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //-------------
    /* fun openContactScreen(
         tredid1: Long?,
         title1: String,
         phoneNumber1: String,
         itemView1: View,
         list1: ArrayList<Conversation>,
         position1: Int
     ) {
         if (ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(State.RESUMED)) {
             Constants.isActivitychange = true
             onMessageClick = true
             CoroutineScope(Dispatchers.IO).launch {
                 Log.e("FFF", "openContactScreen: ")

                 sendMessagerActivity(
                     tredid1, title1, phoneNumber1, itemView1, list1, position1
                 )
             }
         }
     }


     suspend fun sendMessagerActivity(
         tredid: Long?,
         title: String,
         phoneNumber: String,
         itemView: View,
         list: ArrayList<Conversation>,
         position: Int,
     ) {
         Alredyclick = false
         if (tredid != null) {
             markThreadAsRead(tredid)
         }
         withContext(Dispatchers.Main.immediate) {
             try {

                 val options = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                     ActivityOptions.makeSceneTransitionAnimation(
                         this@HomeABActivity,
                         itemView,                        // the view that should morph
                         "shared_element_container"       // must match in SendMessageActivity
                     )
                 } else null
                 val intent = Intent(this@HomeABActivity, SendMessageActivity::class.java).apply {
                     putExtra("tredid", tredid)
                     putExtra("name", title)
                     putExtra("mobileNumber", phoneNumber)
                     putExtra("isgroupmessage", list[position].isgroupmessage)
                 }



 //                startActivity(intent)
                 startActivity(intent, options?.toBundle())
             } catch (e: Exception) {
                 e.printStackTrace()

             }


             Constants.isActivitychange = true
         }
     }
 */
    override fun onClickMenu(
        position: Int,
        list: ArrayList<Conversation>,
        holder: MainMassageAdapter.MainMassageAdapterViewHolder,
    ) {

        AddtoStar(position, list)

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun AddtoStar(position: Int, list: ArrayList<Conversation>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (list[position].phoneNumber?.let { repo.isStarNumberSelectedRepo(it) } == true) {
                list[position].phoneNumber?.let { repo.removeNumberToStarRepo(it) }
                runOnUiThread { adapterMainMassage.notifyDataSetChanged() }
            } else {
                with(list[position]) {
                    star = StarNumber(
                        0,
                        date!!,
                        read,
                        title!!,
                        photoUri,
                        usesCustomTitle,
                        phoneNumber!!,
                        snippet!!,
                        time,
                        type,
                        isnumaric
                    )
                }
                repo.addNumberToStarRepo(star)
                runOnUiThread { adapterMainMassage.notifyDataSetChanged() }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        onbackbress123 = false
        systemfountstartsetting = false
        if (baseConfig.onboardingHomeAgainPending) {
//            if (!isFLow2) {
//            if (isFLow2_3) {
            logOnboardingFunnelStep("First_home_again")
            baseConfig.onboardingHomeAgainPending = false
//            }
        }

//        loadBanner()
//        adsLoadAndShowTopNativeBanner()
        if (!isSyncInProgress && (adapterMainMassage.list.isNotEmpty() || convolist2.isNotEmpty())) {
            setSyncUiVisible(false, 1)
            logD("FFF", "RESUME___  1")

        }
        if (!Settings.canDrawOverlays(this)) {
            updateSmartCallerBannerVisibility(true, 3)
        }

        try {
            logD("FFF", "RESUME___  2")


            if (binding.defaultlayer.isVisible) {
                logD("FFF", "RESUME___  3")

                val isDefaultSms = roleDefaultCheck()
                val hasSmsPermissions = hasCoreSmsPermissions()
                isdefault = isDefaultSms
                logD("FFF", "$hasSmsPermissions")


                if (!hasSmsPermissions) {
                    logD("FFF", "RESUME___  31")

                    if (!isDefaultSms) {
                        permissionlayershow(true, 5)
                        binding.messageloadinglottie.gone()
                        binding.messageloadinglottie2.gone()
                        binding.messageper.gone()
                        binding.messageperper.gone()
                        setMainNoMessageState(false)
                        logD("FFF", "RESUME___  32")
                    } else {
                        permissionlayershow(true, 6)
                        updateDefaultSmsBannerVisibility()
                        binding.messageloadinglottie.gone()
                        binding.messageloadinglottie2.gone()
                        binding.messageper.gone()
                        binding.messageperper.gone()
                        setMainNoMessageState(false)
                        logD("FFF", "RESUME___  33")

//                        if (isFLow2_3) {
//                            loadMessages(false,71)
//                        } else {
//                            loadMessages(true, 61)
//                        }
                    }
                } else {
                    logD("FFF", "RESUME___  4")

                    updateDefaultSmsBannerVisibilitybyFlow2()
                    binding.messageloadinglottie.gone()
                    binding.messageloadinglottie2.gone()
                    binding.messageper.gone()
                    binding.messageperper.gone()
                    permissionlayershow(false, 7)
                    if (shouldReloadHomeDataOnResume()) {
                        if (isFLow1_2) {
                            if (isDefaultSms) {
                                loadMessages(true, 5)
                            }
                        } else {
                            loadMessages(true, 4)
                        }
                    } else {
                        restoreHomeSearchResultsOnResume()
                    }
                }
            }
//            visibleSerchviewonstartcreate()

        } catch (e: Exception) {

        }

        refreshHomeDrafts()
        Log.d("TAG", "onResume: onResume  <----------- 1234 ")
        com.messenger.phone.number.text.sms.service.apps.adsnew.Constants.isAdsClicking = false
        inAppUpdate.onResume()
        // FIXED ANR #7: Move shortcut creation to background thread
        lifecycleScope.launch(Dispatchers.IO) {
            checkShortcut()
        }
        updateDefaultSmsBannerVisibility()
        setStatusbar()
        renderCategoryChips()
        restoreHomeListAfterThemeRefresh(1)
    }

    fun onResumeCopyDub() {


        if (!isSyncInProgress && (adapterMainMassage.list.isNotEmpty() || convolist2.isNotEmpty())) {
            setSyncUiVisible(false, 1)
            logD("FFF", "RESUME_Copy___  1")

        }
        if (!Settings.canDrawOverlays(this)) {
            updateSmartCallerBannerVisibility(true, 3)
        }

        try {
            logD("FFF", "RESUME_Copy___  2")


            if (binding.defaultlayer.isVisible) {
                logD("FFF", "RESUME_Copy___  3")

                val isDefaultSms = roleDefaultCheck()
                val hasSmsPermissions = hasCoreSmsPermissions()
                isdefault = isDefaultSms
                logD("FFF", "$hasSmsPermissions")
                if (!hasSmsPermissions) {
                    logD("FFF", "RESUME_Copy___  31")

                    if (!isDefaultSms) {
                        permissionlayershow(true, 5)
                        binding.messageloadinglottie.gone()
                        binding.messageloadinglottie2.gone()
                        binding.messageper.gone()
                        binding.messageperper.gone()
                        setMainNoMessageState(false)
                        logD("FFF", "RESUME_Copy___  32")
                    } else {
                        permissionlayershow(true, 6)
                        updateDefaultSmsBannerVisibility()
                        binding.messageloadinglottie.gone()
                        binding.messageloadinglottie2.gone()
                        binding.messageper.gone()
                        binding.messageperper.gone()
                        setMainNoMessageState(false)
                        logD("FFF", "RESUME_Copy___  33")

//                        if (isFLow2_3) {
//                            loadMessages(false,71)
//                        } else {
//                            loadMessages(true, 61)
//                        }
                    }
                } else {
                    logD("FFF", "RESUME_Copy___  4")

                    updateDefaultSmsBannerVisibilitybyFlow2()
                    binding.messageloadinglottie.gone()
                    binding.messageloadinglottie2.gone()
                    binding.messageper.gone()
                    binding.messageperper.gone()
                    permissionlayershow(false, 7)
                    if (isFLow1_2) {
                        if (isDefaultSms) {
                            loadMessages(true, 5)
                        }
                    } else {
                        loadMessages(true, 4)
                    }
                }
            }
//            visibleSerchviewonstartcreate()

        } catch (e: Exception) {

        }

        refreshHomeDrafts()
        Log.d("TAG", "onResume: onResume  <----------- 1234 ")
        com.messenger.phone.number.text.sms.service.apps.adsnew.Constants.isAdsClicking = false
        inAppUpdate.onResume()
        checkShortcut()
        updateDefaultSmsBannerVisibility()
        setStatusbar()
        renderCategoryChips()
    }

    private fun refreshHomeDrafts() {
        if (!::adapterMainMassage.isInitialized) return
        adapterMainMassage.updateDrafts(this)
        draftRefreshHandler.removeCallbacks(draftRefreshRunnable)
        draftRefreshHandler.postDelayed(draftRefreshRunnable, 300L)
    }

    private fun refreshHomeSwipe() {
        if (!::adapterMainMassage.isInitialized) return
        val recyclerView = binding.allmassagerecycler
        val refreshAction = Runnable {
            if (!::adapterMainMassage.isInitialized) return@Runnable
            if (::messageSwipeHelper.isInitialized && messageSwipeHelper.hasPendingSwipeState()) {
                recyclerView.postDelayed({ refreshHomeSwipe() }, 80L)
                return@Runnable
            }
            adapterMainMassage.updateviewForSwipe(this)
        }
        if (!recyclerView.isAttachedToWindow) return
        if (recyclerView.isComputingLayout || recyclerView.scrollState != RecyclerView.SCROLL_STATE_IDLE) {
            recyclerView.post(refreshAction)
        } else {
            refreshAction.run()
        }
        draftRefreshHandler.removeCallbacks(draftRefreshRunnable)
        draftRefreshHandler.postDelayed(draftRefreshRunnable, 300L)
    }

    private fun refreshPinnedConversationUi() {
        if (isFinishing || isDestroyed || !::adapterMainMassage.isInitialized) return

        if (allmessage.isNotEmpty()) {
            setupconversation(ArrayList(allmessage))
            if (::messageSwipeHelper.isInitialized) {
                reattachitemswap()
            }
            return
        }

        reloadHomeListFromDatabaseIfNeeded(31)
    }

    private fun schedulePinSwipeRefreshFallback() {
        if (!binding.allmassagerecycler.isAttachedToWindow) return
        pinSwipeRefreshCounter++
        if (pinSwipeRefreshCounter < pinSwipeRefreshThreshold) return

        pinSwipeRefreshCounter = 0
        draftRefreshHandler.removeCallbacks(pinSwipeRefreshRunnable)
        draftRefreshHandler.postDelayed(pinSwipeRefreshRunnable, 120L)
    }

    fun is24HoursPassedAndReset(context: Context): Boolean {
        val prefs = context.getSharedPreferences("event_prefs", Context.MODE_PRIVATE)
        val key = "last_event_time"

        val now = System.currentTimeMillis()
        val lastTime = prefs.getLong(key, 0L)

        val isPassed = lastTime != 0L && now - lastTime >= 24 * 60 * 60 * 1000L

        if (isPassed) {
            prefs.edit().putLong(key, now).apply() // reset
        } else if (lastTime == 0L) {
            prefs.edit().putLong(key, now).apply() // first time
        }

        return isPassed
    }

    fun isFirstTimeToday(context: Context): Boolean {
        val prefs = context.getSharedPreferences("daily_check", Context.MODE_PRIVATE)

        val today = LocalDate.now().toString() // yyyy-MM-dd
        val lastDate = prefs.getString("last_date", null)

        return if (lastDate != today) {
            prefs.edit().putString("last_date", today).apply()
            true
        } else {
            false
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (binding.icDrawer.isDrawerOpen(GravityCompat.START)) {
            binding.icDrawer.closeDrawer(GravityCompat.START)
        } else {


//            val searchView = binding.mainMenu.binding.homeSearchView
//            val currentSearchText = getHomeSearchInput(searchView)?.text?.toString().orEmpty().trim()
//            if (currentSearchText.isNotEmpty()) {
//                searchView.setQuery("", false)
//                filterHomeConversations("")
//                return
//            }
            if (binding.mainMenu.binding.selectioncontenar.isVisible()) {
                selectionRemove()
            } else if (binding.mainMenu.binding.homeSearchInput.text?.trim().toString()
                    .isNotEmpty()
            ) {
                binding.mainMenu.binding.homeSearchInput.setText("")
            } else {

                if (baseConfig.isReviewSubmitted || !baseConfig.isFeedBackOptionShow) {
                    if (config.dialogshowcount == 3) {
                        if (config.ratingdialogshow) {
                            starrationgdialog()
                        } else {
                            config.dialogshowcount += 1
                            finishAffinity()
                        }
                    } else {
                        config.dialogshowcount += 1
                        finishAffinity()
                    }
                    return
                }

//                val shouldShowReview = baseConfig.incrementReviewPromptCount() % 2 == 1
                val shouldShowReview = /*if (isOnline) is24HoursPassedAndReset(this) else*/ false
                if (shouldShowReview) {
                    ReviewDialogManager(this, this) {
                        config.dialogshowcount += 1
                        finishAffinity()
                    }.show()
                } else {
                    if (config.dialogshowcount == 3) {
                        if (config.ratingdialogshow) {
                            starrationgdialog()
                        } else {
                            config.dialogshowcount += 1
                            finishAffinity()
                        }
                    } else {
                        config.dialogshowcount += 1
                        finishAffinity()
                    }
                }

            }
        }
    }


    fun removeAppFromRecents(context: Context) {
        isfisttimeadopen = false
        val activityManager: ActivityManager =
            context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (task in activityManager.getAppTasks()) {
            task.finishAndRemoveTask()
        }
    }

    @SuppressLint("NewApi")
    private fun checkShortcut() {
        if (isNougatMR1Plus()) {
            val newConversation = getCreateNewContactShortcut()
            val serchConversation = getCreatesechContactShortcut()

            val manager = getSystemService(ShortcutManager::class.java)
            try {
                manager.dynamicShortcuts = listOf(newConversation, serchConversation)
            } catch (ignored: Exception) {
            }
        }
    }

    @SuppressLint("NewApi")
    private fun getCreateNewContactShortcut(): ShortcutInfo {
        val newEvent = getString(R.string.new_conversation_ms)
        val drawable = resources.getDrawable(R.drawable.shortcut_plus)
        (drawable as LayerDrawable).findDrawableByLayerId(R.id.shortcut_plus_background)
        val bmp = drawable.convertToBitmap()

        val intent = Intent(this, SelectContactActivity::class.java)
        intent.action = Intent.ACTION_VIEW
        return ShortcutInfo.Builder(this, "new_conversation_ms").setShortLabel(newEvent)
            .setLongLabel(newEvent).setIcon(Icon.createWithBitmap(bmp)).setIntent(intent).build()
    }

    @SuppressLint("NewApi")
    private fun getCreatesechContactShortcut(): ShortcutInfo {
        val newEvent = getString(R.string.new_search_ms)
        val drawable = resources.getDrawable(R.drawable.shortcut_search)
        (drawable as LayerDrawable).findDrawableByLayerId(R.id.shortcut_plus_background)
        val bmp = drawable.convertToBitmap()

        val intent = Intent(this, SearchActivity::class.java)
        intent.action = Intent.ACTION_VIEW
        return ShortcutInfo.Builder(this, "new_Search_ms").setShortLabel(newEvent)
            .setLongLabel(newEvent).setIcon(Icon.createWithBitmap(bmp)).setIntent(intent).build()
    }

    override fun onStart() {
        super.onStart()
//        gooutside = false


        if (systemfountstart) {
            recreate()
        }
        if (sendmessagebuttondefaultset) {
            sendmessagebuttondefaultset = false
            recreate()
        }

        firebaseSetup()
        settextsize()
        themeSetup()
        restoreHomeListAfterThemeRefresh(2)
        binding.messageper.text = resources.getString(R.string.Messages_Loading)
//        Log.e("AdsGenNew__", "onStart: homeee", )
        if (BaseSharedPreferences(this).mIS_SUBSCRIBED) {
            binding.mainMenu.binding.messagePremium.gone()
            binding.mainMenu.binding.qurekaaa.gone()
            binding.constAdViewBottom.gone()
            binding.constnativeBannerAdView.gone()
        } else {
            binding.mainMenu.binding.messagePremium.visible()
            binding.mainMenu.binding.qurekaaa.gone()
//            if (!isOnline()) {
//                binding.constAdViewBottom.gone()
//                binding.constnativeBannerAdView.gone()
//            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            if (messagerDatabaseRepo.isEmpty()) {
                runOnUiThread { binding.conversationsProgressBar.gone() }
            }
        }
        setStatusbar()


        setupSwipemotion()
        CoroutineScope(Dispatchers.IO).launch {
            AutoNotificationStart()
        }
        if (Constants.packagerenlist.isNotEmpty()) {
            if (Constants.packagerenlist[0].freeTrialPeriod != null) {
                if (!config.usertrailscreenshow) {
                    config.usertrailscreenshow = true
                    when (BaseSharedPreferences(this).isCurrentPlan) {
                        "Current Offering" -> {
                            config.userfisttimeshowsubscreen = false
                        }

                        "Experiment Offering" -> {
                            config.userfisttimeshowsubscreen = false
                        }

                        else -> {
                            config.userfisttimeshowsubscreen = false
                        }
                    }
                } else {
                    if (config.userfisttimeshowsubscreen) {
                        config.usertrailscreenshow = true
                    }
                }
            } else {
            }
        }

        oldflowadsload()
        if (!isSplashadsDone) {
            isSplashadsDone = true
            isSplash = true
        } else {
            isSplash = false
        }
        if (BaseSharedPreferences(this).mIS_SUBSCRIBED!!) {
            binding.loadadAbcotenar.gone()
            binding.constAdViewBottom.gone()
            binding.adload.gone()

            binding.constnativeBannerAdView.gone()

        }

        if (BaseSharedPreferences(this).mIS_SUBSCRIBED!!) {
            binding.offerTimeContenar.gone()
        } else {
            if (!offerscreenshow) {
                binding.offerTimeContenar.gone()
            } else {
                if (!GlobalTimer.shouldStartGlobal) {
                    binding.offerTimeContenar.gone()
                } else {
                    when (BaseSharedPreferences(this).isCurrentPlan) {
                        "Current Offering" -> {
                            binding.offerTimeContenar.gone()
                        }

                        "Experiment Offering" -> {
//                            binding.offerTimeContenar.visible()
                            binding.offerTimeContenar.gone()
                        }

                        else -> {
                            binding.offerTimeContenar.gone()
                        }
                    }
                }
            }
        }

        if (config.SelectedLanguage == "ar") {
            binding.textView17.gravity = Gravity.END
            binding.textView18.gravity = Gravity.END
        } else {
            binding.textView17.gravity = Gravity.START
            binding.textView18.gravity = Gravity.START
        }
//        if (CallerModule.hasPhonePermission() && CallerModule.hasOverlayPermission()) {
//            if (CallerModule.startMonitoring()) {
//                "phone state and overlay permission was granted, starting monitoring".log()
//            }
//        }
    }


    private fun oldflowadsload() {

        if (config.FirstTimeovarlaypermissionshow) {
            if (subScreenopen) {
                subScreenopen = false
                config.FirstTimeovarlaypermissionshow = false
//                showPermissiondialog()
            }
        }

        if (Settings.canDrawOverlays(this)) {
            if (!config.FirstTimeovarlaypermissionalow) {
                config.FirstTimeovarlaypermissionalow = true
//                firebaseFunnel("Message_First_overlay_allow")
                "config.firsttimelangEvent <---------------------> Message_first_overlay_allow".log()
            }
            updateSmartCallerBannerVisibility(false, 5)
        } else {
//            updateSmartCallerBannerVisibility(false, 6)
        }

//        binding.smartCaller.shimCallTop.setOnClickListener {
//            gooutside = true
//            takeOverlayPermission(false, "_top")
//
//        }
//        binding.smartCallerBottom.shimCallBottom.setOnClickListener {
//            gooutside = true
//            takeOverlayPermission(false, "_bottom")
//        }
    }

    private fun updateSmartCallerBannerVisibility(show: Boolean, i: Int) {
        "updateSmartCallerBannerVisibility <-----------------------> $show  $i".log()
        val showTop = show && config.home_overlay_banner == "1"
        val showBottom = show && config.home_overlay_banner == "2"
//        if (showTop) {
//            binding.smartCaller.shimCallTop.visible()
//        } else {
//            binding.smartCaller.shimCallTop.gone()
//        }
//        if (showBottom) {
//            binding.smartCallerBottom.shimCallBottom.visible()
//        } else {
//            binding.smartCallerBottom.shimCallBottom.gone()
//        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setupSwipemotion() {

        val displayMetrics: DisplayMetrics = resources.displayMetrics
        val height = (displayMetrics.heightPixels / displayMetrics.density).toInt().dp
        val width = (displayMetrics.widthPixels / displayMetrics.density).toInt().dp
        messageSwipeHelper = MessageSwipeHelper(
            this,
            height,
            width,
            getcurruntseletedLeftcolor(),
            getcurruntseletedRightcolor(),
            getcurruntseletedLeftsrc(),
            getcurruntseletedRightsrc(),
            this
        )
        if (itemTouchHelper != null) {
            itemTouchHelper!!.attachToRecyclerView(null)
        }
        itemTouchHelper = ItemTouchHelper(messageSwipeHelper)
        itemTouchHelper?.attachToRecyclerView(binding.allmassagerecycler)

        if (islinechange) {
            islinechange = false
            adapterMainMassage.notifyDataSetChanged()
        }

    }

    private fun blendWithBaseColor(primaryColor: Int, baseColor: Int, ratio: Float): Int {
        return ColorUtils.blendARGB(baseColor, primaryColor, ratio.coerceIn(0f, 1f))
    }

    fun adjustAlpha11(@ColorInt color: Int, factor: Float): Int {
        val alpha = (android.graphics.Color.alpha(color) * factor).toInt()
        val red = android.graphics.Color.red(color)
        val green = android.graphics.Color.green(color)
        val blue = android.graphics.Color.blue(color)
        return android.graphics.Color.argb(alpha, red, green, blue)
    }

    private fun setStatusbar() {
        val backgroundColor = getProperBackgroundColor()
        val primaryColor = getProperPrimaryColor()
        val chipDetailedBackground = blendWithBaseColor(primaryColor, backgroundColor, 0.08f)
        val chipDetailedBackground1 = blendWithBaseColor(primaryColor, backgroundColor, 0.12f)

        binding.vAnd15StatusBar.setBackgroundColor(backgroundColor)
        binding.main.setBackgroundColor(backgroundColor)
        binding.mainCoordinator.setBackgroundColor(backgroundColor)
        binding.allmassagerecycler.setBackgroundColor(backgroundColor)
        binding.mainMenu.binding.topAppBarLayout.setBackgroundColor(backgroundColor)
        binding.mainMenu.binding.topToolbarHolder.setBackgroundColor(backgroundColor)
        binding.mainMenu.binding.searchBarCard.setCardBackgroundColor(
            blendWithBaseColor(getProperPrimaryColor(), backgroundColor, 0.06f)
        )
        //        binding.mainMenu.binding.defaulttoolshow.setBackgroundColor(backgroundColor)
        binding.mainMenu.binding.lnrTopHomeNewSearchArea.isClickable = true
        binding.mainMenu.binding.lnrTopHomeNewSearchArea.isFocusable = true
        binding.mainMenu.binding.selectioncontenar.setBackgroundColor(backgroundColor)

        binding.defaultlayer.setBackgroundColor(backgroundColor)



        applyTabChipStyle()


        val useLightSystemBars = ThemeModeManager.shouldUseLightSystemBars(this)
        if (useLightSystemBars) {
            binding.mainMenu.binding.icDrawerAbButton.setImageResource(R.drawable.home_material_ic_menu_rounded_dark)
            binding.mainMenu.binding.searchMessageBg.setImageResource(R.drawable.home_material_ic_search_rounded_dark)
            binding.mainMenu.binding.moreButton.setImageResource(R.drawable.home_material_ic_more_vert_rounded_dark)
            binding.mainMenu.binding.filterMessageBg.setImageResource(R.drawable.home_material_ic_filter_alt_rounded_dark)
            binding.mainMenu.binding.moreButtonMark.setImageResource(R.drawable.home_material_ic_more_vert_rounded_dark)
            binding.mainMenu.binding.messagePremium.setImageResource(R.drawable.ic_message_premium_btn)
//            binding.mainMenu.binding.qurekaaa.setImageResource(R.drawable.home_material_ic_workspace_premium_rounded)
            binding.mainMenu.binding.homeSearchInput.setTextColor(resources.getColor(R.color.black2))
            binding.mainMenu.binding.homeSearchInput.setHintTextColor(Color.parseColor("#8C8C99"))
            binding.mainMenu.binding.selectioncount.setTextColor(resources.getColor(R.color.black2))
        } else {
            binding.mainMenu.binding.icDrawerAbButton.setImageResource(R.drawable.home_material_ic_menu_rounded_light)
            binding.mainMenu.binding.searchMessageBg.setImageResource(R.drawable.home_material_ic_search_rounded_light)
            binding.mainMenu.binding.moreButton.setImageResource(R.drawable.home_material_ic_more_vert_rounded_light)
            binding.mainMenu.binding.filterMessageBg.setImageResource(R.drawable.home_material_ic_filter_alt_rounded_light)
            binding.mainMenu.binding.moreButtonMark.setImageResource(R.drawable.home_material_ic_more_vert_rounded_light)
            binding.mainMenu.binding.messagePremium.setImageResource(R.drawable.ic_message_premium_btn)
//            binding.mainMenu.binding.qurekaaa.setImageResource(R.drawable.home_material_ic_workspace_premium_rounded)
            binding.mainMenu.binding.homeSearchInput.setTextColor(resources.getColor(R.color.white))
            binding.mainMenu.binding.homeSearchInput.setHintTextColor(Color.parseColor("#B3FFFFFF"))
            binding.mainMenu.binding.selectioncount.setTextColor(resources.getColor(R.color.white))
        }

        window.statusBarColor = backgroundColor
        window.navigationBarColor = backgroundColor
        window.decorView.setBackgroundColor(backgroundColor)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }

        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
    }

    private fun setDefaultApp() {

        when {
            !roleDefaultCheck() -> {

                Log.d("TAG", "setDefaultApp: <------------> chack")
                try {
                    if (isFLow1_2) {
//                        if (baseConfig.secondtimeflowuseFLow || baseConfig.secondtimeflowuse) {
//                            loadMessages(false, 3)
//                        }
                    } else {
                        loadMessages(false, 2)
                    }
                } catch (e: Exception) {

                }
            }
        }

    }


    private fun getSmsPermissions(): List<String> {
        val permissions = mutableListOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE
        )
//        if (isTiramisuPlus()) {
//            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
//        }
        return permissions
    }

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
            logE("permissionlayershow", "hasCoreSmsPermissions: $perm $s")
            s
        }
    }

    private fun continueAfterSmsPermissions() {
        openAdOnO(true, 1200)
        startSyncIfNeeded(1)
    }

    private fun checkOverlayPermissionOnHomeFirst() {
//permissionCheck(true)
        /*
                val showCenterDialog = config.home_overlay_permission_dialog == "1"
                val showBottomDialog = config.home_overlay_permission_dialog == "2"
        //        val showCenterDialog =  false
        //        val showBottomDialog =  true

        //        if (!onceExperiment) {

                    onceExperiment = true
                    baseConfig.DialogHomeFlowTimeExperiment++

        //            if (!onceShowDialog) {
                        onceShowDialog = true
                        if (showBottomDialog) {

                            if (!Settings.canDrawOverlays(this)) {


                                overlayPermissionRequiredBottomDialog =
                                    OverlayPermissionRequiredBottomSheet.create(
                                        context = this@HomeABActivity,
                                        onCancel = {
                                            firebaseEventMain("overlay_permission_dont_allow_bottom")
                                        },
                                        onAllow = {
                                            takeOverlayPermission(true, true, "_bottom")
                                        })

                                overlayPermissionRequiredBottomDialog?.show()

                            }
                        } else {
                            if (!Settings.canDrawOverlays(this)) {
                                overlayPermissionRequiredDialog = OverlayPermissionRequiredDialog2(this, {
                                    firebaseEventMain("overlay_permission_dont_allow")
                                }, {
                                    takeOverlayPermission(false, true)
                                })
                                overlayPermissionRequiredDialog?.show()
                            }
                        }
                    }


        //            }*/
//        }

        /*
        logD(
            "checkOverlayPermissionOnHomeFirst",
            "-->${baseConfig.isHomeComingFirst}  --  ${baseConfig.isHomeComingSecond}"
        )
        if (!baseConfig.isHomeComingFirst) {
            if (baseConfig.isHomeComingSecond) {
                baseConfig.isHomeComingSecond = false

            }
        } */
    }

    private fun updateDefaultSmsBannerVisibility() {
        val isDefaultSms = roleDefaultCheck()
        Log.e(
            "Krupal", "updateDefaultSmsBannerVisibility: <--------------> ${isDefaultSms} :"
        )
        if (isDefaultSms || isFLow1) {
            config.defaultSmsDialogSuppressed = false
            binding.defaultSmsBanner.gone()
        } else if (!isDefaultSms || isFLow2 || config.bannerShowStart) {
            binding.defaultSmsBanner.visible()

        } else {
            binding.defaultSmsBanner.gone()
        }
    }

    private fun updateDefaultSmsBannerVisibilitybyFlow2() {
        val isDefaultSms = roleDefaultCheck()
        if (isDefaultSms || isFLow1) {
            binding.defaultSmsBanner.gone()
        } else if (!isDefaultSms || isFLow2 || config.bannerShowStart) {
            binding.defaultSmsBanner.visible()

        } else {
            binding.defaultSmsBanner.gone()
        }
    }

    private fun permissionlayershow(boolean: Boolean, num: Int) {
//        toastMess("call")
        Log.d("Krupal", "permissionlayershow: <--------------> ${boolean}-$num")
        if (boolean) {
            binding.defaultlayer.visible()
            isdefault = false
            binding.notdefault = true
            showdefaultbutton(false)
        } else {
            binding.defaultlayer.gone()
            isdefault = true
            binding.notdefault = false
            showdefaultbutton(true)
            openAdOnO(true, 1200)
        }
    }

    suspend fun chackDataBase() {
        isfistcreatdatabase = false
        delay(300)
        if (shouldUseNewSyncFlow) {
            withContext(Dispatchers.Main) {
                triggerFlow23Sync(startBootstrap = true)
            }
            return
        }
        withContext(Dispatchers.IO) {
            Log.d("TAG", "chackDataBase: <-----------> 1")
            getMobileMessage.refreshSmsInbox()
        }
    }

    @SuppressLint("StringFormatInvalid")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        inAppUpdate.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_DOCUMENT_TREE_FOR_SDK_30) {
            if (resultCode == RESULT_OK && data != null && data.data != null) {
                val treeUri = data.data
                val checkedUri = createFirstParentTreeUri(checkedDocumentPath)

                if (treeUri != checkedUri) {
                    val level = getFirstParentLevel(checkedDocumentPath)
                    val firstParentPath = checkedDocumentPath.getFirstParentPath(this, level)
                    toast(getString(R.string.wrong_folder_selected, humanizePath(firstParentPath)))
                    return
                }

                val takeFlags =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                applicationContext.contentResolver.takePersistableUriPermission(treeUri, takeFlags)
                val funAfter = funAfterSdk30Action
                funAfterSdk30Action = null
                funAfter?.invoke(true)
            } else {
                funAfterSdk30Action?.invoke(false)
            }
        } else if (requestCode == CONTACT_INSERT_EDIT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        val contactData: Uri? = data?.data // Get the URI of the saved contact
                        val contactName = retrieveContactName(contactData)
                        if (contactName != null) {
                            if (selecteditemmainpin.isNotEmpty()) {
                                selecteditemmainpin[0].threadId?.let {
                                    messagerDatabaseRepo.UpdateMessageTitleRepo(
                                        contactName, it
                                    )
                                }
                            } else {
                                toastMess(resources.getString(R.string.leku_load_location_error))
                                runOnUiThread {
                                    selectionRemove()
                                }
                            }
                        }
                    }
                    withContext(Dispatchers.Main) {
                        selectionRemove()
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                selectionRemove()
                Toast.makeText(this, getString(R.string.No), Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == DEFAULT_SMS_INSTRUCTION_REQUEST) {
            if (resultCode == RESULT_OK) {
                val from = if (fromSetasdefault.trim().isEmpty()) "" else fromSetasdefault
                openDefaultSmsSettings(from)
            }
        } else if (requestCode == FLOW3_LANGUAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                sendsubactivityOldnew(true)
            }
        } else if (requestCode == MAKE_DEFAULT_APP_REQUEST) {
            "loadMessages permission <-----------------------> 99".log()

            val isDefaultSmsNow = roleDefaultCheck()
            Log.e(
                "FFF",
                "firebaseEventMain -------fff-------:---resultCode:$resultCode ${isDefaultSmsNow}"
            )
            if (resultCode == RESULT_OK) {
                Log.e("FFF", "firebaseEventMain -------fff-@@@@ :--- ")
                config.defaultSmsDialogSuppressed = false
//                val finalfrom =""
                val finalfrom = if (fromSetasdefault == null || fromSetasdefault.trim().isEmpty()) {
                    " "
                } else {
                    "_$fromSetasdefault"
                }
                baseConfig.apply {
                    logOnboardingFunnelStep("First_setasdefaulted")
                    if (baseConfig.HomeDefaultSmsCount > 1) {
                        if (baseConfig.HomeDefaultSmsCount > 3) {
                            firebaseFunnel("Setasdefaulted_3")
                        } else {
                            firebaseFunnel("Setasdefaulted_${baseConfig.HomeDefaultSmsCount}")
                        }
                    }
                    firebaseFunnel("Setasdefaulted")
                }

                "loadMessages permission <-----------------------> 100".log()


                openAdOnO(true, 1200)
                if (!isFLow1_2) {
                    if (!baseConfig.secondtimeflowuseFLow) {
                        baseConfig.secondtimeflowuseFLow = true
                        val isDefaultSms = roleDefaultCheck()
                        if (isDefaultSms) {
                            sendsubactivityOldnew(true)
                        }
                        "loadMessages permission <-----------------------> 20 $isSyncInProgress=$isDefaultSms".log()
                    }
                    askPermissions()
                } else if (isFLow1 && !baseConfig.varAoncePaywllOnCancel /*&& !baseConfig.secondtimeflowuseFLow*/) {
                    "loadMessages permission <-----------------------> 555 ".log()
                    baseConfig.secondtimeflowuseFLow = true
                    baseConfig.varAoncePaywllOnCancel = true
                    sendsubactivityFromVarA(true)

                } /*else if (isFLow2 && !baseConfig.secondtimeflowuse) {
                    "loadMessages permission <-----------------------> 666 ".log()
                    baseConfig.secondtimeflowuse = true
                    flow3Language()
                }*/
                else {
                    "loadMessages permission <-----------------------> 777 ".log()
                    if (roleDefaultCheck() || hasCoreSmsPermissions()) {

                        reloadHomeListFromDatabaseIfNeeded(26)
//                    logHomeListDiag("permission_result_ready index=$index granted=$granted")
                    }
                    if (isFLow2 && !roleDefaultCheck()) {
                        askPermissions()
                    }

//                    onResume()
                }

                if (!config.userfisttimedefaultset) {
                    config.userfisttimedefaultset = true
//                    firebaseFunnel("Message_First_setasdefault")
                    "DataBindingUtil <---------------------> Message_First_setasdefaulted".log()
                    "config.firsttimelangEvent <---------------------> Message_First_setasdefaulted".log()
                }

            } else {
                Log.e(
                    "FFF",
                    "firebaseEventMain -------------------------defaultSmsDialogSuppressed%%%&:---: ${config.defaultSmsDialogSuppressed}"
                )
                if (!isDefaultSmsNow) {
                    val myMs = SystemClock.elapsedRealtime() - msOfDefault
                    Log.e(
                        "FFF",
                        "firebaseEventMain -------------------------msOfDefault%%%&:---: ${msOfDefault}-->myMs"
                    )
                    if (myMs < 500) {
                        openDefaultSmsInstruction("dont")
                    }
                    if (isFLow1) {
                        config.defaultSmsDialogSuppressed = true
                    }
                    if (isFLow2 && !roleDefaultCheck()) {

                        if (!config.isComeDoubleDilogVarBCancle) {
                            config.isComeDoubleDilogVarBCancle = true
                            if (!config.defaultSmsDialogSuppressed) {
                                binding.setDefault.performClick()
                                isopenDirectinter = true
                                config.bannerShowStart = true
                            }
//                            binding.setDefault.performClick()
                        } else {
                            if (isopenDirectinter) {
                                isopenDirectinter = false

                                if (!BaseSharedPreferences(this@HomeABActivity).mIS_SUBSCRIBED!!) {
                                    if (isFLow2) {
                                        askPermissions()
                                    } else {
                                        askPermissions()
                                    }
                                } else {
                                    askPermissions()
                                }
                            } else {
                                askPermissions()
                            }

                        }

                    }
                } else {
                    val finalfrom =
                        if (fromSetasdefault == null || fromSetasdefault.trim().isEmpty()) {
                            " "
                        } else {
                            "_$fromSetasdefault"
                        }

                    baseConfig.apply {
                        logOnboardingFunnelStep("First_setasdefaulted")
                        if (baseConfig.HomeDefaultSmsCount > 1) {
                            if (baseConfig.HomeDefaultSmsCount > 3) {
                                firebaseFunnel("Setasdefaulted_3$finalfrom")
                            } else {
                                firebaseFunnel("Setasdefaulted_${baseConfig.HomeDefaultSmsCount}$finalfrom")
                            }
                        }
                        firebaseFunnel("Setasdefaulted")

                    }

                }
                "loadMessages permission <-----------------------> 77".log()
                if (!isFLow1_2) {
                    askPermissions()
                } else if (isFLow1 && !baseConfig.varAoncePaywllOnCancel/*&& !baseConfig.secondtimeflowuseFLow*/) {
                    baseConfig.secondtimeflowuseFLow = true
                    baseConfig.varAoncePaywllOnCancel = true
                    sendsubactivityFromVarA(true)
                }
                /*else if (isFLow2 && !baseConfig.secondtimeflowuse) {
                    baseConfig.secondtimeflowuse = true
                    flow3Language()
                }*/
            }
        }
    }

    private fun flow3Language() {
        val intent = Intent(this, LanguageActivity::class.java).apply {
            putExtra("mainopen", false)
            putExtra(LanguageActivity.EXTRA_RETURN_RESULT_FOR_FLOW3, true)
        }
        startActivityForResult(intent, FLOW3_LANGUAGE_REQUEST)
    }

    fun Context.hasPermission(permId: String) = ContextCompat.checkSelfPermission(
        this, permId
    ) == PackageManager.PERMISSION_GRANTED

    private fun permissionCheck(isUser: Boolean = false) {


        logD("TAG", "permissionCheck: --->$isUser")
        byUser = isUser

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (!hasPermission(Manifest.permission.POST_NOTIFICATIONS)) {
//                requestNotificationPermissionLauncher.launch(
//                    Manifest.permission.POST_NOTIFICATIONS
//                )
//
//            } else if (!hasPermission(Manifest.permission.READ_PHONE_STATE)) {
//                callStatePermission()
//            } else {
//                beNext()
//            }
//        } else {
        if (!hasPermission(Manifest.permission.READ_PHONE_STATE)) {
            callStatePermission()
        } else {
            beNext()
        }
//        }

    }

    private fun beNext() {
        beforeCreateSetUp()
    }

    private fun proceedAfterNotificationFlow() {
        if (hasPermission(Manifest.permission.READ_PHONE_STATE)) {
            beNext()
        } else {
            callStatePermission()
        }
    }

    private fun showRelationalPhoneDialog() {
        permissionRequiredBottomSheet?.safeDismiss()
        permissionRequiredBottomSheet = PermissionRequiredBottomSheet.create(
            activity = this,
            title = getString(R.string.permission_required_messages_title),
            subtitle = getString(R.string.permission_required_open_settings),
            step1 = getString(R.string.permission_required_step1),
            step2Template = getString(R.string.permission_required_step2_phone),
            step2Highlight = getString(R.string.permission_required_keyword_phone_call),
            step3Template = getString(R.string.permission_required_step3),
            step3Highlight = getString(R.string.permission_required_keyword_allow),
            buttonText = getString(R.string.permission_required_allow_button),
            onAllow = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                activityResultForCallState.launch(intent)
            },
            onCancel = {
                beNext()
            })
        permissionRequiredBottomSheet?.show()

    }

    private fun callStatePermission() {
        requestCallPermissionLauncher.launch(
            Manifest.permission.READ_PHONE_STATE
        )
    }

    private fun showRelationalNotificationDialog() {
        permissionRequiredBottomSheet?.safeDismiss()
        permissionRequiredBottomSheet = PermissionRequiredBottomSheet.create(
            activity = this,
            title = getString(R.string.permission_required_messages_title),
            subtitle = getString(R.string.permission_required_open_settings),
            step1 = getString(R.string.permission_required_step1),
            step2Template = getString(R.string.permission_required_step2_notifications),
            step2Highlight = getString(R.string.permission_required_keyword_notifications),
            step3Template = getString(R.string.permission_required_step3),
            step3Highlight = getString(R.string.permission_required_keyword_allow),
            buttonText = getString(R.string.permission_required_allow_button),
            onAllow = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                activityResultForNotification.launch(intent)
            },
            onCancel = {
                proceedAfterNotificationFlow()
            })
        permissionRequiredBottomSheet?.show()
    }

    override fun onDestroy() {
        NetworkObserver.unregister(this)
        homeBannerTriggerHandler.removeCallbacks(homeBannerDelayRunnable)
        dismissHomeMenu()
        draftRefreshHandler.removeCallbacks(draftRefreshRunnable)
        draftRefreshHandler.removeCallbacks(pinSwipeRefreshRunnable)
        flow23CenterLoaderRunnable?.let { draftRefreshHandler.removeCallbacks(it) }
        flow23CenterLoaderRunnable = null
        adapterMainMassage.onFilterResultsChanged = null


        openAdOnO(false, 0)
        baseConfig.isHomeComingFirst = false
        try {
            if (hasFlow23BootstrapSyncStarted) {
                smsSyncManager.stopRealtimeObservers()
            }
            inAppUpdate.onDestroy()
        } catch (_: Exception) {
        }
        isSplash = false
        try {
            super.onDestroy()
            // your cleanup code
        } catch (e: Exception) {
        }
    }

    private fun allmessagemarkasread() {

        CoroutineScope(Dispatchers.IO).launch {
            markastoallread()
            repo.allmessagemarkasreadRepo()
            repo.allmessagemarkasreadNewRepo()
        }
    }

    private fun beforeCreateSetUp() {

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_DENIED && PermissionChecker.checkSelfPermission(
                this, Manifest.permission.READ_SMS
            ) == PermissionChecker.PERMISSION_GRANTED
            &&
            PermissionChecker.checkSelfPermission(
                this, Manifest.permission.SEND_SMS
            ) == PermissionChecker.PERMISSION_GRANTED &&
            PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
            == PermissionChecker.PERMISSION_GRANTED &&
            PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            == PermissionChecker.PERMISSION_GRANTED

        ) {
            Log.d("TAG", "onCreate: <-------------> 12345 permission done")
            if (isfistcreateTimer) {
                createTimer(Splesh_TIME)
            }
            CoroutineScope(Dispatchers.IO).launch {
                if (isfistcreatdatabase) {
                    Log.d("TAG", "onCreate: <-------------> hdbcdbv")
                    chackDataBase()
                    startProgress(2)
                    Log.d("TAG", "startProgress: <-------->222  1")
                }
            }
        } else {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {

                try {
                    if (roleDefaultCheck()) {
                        ActivityCompat.requestPermissions(
                            this, arrayOf(
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.READ_SMS,
                                Manifest.permission.SEND_SMS,
                                Manifest.permission.RECEIVE_SMS,
                                Manifest.permission.READ_PHONE_STATE,
                            ), 1001
                        )
                    }
                } catch (_: Exception) {
                    Toast.makeText(
                        this, resources.getString(R.string.Something_Went_Wrong), Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                setDefaultApp()
            }
            Log.d("TAG", "onCreate: <-------------> 12345 permission Not")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun selectAllMessage() {
        CoroutineScope(Dispatchers.IO).launch {
            val selectedSnapshot = ArrayList(
                this@HomeABActivity.convolist2.toList()
                    .distinctBy { it.threadId }
                    .sortedWith(
                        compareByDescending<Conversation> { isConversationPinned(it) }
                            .thenByDescending { it.date }
                    )
            )

            if (selectedSnapshot.isEmpty()) {
                return@launch
            }

            val selectedThreadIds = selectedSnapshot.map { it.threadId.toString() }
            val pinnedConversations = config.pinnedConversations
            val hasUnpinnedSelection =
                selectedSnapshot.any { !pinnedConversations.contains(it.threadId.toString()) }

            runOnUiThread {
                this@HomeABActivity.selecteditemmainpin.clear()
                this@HomeABActivity.selecteditemmainpin.addAll(selectedSnapshot)

                adapterMainMassage.selecteditem.clear()
                adapterMainMassage.selecteditem.addAll(selectedSnapshot)

                this@HomeABActivity.selecteditemmain.clear()
                this@HomeABActivity.selecteditemmain.addAll(selectedThreadIds)

                binding.mainMenu.binding.isselection = selectedSnapshot.isNotEmpty()
                Log.d(
                    "jigar",
                    "onMainClick: mainclcik <----> 2 ${selectedSnapshot.size}"
                )
                ("${selectedSnapshot.size} " + resources.getString(R.string.selected)).also {
                    binding.mainMenu.binding.selectioncount.text = it
                }

                if (hasUnpinnedSelection) {
                    binding.mainMenu.binding.messagePin.visible()
                    binding.mainMenu.binding.messageUnpin.gone()
                } else {
                    binding.mainMenu.binding.messagePin.gone()
                    binding.mainMenu.binding.messageUnpin.visible()
                }

                adapterMainMassage.notifyDataSetChanged()
            }
        }
    }


    private fun importEvents() {
        FilePickerDialog(this) {
            showImportEventsDialog(it)
        }
    }

    private fun showImportEventsDialog(path: String) {
//        ImportMessagesDialog(this, path, binding)
    }


    override fun onPurchasedSuccess(purchase: Purchase) {

    }

    override fun onProductAlreadyOwn() {

    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
//        ProductPurchaseHelper.initProductsKeys(this) {
//            val sub = !AdsManagerRepo(this).isNeedToShowAds()
//            BaseSharedPreferences(this).mIS_SUBSCRIBED = sub
//        }
    }


    override fun onBillingKeyNotFound(productId: String) {
    }

    private fun createTimer(seconds: Long) {
        isfistcreateTimer = false
        Log.d("TAG", "createTimer: <-------------> createTimer")
        val countDownTimer: CountDownTimer = object : CountDownTimer(seconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000 + 1

            }

            override fun onFinish() {
                secondsRemaining = 0
                if (application == null) {
                    openNextActivity()
                    return
                }
                if (!BaseSharedPreferences(this@HomeABActivity).mIS_SUBSCRIBED!! && NetworkHelper.isOnline(
                        this@HomeABActivity
                    )
                ) {

//                    mSubscriptionFlow(this@HomeABActivity, "com.messenger.phone.number.text.sms.service.apps.MainActivity")
                } else {
                    openNextActivity()
                }
            }
        }
        countDownTimer.start()
    }

    private fun openNextActivity() {
        if (NetworkHelper.isOnline(this) && !BaseSharedPreferences(this).mIS_SUBSCRIBED!!) {
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        when (requestCode) {
            1001 -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED) {

                        CoroutineScope(Dispatchers.IO).launch {
                            chackDataBase()
                            startProgress(3)
                            Log.d("TAG", "startProgress: <-------->222  4")
                        }

                    } else {
                        val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        if (!showRationale) {
                            gooutside = true
                            val builder = AlertDialog.Builder(this)
                            builder.setTitle(getString(R.string.permission_required))
                            builder.setMessage("Storage Permission are required to save Audios into External Storage")
                            builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                                gooutside = true
                                if (!(isFinishing || isDestroyed)) {
                                    dialog.dismiss()
                                }
                                startInstalledAppDetailsActivity(this)
                            }
                            builder.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                                if (!(isFinishing || isDestroyed)) {
                                    dialog.dismiss()
                                }
                            }
                            builder.create().apply {
                                show()
                            }
                        }
                    }
                }

            }

            SMS_PERMISSION_REQUEST -> {
                if (getSmsPermissions().all { permission ->
                        ContextCompat.checkSelfPermission(
                            this, permission
                        ) == PackageManager.PERMISSION_GRANTED
                    }) {
                    continueAfterSmsPermissions()
                }
            }

            GENERIC_PERM_HANDLER -> {
                isAskingPermissions = false
//                refreshHomeAfterPermissionGrant()

                val granted =
                    grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                actionOnPermission?.let { callback ->
                    actionOnPermission = null
                    callback(granted)
                }
//
                refreshHomeAfterPermissionGrant()

//                val isDefaultSms = roleDefaultCheck()
//                val hasPermissions = hasCoreSmsPermissions()
//                Log.e(
//                    "",
//                    "refreshHomeAfterPermissionGrant: isDefaultSms=$isDefaultSms hasCoreSmsPermissions=$hasPermissions"
//                )
//
//                if (isDefaultSms || hasPermissions /*|| actionOnPermission != null*/) {
//
//                    logAllViewVisibility()
//                }
            }

            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }


    }


    fun startInstalledAppDetailsActivity(context: Activity?) {
        if (context == null) {
            return
        }
        val i = Intent()
        i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        i.addCategory(Intent.CATEGORY_DEFAULT)
        i.data = Uri.parse("package:" + context.packageName)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        context.startActivity(i)
    }

    override fun onMainClick(
        position: Int,
        list: ArrayList<Conversation>,
        selecteditem: ArrayList<Conversation>,
    ) {
        this.selecteditemmain.clear()
        this.selecteditemmainpin.clear()
        selecteditemmainpin.addAll(selecteditem)
        selecteditem.forEach {
            this.selecteditemmain.add(it.threadId.toString())
        }
        binding.mainMenu.binding.isselection = selecteditem.isNotEmpty()
        Log.d("jigar", "onMainClick: mainclcik <----> 1 ${selecteditem.size}")
        ("${selecteditem.size} " + resources.getString(R.string.selected)).also {
            binding.mainMenu.binding.selectioncount.text = "0"
        }
        ("${selecteditem.size} " + resources.getString(R.string.selected)).also {
            binding.mainMenu.binding.selectioncount.text = it
        }
        val pinnedConversations = config.pinnedConversations
        if (selecteditemmainpin.any { !pinnedConversations.contains(it.threadId.toString()) }) {
            binding.mainMenu.binding.messagePin.visible()
            binding.mainMenu.binding.messageUnpin.gone()
        } else {
            binding.mainMenu.binding.messagePin.gone()
            binding.mainMenu.binding.messageUnpin.visible()
        }
    }

    fun handlePermission(permissionId: Int, callback: (granted: Boolean) -> Unit) {
        actionOnPermission = null

        if (hasPermission(permissionId)) {
            callback(true)
        } else {
            isAskingPermissions = true
            actionOnPermission = callback

            ActivityCompat.requestPermissions(
                this,
                arrayOf(getPermissionString(permissionId)),
                GENERIC_PERM_HANDLER
            )
        }
    }

    fun handlePermission(
        permissionId: Int, isRelational: Boolean = false, callback: (Boolean, Boolean) -> Unit
    ) {
        actionOnPermission = null
        if (hasPermission(permissionId)) {
            callback(true, isRelational)
        } else {
            isAskingPermissions = true
            actionOnPermission = { granted ->
                callback(granted, isRelational)
            }
            ActivityCompat.requestPermissions(
                this, arrayOf(getPermissionString(permissionId)), GENERIC_PERM_HANDLER
            )
        }
    }

    private fun onAllPermissionsProcessed(byUser: Boolean) {

        val allGrantedNow = requiredPermissions.all {
            if (it == PERMISSION_RECEIVE_SMS) ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECEIVE_SMS
            ) == PackageManager.PERMISSION_GRANTED
            else hasPermission(it)
        }

        "loadMessages permission <-----------------------> onAllPermissionsProcessed $byUser - $allGrantedNow".log()

        baseConfig.allPermissionRlationalDone = roleDefaultCheck()

        if (!allGrantedNow) {
            "loadMessages permission <-----------------------> some_missing_after_flow".log()

            isAskingPermissions = false
            updateRelationalPermissionSettingFlow(byUser)
            return
        }

        // ✅ All permissions granted → continue
        handleNotificationPermission { granted ->

            if (!granted) {
                PermissionRequiredDialog(
                    activity = this,
                    textId = R.string.allow_notifications_incoming_messages_per,
                    positiveActionCallback = {
                        openNotificationSettings()
                    },
                )
            } else {
                baseConfig.secondtimeflowuseFLow = true
            }

            startSyncIfNeeded(2)
            checkOverlayPermissionOnHomeFirst()

            isAskingPermissions = false
        }
    }

    private fun refreshHomeAfterPermissionGrant(source: Int = 8) {
        val isDefaultSms = roleDefaultCheck()
        val hasPermissions = hasCoreSmsPermissions()
        Log.e(
            "",
            "refreshHomeAfterPermissionGrant: isDefaultSms=$isDefaultSms hasCoreSmsPermissions=$hasPermissions"
        )

        if (!isDefaultSms || !hasPermissions /*|| actionOnPermission != null*/) {
            logHomeListDiag("refresh_skip source=$source")
            return
        }

        logHomeListDiag("refresh_run source=$source")
        updateDefaultSmsBannerVisibilitybyFlow2()
        binding.messageloadinglottie.gone()
        binding.messageloadinglottie2.gone()
        binding.messageper.gone()
        binding.messageperper.gone()
        setMainNoMessageState(false)
        permissionlayershow(false, 10)
        forceHomeListRefresh()
        if (isSyncInProgress) {
            logHomeListDiag("refresh_skip_sync_in_progress source=$source")
            return
        }
        startSyncIfNeeded(source)
    }

    private fun forceHomeListRefresh() {
        if (!::adapterMainMassage.isInitialized) return
        binding.main.post {
            if (isFinishing || isDestroyed) return@post
            logHomeListDiag("force_before")
            binding.executePendingBindings()
            binding.defaultlayer.requestLayout()
            binding.allmassagerecycler.requestLayout()
            binding.allmassagerecycler.invalidate()
            adapterMainMassage.notifyDataSetChanged()
            logHomeListDiag("force_after")
        }
    }

    private fun stabilizeHomeListAfterConversationUpdate(expectedCount: Int, attempt: Int = 0) {
        if (!::adapterMainMassage.isInitialized) return
        if (expectedCount <= 0) return

        binding.allmassagerecycler.postDelayed({
            if (isFinishing || isDestroyed) return@postDelayed

            val hasItems = adapterMainMassage.itemCount > 0 || convolist2.isNotEmpty()
            logHomeListDiag("stabilize expected=$expectedCount attempt=$attempt hasItems=$hasItems")
            if (hasItems) {
                showHomeConversationContent()
            } else if (attempt < 6) {
                stabilizeHomeListAfterConversationUpdate(expectedCount, attempt + 1)
            }
        }, 180L)
    }

    private fun showHomeConversationContent() {
        if (!::adapterMainMassage.isInitialized) return

        logHomeListDiag("showContent_before")
        setMainNoMessageState(false)
        binding.notdefault = false
        binding.defaultlayer.gone()
        updateDefaultSmsBannerVisibilitybyFlow2()
        binding.mainpro.gone()
        binding.messageloading.gone()
        binding.messageloadinglottie.gone()
        binding.messageloadinglottie2.gone()
        binding.messageper.gone()
        binding.messageperper.gone()
        binding.allmassagerecycler.visible()
        binding.conversationsFastscroller.visible()
        showdefaultbutton(true)
        hideFlow23CenterLoader()
        forceHomeListRefresh()
        logHomeListDiag("showContent_after")
    }

    private fun logHomeListDiag(stage: String) {
        val adapterCount =
            if (::adapterMainMassage.isInitialized) adapterMainMassage.itemCount else -1
        val adapterListSize =
            if (::adapterMainMassage.isInitialized) adapterMainMassage.list.size else -1

        if (stage.equals("loader_show_after") || stage.equals("observer_after") || stage.equals("observer_before")) {

            if (roleDefaultCheck() && hasCoreSmsPermissions() && adapterListSize <= 0 && convolist2.size <= 0 && binding.mainpro.visibility == 8) {
                binding.messageloadinglottie.visibility = View.GONE
                binding.defaultlayer.visibility = View.GONE
                mainNoMessageVisibleState = true
                binding.nomessagefoundchack = true
                binding.newmessage.visible()
            }

            if (isFLow2 && hasCoreSmsPermissions() && adapterListSize <= 0 && convolist2.size <= 0 && binding.mainpro.visibility == 8) {
                binding.messageloadinglottie.visibility = View.GONE
                binding.defaultlayer.visibility = View.GONE
                mainNoMessageVisibleState = true
                binding.nomessagefoundchack = true
                binding.newmessage.visible()
            }

        }
//        Log.d(
//            homeDiagTag,
//            "$stage default=${roleDefaultCheck()} perms=${hasCoreSmsPermissions()} sync=$isSyncInProgress " +
//                    "adapterCount=$adapterCount adapterList=$adapterListSize conv=${convolist2.size} all=${allmessage.size} " +
//                    "defaultLayer=${binding.defaultlayer.visibility} defaultBanner=${binding.defaultSmsBanner.visibility} " +
//                    "loading=${binding.messageloading.visibility} mainpro=${binding.mainpro.visibility} " +
//                    "recycler=${binding.allmassagerecycler.visibility} fastScroller=${binding.conversationsFastscroller.visibility} " +
//                    "noMessage=$mainNoMessageVisibleState notDefault=${binding.notdefault}"
//        )
    }

    private val requiredPermissions = listOf(
        PERMISSION_READ_SMS,
        PERMISSION_SEND_SMS,
        PERMISSION_RECEIVE_SMS,
        PERMISSION_READ_CONTACTS,
        PERMISSION_READ_PHONE_STATE
    )

    private val permissionResults = mutableMapOf<Int, Boolean>()

    private fun startPermissionFlow(byUser: Boolean) {
        requestNextPermission(0, byUser)
    }

    private fun requestNextPermission(index: Int, byUser: Boolean) {

        if (index >= requiredPermissions.size) {
            onAllPermissionsProcessed(byUser)
            return
        }

        val permission = requiredPermissions[index]

        if (hasPermission(permission)) {
            "Permission already granted -> $permission".log()
            requestNextPermission(index + 1, byUser)
            return
        }

        handlePermission(permission) { granted ->
            "Permission result -> $permission = $granted".log()
            requestNextPermission(index + 1, byUser)

            val isDefaultSms = roleDefaultCheck()
            val hasPermissions = hasCoreSmsPermissions()
            Log.e(
                "",
                "refreshHomeAfterPermissionGrant: isDefaultSms=$isDefaultSms hasCoreSmsPermissions=$hasPermissions"
            )

            if (isDefaultSms || hasPermissions) {
//                reopenActivity()
//                recreate()
                reloadHomeListFromDatabaseIfNeeded(30)
                logHomeListDiag("permission_result_ready index=$index granted=$granted")
            }
        }
    }

    private fun reloadHomeListFromDatabaseIfNeeded(source: Int) {
        if (!roleDefaultCheck() || !hasCoreSmsPermissions()) return

        CoroutineScope(Dispatchers.IO).launch {
            val latestConversations = repo.getallconversationunarchivforcontactrepo()
            withContext(Dispatchers.Main) {
                if (isFinishing || isDestroyed) return@withContext
                logHomeListDiag("db_reload source=$source size=${latestConversations.size}")
                if (latestConversations.isNotEmpty()) {
                    setupconversation(latestConversations)
                    showHomeConversationContent()
                } else {
                    setMainNoMessageState(true)
                    forceHomeListRefresh()
                }
            }
        }
    }

    private fun reopenActivity() {
        val intent = Intent(this, HomeABActivity::class.java)

        // Optional: Reuse the same intent that started this activity (recommended)
        // val intent = getIntent()

        // Flags to make reopen smoother
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)   // Reduce flash

        // No animation transition
        overridePendingTransition(0, 0)
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    fun startProgress(num: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            if (shouldUseNewSyncFlow) {
                triggerFlow23Sync(startBootstrap = false)
                return@launch
            }
//            setSyncUiVisible(true, 2)
            Log.e("TAG", "startProgress: permissionlayershow//${hasCoreSmsPermissions()}--> $num")
            if (roleDefaultCheck() || hasCoreSmsPermissions()) {
                permissionlayershow(false, 9)
                CoroutineScope(Dispatchers.IO).launch {
                    if (adapterMainMassage.list.isNotEmpty()) {
                        logD(
                            "refreshSmsInbox", "getIsFistTimeMessageget m1"
                        )
                        runOnUiThread {
                            hideSyncUiIfNotSyncing()
                            setMainNoMessageState(false)
                        }
                    } else {
                        runOnUiThread {


                            if (getIsFistTimeMessageget(this@HomeABActivity)) {
                                setSyncUiVisible(true, 8)
                            }
                            logD(
                                "refreshSmsInbox",
                                "getIsFistTimeMessageget m2 ${getIsFistTimeMessageget(this@HomeABActivity)}"
                            )
                            if (getIsFistTimeMessageget(this@HomeABActivity)) {
                                var percentage = 1
                                logD("refreshSmsInbox", "oddie 2")
                                binding.messageloadinglottie.visible()
                                binding.messageloadinglottie2.visible()
                                binding.messageper.visible()
                                binding.messageperper.visible()
                                showdefaultbutton(false)
//                                binding.nomessagefoundchack = false
                                binding.messageloadinglottie2.progress = percentage.toInt()
                                if (percentage.toInt() == 0) {
                                    //binding.messageloadingloti.visible()
                                    binding.messageper.text =
                                        resources.getString(R.string.sync_messages)
                                } else {
                                    //binding.messageloadingloti.gone()
                                    binding.messageper.text =
                                        resources.getString(R.string.sync_messages)
                                }
                            } else {
                                runOnUiThread {
                                    hideSyncUiIfNotSyncing()
                                    logD(
                                        "refreshSmsInbox", "nomessagefoundchack 2 true"
                                    )
//                                    binding.nomessagefoundchack = true
                                }
                            }
                        }
                    }
                    getMobileMessage.onProssageStart = {
                        val rawPercentage = if (totalmessagecount > 0) {
                            (totalgetmessage.toDouble() / totalmessagecount) * 100
                        } else {
                            0.0
                        }
                        val percentage = rawPercentage.coerceIn(0.0, 100.0)
                        logD(
                            "refreshSmsInbox",
                            "getIsFistTimeMessageget onProssageStart ${percentage}"
                        )
                        runOnUiThread {
//                            setSyncUiVisible(true, 3)
                            binding.messageloadinglottie2.progress = percentage.toInt()
                            if (percentage.toInt() == 0) {
                                binding.messageper.text =
                                    resources.getString(R.string.sync_messages)
                            } else {


                                val safePercentage = percentage.toInt().coerceAtMost(99)

                                binding.messageper.text =
                                    resources.getString(R.string.sync_messages)
                                binding.messageperper.text = " ($safePercentage%)"


//                                binding.messageper.text = resources.getString(R.string.sync_messages)
//                                binding.messageperper.text = " (" + percentage.toInt() + "%" + ")"
                            }
                        }
                    }
                }
            } else {
                logD(
                    "refreshSmsInbox", "getIsFistTimeMessageget m3"
                )
//                setSyncUiVisible(true, 4)
            }
            getMobileMessage.onCompleted = {
                logD("refreshSmsInbox", "getIsFistTimeMessageget 44 ${convolist2.size}")
                openAdOnO(true, 1200)
                if (convolist2.isEmpty()) {
                    runOnUiThread {
                        setSyncUiVisible(false, 5)
                        forceHomeListRefresh()
                    }
                    logD(
                        "refreshSmsInbox", "nomessagefoundchack 3 true"
                    )
                    setMainNoMessageState(true)
                } else {
                    runOnUiThread {
                        setSyncUiVisible(false, 6)
                        forceHomeListRefresh()
                    }
                }
            }
        }
    }

    private fun setSyncUiVisible(show: Boolean, i: Int) {
        logD("setSyncUiVisible", "--> $i")
        if (show) {
            isSyncInProgress = true
            binding.mainpro.visible()
            binding.messageloading.gone()
            logD("refreshSmsInbox", "oddie 3")
            binding.messageloadinglottie.visible()
            binding.messageloadinglottie2.visible()
            binding.messageper.visible()
            binding.messageperper.visible()
            showdefaultbutton(false)
        } else {
            isSyncInProgress = false
            binding.mainpro.gone()
            binding.messageloading.gone()
            binding.messageloadinglottie.gone()
            binding.messageloadinglottie2.gone()
            binding.messageper.gone()
            binding.messageperper.gone()
            if (roleDefaultCheck()) {
                showdefaultbutton(true)
            } else {
                showdefaultbutton(false)
            }
        }
    }

    private fun hideSyncUiIfNotSyncing() {
        if (!isSyncInProgress) {
            setSyncUiVisible(false, 7)
        }
    }

    private fun triggerFlow23Sync(startBootstrap: Boolean = true) {
        if (!shouldUseNewSyncFlow) return
        showFlow23CenterLoader()

        if (startBootstrap && !hasFlow23BootstrapSyncStarted) {
            hasFlow23BootstrapSyncStarted = true
            smsSyncManager.onAppBecameDefault()
        } else {
            smsSyncManager.startRealtimeOnly()
        }
    }

    private fun startSyncIfNeeded(count: Int) {
        logD("Krupal", "startSyncIfNeeded---> ${count} $shouldUseNewSyncFlow")
        logHomeListDiag("startSync count=$count")
        if (isSyncInProgress) {
            logHomeListDiag("startSync_skip_already_running count=$count")
            return
        }
        if (shouldUseNewSyncFlow) {
            showFlow23CenterLoader()
            triggerFlow23Sync(startBootstrap = true)
            return
        }
        if (adapterMainMassage.list.isEmpty() && convolist2.isEmpty()) {
            setSyncUiVisible(true, 11)
            binding.messageper.text = resources.getString(R.string.sync_messages)
            binding.messageperper.text = ""
        }
        CoroutineScope(Dispatchers.IO).launch {
            chackDataBase()
            startProgress(4)
        }
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            if (isSyncInProgress && (adapterMainMassage.list.isNotEmpty() || convolist2.isNotEmpty())) {
                setSyncUiVisible(false, 9)
            }
        }
    }

    private fun observeFlow23SyncUi() {
        if (!shouldUseNewSyncFlow) return
        smsSyncManager.isMessageLoading.observe(this) { loading ->
            if (loading == true) {
                showFlow23CenterLoader()
            } else {
                hideFlow23CenterLoader()
            }
        }
        smsSyncManager.lastInsertedCount.observe(this) { count ->
            logHomeListDiag("flow23_inserted count=$count")
        }
        smsSyncManager.isInitialSyncDone.observe(this) { done ->
            logHomeListDiag("flow23_initial_done=$done")
        }
        smsSyncManager.lastError.observe(this) { error ->
            if (!error.isNullOrBlank()) {
                Log.e(homeDiagTag, "flow23_error=$error")
            }
        }
    }

    private fun showFlow23CenterLoader() {
        if (!shouldUseNewSyncFlow) return
        logHomeListDiag("loader_show_before")
        logD(
            "Krupal",
            "startSyncIfNeeded---> --- shouldUseNewSyncFlow ${::adapterMainMassage.isInitialized}--${adapterMainMassage.list.isNotEmpty()}---${convolist2.isNotEmpty()}"
        )
        if (::adapterMainMassage.isInitialized && (adapterMainMassage.list.isNotEmpty() || convolist2.isNotEmpty())) {
            logD("Krupal", "startSyncIfNeeded---> --- shouldUseNewSyncFlow 1")
            hideFlow23CenterLoader()
            return
        }
        logD("Krupal", "startSyncIfNeeded---> --- shouldUseNewSyncFlow 2")

        isSyncInProgress = true
        binding.mainpro.gone()
        binding.messageloading.visible()
        showdefaultbutton(false)
        logHomeListDiag("loader_show_after")
    }

    private fun hideFlow23CenterLoader() {
        flow23CenterLoaderRunnable?.let { draftRefreshHandler.removeCallbacks(it) }
        flow23CenterLoaderRunnable = null
        binding.messageloading.gone()
        logHomeListDiag("loader_hide_before")
        logD(
            "Krupal",
            "startSyncIfNeeded--->0 --- $isSyncInProgress.  $isdefault $shouldUseNewSyncFlow"
        )

        if (shouldUseNewSyncFlow) {
            isSyncInProgress = false
            if (roleDefaultCheck()) {
                showdefaultbutton(true)
            } else {
                showdefaultbutton(false)
            }
        }
        logHomeListDiag("loader_hide_after")
    }

    private val Int.dp
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, toFloat(), resources.displayMetrics
        ).roundToInt()


    private fun conPin(pos: Int, itemView: View) {
        val pinnedConversations = config.pinnedConversations
//        "conPin <----------> 1 pos :- $pos pinnedConversations <---------> ${
//            pinnedConversations.contains(
//                adapterMainMassage.list[pos].threadId.toString()
//            )
//        }".log()
        if (!pinnedConversations.contains(adapterMainMassage.list[pos].threadId.toString())) {
            pinConversationSwipe(true, arrayListOf(adapterMainMassage.list[pos]), pos)
//            "conPin <----------> 2 pos :- $pos convolist2forpin <---------> $${convolist2forpin[pos]}".log()
        } else {
            pinConversationSwipe(false, arrayListOf(adapterMainMassage.list[pos]), pos)
//            "conPin <----------> 3 pos :- $pos convolist2forpin <---------> $${convolist2forpin[pos]}".log()
        }
    }

    private fun conMarkasread(pos: Int, itemView: View) {

        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                if (convolist2.isNotEmpty()) {
                    adapterMainMassage.list[pos].threadId?.let {
                        repo.setisoldmessageRepo(
                            false, it
                        )
                    }
                    adapterMainMassage.list[pos].threadId?.let {
                        repo.setisoldmessageCountRepo(
                            0, it
                        )
                    }
                    adapterMainMassage.list[pos].threadId?.let { markThreadAsRead(it) }
                }
            }
            withContext(Dispatchers.Main) {
                reattachitemswap()
            }
        }
    }

    private fun conCall(pos: Int) {
        dialNumber(adapterMainMassage.list[pos].phoneNumber)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun conDelete(pos: Int, itemView: View) {
        try {


            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    adapterMainMassage.list[pos].threadId?.let {
                        val data = messagerDatabaseRepo.getUserMessageListChackrepo(it)
                        data.forEachIndexed { index, conversation ->
                            repo.insertOrUpdateRecycleBinRepo(
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
                        }
                        deleteConversation(it)
                    }
                    try {
                        repo.deleteConversationRepo(arrayListOf(adapterMainMassage.list[pos].threadId.toString()))

                    } catch (e: Exception) {
                    }


                }
                withContext(Dispatchers.Main) {
                    Snackbar.make(
                        binding.root,
                        "1 " + resources.getString(R.string.conversation_deleted),
                        Snackbar.LENGTH_LONG
                    ).show()
                    reattachitemswap()
                }
            }
        } catch (_: Exception) {

        }
    }

    private fun conArchive(pos: Int, itemView: View) {

        val pinnedConversations = config.pinnedConversations
        if (pinnedConversations.contains(adapterMainMassage.list[pos].threadId.toString())) {
            pinConversationSwipe(false, arrayListOf(adapterMainMassage.list[pos]), pos)
        }

        archiveoldpos = adapterMainMassage.list[pos].threadId!!
        CoroutineScope(Dispatchers.IO).launch {
            messagerDatabaseRepo.archivConversationRepo(arrayListOf(adapterMainMassage.list[pos].threadId.toString()))
            runOnUiThread {
                Snackbar.make(
                    binding.root, "1" + " conversation archived", Snackbar.LENGTH_LONG
                ).setAction("Undo") {
                    CoroutineScope(Dispatchers.IO).launch {
                        messagerDatabaseRepo.removearchivConversationRepo(arrayListOf(archiveoldpos.toString()))
                    }
                }.show()
                reattachitemswap()
            }
        }
    }

    override fun onSwipeRight(pos: Int, itemView: View) {
        when (config.Swipe_Left) {
            "Archive" -> {
                conArchive(pos, itemView)
            }

            "Delete" -> {
                conDelete(pos, itemView)
            }

            "Call" -> {
                conCall(pos)
            }

            "Mark as read" -> {
                conMarkasread(pos, itemView)
            }

            "Pin" -> {
                conPin(pos, itemView)
            }

            "Private Chat" -> {
                conPrivateChate(pos, itemView)
            }
        }
        resetSwipedRow(pos)
    }

    override fun onSwipeLeft(pos: Int, itemView: View) {
        when (config.Swipe_Right) {
            "Archive" -> {
                conArchive(pos, itemView)
            }

            "Delete" -> {
                conDelete(pos, itemView)
            }

            "Call" -> {
                conCall(pos)
            }

            "Mark as read" -> {
                conMarkasread(pos, itemView)
            }

            "Pin" -> {
                conPin(pos, itemView)
            }

            "Private Chat" -> {
                conPrivateChate(pos, itemView)
            }
        }
        resetSwipedRow(pos)
    }

    private fun resetSwipedRow(pos: Int) {
        if (pos == RecyclerView.NO_POSITION) return
        val recyclerView = binding.allmassagerecycler
        val resetAction = {
            if (pos >= 0 && pos < adapterMainMassage.itemCount) {
                recyclerView.itemAnimator?.endAnimations()
                adapterMainMassage.notifyItemChanged(pos)
                recyclerView.invalidate()
            }
        }
        if (recyclerView.isComputingLayout || recyclerView.scrollState != RecyclerView.SCROLL_STATE_IDLE) {
            recyclerView.post(resetAction)
        } else {
            resetAction()
        }
    }

    private fun conPrivateChate(pos: Int, itemView: View) {

        if (config.Lock_Screen_Pin == "Not Set") {
            reattachitemswap()
            toastMess(resources.getString(R.string.Please_set_first_private))
            startActivity(
                Intent(this, LockScreenSetupActivity::class.java).putExtra(
                    "comefrom", 1
                )
            )
        } else {

            CoroutineScope(Dispatchers.IO).launch {

                if (!BaseSharedPreferences(this@HomeABActivity).mIS_SUBSCRIBED!!) {
                    if (repo.getallconversationunPrivacyOrnotListrepo()
                            .distinctBy { it.threadId }.size >= 3
                    ) {
                        CoroutineScope(Dispatchers.Main).launch {
                            reattachitemswap()
                        }
                        sendsubactivity()
                    } else {
                        GlobalScope.launch {
                            withContext(Dispatchers.IO) {
                                config.removePinnedConversations(arrayListOf(adapterMainMassage.list[pos]))
                                repo.PrivacyConversationRepo(arrayListOf(adapterMainMassage.list[pos].threadId.toString()))
                            }
                            withContext(Dispatchers.Main) {
                                reattachitemswap()
                            }
                        }
                    }
                } else {
                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            config.removePinnedConversations(arrayListOf(adapterMainMassage.list[pos]))
                            repo.PrivacyConversationRepo(arrayListOf(adapterMainMassage.list[pos].threadId.toString()))
                        }
                        withContext(Dispatchers.Main) {
                            reattachitemswap()
                        }
                    }
                }

            }
        }
    }


//    private fun conPrivateChate(pos: Int, itemView: View) {
//
//        if (config.Lock_Screen_Pin == "Not Set") {
//            reattachitemswap()
//            toastMess(resources.getString(R.string.Please_set_first_private))
//            startActivity(Intent(this, LockScreenSetupActivity::class.java).putExtra("comefrom", 1))
//        } else {
//            GlobalScope.launch {
//                withContext(Dispatchers.IO) {
//                    config.removePinnedConversations(arrayListOf(convolist2[pos]))
//                    repo.PrivacyConversationRepo(arrayListOf(convolist2[pos].threadId.toString()))
//                }
//                withContext(Dispatchers.Main) {
//                    reattachitemswap()
//                }
//            }
//        }
//    }

    fun getIconPowerMenu(
        context: Context,
        lifecycleOwner: LifecycleOwner?,
        onMenuItemClickListener: OnMenuItemClickListener<PowerMenuItem>,
    ): PowerMenu? {
        val styledContext: Context = ContextThemeWrapper(context, R.style.PopupCardThemeOverlay)
        return PowerMenu.Builder(styledContext).addItem(
            PowerMenuItem(
                "WeChat", false, R.drawable.home_material_ic_workspace_premium_rounded
            )
        ).addItem(
            PowerMenuItem(
                "Facebook", false, R.drawable.home_material_ic_workspace_premium_rounded
            )
        ).addItem(
            PowerMenuItem(
                "Twitter", false, R.drawable.home_material_ic_workspace_premium_rounded
            )
        ).addItem(
            PowerMenuItem(
                "Line", false, R.drawable.home_material_ic_workspace_premium_rounded
            )
        ).addItem(PowerMenuItem("Other")).setLifecycleOwner(lifecycleOwner!!)
            .setOnMenuItemClickListener(onMenuItemClickListener).setAnimation(MenuAnimation.FADE)
            .setMenuRadius(
                context.resources.getDimensionPixelSize(R.dimen.menu_corner_radius).toFloat()
            ).setMenuShadow(
                context.resources.getDimensionPixelSize(R.dimen.menu_elevation).toFloat()
            ).setIsMaterial(true).build()
    }

    private val onIconMenuItemClickListener: OnMenuItemClickListener<PowerMenuItem> =
        OnMenuItemClickListener<PowerMenuItem> { position, item ->
            Toast.makeText(baseContext, item.title, Toast.LENGTH_SHORT).show()
            if (!(isFinishing || isDestroyed)) {
                homemenu?.dismiss()
            }
        }

    private fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else {
            (24 * resources.displayMetrics.density).toInt() // fallback ~24dp
        }
    }

    private fun dismissHomeMenu() {
        if (isFinishing || isDestroyed) return
        homePopupMenu?.dismiss()
        popupdialog?.dismiss()
    }

    private fun showpopupdialog(x1: Float, y1: Float, view: View) {
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

        val popupGravity = if (config.SelectedLanguage == "ar") {
            Gravity.START
        } else {
            Gravity.END
        }
        val popup = PopupMenu(themedContext, view, popupGravity)
        popup.menuInflater.inflate(R.menu.home_popup_menu, popup.menu)
        homePopupMenu = popup

        if (popup.menu is MenuBuilder) {
            val menuBuilder = popup.menu as MenuBuilder
            menuBuilder.setOptionalIconsVisible(true)

            val iconMarginPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, ICON_MARGIN.toFloat(), resources.displayMetrics
            ).toInt()
            val iconSizePx = resources.getDimensionPixelSize(R.dimen.popup_menu_icon_size)
            val iconTintColor = getProperTextColor()

            for (item in menuBuilder.visibleItems) {
                val icon = item.icon ?: continue
                icon.mutate()
                icon.setTint(iconTintColor)
                icon.setBounds(0, 0, iconSizePx, iconSizePx)
                item.icon = InsetDrawable(icon, iconMarginPx, 0, iconMarginPx, 0)
            }
        }

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.home_menu_language -> {
                    firebaseEventMain("Home_Menu_Language_open")
                    startActivity(
                        Intent(
                            this, SettingActivity::class.java
                        ).putExtra("loadiswhatsnew", 1)
                    )
                    dismissHomeMenu()
                }

                R.id.home_menu_mark_read -> {
                    if (binding.newmessage.isVisible) {
                        if (roleDefaultCheck()) {
                            showcommandialog(
                                dialogtital = resources.getString(R.string.Delete_this_conversation),
                                dialogmessage = resources.getString(R.string.mark_all_messages_as_read),
                                positivebutton = resources.getString(R.string.ok),
                                negativebutton = resources.getString(R.string.cancel),
                                "mark all messages as read"
                            )
                        } else {
                            binding.setDefault.performClick()
                        }
                    } else {
                        binding.setDefault.performClick()
                    }
                    dismissHomeMenu()
                }

                R.id.home_menu_filter -> {
                    firebaseEventMain("Home_Menu_Filter_open")
                    if (binding.newmessage.isVisible) {
                        showfilterdialog()
                    } else {
                        binding.setDefault.performClick()
                    }
                    dismissHomeMenu()
                }

                R.id.home_menu_archive -> {
                    firebaseEventMain("Home_Menu_Archive")
                    startActivity(Intent(this, ArchivedActivity::class.java))
                    dismissHomeMenu()
                }

                R.id.home_menu_share -> {
                    firebaseEventMain("Home_Menu_App_Share")
                    shareApp(this)
                    dismissHomeMenu()
                }

                R.id.home_menu_settings -> {
                    firebaseEventMain("Home_Menu_Setting")
                    startActivity(Intent(this, SettingActivity::class.java))
                    dismissHomeMenu()
                }
            }
            true
        }

        popup.setOnDismissListener {
            if (homePopupMenu == popup) {
                homePopupMenu = null
            }
        }
        val fontRes = R.font.lato_semibold

        for (i in 0 until popup.menu.size()) {
            val item = popup.menu.getItem(i)
            applyFontToMenuItem(this, item, fontRes)
        }
        if (isFinishing || isDestroyed) return
        if (!view.isAttachedToWindow) return

        popup.show()
    }


    override fun onpositive(whatfordialog: String) {
        when (whatfordialog) {
            "delete" -> {
                if (!(isFinishing || isDestroyed)) {
                    commanDeleteBlockDialog?.dismiss()
                }
                deleteMessage()
            }

            "mark all messages as read" -> {
                allmessagemarkasread()
                if (!(isFinishing || isDestroyed)) {
                    commanDeleteBlockDialog?.dismiss()
                }
            }

            else -> {
                if (!(isFinishing || isDestroyed)) {
                    commanDeleteBlockDialog?.dismiss()
                }
            }
        }
    }

    override fun onnegative(whatfordialog: String) {
        when (whatfordialog) {
            "delete" -> {
                if (!(isFinishing || isDestroyed)) {
                    commanDeleteBlockDialog?.dismiss()
                }
                selectionRemove()
            }

            "mark all messages as read" -> {
                if (!(isFinishing || isDestroyed)) {
                    commanDeleteBlockDialog?.dismiss()
                }
                selectionRemove()
            }

            else -> {
                if (!(isFinishing || isDestroyed)) {
                    commanDeleteBlockDialog?.dismiss()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent != null) {
            ChackCallIntent(intent)
        }
    }

    fun ChackCallIntent(intent: Intent) {
        if (intent.action == "ACTION_DIAL_NUMBER") {
            val phoneNumber = intent.getStringExtra("PHONE_NUMBER")
            if (!phoneNumber.isNullOrEmpty()) {
                dialNumber(phoneNumber)
            }
        } else {
            super.onNewIntent(intent)
        }
    }


    override fun onClickTab(tabname: String, position: Int, list: ArrayList<Category>) {
        try {
            applyTabChipStyle()
            GlobalScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        convolist2.clear()
                        MessageType = tabname
                        if (MessageType == "otp") {
                            isdefaultcatremove = false
                            GlobalScope.launch {
                                try {
                                    withContext(Dispatchers.IO) {
                                        allmessage.toList().forEachIndexed { index, conversation ->
                                            if (conversation.messagetype == "otp") {
                                                convolist2.add(conversation)

                                            }
                                        }
                                        val distinctConvos = convolist2
                                        val tempList = ArrayList(distinctConvos)
                                        convolist2.clear()
                                        convolist2.addAll(tempList)
                                    }
                                    withContext(Dispatchers.Main) {
                                        val dataunread: ArrayList<Conversation> = arrayListOf()
                                        if (filterdialogselectionmain == "unreadBtn") {
                                            dataunread.clear()
                                            GlobalScope.launch {
                                                try {
                                                    withContext(Dispatchers.IO) {
                                                        convolist2.toList()
                                                            .forEachIndexed { index, conversation ->
                                                                if (conversation.isnewmessage == true) {
                                                                    dataunread.add(conversation)
                                                                }
                                                            }
                                                    }
                                                    withContext(Dispatchers.Main) {
                                                        convolist2 =
                                                            ArrayList(convolist2.distinctBy { it.threadId }
                                                                .sortedWith(compareByDescending<Conversation> {
                                                                    isConversationPinned(it)
                                                                }.thenByDescending { it.date }))
                                                        adapterMainMassage.updateList(dataunread)
                                                    }
                                                } catch (e: Exception) {

                                                }
                                            }

                                        } else if (filterdialogselectionmain == "defaultBtn") {
                                            var dataunread: ArrayList<Conversation> = arrayListOf()
                                            dataunread.addAll(convolist2)
                                            CoroutineScope(Dispatchers.Main).launch {
                                                dataunread =
                                                    ArrayList(dataunread.distinctBy { it.threadId }
                                                        .sortedWith(compareByDescending<Conversation> {
                                                            isConversationPinned(it)
                                                        }.thenByDescending { it.date }))
                                                adapterMainMassage.updateList(dataunread)
                                            }
                                        } else if (filterdialogselectionmain == "todayBtn") {
                                            var dataunread: ArrayList<Conversation> = arrayListOf()
                                            GlobalScope.launch {
                                                try {
                                                    withContext(Dispatchers.IO) {
                                                        dataunread.addAll(filterTodayData(convolist2))
                                                        dataunread =
                                                            ArrayList(dataunread.distinctBy { it.threadId }
                                                                .sortedWith(compareByDescending<Conversation> {
                                                                    isConversationPinned(it)
                                                                }.thenByDescending { it.date }))

                                                    }
                                                    withContext(Dispatchers.Main) {
                                                        adapterMainMassage.updateList(dataunread)
                                                    }
                                                } catch (e: Exception) {

                                                }
                                            }
                                        } else if (filterdialogselectionmain == "monthBtn") {
                                            var dataunread: ArrayList<Conversation> = arrayListOf()
                                            GlobalScope.launch {
                                                try {
                                                    withContext(Dispatchers.IO) {
                                                        val monthandyear =
                                                            extractMonthYearFromString(
                                                                formateddatemain
                                                            )
                                                        if (monthandyear != null) {
                                                            dataunread.addAll(
                                                                filterDataByMonthYear(
                                                                    convolist2,
                                                                    monthandyear.first,
                                                                    monthandyear.second
                                                                )
                                                            )
                                                        } else {
                                                            dataunread.addAll(convolist2)
                                                        }

                                                        dataunread =
                                                            ArrayList(dataunread.distinctBy { it.threadId }
                                                                .sortedWith(compareByDescending<Conversation> {
                                                                    isConversationPinned(it)
                                                                }.thenByDescending { it.date }))

                                                    }
                                                    withContext(Dispatchers.Main) {
                                                        adapterMainMassage.updateList(dataunread)
                                                    }
                                                } catch (e: Exception) {

                                                }
                                            }
                                        } else if (filterdialogselectionmain == "yearBtn") {
                                            var dataunread: ArrayList<Conversation> = arrayListOf()
                                            GlobalScope.launch {
                                                try {
                                                    withContext(Dispatchers.IO) {
                                                        val monthandyear =
                                                            extractYearFromString(formateddatemain)
                                                        if (monthandyear != null) {
                                                            dataunread.addAll(
                                                                filterDataByYear(
                                                                    convolist2, monthandyear
                                                                )
                                                            )
                                                        } else {
                                                            dataunread.addAll(convolist2)
                                                        }
                                                        dataunread =
                                                            ArrayList(dataunread.distinctBy { it.threadId }
                                                                .sortedWith(compareByDescending<Conversation> {
                                                                    isConversationPinned(it)
                                                                }.thenByDescending { it.date }))
                                                    }
                                                    withContext(Dispatchers.Main) {
                                                        adapterMainMassage.updateList(dataunread)
                                                    }
                                                } catch (e: Exception) {

                                                }
                                            }
                                        } else if (filterdialogselectionmain == "dateRangeBtn") {
                                            var dataunread: ArrayList<Conversation> = arrayListOf()
                                            GlobalScope.launch {
                                                try {
                                                    withContext(Dispatchers.IO) {
                                                        val (firstFormattedDate, secondFormattedDate) = extractAndFormatDateRange(
                                                            formateddatemain
                                                        )
                                                        if (firstFormattedDate != null && secondFormattedDate != null) {
                                                            dataunread.addAll(
                                                                filterDataByDateRange(
                                                                    convolist2,
                                                                    firstFormattedDate,
                                                                    secondFormattedDate
                                                                )
                                                            )
                                                        } else {
                                                            dataunread.addAll(convolist2)
                                                        }
                                                        dataunread =
                                                            ArrayList(dataunread.distinctBy { it.threadId }
                                                                .sortedWith(compareByDescending<Conversation> {
                                                                    isConversationPinned(it)
                                                                }.thenByDescending { it.date }))
                                                    }
                                                    withContext(Dispatchers.Main) {
                                                        adapterMainMassage.updateList(dataunread)
                                                    }
                                                } catch (e: Exception) {

                                                }
                                            }
                                        } else {
                                            convolist2 =
                                                ArrayList(convolist2.distinctBy { it.threadId }
                                                    .sortedWith(compareByDescending<Conversation> {
                                                        isConversationPinned(it)
                                                    }.thenByDescending { it.date }))

                                            adapterMainMassage.updateList(convolist2)
                                        }
                                        convolist2forpin = convolist2
                                    }
                                } catch (e: Exception) {

                                }
                            }
                        } else if (MessageType == "Personal") {
                            isdefaultcatremove = false
                            GlobalScope.launch {
                                try {
                                    withContext(Dispatchers.IO) {
                                        allmessage.toList().forEachIndexed { index, conversation ->
                                            if (!conversation.title.replace("+", "")
                                                    .isDigitsOnly() && conversation.phoneNumber.replace(
                                                    "+", ""
                                                ).isDigitsOnly()
                                            ) {
                                                convolist2.add(conversation)
                                            }
                                        }
                                        try {
                                            val distinctConvos = convolist2
                                            val tempList = ArrayList(distinctConvos)
                                            convolist2.clear()
                                            convolist2.addAll(tempList)
                                        } catch (e: Exception) {

                                        }
                                    }
                                    withContext(Dispatchers.Main) {
                                        var dataunread: ArrayList<Conversation> = arrayListOf()
                                        if (filterdialogselectionmain == "unreadBtn") {
                                            dataunread.clear()
                                            GlobalScope.launch {
                                                try {
                                                    withContext(Dispatchers.IO) {
                                                        convolist2.toList()
                                                            .forEachIndexed { index, conversation ->
                                                                if (conversation.isnewmessage == true) {
                                                                    dataunread.add(conversation)
                                                                }
                                                            }
                                                    }
                                                    withContext(Dispatchers.Main) {
                                                        dataunread =
                                                            ArrayList(dataunread.distinctBy { it.threadId }
                                                                .sortedWith(compareByDescending<Conversation> {
                                                                    isConversationPinned(it)
                                                                }.thenByDescending { it.date }))
                                                        adapterMainMassage.updateList(dataunread)
                                                    }
                                                } catch (e: Exception) {

                                                }
                                            }
                                        } else if (filterdialogselectionmain == "defaultBtn") {
                                            var dataunread: ArrayList<Conversation> = arrayListOf()
                                            dataunread.addAll(convolist2)
                                            CoroutineScope(Dispatchers.Main).launch {
                                                dataunread =
                                                    ArrayList(dataunread.distinctBy { it.threadId }
                                                        .sortedWith(compareByDescending<Conversation> {
                                                            isConversationPinned(it)
                                                        }.thenByDescending { it.date }))
                                                adapterMainMassage.updateList(dataunread)
                                            }
                                        } else if (filterdialogselectionmain == "todayBtn") {
                                            var dataunread: ArrayList<Conversation> = arrayListOf()
                                            GlobalScope.launch {
                                                try {
                                                    withContext(Dispatchers.IO) {
                                                        dataunread.addAll(filterTodayData(convolist2))
                                                        dataunread =
                                                            ArrayList(dataunread.distinctBy { it.threadId }
                                                                .sortedWith(compareByDescending<Conversation> {
                                                                    isConversationPinned(it)
                                                                }.thenByDescending { it.date }))

                                                    }
                                                    withContext(Dispatchers.Main) {
                                                        adapterMainMassage.updateList(dataunread)
                                                    }
                                                } catch (e: Exception) {

                                                }
                                            }
                                        } else if (filterdialogselectionmain == "monthBtn") {
                                            var dataunread: ArrayList<Conversation> = arrayListOf()
                                            GlobalScope.launch {
                                                try {
                                                    withContext(Dispatchers.IO) {
                                                        val monthandyear =
                                                            extractMonthYearFromString(
                                                                formateddatemain
                                                            )
                                                        if (monthandyear != null) {
                                                            dataunread.addAll(
                                                                filterDataByMonthYear(
                                                                    convolist2,
                                                                    monthandyear.first,
                                                                    monthandyear.second
                                                                )
                                                            )
                                                        } else {
                                                            dataunread.addAll(convolist2)
                                                        }

                                                        dataunread =
                                                            ArrayList(dataunread.distinctBy { it.threadId }
                                                                .sortedWith(compareByDescending<Conversation> {
                                                                    isConversationPinned(it)
                                                                }.thenByDescending { it.date }))

                                                    }
                                                    withContext(Dispatchers.Main) {
                                                        adapterMainMassage.updateList(dataunread)
                                                    }
                                                } catch (e: Exception) {

                                                }
                                            }
                                        } else if (filterdialogselectionmain == "yearBtn") {
                                            var dataunread: ArrayList<Conversation> = arrayListOf()
                                            GlobalScope.launch {
                                                try {
                                                    withContext(Dispatchers.IO) {
                                                        val monthandyear =
                                                            extractYearFromString(formateddatemain)
                                                        if (monthandyear != null) {
                                                            dataunread.addAll(
                                                                filterDataByYear(
                                                                    convolist2, monthandyear
                                                                )
                                                            )
                                                        } else {
                                                            dataunread.addAll(convolist2)
                                                        }
                                                        dataunread =
                                                            ArrayList(dataunread.distinctBy { it.threadId }
                                                                .sortedWith(compareByDescending<Conversation> {
                                                                    isConversationPinned(it)
                                                                }.thenByDescending { it.date }))
                                                    }
                                                    withContext(Dispatchers.Main) {
                                                        adapterMainMassage.updateList(dataunread)
                                                    }
                                                } catch (e: Exception) {

                                                }
                                            }
                                        } else if (filterdialogselectionmain == "dateRangeBtn") {
                                            var dataunread: ArrayList<Conversation> = arrayListOf()
                                            GlobalScope.launch {
                                                try {
                                                    withContext(Dispatchers.IO) {
                                                        val (firstFormattedDate, secondFormattedDate) = extractAndFormatDateRange(
                                                            formateddatemain
                                                        )
                                                        if (firstFormattedDate != null && secondFormattedDate != null) {
                                                            dataunread.addAll(
                                                                filterDataByDateRange(
                                                                    convolist2,
                                                                    firstFormattedDate,
                                                                    secondFormattedDate
                                                                )
                                                            )
                                                        } else {
                                                            dataunread.addAll(convolist2)
                                                        }
                                                        dataunread =
                                                            ArrayList(dataunread.distinctBy { it.threadId }
                                                                .sortedWith(compareByDescending<Conversation> {
                                                                    isConversationPinned(it)
                                                                }.thenByDescending { it.date }))
                                                    }
                                                    withContext(Dispatchers.Main) {
                                                        adapterMainMassage.updateList(dataunread)
                                                    }
                                                } catch (e: Exception) {

                                                }
                                            }
                                        } else {
                                            convolist2 =
                                                ArrayList(convolist2.distinctBy { it.threadId }
                                                    .sortedWith(compareByDescending<Conversation> {
                                                        isConversationPinned(it)
                                                    }.thenByDescending { it.date }))

                                            adapterMainMassage.updateList(convolist2)
                                        }
                                        convolist2forpin = convolist2
                                    }
                                } catch (e: Exception) {

                                }
                            }
                        } else if (MessageType == "Transaction") {
                            isdefaultcatremove = false
                            GlobalScope.launch {
                              try{  withContext(Dispatchers.IO) {
                                    convolist2.addAll(getOrComputeTransactionMessages(allmessage))
                                }
                                withContext(Dispatchers.Main) {
                                    var dataunread: ArrayList<Conversation> = arrayListOf()
                                    if (filterdialogselectionmain == "unreadBtn") {
                                        dataunread.clear()
                                        GlobalScope.launch {
                                     try{       withContext(Dispatchers.IO) {
                                                convolist2.toList()
                                                    .forEachIndexed { index, conversation ->
                                                        if (conversation.isnewmessage == true) {
                                                            dataunread.add(conversation)
                                                        }
                                                    }
                                            }
                                            withContext(Dispatchers.Main) {

                                                dataunread =
                                                    ArrayList(dataunread.distinctBy { it.threadId }
                                                        .sortedWith(compareByDescending<Conversation> {
                                                            isConversationPinned(it)
                                                        }.thenByDescending { it.date }))

                                                adapterMainMassage.updateList(dataunread)
                                            }
                                        } catch (e: Exception) {

                                        }
                                        }
                                    } else if (filterdialogselectionmain == "defaultBtn") {
                                        var dataunread: ArrayList<Conversation> = arrayListOf()
                                        dataunread.addAll(convolist2)
                                        CoroutineScope(Dispatchers.Main).launch {
                                            dataunread =
                                                ArrayList(dataunread.distinctBy { it.threadId }
                                                    .sortedWith(compareByDescending<Conversation> {
                                                        isConversationPinned(it)
                                                    }.thenByDescending { it.date }))
                                            adapterMainMassage.updateList(dataunread)
                                        }
                                    } else if (filterdialogselectionmain == "todayBtn") {
                                        var dataunread: ArrayList<Conversation> = arrayListOf()
                                        GlobalScope.launch {
                                        try{    withContext(Dispatchers.IO) {
                                                dataunread.addAll(filterTodayData(convolist2))
                                                dataunread =
                                                    ArrayList(dataunread.distinctBy { it.threadId }
                                                        .sortedWith(compareByDescending<Conversation> {
                                                            isConversationPinned(it)
                                                        }.thenByDescending { it.date }))

                                            }
                                            withContext(Dispatchers.Main) {
                                                adapterMainMassage.updateList(dataunread)
                                            }
                                        } catch (e: Exception) {

                                        }   }
                                    } else if (filterdialogselectionmain == "monthBtn") {
                                        var dataunread: ArrayList<Conversation> = arrayListOf()
                                        GlobalScope.launch {
                                        try{    withContext(Dispatchers.IO) {
                                                val monthandyear =
                                                    extractMonthYearFromString(formateddatemain)
                                                if (monthandyear != null) {
                                                    dataunread.addAll(
                                                        filterDataByMonthYear(
                                                            convolist2,
                                                            monthandyear.first,
                                                            monthandyear.second
                                                        )
                                                    )
                                                } else {
                                                    dataunread.addAll(convolist2)
                                                }

                                                dataunread =
                                                    ArrayList(dataunread.distinctBy { it.threadId }
                                                        .sortedWith(compareByDescending<Conversation> {
                                                            isConversationPinned(it)
                                                        }.thenByDescending { it.date }))

                                            }
                                            withContext(Dispatchers.Main) {
                                                adapterMainMassage.updateList(dataunread)
                                            }
                                        } catch (e: Exception) {

                                        }    }
                                    } else if (filterdialogselectionmain == "yearBtn") {
                                        var dataunread: ArrayList<Conversation> = arrayListOf()
                                        GlobalScope.launch {
                                        try{    withContext(Dispatchers.IO) {
                                                val monthandyear =
                                                    extractYearFromString(formateddatemain)
                                                if (monthandyear != null) {
                                                    dataunread.addAll(
                                                        filterDataByYear(
                                                            convolist2, monthandyear
                                                        )
                                                    )
                                                } else {
                                                    dataunread.addAll(convolist2)
                                                }
                                                dataunread =
                                                    ArrayList(dataunread.distinctBy { it.threadId }
                                                        .sortedWith(compareByDescending<Conversation> {
                                                            isConversationPinned(it)
                                                        }.thenByDescending { it.date }))
                                            }
                                            withContext(Dispatchers.Main) {
                                                adapterMainMassage.updateList(dataunread)
                                            }
                                        } catch (e: Exception) {

                                        }    }
                                    } else if (filterdialogselectionmain == "dateRangeBtn") {
                                        var dataunread: ArrayList<Conversation> = arrayListOf()
                                        GlobalScope.launch {
                                        try{   withContext(Dispatchers.IO) {
                                                val (firstFormattedDate, secondFormattedDate) = extractAndFormatDateRange(
                                                    formateddatemain
                                                )
                                                if (firstFormattedDate != null && secondFormattedDate != null) {
                                                    dataunread.addAll(
                                                        filterDataByDateRange(
                                                            convolist2,
                                                            firstFormattedDate,
                                                            secondFormattedDate
                                                        )
                                                    )
                                                } else {
                                                    dataunread.addAll(convolist2)
                                                }
                                                dataunread =
                                                    ArrayList(dataunread.distinctBy { it.threadId }
                                                        .sortedWith(compareByDescending<Conversation> {
                                                            isConversationPinned(it)
                                                        }.thenByDescending { it.date }))
                                            }
                                            withContext(Dispatchers.Main) {
                                                adapterMainMassage.updateList(dataunread)
                                            }
                                        } catch (e: Exception) {

                                        }    }
                                    } else {

                                        convolist2 = ArrayList(convolist2.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))

                                        adapterMainMassage.updateList(convolist2)
                                    }
                                    convolist2forpin = convolist2
                                }
                            } catch (e: Exception) {

                            }       }

                        } else if (MessageType == "Offers") {
                            isdefaultcatremove = false
                            GlobalScope.launch {
                                withContext(Dispatchers.IO) {
                                    convolist2.addAll(getOrComputeOffersMessages(allmessage))
                                    val distinctConvos = convolist2
                                    convolist2.clear()
                                    convolist2.addAll(distinctConvos)
                                }
                                withContext(Dispatchers.Main) {
                                    var dataunread: ArrayList<Conversation> = arrayListOf()
                                    if (filterdialogselectionmain == "unreadBtn") {
                                        dataunread.clear()
                                        GlobalScope.launch {
                                            withContext(Dispatchers.IO) {
                                                convolist2.toList()
                                                    .forEachIndexed { index, conversation ->
                                                        if (conversation.isnewmessage == true) {
                                                            dataunread.add(conversation)
                                                        }
                                                    }
                                            }
                                            withContext(Dispatchers.Main) {
                                                dataunread =
                                                    ArrayList(dataunread.distinctBy { it.threadId }
                                                        .sortedWith(compareByDescending<Conversation> {
                                                            isConversationPinned(it)
                                                        }.thenByDescending { it.date }))
                                                adapterMainMassage.updateList(dataunread)
                                            }
                                        }
                                    } else if (filterdialogselectionmain == "defaultBtn") {
                                        var dataunread: ArrayList<Conversation> = arrayListOf()
                                        dataunread.addAll(convolist2)
                                        CoroutineScope(Dispatchers.Main).launch {
                                            dataunread =
                                                ArrayList(dataunread.distinctBy { it.threadId }
                                                    .sortedWith(compareByDescending<Conversation> {
                                                        isConversationPinned(it)
                                                    }.thenByDescending { it.date }))
                                            adapterMainMassage.updateList(dataunread)
                                        }
                                    } else if (filterdialogselectionmain == "todayBtn") {
                                        var dataunread: ArrayList<Conversation> = arrayListOf()
                                        GlobalScope.launch {
                                            withContext(Dispatchers.IO) {
                                                dataunread.addAll(filterTodayData(convolist2))
                                                dataunread =
                                                    ArrayList(dataunread.distinctBy { it.threadId }
                                                        .sortedWith(compareByDescending<Conversation> {
                                                            isConversationPinned(it)
                                                        }.thenByDescending { it.date }))

                                            }
                                            withContext(Dispatchers.Main) {
                                                adapterMainMassage.updateList(dataunread)
                                            }
                                        }
                                    } else if (filterdialogselectionmain == "monthBtn") {
                                        var dataunread: ArrayList<Conversation> = arrayListOf()
                                        GlobalScope.launch {
                                            withContext(Dispatchers.IO) {
                                                val monthandyear =
                                                    extractMonthYearFromString(formateddatemain)
                                                if (monthandyear != null) {
                                                    dataunread.addAll(
                                                        filterDataByMonthYear(
                                                            convolist2,
                                                            monthandyear.first,
                                                            monthandyear.second
                                                        )
                                                    )
                                                } else {
                                                    dataunread.addAll(convolist2)
                                                }

                                                dataunread =
                                                    ArrayList(dataunread.distinctBy { it.threadId }
                                                        .sortedWith(compareByDescending<Conversation> {
                                                            isConversationPinned(it)
                                                        }.thenByDescending { it.date }))

                                            }
                                            withContext(Dispatchers.Main) {
                                                adapterMainMassage.updateList(dataunread)
                                            }
                                        }
                                    } else if (filterdialogselectionmain == "yearBtn") {
                                        var dataunread: ArrayList<Conversation> = arrayListOf()
                                        GlobalScope.launch {
                                            withContext(Dispatchers.IO) {
                                                val monthandyear =
                                                    extractYearFromString(formateddatemain)
                                                if (monthandyear != null) {
                                                    dataunread.addAll(
                                                        filterDataByYear(
                                                            convolist2, monthandyear
                                                        )
                                                    )
                                                } else {
                                                    dataunread.addAll(convolist2)
                                                }
                                                dataunread =
                                                    ArrayList(dataunread.distinctBy { it.threadId }
                                                        .sortedWith(compareByDescending<Conversation> {
                                                            isConversationPinned(it)
                                                        }.thenByDescending { it.date }))
                                            }
                                            withContext(Dispatchers.Main) {
                                                adapterMainMassage.updateList(dataunread)
                                            }
                                        }
                                    } else if (filterdialogselectionmain == "dateRangeBtn") {
                                        var dataunread: ArrayList<Conversation> = arrayListOf()
                                        GlobalScope.launch {
                                            withContext(Dispatchers.IO) {
                                                val (firstFormattedDate, secondFormattedDate) = extractAndFormatDateRange(
                                                    formateddatemain
                                                )
                                                if (firstFormattedDate != null && secondFormattedDate != null) {
                                                    dataunread.addAll(
                                                        filterDataByDateRange(
                                                            convolist2,
                                                            firstFormattedDate,
                                                            secondFormattedDate
                                                        )
                                                    )
                                                } else {
                                                    dataunread.addAll(convolist2)
                                                }
                                                dataunread =
                                                    ArrayList(dataunread.distinctBy { it.threadId }
                                                        .sortedWith(compareByDescending<Conversation> {
                                                            isConversationPinned(it)
                                                        }.thenByDescending { it.date }))
                                            }
                                            withContext(Dispatchers.Main) {
                                                adapterMainMassage.updateList(dataunread)
                                            }
                                        }
                                    } else {
                                        convolist2 = ArrayList(convolist2.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))
                                        adapterMainMassage.updateList(convolist2)
                                    }
                                    convolist2forpin = convolist2
                                }
                            }
                        } else if (MessageType == "All Messages") {
                            isdefaultcatremove = false

                            GlobalScope.launch {
                                val tempConvList =
                                    ArrayList<Conversation>() // Create a temporary list
                                withContext(Dispatchers.IO) {
                                    allmessage.toList().forEachIndexed { index, conversation ->
                                        tempConvList.add(conversation)
                                    }

                                }
                                val distinctConvList =
                                    ArrayList(tempConvList.distinctBy { it.threadId }
                                        .toList()) // Remove duplicates
                                withContext(Dispatchers.Main) {
                                    convolist2.clear() // Clear convolist2 before adding elements
                                    convolist2.addAll(distinctConvList)
                                    var dataunread: ArrayList<Conversation> = arrayListOf()
                                    if (filterdialogselectionmain == "unreadBtn") {
                                        dataunread.clear()
                                        GlobalScope.launch {
                                          try{  withContext(Dispatchers.IO) {
                                                convolist2.toList()
                                                    .forEachIndexed { index, conversation ->
                                                        if (conversation.isnewmessage == true) {
                                                            dataunread.add(conversation)
                                                        }
                                                    }
                                            }
                                            withContext(Dispatchers.Main) {

                                                dataunread =
                                                    ArrayList(dataunread.distinctBy { it.threadId }
                                                        .sortedWith(compareByDescending<Conversation> {
                                                            isConversationPinned(it)
                                                        }.thenByDescending { it.date }))

                                                adapterMainMassage.updateList(dataunread)
                                            }
                                        } catch (e: Exception) {

                                        }       }
                                    } else if (filterdialogselectionmain == "defaultBtn") {
                                        var dataunread: ArrayList<Conversation> = arrayListOf()
                                        dataunread.addAll(convolist2)
                                        CoroutineScope(Dispatchers.Main).launch {
                                            dataunread =
                                                ArrayList(dataunread.distinctBy { it.threadId }
                                                    .sortedWith(compareByDescending<Conversation> {
                                                        isConversationPinned(it)
                                                    }.thenByDescending { it.date }))
                                            adapterMainMassage.updateList(dataunread)
                                        }
                                    } else if (filterdialogselectionmain == "todayBtn") {
                                        var dataunread: ArrayList<Conversation> = arrayListOf()
                                        GlobalScope.launch {
                                            withContext(Dispatchers.IO) {
                                                dataunread.addAll(filterTodayData(convolist2))
                                                dataunread =
                                                    ArrayList(dataunread.distinctBy { it.threadId }
                                                        .sortedWith(compareByDescending<Conversation> {
                                                            isConversationPinned(it)
                                                        }.thenByDescending { it.date }))

                                            }
                                            withContext(Dispatchers.Main) {
                                                adapterMainMassage.updateList(dataunread)
                                            }
                                        }
                                    } else if (filterdialogselectionmain == "monthBtn") {
                                        var dataunread: ArrayList<Conversation> = arrayListOf()
                                        GlobalScope.launch {
                                            withContext(Dispatchers.IO) {
                                                val monthandyear =
                                                    extractMonthYearFromString(formateddatemain)
                                                if (monthandyear != null) {
                                                    dataunread.addAll(
                                                        filterDataByMonthYear(
                                                            convolist2,
                                                            monthandyear.first,
                                                            monthandyear.second
                                                        )
                                                    )
                                                } else {
                                                    dataunread.addAll(convolist2)
                                                }

                                                dataunread =
                                                    ArrayList(dataunread.distinctBy { it.threadId }
                                                        .sortedWith(compareByDescending<Conversation> {
                                                            isConversationPinned(it)
                                                        }.thenByDescending { it.date }))

                                            }
                                            withContext(Dispatchers.Main) {
                                                adapterMainMassage.updateList(dataunread)
                                            }
                                        }
                                    } else if (filterdialogselectionmain == "yearBtn") {
                                        var dataunread: ArrayList<Conversation> = arrayListOf()
                                        GlobalScope.launch {
                                            withContext(Dispatchers.IO) {
                                                val monthandyear =
                                                    extractYearFromString(formateddatemain)
                                                if (monthandyear != null) {
                                                    dataunread.addAll(
                                                        filterDataByYear(
                                                            convolist2, monthandyear
                                                        )
                                                    )
                                                } else {
                                                    dataunread.addAll(convolist2)
                                                }
                                                dataunread =
                                                    ArrayList(dataunread.distinctBy { it.threadId }
                                                        .sortedWith(compareByDescending<Conversation> {
                                                            isConversationPinned(it)
                                                        }.thenByDescending { it.date }))
                                            }
                                            withContext(Dispatchers.Main) {
                                                adapterMainMassage.updateList(dataunread)
                                            }
                                        }
                                    } else if (filterdialogselectionmain == "dateRangeBtn") {
                                        var dataunread: ArrayList<Conversation> = arrayListOf()
                                        GlobalScope.launch {
                                            withContext(Dispatchers.IO) {
                                                val (firstFormattedDate, secondFormattedDate) = extractAndFormatDateRange(
                                                    formateddatemain
                                                )
                                                if (firstFormattedDate != null && secondFormattedDate != null) {
                                                    dataunread.addAll(
                                                        filterDataByDateRange(
                                                            convolist2,
                                                            firstFormattedDate,
                                                            secondFormattedDate
                                                        )
                                                    )
                                                } else {
                                                    dataunread.addAll(convolist2)
                                                }
                                                dataunread =
                                                    ArrayList(dataunread.distinctBy { it.threadId }
                                                        .sortedWith(compareByDescending<Conversation> {
                                                            isConversationPinned(it)
                                                        }.thenByDescending { it.date }))
                                            }
                                            withContext(Dispatchers.Main) {
                                                adapterMainMassage.updateList(dataunread)
                                            }
                                        }
                                    } else {
                                        convolist2 = ArrayList(convolist2.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))

                                        adapterMainMassage.updateList(convolist2)
                                    }
                                    convolist2forpin = convolist2
                                }
                            }
                        } else {
                            isdefaultcatremove = true
                            GlobalScope.launch {
                             try{   withContext(Dispatchers.IO) {
                                    allmessage.toList().forEachIndexed { index, conversation ->
                                        if (conversation.CategoryName == tabname) {
                                            convolist2.add(conversation)
                                        }
                                    }
                                    val distinctConvos = convolist2.distinctBy { it.threadId }
                                    convolist2.clear()
                                    convolist2.addAll(distinctConvos)
                                }
                                withContext(Dispatchers.Main) {
                                    var dataunread: ArrayList<Conversation> = arrayListOf()
                                    if (filterdialogselectionmain == "unreadBtn") {
                                        dataunread.clear()
                                        GlobalScope.launch {
                                            withContext(Dispatchers.IO) {
                                                convolist2.toList()
                                                    .forEachIndexed { index, conversation ->
                                                        if (conversation.isnewmessage == true) {
                                                            dataunread.add(conversation)
                                                        }
                                                    }
                                            }
                                            withContext(Dispatchers.Main) {

                                                dataunread =
                                                    ArrayList(dataunread.distinctBy { it.threadId }
                                                        .sortedWith(compareByDescending<Conversation> {
                                                            isConversationPinned(it)
                                                        }.thenByDescending { it.date }))

                                                adapterMainMassage.updateList(dataunread)
                                            }
                                        }
                                    } else if (filterdialogselectionmain == "defaultBtn") {
                                        var dataunread: ArrayList<Conversation> = arrayListOf()
                                        dataunread.addAll(convolist2)
                                        CoroutineScope(Dispatchers.Main).launch {
                                            dataunread =
                                                ArrayList(dataunread.distinctBy { it.threadId }
                                                    .sortedWith(compareByDescending<Conversation> {
                                                        isConversationPinned(it)
                                                    }.thenByDescending { it.date }))
                                            adapterMainMassage.updateList(dataunread)
                                        }
                                    } else if (filterdialogselectionmain == "todayBtn") {
                                        var dataunread: ArrayList<Conversation> = arrayListOf()
                                        GlobalScope.launch {
                                            withContext(Dispatchers.IO) {
                                                dataunread.addAll(filterTodayData(convolist2))
                                                dataunread =
                                                    ArrayList(dataunread.distinctBy { it.threadId }
                                                        .sortedWith(compareByDescending<Conversation> {
                                                            isConversationPinned(it)
                                                        }.thenByDescending { it.date }))

                                            }
                                            withContext(Dispatchers.Main) {
                                                adapterMainMassage.updateList(dataunread)
                                            }
                                        }
                                    } else if (filterdialogselectionmain == "monthBtn") {
                                        var dataunread: ArrayList<Conversation> = arrayListOf()
                                        GlobalScope.launch {
                                            withContext(Dispatchers.IO) {
                                                val monthandyear =
                                                    extractMonthYearFromString(formateddatemain)
                                                if (monthandyear != null) {
                                                    dataunread.addAll(
                                                        filterDataByMonthYear(
                                                            convolist2,
                                                            monthandyear.first,
                                                            monthandyear.second
                                                        )
                                                    )
                                                } else {
                                                    dataunread.addAll(convolist2)
                                                }

                                                dataunread =
                                                    ArrayList(dataunread.distinctBy { it.threadId }
                                                        .sortedWith(compareByDescending<Conversation> {
                                                            isConversationPinned(it)
                                                        }.thenByDescending { it.date }))

                                            }
                                            withContext(Dispatchers.Main) {
                                                adapterMainMassage.updateList(dataunread)
                                            }
                                        }
                                    } else if (filterdialogselectionmain == "yearBtn") {
                                        var dataunread: ArrayList<Conversation> = arrayListOf()
                                        GlobalScope.launch {
                                            withContext(Dispatchers.IO) {
                                                val monthandyear =
                                                    extractYearFromString(formateddatemain)
                                                if (monthandyear != null) {
                                                    dataunread.addAll(
                                                        filterDataByYear(
                                                            convolist2, monthandyear
                                                        )
                                                    )
                                                } else {
                                                    dataunread.addAll(convolist2)
                                                }
                                                dataunread =
                                                    ArrayList(dataunread.distinctBy { it.threadId }
                                                        .sortedWith(compareByDescending<Conversation> {
                                                            isConversationPinned(it)
                                                        }.thenByDescending { it.date }))
                                            }
                                            withContext(Dispatchers.Main) {
                                                adapterMainMassage.updateList(dataunread)
                                            }
                                        }
                                    } else if (filterdialogselectionmain == "dateRangeBtn") {
                                        var dataunread: ArrayList<Conversation> = arrayListOf()
                                        GlobalScope.launch {
                                            withContext(Dispatchers.IO) {
                                                val (firstFormattedDate, secondFormattedDate) = extractAndFormatDateRange(
                                                    formateddatemain
                                                )
                                                if (firstFormattedDate != null && secondFormattedDate != null) {
                                                    dataunread.addAll(
                                                        filterDataByDateRange(
                                                            convolist2,
                                                            firstFormattedDate,
                                                            secondFormattedDate
                                                        )
                                                    )
                                                } else {
                                                    dataunread.addAll(convolist2)
                                                }
                                                dataunread =
                                                    ArrayList(dataunread.distinctBy { it.threadId }
                                                        .sortedWith(compareByDescending<Conversation> {
                                                            isConversationPinned(it)
                                                        }.thenByDescending { it.date }))
                                            }
                                            withContext(Dispatchers.Main) {
                                                adapterMainMassage.updateList(dataunread)
                                            }
                                        }
                                    } else {

                                        convolist2 = ArrayList(convolist2.distinctBy { it.threadId }
                                            .sortedWith(compareByDescending<Conversation> {
                                                isConversationPinned(it)
                                            }.thenByDescending { it.date }))

                                        adapterMainMassage.updateList(convolist2)
                                    }
                                    convolist2forpin = convolist2
                                }
                            } catch (e: Exception) {

                            }   }
                        }
                    }
                    withContext(Dispatchers.Main) {

                        binding.allmassagerecycler.scrollToPosition(0)
//                binding.nomessagefoundchack = binding.allmassagerecycler.adapter?.let { it.itemCount <=0 }
                        binding.nomessagefoundchack =
                            binding.allmassagerecycler.adapter?.let { it.itemCount <= 0 }
                    }
                } catch (e: Exception) {

                }
            }
        } catch (e: Exception) {

        }


    }

    suspend fun getCodeReffrence() {
        delay(10)
        val isfromnotification = isfromnotification_New
        val firebasevalue = notify_vaule_New
        "getCodeReffrence setting <-----------------> 6 ${firebasevalue} ${isfromnotification}".log()
        if (isfromnotification) {
            if (firebasevalue == "subscription") {
//                startActivity(
//                    Intent(this, SubscriptionActivity::class.java).putExtra("AppOpen", "SettingsActivity")
//                )
                sendsubactivity()
            } else if (firebasevalue == "startchat") {
                Constants.isActivitychange = true
                startActivity(Intent(this, SelectContactActivity::class.java))
            } else if (firebasevalue == "managecategory") {
                startActivity(Intent(this, CategoryActivity::class.java))
            } else if (firebasevalue == "archive") {
                startActivity(Intent(this, ArchivedActivity::class.java))
            } else if (firebasevalue == "setting") {
                startActivity(Intent(this, SettingActivity::class.java))
            } else if (firebasevalue == "searchmessage") {
                startActivity(Intent(this, SearchActivity::class.java))
            } else if (firebasevalue == "selectcatallmessage") {
                selectautocate = true
                selectautocateName = "All Messages"
            } else if (firebasevalue == "selectcattransaction") {
                selectautocate = true
                selectautocateName = "Transaction"
            } else if (firebasevalue == "selectcatpersonal") {
                selectautocate = true
                selectautocateName = "Personal"
            } else if (firebasevalue == "selectcatoffers") {
                selectautocate = true
                selectautocateName = "Offers"
            } else if (firebasevalue == "selectcatotp") {
                selectautocate = true
                selectautocateName = "otp"
            }
        } else {
            "getCodeReffrence setting <-----------------> 77".log()
        }

    }

    private fun themeSetup() {

        val buttonBgColor = getProperPrimaryColor()
        val contentColor = getProperPrimaryColor().getContrastColor()
        binding.newmessage.setTextColor(contentColor)
        binding.newmessage.iconTint = ColorStateList.valueOf(contentColor)
        binding.newmessage.backgroundTintList = ColorStateList.valueOf(buttonBgColor)



        binding.setDefault.background = createOptionBackground(
            cornerSize = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._50sdp).toFloat(),
            fillColor = getProperPrimaryColor(),
            rippleColor = Color.WHITE.adjustAlpha(0.25f),
            showRipple = true,
        )


        binding.btntxt.setTextColor(getProperPrimaryColor().getContrastColor())
        binding.setDefaultClick.setTextColor(getProperPrimaryColor())


        setupDrawerTheme()
        setStatusbar()
        binding.view11.setHomeViewBgColor(0)
        binding.view13.setHomeViewBgColor(0)
        binding.view13.setHomeViewBgColor(0)
        binding.view500.setHomeViewBgColor(0)
        binding.view501.setHomeViewBgColor(0)
        binding.view502.setHomeViewBgColor(0)
        binding.view503.setHomeViewBgColor(0)
        binding.view504.setHomeViewBgColor(0)
        binding.view505.setHomeViewBgColor(0)
        binding.view506.setHomeViewBgColor(0)
        binding.mainMenu.binding.messageSelectionClose.HomescreenmessagelableNew(0)
        binding.mainMenu.binding.messageUnpin.HomescreenmessagelableNew(0)
        binding.mainMenu.binding.selectallmessage.HomescreenmessagelableNew(0)
        binding.mainMenu.binding.messagePin.HomescreenmessagelableNew(0)
        binding.mainMenu.binding.messageArchive.HomescreenmessagelableNew(0)
        binding.mainMenu.binding.messageDelete.HomescreenmessagelableNew(0)
        binding.mainMenu.binding.moreButtonMark.HomescreenmessagelableNew(0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.mainMenu.binding.messagebutton2.setTextSizeFullApp(getTextSizeForeNormal18MS())
        }
        binding.notistxt.setchatthemecolorTintTxtautodelete(0)
        binding.InAppSignatureShowMesageCount.SetSwitchColor(0)
        binding.themeChange.SetSwitchColor(0)

        val selectionCountColor =
            if (ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM) {
                MaterialColors.getColor(
                    binding.mainMenu.binding.selectioncount,
                    com.google.android.material.R.attr.colorOnSurface
                )
            } else {
                ContextCompat.getColor(
                    this,
                    if (ThemeModeManager.isDarkThemeActive(this)) R.color.white else R.color.black2
                )
            }
        binding.mainMenu.binding.selectioncount.setTextColor(selectionCountColor)
        if (SystemGeneratedIconSwitchAb) {
            SystemGeneratedIconSwitchAb = false
            drawableCache.clear()
            drawableCache2.clear()
        }

        renderCategoryChips()
        adapterMainMassage.notifyDataSetChanged()
        refreshHomeDrafts()
        getLanguage()
    }

    private fun setupDrawerTheme() {
        with(binding) {
            txt1.isSelected = true
            txt2.isSelected = true

            binding.ispro = BaseSharedPreferences(this@HomeABActivity).mIS_SUBSCRIBED!!

            if (BaseSharedPreferences(this@HomeABActivity).mIS_SUBSCRIBED!!) {
                txt1.setText(resources.getString(R.string.share_with_friends))
                txt2.setText(resources.getString(R.string.invite_friends_with_a_tap))
            } else {
                txt1.setText(resources.getString(R.string.Get_premium_today))
                txt2.setText(resources.getString(R.string.Remove_ads_and_unlock_all))
            }


            textView39.setchatthemecolorTintTxtFullapp(0)
            textView40Setting.setchatthemecolorTintTxtFullapp(0)
            textView40.setchatthemecolorTintTxtFullapp(0)
            swipeTxt.setchatthemecolorTintTxtFullapp(0)
            InSignatureShowMesageCount.setchatthemecolorTintTxtFullapp(0)
            textView20.setchatthemecolorTintTxtFullapp(0)
            textView19.setchatthemecolorTintTxtFullapp(0)
            txt44.setchatthemecolorTintTxtFullapp(0)
            txt45.setchatthemecolorTintTxtFullapp(0)
            FontSize.setchatthemecolorTintTxtFullapp(0)
            txt47.setchatthemecolorTintTxtFullapp(0)
            navDrawer.setchatthemecolorforHomeScreenBg(0)
            icFlColorThemeTxt.setchatthemecolorTintImfullappforrecyler(0)
            icFlFontSizeTxt.setchatthemecolorTintImfullappforrecyler(0)
            icFlRecycleBiTxt.setchatthemecolorTintImfullappforrecyler(0)

            ColorThemeimageView19.setchatthemecolorTintImfullappforrecyler(0)
            ColorThemeimageView17.setchatthemecolorTintImfullappforrecyler(0)


            imageView16.setchatthemecolorTintImfullappforrecyler(0)
            backupAndRestoreImageView16.setchatthemecolorTintImfullappforrecyler(0)
            scheduleMessageImageView16.setchatthemecolorTintImfullappforrecyler(0)
            icFlSignatureSwipTxt.setchatthemecolorTintImfullappforrecyler(0)
            icFlConversationSwipTxt.setchatthemecolorTintImfullappforrecyler(0)
            icFlLanguageBtn.setchatthemecolorTintImfullappforrecyler(0)
            backBtn.setchatthemecolorTintImfullappforrecyler(0)
            icFlSettingBtn.setchatthemecolorTintImfullappforrecyler(0)
            icFlSettingBtn2.setchatthemecolorTintImfullappforrecyler(0)
            LanguageBtnCard.setlayoutbackgroundcolordrawer(0)
            ConversationSwipBtnCard.setlayoutbackgroundcolordrawer(0)
            conversationSignatureBtnCardShowMesageCount.setlayoutbackgroundcolordrawer(0)
            scheduleMessageBtnCard.setlayoutbackgroundcolordrawer(0)
            backupAndRestoreBtnCardNew.setlayoutbackgroundcolordrawer(0)
            preandsecBtnCard.setlayoutbackgroundcolordrawer(0)
            ColorThemeBtnCard.setlayoutbackgroundcolordrawer(0)
            FontSizeBtnCard.setlayoutbackgroundcolordrawer(0)
            RecycleBinBtnCard.setlayoutbackgroundcolordrawer(0)

        }

        val isSystemMode = ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM
        val isDarkTheme = ThemeModeManager.isDarkThemeActive(this)
        val surfaceColor =
            MaterialColors.getColor(binding.main, com.google.android.material.R.attr.colorSurface)
        val onSurfaceColor =
            MaterialColors.getColor(binding.main, com.google.android.material.R.attr.colorOnSurface)
        val surfaceContainerColor = MaterialColors.getColor(
            binding.main, com.google.android.material.R.attr.colorSurfaceContainerHighest
        )
        val useDarkIcons = if (isSystemMode) {
            MaterialColors.isColorLight(surfaceColor)
        } else {
            !isDarkTheme
        }

        with(binding) {
            if (BaseSharedPreferences(this@HomeABActivity).mIS_SUBSCRIBED!!) {
                imageView41.setImageResource(
                    if (useDarkIcons) R.drawable.drawer_ab_navigation_sub else R.drawable.drawer_ab_navigation_sub_2
                )
            } else {
                imageView41.setImageResource(
                    if (useDarkIcons) R.drawable.drawer_ab_navigation else R.drawable.drawer_ab_navigation_2
                )
            }

            val toolbarTextColor = if (isSystemMode) {
                onSurfaceColor
            } else {
                ContextCompat.getColor(
                    this@HomeABActivity, if (isDarkTheme) R.color.white else R.color.black2
                )
            }
            mainMenu.binding.messagebutton2.setTextColor(toolbarTextColor)
//            mainMenu.binding.textclose.setColorFilter(getProperTextColor())
            mainMenu.binding.searchMessageBg.setColorFilter(getProperTextColor())
            mainMenu.binding.searchBarCard.setCardBackgroundColor(
                blendWithBaseColor(getProperPrimaryColor(), surfaceColor, 0.06f)
            )
            mainMenu.binding.homeSearchInput.setTextColor(toolbarTextColor)
            mainMenu.binding.homeSearchInput.setHintTextColor(toolbarTextColor.adjustAlpha(0.45f))
//            configureHomeSearchViewIcons(mainMenu.binding.homeSearchView)
//            getHomeSearchInput(mainMenu.binding.homeSearchView)?.let { applyHomeSearchInputStyling(it) }


            notistxt.setTextColor(toolbarTextColor)

            if (isSystemMode) {
                topMessageBackground.setBackgroundColor(surfaceColor)
                SettingBtnCard.setBackgroundColor(surfaceColor)
                card2.setCardBackgroundColor(surfaceContainerColor)
                card1.setCardBackgroundColor(surfaceContainerColor)
            } else if (isDarkTheme) {
                topMessageBackground.setBackgroundColor(Color.parseColor("#282727"))
                SettingBtnCard.setBackgroundColor(Color.parseColor("#282727"))
                card2.setCardBackgroundColor(Color.parseColor("#242525"))
                card1.setCardBackgroundColor(Color.parseColor("#242525"))
            } else {
                topMessageBackground.setBackgroundColor(Color.parseColor("#E8F1FE"))
                SettingBtnCard.setBackgroundColor(Color.parseColor("#E8F1FE"))
                card2.setCardBackgroundColor(Color.parseColor("#F3F8FB"))
                card1.setCardBackgroundColor(Color.parseColor("#F3F8FB"))
            }

            mainMenu.binding.icDrawerAbButton.setImageResource(
                if (useDarkIcons) R.drawable.home_material_ic_menu_rounded_dark else R.drawable.home_material_ic_menu_rounded_light
            )
            mainMenu.binding.searchMessageBg.setImageResource(
                if (useDarkIcons) R.drawable.home_material_ic_search_rounded_dark else R.drawable.home_material_ic_search_rounded_light
            )
            mainMenu.binding.moreButton.setImageResource(
                if (useDarkIcons) R.drawable.home_material_ic_more_vert_rounded_dark else R.drawable.home_material_ic_more_vert_rounded_light
            )
        }
    }

    private fun getLanguage() {
        val lanlist: ArrayList<Languagemodel> = arrayListOf()
        lanlist.add(
            Languagemodel(
                language = "English", languagecode = "en", languagereal = "English"
            )
        )
        lanlist.add(Languagemodel(language = "हिन्दी", languagecode = "hi", languagereal = "Hindi"))
        lanlist.add(
            Languagemodel(
                language = "Spanish", languagecode = "es", languagereal = "Española"
            )
        )
        lanlist.add(
            Languagemodel(
                language = "French", languagecode = "fr", languagereal = "Français"
            )
        )
        lanlist.add(
            Languagemodel(
                language = "Portuguese", languagecode = "pt", languagereal = "Português"
            )
        )
        lanlist.add(
            Languagemodel(
                language = "German", languagecode = "de", languagereal = "Deutsch"
            )
        )
        lanlist.add(Languagemodel(language = "Arabic", languagecode = "ar", languagereal = "عربي"))
        lanlist.add(
            Languagemodel(
                language = "Japanese", languagecode = "ja", languagereal = "日本語"
            )
        )
        lanlist.add(Languagemodel(language = "Korean", languagecode = "ko", languagereal = "한국인"))
        lanlist.add(
            Languagemodel(
                language = "Indonesian", languagecode = "in", languagereal = "bahasa Indonesia"
            )
        )
        lanlist.add(
            Languagemodel(
                language = "Swedish", languagecode = "sv", languagereal = "svenska"
            )
        )
        lanlist.add(
            Languagemodel(
                language = "Turkish", languagecode = "tr", languagereal = "Türkçe"
            )
        )
        try {
            for (index in lanlist.indices) {
                val languagemodel = lanlist[index]
                if (languagemodel.languagecode == config.SelectedLanguage) {
                    binding.appLanguageTxt.setText(languagemodel.language)
                }
            }
        } catch (_: Exception) {
        }
//        config.activeThemeSelection, config.isSystemThemeMode

        val themeLabel = when (ThemeModeManager.getThemeMode(this)) {
            ThemeModeManager.MODE_SYSTEM -> resources.getString(R.string.system_default)
            ThemeModeManager.MODE_DARK -> resources.getString(R.string.Dark)
            else -> resources.getString(R.string.light_mode)
        }
        binding.ColorThemeUserCount.text = themeLabel
        binding.txt45.text = themeLabel


//        if (config.isSystemThemeMode) {
//            binding.ColorThemeUserCount.setText(resources.getString(R.string.system_mode))
//        } else {
//            when (config.activeThemeSelection) {
//                1 -> {
//                    binding.ColorThemeUserCount.setText(resources.getString(R.string.Day_Mode))
//                }
//
//                2 -> {
//                    binding.ColorThemeUserCount.setText(resources.getString(R.string.Dark_Mode))
//                }
//
//                3 -> {
//                    binding.ColorThemeUserCount.setText(resources.getString(R.string.Night_Mode))
//                }
//
//                4 -> {
//                    binding.ColorThemeUserCount.setText(resources.getString(R.string.system_mode))
//                }
//            }
//        }
    }

    fun starrationgdialog() {

//        ReviewManagerRE().review(fortesting = false, this,
//            onSuccess = {
//                Log.d(TAG, "starrationgdialog: <----------> onSuccess")
//                finishAffinity()
//            }, onFailure = { error ->
//                Log.d(TAG, "starrationgdialog: <----------> onFailure ${error}")
//                finishAffinity()
//            })

        config.dialogshowcount = 0
//        val builder = MaterialAlertDialogBuilder(this)
//        val inflater = layoutInflater
//        val binding = RatingDialogLayoutBinding.inflate(inflater)
//        val view = binding.root
//        builder.setCancelable(false)
//            .setTitle(resources.getString(R.string.Rate_this_App))
//            .setMessage(resources.getString(R.string.message_rating))
//            .setPositiveButton("Submit") { dialog, which -> }
//            .setNegativeButton("Cancel") { dialog, which ->
//                dialog.cancel()
//            }
//        builder.setView(view)
//        val dialog = builder.create()
//        dialog.show()
//        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
//            config.ratingdialogshow = false
//            if (binding.ratingBar.rating >= 4) {
//                val appPackageName: String = packageName
//                try {
//                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
//                } catch (anfe: ActivityNotFoundException) {
//                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
//                }
//            } else {
//                toastMess(resources.getString(R.string.Thanks_for_Rating))
//            }
//            dialog.cancel()
//        }
    }


    suspend fun AutoNotificationStart() {
        delay(10)
        if (isSPlus()) {
            val alarmManager: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                try {
                    val canScheduleExactAlarms = alarmManager.canScheduleExactAlarms()
                    if (canScheduleExactAlarms) {
                        if (config.isAutoNotificationStart) {
                            config.isAutoNotificationStart = false
                            val intervalMillis = 1 * 24 * 60 * 60 * 1000L
                            AutonotificationStat(0, intervalMillis, 1)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Handle the exception appropriately
                }
            } else {
                // For devices below API level 31
                if (config.isAutoNotificationStart) {
                    config.isAutoNotificationStart = false
                    AutonotificationStat(0, 1 * 24 * 60 * 60 * 1000L, 1)
                }
            }
        } else {
            if (config.isAutoNotificationStart) {
                config.isAutoNotificationStart = false
                AutonotificationStat(0, 1 * 24 * 60 * 60 * 1000L, 1)
            }
        }
    }


    private fun routeBackWithDeepLink() {
        val intent = Intent(this, HomeABActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("from", "homeoverlay")
        }
        startActivity(intent)

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun showfilterdialog() {
        val dialog by lazy {
            Message_filter_dialog()
        }
        dialog.show(supportFragmentManager, "Message_filter_dialog")
        dialog.dialogdismiss = { filterdialogselection, formateddate, selecteddateinmillisecound ->
            filterdialogselectionmain = filterdialogselection
            formateddatemain = formateddate
            selecteddateinmillisecoundmain = selecteddateinmillisecound.toLong()
            if (filterdialogselection == "unreadBtn") {
                var dataunread: ArrayList<Conversation> = arrayListOf()
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        convolist2.toList().forEachIndexed { index, conversation ->
                            if (conversation.isnewmessage == true) {
                                dataunread.add(conversation)
                            }
                        }
                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                            .sortedWith(compareByDescending<Conversation> {
                                isConversationPinned(it)
                            }.thenByDescending { it.date }))
                    }
                    withContext(Dispatchers.Main) {
                        adapterMainMassage.updateList(dataunread)
                    }
                }
            } else if (filterdialogselection == "defaultBtn") {
                var dataunread: ArrayList<Conversation> = arrayListOf()
                dataunread.addAll(convolist2)
                CoroutineScope(Dispatchers.Main).launch {
                    dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                        .sortedWith(compareByDescending<Conversation> {
                            isConversationPinned(it)
                        }.thenByDescending { it.date }))
                    adapterMainMassage.updateList(dataunread)
                }
            } else if (filterdialogselection == "todayBtn") {
                var dataunread: ArrayList<Conversation> = arrayListOf()
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        dataunread.addAll(filterTodayData(convolist2))
                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                            .sortedWith(compareByDescending<Conversation> {
                                isConversationPinned(it)
                            }.thenByDescending { it.date }))

                    }
                    withContext(Dispatchers.Main) {
                        adapterMainMassage.updateList(dataunread)
                    }
                }
            } else if (filterdialogselection == "monthBtn") {
                var dataunread: ArrayList<Conversation> = arrayListOf()
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        val monthandyear = extractMonthYearFromString(formateddate)
                        if (monthandyear != null) {
                            dataunread.addAll(
                                filterDataByMonthYear(
                                    convolist2, monthandyear.first, monthandyear.second
                                )
                            )
                        } else {
                            dataunread.addAll(convolist2)
                        }

                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                            .sortedWith(compareByDescending<Conversation> {
                                isConversationPinned(it)
                            }.thenByDescending { it.date }))

                    }
                    withContext(Dispatchers.Main) {
                        adapterMainMassage.updateList(dataunread)
                    }
                }
            } else if (filterdialogselection == "yearBtn") {
                var dataunread: ArrayList<Conversation> = arrayListOf()
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        val monthandyear = extractYearFromString(formateddate)
                        if (monthandyear != null) {
                            dataunread.addAll(
                                filterDataByYear(
                                    convolist2, monthandyear
                                )
                            )
                        } else {
                            dataunread.addAll(convolist2)
                        }
                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                            .sortedWith(compareByDescending<Conversation> {
                                isConversationPinned(it)
                            }.thenByDescending { it.date }))
                    }
                    withContext(Dispatchers.Main) {
                        adapterMainMassage.updateList(dataunread)
                    }
                }
            } else if (filterdialogselection == "dateRangeBtn") {
                var dataunread: ArrayList<Conversation> = arrayListOf()
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        val (firstFormattedDate, secondFormattedDate) = extractAndFormatDateRange(
                            formateddatemain
                        )
                        if (firstFormattedDate != null && secondFormattedDate != null) {
                            dataunread.addAll(
                                filterDataByDateRange(
                                    convolist2, firstFormattedDate, secondFormattedDate
                                )
                            )
                        } else {
                            dataunread.addAll(convolist2)
                        }
                        dataunread = ArrayList(dataunread.distinctBy { it.threadId }
                            .sortedWith(compareByDescending<Conversation> {
                                isConversationPinned(it)
                            }.thenByDescending { it.date }))
                    }
                    withContext(Dispatchers.Main) {
                        adapterMainMassage.updateList(dataunread)
                    }
                }
            }
        }
    }

    fun filterTodayData(data: List<Conversation>): List<Conversation> {
        val snapshot = data.toList()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfToday = calendar.timeInMillis  // Start of today (midnight in millis)
        calendar.add(Calendar.DAY_OF_YEAR, 1)  // Move to the next day
        val endOfToday = calendar.timeInMillis - 1  // End of today (just before midnight)
        return snapshot.filter { it.time in startOfToday..endOfToday }
    }


    fun filterDataByMonthYear(data: List<Conversation>, month: Int, year: Int): List<Conversation> {
        val snapshot = data.toList()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfMonth = calendar.timeInMillis
        return snapshot.filter { it.time in startOfMonth..endOfMonth }
    }

    fun extractMonthYearFromString(input: String): Pair<Int, Int>? {
        if (input.startsWith("Month: ")) {
            val monthYearStr = input.removePrefix("Month: ").trim()
            val parts = monthYearStr.split("/")
            if (parts.size == 2) {
                val month = parts[0].toIntOrNull()
                val year = parts[1].toIntOrNull()
                if (month != null && year != null) {
                    return Pair(month, year)
                }
            }
        }
        return null
    }

    fun filterDataByYear(data: List<Conversation>, year: Int): List<Conversation> {
        val snapshot = data.toList()
        val calendar = Calendar.getInstance()
        calendar.set(year, Calendar.JANUARY, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfYear = calendar.timeInMillis
        calendar.set(year, Calendar.DECEMBER, 31, 23, 59, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfYear = calendar.timeInMillis
        return snapshot.filter { it.time in startOfYear..endOfYear }
    }

    fun extractYearFromString(input: String): Int? {
        if (input.startsWith("Year: ")) {
            val yearStr = input.removePrefix("Year: ").trim()
            return yearStr.toIntOrNull()
        }
        return null
    }


    fun extractAndFormatDateRange(dateRange: String): Pair<String?, String?> {
        val dateString = dateRange.replace("Date Range: ", "")
        val dateParts = dateString.split(" - ")
        if (dateParts.size != 2) {
            println("Invalid date range format: $dateRange")
            return Pair(null, null) // Return nulls to indicate an error
        }
        val firstDateString = dateParts[0]
        val secondDateString = dateParts[1]
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            val firstDate = dateFormat.parse(firstDateString)
            val secondDate = dateFormat.parse(secondDateString)
            val formattedFirstDate = dateFormat.format(firstDate)
            val formattedSecondDate = dateFormat.format(secondDate)
            Pair(formattedFirstDate, formattedSecondDate)
        } catch (e: Exception) {
            println("Error parsing dates: $e")
            Pair(null, null) // Return nulls to indicate a failure in parsing
        }
    }

    fun filterDataByDateRange(
        data: List<Conversation>,
        startDate: String,
        endDate: String,
    ): List<Conversation> {
        val snapshot = data.toList()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startDate = dateFormat.parse(startDate) ?: return emptyList()
        val endDate = dateFormat.parse(endDate) ?: return emptyList()
        return snapshot.filter { dateStr ->
            val date = dateFormat.parse(longToDate(dateStr.time!!))
            date != null && !date.before(startDate) && !date.after(endDate)
        }
    }

    fun longToDate(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return format.format(date)
    }


    /*

open app - set default app -
 remove SMS & Contact permission from setting - open app -
 show continue _ click - allow SMS & contact permission from system dialogue
  - check response it's take too much time for load message

    * */
    private fun handleDefaultSmsClick(from: String) {
        val isDefaultSms = roleDefaultCheck()
        Log.d("jfbdvb", "handleDefaultSmsClick: ---> o1 $isDefaultSms ${hasCoreSmsPermissions()}")
        if (isDefaultSms && hasCoreSmsPermissions()) {
            return
        }
        Log.d("jfbdvb", "handleDefaultSmsClick: ---> o2 $from")
        fromSetasdefault = from
        if (config.defaultSmsDialogSuppressed) {
            showDefaultSmsSettingsDialog(from)
            return
        }
        Log.d("jfbdvb", "handleDefaultSmsClick: ---> o3")
        loadMessages(true, 1)
    }

    private fun showDefaultSmsSettingsDialog(from: String) {
        openDefaultSmsInstruction(from)
        return
        val view = DialogMessageBinding.inflate(layoutInflater)

        val isSystemMode = ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM
        val forceDark = ThemeModeManager.isDarkThemeActive(this)
        val bodyTextColor = if (isSystemMode) {
            MaterialColors.getColor(binding.main, com.google.android.material.R.attr.colorOnSurface)
        } else if (forceDark) {
            Color.WHITE
        } else {
            Color.BLACK
        }

        view.message.text = getString(R.string.default_sms_settings_message)
        view.message.setTextColor(bodyTextColor)

        val dialog = MaterialAlertDialogBuilder(
            this, getMaterialDialogTheme()
        ).setTitle(R.string.Set_as_Default_SMS_app).setView(view.root)
            .setPositiveButton(R.string.set_as_default) { _, _ ->
                openDefaultSmsInstruction(from)
            }.setNegativeButton(R.string.cancel, null).create()
        val backgroundColor = getDialogBackgroundColor()

        val shapeDrawable = MaterialShapeDrawable().apply {
            fillColor = ColorStateList.valueOf(backgroundColor)
            shapeAppearanceModel = ShapeAppearanceModel.builder().setAllCornerSizes(80f).build()
        }

        dialog.window?.setBackgroundDrawable(shapeDrawable)

        dialog.setOnShowListener {

//            val backgroundColor = getDialogBackgroundColor()
            val positiveColor = if (isSystemMode) {
                MaterialColors.getColor(
                    binding.main, R.attr.colorPrimary
                )
            } else {
                ContextCompat.getColor(this, R.color.appcolor)
            }
            val negativeColor = if (isSystemMode) {
                MaterialColors.getColor(
                    binding.main, com.google.android.material.R.attr.colorOnSurfaceVariant
                )
            } else if (forceDark) {
                Color.LTGRAY
            } else {
                Color.GRAY
            }
//            val shapeDrawable = MaterialShapeDrawable().apply {
//                fillColor = ColorStateList.valueOf(backgroundColor)
//                shapeAppearanceModel = ShapeAppearanceModel.builder()
//                    .setAllCornerSizes(80f)
//                    .build()
//            }
//
//            dialog.window?.setBackgroundDrawable(shapeDrawable)


            // Buttons
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(positiveColor)

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(negativeColor)

            // Title
            dialog.findViewById<TextView>(
                com.google.android.material.R.id.alertTitle
            )?.setTextColor(bodyTextColor)
        }

        dialog.show()
    }

    private fun Activity.openDefaultSmsInstruction(from: String) {
        Log.e("aaaa", "openDefaultSmsInstruction-from: $from")
        fromSetasdefault = from

        val intent = Intent(this, DefaultSmsInstructionActivity::class.java)
        startActivityForResult(intent, DEFAULT_SMS_INSTRUCTION_REQUEST)
    }

    private fun openDefaultSmsSettings(from: String) {

        fromSetasdefault = from
        val intent = try {
            Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        } catch (_: Exception) {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
        }
        try {
            msOfDefault = SystemClock.elapsedRealtime()
            startActivityForResult(intent, MAKE_DEFAULT_APP_REQUEST)
            gooutside = true
            return
        } catch (_: Exception) {
            val fallback = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }


            startActivityForResult(fallback, MAKE_DEFAULT_APP_REQUEST)
            gooutside = true
            return
        }
    }

    private fun loadMessages(byUser: Boolean, num: Int) {
        if (roleDefaultCheck() && hasCoreSmsPermissions()) {
            startSyncIfNeeded(3)
            return
        }
        if (isQPlus()) {
            "loadMessages permission <-----------------------> 1 -$byUser/--$num".log()
            val roleManager = getSystemService(RoleManager::class.java)
            if (roleManager!!.isRoleAvailable(RoleManager.ROLE_SMS)) {
                if (roleManager.isRoleHeld(RoleManager.ROLE_SMS)) {
                    "loadMessages permission <-----------------------> 3".log()
                    Log.d("jigar <----> ", "loadMessages: <---------> 3")
                    openAdOnO(true, 1200)
                    askPermissions(byUser)
                } else {
                    if (byUser) {
                        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)
                        msOfDefault = SystemClock.elapsedRealtime()
                        startActivityForResult(intent, MAKE_DEFAULT_APP_REQUEST)
                        gooutside = true
                        return
                    }
                    if (baseConfig.flow2_3_firstcomeDone) {
//                    if (!config.hasCheckedDefaultSms) {
                        config.hasCheckedDefaultSms = true
                        "loadMessages permission <-----------------------> 4".log()
                        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)
                        startActivityForResult(intent, MAKE_DEFAULT_APP_REQUEST)
                        gooutside = true
                    } else {
                        openAdOnO(true, 1200)
                        askPermissions(byUser)
                    }
                }
            } else {
                "loadMessages permission <-----------------------> 5".log()
                toast(com.simplemobiletools.commons.R.string.unknown_error_occurred)
                finish()
            }
        } else {
            if (roleDefaultCheck()) {
                "loadMessages permission <-----------------------> 6".log()
                openAdOnO(true, 1200)
                askPermissions(byUser)
            } else {
                if (byUser) {
                    "loadMessages permission <-----------------------> 7".log()
                    val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
                    msOfDefault = SystemClock.elapsedRealtime()
                    startActivityForResult(intent, MAKE_DEFAULT_APP_REQUEST)
                    gooutside = true
                    return
                }

//                if (!config.hasCheckedDefaultSms) {
                if (baseConfig.flow2_3_firstcomeDone) {
                    config.hasCheckedDefaultSms = true

                    "loadMessages permission <-----------------------> 7".log()
                    val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)

                    startActivityForResult(intent, MAKE_DEFAULT_APP_REQUEST)
                    gooutside = true
//                    }
                } else {
                    openAdOnO(true, 1200)
                    askPermissions(byUser)
                }
            }
        }
    }

    /*   private fun askPermissions(byUser: Boolean = false) {

           if (isAskingPermissions) {
               "loadMessages permission <-----------------------> skipping, already asking".log()
               return
           }
   //        if (!isFLow2_3) {
           isAskingPermissions = true
           handlePermission(PERMISSION_READ_SMS) { readGranted ->
               "loadMessages permission <-----------------------> 13".log()
               if (readGranted) {
                   "loadMessages permission <-----------------------> 14".log()
                   Handler(mainLooper).postDelayed({
                       handlePermission(PERMISSION_SEND_SMS) { sendGranted ->
                           "loadMessages permission <-----------------------> 15".log()
                           if (sendGranted) {
                               val proceedToContacts: () -> Unit = {
                                   "loadMessages permission <-----------------------> 16".log()
                                   Handler(mainLooper).postDelayed({
                                       handlePermission(PERMISSION_READ_CONTACTS) { contactsGranted ->
                                           "loadMessages permission <-----------------------> 17".log()
                                           if (contactsGranted) {
                                               handleNotificationPermission { granted ->
                                                   "loadMessages permission <-----------------------> 18".log()
                                                   if (!granted) {
                                                       "loadMessages permission <-----------------------> 19".log()
                                                       PermissionRequiredDialog(
                                                           activity = this,
                                                           textId = R.string.allow_notifications_incoming_messages_per,
                                                           positiveActionCallback = {
                                                               openNotificationSettings()
                                                           },
                                                       )
                                                   } else {
                                                       baseConfig.secondtimeflowuseFLow = true

                                                       "loadMessages permission <-----------------------> 20 $isSyncInProgress=".log()

                                                   }
                                                   isAskingPermissions = false
                                               }
   //                                            adsLoadAndShowTopNativeBanner()
                                               startSyncIfNeeded(2)
                                               checkOverlayPermissionOnHomeFirst()
                                           } else {
                                               "loadMessages permission <-----------------------> 21_contacts_denied".log()
                                               isAskingPermissions = false
                                           }
                                       }
                                   }, 200)
                               }

                               if (ContextCompat.checkSelfPermission(
                                       this, Manifest.permission.RECEIVE_SMS
                                   ) == PackageManager.PERMISSION_GRANTED
                               ) {
                                   proceedToContacts()
                               } else {
                                   actionOnPermission = { receiveGranted ->
                                       if (receiveGranted) {
                                           proceedToContacts()
                                       } else {
                                           baseConfig.allPermissionRlationalDone =
                                               if (this@HomeABActivity.roleDefaultCheck()) true else false
                                           "loadMessages permission <-----------------------> 21_receive_denied".log()
                                           isAskingPermissions = false
                                           updateRelationalPermissionSettingFlow(byUser)
                                       }
                                   }
                                   ActivityCompat.requestPermissions(
                                       this,
                                       arrayOf(Manifest.permission.RECEIVE_SMS),
                                       GENERIC_PERM_HANDLER
                                   )
                               }
                           } else {
                               "loadMessages permission <-----------------------> 21".log()
                               isAskingPermissions = false
                               baseConfig.allPermissionRlationalDone =
                                   if (this@HomeABActivity.roleDefaultCheck()) true else false
                               updateRelationalPermissionSettingFlow(byUser)
                           }
                       }
                   }, 200)
               } else {
                   "loadMessages permission <-----------------------> 22".log()
                   isAskingPermissions = false
                   baseConfig.allPermissionRlationalDone =
                       if (this@HomeABActivity.roleDefaultCheck()) true else false
                   updateRelationalPermissionSettingFlow(byUser)
               }
   //            }
           }
       }*/
    fun getPermissionString(permissionId: Int): String {
        return when (permissionId) {
            PERMISSION_READ_SMS -> Manifest.permission.READ_SMS
            PERMISSION_SEND_SMS -> Manifest.permission.SEND_SMS
            PERMISSION_RECEIVE_SMS -> Manifest.permission.RECEIVE_SMS
            PERMISSION_READ_CONTACTS -> Manifest.permission.READ_CONTACTS
            PERMISSION_READ_PHONE_STATE -> Manifest.permission.READ_PHONE_STATE
            else -> throw IllegalArgumentException("Unknown permission")
        }
    }

    private fun askPermissions(byUser: Boolean = false) {

        if (isAskingPermissions) {
            "loadMessages permission <-----------------------> skipping, already asking".log()
            return
        }

        isAskingPermissions = true
        startPermissionFlow(byUser)
    }

    fun handleNotificationPermission(callback: (granted: Boolean) -> Unit) {
//        if (!isTiramisuPlus()) {
        callback(true)
//        }
//        else {
//            handlePermission(PERMISSION_POST_NOTIFICATIONS) { granted ->
//                callback(granted)
//            }
//        }
    }

    fun showdefaultbutton(show: Boolean) {
        if (show) {
            binding.newmessage.visible()
            binding.tabLayout.visible()
            binding.mainMenu.binding.lnrTopHomeNewSearchArea.visible()
        } else {
            val adapterListSize =
                if (::adapterMainMassage.isInitialized) adapterMainMassage.list.size else -1

            if (isFLow2 && hasCoreSmsPermissions()) {
                binding.newmessage.visible()
                binding.tabLayout.visible()
                binding.defaultlayer.gone()
            } else if (isFLow1 && hasCoreSmsPermissions() && roleDefaultCheck()) {
                binding.tabLayout.visible()
                binding.defaultlayer.gone()
                isdefault = true
                binding.notdefault = false
                binding.newmessage.visible()

            } else {
                binding.newmessage.gone()
                binding.tabLayout.gone()
                binding.defaultlayer.visible()
                isdefault = false
                binding.notdefault = true
            }
            if (isFLow2 && adapterListSize > 0 && convolist2.size > 0) {
                binding.tabLayout.visible()
            } else if (isFLow1 && hasCoreSmsPermissions() && roleDefaultCheck()) {

            } else {

            }
            binding.mainMenu.binding.lnrTopHomeNewSearchArea.visible()
        }

    }

    private fun loadBannerSafe() {
        scheduleHomeBannerLoad()
    }


    private fun loadBannerInternal() {
        maybeShowHomeBanner("manual")
    }


    // ✅ Step 5: Cleanup (important)


    private fun adsLoadAndShowTopNativeBanner() {

        binding.constnativeBannerAdView.gone()
        binding.nativeBannerAdShimmerBottom.gone()
        binding.framenativeBannerAdBottom.gone()


    }

    private fun scheduleHomeBannerLoad() {
        if (homeBannerTriggered) {
            return
        }
        homeBannerTriggerHandler.removeCallbacks(homeBannerDelayRunnable)
        homeBannerTriggerHandler.postDelayed(homeBannerDelayRunnable, 20_000L)
    }

    private fun maybeShowHomeBanner(trigger: String) {
        if (homeBannerTriggered) {
            return
        }
        homeBannerTriggered = true
        homeBannerTriggerHandler.removeCallbacks(homeBannerDelayRunnable)
        adsOrchestrator.showHomeBanner(
            activity = this,
            ui = AdUiBinding(
                rootContainer = binding.constAdViewBottom,
                adFrame = binding.frameAdBottom,
                shimmer = binding.bannerShimmerBottom
            ),
            trigger = trigger,
            onNoAd = { binding.constAdViewBottom.gone() }
        )
    }

    private fun showVIdeo() {
//        binding.contactPhotoBig.alpha = 0f
//        binding.contactPhotoBig.visible()
//        binding.contactPhotoBig.animate().alpha(1f).start()
        showVideoDialog(this)


    }

    fun showVideoDialog(context: Context) {
        // Old video dialog flow disabled; now open dedicated step-by-step instruction screen.
        openDefaultSmsInstruction("")
    }


    private fun firebaseSetup() {
        if (BaseSharedPreferences(this).mIS_SUBSCRIBED!!) {
            disableFCM(this)
        } else {
            enableFCM(this)
        }
    }

    fun disableFCM(context: Context) {

    }

    fun enableFCM(context: Context) {

    }


    fun loadinterads(Adsid: String) {

    }


    private fun showPermissiondialog() {
        if (!Settings.canDrawOverlays(this)) {
            gooutside = true
            // Create the permission dialog
            val permissionDialog =
                AlertDialog.Builder(this).setTitle(getString(R.string.permission_required_1))
                    .setMessage(getString(R.string.we_need_permission_to_display_overlays_on_other_apps))
                    .setPositiveButton(getString(R.string.grant_permission_1)) { dialog, which ->
                        // Redirect user to settings to grant permission
                        gooutside = true
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            "package:$packageName".toUri()
                        )
                        startActivityForResult(intent, 208)
//                    startOverlayPermissionWatcher()
//                    Handler(Looper.myLooper()!!).postDelayed({
//                        startActivity(Intent(this, OverlayPermissionAnimationActivity::class.java))
//                    }, 500)

                    }.setNegativeButton(getString(R.string.cancel_1)) { dialog, which ->
                        // Handle the "Cancel" action
                        if (!(isFinishing || isDestroyed)) {
                            dialog.dismiss()
                        }
                    }.create()

            // Show the dialog
            permissionDialog.show()
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        Alredyclick = false
    }

    fun openAdOnO(value: Boolean, time: Long) {
        openAdRunnable?.let { adOpenHandler.removeCallbacks(it) }
        val runnable = Runnable {}
        openAdRunnable = runnable
        adOpenHandler.postDelayed(runnable, time)
    }

    fun adjustAlpha(color: Int, factor: Float): Int {
        val alpha = (Color.alpha(color) * factor).toInt()
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }

    fun applyPulseColor(@ColorInt color: Int) {
        binding.viewSaveGlow.background?.mutate()?.setTint(color)

        binding.viewSavePulse.background?.mutate()?.setTint(adjustAlpha(color, 0.8f))
    }

    private var savePulseAnimator: AnimatorSet? = null
    private fun createExtraPulseLayer(): View {
        val view = View(this).apply {
            layoutParams = binding.viewSavePulse.layoutParams
            background = binding.viewSavePulse.background?.constantState?.newDrawable()?.mutate()
            alpha = 0f
        }
        (binding.viewSavePulse.parent as ViewGroup).addView(view, 0)
        return view
    }

    private fun startSaveButtonPulse() {

        if (savePulseAnimator?.isStarted == true) return

        val views = listOf(
            binding.viewSaveGlow,   // layer 1 (small)
            binding.viewSavePulse,  // layer 2 (medium)
            createExtraPulseLayer() // layer 3 (big)
        )

        val animators = mutableListOf<Animator>()

        views.forEachIndexed { index, view ->

            view.apply {
                visible()
                scaleX = 1f
                scaleY = 1f
                alpha = 0.25f
            }

            val animator = ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.6f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.6f),
                PropertyValuesHolder.ofFloat(View.ALPHA, 0.25f, 0f)
            ).apply {
                duration = 1900L
                startDelay = (index * 350).toLong() // 👈 wave spacing
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.RESTART // 👈 IMPORTANT
                interpolator = AccelerateDecelerateInterpolator()
            }
            animators.add(animator)
        }
        savePulseAnimator = AnimatorSet().apply {
            playTogether(animators)
            start()
        }
    }

    private fun applyHintImagesForTheme(): Int {
        val useNightHints = if (isDynamicTheme()) {
            isSystemInDarkMode()
        } else {
            ThemeModeManager.isDarkThemeActive(this)
        }
        return if (useNightHints) R.drawable.d_night else R.drawable.d_light
    }


    // Helper function
    private fun logVisibility(tag: String, viewName: String, view: View?) {
        if (view == null) {
            Log.w(tag, "$viewName → NOT FOUND in layout!")
            return
        }
        val status = when (view.visibility) {
            View.VISIBLE -> "VISIBLE"
            View.INVISIBLE -> "INVISIBLE"
            View.GONE -> "GONE"
            else -> "UNKNOWN (${view.visibility})"
        }
        Log.d(tag, "$viewName → $status")
    }
}
