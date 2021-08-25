package com.lethalmaus.streaming_yorkie.intro

import android.content.Context
import com.lethalmaus.streaming_yorkie.common.BaseManager

const val INTRO_LOGIN = "login"
const val INTRO_DASHBOARD = "dashboard"

object IntroManager : BaseManager() {

    override val prefId = "intro"

    fun hasShownLoginIntro(context: Context): Boolean {
        loadSharedPrefs(context)
        return true
    }

    fun hasShownDashboardIntro(context: Context): Boolean {
        loadSharedPrefs(context)
        return true
    }
}