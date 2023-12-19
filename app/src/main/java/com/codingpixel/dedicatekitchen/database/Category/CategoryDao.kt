package com.codingpixel.dedicatekitchen.database.Category

import androidx.room.*
import com.codingpixel.dedicatekitchen.helpers.AppPreferenceManager
import com.codingpixel.dedicatekitchen.helpers.Constants

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAll(): List<Category>

    @Transaction
    @Query("SELECT * FROM categories WHERE terminalID == :terminalId")
    fun getCategoryWithProducts(
        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
            ?: Constants.DEFAULT_TERMINAL_ID
    ): List<CategoryWithProducts>


    @Transaction
    @Query("SELECT * FROM categories WHERE terminalID == :terminalId AND group_name == :groupName")
    fun getMealPrepCategoryWithProducts(
        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
            ?: Constants.DEFAULT_TERMINAL_ID,
        groupName : String = "Meal Prep"
    ): List<CategoryWithProducts>

    @Transaction
    @Query("SELECT * FROM categories WHERE terminalID == :terminalId AND group_name != :groupName")
    fun getTakeoutCategoryWithProducts(
        terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
            ?: Constants.DEFAULT_TERMINAL_ID,
        groupName : String = "Meal Prep"
    ): List<CategoryWithProducts>

    @Query("SELECT * FROM categories WHERE id == :id AND terminalID == :terminalId LIMIT 1")
    fun get(id: String,
            terminalId: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
                ?: Constants.DEFAULT_TERMINAL_ID): Category

//    @Query("SELECT * FROM product WHERE pKey IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<Product>

//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): Product

    @Insert
    fun insert(vararg item: Category)

    @Delete
    fun delete(item: Category)

    @Update
    fun update(vararg item: Category)

    @Query("DELETE FROM categories")
    fun clearAllCategories()
}