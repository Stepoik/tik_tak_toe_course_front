package ru.niime.tiktaktoe.di

import android.content.Context
import org.koin.dsl.module
import ru.niime.tiktaktoe.data.network.provideHttpClient
import ru.niime.tiktaktoe.data.users.UserRepository
import ru.niime.tiktaktoe.ui.game.gameModule
import ru.niime.tiktaktoe.ui.lobbies.gameListModule

private const val SHARED_PREFS = "shared_prefs"

val networkModule = module {
    single { provideHttpClient() }
}

val rootModule = module {
    single { get<Context>().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE) }
    single { UserRepository(get()) }

    includes(
        networkModule,
        gameModule,
        gameListModule
    )
}