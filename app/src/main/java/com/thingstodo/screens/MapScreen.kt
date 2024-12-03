package com.thingstodo.screens

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.thingstodo.model.MapViewModel
import com.thingstodo.model.MapViewModelFactory
import com.thingstodo.model.Search
import com.thingstodo.ui.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview
@Composable
fun MapScreenPreview() {
    AppTheme {
        MapScreen()
    }
}

@Composable
fun MapScreen(
    mapViewModel: MapViewModel = viewModel(
        factory = MapViewModelFactory(Search()))
) {
    val userLocation by mapViewModel.userLocation.collectAsState()
    val placesOfInterest by mapViewModel.placesOfInterest.collectAsState()

    Map(userLocation, placesOfInterest)
}

@Composable
fun Map(
    userLocation: LatLng,
    placesOfInterest: List<Place>,
) {
    var hasSetInitialLocation by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, 15f)
    }
    val coroutineScope = rememberCoroutineScope()
    var cameraBounds by rememberSaveable {
        mutableStateOf(LatLngBounds(LatLng(0.0, 0.0), LatLng(0.0, 0.0)))
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        mapColorScheme = ComposeMapColorScheme.DARK,
        properties = MapProperties(isMyLocationEnabled = true),
        onMapLoaded = {
            if (placesOfInterest.isNotEmpty() && !hasSetInitialLocation) {
                hasSetInitialLocation = true
                coroutineScope.launch {
                    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(cameraBounds, 100)
                    cameraPositionState.animate(cameraUpdate)
                }
            }
        }
    ) {
        val latLngBuilder = LatLngBounds.builder()
        latLngBuilder.include(userLocation)

        placesOfInterest.forEach {
            val location = it.location
            val title = it.displayName

            if (location != null && title != null) {
                Marker(
                    state = MarkerState(location),
                    title = it.displayName
                )
                latLngBuilder.include(location)
            }
        }

        cameraBounds = latLngBuilder.build()
    }
}