package com.project

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.base.BaseActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen()
        }
    }

    @Preview
    @Composable
    private fun screenPreview() {
        MainScreen()
    }

    @Composable
    private fun MainScreen() {
        val tabBarItems = listOf(TabBarItem.homeTab, TabBarItem.mapTab, TabBarItem.settingsTab)

        val navController = rememberNavController()

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(bottomBar = { MainNavigationBar(tabBarItems, navController) }) { padding ->
                NavHost(navController = navController, startDestination = TabBarItem.homeTab.title, modifier = Modifier.padding(padding)) {
                    composable(TabBarItem.homeTab.title) {
                        Text(TabBarItem.homeTab.title)
                    }
                    composable(TabBarItem.mapTab.title) {
                        Text(TabBarItem.mapTab.title)
                    }
                    composable(TabBarItem.settingsTab.title) {
                        Text(TabBarItem.settingsTab.title)
                    }
                }
            }
        }
    }

    @Composable
    private fun MainNavigationBar(tabBarItems: List<TabBarItem>, navController: NavHostController) {
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
                            if (selectedTabIndex != index) {
                                selectedTabIndex = index
                                navController.navigate(tabBarItem.title)
                            }
                        },
                        icon = { Icon(
                            imageVector = if (selectedTabIndex == index) tabBarItem.iconSelected else tabBarItem.iconUnSelected,
                            contentDescription = tabBarItem.title
                        )},
                    )
                }
            }
        )
    }
}