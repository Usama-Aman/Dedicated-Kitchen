package com.codingpixel.dedicatekitchen.database.Category

import androidx.room.*
import com.codingpixel.dedicatekitchen.database.Product.Product
import com.codingpixel.dedicatekitchen.helpers.AppPreferenceManager

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val pKey: Int,
    @ColumnInfo(name = "id") val id: String?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "image") var image: String?,
    @ColumnInfo(name = "terminalID") var terminalID: String? = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId,
    @ColumnInfo(name = "group_name") var groupName: String = ""
)

data class CategoryWithProducts(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "pKey",
        entityColumn = "fkCategoryId"
    )
    val products: List<Product>
)