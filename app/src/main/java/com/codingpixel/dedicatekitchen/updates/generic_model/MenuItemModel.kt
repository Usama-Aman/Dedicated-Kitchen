package com.codingpixel.dedicatekitchen.updates.generic_model

import com.codingpixel.dedicatekitchen.models.MenuItemModel
import com.google.gson.annotations.SerializedName
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import com.google.gson.internal.LinkedTreeMap
import java.lang.StringBuilder


data class CPMenuItemModel(
    val message: String,
    val result: CPMenuItemResult,
    val status: String
)

data class CPMenuItemResult(
    val menu_category: CPMenuCategory,
    val menu_items: List<MenuItemModel>
)

class CPMenuCategory : Serializable {
    val cat_id: String = ""
    val created_at: String = ""
    val deleted_at: Any = ""
    val id: Int = 0
    val image_url: String = ""
    val kitchen_id: Int = 0
    val name: String = ""
    val sort: Int = 0
    val type: String = ""
    val updated_at: String = ""
}

data class CPMenuItem(
    val attributes: CPAttributes,
    val created_at: String,
    val deleted_at: Any,
    val id: Int,
    val image_url: String,
    val kitchen_id: Int,
    val menu_category_id: Int,
    val menu_id: String,
    val sort: Int,
    val updated_at: String
)

class CPAttributes : Serializable {
    val ID: String = ""
    val MasterMID: String = ""
    val MasterCID: String = ""
    val categoryType: String = ""
    val comboDetectType: String = ""
    val comboOptionLinksConfig: String = ""
    val comboPriority: String = ""
    val cost: String = ""
    val count: String = ""
    val course: String = ""

    var extra_options: Any = ""

    val interfaceLink2: String = ""
    val isDiscountable: String = ""
    val isItem86ed: String = ""
    val isOpenPrice: String = ""
    val isRefillable: String = ""
    val isTimerItem: String = ""
    val isWeighScaleItem: String = ""
    val itemAltDescription: String = ""
    val itemDescription: String = ""
    val itemRefundable: String = ""

    //    val linked_options: LinkedOptions="
    val maxOpenPriceAmt: String = ""
    val mealPlanType: String = ""
    val menuItemAltName: String = ""
    val menuItemName: String = ""
    val name: String = ""
    val notes: String = ""
    val numDiscountsAllowed: String = ""
    val plu: String = ""
    val pointsAwarded: String = ""
    val price: String = ""
    val priceOnReceipt: String = ""
    val printName: String = ""
    val promptForQty: String = ""
    val promptSize: String = ""
    val qsrDeptId: String = ""
    val requireCustomer: String = ""
    val scanCode: String = ""
    val sizes: List<Any> = ArrayList()
    val timerMinMinsCharged: String = ""
    val timerRoundingMethod: String = ""
    val timerTimeIntervalMins: String = ""
    val timerTimeRounding: String = ""
    val trackByItemCount: String = ""
    val unit: String = ""
    val updateCount: String = ""
}

class CPMenuItemExtraOptionsModel : Serializable {
    val ID: String = ""
    val categoryId: String = ""
    val forceMaxSelection: String = ""
    val interfaceLink: String = ""
    val masterCID: String = ""
    val maxSelection: String = ""
    val minSelection: String = ""
    val priceGreaterThanQP: String = ""
    val priceLessThanOrEqualToQP: String = ""
    val quantityPoint: String = ""
    val wingsMode: String = ""
    val wingsMultiple: String = ""
}


class MyJson {
    fun read(node: LinkedTreeMap<String, Any>) {
        for ((key, value) in node) {
            if (!getInstanceType(value)) {
                jsonFormat(key, value)
            } else {
                MyJsonBuilder.clearString()
                MyJsonBuilder.append("{")
                MyJson().read(value as LinkedTreeMap<String, Any>)
            }
        }
    }

    private fun jsonFormat(k: String, v: Any) {
        MyJsonBuilder.append(String.format("\"%s\":\"%s\"", k, if (v.toString().isBlank()) "0" else v))
    }

    private fun getInstanceType(obj: Any): Boolean {
        return obj is LinkedTreeMap<*, *>
    }
}

object MyJsonBuilder {
    private val jsonBuilder = StringBuilder()
    fun append(node: String?) {
        jsonBuilder.append(node)
    }

    fun clearString() {
        jsonBuilder.clear()
    }

    private fun format(jsonBuilder: StringBuilder): String {
        jsonBuilder.append("}")

        var adjustedjson = jsonBuilder.toString()
        if (adjustedjson.contains("\"\"")) adjustedjson = adjustedjson.replace("\"\"", "\",\"")
        if (adjustedjson.contains("}\"")) adjustedjson = adjustedjson.replace("}\"", "},\"")
        return adjustedjson
    }

    fun build(): String {
        return format(jsonBuilder)
    }
}

