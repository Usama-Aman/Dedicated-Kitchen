package com.codingpixel.dedicatekitchen.models

import java.io.Serializable

class PackageModel : Serializable {
    var id: Int = 0
    var name: String = ""
    var detail: String = ""
    var offer: String = ""
    var package_id: Int = 0
    var amount: Int = 0
    var price: String = ""
    var selectedPackage: Boolean = false
    var pos_package_id: String? = ""
    var end_price: String = ""
    var bg_color: String = ""
    var font_color: String = ""
    var payment_chunks: String = ""
    var text: String = ""
    var amountArray = ArrayList<TimeSlot>()
    var multiplier: Float = 1f

    var packages_points: String = "0"
    var points_multiplier: Float = 1f

    fun isBronze(): Boolean {
        return name.toLowerCase()?.contains("bronze")
    }

    fun isSilver(): Boolean {
        return name.toLowerCase()?.contains("silver")
    }


    fun isGold(): Boolean {
        return name.toLowerCase()?.contains("gold")
    }

    fun isPlatinum(): Boolean {
        return name.toLowerCase()?.contains("platinum")
    }

    fun initPackagePosId() {

        if (name.toLowerCase().contains("silver"))
            pos_package_id = "20000002"
        else if (name.toLowerCase().contains("gold"))
            pos_package_id = "20000003"
        else if (name.toLowerCase().contains("platinum"))
            pos_package_id = "20000004"
        else if (name.toLowerCase().contains("bronze"))
            pos_package_id = "20000001"
        else
            ""
    }


}