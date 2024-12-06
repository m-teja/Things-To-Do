package com.thingstodo.utils

import android.content.Context

object SharedPreferencesUtil {
    const val DELETE_KEY = "THINGS_TO_DO_DELETED_ITEMS"
    const val FILTER_KEY = "THINGS_TO_DO_FILTERED_CATEGORIES"
    private const val MAXIMUM_NUM_RESULTS = "THINGS_TO_DO_MAXIMUM_NUM_RESULTS"

    fun getMaximumNumResults(context: Context): Int {
        val sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)?: return 5
        return sharedPreferences.getInt(MAXIMUM_NUM_RESULTS, 5)
    }

    fun setMaximumNumResults(context: Context, num: Int) {
        val sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)?: return

        with(sharedPreferences.edit()) {
            putInt(MAXIMUM_NUM_RESULTS, num)
            apply()
        }
    }
}