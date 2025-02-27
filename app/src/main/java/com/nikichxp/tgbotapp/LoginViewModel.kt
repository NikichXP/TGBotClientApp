package com.nikichxp.tgbotapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    var isTokenVisible by mutableStateOf(false)
        private set

    fun toggleTokenVisibility() {
        isTokenVisible = !isTokenVisible
    }

}