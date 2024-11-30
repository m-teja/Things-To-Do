package com.thingstodo.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class HomeViewModel : ViewModel() {
    private val _fullOptionItemList = MutableStateFlow<List<OptionItem>>(emptyList())
    val fullOptionItemList: StateFlow<List<OptionItem>> = _fullOptionItemList.asStateFlow()

    private val _currentOptionItemList = mutableStateListOf<OptionItem>()
    var currentOptionItemList: List<OptionItem> = _currentOptionItemList

    fun setFullOptionItemList(list: List<OptionItem>) {
        _fullOptionItemList.value = list
        _currentOptionItemList.clear()
        _currentOptionItemList.addAll(list)
    }

    fun removeItem(optionItem: OptionItem) {
        _currentOptionItemList.remove(optionItem)
        println(currentOptionItemList.size)
    }
}

data class OptionItem(
    val activity: String = "",
    val category: String = "",
    val icon: ImageVector? = null
)