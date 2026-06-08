package com.messenger.phone.number.text.sms.service.apps.data

import android.content.Context
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.core.content.ContextCompat
import com.demo.adsmanage.Commen.log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.messenger.phone.number.text.sms.service.apps.CommanClass.*
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.camera.FLASH_OFF
import com.messenger.phone.number.text.sms.service.apps.camera.models.CaptureMode
import com.messenger.phone.number.text.sms.service.apps.camera.models.TimerMode

import com.messenger.phone.number.text.sms.service.apps.modelClass.Category
import com.messenger.phone.number.text.sms.service.apps.modelClass.ChatScreenColor
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.CustomProfileAlpha
import com.messenger.phone.number.text.sms.service.apps.modelClass.CustomProfileUri
import com.messenger.phone.number.text.sms.service.apps.modelClass.ProfileColorTheme
import com.messenger.phone.number.text.sms.service.apps.modelClass.ProfileNotificationmodel
import com.simplemobiletools.commons.extensions.getInternalStoragePath
import com.simplemobiletools.commons.helpers.INTERNAL_STORAGE_PATH
import com.simplemobiletools.commons.helpers.LAST_EXPORT_PATH
import com.simplemobiletools.commons.helpers.LAST_VERSION
import com.simplemobiletools.commons.helpers.SHOW_ONLY_CONTACTS_WITH_NUMBERS
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
//import com.vision.aftercall.sdk.core.AfterCallThemeMode
//import com.vision.aftercall.sdk.core.CallerModule
//import com.vision.aftercall.sdk.core.CallerModule.updateStatusBarColor
open class BaseConfig(val context: Context) {

    protected val prefs = context.getSharedPrefs()
    protected val prefs2 = context.getSharedPrefs2()
    protected val prefs3 = context.getSharedPrefs3()
    protected val prefs4 = context.getSharedPrefs4()
    protected val prefs5 = context.getSharedPrefs5()
    protected val prefs6 = context.getSharedPrefs7()

    companion object {
        fun newInstance(context: Context) = BaseConfig(context)
    }

    // 1 for single and 2 for multiple


    var Single_Vs_Multiple_Ads_id: String?
        get() = prefs.getString("Single_Vs_Multiple_Ads_id", "2")!!
        set(lastExportPath) = prefs.edit().putString("Single_Vs_Multiple_Ads_id", lastExportPath)
            .apply()

    var reviewPromptCount: Int
        get() = prefs.getInt("reviewPromptCount", 0)
        set(value) = prefs.edit().putInt("reviewPromptCount", value).apply()

    var isReviewSubmitted: Boolean
        get() = prefs.getBoolean("isReviewSubmitted", false)
        set(value) = prefs.edit().putBoolean("isReviewSubmitted", value).apply()

    fun incrementReviewPromptCount(): Int {
        val nextCount = reviewPromptCount + 1
        reviewPromptCount = nextCount
        return nextCount
    }

    var comefromebg: Boolean
        get() = prefs.getBoolean("comefromebg", false)
        set(introshow) = prefs.edit().putBoolean("comefromebg", introshow).apply()

    var Home_banner_experiment: String?
        get() = prefs.getString("Home_banner_experiment", "1")!!
        set(lastExportPath) = prefs.edit().putString("Home_banner_experiment", lastExportPath)
            .apply()

    var After_call_banner_vs_native: String?
        get() = prefs.getString("After_call_banner_vs_native", "1")!!
        set(lastExportPath) = prefs.edit().putString("After_call_banner_vs_native", lastExportPath)
            .apply()

    var FirstTimeovarlaypermissionshow: Boolean
        get() = prefs.getBoolean("FirstTimeovarlaypermissionshow", true)!!
        set(lastExportPath) = prefs.edit()
            .putBoolean("FirstTimeovarlaypermissionshow", lastExportPath).apply()

    var FirstTimeovarlaypermissionalow: Boolean
        get() = prefs.getBoolean("FirstTimeovarlaypermissionalow", false)!!
        set(lastExportPath) = prefs.edit()
            .putBoolean("FirstTimeovarlaypermissionalow", lastExportPath).apply()


    var ZopIconget_Ms: String?
        get() = prefs.getString("ZopIconget_Ms", "3")!!
        set(lastExportPath) = prefs.edit().putString("ZopIconget_Ms", lastExportPath).apply()

    var FirstTimeShowSubscription: Boolean
        get() = prefs.getBoolean("FirstTimeShowSubscription", true)!!
        set(lastExportPath) = prefs.edit().putBoolean("FirstTimeShowSubscription", lastExportPath)
            .apply()

    var FirstTimeShowSubscriptionTwo: Boolean
        get() = prefs.getBoolean("FirstTimeShowSubscriptionTwo", true)!!
        set(lastExportPath) = prefs.edit()
            .putBoolean("FirstTimeShowSubscriptionTwo", lastExportPath).apply()

    var SecoundtimeSubshow: Int
        get() = prefs.getInt("SecoundtimeSubshow", 1)
        set(lastExportPath) = prefs.edit().putInt("SecoundtimeSubshow", lastExportPath).apply()


    var DialogHomeFlowTimeExperiment: Int
        get() = prefs.getInt("DialogHomeFlowTimeExperiment", 0)
        set(lastExportPath) = prefs.edit().putInt("DialogHomeFlowTimeExperiment", lastExportPath).apply()


    var isHomeComingFirst: Boolean
        get() = prefs.getBoolean("isHomeComingFirst", true)
        set(value) = prefs.edit().putBoolean("isHomeComingFirst", value).apply()
    var languageShowFirstvarA: Boolean
        get() = prefs.getBoolean("languageshowFirstvarA", false)
        set(value) = prefs.edit().putBoolean("languageshowFirstvarA", value).apply()

    var isHomeComingSecond: Boolean
        get() = prefs.getBoolean("isHomeComingSecond", true)
        set(value) = prefs.edit().putBoolean("isHomeComingSecond", value).apply()

    var US_subscription_flow_MS: String
        get() = prefs.getString("US_subscription_flow_MS", "1")!!
        set(lastExportPath) = prefs.edit().putString("US_subscription_flow_MS", lastExportPath)
            .apply()

    var Sub_show_or_not: String
        get() = prefs.getString("Sub_show_or_not", "1")!!
        set(lastExportPath) = prefs.edit().putString("Sub_show_or_not", lastExportPath).apply()

//    var OnBordingFlow_AB: String
//        get() = prefs.getString("OnBordingFlow_AB", "1")!!
//        set(lastExportPath) = prefs.edit().putString("OnBordingFlow_AB", lastExportPath).apply()


    var OnBordingFlow_AB: String
        get() = prefs.getString("OnBordingExp_AB", "1")!!
        set(lastExportPath) = prefs.edit().putString("OnBordingExp_AB", lastExportPath).apply()
    var homeBannerId_AB: String
        get() = prefs.getString("homeBannerId_AB", "1")!!
        set(lastExportPath) = prefs.edit().putString("homeBannerId_AB", lastExportPath).apply()

    var Subscription_Show_VS_NotShow_MS: String
        get() = prefs.getString("Subscription_Show_VS_NotShow_MS", "1")!!
        set(lastExportPath) = prefs.edit()
            .putString("Subscription_Show_VS_NotShow_MS", lastExportPath).apply()



    ////////////////////////////////

    var showfirebasenotification: Boolean
        get() = prefs.getBoolean("showfirebasenotification", true)!!
        set(lastExportPath) = prefs.edit().putBoolean("showfirebasenotification", lastExportPath)
            .apply()

    var NewOnboardingFlow_VS_OldOnboardingFlow_MS: String
        get() = prefs.getString("NewOnboardingFlow_VS_OldOnboardingFlow_MS", "1")!!
        set(lastExportPath) = prefs.edit()
            .putString("NewOnboardingFlow_VS_OldOnboardingFlow_MS", lastExportPath).apply()

    var PermissionOnboardingFlow_VS_ExperimentOnboardingFlow_MS: String
        get() = prefs.getString("PermissionOnboardingFlow_VS_ExperimentOnboardingFlow_MS", "1")!!
        set(lastExportPath) = prefs.edit()
            .putString("PermissionOnboardingFlow_VS_ExperimentOnboardingFlow_MS", lastExportPath)
            .apply()

    var secoundtimelangShow: Boolean
        get() = prefs.getBoolean("secoundtimelangShow", true)
        set(initPhotoMode) = prefs.edit().putBoolean("secoundtimelangShow", initPhotoMode).apply()

    var firsttimeflowuse: Boolean
        get() = prefs.getBoolean("firsttimeflowuse", false)
        set(initPhotoMode) = prefs.edit().putBoolean("firsttimeflowuse", initPhotoMode).apply()

    var secondtimeflowuse: Boolean
        get() = prefs.getBoolean("secondtimeflowuse", false)
        set(initPhotoMode) = prefs.edit().putBoolean("secondtimeflowuse", initPhotoMode).apply()

    var secondtimeflowuseFLow: Boolean
        get() = prefs.getBoolean("secondtimeflowuseFLow", false)
        set(value) = prefs.edit().putBoolean("secondtimeflowuseFLow", value).apply()
    var varAoncePaywllOnCancel: Boolean
        get() = prefs.getBoolean("varAoncePaywllOnCancel", false)
        set(value) = prefs.edit().putBoolean("varAoncePaywllOnCancel", value).apply()

    var allPermissionRlationalDone: Boolean
        get() = prefs.getBoolean("allPermissionRlationalDone", false)
        set(value) = prefs.edit().putBoolean("allPermissionRlationalDone", value).apply()

    var flow2_3_firstcomeDone: Boolean
        get() = prefs.getBoolean("flow2_3_firstcomeDone", false)
        set(value) = prefs.edit().putBoolean("flow2_3_firstcomeDone", value).apply()

    var onboardingHomeAgainPending: Boolean
        get() = prefs.getBoolean("onboardingHomeAgainPending", false)
        set(value) = prefs.edit().putBoolean("onboardingHomeAgainPending", value).apply()

    var firstLanguagedone: Boolean
        get() = prefs.getBoolean("firstLanguagedone", false)
        set(initPhotoMode) = prefs.edit().putBoolean("firstLanguagedone", initPhotoMode).apply()

    var newflowsecoundtimelangshow: Boolean
        get() = prefs.getBoolean("newflowsecoundtimelangshow", true)
        set(initPhotoMode) = prefs.edit().putBoolean("newflowsecoundtimelangshow", initPhotoMode)
            .apply()

    var use_flow_count: String
        get() = prefs.getString("use_flow_count", "1")!!
        set(lastExportPath) = prefs.edit().putString("use_flow_count", lastExportPath).apply()

    fun tryjustcheckMarkOnboardingStepLogged(flow: String, step: String): Boolean {
        val key = "onboarding_funnel_${flow}_${step}"
        if (prefs.getBoolean(key, false)) {
            return false
        }
//        prefs.edit().putBoolean(key, true).apply()
        return true
    }


    fun tryMarkOnboardingStepLogged(flow: String, step: String): Boolean {
        val newKey = "onboarding_funnel_${step}"// V_2.3.9 and that after
        val oldKey = "onboarding_funnel_${flow}_${step}" //V_2.3.8 and that before

        if (prefs.getBoolean(newKey, false) || prefs.getBoolean(oldKey, false)) {
            return false
        }

        prefs.edit().putBoolean(newKey, true).apply()
        return true
    }


    var FirstTimeSplashShow: Boolean
        get() = prefs.getBoolean("FirstTimeSplashShow", true)
        set(initPhotoMode) = prefs.edit().putBoolean("FirstTimeSplashShow", initPhotoMode).apply()

    ////////////////////////////////


    var newadsclass_vs_oldadsclass_MS: String
        get() = prefs.getString("newadsclass_vs_oldadsclass_MS", "1")!!
        set(lastExportPath) = prefs.edit()
            .putString("newadsclass_vs_oldadsclass_MS", lastExportPath).apply()


    var oldbanneradsflow_vs_newbanneradsflow: String
        get() = prefs.getString("oldbanneradsflow_vs_newbanneradsflow", "1")!!
        set(lastExportPath) = prefs.edit()
            .putString("oldbanneradsflow_vs_newbanneradsflow", lastExportPath).apply()


    var oldopenadsflow_vs_newopenadsflow: String
        get() = prefs.getString("oldopenadsflow_vs_newopenadsflow", "2").toString()
        set(backPhotoResIndex) = prefs.edit()
            .putString("oldopenadsflow_vs_newopenadsflow", backPhotoResIndex).apply()

    var lastUsedCameraLens: Int
        get() = prefs.getInt("LAST_USED_CAMERA_LENS", CameraSelector.LENS_FACING_BACK)
        set(lens) = prefs.edit().putInt("LAST_USED_CAMERA_LENS", lens).apply()

    var frontVideoResIndex: Int
        get() = prefs.getInt("FRONT_VIDEO_RESOLUTION_INDEX", 0)
        set(frontVideoResIndex) = prefs.edit()
            .putInt("FRONT_VIDEO_RESOLUTION_INDEX", frontVideoResIndex).apply()

    var backPhotoResIndex: Int
        get() = prefs.getInt("BACK_PHOTO_RESOLUTION_INDEX", 0)
        set(backPhotoResIndex) = prefs.edit()
            .putInt("BACK_PHOTO_RESOLUTION_INDEX", backPhotoResIndex).apply()


    var oldclass_vs_newclass: String
        get() = prefs.getString("oldclass_vs_newclass", "2").toString()
        set(backPhotoResIndex) = prefs.edit().putString("oldclass_vs_newclass", backPhotoResIndex)
            .apply()

    var initPhotoMode: Boolean
        get() = prefs.getBoolean("INIT_PHOTO_MODE", true)
        set(initPhotoMode) = prefs.edit().putBoolean("INIT_PHOTO_MODE", initPhotoMode).apply()

    var isComeFirstTimeInTransaction: Boolean
        get() = prefs.getBoolean("isComeFirstTimeInTransaction", false)
        set(initPhotoMode) = prefs.edit().putBoolean("isComeFirstTimeInTransaction", initPhotoMode)
            .apply()

    var isRatingDialogshow: Boolean
        get() = prefs.getBoolean("isRatingDialogshow", true)
        set(initPhotoMode) = prefs.edit().putBoolean("isRatingDialogshow", initPhotoMode).apply()

    var RatingDialogshowCount: Int
        get() = prefs.getInt("RatingDialogshowCount", 0)
        set(initPhotoMode) = prefs.edit().putInt("RatingDialogshowCount", initPhotoMode).apply()

    var volumeButtonsAsShutter: Boolean
        get() = prefs.getBoolean("VOLUME_BUTTONS_AS_SHUTTER", false)
        set(volumeButtonsAsShutter) = prefs.edit()
            .putBoolean("VOLUME_BUTTONS_AS_SHUTTER", volumeButtonsAsShutter).apply()

    var frontPhotoResIndex: Int
        get() = prefs.getInt("FRONT_PHOTO_RESOLUTION_INDEX", 0)
        set(frontPhotoResIndex) = prefs.edit()
            .putInt("FRONT_PHOTO_RESOLUTION_INDEX", frontPhotoResIndex).apply()

    var savePhotoMetadata: Boolean
        get() = prefs.getBoolean("SAVE_PHOTO_METADATA", true)
        set(savePhotoMetadata) = prefs.edit().putBoolean("SAVE_PHOTO_METADATA", savePhotoMetadata)
            .apply()


    var SecondLAnguageShow: Boolean
        get() = prefs.getBoolean("SecondLAnguageShow", false)
        set(savePhotoMetadata) = prefs.edit().putBoolean("SecondLAnguageShow", savePhotoMetadata)
            .apply()
    var FirstHome: Boolean
        get() = prefs.getBoolean("FirstHome", false)
        set(savePhotoMetadata) = prefs.edit().putBoolean("FirstHome", savePhotoMetadata)
            .apply()
    var FirstWelcomescreen: Boolean
        get() = prefs.getBoolean("FirstWelcomescreen", false)
        set(savePhotoMetadata) = prefs.edit().putBoolean("FirstWelcomescreen", savePhotoMetadata)
            .apply()



       var FirstPerNotification: Boolean
        get() = prefs.getBoolean("FirstPerNotification", false)
        set(savePhotoMetadata) = prefs.edit().putBoolean("FirstPerNotification", savePhotoMetadata)
            .apply()
       var FirstCallStatePerm: Boolean
        get() = prefs.getBoolean("FirstCallStatePerm", false)
        set(savePhotoMetadata) = prefs.edit().putBoolean("FirstCallStatePerm", savePhotoMetadata)
            .apply()

    var FirstOverLayPerm: Boolean
        get() = prefs.getBoolean("FirstOverLayPerm", false)
        set(savePhotoMetadata) = prefs.edit().putBoolean("FirstOverLayPerm", savePhotoMetadata)
            .apply()

       var FirstDialogCount: Boolean
        get() = prefs.getBoolean("FirstDialogCount", false)
        set(savePhotoMetadata) = prefs.edit().putBoolean("FirstDialogCount", savePhotoMetadata)
            .apply()






    var photoQuality: Int
        get() = prefs.getInt("PHOTO_QUALITY", 80)
        set(photoQuality) = prefs.edit().putInt("PHOTO_QUALITY", photoQuality).apply()

    var PermissionAfterCount: Int
        get() = prefs.getInt("PermissionAfterCount", 0)
        set(photoQuality) = prefs.edit().putInt("PermissionAfterCount", photoQuality).apply()



    var HomeDefaultSmsCount: Int
        get() = prefs.getInt("HomeDefaultSmsCount", 0)
        set(photoQuality) = prefs.edit().putInt("HomeDefaultSmsCount", photoQuality).apply()






    var flipPhotos: Boolean
        get() = prefs.getBoolean("FLIP_PHOTOS", true)
        set(flipPhotos) = prefs.edit().putBoolean("FLIP_PHOTOS", flipPhotos).apply()

    var savePhotosFolder: String
        get(): String {
            var path = prefs.getString(
                "SAVE_PHOTOS",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()
            )
            if (!File(path).exists() || !File(path).isDirectory) {
                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    .toString()
                savePhotosFolder = path
            }
            return path!!
        }
        set(path) = prefs.edit().putString("SAVE_PHOTOS", path).apply()

    var showOnlyContactsWithNumbers: Boolean
        get() = prefs.getBoolean(SHOW_ONLY_CONTACTS_WITH_NUMBERS, false)
        set(showOnlyContactsWithNumbers) = prefs.edit()
            .putBoolean(SHOW_ONLY_CONTACTS_WITH_NUMBERS, showOnlyContactsWithNumbers).apply()

    var issubbannershow: Boolean
        set(value) = prefs.edit().putBoolean("issubbannershow", value).apply()
        get() = prefs.getBoolean("issubbannershow", false)

    var setABHomeActivityPref: String
        set(value) = prefs.edit().putString("setABHomeActivityPref", value).apply()
        get() = prefs.getString("setABHomeActivityPref", "2").toString()

    var Intro_Home_USA_Not_Show_Interstitial_pref: String
        set(value) = prefs.edit().putString("Intro_Home_USA_Not_Show_Interstitial", value).apply()
        get() = prefs.getString("Intro_Home_USA_Not_Show_Interstitial", "1").toString()

    var ringtoanselectforallcontact: Boolean
        set(value) = prefs.edit().putBoolean("ringtoanselectforallcontact", value).apply()
        get() = prefs.getBoolean("ringtoanselectforallcontact", false)

    var ringtoanselectforallcontactPath: String
        set(value) = prefs.edit().putString("ringtoanselectforallcontactPath", value).apply()
        get() = prefs.getString("ringtoanselectforallcontactPath", "").toString()

    var last_backup_time: String
        set(value) = prefs.edit().putString("last_backup_time", value).apply()
        get() = prefs.getString("last_backup_time", "Nothing").toString()


    fun getProfileNotification(id: String): ProfileNotificationmodel? {
        val gson = Gson()
        val colorJson = prefs5.getString(id.toString(), null) ?: return null
        val type = object : TypeToken<ProfileNotificationmodel>() {}.type
        return gson.fromJson(colorJson, type)
    }

    fun removeAllProfileNotification() {
        prefs5.edit().clear().apply()
    }

    var ringtoanselectforallcontactName: String
        set(value) = prefs.edit().putString("ringtoanselectforallcontactName", value).apply()
        get() = prefs.getString("ringtoanselectforallcontactName", "").toString()

    fun saveProfileNotification(color: ProfileNotificationmodel) {
        val gson = Gson()
        val colorJson = gson.toJson(color)
        prefs5.edit().putString(color.tredid, colorJson).apply()
    }

    private fun getDefaultInternalPath() =
        if (prefs.contains(INTERNAL_STORAGE_PATH)) "" else context.getInternalStoragePath()


    var internalStoragePath: String
        get() = prefs.getString(INTERNAL_STORAGE_PATH, getDefaultInternalPath())!!
        set(internalStoragePath) = prefs.edit()
            .putString(INTERNAL_STORAGE_PATH, internalStoragePath).apply()


    private fun getEverShownFolders() = hashSetOf(
        internalStoragePath,
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath,
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath,
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath,
        "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath}/Screenshots",
        "$internalStoragePath/WhatsApp/Media/WhatsApp Images",
        "$internalStoragePath/WhatsApp/Media/WhatsApp Images/Sent",
        "$internalStoragePath/WhatsApp/Media/WhatsApp Video",
        "$internalStoragePath/WhatsApp/Media/WhatsApp Video/Sent",
        "$internalStoragePath/WhatsApp/Media/.Statuses",
        "$internalStoragePath/Android/media/com.whatsapp/WhatsApp/Media",
        "$internalStoragePath/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images",
        "$internalStoragePath/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Video"
    )

    var excludedFolders: MutableSet<String>
        get() = prefs.getStringSet(EXCLUDED_FOLDERS, HashSet())!!
        set(excludedFolders) = prefs.edit().remove(EXCLUDED_FOLDERS)
            .putStringSet(EXCLUDED_FOLDERS, excludedFolders).apply()


    var userfisttimeshowsubscreen: Boolean
        get() = prefs.getBoolean(userfisttimeshowsubscreen_pref, true)
        set(showCharacterCounter) = prefs.edit()
            .putBoolean(userfisttimeshowsubscreen_pref, showCharacterCounter).apply()

    var hasCheckedDefaultSms: Boolean
        get() = prefs.getBoolean("hasCheckedDefaultSms", false)
        set(value) = prefs.edit().putBoolean("hasCheckedDefaultSms", value).apply()

    var userfisttimedefaultset: Boolean
        get() = prefs.getBoolean("userfisttimedefaultset", false)
        set(showCharacterCounter) = prefs.edit()
            .putBoolean("userfisttimedefaultset", showCharacterCounter).apply()

    var defaultSmsDialogSuppressed: Boolean
        get() = prefs.getBoolean("defaultSmsDialogSuppressed", false)
        set(value) = prefs.edit().putBoolean("defaultSmsDialogSuppressed", value).apply()

    var bannerShowStart: Boolean
        get() = prefs.getBoolean("bannerShowStart", false)
        set(value) = prefs.edit().putBoolean("bannerShowStart", value).apply()


    var isComeDoubleDilogVarBCancle: Boolean
        get() = prefs.getBoolean("isComeDoubleDilogVarBCancle", false)
        set(value) = prefs.edit().putBoolean("isComeDoubleDilogVarBCancle", value).apply()


    var isFeedBackOptionShow: Boolean
        get() = prefs.getBoolean("isFeedBackOptionShow", false)
        set(isFeedBackOptionShow) = prefs.edit()
            .putBoolean("isFeedBackOptionShow", isFeedBackOptionShow).apply()


    var usertrailscreenshow: Boolean
        get() = prefs.getBoolean(usertrailscreenshow_pref, false)
        set(showCharacterCounter) = prefs.edit()
            .putBoolean(usertrailscreenshow_pref, showCharacterCounter).apply()


    var includedFolders: MutableSet<String>
        get() = prefs.getStringSet(INCLUDED_FOLDERS, HashSet<String>())!!
        set(includedFolders) = prefs.edit().remove(INCLUDED_FOLDERS)
            .putStringSet(INCLUDED_FOLDERS, includedFolders).apply()

    var everShownFolders: Set<String>
        get() = prefs.getStringSet(EVER_SHOWN_FOLDERS, getEverShownFolders())!!
        set(everShownFolders) = prefs.edit().putStringSet(EVER_SHOWN_FOLDERS, everShownFolders)
            .apply()

    var isprivatelocktype: Int
        get() = prefs.getInt(isprivatelocktypepref, 1)
        set(fontsize) = prefs.edit().putInt(isprivatelocktypepref, fontsize).apply()

    var isprivatelockpatten: String
        get() = prefs.getString(isprivatelockpattenpref, "").toString()
        set(fontsize) = prefs.edit().putString(isprivatelockpattenpref, fontsize).apply()

    var is2steplockpatten: String
        get() = prefs.getString(is2steplockpattenpref, "").toString()
        set(fontsize) = prefs.edit().putString(is2steplockpattenpref, fontsize).apply()

    var mmsFileSizeLimit: Long
        get() = prefs.getLong(MMS_FILE_SIZE_LIMIT, FILE_SIZE_NONE)
        set(mmsFileSizeLimit) = prefs.edit().putLong(MMS_FILE_SIZE_LIMIT, mmsFileSizeLimit).apply()

    var showCharacterCounter: Boolean
        get() = prefs.getBoolean(SHOW_CHARACTER_COUNTER, false)
        set(showCharacterCounter) = prefs.edit()
            .putBoolean(SHOW_CHARACTER_COUNTER, showCharacterCounter).apply()

    var showfullsettingshowcase: Boolean
        get() = prefs.getBoolean(showfullsettingshowcasepref, true)
        set(showCharacterCounter) = prefs.edit()
            .putBoolean(showfullsettingshowcasepref, showCharacterCounter).apply()

    var appopenandshowcaseshow: Boolean
        get() = prefs.getBoolean(appopenandshowcaseshowpref, false)
        set(showCharacterCounter) = prefs.edit()
            .putBoolean(appopenandshowcaseshowpref, showCharacterCounter).apply()

    var lastVersion: Int
        get() = prefs.getInt(LAST_VERSION, 0)
        set(lastVersion) = prefs.edit().putInt(LAST_VERSION, lastVersion).apply()

    var themeselectedcolor: Int
        get() = prefs.getInt(
            "themeselectedcolor", ContextCompat.getColor(context, R.color.appcolor)
        )
        set(lastVersion) = prefs.edit().putInt("themeselectedcolor", lastVersion).apply()


    var ismorecolorselected: Boolean
        get() = prefs.getBoolean("ismorecolorselected", false)
        set(lastVersion) = prefs.edit().putBoolean("ismorecolorselected", lastVersion).apply()

    var fragmentshowcasecout: Int
        get() = prefs.getInt(fragmentshowcasecoutpref, 1)
        set(lastVersion) = prefs.edit().putInt(fragmentshowcasecoutpref, lastVersion).apply()

    var customwallpaperselected: Int
        get() = prefs.getInt(customwallpaperselectedpref, 2)
        set(lastVersion) = prefs.edit().putInt(customwallpaperselectedpref, lastVersion).apply()


    var toolbarcolorcustomwallpaper: String?
        get() = prefs.getString(toolbarcolorcustomwallpaperpref, "#FFFFFF")
        set(lastVersion) = prefs.edit().putString(toolbarcolorcustomwallpaperpref, lastVersion)
            .apply()

    var backgroundcolorcustomwallpaper: String?
        get() = prefs.getString(backgroundcolorcustomwallpaperpref, "#FFFFFF")
        set(lastVersion) = prefs.edit().putString(backgroundcolorcustomwallpaperpref, lastVersion)
            .apply()

    var backgroundcolorcustomwallpaperab: String?
        get() = prefs.getString(backgroundcolorcustomwallpaperabpref, "#FFFFFF")
        set(lastVersion) = prefs.edit().putString(backgroundcolorcustomwallpaperabpref, lastVersion)
            .apply()

    var inmessagecolorcustomwallpaper: String?
        get() = prefs.getString(inmessagecolorcustomwallpaperpref, "#4AA6FB")
        set(lastVersion) = prefs.edit().putString(inmessagecolorcustomwallpaperpref, lastVersion)
            .apply()

    var outmessagecolorcustomwallpaper: String?
        get() = prefs.getString(outmessagecolorcustomwallpaperpref, "#E8F4FF")
        set(lastVersion) = prefs.edit().putString(outmessagecolorcustomwallpaperpref, lastVersion)
            .apply()

    var inmessagecolorcustomwallpaperAB: String?
        get() = prefs.getString(inmessagecolorcustomwallpaperABpref, "#EBEFF2")
        set(lastVersion) = prefs.edit().putString(inmessagecolorcustomwallpaperABpref, lastVersion)
            .apply()

    //patten lock setup

    var is2steplocktype: Int
        get() = prefs.getInt(is2steplocktypepref, 1)
        set(fontsize) = prefs.edit().putInt(is2steplocktypepref, fontsize).apply()

    var outmessagecolorcustomwallpaperAB: String?
        get() = prefs.getString(outmessagecolorcustomwallpaperABpref, "#F2F2F2")
        set(lastVersion) = prefs.edit().putString(outmessagecolorcustomwallpaperABpref, lastVersion)
            .apply()

    var iscustomgalleryimageset: Boolean
        get() = prefs.getBoolean(iscustomgalleryimagesetpref, false)
        set(lastVersion) = prefs.edit().putBoolean(iscustomgalleryimagesetpref, lastVersion).apply()

    var smartreplycolor: String?
        get() = prefs.getString(smartreplycolorpref, "#E5EAEF")
        set(lastVersion) = prefs.edit().putString(smartreplycolorpref, lastVersion).apply()

    var lastExportPath: String
        get() = prefs.getString(LAST_EXPORT_PATH, "")!!
        set(lastExportPath) = prefs.edit().putString(LAST_EXPORT_PATH, lastExportPath).apply()

    var drivingmodemessage: String
        get() = prefs.getString(
            drivingmodemessagepref, "I’m Driving right now. i’ll get back to you ASAP"
        )!!
        set(lastExportPath) = prefs.edit().putString(drivingmodemessagepref, lastExportPath).apply()

    var lastselectedpreviews: String
        get() = prefs.getString(notificationpriviewselected, "Show name and message")!!
        set(lastExportPath) = prefs.edit().putString(notificationpriviewselected, lastExportPath)
            .apply()

    var isAllChatWallpaper: Boolean
        get() = prefs.getBoolean(isAllChatWallpaper_pref, false)
        set(lastExportPath) = prefs.edit().putBoolean(isAllChatWallpaper_pref, lastExportPath)
            .apply()

    var isAllChatDefault: Boolean
        get() = prefs.getBoolean(allChatDefault_pref, false)
        set(lastExportPath) = prefs.edit().putBoolean(allChatDefault_pref, lastExportPath).apply()


    var isAllChatColor: Boolean
        get() = prefs.getBoolean(isAllChatColor_pref, false)
        set(lastExportPath) = prefs.edit().putBoolean(isAllChatColor_pref, lastExportPath).apply()

    var isAllChatDefaultColor: Boolean
        get() = prefs.getBoolean(isAllChatDefaultColor_pref, false)
        set(lastExportPath) = prefs.edit().putBoolean(isAllChatDefaultColor_pref, lastExportPath)
            .apply()


    var isadativebennarload: Boolean
        get() = prefs.getBoolean(isadativebennarload_pref, true)
        set(lastExportPath) = prefs.edit().putBoolean(isadativebennarload_pref, lastExportPath)
            .apply()

    var isadativebennarloadlang: Boolean
        get() = prefs.getBoolean(isadativebennarloadlang_pref, true)
        set(lastExportPath) = prefs.edit().putBoolean(isadativebennarloadlang_pref, lastExportPath)
            .apply()

    var SolidColorlist: List<String>
        get() {
            val colorsString = prefs.getString(SolidColorlistpref, null)
            return colorsString?.split(",") ?: emptyList()
        }
        set(value) {
            val colorsString = value.joinToString(",")
            prefs.edit().putString(SolidColorlistpref, colorsString).apply()
        }

    var Swipe_Right: String
        get() = prefs.getString(Swipe_Right_pref, "Nothing")!!
        set(lastExportPath) = prefs.edit().putString(Swipe_Right_pref, lastExportPath).apply()

    var NotiButton1: String
        get() = prefs.getString(NotiButton1_pref, "Reply")!!
        set(lastExportPath) = prefs.edit().putString(NotiButton1_pref, lastExportPath).apply()

    var NotiButton3: String
        get() = prefs.getString(NotiButton3_pref, "Delete")!!
        set(lastExportPath) = prefs.edit().putString(NotiButton3_pref, lastExportPath).apply()

    var NotiButton2: String
        get() = prefs.getString(NotiButton2_pref, "Mark as read")!!
        set(lastExportPath) = prefs.edit().putString(NotiButton2_pref, lastExportPath).apply()

    var Swipe_Left: String
        get() = prefs.getString(Swipe_Left_pref, "Mark as read")!!
        set(lastExportPath) = prefs.edit().putString(Swipe_Left_pref, lastExportPath).apply()

    var Lock_Screen_Pin: String
        get() = prefs.getString(Lock_Screen_Pin_pref, "Not Set")!!
        set(lastExportPath) = prefs.edit().putString(Lock_Screen_Pin_pref, lastExportPath).apply()

    var Full_AppLock_Pin: String
        get() = prefs.getString(Full_AppLock_Pin_pref, "Not Set")!!
        set(lastExportPath) = prefs.edit().putString(Full_AppLock_Pin_pref, lastExportPath).apply()

    var Lock_Screen_Sec_Question: String
        get() = prefs.getString(
            Lock_Screen_Sec_Question_pref_pref, "Select any Security Question"
        )!!
        set(lastExportPath) = prefs.edit()
            .putString(Lock_Screen_Sec_Question_pref_pref, lastExportPath).apply()

    var Password_reset_email_id: String
        get() = prefs.getString(Password_reset_email_id_pref, "")!!
        set(lastExportPath) = prefs.edit().putString(Password_reset_email_id_pref, lastExportPath)
            .apply()

    var Full_AppLock_Sec_Question: String
        get() = prefs.getString(Full_AppLock_Sec_Question_pref, "Select any Security Question")!!
        set(lastExportPath) = prefs.edit().putString(Full_AppLock_Sec_Question_pref, lastExportPath)
            .apply()

    var Message_full_App_Font_Style: String
        get() = prefs.getString(Message_full_App_Font_Style_pref, "2")!!
        set(lastExportPath) = prefs.edit()
            .putString(Message_full_App_Font_Style_pref, lastExportPath).apply()

    var Message_Send_Activity: String
        get() = prefs.getString(Message_Send_Activity_pref, "2")!!
        set(lastExportPath) = prefs.edit().putString(Message_Send_Activity_pref, lastExportPath)
            .apply()

    var Message_Home_screen_Native_Ad_AB: String
        get() = prefs.getString(Message_Home_screen_Native_Ad_AB_pref, "2")!!
        set(lastExportPath) = prefs.edit()
            .putString(Message_Home_screen_Native_Ad_AB_pref, lastExportPath).apply()

    var Message_Home_screen_Native_Ad_Card_And_Normal: String
        get() = prefs.getString(Message_Home_screen_Native_Ad_Card_And_Normal_pref, "2")!!
        set(lastExportPath) = prefs.edit()
            .putString(Message_Home_screen_Native_Ad_Card_And_Normal_pref, lastExportPath).apply()

    var Message_Home_screen_Top_Native_Ad_Show: Boolean
        get() = prefs.getBoolean(Message_Home_screen_Native_Ad_Show_pref, true)!!
        set(lastExportPath) = prefs.edit()
            .putBoolean(Message_Home_screen_Native_Ad_Show_pref, lastExportPath).apply()

    var Language_Screen_banner_vs_native: String
        get() = prefs.getString(Language_Screen_banner_vs_native_pref, "1")!!
        set(lastExportPath) = prefs.edit()
            .putString(Language_Screen_banner_vs_native_pref, lastExportPath).apply()

    var Banner_Home_Top: Boolean
        get() = prefs.getBoolean("Banner_Home_Top", true)!!
        set(lastExportPath) = prefs.edit().putBoolean("Banner_Home_Top", lastExportPath).apply()


    var Banner_Home_Bottom: Boolean
        get() = prefs.getBoolean("Banner_Home_Bottom", true)!!
        set(lastExportPath) = prefs.edit().putBoolean("Banner_Home_Bottom", lastExportPath).apply()

    var Banner_Language_Bottom: Boolean
        get() = prefs.getBoolean("Banner_Language_Bottom", true)!!
        set(lastExportPath) = prefs.edit().putBoolean("Banner_Language_Bottom", lastExportPath)
            .apply()

    var Banner_Bottom_VS_Top: String
        get() = prefs.getString("Banner_Bottom_VS_Top", "1")!!
        set(lastExportPath) = prefs.edit().putString("Banner_Bottom_VS_Top", lastExportPath).apply()

    var Interstitial_SendMessage_Home: Boolean
        get() = prefs.getBoolean("Interstitial_SendMessage_Home", true)!!
        set(lastExportPath) = prefs.edit()
            .putBoolean("Interstitial_SendMessage_Home", lastExportPath).apply()


    var openad_baground: Boolean
        get() = prefs.getBoolean("openad_baground", true)
        set(useSimpleCharacters) = prefs.edit().putBoolean("openad_baground", useSimpleCharacters)
            .apply()

    var openad_baground_count: Long
        get() = prefs.getLong("openad_baground_count", 2L)
        set(useSimpleCharacters) = prefs.edit()
            .putLong("openad_baground_count", useSimpleCharacters).apply()

    var Language_Screen_banner_vs_native_show_or_hide: Boolean
        get() = prefs.getBoolean(Language_Screen_banner_vs_native_show_or_hide_pref, true)!!
        set(lastExportPath) = prefs.edit()
            .putBoolean(Language_Screen_banner_vs_native_show_or_hide_pref, lastExportPath).apply()

    var Message_Language_Screen_Ad_Show_or_Hide: String
        get() = prefs.getString(Message_Language_Screen_Ad_Show_or_Hide_pref, "1")!!
        set(lastExportPath) = prefs.edit()
            .putString(Message_Language_Screen_Ad_Show_or_Hide_pref, lastExportPath).apply()

    var Full_AppLock_Sec_Question_Ans: String
        get() = prefs.getString(
            Full_AppLock_Sec_Question_Ans_pref, "Select any Security Question"
        )!!
        set(lastExportPath) = prefs.edit()
            .putString(Full_AppLock_Sec_Question_Ans_pref, lastExportPath).apply()

    var Lock_Screen_Sec_Question_Ans: String
        get() = prefs.getString(Lock_Screen_Sec_Question_Ans_pref, "Select any Security Question")!!
        set(lastExportPath) = prefs.edit()
            .putString(Lock_Screen_Sec_Question_Ans_pref, lastExportPath).apply()

    var SelectedLanguage: String
        get() = prefs.getString(SelectedLanguagepref, "en")!!
        set(lastExportPath) = prefs.edit().putString(SelectedLanguagepref, lastExportPath).apply()

    var exportSms: Boolean
        get() = prefs.getBoolean(EXPORT_SMS, true)
        set(exportSms) = prefs.edit().putBoolean(EXPORT_SMS, exportSms).apply()

    var setDefaultLanguage: String?
        set(value) = prefs.edit().putString("language", value).apply()
        get() = prefs.getString("language", "en")

    var exportMms: Boolean
        get() = prefs.getBoolean(EXPORT_MMS, true)
        set(exportMms) = prefs.edit().putBoolean(EXPORT_MMS, exportMms).apply()

    var ratingdialogshow: Boolean
        get() = prefs.getBoolean(ratingdialogshow_pref, true)
        set(exportMms) = prefs.edit().putBoolean(ratingdialogshow_pref, exportMms).apply()

    var defprivacyaccept: Boolean
        get() = prefs.getBoolean(defprivacyaccept_pref, true)
        set(exportMms) = prefs.edit().putBoolean(defprivacyaccept_pref, exportMms).apply()


    var Default_Flow_OLD_VS_NEW: String
        set(value) = prefs.edit().putString("Default_Flow_OLD_VS_NEW", value).apply()
        get() = prefs.getString("Default_Flow_OLD_VS_NEW", "1").toString()


    var fontsize: Int
        get() = prefs.getInt(FONT_SIZE, 20)
        set(fontsize) = prefs.edit().putInt(FONT_SIZE, fontsize).apply()

    var dialogshowcount: Int
        get() = prefs.getInt(dialogshowcount_pref, 0)
        set(fontsize) = prefs.edit().putInt(dialogshowcount_pref, fontsize).apply()


    var fontsizeselection: String
        get() = prefs.getString(FONT_SIZE_SELECTION, "Normal")!!
        set(fontsize) = prefs.edit().putString(FONT_SIZE_SELECTION, fontsize).apply()

    var homemessagetitlesize: String
        get() = prefs.getString("homemessagetitle", "Normal")!!
        set(fontsize) = prefs.edit().putString("homemessagetitle", fontsize).apply()

    var Home_App_OpenAD_VS_InterAd_MS: String
        get() = prefs.getString("Home_App_OpenAD_VS_InterAd_MS", "1")!!
        set(fontsize) = prefs.edit().putString("Home_App_OpenAD_VS_InterAd_MS", fontsize).apply()


    var line_selection: Int
        get() = prefs.getInt(line_selectionpref, 3)
        set(fontsize) = prefs.edit().putInt(line_selectionpref, fontsize).apply()

    private val legacyThemeSelectionPrefKey = "chat_theme_selectionpref"
    private val legacySystemThemePrefKey = "chat_theme_System_pref"
    private val legacySelectionLight = 1
    private val legacySelectionDark = 3
    private val legacySelectionSystem = 4

    var theme_mode: Int
        get() {
            if (prefs.contains(theme_mode_pref)) {
                return normalizeThemeMode(
                    prefs.getInt(
                        theme_mode_pref,
                        ThemeModeManager.MODE_SYSTEM
                    )
                )
            }

            val migratedMode = migrateLegacyThemeMode()
            prefs.edit()
                .putInt(theme_mode_pref, migratedMode)
                .remove(legacyThemeSelectionPrefKey)
                .remove(legacySystemThemePrefKey)
                .apply()
            syncAfterCallThemeForMode(migratedMode)
            return migratedMode
        }
        set(value) {
            val normalizedMode = normalizeThemeMode(value)
            prefs.edit()
                .putInt(theme_mode_pref, normalizedMode)
                .remove(legacyThemeSelectionPrefKey)
                .remove(legacySystemThemePrefKey)
                .apply()
            syncAfterCallThemeForMode(normalizedMode)
        }

    var activeThemeSelection: Int
        get() = legacySelectionForMode(theme_mode)
        set(value) {
            val normalizedSelection = normalizeLegacySelection(value)
            theme_mode = when (normalizedSelection) {
                legacySelectionSystem -> ThemeModeManager.MODE_SYSTEM
                legacySelectionDark -> ThemeModeManager.MODE_DARK
                else -> ThemeModeManager.MODE_LIGHT
            }
        }

    var NewHomeScreen_TopBanner_VS_BottomBanner: String
        get() = prefs.getString("NewHomeScreen_TopBanner_VS_BottomBanner", "1").toString()
        set(fontsize) = prefs.edit().putString("NewHomeScreen_TopBanner_VS_BottomBanner", fontsize)
            .apply()

    var AfterCall_Banner_VS_Native_MS: String
        get() = prefs.getString("AfterCall_Banner_VS_Native_MS", "1").toString()
        set(fontsize) = prefs.edit().putString("AfterCall_Banner_VS_Native_MS", fontsize).apply()


    var AfterCall_Collapsible_Vs_Native_Vs_Random_MS: String
        get() = prefs.getString("AfterCall_Collapsible_Vs_Native_Vs_Random_MS", "2").toString()
        set(fontsize) = prefs.edit()
            .putString("AfterCall_Collapsible_Vs_Native_Vs_Random_MS", fontsize).apply()

    var First_Language_Banner_Vs_Native_Vs_Random_MS: String
        get() = prefs.getString("First_Language_Banner_Vs_Native_Vs_Random_MS", "2").toString()
        set(fontsize) = prefs.edit()
            .putString("First_Language_Banner_Vs_Native_Vs_Random_MS", fontsize).apply()


    var isSystemThemeMode: Boolean
        get() = theme_mode == ThemeModeManager.MODE_SYSTEM
        set(value) {
            if (value) {
                theme_mode = ThemeModeManager.MODE_SYSTEM
            } else if (theme_mode == ThemeModeManager.MODE_SYSTEM) {
                theme_mode = if (isDarkMode(context)) {
                    ThemeModeManager.MODE_DARK
                } else {
                    ThemeModeManager.MODE_LIGHT
                }
            }
        }

    private fun migrateLegacyThemeMode(): Int {
        val hasLegacySystem = prefs.contains(legacySystemThemePrefKey)
        val hasLegacySelection = prefs.contains(legacyThemeSelectionPrefKey)

        if (!hasLegacySystem && !hasLegacySelection) {
            return ThemeModeManager.MODE_SYSTEM
        }

        if (prefs.getBoolean(legacySystemThemePrefKey, false)) {
            return ThemeModeManager.MODE_SYSTEM
        }

        val fallbackSelection = if (isDarkMode(context)) legacySelectionDark else legacySelectionLight
        val selection = prefs.getInt(legacyThemeSelectionPrefKey, fallbackSelection)
        return when (normalizeLegacySelection(selection)) {
            legacySelectionSystem -> ThemeModeManager.MODE_SYSTEM
            legacySelectionDark -> ThemeModeManager.MODE_DARK
            else -> ThemeModeManager.MODE_LIGHT
        }
    }

    private fun normalizeThemeMode(mode: Int): Int {
        return when (mode) {
            ThemeModeManager.MODE_SYSTEM,
            ThemeModeManager.MODE_LIGHT,
            ThemeModeManager.MODE_DARK -> mode

            else -> ThemeModeManager.MODE_SYSTEM
        }
    }

    private fun legacySelectionForMode(mode: Int): Int {
        return when (normalizeThemeMode(mode)) {
            ThemeModeManager.MODE_DARK -> legacySelectionDark
            ThemeModeManager.MODE_LIGHT -> legacySelectionLight
            else -> legacySelectionSystem
        }
    }

    private fun normalizeLegacySelection(selection: Int): Int {
        return when (selection) {
            legacySelectionSystem -> legacySelectionSystem
            2, legacySelectionDark -> legacySelectionDark
            legacySelectionLight -> legacySelectionLight
            0 -> if (isDarkMode(context)) legacySelectionDark else legacySelectionLight
            else -> legacySelectionLight
        }
    }

    private fun syncAfterCallThemeForMode(mode: Int) {
        /*try {
            if (!CallerModule.isInitialized()) {
                runCatching {
//                CallerModule.warmInitialize(context)
                }
            }
            if (!CallerModule.isInitialized()) {
                return
            }
            val shouldUseDarkAfterCall =
                mode == ThemeModeManager.MODE_DARK || (mode == ThemeModeManager.MODE_SYSTEM && isDarkMode(context))
            if (shouldUseDarkAfterCall) {
                CallerModule.updateDefaultAfterCallTheme(AfterCallThemeMode.DARK)
                updateStatusBarColor(R.color.toolbar_color_aftercall_dark)
            } else {
                CallerModule.updateDefaultAfterCallTheme(AfterCallThemeMode.LIGHT)
                updateStatusBarColor(R.color.toolbar_color_aftercall)
            }
        }catch (_: Exception){

        }*/

    }

    var contact_app_open: Boolean
        get() = prefs.getBoolean("contact_app_open", false)
        set(fontsize) = prefs.edit().putBoolean("contact_app_open", fontsize).apply()


    var messagecorner: Float
        get() = prefs.getFloat(messagecornerpref, 30f)
        set(fontsize) = prefs.edit().putFloat(messagecornerpref, fontsize).apply()

    var imageAlpha: Float
        get() = prefs.getFloat(imageAlphapref, 0f)
        set(fontsize) = prefs.edit().putFloat(imageAlphapref, fontsize).apply()

    var importSms: Boolean
        get() = prefs.getBoolean(IMPORT_SMS, true)
        set(importSms) = prefs.edit().putBoolean(IMPORT_SMS, importSms).apply()

    var Misscall_Aftercall_OFF: Boolean
        get() = prefs.getBoolean("Misscall_Aftercall_OFF", true)
        set(importSms) = prefs.edit().putBoolean("Misscall_Aftercall_OFF", importSms).apply()

    var importMms: Boolean
        get() = prefs.getBoolean(IMPORT_MMS, true)
        set(importMms) = prefs.edit().putBoolean(IMPORT_MMS, importMms).apply()

    var keyboardHeight: Int
        get() = prefs.getInt(SOFT_KEYBOARD_HEIGHT, context.getDefaultKeyboardHeight())
        set(keyboardHeight) = prefs.edit().putInt(SOFT_KEYBOARD_HEIGHT, keyboardHeight).apply()




    var afterCall_Delay: Long
        get() = prefs.getLong("Aftercall_Delay", 0)
        set(afterCallDelay) = prefs.edit().putLong("Aftercall_Delay", afterCallDelay).apply()
    var aftercall_Priority: Int
        get() = prefs.getInt("Aftercall_Priority", 0)
        set(aftercallPriority) = prefs.edit().putInt("Aftercall_Priority", aftercallPriority).apply()
    var aftercall_Sequence: Int
        get() = prefs.getInt("Aftercall_Sequence", 123)
        set(aftercallSequence) = prefs.edit().putInt("Aftercall_Sequence", aftercallSequence).apply()


    var notificationshowcase: Int
        get() = prefs.getInt(notificationshowcasepref, 1)
        set(keyboardHeight) = prefs.edit().putInt(notificationshowcasepref, keyboardHeight).apply()

    var openadsshowcount: Int = 0

//    var openadsshowcount: Int
//        get() = prefs.getInt("openadsshowcount", 0)
//        set(keyboardHeight) = prefs.edit().putInt("openadsshowcount", keyboardHeight).apply()

    var subscreenshowcount: Int
        get() = prefs.getInt("subscreenshowcount", 0)
        set(keyboardHeight) = prefs.edit().putInt("subscreenshowcount", keyboardHeight).apply()

    var homescreenadscount: Int
        get() = prefs.getInt("homescreenadscount", 0)
        set(keyboardHeight) = prefs.edit().putInt("homescreenadscount", keyboardHeight).apply()

    var introshow: Boolean
        get() = prefs.getBoolean(introshowpref, true)
        set(introshow) = prefs.edit().putBoolean(introshowpref, introshow).apply()


    var tutorialshow: Boolean
        get() = prefs.getBoolean(tutorialshowpref, true)
        set(introshow) = prefs.edit().putBoolean(tutorialshowpref, introshow).apply()

    var privatechattutorialshow: Boolean
        get() = prefs.getBoolean(privatechattutorialshowpref, true)
        set(introshow) = prefs.edit().putBoolean(privatechattutorialshowpref, introshow).apply()

    var consettingtutorialshow: Boolean
        get() = prefs.getBoolean(consettingtutorialshowpref, true)
        set(introshow) = prefs.edit().putBoolean(consettingtutorialshowpref, introshow).apply()

    var autoreplyshow: Boolean
        get() = prefs.getBoolean(autoreplyshowpref, true)
        set(introshow) = prefs.edit().putBoolean(autoreplyshowpref, introshow).apply()

    var whatnewbuttonshow: Boolean
        get() = prefs.getBoolean(whatnewbuttonshowpref, true)
        set(introshow) = prefs.edit().putBoolean(whatnewbuttonshowpref, introshow).apply()


    var isotpdeleteset: Boolean
        get() = prefs.getBoolean(isotpdeletesetpref, false)
        set(introshow) = prefs.edit().putBoolean(isotpdeletesetpref, introshow).apply()

    var maliciousWebsiteBtnSwichP: Boolean
        get() = prefs.getBoolean(maliciousWebsiteBtnSwichPpref, false)
        set(introshow) = prefs.edit().putBoolean(maliciousWebsiteBtnSwichPpref, introshow).apply()

    var isendtoendencrtepted: Boolean
        get() = prefs.getBoolean(isendtoendencrteptedpref, false)
        set(introshow) = prefs.edit().putBoolean(isendtoendencrteptedpref, introshow).apply()

    var isdringmodeone: Boolean
        get() = prefs.getBoolean(isdringmodeonepref, false)
        set(introshow) = prefs.edit().putBoolean(isdringmodeonepref, introshow).apply()

    var isinappbrowser: Boolean
        get() = prefs.getBoolean(isinappbrowserpref, false)
        set(introshow) = prefs.edit().putBoolean(isinappbrowserpref, introshow).apply()

    var isnotificationshow: Boolean
        get() = prefs.getBoolean(isnotificationshowpref, true)
        set(introshow) = prefs.edit().putBoolean(isnotificationshowpref, introshow).apply()

    var iswakescreen: Boolean
        get() = prefs.getBoolean(iswakescreenpref, false)
        set(introshow) = prefs.edit().putBoolean(iswakescreenpref, introshow).apply()

    var isdeliveryconfirmation: Boolean
        get() = prefs.getBoolean(isdeliveryconfirmationpref, false)
        set(introshow) = prefs.edit().putBoolean(isdeliveryconfirmationpref, introshow).apply()

    var userpreferencelanguageCode: String
        get() = prefs.getString(userpreferencelanguageCodepref, "en")!!
        set(introshow) = prefs.edit().putString(userpreferencelanguageCodepref, introshow).apply()


    var Interstitial_Ads_Intro_VS_Default_MS: String
        get() = prefs.getString("Interstitial_Ads_Intro_VS_Default_MS", "2")!!
        set(introshow) = prefs.edit().putString("Interstitial_Ads_Intro_VS_Default_MS", introshow)
            .apply()

    var Interstitial_Notification_Back_Press_Show_VS_Not_MS: String
        get() = prefs.getString("Interstitial_Notification_Back_Press_Show_VS_Not_MS", "1")!!
        set(introshow) = prefs.edit()
            .putString("Interstitial_Notification_Back_Press_Show_VS_Not_MS", introshow).apply()

    var Banner_Home_Old_App_VS_New_App: String
        get() = prefs.getString("Banner_Home_Old_App_VS_New_App", "2")!!
        set(introshow) = prefs.edit().putString("Banner_Home_Old_App_VS_New_App", introshow).apply()

    var Home_Banner_VS_Collapsible: String
        get() = prefs.getString("Home_Banner_VS_Collapsible", "2")!!
        set(introshow) = prefs.edit().putString("Home_Banner_VS_Collapsible", introshow).apply()

    var userpreferencelanguage: String
        get() = prefs.getString(userpreferencelanguagepref, "English")!!
        set(introshow) = prefs.edit().putString(userpreferencelanguagepref, introshow).apply()

    var userpreferenceSignature: String
        get() = prefs.getString(userpreferenceSignaturepref, "Signature")!!
        set(introshow) = prefs.edit().putString(userpreferenceSignaturepref, introshow).apply()

    var firsttimeevent: Boolean
        get() = prefs.getBoolean("firsttimeevent", false)!!
        set(introshow) = prefs.edit().putBoolean("firsttimeevent", introshow).apply()

    var sedulemessageshowcaseshow: Boolean
        get() = prefs.getBoolean("sedulemessageshowcaseshow", true)!!
        set(introshow) = prefs.edit().putBoolean("sedulemessageshowcaseshow", introshow).apply()

    var traslatemessageshowcaseshow: Boolean
        get() = prefs.getBoolean("traslatemessageshowcaseshow", true)!!
        set(introshow) = prefs.edit().putBoolean("traslatemessageshowcaseshow", introshow).apply()

    var sendmessageinteraddcount: Int
        get() = prefs.getInt("sendmessageinteraddcount", 0)!!
        set(introshow) = prefs.edit().putInt("sendmessageinteraddcount", introshow).apply()

    var Backpresscount: Long
        get() = prefs.getLong("Backpresscount", 4)!!
        set(introshow) = prefs.edit().putLong("Backpresscount", introshow).apply()


    fun ringtone(threadId: Long = 0): String {
        val default =
            prefs.getString("ringtone", Settings.System.DEFAULT_NOTIFICATION_URI.toString())

        return when (threadId) {
            0L -> default.toString()
            else -> prefs.getString("ringtone_$threadId", default).toString()
        }
    }

    var userpreferenceAutoMessageDelete: String
        get() = prefs.getString(userpreferenceAutoMessageDeletepref, "0")!!
        set(introshow) = prefs.edit().putString(userpreferenceAutoMessageDeletepref, introshow)
            .apply()

    var isAutoNotificationStart: Boolean
        get() = prefs.getBoolean(isAutoNotificationStartpref, true)
        set(introshow) = prefs.edit().putBoolean(isAutoNotificationStartpref, introshow).apply()

    var userpreferenceSignatureOnOff: Boolean
        get() = prefs.getBoolean(userpreferenceSignatureOnOffpref, false)
        set(introshow) = prefs.edit().putBoolean(userpreferenceSignatureOnOffpref, introshow)
            .apply()

    var totalpucheshcount: Int
        get() = prefs.getInt("totalpucheshcount", 0)
        set(introshow) = prefs.edit().putInt("totalpucheshcount", introshow).apply()

    var SystemTextViewSwitchAb: Boolean
        get() = prefs.getBoolean("SystemTextViewSwitchAb", false)
        set(introshow) = prefs.edit().putBoolean("SystemTextViewSwitchAb", introshow).apply()

    var SystemTextViewSwitchAbCaller: Boolean
        get() = prefs.getBoolean("SystemTextViewSwitchAbCaller", true)
        set(introshow) = prefs.edit().putBoolean("SystemTextViewSwitchAbCaller", introshow).apply()

    var Systemgeneratediconswitchab: Boolean
        get() = prefs.getBoolean("Systemgeneratediconswitchab", true)
        set(introshow) = prefs.edit().putBoolean("Systemgeneratediconswitchab", introshow).apply()

    fun saveOrdertab(tablist: List<Category>) {
        val gson = Gson()
        val json2 = gson.toJson(tablist)
        prefs.edit().putString(tabListpref, json2).apply()
    }

    fun getsaveOrdertab(): ArrayList<Category> {
        val json = prefs.getString(tabListpref, null)
        val type = object : TypeToken<List<Category>>() {}.type
        return ArrayList(Gson().fromJson(json, type) ?: emptyList())
    }

    var sendLongMessageMMS: Boolean
        get() = prefs.getBoolean(SEND_LONG_MESSAGE_MMS, false)
        set(sendLongMessageMMS) = prefs.edit().putBoolean(SEND_LONG_MESSAGE_MMS, sendLongMessageMMS)
            .apply()

    var sendGroupMessageMMS: Boolean
        get() = prefs.getBoolean(SEND_GROUP_MESSAGE_MMS, false)
        set(sendGroupMessageMMS) = prefs.edit()
            .putBoolean(SEND_GROUP_MESSAGE_MMS, sendGroupMessageMMS).apply()

    var useSimpleCharacters: Boolean
        get() = prefs.getBoolean(USE_SIMPLE_CHARACTERS, true)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(USE_SIMPLE_CHARACTERS, useSimpleCharacters).apply()

    var blockedKeywords: Set<String>
        get() = prefs.getStringSet(BLOCKED_KEYWORDS, HashSet<String>())!!
        set(blockedKeywords) = prefs.edit().putStringSet(BLOCKED_KEYWORDS, blockedKeywords).apply()

    var firsttimeusersetlang: Boolean
        get() = prefs.getBoolean(firsttimeusersetlangpref, false)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(firsttimeusersetlangpref, useSimpleCharacters).apply()

    var firsttimelangEvent: Boolean
        get() = prefs.getBoolean("firsttimelangEvent", true)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean("firsttimelangEvent", useSimpleCharacters).apply()

    var Message_First_Intro: Boolean
        get() = prefs.getBoolean("Message_First_Intro", true)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean("Message_First_Intro", useSimpleCharacters).apply()

    var Message_First_Premium: Boolean
        get() = prefs.getBoolean("Message_First_Premium", true)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean("Message_First_Premium", useSimpleCharacters).apply()

    var Message_First_Permissionoverlay: Boolean
        get() = prefs.getBoolean("Message_First_Permissionoverlay", true)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean("Message_First_Permissionoverlay", useSimpleCharacters).apply()

    var funnelOn: Boolean
        get() = prefs.getBoolean("funnelOn", true)
        set(value) = prefs.edit().putBoolean("funnelOn", value).apply()

    var String.funnelState: Boolean
        get() {
            "config.firsttimelangEvent funnelOn:$funnelOn $isLanguageChanging $this".log()
            if (isLanguageChanging) {
                isLanguageChanging = false
                return true
            }
            if (!funnelOn) {
                funnelState = false
                return false
            }

            val state = prefs.getBoolean(this, true)
            if (!state) {
                funnelOn = false
                return false
            } else {
                return state
            }
        }
        set(value) = prefs.edit().putBoolean(this, value).apply()


    var String.funnelStatelang: Boolean
        get() {
//            "config.firsttimelangEvent funnelOn:$funnelOn $isLanguageChanging $this".log()
//            if (isLanguageChanging) {
//                isLanguageChanging = false
//                return true
//            }
            if (!funnelOn) {
                funnelState = false
                return false
            }

            val state = prefs.getBoolean(this, true)
            if (!state) {
                funnelOn = false
                return false
            } else {
                return state
            }
        }
        set(value) = prefs.edit().putBoolean(this, value).apply()


    var Message_First_Home: Boolean
        get() = prefs.getBoolean("Message_First_Home", true)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean("Message_First_Home", useSimpleCharacters).apply()

    var Message_splash_ads_dialog_show: Boolean
        get() = prefs.getBoolean("Message_splash_ads_dialog_show", true)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean("Message_splash_ads_dialog_show", useSimpleCharacters).apply()

    var All_Ads_On: Boolean
        get() = prefs.getBoolean("All_Ads_On", true)
        set(useSimpleCharacters) = prefs.edit().putBoolean("All_Ads_On", useSimpleCharacters)
            .apply()

    var isSplashBannerAdOn: Boolean
        get() = prefs.getBoolean("isSplashBannerAdOn", true)
        set(value) = prefs.edit().putBoolean("isSplashBannerAdOn", value).apply()

    var isAftercallNativeAdOn: Boolean
        get() = prefs.getBoolean("isAftercallNativeAdOn", true)
        set(value) = prefs.edit().putBoolean("isAftercallNativeAdOn", value).apply()

    var Aftercall_BannerAd_VS_NativeAd: String
        get() = prefs.getString("Aftercall_BannerAd_VS_NativeAd", "1") ?: "1"
        set(value) = prefs.edit().putString("Aftercall_BannerAd_VS_NativeAd", value).apply()

    var isHomeBannerAdOn: Boolean
        get() = prefs.getBoolean("isHomeBannerAdOn", true)
        set(value) = prefs.edit().putBoolean("isHomeBannerAdOn", value).apply()

    var isSearchBannerAdOn: Boolean
        get() = prefs.getBoolean("isSearchBannerAdOn", true)
        set(value) = prefs.edit().putBoolean("isSearchBannerAdOn", value).apply()

    var AfterCall_Native_Admob_VS_Applovin: String
        get() = prefs.getString("AfterCall_Native_Admob_VS_Applovin", "1") ?: "1"
        set(value) = prefs.edit().putString("AfterCall_Native_Admob_VS_Applovin", value).apply()

    var home_overlay_banner: String
        get() = prefs.getString("home_overlay_banner", "1") ?: "1"
        set(value) = prefs.edit().putString("home_overlay_banner", value).apply()

    var home_overlay_permission_dialog: String
        get() = prefs.getString("home_overlay_permission_dialog", "1") ?: "1"
        set(value) = prefs.edit().putString("home_overlay_permission_dialog", value).apply()

    var isAftercallBannerAdOn: Boolean
        get() = prefs.getBoolean("isAftercallBannerAdOn", true)
        set(value) = prefs.edit().putBoolean("isAftercallBannerAdOn", value).apply()

    var isLanguageNativeAdOn: Boolean
        get() = prefs.getBoolean("isLanguageNativeAdOn", true)
        set(value) = prefs.edit().putBoolean("isLanguageNativeAdOn", value).apply()

    var isChatNativeAdOn: Boolean
        get() = prefs.getBoolean("isChatNativeAdOn", true)
        set(value) = prefs.edit().putBoolean("isChatNativeAdOn", value).apply()

    var isProfileNativeAdOn: Boolean
        get() = prefs.getBoolean("isProfileNativeAdOn", true)
        set(value) = prefs.edit().putBoolean("isProfileNativeAdOn", value).apply()


    var Message_First_Setasdefaulted: Boolean
        get() = prefs.getBoolean("Message_First_Setasdefaulted", true)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean("Message_First_Home", useSimpleCharacters).apply()

    var userfirsttimeopenapp: Boolean
        get() = prefs.getBoolean(userfirsttimeopenapppref, true)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(userfirsttimeopenapppref, useSimpleCharacters).apply()

    var secondshowcaseshowforonlysetting: Boolean
        get() = prefs.getBoolean(secondshowcaseshowforonlysettingpref, true)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(secondshowcaseshowforonlysettingpref, useSimpleCharacters).apply()

    var thirdshowcaseshowforprivacyandsecurity: Boolean
        get() = prefs.getBoolean(thirdshowcaseshowforprivacyandsecuritypref, true)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(thirdshowcaseshowforprivacyandsecuritypref, useSimpleCharacters).apply()

    var forthdshowcaseshowfordrivingmode: Boolean
        get() = prefs.getBoolean(forthdshowcaseshowfordrivingmodepref, true)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(forthdshowcaseshowfordrivingmodepref, useSimpleCharacters).apply()

    var fifthshowcaseshowforsignature: Boolean
        get() = prefs.getBoolean(fifthshowcaseshowforsignaturepref, true)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(fifthshowcaseshowforsignaturepref, useSimpleCharacters).apply()

    var sixshowcaseshowforinappbrowser: Boolean
        get() = prefs.getBoolean(sixshowcaseshowforinappbrowserpref, true)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(sixshowcaseshowforinappbrowserpref, useSimpleCharacters).apply()

    var sevenshowcaseshowforcharactercount: Boolean
        get() = prefs.getBoolean(sevenshowcaseshowforcharactercountpref, true)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(sevenshowcaseshowforcharactercountpref, useSimpleCharacters).apply()

    var eightshowcaseshowforsendgropmessage: Boolean
        get() = prefs.getBoolean(eightshowcaseshowforsendgropmessagepref, true)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(eightshowcaseshowforsendgropmessagepref, useSimpleCharacters).apply()

    var nineshowcaseshowforsendlongmessage: Boolean
        get() = prefs.getBoolean(nineshowcaseshowforsendlongmessagepref, true)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(nineshowcaseshowforsendlongmessagepref, useSimpleCharacters).apply()

    var tenshowcaseshowforremoveaccents: Boolean
        get() = prefs.getBoolean(tenshowcaseshowforremoveaccentspref, true)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(tenshowcaseshowforremoveaccentspref, useSimpleCharacters).apply()

    var elevenshowcaseshowforremoveaccents: Boolean
        get() = prefs.getBoolean(elevenshowcaseshowforremoveaccentspref, true)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(elevenshowcaseshowforremoveaccentspref, useSimpleCharacters).apply()


    var usershowsendmessageshowcase: Boolean
        get() = prefs.getBoolean(usershowsendmessageshowcasepref, true)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(usershowsendmessageshowcasepref, useSimpleCharacters).apply()

    var usershowviewdetailsshowcase: Boolean
        get() = prefs.getBoolean(usershowviewdetailsshowcasepref, false)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(usershowviewdetailsshowcasepref, useSimpleCharacters).apply()

    var isprivacychatscreenopen: Boolean
        get() = prefs.getBoolean(isprivacychatscreenopenpref, false)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(isprivacychatscreenopenpref, useSimpleCharacters).apply()

    var isapplockchatscreenopen: Boolean
        get() = prefs.getBoolean(isapplockchatscreenopenpref, false)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(isapplockchatscreenopenpref, useSimpleCharacters).apply()

    var isprivacyandsecscreenopen: Boolean
        get() = prefs.getBoolean(isprivacyandsecscreenopenpref, false)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(isprivacyandsecscreenopenpref, useSimpleCharacters).apply()

    var isblocknumberscreenopen: Boolean
        get() = prefs.getBoolean(isblocknumberscreenopenpref, false)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(isblocknumberscreenopenpref, useSimpleCharacters).apply()

    var ismanageblocknumberscreenopen: Boolean
        get() = prefs.getBoolean(ismanageblocknumberscreenopenpref, false)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(ismanageblocknumberscreenopenpref, useSimpleCharacters).apply()


    var isdriwingmodescreenopen: Boolean
        get() = prefs.getBoolean(isdriwingmodescreenopenpref, false)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(isdriwingmodescreenopenpref, useSimpleCharacters).apply()

    var isbackupandrestorescreenopen: Boolean
        get() = prefs.getBoolean(isbackupandrestorescreenopenpref, false)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(isbackupandrestorescreenopenpref, useSimpleCharacters).apply()

    var isschedulemessagescreenopen: Boolean
        get() = prefs.getBoolean(isschedulemessagescreenopenpref, false)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(isschedulemessagescreenopenpref, useSimpleCharacters).apply()

    var isswipemotionscreenopen: Boolean
        get() = prefs.getBoolean(isswipemotionscreenopenpref, false)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(isswipemotionscreenopenpref, useSimpleCharacters).apply()

    var iscolorthemescreenopen: Boolean
        get() = prefs.getBoolean(iscolorthemescreenopenpref, false)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(iscolorthemescreenopenpref, useSimpleCharacters).apply()

    var isapplangscreenopen: Boolean
        get() = prefs.getBoolean(isapplangscreenopenpref, false)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(isapplangscreenopenpref, useSimpleCharacters).apply()

    var isnotificationscreenopen: Boolean
        get() = prefs.getBoolean(isnotificationscreenopenpref, false)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(isnotificationscreenopenpref, useSimpleCharacters).apply()

    var iscustomwallopaperscreenopen: Boolean
        get() = prefs.getBoolean(iscustomwallopaperscreenopenpref, false)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(iscustomwallopaperscreenopenpref, useSimpleCharacters).apply()

    var ismessagetextsizescreenopen: Boolean
        get() = prefs.getBoolean(ismessagetextsizescreenopenpref, false)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(ismessagetextsizescreenopenpref, useSimpleCharacters).apply()

    var ismessagecornerscreenopen: Boolean
        get() = prefs.getBoolean(ismessagecornerscreenopenpref, false)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(ismessagecornerscreenopenpref, useSimpleCharacters).apply()

    var istraslatescreenopen: Boolean
        get() = prefs.getBoolean(istraslatescreenopenpref, false)
        set(useSimpleCharacters) = prefs.edit()
            .putBoolean(istraslatescreenopenpref, useSimpleCharacters).apply()

    var isrationshow: Boolean
        get() = prefs.getBoolean("isrationshow", true)
        set(useSimpleCharacters) = prefs.edit().putBoolean("isrationshow", useSimpleCharacters)
            .apply()


    fun addBlockedKeyword(keyword: String) {
        blockedKeywords = blockedKeywords.plus(keyword)
    }

    fun removeBlockedKeyword(keyword: String) {
        blockedKeywords = blockedKeywords.minus(keyword)
    }

    fun saveCurrentDateOnce() {
        if (!prefs.contains(DATE_KEY)) {
            val currentDate = Calendar.getInstance()
            currentDate.add(Calendar.DAY_OF_MONTH, 5) // Add 5 days to the current date
            val nextFiveDaysDate = currentDate.timeInMillis
            val editor = prefs.edit()
            editor.putLong(DATE_KEY, nextFiveDaysDate)
            editor.apply()
        }
    }

    fun savethidshowcaseforprivacyOnce() {
        if (!prefs.contains(DATE_KEY_privacy)) {
            val currentDate = Calendar.getInstance()
            currentDate.add(Calendar.DAY_OF_MONTH, 3) // Add 5 days to the current date
            val nextFiveDaysDate = currentDate.timeInMillis
            val editor = prefs.edit()
            editor.putLong(DATE_KEY_privacy, nextFiveDaysDate)
            editor.apply()
        }
    }

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = prefs.getLong(DATE_KEY, 0)
        val formattedDate =
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)
        return formattedDate
    }

    fun getThridShowcaseprivacyDate(): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = prefs.getLong(DATE_KEY_privacy, 0)
        val formattedDate =
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)
        return formattedDate
    }

    fun saveProfileColor(color: ChatScreenColor) {
        val gson = Gson()
        val colorJson = gson.toJson(color)
        prefs3.edit().putString(color.tredid, colorJson).apply()
    }

    fun getProfileColor(id: String): ChatScreenColor? {
        val gson = Gson()
        val colorJson = prefs3.getString(id.toString(), null) ?: return null
        val type = object : TypeToken<ChatScreenColor>() {}.type
        return gson.fromJson(colorJson, type)
    }

    fun saveProfileThemeColor(color: ProfileColorTheme) {
        val gson = Gson()
        val colorJson = gson.toJson(color)
        prefs6.edit().putString(color.tredid, colorJson).apply()
    }

    fun getProfileThemeColor(id: String): ProfileColorTheme? {
        val gson = Gson()
        val colorJson = prefs6.getString(id.toString(), null) ?: return null
        val type = object : TypeToken<ProfileColorTheme>() {}.type
        return gson.fromJson(colorJson, type)
    }

    fun removeProfileThemeColor(id: String) {
        prefs6.edit().remove(id).apply()
    }

    fun saveProfileColorAll(color: ChatScreenColor) {
        val gson = Gson()
        val colorJson = gson.toJson(color)
        prefs.edit().putString("AllColor", colorJson).apply()
    }

    fun getProfileColorAll(id: String): ChatScreenColor? {
        val gson = Gson()
        val colorJson = prefs.getString("AllColor", null) ?: return null
        val type = object : TypeToken<ChatScreenColor>() {}.type
        return gson.fromJson(colorJson, type)
    }


    fun removeAllProfileColor() {
        prefs3.edit().clear().apply()
    }


    fun saveProfileUri(color: CustomProfileUri) {
        val gson = Gson()
        val colorJson = gson.toJson(color)
        prefs2.edit().putString(color.tredid, colorJson).apply()
    }

    fun getProfileUri(id: String): CustomProfileUri? {
        val gson = Gson()
        val colorJson = prefs2.getString(id.toString(), null) ?: return null
        val type = object : TypeToken<CustomProfileUri>() {}.type
        return gson.fromJson(colorJson, type)
    }

    fun removeProfileUri(id: String) {
        prefs2.edit().remove(id).apply()
    }

    fun removeAllProfileUri() {
        prefs2.edit().clear().apply()
    }

    fun saveAllMessageList(list: List<Conversation>) {
        val subList = list.take(10)
        val json = Gson().toJson(subList)
        prefs4.edit().putString(AllMessageListpref, json).apply()
    }

    fun getAllMessageList(): List<Conversation>? {
        val gson = Gson()
        val json = prefs4.getString(AllMessageListpref, null)
        val type = object : TypeToken<List<Conversation>>() {}.type
        return if (type == null) {
            emptyList()
        } else {
            gson.fromJson(json, type)
        }
    }

    fun savecustomprofilealpha(color: CustomProfileAlpha) {
        val gson = Gson()
        val colorJson = gson.toJson(color)
        prefs.edit().putString(color.tredid, colorJson).apply()
    }

    fun getcustomprofilealpha(id: String): CustomProfileAlpha? {
        val gson = Gson()
        val colorJson = prefs.getString(id.toString(), null) ?: return null
        val type = object : TypeToken<CustomProfileAlpha>() {}.type
        return gson.fromJson(colorJson, type)
    }

//    var isShowRatingDialog: Boolean
//        set(value) = prefs.edit().putBoolean("isShownewdialog", value).apply()
//        get() = prefs.getBoolean("isShownewdialog", false)

//    var rateDialogPerameter: Int
//        set(value) = prefs.edit().putInt("rate", value).apply()
//        get() = prefs.getInt("rate", 0)


    var isShowRatingDialogNew: Boolean
        set(value) = prefs.edit().putBoolean("isShowRatingDialogNew", value).apply()
        get() = prefs.getBoolean("isShowRatingDialogNew", false)

    var rateDialogPerameterNew: Int
        set(value) = prefs.edit().putInt("rateDialogPerameterNew", value).apply()
        get() = prefs.getInt("rateDialogPerameterNew", 0)

//    *In Your SharedPref*

    var ratingDialogCounter: Int
        set(value) = prefs.edit().putInt("ratingDialogCounter", value).apply()
        get() = prefs.getInt("ratingDialogCounter", 0)

    var Home_Banner_VS_Native_VS_Random: String
        set(value) = prefs.edit().putString("Home_Banner_VS_Native_VS_Random", value).apply()
        get() = prefs.getString("Home_Banner_VS_Native_VS_Random", "3").toString()

//    var isShowRatingDialog: Boolean
//        set(value) = prefs.edit().putBoolean("isShow", value).apply()
//        get() = prefs.getBoolean("isShow", false)


    var isSoundEnabled: Boolean
        get() = prefs.getBoolean("SOUND", true)
        set(enabled) = prefs.edit().putBoolean("SOUND", enabled).apply()


    var flashlightState: Int
        get() = prefs.getInt("FLASHLIGHT_STATE", FLASH_OFF)
        set(state) = prefs.edit().putInt("FLASHLIGHT_STATE", state).apply()


    var backVideoResIndex: Int
        get() = prefs.getInt("BACK_VIDEO_RESOLUTION_INDEX", 0)
        set(backVideoResIndex) = prefs.edit()
            .putInt("BACK_VIDEO_RESOLUTION_INDEX", backVideoResIndex).apply()


    var savePhotoVideoLocation: Boolean
        get() = prefs.getBoolean("SAVE_PHOTO_VIDEO_LOCATION", false)
        set(savePhotoVideoLocation) = prefs.edit()
            .putBoolean("SAVE_PHOTO_VIDEO_LOCATION", savePhotoVideoLocation).apply()


    var captureMode: CaptureMode
        get() = CaptureMode.values()[prefs.getInt(
            "CAPTURE_MODE", CaptureMode.MINIMIZE_LATENCY.ordinal
        )]
        set(captureMode) = prefs.edit().putInt("CAPTURE_MODE", captureMode.ordinal).apply()

    var timerMode: TimerMode
        get() = TimerMode.values().getOrNull(prefs.getInt("TIMER_MODE", TimerMode.OFF.ordinal))
            ?: TimerMode.OFF
        set(timerMode) = prefs.edit().putInt("TIMER_MODE", timerMode.ordinal).apply()


    var subscriptionModelStart: Boolean
        get() = prefs.getBoolean("subscriptionModelStart", true)!!
        set(lastExportPath) = prefs.edit().putBoolean("subscriptionModelStart", lastExportPath)
            .apply()

    var Get_Offer_Config_MS: String
        get() = prefs.getString(
            "Get_Offer_Config_MS", "{\"shouldStartTimer\":true,\"timerMinutes\":9,\"oneTime\":true}"
        )!!
        set(lastExportPath) = prefs.edit().putString("Get_Offer_Config_MS", lastExportPath).apply()

    var sundialogcount: Int
        get() = prefs.getInt("sundialogcount", 0)
        set(backVideoResIndex) = prefs.edit().putInt("sundialogcount", backVideoResIndex).apply()

    var lastChatBackAdDate: String
        get() = prefs.getString("lastChatBackAdDate", "") ?: ""
        set(value) = prefs.edit().putString("lastChatBackAdDate", value).apply()

    fun shouldShowChatBackAd(): Boolean {
        val today = getTodayDate()
        Log.e("FFF", "shouldShowChatBackAd: lastChatBackAdDate = $lastChatBackAdDate     today = $today", )
        return lastChatBackAdDate != today
    }

    fun markChatBackAdShown() {
        lastChatBackAdDate = getTodayDate()
    }

    private fun getTodayDate(): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(Date())
    }

}
