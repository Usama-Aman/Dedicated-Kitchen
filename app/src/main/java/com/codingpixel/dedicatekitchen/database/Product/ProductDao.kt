package com.codingpixel.dedicatekitchen.database.Product

import androidx.room.*
import com.codingpixel.dedicatekitchen.helpers.AppPreferenceManager
import com.codingpixel.dedicatekitchen.helpers.Constants

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAll(): List<Product>

    @Transaction
    @Query("SELECT * FROM products WHERE terminalID == :terminalId")
    fun getProductWithOptions(
        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
            ?: Constants.DEFAULT_TERMINAL_ID
    ): List<ProductWithOptions>

    @Query("SELECT * FROM products WHERE id == :id AND terminalID == :terminalId LIMIT 1")
    fun get(
        id: String,
        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
            ?: Constants.DEFAULT_TERMINAL_ID
    ): Product

//    @Query("SELECT * FROM product WHERE pKey IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<Product>

//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): Product

    @Insert
    fun insert(vararg item: Product)

    @Delete
    fun delete(item: Product)

    @Update
    fun update(vararg item: Product)

    @Query("DELETE FROM products")
    fun clearAllProducts()
}