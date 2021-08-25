package com.lethalmaus.streaming_yorkie

import android.app.Application

@Suppress("unused")
class MainApplication : Application() {
    companion object {
        lateinit var instance: MainApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun getAppClientId() = getString(R.string.app_client_id)
    fun getTWitchClientId() = getString(R.string.twitch_client_id)
}
fun getApplicationContext(): MainApplication = MainApplication.instance