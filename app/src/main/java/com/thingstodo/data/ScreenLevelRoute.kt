package com.thingstodo.data

import androidx.compose.ui.graphics.vector.ImageVector
import com.thingstodo.model.Search
import kotlinx.serialization.Serializable

data class ScreenLevelRoute<T: Any>(val name: String, val route: T, val iconSelected: ImageVector, val iconUnSelected: ImageVector)

//data object HomeTab : ScreenLevelRoute<Home>(name = "Home", route = Home, iconSelected = Icons.Filled.Home, iconUnSelected = Icons.Outlined.Home)
//data object MapTab : ScreenLevelRoute(name = "Map", Map, iconSelected = Icons.Filled.LocationOn, iconUnSelected = Icons.Outlined.LocationOn)
//data object SettingsTab : ScreenLevelRoute(name = "Settings", Settings, iconSelected = Icons.Filled.Settings, iconUnSelected =  Icons.Outlined.Settings)

@Serializable
object HomeRoute

@Serializable
data class MapRoute(val query: String, val radius: Int)

@Serializable
object SettingsRoute