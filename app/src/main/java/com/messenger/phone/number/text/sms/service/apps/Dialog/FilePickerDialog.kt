package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import android.os.Environment
import android.os.Parcelable
import android.view.KeyEvent
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.appevents.codeless.internal.ViewHierarchy.setOnClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMaterialDialogTheme
import com.messenger.phone.number.text.sms.service.apps.CommanClass.handleAndroidSAFDialog
import com.messenger.phone.number.text.sms.service.apps.CommanClass.handleSAFDialogSdk30
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setupDialogStuff
import com.messenger.phone.number.text.sms.service.apps.Dialog.adapter.FilepickerFavoritesAdapter
import com.messenger.phone.number.text.sms.service.apps.Dialog.adapter.FilepickerItemsAdapter
import com.messenger.phone.number.text.sms.service.apps.ThredCallback.ensureBackgroundThread
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import com.simplemobiletools.commons.extensions.getDoesFilePathExist
import com.simplemobiletools.commons.extensions.getIsPathDirectory
import com.simplemobiletools.commons.extensions.getParentPath
import com.simplemobiletools.commons.extensions.internalStoragePath
import com.simplemobiletools.commons.views.Breadcrumbs
import com.simplemobiletools.commons.R
import com.simplemobiletools.commons.dialogs.StoragePickerDialog
import com.simplemobiletools.commons.extensions.areSystemAnimationsEnabled
import com.simplemobiletools.commons.extensions.baseConfig
import com.simplemobiletools.commons.extensions.beGone
import com.simplemobiletools.commons.extensions.beVisible
import com.simplemobiletools.commons.extensions.beVisibleIf
import com.simplemobiletools.commons.extensions.getAlertDialogBuilder
import com.simplemobiletools.commons.extensions.getAndroidSAFFileItems
import com.simplemobiletools.commons.extensions.getColoredDrawableWithColor
import com.simplemobiletools.commons.extensions.getContrastColor
import com.simplemobiletools.commons.extensions.getDirectChildrenCount
import com.simplemobiletools.commons.extensions.getFilenameFromPath
import com.simplemobiletools.commons.extensions.getFolderLastModifieds
import com.simplemobiletools.commons.extensions.getOTGItems
import com.simplemobiletools.commons.extensions.getProperPrimaryColor
import com.simplemobiletools.commons.extensions.getProperTextColor
import com.simplemobiletools.commons.extensions.getSomeAndroidSAFDocument
import com.simplemobiletools.commons.extensions.getSomeDocumentFile
import com.simplemobiletools.commons.extensions.getSomeDocumentSdk30
import com.simplemobiletools.commons.extensions.getTextSize
import com.simplemobiletools.commons.extensions.handleHiddenFolderPasswordProtection
import com.simplemobiletools.commons.extensions.handleLockedFolderOpening
import com.simplemobiletools.commons.extensions.isAccessibleWithSAFSdk30
import com.simplemobiletools.commons.extensions.isInDownloadDir
import com.simplemobiletools.commons.extensions.isPathOnOTG
import com.simplemobiletools.commons.extensions.isRestrictedSAFOnlyRoot
import com.simplemobiletools.commons.extensions.isRestrictedWithSAFSdk30
import com.simplemobiletools.commons.extensions.isVisible
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.models.FileDirItem
import com.simplemobiletools.commons.views.MyFloatingActionButton
import com.simplemobiletools.commons.views.MyRecyclerView
import com.simplemobiletools.commons.views.MyTextView
import java.io.File


class FilePickerDialog(
    val activity: Activity,
    var currPath: String = Environment.getExternalStorageDirectory().toString(),
    val pickFile: Boolean = true,
    var showHidden: Boolean = false,
    val showFAB: Boolean = false,
    val canAddShowHiddenButton: Boolean = false,
    val forceShowRoot: Boolean = false,
    val showFavoritesButton: Boolean = false,
    private val enforceStorageRestrictions: Boolean = true,
    val callback: (pickedPath: String) -> Unit
) : Breadcrumbs.BreadcrumbsListener {

    private var mFirstUpdate = true
    private var mPrevPath = ""
    private var mScrollStates = HashMap<String, Parcelable>()

    private var mDialog: AlertDialog? = null
    private var mDialogView = activity.layoutInflater.inflate(R.layout.dialog_filepicker, null)
    val filepicker_favorites_list = mDialogView.findViewById<MyRecyclerView>(R.id.filepicker_favorites_list)
    val filepicker_favorites_label = mDialogView.findViewById<MyTextView>(R.id.filepicker_favorites_label)
    val filepicker_fab_show_favorites = mDialogView.findViewById<MyFloatingActionButton>(R.id.filepicker_fab_show_favorites)
    val filepicker_favorites_holder = mDialogView.findViewById<RelativeLayout>(R.id.filepicker_favorites_holder)
    val filepicker_placeholder = mDialogView.findViewById<MyTextView>(R.id.filepicker_placeholder)
    val filepicker_fastscroller = mDialogView.findViewById<RecyclerViewFastScroller>(R.id.filepicker_fastscroller)
    val filepicker_fab_show_hidden = mDialogView.findViewById<MyFloatingActionButton>(R.id.filepicker_fab_show_hidden)
    val filepicker_fabs_holder = mDialogView.findViewById<LinearLayout>(R.id.filepicker_fabs_holder)
    val filepicker_breadcrumbs = mDialogView.findViewById<Breadcrumbs>(R.id.filepicker_breadcrumbs)
    val filepicker_fab = mDialogView.findViewById<MyFloatingActionButton>(R.id.filepicker_fab)
    val filepicker_files_holder = mDialogView.findViewById<RelativeLayout>(R.id.filepicker_files_holder)
    val filepicker_list = mDialogView.findViewById<MyRecyclerView>(R.id.filepicker_list)

    init {
        if (!activity.getDoesFilePathExist(currPath)) {
            currPath = activity.internalStoragePath
        }

        if (!activity.getIsPathDirectory(currPath)) {
            currPath = currPath.getParentPath()
        }

        // do not allow copying files in the recycle bin manually
        if (currPath.startsWith(activity.filesDir.absolutePath)) {
            currPath = activity.internalStoragePath
        }



        filepicker_breadcrumbs.apply {
            listener = this@FilePickerDialog
            updateFontSize(activity.getTextSize(), false)
            isShownInDialog = true
        }

        tryUpdateItems()
        setupFavorites()

        val builder = MaterialAlertDialogBuilder(activity, activity.getMaterialDialogTheme())
            .setNegativeButton(R.string.cancel, null)
            .setOnKeyListener { dialogInterface, i, keyEvent ->
                if (keyEvent.action == KeyEvent.ACTION_UP && i == KeyEvent.KEYCODE_BACK) {
                    val breadcrumbs = filepicker_breadcrumbs
                    if (breadcrumbs.getItemCount() > 1) {
                        breadcrumbs.removeBreadcrumb()
                        currPath = breadcrumbs.getLastItem().path.trimEnd('/')
                        tryUpdateItems()
                    } else {
                        mDialog?.dismiss()
                    }
                }
                true
            }

        if (!pickFile) {
            builder.setPositiveButton(R.string.ok, null)
        }



        if (showFAB) {
            filepicker_fab.apply {
                beVisible()
                setOnClickListener { createNewFolder() }
            }
        }

        val secondaryFabBottomMargin = activity.resources.getDimension(if (showFAB) R.dimen.secondary_fab_bottom_margin else R.dimen.activity_margin).toInt()



        filepicker_fabs_holder.apply {
            (layoutParams as CoordinatorLayout.LayoutParams).bottomMargin = secondaryFabBottomMargin
        }



        filepicker_placeholder.setTextColor(activity.getProperTextColor())
        filepicker_fastscroller.updateColors(activity.getProperPrimaryColor())
        filepicker_fab_show_hidden.apply {
            beVisibleIf(!showHidden && canAddShowHiddenButton)
            setOnClickListener {
                activity.handleHiddenFolderPasswordProtection {
                    beGone()
                    showHidden = true
                    tryUpdateItems()
                }
            }
        }



        filepicker_favorites_label.text = "${activity.getString(R.string.favorites)}:"
        filepicker_fab_show_favorites.apply {
            beVisibleIf(showFavoritesButton && context.baseConfig.favorites.isNotEmpty())
            setOnClickListener {
                if (filepicker_favorites_holder.isVisible()) {
                    hideFavorites()
                } else {
                    showFavorites()
                }
            }
        }

        builder.apply {
            activity.setupDialogStuff(mDialogView, this, getTitle()) { alertDialog ->
                mDialog = alertDialog
            }
        }

        if (!pickFile) {
            mDialog?.getButton(-1)?.setOnClickListener {
                verifyPath()
            }
        }
    }

    private fun getTitle() = if (pickFile) R.string.select_file else R.string.select_folder

    private fun createNewFolder() {
        CreateNewFolderDialog(activity, currPath) {
            callback(it)
            mDialog?.dismiss()
        }
    }

    private fun tryUpdateItems() {
        ensureBackgroundThread {
            getItems(currPath) {
                activity.runOnUiThread {
                    filepicker_placeholder.beGone()
                    updateItems(it as ArrayList<FileDirItem>)
                }
            }
        }
    }

    private fun updateItems(items: ArrayList<FileDirItem>) {
        if (!containsDirectory(items) && !mFirstUpdate && !pickFile && !showFAB) {
            verifyPath()
            return
        }

        val sortedItems = items.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
        val adapter = FilepickerItemsAdapter(activity, sortedItems, filepicker_list) {
            if ((it as FileDirItem).isDirectory) {
                activity.handleLockedFolderOpening(it.path) { success ->
                    if (success) {
                        currPath = it.path
                        tryUpdateItems()
                    }
                }
            } else if (pickFile) {
                currPath = it.path
                verifyPath()
            }
        }

        val layoutManager = filepicker_list.layoutManager as LinearLayoutManager
        mScrollStates[mPrevPath.trimEnd('/')] = layoutManager.onSaveInstanceState()!!

        mDialogView.apply {
            filepicker_list.adapter = adapter
            filepicker_breadcrumbs.setBreadcrumb(currPath)

            if (context.areSystemAnimationsEnabled) {
                filepicker_list.scheduleLayoutAnimation()
            }

            layoutManager.onRestoreInstanceState(mScrollStates[currPath.trimEnd('/')])
        }

        mFirstUpdate = false
        mPrevPath = currPath
    }

    private fun verifyPath() {
        when {
            activity.isRestrictedSAFOnlyRoot(currPath) -> {
                val document = activity.getSomeAndroidSAFDocument(currPath) ?: return
                sendSuccessForDocumentFile(document)
            }

            activity.isPathOnOTG(currPath) -> {
                val fileDocument = activity.getSomeDocumentFile(currPath) ?: return
                sendSuccessForDocumentFile(fileDocument)
            }

            activity.isAccessibleWithSAFSdk30(currPath) -> {
                if (enforceStorageRestrictions) {
                    activity.handleSAFDialogSdk30(currPath) {
                        if (it) {
                            val document = activity.getSomeDocumentSdk30(currPath)
                            sendSuccessForDocumentFile(document ?: return@handleSAFDialogSdk30)
                        }
                    }
                } else {
                    sendSuccessForDirectFile()
                }

            }

            activity.isRestrictedWithSAFSdk30(currPath) -> {
                if (enforceStorageRestrictions) {
                    if (activity.isInDownloadDir(currPath)) {
                        sendSuccessForDirectFile()
                    } else {
                        activity.toast(R.string.system_folder_restriction, Toast.LENGTH_LONG)
                    }
                } else {
                    sendSuccessForDirectFile()
                }
            }

            else -> {
                sendSuccessForDirectFile()
            }
        }
    }

    private fun sendSuccessForDocumentFile(document: DocumentFile) {
        if ((pickFile && document.isFile) || (!pickFile && document.isDirectory)) {
            sendSuccess()
        }
    }

    private fun sendSuccessForDirectFile() {
        val file = File(currPath)
        if ((pickFile && file.isFile) || (!pickFile && file.isDirectory)) {
            sendSuccess()
        }
    }

    private fun sendSuccess() {
        currPath = if (currPath.length == 1) {
            currPath
        } else {
            currPath.trimEnd('/')
        }

        callback(currPath)
        mDialog?.dismiss()
    }

    private fun getItems(path: String, callback: (List<FileDirItem>) -> Unit) {
        when {
            activity.isRestrictedSAFOnlyRoot(path) -> {
                activity.handleAndroidSAFDialog(path) {
                    activity.getAndroidSAFFileItems(path, showHidden) {
                        callback(it)
                    }
                }
            }

            activity.isPathOnOTG(path) -> activity.getOTGItems(path, showHidden, false, callback)
            else -> {
                val lastModifieds = activity.getFolderLastModifieds(path)
                getRegularItems(path, lastModifieds, callback)
            }
        }
    }

    private fun getRegularItems(path: String, lastModifieds: HashMap<String, Long>, callback: (List<FileDirItem>) -> Unit) {
        val items = ArrayList<FileDirItem>()
        val files = File(path).listFiles()?.filterNotNull()
        if (files == null) {
            callback(items)
            return
        }

        for (file in files) {
            if (!showHidden && file.name.startsWith('.')) {
                continue
            }

            val curPath = file.absolutePath
            val curName = curPath.getFilenameFromPath()
            val size = file.length()
            var lastModified = lastModifieds.remove(curPath)
            val isDirectory = if (lastModified != null) false else file.isDirectory
            if (lastModified == null) {
                lastModified = 0    // we don't actually need the real lastModified that badly, do not check file.lastModified()
            }

            val children = if (isDirectory) file.getDirectChildrenCount(activity, showHidden) else 0
            items.add(FileDirItem(curPath, curName, isDirectory, children, size, lastModified))
        }
        callback(items)
    }

    private fun containsDirectory(items: List<FileDirItem>) = items.any { it.isDirectory }

    private fun setupFavorites() {
        FilepickerFavoritesAdapter(activity, activity.baseConfig.favorites.toMutableList(), filepicker_favorites_list) {
            currPath = it as String
            verifyPath()
        }.apply {
            filepicker_favorites_list.adapter = this
        }
    }

    private fun showFavorites() {
        filepicker_favorites_holder.beVisible()
        filepicker_files_holder.beGone()
        val drawable = activity.resources.getColoredDrawableWithColor(R.drawable.ic_folder_vector, activity.getProperPrimaryColor().getContrastColor())
        filepicker_fab_show_favorites.setImageDrawable(drawable)
    }

    private fun hideFavorites() {
        mDialogView.apply {
            filepicker_favorites_holder.beGone()
            filepicker_files_holder.beVisible()
            val drawable = activity.resources.getColoredDrawableWithColor(R.drawable.ic_star_vector, activity.getProperPrimaryColor().getContrastColor())
            filepicker_fab_show_favorites.setImageDrawable(drawable)
        }
    }

    override fun breadcrumbClicked(id: Int) {
        if (id == 0) {
            StoragePickerDialog(activity, currPath, forceShowRoot, true) {
                currPath = it
                tryUpdateItems()
            }
        } else {
            val item = filepicker_breadcrumbs.getItem(id)
            if (currPath != item.path.trimEnd('/')) {
                currPath = item.path
                tryUpdateItems()
            }
        }
    }
}
