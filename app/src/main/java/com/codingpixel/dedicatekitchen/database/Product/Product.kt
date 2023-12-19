package com.codingpixel.dedicatekitchen.database.Product

import androidx.room.*
import com.codingpixel.dedicatekitchen.database.Category.Category
import com.codingpixel.dedicatekitchen.database.ProductOption.ProductOption
import com.codingpixel.dedicatekitchen.helpers.AppPreferenceManager
import com.codingpixel.dedicatekitchen.models.MenuItemModel
import java.io.Serializable

@Entity(tableName = "Products")
data class Product(
    @PrimaryKey(autoGenerate = true) val pKey: Int,
    @ColumnInfo(name = "id") val id: String?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "itemDescription") val itemDescription: String?,
    @ColumnInfo(name = "price") val price: String?,
//    @Ignore
    @ColumnInfo(name = "image") val image: String?,
    @ColumnInfo(name = "categoryId") val categoryId: String?,
    @ColumnInfo(name = "fkCategoryId") val fkCategoryId: Int?,
    @ColumnInfo(name = "terminalID") var terminalID: String? = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
)

data class ProductWithOptions(
    @Embedded val product: Product,
    @Relation(
        parentColumn = "pKey",
        entityColumn = "fkProductId"
    )
    val options: List<ProductOption>
)
