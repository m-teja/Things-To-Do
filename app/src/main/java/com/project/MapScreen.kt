package com.project

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.model.Search
import com.project.model.SearchViewModel

@Composable
fun MapScreen(searchViewModel: SearchViewModel = viewModel()) {
    val searchState by searchViewModel.searchQuery.collectAsState()

    Column {
        Text(searchState.query + "\n")
        Text(searchState.radius.toString())
        Button(
            onClick = {
                searchViewModel.updateSearchQuery(Search("new", 122))
            }
        ) {
            Text("buttontest")
        }
    }

}