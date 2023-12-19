package com.codingpixel.dedicatekitchen.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.codingpixel.dedicatekitchen.database.Cart.Cart
import com.codingpixel.dedicatekitchen.database.Cart.CartDao
import com.codingpixel.dedicatekitchen.database.Cart.CartExtraOption
import com.codingpixel.dedicatekitchen.database.Cart.CartExtraOptionDao
import com.codingpixel.dedicatekitchen.database.Category.Category
import com.codingpixel.dedicatekitchen.database.Category.CategoryDao
import com.codingpixel.dedicatekitchen.database.Product.Product
import com.codingpixel.dedicatekitchen.database.Product.ProductDao
import com.codingpixel.dedicatekitchen.database.ProductExtraOption.ProductExtraOption
import com.codingpixel.dedicatekitchen.database.ProductExtraOption.ProductExtraOptionDao
import com.codingpixel.dedicatekitchen.database.ProductOption.ProductOption
import com.codingpixel.dedicatekitchen.database.ProductOption.ProductOptionDao

@Database(
    entities = [Product::class, Category::class, ProductOption::class, ProductExtraOption::class,
                Cart::class, CartExtraOption::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun productOptionDao(): ProductOptionDao
    abstract fun productExtraOptionDao(): ProductExtraOptionDao
    abstract fun cartDao(): CartDao
    abstract fun cartExtraOptionDao(): CartExtraOptionDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
//        @Volatile private var instance2: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
//            instance ?: buildDatabase2(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            AppDatabase::class.java, "dk.db")
            .build()

//        private fun buildDatabase2(context: Context) = Room.databaseBuilder(context,
//            AppDatabase::class.java, "dk2.db")
//            .build()
    }
}