package com.thingstodo.utils

import android.content.Context
import android.content.pm.PackageManager

object ManifestUtils {
    fun getApiKeyFromManifest(context: Context): String? {
        return try {
            val applicationInfo = context.packageManager
                .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            val bundle = applicationInfo.metaData
            bundle.getString("com.google.android.geo.MAPS_API_KEY")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}