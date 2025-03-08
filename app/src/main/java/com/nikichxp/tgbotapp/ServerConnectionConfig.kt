package com.nikichxp.tgbotapp

import android.content.Context
import com.nikichxp.tgbotapp.storage.SharedPrefsDataProvider

class ServerConnectionConfig(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var token: String by SharedPrefsDataProvider(TOKEN_KEY, sharedPreferences)
    var url: String by SharedPrefsDataProvider(URL_KEY, sharedPreferences)

    fun isSetUp(): Boolean {
        return token.isNotEmpty() && url.isNotEmpty()
    }

    fun clearToken() {
        sharedPreferences.edit().remove(TOKEN_KEY).apply()
    }

    companion object {
        private const val TOKEN_KEY = "api_token"
        private const val URL_KEY = "remote_url"
    }
}


