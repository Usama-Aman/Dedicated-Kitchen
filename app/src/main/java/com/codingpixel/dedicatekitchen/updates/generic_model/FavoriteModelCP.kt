package com.codingpixel.dedicatekitchen.updates.generic_model

import com.codingpixel.dedicatekitchen.models.MenuItemModel

data class FavoriteModelCP(
    val message: String,
    val result: FavoriteResultCP,
    val status: String
)

data class FavoriteResultCP(
    val current_page: Int,
    val `data`: List<FavoriteDataCP>,
    val first_page_url: String,
    val from: Int,
    val last_page: Int,
    val last_page_url: String,
    val next_page_url: String,
    val path: String,
    val per_page: Int,
    val prev_page_url: Any,
    val to: Int,
    val total: Int
)

data class FavoriteDataCP(
    val cat_parent_name: String,
    val category_id: String,
    val product: MenuItemModel,
    val product_id: String
)

