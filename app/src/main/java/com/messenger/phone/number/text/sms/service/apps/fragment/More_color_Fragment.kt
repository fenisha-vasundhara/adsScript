package com.messenger.phone.number.text.sms.service.apps.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.demo.adsmanage.Commen.firebaseEventMain
import com.demo.adsmanage.Commen.log
import com.messenger.phone.number.text.sms.service.apps.CommanClass.SystemGeneratedIconSwitchAb
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createOptionBackground
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isfromprofile
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.ThemeColorActivity.Companion.tredid
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentMoreColorBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.ProfileColorTheme
import com.simplemobiletools.commons.extensions.getContrastColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class More_color_Fragment : Fragment() {

    lateinit var binding: FragmentMoreColorBinding

    var profilecolordata: ProfileColorTheme? = null

    var selectedcolor: Int = -1

    var treadid = -1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        requireContext().setLocal()
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_more_color_,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        treadid = tredid
        applyThemeColors()
        with(binding) {
            var initialColor = requireActivity().config.themeselectedcolor
            if (isfromprofile) {
                requireActivity().firebaseEventMain("Contact_More_Color")
                if (treadid == -1L) {
                    requireActivity().toastMess(requireActivity().resources.getString(R.string.Something_Went_Wrong))
                    requireActivity().finish()
                } else {
                    requireActivity().firebaseEventMain("More_Color")
                    profilecolordata =
                        requireActivity().config.getProfileThemeColor(treadid.toString())
                    profilecolordata?.let {
                        initialColor = it.color
                    }
                }
            }

            picker.setColor(initialColor)
            selectedcolor = initialColor
            updateApplyButton()

            picker.selectedColor.subscribe { color ->
                selectedcolor = color
                updateApplyButton()
                "selected color <-------------> ${color}".log()
            }

            binding.apply.setOnClickListener {
                if (isfromprofile) {
                    if (treadid == -1L) {
                        requireActivity().toastMess(requireActivity().resources.getString(R.string.Something_Went_Wrong))
                    } else {
                        val colorTheme = ProfileColorTheme(treadid.toString(), selectedcolor)
                        requireActivity().config.saveProfileThemeColor(colorTheme)
                    }
                } else {
                    requireActivity().config.themeselectedcolor = selectedcolor
                    requireActivity().config.ismorecolorselected = true
                }
                SystemGeneratedIconSwitchAb = true
                requireActivity().toastMess(getString(R.string.theme_applied_successfully))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        applyThemeColors()
    }

    override fun onResume() {
        super.onResume()
        applyThemeColors()
        if (selectedcolor != -1) {
            updateApplyButton()
        }
    }

    private fun applyThemeColors() {
        val surfaceColor = requireContext().getProperBackgroundColor()
        val textColor = requireContext().getProperTextColor()
        val dividerColor = requireContext().getProperSecondaryTextColor().adjustAlpha(0.25f)

        binding.root.setBackgroundColor(surfaceColor)
        binding.hexLabel.setTextColor(textColor)
        binding.hexSign.setTextColor(textColor)
        binding.hex.setTextColor(textColor)
        binding.hexSeparator.setBackgroundColor(dividerColor)
        binding.applyDivider.setBackgroundColor(dividerColor)
    }

    private fun updateApplyButton() {
        binding.apply.background = createOptionBackground(
            cornerSize = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp).toFloat(),
            fillColor = requireContext().getProperPrimaryColor(),
            rippleColor = Color.WHITE.adjustAlpha(0.3f),
            showRipple = true,
            strokeWidth = 0f,
            strokeColor = requireContext().getProperBackgroundColor(),
        )
        binding.apply.setTextColor(requireContext().getProperPrimaryColor().getContrastColor())
    }
}
