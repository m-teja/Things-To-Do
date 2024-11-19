package com.thingstodo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.maps.android.compose.GoogleMap
import com.thingstodo.model.SearchViewModel

@Preview
@Composable
fun MapScreenPreview() {
    MapScreen()
}

@Composable
fun MapScreen(searchViewModel: SearchViewModel = viewModel()) {
    val searchState by searchViewModel.searchQuery.collectAsState()

    GoogleMap(
        modifier = Modifier.fillMaxSize()
    )
}