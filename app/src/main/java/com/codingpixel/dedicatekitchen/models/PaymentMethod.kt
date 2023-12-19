package com.codingpixel.dedicatekitchen.models

class PaymentMethod {

    var title: String = ""
    var subTitle: String = ""

    var isDefaultCard: Boolean = false
    var isSelcted: Boolean = false

    var cardImage: Int = 0
    var card_number: String = ""
    var is_default: Int = 0
    var card_brand : String = ""
}