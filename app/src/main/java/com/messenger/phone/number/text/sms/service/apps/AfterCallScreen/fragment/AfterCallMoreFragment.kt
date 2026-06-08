package com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.incomingCallNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.WelcomeActivity
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentAfterCallMoreBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//@AndroidEntryPoint

/*
*
* */
class AfterCallMoreFragment : Fragment() {

    lateinit var binding: FragmentAfterCallMoreBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setLocal()
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_after_call_more, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            sendMessageAfterCall.setOnClickListener {
                val phoneNumber = if (incomingCallNumber == null) {
                    "000"
                } else {
                    incomingCallNumber
                }
                openMessageApp(phoneNumber!!, "Message")
            }
            ViewMessageAfterCall.setOnClickListener {
                openDefaultMessageApp()
            }
            if (ThemeModeManager.isDarkThemeActive(requireActivity())) {

                sendMessageAfterCall.setBackgroundResource(R.drawable.after_call_bacground)
                ViewMessageAfterCall.setBackgroundResource(R.drawable.after_call_bacground)
                img1.setImageResource(R.drawable.message_send_after_dark)
                img2.setImageResource(R.drawable.message_view_after_dark)

            } else {
                sendMessageAfterCall.setBackgroundResource(R.drawable.after_call_bacground_dark)
                ViewMessageAfterCall.setBackgroundResource(R.drawable.after_call_bacground_dark)
                img1.setImageResource(R.drawable.message_send_after)
                img2.setImageResource(R.drawable.message_view_after)
            }
        }
    }

    private fun openMessageApp(phoneNumber: String, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
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
        }
    }

    private fun openDefaultMessageApp() {
        CoroutineScope(Dispatchers.IO).launch {
            startActivity(Intent(requireActivity(), WelcomeActivity::class.java))
            requireActivity().finish()
        }
    }
}
