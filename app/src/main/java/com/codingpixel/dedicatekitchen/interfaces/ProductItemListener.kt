package com.codingpixel.dedicatekitchen.interfaces

import com.codingpixel.dedicatekitchen.models.MenuItemModel

interface ProductItemListener {
    fun productTapped(position: Int , product : MenuItemModel)
    fun plusTapped(position: Int)
    fun minusTapped(position: Int)
    fun heartTapped(position: Int)
}

