package com.teja_app_productions_things_to_do.utils

import android.content.Context

object SharedPreferencesUtil {
    const val DELETE_KEY = "THINGS_TO_DO_DELETED_ITEMS"
    const val FILTER_KEY = "THINGS_TO_DO_FILTERED_CATEGORIES"
    private const val DARK_MODE_MAP = "THINGS_TO_DO_DARK_MODE_MAP"
    private const val IS_FIRST_TIME = "THINGS_TO_DO_IS_FIRST_TIME"
    private const val NUM_MAP_VISIT = "THINGS_TO_DO_NUM_MAP_VISIT"

    const val MAP_VISIT_COUNT_BEFORE_ADS = 8
    const val DEFAULT_RADIUS = 100

    fun isDarkModeMap(context: Context): Boolean {
        val sharedPreferences =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
                ?: return true
        return sharedPreferences.getBoolean(DARK_MODE_MAP, true)
    }

    fun setDarkModeMap(context: Context, isDarkMode: Boolean) {
        val sharedPreferences =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context) ?: return
        with(sharedPreferences.edit()) {
            putBoolean(DARK_MODE_MAP, isDarkMode)
            apply()
        }
    }

    fun isFirstTime(context: Context): Boolean {
        val sharedPreferences =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
                ?: return true
        return sharedPreferences.getBoolean(IS_FIRST_TIME, true)
    }

    fun setFirstTime(context: Context, isFirstTime: Boolean) {
        val sharedPreferences =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context) ?: return
        with(sharedPreferences.edit()) {
            putBoolean(IS_FIRST_TIME, isFirstTime)
            apply()
        }
    }

    fun getNumMapVisit(context: Context): Int {
        val sharedPreferences =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
                ?: return 0
        return sharedPreferences.getInt(NUM_MAP_VISIT, 0)
    }

    fun incrementNumMapVisit(context: Context) {
        val sharedPreferences =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context) ?: return

        println(sharedPreferences.getInt(NUM_MAP_VISIT, MAP_VISIT_COUNT_BEFORE_ADS))
        with(sharedPreferences.edit()) {
            putInt(NUM_MAP_VISIT, sharedPreferences.getInt(NUM_MAP_VISIT, MAP_VISIT_COUNT_BEFORE_ADS) + 1)
            apply()
        }
    }

    fun resetNumMapVisit(context: Context) {
        val sharedPreferences =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context) ?: return

        println(sharedPreferences.getInt(NUM_MAP_VISIT, MAP_VISIT_COUNT_BEFORE_ADS))
        with(sharedPreferences.edit()) {
            putInt(NUM_MAP_VISIT, 0)
            apply()
        }
    }
}