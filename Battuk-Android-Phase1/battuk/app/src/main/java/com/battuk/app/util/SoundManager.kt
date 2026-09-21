package com.battuk.app.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.battuk.app.R

/**
 * Plays the short UI click sound when [soundOn] is true. Kept as a lightweight
 * singleton-per-Application wrapper around SoundPool so every screen can reuse it.
 */
class SoundManager(context: Context) {

    private val appContext = context.applicationContext
    private var soundOn: Boolean = true

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(2)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private var clickSoundId: Int = 0
    private var loaded = false

    init {
        clickSoundId = soundPool.load(appContext, R.raw.click_sound, 1)
        soundPool.setOnLoadCompleteListener { _, _, status ->
            loaded = status == 0
        }
    }

    fun setSoundOn(on: Boolean) {
        soundOn = on
    }

    fun playClick() {
        if (soundOn && loaded) {
            soundPool.play(clickSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun release() {
        soundPool.release()
    }
}
