package ru.niime.tiktaktoe.ui.lobbies

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.niime.tiktaktoe.data.lobbies.GameRepository

val gameListModule = module {
    single { GameRepository(get()) }

    viewModel { GameListViewModel(get()) }
}