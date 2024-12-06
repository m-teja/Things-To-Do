package com.thingstodo.screens

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources.Theme
import android.location.Location
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.thingstodo.R
import com.thingstodo.model.MapViewModel
import com.thingstodo.model.MapViewModelFactory
import com.thingstodo.model.Search
import com.thingstodo.ui.AppTheme
import com.thingstodo.utils.SharedPreferencesUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.round

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
    val context = LocalContext.current
    var hasSetInitialLocation by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, 12f)
    }
    val coroutineScope = rememberCoroutineScope()
    var cameraBounds by rememberSaveable {
        mutableStateOf(LatLngBounds(LatLng(0.0, 0.0), LatLng(0.0, 0.0)))
    }

    val colorScheme = if (SharedPreferencesUtil.isDarkModeMap(context)) {
        ComposeMapColorScheme.DARK
    } else {
        ComposeMapColorScheme.LIGHT
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        mapColorScheme = colorScheme,
        properties = MapProperties(isMyLocationEnabled = isLocationGranted(context)),
        onMapLoaded = {
            if (!isLocationGranted(context)) {
                coroutineScope.launch {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 0f)
                }
            } else if (placesOfInterest.isNotEmpty() && !hasSetInitialLocation) {
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
            val address = it.formattedAddress

            if (location != null && title != null) {
                val results = FloatArray(3)
                Location.distanceBetween(
                    userLocation.latitude,
                    userLocation.longitude,
                    location.latitude,
                    location.longitude,
                    results
                )
                val distanceBetween = ((results[0] / 100).toInt()) / 10.0

                MarkerInfoWindow(
                    state = MarkerState(location),
                    title = it.displayName,
                    anchor = Offset(0.5f, 0.0f),
                    onInfoWindowClick = {
                        val gmmIntentUri =
                            Uri.parse("geo:0,0?q=$address")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        mapIntent.resolveActivity(context.packageManager)?.let {
                            context.startActivity(mapIntent)
                        }
                    }
                ) { marker: Marker ->
                    Column (
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            elevation = CardDefaults.cardElevation(4.dp),
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = marker.title ?: "",
                                    fontSize = 18.sp,
                                )

                                Text(
                                    text = "$distanceBetween km away",
                                    fontSize = 14.sp
                                )

                                Row (
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "View directions on Google Maps",
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontSize = 14.sp,
                                    )

                                    Icon (
                                        modifier = Modifier.padding(start = 4.dp),
                                        imageVector = ImageVector.vectorResource(R.drawable.link_search_icon),
                                        contentDescription = "link search"
                                    )
                                }

                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(15.dp) // Adjust arrow size
                                .clip(
                                    GenericShape { size, _ ->
                                        moveTo(0f, 0f) // Top left
                                        lineTo(size.width / 2f, size.height) // Bottom center
                                        lineTo(size.width, 0f) // Top right
                                    }
                                )
                                .background(color = CardDefaults.cardColors().containerColor)
                        )
                    }
                }

                latLngBuilder.include(location)
            }
        }
        cameraBounds = latLngBuilder.build()
    }
}