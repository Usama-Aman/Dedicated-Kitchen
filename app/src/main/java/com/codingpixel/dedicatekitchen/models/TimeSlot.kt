package com.codingpixel.dedicatekitchen.models

import java.io.Serializable

class TimeSlot : Serializable {

    var slot: String = ""
    var time: Int = 0
    var time_: String = ""
    var isSelected: Boolean = false
}