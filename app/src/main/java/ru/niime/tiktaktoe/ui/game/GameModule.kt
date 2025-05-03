package ru.niime.tiktaktoe.ui.game

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.niime.tiktaktoe.data.games.GameSessionManager

val gameModule = module {
    single { GameSessionManager() }
    viewModel { params -> GameViewModel(params.get(), get(), get()) }
}