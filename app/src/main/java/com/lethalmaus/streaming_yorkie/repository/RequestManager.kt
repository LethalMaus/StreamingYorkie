package com.lethalmaus.streaming_yorkie.repository

import android.content.Context
import com.lethalmaus.streaming_yorkie.common.BaseManager
import com.lethalmaus.streaming_yorkie.dashboard.user.TWITCH_TOKEN
import com.lethalmaus.streaming_yorkie.dashboard.user.TWITCH_TOKEN_EXPIRES_AT
import com.lethalmaus.streaming_yorkie.dashboard.user.UserManager
import com.lethalmaus.streaming_yorkie.dashboard.user.UserManager.getCurrentUserId
import com.lethalmaus.streaming_yorkie.repository.models.TokenValidation

const val FOLLOWER_COUNT = "follower_count"

object RequestManager : BaseManager() {

    override val prefId = "request"

    fun getFollowersCount(context: Context?): Int {
        loadSharedPrefs(context)
        return sharedPreferences?.getInt("${FOLLOWER_COUNT}_${getCurrentUserId()}", 0)?: 0
    }

    fun setFollowersCount(count: Int) {
        with(sharedPreferences?.edit()) {
            this?.putInt("${FOLLOWER_COUNT}_${getCurrentUserId()}", count)
            this?.commit()
        }
    }
}