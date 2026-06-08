package com.messenger.phone.number.text.sms.service.apps.lockscreen

import android.annotation.SuppressLint
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.messenger.phone.number.text.sms.service.apps.databinding.LayoutNumberItemBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.KeybordPrivacyClick
import com.messenger.phone.number.text.sms.service.apps.modelClass.KeybordModelClass
import java.util.logging.Handler
import javax.inject.Inject

class LockKeyBordAdapter @Inject constructor() : RecyclerView.Adapter<LockKeyBordAdapter.LockKeyBordAdapterViewHolder>() {

    lateinit var keybordPrivacyClick: KeybordPrivacyClick
    var keyborttxt = ""

    class LockKeyBordAdapterViewHolder(var binding: LayoutNumberItemBinding) : ViewHolder(binding.root)

    fun setLeyInterface(keybordPrivacyClick: KeybordPrivacyClick) {
        this.keybordPrivacyClick = keybordPrivacyClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LockKeyBordAdapterViewHolder {
        return LockKeyBordAdapterViewHolder(LayoutNumberItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return buttonlist.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: LockKeyBordAdapterViewHolder, position: Int) {
        with(holder.binding) {
            itemnumberBtn = buttonlist[position]
            chack = "-1"
            button.setOnClickListener {
                if (position == 11) {
                    if (keyborttxt.isNotEmpty()) {
                        keyborttxt = keyborttxt.substring(0, keyborttxt.length - 1)
                        keybordPrivacyClick.onBackpressClick(keyborttxt)
                    }
                } else if (position == 9) {
                    keybordPrivacyClick.onFingerprintclick()
                } else {
                    if (keyborttxt.length<6){
                        keyborttxt += buttonlist[position].buttonCount
                        keybordPrivacyClick.buttonclick(keyborttxt)
                    }
                }

            }
        }
    }

    var buttonlist = ArrayList<KeybordModelClass>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

}