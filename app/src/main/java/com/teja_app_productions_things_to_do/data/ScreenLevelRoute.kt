package com.teja_app_productions_things_to_do.data

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

data class ScreenLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)

@Serializable
object HomeRoute

@Serializable
data class MapRoute(val query: String, val radius: Int)

@Serializable
object SettingsRoute