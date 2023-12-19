package com.codingpixel.dedicatekitchen.models

import java.io.Serializable

class OrderModel : Serializable {
    var pKey: Int = 0
    var viewType : String = ""
    var item_id: String = ""
    var name: String = ""
    var price: String = ""
    var category_id: String = ""
    var quantity: String = ""
    var showProductNote : Boolean = false
    var isChecked : Boolean = true
    var allExtraOptions = ArrayList<ExtraOptionsModel>()
    var options = ArrayList<ExtraItemOrderModel>()
    var total_amount: String = ""
    var img: String = ""
    var itemNote : String = ""
    var isEditable: Boolean = true
    var notes : String = ""

}