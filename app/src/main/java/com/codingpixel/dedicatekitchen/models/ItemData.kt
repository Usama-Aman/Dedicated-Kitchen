package com.codingpixel.dedicatekitchen.models

import java.io.Serializable

class ItemData : Serializable {

    //var viewType : String = ""
    var name: String = ""
    var item_id: String = ""
    var price: String = ""
    var category_id: String = ""
    var quantity: String = ""
    var total_amount: String = ""
    var img: String? = ""
    var note : String = ""



    var subTotal : Float = 0f
    //var isChecked : Boolean = true
    //var allExtraOptions = ArrayList<ExtraOptionsModel>()
    var options = ArrayList<ExtraItemOrderModel>()


    fun getTopTitle() : String {
        return "$name (x$quantity)"
    }

}