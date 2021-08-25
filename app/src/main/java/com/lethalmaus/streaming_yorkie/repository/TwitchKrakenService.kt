package com.lethalmaus.streaming_yorkie.repository

import android.app.Activity
import com.lethalmaus.streaming_yorkie.R
import com.lethalmaus.streaming_yorkie.common.*
import com.lethalmaus.streaming_yorkie.dashboard.user.UserManager
import com.lethalmaus.streaming_yorkie.getApplicationContext
import com.lethalmaus.streaming_yorkie.repository.models.KrakenChannel
import com.lethalmaus.streaming_yorkie.repository.models.KrakenFollowers
import com.lethalmaus.streaming_yorkie.repository.models.KrakenUser
import retrofit2.Call
import retrofit2.http.*

class TwitchKrakenService(override val activity: Activity) : BaseService(activity) {

    object Headers {
        fun kraken(): Map<String, String> {
            val headerMap = mutableMapOf<String, String>()
            headerMap["Authorization"] = "OAuth ${UserManager.getTwitchToken()}"
            headerMap["Accept"] = "application/vnd.twitchtv.v5+json"
            headerMap["Client-ID"] = getApplicationContext().getAppClientId()
            headerMap["Content-Type"] = "application/json; charset=utf-8"
            return headerMap
        }
    }

    interface KrakenEndpoints {

        @GET(krakenUser)
        fun getUser(
            @HeaderMap headers: Map<String, String> = Headers.kraken()
        ): Call<KrakenUser>

        @GET(krakenChannel)
        fun getChannel(
            @HeaderMap headers: Map<String, String> = Headers.kraken()
        ): Call<KrakenChannel>

        @GET(krakenFollowers)
        fun getFollowers(
            @Path("userId") userId: String,
            @Query("limit") limit: Int = 25,
            @Query("direction") direction: String = "desc",
            @Query("cursor") cursor: String = "",
            @HeaderMap headers: Map<String, String> = Headers.kraken()
        ): Call<KrakenFollowers>

    }
    private val krakenEndpoints: KrakenEndpoints by lazy {
        createService(
            service = KrakenEndpoints::class.java,
            baseUrl = activity.getString(R.string.twitch_api)
        )
    }
    suspend fun getUser(): GenericResponse<KrakenUser?> {
        if (refreshTokensIfNeeded()) {
            return krakenEndpoints.getUser().mapToGenericResponse()
        }
        return NetworkError(ErrorCode.TOKENS_EXPIRED.errorCode)
    }
    suspend fun getChannel(): GenericResponse<KrakenChannel?> {
        if (refreshTokensIfNeeded()) {
            return krakenEndpoints.getChannel().mapToGenericResponse()
        }
        return NetworkError(ErrorCode.TOKENS_EXPIRED.errorCode)
    }
    suspend fun getFollowers(): GenericResponse<KrakenFollowers?> {
        if (refreshTokensIfNeeded()) {
            return krakenEndpoints.getFollowers(UserManager.getCurrentUserId()).mapToGenericResponse()
        }
        return NetworkError(ErrorCode.TOKENS_EXPIRED.errorCode)
    }
}