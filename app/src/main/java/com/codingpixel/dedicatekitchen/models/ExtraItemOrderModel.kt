package com.codingpixel.dedicatekitchen.models

import java.io.Serializable

class ExtraItemOrderModel : Serializable {
    var extra_option_id: String = ""
    var name: String = ""
    var price: String = ""
    var quantity: String = ""
    var category_id: String = ""
    var isChecked: Boolean = false

    fun getFormattedPrice(): String {
        return if (price.isNotEmpty() && price.matches("-?\\d+(\\.\\d+)?".toRegex())) {
            "$${String.format("%.2f", price.toFloat())}"
        } else {
            "$${price}"
        }
    }
}