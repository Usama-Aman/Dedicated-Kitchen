package com.codingpixel.dedicatekitchen.viewholders.local

import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.adapters.local.ToggleOptionsAdapter
import com.codingpixel.dedicatekitchen.databinding.SingleToggleSelectionItemBinding
import com.codingpixel.dedicatekitchen.models.local.ToggleOption

class ToggleOptionVH(binding: SingleToggleSelectionItemBinding, adapter: ToggleOptionsAdapter) :
    RecyclerView.ViewHolder(binding.root) {

    private val mBinding: SingleToggleSelectionItemBinding = binding
    private val mAdapter: ToggleOptionsAdapter = adapter

    fun bind(source: ToggleOption, position: Int) {
        mBinding.source = source
        mBinding.position = position
        mBinding.adapter = mAdapter
    }
}