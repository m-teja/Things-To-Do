package com.thingstodo

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.android.libraries.places.api.Places
import com.thingstodo.data.HomeRoute
import com.thingstodo.data.MapRoute
import com.thingstodo.data.ScreenLevelRoute
import com.thingstodo.data.SettingsRoute
import com.thingstodo.model.Search
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
    private fun MainScreen() {
        val navController = rememberNavController()

        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            Scaffold(bottomBar = { MainNavigationBar(navController) }) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = HomeRoute,
                    modifier = Modifier.padding(padding),
                ) {
                    composable<HomeRoute> {
                        HomeScreen(
                            onNavigateToMapScreen = { query, radius ->
                                navController.navigate(route = MapRoute(query, radius))
                            }
                        )
                    }
                    composable<MapRoute> { backStackEntry ->
                        val mapRoute: MapRoute = backStackEntry.toRoute()
                        MapScreen(Search(mapRoute.query, mapRoute.radius))
                    }
                    composable<SettingsRoute> {
                        Text("settings")
                    }
                }
            }
        }
    }

    @Composable
    private fun MainNavigationBar(navController: NavHostController) {
        val screenLevelRoutes = listOf(
            ScreenLevelRoute(name = "Home", route = HomeRoute, icon = Icons.Outlined.Home),
            ScreenLevelRoute(name = "Map", route = MapRoute("mcdonalds", 10), icon = Icons.Outlined.LocationOn),
            ScreenLevelRoute(name = "Settings", route = SettingsRoute, icon =  Icons.Outlined.Settings)
        )

        NavigationBar {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            screenLevelRoutes.forEach { screenLevelRoute ->
                NavigationBarItem (
                    icon = { Icon(screenLevelRoute.icon, contentDescription = screenLevelRoute.name) },
                    label = { Text(screenLevelRoute.name) },
                    selected = currentDestination?.hierarchy?.any { it.hasRoute(screenLevelRoute.route::class) } == true,
                    onClick = {
                        navController.navigate(screenLevelRoute.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    }

    private fun initPlaces() {
        val apiKey = ManifestUtils.getApiKeyFromManifest(this)
        if (!Places.isInitialized() && apiKey != null) {
            Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)
        }
    }
}