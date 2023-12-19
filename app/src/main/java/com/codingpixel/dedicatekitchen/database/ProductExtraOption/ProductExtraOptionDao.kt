package com.codingpixel.dedicatekitchen.database.ProductExtraOption

import androidx.room.*
import com.codingpixel.dedicatekitchen.helpers.AppPreferenceManager
import com.codingpixel.dedicatekitchen.helpers.Constants

@Dao
interface ProductExtraOptionDao {
    @Query("SELECT * FROM productextraoptions WHERE terminalID == :terminalId")
    fun getAll(
        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
            ?: Constants.DEFAULT_TERMINAL_ID
    ): List<ProductExtraOption>

//    @Transaction
//    @Query("SELECT * FROM productextraoptions  WHERE terminalID == :terminalId")
//    fun getProductWithExtraOptions(
//        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
//            ?: Constants.DEFAULT_TERMINAL_ID
//    ): List<ProductOptionWithExtra>

    @Query("SELECT * FROM productextraoptions WHERE categoryId == :id AND terminalID == :terminalId")
    fun getAllById(
        id: String,
        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
            ?: Constants.DEFAULT_TERMINAL_ID
    ): List<ProductExtraOption>

    @Query("SELECT * FROM productextraoptions WHERE id == :id AND terminalID == :terminalId LIMIT 1")
    fun get(
        id: String,
        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
            ?: Constants.DEFAULT_TERMINAL_ID
    ): ProductExtraOption

    @Insert
    fun insert(vararg item: ProductExtraOption)

    @Delete
    fun delete(item: ProductExtraOption)

    @Update
    fun update(vararg item: ProductExtraOption)
}