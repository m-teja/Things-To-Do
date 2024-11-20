package com.thingstodo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.places.api.Places
import com.thingstodo.model.Search
import com.thingstodo.model.SearchViewModel
import com.thingstodo.ui.AppTheme
import com.thingstodo.utils.ManifestUtils

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        initPlaces()

        setContent {
            AppTheme {
                MainScreen()
            }
        }
    }

    @Preview
    @Composable
    private fun screenPreview() {
        AppTheme {
            MainScreen()
        }
    }

    @Composable
    private fun MainScreen(searchViewModel: SearchViewModel = viewModel()) {
        val tabBarItems = listOf(TabBarItem.HomeTab, TabBarItem.MapTab, TabBarItem.SettingsTab)

        val navController = rememberNavController()

        searchViewModel.updateSearchQuery(Search(query = "mcdonalds", radius = 10))

        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            Scaffold(bottomBar = { MainNavigationBar(tabBarItems, navController) }) { padding ->
                NavHost(navController = navController, startDestination = TabBarItem.HomeTab.route, modifier = Modifier.padding(padding)) {
                    composable(TabBarItem.HomeTab.route) {
                        HomeScreen()
                    }
                    composable(TabBarItem.MapTab.route) {
                        MapScreen(searchViewModel)
                    }
                    composable(TabBarItem.SettingsTab.route) {
                        Text(TabBarItem.SettingsTab.route)
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
            content = {
                tabBarItems.forEachIndexed { index, tabBarItem ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = {
                            if (selectedTabIndex != index) {
                                selectedTabIndex = index
                                navController.navigate(tabBarItem.route)
                            }
                        },
                        icon = { Icon(
                            imageVector = if (selectedTabIndex == index) tabBarItem.iconSelected else tabBarItem.iconUnSelected,
                            contentDescription = tabBarItem.route
                        )},
                    )
                }
            }
        )
    }

    private fun initPlaces() {
        val apiKey = ManifestUtils.getApiKeyFromManifest(this)
        if (!Places.isInitialized() && apiKey != null) {
            Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)
        }
    }
}