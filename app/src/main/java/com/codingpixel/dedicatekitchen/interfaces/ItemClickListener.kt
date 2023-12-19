package com.codingpixel.dedicatekitchen.interfaces

import com.codingpixel.dedicatekitchen.models.Category
import com.codingpixel.dedicatekitchen.models.Order

interface ItemClickListener {
    fun itemClicked(position: Int){}
    fun brandTapped(position: Int, title: String)
    fun makeDefaultLocation(position: Int, addressId: Int)
    fun deleteLocation(position: Int, addressId: Int)
    fun cancelOrder(position: Int, orderId: Int) {}
    fun info() {}
    fun reorder(position: Int, orderDetail: Order) {}
    fun orderDetails(position: Int, orderDetail: Order){}
    fun orderCompleted(position: Int)
    fun refreshRow(position: Int) {}
}

interface MenuInterface {
    fun menuItemTapped(products: Category, position: Int)
}
