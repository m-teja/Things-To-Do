package com.thingstodo.utils

import android.content.Context

object SharedPreferencesUtil {
    const val DELETE_KEY = "THINGS_TO_DO_DELETED_ITEMS"
    const val FILTER_KEY = "THINGS_TO_DO_FILTERED_CATEGORIES"
    private const val DARK_MODE_MAP = "THINGS_TO_DO_DARK_MODE_MAP"

    fun isDarkModeMap(context: Context): Boolean {
        val sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)?: return true
        return sharedPreferences.getBoolean(DARK_MODE_MAP, true)
    }

    fun setDarkModeMap(context: Context, isDarkMode: Boolean) {
        val sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)?: return
        with(sharedPreferences.edit()) {
            putBoolean(DARK_MODE_MAP, isDarkMode)
            apply()
        }
    }
}