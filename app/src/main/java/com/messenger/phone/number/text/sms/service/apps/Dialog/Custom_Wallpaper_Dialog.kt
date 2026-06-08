package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.content.res.ColorStateList
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.messenger.phone.number.text.sms.service.apps.CommanClass.backgroundcolorcustomwallpaperold
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.inmessagecolorcustomwallpaperold
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isresetbuttonclickcon
import com.messenger.phone.number.text.sms.service.apps.CommanClass.istemereselect
import com.messenger.phone.number.text.sms.service.apps.CommanClass.outmessagecolorcustomwallpaperold
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.shareprefselectedoldold
import com.messenger.phone.number.text.sms.service.apps.CommanClass.smartreplycolorold
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toolbarcolorcustomwallpaperold
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.adapter.Customwallpaperadapter
import com.messenger.phone.number.text.sms.service.apps.databinding.CustomWallpaperDialogItemBinding
import com.google.android.material.color.MaterialColors

import com.messenger.phone.number.text.sms.service.apps.modelClass.Customwallpaperadaptermodel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.annotation.Nullable

class Custom_Wallpaper_Dialog : MaterialDialogFragment() {

    private var bottomSheetDialog: Dialog? = null

    lateinit var binding: CustomWallpaperDialogItemBinding

    var customwallpaperlist: ArrayList<Customwallpaperadaptermodel> = arrayListOf()

    val customwallpaperadapter: Customwallpaperadapter by lazy {
        Customwallpaperadapter()
    }

    var livewallpaperchangepreview: ((String, String, String, String, String, Boolean, Boolean) -> Unit)? = null

    var isdonebuttonclick = false
    var isresetbuttonclick = false
    var isdonebuttonclickwithoutresete = false

    var backgroundcolorcustomwallpaper: String = ""
    var toolbarcolorcustomwallpaper: String = ""
    var inmessagecolorcustomwallpaper: String = ""
    var outmessagecolorcustomwallpaper: String = ""
    var smartreplycolor: String = ""
    var shareprefselected: Int = 2
    var shareprefselectedold: Int = 2

    var customwallpaperadaptermodelthis = Customwallpaperadaptermodel()


    @OptIn(DelicateCoroutinesApi::class)
    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        requireActivity().setLocal()
        binding = DataBindingUtil.inflate(inflater, R.layout.custom_wallpaper_dialog_item, container, false)

        isdonebuttonclick = false
        isresetbuttonclick = false
        isresetbuttonclickcon = false
        isdonebuttonclickwithoutresete = false

        if (requireActivity().config.Message_Send_Activity == "1") {
            backgroundcolorcustomwallpaper = requireActivity().config.backgroundcolorcustomwallpaper.toString()
        } else {
            backgroundcolorcustomwallpaper = requireActivity().config.backgroundcolorcustomwallpaperab.toString()

        }


        toolbarcolorcustomwallpaper = requireActivity().config.toolbarcolorcustomwallpaper.toString()

        if (requireActivity().config.Message_Send_Activity == "1") {
            inmessagecolorcustomwallpaper = requireActivity().config.inmessagecolorcustomwallpaper.toString()
            outmessagecolorcustomwallpaper = requireActivity().config.outmessagecolorcustomwallpaper.toString()
        } else {
            inmessagecolorcustomwallpaper = requireActivity().config.inmessagecolorcustomwallpaperAB.toString()
            outmessagecolorcustomwallpaper = requireActivity().config.outmessagecolorcustomwallpaperAB.toString()
        }


        smartreplycolor = requireActivity().config.smartreplycolor.toString()
        shareprefselectedoldold = requireActivity().config.customwallpaperselected

        if (requireActivity().config.Message_Send_Activity == "1") {
            backgroundcolorcustomwallpaperold = requireActivity().config.backgroundcolorcustomwallpaper.toString()
        } else {
            backgroundcolorcustomwallpaperold = requireActivity().config.backgroundcolorcustomwallpaperab.toString()

        }


        toolbarcolorcustomwallpaperold = requireActivity().config.toolbarcolorcustomwallpaper.toString()

        if (requireActivity().config.Message_Send_Activity == "1") {
            inmessagecolorcustomwallpaperold = requireActivity().config.inmessagecolorcustomwallpaper.toString()
            outmessagecolorcustomwallpaperold = requireActivity().config.outmessagecolorcustomwallpaper.toString()
        } else {
            inmessagecolorcustomwallpaperold = requireActivity().config.inmessagecolorcustomwallpaperAB.toString()
            outmessagecolorcustomwallpaperold = requireActivity().config.outmessagecolorcustomwallpaperAB.toString()
        }


        smartreplycolorold = requireActivity().config.smartreplycolor.toString()

        shareprefselectedold = requireActivity().config.customwallpaperselected

        if (!customwallpaperadapter.hasObservers()) {
            customwallpaperadapter.setHasStableIds(true)
        }
        binding.adapter = customwallpaperadapter
        GlobalScope.launch {
            createcustomadapterwallpaperlist(true)
        }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setGravity(Gravity.BOTTOM)
        return dialog

    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val colors = requireContext().resolveDialogColors()
        with(binding) {
            customwallpaperadapter.customwallpaperclick = { customwallpaperadaptermodel ->
                istemereselect = true
                shareprefselected = customwallpaperadaptermodel.shareprefselected!!
                GlobalScope.launch {
                    createcustomadapterwallpaperlist(false)
                }
                customwallpaperadaptermodelthis = customwallpaperadaptermodel
                livewallpaperchangepreview?.invoke(
                    customwallpaperadaptermodel.backgroundcolor!!,
                    customwallpaperadaptermodel.toolbarcolor!!,
                    customwallpaperadaptermodel.inmessagebackground!!,
                    customwallpaperadaptermodel.outmessagebackground!!,
                    customwallpaperadaptermodel.startreplaycolor!!,
                    isdonebuttonclick,
                    isdonebuttonclickwithoutresete
                )
            }
            binding.doneBtn.setOnClickListener {
                isdonebuttonclick = true
                isdonebuttonclickwithoutresete = true
                backgroundcolorcustomwallpaper = "#FFFFFF"
                toolbarcolorcustomwallpaper = "#FFFFFF"
                if (requireActivity().config.Message_Send_Activity == "1") {
                    inmessagecolorcustomwallpaper = "#4AA6FB"
                    outmessagecolorcustomwallpaper = "#E8F4FF"
                } else {
                    inmessagecolorcustomwallpaper = "#C2E6C3"
                    outmessagecolorcustomwallpaper = "#FFFFFFF"
                }
                smartreplycolor = "#E5EAEF"
                dismiss()
            }




            binding.resetBtn.setOnClickListener {
                shareprefselected = 2
                isresetbuttonclick = true
                isresetbuttonclickcon = true
                requireContext().config.iscustomgalleryimageset = false
                backgroundcolorcustomwallpaper = if (requireActivity().config.Message_Send_Activity == "1") {
                    "#FFFFFFF"
                } else {
                    "#FFFFFFF"
                }
                toolbarcolorcustomwallpaper = "#FFFFFF"

                if (requireActivity().config.Message_Send_Activity == "1") {
                    inmessagecolorcustomwallpaper = "#4AA6FB"
                    outmessagecolorcustomwallpaper = "#E8F4FF"
                } else {
                    inmessagecolorcustomwallpaper = "#EBEFF2"
                    outmessagecolorcustomwallpaper = "#F2F2F2"
                }


                smartreplycolor = "#E5EAEF"
                isdonebuttonclick = true
                dismiss()
            }
            val cardRadius = resources.getDimension(com.intuit.sdp.R.dimen._15sdp)
            cardMain.background = GradientDrawable().apply {
                cornerRadius = cardRadius
                setColor(colors.surface)
            }
            linearLayout5.setBackgroundColor(colors.surface)
            resetBtn.setTextColor(colors.primary)
            resetBtn.backgroundTintList = ColorStateList.valueOf(colors.optionFill)
            resetBtn.strokeColor = ColorStateList.valueOf(colors.outlineVariant)
            resetBtn.strokeWidth = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp)
            resetBtn.cornerRadius = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._7sdp)
            doneBtn.backgroundTintList = ColorStateList.valueOf(colors.primary)
            doneBtn.setTextColor(
                MaterialColors.getColor(
                    root,
                    com.google.android.material.R.attr.colorOnPrimary,
                    android.graphics.Color.WHITE
                )
            )
            val rippleColor = ColorStateList.valueOf(
                MaterialColors.layer(colors.surface, colors.onSurface, 0.16f)
            )
            resetBtn.rippleColor = rippleColor
            doneBtn.rippleColor = rippleColor
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
//        bottomSheetDialog?.window?.setDimAmount(0F)
        bottomSheetDialog?.window?.setDimAmount(0.2F)
        bottomSheetDialog!!.window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    suspend fun createcustomadapterwallpaperlist(autoacroll: Boolean) {
        delay(10)
        withContext(Dispatchers.IO) {
            customwallpaperlist.clear()

            customwallpaperlist.add(
                Customwallpaperadaptermodel(
                    name = requireContext().resources.getString(R.string.SelectFromGallery_customwallpaper),
                    img = requireContext().resources.getDrawable(R.drawable.customwallpaper1),
                    isselected = false,
                    shareprefselected = 1,
                    toolbarcolor = "#FFFFFF",
                    backgroundcolor = if (requireActivity().config.Message_Send_Activity == "1") {
                        "#FFFFFFF"
                    } else {
                        "#FFFFFFF"
                    },
                    inmessagebackground = if (requireActivity().config.Message_Send_Activity == "1") {
                        "#4AA6FB"
                    } else {
                        "#EBEFF2"
                    },
                    outmessagebackground = if (requireActivity().config.Message_Send_Activity == "1") {
                        "#E8F4FF"
                    } else {
                        "#F2F2F2"
                    },
                    startreplaycolor = "#FFFFFF"
                )
            )


            customwallpaperlist.add(
                Customwallpaperadaptermodel(
                    name = requireContext().resources.getString(R.string.Default_customwallpaper),
                    img =
                    if (requireContext().config.Message_Send_Activity == "2") {
                        requireContext().resources.getDrawable(R.drawable.customwallpaperab)
                    } else {
                        requireContext().resources.getDrawable(R.drawable.customwallpaper2)
                    },
                    isselected = false,
                    shareprefselected = 2,
                    toolbarcolor = "#FFFFFF",
                    backgroundcolor = if (requireActivity().config.Message_Send_Activity == "1") {
                        "#FFFFFFF"
                    } else {
                        "#FFFFFFF"
                    },
                    inmessagebackground = if (requireActivity().config.Message_Send_Activity == "1") {
                        "#4AA6FB"
                    } else {
                        "#EBEFF2"
                    },
                    outmessagebackground = if (requireActivity().config.Message_Send_Activity == "1") {
                        "#E8F4FF"
                    } else {
                        "#F2F2F2"
                    },
                    startreplaycolor = "#E5EAEF"
                )
            )
            customwallpaperlist.add(
                Customwallpaperadaptermodel(
                    name = requireContext().resources.getString(R.string.Candy_customwallpaper),
                    img = requireContext().resources.getDrawable(R.drawable.customwallpaper3),
                    isselected = false,
                    shareprefselected = 3,
                    toolbarcolor = "#FDF2FF",
                    backgroundcolor = "#F9DFFF",
                    inmessagebackground = "#E481FB",
                    outmessagebackground = "#FDF7FF",
                    startreplaycolor = "#FDF2FF"
                )
            )
            customwallpaperlist.add(
                Customwallpaperadaptermodel(
                    name = requireContext().resources.getString(R.string.Unicorn_customwallpaper),
                    img = requireContext().resources.getDrawable(R.drawable.customwallpaper4),
                    isselected = false,
                    shareprefselected = 4,
                    toolbarcolor = "#F6F2FF",
                    backgroundcolor = "#E8DFFF",
                    inmessagebackground = "#9E7EEE",
                    outmessagebackground = "#D6C6FE",
                    startreplaycolor = "#F6F2FF"
                )
            )
            customwallpaperlist.add(
                Customwallpaperadaptermodel(
                    name = requireContext().resources.getString(R.string.Rocket_customwallpaper),
                    img = requireContext().resources.getDrawable(R.drawable.customwallpaper5),
                    isselected = false,
                    shareprefselected = 5,
                    toolbarcolor = "#FFECE7",
                    backgroundcolor = "#FFE5DF",
                    inmessagebackground = "#FFC3B5",
                    outmessagebackground = "#FFF4F1",
                    startreplaycolor = "#FFECE7"
                )
            )
            customwallpaperlist.add(
                Customwallpaperadaptermodel(
                    name = requireContext().resources.getString(R.string.Ocean_customwallpaper),
                    img = requireContext().resources.getDrawable(R.drawable.customwallpaper6),
                    isselected = false,
                    shareprefselected = 6,
                    toolbarcolor = "#E7F1FF",
                    backgroundcolor = "#DFECFF",
                    inmessagebackground = "#98BCF0",
                    outmessagebackground = "#F4F9FF",
                    startreplaycolor = "#E7F1FF"

                )
            )
            customwallpaperlist.add(
                Customwallpaperadaptermodel(
                    name = requireContext().resources.getString(R.string.Apple_customwallpaper),
                    img = requireContext().resources.getDrawable(R.drawable.customwallpaper7),
                    isselected = false,
                    shareprefselected = 7,
                    toolbarcolor = "#FCCCCC",
                    backgroundcolor = "#FFD5D5",
                    inmessagebackground = "#EE4242",
                    outmessagebackground = "#FFEBEB",
                    startreplaycolor = "#FCCCCC"
                )
            )
            customwallpaperlist.add(
                Customwallpaperadaptermodel(
                    name = requireContext().resources.getString(R.string.Honey_customwallpaper),
                    img = requireContext().resources.getDrawable(R.drawable.customwallpaper8),
                    isselected = false,
                    shareprefselected = 8,
                    toolbarcolor = "#FEE1AD",
                    backgroundcolor = "#FDD691",
                    inmessagebackground = "#E3A331",
                    outmessagebackground = "#FFF2DB",
                    startreplaycolor = "#FEE1AD"
                )
            )
            customwallpaperlist.add(
                Customwallpaperadaptermodel(
                    name = requireContext().resources.getString(R.string.Kiwi_customwallpaper),
                    img = requireContext().resources.getDrawable(R.drawable.customwallpaper9),
                    isselected = false,
                    shareprefselected = 9,
                    toolbarcolor = "#B9FBD0",
                    backgroundcolor = "#A1F9BF",
                    inmessagebackground = "#35D06A",
                    outmessagebackground = "#EBFFF2",
                    startreplaycolor = "#B9FBD0"
                )
            )
            customwallpaperlist.add(
                Customwallpaperadaptermodel(
                    name = requireContext().resources.getString(R.string.Rose_customwallpaper),
                    img = requireContext().resources.getDrawable(R.drawable.customwallpaper10),
                    isselected = false,
                    shareprefselected = 10,
                    toolbarcolor = "#FFCEE5",
                    backgroundcolor = "#FFBDDD",
                    inmessagebackground = "#F95CA8",
                    outmessagebackground = "#FFE3F0",
                    startreplaycolor = "#FFCEE5"
                )
            )
            customwallpaperlist.add(
                Customwallpaperadaptermodel(
                    name = requireContext().resources.getString(R.string.Lavender_customwallpaper),
                    img = requireContext().resources.getDrawable(R.drawable.customwallpaper11),
                    isselected = false,
                    shareprefselected = 11,
                    toolbarcolor = "#B9BCFC",
                    backgroundcolor = "#A1A4FB",
                    inmessagebackground = "#474DEA",
                    outmessagebackground = "#E2E4FF",
                    startreplaycolor = "#B9BCFC"
                )
            )
            customwallpaperlist.add(
                Customwallpaperadaptermodel(
                    name = requireContext().resources.getString(R.string.Ocean_customwallpaper_two),
                    img = requireContext().resources.getDrawable(R.drawable.customwallpaper12),
                    isselected = false,
                    shareprefselected = 12,
                    toolbarcolor = "#FFE7E7",
                    backgroundcolor = "#FFDFDF",
                    inmessagebackground = "#F49D9D",
                    outmessagebackground = "#FFF7F4",
                    startreplaycolor = "#FFE7E7"
                )
            )
            customwallpaperlist.forEachIndexed { index, customwallpaperadaptermodel ->

                customwallpaperlist[index].isselected = requireContext().config.customwallpaperselected == customwallpaperadaptermodel.shareprefselected

            }
        }
        withContext(Dispatchers.Main) {
            customwallpaperadapter.datalist = customwallpaperlist
            if (autoacroll) {
//                binding.customWallpaperRecylerview.scrollToPosition(customwallpaperlist.indexOf(customwallpaperlist.firstOrNull { it.isselected }))
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (isdonebuttonclick) {
            Log.d("JIGAR", "onDismiss: <-------------> 1")
            if (isresetbuttonclick) {
                Log.d("JIGAR", "onDismiss: <-------------> 2")
                shareprefselected = 2
                livewallpaperchangepreview?.invoke(
                    backgroundcolorcustomwallpaper,
                    toolbarcolorcustomwallpaper,
                    inmessagecolorcustomwallpaper,
                    outmessagecolorcustomwallpaper,
                    smartreplycolor,
                    isdonebuttonclick,
                    isdonebuttonclickwithoutresete
                )
            } else {
                Log.d("JIGAR", "onDismiss: <-------------> 3")
                customwallpaperadaptermodelthis.backgroundcolor?.let {
                    customwallpaperadaptermodelthis.toolbarcolor?.let { it1 ->
                        customwallpaperadaptermodelthis.inmessagebackground?.let { it2 ->
                            customwallpaperadaptermodelthis.outmessagebackground?.let { it3 ->
                                customwallpaperadaptermodelthis.startreplaycolor?.let { it4 ->
                                    livewallpaperchangepreview?.invoke(
                                        it,
                                        it1,
                                        it2,
                                        it3,
                                        it4,
                                        isdonebuttonclick,
                                        isdonebuttonclickwithoutresete
                                    )
                                }
                            }
                        }
                    }
                }
            }

        } else {
            Log.d("JIGAR", "onDismiss: <-------------> " +
                    "4 inmessagecolorcustomwallpaper ${inmessagecolorcustomwallpaper} " +
                    "\n " +
                    "outmessagecolorcustomwallpaper ${outmessagecolorcustomwallpaper}")

            shareprefselected = shareprefselectedold
            livewallpaperchangepreview?.invoke(
                backgroundcolorcustomwallpaper,
                toolbarcolorcustomwallpaper,
                inmessagecolorcustomwallpaper,
                outmessagecolorcustomwallpaper,
                smartreplycolor,
                isdonebuttonclick,
                isdonebuttonclickwithoutresete
            )
        }

    }
}
