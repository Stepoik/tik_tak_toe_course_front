package ru.niime.tiktaktoe

import android.app.Application
import android.content.Context
import org.koin.core.context.startKoin
import org.koin.dsl.module
import ru.niime.tiktaktoe.di.KoinSDK
import ru.niime.tiktaktoe.di.rootModule

class TikTakToeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val applicationModule = module {
            single<Context> { this@TikTakToeApplication }
        }
        KoinSDK.startDI(applicationModule)
    }
}