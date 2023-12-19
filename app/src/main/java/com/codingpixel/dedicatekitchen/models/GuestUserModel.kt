package com.codingpixel.dedicatekitchen.models

import java.io.Serializable

class GuestUserModel : Serializable {
    var name : String = ""
    var email : String = ""
    var phoneno : String = ""
    var card_number : String = ""
//    var card_month : Int = 0
    var card_month : String = ""
    var card_year  : Int = 0
    var cvc  : Int = 0
}