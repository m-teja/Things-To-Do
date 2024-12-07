package com.teja_app_productions_things_to_do.data

data class OptionData(
    val categories: List<Category>
)

data class Category(
    val categoryName: String,
    val icon: String,
    val activities: List<Activity>
)

data class Activity(
    val id: String,
    val name: String
)
