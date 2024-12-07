package com.thingstodo.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.thingstodo.ui.AppTheme
import com.thingstodo.utils.SharedPreferencesUtil
import com.thingstodo.utils.SharedPreferencesUtil.DELETE_KEY

@Preview
@Composable
fun SettingsScreenPreview() {
    AppTheme {
        SettingsScreen(
            updateCurrentOptionItemList = {},
            onNavigateToHomeScreen = {}
        )
    }
}

@Composable
fun SettingsScreen(
    updateCurrentOptionItemList: (Context) -> Unit,
    onNavigateToHomeScreen: () -> Unit
) {
    val context = LocalContext.current

    var isDarkModeChecked by remember { mutableStateOf(SharedPreferencesUtil.isDarkModeMap(context)) }

    Column(
        modifier = Modifier
            .fillMaxHeight(0.5f)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Enable dark mode for Map screen",
                color = Color.White
            )
            Checkbox(
                checked = isDarkModeChecked,
                onCheckedChange = {
                    SharedPreferencesUtil.setDarkModeMap(context, it)
                    isDarkModeChecked = it
                }
            )
        }

        Button(
            onClick = {
                SharedPreferencesUtil.setFirstTime(context, true)
                onNavigateToHomeScreen()
            }
        ) {
            Text("Show tutorial again")
        }

        Button(
            onClick = {
                val sharedPreferences =
                    androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
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
