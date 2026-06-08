package com.messenger.phone.number.text.sms.service.apps.fragment

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.messenger.phone.number.text.sms.service.apps.CommanClass.SetSwitchColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createCustomDrawable
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isfromprofile
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setchatthemecolor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setchatthemecolorBg
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setchatthemecolorTintImfullapp
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setchatthemecolorTintTxt
import com.messenger.phone.number.text.sms.service.apps.CommanClass.settingABicontintset
import com.messenger.phone.number.text.sms.service.apps.CommanClass.settingABtextcolorset
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.CommanClass.updateStatusbarColorFullApp
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.ThemeColorActivity
import com.messenger.phone.number.text.sms.service.apps.data.Config
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentColorThemeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class ColorThemeFragment : Fragment() {

    lateinit var binding: FragmentColorThemeBinding
    private lateinit var config: Config

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentColorThemeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            config = requireContext().config
            config.iscolorthemescreenopen = true

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

            setChathemedefaultseletion(
                config.activeThemeSelection,
                ThemeModeManager.getThemeMode(requireContext()) == ThemeModeManager.MODE_SYSTEM
            )


            backBtn.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            DayModeButtonClick.setOnClickListener {
                ThemeModeManager.setThemeMode(requireContext(), ThemeModeManager.MODE_LIGHT)
                setChathemedefaultseletion(
                    config.activeThemeSelection,
                    ThemeModeManager.getThemeMode(requireContext()) == ThemeModeManager.MODE_SYSTEM
                )
            }

            DarkModeButtonClick.setOnClickListener {
                ThemeModeManager.setThemeMode(requireContext(), ThemeModeManager.MODE_DARK)
                setChathemedefaultseletion(
                    config.activeThemeSelection,
                    ThemeModeManager.getThemeMode(requireContext()) == ThemeModeManager.MODE_SYSTEM
                )
            }

            NightModeButtonClick.setOnClickListener {
                ThemeModeManager.setThemeMode(requireContext(), ThemeModeManager.MODE_DARK)
                setChathemedefaultseletion(
                    config.activeThemeSelection,
                    ThemeModeManager.getThemeMode(requireContext()) == ThemeModeManager.MODE_SYSTEM
                )
            }

            SystemGeneratedIconSwitchAb.isChecked =
                requireContext().config.Systemgeneratediconswitchab


            SystemGeneratedIconSwitchAb.setOnCheckedChangeListener { _, isChecked ->
//                requireActivity().toastMess("${isChecked}")
                requireContext().config.Systemgeneratediconswitchab = isChecked
                com.messenger.phone.number.text.sms.service.apps.CommanClass.SystemGeneratedIconSwitchAb =
                    true
            }

            ArcticModeButtonClick.setOnClickListener {
                ThemeModeManager.setThemeMode(requireContext(), ThemeModeManager.MODE_SYSTEM)
                setChathemedefaultseletion(
                    config.activeThemeSelection,
                    ThemeModeManager.getThemeMode(requireContext()) == ThemeModeManager.MODE_SYSTEM
                )
            }

            ThemeColorCardBtn.setOnClickListener {
                isfromprofile = false
                requireActivity().startActivity(
                    Intent(
                        requireActivity(),
                        ThemeColorActivity::class.java
                    )
                )
            }


        }
    }

    private fun setChathemedefaultseletion(chatThemeSelection: Int, chatThemeSystem: Boolean) {
        if (chatThemeSystem) {
            binding.themeSelected1.setImageResource(R.drawable.unchacked_redio_button)
            binding.themeSelected2.setImageResource(R.drawable.unchacked_redio_button)
            binding.themeSelected3.setImageResource(R.drawable.unchacked_redio_button)
            binding.themeSelected4.setImageResource(R.drawable.chacked_redio_button)
            CoroutineScope(Dispatchers.Main).launch {
                binding.DayModeButtonClick.background = changeStrokeColor(R.color.color_theme_seletion)
                binding.DarkModeButtonClick.background = changeStrokeColor(R.color.color_theme_seletion)
                binding.NightModeButtonClick.background = changeStrokeColor(R.color.color_theme_seletion)
                binding.ArcticModeButtonClick.background = changeStrokeColor(R.color.appcolor)
            }
            ThemeSetup()
            return
        }


        when (chatThemeSelection) {
            1 -> {
                binding.themeSelected1.setImageResource(R.drawable.chacked_redio_button)
                binding.themeSelected2.setImageResource(R.drawable.unchacked_redio_button)
                binding.themeSelected3.setImageResource(R.drawable.unchacked_redio_button)
                binding.themeSelected4.setImageResource(R.drawable.unchacked_redio_button)
                CoroutineScope(Dispatchers.Main).launch {
                    binding.DayModeButtonClick.background = changeStrokeColor(R.color.appcolor)
                    binding.DarkModeButtonClick.background =
                        changeStrokeColor(R.color.color_theme_seletion)
                    binding.NightModeButtonClick.background =
                        changeStrokeColor(R.color.color_theme_seletion)
                    binding.ArcticModeButtonClick.background =
                        changeStrokeColor(R.color.color_theme_seletion)
                }

            }

            2 -> {
                binding.themeSelected1.setImageResource(R.drawable.unchacked_redio_button)
                binding.themeSelected2.setImageResource(R.drawable.chacked_redio_button)
                binding.themeSelected3.setImageResource(R.drawable.unchacked_redio_button)
                binding.themeSelected4.setImageResource(R.drawable.unchacked_redio_button)
                CoroutineScope(Dispatchers.Main).launch {
                    binding.DayModeButtonClick.background =
                        changeStrokeColor(R.color.color_theme_seletion)
                    binding.DarkModeButtonClick.background = changeStrokeColor(R.color.appcolor)
                    binding.NightModeButtonClick.background =
                        changeStrokeColor(R.color.color_theme_seletion)
                    binding.ArcticModeButtonClick.background =
                        changeStrokeColor(R.color.color_theme_seletion)
                }

            }

            3 -> {
                binding.themeSelected1.setImageResource(R.drawable.unchacked_redio_button)
                binding.themeSelected2.setImageResource(R.drawable.unchacked_redio_button)
                binding.themeSelected3.setImageResource(R.drawable.chacked_redio_button)
                binding.themeSelected4.setImageResource(R.drawable.unchacked_redio_button)
                CoroutineScope(Dispatchers.Main).launch {
                    binding.DayModeButtonClick.background =
                        changeStrokeColor(R.color.color_theme_seletion)
                    binding.DarkModeButtonClick.background =
                        changeStrokeColor(R.color.color_theme_seletion)
                    binding.NightModeButtonClick.background = changeStrokeColor(R.color.appcolor)
                    binding.ArcticModeButtonClick.background =
                        changeStrokeColor(R.color.color_theme_seletion)
                }

            }

            4 -> {
                binding.themeSelected1.setImageResource(R.drawable.unchacked_redio_button)
                binding.themeSelected2.setImageResource(R.drawable.unchacked_redio_button)
                binding.themeSelected3.setImageResource(R.drawable.unchacked_redio_button)
                binding.themeSelected4.setImageResource(R.drawable.chacked_redio_button)

                CoroutineScope(Dispatchers.Main).launch {
                    binding.DayModeButtonClick.background =
                        changeStrokeColor(R.color.color_theme_seletion)
                    binding.DarkModeButtonClick.background =
                        changeStrokeColor(R.color.color_theme_seletion)
                    binding.NightModeButtonClick.background =
                        changeStrokeColor(R.color.color_theme_seletion)
                    binding.ArcticModeButtonClick.background = changeStrokeColor(R.color.appcolor)
                }

            }
        }

//        if (chatThemeSystem) {
//            binding.themeSelected1.setImageResource(R.drawable.unchacked_redio_button)
//            binding.themeSelected2.setImageResource(R.drawable.unchacked_redio_button)
//            binding.themeSelected3.setImageResource(R.drawable.unchacked_redio_button)
//            binding.themeSelected4.setImageResource(R.drawable.chacked_redio_button)
//
//            CoroutineScope(Dispatchers.Main).launch {
//                binding.DayModeButtonClick.background = changeStrokeColor(R.color.color_theme_seletion)
//                binding.DarkModeButtonClick.background = changeStrokeColor(R.color.color_theme_seletion)
//                binding.NightModeButtonClick.background = changeStrokeColor(R.color.color_theme_seletion)
//                binding.ArcticModeButtonClick.background = changeStrokeColor(R.color.appcolor)
//            }
//        } else {
//            when (chatThemeSelection) {
//                1 -> {
//                    binding.themeSelected1.setImageResource(R.drawable.chacked_redio_button)
//                    binding.themeSelected2.setImageResource(R.drawable.unchacked_redio_button)
//                    binding.themeSelected3.setImageResource(R.drawable.unchacked_redio_button)
//                    binding.themeSelected4.setImageResource(R.drawable.unchacked_redio_button)
//                    CoroutineScope(Dispatchers.Main).launch {
//                        binding.DayModeButtonClick.background = changeStrokeColor(R.color.appcolor)
//                        binding.DarkModeButtonClick.background = changeStrokeColor(R.color.color_theme_seletion)
//                        binding.NightModeButtonClick.background = changeStrokeColor(R.color.color_theme_seletion)
//                        binding.ArcticModeButtonClick.background = changeStrokeColor(R.color.color_theme_seletion)
//                    }
//
//                }
//
//                2 -> {
//                    binding.themeSelected1.setImageResource(R.drawable.unchacked_redio_button)
//                    binding.themeSelected2.setImageResource(R.drawable.chacked_redio_button)
//                    binding.themeSelected3.setImageResource(R.drawable.unchacked_redio_button)
//                    binding.themeSelected4.setImageResource(R.drawable.unchacked_redio_button)
//                    CoroutineScope(Dispatchers.Main).launch {
//                        binding.DayModeButtonClick.background = changeStrokeColor(R.color.color_theme_seletion)
//                        binding.DarkModeButtonClick.background = changeStrokeColor(R.color.appcolor)
//                        binding.NightModeButtonClick.background = changeStrokeColor(R.color.color_theme_seletion)
//                        binding.ArcticModeButtonClick.background = changeStrokeColor(R.color.color_theme_seletion)
//                    }
//
//                }
//
//                3 -> {
//                    binding.themeSelected1.setImageResource(R.drawable.unchacked_redio_button)
//                    binding.themeSelected2.setImageResource(R.drawable.unchacked_redio_button)
//                    binding.themeSelected3.setImageResource(R.drawable.chacked_redio_button)
//                    binding.themeSelected4.setImageResource(R.drawable.unchacked_redio_button)
//                    CoroutineScope(Dispatchers.Main).launch {
//                        binding.DayModeButtonClick.background = changeStrokeColor(R.color.color_theme_seletion)
//                        binding.DarkModeButtonClick.background = changeStrokeColor(R.color.color_theme_seletion)
//                        binding.NightModeButtonClick.background = changeStrokeColor(R.color.appcolor)
//                        binding.ArcticModeButtonClick.background = changeStrokeColor(R.color.color_theme_seletion)
//                    }
//
//                }
//
//                4 -> {
//                    binding.themeSelected1.setImageResource(R.drawable.unchacked_redio_button)
//                    binding.themeSelected2.setImageResource(R.drawable.unchacked_redio_button)
//                    binding.themeSelected3.setImageResource(R.drawable.unchacked_redio_button)
//                    binding.themeSelected4.setImageResource(R.drawable.chacked_redio_button)
//
//                    CoroutineScope(Dispatchers.Main).launch {
//                        binding.DayModeButtonClick.background = changeStrokeColor(R.color.color_theme_seletion)
//                        binding.DarkModeButtonClick.background = changeStrokeColor(R.color.color_theme_seletion)
//                        binding.NightModeButtonClick.background = changeStrokeColor(R.color.color_theme_seletion)
//                        binding.ArcticModeButtonClick.background = changeStrokeColor(R.color.appcolor)
//                    }
//
//                }
//            }
//        }
        ThemeSetup()
    }

    suspend fun changeStrokeColor(newStrokeColorId: Int): GradientDrawable =
        withContext(Dispatchers.IO) {
            val drawable = ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.color_theme_section_bg
            ) as GradientDrawable
            drawable.setStroke(
                requireActivity().resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._2sdp),
                ContextCompat.getColor(requireActivity(), newStrokeColorId)
            )
            return@withContext drawable
        }

    override fun onStart() {
        super.onStart()
        ThemeSetup()
    }

    private fun ThemeSetup() {
        binding.defaulttoolshow.setchatthemecolor(0)
        binding.mainBg.setchatthemecolorBg(0)
        binding.backBtn.setchatthemecolorTintImfullapp(0)
        binding.textView3.setchatthemecolorTintTxt(0)
        binding.icFlThemeColorTxt.settingABicontintset(0)
        binding.icFlSystemGeneratedIconSwitchAb.settingABicontintset(0)
        binding.textView434.settingABtextcolorset(0)
        binding.SystemGeneratedIconSwitchTextViewSwitchAb.settingABtextcolorset(0)
        binding.icFlSystemGeneratedIconSwitchAb.settingABicontintset(0)
        requireActivity().updateStatusbarColorFullApp()
        binding.SystemGeneratedIconSwitchAb.SetSwitchColor(0)
        binding.selectedColor.setCardBackgroundColor(requireActivity().config.themeselectedcolor)
        makebackgroundcontenar()
    }

    fun isDarkMode(context: Context): Boolean {
        val mode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return mode == Configuration.UI_MODE_NIGHT_YES
    }

    private fun makebackgroundcontenar() {
        CoroutineScope(Dispatchers.IO).launch {

            val background = requireContext().createCustomDrawable(
                cornerRadiusResId = com.intuit.sdp.R.dimen._10sdp,
                solidColorResId =
                if (requireContext().config.activeThemeSelection == 1) {
                    R.color.white
                } else if (requireContext().config.activeThemeSelection == 2) {
                    R.color.setting_button_color_solid2
                } else if (requireContext().config.activeThemeSelection == 3) {
                    R.color.setting_button_color_solid3
                } else {
                    R.color.white
                },
                strokeColorResId =
                if (requireContext().config.activeThemeSelection == 1) {
                    R.color.setting_button_color_boder
                } else if (requireContext().config.activeThemeSelection == 2) {
                    R.color.setting_button_color_boder1
                } else if (requireContext().config.activeThemeSelection == 3) {
                    R.color.setting_button_color_boder2
                } else {
                    R.color.toolbarcolor2
                },
                strokeWidthResId = com.intuit.sdp.R.dimen._2sdp
            )
            CoroutineScope(Dispatchers.Main).launch {
                binding.settingAbFirstContenar.background = background
            }

        }
    }

}
