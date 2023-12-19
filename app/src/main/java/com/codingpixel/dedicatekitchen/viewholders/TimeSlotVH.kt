package com.codingpixel.dedicatekitchen.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.TimeSlotsAdapter
import com.codingpixel.dedicatekitchen.databinding.SingleTimeSlotBinding
import com.codingpixel.dedicatekitchen.models.TimeSlot

class TimeSlotVH(binding: SingleTimeSlotBinding, adapter: TimeSlotsAdapter) :
    RecyclerView.ViewHolder(binding.root) {

    private val mBinding: SingleTimeSlotBinding = binding
    private val mAdapter: TimeSlotsAdapter = adapter

    fun bind(source: TimeSlot, position: Int) {
        mBinding.source = source
        mBinding.position = position
        mBinding.adapter = mAdapter
        if (source.isSelected)
            mBinding.tvTimeSlot.setBackgroundResource(R.drawable.selected_time_slot_bg)
        else
            mBinding.tvTimeSlot.setBackgroundResource(0)
    }
}