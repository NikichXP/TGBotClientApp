package com.nikichxp.tgbotapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ServerInfoViewModel : ViewModel() {

    var token by mutableStateOf("")
        private set

    var url by mutableStateOf("")
        private set


    fun updateToken(newToken: String) {
        token = newToken
    }

    fun updateUrl(newUrl: String){
        url = newUrl
    }

    fun saveToken(serverConnectionConfig: ServerConnectionConfig) {
        serverConnectionConfig.token = token
    }

    fun saveUrl(serverConnectionConfig: ServerConnectionConfig) {
        serverConnectionConfig.url = url
    }

}