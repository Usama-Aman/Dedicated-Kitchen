package com.codingpixel.dedicatekitchen.interfaces

interface ProductInfoListener {
    fun toggle(catId: String, productId: String, toggle : Int)
}