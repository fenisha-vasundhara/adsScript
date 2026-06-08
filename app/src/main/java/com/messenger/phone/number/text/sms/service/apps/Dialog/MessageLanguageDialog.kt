//package com.messenger.phone.number.text.sms.service.apps.Dialog
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.app.Dialog
//import android.content.ActivityNotFoundException
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Color
//import android.graphics.drawable.ColorDrawable
//import android.os.Bundle
//import android.speech.RecognizerIntent
//import android.util.DisplayMetrics
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.WindowManager
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.databinding.DataBindingUtil
//import androidx.fragment.app.DialogFragment
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProvider
//import com.google.mlkit.nl.languageid.LanguageIdentification
//import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
//import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
//import com.messenger.phone.number.text.sms.service.apps.R
//import com.messenger.phone.number.text.sms.service.apps.adapter.DialogLanguagesadapter
//import com.messenger.phone.number.text.sms.service.apps.databinding.MessageLanguageDialogItemBinding
//
//import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MessageLanguageDialogInterface
//import com.messenger.phone.number.text.sms.service.apps.modelClass.Languagemodel
//import com.messenger.phone.number.text.sms.service.apps.viewModel.MessageLanguageViewModel
//import com.simplemobiletools.commons.extensions.onTextChangeListener
//import com.simplemobiletools.commons.extensions.toast
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.util.Locale
//import javax.annotation.Nullable
//
//class MessageLanguageDialog(var snippet: String?, var position: Int) : MaterialDialogFragment() {
//
//    private var pos: Int = -1
//    private var bottomSheetDialog: Dialog? = null
//    var isfisrttime = true
//    private lateinit var model: MessageLanguageViewModel
//    lateinit var binding: MessageLanguageDialogItemBinding
//    private lateinit var messageLanguageDialogInterface: MessageLanguageDialogInterface
//    var langlist: ArrayList<Languagemodel> = arrayListOf()
//    private val RECORD_AUDIO_PERMISSION_REQUEST = 101
//    private val SPEECH_REQUEST_CODE = 102
//    private val adapterP by lazy {
//        DialogLanguagesadapter()
//    }
//
//    @SuppressLint("SetTextI18n")
//    @Nullable
//    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
//        super.onCreateView(inflater, container, savedInstanceState)
//        binding = DataBindingUtil.inflate(inflater, R.layout.message_language_dialog_item, container, false)
//        binding.adapter = adapterP
//        model = ViewModelProvider(requireActivity())[MessageLanguageViewModel::class.java]
//        model.changelang = binding.searchBarFull.context.config.userpreferencelanguageCode
//        model.refreshdata()
//        model.lanlistlive.observe(viewLifecycleOwner, Observer { data ->
//            data?.let {
//                binding.norecentfound = it.isEmpty()
//                binding.progressbar.gone()
//                synchronized(langlist) {
//                    langlist = ArrayList(it.toList().distinctBy { it.language })
//                }
//                autoscroll(langlist)
//            }
//        })
//
//        binding.searchBarFull.onTextChangeListener { searchString ->
//            filter(searchString)
//        }
//
//
//
//        adapterP.SelectedLanguageClick = { langcode, lang ->
//            model.refreshdata()
//            requireActivity().config.userpreferencelanguage = lang
//            requireActivity().config.userpreferencelanguageCode = langcode
////            messageLanguageDialogInterface.onLanguageClick(langcode, snippet.toString(), position, lang)
//            dismiss()
//        }
//
//        binding.serchCleasr.setOnClickListener {
//            checkMicrophonePermission()
//        }
//
//        return binding.root
//    }
//
//    fun autoscroll(langlist: ArrayList<Languagemodel>) {
//        GlobalScope.launch {
//            withContext(Dispatchers.IO) {
////                model.changelang = binding.searchBarFull.context.config.userpreferencelanguageCode
////                model.refreshdata()
//                Log.d("", "onCreateView: <---------> 11 ${langlist.size}")
//                for ((index, element) in langlist.withIndex()) {
//                    if (element.languagecode == binding.searchBarFull.context.config.userpreferencelanguageCode) {
//                        pos = index
//                        break
//                    }
//                }
//            }
//            withContext(Dispatchers.Main) {
//                Log.d("", "onCreateView: <---------> 1 ${pos}")
//                adapterP.listitem = langlist
//                binding.recyclerviewnested.scrollToPosition(pos)
////                adapterP.notifyDataSetChanged()
//            }
//        }
//    }
//
//    private fun checkMicrophonePermission() {
//        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.RECORD_AUDIO)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                requireActivity(),
//                arrayOf(Manifest.permission.RECORD_AUDIO),
//                RECORD_AUDIO_PERMISSION_REQUEST
//            )
//        } else {
//            openSpeechRecognition()
//        }
//    }
//
//    private fun openSpeechRecognition() {
//        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//        intent.putExtra(
//            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
//        )
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")
//        try {
//            startActivityForResult(intent, SPEECH_REQUEST_CODE)
//        } catch (e: ActivityNotFoundException) {
//            requireActivity().toast(getString(R.string.speech_recognition_not_supported_new))
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
//            val spokenText = matches?.get(0)
//            binding.searchBarFull.setText(spokenText)
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                openSpeechRecognition()
//            } else {
//                requireActivity().toast(getString(R.string.microphone_permission_denied_new))
//            }
//        }
//    }
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val dialog = super.onCreateDialog(savedInstanceState)
//        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        return dialog
//
//    }
//
//    private fun filter(text: String) {
//        try {
//            val filterdNames = ArrayList<Languagemodel>()
//            for (s in langlist) {
//                if (s.language.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))) {
//                    filterdNames.add(s)
//                } else if (s.languagereal.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))) {
//                    filterdNames.add(s)
//                }
//            }
//            binding.norecentfound = filterdNames.isEmpty()
//            adapterP.listitem = filterdNames
//        } catch (e: Exception) {
//        }
//    }
//
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        bottomSheetDialog = dialog
//        val displayMetrics = DisplayMetrics()
//        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
//        val height = displayMetrics.heightPixels
//        var width = displayMetrics.widthPixels
//        width -= width / 8
//        bottomSheetDialog!!.window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
////        bottomSheetDialog!!.window?.setDimAmount(0F)
////        bottomSheetDialog?.setCancelable(false)
//    }
//
//    fun setInterface(messageLanguageDialogInterface: MessageLanguageDialogInterface) {
//        this.messageLanguageDialogInterface = messageLanguageDialogInterface
//    }
//
//}

package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import com.google.android.material.color.MaterialColors
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getDialogBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.adapter.DialogLanguagesadapter
import com.messenger.phone.number.text.sms.service.apps.databinding.MessageLanguageDialogItemBinding

import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MessageLanguageDialogInterface
import com.messenger.phone.number.text.sms.service.apps.modelClass.Languagemodel
import com.messenger.phone.number.text.sms.service.apps.viewModel.MessageLanguageViewModel
import com.simplemobiletools.commons.extensions.onTextChangeListener
import com.simplemobiletools.commons.extensions.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.annotation.Nullable

class MessageLanguageDialog : MaterialDialogFragment() {

    private var snippet: String? = null
    private var position: Int = 0
    private var callinterface: Boolean = false
    private var pos: Int = -1
    private var bottomSheetDialog: Dialog? = null
    var isfisrttime = true
    private lateinit var model: MessageLanguageViewModel
    lateinit var binding: MessageLanguageDialogItemBinding
    private lateinit var messageLanguageDialogInterface: MessageLanguageDialogInterface
    var langlist: ArrayList<Languagemodel> = arrayListOf()
    private val RECORD_AUDIO_PERMISSION_REQUEST = 101
    private val SPEECH_REQUEST_CODE = 102
    private val adapterP by lazy {
        DialogLanguagesadapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            snippet = it.getString(ARG_SNIPPET)
            position = it.getInt(ARG_POSITION, 0)
            callinterface = it.getBoolean(ARG_CALL_INTERFACE, false)
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.message_language_dialog_item,
            container,
            false
        )
        applyDialogTheme()
        binding.adapter = adapterP
        model = ViewModelProvider(requireActivity())[MessageLanguageViewModel::class.java]
        model.changelang = binding.searchBarFull.context.config.userpreferencelanguageCode
        model.refreshdata()
        model.lanlistlive.observe(viewLifecycleOwner, Observer { data ->
            data?.let {
                binding.norecentfound = it.isEmpty()
                binding.progressbar.gone()
                synchronized(langlist) {
                    langlist = ArrayList(it.toList().distinctBy { it.language })
                }
                autoscroll(langlist)
            }
        })

        binding.searchBarFull.onTextChangeListener { searchString ->
            filter(searchString)
        }



        adapterP.SelectedLanguageClick = { langcode, lang ->
            model.refreshdata()
            requireActivity().config.userpreferencelanguage = lang
            requireActivity().config.userpreferencelanguageCode = langcode
            if (callinterface) {
                messageLanguageDialogInterface.onLanguageClick(
                    langcode,
                    snippet.toString(),
                    position,
                    lang
                )
            }
            dismiss()
        }

        binding.serchCleasr.setOnClickListener {
            checkMicrophonePermission()
        }

        return binding.root
    }

    private fun applyDialogTheme() {
        val surfaceColor = requireContext().getDialogBackgroundColor()
        val onSurface = requireContext().getProperTextColor()
        val onSurfaceVariant = requireContext().getProperSecondaryTextColor()
        val primary = requireContext().getProperPrimaryColor()
        val cornerRadius =
            resources.getDimension(com.simplemobiletools.commons.R.dimen.material_dialog_corner_radius)
        val searchCornerRadius = resources.getDimension(com.intuit.sdp.R.dimen._8sdp)
        val strokeWidth = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp)

        binding.root.background = GradientDrawable().apply {
            this.cornerRadius = cornerRadius
            setColor(surfaceColor)
        }
        binding.root.clipToOutline = true
        binding.toolBarMain.setBackgroundColor(surfaceColor)
        binding.searchMessageBg.background = GradientDrawable().apply {
            this.cornerRadius = searchCornerRadius
            setColor(MaterialColors.layer(surfaceColor, onSurface, 0.08f))
            setStroke(strokeWidth, MaterialColors.layer(surfaceColor, onSurface, 0.12f))
        }

        binding.searchBarFull.setTextColor(onSurface)
        binding.searchBarFull.setHintTextColor(onSurfaceVariant)

        binding.progressbar.indeterminateTintList = ColorStateList.valueOf(primary)
        binding.serchCleasr.setColorFilter(onSurfaceVariant)
        binding.messagebutton2.setColorFilter(onSurfaceVariant)

        applyTextColor(binding.root, onSurface)
    }

    private fun applyTextColor(view: View, color: Int) {
        when (view) {
            is androidx.recyclerview.widget.RecyclerView -> return
            is TextView -> view.setTextColor(color)
            is ViewGroup -> {
                for (i in 0 until view.childCount) {
                    applyTextColor(view.getChildAt(i), color)
                }
            }
        }
    }

    fun autoscroll(langlist: ArrayList<Languagemodel>) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
//                model.changelang = binding.searchBarFull.context.config.userpreferencelanguageCode
//                model.refreshdata()
                Log.d("", "onCreateView: <---------> 11 ${langlist.size}")
                for ((index, element) in langlist.withIndex()) {
                    if (element.languagecode == binding.searchBarFull.context.config.userpreferencelanguageCode) {
                        pos = index
                        break
                    }
                }
            }
            withContext(Dispatchers.Main) {
                Log.d("", "onCreateView: <---------> 1 ${pos}")
                adapterP.listitem = langlist
                binding.recyclerviewnested.scrollToPosition(pos)
//                adapterP.notifyDataSetChanged()
            }
        }
    }

    private fun checkMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_AUDIO_PERMISSION_REQUEST
            )
        } else {
            openSpeechRecognition()
        }
    }

    private fun openSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (e: ActivityNotFoundException) {
            requireActivity().toast(getString(R.string.speech_recognition_not_supported_new))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = matches?.get(0)
            binding.searchBarFull.setText(spokenText)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openSpeechRecognition()
            } else {
                requireActivity().toast(getString(R.string.microphone_permission_denied_new))
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog

    }

    private fun filter(text: String) {
        try {
            val filterdNames = ArrayList<Languagemodel>()
            for (s in langlist) {
                if (s.language.lowercase(Locale.getDefault())
                        .contains(text.lowercase(Locale.getDefault()))
                ) {
                    filterdNames.add(s)
                } else if (s.languagereal.lowercase(Locale.getDefault())
                        .contains(text.lowercase(Locale.getDefault()))
                ) {
                    filterdNames.add(s)
                }
            }
            binding.norecentfound = filterdNames.isEmpty()
            adapterP.listitem = filterdNames
        } catch (e: Exception) {
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bottomSheetDialog = dialog
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        var width = displayMetrics.widthPixels
        width -= width / 8
        bottomSheetDialog!!.window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
//        bottomSheetDialog!!.window?.setDimAmount(0F)
//        bottomSheetDialog?.setCancelable(false)
    }

    fun setInterface(messageLanguageDialogInterface: MessageLanguageDialogInterface) {
        this.messageLanguageDialogInterface = messageLanguageDialogInterface
    }

    companion object {
        private const val ARG_SNIPPET = "arg_snippet"
        private const val ARG_POSITION = "arg_position"
        private const val ARG_CALL_INTERFACE = "arg_call_interface"

        fun newInstance(
            snippet: String?,
            position: Int,
            callinterface: Boolean = false
        ): MessageLanguageDialog {
            return MessageLanguageDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_SNIPPET, snippet)
                    putInt(ARG_POSITION, position)
                    putBoolean(ARG_CALL_INTERFACE, callinterface)
                }
            }
        }
    }

}
