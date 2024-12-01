package com.thingstodo.screens

import android.content.Context
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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

    Button(
        onClick = {
            val sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            with(sharedPreferences.edit()) {
                putStringSet(DELETE_KEY, setOf())
                apply()
            }

            updateCurrentOptionItemList(context)
        }
    ) {
        Text("Reset activity list")
    }
}