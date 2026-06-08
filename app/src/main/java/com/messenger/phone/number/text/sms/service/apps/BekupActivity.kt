package com.messenger.phone.number.text.sms.service.apps

import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.demo.adsmanage.Commen.firebaseEventMain
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createOptionBackground
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getBottomNavigationBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperStatusBarColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTempFile
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isexportstart
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isimportstart
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.Dialog.BackupsFileDialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.ImportMessagesDialog
import com.messenger.phone.number.text.sms.service.apps.GoogleMobileAdsConsentManagerChack.GoogleMobileAdsConsentManager
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.ads.v2.AdsOrchestrator


import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityBekupBinding
import com.messenger.phone.number.text.sms.service.apps.helper.MessagesExporter
import com.messenger.phone.number.text.sms.service.apps.modelClass.BackupFilesModel
import com.messenger.phone.number.text.sms.service.apps.modelClass.SmsData
import com.simplemobiletools.commons.extensions.getContrastColor
import com.simplemobiletools.commons.extensions.showErrorToast
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class BekupActivity : AppCompatActivity() {

    @Inject
    lateinit var messagerDatabaseRepo: MessagerDatabaseRepo


    private val smsExporter by lazy { MessagesExporter(this) }

    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

    var whichbuttonclick = "backup"

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    var isshowads: Boolean = false
    private val adsOrchestrator by lazy { AdsOrchestrator.get(application) }

    var datalist = ArrayList<BackupFilesModel>()

    lateinit var binding: ActivityBekupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bekup)
        setBaseTheme(binding.vAnd15StatusBar)
        this.firebaseEventMain("Backup")
        config.fragmentshowcasecout = 13
        config.isbackupandrestorescreenopen = true
        binding.textView3.isSelected = true
        binding.textView42last.text = baseConfig.last_backup_time

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


        binding.textView42lastAb.text = baseConfig.last_backup_time

        googleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.getInstance(applicationContext)

        googleMobileAdsConsentManager.gatherConsent(this@BekupActivity) { consentError ->
            if (consentError != null) {
                Log.w(
                    "TAG",
                    "<-----------> ${consentError.errorCode}. ${consentError.message}"
                )
            }
        }

        if (googleMobileAdsConsentManager.canRequestAds) {
            isshowads = true
        }


        binding.backupNow.setOnClickListener {
            adsOrchestrator.showBackupRewarded(this) {
                startBackupNowFlow()
            }
        }

        binding.restoreBackupCard.setOnClickListener { requestRestoreFlow() }

        binding.restoreBackupCardAb.setOnClickListener { requestRestoreFlow() }



        binding.backBtn.setOnClickListener {
            finish()
        }



    }

    private fun requestRestoreFlow() {
        adsOrchestrator.showBackupRewarded(this) {
            restore()
        }
    }

    private fun startBackupNowFlow() {
        CoroutineScope(Dispatchers.IO).launch {
            runOnUiThread {
                binding.textView41.setText(resources.getString(R.string.backing_up_messages))
                binding.textView41Ab.setText(resources.getString(R.string.backing_up_messages))
            }
            val file = getFilePathNew()
            if (file == null) {
                runOnUiThread {
                    toast(R.string.unknown_error_occurred)
                }
                return@launch
            }
            exportMessagesTo(file.outputStream())
        }
    }

    private fun restore() {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                datalist.clear()
                datalist.addAll(getAllBackupFiles())
            }
            withContext(Dispatchers.Main) {
                if (datalist.isEmpty()) {
                    toast(R.string.backups_not_found)
                    return@withContext
                }

                BackupsFileDialog(datalist = datalist) { data ->
                    Log.d("", "onCreate:BackupsFileDialog <-----------> ${data}")

                    val builder = MaterialAlertDialogBuilder(this@BekupActivity)
                        .setTitle(getString(R.string.restore_from_backup))
                        .setMessage(R.string.are_u_sure_you_want_to_restore_from_backup)
                        .setPositiveButton(R.string.Restore) { dialog, which ->
                            val uri = convertFilePathToUri(data.path)
                            tryImportMessagesFromFile(uri)
                        }
                        .setNegativeButton(R.string.cancel) { dialog, which ->
                            dialog.dismiss()
                        }

                    val dialog = builder.create()  // or builder.show() directly, but we need the instance

                    dialog.setCancelable(true)
                    dialog.setCanceledOnTouchOutside(true)

                    dialog.show()
                }.show(supportFragmentManager, "BackupsFileDialog")
            }
        }
    }

    private fun getFilePath(): File {
        val currentDateTime = LocalDateTime.now()
        val formatter =
            DateTimeFormatter.ofPattern("yyyy,HH-mm-ss") // Note: Changed ":" to "-" to avoid issues with filenames
        val formattedDateTime = currentDateTime.format(formatter)
        val cacheDir: File = cacheDir
        val directoryPath = File(cacheDir, "MessageApp/backup")

        // Create the directories if they don't exist
        if (!directoryPath.exists()) {
            directoryPath.mkdirs()
        }

        val fileName = "$formattedDateTime.json"
        return File(directoryPath, fileName)
    }

    private suspend fun getFilePathNew(): File? = withContext(Dispatchers.IO) {
        val fileName = "${System.currentTimeMillis()}.json"
        try {
            val baseDir = resolveWritableBackupDirectory()
            if (baseDir == null) {
                Log.e("BekupActivity", "Unable to resolve a writable backup directory")
                return@withContext null
            }
            val filePath = File(baseDir, fileName)
            if (!filePath.exists() && !filePath.createNewFile()) {
                Log.e("BekupActivity", "Unable to create backup file: ${filePath.absolutePath}")
                return@withContext null
            }
            return@withContext filePath
        } catch (e: Exception) {
            Log.e("BekupActivity", "Failed to create backup file", e)
            return@withContext null
        }
    }

    private fun exportMessagesTo(outputStream: OutputStream?) {
        isexportstart = true
        toastMess(R.string.exporting)
        runOnUiThread {
            binding.backupProgressCard.visible()
            binding.backupProgressCardAb.visible()
        }
        ensureBackgroundThread {
            smsExporter.exportMessages(outputStream) {
                val toastId = when (it) {
                    MessagesExporter.ExportResult.EXPORT_OK -> R.string.exporting_successful
                    else -> R.string.exporting_failed
                }
                isexportstart = false
                toastMess(toastId)
                runOnUiThread {
                    Handler(mainLooper).postDelayed({
                        binding.backupProgressCard.gone()
                        binding.backupProgressCardAb.gone()
                        baseConfig.last_backup_time = getformatedtime(System.currentTimeMillis())
                        binding.textView42last.text = baseConfig.last_backup_time
                        binding.textView42lastAb.text = baseConfig.last_backup_time
                    }, 2000)
                }
            }
        }
    }

    fun convertFilePathToUri(filePath: String): Uri {
        val file = File(filePath)
        return Uri.fromFile(file)
    }

    private fun tryImportMessagesFromFile(uri: Uri) {
        Log.d(
            "ImportMessages",
            "tryImportMessagesFromFile <--------> : $uri, Scheme: ${uri.scheme}"
        )
        when (uri.scheme) {
            "file" -> showImportEventsDialog(uri.path!!)
            "content" -> {
                val tempFile = getTempFile("messages", "backup.json")
                if (tempFile == null) {
//                    importfail()
                    toast(R.string.unknown_error_occurred)
                    return
                }
                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    val out = FileOutputStream(tempFile)
                    inputStream!!.copyTo(out)
                    showImportEventsDialog(tempFile.absolutePath)
                } catch (e: Exception) {
//                    importfail()
                    showErrorToast(e)
                }
            }

            else -> {
//                importfail()
                toast(R.string.invalid_file_format)
            }
        }
    }


    private fun showImportEventsDialog(path: String) {
        ImportMessagesDialog(this, path, binding)
    }

    override fun onBackPressed() {
        Log.d(
            "",
            "onBackPressed: isexportstart <-----> ${isexportstart} isimportstart <------> ${isimportstart}"
        )
        if (isexportstart || isimportstart) {
            exitDialog()
        } else {
            finish()
        }
    }

    private fun exitDialog() {

        val builder = MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.Confirmation))
            .setCancelable(false)
            .setMessage(resources.getString(R.string.Are_you_sure_you_want))
            .setPositiveButton(resources.getString(R.string.Yes)) { dialog, which ->
                finish()
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.No)) { dialog, which ->
                dialog.dismiss()
            }
        builder.create()
        builder.show()

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
        val optionFillColor = primaryColor.adjustAlpha(0.10f)
        val optionStrokeColor = primaryColor.adjustAlpha(0.30f)
        val optionCorner = resources.getDimension(com.intuit.sdp.R.dimen._5sdp)
        val optionStrokeWidth = resources.getDimension(com.intuit.sdp.R.dimen._1sdp)

        if (config.setABHomeActivityPref == "1") {
            binding.fistContenar.visible()
            binding.fistContenarAb.gone()
        } else {
            binding.fistContenar.gone()
            binding.fistContenarAb.visible()
        }

        binding.root.setBackgroundColor(surfaceColor)
        findViewById<View>(R.id.main)?.setBackgroundColor(surfaceColor)
        binding.toolBarMain.setBackgroundColor(surfaceColor)
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        binding.textView3.setTextColor(textColor)
        binding.backBtn.setIconResource(R.drawable.recycle_bin_material_ic_arrow_back_rounded)
        binding.backBtn.iconTint = ColorStateList.valueOf(secondaryTextColor)

        findViewById<View>(R.id.fist_contenar_chaild)?.setBackgroundColor(surfaceColor)
        findViewById<View>(R.id.fist_contenar_chaild_ab)?.setBackgroundColor(surfaceColor)

        intArrayOf(
            R.id.backup_progres_btn,
            R.id.last_progres_btn,
            R.id.restore_backup_btn,
            R.id.backup_progres_btn_ab,
            R.id.last_progres_btn_ab,
            R.id.restore_backup_btn_ab
        ).forEach { viewId ->
            findViewById<View>(viewId)?.background = createOptionBackground(
                cornerSize = optionCorner,
                fillColor = optionFillColor,
                strokeWidth = optionStrokeWidth,
                strokeColor = surfaceColor,
                showRipple = true,
                rippleColor = primaryColor.adjustAlpha(0.3f)
            )
        }

        intArrayOf(
            R.id.textView41,
            R.id.textView41last,
            R.id.textView41restore,
            R.id.textView41_ab,
            R.id.textView41last_ab,
            R.id.textView41restore_ab
        ).forEach { viewId ->
            findViewById<TextView>(viewId)?.setTextColor(textColor)
        }

//        intArrayOf(
//            R.id.textView42,
//            R.id.textView42last,
//            R.id.textView42restore,
//            R.id.textView42_ab,
//            R.id.textView42last_ab,
//            R.id.textView42restore_ab,
//            R.id.textView42restorenot,
//            R.id.textView42restorenot_ab
//        ).forEach { viewId ->
//            findViewById<TextView>(viewId)?.setTextColor(secondaryTextColor)
//        }

        intArrayOf(
            R.id.ic_backup_progres,
            R.id.ic_last_progres,
            R.id.ic_restore_backup,
            R.id.ic_backup_progres_ab,
            R.id.ic_last_progres_ab,
            R.id.ic_restore_backup_ab
        ).forEach { viewId ->
            findViewById<ImageView>(viewId)?.imageTintList = ColorStateList.valueOf(primaryColor)
        }

        intArrayOf(R.id.progress_bar, R.id.progress_bar_ab).forEach { viewId ->
            findViewById<LinearProgressIndicator>(viewId)?.apply {
                setIndicatorColor(primaryColor)
                trackColor = secondaryTextColor.adjustAlpha(0.24f)
            }
        }

        binding.backupNow.background = createOptionBackground(
            cornerSize = resources.getDimension(com.intuit.sdp.R.dimen._50sdp),
            fillColor = primaryColor,
            showRipple = true,
            rippleColor = Color.WHITE.adjustAlpha(0.3f),
        )

        findViewById<View>(R.id.view)?.setBackgroundColor(secondaryTextColor.adjustAlpha(0.16f))


        findViewById<ImageView>(R.id.imageView42)?.imageTintList =
            ColorStateList.valueOf(primaryColor.getContrastColor())
        findViewById<TextView>(R.id.txt1)?.setTextColor(primaryColor.getContrastColor())

        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars = ThemeModeManager.shouldUseLightSystemBars(this@BekupActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
    }



    fun getformatedtime(currentTimeMillis: Long): String {
        try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy,hh:mm a", Locale.getDefault())
            val date = Date(currentTimeMillis)
            val formattedDate = dateFormat.format(date)
            return formattedDate
        } catch (e: Exception) {
            return currentTimeMillis.toString()
        }
    }

    suspend fun getAllBackupFiles(): ArrayList<BackupFilesModel> = withContext(Dispatchers.IO) {
        val jsonlist = ArrayList<BackupFilesModel>()
        val gson = Gson()
        val listType = object : TypeToken<List<SmsData>>() {}.type
        getBackupDirectories().forEach { directory ->
            if (directory.exists() && directory.isDirectory) {
                directory.listFiles()?.forEach { file ->
                    if (file.isFile && file.name.endsWith(".json")) {
                        val smsDataList = parseBackupFileSafely(file, gson, listType) ?: return@forEach
                        val totalMessages = smsDataList.sumOf { it.sms.size }
                        val backupTime = file.name.removeSuffix(".json").toLongOrNull()
                        jsonlist.add(
                            BackupFilesModel(
                                filename = backupTime?.let { getformatedtime(it) }
                                    ?: file.name.removeSuffix(".json"),
                                fileSize = formatSize(file.length()),
                                totalMessage = "${totalMessages} messages",
                                path = file.path
                            )
                        )
                    }
                }
            }
        }
        return@withContext jsonlist
    }

    private fun resolveWritableBackupDirectory(): File? {
        return getBackupDirectories().firstOrNull { directory ->
            directory.exists() || directory.mkdirs()
        }
    }

    private fun getBackupDirectories(): List<File> {
        val sharedDocumentsDir =
            File(Environment.getExternalStorageDirectory(), "Documents/MessageApp/backup")
        val appDocumentsDir =
            getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.let {
                File(it, "MessageApp/backup")
            }
        return listOfNotNull(sharedDocumentsDir, appDocumentsDir).distinctBy { it.absolutePath }
    }

    private fun parseBackupFileSafely(
        file: File,
        gson: Gson,
        listType: Type
    ): List<SmsData>? {
        return try {
            JsonReader(file.bufferedReader()).use { reader ->
                reader.isLenient = true
                @Suppress("UNCHECKED_CAST")
                (gson.fromJson(reader, listType) as? List<SmsData>) ?: emptyList()
            }
        } catch (e: Exception) {
            Log.w("BekupActivity", "Skipping malformed backup file: ${file.name}", e)
            null
        }
    }

    private fun formatSize(sizeInBytes: Long): String {
        return when {
            sizeInBytes >= 1 shl 30 -> String.format("%.2f GB", sizeInBytes / (1 shl 30).toDouble())
            sizeInBytes >= 1 shl 20 -> String.format("%.2f MB", sizeInBytes / (1 shl 20).toDouble())
            sizeInBytes >= 1 shl 10 -> String.format("%.2f KB", sizeInBytes / (1 shl 10).toDouble())
            else -> "$sizeInBytes Bytes"
        }
    }

}
