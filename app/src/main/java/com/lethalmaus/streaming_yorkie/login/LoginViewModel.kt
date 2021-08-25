package com.lethalmaus.streaming_yorkie.login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.lethalmaus.streaming_yorkie.common.BaseViewModel
import com.lethalmaus.streaming_yorkie.common.GenericResponse
import com.lethalmaus.streaming_yorkie.common.SuccessResponse
import com.lethalmaus.streaming_yorkie.repository.models.TokenValidation
import com.lethalmaus.streaming_yorkie.repository.TwitchAuthService
import kotlinx.coroutines.launch

class LoginViewModel : BaseViewModel() {

    val clientValidationLiveData = MutableLiveData<GenericResponse<TokenValidation?>>()
    val websiteValidationLiveData = MutableLiveData<GenericResponse<TokenValidation?>>()

    fun validateClientToken(context: Context, token: String) = launch {
        val validationResponse = TwitchAuthService(context).validateToken(token)
        if (validationResponse is SuccessResponse) {
            validationResponse.data?.token = token
        }
        emitLiveData(clientValidationLiveData, validationResponse)
    }

    fun validateWebsiteToken(context: Context, token: String) = launch {
        val validationResponse = TwitchAuthService(context).validateToken(token)
        if (validationResponse is SuccessResponse) {
            validationResponse.data?.token = token
        }
        emitLiveData(websiteValidationLiveData, validationResponse)
    }
}