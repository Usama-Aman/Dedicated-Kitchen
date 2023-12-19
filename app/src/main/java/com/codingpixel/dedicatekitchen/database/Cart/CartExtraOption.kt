package com.codingpixel.dedicatekitchen.database.Cart

import androidx.room.*
import com.codingpixel.dedicatekitchen.helpers.AppPreferenceManager

@Entity
data class CartExtraOption(
    @PrimaryKey(autoGenerate = true) val pKey: Int,
    @ColumnInfo(name = "id") val id: Int?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "price") val price: String?,
    @ColumnInfo(name = "quantity") val quantity: Int?,
    @ColumnInfo(name = "categoryId") val categoryId: String?,
    @ColumnInfo(name = "fkCartOptionId") val cartOptionId: Int?,
    @ColumnInfo(name = "terminalID") var terminalID: String? = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
)

data class CartOptionWithExtra(
    @Embedded val cart: Cart,
    @Relation(
        parentColumn = "pKey",
        entityColumn = "fkCartOptionId"
    )
    val cartExtraOptions: List<CartExtraOption>
)