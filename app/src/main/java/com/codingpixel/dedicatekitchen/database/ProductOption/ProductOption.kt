package com.codingpixel.dedicatekitchen.database.ProductOption

import androidx.room.*
import com.codingpixel.dedicatekitchen.helpers.AppPreferenceManager

@Entity(tableName = "ProductOptions")
data class ProductOption(
    @PrimaryKey(autoGenerate = true) val pKey: Int,
    @ColumnInfo(name = "id") val id: String?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "minSelection") val minSelection: String?,
    @ColumnInfo(name = "maxSelection") val maxSelection: String?,
    @ColumnInfo(name = "forceMaxSelection") val forceMaxSelection: String?,
    @ColumnInfo(name = "categoryId") val categoryId: String?,
    @ColumnInfo(name = "fkProductId") val productId: Int?,
    @ColumnInfo(name = "terminalID") var terminalID: String? = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
)