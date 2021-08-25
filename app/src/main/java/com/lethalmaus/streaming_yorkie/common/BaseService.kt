package com.lethalmaus.streaming_yorkie.common

import android.app.Activity
import com.lethalmaus.streaming_yorkie.MainActivity
import com.lethalmaus.streaming_yorkie.dashboard.user.UserManager
import com.lethalmaus.streaming_yorkie.login.LoginFragment

abstract class BaseService (open val activity: Activity) {

    fun refreshTokensIfNeeded(): Boolean {
        if (!UserManager.hasValidTokens(activity)) {
            (activity as MainActivity?)?.navigate(LoginFragment())
            return false
        }
        return true
    }
}