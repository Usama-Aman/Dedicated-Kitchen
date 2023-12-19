package com.codingpixel.dedicatekitchen.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DedicateKitchen : Serializable {

    var did: Int = 0

    @SerializedName("title")
    var name: String = ""

    @SerializedName("id")
    var terminalId: String = ""
    var apiUrl = "CoPixDedicateKitchen-20000002"
    var merchantID = "407917"
    var merchantPassword = "71474481"
    var merchantName = "486730"
    var menuSetName = "CodingPixelOnlineMenu"
    var image_url: String = ""
}