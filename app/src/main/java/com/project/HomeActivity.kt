package com.project

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.project.base.BaseActivity

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            screen()
        }

    }

    @Preview
    @Composable
    private fun screenPreview() {
        screen()
    }

    @Composable
    private fun screen() {
        Scaffold (
            bottomBar = {
                navigationBar()
            }
        ) { content ->
            Text(
                modifier = Modifier.padding(content),
                text = "test"
            )
        }
    }

    @Composable
    private fun navigationBar() {
        NavigationBar (
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary,
            content = {
                NavigationBarItem(
                    label = { Text(text = "Home") },
                    icon = { Icon(Icons.Filled.Home, "home") },
                    selected = false,
                    onClick = {

                    },
                    colors = NavigationBarItemDefaults.colors()
                )
                NavigationBarItem(
                    label = { Text(text = "Map") },
                    icon = { Icon(Icons.Filled.LocationOn, "map") },
                    selected = false,
                    onClick = {},
                    colors = NavigationBarItemDefaults.colors()
                )
                NavigationBarItem(
                    label = { Text(text = "Settings") },
                    icon = { Icon(Icons.Filled.Settings, "settings") },
                    selected = false,
                    onClick = {},
                    colors = NavigationBarItemDefaults.colors()
                )
            },
        )
    }
}