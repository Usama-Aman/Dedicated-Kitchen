package com.codingpixel.dedicatekitchen.interfaces

import com.codingpixel.dedicatekitchen.models.CardModel

interface SelectCardInterface {
    fun selectedCard(cardListing : ArrayList<CardModel> , position : Int)

}
