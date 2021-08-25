package com.lethalmaus.streaming_yorkie.dashboard

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.lethalmaus.streaming_yorkie.common.BaseViewModel
import com.lethalmaus.streaming_yorkie.common.GenericResponse
import com.lethalmaus.streaming_yorkie.repository.TwitchKrakenService
import com.lethalmaus.streaming_yorkie.repository.models.KrakenUser
import kotlinx.coroutines.launch

class DashboardViewModel : BaseViewModel() {

    val userLiveData = MutableLiveData<GenericResponse<KrakenUser?>>()

    fun getUser(activity: Activity) = launch {
        val response = TwitchKrakenService(activity).getUser()
        emitLiveData(userLiveData, response)
    }
}