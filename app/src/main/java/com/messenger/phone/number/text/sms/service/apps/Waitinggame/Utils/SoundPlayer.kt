package com.messenger.phone.number.text.sms.service.apps.Waitinggame.Utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import com.messenger.phone.number.text.sms.service.apps.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.properties.Delegates

class SoundPlayer @Inject constructor(@ApplicationContext context: Context) {
    private var audioAttributes: AudioAttributes? = null
    val SOUND_POOL_MAX = 2

    init {

        //Sound Pool is deprecated in api 21.lollipop
        //so we use AudioAttributes
        audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        soundPool = SoundPool.Builder()
            .setAudioAttributes(audioAttributes)
            .setMaxStreams(SOUND_POOL_MAX)
            .build()
        hitSound = soundPool!!.load(context, R.raw.hit, 1)
        overSound = soundPool!!.load(context, R.raw.over, 1)
    }

    fun playHitSound() {
        soundPool!!.play(hitSound, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    fun playOverSound() {
        soundPool!!.play(overSound, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    companion object {
        private var soundPool: SoundPool? = null
        var hitSound by Delegates.notNull<Int>()
        var overSound by Delegates.notNull<Int>()
    }
}
