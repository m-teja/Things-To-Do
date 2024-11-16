package com.project

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class TabBarItem(val title: String, val iconSelected: ImageVector, val iconUnSelected: ImageVector) {
    object homeTab : TabBarItem(title = "Home", iconSelected = Icons.Filled.Home, iconUnSelected = Icons.Outlined.Home)
    object mapTab : TabBarItem(title = "Map", iconSelected = Icons.Filled.LocationOn, iconUnSelected = Icons.Outlined.LocationOn)
    object settingsTab : TabBarItem(title = "Settings", iconSelected = Icons.Filled.Settings, iconUnSelected =  Icons.Outlined.Settings)

}