package com.codingpixel.dedicatekitchen.models.local

import java.io.Serializable

class ToggleOption : Serializable{

    var optionId: Int = 0
    var optionName: String = ""
    var isChecked: Boolean = false
}