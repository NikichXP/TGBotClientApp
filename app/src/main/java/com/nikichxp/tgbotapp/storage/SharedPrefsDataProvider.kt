package com.nikichxp.tgbotapp.storage

import android.content.SharedPreferences
import kotlin.reflect.KProperty

class SharedPrefsDataProvider(
    private val configKey: String,
    private val sharedPreferences: SharedPreferences
) {

    operator fun getValue(o: Any, property: KProperty<*>): String {
        return sharedPreferences.getString(configKey, "") ?: ""
    }

    operator fun setValue(o: Any, property: KProperty<*>, value: String) {
        sharedPreferences.edit().putString(configKey, value).apply()
    }

}