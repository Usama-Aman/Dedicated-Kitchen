package com.codingpixel.dedicatekitchen.viewholders.order

import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.adapters.order.OrderHistoryOptionsAdapter
import com.codingpixel.dedicatekitchen.databinding.SingleOrderHistoryOptionItemBinding
import com.codingpixel.dedicatekitchen.models.ExtraItemOrderModel

class OrderHistoryOptionVH(
    binding: SingleOrderHistoryOptionItemBinding,
    adapter: OrderHistoryOptionsAdapter
) : RecyclerView.ViewHolder(binding.root) {

    private val mBinding: SingleOrderHistoryOptionItemBinding = binding
    private val mAdapter: OrderHistoryOptionsAdapter = adapter

    fun bind(source: ExtraItemOrderModel, position: Int) {
        mBinding.model = source
        mBinding.position = position
        mBinding.adapter = mAdapter
    }
}