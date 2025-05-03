package ru.niime.tiktaktoe.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module

data object KoinSDK {
    fun startDI(applicationModule: Module) {
        startKoin {
            modules(applicationModule, rootModule)
        }
    }
}