package com.codingpixel.dedicatekitchen.database.ProductOption

import androidx.room.*
import com.codingpixel.dedicatekitchen.helpers.AppPreferenceManager
import com.codingpixel.dedicatekitchen.helpers.Constants

@Dao
interface ProductOptionDao {
    @Query("SELECT * FROM productoptions WHERE terminalID == :terminalId")
    fun getAll(
        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
            ?: Constants.DEFAULT_TERMINAL_ID
    ): List<ProductOption>

    @Query("SELECT * FROM productoptions WHERE name LIKE :search  AND terminalID == :terminalId LIMIT 1")
    fun get(
        search: String,
        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
            ?: Constants.DEFAULT_TERMINAL_ID
    ): ProductOption

    @Query("SELECT * FROM productoptions WHERE fkProductId LIKE :search AND terminalID == :terminalId")
    fun getByID(
        search: Int,
        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
            ?: Constants.DEFAULT_TERMINAL_ID
    ): List<ProductOption>

//    @Transaction
//    @Query("SELECT * FROM productoptions WHERE fkProductId LIKE :search AND terminalID == :terminalId")
//    fun getProductWithExtraOptions(
//        search: Int,
//        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
//            ?: Constants.DEFAULT_TERMINAL_ID
//    ): List<ProductOptionWithExtra>

    @Insert
    fun insert(vararg item: ProductOption)

    @Delete
    fun delete(item: ProductOption)

    @Update
    fun update(vararg item: ProductOption)

    @Query("DELETE FROM productoptions")
    fun clearAllOptions()
}