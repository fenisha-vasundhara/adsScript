package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.WhatNewDialogTemBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.WhatNewModel
import javax.annotation.Nullable

class WhatsNewDialog(
    var tital: String?,
    var titalbottomtxt: String?,
    var src: Int?,
    var srcbottomtxt: String?,
    var lasttxt1: String?,
    var pos: Int,
    var commanlist: ArrayList<WhatNewModel>
) : MaterialDialogFragment() {

    private var titalbottom: String? = null
    private var titalsidecom: String? = null
    private var titalcom: String? = null
    private var bottomSheetDialog: Dialog? = null
    var selection = 0
    lateinit var binding: WhatNewDialogTemBinding

    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.what_new_dialog_tem, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            if (pos == 1) {
                idLLDots.visible()
                idLLDots1.gone()
                nextbtn.visible()
                title2.visible()
                setImageselection(0)
                selection = 1
                setTxt(commanlist[0].title, commanlist[0].titleside, commanlist[0].titlebottom, selection, commanlist[0].img)

                nextbtn.setOnClickListener {
                    when (selection) {
                        0 -> {

                        }

                        1 -> {
                            setImageselection(1)
                            selection = 2
                            setTxt(commanlist[1].title, commanlist[1].titleside, commanlist[1].titlebottom, 1, commanlist[1].img)
                        }

                        2 -> {
                            setImageselection(2)
                            selection = 3
                            setTxt(commanlist[2].title, commanlist[2].titleside, commanlist[2].titlebottom, 2, commanlist[2].img)
                        }

                        3 -> {
                            setImageselection(3)
                            setTxt(commanlist[3].title, commanlist[3].titleside, commanlist[3].titlebottom, 3, commanlist[3].img)
                        }
                    }

                }


            } else if (pos == 7) {
                idLLDots.gone()
                idLLDots1.visible()
                nextbtn.visible()
                title2.visible()

                setImageselectionCon(0)
                selection = 1
                setTxtCon(commanlist[0].title, commanlist[0].titleside, commanlist[0].lasttxt, selection, commanlist[0].img)

                nextbtn.setOnClickListener {
                    when (selection) {
                        0 -> {

                        }

                        1 -> {
                            setImageselectionCon(1)
                            selection = 3
                            setTxtCon(commanlist[1].title, commanlist[1].titleside, commanlist[1].lasttxt, 3, commanlist[1].img)
                        }

                        2 -> {

                        }
                    }

                }
            } else {
                idLLDots.gone()
                title2.gone()
                nextbtn.gone()
                if (tital == null) {
                    textView15.gone()
                }
                if (titalbottomtxt == null) {
                    bottomtxt.gone()
                }
                if (srcbottomtxt == null) {
                    textView16.gone()
                }
                if (lasttxt1 == null) {
                    lasttxt.gone()
                }
                if (src == null) {
                    imageView22.gone()
                }
                bottomtxt.text = titalbottomtxt
                textView16.text = srcbottomtxt
                textView15.text = tital
                lasttxt.text = lasttxt1
                src?.let { imageView22.setImageResource(it) }
            }
            imageView23.setOnClickListener {
                bottomSheetDialog?.dismiss()
            }
        }
    }

    private fun setTxt(title: String?, titleside: String?, titlebottom: String?, selection: Int, img: Int?) {
        titalcom = title
        titalsidecom = titleside
        titalbottom = titlebottom
        with(binding) {
            Log.d("4234", "setTxt: <--------> ${selection}")
            if (selection == 3) {
                lasttxt.gone()
                textView16.visible()
                imageView23.visible()
                idLLDots.gone()
                title2.text = titalsidecom
                nextbtn.gone()

                if (title == null) {
                    textView15.gone()
                }
                if (titalbottomtxt == null) {
                    bottomtxt.gone()
                }
                if (srcbottomtxt == null) {
                    textView16.gone()
                }
                if (lasttxt1 == null) {
                    lasttxt.gone()
                }
                if (src == null) {
                    imageView22.gone()
                }
                bottomtxt.text = titalbottomtxt
                textView16.text = srcbottomtxt
                textView15.text = title
                lasttxt.text = lasttxt1
                src?.let { imageView22.setImageResource(it) }

            } else {
                lasttxt.gone()
                imageView23.gone()
                textView16.gone()
                textView15.text = titalcom
                title2.text = titalsidecom
                bottomtxt.text = titalbottom
                img?.let { imageView22.setImageResource(it) }
            }
        }
    }

    private fun setTxtCon(title: String?, titleside: String?, titlebottom12: String?, selection: Int, img: Int?) {
        titalcom = title
        titalsidecom = titleside
//        titalbottom = titlebottom
        with(binding) {
            title2.gone()
            bottomtxt.gone()
            Log.d("4234", "setTxt: <--------> ${selection}")
            if (selection == 3) {
//                lasttxt.gone()
//                textView16.visible()
                imageView23.visible()
                textView16Con.visible()
//                idLLDots.gone()
//                title2.text = titalsidecom
                nextbtn.gone()

                if (title == null) {
                    textView15.gone()
                }
//                if (titalbottomtxt == null) {
//                    bottomtxt.gone()
//                }
                if (srcbottomtxt == null) {
                    textView16Con.gone()
                }
                if (lasttxt1 == null) {
                    lasttxt.gone()
                }
                if (src == null) {
                    imageView22.gone()
                }
//                bottomtxt.text = titalbottomtxt
                textView16Con.text = titlebottom12
                textView15.text = title
                lasttxt.text = lasttxt1
                src?.let { imageView22.setImageResource(it) }

            } else {
                lasttxt.gone()
                imageView23.gone()
                textView16.gone()
                textView16Con.text = titlebottom12
                textView16Con.visible()
                textView15.text = titalcom
//                title2.text = titalsidecom
//                bottomtxt.text = titalbottom
                img?.let { imageView22.setImageResource(it) }
            }
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
        bottomSheetDialog?.setCancelable(false)
    }

    fun setImageselection(item_pos: Int) {
        when (item_pos) {
            0 -> {

                binding.idTVSlideZero.setImageResource(R.drawable.into_selected)
                binding.idTVSlideZero.layoutParams.width = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                binding.idTVSlideZero.layoutParams.height = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                binding.idTVSlideZero.requestLayout()

                binding.idTVSlideOne.setImageResource(R.drawable.intro_unselected)
                binding.idTVSlideOne.layoutParams.width = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideOne.layoutParams.height = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideOne.requestLayout()

                binding.idTVSlideTwo.setImageResource(R.drawable.intro_unselected)
                binding.idTVSlideTwo.layoutParams.width = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideTwo.layoutParams.height = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideTwo.requestLayout()

                binding.idTVSlideThree.setImageResource(R.drawable.intro_unselected)
                binding.idTVSlideThree.layoutParams.width = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideThree.layoutParams.height = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideThree.requestLayout()
            }

            1 -> {
                binding.idTVSlideZero.setImageResource(R.drawable.intro_unselected)
                binding.idTVSlideZero.layoutParams.width = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideZero.layoutParams.height = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideZero.requestLayout()

                binding.idTVSlideOne.setImageResource(R.drawable.into_selected)
                binding.idTVSlideOne.layoutParams.width = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                binding.idTVSlideOne.layoutParams.height = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                binding.idTVSlideOne.requestLayout()

                binding.idTVSlideTwo.setImageResource(R.drawable.intro_unselected)
                binding.idTVSlideTwo.layoutParams.width = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideTwo.layoutParams.height = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideTwo.requestLayout()

                binding.idTVSlideThree.setImageResource(R.drawable.intro_unselected)
                binding.idTVSlideThree.layoutParams.width = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideThree.layoutParams.height = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideThree.requestLayout()
            }

            2 -> {
                binding.idTVSlideZero.setImageResource(R.drawable.intro_unselected)
                binding.idTVSlideZero.layoutParams.width = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideZero.layoutParams.height = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideZero.requestLayout()

                binding.idTVSlideOne.setImageResource(R.drawable.intro_unselected)
                binding.idTVSlideOne.layoutParams.width = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideOne.layoutParams.height = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideOne.requestLayout()

                binding.idTVSlideTwo.setImageResource(R.drawable.into_selected)
                binding.idTVSlideTwo.layoutParams.width = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                binding.idTVSlideTwo.layoutParams.height = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                binding.idTVSlideTwo.requestLayout()

                binding.idTVSlideThree.setImageResource(R.drawable.intro_unselected)
                binding.idTVSlideThree.layoutParams.width = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideThree.layoutParams.height = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideThree.requestLayout()
            }

            3 -> {
                binding.idTVSlideZero.setImageResource(R.drawable.intro_unselected)
                binding.idTVSlideZero.layoutParams.width = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideZero.layoutParams.height = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideZero.requestLayout()

                binding.idTVSlideOne.setImageResource(R.drawable.intro_unselected)
                binding.idTVSlideOne.layoutParams.width = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideOne.layoutParams.height = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideOne.requestLayout()

                binding.idTVSlideTwo.setImageResource(R.drawable.intro_unselected)
                binding.idTVSlideTwo.layoutParams.width = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideTwo.layoutParams.height = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideTwo.requestLayout()

                binding.idTVSlideThree.setImageResource(R.drawable.into_selected)
                binding.idTVSlideThree.layoutParams.width = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                binding.idTVSlideThree.layoutParams.height = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                binding.idTVSlideThree.requestLayout()

            }
        }
    }

    fun setImageselectionCon(item_pos: Int) {
        binding.idTVSlideTwo.gone()
        binding.idTVSlideThree.gone()
        when (item_pos) {
            0 -> {

                binding.idTVSlideZero1.setImageResource(R.drawable.into_selected)
                binding.idTVSlideZero1.layoutParams.width = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                binding.idTVSlideZero1.layoutParams.height = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                binding.idTVSlideZero1.requestLayout()

                binding.idTVSlideOne1.setImageResource(R.drawable.intro_unselected)
                binding.idTVSlideOne1.layoutParams.width = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideOne1.layoutParams.height = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideOne1.requestLayout()

            }

            1 -> {
                binding.idTVSlideZero1.setImageResource(R.drawable.intro_unselected)
                binding.idTVSlideZero1.layoutParams.width = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideZero1.layoutParams.height = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                binding.idTVSlideZero1.requestLayout()

                binding.idTVSlideOne1.setImageResource(R.drawable.into_selected)
                binding.idTVSlideOne1.layoutParams.width = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                binding.idTVSlideOne1.layoutParams.height = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                binding.idTVSlideOne1.requestLayout()

            }

        }
    }

}