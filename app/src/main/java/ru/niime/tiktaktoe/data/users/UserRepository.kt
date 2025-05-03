package ru.niime.tiktaktoe.data.users

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.prefs.Preferences

class UserRepository(
    private val prefs: SharedPreferences
) {
    suspend fun getUserId(): String = withContext(Dispatchers.IO) {
        val existing = prefs.getString(KEY_PLAYER_ID, null)

        if (existing != null) {
            existing
        } else {
            val newId = UUID.randomUUID().toString()
            prefs.edit().putString(KEY_PLAYER_ID, newId).apply()
            newId
        }
    }

    companion object {
        private const val KEY_PLAYER_ID = "player_id"
    }
}