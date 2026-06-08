package com.messenger.phone.number.text.sms.service.apps

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.demo.adsmanage.basemodule.BaseActivity
import com.messenger.phone.number.text.sms.service.apps.CommanClass.categoryContactlistAdapterList
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createCustomDrawable
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.selectedCatList
import com.messenger.phone.number.text.sms.service.apps.CommanClass.selectedContactListdb
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.updateStatusbarColorFullApp
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.adapter.CategoryContactlistAdapter
import com.messenger.phone.number.text.sms.service.apps.adapter.MainMassageAdapter
import com.messenger.phone.number.text.sms.service.apps.adapter.SelectContactAdapter
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityCategoryContactlistBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.CategoryContactlistAdapterMessageClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.ContactNumberClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MainMessageClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MessageClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MoreOPtionClick
import com.messenger.phone.number.text.sms.service.apps.modelClass.CategoryNumber
import com.messenger.phone.number.text.sms.service.apps.modelClass.Contact
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetAllConversationArchivedViewModel
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetAllConversationViewModel
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetCatMobilenumberRepoViewModel
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetCatMobilenumberRepoViewModelFactory
import com.simplemobiletools.commons.extensions.onTextChangeListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class CategoryContactlistActivity : AppCompatActivity(), MessageClick, MainMessageClick, MoreOPtionClick {

    lateinit var binding: ActivityCategoryContactlistBinding

    @Inject
    lateinit var repo: MessagerDatabaseRepo

    @Inject
    lateinit var adapterMainMassage: MainMassageAdapter

    private var convolist2: ArrayList<Conversation> = arrayListOf()

    var selecteditemmain: ArrayList<String> = arrayListOf()

    private lateinit var mProgressDialog: ProgressDialog

    var catname: String = "catname"

    lateinit var model: GetAllConversationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_category_contactlist)
        binding.SearchContact.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        catname = intent.getStringExtra("catname").toString()
        binding.conversationsFastscroller.updateColors(resources.getColor(R.color.procolor, theme))
        binding.adapter = adapterMainMassage
        adapterMainMassage.catselection = true
        adapterMainMassage.setHasStableIds(true)
        adapterMainMassage.isselectedAdapter = false
        adapterMainMassage.ismoreoption = true
        adapterMainMassage.setInterface(this@CategoryContactlistActivity)
        adapterMainMassage.setInterfaceMoreClick(this@CategoryContactlistActivity)
        adapterMainMassage.setInterfaceMainClick(this@CategoryContactlistActivity)

        mProgressDialog = ProgressDialog(this, R.style.Dialog_Custom)
        mProgressDialog.setCancelable(false)
        mProgressDialog.setMessage("Loading...")


        CoroutineScope(Dispatchers.Main).launch {
            val background = createCustomDrawable(
                cornerRadiusResId = com.intuit.sdp.R.dimen._30sdp,
                solidColorResId =
                if (config.activeThemeSelection == 1) {
                    R.color.langsechbg
                } else if (config.activeThemeSelection == 2) {
                    R.color.toolbarcolor2
                } else if (config.activeThemeSelection == 3) {
                    R.color.toolbarcolor3new
                } else {
                    R.color.toolbarcolor2
                },
                strokeColorResId =
                if (config.activeThemeSelection == 1) {
                    R.color.langsechbg
                } else if (config.activeThemeSelection == 2) {
                    R.color.toolbarcolor2
                } else if (config.activeThemeSelection == 3) {
                    R.color.toolbarcolor3new
                } else {
                    R.color.toolbarcolor2
                },
                strokeWidthResId = com.intuit.sdp.R.dimen._1sdp
            )
            binding.searchBarCon.background = background
        }

        if (config.activeThemeSelection == 1) {
            binding.SearchContact.setTextColor(resources.getColor(R.color.black2))
        } else {
            binding.SearchContact.setTextColor(resources.getColor(R.color.white))
        }


        model = ViewModelProvider(this)[GetAllConversationViewModel::class.java]
        model.GetAllConversationlivelist.observe(this, Observer {
            setupconversation(it)
        })

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.messageAdd.setOnClickListener {
            addtocatContact()
        }

        binding.serchCleasr.setOnClickListener {
            binding.SearchContact.text.clear()
        }

        binding.SearchContact.onTextChangeListener { searchString ->
            filter(searchString)
        }

    }

    private fun filter(editable: String) {
        if (editable.isEmpty()) {
            binding.serchCleasr.gone()
        } else {
            binding.serchCleasr.visible()
        }

        try {
            val filterdNames = ArrayList<Conversation>()
            for (s in convolist2) {
                if (s.title.lowercase(Locale.getDefault()).contains(editable.lowercase(Locale.getDefault()))) {
                    filterdNames.add(s)
                } else if (s.phoneNumber.lowercase(Locale.getDefault()).contains(editable.lowercase(Locale.getDefault()))) {
                    filterdNames.add(s)
                }
            }
            adapterMainMassage.updateList(filterdNames)
        } catch (e: Exception) {
        }
    }

    private fun selectionRemove() {
        binding.isselection = false
        selecteditemmain.clear()
        adapterMainMassage.selecteditem.clear()
        adapterMainMassage.notifyDataSetChanged()
    }

    private fun addtocatContact() {
        if (selecteditemmain.isEmpty()) {
            selectionRemove()
        } else {
            var datadeletelist = ArrayList<String>()
            CoroutineScope(Dispatchers.IO).launch {
                repo.updatemessageCatRepo(catname, selecteditemmain)
                withContext(Dispatchers.Main) {
                    selectionRemove()
                }
                val tablist = repo.getallTabOnlyListrepo()
                tablist.forEachIndexed { index, category ->
                    if (!category.isDefaultCategory) {
                        Log.d("", "addtocatContact: <--------> 1 ${category.catName}")
                        if (!repo.isCatDataAvailableRepo(category.catName)) {
                            Log.d("", "addtocatContact: <--------> 2 ${category.catName}")
                            repo.deleteDataByCatNameRepo(arrayListOf(category.catName))
                        }
                    }
                }
                finish()
            }
        }
    }

    private fun setupconversation(it: List<Conversation>?) {
        try {
            convolist2.clear()
            convolist2 = ArrayList(it?.distinctBy { it.threadId } as ArrayList<Conversation>)
            adapterMainMassage.updateList(convolist2)
            binding.nomessagefoundchack = it.isEmpty()
        } catch (_: Exception) {
        }
    }

    override fun onMainClick(position: Int, list: ArrayList<Conversation>, selecteditem: ArrayList<Conversation>) {
        this.selecteditemmain.clear()
        if (selecteditem.isEmpty()) {
            binding.messageAdd.gone()
        } else {
            binding.messageAdd.visible()
        }
        selecteditem.forEach {
            this.selecteditemmain.add(it.threadId.toString())
        }
        binding.isselection = selecteditem.isNotEmpty()
        ("${selecteditem.size} " + resources.getString(R.string.selected)).also { binding.selectioncount.text = it }
    }

    override fun onClick(mobilenumber: Long?, pos: Int, title: String, phoneNumber: String, holder: MainMassageAdapter.MainMassageAdapterViewHolder, list: ArrayList<Conversation>, position: Int) {

    }

    override fun onClickMenu(position: Int, list: ArrayList<Conversation>, holder: MainMassageAdapter.MainMassageAdapterViewHolder) {

    }


    override fun onDestroy() {
        super.onDestroy()
        CoroutineScope(Dispatchers.IO).launch {
            if (!repo.isCatDataAvailableRepo(catname)) {
                repo.deleteDataByCatNameRepo(arrayListOf(catname))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        ThemeSetup()
    }

    private fun ThemeSetup() {
        updateStatusbarColorFullApp()
    }
}