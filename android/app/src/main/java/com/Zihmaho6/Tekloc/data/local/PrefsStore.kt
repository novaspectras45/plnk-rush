package com.Zihmaho6.Tekloc.data.local

import android.content.Context
import android.content.SharedPreferences

class PrefsStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun readInt(key: String, fallback: Int): Int = prefs.getInt(key, fallback)

    fun writeInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun readLong(key: String, fallback: Long): Long = prefs.getLong(key, fallback)

    fun writeLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    fun readBoolean(key: String, fallback: Boolean): Boolean = prefs.getBoolean(key, fallback)

    fun writeBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun readString(key: String, fallback: String): String =
        prefs.getString(key, fallback) ?: fallback

    fun writeString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        const val STORE_NAME = "plinko_rush_store"
        const val KEY_STAGE_STARS = "stage_stars"
        const val KEY_STAGE_BEST = "stage_best"
        const val KEY_BONUS_STARS = "bonus_stars"
        const val KEY_STREAK = "streak"
        const val KEY_CORE = "selected_core"
        const val KEY_TUTORIAL = "tutorial_seen"
        const val KEY_DAILY_DAY = "daily_day"
        const val KEY_DAILY_STARS = "daily_stars"
        const val KEY_DAILY_SCORE = "daily_score"
        const val KEY_TRAJECTORY = "setting_trajectory"
        const val KEY_DROP_SPEED = "setting_drop_speed"
        const val KEY_CLOCK = "setting_clock"
    }
}
