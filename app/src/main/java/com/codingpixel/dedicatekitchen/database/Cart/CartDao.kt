package com.codingpixel.dedicatekitchen.database.Cart

import androidx.room.*
import com.codingpixel.dedicatekitchen.helpers.AppPreferenceManager
import com.codingpixel.dedicatekitchen.helpers.ApplicationEnum
import com.codingpixel.dedicatekitchen.helpers.Constants

@Dao
interface CartDao {
    @Query("SELECT * FROM cart WHERE terminalID == :terminalId")
    fun getAll(
        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
            ?: Constants.DEFAULT_TERMINAL_ID
    ): List<Cart>


    @Query("SELECT * FROM cart")
    fun getAllWithOUtTerminal(
    ): List<Cart>

    @Query("SELECT * FROM cart WHERE id == :id AND terminalID == :terminalId LIMIT 1")
    fun get(
        id: Int,
        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
            ?: Constants.DEFAULT_TERMINAL_ID
    ): Cart

    @Query("SELECT * FROM cart WHERE pKey == :pId AND terminalID == :terminalId LIMIT 1")
    fun getItem(
        pId: Int,
        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
            ?: Constants.DEFAULT_TERMINAL_ID
    ): Cart?

    @Query("SELECT COUNT(*) FROM cart")
    fun getCartCount(): Int


    @Query("SELECT EXISTS (SELECT 1 FROM cart WHERE orderType = :type AND terminalID == :terminalId LIMIT 1)")
    fun exists(
        type: String,
        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
            ?: Constants.DEFAULT_TERMINAL_ID
    ): Boolean

    @Query("SELECT * FROM cart WHERE name = :name  AND terminalID == :terminalId LIMIT 1")
    fun getByName(


        name: String,
        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
            ?: Constants.DEFAULT_TERMINAL_ID
    ): Cart?

//    @Query("SELECT * FROM product WHERE pKey IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<Product>

//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): Product

    @Insert
    fun insert(item: Cart): Long

    @Delete
    fun delete(item: Cart)

    @Update
    fun update(vararg item: Cart)

    @Query("DELETE FROM cart WHERE terminalID == :terminalId")
    fun clearTable(
        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
            ?: Constants.DEFAULT_TERMINAL_ID
    )
}