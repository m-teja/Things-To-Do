package com.thingstodo.model

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.SearchByTextRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Arrays


class SearchViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow(Search())
    val searchQuery: StateFlow<Search> = _searchQuery.asStateFlow()

    private val _userLocation = MutableStateFlow(LatLng(0.0, 0.0))
    val userLocation: StateFlow<LatLng> = _userLocation.asStateFlow()

    fun updateSearchQuery(search : Search) {
        _searchQuery.value = search
    }

    fun updateUserLocation(latLng: LatLng) {
        _userLocation.value = latLng
    }

//    private fun getPlacesOfInterest(userLatLng: LatLng) {
//        // Specify the list of fields to return.
//        val placeFields = listOf(Place.Field.ID, Place.Field.DISPLAY_NAME)
//
//        // Define latitude and longitude coordinates of the search area.
//        val southWest = LatLng(37.38816277477739, -122.08813770258874)
//        val northEast = LatLng(37.39580487866437, -122.07702325966572)
//
//        // Use the builder to create a SearchByTextRequest object.
//        val searchByTextRequest = SearchByTextRequest.builder("Spicy Vegetarian Food", placeFields)
//            .setMaxResultCount(10)
//            .setLocationRestriction(CircularBounds.newInstance(userLatLng, searchQuery.value.radius.toDouble())).build()
//
//        // Call PlacesClient.searchByText() to perform the search.
//        // Define a response handler to process the returned List of Place objects.
//        placesClient.searchByText(searchByTextRequest)
//            .addOnSuccessListener { response ->
//                val places: List<Place> = response.getPlaces()
//            }
//    }

}

data class Search(
    val query : String = "",
    val radius : Int = 0,
    //TODO add marked items
)