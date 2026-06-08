package com.messenger.phone.number.text.sms.service.apps.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.demo.adsmanage.Commen.firebaseEventMain
import com.demo.adsmanage.Commen.log
import com.messenger.phone.number.text.sms.service.apps.CommanClass.SystemGeneratedIconSwitchAb
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isfromprofile
import com.messenger.phone.number.text.sms.service.apps.CommanClass.materialColors
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.ThemeColorActivity.Companion.tredid
import com.messenger.phone.number.text.sms.service.apps.adapter.Color_Recyclerview_Adapter
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentMaterialcolorBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.Color_Model
import com.messenger.phone.number.text.sms.service.apps.modelClass.ProfileColorTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MaterialcolorFragment : Fragment() {

    lateinit var binding: FragmentMaterialcolorBinding

    val adapter by lazy {
        Color_Recyclerview_Adapter(requireActivity())
    }

    var treadid = -1L

    var profilecolordata: ProfileColorTheme? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        requireContext().setLocal()
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_materialcolor, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            treadid = tredid
            adapter.setHasStableIds(true)
            if (isfromprofile) {
                requireActivity().firebaseEventMain("Contact_Material_Color_Select")
                if (treadid == -1L) {
                    requireActivity().toastMess(requireActivity().resources.getString(R.string.Something_Went_Wrong))
                    requireActivity().finish()
                } else {
                    requireActivity().firebaseEventMain("Material_Color_Select")
                    profilecolordata =
                        requireActivity().config.getProfileThemeColor(treadid.toString())
                    profilecolordata?.let {
                        adapter.selectedColor = it.color
                    }
                }
            } else {
                adapter.selectedColor = requireActivity().config.themeselectedcolor
            }
            colorRecyclerView.adapter = adapter
            setAdapter()
        }

        adapter.oncolorclick = { color ->
            adapter.selectedColor = color
            if (isfromprofile) {
                if (treadid == -1L) {
                    requireActivity().toastMess(requireActivity().resources.getString(R.string.Something_Went_Wrong))
                } else {
                    val colorTheme = ProfileColorTheme(treadid.toString(), color)
                    requireActivity().config.saveProfileThemeColor(colorTheme)
                }
            } else {
                requireActivity().config.themeselectedcolor = color
                requireActivity().config.ismorecolorselected = false
            }
            SystemGeneratedIconSwitchAb = true
        }
    }

    private fun setAdapter() {
        adapter.list = requireContext().materialColors
    }

    override fun onStart() {
        super.onStart()
        try {
            adapter.notifyDataSetChanged()
        } catch (E: Exception) {
        }

    }
}