package com.codingpixel.dedicatekitchen.models

import java.io.Serializable

class OrderToDbModel : Serializable {
    var table_trans_id: String = ""
    //var items_data : ArrayList<OrderModel> = ArrayList()
    var items_data : ArrayList<ItemData> = ArrayList()
}