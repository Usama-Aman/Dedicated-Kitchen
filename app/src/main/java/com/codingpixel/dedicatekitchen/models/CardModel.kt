package com.codingpixel.dedicatekitchen.models

import java.io.Serializable

class CardModel : Serializable {
    var cardName: String = ""
    var cardNumber: String = ""
    var id: Int = 0
    var stripe_id: String = ""
    var card_holder_name: String = ""
    var brand: String = ""
    var stripe_card_id : String? = ""
    var last4: String = ""
    var exp_month: String = ""
    var exp_year: String = ""
    var is_default: Int = 0
    var country: String = ""
    var isChecked: Boolean = false
    var clickable: Boolean = true

    var title: String = ""
    var subTitle: String = ""

    var isDefaultCard: Boolean = false
    var isSelected: Boolean = false

    var cardImage: Int = 0
    var card_number: String = ""
    var card_brand : String = ""
    var card_last_four : Int = 0

}
