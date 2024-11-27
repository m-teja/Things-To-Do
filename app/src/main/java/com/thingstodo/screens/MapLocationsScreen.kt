package com.thingstodo.screens

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

@Composable
fun UserLocationRequest(
    fusedLocationClient: FusedLocationProviderClient,
    userLocation: LatLng,
    updateUserLocation: (LatLng) -> Unit
) {
    val context = LocalContext.current

    val onSuccessListener = OnSuccessListener<Location> {
        it?.let {
            // Update the user's location in the state
            val userLatLng = LatLng(it.latitude, it.longitude)
            updateUserLocation(userLatLng)
        }
    }

    // Handle permission requests for accessing fine location
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Fetch the user's location and update the camera if permission is granted
            fetchUserLocation(context, fusedLocationClient, onSuccessListener)
        } else {
            // Handle the case when permission is denied
            Toast.makeText(context, "Location permission is not granted.", Toast.LENGTH_LONG).show()
        }
    }

    // Request the location permission when the composable is launched
    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            // Check if the location permission is already granted
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // Fetch the user's location and update the camera
                if (userLocation.latitude == 0.0 && userLocation.longitude == 0.0) {
                    fetchUserLocation(context, fusedLocationClient, onSuccessListener)
                }
            }
            else -> {
                // Request the location permission if it has not been granted
                permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
}

private fun fetchUserLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    onSuccessListener: OnSuccessListener<Location>
) {
    // Check if the location permission is granted
    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        try {
            // Fetch the last known location
            fusedLocationClient.lastLocation.addOnSuccessListener(onSuccessListener)
        } catch (e: SecurityException) {
            Toast.makeText(context, "Permission for location access was revoked: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    } else {
        Toast.makeText(context, "Location permission is not granted.", Toast.LENGTH_LONG).show()
    }
}