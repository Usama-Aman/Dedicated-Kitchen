package com.codingpixel.dedicatekitchen.interfaces

import com.codingpixel.dedicatekitchen.models.CardModel
import com.codingpixel.dedicatekitchen.models.OrderModel
import com.codingpixel.dedicatekitchen.models.PackageModel

interface CartItemsListener {
    fun itemTapped(position: Int)
    fun plusTapped(position: Int, pKey: Int)
    fun minusTapped(position: Int, pKey: Int)
    fun editTapped(position: Int, productDetail: OrderModel)
}

interface ClickListener {
    fun onClick(position: Int)
}

interface AddedCardInterface {
    fun addedCardInfo(card: CardModel)
}

interface SubscriptionInterface {
    fun packageSelected(packageDetail: PackageModel)
    fun amountSelected(position: Int , packageDetail : PackageModel){}
}