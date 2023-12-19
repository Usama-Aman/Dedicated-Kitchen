package com.codingpixel.dedicatekitchen.models

import com.codingpixel.dedicatekitchen.helpers.Constants

class Product {

    var title: String = ""
    var description: String = ""

    var isFavourite: Boolean = false

    var price: Float = 12.0f
    var quantity: Int = 0


    fun getFormattedPrice(): String {

        return "${Constants.DOLLAR_SIGN} ${quantity * price}"
    }
}