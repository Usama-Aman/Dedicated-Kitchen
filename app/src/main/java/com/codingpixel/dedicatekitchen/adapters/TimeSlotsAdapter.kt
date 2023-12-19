package com.codingpixel.dedicatekitchen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.databinding.SingleTimeSlotBinding
import com.codingpixel.dedicatekitchen.interfaces.SelectedTimeSlotListener
import com.codingpixel.dedicatekitchen.models.TimeSlot
import com.codingpixel.dedicatekitchen.viewholders.TimeSlotVH

class TimeSlotsAdapter(
    private val timeSlots: ArrayList<TimeSlot>,
    private val itemClickListener: SelectedTimeSlotListener? = null
) : RecyclerView.Adapter<TimeSlotVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding: SingleTimeSlotBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.single_time_slot,
                parent,
                false
            )
        return TimeSlotVH(binding = binding, adapter = this@TimeSlotsAdapter)
    }

    override fun getItemCount(): Int {
        return timeSlots.size
    }

    override fun onBindViewHolder(holder: TimeSlotVH, position: Int) {
        holder.bind(source = timeSlots[position], position = position)
    }

    fun itemTapped(position : Int){
        itemClickListener?.timeSlot(timeSlots[position])
    }
}