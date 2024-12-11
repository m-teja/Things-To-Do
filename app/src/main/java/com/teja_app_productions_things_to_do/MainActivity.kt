package com.teja_app_productions_things_to_do

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.teja_app_productions_things_to_do.data.HomeRoute
import com.teja_app_productions_things_to_do.data.MapRoute
import com.teja_app_productions_things_to_do.data.ScreenLevelRoute
import com.teja_app_productions_things_to_do.data.SettingsRoute
import com.teja_app_productions_things_to_do.model.HomeViewModel
import com.teja_app_productions_things_to_do.model.MapViewModel
import com.teja_app_productions_things_to_do.model.MapViewModelFactory
import com.teja_app_productions_things_to_do.model.Search
import com.teja_app_productions_things_to_do.screens.HomeScreen
import com.teja_app_productions_things_to_do.screens.MapScreen
import com.teja_app_productions_things_to_do.screens.SettingsScreen
import com.teja_app_productions_things_to_do.screens.UserLocationRequest
import com.teja_app_productions_things_to_do.screens.isLocationGranted
import com.teja_app_productions_things_to_do.ui.AppTheme
import com.teja_app_productions_things_to_do.utils.AdsUtil
import com.teja_app_productions_things_to_do.utils.AdsUtil.TEST_DEVICE_HASHED_ID
import com.teja_app_productions_things_to_do.utils.ManifestUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var placesClient: PlacesClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme {
                initPlaces()
                initAds()
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
                Toast.makeText(context, "Location permissions were not granted.", Toast.LENGTH_LONG)
                    .show()
            }
        })
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            Scaffold(bottomBar = { MainNavigationBar(navController) }) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = HomeRoute,
                    enterTransition = { fadeIn() },
                    exitTransition = { fadeOut() },
                    popEnterTransition = { fadeIn() },
                    popExitTransition = { fadeOut() },
                    modifier = Modifier
                        .padding(padding)
                ) {
                    homeViewModel.updateCurrentSearch(context, "")
                    composable<HomeRoute> {
                        HomeScreen(
                            homeViewModel = homeViewModel,
                            onNavigateToMapScreen = { query, radius ->
                                if (!mapViewModel.searchQuery.equals(query) && isLocationGranted(
                                        context
                                    )
                                ) {
                                    mapViewModel.updateSearchQuery(Search(query, radius))
                                    placesClient?.let { placesClient ->
                                        mapViewModel.updatePlacesOfInterest(placesClient)
                                    }
                                }

                                navController.navigate(route = MapRoute(query, radius)) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            paddingValues = padding
                        )
                    }
                    composable<MapRoute> {
                        MapScreen(mapViewModel = mapViewModel)
                    }
                    composable<SettingsRoute> {
                        SettingsScreen(
                            homeViewModel::updateCurrentFilter,
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
            ScreenLevelRoute(
                name = "Map",
                route = MapRoute("", 0),
                icon = Icons.Outlined.LocationOn
            ),
            ScreenLevelRoute(
                name = "Settings",
                route = SettingsRoute,
                icon = Icons.Outlined.Settings
            )
        )

        var debounceState by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }

        NavigationBar {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            screenLevelRoutes.forEach { screenLevelRoute ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            screenLevelRoute.icon,
                            contentDescription = screenLevelRoute.name
                        )
                    },
                    label = { Text(screenLevelRoute.name) },
                    selected = currentDestination?.hierarchy?.any { it.hasRoute(screenLevelRoute.route::class) } == true,
                    onClick = {

                        if (
                            System.currentTimeMillis() - debounceState > 500L &&
                            currentDestination?.hierarchy?.any { it.hasRoute(screenLevelRoute.route::class) } == false
                        ) {
                            debounceState = System.currentTimeMillis()
                            navController.navigate(screenLevelRoute.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
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
        val fusedLocationClient =
            remember { LocationServices.getFusedLocationProviderClient(context) }
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

    private fun initAds() {
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@MainActivity) {}
        }

        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(listOf(TEST_DEVICE_HASHED_ID)).build()
        )

        AdsUtil.loadMapInterstitial(this)
    }
}