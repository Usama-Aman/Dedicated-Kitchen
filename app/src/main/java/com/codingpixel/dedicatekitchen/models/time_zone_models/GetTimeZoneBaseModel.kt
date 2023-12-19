package com.codingpixel.dedicatekitchen.models.time_zone_models

data class GetTimeZoneBaseModel(
    val dstOffset: Int = 0,
    val rawOffset: Int = 0,
    var  status: String = "",
    val timeZoneId: String = "",
    val timeZoneName: String = "",
    var message : String = ""

)