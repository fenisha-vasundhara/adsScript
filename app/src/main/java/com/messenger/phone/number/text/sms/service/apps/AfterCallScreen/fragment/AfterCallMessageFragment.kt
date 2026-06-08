package com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.demo.adsmanage.Commen.beGone
import com.demo.adsmanage.Commen.beVisible
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.incomingCallNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentAfterCallMessageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AfterCallMessageFragment : Fragment() {

    lateinit var binding: FragmentAfterCallMessageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setLocal()
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_after_call_message,
            container,
            false
        )
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding)
        {
//            ansThreeTxt.setHintTextColor(Color.parseColor("#87A0B7"))
            if (requireActivity().config.activeThemeSelection == 1) {
                afterCallDirectMessageOne.setCardBackgroundColor(Color.parseColor("#F2F2F2"))
                afterCallDirectMessageTwo.setCardBackgroundColor(Color.parseColor("#F2F2F2"))
                afterCallDirectMessageThree.setCardBackgroundColor(Color.parseColor("#F2F2F2"))
                afterCallDirectMessageCustome.setCardBackgroundColor(Color.parseColor("#F2F2F2"))
            } else {
                afterCallDirectMessageOne.setCardBackgroundColor(Color.parseColor("#232323"))
                afterCallDirectMessageTwo.setCardBackgroundColor(Color.parseColor("#232323"))
                afterCallDirectMessageThree.setCardBackgroundColor(Color.parseColor("#232323"))
                afterCallDirectMessageCustome.setCardBackgroundColor(Color.parseColor("#232323"))
            }

            afterCallDirectMessageOne.setOnClickListener {
                closeKeyboard()
                checkBox1.setImageResource(R.drawable.aftercallcheckbox)
                checkBox2.setImageResource(R.drawable.aftercallring)
                checkBox3.setImageResource(R.drawable.aftercallring)
                ansOneSend.beVisible()
                ansTwoTxtSend.beGone()
                ansThreeTxtSend.beGone()
                ansCustomeTxtSend.beGone()

            }
            afterCallDirectMessageTwo.setOnClickListener {
                closeKeyboard()
                checkBox1.setImageResource(R.drawable.aftercallring)
                checkBox2.setImageResource(R.drawable.aftercallcheckbox)
                checkBox3.setImageResource(R.drawable.aftercallring)
                ansOneSend.beGone()
                ansTwoTxtSend.beVisible()
                ansThreeTxtSend.beGone()
                ansCustomeTxtSend.beGone()
            }
            afterCallDirectMessageThree.setOnClickListener {
                closeKeyboard()
                checkBox1.setImageResource(R.drawable.aftercallring)
                checkBox2.setImageResource(R.drawable.aftercallring)
                checkBox3.setImageResource(R.drawable.aftercallcheckbox)
                ansOneSend.beGone()
                ansTwoTxtSend.beGone()
                ansThreeTxtSend.beVisible()
                ansCustomeTxtSend.beGone()
            }
            afterCallDirectMessageCustome.setOnClickListener {
                checkBox1.setImageResource(R.drawable.aftercallring)
                checkBox2.setImageResource(R.drawable.aftercallring)
                checkBox3.setImageResource(R.drawable.aftercallring)
                ansOneSend.beGone()
                ansTwoTxtSend.beGone()
                ansThreeTxtSend.beGone()
                ansCustomeTxtSend.beVisible()
            }
            ansThreeEdit.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    checkBox1.setImageResource(R.drawable.aftercallring)
                    checkBox2.setImageResource(R.drawable.aftercallring)
                    checkBox3.setImageResource(R.drawable.aftercallring)
                    ansOneSend.beGone()
                    ansTwoTxtSend.beGone()
                    ansThreeTxtSend.beGone()
                    ansCustomeTxtSend.beVisible()
                }
            }
            ansThreeEdit.setOnClickListener {
                checkBox1.setImageResource(R.drawable.aftercallring)
                checkBox2.setImageResource(R.drawable.aftercallring)
                checkBox3.setImageResource(R.drawable.aftercallring)
                ansOneSend.beGone()
                ansTwoTxtSend.beGone()
                ansThreeTxtSend.beGone()
                ansCustomeTxtSend.beVisible()
            }

            ansOneSend.setOnClickListener {
                incomingCallNumber?.let { it1 -> openMessageApp(it1, "Sorry, I can't talk right now") }
            }
            ansTwoTxtSend.setOnClickListener {
                incomingCallNumber?.let { it1 -> openMessageApp(it1, "Can I call you later?") }
            }
            ansThreeTxtSend.setOnClickListener {
                incomingCallNumber?.let { it1 -> openMessageApp(it1, "I'm on my way") }
            }
            ansCustomeTxtSend.setOnClickListener {
                var txt = ansThreeEdit.text.toString()
                if (txt.trim().isEmpty()) {
                    requireActivity().toastMess("please write message")
                } else {
                    openMessageApp(incomingCallNumber!!, txt)
                }
            }
        }
    }

    private fun closeKeyboard() {
        binding.ansThreeEdit.clearFocus()
        val view = requireActivity().currentFocus
        view?.let {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun openMessageApp(phoneNumber1: String, message: String) {
        val phoneNumber = if (phoneNumber1.isBlank()) {
            "000"
        } else {
            phoneNumber1
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val uri = Uri.parse("smsto:$phoneNumber")
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    putExtra("sms_body", message)
                }
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        requireActivity().toastMess("something went wrong")
                    }
                }
            }catch (e:Exception){
                CoroutineScope(Dispatchers.Main).launch {
                    requireActivity().toastMess("something went wrong")
                }
            }
        }
    }
}