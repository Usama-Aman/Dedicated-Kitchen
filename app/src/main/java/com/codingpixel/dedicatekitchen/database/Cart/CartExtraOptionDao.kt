package com.codingpixel.dedicatekitchen.database.Cart

import androidx.room.*
import com.codingpixel.dedicatekitchen.database.ProductOption.ProductOption
import com.codingpixel.dedicatekitchen.helpers.AppPreferenceManager
import com.codingpixel.dedicatekitchen.helpers.Constants

@Dao
interface CartExtraOptionDao {
    @Query("SELECT * FROM cartextraoption  WHERE terminalID == :terminalId")
    fun getAll(
        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
            ?: Constants.DEFAULT_TERMINAL_ID
    ): List<CartExtraOption>

    @Transaction
    @Query("SELECT * FROM cartextraoption WHERE terminalID == :terminalId")
    fun getCartWithExtraOptions(
        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
            ?: Constants.DEFAULT_TERMINAL_ID
    ): List<CartOptionWithExtra>

    @Query("SELECT * FROM cartextraoption WHERE categoryId LIKE :search AND  terminalID == :terminalId")
    fun getAll(
        search: Int,
        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
            ?: Constants.DEFAULT_TERMINAL_ID
    ): List<CartExtraOption>

    @Query("SELECT * FROM cartextraoption WHERE fkCartOptionId LIKE :search AND  terminalID == :terminalId")
    fun getAllById(
        search: Int,
        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
            ?: Constants.DEFAULT_TERMINAL_ID
    ): List<CartExtraOption>

    @Query("SELECT * FROM cartextraoption WHERE name LIKE :search AND  terminalID == :terminalId LIMIT 1")
    fun get(
        search: String,
        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
            ?: Constants.DEFAULT_TERMINAL_ID
    ): CartExtraOption

//    @Query("SELECT * FROM product WHERE pKey IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<Product>

//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): Product

    @Insert
    fun insert(vararg item: CartExtraOption)

    @Delete
    fun delete(item: CartExtraOption)

    @Update
    fun update(vararg item: CartExtraOption)

    @Query("DELETE FROM cartextraoption")
    fun clearTable()
}