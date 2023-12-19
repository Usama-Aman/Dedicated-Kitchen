package com.codingpixel.dedicatekitchen.updates.generic_model

import com.codingpixel.dedicatekitchen.models.Category
import com.codingpixel.dedicatekitchen.models.DedicateKitchen

data class GetAllKitchenModel(
    val message: String,
    val result: List<DedicateKitchen>,
    val status: String
)

data class GetMenuCategoryByKitchenIdModel(
    val message: String,
    val result: List<Category>,
    val status: String
)
