package com.teja_app_productions_things_to_do.utils

import android.content.Context
import com.google.gson.Gson
import com.teja_app_productions_things_to_do.data.OptionData
import java.io.IOException
import java.io.InputStream


object JsonParserUtil {
    fun getOptionItems(context: Context): OptionData {
        val json: String? =
            inputStreamToString(context.resources.openRawResource(com.teja_app_productions_things_to_do.R.raw.options))
        val model = Gson().fromJson(json, OptionData::class.java)     // No need to add TypeAdapter

        return model
    }

    private fun inputStreamToString(inputStream: InputStream): String? {
        try {
            val bytes = ByteArray(inputStream.available())
            inputStream.read(bytes, 0, bytes.size)
            val json = String(bytes)
            return json
        } catch (e: IOException) {
            return null
        }
    }
}