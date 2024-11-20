package com.thingstodo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thingstodo.model.HomeViewModel
import com.thingstodo.model.OptionItem
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = viewModel()) {
    val testList = listOf(
        OptionItem("activity1", "category1", Icons.Filled.ThumbUp),
        OptionItem("activity2", "category2", Icons.Filled.Star),
        OptionItem("activity3", "category3", Icons.Filled.Add),
        OptionItem("activity4", "category4", Icons.Filled.Face),
        OptionItem("activity5", "category5", Icons.Filled.Email),
    )
    homeViewModel.updateOptionItemList(testList)
    OptionList(homeViewModel.optionItemList.value)
}

@Composable
fun OptionList(optionItems: List<OptionItem>) {
    LazyColumn (
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ){
        items(optionItems) { optionItem ->
            Option(optionItem)

        }
    }
}

@Composable
fun Option(optionItem: OptionItem) {
    Card (
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(optionItem.activity)
        Text(optionItem.category)
        optionItem.icon?.let { Icon(it, optionItem.activity) }
    }
}