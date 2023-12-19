package com.codingpixel.dedicatekitchen.models

import com.codingpixel.dedicatekitchen.updates.generic_model.CPAttributes
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ExtraOptionsModel :Serializable {
    var id : Any = ""
    var name : String = ""
    var minSelection: String = ""
    var maxSelection: String = ""
    var forceMaxSelection: String = "1"
    var categoryId: String = ""
    var sectionIndex :Int = 0
    var checked : Boolean = false

    var menuItems = ArrayList<MenuItemModel>()
    var showSubMenu : Boolean = false

    val attributes: CPAttributes = CPAttributes()
    val created_at: String = ""
    val deleted_at: Any = ""


    val image_url: Any = ""
    val kitchen_id: Any = ""
    val option_category_id: Int = 0
    val option_id: String = ""
    val sort: Int = 0
    val updated_at: String = ""
    var cat_id: String = ""

}

