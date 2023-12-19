package com.codingpixel.dedicatekitchen.models

import java.io.Serializable

class LocationModel : Serializable {
    var id: Int = 0
    var address: String = ""
    var country: String = ""
    var city: String = ""
    var latitude: String = ""
    var longitude: String = ""
    var is_default: Int = 0
}