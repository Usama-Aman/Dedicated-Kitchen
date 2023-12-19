package com.codingpixel.dedicatekitchen.models

class UserLocation {

    var id: Int = 0
    var address: String = ""
    var country: String = ""
    var city: String? = ""
    var state: String? = ""
    var postal_code: String? = ""
    var latitude: String = ""
    var longitude: String = ""
    var is_default: Int = 0
    var isChecked : Boolean = false
}