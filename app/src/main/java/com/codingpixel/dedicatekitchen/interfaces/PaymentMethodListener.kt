package com.codingpixel.dedicatekitchen.interfaces

interface PaymentMethodListener {
    fun itemTapped(position : Int)
    fun makeCardDefault(position : Int)
    fun deleteTapped(position : Int , cardId : Int)

}

interface CardInterface{
    fun deleteCard(position: Int , id : Int)
}