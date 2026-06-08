package com.messenger.phone.number.text.sms.service.apps.Waitinggame.ui

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.demo.adsmanage.Commen.Constants
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.Waitinggame.Utils.SoundPlayer
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityPlayGameBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject
import kotlin.math.floor

@AndroidEntryPoint
class PlayGameActivity : AppCompatActivity() {

    lateinit var binding: ActivityPlayGameBinding

    @Inject
    lateinit var sound: SoundPlayer

    private var frameHeight = 0
    private var boxSize = 0
    private var screenWidth = 0
    private var screenHeight = 0

    //position
    private var boxY = 0
    private var orangeX = 0
    private var orangeY = 0
    private var pinkX = 0
    private var pinkY = 0
    private var blackX = 0
    private var blackY = 0

    //Speed
    private var boxSpeed = 0
    private var orangeSpeed = 0
    private var pinkSpeed = 0
    private var blackSpeed = 0

    //Score
    private var score = 0

    //Initilaize Class
    private val handler = Handler()
    private var timer = Timer()


    //status Check
    private var action_flag = false
    private var start_flag = false
    private var pause_flg = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play_game)

        //Get Screen Size;
        val windowManager = windowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        screenWidth = size.x
        screenHeight = size.y

        with(binding) {
            playoption.isEnabled = false
            black.y = -80f
            black.x = -80f
            pink.y = -80f
            pink.x = -80f
            orange.y = -80f
            orange.x = -80f
            scoreLabel.text = "Score : 0"
        }
    }

    fun changepos() {
        hitCheck()
        //orange
        orangeX -= orangeSpeed
        if (orangeX < 0) {
            orangeX = screenWidth + 20
            orangeY = floor(Math.random() * (frameHeight - binding.orange.height)).toInt()
        }
        binding.orange.y = orangeY.toFloat()
        binding.orange.x = orangeX.toFloat()

        //black
        blackX -= blackSpeed
        if (blackX < 0) {
            blackX = screenWidth + 10
            blackY = floor(Math.random() * (frameHeight - binding.black.height)).toInt()
        }
        binding.black.y = blackY.toFloat()
        binding.black.x = blackX.toFloat()

        //pink
        pinkX -= pinkSpeed
        if (pinkX < 0) {
            pinkX = screenWidth + 5000
            pinkY = floor(Math.random() * (frameHeight - binding.pink.getHeight())).toInt()
        }
        binding.pink.y = pinkY.toFloat()
        binding.pink.x = pinkX.toFloat()
        boxSpeed = Math.round(screenHeight / 60f)
        orangeSpeed = Math.round(screenWidth / 60f)
        pinkSpeed = Math.round(screenWidth / 36f)
        blackSpeed = Math.round(screenWidth / 45f)
        Log.v("SPEED_BOX : ", boxSpeed.toString() + "")
        Log.v("SPEED_PINK : ", pinkSpeed.toString() + "")
        Log.v("SPEED_ORANGE : ", orangeSpeed.toString() + "")
        Log.v("SPEED_BLACK : ", blackSpeed.toString() + "")

        //Move Box
        if (action_flag) {
            boxY -= boxSpeed
            binding.box.setImageResource(R.drawable.down)
        } else {
            boxY += boxSpeed
            binding.box.setImageResource(R.drawable.up)
        }

        //check box position
        if (boxY < 0) boxY = 0
        if (boxY > frameHeight - boxSize) boxY = frameHeight - boxSize
        binding.box.y = boxY.toFloat()
        binding.scoreLabel.text = "Score : $score"
    }

    fun hitCheck() {

        //orange
        val orangeCenterX: Int = orangeX + binding.orange.width / 2
        val orangeCenterY: Int = orangeY + binding.orange.height / 2
        if (orangeCenterX in 0..boxSize && boxY <= orangeCenterY && orangeCenterY <= boxY + boxSize) {
            score += 10
            orangeX = -10
            sound.playHitSound()
        }

        //pink
        val pinkCenterX: Int = pinkX + binding.pink.width / 2
        val pinkCenterY: Int = pinkY + binding.pink.height / 2
        if (pinkCenterX in 0..boxSize && boxY <= pinkCenterY && pinkCenterY <= boxY + boxSize) {
            score += 30
            pinkX = -10
            sound.playHitSound()
        }

        //Black
        val blackCenterX: Int = blackX + binding.black.width / 2
        val blackCenterY: Int = blackY + binding.black.height / 2
        if (blackCenterX in 0..boxSize && boxY <= blackCenterY && blackCenterY <= boxY + boxSize) {
            // Stop Timer!!
            timer.cancel()
            sound.playOverSound()


            //Show Result
            val intent = Intent(applicationContext, resultActivity::class.java)
            intent.putExtra("SCORE", score)
            startActivity(intent)
            finish()
        }
    }

    fun pausePushed(view: View?) {
        if (!pause_flg) {
            pause_flg = true
            timer.cancel()
            binding.playoption.setImageResource(R.drawable.play)
        } else {
            pause_flg = false
            binding.playoption.setImageResource(R.drawable.pause)
            timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    handler.post { changepos() }
                }
            }, 0, 20)
        }
    }

    override fun onTouchEvent(me: MotionEvent): Boolean {
        if (!start_flag) {
            start_flag = true
            frameHeight = binding.frame.height
            boxY = binding.box.y.toInt()
            boxSize = binding.box.height
            binding.startLabel.visibility = View.GONE
            binding.playoption.isEnabled = true
            timer.schedule(object : TimerTask() {
                override fun run() {
                    handler.post { changepos() }
                }
            }, 0, 20)
        } else {
            if (me.action == MotionEvent.ACTION_DOWN) {
                action_flag = true
            } else if (me.action == MotionEvent.ACTION_UP) {
                action_flag = false
            }
        }
        return true
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed()
            pause_flg = true
            timer.cancel()
            binding.playoption.setImageResource(R.drawable.play)
            return true // Consume the event
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onBackPressed() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("End Game")
        builder.setMessage("Are You Sure?")
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
            finish()
        })
        builder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
        val alert: AlertDialog = builder.create()
        alert.show()
    }
}