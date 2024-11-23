package com.thingstodo

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thingstodo.datamodels.Activity
import com.thingstodo.datamodels.Category
import com.thingstodo.model.HomeViewModel
import com.thingstodo.model.OptionItem
import com.thingstodo.ui.AppTheme
import com.thingstodo.utils.JsonParser

@Preview
@Composable
fun HomeScreenPreview() {
    AppTheme {
        HomeScreen()
    }
}

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = viewModel()) {
    val optionItems = getOptionItemListFromJson(LocalContext.current)

    homeViewModel.updateOptionItemList(optionItems)
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
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(0.40f)
                    .padding(start = 10.dp),
                text = optionItem.activity
            )

            Text(
                modifier = Modifier.fillMaxWidth(0.40f),
                text = optionItem.category
            )

            optionItem.icon?.let {
                Icon(
                    modifier = Modifier.fillMaxWidth(),
                    imageVector = it,
                    contentDescription = optionItem.activity
                )
            }
        }
    }
}

@Composable
private fun getOptionItemListFromJson(context: Context): MutableList<OptionItem> {
    val optionItems = JsonParser.getOptionItems(context)

    val optionItemList = mutableListOf<OptionItem>()
    optionItems.categories.forEach { category: Category ->
        val id = context.resources.getIdentifier(category.icon, "drawable", context.packageName)
        val categoryIcon = ImageVector.vectorResource(id)
        category.activities.forEach { activity: Activity ->
            optionItemList.add(OptionItem(activity.name, category.categoryName, categoryIcon))
        }
    }

    return optionItemList
}