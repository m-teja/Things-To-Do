package com.thingstodo.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.thingstodo.model.HomeViewModel.Companion.DELETE_KEY
import com.thingstodo.ui.AppTheme

@Preview
@Composable
fun SettingsScreenPreview() {
    AppTheme {
        SettingsScreen {}
    }
}

@Composable
fun SettingsScreen(
    updateCurrentOptionItemList: (Context) -> Unit
) {
    val context = LocalContext.current

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = {
                val sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
                with(sharedPreferences.edit()) {
                    putStringSet(DELETE_KEY, setOf())
                    apply()
                }

                updateCurrentOptionItemList(context)

                Toast.makeText(context, "Activity list has been reset", Toast.LENGTH_LONG).show()
            }
        ) {
            Text("Reset activity list")
        }

//        AndroidView(
//            modifier = Modifier.fillMaxWidth(),
//            factory = { context ->
//                AdView(context).apply {
//                    setAdSize(AdSize.BANNER)
//                    adUnitId = context.getString(R.string.ad_id_banner)
//                    loadAd(AdRequest.Builder().build())
//                }
//            }
//        )
    }

}