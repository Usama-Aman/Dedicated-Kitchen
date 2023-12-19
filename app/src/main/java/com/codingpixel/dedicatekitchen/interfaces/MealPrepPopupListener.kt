package com.codingpixel.dedicatekitchen.interfaces

interface MealPrepPopupListener {
    fun deliverySelected(date: String, time: String, location: String)
    fun takeOutSelected(date: String, time: String)
}