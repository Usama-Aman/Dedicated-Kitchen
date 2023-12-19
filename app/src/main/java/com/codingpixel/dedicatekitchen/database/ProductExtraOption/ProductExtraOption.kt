package com.codingpixel.dedicatekitchen.database.ProductExtraOption

import androidx.room.*
import com.codingpixel.dedicatekitchen.database.ProductOption.ProductOption
import com.codingpixel.dedicatekitchen.helpers.AppPreferenceManager

@Entity(tableName = "ProductExtraOptions")
data class ProductExtraOption(
    @PrimaryKey(autoGenerate = true) val pKey: Int,
    @ColumnInfo(name = "id") val id: String?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "price") val price: String?,
//    @Ignore
    @ColumnInfo(name = "image") val image: String?,
    @ColumnInfo(name = "categoryId") val categoryId: String?,
    @ColumnInfo(name = "fkProductOptionId") val productOptionId: Int?,
    @ColumnInfo(name = "terminalID") var terminalID: String? = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
)

//data class ProductOptionWithExtra(
//    @Embedded val option: ProductOption,
//    @Relation(
//        parentColumn = "pKey",
//        entityColumn = "fkProductOptionId"
//    )
//    val optionExtras: List<ProductExtraOption>
//)