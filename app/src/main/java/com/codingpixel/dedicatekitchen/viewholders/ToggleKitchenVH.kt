package com.codingpixel.dedicatekitchen.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.adapters.ToggleKitchenAdapter
import com.codingpixel.dedicatekitchen.databinding.SingleKitchenToggleItemBinding
import com.codingpixel.dedicatekitchen.models.Kitchen

class ToggleKitchenVH(binding: SingleKitchenToggleItemBinding, adapter: ToggleKitchenAdapter) :
    RecyclerView.ViewHolder(binding.root) {

    private val mBinding: SingleKitchenToggleItemBinding = binding
    private val mAdapter: ToggleKitchenAdapter = adapter

    fun bind(source: Kitchen, position: Int) {
        mBinding.source = source
        mBinding.adapter = mAdapter
        mBinding.position = position
    }
}