package ru.niime.tiktaktoe.ui.common

import java.util.UUID

data class ErrorMessage(
    val message: String,
    val uuid: String = UUID.randomUUID().toString()
)
