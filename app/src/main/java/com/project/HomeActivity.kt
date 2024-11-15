package com.project

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
        val homeTab = TabBarItem(title = "Home", iconSelected = Icons.Filled.Home, iconUnSelected = Icons.Outlined.Home)
        val mapTab = TabBarItem(title = "Map", iconSelected = Icons.Filled.LocationOn, iconUnSelected = Icons.Outlined.LocationOn)
        val settingsTab = TabBarItem(title = "Settings", iconSelected = Icons.Filled.Settings, iconUnSelected =  Icons.Outlined.Settings)

        val tabBarItems = listOf(homeTab, mapTab, settingsTab)

        val navController = rememberNavController()

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(bottomBar = { navigationBar(tabBarItems, navController) }) { padding ->
                NavHost(navController = navController, startDestination = homeTab.title, modifier = Modifier.padding(padding)) {
                    composable(homeTab.title) {
                        Text(homeTab.title)
                    }
                    composable(mapTab.title) {
                        Text(mapTab.title)
                    }
                    composable(settingsTab.title) {
                        Text(settingsTab.title)
                    }
                }
            }
        }
    }

    @Composable
    private fun navigationBar(tabBarItems: List<TabBarItem>, navController: NavHostController) {
        var selectedTabIndex by rememberSaveable {
            mutableIntStateOf(0)
        }

        NavigationBar (
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary,
            content = {
                tabBarItems.forEachIndexed { index, tabBarItem ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            navController.navigate(tabBarItem.title)
                        },
                        icon = { Icon(
                            imageVector = if (selectedTabIndex == index) tabBarItem.iconSelected else tabBarItem.iconUnSelected,
                            contentDescription = tabBarItem.title
                        )}
                    )
                }
            }
        )
    }
}

data class TabBarItem(
    val title: String,
    val iconSelected: ImageVector,
    val iconUnSelected: ImageVector
)