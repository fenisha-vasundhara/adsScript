package com.messenger.phone.number.text.sms.service.apps.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.Dialog.WhatsNewDialog
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.adapter.WhatNewAdapter
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentWhatNewBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.WhatNewModel
import com.messenger.phone.number.text.sms.service.apps.viewModel.WhatNewViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class WhatNewFragment : Fragment() {


    lateinit var binding: FragmentWhatNewBinding

    var whatnewlist: ArrayList<WhatNewModel> = arrayListOf()
    var whatnewlistconver: ArrayList<WhatNewModel> = arrayListOf()

    @Inject
    lateinit var whatNewAdapter: WhatNewAdapter

    lateinit var whatNewViewModel: WhatNewViewModel

    var whatsNewDialog: WhatsNewDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireContext().setLocal()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_what_new, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        whatNewViewModel = ViewModelProvider(requireActivity())[WhatNewViewModel::class.java]
        whatNewViewModel.getwhatsnew(requireContext())

//        whatnewlist.add(WhatNewModel(requireActivity().resources.getString(R.string.whatnew2sep1), requireActivity().resources.getString(R.string.Tap_to_turn_on_Button), null, R.drawable.whatnewtwoturnon, requireActivity().resources.getString(R.string.Tuen_on)))
//        whatnewlist.add(WhatNewModel(requireActivity().resources.getString(R.string.whatnew2), requireActivity().resources.getString(R.string.Create6Digit), null, R.drawable.whatnewtwoconpin, requireActivity().resources.getString(R.string.Confirm_PIN)))
//        whatnewlist.add(WhatNewModel(requireActivity().resources.getString(R.string.whatnew3), requireActivity().resources.getString(R.string.AddedSecurity), null, R.drawable.whatnewtwosecque, requireActivity().resources.getString(R.string.Security_Question)))
        whatnewlist.add(WhatNewModel(requireActivity().resources.getString(R.string.whatnew4), null, null, null, requireActivity().resources.getString(R.string.Turned_on)))

        whatnewlistconver.add(WhatNewModel(requireActivity().resources.getString(R.string.Translate_Conversation_new),
            requireActivity().resources.getString(R.string.Translate_Conversation_new),
            requireActivity().resources.getString(R.string.This_messenger_app_enables_users_to_engage)
            , R.drawable.traslatefirstimg,
            requireActivity().resources.getString(R.string.Translate_Conversation_new)
        ))
        whatnewlistconver.add(WhatNewModel(requireActivity().resources.getString(R.string.Translate_Conversation_new),
            requireActivity().resources.getString(R.string.Create6Digit),
            requireActivity().resources.getString(R.string.Should_the_user_wish_to_translate),
            R.drawable.traslatetwoimg,
            requireActivity().resources.getString(R.string.Translate_Conversation_new)))

        with(binding) {
            whatadapter = whatNewAdapter
            whatNewViewModel.whatsnewlivelist.observe(requireActivity()) {
                whatNewAdapter.whatnewlist = ArrayList(it)
            }
            backBtn.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
//            whatNewAdapter.whatistemclick = {
//                when (it) {
//                    0 -> {
//                        setupwhatnewdialog(
//                            "1. ${requireActivity().resources.getString(R.string.Privacy_chat)}",
//                            null,
//                            R.drawable.what_new_private_chat,
//                            requireActivity().resources.getString(R.string.whatnewprivatechat_srctital),
//                            requireActivity().resources.getString(R.string.whatnewprivatechat_last), 0,
//                            arrayListOf()
//                        )
//                    }
//
//                    1 -> {
//                        setupwhatnewdialog(
//                            "2. ${requireActivity().resources.getString(R.string.Privacy_chat)}",
//                            requireActivity().resources.getString(R.string.TwoStepverification),
//                            R.drawable.whatnewtwoturnedon,
//                            requireActivity().resources.getString(R.string.Two_factor_authentication),
//                            null,
//                            1,
//                            whatnewlist
//                        )
//                    }
//
//                    2 -> {
//                        setupwhatnewdialog(
//                            "3. ${requireActivity().resources.getString(R.string.Conversation_Setting)}",
//                            requireActivity().resources.getString(R.string.conversation_titalbottomtxt),
//                            R.drawable.what_new_con_chat,
//                            requireActivity().resources.getString(R.string.conversation_srcbottomtxt),
//                            null, 2, arrayListOf()
//                        )
//                    }
//
//                    3 -> {
//                        setupwhatnewdialog(
//                            "4. ${requireActivity().resources.getString(R.string.Message_Corner)}",
//                            requireActivity().resources.getString(R.string.messagecorner_titalbottomtxt),
//                            R.drawable.what_new_mess_corner_chat,
//                            requireActivity().resources.getString(R.string.messagecorner_srcbottomtxt),
//                            null, 3, arrayListOf()
//                        )
//                    }
//
//                    4 -> {
//                        setupwhatnewdialog(
//                            "5. ${requireActivity().resources.getString(R.string.color_theme)}",
//                            requireActivity().resources.getString(R.string.colortheme_titalbottomtxt),
//                            R.drawable.what_new_color_theme,
//                            requireActivity().resources.getString(R.string.colortheme_srcbottomtxt),
//                            null, 4, arrayListOf()
//                        )
//                    }
//
//                    5 -> {
//                        setupwhatnewdialog(
//                            "6. ${requireActivity().resources.getString(R.string.Auto_Reply)}",
//                            null,
//                            R.drawable.what_new_auto_reply,
//                            requireActivity().resources.getString(R.string.autoreply_srcbottomtxt),
//                            null, 5, arrayListOf()
//                        )
//                    }
//
//                    6 -> {
//                        setupwhatnewdialog(
//                            "7. ${requireActivity().resources.getString(R.string.Driving_Mode)}",
//                            requireActivity().resources.getString(R.string.drivingmode_titalbottomtxt),
//                            R.drawable.what_new_driving_mode,
//                            requireActivity().resources.getString(R.string.drivingmode_srcbottomtxt),
//                            null, 6, arrayListOf()
//                        )
//                    }
//
//                    7 -> {
//                        setupwhatnewdialog(
//                            "7. ${requireActivity().resources.getString(R.string.Translate_Conversation)}",
//                            requireActivity().resources.getString(R.string.drivingmode_titalbottomtxt),
//                            R.drawable.traslatetwoimg,
//                            requireActivity().resources.getString(R.string.drivingmode_srcbottomtxt),
//                            null, 7, whatnewlistconver
//                        )
//                    }
//
//                }
//            }
        }
    }

    private fun setupwhatnewdialog(
        tital: String?, titalbottomtxt: String?, src: Int?, srcbottomtxt: String?, lasttxt1: String?, pos: Int, arrayListOf: ArrayList<WhatNewModel>
    ) {
        whatsNewDialog = WhatsNewDialog(tital, titalbottomtxt, src, srcbottomtxt, lasttxt1, pos, arrayListOf)
        whatsNewDialog?.show(requireActivity().supportFragmentManager, "whatsNewDialog")
    }
}