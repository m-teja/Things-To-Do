package com.teja_app_productions_things_to_do.screens

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.model.Place
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.teja_app_productions_things_to_do.R
import com.teja_app_productions_things_to_do.model.MapViewModel
import com.teja_app_productions_things_to_do.model.MapViewModelFactory
import com.teja_app_productions_things_to_do.model.Search
import com.teja_app_productions_things_to_do.ui.AppTheme
import com.teja_app_productions_things_to_do.utils.AdsUtil
import com.teja_app_productions_things_to_do.utils.SharedPreferencesUtil
import kotlinx.coroutines.flow.StateFlow
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
        factory = MapViewModelFactory(Search())
    )
) {

    LoadInterstitialAdIfApplicable()
    Map(
        userLocationState = mapViewModel.userLocation,
        placesOfInterestState = mapViewModel.placesOfInterest,
        searchQuery = mapViewModel.searchQuery.collectAsState().value.query
    )
}

@Composable
fun LoadInterstitialAdIfApplicable() {
    val context = LocalContext.current
    if (SharedPreferencesUtil.getNumMapVisit(context) >= SharedPreferencesUtil.MAP_VISIT_COUNT_BEFORE_ADS) {
        AdsUtil.showMapInterstitial(context as Activity, onAdDismissed = {})
        SharedPreferencesUtil.resetNumMapVisit(context)
    } else {
        SharedPreferencesUtil.incrementNumMapVisit(context)
    }
}

@Composable
fun Map(
    userLocationState: StateFlow<LatLng>,
    placesOfInterestState: StateFlow<List<Place>>,
    searchQuery: String
) {
    val userLocation = userLocationState.collectAsStateWithLifecycle().value
    val placesOfInterest = placesOfInterestState.collectAsStateWithLifecycle().value

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var hasSetInitialLocation by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
        position = if (userLocation.latitude == 0.0 && userLocation.longitude == 0.0)
            CameraPosition.fromLatLngZoom(userLocation, 0f)
        else
            CameraPosition.fromLatLngZoom(userLocation, 12f)
    }

    var cameraBounds by rememberSaveable {
        mutableStateOf(LatLngBounds(LatLng(0.0, 0.0), LatLng(0.0, 0.0)))
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        mapColorScheme = if (SharedPreferencesUtil.isDarkModeMap(context)) {
            ComposeMapColorScheme.DARK
        } else {
            ComposeMapColorScheme.LIGHT
        },
        properties = MapProperties(isMyLocationEnabled = isLocationGranted(context)),
        onMapLoaded = {
            if (placesOfInterest.isEmpty() && searchQuery.isNotEmpty()) {
                Toast.makeText(
                    context,
                    "No results found, check your location settings and try a different search query",
                    Toast.LENGTH_LONG
                ).show()
            } else if (isLocationGranted(context) && placesOfInterest.isNotEmpty() && !hasSetInitialLocation) {
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
                    Column(
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

                                Row(
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "View directions on Google Maps",
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontSize = 14.sp,
                                    )

                                    Icon(
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

                val dummyLocation = LatLng(
                    userLocation.latitude - (location.latitude - userLocation.latitude),
                    userLocation.longitude - (location.longitude - userLocation.longitude)
                )
                latLngBuilder.include(dummyLocation)
                latLngBuilder.include(location)
            }

        }
        cameraBounds = latLngBuilder.build()
    }
}