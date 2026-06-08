package com.messenger.phone.number.text.sms.service.apps.Waitinggame.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityResultBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class resultActivity : AppCompatActivity() {

    lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_result)

        val score = intent.getIntExtra("SCORE", 0)
        binding.scoreLabel.text = score.toString() + ""

        val settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE)
        val highscore = settings.getInt("HIGH_SCORE", 0)

        if (score > highscore) {
            binding.highScoreLabel.text = "High Score : $score"
            //save
            val editor = settings.edit()
            editor.putInt("HIGH_SCORE", score)
            editor.commit()
        } else {
            binding.highScoreLabel.text = "High Score : $highscore"
        }

    }

    fun tryAgain(view: View?) {
        val intent = Intent(applicationContext, GameActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed()
            return true // Consume the event
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}