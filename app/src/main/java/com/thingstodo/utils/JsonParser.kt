package com.thingstodo.utils

import android.R
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.thingstodo.datamodels.OptionData
import java.io.IOException
import java.io.InputStream




object JsonParser {
    fun getOptionItems(context: Context) : OptionData {
        val json: String? = inputStreamToString(context.resources.openRawResource(com.thingstodo.R.raw.options))
        val model = Gson().fromJson(json, OptionData::class.java)     // No need to add TypeAdapter
        Log.d("testing", model.toString())

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