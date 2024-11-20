package com.thingstodo.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class SearchViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow(Search())
    val searchQuery: StateFlow<Search> = _searchQuery.asStateFlow()

    private val _userLocation = MutableStateFlow(LatLng(0.0, 0.0))
    val userLocation: StateFlow<LatLng> = _userLocation.asStateFlow()

    private val _placesOfInterest = MutableStateFlow<List<Place>>(emptyList())
    val placesOfInterest: StateFlow<List<Place>> = _placesOfInterest.asStateFlow()

    fun updateSearchQuery(search : Search) {
        _searchQuery.value = search
    }

    fun updateUserLocation(latLng: LatLng) {
        _userLocation.value = latLng
    }

    fun updatePlacesOfInterest(userLatLng: LatLng, placesClient: PlacesClient) {
        // Specify the list of fields to return.
        val placeFields = listOf(Place.Field.DISPLAY_NAME, Place.Field.LOCATION)

        // Use the builder to create a SearchByTextRequest object.
        val searchByTextRequest = SearchByTextRequest.builder(searchQuery.value.query, placeFields)
            .setMaxResultCount(5)
            .setLocationBias(CircularBounds.newInstance(userLatLng, searchQuery.value.radius.toDouble())).build()

        // Call PlacesClient.searchByText() to perform the search.
        // Define a response handler to process the returned List of Place objects.
        placesClient.searchByText(searchByTextRequest)
            .addOnSuccessListener { response ->
                _placesOfInterest.value = response.places
            }
    }

}

data class Search(
    val query : String = "",
    val radius : Int = 0,
    //TODO add marked items
)