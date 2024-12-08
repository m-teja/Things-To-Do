package com.teja_app_productions_things_to_do.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.teja_app_productions_things_to_do.R
import com.teja_app_productions_things_to_do.ui.AppTheme
import com.teja_app_productions_things_to_do.utils.SharedPreferencesUtil
import com.teja_app_productions_things_to_do.utils.SharedPreferencesUtil.DELETE_KEY

private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741"
const val TEST_DEVICE_HASHED_ID = "37A93361FA4402FA38C27C8FDA9C5337"

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

    MobileAds.setRequestConfiguration(
        RequestConfiguration.Builder().setTestDeviceIds(listOf(TEST_DEVICE_HASHED_ID)).build()
    )

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.4f)
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
        }

        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = {
                val adView = AdView(it)

                adView.apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = AD_UNIT_ID
                    loadAd(AdRequest.Builder().build())
                }

            }
        )
    }
}
