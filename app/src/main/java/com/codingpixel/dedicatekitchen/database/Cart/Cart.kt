package com.codingpixel.dedicatekitchen.database.Cart

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.codingpixel.dedicatekitchen.helpers.AppPreferenceManager
import com.codingpixel.dedicatekitchen.models.ExtraItemOrderModel
import com.codingpixel.dedicatekitchen.models.ExtraOptionsModel

@Entity
data class Cart(
    @PrimaryKey(autoGenerate = true) val pKey: Int = 0,
    @ColumnInfo(name = "id") val id: Int?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "price") val price: String?,
    @ColumnInfo(name = "categoryId") val categoryId: String?,
    @ColumnInfo(name = "quantity") val quantity: Int?,
    @ColumnInfo(name = "img") val img: String?,
    @ColumnInfo(name = "totalPrice") val totalPrice: String?,
    @ColumnInfo(name = "orderType") val orderType: String?,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "terminalID") var terminalID: String? = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
)