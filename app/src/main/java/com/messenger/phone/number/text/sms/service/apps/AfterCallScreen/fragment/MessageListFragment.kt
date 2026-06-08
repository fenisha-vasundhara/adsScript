package com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.fragment

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.blockcontectremove
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.SendMessageActivity
import com.messenger.phone.number.text.sms.service.apps.WelcomeActivity
import com.messenger.phone.number.text.sms.service.apps.adapter.MainMassageAdapter
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentMessageListBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MainMessageClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MessageClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MoreOPtionClick
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetAllConversationViewModel
import dagger.Lazy
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


class MessageListFragment : Fragment(),
    MessageClick,
    MoreOPtionClick
{

    private var _binding: FragmentMessageListBinding? = null
    private val binding get() = _binding!!


    private val adapter by lazy { MainMassageAdapter() }
    private lateinit var viewModel: GetAllConversationViewModel
    private var categoryName: String? = null
    private var filterMode: String = "defaultBtn"

    companion object {
        private const val MAX_VISIBLE_ITEMS = 5
        private const val ARG_CATEGORY = "category_name"
        private const val ARG_FILTER = "filter_mode"

        fun newInstance(
            category: String? = null,
            filter: String = "defaultBtn"
        ): MessageListFragment = MessageListFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_CATEGORY, category)
                putString(ARG_FILTER, filter)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryName = it.getString(ARG_CATEGORY)
            filterMode = it.getString(ARG_FILTER) ?: "defaultBtn"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageListBinding.inflate(inflater, container, false)
        return binding.root
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface FragmentEntryPoint {
        fun messagerDatabaseRepo(): MessagerDatabaseRepo
        fun realm(): io.github.xilinjia.krdb.Realm
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {


            setupRecyclerView()

            val entryPoint = EntryPointAccessors.fromApplication(
                requireContext().applicationContext,
                FragmentEntryPoint::class.java
            )

            val repo = entryPoint.messagerDatabaseRepo()
            val realm = entryPoint.realm()

            val factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return GetAllConversationViewModel(repo, requireContext(), Lazy { realm }) as T
                }
            }



            viewModel = ViewModelProvider(
                this@MessageListFragment,
                factory
            )[GetAllConversationViewModel::class.java]

            observeData()
            setupClickListeners()
            setupShowMoreClick()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerMessages.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MessageListFragment.adapter
            setHasFixedSize(false)
            isNestedScrollingEnabled = false       }

        adapter.apply {
            isselectedAdapter = false
            ismoreoption = true
            showpinnedconversation = true

            newmessagecountshow = false


            setInterface(this@MessageListFragment)
            setInterfaceMoreClick(this@MessageListFragment)
        }


    }


    private fun setupShowMoreClick() {
        binding.tvShowMore.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                startActivity(Intent(requireActivity(), WelcomeActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    private fun observeData() {
        viewModel.GetAllConversationlivelistAlldata.observe(viewLifecycleOwner) { allConversations ->
            processConversations(allConversations)
        }
    }

    private fun processConversations(allConversations: List<Conversation>) {
        val filtered = when (categoryName) {
            null, "All Messages" -> allConversations
            else -> allConversations.filter { it.CategoryName == categoryName }
        }

        val sortedList = filtered
            .distinctBy { it.threadId }
            .sortedWith(
                compareByDescending<Conversation> {
                    requireContext().config.pinnedConversations.contains(it.threadId.toString())
                }.thenByDescending { it.date }
            )

        val totalItems = sortedList.size
        val displayList = if (totalItems > MAX_VISIBLE_ITEMS) {
            ArrayList(sortedList.take(MAX_VISIBLE_ITEMS))   // Show only first 5
        } else {
            ArrayList(sortedList)
        }

        adapter.updateList(displayList)

        // Show/Hide "Show More" TextView
        binding.tvShowMore.visibility = if (totalItems > MAX_VISIBLE_ITEMS) View.VISIBLE else View.GONE

        // Empty state handling
        val isEmpty = displayList.isEmpty()
        binding.layoutEmpty?.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.recyclerMessages.visibility = if (isEmpty) View.GONE else View.VISIBLE


    }


    private fun setupClickListeners() {

    }

    override fun onClick(
        tredid: Long?,
        pos: Int,
        title: String,
        phoneNumber: String,
        holder: MainMassageAdapter.MainMassageAdapterViewHolder,
        list: ArrayList<Conversation>,
        position: Int
    ) {
        val intent = Intent(requireContext(), SendMessageActivity::class.java).apply {
            putExtra("tredid", tredid)
            putExtra("name", title)
            putExtra("mobileNumber", phoneNumber)
            putExtra("isgroupmessage", list[position].isgroupmessage)
            putExtra("open_from_after_call_list", true)
        }
        startActivity(intent)
        requireActivity().finish()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClickMenu(
        position: Int,
        list: ArrayList<Conversation>,
        holder: MainMassageAdapter.MainMassageAdapterViewHolder
    ) {


    }
}
