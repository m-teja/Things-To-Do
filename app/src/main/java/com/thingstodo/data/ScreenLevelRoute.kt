package com.thingstodo.data

import androidx.compose.ui.graphics.vector.ImageVector
import com.thingstodo.model.Search
import kotlinx.serialization.Serializable

data class ScreenLevelRoute<T: Any>(val name: String, val route: T, val icon: ImageVector)

@Serializable
object HomeRoute

@Serializable
data class MapRoute(val query: String, val radius: Int)

@Serializable
object SettingsRoute