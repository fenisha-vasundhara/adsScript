package com.messenger.phone.number.text.sms.service.apps

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.demo.adsmanage.Commen.log
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createCustomDrawable
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.handleDefaultSmsClick_1
import com.messenger.phone.number.text.sms.service.apps.CommanClass.selectedCatList
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.updateStatusbarColorFullApp
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.Dialog.AddCategoryDialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.CommanDeleteBlockDialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.RenameCategoryDialog
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.adapter.CatAdapter

import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityCategoryBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.CommanDeleteBlockDialogInterface
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.catAdapterLongClick
import com.messenger.phone.number.text.sms.service.apps.modelClass.Category
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetAllCatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections
import javax.inject.Inject

@AndroidEntryPoint
class CategoryActivity : AppCompatActivity(), catAdapterLongClick, CommanDeleteBlockDialogInterface {

    lateinit var binding: ActivityCategoryBinding

    @Inject
    lateinit var adapter: CatAdapter

    @Inject
    lateinit var repo: MessagerDatabaseRepo

    private var commanDeleteBlockDialog: CommanDeleteBlockDialog? = null

    var tablist: ArrayList<Category> = arrayListOf()
    private lateinit var dragHelper: ItemTouchHelper

    @Inject
    lateinit var addCategoryDialog: AddCategoryDialog

    @Inject
    lateinit var renameCategoryDialog: RenameCategoryDialog

    var selecteditemmain: ArrayList<String> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_category)
        dragHelperfun()
        selectedCatList.clear()
        with(binding) {
            catAdapter = adapter
            adapter.setInterface(this@CategoryActivity)
        }

        CoroutineScope(Dispatchers.Main).launch {

            val background = createCustomDrawable(
                cornerRadiusResId = com.intuit.sdp.R.dimen._20sdp,
                solidColorResId =
                if (config.activeThemeSelection == 1) {
                    R.color.managecatbgone
                } else if (config.activeThemeSelection == 2) {
                    R.color.toolbarcolor2
                } else if (config.activeThemeSelection == 3) {
                    R.color.toolbarcolor3new
                } else {
                    R.color.toolbarcolor2
                },
                strokeColorResId =
                R.color.appcolor,
                strokeWidthResId = com.intuit.sdp.R.dimen._1sdp
            )

            binding.addNewCat.background = background
        }

        CoroutineScope(Dispatchers.Main).launch {

            val background = createCustomDrawable(
                cornerRadiusResId = com.intuit.sdp.R.dimen._15sdp,
                solidColorResId =
                if (config.activeThemeSelection == 1) {
                    R.color.managecatallonesolid
                } else if (config.activeThemeSelection == 2) {
                    R.color.toolbarcolor2
                } else if (config.activeThemeSelection == 3) {
                    R.color.toolbarcolor3new
                } else {
                    R.color.toolbarcolor2
                },
                strokeColorResId =
                R.color.managecatallonestroke,
                strokeWidthResId = com.intuit.sdp.R.dimen._1sdp
            )

            binding.bgshow.background = background

            "dfdsfds".log()
        }

        val model = ViewModelProvider(this)[GetAllCatViewModel::class.java]
        model.livecatlist.observe(this, Observer {
            setCatdata(it)
        })

        binding.addNewCat.setOnClickListener {
            addCategoryDialog.show(supportFragmentManager, "addCategoryDialog")
        }

        binding.editmodeon.setOnClickListener {
            if (adapter.editmodeon) {
                binding.isselection = false
                selecteditemmain.clear()
                selectedCatList.clear()
                adapter.editmodeon = false
                adapter.notifyDataSetChanged()
            } else {
                adapter.editmodeon = true
                binding.isselection = true
                selecteditemmain.clear()
                selectedCatList.clear()
                ("0 " + resources.getString(R.string.selected)).also { binding.selectioncount.text = it }
                adapter.notifyDataSetChanged()
            }

        }
        binding.backBtn.setOnClickListener {
            finish()
        }


        binding.messageDelete.setOnClickListener {
            if (packageName == Telephony.Sms.getDefaultSmsPackage(this@CategoryActivity)) {
            showcommandialog(
                dialogtital = resources.getString(R.string.Delete_this_conversation),
                dialogmessage = resources.getString(R.string.This_is_permanent_cat),
                positivebutton = resources.getString(R.string.Delete),
                negativebutton = resources.getString(R.string.cancel),
                "delete"
            )}else{
                handleDefaultSmsClick_1(this@CategoryActivity)
            }
        }

        binding.messageEdit.setOnClickListener {
            try {
                renameCategoryDialog.catname = selectedCatList[0]
                renameCategoryDialog.show(supportFragmentManager, "renameCategoryDialog")
            } catch (_: Exception) {
            }
        }

        binding.messageSelectionClose.setOnClickListener {
            selectionRemove()
        }

        renameCategoryDialog.dismissdialog = {
            selectionRemove()
        }


    }

    private fun changebg(list: ArrayList<Category>) {
        val sdk = android.os.Build.VERSION.SDK_INT

        val bgColor: Int
        val bgDrawable: Drawable?
        binding.bgshow.background = null
        if (list.isEmpty()) {
            bgColor = ContextCompat.getColor(this, R.color.white)
            bgDrawable = null
            Log.d("binding", "changebg: 2")
        } else {
            bgColor = Color.TRANSPARENT  // Change this to the desired background color for non-empty list
            bgDrawable = ContextCompat.getDrawable(this, R.drawable.con_cat_bg)
            Log.d("binding", "changebg: 1")
        }

        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            binding.bgshow.setBackgroundColor(bgColor)
        } else {
            binding.bgshow.backgroundTintList = ColorStateList.valueOf(bgColor)
        }

        // Set the background drawable separately
        binding.bgshow.background = bgDrawable



        adapter?.startDrag = {
            dragHelper.startDrag(it)
        }

    }


    private fun selectionRemove() {
        binding.isselection = false
        selecteditemmain.clear()
        selectedCatList.clear()
        adapter.editmodeon = false
        binding.messageDelete.gone()
        binding.messageEdit.gone()
        adapter.notifyDataSetChanged()
    }

    override fun onLongClick(position: Int, list: ArrayList<Category>) {
        this.selecteditemmain.clear()
        if (selectedCatList.isEmpty()) {
            binding.messageDelete.gone()
            binding.messageEdit.gone()
        } else if (selectedCatList.size == 1) {
            binding.messageDelete.visible()
            binding.messageEdit.visible()
        } else {
            binding.messageDelete.visible()
            binding.messageEdit.gone()
        }
        selectedCatList.forEach {
            this.selecteditemmain.add(it)
        }
        if (selectedCatList.isNotEmpty()) {
            adapter.editmodeon = true
            binding.isselection = true
            adapter.notifyDataSetChanged()
        } else {
            binding.isselection = false
            selecteditemmain.clear()
            selectedCatList.clear()
            adapter.editmodeon = false
            adapter.notifyDataSetChanged()
        }

        Log.d("selectedCatList.size", "onLongClick: >dlsdsa <--------> ${selectedCatList.size}")
        if (selectedCatList.isEmpty()) {
            ("0 " + resources.getString(R.string.selected)).also { binding.selectioncount.text = it }
        } else {
            ("${selectedCatList.size} " + resources.getString(R.string.selected)).also { binding.selectioncount.text = it }
        }

    }

    override fun onClick(list: ArrayList<Category>, position: Int) {
        startActivity(Intent(this, CategoryContactlistActivity::class.java).putExtra("catname", list[position].catName))
    }

    private fun showcommandialog(
        dialogtital: String,
        dialogmessage: String,
        positivebutton: String,
        negativebutton: String,
        whatfordialog: String
    ) {
        commanDeleteBlockDialog = CommanDeleteBlockDialog.newInstance(
            dialogtital,
            dialogmessage,
            positivebutton,
            negativebutton,
            whatfordialog
        )
        commanDeleteBlockDialog?.setInterface(this)
        commanDeleteBlockDialog?.show(supportFragmentManager, "delete")

    }

    override fun onpositive(whatfordialog: String) {
        when (whatfordialog) {
            "delete" -> {
                commanDeleteBlockDialog?.dismiss()
                deleteCat()
            }
        }
    }

    override fun onnegative(whatfordialog: String) {
        when (whatfordialog) {
            "delete" -> {
                commanDeleteBlockDialog?.dismiss()
                selectionRemove()
            }

            else -> {
                selectionRemove()
                commanDeleteBlockDialog?.dismiss()
            }
        }
    }

    fun deleteCat() {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                repo.deleteDataByCatNameRepo(selectedCatList)
            }
            withContext(Dispatchers.Main) {
                selectionRemove()
            }
        }
    }

    private fun setCatdata(dbtablist: List<Category>?) {
        adapter.list.clear()
        tablist.clear()
        if (dbtablist != null) {
            ArrayList(dbtablist).forEachIndexed { index, category ->
                if (category.catName != "Create Category")
                    if (category.isDefaultCategory) {
                        tablist.add(category)
                    }
            }
        }
        adapter.list = tablist
    }


    private fun dragHelperfun() {
        dragHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                viewHolder.itemView.elevation = 16F
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition
                Collections.swap(tablist, from, to)
                adapter.notifyItemMoved(from, to)
                return true
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                viewHolder?.itemView?.elevation = 0F
                if (actionState == 0) {
                    CoroutineScope(Dispatchers.IO).launch {
                        repo.deleteAllCatRepo(tablist)
                        if (tablist.isNotEmpty()) {
                            repo.insertOrUpdateCategoryUsingListrepo(tablist.map {
                                Category(catName = it.catName, filterName = it.filterName, id = 0, isDefaultCategory = it.isDefaultCategory)
                            })
                        }
                    }
                }
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }

        })

        dragHelper.attachToRecyclerView(binding.catlist)

    }

    override fun onStart() {
        super.onStart()
        ThemeSetup()
    }

    private fun ThemeSetup() {
        updateStatusbarColorFullApp()
    }

}