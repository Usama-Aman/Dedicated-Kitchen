package com.codingpixel.dedicatekitchen.models

import java.io.Serializable

class OptionModel : Serializable {
    var ID: String = ""
    var name: String = ""
    var masterCid: String = ""
    var option  = ArrayList<MenuItemModel>()
}