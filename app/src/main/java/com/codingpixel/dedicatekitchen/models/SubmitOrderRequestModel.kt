package com.codingpixel.dedicatekitchen.models

import java.io.Serializable

class SubmitOrderRequestModel : Serializable {
    var order_type: Int = 0
    var terminal_pickup_time: String = ""
    var payment_method: Int = 0 // 2-> From DK Wallet, else -> Credit Card
    var card_id: Int = 0
    var stripe_charge_id: String = ""
    var payment_status: String = ""
    var pos_table_trans_id: String = ""
    var pos_table_trans_api_params: String = ""
    var pos_order_table_trans_id: String = ""
    var pos_order_payment_auth_id: String = ""
    var name: String = ""
    var phoneno: String = ""
    var card_number: String = ""

    //    var card_month: Int = 0
    var card_month: String = ""
    var card_year: Int = 0
    var cvc: Int = 0

    var email: String = ""
    var subtotal: String = ""
    var tax: String = ""
    var total_amount: String = ""
    var order_id: Int = 0
    var order_note: String = ""
    var pickup_date_time: String = ""
    var delivery_address: String = ""

    var tip: Float = 0f
    var points_discount: Float = 0f

    var terminal_name: String = ""
    var is_group = ""

}