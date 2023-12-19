package com.codingpixel.dedicatekitchen.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.math.BigInteger

class Category : Serializable {

    @SerializedName("id")
    var ID: String = ""
    var name: String = ""
    var masterCid: String = ""
    var isChecked: Boolean = false
    var menuItems = ArrayList<MenuItemModel>()
    var sectionIndex: Int = 0
    var image: String = ""
    var groupName = ""
    var cat_id = ""
    var kitchen_id: BigInteger = BigInteger("0")
    var type = ""
    var image_url = ""
    var sort = ""
    var deleted_at = ""
    var created_at = ""
    var updated_at = ""
    var image_base_url = "http://dedicatekitchen.com/"
}