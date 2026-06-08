package com.messenger.phone.number.text.sms.service.apps

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import com.messenger.phone.number.text.sms.service.apps.fragment.SettingFragment
import com.messenger.phone.number.text.sms.service.apps.fragment.WhatNewFragment
import dagger.hilt.android.AndroidEntryPoint


class BubbleActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bubble)


//        supportFragmentManager.beginTransaction()
//            .setReorderingAllowed(true)
//            .add(findViewById<FrameLayout>(R.id.fragl).id, SettingFragment::class.java, null)
//            .commit()
    }
}