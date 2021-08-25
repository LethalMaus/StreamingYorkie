package com.lethalmaus.streaming_yorkie.repository

import android.content.Context
import com.lethalmaus.streaming_yorkie.R
import com.lethalmaus.streaming_yorkie.common.GenericResponse
import com.lethalmaus.streaming_yorkie.common.createService
import com.lethalmaus.streaming_yorkie.common.mapToGenericResponse
import com.lethalmaus.streaming_yorkie.repository.models.TokenValidation
import retrofit2.Call
import retrofit2.http.*

class TwitchAuthService(val context: Context) {

    interface AuthEndpoints {

        @GET(twitchAuthValidate)
        fun validateToken(
            @Header("Authorization") accessToken: String,
        ): Call<TokenValidation>

    }
    private val authEndpoints: AuthEndpoints by lazy {
        createService(
            service = AuthEndpoints::class.java,
            baseUrl = context.getString(R.string.twitch_url_oauth2)
        )
    }
    suspend fun validateToken(token: String): GenericResponse<TokenValidation?> {
        return authEndpoints.validateToken("OAuth $token").mapToGenericResponse()
    }
}