package com.thingstodo.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thingstodo.R
import com.thingstodo.data.Activity
import com.thingstodo.data.Category
import com.thingstodo.model.HomeViewModel
import com.thingstodo.model.OptionItem
import com.thingstodo.ui.AppTheme
import com.thingstodo.utils.JsonParserUtil
import com.thingstodo.utils.SharedPreferencesUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.floor

@Preview
@Composable
fun HomeScreenPreview() {
    AppTheme {
        HomeScreen(onNavigateToMapScreen = {_, _ -> })
    }
}

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    onNavigateToMapScreen: (String, Int) -> Unit
) {
    getOptionItemListFromJson(LocalContext.current, homeViewModel::setFullOptionItemList)
    homeViewModel.updateCurrentOptionItemList(LocalContext.current)

    val currentOptionItemList = homeViewModel.currentOptionItemList

    OptionList(
        optionItems = currentOptionItemList,
        onNavigateToMapScreen = onNavigateToMapScreen,
        removeItem = homeViewModel::removeItem,
        addItem = homeViewModel::addItem,
        updateCurrentFilter = homeViewModel::updateCurrentFilter,
        updateCurrentSearch = homeViewModel::updateCurrentSearch
    )
}

@Composable
fun OptionList(
    optionItems: List<OptionItem>,
    onNavigateToMapScreen: (String, Int) -> Unit,
    removeItem: (Context, OptionItem) -> Unit,
    addItem: (Context, OptionItem) -> Unit,
    updateCurrentFilter: (Context, Set<String>) -> Unit,
    updateCurrentSearch: (Context, String) -> Unit
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val undoSnackBarHostState = remember { SnackbarHostState() }

    var scrollToIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showTutorialDialog by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }

    if (showFilterDialog) {
        FilterDialog(onClose = {
            showFilterDialog = false
            updateCurrentFilter(context, it)
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f, fill = false)
                .padding(bottom = 6.dp),
            state = listState,
        ) {
            itemsIndexed(
                optionItems,
                key = { _, optionItem ->
                    optionItem.activity
                }
            ) { index, optionItem ->

                Column (
                    modifier = Modifier.animateItem(),
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val isStartOfCategory = (index == 0 || optionItems[index - 1].category != optionItem.category)
                    if (isStartOfCategory) {
                        Text(
                            modifier = Modifier.padding(top = 10.dp),
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            text = optionItem.category.uppercase()
                        )
                        HorizontalDivider(thickness = 2.dp)
                    }

                    Option(
                        optionItem = optionItem,
                        isHighlightedAnimation = (index == scrollToIndex),
                        onNavigateToMapScreen = onNavigateToMapScreen,
                        resetHighlightIndex = {
                            scrollToIndex = null
                        },
                        onDelete = {
                            removeItem(context, optionItem)
                            scrollToIndex = null
                            coroutineScope.launch {
                                undoSnackBarHostState.currentSnackbarData?.dismiss()
                                val result = undoSnackBarHostState.showSnackbar(
                                    message = "Deleted " + optionItem.activity,
                                    actionLabel = "Undo",
                                    duration = SnackbarDuration.Short
                                )

                                if (result == SnackbarResult.ActionPerformed) {
                                    addItem(context, optionItem)
                                }
                            }
                        }
                    )
                }
            }
        }

        SnackbarHost(
            hostState = undoSnackBarHostState,
        )

        var buttonOffset: Offset by remember { mutableStateOf(Offset.Zero) }
        Column (
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surface)
                .onGloballyPositioned {
                    buttonOffset = Offset(it.positionInParent().x, it.positionInParent().y)
                    println(buttonOffset)
                    showTutorialDialog = SharedPreferencesUtil.isFirstTime(context)
                }
        ) {
            if (showSearchBar) {
                SearchActivityBar(updateCurrentSearch, onClose = {
                    showSearchBar = false
                })
            }

            if (showTutorialDialog) {
                TutorialDialog(buttonOffset)
            }

            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                FilterButton(onClick = {
                    showFilterDialog = true
                })

                RandomButton(onClick = {
                    coroutineScope.launch {
                        if (optionItems.isNotEmpty()) {
                            val randIndex = floor(Math.random() * optionItems.size).toInt()
                            listState.animateScrollToItem(index = randIndex, scrollOffset = -400)
                            scrollToIndex = randIndex
                        }
                    }
                })

                SearchButton(onClick = {
                    showSearchBar = !showSearchBar
                    if (!showSearchBar) {
                        updateCurrentSearch(context, "")
                    }
                })
            }
        }
    }
}

@Composable
fun FilterButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            tint = Color.White,
            imageVector = ImageVector.vectorResource(R.drawable.filter_icon),
            contentDescription = "filter"
        )
    }
}

@Composable
fun TutorialDialog(buttonOffset: Offset) {
    val density = LocalDensity.current
    var dpOffset by remember {
        mutableStateOf((-1000).dp) // outside
    }

    Dialog (
        onDismissRequest = {},
    ) {
        (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(0.9f)
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .offset(x = 0.dp, y = dpOffset),
        ) {
            Column (
                modifier = Modifier
                    .wrapContentSize()
                    .onGloballyPositioned {
                        val position = buttonOffset.y - it.size.height
                        dpOffset = with (density) {
                            position.toDp()
                        }
                        println(buttonOffset)
                        println(it.size)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Card(
                    elevation = CardDefaults.cardElevation(4.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        Text(
                            text = "Tap \"Randomize!\" to randomly select an activity",
                            fontSize = 14.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(15.dp) // Adjust arrow size
                        .clip(
                            GenericShape { size, _ ->
                                moveTo(0f, 0f) // Top left
                                lineTo(size.width / 2f, size.height) // Bottom center
                                lineTo(size.width, 0f) // Top right
                            }
                        )
                        .background(color = CardDefaults.cardColors().containerColor)
                )
            }
        }
    }
}

@Composable
fun FilterDialog(onClose: (Set<String>) -> Unit) {
    val currentFilteredSet = HomeViewModel.getCurrentFilter(LocalContext.current)
    val newFilteredSet = remember { mutableStateListOf<String>() }
    newFilteredSet.addAll(currentFilteredSet)

    fun addFilter (filter: String) {
        newFilteredSet.add(filter)
    }

    fun removeFilter(filter: String) {
        newFilteredSet.remove(filter)
    }

    Dialog (
        onDismissRequest = {
            onClose(newFilteredSet.toSet())
        },
    ) {
        (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(0.9f)
        Card (
            shape = RoundedCornerShape(16.dp)
        ) {
            Column (
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "Filter by Category",
                    fontSize = 20.sp
                )

                FilterRow(HomeViewModel.RECREATION, HomeViewModel.SPORTS, newFilteredSet, ::addFilter, ::removeFilter)
                FilterRow(HomeViewModel.SHOPPING, HomeViewModel.HOSPITALITY, newFilteredSet, ::addFilter, ::removeFilter)
                FilterRow(HomeViewModel.CULTURE, HomeViewModel.EDUCATION, newFilteredSet, ::addFilter, ::removeFilter)
                FilterRow(HomeViewModel.RELIGION, HomeViewModel.OTHER, newFilteredSet, ::addFilter, ::removeFilter)

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton (
                        onClick = {
                            newFilteredSet.clear()
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("SELECT ALL")
                    }

                    TextButton (
                        onClick = { onClose(newFilteredSet.toSet()) },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("DONE")
                    }
                }
            }
        }
    }
}

@Composable
fun FilterRow(
    category1: String,
    category2: String,
    currentFilteredSet: SnapshotStateList<String>,
    addFilter: (filter: String) -> Unit,
    removeFilter: (filter: String) -> Unit
) {

    Row (
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(0.5f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = category1,
                fontSize = 14.sp
            )
            Checkbox(
                checked = !currentFilteredSet.contains(category1),
                onCheckedChange = {
                    if (it) removeFilter(category1) else addFilter(category1)
                }
            )
        }

        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ){
            Text(
                text = category2,
                fontSize = 14.sp
            )
            Checkbox(
                checked = !currentFilteredSet.contains(category2),
                onCheckedChange = {
                    if (it) removeFilter(category2) else addFilter(category2)
                }
            )
        }
    }
}

@Composable
fun RandomButton(onClick: () -> Unit) {
    Button(
        onClick = onClick
    ) {
        Text(text = "Randomize!")
    }
}

@Composable
fun SearchButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            tint = Color.White,
            imageVector = ImageVector.vectorResource(R.drawable.search_icon),
            contentDescription = "search"
        )
    }
}

@Composable
fun SearchActivityBar(
    updateCurrentSearch: (Context, String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val windowInfo = LocalWindowInfo.current

    var text by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    var hasChanged by rememberSaveable { mutableStateOf(false) }

    Row (
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField (
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .focusRequester(focusRequester)
                .padding(5.dp),
            shape = RoundedCornerShape(20.dp),
            singleLine = true,
            placeholder = {
                Text(text = "Search for an activity")
            },
            value = text,
            onValueChange = {
                text = it
                updateCurrentSearch(context, text)
                hasChanged = true
            },
        )

        if (hasChanged) {
            TextButton(
                onClick = {
                    updateCurrentSearch(context, "")
                    onClose()
                }
            ) {
                Text("Cancel")
            }
        }
    }

    LaunchedEffect(windowInfo) {
        snapshotFlow { windowInfo.isWindowFocused }.collect { isWindowFocused ->
            if (isWindowFocused) {
                focusRequester.requestFocus()
            }
        }
    }
}

@Composable
fun Option(
    optionItem: OptionItem,
    isHighlightedAnimation: Boolean,
    onNavigateToMapScreen: (String, Int) -> Unit,
    resetHighlightIndex: () -> Unit,
    onDelete: () -> Unit
) {

    var currentlyHighlighted by remember { mutableStateOf(false) }

    if (isHighlightedAnimation) {
        LaunchedEffect (true) {
            for (i in 0 until 3) {
                currentlyHighlighted = true
                delay(200)
                currentlyHighlighted = false
                delay(200)
            }
            currentlyHighlighted = true
            resetHighlightIndex()
        }
    }

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        elevation = CardDefaults.cardElevation(4.dp),

        colors = if (currentlyHighlighted) {
            CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f))
        } else CardDefaults.cardColors(),
        onClick = {
            onNavigateToMapScreen(optionItem.activity, 10)
        }
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.SpaceBetween
            ) {
                Column (
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ){
                    Text(
                        fontFamily = FontFamily.Serif,
                        text = optionItem.activity,
                    )

                    Text(
                        fontFamily = FontFamily.Serif,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        text = optionItem.category
                    )
                }

                optionItem.icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = optionItem.activity
                    )
                }
            }
            val middleWord = when (optionItem.activity[0].lowercaseChar()) {
                'a', 'e', 'i', 'o', 'u' -> "an"
                else -> "a"
            }
            val message = "Search for " + middleWord + " " + optionItem.activity

            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.SpaceBetween
            ) {

                IconButton (
                    onClick = {
                        onDelete()
                    }
                ) {
                    Icon(
                        modifier = Modifier
                            .background(color = MaterialTheme.colorScheme.errorContainer)
                            .padding(3.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.delete_icon),
                        contentDescription = "delete"
                    )
                }

                Row (
                ) {
                    Text(
                        fontFamily = FontFamily.Serif,
                        fontSize = 14.sp,
                        text =  message
                    )

                    Icon (
                        modifier = Modifier.padding(start = 4.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.link_search_icon),
                        contentDescription = "link search"
                    )
                }
            }
        }

    }
}

@Composable
private fun getOptionItemListFromJson(
    context: Context,
    setFullOptionItemList: (list: List<OptionItem>) -> Unit) {
    val optionItems = JsonParserUtil.getOptionItems(context)

    val optionItemList = mutableListOf<OptionItem>()
    optionItems.categories.forEach { category: Category ->
        val id = context.resources.getIdentifier(category.icon, "drawable", context.packageName)
        val categoryIcon = ImageVector.vectorResource(id)
        category.activities.forEach { activity: Activity ->
            optionItemList.add(OptionItem(activity.name, category.categoryName, categoryIcon))
        }
    }

    setFullOptionItemList(optionItemList)
}

fun Modifier.customDialogModifier() = layout { measurable, constraints ->

    val placeable = measurable.measure(constraints);
    layout(constraints.maxWidth, constraints.maxHeight){
        placeable.place(0, constraints.maxHeight - placeable.height, 10f)
    }
}