package com.thingstodo.model

import android.content.Context
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
    }

    fun updateCurrentOptionItemList(context: Context) {
        _currentOptionItemList.clear()

        val sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)?: return
        val currentDeleteSet = sharedPreferences.getStringSet(DELETE_KEY, setOf())?.toMutableSet()

        val filteredList = _fullOptionItemList.value.filter { optionItem ->
            !(currentDeleteSet?.contains(optionItem.activity)?: true)
        }
        _currentOptionItemList.addAll(filteredList)
    }

    fun removeItem(context: Context, optionItem: OptionItem) {
        _currentOptionItemList.remove(optionItem)
        val sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)?: return
        val currentDeleteSet = sharedPreferences.getStringSet(DELETE_KEY, setOf())?.toMutableSet()

        with(sharedPreferences.edit()) {
            currentDeleteSet?.add(optionItem.activity)
            putStringSet(DELETE_KEY, currentDeleteSet)
            apply()
        }

        updateCurrentOptionItemList(context)
    }

    companion object {
        const val DELETE_KEY = "THINGS_TO_DO_DELETED_ITEMS"
    }
}

data class OptionItem(
    val activity: String = "",
    val category: String = "",
    val icon: ImageVector? = null
)