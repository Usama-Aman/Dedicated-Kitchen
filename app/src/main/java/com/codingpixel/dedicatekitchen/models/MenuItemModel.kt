package com.codingpixel.dedicatekitchen.models

import com.codingpixel.dedicatekitchen.updates.generic_model.CPAttributes
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MenuItemModel : Serializable {

    var image: String = ""
    var menuItemName: String = ""
    var price: String = ""
    var updatedPrice: String = ""

    @SerializedName("id")
    var ID: String = ""
    var terminalId: String = ""
    var quantity: String = "1"
    var categoryId: String = ""
    var itemDescription: String = ""
    var showQuantityChanger: Boolean = false
    var description: String = ""
    var isChecked: Boolean = false

    var extraOptions = ArrayList<ExtraOptionsModel>()
    var isFavourite: Boolean = false
    var isFavouriteFetched: Boolean = false


    var attributes: CPAttributes = CPAttributes()
    var created_at: String = ""
    var deleted_at: Any = ""
    var image_url: String = ""
    var kitchen_id: Int = 0
    var menu_category_id: Int = 0
    var menu_id: String = ""
    var sort: Int = 0
    var image_base_url = "http://dedicatekitchen.com/"
    var category_name = ""

    fun getFormattedPrice(): String {
        return try {
            if (price.isNotEmpty())
                "$${String.format("%.2f", price.toFloat())}"
            else {
                "$0.0"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "$0.0"
        }

    }


}
