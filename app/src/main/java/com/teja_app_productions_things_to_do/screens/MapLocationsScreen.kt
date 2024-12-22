package com.teja_app_productions_things_to_do.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener

@Composable
fun UserLocationRequest(
    fusedLocationClient: FusedLocationProviderClient,
    updateUserLocation: (LatLng) -> Unit,
    onFinished: (Boolean) -> Unit
) {
    val context = LocalContext.current

    val onSuccessListener = OnSuccessListener<Location> {
        it?.let {
            // Update the user's location in the state
            val userLatLng = LatLng(it.latitude, it.longitude)
            println(userLatLng)
            updateUserLocation(userLatLng)
            onFinished(true)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Fetch the user's location and update the camera if permission is granted
            fetchUserLocation(context, fusedLocationClient, onSuccessListener, onFinished)
            onFinished(true)
        } else {
            // Handle the case when permission is denied
            onFinished(false)
        }
    }

    LaunchedEffect (Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            // Check if the location permission is already granted
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> {
                fetchUserLocation(context, fusedLocationClient, onSuccessListener, onFinished)
            }

            else -> {
                // Request the location permission if it has not been granted
                permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }
    }

}

fun isLocationGranted(context: Context): Boolean {
    return (ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED)
}

private fun fetchUserLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    onSuccessListener: OnSuccessListener<Location>,
    onFinished: (Boolean) -> Unit
) {
    // Check if the location permission is granted
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        try {
            // Fetch the last known location
            fusedLocationClient.lastLocation.addOnSuccessListener(onSuccessListener)
        } catch (e: SecurityException) {
            onFinished(false)
        }
    } else {
        onFinished(false)
    }
}