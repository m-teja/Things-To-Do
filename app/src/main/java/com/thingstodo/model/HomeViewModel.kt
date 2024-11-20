package com.thingstodo.model

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.google.android.libraries.places.api.model.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class HomeViewModel : ViewModel() {
    private val _optionItemList = MutableStateFlow<List<OptionItem>>(emptyList())
    val optionItemList: StateFlow<List<OptionItem>> = _optionItemList.asStateFlow()

    fun updateOptionItemList(list: List<OptionItem>) {
        _optionItemList.value = list
    }
}

data class OptionItem(
    val activity: String = "",
    val category: String = "",
    val icon: ImageVector? = null
)