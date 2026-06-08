package com.messenger.phone.number.text.sms.service.apps.fragment

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.content.Intent
import android.database.DatabaseUtils
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.SendMessageActivity
import com.messenger.phone.number.text.sms.service.apps.adapter.CategoryContactlistAdapter
import com.messenger.phone.number.text.sms.service.apps.adapter.SelectContactAdapter
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentCategoryBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.CategoryContactlistAdapterMessageClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.ContactNumberClick
import com.messenger.phone.number.text.sms.service.apps.modelClass.CategoryNumber
import com.messenger.phone.number.text.sms.service.apps.modelClass.Contact
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetCatMobilenumberRepoViewModel
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetCatMobilenumberRepoViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CategoryFragment : Fragment(), ContactNumberClick {

    lateinit var binding: FragmentCategoryBinding

    var catname: String = "No"

    @Inject
    lateinit var adapter: SelectContactAdapter

    @Inject
    lateinit var repo: MessagerDatabaseRepo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireContext().setLocal()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val b = arguments
        if (b != null) {
            catname = b.getString("CatName").toString()
        }
        binding.adapterCategory = adapter
//        adapter.setContactclick(this)
//
//        val model = ViewModelProvider(this, GetCatMobilenumberRepoViewModelFactory(repo, catname))[GetCatMobilenumberRepoViewModel::class.java]
//
//        model.livelist.observe(requireActivity(), Observer {
//            adapter.list = it as ArrayList<Contact>
//        })

    }

    override fun onClick(mobilenumber: String, pos: Int, name: String) {
        startActivity(Intent(requireContext(), SendMessageActivity::class.java).putExtra("mobilenumber", mobilenumber))
    }

    override fun OnLongClick() {

    }

}