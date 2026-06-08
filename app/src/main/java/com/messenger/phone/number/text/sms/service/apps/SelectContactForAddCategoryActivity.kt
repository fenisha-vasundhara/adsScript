package com.messenger.phone.number.text.sms.service.apps

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.demo.adsmanage.basemodule.BaseActivity
import com.messenger.phone.number.text.sms.service.apps.CommanClass.selectedContactList
import com.messenger.phone.number.text.sms.service.apps.CommanClass.selectedContactListdb
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.adapter.MainMassageAdapter
import com.messenger.phone.number.text.sms.service.apps.adapter.SelectContactAdapter
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivitySelectContactForAddCategoryBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.ContactNumberClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MessageClick
import com.messenger.phone.number.text.sms.service.apps.modelClass.CategoryNumber
import com.messenger.phone.number.text.sms.service.apps.modelClass.Contact
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetAllConversationViewModel
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetContactNumberViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject
import kotlin.math.log

@AndroidEntryPoint
class SelectContactForAddCategoryActivity : AppCompatActivity(), ContactNumberClick {
    lateinit var binding: ActivitySelectContactForAddCategoryBinding

    @Inject
    lateinit var adapterMainMassage: SelectContactAdapter

    @Inject
    lateinit var repo: MessagerDatabaseRepo

    lateinit var model: GetAllConversationViewModel

    var list = ArrayList<Contact>()
    var list2 = ArrayList<Contact>()

    var catname: String = "catname"

    var contact = ArrayList<Contact>()
//    override fun getActivityContext(): AppCompatActivity {
//        return this
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_contact_for_add_category)

        catname = intent.getStringExtra("catname").toString()

        adapterMainMassage.setHasStableIds(true)
        binding.adapter = adapterMainMassage

        adapterMainMassage.isselectedAdapter = true
//        adapterMainMassage.setContactclick(this)


        val model = ViewModelProvider(this)[GetContactNumberViewModel::class.java]

        model.ccontactlist.observe(this) {
            list2.clear()
            contact = it as ArrayList<Contact>
            chack(it)
        }

        binding.AddContacttolist.setOnClickListener {

            AddContact()

        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun chack(conversations: ArrayList<Contact>?) {


        Log.d("data", "chack:conversations: ArrayList<Contact>? <------------> ${conversations}")

//        CoroutineScope(Dispatchers.IO).launch {
//
//            datalist?.forEach { i ->
//                if (!repo.isCatNumberSelectedRepo(i.onlynumber)) {
//                    list2.add(i)
//                }
//            }
//            runOnUiThread {
//                adapterMainMassage.list = list2
//            }
//        }

//        val datalist = conversations?.let { ArrayList(it) }
//        GlobalScope.launch {
//            withContext(Dispatchers.IO) {
//                try {
//                    datalist?.iterator()?.forEach { i ->
//                        if (!repo.isCatNumberSelectedRepo(i.onlynumber)) {
//                            list2.add(i)
//                        }
//                    }
//                } catch (e: Exception) {
//                }
//            }
//            withContext(Dispatchers.Main) {
//                val list3 = ArrayList(list2.distinctBy { it.number } as ArrayList<Contact>)
//                adapterMainMassage.list = list3
//            }
//        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @OptIn(DelicateCoroutinesApi::class)
    private fun AddContact() {

        if (selectedContactListdb.isEmpty()) {
            Toast.makeText(this, getString(R.string.select_contact_NEW), Toast.LENGTH_SHORT).show()
        } else {
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    list2.forEachIndexed { index, conversation ->
                        if (selectedContactListdb.contains(conversation.number)) {
                            list.add(
                                Contact(conversation.name, conversation.number, conversation.contactId, conversation.onlynumber, catname)
                            )
                        }
                    }
                    repo.AddCatContactAppRepo(list)
                }
                withContext(Dispatchers.Main) {
                    selectedContactListdb.clear()
                    list.clear()
                    adapterMainMassage.notifyDataSetChanged()
                    finish()
                }
            }
        }

    }

    override fun onClick(mobilenumber: String, pos: Int, name: String) {

    }

    override fun OnLongClick() {

    }

//    override fun onClick(mobilenumber: String, pos: Int) {
//
//    }
}