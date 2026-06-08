package com.messenger.phone.number.text.sms.service.apps.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.shareApp
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentInviteaFriendBinding

class InviteaFriendFragment : Fragment() {

    lateinit var binding: FragmentInviteaFriendBinding

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireContext().setLocal()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invitea_friend, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            fontSize10 = requireActivity().getTextSizeForeNormal10MS()
            fontSize13 = requireActivity().getTextSizeForeNormal13MS()
            fontSize18 = requireActivity().getTextSizeForeNormal18MS()
            fontSize8 = requireActivity().getTextSizeForeNormal8MS()
            fontSize15 = requireActivity().getTextSizeHometitleMS()

            binding.textsizechagefor10 = fontSize10
            binding.textsizechagefor13 = fontSize13
            binding.textsizechagefor18 = fontSize18
            binding.textsizechagefor8 = fontSize8
            binding.textsizechagefor15 = fontSize15

            shareAppLint.text = "https://play.google.com/store/apps/details?id=" + context?.packageName
            shareAppLint.maxEms = 28
            inviteUserBtnCard.setOnClickListener {
                shareApp(requireContext())
            }

            backBtn.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

}