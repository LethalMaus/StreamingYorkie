package com.lethalmaus.streaming_yorkie.dashboard.user

import android.content.Context
import com.lethalmaus.streaming_yorkie.common.BaseManager
import com.lethalmaus.streaming_yorkie.repository.models.TokenValidation

const val TWITCH_TOKEN = "twitch_token"
const val TWITCH_TOKEN_EXPIRES_AT = "twitch_token_expires_in"
const val TWITCH_WEBSITE_TOKEN = "twitch_website_token"
const val TWITCH_WEBSITE_TOKEN_EXPIRES_AT = "twitch_website_token_expires_in"
const val CURRENT_USER_ID = "current_user_id"
const val CURRENT_USER_LOGO = "current_user_logo"
const val CURRENT_USERNAME = "current_username"

object UserManager : BaseManager() {

    override val prefId = "user"

    fun hasValidTokens(context: Context?): Boolean {
        loadSharedPrefs(context)
        val currentUnix = System.currentTimeMillis() / 1000
        return getTwitchTokenExpiresAt() > currentUnix && getTwitchWebsiteTokenExpiresAt() > currentUnix
    }

    fun setTwitchWebsiteToken(validation: TokenValidation?) {
        validation?.let {
            with (sharedPreferences?.edit()) {
                this?.putString("${TWITCH_WEBSITE_TOKEN}_${it.userId}", it.token)
                this?.putLong("${TWITCH_WEBSITE_TOKEN_EXPIRES_AT}_${it.userId}", (System.currentTimeMillis() / 1000) + 518400)
                this?.commit()
            }
        }
    }

    fun getTwitchWebsiteToken(userId: String): String {
        return sharedPreferences?.getString("${TWITCH_WEBSITE_TOKEN}_${getCurrentUserId()}", "")?: ""
    }

    private fun getTwitchWebsiteTokenExpiresAt(): Long {
        return sharedPreferences?.getLong("${TWITCH_WEBSITE_TOKEN_EXPIRES_AT}_${getCurrentUserId()}", 0)?: 0
    }

    fun setTwitchToken(validation: TokenValidation?) {
        validation?.let {
            setCurrentUserId(it.userId)
            with(sharedPreferences?.edit()) {
                this?.putString("${TWITCH_TOKEN}_${it.userId}", it.token)
                this?.putLong("${TWITCH_TOKEN_EXPIRES_AT}_${it.userId}", (System.currentTimeMillis() / 1000) + it.expiresIn)
                this?.commit()
            }
        }
    }

    fun getTwitchToken(): String {
        return sharedPreferences?.getString("${TWITCH_TOKEN}_${getCurrentUserId()}", "")?: ""
    }

    private fun getTwitchTokenExpiresAt(): Long {
        return sharedPreferences?.getLong("${TWITCH_TOKEN_EXPIRES_AT}_${getCurrentUserId()}", 0)?: 0
    }

    fun setCurrentUserId(userId: String) {
        with(sharedPreferences?.edit()) {
            this?.putString(CURRENT_USER_ID, userId)
            this?.commit()
        }
    }

    fun getCurrentUserId(): String {
        return sharedPreferences?.getString(CURRENT_USER_ID, "")?: ""
    }

    fun setCurrentUserLogo(logo: String?) {
        with(sharedPreferences?.edit()) {
            this?.putString("${CURRENT_USER_ID}_${CURRENT_USER_LOGO}", logo)
            this?.commit()
        }
    }

    fun getCurrentUserLogo(): String {
        return sharedPreferences?.getString("${CURRENT_USER_ID}_${CURRENT_USER_LOGO}", "")?: ""
    }

    fun setCurrentUserName(userName: String?) {
        with(sharedPreferences?.edit()) {
            this?.putString("${CURRENT_USER_ID}_${CURRENT_USERNAME}", userName)
            this?.commit()
        }
    }

    fun getCurrentUserName(): String {
        return sharedPreferences?.getString("${CURRENT_USER_ID}_${CURRENT_USERNAME}", "")?: ""
    }
}