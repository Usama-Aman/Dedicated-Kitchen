package com.codingpixel.dedicatekitchen.interfaces

import com.codingpixel.dedicatekitchen.models.MenuItemModel

interface MealPrepListener {
    fun headerTapped(position : Int)
    fun itemTapped(position: Int, itemId: String, categoryHeaderId: Int, itemDetail: MenuItemModel)
    fun plusTapped(sectionIndex: Int, subSectionIndex: Int)
    fun minusTapped(sectionIndex: Int, subSectionIndex: Int)
    fun showToast(context: String)
    fun cantSelectMore(sectionIndex: Int, subSectionIndex: Int)
}