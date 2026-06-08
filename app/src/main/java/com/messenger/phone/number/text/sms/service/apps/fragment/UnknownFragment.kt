package com.messenger.phone.number.text.sms.service.apps.fragment

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.SendMessageActivity
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetAllUnknownConversationViewModel
import com.messenger.phone.number.text.sms.service.apps.adapter.MainMassageAdapter
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentUnknownBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.ItemConversationBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MessageClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MoreOPtionClick
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.StarNumber
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class UnknownFragment : Fragment(), MessageClick, MoreOPtionClick {

    lateinit var binding: FragmentUnknownBinding

    @Inject
    lateinit var adapterMainMassage: MainMassageAdapter

    private lateinit var star: StarNumber


    @Inject
    lateinit var repo: MessagerDatabaseRepo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireContext().setLocal()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_unknown, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            if (!adapterMainMassage.hasObservers()) {
                adapterMainMassage.setHasStableIds(true)
            }
            adapter = adapterMainMassage

            adapterMainMassage.isselectedAdapter = false
            adapterMainMassage.ismoreoption = true

            adapterMainMassage.setInterface(this@UnknownFragment)
            adapterMainMassage.setInterfaceMoreClick(this@UnknownFragment)

            val model = ViewModelProvider(requireActivity())[GetAllUnknownConversationViewModel::class.java]

            model.GetAllPersnalConversationlivelist.observe(requireActivity(), Observer {

                adapterMainMassage.list = it.distinctBy { it.phoneNumber } as ArrayList<Conversation>

            })

        }
    }

    override fun onClick(mobilenumber: Long?, pos: Int, title: String, phoneNumber: String, holder: MainMassageAdapter.MainMassageAdapterViewHolder, list: ArrayList<Conversation>, position: Int) {
        startActivity(Intent(requireContext(), SendMessageActivity::class.java).putExtra("mobilenumber", mobilenumber))
    }

    override fun onClickMenu(position: Int, list: ArrayList<Conversation>, holder: MainMassageAdapter.MainMassageAdapterViewHolder) {

        ShowMenu(position, list, holder.binding)

    }

    private fun ShowMenu(position: Int, list: ArrayList<Conversation>, binding: ItemConversationBinding) {

        val popupMenu = PopupMenu(requireContext(), binding.moreOPtion)
        popupMenu.menuInflater.inflate(R.menu.star_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.star -> {
                    AddtoStar(position, list)
                }
            }
            true
        }
        popupMenu.show()

    }

    private fun AddtoStar(position: Int, list: ArrayList<Conversation>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (repo.isStarNumberSelectedRepo(list[position].phoneNumber!!)) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), requireContext().resources.getString(R.string.No), Toast.LENGTH_SHORT).show()
                }
            } else {
                with(list[position]) {
                    star = StarNumber(
                        0, date!!, read, title!!, photoUri, usesCustomTitle, phoneNumber!!, snippet!!, time, type, isnumaric
                    )
                }
                repo.addNumberToStarRepo(star)
            }
        }
    }

}