package com.codingpixel.dedicatekitchen.interfaces

interface KitchenTimeListener {
    fun isKitchenOpen(isKitchenOpen: Boolean, canadianTime: String)
    fun isPastNine(isPastNine: Boolean, canadianTime: String)
    fun isError()
}