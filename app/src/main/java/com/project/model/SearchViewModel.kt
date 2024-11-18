package com.project.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class SearchViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow(Search())
    val searchQuery: StateFlow<Search> = _searchQuery.asStateFlow()

    fun updateSearchQuery(search : Search) {
        _searchQuery.value = search
    }
}

data class Search(
    val query : String = "",
    val radius : Int = 0,
    //TODO add marked items
)