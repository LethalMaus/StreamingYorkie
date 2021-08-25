package com.lethalmaus.streaming_yorkie.common

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

abstract class BaseManager {

    abstract val prefId: String
    var sharedPreferences: SharedPreferences? = null
    var gson = Gson()

    fun loadSharedPrefs(context: Context?) {
        if (sharedPreferences == null) {
            sharedPreferences = context?.getSharedPreferences(prefId, Context.MODE_PRIVATE)
        }
    }
}