package com.codingpixel.dedicatekitchen.models

import com.codingpixel.dedicatekitchen.helpers.CommonMethods
import com.codingpixel.dedicatekitchen.helpers.Constants
import com.google.gson.Gson
import java.io.Serializable


class Order : Serializable {

    var id: Int = 0
    var pos_table_trans_api_params: String = "" //convert this to json
    var subtotal: Double = 0.0
    var tip: Double? = 0.0
    var points_discount: Double? = 0.0
    var tax: Double = 0.0
    var total_amount: Double = 0.0
    var orderType: String = ""
    var order_type: String? = "" // takeout , mp_delivery , mp_takeout
    var pickup_time: String = "" // 3 -> Add 3 Hours in Current Time and show ETA
    var delivery_address: String? = "" // can be null
    var payment_status: String = "" // complete , cancel_by_guest
    var created_at: String = ""
    var pickup_date_time: String? = ""
    var orderStatus: String = ""
    var user_card_id: Int? = 0

    var terminal_name: String? = ""

    var orderInfo = OrderToDbModel()

    var deliveryDateTime: String = ""

    fun orderDetail(): OrderToDbModel {
        orderInfo = try {
            Gson().fromJson(pos_table_trans_api_params, OrderToDbModel::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            OrderToDbModel()
        }
        return orderInfo
    }

    fun showButtons(): Boolean {
        return orderType == "past"
    }

//    fun getFormattedOrderNumber() : String {
//        val format = Constants.ORDER_NUMBER_FORMAT
//
//        for (i in id.toString().indices){
//
//        }
//    }

    fun getFormattedOrderType(): String {
        return when {
            (order_type ?: "").isEmpty() -> ""
            order_type ?: "" == "takeout" -> "Takeout"
            order_type ?: "" == "mp_delivery" -> "Mealprep Delivery"
            order_type ?: "" == "mp_takeout" -> "Mealprep Takeout"
            else -> ""
        }
    }

    fun getOrderTitle(): String {
        val title = "00000000"
        return "Order Number: #${title.dropLast(id.toString().length)}$id"
    }

    fun getFormattedOrderNumber(): String {
        val title = "00000000"
        return "#${title.dropLast(id.toString().length)}$id"
    }

    fun initDeliveryDateTime() {
        if (order_type == "mp_delivery") {
            val prefFixDate = CommonMethods.orderDateOnlyFormatter(pickup_date_time)
            val dateTimeSplitArr = (pickup_date_time ?: "").split(" ")
            if (dateTimeSplitArr.size >= 2) {
                val dateStr = dateTimeSplitArr[0]
                val timeStr = dateTimeSplitArr[1]
                val timeArr = timeStr.split(":")
                deliveryDateTime =
                    prefFixDate + " " + Constants.DOT + " " + CommonMethods.orderTimeFormatter2(
                        pickup_date_time
                    ) + " - " + CommonMethods.orderTimeFormatter3(
                        pickup_date_time
                    )
//                deliveryDateTime = if (timeArr.isNotEmpty()) {
//                    val startingHour = timeArr[0].toInt()
//                    if (startingHour == 12) {
//                        prefFixDate + " " + Constants.DOT + " 10 AM - 12 PM"
//                    } else {
//                        if (timeStr == "14:00:00" || timeStr == "02:00:00")
//                            prefFixDate + " " + Constants.DOT + " 12 PM - 2 PM"
//                        else
//                            prefFixDate + " " + Constants.DOT + " ${(startingHour - 2)} PM - $startingHour PM"
//                    }
//                } else {
//                    prefFixDate + " " + Constants.DOT + " " + CommonMethods.orderTimeFormatter(
//                        pickup_date_time
//                    )
//                }
            } else {

                deliveryDateTime = if ((pickup_date_time ?: "").isNotEmpty())
                    CommonMethods.orderDateOnlyFormatter(pickup_date_time) + " " + Constants.DOT + " " + CommonMethods.orderTimeFormatter(
                        pickup_date_time
                    )
                else
                    "N/A"

//                deliveryDateTime =
//                    CommonMethods.orderDateOnlyFormatter(pickup_date_time) + " " + Constants.DOT + " " + CommonMethods.orderTimeFormatter(
//                        pickup_date_time
//                    }

            }


        }
//        else if (order_type == "mp_takeout") {
//            deliveryDateTime = if ((pickup_date_time ?: "").isNotEmpty()) {
//                CommonMethods.orderDateOnlyFormatter(pickup_date_time) + " " + Constants.DOT + " " + CommonMethods.orderTimeFormatterMealPrepPickup(
//                    pickup_date_time
//                )
//            } else {
//                "N/A"
//            }
        else {

            deliveryDateTime = if ((pickup_date_time ?: "").isNotEmpty()) {
                CommonMethods.orderDateOnlyFormatter(pickup_date_time) + " " + Constants.DOT + " " + CommonMethods.orderTimeFormatter(
                    pickup_date_time
                )
            } else {
                "N/A"

                //                deliveryDateTime =
                //                    CommonMethods.orderDateOnlyFormatter(pickup_date_time) + " " + Constants.DOT + " " + CommonMethods.orderTimeFormatter(
                //                        pickup_date_time
                //                    )
            }


        }
    }
}