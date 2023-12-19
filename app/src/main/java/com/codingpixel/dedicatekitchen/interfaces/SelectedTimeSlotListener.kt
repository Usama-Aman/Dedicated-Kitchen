package com.codingpixel.dedicatekitchen.interfaces

import com.codingpixel.dedicatekitchen.models.DedicateKitchen
import com.codingpixel.dedicatekitchen.models.TimeSlot

interface SelectedTimeSlotListener {
    fun timeSlot(timeSlot: TimeSlot)
}

interface SelectListener {
    fun selectKitchen(kitchen: String, id: String) {}
    fun selectTime(time: Int, timeValue: String) {}
    fun changeKitchen(selectedKitchen: DedicateKitchen) {}
}