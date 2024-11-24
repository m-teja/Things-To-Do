package com.thingstodo

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thingstodo.data.Activity
import com.thingstodo.data.Category
import com.thingstodo.model.HomeViewModel
import com.thingstodo.model.OptionItem
import com.thingstodo.ui.AppTheme
import com.thingstodo.utils.JsonParser

@Preview
@Composable
fun HomeScreenPreview() {
    AppTheme {
        HomeScreen(onNavigateToMapScreen = {a, b -> })
    }
}

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    onNavigateToMapScreen: (String, Int) -> Unit
) {
    val optionItems = getOptionItemListFromJson(LocalContext.current)

    homeViewModel.updateOptionItemList(optionItems)
    OptionList(optionItems, onNavigateToMapScreen)
}

@Composable
fun OptionList(
    optionItems: List<OptionItem>, onNavigateToMapScreen: (String, Int) -> Unit
) {
    LazyColumn (
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ){
        items(optionItems) { optionItem ->
            Option(optionItem, onNavigateToMapScreen)
        }
    }
}

@Composable
fun rememberLazyListState(
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LazyListState {
    return rememberSaveable(saver = LazyListState.Saver) {
        LazyListState(
            initialFirstVisibleItemIndex, initialFirstVisibleItemScrollOffset
        )
    }
}

@Composable
fun Option(
    optionItem: OptionItem,
    onNavigateToMapScreen: (String, Int) -> Unit
) {
    Card (
        modifier = Modifier.fillMaxWidth(),
        onClick = { onNavigateToMapScreen(optionItem.activity, 10) }
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