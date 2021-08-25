package com.lethalmaus.streaming_yorkie.repository

import android.app.Activity
import com.lethalmaus.streaming_yorkie.R
import com.lethalmaus.streaming_yorkie.common.*
import com.lethalmaus.streaming_yorkie.dashboard.user.UserManager
import com.lethalmaus.streaming_yorkie.getApplicationContext
import com.lethalmaus.streaming_yorkie.repository.models.HelixFollows
import com.lethalmaus.streaming_yorkie.repository.models.KrakenChannel
import com.lethalmaus.streaming_yorkie.repository.models.KrakenFollowers
import com.lethalmaus.streaming_yorkie.repository.models.KrakenUser
import retrofit2.Call
import retrofit2.http.*

class TwitchHelixService(override val activity: Activity) : BaseService(activity) {

    object Headers {
        fun kraken(): Map<String, String> {
            val headerMap = mutableMapOf<String, String>()
            headerMap["Authorization"] = "Bearer ${UserManager.getTwitchToken()}"
            headerMap["Accept"] = "application/vnd.twitchtv.v5+json"
            headerMap["Client-ID"] = getApplicationContext().getAppClientId()
            headerMap["Content-Type"] = "application/json; charset=utf-8"
            return headerMap
        }
    }

    interface HelixEndpoints {

        @GET(helixFollows)
        fun getFollowers(
            @Query("to_id") userId: String,
            @Query("first") limit: Int = 25,
            @Query("after") cursor: String = "",
            @HeaderMap headers: Map<String, String> = Headers.kraken()
        ): Call<HelixFollows>

    }
    private val helixEndpoints: HelixEndpoints by lazy {
        createService(
            service = HelixEndpoints::class.java,
            baseUrl = activity.getString(R.string.twitch_api)
        )
    }
    suspend fun getFollowers(cursor: String = ""): GenericResponse<HelixFollows?> {
        if (refreshTokensIfNeeded()) {
            return helixEndpoints.getFollowers(UserManager.getCurrentUserId(), cursor = cursor).mapToGenericResponse()
        }
        return NetworkError(ErrorCode.TOKENS_EXPIRED.errorCode)
    }
}