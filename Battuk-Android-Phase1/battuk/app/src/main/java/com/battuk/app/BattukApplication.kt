package com.battuk.app

import android.app.Application
import com.battuk.app.data.AppDatabase
import com.battuk.app.data.BattukRepository
import com.battuk.app.util.PreferencesManager
import com.battuk.app.util.SoundManager

class BattukApplication : Application() {

    lateinit var repository: BattukRepository
        private set

    lateinit var preferencesManager: PreferencesManager
        private set

    lateinit var soundManager: SoundManager
        private set

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getInstance(this)
        repository = BattukRepository(database)
        preferencesManager = PreferencesManager(this)
        soundManager = SoundManager(this)
    }
}
