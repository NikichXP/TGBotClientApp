package com.nikichxp.tgbotapp

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ServerInfoViewModel : ViewModel() {

    init {
        Log.i(TAG, "Server info view model init section")
    }

    var token by mutableStateOf("")
        private set

    var url by mutableStateOf("")
        private set

    fun updateToken(newToken: String) {
        token = newToken
    }

    fun updateUrl(newUrl: String){
        Log.i(TAG, "Update url: $newUrl")
        url = newUrl
    }

    fun saveToken(serverConnectionConfig: ServerConnectionConfig) {
        serverConnectionConfig.token = token
    }

    fun saveUrl(serverConnectionConfig: ServerConnectionConfig) {
        serverConnectionConfig.url = url
    }

    companion object {
        private const val TAG = "ServerInfoViewModel"
    }

}