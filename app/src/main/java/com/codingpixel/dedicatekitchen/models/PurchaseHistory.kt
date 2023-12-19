package com.codingpixel.dedicatekitchen.models

import com.codingpixel.dedicatekitchen.helpers.CommonMethods
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PurchaseHistory : Serializable {

    var package_id: Int = 0
    var package_amount: Int = 0
    var created_at: String = ""

    @SerializedName("package")
    var purchasedPackage: PackageModel = PackageModel()


    var formattedDate: String = ""
    var footerTitle: String = ""
    var formattedAmount: String = ""

    fun initData() {
        formattedDate = CommonMethods.historyTimeFormatter(date = created_at) ?: ""
        formattedAmount = "$${package_amount}"
        footerTitle =
            "${purchasedPackage.name} (${(package_amount * purchasedPackage.points_multiplier).toInt()} Points)"

    }


}