package com.thingstodo.data

import com.thingstodo.model.HomeViewModel
import com.thingstodo.model.MapViewModel
import kotlinx.serialization.Serializable

@Serializable
data class Home(val homeViewModel: HomeViewModel)

@Serializable
data class Map(val mapViewModel: MapViewModel)