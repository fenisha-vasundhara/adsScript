package com.messenger.phone.number.text.sms.service.apps

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.android.material.radiobutton.MaterialRadioButton

class WallpaperChoiceDialogAdapter(
    context: Context,
    private val options: Array<String>,
    selectedPosition: Int,
    primaryColor: Int,
    textColor: Int
) : ArrayAdapter<String>(context, 0, options) {

    private val inflater = LayoutInflater.from(context)
    private val radioTint = ColorStateList(
        arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf()
        ),
        intArrayOf(
            primaryColor,
            textColor
        )
    )

    private var selectedPosition = selectedPosition
    private val textColor = textColor

    override fun getCount(): Int = options.size

    override fun getItem(position: Int): String = options[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.item_wallpaper_choice_dialog, parent, false)
        val radioButton = view.findViewById<MaterialRadioButton>(R.id.wallpaperChoiceRadio)
        val textView = view.findViewById<TextView>(R.id.wallpaperChoiceText)

        radioButton.buttonTintList = radioTint
        radioButton.isChecked = position == selectedPosition
        textView.text = getItem(position)
        textView.setTextColor(textColor)

        return view
    }

    fun updateSelection(position: Int) {
        if (selectedPosition == position) {
            return
        }
        selectedPosition = position
        notifyDataSetChanged()
    }
}
