package com.codingpixel.dedicatekitchen.updates.generic_model

data class CPMenuItemOptions(
    val message: String,
    val result: List<CPMenuOptionsItemResult>,
    val status: String
)

data class CPMenuOptionsItemResult(
    val cat_id: String,
    val created_at: String,
    val deleted_at: Any,
    val id: Int,
    val image_url: Any,
    val kitchen_id: Any,
    val name: String,
    val options: List<CPMenuItemOption>,
    val sort: Int,
    val updated_at: String
)

data class CPMenuItemOption(
    val attributes: CPAttributes,
    val created_at: String,
    val deleted_at: Any,
    val id: Int,
    val image_url: Any,
    val kitchen_id: Any,
    val option_category_id: Int,
    val option_id: String,
    val sort: Int,
    val updated_at: String
)
