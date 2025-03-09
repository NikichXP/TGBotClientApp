package com.nikichxp.tgbotapp

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class TGBotApp : Application() {

    private val appModule = module {
        single { ServerInfoViewModel() }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TGBotApp)
            modules(appModule)
        }
    }

}