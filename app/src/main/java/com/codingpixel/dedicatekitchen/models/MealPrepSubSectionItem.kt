package com.codingpixel.dedicatekitchen.models

import com.codingpixel.dedicatekitchen.helpers.Constants

class MealPrepSubSectionItem {

    var sectionIndex : Int = 0

    var title: String = ""
    var info: String = ""
    var startPrice: Float = 0.0f
    var endPrice: Float = 0.0f

    var checked: Boolean = false

    var quantity: Int = 1
    var hasQuanity: Boolean = false


    fun getPriceRange(): String {
        return if (endPrice > 0.0f) {
            "${Constants.DOLLAR_SIGN}${startPrice}  |  ${Constants.DOLLAR_SIGN}${endPrice}"
        } else {
            "${Constants.DOLLAR_SIGN}${startPrice}"
        }
    }

    fun getPricePerQuantity(): String {
        return "${Constants.DOLLAR_SIGN}${startPrice * quantity}"
    }

}