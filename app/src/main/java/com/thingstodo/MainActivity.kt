package com.thingstodo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.thingstodo.screens.HomeScreen
import com.thingstodo.screens.MapScreen
import com.thingstodo.data.HomeRoute
import com.thingstodo.data.MapRoute
import com.thingstodo.data.ScreenLevelRoute
import com.thingstodo.data.SettingsRoute
import com.thingstodo.model.HomeViewModel
import com.thingstodo.model.MapViewModel
import com.thingstodo.model.MapViewModelFactory
import com.thingstodo.model.Search
import com.thingstodo.screens.SettingsScreen
import com.thingstodo.screens.UserLocationRequest
import com.thingstodo.screens.isLocationGranted
import com.thingstodo.ui.AppTheme
import com.thingstodo.utils.ManifestUtil
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                initPlaces()
                MainScreen()
            }
        }
    }

    @Preview
    @Composable
    private fun ScreenPreview() {
        AppTheme {
            MainScreen()
        }
    }

    @Composable
    private fun MainScreen() {
        val context = LocalContext.current

        val navController = rememberNavController()
        val homeViewModel: HomeViewModel = viewModel()
        val mapViewModel: MapViewModel = viewModel(factory = MapViewModelFactory(Search()))

        RequestPermissions(mapViewModel = mapViewModel, onFinished = {
            if (!it) {
                Toast.makeText(context, "Location permissions were not granted.", Toast.LENGTH_LONG).show()
            }
        })
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            Scaffold(bottomBar = { MainNavigationBar(navController) }) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = HomeRoute,
                    modifier = Modifier
                        .consumeWindowInsets(padding)
                        .padding(padding)
                        .imePadding(),
                ) {
                    composable<HomeRoute> {
                        HomeScreen(
                            homeViewModel = homeViewModel,
                            onNavigateToMapScreen = { query, radius ->
                                if (!mapViewModel.searchQuery.equals(query) && isLocationGranted(context)) {
                                    mapViewModel.updateSearchQuery(Search(query, radius))
                                    mapViewModel.updatePlacesOfInterest(placesClient)
                                }

                                navController.navigate(route = MapRoute(query, radius)) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                    composable<MapRoute> {
                        MapScreen(mapViewModel = mapViewModel)
                    }
                    composable<SettingsRoute> {
                        SettingsScreen(
                            homeViewModel::updateCurrentOptionItemList,
                            onNavigateToHomeScreen = {
                                navController.navigate(route = HomeRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun MainNavigationBar(navController: NavHostController) {
        val screenLevelRoutes = listOf(
            ScreenLevelRoute(name = "Home", route = HomeRoute, icon = Icons.Outlined.Home),
            ScreenLevelRoute(name = "Map", route = MapRoute("", 0), icon = Icons.Outlined.LocationOn),
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
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun RequestPermissions(
        mapViewModel: MapViewModel,
        onFinished: (Boolean) -> Unit
    ) {
        val context = LocalContext.current
        val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
        val userLocation by mapViewModel.userLocation.collectAsState()

        UserLocationRequest(
            fusedLocationClient = fusedLocationClient,
            userLocation = userLocation,
            updateUserLocation = mapViewModel::updateUserLocation,
            onFinished = onFinished
        )
    }

    private fun initPlaces() {
        val apiKey = ManifestUtil.getApiKeyFromManifest(this)
        if (!Places.isInitialized() && apiKey != null) {
            Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)
            placesClient = Places.createClient(this)
        }
    }
}